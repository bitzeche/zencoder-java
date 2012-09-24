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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import de.bitzeche.video.transcoding.zencoder.IZencoderClient;
import de.bitzeche.video.transcoding.zencoder.ZencoderClient;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderAPIVersion;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderDenoiseFilter;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderRegion;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderS3AccessControlRight;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderJob;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderNotification;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderOutput;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderS3AccessControlItem;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderThumbnail;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderWatermark;
import de.bitzeche.video.transcoding.zencoder.response.ZencoderErrorResponseException;

public class ZencoderClientTest {

	private static String API_KEY = "6ff283d80d91b0b6221d6c19d47fc7a0";
	private static final ZencoderRegion ZENCODER_REGION = ZencoderRegion.EUROPE;
	private static final String TEST_VIDEO_URL = "http://ca.bitzeche.de/big_buck_bunny_720p_h264.mov";
	Map<ZencoderAPIVersion, Integer> jobMap = new HashMap<ZencoderAPIVersion, Integer>();

	public ZencoderClientTest() {
		if ("".equals(API_KEY)) {
			throw new IllegalArgumentException(
					"We need an API key to run these tests");
		}
	}

	public IZencoderClient createClient(ZencoderAPIVersion apiVersion) {
		return new ZencoderClient(API_KEY, apiVersion);
	}

	@Test(dataProvider = "ApiVersionDS")
	public void constructor(ApiVersionProvider provider) {
		createClient(provider.getApiVersion());
	}

	@Test
	public void createJobAndCheckXML() {

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

		out2.setDeblock(true);
		out2.setAutolevel(true);
		out2.setDenoise(ZencoderDenoiseFilter.WEAK);
		ZencoderJob job = new ZencoderJob("http://test4/");
		job.addOutput(out);
		job.addOutput(out2);
		job.setPrivate(true);

		String doc = StringUtil.stripSpacesAndLineBreaksFrom(job);
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><api-request><input>http://test4/</input><download_connections>5</download_connections><test>0</test><private>1</private><outputstype=\"array\"><output><label>test</label><url>se://test/</url><speed>4</speed><public>0</public><video_codec>h264</video_codec><upscale>0</upscale><deinterlace>detect</deinterlace><skip_video>0</skip_video><deblock>0</deblock><autolevel>0</autolevel><audio_codec>aac</audio_codec><skip_audio>0</skip_audio><watermarks><watermark><url>http://url/</url><x>-10</x><y>-10</y></watermark><watermark><url>http://url/</url><x>-10</x><y>-10</y></watermark></watermarks><access-controls><access_control><grantee>test</grantee><permissions><permission>FULL_CONTROL</permission><permission>READ</permission></permissions></access_control></access-controls><notificationstype=\"array\"><notification><url>test@test.de</url></notification><notification><url>test2@test.de</url></notification></notifications></output><output><label>test2</label><url>se://test2/</url><speed>4</speed><public>0</public><video_codec>h264</video_codec><upscale>0</upscale><deinterlace>detect</deinterlace><skip_video>0</skip_video><denoise>weak</denoise><deblock>1</deblock><autolevel>1</autolevel><audio_codec>aac</audio_codec><skip_audio>0</skip_audio></output></outputs></api-request>";
		// System.out.println(doc);
		Assert.assertEquals(doc, expected);
	}

	@Test(dataProvider = "ApiVersionDS")
	public void createAndCancelJobTest(ApiVersionProvider provider)
			throws ZencoderErrorResponseException {

		ZencoderAPIVersion apiVersion = provider.getApiVersion();
		IZencoderClient client = createClient(apiVersion);
		ZencoderJob job = new ZencoderJob(TEST_VIDEO_URL);
		job.setZencoderRegion(ZENCODER_REGION);
		job.setTest(true);

		client.createJob(job);
		int jobId = job.getJobId();
		Assert.assertTrue(jobId >= 0);

		jobMap.put(apiVersion, jobId);

		boolean canceled = client.cancelJob(job);
		Assert.assertTrue(canceled);
	}

	@Test(dataProvider = "ApiVersionDS", expectedExceptions = ZencoderErrorResponseException.class)
	public void createJobAndProduceErrorWithMalformedVideoURL(
			ApiVersionProvider provider) throws ZencoderErrorResponseException {
		ZencoderAPIVersion apiVersion = provider.getApiVersion();
		IZencoderClient client = createClient(apiVersion);
		ZencoderJob job = new ZencoderJob("s" + TEST_VIDEO_URL);
		job.setZencoderRegion(ZENCODER_REGION);
		job.setTest(true);

		client.createJob(job);
	}

	@Test(dataProvider = "ApiVersionDS", dependsOnMethods = "createAndCancelJobTest")
	public void resubmitAndCancelJobTest(ApiVersionProvider provider) {

		ZencoderAPIVersion apiVersion = provider.getApiVersion();
		IZencoderClient client = createClient(apiVersion);
		ZencoderJob job = new ZencoderJob("");
		job.setJobId(jobMap.get(apiVersion));

		boolean resubmitted = client.resubmitJob(job);

		Assert.assertTrue(resubmitted);

		boolean canceled = client.cancelJob(job);
		Assert.assertTrue(canceled);
	}

	@Test(dataProvider = "ApiVersionDS", dependsOnMethods = "resubmitAndCancelJobTest")
	public void deleteTest(ApiVersionProvider provider) {
		ZencoderAPIVersion apiVersion = provider.getApiVersion();
		IZencoderClient client = createClient(apiVersion);
		ZencoderJob job = new ZencoderJob("");
		job.setJobId(jobMap.get(apiVersion));
		client.deleteJob(job);
	}

	abstract class ApiVersionProvider {
		public ZencoderAPIVersion getApiVersion() {
			return null;
		}
	}

	@DataProvider(name = "ApiVersionDS")
	public Object[][] getApiVersion() {
		return new Object[][] { new Object[] { new ApiVersionProvider() {
			@Override
			public ZencoderAPIVersion getApiVersion() {
				return ZencoderAPIVersion.API_V1;
			}
		} }, new Object[] { new ApiVersionProvider() {
			@Override
			public ZencoderAPIVersion getApiVersion() {
				return ZencoderAPIVersion.API_V2;
			}
		} }, new Object[] { new ApiVersionProvider() {
			@Override
			public ZencoderAPIVersion getApiVersion() {
				return ZencoderAPIVersion.API_DEV;
			}
		} } };
	}
}
