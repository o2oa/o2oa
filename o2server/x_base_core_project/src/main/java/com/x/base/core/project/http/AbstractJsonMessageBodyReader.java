package com.x.base.core.project.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import com.x.base.core.project.gson.XGsonBuilder;

public abstract class AbstractJsonMessageBodyReader<T> implements MessageBodyReader<T> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return true;
	}

	@Override
	public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream stream)
			throws IOException, WebApplicationException {
		try {
			// UTF-8 only
			return XGsonBuilder.instance().fromJson(new InputStreamReader(stream, "UTF-8"), type);
		} catch (Exception e) {
			// 此方法在JAXRS方法之前运行,无法捕获违例
			e.printStackTrace();
			return null;
		}
	}
}