package com.x.server.console.server.web;

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.DeferredContentProvider;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.util.Callback;

public class Proxy extends ProxyServlet {

	private static final long serialVersionUID = 2737360000716631564L;

	@Override
	protected String rewriteTarget(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		return target(url, this.getServletConfig().getInitParameter("port"));
	}

	private String target(String url, String port) {
		int x = StringUtils.indexOf(url, ":", 8);
		int y = StringUtils.indexOf(url, "/", 8);
		if ((x > 0) && (y > 0)) {
			return url.substring(0, x) + port(url, port) + url.substring(y);
		} else if (y > 0) {
			return url.substring(0, y) + port(url, port) + url.substring(y);
		} else {
			return null;
		}
	}

	private String port(String url, String port) {
		if (StringUtils.startsWithIgnoreCase(url, "https://") || StringUtils.startsWithIgnoreCase(url, "wss://")) {
			if (StringUtils.equals(port, "443")) {
				return "";
			}
		} else if (StringUtils.startsWithIgnoreCase(url, "http://") || StringUtils.startsWithIgnoreCase(url, "ws://")) {
			if (StringUtils.equals(port, "80")) {
				return "";
			}
		}
		return ":" + port;
	}

	@Override
	protected ContentProvider proxyRequestContent(HttpServletRequest request, HttpServletResponse response,
			Request proxyRequest) throws IOException {
		ServletInputStream input = request.getInputStream();
		DeferredContentProvider provider = new DeferredContentProvider();
		input.setReadListener(newReadListener(request, response, proxyRequest, provider));
		return provider;
	}

	@Override
	protected void onResponseContent(HttpServletRequest request, HttpServletResponse response, Response proxyResponse,
			byte[] buffer, int offset, int length, Callback callback) {
		try {
			if (_log.isDebugEnabled())
				_log.debug("{} proxying content to downstream: {} bytes", getRequestId(request), length);
			StreamWriter writeListener = (StreamWriter) request.getAttribute(WRITE_LISTENER_ATTRIBUTE);
			if (writeListener == null) {
				writeListener = newWriteListener(request, proxyResponse);
				request.setAttribute(WRITE_LISTENER_ATTRIBUTE, writeListener);

				// Set the data to write before calling setWriteListener(), because
				// setWriteListener() may trigger the call to onWritePossible() on
				// a different thread and we would have a race.
				writeListener.data(buffer, offset, length, callback);

				// Setting the WriteListener triggers an invocation to onWritePossible().
				response.getOutputStream().setWriteListener(writeListener);
			} else {
				writeListener.data(buffer, offset, length, callback);
				writeListener.onWritePossible();
			}
		} catch (Throwable x) {
			callback.failed(x);
			proxyResponse.abort(x);
		}
	}
}
