package com.dili.alm.exceptions;

public class ApplicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -882103454262982353L;

	public ApplicationException() {
		// TODO Auto-generated constructor stub
	}

	public ApplicationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ApplicationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ApplicationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	// 这个地方重写此方法解决checked异常性能问题
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}

}
