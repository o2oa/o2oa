package com.x.organization.assemble.control.servlet.output;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.DateTools;
import com.x.organization.assemble.control.MappingItem;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;
import com.x.organization.core.entity.CompanyAttribute_;
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.CompanyDuty_;
import com.x.organization.core.entity.Company_;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentAttribute;
import com.x.organization.core.entity.DepartmentAttribute_;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.DepartmentDuty_;
import com.x.organization.core.entity.Department_;
import com.x.organization.core.entity.GenderType;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;
import com.x.organization.core.entity.Person_;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;

@WebServlet(urlPatterns = "/servlet/output/*")
@MultipartConfig
public class OutputServlet extends AbstractServletAction {

	private static final long serialVersionUID = 5696850299231151065L;

	@HttpMethodDescribe(value = "将通讯录导出成Excel")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Workbook workbook = new XSSFWorkbook()) {
			request.setCharacterEncoding("UTF-8");
			EffectivePerson effectivePerson = this.effectivePerson(request);
			if (!effectivePerson.isManager()) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
			}
			this.createPersonSheet(emc, workbook);
			this.createGroupSheet(emc, workbook);
			this.createRoleSheet(emc, workbook);
			this.createCompanySheet(emc, workbook);
			this.createDepartmentSheet(emc, workbook);
			this.createIdentitySheet(emc, workbook);
			this.createPersonAttributeSheet(emc, workbook);
			this.createCompanyAttributeSheet(emc, workbook);
			this.createCompanyDutySheet(emc, workbook);
			this.createDepartmentAttributeSheet(emc, workbook);
			this.createDepartmentDutySheet(emc, workbook);
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ URLEncoder.encode("organization_" + DateTools.formatDate(new Date()) + ".xlsx", "utf-8"));
			workbook.write(response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			this.result(response, result);
		}
	}

	private void setCellValue(Cell cell, JpaObject jpaObject, MappingItem item) throws Exception {
		switch (item.getType()) {
		case string:
			String stringValue = jpaObject.get(item.getField(), String.class);
			if (null != stringValue) {
				cell.setCellValue(stringValue);
			}
			break;
		case integer:
			Integer integerValue = jpaObject.get(item.getField(), Integer.class);
			if (null != integerValue) {
				cell.setCellValue(integerValue);
			}
			break;
		case date:
			Date dateValue = jpaObject.get(item.getField(), Date.class);
			if (null != dateValue) {
				cell.setCellValue(dateValue);
			}
			break;
		case genderType:
			GenderType genderTypeValue = jpaObject.get("genderType", GenderType.class);
			if (null != genderTypeValue) {
				if (Objects.equals(GenderType.f, genderTypeValue)) {
					cell.setCellValue("女");
				} else if (Objects.equals(GenderType.m, genderTypeValue)) {
					cell.setCellValue("男");
				} else {
					cell.setCellValue("未知");
				}
			}
			break;
		case stringList:
			@SuppressWarnings("unchecked")
			List<String> stringListValue = (List<String>) jpaObject.get(item.getField());
			if (null != stringListValue) {
				cell.setCellValue(StringUtils.join(stringListValue, ","));
			}
			break;
		default:
			break;
		}
	}

	private List<Person> listPerson(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		cq.select(root).orderBy(cb.asc(root.get(Person_.name)));
		return em.createQuery(cq).getResultList();
	}

	private List<Group> listGroup(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Group> cq = cb.createQuery(Group.class);
		Root<Group> root = cq.from(Group.class);
		cq.select(root).orderBy(cb.asc(root.get(Group_.name)));
		return em.createQuery(cq).getResultList();
	}

	private List<Role> listRole(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Role> cq = cb.createQuery(Role.class);
		Root<Role> root = cq.from(Role.class);
		cq.select(root).orderBy(cb.asc(root.get(Role_.name)));
		return em.createQuery(cq).getResultList();
	}

	private List<Company> listCompany(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Company.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Company> cq = cb.createQuery(Company.class);
		Root<Company> root = cq.from(Company.class);
		cq.select(root).orderBy(cb.asc(root.get(Company_.level)), cb.asc(root.get(Company_.name)));
		return em.createQuery(cq).getResultList();
	}

	private List<Department> listDepartment(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Department.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Department> cq = cb.createQuery(Department.class);
		Root<Department> root = cq.from(Department.class);
		cq.select(root).orderBy(cb.asc(root.get(Department_.level)), cb.asc(root.get(Department_.name)));
		return em.createQuery(cq).getResultList();
	}

	private List<Identity> listIdentity(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		cq.select(root).orderBy(cb.asc(root.get(Identity_.name)));
		return em.createQuery(cq).getResultList();
	}

	private List<PersonAttribute> listPersonAttribute(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PersonAttribute> cq = cb.createQuery(PersonAttribute.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		cq.select(root).orderBy(cb.asc(root.get(PersonAttribute_.name)));
		return em.createQuery(cq).getResultList();
	}

	private List<CompanyAttribute> listCompanyAttribute(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(CompanyAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CompanyAttribute> cq = cb.createQuery(CompanyAttribute.class);
		Root<CompanyAttribute> root = cq.from(CompanyAttribute.class);
		cq.select(root).orderBy(cb.asc(root.get(CompanyAttribute_.name)));
		return em.createQuery(cq).getResultList();
	}

	private List<CompanyDuty> listCompanyDuty(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(CompanyDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CompanyDuty> cq = cb.createQuery(CompanyDuty.class);
		Root<CompanyDuty> root = cq.from(CompanyDuty.class);
		cq.select(root).orderBy(cb.asc(root.get(CompanyDuty_.name)));
		return em.createQuery(cq).getResultList();
	}

	private List<DepartmentAttribute> listDepartmentAttribute(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(DepartmentAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DepartmentAttribute> cq = cb.createQuery(DepartmentAttribute.class);
		Root<DepartmentAttribute> root = cq.from(DepartmentAttribute.class);
		cq.select(root).orderBy(cb.asc(root.get(DepartmentAttribute_.name)));
		return em.createQuery(cq).getResultList();
	}

	private List<DepartmentDuty> listDepartmentDuty(EntityManagerContainer emc) throws Exception {
		EntityManager em = emc.get(DepartmentDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DepartmentDuty> cq = cb.createQuery(DepartmentDuty.class);
		Root<DepartmentDuty> root = cq.from(DepartmentDuty.class);
		cq.select(root).orderBy(cb.asc(root.get(DepartmentDuty_.name)));
		return em.createQuery(cq).getResultList();
	}

	private void fillRow(Sheet sheet, Integer index, JpaObject o, List<MappingItem> mappings) throws Exception {
		Row row = sheet.createRow(index);
		Cell cell = null;
		for (int i = 0; i < mappings.size(); i++) {
			MappingItem item = mappings.get(i);
			cell = row.createCell(i);
			this.setCellValue(cell, o, item);
		}
	}

	private void createPersonSheet(EntityManagerContainer emc, Workbook workbook) throws Exception {
		Sheet sheet = workbook.createSheet("人员");
		List<MappingItem> mappings = MappingItem.personMappings();
		Row row = sheet.createRow(0);
		Cell cell = null;
		for (int i = 0; i < mappings.size(); i++) {
			MappingItem item = mappings.get(i);
			cell = row.createCell(i);
			cell.setCellValue(item.getName());
		}
		List<Person> list = this.listPerson(emc);
		int index = 1;
		for (JpaObject o : list) {
			this.fillRow(sheet, index++, o, mappings);
		}
	}

	private void createGroupSheet(EntityManagerContainer emc, Workbook workbook) throws Exception {
		Sheet sheet = workbook.createSheet("群组");
		List<MappingItem> mappings = MappingItem.groupMappings();
		Row row = sheet.createRow(0);
		Cell cell = null;
		for (int i = 0; i < mappings.size(); i++) {
			MappingItem item = mappings.get(i);
			cell = row.createCell(i);
			cell.setCellValue(item.getName());
		}
		List<Group> list = this.listGroup(emc);
		int index = 1;
		for (JpaObject o : list) {
			this.fillRow(sheet, index++, o, mappings);
		}
	}

	private void createRoleSheet(EntityManagerContainer emc, Workbook workbook) throws Exception {
		Sheet sheet = workbook.createSheet("角色");
		List<MappingItem> mappings = MappingItem.roleMappings();
		Row row = sheet.createRow(0);
		Cell cell = null;
		for (int i = 0; i < mappings.size(); i++) {
			MappingItem item = mappings.get(i);
			cell = row.createCell(i);
			cell.setCellValue(item.getName());
		}
		List<Role> list = this.listRole(emc);
		int index = 1;
		for (JpaObject o : list) {
			this.fillRow(sheet, index++, o, mappings);
		}
	}

	private void createCompanySheet(EntityManagerContainer emc, Workbook workbook) throws Exception {
		Sheet sheet = workbook.createSheet("公司");
		List<MappingItem> mappings = MappingItem.companyMappings();
		Row row = sheet.createRow(0);
		Cell cell = null;
		for (int i = 0; i < mappings.size(); i++) {
			MappingItem item = mappings.get(i);
			cell = row.createCell(i);
			cell.setCellValue(item.getName());
		}
		List<Company> list = this.listCompany(emc);
		int index = 1;
		for (JpaObject o : list) {
			this.fillRow(sheet, index++, o, mappings);
		}
	}

	private void createDepartmentSheet(EntityManagerContainer emc, Workbook workbook) throws Exception {
		Sheet sheet = workbook.createSheet("部门");
		List<MappingItem> mappings = MappingItem.departmentMappings();
		Row row = sheet.createRow(0);
		Cell cell = null;
		for (int i = 0; i < mappings.size(); i++) {
			MappingItem item = mappings.get(i);
			cell = row.createCell(i);
			cell.setCellValue(item.getName());
		}
		List<Department> list = this.listDepartment(emc);
		int index = 1;
		for (JpaObject o : list) {
			this.fillRow(sheet, index++, o, mappings);
		}
	}

	private void createIdentitySheet(EntityManagerContainer emc, Workbook workbook) throws Exception {
		Sheet sheet = workbook.createSheet("身份");
		List<MappingItem> mappings = MappingItem.identityMappings();
		Row row = sheet.createRow(0);
		Cell cell = null;
		for (int i = 0; i < mappings.size(); i++) {
			MappingItem item = mappings.get(i);
			cell = row.createCell(i);
			cell.setCellValue(item.getName());
		}
		List<Identity> list = this.listIdentity(emc);
		int index = 1;
		for (JpaObject o : list) {
			this.fillRow(sheet, index++, o, mappings);
		}
	}

	private void createPersonAttributeSheet(EntityManagerContainer emc, Workbook workbook) throws Exception {
		Sheet sheet = workbook.createSheet("人员属性");
		List<MappingItem> mappings = MappingItem.personAttributeMappings();
		Row row = sheet.createRow(0);
		Cell cell = null;
		for (int i = 0; i < mappings.size(); i++) {
			MappingItem item = mappings.get(i);
			cell = row.createCell(i);
			cell.setCellValue(item.getName());
		}
		List<PersonAttribute> list = this.listPersonAttribute(emc);
		int index = 1;
		for (JpaObject o : list) {
			this.fillRow(sheet, index++, o, mappings);
		}
	}

	private void createCompanyAttributeSheet(EntityManagerContainer emc, Workbook workbook) throws Exception {
		Sheet sheet = workbook.createSheet("公司属性");
		List<MappingItem> mappings = MappingItem.companyAttributeMappings();
		Row row = sheet.createRow(0);
		Cell cell = null;
		for (int i = 0; i < mappings.size(); i++) {
			MappingItem item = mappings.get(i);
			cell = row.createCell(i);
			cell.setCellValue(item.getName());
		}
		List<CompanyAttribute> list = this.listCompanyAttribute(emc);
		int index = 1;
		for (JpaObject o : list) {
			this.fillRow(sheet, index++, o, mappings);
		}
	}

	private void createCompanyDutySheet(EntityManagerContainer emc, Workbook workbook) throws Exception {
		Sheet sheet = workbook.createSheet("公司职务");
		List<MappingItem> mappings = MappingItem.companyDutyMappings();
		Row row = sheet.createRow(0);
		Cell cell = null;
		for (int i = 0; i < mappings.size(); i++) {
			MappingItem item = mappings.get(i);
			cell = row.createCell(i);
			cell.setCellValue(item.getName());
		}
		List<CompanyDuty> list = this.listCompanyDuty(emc);
		int index = 1;
		for (JpaObject o : list) {
			this.fillRow(sheet, index++, o, mappings);
		}
	}

	private void createDepartmentAttributeSheet(EntityManagerContainer emc, Workbook workbook) throws Exception {
		Sheet sheet = workbook.createSheet("部门属性");
		List<MappingItem> mappings = MappingItem.departmentAttributeMappings();
		Row row = sheet.createRow(0);
		Cell cell = null;
		for (int i = 0; i < mappings.size(); i++) {
			MappingItem item = mappings.get(i);
			cell = row.createCell(i);
			cell.setCellValue(item.getName());
		}
		List<DepartmentAttribute> list = this.listDepartmentAttribute(emc);
		int index = 1;
		for (JpaObject o : list) {
			this.fillRow(sheet, index++, o, mappings);
		}
	}

	private void createDepartmentDutySheet(EntityManagerContainer emc, Workbook workbook) throws Exception {
		Sheet sheet = workbook.createSheet("部门职务");
		List<MappingItem> mappings = MappingItem.departmentDutyMappings();
		Row row = sheet.createRow(0);
		Cell cell = null;
		for (int i = 0; i < mappings.size(); i++) {
			MappingItem item = mappings.get(i);
			cell = row.createCell(i);
			cell.setCellValue(item.getName());
		}
		List<DepartmentDuty> list = this.listDepartmentDuty(emc);
		int index = 1;
		for (DepartmentDuty o : list) {
			this.fillRow(sheet, index++, o, mappings);
		}
	}
}