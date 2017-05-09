package com.x.organization.assemble.personal.jaxrs.person;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.DefaultCharset;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutOnline;
import com.x.base.core.project.x_collaboration_assemble_websocket;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.Person;
import com.x.organization.assemble.personal.Business;
import com.x.organization.assemble.personal.ThisApplication;
import com.x.organization.core.entity.GenderType;
import com.x.organization.core.express.Organization;
import com.x.organization.core.express.wrap.WrapPerson;

import net.sf.ehcache.Element;

class ActionGetComplex extends ActionBase {

	private static final String KEY_NAME = "name";
	private static final String KEY_DEPARTMENT = "department";
	private static final String KEY_COMPANY = "company";
	private static final String KEY_DEPARTMENTDUTYLIST = "departmentDutyList";
	private static final String KEY_COMPANYDUTYLIST = "companyDutyList";

	ActionResult<WrapOutPerson> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<WrapOutPerson> result = new ActionResult<>();
		WrapOutPerson wrap = null;
		if (Config.token().isInitialManager(effectivePerson.getName())) {
			wrap = new WrapOutPerson();
			Config.token().initialManagerInstance().copyTo(wrap);
		} else {
			String cacheKey = ApplicationCache.concreteCacheKey(effectivePerson.getName(), this.getClass().getName());
			Element element = cache.get(cacheKey);
			if ((null != element) && (null != element.getObjectValue())) {
				wrap = (WrapOutPerson) element.getObjectValue();
			} else {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					WrapPerson person = business.organization().person().getWithName(effectivePerson.getName());
					if (null != person) {
						wrap = expressPersonOutCopier.copy(person);
						if (StringUtils.isEmpty(wrap.getIcon())) {
							if (Objects.equals(wrap.getGenderType(), GenderType.f)) {
								wrap.setIcon(Person.ICON_FEMALE);
							} else if (Objects.equals(wrap.getGenderType(), GenderType.m)) {
								wrap.setIcon(Person.ICON_MALE);
							} else {
								wrap.setIcon(Person.ICON_UNKOWN);
							}
						}
						wrap.setIdentityList(this.listIdentity(business.organization(), wrap.getName()));
						wrap.setOnlineStatus(this.getOnlineStatus(business, wrap.getName()));
						cache.put(new Element(cacheKey, wrap));
					}
				}
			}
		}
		result.setData(wrap);
		return result;
	}

	private List<Map<String, Object>> listIdentity(Organization organization, String personName) throws Exception {
		List<Map<String, Object>> list = new ArrayList<>();
		List<String> os = organization.identity().listNameWithPerson(personName);
		for (String str : os) {
			Map<String, Object> map = new HashMap<>();
			map.put(KEY_NAME, str);
			map.put(KEY_DEPARTMENT, organization.department().getNameWithIdentity(str));
			map.put(KEY_COMPANY, organization.company().getNameWithIdentity(str));
			map.put(KEY_DEPARTMENTDUTYLIST, organization.departmentDuty().listNameWithIdentity(str));
			map.put(KEY_COMPANYDUTYLIST, organization.companyDuty().listNameWithIdentity(str));
			list.add(map);
		}
		return list;
	}

	private String getOnlineStatus(Business business, String person) throws Exception {
		WrapOutOnline online = ThisApplication.context().applications()
				.getQuery(x_collaboration_assemble_websocket.class,
						"online/person/" + URLEncoder.encode(person, DefaultCharset.name))
				.getData(WrapOutOnline.class);
		return online.getOnlineStatus();
	}
}
