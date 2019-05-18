#!/usr/bin/env python
from http.server import BaseHTTPRequestHandler, HTTPServer
from urllib.parse import urlparse, parse_qs
from StyleChecker import            evaluate_style
from CredibilityChecker import      evaluate_credibilty
from PropagationChecker import     evaluate_propagation
from KnowledgeChecker import        evaluate_knowledge


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

        #wait for scores to get
        points_style =      evaluate_style(currentRequestID,evaltext,url)
        points_credibility= evaluate_credibilty(currentRequestID,evaltext,url)
        points_propagation= evaluate_propagation(currentRequestID,evaltext,url)
        points_knowledge =  evaluate_knowledge(currentRequestID,evaltext,url)
 
        # Send message back to client
        message = "Hello world!"

        # Write content as utf-8 data
        self.wfile.write(bytes(message, "utf8"))
        return
 
def run():
  print('starting server...')
 
  # Server settings
  # Choose port 8080, for port 80, which is normally used for a http server, you need root access
  server_address = ('172.16.78.236', 8081)
  httpd = HTTPServer(server_address, testHTTPServer_RequestHandler)
  print('running server...')
  httpd.serve_forever()
run()

