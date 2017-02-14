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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
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
	@Path("list/publish/filter/catagory/{catagoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPublishAppSearchFilter( @Context HttpServletRequest request, @PathParam("catagoryId") String catagoryId ) {		
		ActionResult<WrapOutSearchFilter> result = listAppSearchFilterForDocStatus( request, "published", catagoryId);
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有草稿文档分类列表", response = WrapOutSearchFilter.class)
	@GET
	@Path("list/draft/filter/catagory/{catagoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listDraftAppSearchFilter( @Context HttpServletRequest request, @PathParam("catagoryId") String catagoryId ) {		
		ActionResult<WrapOutSearchFilter> result = listAppSearchFilterForDocStatus( request, "draft", catagoryId);
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取用户有权限访问的所有已归档文档分类列表", response = WrapOutSearchFilter.class)
	@GET
	@Path("list/archive/filter/catagory/{catagoryId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listArchivedAppSearchFilter( @Context HttpServletRequest request, @PathParam("catagoryId") String catagoryId ) {		
		ActionResult<WrapOutSearchFilter> result = listAppSearchFilterForDocStatus( request, "archived", catagoryId );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 从文档信息中查询出涉及的所有栏目信息，分类信息，以及部门和公司信息列表
	 * @param request
	 * @param docStatus
	 * @param catagoryId
	 * @return
	 */
	private ActionResult<WrapOutSearchFilter> listAppSearchFilterForDocStatus( HttpServletRequest request, String docStatus, String catagoryId ){
		ActionResult<WrapOutSearchFilter> result = new ActionResult<>();
		WrapOutSearchFilter wrap = new WrapOutSearchFilter();
		List<String> app_ids = null;
		List<AppFilter> appFilterList = null;
		List<CatagoryFilter> catagoryFilterList = null;
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
				result.error(e);
				result.setUserMessage("系统在检查用户是否为平台管理员时发生异常。");
				logger.error( "system check user is xadmin got an exception", e );
			}
		}
		if( check ){
			//查询用户可以访问的所有栏目ID列表
			if( isXAdmin ){
				try {
					app_ids = appInfoServiceAdv.listAllIds();
				} catch (Exception e) {
					check = false;
					result.error(e);
					result.setUserMessage("系统在查询所有栏目信息ID列表时发生异常。");
					logger.error( "system query all appinfo ids got an exception", e );
				}
			}else{
				try {
					app_ids = appInfoServiceAdv.listAppInfoByUserPermission( currentPerson.getName() );
				} catch (Exception e) {
					check = false;
					result.error(e);
					result.setUserMessage("系统在根据用户权限查询用户可访问的所有栏目信息ID列表时发生异常。");
					logger.error( "system query app info ids with uer permission got an exception", e );
				}
			}
		}
		//1、获取栏目统计列表
		if( check ){
			//查询用户可访问的文档涉及到的所有栏目信息以及各栏目内文档数量
			try {
				appFilterList = searchServiceAdv.listAppInfoSearchFilter( app_ids, docStatus, catagoryId );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据可访问栏目ID列表，文档状态以及可访问分类ID统计涉及到的所有栏目名称列表时发生异常。");
				logger.error( "system qeury appNames with appids, docstatus and catagoryid got an exception", e );
			}
		}
		//2、获取分类统计列表
		if( check ){
			try {
				catagoryFilterList = searchServiceAdv.listCatagorySearchFilter( app_ids, docStatus, catagoryId );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据可访问栏目ID列表，文档状态以及可访问分类ID统计涉及到的所有分类名称列表时发生异常。");
				logger.error( "system qeury catagoryNames with appids, docstatus and catagoryid got an exception", e );
			}
		}
		if( check ){
			try {
				companyFilterList = searchServiceAdv.listCompanySearchFilter( app_ids, docStatus, catagoryId );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据可访问栏目ID列表，文档状态以及可访问分类ID统计涉及到的所有公司名称列表时发生异常。");
				logger.error( "system qeury companyNames with appids, docstatus and catagoryid got an exception", e );
			}
		}
		if( check ){
			try {
				departmentFilterList = searchServiceAdv.listDepartmentSearchFilter( app_ids, docStatus, catagoryId );
			} catch (Exception e) {
				check = false;
				result.error(e);
				result.setUserMessage("系统在根据可访问栏目ID列表，文档状态以及可访问分类ID统计涉及到的所有部门名称列表时发生异常。");
				logger.error( "system qeury departmentNames with appids, docstatus and catagoryid got an exception", e );
			}
		}
		if( check ){
			wrap.setAppfileter_list( appFilterList );
			wrap.setCatagoryfileter_list( catagoryFilterList );
			wrap.setCompanyfileter_list( companyFilterList );
			wrap.setDepartmentfileter_list( departmentFilterList );
			result.setData(wrap);
		}
		return result;
	}
}