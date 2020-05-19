package com.x.organization.assemble.control.jaxrs.inputperson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
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
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.ThisApplication;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;

import net.sf.ehcache.Element;

class ActionInputAll extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionInputAll.class);
	
	private  boolean dutyFlag = false;
	private  boolean unitTypeFlag = false;
	private  boolean unitFlag = false;
	private  boolean personFlag = false;
	private  boolean groupFlag = false;
	
	private  boolean wholeFlag = true;
	
	private static Map<String, String> typeMap = new HashMap<String,String>();

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
			cache.put(new Element(flag, cacheInputResult));
			ApplicationCache.notify(Person.class);
			ApplicationCache.notify(Group.class);
			ApplicationCache.notify(Role.class);
			ApplicationCache.notify(Identity.class);
			ApplicationCache.notify(PersonAttribute.class);
			ApplicationCache.notify(Unit.class);
			ApplicationCache.notify(UnitDuty.class);
			Wo wo = new Wo();
			wo.setFlag(flag);
			result.setData(wo);
			return result;
		}
	}

	private void scan(Business business, XSSFWorkbook workbook) throws Exception {
	//导入组织级别
		Sheet sheet = workbook.getSheetAt(1);
		System.out.println("sheet="+sheet.getSheetName());
		UnitTypeSheetConfigurator configurator = new UnitTypeSheetConfigurator(workbook, sheet);
		if (null == configurator.getTypeCodeColumn()) {
			throw new ExceptionTypeCodeColumnEmpty();
		}
		if (null == configurator.getTypeNameColumn()) {
			throw new ExceptionTypeNameColumnEmpty();
		}
	
		for (int i = configurator.getFirstRow(); i <= configurator.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			if (null != row) {
				String name = configurator.getCellStringValue(row.getCell(configurator.getTypeCodeColumn()));
				String value = configurator.getCellStringValue(row.getCell(configurator.getTypeNameColumn()));
				System.out.println("name="+name+"_value="+value);
				if(StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)){
					typeMap.put(name, value);
					
				}else{
					unitTypeFlag = true;
				}
			}
		}
		if(!unitTypeFlag){
			this.scanUnit(business,workbook);
		}
	}
	
	private void scanUnit(Business business, XSSFWorkbook workbook) throws Exception {
	//导入组织信息
			Sheet sheet = workbook.getSheetAt(2);
			UnitSheetConfigurator configurator = new UnitSheetConfigurator(workbook, sheet);
			List<UnitItem> unit = this.scanUnitList(configurator, sheet);
			wholeFlag = this.checkUnit(business, workbook, configurator, unit); 
			if(wholeFlag){
				//this.persistUnit(workbook, configurator, unit);
				this.scanPerson(business, workbook);
			}
	}
	
	private void scanPerson(Business business, XSSFWorkbook workbook) throws Exception {
	//导入人员信息	
		Sheet sheet = workbook.getSheetAt(3);
		PersonSheetConfigurator configurator = new PersonSheetConfigurator(workbook, sheet);
		List<PersonItem> person = this.scanPersonList(configurator, sheet);
		wholeFlag = this.checkPerson(business, workbook, configurator, person); 
		if(wholeFlag){
			
		}
	}

	private List<UnitItem> scanUnitList(UnitSheetConfigurator configurator, Sheet sheet) throws Exception {
		if (null == configurator.getNameColumn()) {
			throw new ExceptionUnitNameColumnEmpty();
		}
		if (null == configurator.getUniqueColumn()) {
			throw new ExceptionUnitUniqueColumnEmpty();
		}
		if (null == configurator.getUnitTypeColumn()) {
			throw new ExceptionTypeCodeColumnEmpty();
		}
		List<UnitItem> unit = new ArrayList<>();
		for (int i = configurator.getFirstRow(); i <= configurator.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			if (null != row) {
				String name = configurator.getCellStringValue(row.getCell(configurator.getNameColumn()));
				UnitItem unitItem = new UnitItem();
				//if (StringUtils.isNotEmpty(name)) {
					unitItem.setRow(i);
					name = StringUtils.trimToEmpty(name);
					unitItem.setName(name);
					if (null != configurator.getShortNameColumn()) {
						String shortName = configurator
								.getCellStringValue(row.getCell(configurator.getShortNameColumn()));
						shortName = StringUtils.trimToEmpty(shortName);
						unitItem.setShortName(shortName);
					}
					if (null != configurator.getUniqueColumn()) {
						String unique = configurator.getCellStringValue(row.getCell(configurator.getUniqueColumn()));
						unique = StringUtils.trimToEmpty(unique);
						unitItem.setUnique(unique);
					}
					if (null != configurator.getUnitTypeColumn()) {
						String typeList = configurator.getCellStringValue(row.getCell(configurator.getUnitTypeColumn()));
						typeList = StringUtils.trimToEmpty(typeList);
						typeList = typeMap.get(typeList);
						System.out.println("typeListx="+typeList);
						List<String> typeListStr = new ArrayList<>();
						typeListStr.add(typeList);
						unitItem.setTypeList(typeListStr);
					}
					if (null != configurator.getSuperiorColumn()) {
						String superior = configurator.getCellStringValue(row.getCell(configurator.getSuperiorColumn()));
						superior = StringUtils.trimToEmpty(superior);
						unitItem.setSuperior(superior);
					}
					if (null != configurator.getOrderNumberColumn()) {
						String orderNumber = configurator.getCellStringValue(row.getCell(configurator.getOrderNumberColumn()));
						orderNumber = StringUtils.trimToEmpty(orderNumber);
						Integer order = null;
						if(orderNumber!=null){
							order = Integer.valueOf(orderNumber);
						}
						unitItem.setOrderNumber(order);
					}
					if (null != configurator.getDescriptionColumn()) {
						String description = configurator.getCellStringValue(row.getCell(configurator.getDescriptionColumn()));
						description = StringUtils.trimToEmpty(description);
						unitItem.setDescription(description);
					}
					
					if (!configurator.getAttributes().isEmpty()) {
						for (Entry<String, Integer> en : configurator.getAttributes().entrySet()) {
							String value = configurator.getCellStringValue(row.getCell(en.getValue()));
							value = StringUtils.trimToEmpty(value);
							unitItem.getAttributes().put(en.getKey(), value);
						}
					}
					//unit.add(unitItem);
					logger.debug("scan unit:{}.", unitItem);
				//}
				unit.add(unitItem);
			}
		}
		return unit;
	}
	
	private List<PersonItem> scanPersonList(PersonSheetConfigurator configurator, Sheet sheet) throws Exception {
		if (null == configurator.getNameColumn()) {
			throw new ExceptionNameColumnEmpty();
		}
		if (null == configurator.getUniqueColumn()) {
			throw new ExceptionUniqueColumnEmpty();
		}
		if (null == configurator.getEmployeeColumn()) {
			throw new ExceptionEmployeeColumnEmpty();
		}
		if (null == configurator.getMobileColumn()) {
			throw new ExceptionMobileColumnEmpty();
		}
		if (configurator.getAttributes().isEmpty()) {
			throw new ExceptionIdNumberColumnEmpty();
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
					logger.debug("scan person:{}.", personItem);
				}
			}
		}
		return people;
	}

	private boolean checkUnit(Business business, XSSFWorkbook workbook, UnitSheetConfigurator configurator,
			List<UnitItem> unit) throws Exception {
		//校验导入的组织
		EntityManagerContainer emc = business.entityManagerContainer();
		boolean validate = true;
		for (UnitItem o : unit) {
			System.out.println("正在校验用户:{}."+ o.getName());
			if (StringUtils.isEmpty(o.getName())) {
				this.setUnitMemo(workbook, configurator, o, "组织名称不能为空.");
				validate = false;
				continue;
			}
			if (StringUtils.isEmpty(o.getUnique())) {
				this.setUnitMemo(workbook, configurator, o, "组织编号不能为空.");
				validate = false;
				continue;
			}
			if (ListTools.isEmpty(o.getTypeList())) {
				this.setUnitMemo(workbook, configurator, o, "组织级别编号不能为空.");
				validate = false;
				continue;
			}
		}
		if (validate) {
			for (UnitItem o : unit) {
				for (UnitItem item : unit) {
					if (o != item) {
						if (StringUtils.isNotEmpty(o.getUnique()) && StringUtils.equals(o.getUnique(), item.getUnique())) {
							this.setUnitMemo(workbook, configurator, o, "唯一编码冲突,本次导入中不唯一.");
							validate = false;
							continue;
						}
					}
				}
				
				Unit p = null;
				p = emc.flag(o.getUnique(), Unit.class);
				if (null != p) {
					this.setUnitMemo(workbook, configurator, o, "组织编号: " + o.getUnique() + " 与已经存在组织: " + p.getName() + " 冲突.");
					validate = false;
					continue;
				}
				this.setUnitMemo(workbook, configurator, o, "校验通过.");
			}
		}
		return validate;
	}
	
	private boolean checkPerson(Business business, XSSFWorkbook workbook, PersonSheetConfigurator configurator,
			List<PersonItem> person) throws Exception {
		//校验导入的组织
		EntityManagerContainer emc = business.entityManagerContainer();
		boolean validate = true;
		for (PersonItem o : person) {
			System.out.println("正在校验用户:{}."+ o.getName());
			if (StringUtils.isEmpty(o.getName())) {
				this.setPersonMemo(workbook, configurator, o, "人员姓名不能为空.");
				validate = false;
				continue;
			}
			if (StringUtils.isEmpty(o.getUnique())) {
				this.setPersonMemo(workbook, configurator, o, "人员编号不能为空.");
				validate = false;
				continue;
			}
			if (StringUtils.isEmpty(o.getEmployee())) {
				this.setPersonMemo(workbook, configurator, o, "登录账号不能为空.");
				validate = false;
				continue;
			}
			if (StringUtils.isEmpty(o.getMobile())) {
				this.setPersonMemo(workbook, configurator, o, "手机号码不能为空.");
				validate = false;
				continue;
			}
			if(o.getAttributes().isEmpty()){
				this.setPersonMemo(workbook, configurator, o, "身份证号不能为空.");
				validate = false;
				continue;
			}
		}
		if (validate) {
			for (PersonItem o : person) {
				for (PersonItem item : person) {
					if (o != item) {
						if (StringUtils.isNotEmpty(o.getUnique()) && StringUtils.equals(o.getUnique(), item.getUnique())) {
							this.setPersonMemo(workbook, configurator, o, "唯一编码冲突,本次导入中不唯一.");
							validate = false;
							continue;
						}
					}
				}
				
				Person p = null;
				p = emc.flag(o.getUnique(), Person.class);
				if (null != p) {
					this.setPersonMemo(workbook, configurator, o, "人员编号: " + o.getUnique() + " 与已经存在人员: " + p.getName() + " 冲突.");
					validate = false;
					continue;
				}
				this.setPersonMemo(workbook, configurator, o, "校验通过.");
			}
		}
		return validate;
	}
	
	private void persistUnit(XSSFWorkbook workbook, UnitSheetConfigurator configurator, List<UnitItem> unit) throws Exception {
		for (List<UnitItem> list : ListTools.batch(unit, 200)) {
			for (UnitItem o : list) {
				logger.debug("正在保存组织:{}.", o.getName());
				Unit unitObject = new Unit();
				o.copyTo(unitObject);
				
				String resp = this.saveUnit("unit", unitObject);
				System.out.println("respMass="+resp);
				if("".equals(resp)){
					this.setUnitMemo(workbook, configurator, o, "已导入.");
				}else{
					this.setUnitMemo(workbook, configurator, o, resp);
				}
				
			}
		}
	}

	private void setUnitMemo(XSSFWorkbook workbook, UnitSheetConfigurator configurator, UnitItem unitItem,
			String memo) {
		Sheet sheet = workbook.getSheetAt(configurator.getSheetIndex());
		Row row = sheet.getRow(unitItem.getRow());
		Cell cell = CellUtil.getCell(row, configurator.getMemoColumn());
		cell.setCellValue(memo);
	}
	private void setPersonMemo(XSSFWorkbook workbook, PersonSheetConfigurator configurator, PersonItem personItem,
			String memo) {
		Sheet sheet = workbook.getSheetAt(configurator.getSheetIndex());
		Row row = sheet.getRow(personItem.getRow());
		Cell cell = CellUtil.getCell(row, configurator.getMemoColumn());
		cell.setCellValue(memo);
	}
	
	private String saveUnit(String path ,Unit unit) throws Exception{
		ActionResponse resp =  ThisApplication.context().applications()
				.postQuery(x_organization_assemble_control.class, path, unit);
		return resp.getMessage();
	}

	
	private void concretePassword(List<PersonItem> people) throws Exception {
		Pattern pattern = Pattern.compile(com.x.base.core.project.config.Person.REGULAREXPRESSION_SCRIPT);
		Matcher matcher = pattern.matcher(Config.person().getPassword());
		if (matcher.matches()) {
			String eval = ScriptFactory.functionalization(StringEscapeUtils.unescapeJson(matcher.group(1)));
			ScriptContext scriptContext = new SimpleScriptContext();
			Bindings bindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
			for (PersonItem o : people) {
				bindings.put("person", o);
				String pass = ScriptFactory.scriptEngine.eval(eval, scriptContext).toString();
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