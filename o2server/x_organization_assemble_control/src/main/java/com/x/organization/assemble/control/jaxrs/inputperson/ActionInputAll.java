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

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

import org.apache.commons.lang3.BooleanUtils;
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
import com.x.organization.core.entity.Identity_;
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
	
	private  boolean wholeFlag = false;
	
	private static Map<String, String> typeMap = new HashMap<String,String>();
	private static Map<String, String> dutyMap = new HashMap<String,String>();
	
	List<UnitItem> unit = new ArrayList<>();
	List<PersonItem> person = new ArrayList<>();
	List<IdentityItem> identity = new ArrayList<>();
	List<DutyItem> duty = new ArrayList<>();
	UnitSheetConfigurator configuratorUnit = null;
	PersonSheetConfigurator configuratorPerson = null;
	IdentitySheetConfigurator configuratorIdentity = null;

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
			configuratorUnit = new UnitSheetConfigurator(workbook, sheet);
			unit = this.scanUnitList(configuratorUnit, sheet);
			wholeFlag = this.checkUnit(business, workbook, configuratorUnit, unit); 
			if(wholeFlag){
				//this.persistUnit(workbook, configurator, unit);
				this.scanPerson(business, workbook);
			}
	}
	
	private void scanPerson(Business business, XSSFWorkbook workbook) throws Exception {
	//导入人员信息	
		Sheet sheet = workbook.getSheetAt(3);
		configuratorPerson = new PersonSheetConfigurator(workbook, sheet);
		person = this.scanPersonList(configuratorPerson, sheet);
		wholeFlag = this.checkPerson(business, workbook, configuratorPerson, person); 
		if(wholeFlag){
			this.scanIdentity(business, workbook,person,unit);
		}
	}
	
	private void scanIdentity(Business business, XSSFWorkbook workbook ,List<PersonItem> persons,List<UnitItem> units) throws Exception {
		//导入身份信息	
			Sheet sheet = workbook.getSheetAt(4);
			configuratorIdentity = new IdentitySheetConfigurator(workbook, sheet);
			//校验导入的职务信息
			this.scanDuty(business,workbook);
			if(!dutyFlag){
				wholeFlag = this.checkIdentity(business, workbook, configuratorIdentity, sheet,persons,units); 
				if(wholeFlag){
						//保存组织，人员
						this.persistUnit(workbook, configuratorUnit, unit);
						
						identity = this.scanIdentityList(business,configuratorIdentity, sheet);
						duty = this.scanDutyList(business,configuratorIdentity, sheet);
													
				}
			}
			
			
	}
	
	private void scanDuty(Business business, XSSFWorkbook workbook) throws Exception {
		//导入职务信息	
			Sheet sheet = workbook.getSheetAt(5);
			DutySheetConfigurator configurator = new DutySheetConfigurator(workbook, sheet);
			for (int i = configurator.getFirstRow(); i <= configurator.getLastRow(); i++) {
				Row row = sheet.getRow(i);
				if (null != row) {
					String name = configurator.getCellStringValue(row.getCell(configurator.getNameColumn()));
					String key = configurator.getCellStringValue(row.getCell(configurator.getUniqueColumn()));
					System.out.println("职务name="+name+"_职务value="+key);
					if(StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(key)){
						dutyMap.put(key, name);
						
					}else{
						dutyFlag = true;
					}
				}
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
		List<UnitItem> units = new ArrayList<>();
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
				units.add(unitItem);
			}
		}
		return units;
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
		//System.out.println(configurator.getAttributes().get(""));
		if (configurator.getAttributes().isEmpty()) {
			throw new ExceptionIdNumberColumnEmpty();
		}
		List<PersonItem> peoples = new ArrayList<>();
		for (int i = configurator.getFirstRow(); i <= configurator.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			if (null != row) {
				String name = configurator.getCellStringValue(row.getCell(configurator.getNameColumn()));
				String mobile = configurator.getCellStringValue(row.getCell(configurator.getMobileColumn()));
				//if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(mobile)) {
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
					peoples.add(personItem);
					logger.debug("scan person:{}.", personItem);
				//}
			}
		}
		return peoples;
	}
	
	private List<IdentityItem> scanIdentityList(Business business,IdentitySheetConfigurator configurator, Sheet sheet) throws Exception {

		if (null == configurator.getUniqueColumn()) {
			throw new ExceptionUniqueColumnEmpty();
		}
		if (null == configurator.getUnitCodeColumn()) {
			throw new ExceptionUnitUniqueColumnEmpty();
		}

		List<IdentityItem> identitys = new ArrayList<>();
		for (int i = configurator.getFirstRow(); i <= configurator.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			if (null != row) {
				String unique = configurator.getCellStringValue(row.getCell(configurator.getUniqueColumn()));
				String unitCode = configurator.getCellStringValue(row.getCell(configurator.getUnitCodeColumn()));
				String majorStr = configurator.getCellStringValue(row.getCell(configurator.getMajorColumn()));
				Boolean major = false;
				if(majorStr.equals("true")){
					major = BooleanUtils.toBooleanObject(majorStr);
				}
				//if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(mobile)) {
					IdentityItem identityItem = new IdentityItem();
					identityItem.setRow(i);
					identityItem.setPersonCode(unique);
					identityItem.setUnitCode(unitCode);
					identityItem.setMajor(major);
					
					EntityManagerContainer emc = business.entityManagerContainer();
					Person person = null;
					person = emc.flag(unique, Person.class);
					if(person != null){
						identityItem.setName(StringUtils.trimToEmpty(person.getName()));
						identityItem.setPerson(StringUtils.trimToEmpty(person.getId()));					
					}
					
					Unit u = null;
					u = emc.flag(unitCode, Unit.class);
					if(u != null){
						identityItem.setUnit(u.getId());
						identityItem.setUnitLevel(u.getLevel());
						identityItem.setUnitLevelName(u.getLevelName());
						identityItem.setUnitName(u.getName());					
					}
					
					identitys.add(identityItem);
					logger.debug("scan identity:{}.", identityItem);
				//}
			}
		}
		return identitys;
	}
	
	private List<DutyItem> scanDutyList(Business business,IdentitySheetConfigurator configurator, Sheet sheet) throws Exception {
		if (null == configurator.getDutyCodeColumn()) {
			throw new ExceptionDutyCodeColumnEmpty();
		}
		List<Identity> identitys = new ArrayList<>();
		List<DutyItem> dutys = new ArrayList<>();
		for (int i = configurator.getFirstRow(); i <= configurator.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			if (null != row) {
				String dutyCode = configurator.getCellStringValue(row.getCell(configurator.getDutyCodeColumn()));
				String unitCode = configurator.getCellStringValue(row.getCell(configurator.getUnitCodeColumn()));
				String personCode = configurator.getCellStringValue(row.getCell(configurator.getUniqueColumn()));
				
				if(StringUtils.isNotEmpty(dutyCode)){
					DutyItem dutyItem = new DutyItem();
					dutyItem.setRow(i);
					dutyItem.setUnique(dutyCode);
					dutyItem.setName(dutyMap.get(dutyCode));
					EntityManagerContainer emc = business.entityManagerContainer();
					
					Unit u = null;
					u = emc.flag(unitCode, Unit.class);
					if(u != null){
						dutyItem.setUnit(u.getId());
					}
					
					Person person = null;
					person = emc.flag(personCode, Person.class);
					if(person != null){
						EntityManager em = business.entityManagerContainer().get(Identity.class);
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
						Root<Identity> root = cq.from(Identity.class);
						Predicate p = cb.equal(root.get(Identity_.person), person.getId());		
						identitys = em.createQuery(cq.select(root).where(p)).getResultList();
					}
					if(ListTools.isNotEmpty(identitys)){
						for (Identity identity : identitys) {
							if(unitCode.equals(identity.getUnit())){
								List<String> didylist = new ArrayList<>();
								didylist.add(identity.getDistinguishedName());
								dutyItem.setIdentityList(didylist);
							}
						}
					}
					
					logger.debug("scan duty:{}.", dutyItem);
					dutys.add(dutyItem);
				}
				
					
				
			}
		}
		return dutys;
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
		//校验导入的人员
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
			if(o.getAttributes().isEmpty()|| StringUtils.isEmpty(o.getAttributes().get("idNumber"))){
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
	
	private boolean checkIdentity(Business business, XSSFWorkbook workbook, IdentitySheetConfigurator configurator,
			 Sheet sheet ,List<PersonItem> persons,List<UnitItem> units) throws Exception {
		//校验导入的身份
		if (null == configurator.getUniqueColumn()) {
			throw new ExceptionUniqueColumnEmpty();
		}
		if (null == configurator.getUnitCodeColumn()) {
			throw new ExceptionUnitUniqueColumnEmpty();
		}
		List<IdentityItem> identitys = new ArrayList<>();	
		EntityManagerContainer emc = business.entityManagerContainer();
		boolean validate = true;
		for (int i = configurator.getFirstRow(); i <= configurator.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			if (null != row) {
				String unique = configurator.getCellStringValue(row.getCell(configurator.getUniqueColumn()));
				String unitCode = configurator.getCellStringValue(row.getCell(configurator.getUnitCodeColumn()));
				String dutyCode = configurator.getCellStringValue(row.getCell(configurator.getDutyCodeColumn()));
				System.out.println("正在校验人员 :{}."+ unique);
				boolean personcheck = false;
				boolean unitcheck = false;
				IdentityItem identityItem = new IdentityItem();
				identityItem.setRow(i);
				identitys.add(identityItem);
				if (StringUtils.isEmpty(unique)) {
					this.setIdentityMemo(workbook, configurator, identityItem, "人员编号不能为空.");
					validate = false;
					continue;
				}
				if (StringUtils.isEmpty(unitCode)) {
					this.setIdentityMemo(workbook, configurator, identityItem, "组织编号不能为空.");
					validate = false;
					continue;
				}
				if (StringUtils.isNotEmpty(dutyCode)) {
					String dutyName = dutyMap.get(dutyCode);
					if (StringUtils.isEmpty(dutyName)) {
						this.setIdentityMemo(workbook, configurator, identityItem, "系统没有对应的职务.");
						validate = false;
						continue;
					}
					
				}
				
				Person person = null;
				person = emc.flag(unique, Person.class);
				if(person != null){
					personcheck = true;
				}else{
					for (PersonItem personItem : persons) {
						if (StringUtils.isNotEmpty(personItem.getUnique()) && StringUtils.equals(personItem.getUnique(), unique)) {
							personcheck = true;
						}
					}
				}
				
				Unit unit = null;
				unit = emc.flag(unitCode, Unit.class);
				if(unit != null){
					unitcheck = true;
				}else{
					for (UnitItem unitItem : units) {
						if (StringUtils.isNotEmpty(unitItem.getUnique()) && StringUtils.equals(unitItem.getUnique(), unitCode)) {
							unitcheck = true;
						}
					}
				}
				
				if (!personcheck) {
					this.setIdentityMemo(workbook, configurator, identityItem, "系统不存在该人员.");
					validate = false;
					continue;
				}
				if (!unitcheck) {
					this.setIdentityMemo(workbook, configurator, identityItem, "系统不存在该组织.");
					validate = false;
					continue;
				}
				
			}
		}
		
		if (validate) {
			for (IdentityItem o : identitys) {
				this.setIdentityMemo(workbook, configurator, o, "校验通过.");
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
	private void setIdentityMemo(XSSFWorkbook workbook, IdentitySheetConfigurator configurator, IdentityItem identityItem,
			String memo) {
		Sheet sheet = workbook.getSheetAt(configurator.getSheetIndex());
		Row row = sheet.getRow(identityItem.getRow());
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