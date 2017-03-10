package com.x.organization.core.express.factory;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.exception.RunningException;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.base.core.utils.ListTools;
import com.x.organization.core.express.wrap.WrapRole;

public class RoleFactory {

	private static Logger logger = LoggerFactory.getLogger(RoleFactory.class);

	private Type wrapRoleCollectionType = new TypeToken<ArrayList<WrapRole>>() {
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
					"role/list/person/" + URLEncoder.encode(name, "UTF-8"), wrapRoleCollectionType);
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listWithPerson person: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<String> listNameWithPerson(String name) throws Exception {
		List<WrapRole> os = this.listWithPerson(name);
		List<String> list = ListTools.extractProperty(os, "name", String.class, true, true);
		return list;
	}

	public List<WrapRole> listWithGroup(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"role/list/group/" + URLEncoder.encode(name, "UTF-8"), wrapRoleCollectionType);
		} catch (Exception e) {
			throw new Exception("listWithGroup person{name:" + name + "} error.", e);
		}
	}

	public List<WrapRole> listPinyinInitial(String key) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"role/list/pinyininitial/" + URLEncoder.encode(key, "UTF-8"), wrapRoleCollectionType);
		} catch (Exception e) {
			throw new Exception("listPinyinInitial key:" + key + " error.", e);
		}
	}

	public List<WrapRole> listLikePinyin(String key) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"role/list/like/pinyin/" + URLEncoder.encode(key, "UTF-8"), wrapRoleCollectionType);
		} catch (Exception e) {
			throw new Exception("listLikePinyin key:" + key + " error.", e);
		}
	}

	public List<WrapRole> listLike(String key) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"role/list/like/" + URLEncoder.encode(key, "UTF-8"), wrapRoleCollectionType);
		} catch (Exception e) {
			throw new Exception("listLike key:" + key + " error.", e);
		}
	}

	public Boolean hasAny(String person, String... roles) throws Exception {
		List<String> list = this.listNameWithPerson(person);
		return ListTools.containsAny(list, Arrays.asList(roles));
	}

	public Boolean hasAll(String person, String... roles) throws Exception {
		List<String> list = this.listNameWithPerson(person);
		if (list.isEmpty()) {
			return false;
		}
		return ListTools.containsAll(list, Arrays.asList(roles));
	}

}
