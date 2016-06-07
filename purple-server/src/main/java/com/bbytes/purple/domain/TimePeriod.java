package com.bbytes.purple.domain;

public enum TimePeriod {

	Today(0,"day"),Yesterday(1,"day"), Weekly(7,"day"), BiWeekly(15,"day"), Monthly(30,"day"), Quaterly(90,"month"), Yearly(365,"month");

	Integer days;
	
	String aggrType;

	/**
	 * @return the aggrType
	 */
	public String getAggrType() {
		return aggrType;
	}

	TimePeriod(int days,String aggrType) {
		this.days = days;
		this.aggrType=aggrType;
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
