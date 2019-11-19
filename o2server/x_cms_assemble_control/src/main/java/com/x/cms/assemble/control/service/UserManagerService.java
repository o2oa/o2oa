package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.cms.assemble.control.ThisApplication;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;

/**
 * 组织人员角色相关信息的服务类
 * 
 * @author O2LEE
 */
public class UserManagerService {
	
	/**
	 * 根据人员名称获取人员
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public Person getPerson( String personName ) throws Exception {
		Business business = null;
		Person person = null;
		List<Person> personList = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			person = business.organization().person().getObject( personName );
			if( person == null ) {
				if( personName.endsWith( "@P" ) && personName.split( "@P" ).length == 3 ) {
					personList = business.organization().person().listObject( personName.split( "@" )[1] );
					if( ListTools.isNotEmpty( personList )) {
						return personList.get(0);
					}
				}
			}
			if( person == null ) {
				if( personName.endsWith( "@P" ) && personName.split( "@P" ).length == 3 ) {
					personList = business.organization().person().listObject( personName.split( "@" )[0] );
					if( ListTools.isNotEmpty( personList )) {
						return personList.get(0);
					}
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return person;
	}
	
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
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().getWithIdentity( identity );
		} catch( NullPointerException e ) {
			System.out.println("根据身份获取所属组织名称时发生NullPointerException异常。identity：" + identity );
			return null;
		} catch (Exception e) {
			System.out.println("根据身份获取所属组织名称时发生异常。identity：" + identity );
			throw e;
		}
	}
	
	/**
	 * 根据人员姓名获取人员所属的一级组织名称，如果人员有多个身份，则取组织等级最大的身份
	 * @param personName
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
	 * 根据身份名称获取身份所属的顶层组织名称
	 * 
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getTopUnitNameByIdentity( String identity ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().getWithIdentityWithLevel( identity, 1 );
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
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
						break;
					}
				}
			}
			return resultIdentity;
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据用户的身份查询用户的姓名
	 * 
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getPersonNameWithIdentity( String identity ) throws Exception {
		if ( StringUtils.isEmpty( identity )) {
			throw new Exception("identity is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().getWithIdentity( identity );
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}


	/**
	 * 获取人员所属的所有组织名称列表
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listUnitNamesWithPerson( String personName ) throws Exception {
		List<String> unitNames = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			unitNames = business.organization().unit().listWithPersonSupNested( personName );
			return unitNames == null ? new ArrayList<>():unitNames ;
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据用户姓名查询用户所有的身份信息
	 * 
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdentitiesWithPerson( String userName ) throws Exception {
		if ( StringUtils.isEmpty( userName )) {
			throw new Exception("userName is null!");
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.organization().identity().listWithPerson(userName);
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 列示人员所拥有的所有角色信息
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listRoleNamesByPerson( String personName ) throws Exception {
		Business business = null;
		List<String> roleList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			roleList = business.organization().role().listWithPerson( personName );
			if (roleList != null && roleList.size() > 0) {
				roleList.stream().filter( r -> !nameList.contains( r )).distinct().forEach( r -> nameList.add( r ));
			}
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
		return nameList == null ? new ArrayList<>():nameList;
	}

	/**
	 * 列示人员所拥有的所有群组信息
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listGroupNamesByPerson(String personName) throws Exception {
		Business business = null;
		List<String> groupList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			groupList = business.organization().group().listWithPerson( personName );
			if (groupList != null && groupList.size() > 0) {
				groupList.stream().filter( g -> !nameList.contains( g )).distinct().forEach( g -> nameList.add( g ));
			}
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
		return nameList == null ? new ArrayList<>():nameList;
	}
	
	/**
	 * 判断用户是否有指定的平台角色，比如CMS系统管理员
	 * 
	 * @param personName
	 * @param roleName
	 * @return
	 * @throws Exception
	 */
	public boolean isHasPlatformRole( String personName, String roleName) throws Exception {
		if ( StringUtils.isEmpty( personName )) {
			throw new Exception("personName is null!");
		}
		if ( StringUtils.isEmpty( roleName )) {
			throw new Exception("roleName is null!");
		}
		List<String> roleList = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			roleList = business.organization().role().listWithPerson( personName );
			if (roleList != null && !roleList.isEmpty()) {
				if( roleList.stream().filter( r -> roleName.equalsIgnoreCase( r )).count() > 0 ){
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
	 * 判断指定人员是否是管理员
	 * 1、xadmin
	 * 2、拥有manager角色
	 * 3、拥有CMSManager@CMSManagerSystemRole@R角色
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Boolean isManager( EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson == null) {
			throw new Exception("currentPerson is null!");
		}
		if(effectivePerson.isManager()){
			return true;
		}
		if( effectivePerson.isManager() || effectivePerson.isCipher() ){
			return true;
		}
		if( this.isHasPlatformRole( effectivePerson.getDistinguishedName(), ThisApplication.ROLE_CMSManager)){
			return true;
		}
		return false;
	}
	
	public String getPersonIdentity( String personName, String identity ) throws Exception {
		List<String> identityNames = null;
		String identityName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			identityNames = listIdentitiesWithPerson( personName );
//			LogUtil.INFO("identityNames:", identityNames );
			if( StringUtils.isEmpty( identity ) ) {
				if ( identityNames.size() == 0 ) {
					throw new Exception("perons has no identity. personName:" + personName );
				} else if ( identityNames.size() > 0 ) {
					identityName = identityNames.get(0);
				}
			}else {
				//判断传入的身份是否合法
				identityName = this.findIdentity( identityNames, identity );
				if ( StringUtils.isEmpty( identity ) ) {
					identityName = identityNames.get(0);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return identityName;
	}

	private String findIdentity( List<String> identityNames, String identityName ) throws Exception {
		if( identityName != null && !identityName.isEmpty() && !"null".equals(identityName)){
			if( identityNames != null && !identityNames.isEmpty() ){
				for ( String identity : identityNames ) {
					if ( identity.equalsIgnoreCase(identityName) ) {
						return identity;
					}
				}
			}
		}else{
			if( identityNames != null && !identityNames.isEmpty() ){
				return identityNames.get(0);
			}
		}
		return null;
	}

	public List<String> getPersonPermissionCodes(String personName) throws Exception {
		List<String> queryObjectNames = new ArrayList<>();
		List<String> groupNames = null;
		List<String> roleNames = null;
		List<String> unitNames = null;
		
		//选查询个人涉及的所有组织角色以及群组编码
		groupNames = listGroupNamesByPerson( personName );
		roleNames = listRoleNamesByPerson( personName );
		unitNames = listUnitNamesWithPerson(personName);
		
		queryObjectNames.add( personName );
		
		//将三个列表合为一个，再进行分类权限查询
		if( groupNames != null && !groupNames.isEmpty() ) {
			for( String name : groupNames ) {
				if( !queryObjectNames.contains( name )) {
					queryObjectNames.add( name );
				}
			}
		}
		if( roleNames != null && !roleNames.isEmpty() ) {
			for( String name : roleNames ) {
				if( !queryObjectNames.contains( name )) {
					queryObjectNames.add( name );
				}
			}
		}
		if( unitNames != null && !unitNames.isEmpty() ) {
			for( String name : unitNames ) {
				if( !queryObjectNames.contains( name )) {
					queryObjectNames.add( name );
				}
			}
		}
		return queryObjectNames;
	}

	/**
	 * 根据组织名称，查询组织内所有的人员标识，包括下级组织
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public List<String> listPersonWithUnit( String unitName ) throws Exception {
		if ( StringUtils.isEmpty( unitName )) {
			throw new Exception("unitName is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Business business = new Business(emc); 
			return business.organization().person().listWithUnitSubNested( unitName );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据群组名称，查询群组内所有的人员标识
	 * @param groupName
	 * @return
	 * @throws Exception
	 */
	public List<String> listPersonWithGroup( String groupName ) throws Exception {
		if ( StringUtils.isEmpty( groupName )) {
			throw new Exception("groupName is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Business business = new Business(emc); 
			return business.organization().person().listWithGroup( groupName );
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listPersonWithRole(String role ) throws Exception {
		if ( StringUtils.isEmpty( role )) {
			throw new Exception("role is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Business business = new Business(emc); 
			return business.organization().person().listWithRole( role );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询系统内所有顶级组织的数量
	 * @return
	 * @throws Exception
	 */
	public int countTopUnit() throws Exception {
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Business business = new Business(emc); 
			List<String> unitNames = business.organization().unit().listWithLevel(1);
			if( ListTools.isNotEmpty( unitNames )) {
				return unitNames.size();
			}
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/**
	 * 判断指定组织是否是顶级组织
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public boolean isTopUnit(String unitName ) throws Exception {
		if ( StringUtils.isEmpty( unitName )) {
			throw new Exception("unitName is empty!");
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			Business business = new Business(emc); 
			List<String> unitNames = business.organization().unit().listWithLevel(1);
			if( ListTools.isNotEmpty( unitNames ) && unitNames.contains( unitName )) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}
}