package com.x.organization.assemble.control.servlet.input;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.Crypto;
import com.x.base.core.DefaultCharset;
import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.Config;
import com.x.base.core.utils.DateTools;
import com.x.base.core.utils.StringTools;
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

@WebServlet(urlPatterns = "/servlet/input/person")
@MultipartConfig
public class ActionInputPerson extends AbstractServletAction {

	private static Logger logger = LoggerFactory.getLogger(ActionInputPerson.class);

	private static List<String> genderTypeFemaleItems = Arrays.asList(new String[] { "f", "女", "female" });
	private static List<String> genderTypeMaleItems = Arrays.asList(new String[] { "m", "男", "male" });

	private static final long serialVersionUID = -965077663224296165L;

	@HttpMethodDescribe(value = "批量导入人员", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		EffectivePerson effectivePerson = this.effectivePerson(request);
		try {
			this.setCharacterEncoding(request, response);
			if (!effectivePerson.isManager()) {
				throw new SufficientPermissionException(effectivePerson.getName());
			}
			if (!this.isMultipartContent(request)) {
				throw new Exception("not multi part request.");
			}
			FileItemIterator fileItemIterator = this.getItemIterator(request);
			while (fileItemIterator.hasNext()) {
				FileItemStream item = fileItemIterator.next();
				try (InputStream in = item.openStream(); XSSFWorkbook workbook = new XSSFWorkbook(in)) {
					if (!item.isFormField()) {
						this.scan(workbook);
						response.setHeader("Content-Type", "application/octet-stream");
						response.setHeader("Content-Disposition",
								"attachment; filename=" + URLEncoder.encode(
										"person_result_" + DateTools.formatDate(new Date()) + ".xlsx",
										DefaultCharset.name));
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
			logger.error(e, effectivePerson, request, null);
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			this.result(response, result);
		}
	}

	private void scan(XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheetAt(0);
		List<PersonItem> people = new ArrayList<>();
		PersonSheetConfigurator configurator = new PersonSheetConfigurator(workbook, sheet);
		this.scanPerson(configurator, sheet, people);
		this.concretePassword(people);
		this.persist(workbook, configurator, people);
	}

	private void concretePassword(List<PersonItem> people) throws Exception {
		Pattern pattern = Pattern.compile(com.x.base.core.project.server.Person.RegularExpression_Script);
		Matcher matcher = pattern.matcher(Config.person().getPassword());
		if (matcher.matches()) {
			String eval = matcher.group(1);
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("nashorn");
			for (PersonItem o : people) {
				engine.put("person", o);
				String pass = engine.eval(eval).toString();
				o.setPassword(pass);
			}
		} else {
			for (PersonItem o : people) {
				o.setPassword(Config.person().getPassword());
			}
		}
		for (PersonItem o : people) {
			o.setPassword(Crypto.encrypt(o.getPassword(), Config.token().getKey()));
		}
	}

	private void scanPerson(PersonSheetConfigurator configurator, Sheet sheet, List<PersonItem> people)
			throws Exception {
		if (null == configurator.getNameColumn()) {
			throw new NameColumnEmptyException();
		}
		if (null == configurator.getMobileColumn()) {
			throw new MobileColumnEmptyException();
		}
		for (int i = configurator.getFirstRow(); i <= configurator.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			if (null != row) {
				PersonItem personItem = new PersonItem();
				personItem.setRow(i);
				String name = configurator.getCellStringValue(row.getCell(configurator.getNameColumn()));
				name = StringUtils.trimToEmpty(name);
				String mobile = configurator.getCellStringValue(row.getCell(configurator.getMobileColumn()));
				mobile = StringUtils.trimToEmpty(mobile);
				GenderType genderType = GenderType.d;
				if (null != configurator.getGenderTypeColumn()) {
					String gender = configurator.getCellStringValue(row.getCell(configurator.getGenderTypeColumn()));
					gender = StringUtils.trimToEmpty(gender);
					if (genderTypeMaleItems.contains(gender)) {
						genderType = GenderType.m;
					}
					if (genderTypeFemaleItems.contains(gender)) {
						genderType = GenderType.f;
					}
				}
				personItem.setName(name);
				personItem.setGenderType(genderType);
				personItem.setMobile(mobile);
				if (null != configurator.getEmployeeColumn()) {
					String employee = configurator.getCellStringValue(row.getCell(configurator.getEmployeeColumn()));
					employee = StringUtils.trimToEmpty(employee);
					personItem.setEmployee(employee);
				}
				if (null != configurator.getUniqueColumn()) {
					String unique = configurator.getCellStringValue(row.getCell(configurator.getUniqueColumn()));
					unique = StringUtils.trimToEmpty(unique);
					personItem.setUnique(unique);
				}
				if (null != configurator.getMailColumn()) {
					String mail = configurator.getCellStringValue(row.getCell(configurator.getMailColumn()));
					mail = StringUtils.trimToEmpty(mail);
					personItem.setMail(mail);
				}
				if (!configurator.getAttributes().isEmpty()) {
					for (Entry<String, Integer> en : configurator.getAttributes().entrySet()) {
						String value = configurator.getCellStringValue(row.getCell(en.getValue()));
						value = StringUtils.trimToEmpty(value);
						personItem.getAttributes().put(en.getKey(), value);
					}
				}
				people.add(personItem);
			}
		}
	}

	private void persist(XSSFWorkbook workbook, PersonSheetConfigurator configurator, List<PersonItem> people)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Person p = null;
			boolean validate = true;
			for (PersonItem o : people) {
				if (StringUtils.isEmpty(o.getName())) {
					this.setMemo(workbook, configurator, o, "姓名不能为空.");
					validate = false;
					continue;
				}
				if (StringUtils.isEmpty(o.getMobile())) {
					this.setMemo(workbook, configurator, o, "手机号不能为空.");
					validate = false;
					continue;
				}
				if (!StringTools.isMobile(o.getMobile())) {
					this.setMemo(workbook, configurator, o, "手机号格式错误.");
					validate = false;
					continue;
				}
				if (StringUtils.isNotEmpty(o.getMail())) {
					if (!StringTools.isMail(o.getMail())) {
						this.setMemo(workbook, configurator, o, "邮件地址格式错误.");
						validate = false;
						continue;
					}
				}
			}
			if (validate) {
				List<String> cannotDuplicateList = new ArrayList<>();
				for (PersonItem o : people) {
					if (cannotDuplicateList.contains(o.getName())) {
						this.setMemo(workbook, configurator, o, "姓名冲突.");
						validate = false;
						continue;
					} else {
						cannotDuplicateList.add(o.getName());
					}
					if (cannotDuplicateList.contains(o.getMobile())) {
						this.setMemo(workbook, configurator, o, "手机号冲突.");
						validate = false;
						continue;
					} else {
						cannotDuplicateList.add(o.getMobile());
					}
					if (StringUtils.isNotEmpty(o.getMail())) {
						if (cannotDuplicateList.contains(o.getMail())) {
							this.setMemo(workbook, configurator, o, "邮件地址冲突.");
							validate = false;
							continue;
						} else {
							cannotDuplicateList.add(o.getMail());
						}
					}
					if (StringUtils.isNotEmpty(o.getEmployee())) {
						if (cannotDuplicateList.contains(o.getEmployee())) {
							this.setMemo(workbook, configurator, o, "员工编号冲突.");
							validate = false;
							continue;
						} else {
							cannotDuplicateList.add(o.getEmployee());
						}
					}
					if (StringUtils.isNotEmpty(o.getUnique())) {
						if (cannotDuplicateList.contains(o.getUnique())) {
							this.setMemo(workbook, configurator, o, "唯一编码冲突.");
							validate = false;
							continue;
						} else {
							cannotDuplicateList.add(o.getUnique());
						}
					}
				}
			}
			if (validate) {
				for (PersonItem o : people) {
					p = emc.flag(o.getName(), Person.class, ExceptionWhen.none, false, Person.FLAGS);
					if (null != p) {
						this.setMemo(workbook, configurator, o,
								"姓名: " + o.getName() + " 与已经存在用户: " + p.getName() + " 冲突.");
						validate = false;
						continue;
					}
					p = emc.flag(o.getMobile(), Person.class, ExceptionWhen.none, false, Person.FLAGS);
					if (null != p) {
						this.setMemo(workbook, configurator, o,
								"手机号: " + o.getMobile() + " 与已经存在用户: " + p.getName() + " 冲突.");
						continue;
					}
					if (StringUtils.isNotEmpty(o.getUnique())) {
						p = emc.flag(o.getUnique(), Person.class, ExceptionWhen.none, false, Person.FLAGS);
						if (null != p) {
							this.setMemo(workbook, configurator, o,
									"唯一编码: " + o.getUnique() + " 与已经存在用户: " + p.getName() + " 冲突.");
							validate = false;
							continue;
						}
					}
					if (StringUtils.isNotEmpty(o.getEmployee())) {
						p = emc.flag(o.getEmployee(), Person.class, ExceptionWhen.none, false, Person.FLAGS);
						if (null != p) {
							this.setMemo(workbook, configurator, o,
									"员工编号: " + o.getEmployee() + " 与已经存在用户: " + p.getName() + " 冲突.");
							validate = false;
							continue;
						}
					}
					if (StringUtils.isNotEmpty(o.getMail())) {
						if (!StringTools.isMail(o.getMail())) {
							this.setMemo(workbook, configurator, o, "邮件地址格式错误.");
							validate = false;
							continue;
						}
						p = emc.flag(o.getMail(), Person.class, ExceptionWhen.none, false, Person.FLAGS);
						if (null != p) {
							this.setMemo(workbook, configurator, o,
									"邮件地址: " + o.getMail() + " 与已经存在用户: " + p.getName() + " 冲突.");
							validate = false;
							continue;
						}
					}
					this.setMemo(workbook, configurator, o, "校验成功");
				}
			}
			if (validate) {
				emc.beginTransaction(Person.class);
				emc.beginTransaction(PersonAttribute.class);
				for (PersonItem o : people) {
					Person person = new Person();
					o.copyTo(person);
					emc.persist(person, CheckPersistType.all);
					for (Entry<String, String> en : o.getAttributes().entrySet()) {
						if (StringUtils.isNotEmpty(en.getValue())) {
							PersonAttribute personAttribute = new PersonAttribute();
							personAttribute.setName(en.getKey());
							personAttribute.setAttributeList(new ArrayList<String>());
							personAttribute.getAttributeList().add(en.getValue());
							personAttribute.setPerson(person.getId());
							emc.persist(personAttribute);
						}
					}
				}
				emc.commit();
			}
		}
	}

	private void setMemo(XSSFWorkbook workbook, PersonSheetConfigurator configurator, PersonItem personItem,
			String memo) {
		Sheet sheet = workbook.getSheetAt(configurator.getSheetIndex());
		Row row = sheet.getRow(personItem.getRow());
		Cell cell = CellUtil.getCell(row, configurator.getMemoColumn());
		cell.setCellValue(memo);
	}

}