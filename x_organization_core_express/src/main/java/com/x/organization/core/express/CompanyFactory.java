package com.x.organization.core.express;

import java.net.URLEncoder;
import java.util.List;

import com.x.base.core.exception.RunningException;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.utils.ListTools;
import com.x.organization.core.express.wrap.WrapCompany;

public class CompanyFactory {

	private static Logger logger = LoggerFactory.getLogger(CompanyFactory.class);

	CompanyFactory(Context context) {
		this.context = context;
	}

	private Context context;

	public WrapCompany getWithName(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class, "company/" + URLEncoder.encode(name, "UTF-8"))
					.getData(WrapCompany.class);
		} catch (Exception e) {
			RunningException re = new RunningException(e, "getWithName name: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listWithPerson(String name) throws Exception {
		try {
			return context.applications().getQuery(x_organization_assemble_express.class,
					"company/list/person/" + URLEncoder.encode(name, "UTF-8")).getDataAsList(WrapCompany.class);
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listWithPerson person: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<String> listNameWithPerson(String name) throws Exception {
		List<WrapCompany> os = this.listWithPerson(name);
		List<String> list = ListTools.extractProperty(os, "name", String.class, true, true);
		return list;
	}

	public List<WrapCompany> listWithPersonSupNested(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"company/list/person/" + URLEncoder.encode(name, "UTF-8") + "/sup/nested")
					.getDataAsList(WrapCompany.class);
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listWithPersonSupNested person: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<String> listNameWithPersonSupNested(String name) throws Exception {
		List<WrapCompany> os = this.listWithPersonSupNested(name);
		List<String> list = ListTools.extractProperty(os, "name", String.class, true, true);
		return list;
	}

	public WrapCompany getWithIdentity(String identityName) throws Exception {
		try {
			return context.applications().getQuery(x_organization_assemble_express.class,
					"company/identity/" + URLEncoder.encode(identityName, "UTF-8")).getData(WrapCompany.class);
		} catch (Exception e) {
			RunningException re = new RunningException(e, "getWithIdentity identity: {} error.", identityName);
			logger.error(re);
			throw re;
		}
	}

	public String getNameWithIdentity(String identityName) throws Exception {
		WrapCompany o = this.getWithIdentity(identityName);
		if (null != o) {
			return o.getName();
		} else {
			return "";
		}
	}

	public WrapCompany getWithDepartment(String name) throws Exception {
		try {
			WrapCompany company = context.applications().getQuery(x_organization_assemble_express.class,
					"company/department/" + URLEncoder.encode(name, "UTF-8")).getData(WrapCompany.class);
			return company;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "getWithDepartment department: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listSubDirect(String name) throws Exception {
		try {
			List<WrapCompany> list = context.applications()
					.getQuery(x_organization_assemble_express.class,
							"company/list/" + URLEncoder.encode(name, "UTF-8") + "/sub/direct")
					.getDataAsList(WrapCompany.class);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listSubDirect company: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listSubNested(String name) throws Exception {
		try {
			List<WrapCompany> list = context.applications()
					.getQuery(x_organization_assemble_express.class,
							"company/list/" + URLEncoder.encode(name, "UTF-8") + "/sub/nested")
					.getDataAsList(WrapCompany.class);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listSubNested company: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public WrapCompany getSupDirect(String name) throws Exception {
		try {
			WrapCompany company = context.applications().getQuery(x_organization_assemble_express.class,
					"company/" + URLEncoder.encode(name, "UTF-8") + "/sup/direct").getData(WrapCompany.class);
			return company;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "getSupDirect company: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listSupNested(String name) throws Exception {
		try {
			List<WrapCompany> list = context.applications()
					.getQuery(x_organization_assemble_express.class,
							"company/list/" + URLEncoder.encode(name, "UTF-8") + "/sup/nested")
					.getDataAsList(WrapCompany.class);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listSupNested company: {} error.", name);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listAll() throws Exception {
		try {
			ActionResponse resp = context.applications().getQuery(x_organization_assemble_express.class,
					"company/list/all");
			List<WrapCompany> list = resp.getDataAsList(WrapCompany.class);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listAll error.");
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listTop() throws Exception {
		try {
			List<WrapCompany> list = context.applications()
					.getQuery(x_organization_assemble_express.class, "company/list/top")
					.getDataAsList(WrapCompany.class);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listTop error.");
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listWithCompanyAttribute(String attributeName, String attributeValue) throws Exception {
		try {
			List<WrapCompany> list = context.applications()
					.getQuery(x_organization_assemble_express.class,
							"company/list/companyattribute/" + URLEncoder.encode(attributeName, "UTF-8") + "/"
									+ URLEncoder.encode(attributeValue, "UTF-8"))
					.getDataAsList(WrapCompany.class);
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
			List<WrapCompany> list = context.applications()
					.getQuery(x_organization_assemble_express.class,
							"company/list/pinyininitial/" + URLEncoder.encode(key, "UTF-8"))
					.getDataAsList(WrapCompany.class);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listPinyinInitial key: {} error.", key);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listLikePinyin(String key) throws Exception {
		try {
			List<WrapCompany> list = context.applications()
					.getQuery(x_organization_assemble_express.class,
							"company/list/like/pinyin/" + URLEncoder.encode(key, "UTF-8"))
					.getDataAsList(WrapCompany.class);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listLikePinyin key: {} error.", key);
			logger.error(re);
			throw re;
		}
	}

	public List<WrapCompany> listLike(String key) throws Exception {
		try {
			List<WrapCompany> list = context.applications().getQuery(x_organization_assemble_express.class,
					"company/list/like/" + URLEncoder.encode(key, "UTF-8")).getDataAsList(WrapCompany.class);
			return list;
		} catch (Exception e) {
			RunningException re = new RunningException(e, "listLike key: {} error.", key);
			logger.error(re);
			throw re;
		}
	}
}