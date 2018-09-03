package com.x.face.assemble.control.jaxrs.person;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.ListTools;
import com.x.face.assemble.control.Business;
import com.x.organization.core.entity.Person;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Person person = new Person();
			if (!business.editable(effectivePerson, person)) {
				throw new ExceptionDenyCreatePerson(effectivePerson, wi.getName());
			}
			Wi.copier.copy(wi, person);
			this.checkName(business, person.getName(), person.getId());
			this.checkMobile(business, person.getMobile(), person.getId());
			this.checkEmployee(business, person.getEmployee(), person.getId());
			if (StringUtils.isNotEmpty(person.getUnique())) {
				this.checkUnique(business, person.getUnique(), person.getId());
			}
			this.checkMail(business, person.getMail(), person.getId());
			if (StringUtils.isNotEmpty(wi.getSuperior())) {
				Person superior = business.person().pick(wi.getSuperior());
				if (null == superior) {
					throw new ExceptionSuperiorNotExist(wi.getSuperior());
				}
				person.setSuperior(superior.getId());
			}
			/** 不设置默认头像,可以通过为空直接显示默认头像 */
			if (StringUtils.isNotEmpty(wi.getPassword())) {
				business.person().setPassword(person, wi.getPassword());
			} else {
				//String str = Config.person().password();
				String str = Config.person().getPassword();
				Pattern pattern = Pattern.compile(com.x.base.core.project.config.Person.RegularExpression_Script);
				Matcher matcher = pattern.matcher(str);
				if (matcher.matches()) {
					String eval = matcher.group(1);
					ScriptEngineManager factory = new ScriptEngineManager();
					ScriptEngine engine = factory.getEngineByName("nashorn");
					engine.put("person", person);
					String pass = engine.eval(eval).toString();
					business.person().setPassword(person, pass);
				} else {
					business.person().setPassword(person, str);
				}
			}
			/** 设置默认管理员 */
			this.convertControllerList(effectivePerson, business, person);
			emc.beginTransaction(Person.class);
			emc.persist(person, CheckPersistType.all);
			emc.commit();
			/** 刷新缓存 */
			ApplicationCache.notify(Person.class);
			/** 通知x_collect_service_transmit同步数据到collect */
			business.instrument().collect().person();
			Wo wo = new Wo();
			wo.setId(person.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Person {

		private static final long serialVersionUID = 1571810726944802231L;

		static WrapCopier<Wi, Person> copier = WrapCopierFactory.wi(Wi.class, Person.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, "icon", "pinyin", "pinyinInitial", "password",
						"passwordExpiredTime", "changePasswordTime", "lastLoginTime", "lastLoginAddress",
						"lastLoginClient"));

	}

	private void convertControllerList(EffectivePerson effectivePerson, Business business, Person person)
			throws Exception {
		List<String> list = new ArrayList<>();
		if (effectivePerson.isManager()) {
			list.add(effectivePerson.getDistinguishedName());
		}
		if (ListTools.isNotEmpty(person.getControllerList())) {
			list.addAll(person.getControllerList());
		}
		if (ListTools.isNotEmpty(list)) {
			List<Person> os = business.person().pick(list);
			List<String> ids = ListTools.extractProperty(os, JpaObject.id_FIELDNAME, String.class, true, true);
			ids.remove(person.getId());
			person.setControllerList(ids);
		}
	}

}
