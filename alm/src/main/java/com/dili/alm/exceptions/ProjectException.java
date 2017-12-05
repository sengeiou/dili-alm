package com.dili.alm.exceptions;

public class ProjectException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 481485125636785308L;

	public ProjectException() {
		super();
	}

	public ProjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ProjectException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProjectException(String message) {
		super(message);
	}

	public ProjectException(Throwable cause) {
		super(cause);
	}

	@Override
	public Throwable fillInStackTrace() {
		return super.fillInStackTrace();
	}

}
