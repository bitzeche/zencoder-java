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

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.testng.Assert;
import org.testng.annotations.Test;

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderS3AccessControlRight;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderS3AccessControlItem;

public class ZencoderS3AccessControlItemTest {

	@Test
	public void test() throws ParserConfigurationException {
		List<ZencoderS3AccessControlRight> rights = new ArrayList<ZencoderS3AccessControlRight>();
		rights.add(ZencoderS3AccessControlRight.FULL_CONTROL);
		rights.add(ZencoderS3AccessControlRight.READ);

		ZencoderS3AccessControlItem ca = new ZencoderS3AccessControlItem(
				"test", rights);
		String doc = StringUtil.stripSpacesAndLineBreaksFrom(ca.toString());
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><access_control><grantee>test</grantee><permissions><permission>FULL_CONTROL</permission><permission>READ</permission></permissions></access_control>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}
}
