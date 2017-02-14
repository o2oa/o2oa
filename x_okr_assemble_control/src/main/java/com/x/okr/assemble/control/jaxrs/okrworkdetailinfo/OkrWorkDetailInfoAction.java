package com.x.okr.assemble.control.jaxrs.okrworkdetailinfo;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import com.x.okr.assemble.control.service.OkrWorkBaseInfoService;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;


@Path( "okrworkdetailinfo" )
public class OkrWorkDetailInfoAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkDetailInfoAction.class );
	private BeanCopyTools<OkrWorkDetailInfo, WrapOutOkrWorkDetailInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkDetailInfo.class, WrapOutOkrWorkDetailInfo.class, null, WrapOutOkrWorkDetailInfo.Excludes);
	private OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	private OkrWorkBaseInfoService okrWorkBaseInfoService = new OkrWorkBaseInfoService();

	@HttpMethodDescribe(value = "新建或者更新OkrWorkDetailInfo对象.", request = WrapInOkrWorkDetailInfo.class, response = WrapOutOkrWorkDetailInfo.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, WrapInOkrWorkDetailInfo wrapIn) {
		ActionResult<WrapOutOkrWorkDetailInfo> result = new ActionResult<>();
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to save OkrWorkDetailInfo......" );
		boolean check = true;
		if( wrapIn != null ){
			if( wrapIn.getId() == null ){
				check = false;
				result.error( new Exception( "工作详细信息的ID为空，无法继续进行数据保存!" ) );
				result.setUserMessage( "工作详细信息的ID为空，无法继续进行数据保存!" );
			}
			if( check ){
				//查询工作信息，补充工作详细信息的ID
				try {
					okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getId() );
					if( okrWorkBaseInfo == null ){
						check = false;
						result.error( new Exception( "根据工作信息不存在，无法继续进行数据保存!id:" + wrapIn.getId() ) );
						result.setUserMessage( "根据工作信息不存在，无法继续进行数据保存!id:" + wrapIn.getId() );
					}else{
						wrapIn.setCenterId( okrWorkBaseInfo.getCenterId() ); //ID需要查询确认一下，数据一定要有效
					}
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "根据工作详细信息ID获取工作信息数据异常，无法继续进行数据保存!id:" + wrapIn.getId() );
				}
			}			
			try {
				okrWorkDetailInfo = okrWorkDetailInfoService.save( wrapIn );
				if( okrWorkDetailInfo != null ){
					result.setUserMessage( okrWorkDetailInfo.getId() );
				}else{
					result.error( new Exception( "系统在保存工作详细信息信息时发生异常!" ) );
					result.setUserMessage( "系统在保存工作详细信息信息时发生异常!" );
				}
			} catch (Exception e) {
				result.error( e );
				result.setUserMessage( "系统在保存工作详细信息信息时发生异常!" );
				logger.error( "OkrWorkDetailInfoService save object got an exception", e );
			}
		}else{
			result.error( new Exception( "请求传入的参数为空，无法继续保存工作详细信息!" ) );
			result.setUserMessage( "请求传入的参数为空，无法继续保存工作详细信息!" );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkDetailInfo数据对象.", response = WrapOutOkrWorkDetailInfo.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkDetailInfo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user " + currentPerson.getName() + "[proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to delete okrWorkDetailInfo{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not delete any object." );
		}
		try{
			okrWorkDetailInfoService.delete( id );
			result.setUserMessage( "成功删除工作详细信息数据信息。id=" + id );
		}catch(Exception e){
			logger.error( "system delete okrWorkDetailInfoService get an exception, {'id':'"+id+"'}", e );
			result.setUserMessage( "删除工作详细信息数据过程中发生异常。" );
			result.error( e );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID获取OkrWorkDetailInfo对象.", response = WrapOutOkrWorkDetailInfo.class)
	@GET
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutOkrWorkDetailInfo> result = new ActionResult<>();
		WrapOutOkrWorkDetailInfo wrap = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		//logger.debug( "user[" + currentPerson.getName() + "][proxy:'"+ThisApplication.getLoginIdentity( currentPerson.getName() )+"'] try to get okrWorkDetailInfo{'id':'"+id+"'}......" );
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {
			okrWorkDetailInfo = okrWorkDetailInfoService.get( id );
			if( okrWorkDetailInfo != null ){
				wrap = wrapout_copier.copy( okrWorkDetailInfo );
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
}
