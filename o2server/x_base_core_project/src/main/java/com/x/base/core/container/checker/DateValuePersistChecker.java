package com.x.base.core.container.checker;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainerBasic;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.tools.DateTools;

public class DateValuePersistChecker extends AbstractChecker {

	public DateValuePersistChecker(EntityManagerContainerBasic emc) {
		super(emc);
	}

	public void check(Field field, Date value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (Objects.equals(checkPersistType, CheckPersistType.all)
				|| Objects.equals(checkPersistType, CheckPersistType.baseOnly)) {
			this.allowEmpty(field, value, jpa, checkPersist, checkPersistType);
			this.max(field, value, jpa, checkPersist, checkPersistType);
			this.min(field, value, jpa, checkPersist, checkPersistType);
		}
	}

	private void allowEmpty(Field field, Date value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if ((!checkPersist.allowEmpty()) && (value == null)) {
			throw new Exception("check persist dateValue allowEmpty error, class:" + jpa.getClass().getName()
					+ ", field:" + field.getName() + ", can not be null.");
		}
	}

	private void max(Field field, Date value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.max()) && (value != null)) {
			Date date = DateTools.parse(checkPersist.max());
			if (value.after(date)) {
				throw new Exception("check persist dateValue max error, class:" + jpa.getClass().getName() + ", field:"
						+ field.getName() + ", can not after:" + checkPersist.max() + ".");
			}
		}
	}

	private void min(Field field, Date value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.min()) && (value != null)) {
			Date date = DateTools.parse(checkPersist.min());
			if (value.before(date)) {
				throw new Exception("check persist dateValue min error, class:" + jpa.getClass().getName() + ", field:"
						+ field.getName() + ", can not before:" + checkPersist.min() + ".");
			}
		}
	}
}