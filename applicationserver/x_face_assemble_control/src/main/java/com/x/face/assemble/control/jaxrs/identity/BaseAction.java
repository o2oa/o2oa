package com.x.face.assemble.control.jaxrs.identity;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.face.assemble.control.Business;
import com.x.organization.core.entity.Identity;

abstract class BaseAction extends StandardJaxrsAction {

	/** 如果唯一标识不为空,要检查唯一标识是否唯一 */
	protected boolean uniqueDuplicateWhenNotEmpty(Business business, Identity identity) throws Exception {
		if (StringUtils.isNotEmpty(identity.getUnique())) {
			if (business.entityManagerContainer().duplicateWithFlags(identity.getId(), Identity.class,
					identity.getUnique())) {
				return true;
			}
		}
		return false;
	}

	// protected void adjustJunior(Business business, Identity identity) throws
	// Exception {
	// List<String> os = new ArrayList<>();
	// for (String str : ListTools.trim(identity.getJuniorList(), true, true)) {
	// Identity o = business.identity().pick(str);
	// if (null == o) {
	// throw new ExceptionIdentityNotExist(str);
	// } else {
	// os.add(o.getId());
	// }
	// }
	// identity.setJuniorList(os);
	// }

}