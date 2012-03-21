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

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderRegion;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;

public class ZencoderJobTest {

	@Test
	public void testWithoutOptions() throws ParserConfigurationException {
		ZencoderJob job = new ZencoderJob("http://testpath/");
		String doc = StringUtil.stripSpacesAndLineBreaksFrom(job.toString());
		String expected = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<api-request>" + "<input>http://testpath/</input>"
				+ "<download_connections>5</download_connections>"
				+ "<test>0</test><private>0</private>" + "</api-request>").replaceAll(" ", "");
		Assert.assertEquals(doc, expected);
	}
	
	@Test
	public void testWithOptions() throws ParserConfigurationException {
		ZencoderJob job = new ZencoderJob("http://testpath/");
		
		job.setDownloadConnections(10);
		job.setTest(true);
		job.setPrivate(true);
		job.setZencoderRegion(ZencoderRegion.ASIA);
		
		String doc = StringUtil.stripSpacesAndLineBreaksFrom(job.toString());
		String expected = ("<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><api-request><input>http://testpath/</input><region>asia</region><download_connections>10</download_connections><test>1</test><private>1</private></api-request>");
		Assert.assertEquals(doc, expected);
	}
	
}
