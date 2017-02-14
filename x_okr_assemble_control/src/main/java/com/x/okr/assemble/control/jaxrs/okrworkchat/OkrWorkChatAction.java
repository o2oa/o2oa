package com.x.okr.assemble.control.jaxrs.okrworkchat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkbaseinfo.WrapOutOkrWorkBaseInfo;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoService;
import com.x.okr.assemble.control.service.OkrWorkChatService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkChat;


@Path( "okrworkchat" )
public class OkrWorkChatAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkChatAction.class );
	private BeanCopyTools<OkrWorkChat, WrapOutOkrWorkChat> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkChat.class, WrapOutOkrWorkChat.class, null, WrapOutOkrWorkChat.Excludes);
	private OkrWorkChatService okrWorkChatService = new OkrWorkChatService();
	private OkrWorkBaseInfoService okrWorkBaseInfoService = new OkrWorkBaseInfoService();
	private OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	private OkrUserInfoService okrUserInfoService = new OkrUserInfoService();

	@HttpMethodDescribe(value = "新建或者更新OkrWorkChat对象.", request = WrapInOkrWorkChat.class, response = WrapOutOkrWorkChat.class)
	@POST
	@Produces( HttpMediaType.APPLICATION_JSON_UTF_8 )
	@Consumes( MediaType.APPLICATION_JSON )
	public Response post( @Context HttpServletRequest request, WrapInOkrWorkChat wrapIn ) {
		ActionResult< WrapOutOkrWorkChat > result = new ActionResult<>();
		OkrWorkChat okrWorkChat = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		boolean check = true;		
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		
		if( wrapIn == null ){
			check = false;
			result.error( new Exception( "系统未获取到需要保存的信息，操作无法继续！" ) );
			result.setUserMessage( "系统未获取到需要保存的信息，操作无法继续！" );
		}
		
		if( check ){
			//对wrapIn里的信息进行校验
			if( okrUserCache.getLoginUserName() == null ){
				check = false;
				result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
				result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
			}
		}		
		
		if( check ){
			//校验工作ID是否合法
			if( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ){
				check = false;
				result.error( new Exception( "系统未获取到工作ID，无法进行交流保存!" ) );
				result.setUserMessage( "系统未获取到工作ID，无法进行交流保存!" );
			}
		}
		
		if( check ){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
				if( okrWorkBaseInfo == null ){
					check = false;
					result.error( new Exception( "系统未查询到指定ID的工作!" ) );
					result.setUserMessage( "系统未查询到指定ID的工作!" );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询工作信息时发生异常!" );
			}
		}
		
		if( check ){
			wrapIn.setSenderName( okrUserCache.getLoginUserName() );
			wrapIn.setSenderIdentity( okrUserCache.getLoginIdentityName() );
			wrapIn.setCenterId(  okrWorkBaseInfo.getCenterId() );
			wrapIn.setCenterTitle( okrWorkBaseInfo.getCenterTitle() );
			wrapIn.setWorkId( okrWorkBaseInfo.getId() );
			wrapIn.setWorkTitle( okrWorkBaseInfo.getTitle() );
		}
		
		if( check ){
			try {
				okrWorkChat = okrWorkChatService.save( wrapIn );
				if( okrWorkChat != null ){
					result.setUserMessage( okrWorkChat.getId() );
					okrWorkDynamicsService.workChatDynamic(
							okrWorkBaseInfo, 
							"发送工作交流", 
							currentPerson.getName(),
							okrUserCache.getLoginUserName(),
							okrUserCache.getLoginIdentityName() , 
							okrWorkChat.getContent(),
							"工作交流发送成功！");
				}else{
					result.error( new Exception( "系统在保存工作交流信息时发生异常!" ) );
					result.setUserMessage( "系统在保存工作交流信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存工作交流信息时发生异常!" );
				logger.error( "OkrWorkChatService save object got an exception", e );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkChat数据对象.", response = WrapOutOkrWorkChat.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkChat> result = new ActionResult<>();
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrWorkChatService.delete( id );
			result.setUserMessage( "成功删除工作动态数据信息。id=" + id );
		}catch(Exception e){
			logger.error( "system delete okrWorkChatService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除工作动态数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkChat对象.", response = WrapOutOkrWorkChat.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkChat> result = new ActionResult<>();
		WrapOutOkrWorkChat wrap = null;
		OkrWorkChat okrWorkChat = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrWorkChat = okrWorkChatService.get( id );
			if( okrWorkChat != null ){
				wrap = wrapout_copier.copy( okrWorkChat );
				result.setData(wrap);
			}else{
				logger.error( "system can not get any object by {'id':'"+id+"'}. " );
			}
		} catch (Throwable th) {
			logger.error( "system get by id got an exception" );
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的WrapOutOkrWorkChat,下一页.", response = WrapOutOkrWorkBaseInfo.class, request = WrapInFilter.class)
	@PUT
	@Path( "filter/list/{id}/next/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listNextWithFilter( @Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkChat>> result = new ActionResult<List<WrapOutOkrWorkChat>>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutOkrWorkChat> wrapOutOkrWorkChatList = null;
		List<OkrWorkChat> chatList = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Long total = 0L;
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( count == null ){
			count = 20;
		}
		
		if( check ){
			//对wrapIn里的信息进行校验
			if( okrUserCache.getLoginUserName() == null ){
				check = false;
				result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
				result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
			}
		}
		
		if( check ){
			//对wrapIn里的信息进行校验
			if( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ){
				check = false;
				result.error( new Exception( "查询传入的workId为空，无法查询交流信息!" ) );
				result.setUserMessage( "查询传入的workId为空，无法查询交流信息!" );
			}
		}
		
		if( check ){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
				if( okrWorkBaseInfo == null ){
					check = false;
					result.error( new Exception( "系统未查询到指定ID的工作!" ) );
					result.setUserMessage( "系统未查询到指定ID的工作!" );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询工作信息时发生异常!" );
			}
		}
		
		if( check ){
			try{
				chatList = okrWorkChatService.listChatNextWithFilter( id, count, wrapIn);
				total = okrWorkChatService.getChatCountWithFilter(wrapIn);
				wrapOutOkrWorkChatList = wrapout_copier.copy(chatList);	
				result.setData( wrapOutOkrWorkChatList );
				result.setCount( total );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "列示根据过滤条件的WrapOutOkrWorkChat,上一页.", response = WrapOutOkrWorkBaseInfo.class, request = WrapInFilter.class)
	@PUT
	@Path( "filter/list/{id}/prev/{count}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listPrevWithFilter(@Context HttpServletRequest request, @PathParam( "id" ) String id, @PathParam( "count" ) Integer count, WrapInFilter wrapIn) {
		ActionResult<List<WrapOutOkrWorkChat>> result = new ActionResult<List<WrapOutOkrWorkChat>>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutOkrWorkChat> wrapOutOkrWorkChatList = null;
		List<OkrWorkChat> chatList = null;
		Long total = 0L;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		boolean check = true;
		OkrUserCache  okrUserCache  = null;
		
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( currentPerson.getName() );
		} catch (Exception e1) {
			check = false;
			result.error( new Exception( "系统获取用户登录记录信息发生异常!" ) );
			result.setUserMessage( "系统获取用户登录记录信息发生异常!" );
			logger.error( "system get login indentity with person name got an exception", e1 );
		}		
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			logger.error( "system query user identity got an exception.user:" + currentPerson.getName());
			result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
			result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
		}
		if( count == null ){
			count = 20;
		}
		
		if( check ){
			//对wrapIn里的信息进行校验
			if( okrUserCache.getLoginUserName() == null ){
				check = false;
				result.error( new Exception( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" ) );
				result.setUserMessage( "系统未获取到用户登录身份(登录用户名)，请重新打开应用!" );
			}
		}
		
		if( check ){
			//对wrapIn里的信息进行校验
			if( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ){
				check = false;
				result.error( new Exception( "查询传入的workId为空，无法查询交流信息!" ) );
				result.setUserMessage( "查询传入的workId为空，无法查询交流信息!" );
			}
		}
		
		if( check ){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
				if( okrWorkBaseInfo == null ){
					check = false;
					result.error( new Exception( "系统未查询到指定ID的工作!" ) );
					result.setUserMessage( "系统未查询到指定ID的工作!" );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询工作信息时发生异常!" );
			}
		}
		
		if( check ){
			try{
				chatList = okrWorkChatService.listChatPrevWithFilter( id, count, wrapIn);
				total = okrWorkChatService.getChatCountWithFilter(wrapIn);
				wrapOutOkrWorkChatList = wrapout_copier.copy(chatList);	
				result.setData( wrapOutOkrWorkChatList );
				result.setCount( total );
			}catch(Throwable th){
				logger.error( "system filter okrWorkBaseInfo got an exception." );
				th.printStackTrace();
				result.error(th);
			}
		}		
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
