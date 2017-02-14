package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.organization.core.express.wrap.WrapCompany;
import com.x.organization.core.express.wrap.WrapDepartment;
import com.x.organization.core.express.wrap.WrapGroup;
import com.x.organization.core.express.wrap.WrapIdentity;
import com.x.organization.core.express.wrap.WrapPerson;
import com.x.organization.core.express.wrap.WrapRole;

/**
 * 用户组织公司信息管理服务类
 * @author LIYI
 *
 */
public class UserManagerService {
	
	/**
	 * 根据员工姓名获取部门名称，多个身份只取第一个部门
	 * @param employeeName
	 * @return
	 * @throws Exception 
	 */
	public String getDepartmentNameByEmployeeName( String employeeName ) throws Exception{
		List<WrapIdentity> identities = null;		
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			identities = business.organization().identity().listWithPerson( employeeName );
			if ( identities.size() == 0 ) {//该员工目前没有分配身份
				throw new Exception( "can not get identity of person:" + employeeName + "." );
			} else {
				return identities.get(0).getDepartment();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据员工姓名获取部门名称，多个身份取多个部门
	 * @param employeeName
	 * @return
	 * @throws Exception 
	 */
	public List<String> listDepartmentNameByEmployeeName( String employeeName ) throws Exception{
		List<String> departmentNames = new ArrayList<>();
		List<WrapIdentity> identities = null;		
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			identities = business.organization().identity().listWithPerson( employeeName );
			if ( identities.size() == 0 ) {//该员工目前没有分配身份
				throw new Exception( "can not get identity of person:" + employeeName + "." );
			}
			for( WrapIdentity identity : identities ){
				departmentNames.add( identity.getDepartment() );
			}
		} catch ( Exception e ) {
			throw e;
		}
		return departmentNames;
	}
	
	/**
	 * 根据身份名称获取部门名称
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	public String getDepartmentNameByIdentity( String identity ) throws Exception{	
		Business business = null;
		WrapDepartment wrapDepartment = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			wrapDepartment = business.organization().department().getWithIdentity( identity );
			if ( wrapDepartment == null ) {//该根据身份无法查询到组织信息
				throw new Exception( "can not get organization of identity:" + identity + "." );
			} else {
				return wrapDepartment.getName();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据身份名称获取部门名称
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	public String getCompanyNameByIdentity( String identity ) throws Exception{	
		Business business = null;
		WrapCompany wrapCompany = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			wrapCompany = business.organization().company().getWithIdentity( identity );
			if ( wrapCompany == null ) {//该根据身份无法查询到组织信息
				throw new Exception( "can not get company of identity:" + identity + "." );
			} else {
				return wrapCompany.getName();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 根据员工姓名获取公司名称，多个身份只取第一个身份
	 * @param employeeName
	 * @return
	 * @throws Exception 
	 */
	public String getCompanyNameByEmployeeName( String employeeName ) throws Exception{
		String identity = null;
		List<WrapIdentity> identities = null;
		WrapCompany wrapCompany = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			identities = business.organization().identity().listWithPerson( employeeName );
			if ( identities.size() == 0 ) {//该员工目前没有分配身份
				throw new Exception( "can not get identity of person:" + employeeName + "." );
			} else {
				identity = identities.get(0).getName();
			}
			if( identity != null && !identity.isEmpty() ){
				wrapCompany = business.organization().company().getWithIdentity( identity );
			}
			if( wrapCompany != null ){
				return wrapCompany.getName();
			}
		} catch ( Exception e ) {
			throw e;
		}
		return null;
	}
	
