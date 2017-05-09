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

import com.google.gson.JsonElement;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.jaxrs.ResponseFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception.WorkDetailDeleteException;
import com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception.WorkDetailSaveException;
import com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception.WorkIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception.WorkNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception.WorkQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrworkdetailinfo.exception.WrapInConvertException;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkDetailInfoService;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkDetailInfo;


@Path( "okrworkdetailinfo" )
public class OkrWorkDetailInfoAction extends StandardJaxrsAction{
	private Logger logger = LoggerFactory.getLogger( OkrWorkDetailInfoAction.class );
	private BeanCopyTools<OkrWorkDetailInfo, WrapOutOkrWorkDetailInfo> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkDetailInfo.class, WrapOutOkrWorkDetailInfo.class, null, WrapOutOkrWorkDetailInfo.Excludes);
	private OkrWorkDetailInfoService okrWorkDetailInfoService = new OkrWorkDetailInfoService();
	private OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();

	@HttpMethodDescribe(value = "新建或者更新OkrWorkDetailInfo对象.", request = JsonElement.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@Context HttpServletRequest request, JsonElement jsonElement) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		WrapInOkrWorkDetailInfo wrapIn = null;
		Boolean check = true;
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInOkrWorkDetailInfo.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new WrapInConvertException( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if( check ){
			if( wrapIn.getId() == null ){
				check = false;
				Exception exception = new WorkIdEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
			if( check ){
				//查询工作信息，补充工作详细信息的ID
				try {
					okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getId() );
					if( okrWorkBaseInfo == null ){
						check = false;
						Exception exception = new WorkNotExistsException( wrapIn.getId() );
						result.error( exception );
						//logger.error( e, effectivePerson, request, null);
					}else{
						wrapIn.setCenterId( okrWorkBaseInfo.getCenterId() ); //ID需要查询确认一下，数据一定要有效
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new WorkQueryByIdException( e, wrapIn.getId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}			
			try {
				okrWorkDetailInfoService.save( wrapIn );
				result.setData( new WrapOutId(wrapIn.getId()) );
			} catch (Exception e) {
				Exception exception = new WorkDetailSaveException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe(value = "根据ID删除OkrWorkDetailInfo数据对象.", response = WrapOutId.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		EffectivePerson effectivePerson = this.effectivePerson( request );
		
		if( id == null || id.isEmpty() ){
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}else{
			try{
				okrWorkDetailInfoService.delete( id );
				result.setData( new WrapOutId(id) );
			}catch(Exception e){
				Exception exception = new WorkDetailDeleteException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
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
		EffectivePerson effectivePerson = this.effectivePerson( request );
		
		WrapOutOkrWorkDetailInfo wrap = null;
		OkrWorkDetailInfo okrWorkDetailInfo = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new WorkIdEmptyException();
			result.error( exception );
			//logger.error( e, effectivePerson, request, null);
		}
		try {
			okrWorkDetailInfo = okrWorkDetailInfoService.get( id );
			if( okrWorkDetailInfo != null ){
				wrap = wrapout_copier.copy( okrWorkDetailInfo );
				result.setData(wrap);
			}else{
				Exception exception = new WorkNotExistsException( id );
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		} catch (Exception e) {
			Exception exception = new WorkQueryByIdException( e, id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
}
