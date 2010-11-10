/**
 * Copyright (C) ${year} Bitzeche GmbH <info@bitzeche.de>
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

import junit.framework.Assert;

import org.testng.annotations.Test;

import de.bitzeche.video.transcoding.zencoder.IZencoderClient;
import de.bitzeche.video.transcoding.zencoder.ZencoderClient;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderS3AccessControlRight;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderNotification;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderOutput;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderS3AccessControlItem;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderThumbnail;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderWatermark;

public class ZencoderClientTest {

	IZencoderClient client;
	String API_KEY = "";

	public ZencoderClientTest() {
		client = new ZencoderClient(API_KEY);
	}

	@Test
	public void createJob() {
		ZencoderWatermark watermark = new ZencoderWatermark("http://url/");
		ZencoderWatermark watermark2 = new ZencoderWatermark("http://url/");
		ZencoderNotification notif = new ZencoderNotification("test@test.de");
		ZencoderNotification notif2 = new ZencoderNotification("test2@test.de");
		ZencoderThumbnail thumb = new ZencoderThumbnail();
		thumb.setInterval(10);
		List<ZencoderS3AccessControlRight> rights = new ArrayList<ZencoderS3AccessControlRight>();
		rights.add(ZencoderS3AccessControlRight.FULL_CONTROL);
		rights.add(ZencoderS3AccessControlRight.READ);
		ZencoderS3AccessControlItem s3 = new ZencoderS3AccessControlItem(
				"test", rights);
		thumb.addAcl(s3);
		ZencoderOutput out = new ZencoderOutput("test", "se://test/");
		ZencoderOutput out2 = new ZencoderOutput("test2", "se://test2/");
		out.addAcl(s3);
		out.addNotification(notif);
		out.addNotification(notif2);
		out.addWatermark(watermark);
		out.addWatermark(watermark2);
		ZencoderJob job = new ZencoderJob("http://test4/");
		job.addOutput(out);
		job.addOutput(out2);

		String doc = job.toString().replaceAll(" ", "").replaceAll("\n", "");
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><api-request><input>http://test4/</input><download_connections>5</download_connections><test>0</test><outputstype=\"array\"><ouput><label>test</label><url>se://test/</url><speed>4</speed><public>0</public><video_codec>h264</video_codec><upscale>0</upscale><onepass>0</onepass><deinterlace>detect</deinterlace><skip_video>0</skip_video><audio_codec>aac</audio_codec><skip_audio>0</skip_audio><watermarks><watermark><url>http://url/</url><x>-10</x><y>-10</y></watermark><watermark><url>http://url/</url><x>-10</x><y>-10</y></watermark></watermarks><access-controls><access_control><grantee>test</grantee><permissions><permission>FULL_CONTROL</permission><permission>READ</permission></permissions></access_control></access-controls><notificationstype=\"array\"><notification><url>test@test.de</url></notification><notification><url>test2@test.de</url></notification></notifications></ouput><ouput><label>test2</label><url>se://test2/</url><speed>4</speed><public>0</public><video_codec>h264</video_codec><upscale>0</upscale><onepass>0</onepass><deinterlace>detect</deinterlace><skip_video>0</skip_video><audio_codec>aac</audio_codec><skip_audio>0</skip_audio></ouput></outputs></api-request>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}

	@Test
	public void deleteTest() {
		ZencoderJob job = new ZencoderJob("");
		job.setJobId(439422);
		boolean res = client.deleteJob(job);
		if (res)
			Assert.fail("Shouldn't suceed deleting Job");
	}
}
