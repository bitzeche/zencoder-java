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

import de.bitzeche.video.transcoding.zencoder.job.ZencoderWatermark;

public class ZencoderWatermarkTest {

	@Test
	public void testWithoutOptions() throws ParserConfigurationException {
		ZencoderWatermark watermark = new ZencoderWatermark("http://url/");
		String doc = StringUtil.stripSpacesAndLineBreaksFrom( watermark );
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

		String doc = StringUtil.stripSpacesAndLineBreaksFrom( watermark );
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><watermark><url>http://url/</url><x>21%</x><y>12345</y><width>200</width><height>10</height></watermark>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}
}
