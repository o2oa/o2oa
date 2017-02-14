package com.x.organization.assemble.control.servlet.input;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.Crypto;
import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.DateTools;
import com.x.organization.assemble.control.MappingItem;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;
import com.x.organization.core.entity.CompanyDuty;
import com.x.organization.core.entity.Department;
import com.x.organization.core.entity.DepartmentAttribute;
import com.x.organization.core.entity.DepartmentDuty;
import com.x.organization.core.entity.GenderType;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Role;

@WebServlet(urlPatterns = "/servlet/input")
@MultipartConfig
public class InputServlet extends HttpServlet {

	private static final long serialVersionUID = -965077663224296165L;

	@HttpMethodDescribe(value = "批量导入人员", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);
			if (!effectivePerson.isManager()) {
				throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
			}
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("not multi part request.");
			}
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator fileItemIterator = upload.getItemIterator(request);
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
						InputStream in = item.openStream();
						XSSFWorkbook workbook = new XSSFWorkbook(in)) {
					if (!item.isFormField()) {
						this.input(emc, workbook);
						response.setHeader("Content-Type", "application/octet-stream");
						response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder
								.encode("organization_result_" + DateTools.formatDate(new Date()) + ".xlsx", "utf-8"));
						workbook.write(response.getOutputStream());
						return;
					}
				}
			}
			ApplicationCache.notify(Person.class);
			ApplicationCache.notify(Group.class);
			ApplicationCache.notify(Role.class);
			ApplicationCache.notify(Company.class);
			ApplicationCache.notify(Department.class);
			ApplicationCache.notify(Identity.class);
			ApplicationCache.notify(PersonAttribute.class);
			ApplicationCache.notify(CompanyAttribute.class);
			ApplicationCache.notify(CompanyDuty.class);
			ApplicationCache.notify(DepartmentAttribute.class);
			ApplicationCache.notify(DepartmentDuty.class);
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			FileUploadServletTools.result(response, result);
		}
	}

	private void input(EntityManagerContainer emc, XSSFWorkbook workbook) {
		try {
			this.intputPerson(emc, workbook);
			this.intputGroup(emc, workbook);
			this.intputRole(emc, workbook);
			this.intputCompany(emc, workbook);
			this.intputDepartment(emc, workbook);
			this.intputIdentity(emc, workbook);
			this.intputPersonAttribute(emc, workbook);
			this.intputCompanyAttribute(emc, workbook);
			this.intputCompanyDuty(emc, workbook);
			this.intputDepartmentAttribute(emc, workbook);
			this.intputDepartmentDuty(emc, workbook);
			emc.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setValue(Row row, JpaObject jpaObject, MappingItem item) throws Exception {
		Cell cell = row.getCell(item.getColumn());
		if (null != cell) {
			switch (item.getType()) {
			case string:
				String stringValue = cell.getStringCellValue();
				if (null != stringValue) {
					PropertyUtils.setProperty(jpaObject, item.getField(), stringValue);
				}
				break;
			case integer:
				Double doubeValue = cell.getNumericCellValue();
				if (null != doubeValue) {
					PropertyUtils.setProperty(jpaObject, item.getField(), doubeValue.intValue());
				}
				break;
			case date:
				Date dateValue = cell.getDateCellValue();
				if (null != dateValue) {
					PropertyUtils.setProperty(jpaObject, item.getField(), dateValue);
				}
				break;
			case genderType:
				String genderTypeStringValue = cell.getStringCellValue();
				if (StringUtils.isNotEmpty(genderTypeStringValue)) {
					if (StringUtils.equalsIgnoreCase(GenderType.f.toString(), genderTypeStringValue)) {
						PropertyUtils.setProperty(jpaObject, item.getField(), GenderType.f);
					} else if (StringUtils.equalsIgnoreCase(GenderType.m.toString(), genderTypeStringValue)) {
						PropertyUtils.setProperty(jpaObject, item.getField(), GenderType.m);
					} else {
						PropertyUtils.setProperty(jpaObject, item.getField(), GenderType.d);
					}
				}
				break;
			case stringList:
				String stringListValue = cell.getStringCellValue();
				if (null != stringListValue) {
					List<String> list = new ArrayList<>(Arrays.asList(StringUtils.split(stringListValue, ",")));
					PropertyUtils.setProperty(jpaObject, item.getField(), list);
				}
				break;
			default:
				break;
			}
		}
	}

	private void intputPerson(EntityManagerContainer emc, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheet("人员");
		if (null != sheet) {
			List<MappingItem> mappings = MappingItem.personMappings(sheet);
			Row row = null;
			List<Person> list = new ArrayList<>();
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				try {
					Person o = new Person();
					for (MappingItem item : mappings) {
						this.setValue(row, o, item);
					}
					if (null == emc.find(o.getId(), Person.class)) {
						list.add(o);
					} else {
						list.add(null);
						row.createCell(row.getLastCellNum()).setCellValue("person already existed.");
					}
				} catch (Exception e) {
					row.createCell(row.getLastCellNum()).setCellValue(e.getMessage());
					throw e;
				}
			}
			int n = 0;
			try {
				emc.beginTransaction(Person.class);
				for (n = 0; n < list.size(); n++) {
					Person o = list.get(n);
					if (null != o) {
						if (StringUtils.isEmpty(o.getPassword())) {
							/* 使用默认密码 */
							o.setPassword(Crypto.encrypt(Config.personTemplate().getDefaultPassword(),
									Config.token().getKey()));
						} else if ((StringUtils.startsWith(o.getPassword(), "("))
								&& (StringUtils.endsWith(o.getPassword(), ")"))) {
							/* 使用明文密码 */
							String str = o.getPassword().substring(1, o.getPassword().length() - 1);
							if (StringUtils.isEmpty(str)) {
								throw new Exception("can not use empty as password.");
							} else {
								o.setPassword(Crypto.encrypt(str, Config.token().getKey()));
							}
						}
						emc.persist(list.get(n));
					}
				}
				for (n = 0; n < list.size(); n++) {
					Person o = list.get(n);
					if (null != o) {
						emc.check(list.get(n), CheckPersistType.all);
					}
				}
			} catch (Exception e) {
				row = sheet.getRow(n + 1 + sheet.getFirstRowNum());
				row.createCell(row.getLastCellNum() + 1).setCellValue(e.getMessage());
				throw e;
			}
		}
	}

	private void intputGroup(EntityManagerContainer emc, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheet("群组");
		if (null != sheet) {
			List<MappingItem> mappings = MappingItem.groupMappings(sheet);
			Row row = null;
			List<Group> list = new ArrayList<>();
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				try {
					Group o = new Group();
					for (MappingItem item : mappings) {
						this.setValue(row, o, item);
					}
					if (null == emc.find(o.getId(), Group.class)) {
						list.add(o);
					} else {
						list.add(null);
						row.createCell(row.getLastCellNum()).setCellValue("group already existed.");
					}
				} catch (Exception e) {
					row.createCell(row.getLastCellNum()).setCellValue(e.getMessage());
					throw e;
				}
			}
			int n = 0;
			try {
				emc.beginTransaction(Group.class);
				for (n = 0; n < list.size(); n++) {
					Group o = list.get(n);
					if (null != o) {
						emc.persist(list.get(n));
					}
				}
				for (n = 0; n < list.size(); n++) {
					Group o = list.get(n);
					if (null != o) {
						emc.check(list.get(n), CheckPersistType.all);
					}
				}
			} catch (Exception e) {
				row = sheet.getRow(n + 1 + sheet.getFirstRowNum());
				row.createCell(row.getLastCellNum() + 1).setCellValue(e.getMessage());
				throw e;
			}
		}
	}

	private void intputRole(EntityManagerContainer emc, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheet("角色");
		if (null != sheet) {
			List<MappingItem> mappings = MappingItem.roleMappings(sheet);
			Row row = null;
			List<Role> list = new ArrayList<>();
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				try {
					Role o = new Role();
					for (MappingItem item : mappings) {
						this.setValue(row, o, item);
					}
					if (null == emc.find(o.getId(), Role.class)) {
						list.add(o);
					} else {
						list.add(null);
						row.createCell(row.getLastCellNum()).setCellValue("role already existed.");
					}
				} catch (Exception e) {
					row.createCell(row.getLastCellNum()).setCellValue(e.getMessage());
					throw e;
				}
			}
			int n = 0;
			try {
				emc.beginTransaction(Role.class);
				for (n = 0; n < list.size(); n++) {
					Role o = list.get(n);
					if (null != o) {
						emc.persist(list.get(n));
					}
				}
				for (n = 0; n < list.size(); n++) {
					Role o = list.get(n);
					if (null != o) {
						emc.check(list.get(n), CheckPersistType.all);
					}
				}
			} catch (Exception e) {
				row = sheet.getRow(n + 1 + sheet.getFirstRowNum());
				row.createCell(row.getLastCellNum() + 1).setCellValue(e.getMessage());
				throw e;
			}
		}
	}

	private void intputCompany(EntityManagerContainer emc, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheet("公司");
		if (null != sheet) {
			List<MappingItem> mappings = MappingItem.companyMappings(sheet);
			Row row = null;
			List<Company> list = new ArrayList<>();
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				try {
					Company o = new Company();
					for (MappingItem item : mappings) {
						this.setValue(row, o, item);
					}
					if (null == emc.find(o.getId(), Company.class)) {
						list.add(o);
					} else {
						list.add(null);
						row.createCell(row.getLastCellNum()).setCellValue("company already existed.");
					}
				} catch (Exception e) {
					row.createCell(row.getLastCellNum()).setCellValue(e.getMessage());
					throw e;
				}
			}
			int n = 0;
			try {
				emc.beginTransaction(Company.class);
				for (n = 0; n < list.size(); n++) {
					Company o = list.get(n);
					if (null != o) {
						emc.persist(list.get(n));
					}
				}
				for (n = 0; n < list.size(); n++) {
					Company o = list.get(n);
					if (null != o) {
						emc.check(list.get(n), CheckPersistType.all);
					}
				}
			} catch (Exception e) {
				row = sheet.getRow(n + 1 + sheet.getFirstRowNum());
				row.createCell(row.getLastCellNum() + 1).setCellValue(e.getMessage());
				throw e;
			}
		}
	}

	private void intputDepartment(EntityManagerContainer emc, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheet("部门");
		if (null != sheet) {
			List<MappingItem> mappings = MappingItem.departmentMappings(sheet);
			Row row = null;
			List<Department> list = new ArrayList<>();
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				try {
					Department o = new Department();
					for (MappingItem item : mappings) {
						this.setValue(row, o, item);
					}
					if (null == emc.find(o.getId(), Department.class)) {
						list.add(o);
					} else {
						list.add(null);
						row.createCell(row.getLastCellNum()).setCellValue("department already existed.");
					}
				} catch (Exception e) {
					row.createCell(row.getLastCellNum()).setCellValue(e.getMessage());
					throw e;
				}
			}
			int n = 0;
			try {
				emc.beginTransaction(Department.class);
				for (n = 0; n < list.size(); n++) {
					Department o = list.get(n);
					if (null != o) {
						emc.persist(list.get(n));
					}
				}
				for (n = 0; n < list.size(); n++) {
					Department o = list.get(n);
					if (null != o) {
						emc.check(list.get(n), CheckPersistType.all);
					}
				}
			} catch (Exception e) {
				row = sheet.getRow(n + 1 + sheet.getFirstRowNum());
				row.createCell(row.getLastCellNum() + 1).setCellValue(e.getMessage());
				throw e;
			}
		}
	}

	private void intputIdentity(EntityManagerContainer emc, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheet("身份");
		if (null != sheet) {
			List<MappingItem> mappings = MappingItem.identityMappings(sheet);
			Row row = null;
			List<Identity> list = new ArrayList<>();
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				try {
					Identity o = new Identity();
					for (MappingItem item : mappings) {
						this.setValue(row, o, item);
					}
					if (null == emc.find(o.getId(), Identity.class)) {
						list.add(o);
					} else {
						list.add(null);
						row.createCell(row.getLastCellNum()).setCellValue("identity already existed.");
					}
				} catch (Exception e) {
					row.createCell(row.getLastCellNum()).setCellValue(e.getMessage());
					throw e;
				}
			}
			int n = 0;
			try {
				emc.beginTransaction(Identity.class);
				for (n = 0; n < list.size(); n++) {
					Identity o = list.get(n);
					if (null != o) {
						emc.persist(list.get(n));
					}
				}
				for (n = 0; n < list.size(); n++) {
					Identity o = list.get(n);
					if (null != o) {
						emc.check(list.get(n), CheckPersistType.all);
					}
				}
			} catch (Exception e) {
				row = sheet.getRow(n + 1 + sheet.getFirstRowNum());
				row.createCell(row.getLastCellNum() + 1).setCellValue(e.getMessage());
				throw e;
			}
		}
	}

	private void intputPersonAttribute(EntityManagerContainer emc, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheet("人员属性");
		if (null != sheet) {
			List<MappingItem> mappings = MappingItem.personAttributeMappings(sheet);
			Row row = null;
			List<PersonAttribute> list = new ArrayList<>();
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				try {
					PersonAttribute o = new PersonAttribute();
					for (MappingItem item : mappings) {
						this.setValue(row, o, item);
					}
					if (null == emc.find(o.getId(), PersonAttribute.class)) {
						list.add(o);
					} else {
						list.add(null);
						row.createCell(row.getLastCellNum()).setCellValue("personAttribute already existed.");
					}
				} catch (Exception e) {
					row.createCell(row.getLastCellNum()).setCellValue(e.getMessage());
					throw e;
				}
			}
			int n = 0;
			try {
				emc.beginTransaction(PersonAttribute.class);
				for (n = 0; n < list.size(); n++) {
					PersonAttribute o = list.get(n);
					if (null != o) {
						emc.persist(list.get(n));
					}
				}
				for (n = 0; n < list.size(); n++) {
					PersonAttribute o = list.get(n);
					if (null != o) {
						emc.check(list.get(n), CheckPersistType.all);
					}
				}
			} catch (Exception e) {
				row = sheet.getRow(n + 1 + sheet.getFirstRowNum());
				row.createCell(row.getLastCellNum() + 1).setCellValue(e.getMessage());
				throw e;
			}
		}
	}

	private void intputCompanyAttribute(EntityManagerContainer emc, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheet("公司属性");
		if (null != sheet) {
			List<MappingItem> mappings = MappingItem.companyAttributeMappings(sheet);
			Row row = null;
			List<CompanyAttribute> list = new ArrayList<>();
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				try {
					CompanyAttribute o = new CompanyAttribute();
					for (MappingItem item : mappings) {
						this.setValue(row, o, item);
					}
					if (null == emc.find(o.getId(), CompanyAttribute.class)) {
						list.add(o);
					} else {
						list.add(null);
						row.createCell(row.getLastCellNum()).setCellValue("companyAttribute already existed.");
					}
				} catch (Exception e) {
					row.createCell(row.getLastCellNum()).setCellValue(e.getMessage());
					throw e;
				}
			}
			int n = 0;
			try {
				emc.beginTransaction(CompanyAttribute.class);
				for (n = 0; n < list.size(); n++) {
					CompanyAttribute o = list.get(n);
					if (null != o) {
						emc.persist(list.get(n));
					}
				}
				for (n = 0; n < list.size(); n++) {
					CompanyAttribute o = list.get(n);
					if (null != o) {
						emc.check(list.get(n), CheckPersistType.all);
					}
				}
			} catch (Exception e) {
				row = sheet.getRow(n + 1 + sheet.getFirstRowNum());
				row.createCell(row.getLastCellNum() + 1).setCellValue(e.getMessage());
				throw e;
			}
		}
	}

	private void intputCompanyDuty(EntityManagerContainer emc, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheet("公司职务");
		if (null != sheet) {
			List<MappingItem> mappings = MappingItem.companyDutyMappings(sheet);
			Row row = null;
			List<CompanyDuty> list = new ArrayList<>();
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				try {
					CompanyDuty o = new CompanyDuty();
					for (MappingItem item : mappings) {
						this.setValue(row, o, item);
					}
					if (null == emc.find(o.getId(), CompanyDuty.class)) {
						list.add(o);
					} else {
						list.add(null);
						row.createCell(row.getLastCellNum()).setCellValue("companyDuty already existed.");
					}
				} catch (Exception e) {
					row.createCell(row.getLastCellNum()).setCellValue(e.getMessage());
					throw e;
				}
			}
			int n = 0;
			try {
				emc.beginTransaction(CompanyDuty.class);
				for (n = 0; n < list.size(); n++) {
					CompanyDuty o = list.get(n);
					if (null != o) {
						emc.persist(list.get(n));
					}
				}
				for (n = 0; n < list.size(); n++) {
					CompanyDuty o = list.get(n);
					if (null != o) {
						emc.check(list.get(n), CheckPersistType.all);
					}
				}
			} catch (Exception e) {
				row = sheet.getRow(n + 1 + sheet.getFirstRowNum());
				row.createCell(row.getLastCellNum() + 1).setCellValue(e.getMessage());
				throw e;
			}
		}
	}

	private void intputDepartmentAttribute(EntityManagerContainer emc, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheet("部门属性");
		if (null != sheet) {
			List<MappingItem> mappings = MappingItem.departmentAttributeMappings(sheet);
			Row row = null;
			List<DepartmentAttribute> list = new ArrayList<>();
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				try {
					DepartmentAttribute o = new DepartmentAttribute();
					for (MappingItem item : mappings) {
						this.setValue(row, o, item);
					}
					if (null == emc.find(o.getId(), DepartmentAttribute.class)) {
						list.add(o);
					} else {
						list.add(null);
						row.createCell(row.getLastCellNum()).setCellValue("departmentAttribute already existed.");
					}
				} catch (Exception e) {
					row.createCell(row.getLastCellNum()).setCellValue(e.getMessage());
					throw e;
				}
			}
			int n = 0;
			try {
				emc.beginTransaction(DepartmentAttribute.class);
				for (n = 0; n < list.size(); n++) {
					DepartmentAttribute o = list.get(n);
					if (null != o) {
						emc.persist(list.get(n));
					}
				}
				for (n = 0; n < list.size(); n++) {
					DepartmentAttribute o = list.get(n);
					if (null != o) {
						emc.check(list.get(n), CheckPersistType.all);
					}
				}
			} catch (Exception e) {
				row = sheet.getRow(n + 1 + sheet.getFirstRowNum());
				row.createCell(row.getLastCellNum() + 1).setCellValue(e.getMessage());
				throw e;
			}
		}
	}

	private void intputDepartmentDuty(EntityManagerContainer emc, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheet("部门职务");
		if (null != sheet) {
			List<MappingItem> mappings = MappingItem.departmentDutyMappings(sheet);
			Row row = null;
			List<DepartmentDuty> list = new ArrayList<>();
			for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				try {
					DepartmentDuty o = new DepartmentDuty();
					for (MappingItem item : mappings) {
						this.setValue(row, o, item);
					}
					if (null == emc.find(o.getId(), DepartmentDuty.class)) {
						list.add(o);
					} else {
						list.add(null);
						row.createCell(row.getLastCellNum()).setCellValue("departmentDuty already existed.");
					}
				} catch (Exception e) {
					row.createCell(row.getLastCellNum()).setCellValue(e.getMessage());
					throw e;
				}
			}
			int n = 0;
			try {
				emc.beginTransaction(DepartmentDuty.class);
				for (n = 0; n < list.size(); n++) {
					DepartmentDuty o = list.get(n);
					if (null != o) {
						emc.persist(list.get(n));
					}
				}
				for (n = 0; n < list.size(); n++) {
					DepartmentDuty o = list.get(n);
					if (null != o) {
						emc.check(list.get(n), CheckPersistType.all);
					}
				}
			} catch (Exception e) {
				row = sheet.getRow(n + 1 + sheet.getFirstRowNum());
				row.createCell(row.getLastCellNum() + 1).setCellValue(e.getMessage());
				throw e;
			}
		}
	}

}