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

package de.bitzeche.video.transcoding.zencoder.job;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.bitzeche.video.transcoding.zencoder.enums.ZencoderAspectMode;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderAudioCodec;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderDeinterlace;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderDenoiseFilter;
import de.bitzeche.video.transcoding.zencoder.enums.ZencoderVideoCodec;
import de.bitzeche.video.transcoding.zencoder.util.XmlUtility;

public class ZencoderOutput {

	private Document xmlDocument;
	private List<ZencoderWatermark> watermarks = new ArrayList<ZencoderWatermark>();
	private ZencoderThumbnail thumbnail;
	private List<ZencoderNotification> notifications = new ArrayList<ZencoderNotification>();

	/*
	 * General
	 */
	private String outputURL = null;
	private String baseURL = null;
	private String filename = null;
	private String label;
	private int speed = 4;
	private String startClip;
	private String clipLength;
	private Integer id;

	/*
	 * Video
	 */
	private ZencoderVideoCodec videoCodec = ZencoderVideoCodec.h264;
	private int width;
	private int height;
	private String size;
	private boolean upscale;
	private ZencoderAspectMode aspectMode = ZencoderAspectMode.NONE;
	private int videoQuality;
	private int videoBitrate;
	private int videoBitrateCap;
	private int bufferSize;
	private ZencoderDeinterlace deinterlace = ZencoderDeinterlace.detect;
	private float maxFrameRate;
	private float frameRate;
	private int decimate;
	private int keyFrameInterval;
	private int rotate;
	private boolean skipVideo = false;
	private boolean autolevel = false;
	private boolean deblock = false;
	private ZencoderDenoiseFilter denoise = ZencoderDenoiseFilter.NONE;
	/*
	 * Audio
	 */
	private ZencoderAudioCodec audioCodec = ZencoderAudioCodec.aac;
	private int audioBitrate;
	private int audioQuality;
	private int audioSamplerate;
	private int audioChannels;
	private boolean skipAudio = false;

	/*
	 * S3
	 */
	private boolean isPublic = false;
	private List<ZencoderS3AccessControlItem> aclItems = new ArrayList<ZencoderS3AccessControlItem>();

	public ZencoderOutput(String label) {
		this.label = label;
	}

	public ZencoderOutput(String label, String baseUrl, String filename) {
		this.filename = filename;
		this.baseURL = baseUrl;
		this.label = label;
	}

	public ZencoderOutput(String label, String outputUrl) {
		this.outputURL = outputUrl;
		this.label = label;
	}

	public Element createXML(Document document) {
		this.xmlDocument = document;
		Element root = createElement("output");

		if (id != null)
		{
			createAndAppendElement("id", this.id, root);
		}
		createAndAppendElement("label", this.label, root);
		createAndAppendElement("url", this.outputURL, root);
		createAndAppendElement("base_url", this.baseURL, root);
		createAndAppendElement("filename", this.filename, root);
		createAndAppendElement("speed", this.speed, root);
		createAndAppendElement("start_clip", this.startClip, root);
		createAndAppendElement("clip_length", this.clipLength, root);
		createAndAppendElement("public", this.isPublic, root);

		createAndAppendElement("video_codec", this.videoCodec.name(), root);
		createAndAppendElement("width", this.width, root);
		createAndAppendElement("height", this.height, root);
		createAndAppendElement("size", this.size, root);
		createAndAppendElement("upscale", this.upscale, root);
		if (!this.aspectMode.equals(ZencoderAspectMode.NONE))
			createAndAppendElement("aspect_mode", this.aspectMode.name(), root);
		createAndAppendElement("quality", this.videoQuality, root);
		createAndAppendElement("video_bitrate", this.videoBitrate, root);
		createAndAppendElement("bitrate_cap", this.videoBitrateCap, root);
		createAndAppendElement("buffer_size", this.bufferSize, root);
		createAndAppendElement("deinterlace", this.deinterlace.name(), root);
		createAndAppendElement("max_frame_rate", this.maxFrameRate, root);
		createAndAppendElement("frame_rate", this.frameRate, root);
		createAndAppendElement("decimate", this.decimate, root);
		createAndAppendElement("keyframe_interval", this.keyFrameInterval, root);
		createAndAppendElement("rotate", this.rotate, root);
		createAndAppendElement("skip_video", this.skipVideo, root);
		if (!this.denoise.equals(ZencoderDenoiseFilter.NONE))
			createAndAppendElement("denoise",
					this.denoise.name().toLowerCase(), root);
		createAndAppendElement("deblock", this.deblock, root);
		createAndAppendElement("autolevel", this.autolevel, root);

		createAndAppendElement("audio_codec", this.audioCodec.name(), root);
		createAndAppendElement("audio_bitrate", this.audioBitrate, root);
		createAndAppendElement("audio_sample_rate", this.audioSamplerate, root);
		createAndAppendElement("audio_quality", this.audioQuality, root);
		createAndAppendElement("audio_channels", this.audioChannels, root);
		createAndAppendElement("skip_audio", this.skipAudio, root);

		if (this.watermarks.size() != 0) {
			Element wms = document.createElement("watermarks");
			root.appendChild(wms);
			for (ZencoderWatermark item : this.watermarks) {
				Element wm = item.createXML(document);
				if (wm != null) {
					wms.appendChild(wm);
				}
			}
		}
		if (this.thumbnail != null) {
			Element tn = this.thumbnail.createXML(document);
			if (tn != null) {
				root.appendChild(tn);
			}
		}
		if (this.aclItems.size() != 0) {
			Element acl = document.createElement("access-controls");
			root.appendChild(acl);
			for (ZencoderS3AccessControlItem item : this.aclItems) {
				Element acls = item.createXML(document);
				if (acls != null) {
					acl.appendChild(acls);
				}
			}
		}
		if (this.notifications.size() != 0) {
			Element notifis = document.createElement("notifications");
			notifis.setAttribute("type", "array");
			root.appendChild(notifis);
			for (ZencoderNotification item : this.notifications) {
				Element notif = item.createXML(document);
				if (notif != null) {
					notifis.appendChild(notif);
				}
			}
		}

		return root;
	}

