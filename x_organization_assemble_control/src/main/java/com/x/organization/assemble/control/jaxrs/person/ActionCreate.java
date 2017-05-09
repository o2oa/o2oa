package com.x.organization.assemble.control.jaxrs.person;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.wrapin.WrapInPerson;
import com.x.organization.core.entity.Person;

class ActionCreate extends ActionBase {

	protected ActionResult<WrapOutId> execute(EffectivePerson effectivePerson, WrapInPerson wrapIn) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutId> result = new ActionResult<>();
			WrapOutId wrap = new WrapOutId();
			Business business = new Business(emc);
			if (!business.personCreateAvailable(effectivePerson)) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
			}
			// "id", "name", "mobile","unique", "employee", "mail",
			// "qq","weixin","display"
			this.checkName(business, wrapIn.getName(), null);
			this.checkMobile(business, wrapIn.getMobile(), null);
			this.checkUnique(business, wrapIn.getUnique(), null);
			this.checkEmployee(business, wrapIn.getEmployee(), null);
			this.checkMail(business, wrapIn.getMail(), null);
			this.checkQq(business, wrapIn.getQq(), null);
			this.checkWeixin(business, wrapIn.getWeixin(), null);
			this.checkDisplay(business, wrapIn.getDisplay(), null);

			/* 不设置默认头像,可以通过为空直接显示默认头像 */
			Person person = inCopier.copy(wrapIn);
			if (StringUtils.isNotEmpty(wrapIn.getPassword())) {
				business.person().setPassword(person, wrapIn.getPassword());
			} else {
				String str = Config.person().getPassword();
				Pattern pattern = Pattern.compile(com.x.base.core.project.server.Person.RegularExpression_Script);
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
			// 设置默认管理员
			List<String> controllerList = new ArrayList<String>();
			// 查找管理员的ID如果是xadmin就忽略
			String adminId = business.person().getWithName(effectivePerson.getName(), null);
			if (StringUtils.isNoneEmpty(adminId)) {
				controllerList.add(adminId);
			}
			person.setControllerList(controllerList);
			emc.beginTransaction(Person.class);
			emc.persist(person, CheckPersistType.all);
			emc.commit();
			/* 刷新缓存 */
			this.cacheNotify();
			/* 通知x_collect_service_transmit同步数据到collect */
			business.instrument().collect().person();
			wrap = new WrapOutId(person.getId());
			result.setData(wrap);
			return result;
		}
	}

}
