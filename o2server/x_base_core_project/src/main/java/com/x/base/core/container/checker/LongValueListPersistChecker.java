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

public class LongValueListPersistChecker extends AbstractChecker {

	public LongValueListPersistChecker(EntityManagerContainerBasic emc) {
		super(emc);
	}

	public void check(Field field, List<Long> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (Objects.equals(checkPersistType, CheckPersistType.all)
				|| Objects.equals(checkPersistType, CheckPersistType.baseOnly)) {
			this.allowEmpty(field, values, jpa, checkPersist, checkPersistType);
			this.allowContainEmpty(field, values, jpa, checkPersist, checkPersistType);
			this.max(field, values, jpa, checkPersist, checkPersistType);
			this.min(field, values, jpa, checkPersist, checkPersistType);
		}
	}

	private void allowEmpty(Field field, List<Long> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if ((!checkPersist.allowEmpty()) && (ListTools.nullToEmpty(values).isEmpty())) {
			throw new Exception("check persist longValueList allowEmpty error, class:" + jpa.getClass().getName()
					+ ", field:" + field.getName() + ", can not be null or emtpy.");
		}
	}

	private void allowContainEmpty(Field field, List<Long> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (!checkPersist.allowContainEmpty()) {
			for (Long o : ListTools.nullToEmpty(values)) {
				if (null == o) {
					throw new Exception("check persist longValueList allowContainEmpty error, class:"
							+ jpa.getClass().getName() + ", field:" + field.getName() + ", can not contain null.");
				}
			}
		}
	}

	private void max(Field field, List<Long> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.max())) {
			Long max = Long.parseLong(checkPersist.max());
			for (Long o : ListTools.nullToEmpty(values)) {
				if (null != o && o > max) {
					throw new Exception("check persist longValueList max error, class:" + jpa.getClass().getName()
							+ ", field:" + field.getName() + ", can not contain value larger than:" + checkPersist.max()
							+ ".");
				}
			}
		}
	}

	private void min(Field field, List<Long> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.min())) {
			Long min = Long.parseLong(checkPersist.min());
			for (Long o : ListTools.nullToEmpty(values)) {
				if (null != o && o < min) {
					throw new Exception("check persist longValueList min error, class:" + jpa.getClass().getName()
							+ ", field:" + field.getName() + ", can not contain value lesser than:" + checkPersist.min()
							+ ".");
				}
			}
		}
	}
}