	protected Element createElement(String name) {
		if (this.xmlDocument != null) {
			return xmlDocument.createElement(name);
		}
		return null;
	}

	protected Element createElement(String name, String textValue) {
		Element elem = createElement(name);
		if (elem != null) {
			elem.setTextContent(textValue);
		}
		return elem;
	}

	protected void createAndAppendElement(String name, String textValue,
			Element rootNode) {
		if (textValue != null) {
			Element elem = createElement(name, textValue);
			if (elem != null) {
				rootNode.appendChild(elem);
			}
		}
	}

	protected void createAndAppendElement(String name, boolean value,
			Element rootNode) {
		createAndAppendElement(name, (value ? "1" : "0"), rootNode);
	}

	protected void createAndAppendElement(String name, int value,
			Element rootNode) {
//		if (value != null && value != 0) {
		if (value != 0) {
			createAndAppendElement(name, ("" + value), rootNode);
		}
	}

	protected void createAndAppendElement(String name, float value,
			Element rootNode) {
		if (value != 0) {
			createAndAppendElement(name, ("" + value), rootNode);
		}
	}

	/*
	 * ######## Getter ##########
	 */

	public String getOutputURL() {
		return outputURL;
	}

	public String getBasetURL() {
		return baseURL;
	}

	public String getFilename() {
		return filename;
	}

	public String getLabel() {
		return label;
	}

	public Integer getId() {
		return id;
	}

	public int getSpeed() {
		return speed;
	}

	public String getStartClip() {
		return startClip;
	}

	public String getClipLength() {
		return clipLength;
	}

