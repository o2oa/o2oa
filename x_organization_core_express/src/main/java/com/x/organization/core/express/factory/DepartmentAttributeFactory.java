package com.x.organization.core.express.factory;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.organization.core.express.wrap.WrapDepartmentAttribute;

public class DepartmentAttributeFactory {

	private Type collectionType = new TypeToken<ArrayList<WrapDepartmentAttribute>>() {
	}.getType();

	public WrapDepartmentAttribute getWithName(String name, String departmentName) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(
					x_organization_assemble_express.class, "departmentattribute/" + URLEncoder.encode(name, "UTF-8")
							+ "/department/" + URLEncoder.encode(departmentName, "UTF-8"),
					WrapDepartmentAttribute.class);
		} catch (Exception e) {
			throw new Exception("getWithNameWithDepartment name:" + name + ", department:" + departmentName + " error.",
					e);
		}
	}

	public List<WrapDepartmentAttribute> listWithDepartment(String departmentName) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"departmentattribute/list/department/" + URLEncoder.encode(departmentName, "UTF-8"),
					collectionType);
		} catch (Exception e) {
			throw new Exception("listWithDepartment person{name:" + departmentName + "} error.", e);
		}
	}

	public List<WrapDepartmentAttribute> listWithName(String name) throws Exception {
		try {
			return AbstractThisApplication.applications.getQuery(x_organization_assemble_express.class,
					"departmentattribute/list/" + URLEncoder.encode(name, "UTF-8"), collectionType);
		} catch (Exception e) {
			throw new Exception("listWithName person{name:" + name + "} error.", e);
		}
	}
}
