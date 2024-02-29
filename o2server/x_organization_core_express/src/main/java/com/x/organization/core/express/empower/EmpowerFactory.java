package com.x.organization.core.express.empower;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.organization.Empower;

public class EmpowerFactory {

	public EmpowerFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	/** 根据应用和流程获取指定身份是否有委托 */
	public List<Empower> listWithIdentityObject(String application, String edition, String process, String work,
			String value) throws Exception {
		return ActionListWithIdentityObject.execute(context, application, edition, process, work, Arrays.asList(value));
	}

	/** 根据应用和流程获取指定身份是否有委托 */
	public List<Empower> listWithIdentityObject(String application, String edition, String process, String work,
			List<String> values) throws Exception {
		return ActionListWithIdentityObject.execute(context, application, edition, process, work, values);
	}

}
