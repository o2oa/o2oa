package com.x.organization.core.express.distinguishedname;

import java.util.Arrays;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.tools.ListTools;

public class DistinguishedNameFactory {

	public DistinguishedNameFactory(AbstractContext context) {
		this.context = context;
	}

	private AbstractContext context;

	public String get(String value) throws Exception {
		List<String> os = ActionList.execute(context, Arrays.asList(value));
		if (ListTools.isEmpty(os)) {
			return "";
		} else {
			return os.get(0);
		}
	}

	public List<String> list(List<String> values) throws Exception {
		return ActionList.execute(context, values);
	}

	public List<String> list(String... values) throws Exception {
		return ActionList.execute(context, Arrays.asList(values));
	}
}
