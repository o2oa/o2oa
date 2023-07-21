package com.x.base.core.project.webservices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.message.Message;

import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.connection.HttpConnectionResponse;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class WebservicesClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebservicesClient.class);

    public Object[] jaxws(String wsdl, String method, Object... objects) throws Exception {
        Object[] result = null;
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        try (Client client = dcf.createClient(wsdl)) {
            result = client.invoke(method, objects);
        }
        return result;
    }

    public Object[] jaxws(Map<String, List<String>> soapHeader, String wsdl, String method, Object... objects)
            throws Exception {
        Object[] result = null;
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        try (Client client = dcf.createClient(wsdl)) {
            if (null != soapHeader) {
                client.getRequestContext().put(Message.PROTOCOL_HEADERS, soapHeader);
            }
            result = client.invoke(method, objects);
        }
        return result;
    }

    public Object[] soap(String wsdl, String method, Object... objects) throws Exception {
        return jaxws(wsdl, method, objects);
    }

    public Object[] soap(Map<String, List<String>> soapHeader, String wsdl, String method, Object... objects)
            throws Exception {
        return jaxws(soapHeader, wsdl, method, objects);
    }

    public String jaxwsXml(String url, String soapXML) throws Exception {
        List<NameValuePair> heads = Arrays.asList(new NameValuePair(ConnectionAction.CONTENT_TYPE, MediaType.TEXT_XML));
        return HttpConnection.postAsString(url, heads, soapXML);
    }

    public HttpConnectionResponse restful(String method, String url, Map<String, String> heads, String body,
            int connectTimeout, int readTimeout) {
        if (StringUtils.equalsAnyIgnoreCase(method, ConnectionAction.METHOD_GET)) {
            return get(url, heads, connectTimeout, readTimeout);
        }
        if (StringUtils.equalsAnyIgnoreCase(method, ConnectionAction.METHOD_POST)) {
            return post(url, heads, body, connectTimeout, readTimeout);
        }
        if (StringUtils.equalsAnyIgnoreCase(method, ConnectionAction.METHOD_DELETE)) {
            return delete(url, heads, connectTimeout, readTimeout);
        }
        if (StringUtils.equalsAnyIgnoreCase(method, ConnectionAction.METHOD_PUT)) {
            return put(url, heads, body, connectTimeout, readTimeout);
        }
        return null;
    }

    private HttpConnectionResponse get(String url, Map<String, String> heads, int connectTimeout, int readTimeout) {
        List<NameValuePair> list = jaxrsHeads(heads);
        return HttpConnection.get(url, list, connectTimeout, readTimeout, o -> {
            HttpConnectionResponse response = new HttpConnectionResponse();
            try {
                response.setResponseCode(o.getResponseCode());
                response.setBody(HttpConnection.readResultString(o));
                o.getHeaderFields().keySet().forEach(k -> response.getHeaders().put(k, o.getHeaderField(k)));
                return response;
            } catch (Exception e) {
                LOGGER.error(e);
                return null;
            }
        });
    }

    private HttpConnectionResponse post(String url, Map<String, String> heads, String body, int connectTimeout,
            int readTimeout) {
        List<NameValuePair> list = jaxrsHeads(heads);
        return HttpConnection.post(url, list, body, connectTimeout, readTimeout, o -> {
            HttpConnectionResponse response = new HttpConnectionResponse();
            try {
                response.setResponseCode(o.getResponseCode());
                response.setBody(HttpConnection.readResultString(o));
                o.getHeaderFields().keySet().forEach(k -> response.getHeaders().put(k, o.getHeaderField(k)));
                return response;
            } catch (Exception e) {
                LOGGER.error(e);
                return null;
            }
        });
    }

    private HttpConnectionResponse delete(String url, Map<String, String> heads, int connectTimeout, int readTimeout) {
        List<NameValuePair> list = jaxrsHeads(heads);
        return HttpConnection.delete(url, list, connectTimeout, readTimeout, o -> {
            HttpConnectionResponse response = new HttpConnectionResponse();
            try {
                response.setResponseCode(o.getResponseCode());
                response.setBody(HttpConnection.readResultString(o));
                o.getHeaderFields().keySet().forEach(k -> response.getHeaders().put(k, o.getHeaderField(k)));
                return response;
            } catch (Exception e) {
                LOGGER.error(e);
                return null;
            }
        });
    }

    private HttpConnectionResponse put(String url, Map<String, String> heads, String body, int connectTimeout,
            int readTimeout) {
        List<NameValuePair> list = jaxrsHeads(heads);
        return HttpConnection.put(url, list, body, connectTimeout, readTimeout, o -> {
            HttpConnectionResponse response = new HttpConnectionResponse();
            try {
                response.setResponseCode(o.getResponseCode());
                response.setBody(HttpConnection.readResultString(o));
                o.getHeaderFields().keySet().forEach(k -> response.getHeaders().put(k, o.getHeaderField(k)));
                return response;
            } catch (Exception e) {
                LOGGER.error(e);
                return null;
            }
        });
    }

    private List<NameValuePair> jaxrsHeads(Map<String, String> heads) {
        List<NameValuePair> list = new ArrayList<>();
        if (null != heads) {
            heads.entrySet().forEach(en -> list.add(new NameValuePair(en.getKey(), en.getValue())));
        }
        return list;
    }

}
