package com.x.cms.assemble.control.jaxrs.search;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.jaxrs.search.exception.AppInfoFilterListException;
import com.x.cms.assemble.control.jaxrs.search.exception.AppInfoIdsListAllException;
import com.x.cms.assemble.control.jaxrs.search.exception.AppInfoListViewableInPermissionException;
import com.x.cms.assemble.control.jaxrs.search.exception.CategoryInfoFilterListException;
import com.x.cms.assemble.control.jaxrs.search.exception.CompanyNameFilterListException;
import com.x.cms.assemble.control.jaxrs.search.exception.DepartmentNameFilterListException;
import com.x.cms.assemble.control.jaxrs.search.exception.UserManagerCheckException;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.SearchServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;

@Path("searchfilter")
public class SearchFilterAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( SearchFilterAction.class );
	private AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	private SearchServiceAdv searchServiceAdv = new SearchServiceAdv();
	private UserManagerService userManagerService = new UserManagerService();
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有已发布文档分类列表", response = WrapOutSearchFilter.class)
	@GET
	@Path("list/publish/filter/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPublishAppSearchFilter( @Context HttpServletRequest request, @PathParam("categoryId") String categoryId ) {		
		ActionResult<WrapOutSearchFilter> result = listAppSearchFilterForDocStatus( request, "published", categoryId);
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有草稿文档分类列表", response = WrapOutSearchFilter.class)
	@GET
	@Path("list/draft/filter/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftAppSearchFilter( @Context HttpServletRequest request, @PathParam("categoryId") String categoryId ) {		
		ActionResult<WrapOutSearchFilter> result = listAppSearchFilterForDocStatus( request, "draft", categoryId);
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有已归档文档分类列表", response = WrapOutSearchFilter.class)
	@GET
	@Path("list/archive/filter/category/{categoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listArchivedAppSearchFilter( @Context HttpServletRequest request, @PathParam("categoryId") String categoryId ) {		
		ActionResult<WrapOutSearchFilter> result = listAppSearchFilterForDocStatus( request, "archived", categoryId );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 从文档信息中查询出涉及的所有栏目信息，分类信息，以及部门和公司信息列表
	 * @param request
	 * @param docStatus
	 * @param categoryId
	 * @return
	 */
	private ActionResult<WrapOutSearchFilter> listAppSearchFilterForDocStatus( HttpServletRequest request, String docStatus, String categoryId ){
		ActionResult<WrapOutSearchFilter> result = new ActionResult<>();
		WrapOutSearchFilter wrap = new WrapOutSearchFilter();
		List<String> app_ids = null;
		List<AppFilter> appFilterList = null;
		List<CategoryFilter> categoryFilterList = null;
		List<CompanyFilter> companyFilterList = null;
		List<DepartmentFilter> departmentFilterList = null;
		Boolean isXAdmin = false;
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson(request);
		
		if( check ){
			try {
				isXAdmin = userManagerService.isXAdmin( request, currentPerson );
			} catch (Exception e) {
				check = false;
				Exception exception = new UserManagerCheckException( e, currentPerson.getName() );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		if( check ){
			if( isXAdmin ){
				try {
					app_ids = appInfoServiceAdv.listAllIds();
				} catch (Exception e) {
					check = false;
					Exception exception = new AppInfoIdsListAllException( e );
					result.error( exception );
					logger.error( e, currentPerson, request, null);
				}
			}else{
				try {
					app_ids = appInfoServiceAdv.listViewableAppInfoByUserPermission( currentPerson.getName() );
				} catch (Exception e) {
					check = false;
					Exception exception = new AppInfoListViewableInPermissionException( e, currentPerson.getName() );
					result.error( exception );
					logger.error( e, currentPerson, request, null);
				}
			}
		}
		//1、获取栏目统计列表
		if( check ){
			//查询用户可访问的文档涉及到的所有栏目信息以及各栏目内文档数量
			try {
				appFilterList = searchServiceAdv.listAppInfoSearchFilter( app_ids, docStatus, categoryId );
			} catch (Exception e) {
				check = false;
				Exception exception = new AppInfoFilterListException( e, app_ids, docStatus, categoryId );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		//2、获取分类统计列表
		if( check ){
			try {
				categoryFilterList = searchServiceAdv.listCategorySearchFilter( app_ids, docStatus, categoryId );
			} catch (Exception e) {
				check = false;
				Exception exception = new CategoryInfoFilterListException( e, app_ids, docStatus, categoryId );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				companyFilterList = searchServiceAdv.listCompanySearchFilter( app_ids, docStatus, categoryId );
			} catch (Exception e) {
				check = false;
				Exception exception = new CompanyNameFilterListException( e, app_ids, docStatus, categoryId );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		if( check ){
			try {
				departmentFilterList = searchServiceAdv.listDepartmentSearchFilter( app_ids, docStatus, categoryId );
			} catch (Exception e) {
				check = false;
				Exception exception = new DepartmentNameFilterListException( e, app_ids, docStatus, categoryId );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		if( check ){
			wrap.setAppfileter_list( appFilterList );
			wrap.setCategoryfileter_list( categoryFilterList );
			wrap.setCompanyfileter_list( companyFilterList );
			wrap.setDepartmentfileter_list( departmentFilterList );
			result.setData(wrap);
		}
		return result;
	}
}