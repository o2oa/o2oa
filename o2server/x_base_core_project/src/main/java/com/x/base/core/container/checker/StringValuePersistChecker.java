package com.x.base.core.container.checker;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainerBasic;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.NotEqual;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.tools.StringTools;

public class StringValuePersistChecker extends AbstractChecker {

	public StringValuePersistChecker(EntityManagerContainerBasic emc) {
		super(emc);
	}

	public void check(Field field, String value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (Objects.equals(checkPersistType, CheckPersistType.all)
				|| Objects.equals(checkPersistType, CheckPersistType.baseOnly)) {
			this.allowEmpty(field, value, jpa, checkPersist, checkPersistType);
			this.length(field, value, jpa, checkPersist, checkPersistType);
			this.simply(field, value, jpa, checkPersist, checkPersistType);
			this.fileName(field, value, jpa, checkPersist, checkPersistType);
			this.mail(field, value, jpa, checkPersist, checkPersistType);
			this.mobile(field, value, jpa, checkPersist, checkPersistType);
			this.pattern(field, value, jpa, checkPersist, checkPersistType);
		}
		if (Objects.equals(checkPersistType, CheckPersistType.all)
				|| Objects.equals(checkPersistType, CheckPersistType.citationOnly)) {
			this.citationExists(this.emc, field, value, jpa, checkPersist, checkPersistType);
			this.citationNotExists(this.emc, field, value, jpa, checkPersist, checkPersistType);
		}
	}

	private void allowEmpty(Field field, String value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if ((!checkPersist.allowEmpty()) && (value == null || value.toString().length() < 1)) {
			throw new Exception("check persist stirngValue allowEmpty error, class:" + jpa.getClass().getName()
					+ ", field:" + field.getName() + ", can not be null or empty.");
		}
	}

	private void length(Field field, String value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		Column column = field.getAnnotation(Column.class);
		int len = Objects.toString(value).getBytes(Charset.forName("UTF-8")).length;
		if (len > column.length()) {
			throw new Exception("check persist stirngValue length error, class:" + jpa.getClass().getName() + ", field:"
					+ field.getName() + ", value:" + value + ", length:" + len + ", max:" + column.length() + ".");
		}
	}

	/**
	 * 取消simply校验,因为其他语言比如西语在目前的校验规则下全是特殊字符.
	 * 
	 * @param field
	 * @param value
	 * @param jpa
	 * @param checkPersist
	 * @param checkPersistType
	 * @throws Exception
	 */
	private void simply(Field field, String value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
//		if (checkPersist.simplyString() && StringUtils.isNotEmpty(value)) {
//			if (!StringTools.isSimply(value)) {
//				throw new Exception("check persist stirngValue simply error, class:" + jpa.getClass().getName()
//						+ ", field:" + field.getName() + ", value:" + value + ".");
//			}
//		}
	}

	private void fileName(Field field, String value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (checkPersist.fileNameString() && StringUtils.isNotEmpty(value)) {
			if (!StringTools.isFileName(value)) {
				throw new Exception("check persist stirngValue fileName error, class:" + jpa.getClass().getName()
						+ ", field:" + field.getName() + ", value:" + value + ".");
			}
		}
	}

	private void mail(Field field, String value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (checkPersist.mailString() && StringUtils.isNotEmpty(value)) {
			if (!StringTools.isMail(value)) {
				throw new Exception("check persist stirngValue mail error, class:" + jpa.getClass().getName()
						+ ", field:" + field.getName() + ", value:" + value + ".");
			}
		}
	}

	private void mobile(Field field, String value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (checkPersist.mobileString() && StringUtils.isNotEmpty(value)) {
			if (!StringTools.isMobile(value)) {
				throw new Exception("check persist stirngValue mobile error, class:" + jpa.getClass().getName()
						+ ", field:" + field.getName() + ", value:" + value + ".");
			}

		}
	}

	private void pattern(Field field, String value, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.pattern()) && StringUtils.isNotEmpty(value)) {
			Pattern pattern = Pattern.compile(checkPersist.pattern());
			Matcher matcher = pattern.matcher(value);
			if (!matcher.find()) {
				throw new Exception(
						"check persist stirngValue pattern error, class:" + jpa.getClass().getName() + ", field:"
								+ field.getName() + ", value:" + value + ", pattern:" + checkPersist.pattern() + ".");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void citationExists(EntityManagerContainerBasic emc, Field field, String value, JpaObject jpa,
			CheckPersist checkPersist, CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(value) && (!ArrayUtils.contains(checkPersist.excludes(), value))) {
			for (CitationExist citationExist : checkPersist.citationExists()) {
				EntityManager em = emc.get(citationExist.type());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> cq = cb.createQuery(Long.class);
				Root<? extends JpaObject> root = cq.from(citationExist.type());
				Predicate p = cb.disjunction();
				for (String str : citationExist.fields()) {
					Path<?> path = root.get(str);
					if (JpaObjectTools.isList(path)) {
						p = cb.or(p, cb.isMember(value, (Path<List<String>>) path));
					} else {
						p = cb.or(p, cb.equal(path, value));
					}
				}
				p = cb.and(p, cb.notEqual(root.get("id"), jpa.getId()));
				for (Equal o : citationExist.equals()) {
					p = cb.and(p, cb.equal(root.get(o.field()), jpa.get(o.property())));
				}
				for (NotEqual o : citationExist.notEquals()) {
					p = cb.and(p, cb.notEqual(root.get(o.field()), jpa.get(o.property())));
				}
				cq.select(cb.count(root)).where(p);
				Long count = em.createQuery(cq).getSingleResult();
				if (count == 0) {
					throw new Exception("check persist stirngValue citationExists error, class:"
							+ jpa.getClass().getName() + ", field:" + field.getName() + ", value: " + value
							+ " must be a existed in class:" + citationExist.type() + ", fields:"
							+ StringUtils.join(citationExist.fields(), ",") + ".");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void citationNotExists(EntityManagerContainerBasic emc, Field field, String value, JpaObject jpa,
			CheckPersist checkPersist, CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(value) && (!ArrayUtils.contains(checkPersist.excludes(), value))) {
			for (CitationNotExist citationNotExist : checkPersist.citationNotExists()) {
				EntityManager em = emc.get(citationNotExist.type());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> cq = cb.createQuery(Long.class);
				Root<? extends JpaObject> root = cq.from(citationNotExist.type());
				Predicate p = cb.disjunction();
				for (String str : citationNotExist.fields()) {
					Path<?> path = root.get(str);
					if (JpaObjectTools.isList(path)) {
						p = cb.or(p, cb.isMember(value, (Path<List<String>>) path));
					} else {
						p = cb.or(p, cb.equal(path, value));
					}
				}
				p = cb.and(p, cb.notEqual(root.get("id"), jpa.getId()));
				for (Equal o : citationNotExist.equals()) {
					p = cb.and(p, cb.equal(root.get(o.field()), jpa.get(o.property())));
				}
				for (NotEqual o : citationNotExist.notEquals()) {
					p = cb.and(p, cb.notEqual(root.get(o.field()), jpa.get(o.property())));
				}
				cq.select(cb.count(root)).where(p);
				Long count = em.createQuery(cq).getSingleResult();
				if (count != 0) {
					throw new Exception("check persist stirngValue citationNotExists error, class:"
							+ jpa.getClass().getName() + ", field:" + field.getName() + ", value: " + value
							+ " must be a not existed in class:" + citationNotExist.type() + ", fields:"
							+ StringUtils.join(citationNotExist.fields(), ",") + ".");
				}
			}
		}
	}
}