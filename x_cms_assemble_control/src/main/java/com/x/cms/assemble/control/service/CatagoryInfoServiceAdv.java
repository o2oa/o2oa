package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.CatagoryInfo;

public class CatagoryInfoServiceAdv {
	
	private Logger logger = LoggerFactory.getLogger( CatagoryInfoServiceAdv.class );
	private LogService logService = new LogService();
	private UserManagerService userManagerService = new UserManagerService();
	private CatagoryInfoService catagoryInfoService = new CatagoryInfoService();
	private AppCatagoryAdminService appCatagoryAdminService = new AppCatagoryAdminService();
	private AppCatagoryPermissionService appCatagoryPermissionService = new AppCatagoryPermissionService();
	
	public List<String> listByAppId( String appId ) throws Exception {
		if( appId == null || appId.isEmpty() ){
			throw new Exception("appId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return catagoryInfoService.listByAppId( emc, appId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 根据栏目ID以及用户的权限获取用户可访问所的所有分类ID列表
	 * 1、栏目下所有未设置访问权限的（全员可以访问的Catagory）
	 * 2、该用户自己为管理员的所有Catagory
	 * 3、该用户以及用户所在的部门，公司有权限访问的所有Catagory
	 * @param appId
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	public List<String> listByAppIdAndUserPermission( String appId, String name ) throws Exception {
		if( appId == null || appId.isEmpty() ){
			throw new Exception("appId is null!");
		}
		if( name == null || name.isEmpty() ){
			throw new Exception("name is null!");
		}
		List<String> viewAbleCatagoryIds = new ArrayList<>();
		List<String> catagoryIds = null;
		List<String> departmentNames = null;
		List<String> companyNames = null;
		//1、在指定APPID下所有未设置访问权限的（全员可以访问的Catagory）
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			catagoryIds = catagoryInfoService.listNoPermissionCatagoryIds( emc, appId );
			if (catagoryIds != null && !catagoryIds.isEmpty()) {
				for (String id : catagoryIds) {
					if (!viewAbleCatagoryIds.contains(id)) {
						viewAbleCatagoryIds.add(id);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		// 2、该用户自己为管理员的所有Catagory
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			catagoryIds = appCatagoryAdminService.listCatagoryInfoIdsByAdminName(emc, name, appId );
			if (catagoryIds != null && !catagoryIds.isEmpty()) {
				for (String id : catagoryIds) {
					if (!viewAbleCatagoryIds.contains(id)) {
						viewAbleCatagoryIds.add(id);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		// 3、该用户以及用户所在的部门，公司有权限访问的所有Catagory
		departmentNames = userManagerService.listDepartmentNameByEmployeeName(name);
		companyNames = userManagerService.listCompanyNameByEmployeeName(name);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			catagoryIds = appCatagoryPermissionService.listCatagoryIdsByPermission(emc, name, departmentNames, companyNames, appId );
			if ( catagoryIds != null && !catagoryIds.isEmpty()) {
				for ( String id : catagoryIds ) {
					if (!viewAbleCatagoryIds.contains(id)) {
						viewAbleCatagoryIds.add(id);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return viewAbleCatagoryIds;
	}

	public List<CatagoryInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.isEmpty() ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return catagoryInfoService.list( emc, ids );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public CatagoryInfo get( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return catagoryInfoService.get( emc, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<CatagoryInfo> listAll() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return catagoryInfoService.listAll( emc );
		} catch ( Exception e ) {
			throw e;
		}
	}
	
	public CatagoryInfo saveBaseInfo( CatagoryInfo catagoryInfo, EffectivePerson currentPerson) throws Exception {
		if( catagoryInfo == null ){
			throw new Exception( "catagoryInfo is null." );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			catagoryInfo = catagoryInfoService.saveBaseInfo( emc, catagoryInfo );
			logService.log( emc, currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功更新一个分类基础信息", catagoryInfo.getId(), "", "", "", "CATAGORY", "更新");
		} catch ( Exception e ) {
			throw e;
		}
		return catagoryInfo;
	}
	
	public CatagoryInfo save( CatagoryInfo catagoryInfo, EffectivePerson currentPerson ) throws Exception {
		if( catagoryInfo == null ){
			throw new Exception( "catagoryInfo is null." );
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			catagoryInfo = catagoryInfoService.save( emc, catagoryInfo );
			logService.log( emc, currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功创建一个分类信息", catagoryInfo.getId(), "", "", "", "CATAGORY", "新增");
		} catch ( Exception e ) {
			throw e;
		}
		return catagoryInfo;
	}

	public Boolean catagoryInfoEditAvailable(HttpServletRequest request, EffectivePerson currentPerson, String id) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			return business.catagoryInfoEditAvailable( request, currentPerson, id );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public void delete( String id, EffectivePerson currentPerson) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			catagoryInfoService.delete( emc, id );
			logService.log( emc, currentPerson.getName(), "用户[" + currentPerson.getName() + "]成功删除一个分类信息", id, "", "", "", "CATAGORY", "删除");
		} catch ( Exception e ) {
			throw e;
		}
	}
}
