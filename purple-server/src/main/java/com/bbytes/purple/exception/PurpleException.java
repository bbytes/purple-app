package com.bbytes.purple.exception;

public class PurpleException extends Exception {

	private static final long serialVersionUID = -2317729349964250645L;

	public PurpleException(String str, Exception ex) {
		super(ex);
	}
}
