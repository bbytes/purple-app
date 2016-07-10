package com.bbytes.purple.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public final class StringUtils {

	private static SecureRandom random = new SecureRandom();

	private StringUtils() {
	}

	public static String checkNotBlank(String string) {
		Preconditions.checkArgument(string != null && string.trim().length() > 0);
		return string;
	}

	public static String commaSeparate(Collection<String> collectionOfStrings) {
		return Joiner.on(",").join(collectionOfStrings);
	}

	/**
	 * Random password generator
	 */
	public static String nextSessionId() {
		return new BigInteger(40, random).toString(32);
	}

	/**
	 * Method return date with specified timezone.
	 * 
	 * @param date
	 * @param timezone
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateByTimezone(Date date, String timezone) throws ParseException {

		SimpleDateFormat timeZoneDateFormatter = new SimpleDateFormat(GlobalConstants.DATE_TIME_FORMAT);
		SimpleDateFormat formatter = new SimpleDateFormat(GlobalConstants.DATE_TIME_FORMAT);

		timeZoneDateFormatter.setTimeZone(TimeZone.getTimeZone(timezone));
		String dateWithTimezoneString = timeZoneDateFormatter.format(date);

		Date timezoneDate = formatter.parse(dateWithTimezoneString);

		return timezoneDate;
	}
}
