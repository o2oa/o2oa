package com.x.organization.core.express.trust;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.organization.Trust;

public class TrustFactory {

	public TrustFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 根据应用和流程获取指定身份是否有委托 */
	public List<Trust> listWithIdentityObject(String application, String process, String value) throws Exception {
		return ActionListWithIdentityObject.execute(context, application, process, Arrays.asList(value));
	}

	/** 根据应用和流程获取指定身份是否有委托 */
	public List<Trust> listWithIdentityObject(String application, String process, Collection<String> values)
			throws Exception {
		return ActionListWithIdentityObject.execute(context, application, process, values);
	}

}
