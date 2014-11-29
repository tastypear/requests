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
```
Post and other method:
```java
resp = Requests.post(url).text();
resp = Requests.head(url).text();
resp = Requests.delete(url).text();
resp = Requests.options(url).text();
```
##Passing Parameters
You can pass parameters in urls use param or params method:
```java
Map<String, Object> params = new HashMap<>();
params.put("k1", "v1");
params.put("k2", "v2");
Response<String> resp = Requests.get(url).param("key1", "value1").params(params).text();
```
##Response Content
The response object have common http response field to be used:
```java
Response<String> resp = Requests.get(url).text();
int statusCode = resp.getStatusCode();
Headers headers = resp.getHeaders();
Cookies cookies = resp.getCookies();
String body = resp.getBody();
```
The text() method here trans http response body as String, there are other method to process http response:
```java
// get response as string, and use provided encoding
Response<String> resp = Requests.get(url).text("UTF-8");
// get response as bytes
Response<byte[]> resp1 = Requests.get(url).bytes();
// save response as file 
Response<File> resp2 = Requests.get(url).file("/path/to/save/file");
```
or you can custom http response processor your self:
```java
Response<Json> resp1 = Requests.get(url).client(new ResponseProcessor<Json>() {
        @Override
        public Json convert(int statusCode, Headers headers, HttpEntity httpEntity) throws IOException {
            return null;
        }
    });
```
