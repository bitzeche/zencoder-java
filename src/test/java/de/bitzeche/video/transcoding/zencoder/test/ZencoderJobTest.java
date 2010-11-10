package de.bitzeche.video.transcoding.zencoder.test;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderRegion;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;

public class ZencoderJobTest {

	@Test
	public void testWithoutOptions() throws ParserConfigurationException {
		ZencoderJob job = new ZencoderJob("http://testpath/");
		String doc = job.toString().replaceAll(" ", "").replaceAll("\n", "");
		String expected = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<api-request>" + "<input>http://testpath/</input>"
				+ "<download_connections>5</download_connections>"
				+ "<test>0</test>" + "</api-request>").replaceAll(" ", "");
		Assert.assertEquals(doc, expected);
	}
	
	@Test
	public void testWithOptions() throws ParserConfigurationException {
		ZencoderJob job = new ZencoderJob("http://testpath/");
		
		job.setDownloadConnections(10);
		job.setTest(true);
		job.setZencoderRegion(ZencoderRegion.ASIA);
		
		String doc = job.toString().replaceAll(" ", "").replaceAll("\n", "");
		String expected = ("<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><api-request><input>http://testpath/</input><region>asia</region><download_connections>10</download_connections><test>1</test></api-request>");
		Assert.assertEquals(doc, expected);
//		System.out.println(doc);
	}
	
}
