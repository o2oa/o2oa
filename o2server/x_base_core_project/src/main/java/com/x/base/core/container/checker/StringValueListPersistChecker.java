package com.x.base.core.container.checker;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.ElementColumn;

import com.x.base.core.container.EntityManagerContainerBasic;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.NotEqual;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;

public class StringValueListPersistChecker extends AbstractChecker {
	public StringValueListPersistChecker(EntityManagerContainerBasic emc) {
		super(emc);
	}

	public void check(Field field, List<String> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (Objects.equals(checkPersistType, CheckPersistType.all)
				|| Objects.equals(checkPersistType, CheckPersistType.baseOnly)) {
			this.allowEmpty(field, values, jpa, checkPersist, checkPersistType);
			this.allowContainEmpty(field, values, jpa, checkPersist, checkPersistType);
			this.length(field, values, jpa, checkPersist, checkPersistType);
			this.simply(field, values, jpa, checkPersist, checkPersistType);
			this.fileName(field, values, jpa, checkPersist, checkPersistType);
			this.mail(field, values, jpa, checkPersist, checkPersistType);
			this.moible(field, values, jpa, checkPersist, checkPersistType);
			this.pattern(field, values, jpa, checkPersist, checkPersistType);
		}
		if (Objects.equals(checkPersistType, CheckPersistType.all)
				|| Objects.equals(checkPersistType, CheckPersistType.citationOnly)) {
			this.citationExists(this.emc, field, values, jpa, checkPersist, checkPersistType);
			this.citationNotExists(this.emc, field, values, jpa, checkPersist, checkPersistType);
		}
	}

	private void allowEmpty(Field field, List<String> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if ((!checkPersist.allowEmpty()) && (ListTools.nullToEmpty(values).isEmpty())) {
			throw new Exception("check persist stirngValueList allowEmpty error, class:" + jpa.getClass().getName()
					+ ", field:" + field.getName() + ", can not be null or empty.");
		}
	}

	private void allowContainEmpty(Field field, List<String> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (!checkPersist.allowContainEmpty()) {
			for (String str : ListTools.nullToEmpty(values)) {
				if (StringUtils.isEmpty(str)) {
					throw new Exception("check persist stirngValueList allowContainEmpty error, class:"
							+ jpa.getClass().getName() + ", field:" + field.getName() + ",values:"
							+ StringUtils.join(values, ",") + " can not contain null or empty value.");
				}
			}
		}
	}

	private void length(Field field, List<String> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		ElementColumn elementColumn = field.getAnnotation(ElementColumn.class);
		for (String str : ListTools.nullToEmpty(values)) {
			if (StringUtils.isNotEmpty(str)) {
				int len = Objects.toString(str).getBytes(Charset.forName("UTF-8")).length;
				if (len > elementColumn.length()) {
					throw new Exception("check persist stirngValueList length error, class:" + jpa.getClass().getName()
							+ ", field:" + field.getName() + ", values:" + StringUtils.join(values, ",") + ",value:"
							+ str + ", length:" + len + ", max:" + elementColumn.length() + ".");
				}
			}
		}
	}

	private void simply(Field field, List<String> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (checkPersist.simplyString()) {
			for (String str : ListTools.nullToEmpty(values)) {
				if (StringUtils.isNotEmpty(str)) {
					if (!StringTools.isSimply(str)) {
						throw new Exception("check persist stringValueList simply error, class:"
								+ jpa.getClass().getName() + ", field:" + field.getName() + ", values:"
								+ StringUtils.join(values, ",") + "value:" + str + ".");
					}
				}
			}
		}
	}

	private void fileName(Field field, List<String> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (checkPersist.fileNameString()) {
			for (String str : ListTools.nullToEmpty(values)) {
				if (StringUtils.isNotEmpty(str)) {
					if (!StringTools.isFileName(str)) {
						throw new Exception("check persist stringValueList fileName error, class:"
								+ jpa.getClass().getName() + ", field:" + field.getName() + ", values:"
								+ StringUtils.join(values, ",") + "value:" + str + ".");
					}
				}
			}
		}
	}

