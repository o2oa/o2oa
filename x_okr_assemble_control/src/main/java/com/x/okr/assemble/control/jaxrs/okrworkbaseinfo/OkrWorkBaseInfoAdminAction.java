package com.x.okr.assemble.control.jaxrs.okrworkbaseinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.application.jaxrs.InTerms;
import com.x.base.core.application.jaxrs.LikeTerms;
import com.x.base.core.application.jaxrs.MemberTerms;
import com.x.base.core.application.jaxrs.NotEqualsTerms;
import com.x.base.core.application.jaxrs.NotInTerms;
import com.x.base.core.application.jaxrs.NotMemberTerms;
import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.SortTools;
import com.x.okr.assemble.control.jaxrs.okrcenterworkinfo.WrapOutOkrCenterWorkInfo;
import com.x.okr.assemble.control.jaxrs.okrworkauthorizerecord.WrapOutOkrWorkAuthorizeRecord;
import com.x.okr.assemble.control.service.OkrCenterWorkInfoService;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkAuthorizeRecordService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoService;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.entity.OkrCenterWorkInfo;
import com.x.okr.entity.OkrWorkAuthorizeRecord;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;

/**
 * 具体工作项有短期工作还长期工作，短期工作不需要自动启动定期汇报，由人工撰稿汇报即可
 */

