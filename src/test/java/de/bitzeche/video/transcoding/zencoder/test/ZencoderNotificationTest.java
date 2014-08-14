/**
 * Copyright (C) 2013 Bitzeche GmbH <info@bitzeche.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.bitzeche.video.transcoding.zencoder.test;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.bitzeche.video.transcoding.zencoder.job.ZencoderNotification;

import java.util.HashMap;

public class ZencoderNotificationTest {

	@Test
	public void test() throws ParserConfigurationException {
		ZencoderNotification notif = new ZencoderNotification("test@test.de");
		String doc = StringUtil.stripSpacesAndLineBreaksFrom(notif.toString());
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><notification><url>test@test.de</url></notification>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}
	@Test
	public void testWithOptions() throws ParserConfigurationException {
		ZencoderNotification notif = new ZencoderNotification("test@test.de");
		notif.setFormat("xml");
		String doc = StringUtil.stripSpacesAndLineBreaksFrom(notif.toString());
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><notification><format>xml</format><url>test@test.de</url></notification>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}

    @Test
    public void testWithHeaders() throws ParserConfigurationException {
        ZencoderNotification notif = new ZencoderNotification("test@test.de");
        notif.setHeaders(new HashMap<String, String>(){{
            put("cloudfront_url", "http://asdjaosidjas.cloudfront.net");
        }});
        String doc = StringUtil.stripSpacesAndLineBreaksFrom(notif.toString());
        String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><notification><headers><cloudfront_url>http://asdjaosidjas.cloudfront.net</cloudfront_url></headers><url>test@test.de</url></notification>";
        // System.out.println(doc);
        Assert.assertEquals(doc, expected);
    }
}
