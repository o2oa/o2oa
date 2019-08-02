package com.x.teamwork.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.assemble.control.Business;


/**
 * 组织人员角色相关信息的服务类
 * 
 * @author O2LEE
 */
public class UserManagerService {
	
	private Logger logger = LoggerFactory.getLogger(UserManagerService.class);
	
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
	 * 如果用户有多个身份，由type参数来决定取组织级别最大的组织名称或者最小的组织名称
	 * 
	 * @param personName  distinguishedName
	 * @param type  max|min
	 * @return
	 * @throws Exception
	 */
	public String getUnitNameWithPerson( String personName, String type ) throws Exception {
		List<String> unitNames = null;		
		Business business = null;
		Integer level = 0;
		String result = null;
		Unit unit = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unitNames = business.organization().unit().listWithPerson( personName );
			if( ListTools.isNotEmpty( unitNames )  ) {
				for( String unitName : unitNames ) {
					unit = business.organization().unit().getObject( unitName );
					if( "max".equalsIgnoreCase( type )) {
						if( level < unit.getLevel() ) {
							level = unit.getLevel();
							result = unitName;
						}
					}else {
						if( level > unit.getLevel() ) {
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
	 * 根据个人姓名，由type参数来决定取组织级别最大的身份名称或者最小的身份名称
	 * 
	 * @param personName  distinguishedName
	 * @param type  max|min
	 * @return
	 * @throws Exception
	 */
	public String getIdentityWithPerson( String personName, String type ) throws Exception {
		if( personName.endsWith( "@I" )) {
			return personName;
		}
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
			if( ListTools.isNotEmpty( identities )  ) {				
				for( String identity : identities ) {
					unitName = business.organization().unit().getWithIdentity( identity );
					unit = business.organization().unit().getObject( unitName );
					if(StringUtils.isEmpty( resultIdentity )) {
						resultIdentity = identity;
						level = unit.getLevel();
					}
					if( "max".equalsIgnoreCase( type )) {
						if( level < unit.getLevel() ) {
							level = unit.getLevel();
							resultIdentity = identity;
						}
					}else {
						if( level > unit.getLevel() ) {
							level = unit.getLevel();
							resultIdentity = identity;
						}
					}
				}
			}
			return resultIdentity;
		} catch ( Exception e ) {
			throw e;
		}
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
			logger.info( "根据身份获取所属组织名称时发生异常。identity：" + identity );
			throw e;
		} catch (Exception e) {
			logger.info( "根据身份获取所属组织名称时发生未知异常。identity：" + identity );
			throw e;
		}
	}
	
	/**
	 * 根据人员姓名获取人员所属的一级组织名称，如果人员有多个身份，则取组织等级最大的身份
	 * @param personName  distinguishedName
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
			identity = getIdentityWithPerson( personName, "max" );
			if( StringUtils.isNotEmpty( identity )){
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
	 * 根据用户的身份查询用户的姓名
	 * 
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getPersonNameWithIdentity( String identity ) throws Exception {
		if ( StringUtils.isEmpty( identity )) {
			throw new Exception("identity is empty!");
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
	 * @param personName  distinguishedName
	 * @return
	 * @throws Exception
	 */
	public List<String> listUnitNamesWithPerson( String personName ) throws Exception {
		List<String> unitNames = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			unitNames = business.organization().unit().listWithPersonSupNested( personName );
			return unitNames;
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 根据用户姓名查询用户所有的身份信息
	 * 
	 * @param userName  distinguishedName
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdentitiesWithPerson( String userName ) throws Exception {
		if (userName == null || userName.isEmpty()) {
			throw new Exception("userName is null!");
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.organization().identity().listWithPerson( userName );
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 列示人员所拥有的所有角色信息
	 * @param personName  distinguishedName
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
			if ( ListTools.isNotEmpty( roleList ) ) {
				roleList.stream().filter( r -> !nameList.contains( r )).distinct().forEach( r -> nameList.add( r ));
			}
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
		return nameList;
	}

	/**
	 * 列示人员所拥有的所有群组信息
	 * @param personName distinguishedName
	 * @return
	 * @throws Exception
	 */
	public List<String> listGroupNamesByPerson( String personName ) throws Exception {
		Business business = null;
		List<String> groupList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			groupList = business.organization().group().listWithPerson( personName );
			if ( ListTools.isNotEmpty( groupList )) {
				groupList.stream().filter( g -> !nameList.contains( g )).distinct().forEach( g -> nameList.add( g ));
			}
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
		return nameList;
	}
	
	/**
	 * 判断用户是否有指定的平台角色，比如CMS系统管理员
	 * 
	 * @param personName  distinguishedName
	 * @param roleName  distinguishedName
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
			if ( ListTools.isNotEmpty( roleList ) ) {
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
	 * 检查用户是否拥有指定的身份，如果有就用这个身份，如果没有就取第一个身份
	 * @param personName distinguishedName
	 * @param identity  distinguishedName
	 * @return
	 * @throws Exception
	 */
	public String checkPersonIdentity( String personName, String identity ) throws Exception {
		String identityName = null;
		List<String> identityNames = listIdentitiesWithPerson( personName );
		if ( ListTools.isEmpty( identityNames )) {
			throw new Exception("perons has no identity.");
		}
		if( StringUtils.isEmpty( identity ) ) {
			identityName = identityNames.get(0);
		}else { //判断传入的身份是否合法
			identityName = this.findIdentity( identityNames, identity );
			if ( identityName == null ) {
				identityName = identityNames.get(0);
			}
		}
		return identityName;
	}	

	/**
	 * 根据人员标识查询该用户拥有的所有的组织、群组、角色名列表
	 * @param personName   distinguishedName
	 * @return
	 * @throws Exception
	 */
	public List<String> listPersonPermissionCodes(String personName) throws Exception {
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
	 * 在List里找指定的值，如果找不到就返回空，如果需要匹配的identityName为空，那么就返回第一个身份
	 * @param identityNames
	 * @param identityName
	 * @return
	 * @throws Exception
	 */
	private String findIdentity( List<String> identityNames, String identityName ) throws Exception {
		if( StringUtils.isNotEmpty( identityName )){
			if( ListTools.isNotEmpty( identityNames ) ){
				for ( String identity : identityNames ) {
					if ( identity.equalsIgnoreCase(identityName) ) {
						return identity;
					}
				}
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