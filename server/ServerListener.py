#!/usr/bin/env python
from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import urlparse, parse_qs
import json
from StyleChecker import evaluate_style
from CredibilityChecker import evaluate_credibility
from PropagationChecker import evaluate_propagation
from KnowledgeChecker import evaluate_knowledge
import time

request_cache = {}

# HTTPRequestHandler class
class testHTTPServer_RequestHandler(BaseHTTPRequestHandler):
    # GET
    def do_GET(self):
        # Send response status code
        self.send_response(200)

        # Send headers
        self.send_header('Content-type','text/html')
        self.end_headers()

        #eval url
        query_components = parse_qs(urlparse(self.path).query)
        currentRequestID = query_components["requestID"][0]
        evaltext = query_components["text"][0]
        url = query_components["url"][0]
        query_components = query_components.pop("requestID")

        #check if presentation query
        if ("bbsamplebb" in url ):
            filepath = ""
            if ("q1" in url ):
                filepath = "presQ1.json"
            elif ("q2" in url ):
                filepath = "presQ2.json"
            elif ("q3" in url ):
                filepath = "presQ3.json"
            elif ("q4" in url ):
                filepath = "presQ4.json"

            with open(filepath, 'r') as file:
                return_json = file.read().replace('\n', '')
            
        #cache
        elif json.dumps(query_components) in request_cache:
            return_json = request_cache[json.dumps(query_components)]
        #normal
        else: 
            if (evaltext == "empty"):
                evaltext = ""

            #merge json
            merged_jsons = {}
            merged_jsons["credibility"] = evaluate_credibility(currentRequestID,evaltext,url)
            merged_jsons["style"] = evaluate_style(currentRequestID,evaltext,url)
            #merged_jsons["propagation"] = evaluate_propagation(currentRequestID,evaltext,url)
            #merged_jsons["knowledge"] = evaluate_knowledge(currentRequestID,evaltext,url)

            merged_jsons["style"] = {"score": 95.544, "apis": []}
            merged_jsons["propagation"] = {"score": 69.33, "apis": [
                {"name": "FakeNewsAi", "score": 2.9665915957085165, "info": ""},
                {"name": "Averfai", "score": 2, "info": "tags: unworthy"}, 
                {"name": "Jo", "score": 4, "info": "site types: "}
                ]}
            merged_jsons["knowledge"] = {"score": 44.33, "apis": [
                {"name": "Wikipedia", "score": 35.293829, "info": ""},
                {"name": "Factual", "score": 50.239, "info": "tags: fake, clickbait "}, 
                {"name": "Averfai", "score": 20, "info": "site types: claim "},
                {"name": "KnowledgeBase", "score": 20, "info": "site types: bait"},
                {"name": "Jo", "score": 99, "info": "site types: news"}
                ]}

            return_json = json.dumps(merged_jsons)
            request_cache[json.dumps(query_components)] = return_json

        print(return_json)

        #time.sleep(7)

        #TEST
        self.wfile.write(bytes(return_json, "utf8"))
        return

def run():
    print('starting server...')
    server_address = ("192.168.43.7" , 8081)
 #   server_address = ("172.16.51.23" , 8081)
    httpd = HTTPServer(server_address, testHTTPServer_RequestHandler)
    print('running server...')
    httpd.serve_forever()

if __name__ == "__main__":
  run()

