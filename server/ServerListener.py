#!/usr/bin/env python
from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import urlparse, parse_qs
import json
from StyleChecker import evaluate_style
from CredibilityChecker import evaluate_credibility
from PropagationChecker import evaluate_propagation
from KnowledgeChecker import evaluate_knowledge
import time

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
        print(query_components)
        currentRequestID = query_components["requestID"]
        evaltext = query_components["text"]
        url = query_components["url"]
        if (evaltext == "empty"):
            evaltext = ""

        #after 10 seconds merge all json files, even if they are empty --> timeout
        #TODO

        #merge json
        merged_jsons = {}
        merged_jsons["credibility"] = evaluate_credibilty(currentRequestID,evaltext,url)
        merged_jsons["style"] = evaluate_style(currentRequestID,evaltext,url)
        merged_jsons["propagation"] = evaluate_propagation(currentRequestID,evaltext,url)
        merged_jsons["knowledge"] = evaluate_knowledge(currentRequestID,evaltext,url)
        return_json = json.dumps(merged_jsons)

        #time.sleep(7)

        #TEST
        self.wfile.write(bytes(return_json, "utf8"))
        return

def run():
    print('starting server...')
    server_address = ("192.168.43.7" , 8081)
    httpd = HTTPServer(server_address, testHTTPServer_RequestHandler)
    print('running server...')
    httpd.serve_forever()
run()