	private void mail(Field field, List<String> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (checkPersist.mailString()) {
			for (String str : ListTools.nullToEmpty(values)) {
				if (StringUtils.isNotEmpty(str)) {
					if (!StringTools.isMail(str)) {
						throw new Exception("check persist stringValueList mail error, class:"
								+ jpa.getClass().getName() + ", field:" + field.getName() + ", values:"
								+ StringUtils.join(values, ",") + ", value:" + str + ".");
					}
				}
			}
		}
	}

	private void pattern(Field field, List<String> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (StringUtils.isNotEmpty(checkPersist.pattern())) {
			Pattern pattern = Pattern.compile(checkPersist.pattern());
			for (String str : ListTools.nullToEmpty(values)) {
				Matcher matcher = pattern.matcher(str);
				if (!matcher.find()) {
					throw new Exception(
							"check persist stirngValue pattern error, class:" + jpa.getClass().getName() + ", field:"
									+ field.getName() + ", value:" + str + ", pattern:" + checkPersist.pattern() + ".");
				}
			}
		}
	}

	private void moible(Field field, List<String> values, JpaObject jpa, CheckPersist checkPersist,
			CheckPersistType checkPersistType) throws Exception {
		if (checkPersist.mobileString()) {
			for (String str : ListTools.nullToEmpty(values)) {
				if (StringUtils.isNotEmpty(str)) {
					if (!StringTools.isMobile(str)) {
						throw new Exception("check persist stringValueList mobile error, class:"
								+ jpa.getClass().getName() + ", field:" + field.getName() + ", values:"
								+ StringUtils.join(values, ",") + "value:" + str + ".");
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void citationExists(EntityManagerContainerBasic emc, Field field, List<String> values, JpaObject jpa,
			CheckPersist checkPersist, CheckPersistType checkPersistType) throws Exception {
		next: for (String value : ListTools.nullToEmpty(values)) {
			if (ArrayUtils.contains(checkPersist.excludes(), value)) {
				continue next;
			}
			for (CitationExist citationExist : checkPersist.citationExists()) {
				EntityManager em = emc.get(citationExist.type());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> cq = cb.createQuery(Long.class);
				Root<? extends JpaObject> root = cq.from(citationExist.type());
				Predicate p = cb.disjunction();
				for (String str : citationExist.fields()) {
					/* 如果值过长db2会报错 302 22001 */
					if (JpaObjectTools.withinDefinedLength(value, citationExist.type(), str)) {
						Path<?> path = root.get(str);
						if (JpaObjectTools.isList(path)) {
							p = cb.or(p, cb.isMember(value, (Path<List<String>>) path));
						} else {
							p = cb.or(p, cb.equal(path, value));
						}
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
					throw new Exception("check persist stringValueList citationExists error, class:"
							+ jpa.getClass().getName() + ", field:" + field.getName() + ", values: "
							+ StringUtils.join(values, ",") + ", value:" + value + ", must be a existed in class:"
							+ citationExist.type() + ", fields:" + StringUtils.join(citationExist.fields(), ",") + ".");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void citationNotExists(EntityManagerContainerBasic emc, Field field, List<String> values, JpaObject jpa,
			CheckPersist checkPersist, CheckPersistType checkPersistType) throws Exception {
		next: for (String value : ListTools.nullToEmpty(values)) {
			if (ArrayUtils.contains(checkPersist.excludes(), value)) {
				continue next;
			}
			for (CitationNotExist citationNotExist : checkPersist.citationNotExists()) {
				EntityManager em = emc.get(citationNotExist.type());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> cq = cb.createQuery(Long.class);
				Root<? extends JpaObject> root = cq.from(citationNotExist.type());
				Predicate p = cb.disjunction();
				for (String str : citationNotExist.fields()) {
					/* 如果值过长db2会报错 302 22001 */
					if (JpaObjectTools.withinDefinedLength(value, citationNotExist.type(), str)) {
						Path<?> path = root.get(str);
						if (JpaObjectTools.isList(path)) {
							p = cb.or(p, cb.isMember(value, (Path<List<String>>) path));
						} else {
							p = cb.or(p, cb.equal(path, value));
						}
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
					throw new Exception(
							"check persist stringValueList citationNotExists error, class:" + jpa.getClass().getName()
									+ ", field:" + field.getName() + ", values: " + StringUtils.join(values, ",")
									+ ", value:" + value + ", already existed in class:" + citationNotExist.type()
									+ ", fields:" + StringUtils.join(citationNotExist.fields(), ",") + ".");
				}
			}
		}
	}
}