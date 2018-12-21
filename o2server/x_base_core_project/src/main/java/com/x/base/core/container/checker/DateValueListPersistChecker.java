package com.x.base.core.container.checker;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainerBasic;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

public class DateValueListPersistChecker extends AbstractChecker {

	public DateValueListPersistChecker(EntityManagerContainerBasic emc) {
		super(emc);
	}

	public void check(Field field, List<Date> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (Objects.equals(checkPersistType, CheckPersistType.all)
				|| Objects.equals(checkPersistType, CheckPersistType.baseOnly)) {
			this.allowEmpty(field, values, jpa, checkPersist, checkPersistType);
			this.allowContainEmpty(field, values, jpa, checkPersist, checkPersistType);
			this.max(field, values, jpa, checkPersist, checkPersistType);
			this.min(field, values, jpa, checkPersist, checkPersistType);
		}
	}

	private void allowEmpty(Field field, List<Date> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if ((!checkPersist.allowEmpty()) && (ListTools.nullToEmpty(values).isEmpty())) {
			throw new Exception("check persist dateValueList allowEmpty error, class:" + jpa.getClass().getName()
					+ ", field:" + field.getName() + ", can not be null or empty.");
		}
	}

	private void allowContainEmpty(Field field, List<Date> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (!checkPersist.allowContainEmpty()) {
			for (Date o : ListTools.nullToEmpty(values)) {
				if (null == o) {
					throw new Exception("check persist dateValueList allowContainEmpty error, class:"
							+ jpa.getClass().getName() + ", field:" + field.getName() + ", can not contain null.");
				}
			}

		}
	}

	private void max(Field field, List<Date> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.max())) {
			Date date = DateTools.parse(checkPersist.max());
			for (Date o : ListTools.nullToEmpty(values)) {
				if (null != o && o.after(date)) {
					throw new Exception(
							"check persist dateValueList max error, class:" + jpa.getClass().getName() + ", field:"
									+ field.getName() + ", can not contain value after:" + checkPersist.max() + ".");
				}
			}
		}
	}

	private void min(Field field, List<Date> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.min())) {
			Date date = DateTools.parse(checkPersist.min());
			for (Date o : ListTools.nullToEmpty(values)) {
				if (null != o && o.before(date)) {
					throw new Exception(
							"check persist dateValueList min error, class:" + jpa.getClass().getName() + ", field:"
									+ field.getName() + ", can not contain value before:" + checkPersist.min() + ".");
				}
			}
		}
	}
}