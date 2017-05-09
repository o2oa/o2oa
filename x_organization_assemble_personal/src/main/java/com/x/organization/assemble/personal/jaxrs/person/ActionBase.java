package com.x.organization.assemble.personal.jaxrs.person;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.personal.wrapout.WrapOutCompanyDuty;
import com.x.organization.assemble.personal.wrapout.WrapOutDepartmentDuty;
import com.x.organization.assemble.personal.wrapout.WrapOutIdentity;
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.express.wrap.WrapPerson;

import net.sf.ehcache.Ehcache;

abstract class ActionBase extends StandardJaxrsAction {

	static BeanCopyTools<WrapPerson, WrapOutPerson> expressPersonOutCopier = BeanCopyToolsBuilder
			.create(WrapPerson.class, WrapOutPerson.class, null, WrapOutPerson.Excludes);

	static BeanCopyTools<Person, WrapOutPerson> personOutCopier = BeanCopyToolsBuilder.create(Person.class,
			WrapOutPerson.class, null, WrapOutPerson.Excludes);

	static BeanCopyTools<WrapInPerson, Person> personInCopier = BeanCopyToolsBuilder.create(WrapInPerson.class,
			Person.class, null, WrapInPerson.Excludes);

	static BeanCopyTools<Identity, WrapOutIdentity> identityOutCopier = BeanCopyToolsBuilder.create(Identity.class,
			WrapOutIdentity.class, null, WrapOutIdentity.Excludes);

	static BeanCopyTools<CompanyDuty, WrapOutCompanyDuty> companyDutyOutCopier = BeanCopyToolsBuilder
			.create(CompanyDuty.class, WrapOutCompanyDuty.class, null, WrapOutCompanyDuty.Excludes);

	static BeanCopyTools<DepartmentDuty, WrapOutDepartmentDuty> departmentDutyOutCopier = BeanCopyToolsBuilder
			.create(DepartmentDuty.class, WrapOutDepartmentDuty.class, null, WrapOutDepartmentDuty.Excludes);

	Ehcache cache = ApplicationCache.instance().getCache(Person.class);

}