package de.bitzeche.video.transcoding.zencoder.test;

import junit.framework.Assert;

import org.testng.annotations.Test;

import de.bitzeche.video.transcoding.zencoder.IZencoderClient;
import de.bitzeche.video.transcoding.zencoder.ZencoderClient;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;

public class ZencoderClientTest {

	IZencoderClient client;
	String API_KEY = "";

	public ZencoderClientTest() {
		client = new ZencoderClient(API_KEY);
	}

//	@Test
	public void deleteTest() {
		ZencoderJob job = new ZencoderJob("");
		job.setJobId(439422);
		boolean res = client.deleteJob(job);
		if (res)
			Assert.fail("Shouldn't suceed deleting Job");
	}
}
