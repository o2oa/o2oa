package com.x.base.core.project.connection;

import java.net.HttpURLConnection;
import java.net.URL;

import com.x.base.core.project.exception.PromptException;

public class ExceptionReadBinary extends PromptException {

	private static final long serialVersionUID = 7551134321893884285L;

	public ExceptionReadBinary(Throwable e, HttpURLConnection connection, int code) {
		super(e, "read binary input error, address: {}, method: {}, code: {}, because: {}.",
				null == connection ? null : connection.getURL(),
				null == connection ? null : connection.getRequestMethod(), code, e.getMessage());
	}

	public ExceptionReadBinary(URL url, String method, int code) {
		super("read binary input error, address: {}, method: {}, code: {}.", url, method, code);
	}

	public ExceptionReadBinary(URL url, String method, int code, byte[] bytes) {
		super("read binary input error, address: {}, method: {}, code:{}, because: {}.", url, method, code,
				new String(bytes));
	}
}
