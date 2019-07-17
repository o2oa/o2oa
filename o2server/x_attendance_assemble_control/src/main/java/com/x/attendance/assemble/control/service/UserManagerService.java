package com.x.attendance.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;

/**
 * 用户组织顶层组织信息管理服务类
 * @author LIYI
 *
 */
public class UserManagerService {

	public List<String> listIdentitiesWithPerson( String personName ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().identity().listWithPerson( personName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	/**
	 * 根据员工姓名获取组织名称
	 * 如果用户有多个身份，则取组织级别最大的组织名称
	 * @param employeeName
	 * @return
	 * @throws Exception 
	 */
	public String getUnitNameWithPersonName( String personName ) throws Exception{
		List<String> unitNames = null;		
		Business business = null;
		Integer level = 0;
		String result = null;
		Unit unit = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unitNames = business.organization().unit().listWithPerson( personName );
			if( ListTools.isNotEmpty( unitNames ) ) {
				for( String unitName : unitNames ) {
					if( StringUtils.isNotEmpty( unitName ) && !"null".equals( unitName ) ) {
						unit = business.organization().unit().getObject( unitName );
						if( level < unit.getLevel() ) {
							level = unit.getLevel();
							result = unitName;
						}
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 根据身份名称获取组织名称
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	public String getUnitNameWithIdentity( String identity ) throws Exception{	
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().getWithIdentity( identity );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据身份名称获取顶层组织名称
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	public String getTopUnitNameByIdentity( String identity ) throws Exception{	
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			String unitName = business.organization().unit().getWithIdentityWithLevel( identity, 1 );
			if( StringUtils.isEmpty( unitName )) {
				unitName = getUnitNameWithIdentity(identity);
			}
			return unitName;
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 检查组织名称是否有效
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public String checkUnitNameExists( String unitName ) throws Exception {
		if( unitName == null || unitName.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().get( unitName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据员工姓名获取顶层组织名称，多个身份只取第一个身份
	 * @param employeeName
	 * @return
	 * @throws Exception 
	 */
	public String getTopUnitNameWithPersonName( String personName ) throws Exception{
		String identity = null;
		String topUnitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			//兼容一下传过来的personName有可能是个人，有可能是身份
			personName = business.organization().person().get( personName );
			identity = getIdentityWithPerson( personName );
			if( StringUtils.isNotEmpty( identity ) ){
				topUnitName = business.organization().unit().getWithIdentityWithLevel( identity, 1 );
			}
			if( StringUtils.isEmpty( topUnitName )) {
				topUnitName = getUnitNameWithIdentity(identity);
			}
			return topUnitName;
		} catch ( Exception e ) {
			throw e;
		}
	}
	/**
	 * 根据个人姓名获取所有身份中组织等级最高的一个身份
	 * 
	 * @param personName
	 * @return
	 * @throws Exception 
	 */
	private String getIdentityWithPerson( String personName ) throws Exception {
		List<String> identities = null;
		String unitName = null;
		Integer level = 0;
		String resultIdentity = null;
		Unit unit = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			//兼容一下传过来的personName有可能是个人，有可能是身份
			personName = business.organization().person().get( personName );
			identities = business.organization().identity().listWithPerson( personName );
			if( identities != null && !identities.isEmpty() ) {
				for( String identity : identities ) {
					unitName = business.organization().unit().getWithIdentity( identity );
					unit = business.organization().unit().getObject( unitName );
					if( level < unit.getLevel() ) {
						level = unit.getLevel();
						resultIdentity = identity;
					}
				}
			}
			return resultIdentity;
		} catch ( Exception e ) {
			throw e;
		}
	}
	/**
	 * 获取上级组织名称
	 * @param unitName
	 * @return
	 * @throws Exception 
	 */
	public String getParentUnitWithUnitName( String unitName ) throws Exception {
		Unit currentUnit = null;
		Unit parentUnit = null;
		String parentUnitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			currentUnit = business.organization().unit().getObject( unitName );
			if( currentUnit != null ) {
				parentUnitName = currentUnit.getSuperior();
				if( StringUtils.isNotEmpty( parentUnitName ) && !"0".equals( parentUnitName ) ) {
					try {
						parentUnit = business.organization().unit().getObject( currentUnit.getSuperior() );
						if( parentUnit == null ) {
							return null;
						}else {
							return parentUnit.getDistinguishedName();
						}
					}catch( NullPointerException e ) {
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 根据组织名称获取顶层组织名称(递归)
	 * @param organizationName
	 * @return
	 * @throws Exception 
	 */
	public String getTopUnitNameWithUnitName( String unitName ) throws Exception{
		Unit currentUnit = null;
		Unit parentUnit = null;
		String parentUnitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			currentUnit = business.organization().unit().getObject( unitName );
			if( currentUnit != null ) {
				parentUnitName = currentUnit.getSuperior();
				if( StringUtils.isNotEmpty( parentUnitName ) && !"0".equals( parentUnitName ) ) {
					try {
						parentUnit = business.organization().unit().getObject( parentUnitName );
					}catch( NullPointerException e ) {
					}
				}
				if( parentUnit == null ) {
					return currentUnit.getDistinguishedName();
				}else {
					return getTopUnitNameWithUnitName( parentUnit.getDistinguishedName() );
				}
			}else {
				throw new Exception("unit is not exists:" + unitName);
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据上级组织名称获取所有下级组织名称列表
	 * @param parentUnitName
	 * @return
	 * @throws Exception 
	 */
	public List<String> listSubUnitNameWithParent(String parentUnitName ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().listWithUnitSubNested( parentUnitName );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据人员身份信息获取人员姓名
	 * @param identityName
	 * @return
	 * @throws Exception
	 */
	public String getPersonNameByIdentity( String identityName ) throws Exception {
		if( identityName == null || identityName.isEmpty() ){
			throw new Exception( "identityName is null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().getWithIdentity( identityName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 检查人员是否存在
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String checkPersonExists( String personName ) throws Exception {
		if( personName == null || personName.isEmpty() ){
			throw new Exception( "personName is null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().get( personName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> listUnitNamesWithPerson(String personName) throws Exception {
		if( personName == null || personName.isEmpty() ){
			throw new Exception( "personName is null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().listWithPerson( personName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	public List<String> listAttributeWithPersonWithName(String personName, String attributeName ) throws Exception {
		if( personName == null || personName.isEmpty() ){
			throw new Exception( "personName is null!" );
		}
		if( attributeName == null || attributeName.isEmpty() ){
			throw new Exception( "attributeName is null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().personAttribute().listAttributeWithPersonWithName( personName, attributeName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	/**
	 * 根据个人获取汇报对象
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getReporterWithPerson(String personName) throws Exception {
		if( personName == null || personName.isEmpty() ){
			throw new Exception( "personName is null!" );
		}
		Person person = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			person = business.organization().person().getObject( personName );
			if( person != null ) {
				if( person.getSuperior() != null ) {
					return business.organization().person().get( person.getSuperior() );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}
	
	public List<String> getUnitDutyWithUnitWithDuty( String unitName, String dutyName ) throws Exception {
		if( unitName == null || unitName.isEmpty() ){
			throw new Exception( "unitName is null!" );
		}
		if( dutyName == null || dutyName.isEmpty() ){
			throw new Exception( "dutyName is null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unitDuty().listIdentityWithUnitWithName( unitName, dutyName );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> getUnitDutyWithPersonWithDuty( String personName, String dutyName ) throws Exception {
		if( personName == null || personName.isEmpty() ){
			throw new Exception( "personName is null!" );
		}
		if( dutyName == null || dutyName.isEmpty() ){
			throw new Exception( "dutyName is null!" );
		}
		String unitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			try {
				unitName = getUnitNameWithPersonName( personName );
			}catch( NullPointerException ex ) {
				throw new Exception("根据个人查询组织信息发生异常。person:" + personName );
			}
			if( unitName == null || unitName.isEmpty() ) {
				try {
					unitName = getUnitNameWithIdentity( personName );
				}catch( NullPointerException ex ) {
					throw new Exception("根据个人查询组织信息发生异常。person:" + personName );
				}
			}
			if( unitName == null || unitName.isEmpty() ) {
				return null;
			}else {
				try {
					return business.organization().unitDuty().listIdentityWithUnitWithName( unitName, dutyName );
				}catch( NullPointerException ex ) {
					throw new Exception("根据组织查询组织职位时发生异常。unitName:" + unitName + ", dutyName:" + dutyName );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public List<String> getUnitDutyWithIdentityWithDuty( String identity, String dutyName ) throws Exception {
		if( identity == null || identity.isEmpty() ){
			throw new Exception( "identity is null!" );
		}
		if( dutyName == null || dutyName.isEmpty() ){
			throw new Exception( "dutyName is null!" );
		}
		String unitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			try {
				unitName = getUnitNameWithIdentity( identity );
			}catch( NullPointerException ex ) {
				throw new Exception("根据个人身份查询组织信息发生异常。identity:" + identity );
			}
			if( unitName == null || unitName.isEmpty() ) {
				throw new Exception("根据个人身份未查询到所属的组织信息。identity:" + identity );
			}else {
				try {
					return business.organization().unitDuty().listIdentityWithUnitWithName( unitName, dutyName );
				}catch( NullPointerException ex ) {
					throw new Exception("根据组织查询组织职位时发生异常。unitName:" + unitName + ", dutyName:" + dutyName );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	public String getUnitLevelNameWithName(String unitName) throws Exception {
		if( unitName == null || unitName.isEmpty() ){
			throw new Exception( "unitName is null!" );
		}
		Unit unit = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unit = business.organization().unit().getObject(unitName);
			if( unit != null ) {
				return unit.getLevelName();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}
}