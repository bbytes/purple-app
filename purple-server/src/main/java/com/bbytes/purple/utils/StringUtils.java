package com.bbytes.purple.utils;

import java.util.Collection;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public final class StringUtils {
	private StringUtils() {
	}

	public static String checkNotBlank(String string) {
		Preconditions.checkArgument(string != null && string.trim().length() > 0);
		return string;
	}

	public static String commaSeparate(Collection<String> collectionOfStrings) {
		return Joiner.on(",").join(collectionOfStrings);
	}
}
