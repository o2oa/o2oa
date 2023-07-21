package com.x.organization.assemble.control.jaxrs.export;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;

class ActionZhengwuDingdingPerson extends BaseAction {

	private List<Unit> allUnit;
	private List<Person> allPerson;
	private List<Identity> allIdentity;

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Workbook workBook = new XSSFWorkbook()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			allUnit = this.listUnit(business);
			allPerson = this.listPerson(business);
			allIdentity = emc.listAll(Identity.class);
			List<Person> sortedPerson = new ArrayList<>();
			for (Unit u : this.allUnit) {
				List<Person> os = this.listPersonWithUnit(u);
				for (Person p : os) {
					if (!sortedPerson.contains(p)) {
						sortedPerson.add(p);
					}
				}
			}
			List<User> users = new ArrayList<>();
			for (Person person : sortedPerson) {
				List<Unit> units = this.listUnitWithPerson(person);
				User user = new User();
				user.setName(person.getName());
				user.setZhengwuDingdingId(person.getZhengwuDingdingId());
				user.setMobile(person.getMobile());
				user.setUnitList(ListTools.extractProperty(units, Unit.name_FIELDNAME, String.class, true, false));
				users.add(user);
			}
			Sheet sheet = workBook.createSheet("政务钉钉人员");
			Row row = sheet.createRow(0);
			row.createCell(0).setCellValue("姓名");
			row.createCell(1).setCellValue("手机");
			row.createCell(2).setCellValue("政务钉钉ID");
			for (int i = 0; i < users.size(); i++) {
				User user = users.get(i);
				row = sheet.createRow(i + 1);
				row.createCell(0).setCellValue(user.getName());
				row.createCell(1).setCellValue(user.getMobile());
				row.createCell(2).setCellValue(user.getZhengwuDingdingId());
				for (int j = 0; j < user.getUnitList().size(); j++) {
					row.createCell(j + 3).setCellValue(user.getUnitList().get(j));
				}
			}
			workBook.write(baos);
			CacheFileResult cacheFileResult = new CacheFileResult();
			cacheFileResult.setBytes(baos.toByteArray());
			cacheFileResult.setName("政务钉钉人员.xlsx");
			Wo wo = new Wo();
			wo.setFlag(StringTools.uniqueToken());
			CacheKey cacheKey = new CacheKey(wo.getFlag());
			CacheManager.put(this.cacheCategory, cacheKey, cacheFileResult);
			result.setData(wo);
			return result;
		}
	}

	private List<Person> listPerson(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		Predicate p = cb.isNotEmpty(root.get(Person.zhengwuDingdingId_FIELDNAME));
		List<Person> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Unit> listUnit(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.isNotEmpty(root.get(Unit.zhengwuDingdingId_FIELDNAME));
		List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList();
		os = os.stream().sorted(Comparator.comparing(Unit::getOrderNumber, Comparator.nullsLast(Integer::compareTo)))
				.collect(Collectors.toList());
		return os;
	}

	private List<Person> listPersonWithUnit(Unit unit) throws Exception {
		List<Identity> identities = this.listIdentityWithUnit(unit);
		return this.listWithIdentity(identities);
	}

	private List<Identity> listIdentityWithUnit(Unit unit) throws Exception {
		List<Identity> os = allIdentity.stream().filter(o -> {
			return StringUtils.equals(unit.getId(), o.getUnit());
		}).collect(Collectors.toList());
		return os;
	}

	private List<Person> listWithIdentity(List<Identity> identities) throws Exception {
		final List<String> ids = ListTools.extractProperty(identities, Identity.person_FIELDNAME, String.class, true,
				true);
		List<Person> os = allPerson.stream().filter(o -> {
			return ListTools.contains(ids, o.getId());
		}).collect(Collectors.toList());
		return os;
	}

	private List<Unit> listUnitWithPerson(Person person) throws Exception {
		List<Identity> identities = this.listIdentityWithPerson(person);
		final List<String> ids = ListTools.extractProperty(identities, Identity.unit_FIELDNAME, String.class, true,
				true);
		List<Unit> os = allUnit.stream().filter(o -> {
			return ListTools.contains(ids, o.getId());
		}).collect(Collectors.toList());
		return os;
	}

	private List<Identity> listIdentityWithPerson(Person person) {
		List<Identity> os = allIdentity.stream().filter(o -> {
			return StringUtils.equals(o.getPerson(), person.getId());
		}).collect(Collectors.toList());
		return os;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("返回的结果标识")
		private String flag;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}

	}

	public static class User extends GsonPropertyObject {
		private String name;

		private String mobile;

		private String zhengwuDingdingId;

		private String zhengwuDingdingTitle;

		private List<String> unitList;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getZhengwuDingdingId() {
			return zhengwuDingdingId;
		}

		public void setZhengwuDingdingId(String zhengwuDingdingId) {
			this.zhengwuDingdingId = zhengwuDingdingId;
		}

		public String getZhengwuDingdingTitle() {
			return zhengwuDingdingTitle;
		}

		public void setZhengwuDingdingTitle(String zhengwuDingdingTitle) {
			this.zhengwuDingdingTitle = zhengwuDingdingTitle;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}
	}
}