package com.x.organization.assemble.express.jaxrs.person;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.CacheFactory;
import com.x.organization.core.entity.Person;

import net.sf.ehcache.Ehcache;

class BaseAction extends StandardJaxrsAction {

	Ehcache cache = CacheFactory.getOrganizationCache();

	static class WoPersonListAbstract extends GsonPropertyObject {

		@FieldDescribe("个人识别名")
		private List<String> personList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

	}

	protected <T extends com.x.base.core.project.organization.Person> T convert(Business business, Person person,
			Class<T> clz) throws Exception {
		T t = clz.newInstance();
		t.setName(person.getName());
		t.setGenderType(person.getGenderType());
		t.setSignature(person.getSignature());
		t.setDescription(person.getDescription());
		t.setEmployee(person.getEmployee());
		t.setUnique(person.getUnique());
		t.setDistinguishedName(person.getDistinguishedName());
		t.setOrderNumber(person.getOrderNumber());
		if (StringUtils.isNotEmpty(person.getSuperior())) {
			Person s = business.person().pick(person.getSuperior());
			if (null != s) {
				t.setSuperior(s.getDistinguishedName());
			}
		}
		t.setMail(person.getMail());
		t.setWeixin(person.getWeixin());
		t.setQq(person.getQq());
		t.setMobile(person.getMobile());
		t.setOfficePhone(person.getOfficePhone());
		t.setBoardDate(person.getBoardDate());
		t.setBirthday(person.getBirthday());
		t.setAge(person.getAge());
		t.setQiyeweixinId(person.getQiyeweixinId());
		t.setDingdingId(person.getDingdingId());
		t.setZhengwuDingdingId(person.getZhengwuDingdingId());
		return t;

	}

}