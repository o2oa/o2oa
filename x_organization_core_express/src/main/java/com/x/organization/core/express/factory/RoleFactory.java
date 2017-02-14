package com.x.organization.core.express.factory;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.organization.core.express.wrap.WrapRole;

public class RoleFactory {

	private Type collectionType = new TypeToken<ArrayList<WrapRole>>() {
	}.getType();

	public WrapRole getWithName(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"role/list/person/" + URLEncoder.encode(name, "UTF-8"), WrapRole.class);
		} catch (Exception e) {
			throw new Exception("getWithName person{name:" + name + "} error.", e);
		}
	}

	public List<WrapRole> listWithPerson(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"role/list/person/" + URLEncoder.encode(name, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listWithPerson person{name:" + name + "} error.", e);
		}
	}

	public List<WrapRole> listWithGroup(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"role/list/group/" + URLEncoder.encode(name, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listWithGroup person{name:" + name + "} error.", e);
		}
	}

	public List<WrapRole> listPinyinInitial(String key) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"role/list/pinyininitial/" + URLEncoder.encode(key, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listPinyinInitial key:" + key + " error.", e);
		}
	}

	public List<WrapRole> listLikePinyin(String key) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"role/list/like/pinyin/" + URLEncoder.encode(key, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listLikePinyin key:" + key + " error.", e);
		}
	}

	public List<WrapRole> listLike(String key) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"role/list/like/" + URLEncoder.encode(key, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listLike key:" + key + " error.", e);
		}
	}

	public Boolean hasAny(String person, String... roles) throws Exception {
		List<WrapRole> list = this.listWithPerson(person);
		for (WrapRole o : list) {
			if (ArrayUtils.contains(roles, o.getName())) {
				return true;
			}
		}
		return false;
	}

	public Boolean hasAll(String person, String... roles) throws Exception {
		List<WrapRole> list = this.listWithPerson(person);
		List<String> names = new ArrayList<>();
		for (WrapRole o : list) {
			names.add(o.getName());
		}
		if (names.isEmpty()) {
			return false;
		}
		for (String str : roles) {
			if (!names.contains(str)) {
				return false;
			}
		}
		return true;
	}

}
