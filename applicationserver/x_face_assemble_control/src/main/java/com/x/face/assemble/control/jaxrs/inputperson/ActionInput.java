package com.x.face.assemble.control.jaxrs.inputperson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;
import com.x.face.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Role;

import net.sf.ehcache.Element;

class ActionInput extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionInput.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				InputStream is = new ByteArrayInputStream(bytes);
				XSSFWorkbook workbook = new XSSFWorkbook(is);
				ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			this.scan(business, workbook);
			String name = "person_result_" + DateTools.formatDate(new Date()) + ".xls";
			workbook.write(os);
			CacheInputResult cacheInputResult = new CacheInputResult();
			cacheInputResult.setName(name);
			cacheInputResult.setBytes(os.toByteArray());
			String flag = StringTools.uniqueToken();
			cache.put(new Element(flag, cacheInputResult));
			ApplicationCache.notify(Person.class);
			ApplicationCache.notify(Group.class);
			ApplicationCache.notify(Role.class);
			ApplicationCache.notify(Identity.class);
			ApplicationCache.notify(PersonAttribute.class);
			Wo wo = new Wo();
			wo.setFlag(flag);
			result.setData(wo);
			return result;
		}
	}

	private void scan(Business business, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheetAt(0);
		List<PersonItem> people = new ArrayList<>();
		PersonSheetConfigurator configurator = new PersonSheetConfigurator(workbook, sheet);
		this.scanPerson(configurator, sheet, people);
		this.concretePassword(people);
		this.persist(business, workbook, configurator, people);
	}

	private void concretePassword(List<PersonItem> people) throws Exception {
		Pattern pattern = Pattern.compile(com.x.base.core.project.config.Person.RegularExpression_Script);
		// Matcher matcher = pattern.matcher(Config.person().password());
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
				// o.setPassword(Config.person().password());
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
			throw new ExceptionNameColumnEmpty();
		}
		if (null == configurator.getMobileColumn()) {
			throw new ExceptionMobileColumnEmpty();
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

	private void persist(Business business, XSSFWorkbook workbook, PersonSheetConfigurator configurator,
			List<PersonItem> people) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
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
				p = emc.flag(o.getName(), Person.class);
				if (null != p) {
					this.setMemo(workbook, configurator, o, "姓名: " + o.getName() + " 与已经存在用户: " + p.getName() + " 冲突.");
					validate = false;
					continue;
				}
				p = emc.flag(o.getMobile(), Person.class);
				if (null != p) {
					this.setMemo(workbook, configurator, o,
							"手机号: " + o.getMobile() + " 与已经存在用户: " + p.getName() + " 冲突.");
					continue;
				}
				if (StringUtils.isNotEmpty(o.getUnique())) {
					p = emc.flag(o.getUnique(), Person.class);
					if (null != p) {
						this.setMemo(workbook, configurator, o,
								"唯一编码: " + o.getUnique() + " 与已经存在用户: " + p.getName() + " 冲突.");
						validate = false;
						continue;
					}
				}
				if (StringUtils.isNotEmpty(o.getEmployee())) {
					p = emc.flag(o.getEmployee(), Person.class);
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
					p = emc.flag(o.getMail(), Person.class);
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

	private void setMemo(XSSFWorkbook workbook, PersonSheetConfigurator configurator, PersonItem personItem,
			String memo) {
		Sheet sheet = workbook.getSheetAt(configurator.getSheetIndex());
		Row row = sheet.getRow(personItem.getRow());
		Cell cell = CellUtil.getCell(row, configurator.getMemoColumn());
		cell.setCellValue(memo);
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

}