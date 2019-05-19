import urllib.request
from urllib.parse import urlparse, parse_qs
from http.client import HTTPResponse
import json
import math

def evaluate_credibility(currentRequestID,text,url):
    overall_score = 0
    message = ""

    api_jsons = []
    #get all information
    api_jsons.append(get_FakeNewsAI(url))
    api_jsons.append(get_Averifai_SourceCheck(url))
    api_jsons.append(get_Averfai_FakeReferences(text))
    #...

    #calculate overall credibilty
    scores = []
    for api_result in api_jsons:
    # if score == -1 --> ignore
        if (api_result["score"] > 0):
            scores.append(api_result["score"])
    if (len(scores) > 0):
        overall_score = sum(scores) / len(scores)
    else:
        overall_score = -1

    # if message == "" ignore

    return_json = {}
    return_json["score"] = (overall_score)

    return_json["apis"] = api_jsons

    return return_json


def get_FakeNewsAI(url):
    fakenewsai = "http://www.fakenewsai.com/detect?url=<url>"
    response = urllib.request.urlopen(fakenewsai.replace("<url>",url))
    string = response.read().decode('utf-8')
    json_obj = json.loads(string)
    if (json_obj['error']):
        score = -1
    try:
        resultAccuracy = json_obj['result']
        fake = json_obj['fake']
        if (fake):
            score = 10
        else:
            score = 90
    except:
        score = -1
    return to_api_json("FakeNewsAi",score,"")

def get_Averifai_SourceCheck(url):
    adverifai = "https://adverifai-api.p.rapidapi.com/source_check?url=<url>"
    score = -1 
    message = ""
    request = urllib.request.Request(adverifai.replace("<url>",url),
            headers = {
                "X-RapidAPI-Host": "adverifai-api.p.rapidapi.com",
                "X-RapidAPI-Key": "bf1a959dc0msh6fa10f164eecd72p1b9501jsn8bd06e9dd1f0" }
            ) 
    with urllib.request.urlopen(request) as response:
        response = response.read().decode('utf-8')
    json_obj = json.loads(response)
    try: 
        message = json_obj["fakeDescription"]
    except:
        pass
    return to_api_json("Averfai",score,"tags: "+message) 
    
def to_api_json(name,score,info):
    output_json_api = {}
    output_json_api["name"] = name 
    output_json_api["score"] = score 
    output_json_api["info"] = info
    return output_json_api


def get_Averfai_FakeReferences(text):
    if (len(text) > 100):
        #only good for titles
        return (score,message)

    adverifai = "https://adverifai-api.p.rapidapi.com/fake_ref?headline=<text>"
    score = -1 
    message = ""
    request = urllib.request.Request(adverifai.replace("<text>",urllib.parse.quote(text)),
            headers = {
                "X-RapidAPI-Host": "adverifai-api.p.rapidapi.com",
                "X-RapidAPI-Key": "bf1a959dc0msh6fa10f164eecd72p1b9501jsn8bd06e9dd1f0" }
            ) 
    with urllib.request.urlopen(request) as response:
        response = response.read().decode('utf-8')
    json_obj = json.loads(response)

    sitetypes = []
    try:
        numberRefs = len(json_obj["fakeRef"])
        score = 100 * 2**(-numberRefs)
        #site types
        for ref in json_obj["fakeRef"]:
            sitetypes.append(ref["site_type"])
    except:
        pass
    message = "site types: " + ''.join(list(dict.fromkeys(sitetypes)))
    return to_api_json("Averfai",score,message) 

#print(get_Averfai_FakeReferences("Love Me Trump"))
#print(get_Averifai_SourceCheck("https://www.activistpost.com"))
#print(get_Averifai_SourceCheck("bients.com"))
#print(get_Averifai_SourceCheck("facebook.com"))
#print(get_FakeNewsAI("https://www.activistpost.com"))
