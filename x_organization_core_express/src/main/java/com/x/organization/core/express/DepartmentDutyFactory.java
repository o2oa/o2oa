package com.x.organization.core.express;

import java.net.URLEncoder;
import java.util.List;

import com.x.base.core.DefaultCharset;
import com.x.base.core.exception.RunningException;
import com.x.base.core.project.Context;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.base.core.utils.ListTools;
import com.x.organization.core.express.wrap.WrapDepartmentDuty;

public class DepartmentDutyFactory {

	DepartmentDutyFactory(Context context) {
		this.context = context;
	}

	private Context context;

	/**
	 * 根据名称和部门名称获取部门职务 <br/>
	 * 与后台getWithNameWithDepartment对应<br/>
	 */
	public WrapDepartmentDuty getWithNameWithDepartment(String name, String department) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"departmentduty/" + URLEncoder.encode(name, DefaultCharset.name) + "/department/"
									+ URLEncoder.encode(department, DefaultCharset.name))
					.getData(WrapDepartmentDuty.class);
		} catch (Exception e) {
			throw new RunningException(e, "getWithNameWithDepartment name:{}, department: {}, error.", name,
					department);
		}
	}

	/**
	 * 根据指定名称y获取部门职务<br/>
	 * 与后台listWithName对应<br/>
	 */
	public List<WrapDepartmentDuty> listWithName(String name) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"departmentduty/list/" + URLEncoder.encode(name, DefaultCharset.name))
					.getDataAsList(WrapDepartmentDuty.class);
		} catch (Exception e) {
			throw new RunningException(e, "listWithName name: {}, error.", name);
		}
	}

	/**
	 * 根据指定名称y获取部门职务名称<br/>
	 */
	public List<String> listNameWithName(String name) throws Exception {
		List<WrapDepartmentDuty> os = this.listWithName(name);
		return ListTools.extractProperty(os, "name", String.class, true, true);
	}

	/**
	 * 根据identity获取部门职务<br/>
	 * 与后台listWithIdentity对应<br/>
	 */
	public List<WrapDepartmentDuty> listWithIdentity(String identity) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"departmentduty/list/identity/" + URLEncoder.encode(identity, DefaultCharset.name))
					.getDataAsList(WrapDepartmentDuty.class);
		} catch (Exception e) {
			throw new RunningException(e, "listWithIdentity identity: {}, error.", identity);
		}
	}

	/**
	 * 根据identity获取部门职务名称<br/>
	 */
	public List<String> listNameWithIdentity(String identity) throws Exception {
		List<WrapDepartmentDuty> os = this.listWithIdentity(identity);
		return ListTools.extractProperty(os, "name", String.class, true, true);
	}

	/**
	 * 根据Person获取部门职务<br/>
	 * 与后台listWithPerson对应<br/>
	 */
	public List<WrapDepartmentDuty> listWithPerson(String person) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"departmentduty/list/person/" + URLEncoder.encode(person, DefaultCharset.name))
					.getDataAsList(WrapDepartmentDuty.class);
		} catch (Exception e) {
			throw new RunningException(e, "listWithPerson person: {}, error.");
		}
	}

	/**
	 * 根据Person获取部门职务名称<br/>
	 */
	public List<String> listNameWithPerson(String person) throws Exception {
		List<WrapDepartmentDuty> os = this.listWithPerson(person);
		return ListTools.extractProperty(os, "name", String.class, true, true);
	}

	/**
	 * 根据Department获取所有部门职务<br/>
	 * 与后台listWithDepartment对应<br/>
	 */
	public List<WrapDepartmentDuty> listWithDepartment(String department) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"departmentduty/list/department/" + URLEncoder.encode(department, DefaultCharset.name))
					.getDataAsList(WrapDepartmentDuty.class);
		} catch (Exception e) {
			throw new RunningException(e, "listWithDepartment department: {}, error.", department);
		}
	}

	/**
	 * 根据Department获取所有部门职务名称<br/>
	 */
	public List<String> listNameWithDepartment(String department) throws Exception {
		List<WrapDepartmentDuty> os = this.listWithDepartment(department);
		return ListTools.extractProperty(os, "name", String.class, true, true);
	}

	/** 根据个人和部门获取部门职务 */
	public List<WrapDepartmentDuty> listWithPersonWithDepartment(String person, String department) throws Exception {
		try {
			return context.applications()
					.getQuery(x_organization_assemble_express.class,
							"departmentduty/list/person/" + URLEncoder.encode(person, DefaultCharset.name)
									+ "/department/" + URLEncoder.encode(department, DefaultCharset.name))
					.getDataAsList(WrapDepartmentDuty.class);
		} catch (Exception e) {
			throw new RunningException(e, "listWithPersonWithDepartment person: {}, department: {}, error.", person,
					department);
		}
	}

	/** 根据个人和部门获取部门职务名称 */
	public List<String> listNameWithPersonWithDepartment(String person, String department) throws Exception {
		List<WrapDepartmentDuty> os = this.listWithPersonWithDepartment(person, department);
		return ListTools.extractProperty(os, "name", String.class, true, true);
	}

}