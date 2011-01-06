package de.bitzeche.video.transcoding.zencoder.test;

public class StringUtil {

	public static String stripSpacesAndLineBreaksFrom( Object source ) {
		return source.toString().replaceAll(" ", "").replaceAll("[\n|\r]", "");
	}
	
}
