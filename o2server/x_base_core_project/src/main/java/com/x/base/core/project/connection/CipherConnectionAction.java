package com.x.base.core.project.connection;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.tools.ListTools;

public class CipherConnectionAction {

	private CipherConnectionAction() {
	}

	public static ActionResponse get(Boolean xdebugger, String address) throws Exception {
		List<NameValuePair> headers = cipher();
		if (BooleanUtils.isTrue(xdebugger)) {
			headers.add(new NameValuePair(HttpToken.X_Debugger, true));
		}
		return ConnectionAction.get(address, headers);
	}

	public static ActionResponse get(Boolean xdebugger, Application application, String... strs) throws Exception {
		String addr = application.getUrlJaxrsRoot() + trim(Applications.joinQueryUri(strs));
		return get(xdebugger, addr);
	}

	public static byte[] getBinary(Boolean xdebugger, String address) throws Exception {
		List<NameValuePair> headers = cipher();
		if (BooleanUtils.isTrue(xdebugger)) {
			headers.add(new NameValuePair(HttpToken.X_Debugger, true));
		}
		return ConnectionAction.getBinary(address, headers);
	}

	public static byte[] getBinary(Boolean xdebugger, Application application, String... strs) throws Exception {
		String addr = application.getUrlJaxrsRoot() + trim(Applications.joinQueryUri(strs));
		return getBinary(xdebugger, addr);
	}

	public static ActionResponse delete(Boolean xdebugger, String address) throws Exception {
		List<NameValuePair> headers = cipher();
		if (BooleanUtils.isTrue(xdebugger)) {
			headers.add(new NameValuePair(HttpToken.X_Debugger, true));
		}
		return ConnectionAction.delete(address, headers);
	}

	public static ActionResponse delete(Boolean xdebugger, Application application, String... strs) throws Exception {
		String addr = application.getUrlJaxrsRoot() + trim(Applications.joinQueryUri(strs));
		return delete(xdebugger, addr);
	}

	public static byte[] deleteBinary(Boolean xdebugger, String address) throws Exception {
		List<NameValuePair> headers = cipher();
		if (BooleanUtils.isTrue(xdebugger)) {
			headers.add(new NameValuePair(HttpToken.X_Debugger, true));
		}
		return ConnectionAction.deleteBinary(address, headers);
	}

	public static byte[] deleteBinary(Boolean xdebugger, Application application, String... strs) throws Exception {
		String addr = application.getUrlJaxrsRoot() + trim(Applications.joinQueryUri(strs));
		return deleteBinary(xdebugger, addr);
	}

	public static ActionResponse post(Boolean xdebugger, String address, Object body) throws Exception {
		List<NameValuePair> headers = cipher();
		if (BooleanUtils.isTrue(xdebugger)) {
			headers.add(new NameValuePair(HttpToken.X_Debugger, true));
		}
		return ConnectionAction.post(address, headers, body);
	}

	public static ActionResponse post(Boolean xdebugger, Object body, Application application, String... strs)
			throws Exception {
		String addr = application.getUrlJaxrsRoot() + trim(Applications.joinQueryUri(strs));
		return post(xdebugger, addr, body);
	}

	public static byte[] postBinary(Boolean xdebugger, String address, Object body) throws Exception {
		List<NameValuePair> headers = cipher();
		if (BooleanUtils.isTrue(xdebugger)) {
			headers.add(new NameValuePair(HttpToken.X_Debugger, true));
		}
		return ConnectionAction.postBinary(address, headers, body);
	}

	public static byte[] postBinary(Boolean xdebugger, Object body, Application application, String... strs)
			throws Exception {
		String addr = application.getUrlJaxrsRoot() + trim(Applications.joinQueryUri(strs));
		return postBinary(xdebugger, addr, body);
	}

	public static byte[] postMultiPartBinary(Boolean xdebugger, String address, Collection<FormField> formFields,
			Collection<FilePart> fileParts) throws Exception {
		List<NameValuePair> headers = cipher();
		if (BooleanUtils.isTrue(xdebugger)) {
			headers.add(new NameValuePair(HttpToken.X_Debugger, true));
		}
		return ConnectionAction.postMultiPartBinary(address, headers, formFields, fileParts);
	}

	public static byte[] postMultiPartBinary(Boolean xdebugger, Collection<FormField> formFields,
			Collection<FilePart> fileParts, Application application, String... strs) throws Exception {
		String addr = application.getUrlJaxrsRoot() + trim(Applications.joinQueryUri(strs));
		return postMultiPartBinary(xdebugger, addr, formFields, fileParts);
	}

	public static ActionResponse put(Boolean xdebugger, String address, Object body) throws Exception {
		List<NameValuePair> headers = cipher();
		if (BooleanUtils.isTrue(xdebugger)) {
			headers.add(new NameValuePair(HttpToken.X_Debugger, true));
		}
		return ConnectionAction.put(address, headers, body);
	}

	public static ActionResponse put(Boolean xdebugger, Object body, Application application, String... strs)
			throws Exception {
		String addr = application.getUrlJaxrsRoot() + trim(Applications.joinQueryUri(strs));
		return put(xdebugger, addr, body);
	}

	public static byte[] putBinary(Boolean xdebugger, String address, Object body) throws Exception {
		List<NameValuePair> headers = cipher();
		if (BooleanUtils.isTrue(xdebugger)) {
			headers.add(new NameValuePair(HttpToken.X_Debugger, true));
		}
		return ConnectionAction.putBinary(address, headers, body);
	}

	public static byte[] putBinary(Boolean xdebugger, Object body, Application application, String... strs)
			throws Exception {
		String addr = application.getUrlJaxrsRoot() + trim(Applications.joinQueryUri(strs));
		return putBinary(xdebugger, addr, body);
	}

	public static byte[] putMultiPartBinary(Boolean xdebugger, String address, Collection<FormField> formFields,
			Collection<FilePart> fileParts) throws Exception {
		List<NameValuePair> headers = cipher();
		if (BooleanUtils.isTrue(xdebugger)) {
			headers.add(new NameValuePair(HttpToken.X_Debugger, true));
		}
		return ConnectionAction.putMultiPartBinary(address, headers, formFields, fileParts);
	}

	public static byte[] putMultiPartBinary(Boolean xdebugger, Collection<FormField> formFields,
			Collection<FilePart> fileParts, Application application, String... strs) throws Exception {
		String addr = application.getUrlJaxrsRoot() + trim(Applications.joinQueryUri(strs));
		return putMultiPartBinary(xdebugger, addr, formFields, fileParts);
	}

	public static List<NameValuePair> cipher() throws Exception {
		EffectivePerson effectivePerson = EffectivePerson.cipher(Config.token().getCipher());
		return ListTools.toList(new NameValuePair(HttpToken.X_Token, effectivePerson.getToken()));
	}

	public static String trim(String uri) {
		if (StringUtils.isEmpty(uri)) {
			return "";
		}
		if (StringUtils.startsWith(uri, "/jaxrs/")) {
			return StringUtils.substringAfter(uri, "/jaxrs/");
		}
		if (StringUtils.startsWith(uri, "/")) {
			return StringUtils.substringAfter(uri, "/");
		}
		return uri;
	}

}