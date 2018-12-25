package com.x.base.core.container.checker;

import java.lang.reflect.Field;
import java.util.Objects;

import com.x.base.core.container.EntityManagerContainerBasic;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckPersistType;

public class BooleanValuePersistChecker extends AbstractChecker {

	public BooleanValuePersistChecker(EntityManagerContainerBasic emc) {
		super(emc);
	}

	public void check(Field field, Boolean value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (Objects.equals(checkPersistType, CheckPersistType.all)
				|| Objects.equals(checkPersistType, CheckPersistType.baseOnly)) {
			this.allowEmpty(field, value, jpa, checkPersist, checkPersistType);
		}
	}

	private void allowEmpty(Field field, Boolean value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if ((!checkPersist.allowEmpty()) && (value == null)) {
			throw new Exception("check persist booleanValue allowEmpty error, class:" + jpa.getClass().getName()
					+ ", field:" + field.getName() + ", can not be null.");
		}
	}
}