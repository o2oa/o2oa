package com.x.base.core.project.connection;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.NameValuePair;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.ListTools;

public class CipherConnectionAction {

	public static ActionResponse get(String address) throws Exception {
		return ConnectionAction.get(address, cipher());
	}

	public static ActionResponse get(Application application, String... strs) throws Exception {
		String addr = application.getUrlRoot() + trim(Applications.joinQueryUri(strs));
		return ConnectionAction.get(addr, cipher());
	}

	public static ActionResponse delete(String address) throws Exception {
		return ConnectionAction.delete(address, cipher());
	}

	public static ActionResponse delete(Application application, String... strs) throws Exception {
		String addr = application.getUrlRoot() + trim(Applications.joinQueryUri(strs));
		return ConnectionAction.delete(addr, cipher());
	}

	public static ActionResponse post(String address, Object body) throws Exception {
		return ConnectionAction.post(address, cipher(), body);
	}

	public static ActionResponse post(Object body, Application application, String... strs) throws Exception {
		String addr = application.getUrlRoot() + trim(Applications.joinQueryUri(strs));
		return ConnectionAction.post(addr, cipher(), body);
	}

	public static ActionResponse put(String address, Object body) throws Exception {
		return ConnectionAction.put(address, cipher(), body);
	}

	public static ActionResponse put(Object body, Application application, String... strs) throws Exception {
		String addr = application.getUrlRoot() + trim(Applications.joinQueryUri(strs));
		return ConnectionAction.put(addr, cipher(), body);
	}

	private static List<NameValuePair> cipher() throws Exception {
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