package com.x.organization.assemble.control.alpha.jaxrs.company;

import com.x.base.core.exception.PromptException;

class ExceptionDenyDeleteBecauseReferenced extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyDeleteBecauseReferenced(String name, Long countSubCompanyNested, Long countSubDepartmentNested,
			Long countCompanyAttribute, Long countCompanyDuty) {
		super("删除公司: {}, 失败, 存在{}个子公司, {}个子部门, {}个公司属性, {}个公司职务.", name, countSubCompanyNested,
				countSubDepartmentNested, countCompanyAttribute, countCompanyDuty);
	}
}
