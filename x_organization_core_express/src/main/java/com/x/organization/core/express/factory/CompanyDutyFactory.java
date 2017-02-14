package com.x.organization.core.express.factory;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.organization.core.express.wrap.WrapCompanyDuty;

public class CompanyDutyFactory {

	private Type collectionType = new TypeToken<ArrayList<WrapCompanyDuty>>() {
	}.getType();

	public WrapCompanyDuty getWithName(String name, String company) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class, "companyduty/"
					+ URLEncoder.encode(name, "UTF-8") + "/company/" + URLEncoder.encode(company, "UTF-8"),
					WrapCompanyDuty.class);
		} catch (Exception e) {
			throw new Exception("getWithNameWithCompany name:" + name + ", company:" + company + " error.", e);
		}
	}

	public List<WrapCompanyDuty> listWithCompany(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"companyduty/list/company/" + URLEncoder.encode(name, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listWithCompany name:" + name + " error.", e);
		}
	}

	public List<WrapCompanyDuty> listWithName(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"companyduty/list/" + URLEncoder.encode(name, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listWithName name:" + name + " error.", e);
		}
	}

	public List<WrapCompanyDuty> listWithIdentity(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"companyduty/list/identity/" + URLEncoder.encode(name, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listWithIdentity name:" + name + " error.", e);
		}
	}

	public List<WrapCompanyDuty> listWithPerson(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"companyduty/list/person/" + URLEncoder.encode(name, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listWithPerson name:" + name + " error.", e);
		}
	}

}
