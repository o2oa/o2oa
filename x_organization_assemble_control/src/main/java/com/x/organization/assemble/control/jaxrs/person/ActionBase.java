package com.x.organization.assemble.control.jaxrs.person;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInPerson;
import com.x.organization.assemble.control.wrapout.WrapOutPerson;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentAttribute;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.GenderType;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Role;

class ActionBase extends StandardJaxrsAction {
	protected static BeanCopyTools<Person, WrapOutPerson> outCopier = BeanCopyToolsBuilder.create(Person.class,
			WrapOutPerson.class, null, WrapOutPerson.Excludes);

	protected static BeanCopyTools<WrapInPerson, Person> inCopier = BeanCopyToolsBuilder.create(WrapInPerson.class,
			Person.class);

	protected void updateIcon(WrapOutPerson wrap) throws Exception {
		if (StringUtils.isEmpty(wrap.getIcon())) {
			if (Objects.equals(GenderType.f, wrap.getGenderType())) {
				wrap.setIcon(com.x.base.core.project.server.Person.ICON_FEMALE);
			} else if (Objects.equals(GenderType.m, wrap.getGenderType())) {
				wrap.setIcon(com.x.base.core.project.server.Person.ICON_MALE);
			} else {
				wrap.setIcon(com.x.base.core.project.server.Person.ICON_UNKOWN);
			}
		}
	}

	protected void updateIcon(List<WrapOutPerson> wraps) throws Exception {
		for (WrapOutPerson o : wraps) {
			updateIcon(o);
		}
	}

	protected void checkName(Business business, String name, String excludeId) throws Exception {
		// "id", "name", "unique", "employee", "mobile", "mail",
		// "qq","weixin","display"
		if (StringUtils.isEmpty(name) || (!StringTools.isSimply(name)) || Config.token().isInitialManager(name)) {
			throw new InvalidNameException(name);
		}
		if (null != business.entityManagerContainer().find(name, Person.class)) {
			throw new NameDuplicateException(name, "数据库标志");
		}
		if (StringUtils.isNotEmpty(business.person().getWithName(name, excludeId))) {
			throw new NameDuplicateException(name, "名称");
		}
		if (StringUtils.isNotEmpty(business.person().getWithUnique(name, excludeId))) {
			throw new NameDuplicateException(name, "员工唯一标志");
		}
		if (StringUtils.isNotEmpty(business.person().getWithEmployee(name, excludeId))) {
			throw new NameDuplicateException(name, "员工号");
		}
		if (StringUtils.isNotEmpty(business.person().getWithMobile(name, excludeId))) {
			throw new NameDuplicateException(name, "手机号");
		}
		if (StringUtils.isNotEmpty(business.person().getWithMail(name, excludeId))) {
			throw new NameDuplicateException(name, "邮件地址");
		}
		if (StringUtils.isNotEmpty(business.person().getWithQq(name, excludeId))) {
			throw new NameDuplicateException(name, "QQ号");
		}
		if (StringUtils.isNotEmpty(business.person().getWithWeixin(name, excludeId))) {
			throw new NameDuplicateException(name, "微信号");
		}
		if (StringUtils.isNotEmpty(business.person().getWithDisplay(name, excludeId))) {
			throw new NameDuplicateException(name, "显示名");
		}
	}

	protected void checkMobile(Business business, String mobile, String excludeId) throws Exception {
		// "id", "name", "unique", "employee", "mobile", "mail",
		// "qq","weixin","display"
		// 手机号不可能和id,mail重复
		if (StringUtils.isEmpty(mobile) || (!StringTools.isMobile(mobile))) {
			throw new InvalidMobileException(mobile);
		}
		if (StringUtils.isNotEmpty(business.person().getWithName(mobile, excludeId))) {
			throw new MobileDuplicateException(mobile, "名称");
		}
		if (StringUtils.isNotEmpty(business.person().getWithUnique(mobile, excludeId))) {
			throw new MobileDuplicateException(mobile, "员工唯一标志");
		}
		if (StringUtils.isNotEmpty(business.person().getWithEmployee(mobile, excludeId))) {
			throw new MobileDuplicateException(mobile, "员工号");
		}
		if (StringUtils.isNotEmpty(business.person().getWithMobile(mobile, excludeId))) {
			throw new MobileDuplicateException(mobile, "手机号");
		}
		if (StringUtils.isNotEmpty(business.person().getWithQq(mobile, excludeId))) {
			throw new MobileDuplicateException(mobile, "QQ号");
		}
		if (StringUtils.isNotEmpty(business.person().getWithWeixin(mobile, excludeId))) {
			throw new MobileDuplicateException(mobile, "微信号");
		}
		if (StringUtils.isNotEmpty(business.person().getWithDisplay(mobile, excludeId))) {
			throw new MobileDuplicateException(mobile, "显示名");
		}
	}

