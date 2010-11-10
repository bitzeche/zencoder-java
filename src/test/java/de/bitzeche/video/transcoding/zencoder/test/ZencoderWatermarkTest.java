package de.bitzeche.video.transcoding.zencoder.test;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.bitzeche.video.transcoding.zencoder.job.ZencoderWatermark;

public class ZencoderWatermarkTest {

	@Test
	public void testWithoutOptions() throws ParserConfigurationException {
		ZencoderWatermark watermark = new ZencoderWatermark("http://url/");
		String doc = watermark.toString().replaceAll(" ", "")
				.replaceAll("\n", "");
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><watermark><url>http://url/</url><x>-10</x><y>-10</y></watermark>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}

	@Test
	public void testWithOptions() throws ParserConfigurationException {
		ZencoderWatermark watermark = new ZencoderWatermark("http://url/");
		watermark.setHeight(10);
		watermark.setWidth(200);
		watermark.setX("21%");
		watermark.setY("12345");

		String doc = watermark.toString().replaceAll(" ", "")
				.replaceAll("\n", "");
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><watermark><url>http://url/</url><x>21%</x><y>12345</y><width>200</width><height>10</height></watermark>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}
}
