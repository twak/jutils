package org.twak.utils;

public class Stringz {

	public static String splitCamelCase( String s ) {
		return s.replaceAll( String.format( "%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])", "(?<=[A-Za-z])(?=[^A-Za-z])" ), " " );
	}
}