	/**
	 * 根据员工姓名获取公司名称，多个身份取多个公司名称
	 * @param employeeName
	 * @return
	 * @throws Exception 
	 */
	public List<String> listCompanyNameByEmployeeName( String employeeName ) throws Exception{
		List<String> companyNames = new ArrayList<>();
		List<WrapIdentity> identities = null;	
		WrapCompany wrapCompany = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			identities = business.organization().identity().listWithPerson( employeeName );
			if ( identities.size() == 0 ) {
				throw new Exception( "can not get identity of person:" + employeeName + "." );
			}
			for( WrapIdentity identity : identities ){
				wrapCompany = business.organization().company().getWithIdentity( identity.getName() );
				if( wrapCompany != null ){
					companyNames.add( wrapCompany.getName() );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return companyNames;
	}
	
	/**
	 * 根据组织名称获取公司名称
	 * @param organizationName
	 * @return
	 * @throws Exception 
	 */
	public String getCompanyNameByOrganizationName( String organizationName ) throws Exception{
		WrapDepartment wrapDepartment = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			wrapDepartment = business.organization().department().getWithName( organizationName );
			if( wrapDepartment != null ){
				return wrapDepartment.getCompany();
			}else{
				throw new Exception( "can not find company info with organization name:" + organizationName );
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据个人姓名，获取人员的第一个身份
	 * @param personName
	 * @return
	 * @throws Exception 
	 */
	public String getFistIdentityNameByPerson( String personName ) throws Exception {
		List<WrapIdentity> identities = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			identities = business.organization().identity().listWithPerson( personName );
			if ( identities.size() == 0 ) {//该员工目前没有分配身份
				throw new Exception( "can not get identity of person:" + personName + "." );
			} else {
				return identities.get(0).getName();
			}
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据公司名称获取所有下级公司名称列表
	 * @param companyName
	 * @return
	 * @throws Exception 
	 */
	public List<String> listSubCompanyNameList(String companyName) throws Exception {
		Business business = null;
		List<WrapCompany> companyList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			companyList = business.organization().company().listSubNested( companyName );
			if( companyList != null && companyList.size() > 0 ){
				for( WrapCompany company : companyList ){
					nameList.add( company.getName() );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return nameList;
	}

	/**
	 * 根据部门名称获取所有下级部门名称列表
	 * @param query_creatorOrganizationName
	 * @return
	 * @throws Exception 
	 */
	public List<String> listSubOrganizationNameList(String organizationName) throws Exception {
		Business business = null;
		List<WrapDepartment> departmentList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			departmentList = business.organization().department().listSubNested( organizationName );
			if( departmentList != null && departmentList.size() > 0 ){
				for( WrapDepartment department : departmentList ){
					nameList.add( department.getName() );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return nameList;
	}

	/**
	 * 查询当前用户是否有指定的身份信息
	 * @param name
	 * @param loginIdentity
	 * @return
	 * @throws Exception 
	 */
	public boolean hasIdentity( String name, String loginIdentity ) throws Exception {
		if( loginIdentity == null || loginIdentity.isEmpty() ){
			throw new Exception( "loginIdentity is null!" );
		}
		Business business = null;
		WrapPerson wrapPerson = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			wrapPerson = business.organization().person().getWithIdentity( loginIdentity );
			if( wrapPerson != null && wrapPerson.getName().equals( name )){
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
	public WrapPerson getUserNameByIdentity( String identity ) throws Exception {
		if( identity == null || identity.isEmpty() ){
			throw new Exception( "identity is null!" );
		}
		Business business = null;
		WrapPerson wrapPerson = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			wrapPerson = business.organization().person().getWithIdentity( identity );
		} catch ( Exception e ) {
			throw e;
		}
		return wrapPerson;
	}
	
	/**
	 * 根据公司名称查询公司对象
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public WrapCompany getCompanyByName( String name ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		Business business = null;
		WrapCompany company = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			company = business.organization().company().getWithName( name );
		} catch ( Exception e ) {
			throw e;
		}
		return company;
	}
	
	/**
	 * 根据部门名称查询部门对象
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public WrapDepartment getDepartmentByName( String name ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		Business business = null;
		WrapDepartment department = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			department = business.organization().department().getWithName( name );
		} catch ( Exception e ) {
			throw e;
		}
		return department;
	}
	
	/**
	 * 根据人员名称查询人员对象
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public WrapPerson  getPersonByName( String name ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		Business business = null;
		WrapPerson  person  = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			person = business.organization().person().getWithName( name );
		} catch ( Exception e ) {
			throw e;
		}
		return person;
	}
	
	/**
	 * 根据用户姓名查询用户所有的身份信息
	 * @param userName
	 * @return
	 * @throws Exception 
	 */
	public List<WrapIdentity> listUserIdentities(String userName) throws Exception {
		if( userName == null || userName.isEmpty() ){
			throw new Exception( "userName is null!" );
		}
		Business business = null;
		List<WrapIdentity> wrapIdentities = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			wrapIdentities = business.organization().identity().listWithPerson( userName );
		} catch ( Exception e ) {
			throw e;
		}
		return wrapIdentities;
	}

	/**
	 * 根据用户唯一标识来获取用户对象
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public WrapPerson getUserByFlag( String flag ) throws Exception {
		if( flag == null || flag.isEmpty() ){
			throw new Exception( "flag is null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().flag( flag );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	/**
	 * 判断用户是否有指定的平台角色，比如CMS系统管理员
	 * @param name
	 * @param string
	 * @return
	 * @throws Exception 
	 */
	public boolean isHasPlatformRole( String name, String roleName ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		if( roleName == null || roleName.isEmpty() ){
			throw new Exception( "roleName is null!" );
		}
		List<WrapRole> roleList = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			roleList = business.organization().role().listWithPerson( name );
			if( roleList != null && !roleList.isEmpty() ){
				for( WrapRole role : roleList ){
					if( role.getName().equalsIgnoreCase( roleName )){
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

	public List<String> listUserNamesByOrganization( String organizationName ) throws Exception {
		Business business = null;
		List<WrapPerson> personList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			personList = business.organization().person().listWithDepartment( organizationName  );
			if( personList != null && personList.size() > 0 ){
				for( WrapPerson person : personList ){
					nameList.add( person.getName() );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return nameList;
	}

	public List<String> listUserNamesByGroupName( String groupName ) throws Exception {
		Business business = null;
		List<WrapPerson> personList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			personList = business.organization().person().listWithGroupSubNested( groupName );
			if( personList != null && personList.size() > 0 ){
				for( WrapPerson person : personList ){
					nameList.add( person.getName() );
				}
			}
		} catch ( Exception e ) {
			throw e;
		}
		return nameList;
	}

	public WrapGroup getGroupByName( String name ) throws Exception {
		if( name == null || name.isEmpty() ){
			throw new Exception( "name is null!" );
		}
		Business business = null;
		WrapGroup group = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			group = business.organization().group().getWithName( name );
		} catch ( Exception e ) {
			throw e;
		}
		return group;
	}

	public Boolean isXAdmin(HttpServletRequest request, EffectivePerson currentPerson) throws Exception {
		if( request == null ){
			throw new Exception( "request is null!" );
		}
		if( currentPerson == null ){
			throw new Exception( "currentPerson is null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.isXAdmin( request, currentPerson );
		} catch ( Exception e ) {
			throw e;
		}
	}
}