@Path( "admin/okrworkbaseinfo" )
public class OkrWorkBaseInfoAdminAction extends StandardJaxrsAction{	
	private Logger logger = LoggerFactory.getLogger( OkrWorkBaseInfoAdminAction.class );
	private BeanCopyTools<OkrWorkBaseInfo, WrapOutOkrWorkBaseInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkBaseInfo.class, WrapOutOkrWorkBaseInfo.class, null, WrapOutOkrWorkBaseInfo.Excludes);
	private BeanCopyTools<OkrCenterWorkInfo, WrapOutOkrCenterWorkInfo> okrCenterWorkInfo_wrapout_copier = BeanCopyToolsBuilder.create( OkrCenterWorkInfo.class, WrapOutOkrCenterWorkInfo.class, null, WrapOutOkrCenterWorkInfo.Excludes);
	private BeanCopyTools<OkrWorkAuthorizeRecord, WrapOutOkrWorkAuthorizeRecord> okrWorkAuthorizeRecord_wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkAuthorizeRecord.class, WrapOutOkrWorkAuthorizeRecord.class, null, WrapOutOkrWorkAuthorizeRecord.Excludes);
	private OkrCenterWorkInfoService okrCenterWorkInfoService = new OkrCenterWorkInfoService();
	private OkrWorkAuthorizeRecordService okrWorkAuthorizeRecordService = new OkrWorkAuthorizeRecordService();
	private OkrWorkBaseInfoService okrWorkBaseInfoService = new OkrWorkBaseInfoService();
	private OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();

	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[草稿],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = WrapInAdminFilter.class)
	@PUT
	@Path( "filter/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInAdminFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		Boolean check = true;
		
//		EffectivePerson currentPerson = this.effectivePerson(request);
//		Organization organization = new Organization();
//		Boolean hasPermission = false;
//		try {
//			hasPermission = organization.role().hasAny(currentPerson.getName(),"OkrSystemAdmin" );
//			if( !hasPermission ){
//				check = false;
//				result.error( new Exception("用户未拥有操作权限[OkrSystemAdmin]！") );
//				result.setUserMessage( "用户未拥有操作权限[OkrSystemAdmin]！" );
//			}
//		} catch (Exception e) {
//			logger.error( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!", e );
//			check = false;
//			result.error( e );
//			result.setUserMessage( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!" );
//		}
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception( "请求传入的参数为空，无法继续保存工作信息!" ) );
				result.setUserMessage( "请求传入的参数为空，无法继续保存工作信息!" );
			}
		}
		if( check ){
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "title", wrapIn.getFilterLikeContent() );
				likesMap.put( "shortWorkDetail", wrapIn.getFilterLikeContent() );
				likesMap.put( "centerTitle", wrapIn.getFilterLikeContent() );
				likesMap.put( "creatorIdentity", wrapIn.getFilterLikeContent() );
				likesMap.put( "workType", wrapIn.getFilterLikeContent() );
				likesMap.put( "responsibilityEmployeeName", wrapIn.getFilterLikeContent() );
				likesMap.put( "workProcessStatus", wrapIn.getFilterLikeContent() );
			}
		}
		if( check ){
			sequenceField = wrapIn.getSequenceField();
			try{
				result = this.standardListNext( wrapout_copier, id, count, sequenceField,  equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
						membersMap, notMembersMap, false, wrapIn.getOrder() );
			}catch(Throwable th){
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件查询的OkrWorkBaseInfo[草稿],下一页.", response = WrapOutOkrCenterWorkInfo.class, request = WrapInAdminFilter.class)
	@PUT
	@Path( "filter/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterListPrevWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInAdminFilter wrapIn ) {
		ActionResult<List<WrapOutOkrWorkBaseInfo>> result = new ActionResult<>();
		String sequenceField = null;
		EqualsTerms equalsMap = new EqualsTerms();
		NotEqualsTerms notEqualsMap = new NotEqualsTerms();
		InTerms insMap = new InTerms();
		NotInTerms notInsMap = new NotInTerms();
		MemberTerms membersMap = new MemberTerms();
		NotMemberTerms notMembersMap = new NotMemberTerms();
		LikeTerms likesMap = new LikeTerms();
		Boolean check = true;

//		EffectivePerson currentPerson = this.effectivePerson(request);
//		Organization organization = new Organization();
//		Boolean hasPermission = false;
//		try {
//			hasPermission = organization.role().hasAny(currentPerson.getName(),"OkrSystemAdmin" );
//			if( !hasPermission ){
//				check = false;
//				result.error( new Exception("用户未拥有操作权限[OkrSystemAdmin]！") );
//				result.setUserMessage( "用户未拥有操作权限[OkrSystemAdmin]！" );
//			}
//		} catch (Exception e) {
//			logger.error( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!", e );
//			check = false;
//			result.error( e );
//			result.setUserMessage( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!" );
//		}
		if( check ){
			if( wrapIn == null ){
				check = false;
				result.error( new Exception( "请求传入的参数为空，无法继续保存工作信息!" ) );
				result.setUserMessage( "请求传入的参数为空，无法继续保存工作信息!" );
			}
		}
		if( check ){
			if( wrapIn.getFilterLikeContent() != null && !wrapIn.getFilterLikeContent().isEmpty() ){
				likesMap.put( "title", wrapIn.getFilterLikeContent() );
				likesMap.put( "shortWorkDetail", wrapIn.getFilterLikeContent() );
				likesMap.put( "centerTitle", wrapIn.getFilterLikeContent() );
				likesMap.put( "creatorIdentity", wrapIn.getFilterLikeContent() );
				likesMap.put( "workType", wrapIn.getFilterLikeContent() );
				likesMap.put( "responsibilityEmployeeName", wrapIn.getFilterLikeContent() );
				likesMap.put( "workProcessStatus", wrapIn.getFilterLikeContent() );
			}
		}
		if( check ){
			sequenceField = wrapIn.getSequenceField();
			try{
				result = this.standardListNext( wrapout_copier, id, count, sequenceField,  equalsMap, notEqualsMap, likesMap, insMap, notInsMap, 
						membersMap, notMembersMap, true, wrapIn.getOrder() );
			}catch(Throwable th){
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取OkrWorkBaseInfo对象.", response = WrapOutOkrWorkBaseInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		WrapOutOkrWorkBaseInfo wrap = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		List<String> ids = null;
		List<OkrWorkAuthorizeRecord> okrWorkAuthorizeRecordList = null;
		Boolean check = true;

//		EffectivePerson currentPerson = this.effectivePerson(request);
//		Organization organization = new Organization();
//		Boolean hasPermission = false;
//		try {
//			hasPermission = organization.role().hasAny(currentPerson.getName(),"OkrSystemAdmin" );
//			if( !hasPermission ){
//				check = false;
//				result.error( new Exception("用户未拥有操作权限[OkrSystemAdmin]！") );
//				result.setUserMessage( "用户未拥有操作权限[OkrSystemAdmin]！" );
//			}
//		} catch (Exception e) {
//			logger.error( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!", e );
//			check = false;
//			result.error( e );
//			result.setUserMessage( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!" );
//		}
		if(check){
			if( id == null || id.isEmpty() ){
				check = false;
				result.setUserMessage( "传入的参数ID为空，无法查询数据!" );
				logger.error( "id is null, system can not get any object." );
			}
		}
		if(check){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get(id);
			} catch (Exception e) {
				check = false;
				logger.error( "system get okrWorkBaseInfo by id get an exception", e );
				result.setUserMessage( "系统根据ID查询工作信息时发生异常。" );
				result.error(e);
			}
		}
		if(check){
			if( okrWorkBaseInfo != null ){
				try {
					wrap = wrapout_copier.copy( okrWorkBaseInfo );
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					result.setUserMessage( "系统将工作信息转换为输出格式时发生异常。" );
					logger.error( "system copy okrWorkBaseInfo to wrap info got an exception." );
					result.error( e );
				}
			}else{
				check = false;
				result.setUserMessage( "系统未能查询到指定ID的工作信息。" );
				logger.error( "okrWorkBaseInfo{'id':'"+id+"'} not exists." );
				result.error( new Exception("系统未能查询到指定ID的工作信息") );
			}
		}
		if(check){
			try {
				okrWorkDetailInfo = okrWorkDetailInfoService.get( id );
			} catch ( Exception e) {
				check = false;
				logger.error( "system get okrWorkDetailInfo by id get an exception", e );
				result.error( e );
				result.setUserMessage( "系统根据工作ID查询工作详细信息时发生异常。" );
			}
		}		
		if( check ){
			//获取该工作所有的授权信息
			try {
				ids = okrWorkAuthorizeRecordService.listByWorkId( id );
			} catch (Exception e) {
				check = false;
				logger.error( "system list okrWorkAuthorizeRecord ids by work id get an exception", e );
				result.error( e );
				result.setUserMessage( "根据工作ID获取该工作所有的授权信息ID时发生异常。" );
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					okrWorkAuthorizeRecordList = okrWorkAuthorizeRecordService.list( ids );
				} catch (Exception e) {
					check = false;
					logger.error( "system list okrWorkAuthorizeRecord by ids get an exception", e );
					result.error( e );
					result.setUserMessage( "根据ID列表获取授权信息列表时发生异常。" );
				}
			}		
		}		
		if( check ){
			if( okrWorkAuthorizeRecordList != null ){
				try {
					wrap.setOkrWorkAuthorizeRecords( okrWorkAuthorizeRecord_wrapout_copier.copy( okrWorkAuthorizeRecordList ) );
				} catch (Exception e) {
					check = false;
					logger.error( "system copy okrWorkAuthorizeRecord list to wraps get an exception", e );
					result.error( e );
					result.setUserMessage( "系统将工作授权信息列表转换为输出格式列表时发生异常。" );
				}
			}
		}		
		if( check ){
			if( okrWorkDetailInfo != null ){
				wrap.setWorkDetail( okrWorkDetailInfo.getWorkDetail() );
				wrap.setDutyDescription( okrWorkDetailInfo.getDutyDescription() );
				wrap.setLandmarkDescription( okrWorkDetailInfo.getLandmarkDescription() );
				wrap.setMajorIssuesDescription( okrWorkDetailInfo.getMajorIssuesDescription() );
				wrap.setProgressAction( okrWorkDetailInfo.getProgressAction() );
				wrap.setProgressPlan( okrWorkDetailInfo.getProgressPlan() );
				wrap.setResultDescription( okrWorkDetailInfo.getResultDescription() );
				result.setData(wrap);
			}else{
				logger.error( "system can not get any okrWorkDetailInfo by {'id':'"+id+"'}. " );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID强制删除OkrWorkBaseInfo数据对象.", response = WrapOutOkrWorkBaseInfo.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteForce( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		ActionResult<WrapOutOkrWorkBaseInfo> result = new ActionResult<>();
		OkrWorkBaseInfo okrWorkBaseInfo = null;
//		OkrUserCache  okrUserCache  = null;
		Boolean check = true;

		EffectivePerson currentPerson = this.effectivePerson(request);
//		Organization organization = new Organization();
//		Boolean hasPermission = false;
//		try {
//			hasPermission = organization.role().hasAny(currentPerson.getName(),"OkrSystemAdmin" );
//			if( !hasPermission ){
//				check = false;
//				result.error( new Exception("用户未拥有操作权限[OkrSystemAdmin]！") );
//				result.setUserMessage( "用户未拥有操作权限[OkrSystemAdmin]！" );
//			}
//		} catch (Exception e) {
//			logger.error( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!", e );
//			check = false;
//			result.error( e );
//			result.setUserMessage( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!" );
//		}
//		if( check ){
//			try {
//				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
//			} catch (Exception e1) {
//				check = false;
//				result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
//				result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
//				logger.error( "system get login indentity with person name got an exception", e1 );
//			}	
//		}
//		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
//			check = false;
//			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
//			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
//			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
//		}
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.setUserMessage( "传入的ID为空，无法删除任何工作信息。" );
				result.error( new Exception( "传入的ID为空，无法删除任何工作信息。") ); 
				logger.error( "id is null, system can not delete any object." );
			}
		}
		if( check ){
			try{
				okrWorkBaseInfo = okrWorkBaseInfoService.get( id );
			}catch(Exception e){
				check = false;
				logger.error( "system get okrWorkBaseInfo by id get an exception, {'id':'"+id+"'}", e );
				result.setUserMessage( "删除工作信息数据过程中发生异常。" );
				result.error( e );
			}
		}
		if( check ){
			if( okrWorkBaseInfo == null ){
				check = false;
				logger.error( "okrWorkBaseInfo is not exists, {'id':'"+id+"'}" );
				result.setUserMessage( "需要删除的工作信息不存在。" );
				result.error( new Exception("需要删除的工作信息不存在") );
			}
		}
		if( check ){
			try{
				okrWorkBaseInfoService.deleteForce( id );
				result.setUserMessage( "成功删除工作信息数据信息。id=" + id );
			}catch(Exception e){
				check = false;
				logger.error( "system delete okrWorkBaseInfoService get an exception, {'id':'"+id+"'}", e );
				result.setUserMessage( "删除工作信息数据过程中发生异常。" );
				result.error( e );
			}
		}
		if( check ){
			try{
				okrWorkDynamicsService.workDynamic(
						okrWorkBaseInfo.getCenterId(), 
						okrWorkBaseInfo.getId(),
						okrWorkBaseInfo.getTitle(),
						"删除具体工作", 
						currentPerson.getName(), 
						currentPerson.getName(), 
						currentPerson.getName() , 
						"删除具体工作：" + okrWorkBaseInfo.getTitle(), 
						"具体工作删除成功！"
				);
			}catch(Exception e){
				logger.error( "system record workDynamic get an exception", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	@HttpMethodDescribe( value = "根据中心工作ID获取我可以看到的所有OkrWorkBaseInfo对象.", response = WrapOutOkrWorkBaseInfo.class )
	@GET
	@Path( "center/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listWorkInCenter( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		ActionResult<WrapOutOkrCenterWorkInfo> result = new ActionResult<WrapOutOkrCenterWorkInfo>();
		List<WrapOutOkrWorkBaseInfo> all_wrapWorkBaseInfoList = null;
		List<OkrWorkBaseInfo> all_workBaseInfoList = null;
		WrapOutOkrCenterWorkInfo wrapOutOkrCenterWorkInfo = null;
		OkrCenterWorkInfo okrCenterWorkInfo  = null;
		Boolean check = true;

//		EffectivePerson currentPerson = this.effectivePerson(request);
//		Organization organization = new Organization();
//		Boolean hasPermission = false;
//		try {
//			hasPermission = organization.role().hasAny(currentPerson.getName(),"OkrSystemAdmin" );
//			if( !hasPermission ){
//				check = false;
//				result.error( new Exception("用户未拥有操作权限[OkrSystemAdmin]！") );
//				result.setUserMessage( "用户未拥有操作权限[OkrSystemAdmin]！" );
//			}
//		} catch (Exception e) {
//			logger.error( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!", e );
//			check = false;
//			result.error( e );
//			result.setUserMessage( "判断用户是否拥有操作权限[OkrSystemAdmin]时发生异常!" );
//		}
		if( check ){
			try{
				okrCenterWorkInfo = okrCenterWorkInfoService.get( id );//查询中心工作信息是否存在
			}catch( Exception e ){
				check = false;
				logger.error( "system filter okrWorkBaseInfo got an exception.", e );
				result.setUserMessage("系统在根据ID查询中心工作信息时发生异常。");
				result.error( e );
			}
		}
		if( check ){
			if( okrCenterWorkInfo == null ){
				check = false;
				result.error( new Exception( "中心工作不存在！" ));
				result.setUserMessage( "中心工作不存在！" );
				logger.error( "center work{'id':'" + id + "'} is not exists." );
			}
		}
		if( check ){
			try {
				wrapOutOkrCenterWorkInfo = okrCenterWorkInfo_wrapout_copier.copy( okrCenterWorkInfo );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在将中心工作信息转化为输出格式时发生异常！" );
				logger.error( "system copy center work info to wrap got an exception.", e );
			}
		}
		if( check ){
			//获取到该中心工作下所有的工作信息
			try {
				all_workBaseInfoList = okrWorkBaseInfoService.listWorkInCenter( id, null );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据中心工作ID查询所有工作信息时发生异常！" );
				logger.error( "system list all work info by center work id got an exception.", e );
			}
		}
		if( check ){
			if( all_workBaseInfoList != null ){
				try {
					all_wrapWorkBaseInfoList = wrapout_copier.copy( all_workBaseInfoList );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在转化工作信息列表为输出列表时发生异常！" );
					logger.error( "system copy work base info list to wraps got an exception.", e );
				}
			}
		}
		if( check ){
			if( all_wrapWorkBaseInfoList != null && !all_wrapWorkBaseInfoList.isEmpty() ){
				try {
					SortTools.asc( all_wrapWorkBaseInfoList, "completeDateLimit" );
				} catch (Exception e) {
					result.setUserMessage( "系统为工作进行排序时发生异常！" );
					logger.error( "system sort work list got an exception.", e );
					result.error( e );
				}
			}
			wrapOutOkrCenterWorkInfo.setWorks( all_wrapWorkBaseInfoList );
			result.setData( wrapOutOkrCenterWorkInfo );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}