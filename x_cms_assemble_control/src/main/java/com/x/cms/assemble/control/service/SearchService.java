package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.utils.annotation.MethodDescribe;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.search.AppFilter;
import com.x.cms.assemble.control.jaxrs.search.CatagoryFilter;
import com.x.cms.assemble.control.jaxrs.search.CompanyFilter;
import com.x.cms.assemble.control.jaxrs.search.DepartmentFilter;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CatagoryInfo;

public class SearchService {

	@MethodDescribe("根据用户列出所有可见的已发布文档涉及的应用栏目以及文档数量")
	public List<AppFilter> listAppInfoSearchFilter(EntityManagerContainer emc, List<String> app_ids, String docStatus, String catagoryId) throws Exception {
		if( app_ids == null ){
			return null;
		}
		List<AppFilter> appFilterList = new ArrayList<AppFilter>();
		AppFilter appFilter = null;
		List<String> appids_user = null;
		AppInfo appInfo = null;
		Business business = new Business( emc );
		//先distinct用户可以访问的文档涉及到多少个可以访问的应用栏目
		appids_user = business.getSearchFactory().listDistinctAppInfoFromDocument( app_ids, docStatus, catagoryId );
		if( appids_user != null && appids_user.size() > 0 ){
			for( String appid : appids_user ){
				appInfo = emc.find( appid, AppInfo.class );
				if( appInfo != null ){
					appFilter = new AppFilter(
							appInfo.getId(), appInfo.getAppName(), business.getSearchFactory().getAppInfoDocumentCount( app_ids, appid, docStatus, catagoryId )
						);
						appFilterList.add( appFilter );
				}
			}
		}		
		return appFilterList;
	}

	@MethodDescribe("根据用户列出所有可见的已发布文档涉及的应用分类以及文档数量")
	public List<CatagoryFilter> listCatagorySearchFilter( EntityManagerContainer emc, List<String> app_ids,
			String docStatus, String catagoryId) throws Exception {
		if( app_ids == null ){
			return null;
		}
		List<CatagoryFilter> catagoryFilterList = new ArrayList<CatagoryFilter>();
		CatagoryFilter catagoryFilter = null;
		List<String> catagoryids_user = null;
		CatagoryInfo catagoryInfo = null;
		AppInfo appInfo = null;
		Business business = new Business( emc );
		//先distinct用户可以访问的文档涉及到多少个可以访问的分类
		catagoryids_user = business.getSearchFactory().listDistinctCatagoryFromDocument( app_ids, docStatus, catagoryId );
		if( catagoryids_user != null && catagoryids_user.size() > 0 ){
			for( String _catagoryId : catagoryids_user ){
				catagoryInfo = emc.find( _catagoryId, CatagoryInfo.class );
				if( catagoryInfo != null ){
					appInfo = emc.find( catagoryInfo.getAppId(), AppInfo.class );
					if( appInfo != null ){
						catagoryFilter = new CatagoryFilter(
								appInfo.getId(), appInfo.getAppName(), catagoryInfo.getId(), catagoryInfo.getCatagoryName(), 
								business.getSearchFactory().getCatagoryInfoDocumentCount( app_ids, _catagoryId, docStatus )
						);
						catagoryFilterList.add( catagoryFilter );
					}
				}
			}
		}		
		return catagoryFilterList;
	}
	
	@MethodDescribe("根据用户列出所有可见的已发布文档涉及的拟稿公司以及文档数量")
	public List<CompanyFilter> listCompanySearchFilter(EntityManagerContainer emc, List<String> app_ids,
			String docStatus, String catagoryId) throws Exception {
		if( app_ids == null ){
			return null;
		}
		List<CompanyFilter> companyFilterList = new ArrayList<CompanyFilter>();
		CompanyFilter companyFilter = null;
		List<String> companyids = null;
		Business business = new Business( emc );
		//先distinct用户可以访问的文档涉及到多少个可以访问的分类
		companyids = business.getSearchFactory().listDistinctCompanyFromDocument( app_ids, docStatus, catagoryId);
		if( companyids != null && companyids.size() > 0 ){
			for( String creatorCompany : companyids ){
				//循环进行数据统计				
				companyFilter = new CompanyFilter(
						creatorCompany, creatorCompany, business.getSearchFactory().getCompanyDocumentCount( app_ids, creatorCompany, docStatus, catagoryId)
				);
				companyFilterList.add( companyFilter );
			}
		}		
		return companyFilterList;
	}

	@MethodDescribe("根据用户列出所有可见的已发布文档涉及的拟稿部门以及文档数量")
	public List<DepartmentFilter> listDepartmentSearchFilter(EntityManagerContainer emc, List<String> app_ids,
			String docStatus, String catagoryId) throws Exception {
		if( app_ids == null ){
			return null;
		}
		List<DepartmentFilter> departmentFilterList = new ArrayList<DepartmentFilter>();
		DepartmentFilter departmentFilter = null;
		List<String> departments = null;
		Business business = new Business( emc );
		//先distinct用户可以访问的文档涉及到多少个可以访问的分类
		departments = business.getSearchFactory().listDistinctDepartmentyFromDocument( app_ids, docStatus, catagoryId);
		if( departments != null && departments.size() > 0 ){
			for( String creatorDepartment : departments ){
				//循环进行数据统计				
				departmentFilter = new DepartmentFilter(
						creatorDepartment, creatorDepartment, business.getSearchFactory().getDeparmentyDocumentCount( app_ids, creatorDepartment, docStatus, catagoryId)
				);
				departmentFilterList.add( departmentFilter );
			}
		}		
		return departmentFilterList;
	}

}
