package com.bbytes.purple.exception;

public class PurpleNoResultException extends Exception {

	private static final long serialVersionUID = -2317729349964250645L;

	private String errConstant;

	public PurpleNoResultException(String message, String errConstant, Throwable ex) {
		super(message, ex);
		this.errConstant = errConstant;
	}

	public PurpleNoResultException() {
	}

	public String getErrConstant() {
		return errConstant;
	}

	public void setErrConstant(String errConstant) {
		this.errConstant = errConstant;
	}
}
