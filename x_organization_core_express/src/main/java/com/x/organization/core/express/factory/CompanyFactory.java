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
import com.x.organization.core.express.wrap.WrapCompany;

public class CompanyFactory {

	private static Logger logger = LoggerFactory.getLogger(CompanyFactory.class);

	private static Type wrapCompanyCollectionType = new TypeToken<ArrayList<WrapCompany>>() {
	}.getType();

	public WrapCompany getWithName(String name) throws Exception {
		try {
			WrapCompany company = AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"company/" + URLEncoder.encode(name, "UTF-8"), WrapCompany.class);
			return company;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "getWithName name: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listWithPerson(String name) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class, "company/list/person/" + URLEncoder.encode(name, "UTF-8"),
					wrapCompanyCollectionType);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listWithPerson person: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<String> ListNameWithPerson(String name) throws Exception {
		List<WrapCompany> os = this.listWithPerson(name);
		List<String> list = ListTools.extractProperty(os, "name", String.class, true, true);
		return list;
	}

	public WrapCompany getWithIdentity(String name) throws Exception {
		try {
			WrapCompany company = AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"company/identity/" + URLEncoder.encode(name, "UTF-8"), WrapCompany.class);
			return company;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "getWithIdentity identity: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public WrapCompany getWithDepartment(String name) throws Exception {
		try {
			WrapCompany company = AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"company/department/" + URLEncoder.encode(name, "UTF-8"), WrapCompany.class);
			return company;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "getWithDepartment department: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listSubDirect(String name) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"company/list/" + URLEncoder.encode(name, "UTF-8") + "/sub/direct", wrapCompanyCollectionType);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listSubDirect company: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listSubNested(String name) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"company/list/" + URLEncoder.encode(name, "UTF-8") + "/sub/nested", wrapCompanyCollectionType);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listSubNested company: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public WrapCompany getSupDirect(String name) throws Exception {
		try {
			WrapCompany company = AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"company/" + URLEncoder.encode(name, "UTF-8") + "/sup/direct", wrapCompanyCollectionType);
			return company;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "getSupDirect company: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listSupNested(String name) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"company/list/" + URLEncoder.encode(name, "UTF-8") + "/sup/nested", wrapCompanyCollectionType);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listSupNested company: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listAll() throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications
					.getQuery(x_organization_assemble_express.class, "company/list/all", wrapCompanyCollectionType);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listAll error.");
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listTop() throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications
					.getQuery(x_organization_assemble_express.class, "company/list/top", wrapCompanyCollectionType);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listTop error.");
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listWithCompanyAttribute(String attributeName, String attributeValue) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"company/list/companyattribute/" + URLEncoder.encode(attributeName, "UTF-8") + "/"
							+ URLEncoder.encode(attributeValue, "UTF-8"),
					wrapCompanyCollectionType);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listWithCompanyAttribute name: {}, value: {} error.",
					attributeName, attributeValue);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listPinyinInitial(String key) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"company/list/pinyininitial/" + URLEncoder.encode(key, "UTF-8"), wrapCompanyCollectionType);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listPinyinInitial key: {} error.", key);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listLikePinyin(String key) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class,
					"company/list/like/pinyin/" + URLEncoder.encode(key, "UTF-8"), wrapCompanyCollectionType);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listLikePinyin key: {} error.", key);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listLike(String key) throws Exception {
		try {
			List<WrapCompany> list = AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class, "company/list/like/" + URLEncoder.encode(key, "UTF-8"),
					wrapCompanyCollectionType);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listLike key: {} error.", key);
			logger.error(re);
			throw re;
		}
	}
}