#Requests

Requests is a http request lib for human beings who use java inspired by python requests module.
The Httpclient lib is greate, but has too complex API, which confuse beginners. Requests build simple and flexible api, both for common and advanced Usage.

#User Guide
##Get Requests
Requests is now in maven central repo.
```xml
<dependency>
    <groupId>net.dongliu</groupId>
    <artifactId>requests</artifactId>
    <version>1.8.0</version>
</dependency>
```
##Make request
Simple example that do http get request:
```java
String url = ...;
Response<String> resp = Requests.get(url).text();
int statusCode = resp.getStatusCode();
Headers headers = resp.getHeaders();
String body = resp.getBody();
```
The text() method here trans http response body as String.
Post and other method:
```java
resp = Requests.post(url).text();
resp = Requests.head(url).text();
resp = Requests.delete(url).text();
resp = Requests.options(url).text();
```
