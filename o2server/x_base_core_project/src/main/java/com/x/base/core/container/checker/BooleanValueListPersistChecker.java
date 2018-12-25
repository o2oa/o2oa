package com.x.base.core.container.checker;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainerBasic;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.tools.ListTools;

public class BooleanValueListPersistChecker extends AbstractChecker {

	public BooleanValueListPersistChecker(EntityManagerContainerBasic emc) {
		super(emc);
	}

	public void check(Field field, List<Boolean> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (Objects.equals(checkPersistType, CheckPersistType.all)
				|| Objects.equals(checkPersistType, CheckPersistType.baseOnly)) {
			this.allowEmpty(field, values, jpa, checkPersist, checkPersistType);
			this.allowContainEmpty(field, values, jpa, checkPersist, checkPersistType);
		}
	}

	private void allowEmpty(Field field, List<Boolean> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if ((!checkPersist.allowEmpty()) && (ListTools.nullToEmpty(values).isEmpty())) {
			throw new Exception("check persist booleanValueList allowEmpty error, class:" + jpa.getClass().getName()
					+ ", field:" + field.getName() + ", can not be null or empty.");
		}
	}

	private void allowContainEmpty(Field field, List<Boolean> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (!checkPersist.allowContainEmpty()) {
			for (Boolean o : ListTools.nullToEmpty(values)) {
				if (null == o) {
					throw new Exception("check persist booleanValueList allowContainEmpty error, class:"
							+ jpa.getClass().getName() + ", field:" + field.getName() + ",values:"
							+ StringUtils.join(values, ",") + " can not contain null.");
				}
			}
		}
	}
}