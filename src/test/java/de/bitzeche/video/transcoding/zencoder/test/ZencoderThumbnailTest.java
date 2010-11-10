package de.bitzeche.video.transcoding.zencoder.test;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.bitzeche.video.transcoding.zencoder.job.ZencoderThumbnail;

public class ZencoderThumbnailTest {

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testWithoutOptions() throws ParserConfigurationException {
		ZencoderThumbnail thumb = new ZencoderThumbnail();
		thumb.toString();
	}

	@Test
	public void testWithOptions() throws ParserConfigurationException {
		ZencoderThumbnail thumb = new ZencoderThumbnail();
		thumb.setBaseUrl("http://url/");
		thumb.setNumber(101);
		thumb.setInterval(10);
		thumb.setPrefix("abc");
		thumb.setSize("120x1999");
		String doc = thumb.toString().replaceAll(" ", "").replaceAll("\n", "");
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><thumbnails><number>101</number><interval>10</interval><size>120x1999</size><base_url>http://url/</base_url><prefix>abc</prefix><public>0</public></thumbnails>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}
}