	public ZencoderVideoCodec getVideoCodec() {
		return videoCodec;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getSize() {
		return size;
	}

	public boolean isUpscale() {
		return upscale;
	}

	public ZencoderAspectMode getAspectMode() {
		return aspectMode;
	}

	public int getVideoQuality() {
		return videoQuality;
	}

	public int getVideoBitrate() {
		return videoBitrate;
	}

	public int getVideoBitrateCap() {
		return videoBitrateCap;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public ZencoderDeinterlace getDeinterlace() {
		return deinterlace;
	}

	public float getMaxFrameRate() {
		return maxFrameRate;
	}

	public float getFrameRate() {
		return frameRate;
	}

	public int getDecimate() {
		return decimate;
	}

	public int getKeyFrameInterval() {
		return keyFrameInterval;
	}

	public int getRotate() {
		return rotate;
	}

	public boolean isSkipVideo() {
		return skipVideo;
	}

	public ZencoderAudioCodec getAudioCodec() {
		return audioCodec;
	}

	public int getAudioBitrate() {
		return audioBitrate;
	}

	public int getAudioQuality() {
		return audioQuality;
	}

	public int getAudioSamplerate() {
		return audioSamplerate;
	}

	public int getAudioChannels() {
		return audioChannels;
	}

	public boolean isSkipAudio() {
		return skipAudio;
	}

	public boolean isPublic() {
		return isPublic;
	}

	/*
	 * ###### Setters #########
	 */

	public void setOutputURL(String outputURL) {
		this.outputURL = outputURL;
	}

	public void setBasetURL(String basetURL) {
		this.baseURL = basetURL;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setZencoderVideoCodec(ZencoderVideoCodec zencoderVideoCodec) {
		this.videoCodec = zencoderVideoCodec;
	}

	public void setSpeed(int speed) {
		if (speed < 1) {
			this.speed = 1;
		} else if (speed > 5) {
			this.speed = 5;
		} else {
			this.speed = speed;
		}
	}

	public void setVideoQuality(int quality) {
		if (quality < 1) {
			this.videoQuality = 1;
		} else if (quality > 5) {
			this.videoQuality = 5;
		} else {
			this.videoQuality = quality;
		}
	}

	public void setVideoBitrate(int bitrate) {
		if (bitrate < 1) {
			this.videoBitrate = 100;
		} else {
			this.videoBitrate = bitrate;
		}
	}

	public void setVideoBitrateCap(int bitrate) {
		if (bitrate < 1) {
			this.videoBitrateCap = 100;
		} else {
			this.videoBitrateCap = bitrate;
		}
	}

	public void setBufferSize(int size) {
		if (size < 1) {
			this.bufferSize = 1000;
		} else {
			this.bufferSize = size;
		}
	}

	public void setAudioCodec(ZencoderAudioCodec codec) {
		if ((videoCodec.equals(ZencoderVideoCodec.h264) || videoCodec
				.equals(ZencoderVideoCodec.vp6))
				&& !(codec.equals(ZencoderAudioCodec.mp3) || codec
						.equals(ZencoderAudioCodec.aac))) {
			throw new IllegalArgumentException(
					"H264 and VP6 only support MP3 or AAC");
		} else if ((videoCodec.equals(ZencoderVideoCodec.theora) || videoCodec
				.equals(ZencoderVideoCodec.vp8))
				&& !codec.equals(ZencoderAudioCodec.vorbis)) {
			throw new IllegalArgumentException(
					"H264 and VP8 only support MP3 or AAC");
		}
		this.audioCodec = codec;
	}

	public void setAudioBitrate(int bitrate) {
		if (bitrate < 1) {
			this.audioBitrate = 64;
		} else {
			this.audioBitrate = bitrate;
		}
	}

	public void setAudioQuality(int quality) {
		if (quality < 1) {
			this.audioQuality = 1;
		} else if (quality > 5) {
			this.audioQuality = 5;
		} else {
			this.audioQuality = quality;
		}
	}

	public void setAudioSamplerate(int rate) {
		if (rate < 1000) {
			this.audioSamplerate = 44100;
		} else if (rate > 48000) {
			this.audioSamplerate = 48000;
		} else {
			this.audioSamplerate = rate;
		}
	}

	public void setAudioChannels(int channels) {
		if (channels <= 1) {
			this.audioChannels = 1;
		} else {
			this.audioChannels = 2;
		}
	}

	public void setFrameRate(float rate) {
		if (rate < 1) {
			rate = 1;
		} else if (rate > 30) {
			rate = 30;
		}
		this.frameRate = rate;
	}

	public void setMaxFrameRate(float rate) {
		if (rate < 1) {
			rate = 1;
		} else if (rate > 30) {
			rate = 30;
		}
		this.maxFrameRate = rate;
	}

	public void setKeyFrameInterval(int interval) {
		if (interval < 1) {
			interval = 250;
		}
		this.keyFrameInterval = interval;
	}

	public void setRotate(int degrees) {
		if (degrees % 90 == 0 && degrees >= 90 && degrees <= 270) {
			this.rotate = degrees;
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setStartClip(String startClip) {
		this.startClip = startClip;
	}

	public void setClipLength(String clipLength) {
		this.clipLength = clipLength;
	}

	public void setVideoCodec(ZencoderVideoCodec videoCodec) {
		this.videoCodec = videoCodec;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setUpscale(boolean upscale) {
		this.upscale = upscale;
	}

	public void setAspectMode(ZencoderAspectMode aspectMode) {
		this.aspectMode = aspectMode;
	}

	public void setDeinterlace(ZencoderDeinterlace deinterlace) {
		this.deinterlace = deinterlace;
	}

	public void setDecimate(int decimate) {
		this.decimate = decimate;
	}

	public void setSkipVideo(boolean skipVideo) {
		this.skipVideo = skipVideo;
	}

	public void setSkipAudio(boolean skipAudio) {
		this.skipAudio = skipAudio;
	}

	public void setThumbnail(ZencoderThumbnail thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void addAcl(ZencoderS3AccessControlItem item) {
		this.aclItems.add(item);
	}

	public void deleteAcl(ZencoderS3AccessControlItem item) {
		this.aclItems.remove(item);
	}

	public void addNotification(ZencoderNotification item) {
		this.notifications.add(item);
	}

	public void deleteNotification(ZencoderNotification item) {
		this.notifications.remove(item);
	}

	public void addWatermark(ZencoderWatermark item) {
		this.watermarks.add(item);
	}

	public void deleteWatermark(ZencoderWatermark item) {
		this.watermarks.remove(item);
	}

	public void setAutolevel(boolean autolevel) {
		this.autolevel = autolevel;
	}

	public void setDeblock(boolean deblock) {
		this.deblock = deblock;
	}

	public void setDenoise(ZencoderDenoiseFilter denoise) {
		this.denoise = denoise;
	}

	public String toString() {
		Element elem;
		Document document;
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory
					.newDocumentBuilder();
			document = documentBuilder.newDocument();

			elem = createXML(document);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		if (elem != null) {
			document.appendChild(elem);
			try {
				return XmlUtility.xmltoString(document);
			} catch (TransformerException e) {
				throw new RuntimeException(e);
			}
		}
		return this.getClass().getSimpleName();
	}
}
