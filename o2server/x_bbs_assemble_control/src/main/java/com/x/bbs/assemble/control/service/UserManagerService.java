package com.x.bbs.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSShutup;

/**
 * 用户组织顶层组织信息管理服务类
 *
 * @author LIYI
 *
 */
public class UserManagerService {

	/**
	 * 根据用户唯一标识来获取用户对象
	 *
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public String getPersonNameByFlag(String flag) throws Exception {
		if (flag == null || flag.isEmpty()) {
			throw new Exception("flag is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().get( flag );
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据员工姓名获取组织名称，多个身份只取第一个组织
	 *
	 * @param employeeName
	 * @return
	 * @throws Exception
	 */
	public String getUnitNameWithPerson(String personName ) throws Exception {
		List<String> identities = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			identities = business.organization().identity().listWithPerson( personName );
			if ( identities == null || identities.size() == 0) {// 该员工目前没有分配身份
				return null;
			} else {
				return business.organization().unit().getWithIdentity( identities.get( 0 ) );
			}
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据员工身份获取组织名称
	 *
	 * @param employeeName
	 * @return
	 * @throws Exception
	 */
	public String getUnitNameWithIdentity(String identity ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().getWithIdentity( identity );
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	public String checkUnitExistsWithFlag(String unitName) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().get( unitName );
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listUnitNamesWithPerson(String personName ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().listWithPerson( personName );
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listUnitSupNestedWithPerson(String personName ) throws Exception {
		Business business = new Business(null);
		return business.organization().unit().listWithPersonSupNested( personName );
	}

	public String getTopUnitNameWithPerson( String personName ) throws Exception {
		List<String> identities = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			identities = business.organization().identity().listWithPerson( personName );
			if ( identities == null || identities.size() == 0) {// 该员工目前没有分配身份
				throw new Exception("can not get identity of personName:" + personName + ".");
			} else {
				return business.organization().unit().getWithIdentityWithLevel( identities.get( 0 ), 1 );
			}
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 根据身份名称获取顶层组织名称
	 *
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getTopUnitNameWithIdentity( String identity ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().getWithIdentityWithLevel( identity, 1 );
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listSupUnitNameWithParent( String unitName ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().listWithUnitSubNested( unitName );
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listGroupNamesSupNestedWithPerson(String personName ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().group().listWithPerson( personName );
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据组织名称获取所有下级组织名称列表
	 *
	 * @param query_creatorUnitName
	 * @return
	 * @throws Exception
	 */
	public List<String> listSubUnitNameWithParent(String parentName) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().listWithUnitSubNested( parentName );
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 判断用户是否有指定的平台角色，比如BBS系统管理员
	 *
	 * @param name
	 * @param roleName
	 * @return
	 * @throws Exception
	 */
	public boolean isHasPlatformRole( String name, String roleName ) throws Exception {
		if (name == null || name.isEmpty()) {
			throw new Exception("name is null!");
		}
		if (roleName == null || roleName.isEmpty()) {
			throw new Exception("roleName is null!");
		}
		List<String> roleList = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			roleList = business.organization().role().listWithPerson( name );
			if ( ListTools.isNotEmpty( roleList ) ) {
				for ( String role : roleList ) {
					if ( role.split("@")[0].equalsIgnoreCase( roleName ) || role.split("@")[0].equalsIgnoreCase( "BSSManager" )) {
						return true;
					}
				}
			} else {
				return false;
			}
		} catch ( NullPointerException e ) {
			return false;
		} catch ( Exception e ) {
			throw e;
		}
		return false;
	}

	public List<String> listPersonNamesWithUnitName( String unitName ) throws Exception {
		Business business = null;
		List<String> personList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			personList = business.organization().person().listWithUnitSubNested( unitName );
			if (personList != null && personList.size() > 0) {
				for ( String  person : personList) {
					nameList.add( person );
				}
			}
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
		return nameList;
	}

	public List<String> listPersonNamesWithGroupName( String groupName ) throws Exception {
		Business business = null;
		List<String> personList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			personList = business.organization().person().listWithGroup( groupName );
			if (personList != null && personList.size() > 0) {
				for ( String person : personList) {
					nameList.add( person );
				}
			}
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
		return nameList;
	}

	public String checkGroupExsitsWithName( String groupName ) throws Exception {
		if (groupName == null || groupName.isEmpty()) {
			throw new Exception("name is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().group().get( groupName );
		} catch ( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 判断指定用户是否被禁用
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean personHasShutup(String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.countEqual(BBSShutup.class, BBSShutup.person_FIELDNAME, person) > 0;
		}
	}
}
