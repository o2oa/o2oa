package com.x.organization.core.express.factory;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.organization.core.express.wrap.WrapCompany;

public class CompanyFactory {

	private Type collectionType = new TypeToken<ArrayList<WrapCompany>>() {
	}.getType();

	public WrapCompany getWithName(String name) throws Exception {
		try {
			WrapCompany company = AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"company/" + URLEncoder.encode(name, "UTF-8"), WrapCompany.class);
			return company;
		} catch (Exception e) {
			throw new Exception("getWithName {name:" + name + "} error.", e);
		}
	}

	public List<WrapCompany> listWithPerson(String name) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class, "company/list/person/" + URLEncoder.encode(name, "UTF-8"),
					collectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listWithPerson person{name:" + name + "} error.", e);
		}
	}

	public WrapCompany getWithIdentity(String name) throws Exception {
		try {
			WrapCompany company = AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"company/identity/" + URLEncoder.encode(name, "UTF-8"), WrapCompany.class);
			return company;
		} catch (Exception e) {
			throw new Exception("getWithIdentity {name:" + name + "} error.", e);
		}
	}

	public WrapCompany getWithDepartment(String name) throws Exception {
		try {
			WrapCompany company = AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"company/department/" + URLEncoder.encode(name, "UTF-8"), WrapCompany.class);
			return company;
		} catch (Exception e) {
			throw new Exception("getWithDepartment {name:" + name + "} error.", e);
		}
	}

	public List<WrapCompany> listSubDirect(String name) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"company/list/" + URLEncoder.encode(name, "UTF-8") + "/sub/direct", collectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listSubDirect person{name:" + name + "} error.", e);
		}
	}

	public List<WrapCompany> listSubNested(String name) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"company/list/" + URLEncoder.encode(name, "UTF-8") + "/sub/nested", collectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listSubNested person{name:" + name + "} error.", e);
		}
	}

	public WrapCompany getSupDirect(String name) throws Exception {
		try {
			WrapCompany company = AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"company/" + URLEncoder.encode(name, "UTF-8") + "/sup/direct", collectionType);
			return company;
		} catch (Exception e) {
			throw new Exception("getSupDirect person{name:" + name + "} error.", e);
		}
	}

	public List<WrapCompany> listSupNested(String name) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"company/list/" + URLEncoder.encode(name, "UTF-8") + "/sup/nested", collectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listSupNested person{name:" + name + "} error.", e);
		}
	}

	public List<WrapCompany> listAll() throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications
					.getQuery(x_organization_assemble_express.class, "company/list/all", collectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listAll error.", e);
		}
	}

	public List<WrapCompany> listTop() throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications
					.getQuery(x_organization_assemble_express.class, "company/list/top", collectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listTop error.", e);
		}
	}

	public List<WrapCompany> listWithCompanyAttribute(String attributeName, String attributeValue) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"company/list/companyattribute/" + URLEncoder.encode(attributeName, "UTF-8") + "/"
							+ URLEncoder.encode(attributeValue, "UTF-8"),
					collectionType);
			return list;
		} catch (Exception e) {
			throw new Exception(
					"listWithCompanyAttribute{name:" + attributeName + ", value:" + attributeValue + "} error.", e);
		}
	}

	public List<WrapCompany> listPinyinInitial(String key) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"company/list/pinyininitial/" + URLEncoder.encode(key, "UTF-8"), collectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listPinyinInitial person error.", e);
		}
	}

	public List<WrapCompany> listLikePinyin(String key) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"company/list/like/pinyin/" + URLEncoder.encode(key, "UTF-8"), collectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listLikePinyin person error.", e);
		}
	}

	public List<WrapCompany> listLike(String key) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class, "company/list/like/" + URLEncoder.encode(key, "UTF-8"),
					collectionType);
			return list;
		} catch (Exception e) {
			throw new Exception("listLike person error.", e);
		}
	}
}