package com.x.base.core.container.checker;

import java.lang.reflect.Field;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainerBasic;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckPersistType;

public class DoubleValuePersistChecker extends AbstractChecker {

	public DoubleValuePersistChecker(EntityManagerContainerBasic emc) {
		super(emc);
	}

	public void check(Field field, Double value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (Objects.equals(checkPersistType, CheckPersistType.all)
				|| Objects.equals(checkPersistType, CheckPersistType.baseOnly)) {
			this.allowEmpty(field, value, jpa, checkPersist, checkPersistType);
			this.max(field, value, jpa, checkPersist, checkPersistType);
			this.min(field, value, jpa, checkPersist, checkPersistType);
		}
	}

	private void allowEmpty(Field field, Double value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if ((!checkPersist.allowEmpty()) && (null == value)) {
			throw new Exception("check persist doubleValue allowEmpty error, class:" + jpa.getClass().getName() + ", field:"
					+ field.getName() + ", can not be null.");
		}
	}

	private void max(Field field, Double value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.max()) && (null != value)) {
			Double max = Double.parseDouble(checkPersist.max());
			if (max < value) {
				throw new Exception("check persist doubleValue max error, class:" + jpa.getClass().getName() + ", field:"
						+ field.getName() + ", can not larger then:" + checkPersist.max() + ".");
			}
		}
	}

	private void min(Field field, Double value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.min()) && (null != value)) {
			Double min = Double.parseDouble(checkPersist.min());
			if (min > value) {
				throw new Exception("check persist doubleValue min error, class:" + jpa.getClass().getName() + ", field:"
						+ field.getName() + ", can not lesser then:" + checkPersist.min() + ".");
			}
		}
	}
}