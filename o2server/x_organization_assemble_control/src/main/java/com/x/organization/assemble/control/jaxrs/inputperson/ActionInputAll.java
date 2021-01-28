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
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
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

import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;

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
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.UnitAttribute;
import com.x.base.core.project.script.ScriptFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.general.core.entity.GeneralFile;
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

class ActionInputAll extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionInputAll.class);
	private static ReentrantLock lock = new ReentrantLock();
	private  boolean dutyFlag = false;
	
	private  boolean wholeFlag = false;
	
	private static Map<String, String> dutyMap = new HashMap<String,String>();
	private static Map<String, String> dutyDescriptionMap = new HashMap<String,String>();
	
	List<UnitItem> unit = new ArrayList<>();
	List<PersonItem> person = new ArrayList<>();
	List<IdentityItem> identity = new ArrayList<>();
	List<DutyItem> duty = new ArrayList<>();
	List<UnitDuty> editduty = new ArrayList<>();
	List<GroupItem> group = new ArrayList<>();
	UnitSheetConfigurator configuratorUnit = null;
	PersonSheetConfigurator configuratorPerson = null;
	IdentitySheetConfigurator configuratorIdentity = null;
	DutySheetConfigurator configuratorDuty = null;
	GroupSheetConfigurator configuratorGroup = null;

	ActionResult<Wo> execute(EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition)
			throws Exception {
		lock.lock();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				InputStream is = new ByteArrayInputStream(bytes);
				XSSFWorkbook workbook = new XSSFWorkbook(is);
				ByteArrayOutputStream os = new ByteArrayOutputStream()) { 
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			this.scanUnit(business, workbook);
			String name = "person_input_" + DateTools.formatDate(new Date()) + ".xlsx";
			workbook.write(os);
			String flag = saveAttachment(os.toByteArray(),name,effectivePerson);
			CacheManager.notify(Person.class);
			CacheManager.notify(Group.class);
			CacheManager.notify(Role.class);
			CacheManager.notify(Identity.class);
			CacheManager.notify(PersonAttribute.class);
			CacheManager.notify(Unit.class);
			CacheManager.notify(UnitDuty.class);
			Wo wo = new Wo();
			wo.setFlag(flag);
			result.setData(wo);
			return result;
		}finally {
			lock.unlock();
		}
	}

	
	
	private void scanUnit(Business business, XSSFWorkbook workbook) throws Exception {
	//导入组织信息
		System.out.println("开始导入人员组织所有数据--------start");
			Sheet sheet = workbook.getSheetAt(1);
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
		System.out.println("--------scanPerson");
		Sheet sheet = workbook.getSheetAt(2);
		configuratorPerson = new PersonSheetConfigurator(workbook, sheet);
		person = this.scanPersonList(configuratorPerson, sheet);
		System.out.println("person="+person.size());
		wholeFlag = this.checkPerson(business, workbook, configuratorPerson, person); 
		if(wholeFlag){
			this.scanIdentity(business, workbook,person,unit);
		}
	}
	
	private void scanIdentity(Business business, XSSFWorkbook workbook ,List<PersonItem> persons,List<UnitItem> units) throws Exception {
		//导入身份信息	
		System.out.println("--------scanIdentity");
			Sheet sheet = workbook.getSheetAt(3);
			configuratorIdentity = new IdentitySheetConfigurator(workbook, sheet);
			//校验导入的职务信息
			this.scanDuty(business,workbook);
			if(!dutyFlag){
				wholeFlag = this.checkIdentity(business, workbook, configuratorIdentity, sheet,persons,units); 
				if(wholeFlag){
						
						if(wholeFlag){
							//保存组织，人员
							System.out.println("开始导入组织信息--------");
							this.persistUnit(business,workbook, configuratorUnit, unit);
							System.out.println("开始导入人员信息--------");
							this.persistPerson(business,workbook, configuratorPerson, person);
							System.out.println("开始导入身份信息--------");
							identity = this.scanIdentityList(business,configuratorIdentity, sheet);
							//保存身份
							this.persistIdentity(workbook, configuratorIdentity, identity);
							System.out.println("开始导入职务信息--------");
							//保存职务
							duty = this.scanDutyList(business,identity, sheet);
							//this.persistDuty(workbook, configuratorDuty, duty);
							this.persistDuty(workbook, configuratorDuty, duty,business);
							
							//保存群组
							//校验群组
							wholeFlag = this.checkGroup(business,workbook,person,unit);
							System.out.println("开始导入群组信息--------");
							this.scanGroup(business,workbook,person,unit);
							this.persistGroup(business,workbook, configuratorGroup, group);
							System.out.println("开始导入人员组织所有数据--------end");
						}
													
				}
			}
			
			
	}
	
	private void scanDuty(Business business, XSSFWorkbook workbook) throws Exception  {
		//导入职务信息
		System.out.println("--------scanDuty");
			Sheet sheet = workbook.getSheetAt(4);
			DutySheetConfigurator configurator = new DutySheetConfigurator(workbook, sheet);
			for (int i = configurator.getFirstRow(); i <= configurator.getLastRow(); i++) {
				Row row = sheet.getRow(i);
				if (null != row) {
					String name = configurator.getCellStringValue(row.getCell(configurator.getNameColumn()));
					String key = configurator.getCellStringValue(row.getCell(configurator.getUniqueColumn()));
					String dutyDescription = configurator.getCellStringValue(row.getCell(configurator.getDescriptionColumn()));
					//System.out.println("职务name="+name+"_职务value="+key);
					if(StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(key)){
						dutyMap.put(key, name);
						
					}else{
						dutyFlag = true;
					}
					if(StringUtils.isNotEmpty(dutyDescription) && StringUtils.isNotEmpty(key)){
						dutyDescriptionMap.put(key, dutyDescription);
					}else{
						dutyDescriptionMap.put(key, "");
					}
				}
			}
	}
	private void scanGroup(Business business, XSSFWorkbook workbook ,List<PersonItem> persons,List<UnitItem> units) throws Exception {
		//导入群组信息	
			Sheet sheet = workbook.getSheetAt(5);
			configuratorGroup = new GroupSheetConfigurator(workbook, sheet);
			group = this.scanGroupList(business,configuratorGroup, sheet);
		
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
					if (null != configurator.getUniqueColumn()) {
						String unique = configurator.getCellStringValue(row.getCell(configurator.getUniqueColumn()));
						unique = StringUtils.trimToEmpty(unique);
						unitItem.setUnique(unique);
					}
					if (null != configurator.getUnitTypeColumn()) {
						String typeList = configurator.getCellStringValue(row.getCell(configurator.getUnitTypeColumn()));
						typeList = StringUtils.trimToEmpty(typeList);
						List<String> typeListStr = new ArrayList<>();
						if(StringUtils.isNotEmpty(typeList)){
							typeListStr.add(typeList);
						}
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
						if(StringUtils.isNotEmpty(orderNumber)){
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
		/*if (null == configurator.getUniqueColumn()) {
			throw new ExceptionUniqueColumnEmpty();
		}*/
		if (null == configurator.getEmployeeColumn()) {
			throw new ExceptionEmployeeColumnEmpty();
		}
		if (null == configurator.getMobileColumn()) {
			throw new ExceptionMobileColumnEmpty();
		}
		//System.out.println(configurator.getAttributes().get(""));
		/*if (configurator.getAttributes().isEmpty()) {
			throw new ExceptionIdNumberColumnEmpty();
		}*/
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
					//mobile = StringUtils.trimToEmpty(mobile);
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
					
					if(StringUtils.isNotEmpty(mobile)){
						personItem.setMobile(mobile);
					}else{
						personItem.setMobile("139"+this.getCard());
					}
					
			
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
						
						if(null == configurator.getEmployeeColumn()){
							personItem.setEmployee(unique);
						}
					}
					if (null != configurator.getOfficePhoneColumn()) {
						String officePhone = configurator.getCellStringValue(row.getCell(configurator.getOfficePhoneColumn()));
						officePhone = StringUtils.trimToEmpty(officePhone);
						personItem.setOfficePhone(officePhone);
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
				String dutyCode = configurator.getCellStringValue(row.getCell(configurator.getDutyCodeColumn()));
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
					
					if(StringUtils.isNotEmpty(dutyCode)){
						identityItem.setDutyCode(dutyCode);
					}
					
					EntityManagerContainer emc = business.entityManagerContainer();
					Person personobj = null;
					personobj = emc.flag(unique, Person.class);
					if(personobj != null){
						identityItem.setName(StringUtils.trimToEmpty(personobj.getName()));
						identityItem.setPerson(StringUtils.trimToEmpty(personobj.getId()));					
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
					
					int idcount = 0;
					for(IdentityItem idItem : identitys){
						if(unique.equals(idItem.getPersonCode()) && unitCode.equals(idItem.getUnitCode())){
							idcount = idcount+1;
						}
					}
					if(idcount>1){
						identitys.remove(identityItem);
					}
					logger.debug("scan identity:{}.", identityItem);
				//}
			}
		}
		return identitys;
	}
	
	private List<GroupItem> scanGroupList(Business business,GroupSheetConfigurator configurator, Sheet sheet) throws Exception {

		List<GroupItem> groups = new ArrayList<>();
		for (int i = configurator.getFirstRow(); i <= configurator.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			if (null != row) {
				String name = configurator.getCellStringValue(row.getCell(configurator.getNameColumn()));
				String unique = configurator.getCellStringValue(row.getCell(configurator.getUniqueColumn()));
				String personCode = configurator.getCellStringValue(row.getCell(configurator.getPersonCodeColumn()));
				String unitCode = configurator.getCellStringValue(row.getCell(configurator.getUnitCodeColumn()));
				String groupCode = configurator.getCellStringValue(row.getCell(configurator.getGroupCodeColumn()));
				String description = configurator.getCellStringValue(row.getCell(configurator.getDescriptionColumn()));
				
				//if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(mobile)) {
					GroupItem groupItem = new GroupItem();
					groupItem.setRow(i);
					groupItem.setPersonCode(personCode);
					groupItem.setUnitCode(unitCode);
					groupItem.setName(name);
					groupItem.setUnique(unique);
					
					EntityManagerContainer emc = business.entityManagerContainer();
					Person personobj = null;
					personobj = emc.flag(personCode, Person.class);
					if(personobj != null){
						List<String> personList = new ArrayList<>();
						personList.add(personobj.getId());
						groupItem.setPersonList(personList);			
					}
					
					Unit u = null;
					u = emc.flag(unitCode, Unit.class);
					
					if(u != null){
						List<String> unitList = new ArrayList<>();
						unitList.add(u.getId());
						groupItem.setUnitList(unitList);
					}
					if(StringUtils.isNotEmpty(groupCode)){
						groupItem.setGroupCode(groupCode);
						Group groupobj = emc.flag(groupCode, Group.class);
						 if(groupobj != null){
							 List<String> groupList = new ArrayList<>();
							 groupList.add(groupobj.getId());
							 groupItem.setGroupList(groupList);
						 }else{
							 
						 }
					}
					if(StringUtils.isNotEmpty(description)){
						groupItem.setDescription(description);
					}
					
					groups.add(groupItem);
					logger.debug("scan group:{}.", groupItem);
				//}
			}
		}
		return groups;
	}
	
	private List<DutyItem> scanDutyList(Business business,List<IdentityItem> identityItems, Sheet sheet) throws Exception {
		
		List<DutyItem> dutys = new ArrayList<>();
		for (int i = 0; i < identityItems.size(); i++) {
			IdentityItem identutyIt = identityItems.get(i);
			if (null != identutyIt) {
				String dutyCode = identutyIt.getDutyCode();
				String unitCode = identutyIt.getUnitCode();
				String personCode = identutyIt.getPersonCode();
				List<Identity> identitys = new ArrayList<>();
				
				if(StringUtils.isNotEmpty(dutyCode)){
					DutyItem dutyItem = new DutyItem();
					dutyItem.setRow(i+1);
					dutyItem.setName(dutyMap.get(dutyCode));
					dutyItem.setDescription(dutyDescriptionMap.get(dutyCode));
					EntityManagerContainer emc = business.entityManagerContainer();
					
					Unit u = null;
					String unitId = null;
					u = emc.flag(unitCode, Unit.class);
					if(u != null){
						dutyItem.setUnit(u.getId());
						unitId = u.getId();
						dutyItem.setUnique(dutyCode+unitId);
					}
					
					Person personObj = null;
					personObj = emc.flag(personCode, Person.class);
					if(personObj != null){
						EntityManager em = business.entityManagerContainer().get(Identity.class);
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
						Root<Identity> root = cq.from(Identity.class);
						//System.out.println("personid="+personObj.getId());
						Predicate p = cb.equal(root.get(Identity_.person), personObj.getId());	
						p = cb.and(p,cb.equal(root.get(Identity_.unit), u.getId()));
						identitys = em.createQuery(cq.select(root).where(p)).getResultList();
						
						if(personCode.equals("zhengping2")){
							System.out.println("identitys="+identitys.size());
						}
					}
					
					
					if(ListTools.isNotEmpty(identitys)){
						List<String> didylist = new ArrayList<>();
						/*for (Identity identity : identitys) {
							if(unitId.equals(identity.getUnit())){
								//System.out.println("unitCode="+unitCode);
								didylist.add(identity.getDistinguishedName());
							}
						}*/
						didylist.add(identitys.get(0).getId());
						dutyItem.setIdentityList(didylist);
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
			//System.out.println("正在校验组织:{}."+ o.getName());
			if (StringUtils.isEmpty(o.getName())) {
				this.setUnitMemo(workbook, configurator, o, "组织名称不能为空.");
				validate = false;
				continue;
			}
			if (StringUtils.isEmpty(o.getUnique())) {
				this.setUnitMemo(workbook, configurator, o, "组织唯一编码不能为空.");
				validate = false;
				continue;
			}
			if (ListTools.isEmpty(o.getTypeList())) {
				this.setUnitMemo(workbook, configurator, o, "组织级别名称不能为空.");
				validate = false;
				continue;
			}
			this.setUnitMemo(workbook, configurator, o, "校验通过.");
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
				/*if(!validate){
					continue;
				}*/
				Unit p = null;
				p = emc.flag(o.getUnique(), Unit.class);
				if (null != p) {
					this.setUnitMemo(workbook, configurator, o, "组织编号: " + o.getUnique() + " 与已经存在组织: " + p.getName() + " 冲突.");
					validate = false;
					continue;
				}
				//this.setUnitMemo(workbook, configurator, o, "校验通过.");
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
			//System.out.println("正在校验用户:{}."+ o.getName());
			this.setPersonMemo(workbook, configurator, o, "校验通过.");
			if (StringUtils.isEmpty(o.getName())) {
				this.setPersonMemo(workbook, configurator, o, "人员姓名不能为空.");
				validate = false;
				continue;
			}
			/*if (StringUtils.isEmpty(o.getUnique())) {
				this.setPersonMemo(workbook, configurator, o, "人员编号不能为空.");
				validate = false;
				continue;
			}*/
			if (StringUtils.isEmpty(o.getUnique())) {
				this.setPersonMemo(workbook, configurator, o, "员工唯一编码不能为空.");
				validate = false;
				continue;
			}
			if (StringUtils.isEmpty(o.getMobile())) {
				this.setPersonMemo(workbook, configurator, o, "手机号码不能为空.");
				validate = false;
				continue;
			}
			if(!this.checkMobile(business, o.getMobile())){
				this.setPersonMemo(workbook, configurator, o, "手机号码不符合指定规则.");
				validate = false;
				continue;
			}
			/*if(o.getAttributes().isEmpty()|| StringUtils.isEmpty(o.getAttributes().get("idNumber"))){
				this.setPersonMemo(workbook, configurator, o, "身份证号不能为空.");
				validate = false;
				continue;
			}*/
		}
		if (validate) {
			for (PersonItem o : person) {
				for (PersonItem item : person) {
					if (o != item) {
						if (StringUtils.isNotEmpty(o.getUnique()) && StringUtils.equals(o.getUnique(), item.getUnique())) {
							this.setPersonMemo(workbook, configurator, o, "员工账号冲突,本次导入中不唯一.");
							validate = false;
							continue;
						}
						if (StringUtils.isNotEmpty(o.getMobile()) && StringUtils.equals(o.getMobile(), item.getMobile())) {
							this.setPersonMemo(workbook, configurator, o, "手机号码冲突,本次导入中不唯一."); 
							validate = false;
							continue;
						}
					}
				}
				
				/*if(!validate){
					continue;
				}*/
				Person p = null;
				p = emc.flag(o.getUnique(), Person.class);
				if (null != p) {
					this.setPersonMemo(workbook, configurator, o, "员工账号: " + o.getUnique() + " 与已经存在人员: " + p.getName() + " 冲突.");
					validate = false;
					continue;
				}
				
				Person pc = null;
				pc = emc.flag(o.getMobile(), Person.class);
				if (null != pc) {
					System.out.println("手机号码: " + o.getMobile() + " 与已经存在手机号码: " + pc.getMobile() + " 冲突.");
					this.setPersonMemo(workbook, configurator, o, "手机号码: " + o.getMobile() + " 与已经存在手机号码: " + pc.getMobile() + " 冲突.");
					validate = false;
					continue;
				}
				
				//this.setPersonMemo(workbook, configurator, o, "校验通过.");
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
				//System.out.println("正在校验人员身份 :{}."+ unique);
				boolean personcheck = false;
				boolean unitcheck = false;
				IdentityItem identityItem = new IdentityItem();
				identityItem.setRow(i);
				identitys.add(identityItem);
				this.setIdentityMemo(workbook, configurator, identityItem, "校验通过.");
				if (StringUtils.isEmpty(unique)) {
					this.setIdentityMemo(workbook, configurator, identityItem, "员工唯一编码不能为空.");
					validate = false;
					continue;
				}
				if (StringUtils.isEmpty(unitCode)) {
					this.setIdentityMemo(workbook, configurator, identityItem, "组织唯一编码不能为空.");
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
				
				//if (validate) {
					//this.setIdentityMemo(workbook, configurator, identityItem, "校验通过.");
				//}
				
			}
		}
		
		/*if (validate) {
			for (IdentityItem o : identitys) {
				this.setIdentityMemo(workbook, configurator, o, "校验通过.");
			}
		}*/
		return validate;
	}
	
	private boolean checkGroup(Business business, XSSFWorkbook workbook, List<PersonItem> persons,List<UnitItem> units) throws Exception {
		//校验导入的群组
		Sheet sheet = workbook.getSheetAt(5);
		configuratorGroup = new GroupSheetConfigurator(workbook, sheet);
		GroupSheetConfigurator configurator = configuratorGroup;
		
		if (null == configurator.getNameColumn()) {
			throw new ExceptionGroupNameColumnEmpty();
		}
		if (null == configurator.getUniqueColumn()) {
			throw new ExceptionGroupCodeColumnEmpty();
		}
		
		List<GroupItem> groups = new ArrayList<>();	
		EntityManagerContainer emc = business.entityManagerContainer();
		boolean validate = true;
		for (int i = configurator.getFirstRow(); i <= configurator.getLastRow(); i++) {
			Row row = sheet.getRow(i);
			if (null != row) {
				String name = configurator.getCellStringValue(row.getCell(configurator.getNameColumn()));
				String unique = configurator.getCellStringValue(row.getCell(configurator.getUniqueColumn()));
				String personCode = configurator.getCellStringValue(row.getCell(configurator.getPersonCodeColumn()));
				String unitCode = configurator.getCellStringValue(row.getCell(configurator.getUnitCodeColumn()));
				
				//System.out.println("正在校验群组 :{}."+ name);
				boolean personcheck = false;
				boolean unitcheck = false;
				GroupItem groupItem = new GroupItem();
				groupItem.setRow(i);
				groups.add(groupItem);
				if (StringUtils.isEmpty(name)) {
					this.setGroupMemo(workbook, configurator, groupItem, "群组名称不能为空.");
					validate = false;
					continue;
				}
				if (StringUtils.isEmpty(unique)) {
					this.setGroupMemo(workbook, configurator, groupItem, "群组编号不能为空.");
					validate = false;
					continue;
				}
				
				Person person = null;
				person = emc.flag(personCode, Person.class);
				if(person != null){
					personcheck = true;
				}else{
					for (PersonItem personItem : persons) {
						if (StringUtils.isNotEmpty(personItem.getUnique()) && StringUtils.equals(personItem.getUnique(), personCode)) {
							personcheck = true;
						}
					}
				}
				
				if(StringUtils.isEmpty(unitCode)){
					unitcheck = true;
				}else{
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
				}
				
				
				if (!personcheck) {
					this.setGroupMemo(workbook, configurator, groupItem, "系统不存在该人员.");
					validate = false;
					continue;
				}
				if (!unitcheck) {
					this.setGroupMemo(workbook, configurator, groupItem, "系统不存在该组织.");
					validate = false;
					continue;
				}
				
				//if (validate) {
					this.setGroupMemo(workbook, configurator, groupItem, "校验通过.");
				//}
				
			}
		}
		
		/*if (validate) {
			for (GroupItem o : groups){
				this.setGroupMemo(workbook, configurator, o, "校验通过.");
			}
		}*/
		return validate;
	}
	
	
	private void persistUnit(Business business,XSSFWorkbook workbook, UnitSheetConfigurator configurator, List<UnitItem> unitItems) throws Exception {
		for (List<UnitItem> list : ListTools.batch(unitItems, 200)) {
			for (UnitItem o : list) {
				logger.debug("正在保存组织:{}.", o.getName());
				Unit unitObject = new Unit();
				o.copyTo(unitObject);
				
				String resp = this.saveUnit("unit", unitObject);
				
				if("".equals(resp)){
					EntityManagerContainer emc = business.entityManagerContainer();
					Unit po = emc.flag(unitObject.getUnique(), Unit.class);
					boolean validate = true;
					for (Entry<String, String> en : o.getAttributes().entrySet()) {
						if (StringUtils.isNotEmpty(en.getValue())) {
							UnitAttribute unitAttribute = new UnitAttribute();
							unitAttribute.setName(en.getKey());
							unitAttribute.setAttributeList(new ArrayList<String>());
							unitAttribute.getAttributeList().add(en.getValue());
							unitAttribute.setUnit(po.getId());
							String respAtt = saveUnitAttribute("unitattribute",unitAttribute);
							if("".equals(respAtt)){
							}else{
								System.out.println("respMass="+respAtt);
								this.setUnitMemo(workbook, configurator, o, respAtt);
							}
						}
					}
					if(validate){
						this.setUnitMemo(workbook, configurator, o, "已导入.");
					}
				}else{
					System.out.println("respMass="+resp);
					this.setUnitMemo(workbook, configurator, o, resp);
				}
				
			}
		}
	}
	private void persistPerson(Business business,XSSFWorkbook workbook, PersonSheetConfigurator configurator, List<PersonItem> personItems) throws Exception {
		//EntityManagerContainer emc = business.entityManagerContainer();
		for (List<PersonItem> list : ListTools.batch(personItems, 200)) {
			for (PersonItem o : list) {
				logger.debug("正在保存人员:{}.", o.getName());
				Person personObject = new Person();
				o.copyTo(personObject);
				
				String resp = this.savePerson("person", personObject);
				
				if("".equals(resp)){
					EntityManagerContainer emc = business.entityManagerContainer();
					Person po = emc.flag(personObject.getUnique(), Person.class);
					boolean validate = true;
					for (Entry<String, String> en : o.getAttributes().entrySet()) {
						if (StringUtils.isNotEmpty(en.getValue())) {
							PersonAttribute personAttribute = new PersonAttribute();
							personAttribute.setName(en.getKey());
							personAttribute.setAttributeList(new ArrayList<String>());
							personAttribute.getAttributeList().add(en.getValue());
							personAttribute.setPerson(po.getId());
							String respAtt = savePersonAttribute("personattribute",personAttribute);
							if("".equals(respAtt)){
							}else{
								System.out.println("respMass="+respAtt);
								this.setPersonMemo(workbook, configurator, o, respAtt);
							}
						}
					}
					if(validate){
						this.setPersonMemo(workbook, configurator, o, "已导入.");
					}
				}else{
					System.out.println("respMass="+resp);
					this.setPersonMemo(workbook, configurator, o, resp);
				}
				
			}
		}
	}
	
	private void persistIdentity(XSSFWorkbook workbook, IdentitySheetConfigurator configurator, List<IdentityItem> identityItems) throws Exception {
		for (List<IdentityItem> list : ListTools.batch(identityItems, 200)) {
			for (IdentityItem o : list) {
				logger.debug("正在保存人员:{}.", o.getName());
				Identity identityObject = new Identity();
				o.copyTo(identityObject);
				
				String resp = this.saveIdentity("identity", identityObject);
				
				if("".equals(resp)){
					this.setIdentityMemo(workbook, configurator, o, "已导入.");
				}else{
					System.out.println("respMass="+resp);
					this.setIdentityMemo(workbook, configurator, o, resp);
				}
				
			}
		}
	}
	
	private void persistDuty(XSSFWorkbook workbook, DutySheetConfigurator configurator, List<DutyItem> dutyItems,Business business) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		for (List<DutyItem> list : ListTools.batch(dutyItems, 200)) {
			for (DutyItem o : list) {
				if(StringUtils.isNotEmpty(o.getUnique()) && this.getDuty(emc,o)){
				}else{
					logger.debug("正在保存职务:{}.", o.getName());
					UnitDuty dutyObject = new UnitDuty();
					o.copyTo(dutyObject);
					String resp = this.saveDuty("unitduty", dutyObject);
					if("".equals(resp)){
					}else{
						System.out.println("respMassduty="+resp);
					}
					
				}
				
			}
		}
		
	}
	
	private void persistGroup(Business business,XSSFWorkbook workbook, GroupSheetConfigurator configurator, List<GroupItem> groupItems) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		for (List<GroupItem> list : ListTools.batch(groupItems, 200)) {
			for (GroupItem o : list) {
				Group g = emc.flag(o.getUnique(), Group.class);
				if(g != null){
					List<String> personList = g.getPersonList();
					List<String> unitList = g.getUnitList();
					List<String> groupList = g.getGroupList();
					if(ListTools.isNotEmpty(o.getPersonList())){
						personList.addAll(o.getPersonList());
					}
					if(ListTools.isNotEmpty(o.getUnitList())){
						unitList.addAll(o.getUnitList());
					}
					if(ListTools.isNotEmpty(o.getGroupList())){
						groupList.addAll(o.getGroupList());
					}
					if(StringUtils.isNotEmpty(o.getGroupCode())){
						Group gp = emc.flag(o.getGroupCode(), Group.class);
						groupList.add(gp.getId());
					}
					
					g.setPersonList(personList);
					g.setUnitList(unitList);
					g.setGroupList(groupList);
					
					String respEdit = this.editGroup("group/"+g.getId(), g);
					
					if("".equals(respEdit)){
						this.setGroupMemo(workbook, configurator, o, "已导入.");
					}else{
						System.out.println("respEditMass="+respEdit);
						this.setGroupMemo(workbook, configurator, o, respEdit);
					}
					
				}else{
					logger.debug("正在保存群组:{}.", o.getName());
					if(StringUtils.isNotEmpty(o.getGroupCode())){
						List<String> groupList = o.getGroupList();
						if(ListTools.isEmpty(groupList)){
							Group groupobj = emc.flag(o.getGroupCode(), Group.class);
							if(groupobj != null){
								List<String> glist = new ArrayList<>();
								glist.add(groupobj.getId());
								 o.setGroupList(glist);
							}
						}
					}
					
					Group groupObject = new Group();
					o.copyTo(groupObject);
					
					String resp = this.saveGroup("group", groupObject);
					
					if("".equals(resp)){
						this.setGroupMemo(workbook, configurator, o, "已导入.");
					}else{
						System.out.println("respMass="+resp);
						this.setGroupMemo(workbook, configurator, o, resp);
					}
				}
				
			}
		}
		/*for(List<Group> unitlist : ListTools.batch(group, 200)){
			for (Group uo : unitlist) {
				this.editGroup("group/"+uo.getId(),uo);
			}
		}*/
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
	
	private void setGroupMemo(XSSFWorkbook workbook, GroupSheetConfigurator configurator, GroupItem groupItem,
			String memo) {
		Sheet sheet = workbook.getSheetAt(configurator.getSheetIndex());
		Row row = sheet.getRow(groupItem.getRow());
		Cell cell = CellUtil.getCell(row, configurator.getMemoColumn());
		cell.setCellValue(memo);
	}
	
	private String saveUnit(String path ,Unit unitObj) throws Exception{
		ActionResponse resp =  ThisApplication.context().applications()
				.postQuery(x_organization_assemble_control.class, path, unitObj);
		return resp.getMessage();
	}
	
	private String saveUnitAttribute(String path ,UnitAttribute unitAttribute) throws Exception{
		ActionResponse resp =  ThisApplication.context().applications()
				.postQuery(x_organization_assemble_control.class, path, unitAttribute);
		return resp.getMessage();
	}
	
	private String savePerson(String path ,Person personObj) throws Exception{
		ActionResponse resp =  ThisApplication.context().applications()
				.postQuery(x_organization_assemble_control.class, path, personObj);
		return resp.getMessage();
	}
	
	private String savePersonAttribute(String path ,PersonAttribute personAttribute) throws Exception{
		ActionResponse resp =  ThisApplication.context().applications()
				.postQuery(x_organization_assemble_control.class, path, personAttribute);
		return resp.getMessage();
	}
	
	private String saveIdentity(String path ,Identity identityObj) throws Exception{
		ActionResponse resp =  ThisApplication.context().applications()
				.postQuery(x_organization_assemble_control.class, path, identityObj);
		return resp.getMessage();
	}
	
	private String saveDuty(String path ,UnitDuty dutyObj) throws Exception{
		ActionResponse resp =  ThisApplication.context().applications()
				.postQuery(x_organization_assemble_control.class, path, dutyObj);
		return resp.getMessage();
	}
	
	private String editDuty(String path ,UnitDuty dutyObj) throws Exception{
		ActionResponse resp =  ThisApplication.context().applications()
				.putQuery(x_organization_assemble_control.class, path, dutyObj);
		return resp.getMessage();
	}
	
	private String saveGroup(String path ,Group groupObj) throws Exception{
		ActionResponse resp =  ThisApplication.context().applications()
				.postQuery(x_organization_assemble_control.class, path, groupObj);
		return resp.getMessage();
	}
	
	private String editGroup(String path ,Group groupObj) throws Exception{
		ActionResponse resp =  ThisApplication.context().applications()
				.putQuery(x_organization_assemble_control.class, path, groupObj);
		return resp.getMessage();
	}
	
	private boolean getDuty(EntityManagerContainer emc, DutyItem dutyItem) throws Exception{
		boolean checkduty = false;
		UnitDuty unitDuty = null;
		unitDuty= emc.flag(dutyItem.getUnique(), UnitDuty.class);
		if(unitDuty != null){
			checkduty = true;
			List<String> identityList = new ArrayList<>();
			identityList = unitDuty.getIdentityList();
			identityList.addAll(dutyItem.getIdentityList());
			unitDuty.setIdentityList(identityList);
			//editduty.add(unitDuty);
			this.editDuty("unitduty/"+unitDuty.getId(),unitDuty);
		}
		
		return checkduty;
	}
	
	   //生成随机数
	private String getCard(){
	       Random rand=new Random();//生成随机数
	        String cardNnumer="";
	        for(int a=0;a<8;a++){
	        cardNnumer+=rand.nextInt(10);//生成6位数字
	        }
	       return cardNnumer;


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

	private String saveAttachment(byte[] bytes,String attachmentName,EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
			GeneralFile generalFile = new GeneralFile(gfMapping.getName(), attachmentName, effectivePerson.getDistinguishedName());
			generalFile.saveContent(gfMapping, bytes, attachmentName);
			emc.beginTransaction(GeneralFile.class);
			emc.persist(generalFile, CheckPersistType.all);
			emc.commit();
			return generalFile.getId();
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