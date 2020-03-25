package com.x.okr.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.okr.assemble.control.Business;

/**
 * 用户组织顶层组织信息管理服务类
 * @author O2LEE
 *
 */
public class OkrUserManagerService {

	/**
	 * 根据员工姓名获取组织名称
	 * 如果用户有多个身份，则取组织级别最大的组织名称
	 * 
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getUnitNameWithPerson( String personName ) throws Exception {
		List<String> unitNames = null;		
		Business business = null;
		Integer level = 0;
		String result = null;
		Unit unit = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unitNames = business.organization().unit().listWithPerson( personName );
			if( unitNames != null && !unitNames.isEmpty() ) {
				for( String unitName : unitNames ) {
					unit = business.organization().unit().getObject( unitName );
					if( level < unit.getLevel() ) {
						level = unit.getLevel();
						result = unitName;
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return result;
	}
	
	/**
	 * 根据身份名称获取身份所属的组织名称
	 * 
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getUnitNameByIdentity( String identity ) throws Exception {
		Business business = null;
		String unitName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unitName = business.organization().unit().getWithIdentity( identity );
			if ( unitName == null ) {// 该根据身份无法查询到组织信息
				return null;
			} else {
				return unitName;
			}
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据身份名称获取身份所属的顶层组织名称
	 * 
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getTopUnitNameByIdentity( String identity ) throws Exception {
		Business business = null;
		String unitName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unitName = business.organization().unit().getWithIdentityWithLevel( identity, 1 );
			if ( unitName == null ) {
				return null;
			} else {
				return unitName;
			}
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据人员姓名获取人员所属的顶层组织名称，如果人员有多个身份，则取组织等级最大的身份
	 * @param personName
	 * @param level
	 * @return
	 * @throws Exception
	 */
	public String getTopUnitNameWithPerson( String personName ) throws Exception {
		String identity = null;
		String topUnitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			//兼容一下传过来的perosnName有可能是个人，有可能是身份
			personName = business.organization().person().get( personName );
			identity = getIdentityWithPerson( personName );
			if( identity != null && !identity.isEmpty() ){
				topUnitName = business.organization().unit().getWithIdentityWithLevel( identity, 1 );
			}
			return topUnitName;
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据个人姓名，根据个人姓名获取所有身份中组织等级最高的一个身份
	 * 
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getIdentityWithPerson( String personName ) throws Exception {
		List<String> identities = null;
		String unitName = null;
		Integer level = 0;
		String resultIdentity = null;
		Unit unit = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			//兼容一下传过来的perosnName有可能是个人，有可能是身份
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
	 * 查询当前用户是否有指定的身份信息
	 * @param name
	 * @param loginIdentity
	 * @return
	 * @throws Exception 
	 */
	public boolean hasIdentity( String personName, String loginIdentity ) throws Exception {
		if( loginIdentity == null || loginIdentity.isEmpty() ){
			throw new Exception( "loginIdentity is null!" );
		}
		Business business = null;
		List<String> identities = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			identities = business.organization().identity().listWithPerson( personName );
//			System.out.println(">>>>>>>>>>>loginIdentity:" + loginIdentity );
//			if( ListTools.isNotEmpty( identities )) {
//				for( String identity : identities ) {
//					System.out.println(">>>>>>>>>>>identity:" + identity );
//				}
//			}			
			if( identities.contains( loginIdentity )){
				return true;
			}
		} catch ( Exception e ) {
			throw e;
		}
		return false;
	}

	/**
	 * 根据用户的身份查询用户的姓名
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	public String getPersonNameByIdentity( String identity ) throws Exception {
		if( identity == null || identity.isEmpty() ){
			throw new Exception( "identity is null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().getWithIdentity( identity );
		} catch ( Exception e ) {
			throw e;
		}
	}	

	/**
	 * 根据用户唯一标识来获取用户对象
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public String getPersonNameWithFlag( String flag ) throws Exception {
		if( flag == null || flag.isEmpty() ){
			throw new Exception( "flag is null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().get( flag );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 判断用户是否有指定的平台角色，比如CMS系统管理员
	 * 
	 * @param name
	 * @param string
	 * @return
	 * @throws Exception
	 */
	public boolean isHasPlatformRole( String personName, String roleName) throws Exception {
		if ( personName == null || personName.isEmpty()) {
			throw new Exception("personName is null!");
		}
		if (roleName == null || roleName.isEmpty()) {
			throw new Exception("roleName is null!");
		}
		List<String> roleList = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			roleList = business.organization().role().listWithPerson( personName );
			if (roleList != null && !roleList.isEmpty()) {
				if( roleList.stream().filter( r -> roleName.equalsIgnoreCase( r.split("@")[0] )).count() > 0 ){
					return true;
				}
			} else {
				return false;
			}
		} catch( NullPointerException e ) {
			return false;
		} catch (Exception e) {
			throw e;
		}
		return false;
	}
	
	/**
	 * 判断用户是否是系统工作管理员
	 * @param userName
	 * @param roleName
	 * @return
	 * @throws Exception 
	 */
	public boolean isOkrWorkManager( String userIdentity ) throws Exception {
		if( userIdentity == null || userIdentity.isEmpty() ){
			throw new Exception( "userIdentity is null!" );
		}
		String[] configValues = null;
		String configValue = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			configValue = business.okrConfigSystemFactory().getValueWithConfigCode("TOPUNIT_WORK_ADMIN");
			if( configValue != null && !configValue.isEmpty() ){
				configValues = configValue.split( "," );
				for( String identityName : configValues ){
					if( identityName.equalsIgnoreCase( userIdentity )){
						return true;
					}
				}
			}else{
				return false;
			}
		} catch ( Exception e ) {
			throw e;
		}
		return false;
	}

	/**
	 * 
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public Boolean isIdentityExsits( String identity ) throws Exception {
		if( identity == null || identity.isEmpty() ){
			throw new Exception( "identity is null!" );
		}
		Business business = null;
		String result = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			result = business.organization().identity().get( identity );
			if( result == null || result.isEmpty() ){
				return false;
			}else{
				return true;
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 获取员工号
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getEmployeeNoWithPerson(String personName) throws Exception {
		if( personName == null || personName.isEmpty() ){
			throw new Exception( "personName is null!" );
		}
		Business business = null;
		Person person = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			person = business.organization().person().getObject(personName);
			return person.getEmployee();
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 获取Unique
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getUniqueWithPerson(String personName) throws Exception {
		if( personName == null || personName.isEmpty() ){
			throw new Exception( "personName is null!" );
		}
		Business business = null;
		Person person = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			person = business.organization().person().getObject(personName);
			return person.getUnique();
		} catch ( Exception e ) {
			throw e;
		}
	}
}