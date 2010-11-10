package de.bitzeche.video.transcoding.zencoder.test;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.bitzeche.video.transcoding.zencoder.job.ZencoderNotification;

public class ZencoderNotificationTest {

	@Test
	public void test() throws ParserConfigurationException {
		ZencoderNotification notif = new ZencoderNotification("test@test.de");
		String doc = notif.toString().replaceAll(" ", "").replaceAll("\n", "");
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><notification><url>test@test.de</url></notification>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}
	@Test
	public void testWithOptions() throws ParserConfigurationException {
		ZencoderNotification notif = new ZencoderNotification("test@test.de");
		notif.setFormat("xml");
		String doc = notif.toString().replaceAll(" ", "").replaceAll("\n", "");
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><notification><format>xml</format><url>test@test.de</url></notification>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}
}
