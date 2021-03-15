package org.twak.utils;

import java.util.Random;

public class Enumz {

	public static <T extends Enum<?>> T random (Random randy, Class<T> k){
		int x = randy .nextInt(k.getEnumConstants().length);
		return k.getEnumConstants()[x];
	}
}
