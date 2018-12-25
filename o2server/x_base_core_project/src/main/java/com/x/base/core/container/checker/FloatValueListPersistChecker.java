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

public class FloatValueListPersistChecker extends AbstractChecker {

	public FloatValueListPersistChecker(EntityManagerContainerBasic emc) {
		super(emc);
	}

	public void check(Field field, List<Float> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (Objects.equals(checkPersistType, CheckPersistType.all)
				|| Objects.equals(checkPersistType, CheckPersistType.baseOnly)) {
			this.allowEmpty(field, values, jpa, checkPersist, checkPersistType);
			this.allowContainEmpty(field, values, jpa, checkPersist, checkPersistType);
			this.max(field, values, jpa, checkPersist, checkPersistType);
			this.min(field, values, jpa, checkPersist, checkPersistType);
		}
	}

	private void allowEmpty(Field field, List<Float> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if ((!checkPersist.allowEmpty()) && (ListTools.nullToEmpty(values).isEmpty())) {
			throw new Exception("check persist floatValueList allowEmpty error, class:" + jpa.getClass().getName()
					+ ", field:" + field.getName() + ", can not be null or empty.");
		}
	}

	private void allowContainEmpty(Field field, List<Float> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (!checkPersist.allowContainEmpty()) {
			for (Float o : ListTools.nullToEmpty(values)) {
				if (null == o) {
					throw new Exception("check persist floatValueList allowContainEmpty error, class:"
							+ jpa.getClass().getName() + ", field:" + field.getName() + ", can not contain null.");
				}
			}

		}
	}

	private void max(Field field, List<Float> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.max())) {
			Float max = Float.parseFloat(checkPersist.max());
			for (Float o : ListTools.nullToEmpty(values)) {
				if (null != o && o > max) {
					throw new Exception("check persist floatValueList max error, class:" + jpa.getClass().getName()
							+ ", field:" + field.getName() + ", can not contain value larger than:" + checkPersist.max()
							+ ".");
				}
			}
		}
	}

	private void min(Field field, List<Float> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.min())) {
			Float min = Float.parseFloat(checkPersist.min());
			for (Float o : ListTools.nullToEmpty(values)) {
				if (null != o && o < min) {
					throw new Exception("check floatValueList min error, class:" + jpa.getClass().getName() + ", field:"
							+ field.getName() + ", can not contain value lesser than:" + checkPersist.min() + ".");
				}
			}
		}
	}
}