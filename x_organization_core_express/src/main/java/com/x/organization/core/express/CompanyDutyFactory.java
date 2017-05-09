package com.x.organization.core.express;

import java.net.URLEncoder;
import java.util.List;

import com.x.base.core.DefaultCharset;
import com.x.base.core.exception.RunningException;
import com.x.base.core.project.Context;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.base.core.utils.ListTools;
import com.x.organization.core.express.wrap.WrapCompanyDuty;

public class CompanyDutyFactory {

	CompanyDutyFactory(Context context) {
		this.context = context;
	}

	private Context context;

	/**
	 * 根据名称和公司名称获取公司职务<br/>
	 * 与后台getWithNameWithCompany对应
	 */
	public WrapCompanyDuty getWithNameWithCompany(String name, String company) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"companyduty/" + URLEncoder.encode(name, DefaultCharset.name) + "/company/"
									+ URLEncoder.encode(company, DefaultCharset.name))
					.getData(WrapCompanyDuty.class);
		} catch (Exception e) {
			throw new RunningException(e, "getWithNameWithCompany name: {}, company: {}, error.", name, company);
		}
	}

	/**
	 * 根据名称获取所有职务<br/>
	 * 与后台listWithName对应<br/>
	 */
	public List<WrapCompanyDuty> listWithName(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"companyduty/list/" + URLEncoder.encode(name, DefaultCharset.name))
					.getDataAsList(WrapCompanyDuty.class);
		} catch (Exception e) {
			throw new RunningException(e, "listWithName name: {}, error.", name);
		}
	}

	public List<String> listNameWithName(String name) throws Exception {
		List<WrapCompanyDuty> os = this.listWithName(name);
		return ListTools.extractProperty(os, "name", String.class, true, true);
	}

	/**
	 * 根据身份获取公司职务<br/>
	 * 与后台listWithIdentity对应<br/>
	 */
	public List<WrapCompanyDuty> listWithIdentity(String identity) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"companyduty/list/identity/" + URLEncoder.encode(identity, DefaultCharset.name))
					.getDataAsList(WrapCompanyDuty.class);
		} catch (Exception e) {
			throw new RunningException(e, "listWithIdentity identity:{}, error.", identity);
		}
	}

	public List<String> listNameWithIdentity(String identity) throws Exception {
		List<WrapCompanyDuty> os = this.listWithIdentity(identity);
		return ListTools.extractProperty(os, "name", String.class, true, true);
	}

	/**
	 * 根据个人获取公司职务<br/>
	 * 与后台listWithPerson对应<br/>
	 */
	public List<WrapCompanyDuty> listWithPerson(String person) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"companyduty/list/person/" + URLEncoder.encode(person, DefaultCharset.name))
					.getDataAsList(WrapCompanyDuty.class);
		} catch (Exception e) {
			throw new RunningException(e, "listWithIdentity person:{}, error.", person);
		}
	}

	public List<String> listNameWithPerson(String person) throws Exception {
		List<WrapCompanyDuty> os = this.listWithPerson(person);
		return ListTools.extractProperty(os, "name", String.class, true, true);
	}

	/**
	 * 根据公司获取公司职务<br/>
	 * 与后台listWithCompany对应<br/>
	 */
	public List<WrapCompanyDuty> listWithCompany(String company) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"companyduty/list/company/" + URLEncoder.encode(company, DefaultCharset.name))
					.getDataAsList(WrapCompanyDuty.class);
		} catch (Exception e) {
			throw new RunningException(e, "listWithCompany company:{}, error.", company);
		}
	}

	public List<String> listNameWithCompany(String company) throws Exception {
		List<WrapCompanyDuty> os = this.listWithCompany(company);
		return ListTools.extractProperty(os, "name", String.class, true, true);
	}

	/**
	 * 根据个人和公司获取公司职务<br/>
	 * 与后台listWithPersonWithCompany对应<br/>
	 */
	public List<WrapCompanyDuty> listWithPersonWithCompany(String person, String company) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"departmentduty/list/person/" + URLEncoder.encode(person, DefaultCharset.name) + "/company/"
									+ URLEncoder.encode(company, DefaultCharset.name))
					.getDataAsList(WrapCompanyDuty.class);
		} catch (Exception e) {
			throw new RunningException(e, "listWithPersonWithCompany person: {}, company: {}.", person, company);
		}
	}

	public List<String> listNameWithPersonWithCompany(String person, String company) throws Exception {
		List<WrapCompanyDuty> os = this.listWithPersonWithCompany(person, company);
		return ListTools.extractProperty(os, "name", String.class, true, true);
	}

}
