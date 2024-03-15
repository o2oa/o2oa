package com.x.organization.assemble.express.jaxrs.distinguishedname;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.organization.PersonAttribute;
import com.x.base.core.project.organization.Role;
import com.x.base.core.project.organization.UnitAttribute;
import com.x.base.core.project.organization.UnitDuty;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;

class BaseAction extends StandardJaxrsAction {

	protected CacheCategory cacheCategory = new CacheCategory(Person.class, PersonAttribute.class, Identity.class,
			Unit.class, UnitDuty.class, UnitAttribute.class, Role.class, Group.class);

	protected boolean pick(Business business, String distinguishedName) throws Exception {
		if (StringUtils.isNotEmpty(distinguishedName)) {
			if (OrganizationDefinition.isPersonDistinguishedName(distinguishedName)) {
				return null != business.person().pick(distinguishedName);
			} else if (OrganizationDefinition.isIdentityDistinguishedName(distinguishedName)) {
				return null != business.identity().pick(distinguishedName);
			} else if (OrganizationDefinition.isGroupDistinguishedName(distinguishedName)) {
				return null != business.group().pick(distinguishedName);
			} else if (OrganizationDefinition.isRoleDistinguishedName(distinguishedName)) {
				return null != business.role().pick(distinguishedName);
			} else if (OrganizationDefinition.isUnitDistinguishedName(distinguishedName)) {
				return null != business.unit().pick(distinguishedName);
			} else if (OrganizationDefinition.isUnitDutyDistinguishedName(distinguishedName)) {
				return null != business.unitDuty().pick(distinguishedName);
			} else if (OrganizationDefinition.isUnitAttributeDistinguishedName(distinguishedName)) {
				return null != business.unitAttribute().pick(distinguishedName);
			} else if (OrganizationDefinition.isPersonAttributeDistinguishedName(distinguishedName)) {
				return null != business.personAttribute().pick(distinguishedName);
			}
		}
		return false;
	}
}