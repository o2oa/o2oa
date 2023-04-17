package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Identity;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.organization.core.entity.PersistenceProperties;

/**
 * 组织人员角色相关信息的服务类
 *
 * @author O2LEE
 */
public class UserManagerService {

	private static Logger logger = LoggerFactory.getLogger(UserManagerService.class);

	/**
	 * 根据人员名称获取人员
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public Person getPerson(String personName) throws Exception {
		Person person = null;
		try {
			Business business = new Business(null);
			if(personName.split("@").length == 2){
				personName = personName.split("@")[0];
			}
			person = business.organization().person().getObject(personName);
		} catch (Exception e) {
			throw e;
		}
		return person;
	}

	/**
	 * 根据员工姓名获取组织名称 如果用户有多个身份，则取组织级别最大的组织名称
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getUnitNameWithPerson(String personName) throws Exception {
		List<String> unitNames = null;
		Business business = null;
		Integer level = 0;
		String result = null;
		Unit unit = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unitNames = business.organization().unit().listWithPerson(personName);
			if ( ListTools.isEmpty( unitNames )) {
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					unitNames = business.organization().unit().listWithPerson( personName.split("@")[0] );
				}
			}
			if ( ListTools.isNotEmpty( unitNames )) {
				for (String unitName : unitNames) {
					unit = business.organization().unit().getObject(unitName);
					if (level < unit.getLevel()) {
						level = unit.getLevel();
						result = unitName;
					}
				}
			}
		} catch (Exception e) {
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
	public String getUnitNameByIdentity(String identity) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().getWithIdentity(identity);
		} catch (NullPointerException e) {
			System.out.println("根据身份获取所属组织名称时发生NullPointerException异常。identity：" + identity);
			return null;
		} catch (Exception e) {
			System.out.println("根据身份获取所属组织名称时发生异常。identity：" + identity);
			throw e;
		}
	}

	/**
	 * 根据人员姓名获取人员所属的一级组织名称，如果人员有多个身份，则取组织等级最大的身份
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getTopUnitNameWithPerson(String personName) throws Exception {
		String identity = null;
		String topUnitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			// 兼容一下传过来的perosnName有可能是个人，有可能是身份
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			identity = getMajorIdentityWithPerson(personName);
			if (StringUtils.isNotBlank(identity)) {
				topUnitName = business.organization().unit().getWithIdentityWithLevel(identity, 1);
			}
			return topUnitName;
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
	public String getTopUnitNameByIdentity(String identity) throws Exception {
		try {
			Business business = new Business(null);
			return business.organization().unit().getWithIdentityWithLevel(identity, 1);
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据个人姓名，根据个人姓名获取主身份
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getMajorIdentityWithPerson( String personName ) throws Exception {
		List<String> identities = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			identities = business.organization().identity().listWithPerson(personName);
			if (ListTools.isNotEmpty( identities )) {
				if( identities.size() == 1 ) {
					return identities.get(0);
				}else{
					for (String identity : identities) {
						Identity obj = business.organization().identity().getObject(identity);
						if (obj!= null && BooleanUtils.isTrue(obj.getMajor())) {
							return identity;
						}
					}
				}
				return identities.get(0);
			}
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
	public String getPersonNameWithIdentity(String identity) throws Exception {
		if (StringUtils.isEmpty(identity)) {
			throw new Exception("identity is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().getWithIdentity(identity);
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 获取人员所属的所有组织名称列表
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listUnitNamesWithPerson(String personName) throws Exception {
		List<String> unitNames = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			unitNames = business.organization().unit().listWithPersonSupNested(personName);
			return unitNames == null ? new ArrayList<>() : unitNames;
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据用户姓名查询用户所有的身份信息
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdentitiesWithPerson(String personName) throws Exception {
		if (StringUtils.isEmpty(personName)) {
			throw new Exception("userName is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			return business.organization().identity().listWithPerson(personName);
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 列示人员所拥有的所有角色信息
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listRoleNamesByPerson(String personName) throws Exception {
		Business business = null;
		List<String> roleList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			roleList = business.organization().role().listWithPerson(personName);
			if (roleList != null && roleList.size() > 0) {
				roleList.stream().filter(r -> !nameList.contains(r)).distinct().forEach(r -> nameList.add(r));
			}
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			throw e;
		}
		return nameList == null ? new ArrayList<>() : nameList;
	}

	/**
	 * 列示人员所拥有的所有群组信息
	 *
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
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			groupList = business.organization().group().listWithPerson(personName);
			if (groupList != null && groupList.size() > 0) {
				groupList.stream().filter(g -> !nameList.contains(g)).distinct().forEach(g -> nameList.add(g));
			}
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			throw e;
		}
		return nameList == null ? new ArrayList<>() : nameList;
	}

	/**
	 * 判断用户是否有指定的平台角色，比如CMS系统管理员
	 *
	 * @param personName
	 * @param roleName
	 * @return
	 * @throws Exception
	 */
	public boolean isHasPlatformRole(String personName, String roleName) throws Exception {
		if (StringUtils.isEmpty(personName)) {
			throw new Exception("personName is null!");
		}
		if (StringUtils.isEmpty(roleName)) {
			throw new Exception("roleName is null!");
		}
		List<String> roleList = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			roleList = business.organization().role().listWithPerson(personName);
			if (roleList != null && !roleList.isEmpty()) {
				if (roleList.stream().filter(r -> roleName.equalsIgnoreCase(r)).count() > 0) {
					return true;
				}
			} else {
				return false;
			}
		} catch (NullPointerException e) {
			return false;
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	/**
	 * 判断指定人员是否是管理员 1、xadmin 2、拥有manager角色 3、拥有CMSManager@CMSManagerSystemRole@R角色
	 *
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Boolean isManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson == null) {
			throw new Exception("effectivePerson is null!");
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.isHasPlatformRole(effectivePerson.getDistinguishedName(), ThisApplication.ROLE_CMSManager)) {
			return true;
		}
		return false;
	}

	public String getPersonIdentity(String personName, String identity) throws Exception {
		List<String> identityNames = null;
		String identityName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			identityNames = listIdentitiesWithPerson(personName);
//			LogUtil.INFO("identityNames:", identityNames );
			if (StringUtils.isEmpty(identity)) {
				if (identityNames.size() == 0) {
					throw new Exception("person has no identity. personName:" + personName);
				} else if (identityNames.size() > 0) {
					identityName = identityNames.get(0);
				}
			} else {
				// 判断传入的身份是否合法
				identityName = this.findIdentity(identityNames, identity);
				if (StringUtils.isEmpty(identity)) {
					identityName = identityNames.get(0);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return identityName;
	}

	private String findIdentity(List<String> identityNames, String identityName) throws Exception {
		if (identityName != null && !identityName.isEmpty() && !"null".equals(identityName)) {
			if (identityNames != null && !identityNames.isEmpty()) {
				for (String identity : identityNames) {
					if (identity.equalsIgnoreCase(identityName)) {
						return identity;
					}
				}
			}
		} else {
			if (identityNames != null && !identityNames.isEmpty()) {
				return identityNames.get(0);
			}
		}
		return null;
	}

	/**
	 * 根据人员、组织、群组查询人员信息
	 * 参数name必须为xx@xx@x格式
	 * @param name
	 * @return
	 */
	public List<String> listPersonWithName(String name) {
		List<String> list = new ArrayList<>();
		if (StringUtils.isEmpty( name ) || name.indexOf("@") == -1) {
			return list;
		}
		Matcher matcher = PersistenceProperties.Person.distinguishedName_pattern.matcher(name);
		if(matcher.find()){
			list.add(name);
			return list;
		}
		matcher = PersistenceProperties.Unit.distinguishedName_pattern.matcher(name);
		if(matcher.find()) {
			try {
				Business business = new Business(null);
				return business.organization().person().listWithUnitSubNested(name);
			} catch (Exception e) {
				logger.warn("根据组织【{}】查询人员异常：{}", name,e.getMessage());
			}
		}
		matcher = PersistenceProperties.Identity.distinguishedName_pattern.matcher(name);
		if(matcher.find()){
			try {
				Business business = new Business(null);
				String person = business.organization().person().getWithIdentity(name);
				if(StringUtils.isNotBlank(person)){
					list.add(person);
				}
				return list;
			} catch (Exception e) {
				logger.warn("根据组织【{}】查询人员异常：{}", name,e.getMessage());
			}
		}
		matcher = PersistenceProperties.Group.distinguishedName_pattern.matcher(name);
		if(matcher.find()) {
			try {
				Business business = new Business(null);
				return business.organization().person().listWithGroup( name );
			} catch (Exception e) {
				logger.warn("根据组织【{}】查询人员异常：{}", name,e.getMessage());
			}
		}
		return list;
	}

	/**
	 * 根据组织名称，查询组织内所有的人员标识，包括下级组织<br/>
	 * 2020-06-12 改为使用唯一标识查询<br/>
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public List<String> listPersonWithUnit(String unitName) throws Exception {
		if (StringUtils.isEmpty( unitName )) {
			throw new Exception("unitName is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			//2020-06-12 unitName可能有3段，可能有2段，统一使用中间的唯一标识来进行查询
			String unique = getUniqueWithName( unitName );

			return business.organization().person().listWithUnitSubNested( unique );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 获取组织对象的唯一标识<br/>
	 *<br/>
	 * 组织对象标识一般会有3段，如 综合部@1304-73504398-13419-0347@U, 张三@293041-9305983-04258-0943@P<br/>
	 * 文档权限里也会存在2段，因为第一段经常会变，如组织：行政综合部@1304-73504398-13419-0347@U<br/>
	 * 所以查询的时候最好只用中间的唯一标识来查询<br/>
	 * @param orgObjectName
	 * @return
	 */
	private String getUniqueWithName(String orgObjectName ) {
		if( StringUtils.isNotEmpty( orgObjectName )){
			String[] array = orgObjectName.split("@");
			if( array.length == 3 ){
				return array[1];
			}else if( array.length == 2 ){
				return array[0];
			}else{
				return orgObjectName;
			}
		}
		return null;
	}

	/**
	 * 根据群组名称，查询群组内所有的人员标识<br/>
	 * 2020-06-12 改为使用唯一标识查询<br/>
	 *
	 * @param groupName
	 * @return
	 * @throws Exception
	 */
	public List<String> listPersonWithGroup(String groupName) throws Exception {
		if (StringUtils.isEmpty(groupName)) {
			throw new Exception("groupName is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			//2020-06-12 unitName可能有3段，可能有2段，统一使用中间的唯一标识来进行查询
			String unique = getUniqueWithName( groupName );

			return business.organization().person().listWithGroup( unique );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据角色名称，查询角色成员内所有的人员标识<br/>
	 * 2020-06-12 改为使用唯一标识查询<br/>
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public List<String> listPersonWithRole(String role) throws Exception {
		if (StringUtils.isEmpty(role)) {
			throw new Exception("role is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			//2020-06-12 unitName可能有3段，可能有2段，统一使用中间的唯一标识来进行查询
			String unique = getUniqueWithName( role );

			return business.organization().person().listWithRole( unique );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询系统内所有顶级组织的数量
	 *
	 * @return
	 * @throws Exception
	 */
	public int countTopUnit() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> unitNames = business.organization().unit().listWithLevel(1);
			if (ListTools.isNotEmpty(unitNames)) {
				return unitNames.size();
			}
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/**
	 * 判断指定组织是否是顶级组织
	 *
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public boolean isTopUnit(String unitName) throws Exception {
		if (StringUtils.isEmpty(unitName)) {
			return false;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String[] splitName = unitName.split("@");
			if(splitName.length == 2){
				unitName = business.organization().unit().get(splitName[0]);
			}
			List<String> unitNames = business.organization().unit().listWithLevel(1);
			if (ListTools.isNotEmpty(unitNames) && unitNames.contains(unitName)) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	public boolean hasCategoryManagerPermission( EffectivePerson person, String appId) throws Exception {
		if( person.isManager()){
			return true;
		}
		UserManagerService userManagerService = new UserManagerService();
		//Manager管理员
		if( userManagerService.isHasPlatformRole( person.getDistinguishedName(), ThisApplication.ROLE_Manager )){
			return true;
		}
		//CMS管理员
		if( userManagerService.isHasPlatformRole( person.getDistinguishedName(), ThisApplication.ROLE_CMSManager )){
			return true;
		}

		//查询用户是否为该栏目的管理者
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			//是管理员
			if( ListTools.isNotEmpty(appInfo.getManageablePersonList()) && ListTools.contains( appInfo.getManageablePersonList(), person.getDistinguishedName() )){
				return true;
			}
			if( ListTools.isNotEmpty( appInfo.getManageableUnitList() )){
				List<String> unitNames = userManagerService.listUnitNamesWithPerson( person.getDistinguishedName() );
				if( ListTools.isNotEmpty( unitNames )){
					unitNames.retainAll( appInfo.getManageableUnitList() );
					if( ListTools.isNotEmpty( unitNames )){
						return true;
					}
				}
			}
			if( ListTools.isNotEmpty( appInfo.getManageableGroupList() )){
				List<String> groupNames = userManagerService.listGroupNamesByPerson( person.getDistinguishedName() );
				if( ListTools.isNotEmpty( groupNames )){
					groupNames.retainAll( appInfo.getManageableGroupList() );
					if( ListTools.isNotEmpty( groupNames )){
						return true;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return false;
	}

	public boolean hasAppInfoManagerPermission( EffectivePerson person ) throws Exception {
		//系统管理员
		if( person.isManager() || person.isCipher() ){
			return true;
		}
		if( StringUtils.equalsIgnoreCase("xadmin", person.getName() ) || StringUtils.equalsIgnoreCase("xadmin", person.getDistinguishedName() ) ){
			return true;
		}
		//CMS管理员
		UserManagerService userManagerService = new UserManagerService();
		if( userManagerService.isHasPlatformRole( person.getDistinguishedName(), ThisApplication.ROLE_CMSManager )){
			return true;
		}
		return false;
	}
}
