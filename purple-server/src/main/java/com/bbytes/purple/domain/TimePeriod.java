package com.bbytes.purple.domain;

public enum TimePeriod {

	WEEKLY(7);
	
	Integer days;
	
	TimePeriod(int days){
		this.days = days;
	}

	/**
	 * @return the days
	 */
	public Integer getDays() {
		return days;
	}

	/**
	 * @param days the days to set
	 */
	public void setDays(Integer days) {
		this.days = days;
	}
	
	
}
