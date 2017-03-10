package com.x.organization.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static final int length_unique = JpaObject.length_255B;

	public static class Company {
		public static final String table = "ORG_COMPANY";
	}

	public static class CompanyAttribute {
		public static final String table = "ORG_COMPANYATTRIBUTE";
	}

	public static class CompanyDuty {
		public static final String table = "ORG_COMPANYDUTY";
	}

	public static class Custom {
		public static final String table = "ORG_CUSTOM";
	}

	public static class Definition {
		public static final String table = "ORG_DEFINTION";
	}

	public static class Department {
		public static final String table = "ORG_DEPARTMENT";
	}

	public static class DepartmentAttribute {
		public static final String table = "ORG_DEPARTMENTATTRIBUTE";
	}

	public static class DepartmentDuty {
		public static final String table = "ORG_DEPARTMENTDUTY";
	}

	public static class Group {
		public static final String table = "ORG_GROUP";
	}

	public static class Identity {
		public static final String table = "ORG_IDENTITY";
	}

	public static class Person {
		public static final String table = "ORG_PERSON";
	}

	public static class PersonAttribute {
		public static final String table = "ORG_PERSONATTRIBUTE";
	}

	public static class Role {
		public static final String table = "ORG_ROLE";
	}

	public static class Bind {
		public static final String table = "ORG_BIND";
	}
}
