package de.bitzeche.video.transcoding.zencoder.test;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderS3AccessControlRight;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderNotification;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderS3AccessControlItem;

public class ZencoderS3AccessControlItemTest {

	@Test
	public void test() throws ParserConfigurationException {
		List<ZencoderS3AccessControlRight> rights = new ArrayList<ZencoderS3AccessControlRight>();
		rights.add(ZencoderS3AccessControlRight.FULL_CONTROL);
		rights.add(ZencoderS3AccessControlRight.READ);

		ZencoderS3AccessControlItem ca = new ZencoderS3AccessControlItem(
				"test", rights);
		String doc = ca.toString().replaceAll(" ", "").replaceAll("\n", "");
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><access_control><grantee>test</grantee><permissions><permission>FULL_CONTROL</permission><permission>READ</permission></permissions></access_control>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}
}