	protected void checkUnique(Business business, String unique, String excludeId) throws Exception {
		// "id", "name", "unique", "employee", "mobile", "mail",
		// "qq","weixin","display"
		// 手机号不可能和id,mail重复
		if (StringUtils.isNotEmpty(unique)) {
			if (!StringTools.isSimply(unique)) {
				throw new InvalidUniqueException(unique);
			}
			if (null != business.entityManagerContainer().find(unique, Person.class)) {
				throw new UniqueDuplicateException(unique, "数据库标志");
			}
			if (StringUtils.isNotEmpty(business.person().getWithName(unique, excludeId))) {
				throw new UniqueDuplicateException(unique, "名称");
			}
			if (StringUtils.isNotEmpty(business.person().getWithUnique(unique, excludeId))) {
				throw new UniqueDuplicateException(unique, "员工唯一标志");
			}
			if (StringUtils.isNotEmpty(business.person().getWithEmployee(unique, excludeId))) {
				throw new UniqueDuplicateException(unique, "员工号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithMobile(unique, excludeId))) {
				throw new UniqueDuplicateException(unique, "手机号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithMail(unique, excludeId))) {
				throw new UniqueDuplicateException(unique, "邮件地址");
			}
			if (StringUtils.isNotEmpty(business.person().getWithQq(unique, excludeId))) {
				throw new UniqueDuplicateException(unique, "QQ号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithWeixin(unique, excludeId))) {
				throw new UniqueDuplicateException(unique, "微信号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithDisplay(unique, excludeId))) {
				throw new UniqueDuplicateException(unique, "显示名");
			}
		}
	}

	protected void checkEmployee(Business business, String employee, String excludeId) throws Exception {
		// "id", "name", "unique", "employee", "mobile", "mail",
		// "qq","weixin","display"
		if (StringUtils.isNotEmpty(employee)) {
			if (!StringTools.isSimply(employee)) {
				throw new InvalidEmployeeException(employee);
			}
			if (null != business.entityManagerContainer().find(employee, Person.class)) {
				throw new EmployeeDuplicateException(employee, "数据库标志");
			}
			if (StringUtils.isNotEmpty(business.person().getWithName(employee, excludeId))) {
				throw new EmployeeDuplicateException(employee, "名称");
			}
			if (StringUtils.isNotEmpty(business.person().getWithUnique(employee, excludeId))) {
				throw new EmployeeDuplicateException(employee, "员工唯一标志");
			}
			if (StringUtils.isNotEmpty(business.person().getWithEmployee(employee, excludeId))) {
				throw new EmployeeDuplicateException(employee, "员工号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithMobile(employee, excludeId))) {
				throw new EmployeeDuplicateException(employee, "手机号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithMail(employee, excludeId))) {
				throw new EmployeeDuplicateException(employee, "邮件地址");
			}
			if (StringUtils.isNotEmpty(business.person().getWithQq(employee, excludeId))) {
				throw new EmployeeDuplicateException(employee, "QQ号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithWeixin(employee, excludeId))) {
				throw new EmployeeDuplicateException(employee, "微信号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithDisplay(employee, excludeId))) {
				throw new EmployeeDuplicateException(employee, "显示名");
			}
		}
	}

	protected void checkMail(Business business, String mail, String excludeId) throws Exception {
		// "id", "name", "unique", "employee", "mobile", "mail",
		// "qq","weixin","display"
		// 邮件不可能和手机号重复
		if (StringUtils.isNotEmpty(mail)) {
			if (!StringTools.isMail(mail)) {
				throw new InvalidMailException(mail);
			}
			if (null != business.entityManagerContainer().find(mail, Person.class)) {
				throw new MailDuplicateException(mail, "数据库标志");
			}
			if (StringUtils.isNotEmpty(business.person().getWithName(mail, excludeId))) {
				throw new MailDuplicateException(mail, "名称");
			}
			if (StringUtils.isNotEmpty(business.person().getWithUnique(mail, excludeId))) {
				throw new MailDuplicateException(mail, "员工唯一标志");
			}
			if (StringUtils.isNotEmpty(business.person().getWithEmployee(mail, excludeId))) {
				throw new MailDuplicateException(mail, "员工号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithMobile(mail, excludeId))) {
				throw new MailDuplicateException(mail, "手机号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithQq(mail, excludeId))) {
				throw new MailDuplicateException(mail, "QQ号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithWeixin(mail, excludeId))) {
				throw new MailDuplicateException(mail, "微信号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithDisplay(mail, excludeId))) {
				throw new MailDuplicateException(mail, "显示名");
			}
		}
	}

	protected void checkQq(Business business, String qq, String excludeId) throws Exception {
		// "id", "name", "unique", "employee", "mobile", "mail",
		// "qq","weixin","display"
		if (StringUtils.isNotEmpty(qq)) {
			if (null != business.entityManagerContainer().find(qq, Person.class)) {
				throw new QqDuplicateException(qq, "数据库标志");
			}
			if (StringUtils.isNotEmpty(business.person().getWithName(qq, excludeId))) {
				throw new QqDuplicateException(qq, "名称");
			}
			if (StringUtils.isNotEmpty(business.person().getWithUnique(qq, excludeId))) {
				throw new QqDuplicateException(qq, "员工唯一标志");
			}
			if (StringUtils.isNotEmpty(business.person().getWithEmployee(qq, excludeId))) {
				throw new QqDuplicateException(qq, "员工号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithMobile(qq, excludeId))) {
				throw new QqDuplicateException(qq, "手机号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithMail(qq, excludeId))) {
				throw new QqDuplicateException(qq, "邮件地址");
			}
			if (StringUtils.isNotEmpty(business.person().getWithQq(qq, excludeId))) {
				throw new QqDuplicateException(qq, "QQ号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithWeixin(qq, excludeId))) {
				throw new QqDuplicateException(qq, "微信号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithDisplay(qq, excludeId))) {
				throw new QqDuplicateException(qq, "显示名");
			}
		}
	}

	protected void checkWeixin(Business business, String weixin, String excludeId) throws Exception {
		// "id", "name", "unique", "employee", "mobile", "mail",
		// "qq","weixin","display"
		/* weixin不用检查格式 */
		if (StringUtils.isNotEmpty(weixin)) {
			if (null != business.entityManagerContainer().find(weixin, Person.class)) {
				throw new WeixinDuplicateException(weixin, "数据库标志");
			}
			if (StringUtils.isNotEmpty(business.person().getWithName(weixin, excludeId))) {
				throw new WeixinDuplicateException(weixin, "名称");
			}
			if (StringUtils.isNotEmpty(business.person().getWithUnique(weixin, excludeId))) {
				throw new WeixinDuplicateException(weixin, "员工唯一标志");
			}
			if (StringUtils.isNotEmpty(business.person().getWithEmployee(weixin, excludeId))) {
				throw new WeixinDuplicateException(weixin, "员工号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithMobile(weixin, excludeId))) {
				throw new WeixinDuplicateException(weixin, "手机号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithMail(weixin, excludeId))) {
				throw new WeixinDuplicateException(weixin, "邮件地址");
			}
			if (StringUtils.isNotEmpty(business.person().getWithQq(weixin, excludeId))) {
				throw new WeixinDuplicateException(weixin, "QQ号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithWeixin(weixin, excludeId))) {
				throw new WeixinDuplicateException(weixin, "微信号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithDisplay(weixin, excludeId))) {
				throw new WeixinDuplicateException(weixin, "显示名");
			}
		}
	}

	protected void checkDisplay(Business business, String display, String excludeId) throws Exception {
		// "id", "name", "unique", "employee", "mobile", "mail",
		// "qq","weixin","display"
		if (StringUtils.isNotEmpty(display)) {
			if (!StringTools.isSimply(display)) {
				throw new InvalidDisplayException(display);
			}
			if (null != business.entityManagerContainer().find(display, Person.class)) {
				throw new DisplayDuplicateException(display, "数据库标志");
			}
			if (StringUtils.isNotEmpty(business.person().getWithName(display, excludeId))) {
				throw new DisplayDuplicateException(display, "名称");
			}
			if (StringUtils.isNotEmpty(business.person().getWithUnique(display, excludeId))) {
				throw new DisplayDuplicateException(display, "员工唯一标志");
			}
			if (StringUtils.isNotEmpty(business.person().getWithEmployee(display, excludeId))) {
				throw new DisplayDuplicateException(display, "员工号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithMobile(display, excludeId))) {
				throw new DisplayDuplicateException(display, "手机号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithMail(display, excludeId))) {
				throw new DisplayDuplicateException(display, "邮件地址");
			}
			if (StringUtils.isNotEmpty(business.person().getWithQq(display, excludeId))) {
				throw new DisplayDuplicateException(display, "QQ号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithWeixin(display, excludeId))) {
				throw new DisplayDuplicateException(display, "微信号");
			}
			if (StringUtils.isNotEmpty(business.person().getWithDisplay(display, excludeId))) {
				throw new DisplayDuplicateException(display, "显示名");
			}
		}
	}

	protected void cacheNotify() throws Exception {
		ApplicationCache.notify(Person.class);
		ApplicationCache.notify(Group.class);
		ApplicationCache.notify(Role.class);
		ApplicationCache.notify(Company.class);
		ApplicationCache.notify(Department.class);
		ApplicationCache.notify(Identity.class);
		ApplicationCache.notify(PersonAttribute.class);
		ApplicationCache.notify(CompanyAttribute.class);
		ApplicationCache.notify(CompanyDuty.class);
		ApplicationCache.notify(DepartmentAttribute.class);
		ApplicationCache.notify(DepartmentDuty.class);
	}
}