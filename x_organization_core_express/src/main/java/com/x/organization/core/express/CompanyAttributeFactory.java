package com.x.organization.core.express;

import java.net.URLEncoder;
import java.util.List;

import com.x.base.core.project.Context;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.organization.core.express.wrap.WrapCompanyAttribute;
import com.x.organization.core.express.wrap.WrapDepartmentDuty;

public class CompanyAttributeFactory {

	CompanyAttributeFactory(Context context) {
		this.context = context;
	}

	private Context context;

	public WrapCompanyAttribute getWithName(String name, String companyName) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class, "companyattribute/"
							+ URLEncoder.encode(name, "UTF-8") + "/company/" + URLEncoder.encode(companyName, "UTF-8"))
					.getData(WrapCompanyAttribute.class);
		} catch (Exception e) {
			throw new Exception("getWithNameWithCompany name:" + name + ", company:" + companyName + " error.", e);
		}
	}

	public List<WrapDepartmentDuty> listWithCompany(String companyName) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"companyattribute/list/company/" + URLEncoder.encode(companyName, "UTF-8"))
					.getDataAsList(WrapDepartmentDuty.class);
		} catch (Exception e) {
			throw new Exception("listWithCompany name:" + companyName + " error.", e);
		}
	}

	public List<WrapDepartmentDuty> listWithName(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"companyattribute/list/" + URLEncoder.encode(name, "UTF-8"))
					.getDataAsList(WrapDepartmentDuty.class);
		} catch (Exception e) {
			throw new Exception("listWithName name:" + name + " error.", e);
		}
	}
}
