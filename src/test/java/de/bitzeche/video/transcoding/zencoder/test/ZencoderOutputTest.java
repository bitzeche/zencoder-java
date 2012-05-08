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

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderAspectMode;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderAudioCodec;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderDeinterlace;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderDenoiseFilter;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderVideoCodec;
import de.bitzeche.video.transcoding.zencoder.job.ZencoderOutput;

public class ZencoderOutputTest {

	@Test
	public void testWithoutOptions() throws ParserConfigurationException {
		ZencoderOutput output = new ZencoderOutput("test", "http://testpath/");

		String doc = StringUtil.stripSpacesAndLineBreaksFrom(output.toString());
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><output><label>test</label><url>http://testpath/</url><speed>4</speed><public>0</public><video_codec>h264</video_codec><upscale>0</upscale><deinterlace>detect</deinterlace><skip_video>0</skip_video><deblock>0</deblock><autolevel>0</autolevel><audio_codec>aac</audio_codec><skip_audio>0</skip_audio></output>";
//		System.out.println("TestWithoutOptions computed: " + doc + "\n\nExpected: " + expected);
		Assert.assertEquals(doc, expected);
	}

	@Test
	public void testWithOptions() throws ParserConfigurationException {
		ZencoderOutput output = new ZencoderOutput("test", "http://testpath/");
		output.setAspectMode(ZencoderAspectMode.crop);
		output.setAudioBitrate(128);
		output.setAudioChannels(1);
		output.setAudioCodec(ZencoderAudioCodec.mp3);
		output.setAudioQuality(1);
		output.setAudioSamplerate(12300);
		output.setBufferSize(12345);
		output.setClipLength("12.5");
		output.setDecimate(2);
		output.setDeinterlace(ZencoderDeinterlace.on);
		output.setFrameRate(13);
		output.setHeight(1234);
		output.setKeyFrameInterval(12);
		output.setMaxFrameRate(29);
		output.setRotate(90);
		output.setSize("123X456");
		output.setSkipAudio(true);
		output.setSpeed(1);
		output.setStartClip("56:00");
		output.setSkipVideo(true);
		output.setUpscale(true);
		output.setVideoBitrate(12345);
		output.setVideoBitrateCap(123);
		output.setVideoQuality(1);
		output.setVideoCodec(ZencoderVideoCodec.theora);
		output.setWidth(12);
		output.setPublic(true);
		output.setDeblock(true);
		output.setAutolevel(true);
		output.setDenoise(ZencoderDenoiseFilter.WEAK);

		String doc = StringUtil.stripSpacesAndLineBreaksFrom(output.toString());
//		System.out.println("TestWithOptions: " + doc);
		String expected = "<?xmlversion=\"1.0\"encoding=\"UTF-8\"?><output><label>test</label><url>http://testpath/</url><speed>1</speed><start_clip>56:00</start_clip><clip_length>12.5</clip_length><public>1</public><video_codec>theora</video_codec><width>12</width><height>1234</height><size>123X456</size><upscale>1</upscale><aspect_mode>crop</aspect_mode><quality>1</quality><video_bitrate>12345</video_bitrate><bitrate_cap>123</bitrate_cap><buffer_size>12345</buffer_size><deinterlace>on</deinterlace><max_frame_rate>29.0</max_frame_rate><frame_rate>13.0</frame_rate><decimate>2</decimate><keyframe_interval>12</keyframe_interval><rotate>90</rotate><skip_video>1</skip_video><denoise>weak</denoise><deblock>1</deblock><autolevel>1</autolevel><audio_codec>mp3</audio_codec><audio_bitrate>128</audio_bitrate><audio_sample_rate>12300</audio_sample_rate><audio_quality>1</audio_quality><audio_channels>1</audio_channels><skip_audio>1</skip_audio></output>";
		Assert.assertEquals(doc, expected);
	}

}
