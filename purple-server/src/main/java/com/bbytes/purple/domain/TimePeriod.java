package com.bbytes.purple.domain;

public enum TimePeriod {

	Today(0),Yesterday(1), Weekly(7), BiWeekly(15), Monthly(30), Quaterly(90), Yearly(365);

	Integer days;

	TimePeriod(int days) {
		this.days = days;
	}

	/**
	 * @return the days
	 */
	public Integer getDays() {
		return days;
	}

	/**
	 * @param days
	 *            the days to set
	 */
	public void setDays(Integer days) {
		this.days = days;
	}

}
