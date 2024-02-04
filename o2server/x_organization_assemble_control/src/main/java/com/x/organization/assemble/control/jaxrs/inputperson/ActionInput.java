package com.x.organization.assemble.control.jaxrs.inputperson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.graalvm.polyglot.Source;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.GraalvmScriptingFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Role;

class ActionInput extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionInput.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				InputStream is = new ByteArrayInputStream(bytes);
				XSSFWorkbook workbook = new XSSFWorkbook(is);
				ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			this.scan(business, workbook);
			String name = "person_" + DateTools.formatDate(new Date()) + ".xlsx";
			workbook.write(os);
			CacheInputResult cacheInputResult = new CacheInputResult();
			cacheInputResult.setName(name);
			cacheInputResult.setBytes(os.toByteArray());
			String flag = StringTools.uniqueToken();
			CacheKey cacheKey = new CacheKey(flag);
			CacheManager.put(this.cache, cacheKey, cacheInputResult);
			CacheManager.notify(Person.class);
			CacheManager.notify(Group.class);
			CacheManager.notify(Role.class);
			CacheManager.notify(Identity.class);
			CacheManager.notify(PersonAttribute.class);
			Wo wo = new Wo();
			wo.setFlag(flag);
			result.setData(wo);
			return result;
		}
	}

	private void scan(Business business, XSSFWorkbook workbook) throws Exception {
		Sheet sheet = workbook.getSheetAt(0);
		PersonSheetConfigurator configurator = new PersonSheetConfigurator(workbook, sheet);
		List<PersonItem> people = this.scanPerson(configurator, sheet);
		this.concretePassword(people);
		this.persist(business, workbook, configurator, people);
	}

	private void concretePassword(List<PersonItem> people) throws Exception {
		Pattern pattern = Pattern.compile(com.x.base.core.project.config.Person.REGULAREXPRESSION_SCRIPT);
		Matcher matcher = pattern.matcher(Config.person().getPassword());
		if (matcher.matches()) {
			Source source = GraalvmScriptingFactory.functionalization(StringEscapeUtils.unescapeJson(matcher.group(1)));
			GraalvmScriptingFactory.Bindings bindings = new GraalvmScriptingFactory.Bindings();
			for (PersonItem o : people) {
				bindings.putMember(GraalvmScriptingFactory.BINDING_NAME_SERVICE_PERSON, o);
				Optional<String> opt = GraalvmScriptingFactory.evalAsString(source, bindings);
				if (opt.isPresent()) {
					o.setPassword(opt.get());
				}
			}
		} else {
			for (PersonItem o : people) {
				o.setPassword(Config.person().getPassword());
			}
		}
		for (PersonItem o : people) {
			o.setPassword(Crypto.encrypt(o.getPassword(), Config.token().getKey(), Config.person().getEncryptType()));
		}
	}

	private List<PersonItem> scanPerson(PersonSheetConfigurator configurator, Sheet sheet)
			throws ExceptionNameColumnEmpty, ExceptionMobileColumnEmpty {
		if (null == configurator.getNameColumn()) {
			throw new ExceptionNameColumnEmpty();
		}
		if (null == configurator.getMobileColumn()) {
			throw new ExceptionMobileColumnEmpty();
		}
		List<PersonItem> people = new ArrayList<>();
		for (int i = configurator.getFirstRow(); i <= configurator.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			if (null != row) {
				String name = configurator.getCellStringValue(row.getCell(configurator.getNameColumn()));
				String mobile = configurator.getCellStringValue(row.getCell(configurator.getMobileColumn()));
				if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(mobile)) {
					PersonItem personItem = new PersonItem();
					personItem.setRow(i);
					name = StringUtils.trimToEmpty(name);
					mobile = StringUtils.trimToEmpty(mobile);
					GenderType genderType = GenderType.d;
					if (null != configurator.getGenderTypeColumn()) {
						String gender = configurator
								.getCellStringValue(row.getCell(configurator.getGenderTypeColumn()));
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
						String employee = configurator
								.getCellStringValue(row.getCell(configurator.getEmployeeColumn()));
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
					LOGGER.debug("scan person:{}.", personItem);
				}
			}
		}
		return people;
	}

	private void persist(Business business, XSSFWorkbook workbook, PersonSheetConfigurator configurator,
			List<PersonItem> people) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		Person p = null;
		boolean validate = true;
		for (PersonItem o : people) {
			LOGGER.debug("正在校验用户:{}.", o.getName());
			if (StringUtils.isEmpty(o.getName())) {
				this.setMemo(workbook, configurator, o, "姓名不能为空.");
				validate = false;
				continue;
			}
			if (!Config.person().isMobile(o.getMobile())) {
				this.setMemo(workbook, configurator, o, "手机号为空或者格式错误.");
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
			for (PersonItem o : people) {
				for (PersonItem item : people) {
					if (o != item) {
						if (StringUtils.equals(o.getMobile(), item.getMobile())) {
							this.setMemo(workbook, configurator, o, "手机号冲突,本次导入中不唯一.");
							validate = false;
							continue;
						}
						if (StringUtils.isNotEmpty(o.getMail()) && StringUtils.equals(o.getMail(), item.getMail())) {
							this.setMemo(workbook, configurator, o, "邮件地址冲突,本次导入中不唯一.");
							validate = false;
							continue;
						}
						if (StringUtils.isNotEmpty(o.getEmployee())
								&& StringUtils.equals(o.getEmployee(), item.getEmployee())) {
							this.setMemo(workbook, configurator, o, "员工编号冲突,本次导入中不唯一.");
							validate = false;
							continue;
						}
						if (StringUtils.isNotEmpty(o.getUnique())
								&& StringUtils.equals(o.getUnique(), item.getUnique())) {
							this.setMemo(workbook, configurator, o, "唯一编码冲突,本次导入中不唯一.");
							validate = false;
							continue;
						}
					}
				}
			}
			if (validate) {
				for (PersonItem o : people) {
					p = emc.flag(o.getMobile(), Person.class);
					if (null != p) {
						this.setMemo(workbook, configurator, o,
								"手机号: " + o.getMobile() + " 与已经存在用户: " + p.getName() + " 冲突.");
						validate = false;
						continue;
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
					this.setMemo(workbook, configurator, o, "校验通过.");
				}
			}
			if (validate) {
				for (List<PersonItem> list : ListTools.batch(people, 200)) {
					emc.beginTransaction(Person.class);
					emc.beginTransaction(PersonAttribute.class);
					for (PersonItem o : list) {
						LOGGER.debug("正在保存用户:{}.", o.getName());
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
						this.setMemo(workbook, configurator, o, "已导入.");
					}
					emc.commit();
				}
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