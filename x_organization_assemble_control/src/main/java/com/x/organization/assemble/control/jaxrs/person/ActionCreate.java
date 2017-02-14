package com.x.organization.assemble.control.jaxrs.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.PersonTemplate;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInPerson;
import com.x.organization.core.entity.GenderType;
import com.x.organization.core.entity.Person;

public class ActionCreate extends ActionBase {

	protected WrapOutId execute(Business business, EffectivePerson effectivePerson, WrapInPerson wrapIn)
			throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		if (!business.personCreateAvailable(effectivePerson)) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
		Person person = inCopier.copy(wrapIn);
		if (StringUtils.isNotEmpty(wrapIn.getPassword())) {
			business.setPassword(person, wrapIn.getPassword());
		} else {
			String str = Config.personTemplate().getDefaultPassword();
			Config.personTemplate();
			Pattern pattern = Pattern.compile(PersonTemplate.RegularExpression_Script);
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				String eval = matcher.group(1);
				ScriptEngineManager factory = new ScriptEngineManager();
				ScriptEngine engine = factory.getEngineByName("nashorn");
				engine.put("person", person);
				String pass = engine.eval(eval).toString();
				business.setPassword(person, pass);
			} else {
				business.setPassword(person, str);
			}
		}
		if (Objects.equals(wrapIn.getGenderType(), GenderType.m)) {
			person.setIcon(Config.personTemplate().getDefaultIconMale());
		} else if (Objects.equals(wrapIn.getGenderType(), GenderType.f)) {
			person.setIcon(Config.personTemplate().getDefaultIconFemale());
		} else {
			person.setIcon(Config.personTemplate().getDefaultIcon());
		}
		// 设置默认管理员
		List<String> controllerList = new ArrayList<String>();
		// 查找管理员的ID如果是xadmin就忽略
		String adminId = business.person().getWithName(effectivePerson.getName());
		if (StringUtils.isNoneEmpty(adminId)) {
			controllerList.add(adminId);
		}
		person.setControllerList(controllerList);
		emc.beginTransaction(Person.class);
		emc.persist(person, CheckPersistType.all);
		emc.commit();
		ApplicationCache.notify(Person.class);
		/* 通知x_collect_service_transmit同步数据到collect */
		this.collectTransmit();
		WrapOutId wrap = new WrapOutId(person.getId());
		return wrap;
	}

}
