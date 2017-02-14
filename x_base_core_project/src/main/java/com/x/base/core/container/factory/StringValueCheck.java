package com.x.base.core.container.factory;

import java.lang.reflect.Field;

import com.x.base.core.entity.JpaObject;

public class StringValueCheck {
	protected void stringValue_check_notAllowEmpty(JpaObject jpa, Field fld, Object o) throws Exception {
		if (o == null) {
			throw new Exception("check not allowEmpty error, class:" + jpa.getClass().getCanonicalName() + ", field:"
					+ fld.getName() + ", can not be null.");
		}
		if (o.toString().length() < 1) {
			throw new Exception("check not allowEmpty error, class:" + jpa.getClass().getCanonicalName() + ", field:"
					+ fld.getName() + ", type:" + o.getClass().getCanonicalName() + ", can not be empty.");
		}
	}
}