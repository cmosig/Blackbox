import urllib.request

f = urllib.request.urlopen('http://172.16.76.69:8081/eval?url=http://google.com&text=Fakenewsisreal&requestID=12345')
#f = urllib.request.urlopen('http://192.168.43.7:8081/eval?url=https://www.activistpost.com/&text=Love&requestID=18366487278223')
# f = urllib.request.urlopen('http://172.16.76.69:8081/eval?url=http://bbsamplebb.q1.com&text=Love&requestID=18366487278223')
# f = urllib.request.urlopen('http://172.16.76.69:8081/eval?url=http://bbsamplebb.q1.com&text=Love&requestID=18366487278223')

print(f.read())
