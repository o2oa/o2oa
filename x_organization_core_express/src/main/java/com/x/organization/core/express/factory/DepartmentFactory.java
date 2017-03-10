package com.x.organization.core.express.factory;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.exception.RunningException;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.base.core.utils.ListTools;
import com.x.organization.core.express.wrap.WrapDepartment;

public class DepartmentFactory {

	private static Logger logger = LoggerFactory.getLogger(DepartmentFactory.class);

	private static Type wrapDepartmentCollectionType = new TypeToken<ArrayList<WrapDepartment>>() {
	}.getType();

	public WrapDepartment getWithName(String name) throws Exception {
		try {
			WrapDepartment department = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class, "department/" + URLEncoder.encode(name, "UTF-8"),
					WrapDepartment.class);
			return department;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "getWithName name: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapDepartment> listAll() throws Exception {
		try {
			List<WrapDepartment> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class, "department/list/all", wrapDepartmentCollectionType);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listAll error.");
			logger.error(re);
			throw re;
		}
	}

	public List<WrapDepartment> listWithPerson(String name) throws Exception {
		try {
			List<WrapDepartment> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class, "department/list/person/" + URLEncoder.encode(name, "UTF-8"),
					wrapDepartmentCollectionType);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listWithPerson person: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<String> ListNameWithPerson(String name) throws Exception {
		List<WrapDepartment> os = this.listWithPerson(name);
		List<String> list = ListTools.extractProperty(os, "name", String.class, true, true);
		return list;
	}

	public WrapDepartment getWithIdentity(String name) throws Exception {
		try {
			WrapDepartment department = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class, "department/identity/" + URLEncoder.encode(name, "UTF-8"),
					WrapDepartment.class);
			return department;
		} catch (Exception e) {
			throw new Exception("getWithIdentity {name:" + name + "} error.", e);
		}
	}

	public WrapDepartment getSupDirect(String name) throws Exception {
		try {
			WrapDepartment department = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"department/" + URLEncoder.encode(name, "UTF-8") + "/sup/direct", WrapDepartment.class);
			return department;
		} catch (Exception e) {
			throw new Exception("getSupDirect {name:" + name + "} error.", e);
		}
	}

	public List<WrapDepartment> listSupNested(String name) throws Exception {
		try {
			List<WrapDepartment> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"department/list/" + URLEncoder.encode(name, "UTF-8") + "/sup/nested",
					wrapDepartmentCollectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listSupNested person{name:" + name + "} error.", e);
		}
	}

	public List<WrapDepartment> listSubDirect(String name) throws Exception {
		try {
			List<WrapDepartment> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"department/list/" + URLEncoder.encode(name, "UTF-8") + "/sub/direct",
					wrapDepartmentCollectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listSubDirect person{name:" + name + "} error.", e);
		}
	}

	public List<WrapDepartment> listSubNested(String name) throws Exception {
		try {
			List<WrapDepartment> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"department/list/" + URLEncoder.encode(name, "UTF-8") + "/sub/nested",
					wrapDepartmentCollectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listSubNested person{name:" + name + "} error.", e);
		}
	}

	public List<WrapDepartment> listTopWithCompany(String name) throws Exception {
		try {
			List<WrapDepartment> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"department/list/company/" + URLEncoder.encode(name, "UTF-8") + "/top",
					wrapDepartmentCollectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listTopWithCompany person{name:" + name + "} error.", e);
		}
	}

	public List<WrapDepartment> listWithCompanySubNested(String name) throws Exception {
		try {
			List<WrapDepartment> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"department/list/company/" + URLEncoder.encode(name, "UTF-8") + "/sub/nested",
					wrapDepartmentCollectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listWithCompanySubNested person{name:" + name + "} error.", e);
		}
	}

	public List<WrapDepartment> listWithDepartmentAttribute(String attributeName, String attributeValue)
			throws Exception {
		try {
			List<WrapDepartment> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"department/list/departmentAttribute/" + URLEncoder.encode(attributeName, "UTF-8") + "/"
							+ URLEncoder.encode(attributeValue, "UTF-8"),
					wrapDepartmentCollectionType);
			return list;
		} catch (Exception e) {
			throw new Exception(
					"listWithDepartmentAttribute{name:" + attributeName + ", value:" + attributeValue + "} error.", e);
		}
	}

	public List<WrapDepartment> listPinyinInitial(String key) throws Exception {
		try {
			List<WrapDepartment> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"department/list/pinyininitial/" + URLEncoder.encode(key, "UTF-8"), wrapDepartmentCollectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listPinyinInitial person error.", e);
		}
	}

	public List<WrapDepartment> listLikePinyin(String key) throws Exception {
		try {
			List<WrapDepartment> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"department/list/like/pinyin/" + URLEncoder.encode(key, "UTF-8"), wrapDepartmentCollectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listLikePinyin person error.", e);
		}
	}

	public List<WrapDepartment> listLike(String key) throws Exception {
		try {
			List<WrapDepartment> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class, "department/list/like/" + URLEncoder.encode(key, "UTF-8"),
					wrapDepartmentCollectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listLike person error.", e);
		}
	}

}
