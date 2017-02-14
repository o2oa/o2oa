package com.x.organization.core.express.factory;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.http.WrapOutCount;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.organization.core.express.wrap.WrapPerson;

public class PersonFactory {

	private Type collectionType = new TypeToken<ArrayList<WrapPerson>>() {
	}.getType();

	public String checkName(Object obj, String propertyName) throws Exception {
		Object o = PropertyUtils.getProperty(obj, propertyName);
		if (null != o) {
			WrapPerson wrap = this.getWithName(Objects.toString(o));
			if (null != wrap) {
				PropertyUtils.setProperty(obj, propertyName, wrap.getName());
				return wrap.getName();
			} else {
				PropertyUtils.setProperty(obj, propertyName, null);
			}
		}
		return null;
	}

	public Long countWithCompanySubDirect(String companyName) throws Exception {
		try {
			WrapOutCount wrap = AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/count/company/" + URLEncoder.encode(companyName, "UTF-8") + "/sub/direct",
					WrapOutCount.class);
			return wrap.getCount();
		} catch (Exception e) {
			throw new Exception("countWithCompanySubDirect {company:" + companyName + "} error.", e);
		}
	}

	public Long countWithCompanySubNested(String companyName) throws Exception {
		try {
			WrapOutCount wrap = AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/count/company/" + URLEncoder.encode(companyName, "UTF-8") + "/sub/nested",
					WrapOutCount.class);
			return wrap.getCount();
		} catch (Exception e) {
			throw new Exception("countWithCompanySubNested {company:" + companyName + "} error.", e);
		}
	}

	public Long countWithDepartmentSubDirect(String departmentName) throws Exception {
		try {
			WrapOutCount wrap = AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/count/department/" + URLEncoder.encode(departmentName, "UTF-8") + "/sub/direct",
					WrapOutCount.class);
			return wrap.getCount();
		} catch (Exception e) {
			throw new Exception("countWithDepartmentSubDirect {department:" + departmentName + "} error.", e);
		}
	}

	public Long countWithDepartmentSubNested(String departmentName) throws Exception {
		try {
			WrapOutCount wrap = AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/count/department/" + URLEncoder.encode(departmentName, "UTF-8") + "/sub/nested",
					WrapOutCount.class);
			return wrap.getCount();
		} catch (Exception e) {
			throw new Exception("countWithDepartmentSubNested {department:" + departmentName + "} error.", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> checkNameList(Object obj, String propertyName, boolean unique) throws Exception {
		List<String> list = new ArrayList<>();
		Object o = PropertyUtils.getProperty(obj, propertyName);
		if (null != o) {
			for (String str : (List<String>) o) {
				WrapPerson wrap = this.getWithName(str);
				if (null != wrap) {
					if (unique && list.contains(wrap.getName())) {
						continue;
					}
					list.add(wrap.getName());
				}
			}
		}
		PropertyUtils.setProperty(obj, propertyName, list);
		return list;
	}

	public WrapPerson getWithName(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/" + URLEncoder.encode(name, "UTF-8"), WrapPerson.class);
		} catch (Exception e) {
			throw new Exception("getWithName {name:" + name + "} error.", e);
		}
	}

	public WrapPerson flag(String flag) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/flag/" + URLEncoder.encode(flag, "UTF-8"), WrapPerson.class);
		} catch (Exception e) {
			throw new Exception("flag {flag:" + flag + "} error.", e);
		}
	}

	public WrapPerson getWithIdentity(String identity) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/identity/" + URLEncoder.encode(identity, "UTF-8"), WrapPerson.class);
		} catch (Exception e) {
			throw new Exception("getWithIdentity {identity:" + identity + "} error.", e);
		}
	}

	public WrapPerson getWithCredential(String credential) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/credential/" + URLEncoder.encode(credential, "UTF-8"), WrapPerson.class);
		} catch (Exception e) {
			throw new Exception("getWithCredential {credential:" + credential + "} error.", e);
		}
	}

	public List<WrapPerson> listWithDepartment(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/list/department/" + URLEncoder.encode(name, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listWithDepartment name:" + name + " error.", e);
		}
	}

	public List<WrapPerson> listWithPersonAttribute(String attribute) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/list/personattribute/" + URLEncoder.encode(attribute, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listWithPersonAttribute name:" + attribute + " error.", e);
		}
	}

	public List<WrapPerson> listWithGroupSubDirect(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/list/group/" + URLEncoder.encode(name, "UTF-8") + "/sub/direct", collectionType);
		} catch (Exception e) {
			throw new Exception("listWithGroupSubDirect name:" + name + " error.", e);
		}
	}

	public List<WrapPerson> listWithGroupSubNested(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/list/group/" + URLEncoder.encode(name, "UTF-8") + "/sub/nested", collectionType);
		} catch (Exception e) {
			throw new Exception("listWithGroupSubNested name:" + name + " error.", e);
		}
	}

	public List<WrapPerson> listPinyinInitial(String key) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/list/pinyininitial/" + URLEncoder.encode(key, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listPinyinInitial key:" + key + " error.", e);
		}
	}

	public List<WrapPerson> listLikePinyin(String key) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/list/like/pinyin/" + URLEncoder.encode(key, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listLikePinyin key:" + key + " error.", e);
		}
	}

	public List<WrapPerson> listLike(String key) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/list/like/" + URLEncoder.encode(key, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listLike key:" + key + " error.", e);
		}
	}

	public List<WrapPerson> listLoginRecent(Integer count) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"person/list/login/recent/" + count, collectionType);
		} catch (Exception e) {
			throw new Exception("ListLoginRecent count:" + count + " error.", e);
		}
	}

}
