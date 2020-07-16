package com.x.base.core.project.connection;

import java.net.HttpURLConnection;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDeleteBinary extends PromptException {

	private static final long serialVersionUID = 7551134321893884285L;

	public ExceptionDeleteBinary(Throwable e, HttpURLConnection connection) {
		super(e, "delete binary error, address: {}, method: {}, because: {}.",
				null == connection ? null : connection.getURL(),
				null == connection ? null : connection.getRequestMethod(), e.getMessage());
	}

	public ExceptionDeleteBinary(HttpURLConnection connection) {
		super("delete binary error, address: {}, method: {}.", null == connection ? null : connection.getURL(),
				null == connection ? null : connection.getRequestMethod());
	}

}
