package com.x.base.core.container.checker;

import java.lang.reflect.Field;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainerBasic;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckPersistType;

public class IntegerValuePersistChecker extends AbstractChecker {

	public IntegerValuePersistChecker(EntityManagerContainerBasic emc) {
		super(emc);
	}

	public void check(Field field, Integer value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (Objects.equals(checkPersistType, CheckPersistType.all)
				|| Objects.equals(checkPersistType, CheckPersistType.baseOnly)) {
			this.allowEmpty(field, value, jpa, checkPersist, checkPersistType);
			this.max(field, value, jpa, checkPersist, checkPersistType);
			this.min(field, value, jpa, checkPersist, checkPersistType);
		}
	}

	private void allowEmpty(Field field, Integer value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if ((!checkPersist.allowEmpty()) && (null == value)) {
			throw new Exception("check persist integerValue allowEmpty error, class:" + jpa.getClass().getName() + ", field:"
					+ field.getName() + ", can not be null.");
		}
	}

	private void max(Field field, Integer value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.max()) && (null != value)) {
			Integer max = Integer.parseInt(checkPersist.max());
			if (max < value) {
				throw new Exception("check persist integerValue max error, class:" + jpa.getClass().getName() + ", field:"
						+ field.getName() + ", can not larger then:" + checkPersist.max() + ".");
			}
		}
	}

	private void min(Field field, Integer value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.min()) && (null != value)) {
			Integer min = Integer.parseInt(checkPersist.min());
			if (min > value) {
				throw new Exception("check persist integerValue min error, class:" + jpa.getClass().getName() + ", field:"
						+ field.getName() + ", can not lesser then:" + checkPersist.min() + ".");
			}
		}
	}
}