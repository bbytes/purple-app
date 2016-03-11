package com.bbytes.purple.exception;

public class PurpleException extends Exception {

	private static final long serialVersionUID = -2317729349964250645L;

	private String errConstant;

	public PurpleException(String message, String errConstant, Exception ex) {
		super(message,ex);
		this.errConstant = errConstant;
	}
	
	public PurpleException(String message, String errConstant) {
		super(message);
		this.errConstant = errConstant;
	}

	public String getErrConstant() {
		return errConstant;
	}

	public void setErrConstant(String errConstant) {
		this.errConstant = errConstant;
	}
}
