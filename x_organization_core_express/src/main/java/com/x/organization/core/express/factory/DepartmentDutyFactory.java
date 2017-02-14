package com.x.organization.core.express.factory;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.organization.core.express.wrap.WrapDepartmentDuty;

public class DepartmentDutyFactory {

	private Type collectionType = new TypeToken<ArrayList<WrapDepartmentDuty>>() {
	}.getType();

	public WrapDepartmentDuty getWithName(String name, String department) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class, "departmentduty/" + URLEncoder.encode(name, "UTF-8")
					+ "/department/" + URLEncoder.encode(department, "UTF-8"), WrapDepartmentDuty.class);
		} catch (Exception e) {
			throw new Exception("getWithNameWithDepartment departmentDuty{name:" + name + ", department:" + department + "} error.", e);
		}
	}

	public List<WrapDepartmentDuty> listWithPerson(String person) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"departmentduty/list/person/" + URLEncoder.encode(person, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listWithPerson person{name:" + person + "} error.", e);
		}
	}

	public List<WrapDepartmentDuty> listWithDepartment(String department) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"departmentduty/list/department/" + URLEncoder.encode(department, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listWithDepartment department{name:" + department + "} error.", e);
		}
	}

}
