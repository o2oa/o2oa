package com.x.crm.assemble.control.jaxrs.customer;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.crm.core.entity.CustomerBaseInfo;

public class BaseAction extends StandardJaxrsAction {

	public static class _WoId extends WoId {

	}
	
	public static class Wo extends CustomerBaseInfo {
		private static final long serialVersionUID = 1L;
	}

	public static class Wi extends CustomerBaseInfo {
		private static final long serialVersionUID = 976190741257138060L;
		static WrapCopier<Wi, CustomerBaseInfo> copier = WrapCopierFactory.wi(Wi.class, CustomerBaseInfo.class, null, ListTools.toList(JpaObject.FieldsUnmodify));
	}
}
