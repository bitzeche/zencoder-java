/**
 * Copyright (C) 2012 Bitzeche GmbH <info@bitzeche.de>
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

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderThumbnailFormat;
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
		thumb.setFormat(ZencoderThumbnailFormat.JPG);
		String doc = StringUtil.stripSpacesAndLineBreaksFrom(thumb.toString());
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><thumbnails><number>101</number><interval>10</interval><size>120x1999</size><format>jpg</format><base_url>http://url/</base_url><prefix>abc</prefix><public>0</public></thumbnails>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}
}
