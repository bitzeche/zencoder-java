zencoder-java
=============

Java Client Library for Zencoder API

**Current release version:** 0.9.3
**Current development version:** 0.9.4-SNAPSHOT

## How to use zencoder-java

Zencoder-java currently supports most of the functions of Zencoder, e.g.:
- Watermarks
- Multiple outputs
- S3 storage (both for in and output)
- Notifications (email, HTTP, ...)
- ...

Using zencoder-java to create your zencoder jobs is fairly easy:  
### Create a client instance
```java
ZencoderClient zencoderClient = new ZencoderClient(API_KEY, API_VERSION);
HttpClient client = new HttpClient(new SimpleHttpConnectionManager());
ApacheHttpClientHandler apacheHttpClientHandler = new ApacheHttpClientHandler(client, new DefaultApacheHttpClientConfig());
ApacheHttpClient httpClient = new ApacheHttpClient(apacheHttpClientHandler);
zencoderClient.setHttpClient(httpClient); 
```
### Create Output
```java
//Add notification email
ZencoderNotification notification = new ZencoderNotification("test@test.de");

ZencoderOutput output = new ZencoderOutput("test", "se://test/");
output.addNotification(notification);

ZencoderJob job = new ZencoderJob("http://ca.bitzeche.de/big_buck_bunny_720p_h264.mov");
job.addOutput(output);
//region where the job is processed
job.setZencoderRegion(ZencoderRegion.EUROPE);
//set test mode
job.setTest(true);
```

### Submit Job
```java
client.createJob(job);
```

### Cancel Job
```java
client.cancelJob(job);
```

### Resubmit Job
```java
boolean resubmitted = client.resubmitJob(job);
```

### Delete Job
```java
client.deleteJob(job);
```


### Add Watermark
```java
ZencoderWatermark watermark = new ZencoderWatermark("http://url/");
output.addWatermark(watermark);
```