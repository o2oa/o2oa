package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.util.ArrayList;
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

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.utils.SortTools;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.entity.HotPictureInfo;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

@Path("user/hotpic")
public class HotPictureInfoAction extends AbstractJaxrsAction {

	private Logger logger = LoggerFactory.getLogger( HotPictureInfoAction.class );
	private HotPictureInfoServiceAdv hotPictureInfoService = new HotPictureInfoServiceAdv();
	private BeanCopyTools<WrapInHotPictureInfo, HotPictureInfo> wrapin_copier = BeanCopyToolsBuilder.create( WrapInHotPictureInfo.class, HotPictureInfo.class, null, WrapInHotPictureInfo.Excludes );
	private BeanCopyTools< HotPictureInfo, WrapOutHotPictureInfo > wrapout_copier = BeanCopyToolsBuilder.create( HotPictureInfo.class, WrapOutHotPictureInfo.class, null, WrapOutHotPictureInfo.Excludes);
	private Ehcache cache = ApplicationCache.instance().getCache( HotPictureInfo.class);
	
	@HttpMethodDescribe(value = "查询指定的图片的base64编码.", response = WrapOutString.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = null;
		HotPictureInfo hotPictureInfo = null;
		Boolean check = true;
		
		if (check) {
			if (id == null || id.isEmpty() || "(0)".equals(id)) {
				check = false;
				result.error(new Exception("id can not be null!"));
				result.setUserMessage("传入的参数id为空。");
			}
		}
		
		String cacheKey = "base64#" + id;
		Element element = cache.get(cacheKey);
		if (check) {
			if (null != element) {
				wrap = ( WrapOutString ) element.getObjectValue();
				result.setData(wrap);
			} else {
				try {
					hotPictureInfo = hotPictureInfoService.get(id);
					if ( hotPictureInfo == null ) {
						result.error(new Exception("hotPictureInfo is not exists."));
						result.setUserMessage("指定的图片信息不存在！");
						logger.error("hotPictureInfo is not exists!id:" + id);
					}else{
						wrap = new WrapOutString();
						wrap.setValue( hotPictureInfo.getPictureBase64() );
						cache.put(new Element(cacheKey, wrap));
						result.setData(wrap);
					}
				} catch (Exception e) {
					check = false;
					result.error(e);
					result.setUserMessage("系统在根据ID查询热图信息时发生异常！");
					logger.error("system query hotPictureInfo with id got an exception!id:" + id, e);
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@SuppressWarnings("unchecked")
	@HttpMethodDescribe( value = "根据应用类型以及信息ID查询热图信息.", response = WrapOutHotPictureInfo.class )
	@GET
	@Path("{application}/{infoId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByApplicationAndInfoId(@Context HttpServletRequest request, @PathParam("application") String application, @PathParam("infoId") String infoId) {
		ActionResult<List<WrapOutHotPictureInfo>> result = new ActionResult<>();
		List<WrapOutHotPictureInfo> wraps = null;
		List<HotPictureInfo> hotPictureInfos = null;
		Boolean check = true;
		
		if( check ){
			if( application == null || application.isEmpty()|| "(0)".equals( application ) ){
				check = false;
				result.error( new Exception( "application can not be null!" ) );
				result.setUserMessage( "传入的参数application为空。" );
			}
		}
		if( check ){
			if( infoId == null || infoId.isEmpty() || "(0)".equals( infoId ) ){
				check = false;
				result.error( new Exception( "infoId can not be null!" ) );
				result.setUserMessage( "传入的参数infoId为空。" );
			}
		}
		
		String cacheKey = "list#" + application + "#" + infoId;
		Element element = cache.get(cacheKey);
		
		if( check ){
			if (null != element) {
				wraps = ( List<WrapOutHotPictureInfo> ) element.getObjectValue();
				result.setData( wraps );
			} else {
				if( check ){
					try{
						hotPictureInfos = hotPictureInfoService.listByApplicationInfoId( application, infoId );
					}catch( Exception e ){
						check = false;
						result.error( e );
						result.setUserMessage( "系统在根据ID查询热图信息时发生异常！" );
						logger.error( "system query hotPictureInfo with id got an exception!infoId:" + infoId, e );
					}
				}
				if( check ){
					if( hotPictureInfos != null && !hotPictureInfos.isEmpty() ){
						try {
							wraps = wrapout_copier.copy( hotPictureInfos );
							cache.put( new Element(cacheKey, wraps) );
							result.setData( wraps );
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "系统在将信息列表转换为输出格式时发生异常" );
							logger.error( "system copy hotpic list to wraps got an exception!", e );
						}
					}
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@SuppressWarnings("unchecked")
	@HttpMethodDescribe(value = "列示根据过滤条件的HotPictureInfo,下一页.", response = WrapOutHotPictureInfo.class, request = WrapInFilter.class )
	@PUT
	@Path("filter/list/page/{page}/count/{count}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listForPage( @Context HttpServletRequest request, @PathParam("page") Integer page, @PathParam("count") Integer count, WrapInFilter wrapIn ) {
		ActionResult<List<WrapOutHotPictureInfo>> result = new ActionResult<>();
		List<WrapOutHotPictureInfo> wraps_out = new ArrayList<WrapOutHotPictureInfo>();
		List<WrapOutHotPictureInfo> wraps = new ArrayList<WrapOutHotPictureInfo>();
		List<HotPictureInfo> hotPictureInfoList = null;
		Integer selectTotal = 0;
		Long total = 0L;
		Boolean check = true;
		if( check ){
			if( wrapIn == null ){
				wrapIn = new WrapInFilter();
			}
		}
		if( check ){
			if( page == null ){
				page = 1;
			}
			if( page <= 0 ){
				page = 1;
			}
		}
		if( check ){
			if( count == null ){
				count = 20;
			}
			if( count <= 0 ){
				count = 20;
			}
		}		
		selectTotal = page * count;
		
		String cacheKey1 = "filter#" + page + "#" + count+ "#" + wrapIn.getApplication()+ "#" + wrapIn.getInfoId()+ "#" + wrapIn.getTitle();
		Element element1 = cache.get( cacheKey1 );
		String cacheKey2 = "total#" + page + "#" + count+ "#" + wrapIn.getApplication()+ "#" + wrapIn.getInfoId()+ "#" + wrapIn.getTitle();
		Element element2 = cache.get( cacheKey2 );
		if( check ){
			if (null != element1 && null != element2 ) {
				wraps = ( List<WrapOutHotPictureInfo> ) element1.getObjectValue();
				result.setCount(Long.parseLong( element2.getObjectValue().toString()) );
				result.setData( wraps );
			} else {
				if( check ){
					if( selectTotal > 0 ){
						try{
							total = hotPictureInfoService.count( wrapIn.getApplication(), wrapIn.getInfoId(), wrapIn.getTitle() );
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "查询符合查询条件的信息总数时发生异常！" );
							logger.error( "system count HotPicture info with condition got an exceptin.", e );
						}
					}
				}
				if( check ){
					if( selectTotal > 0 && total > 0 ){
						try{
							hotPictureInfoList = hotPictureInfoService.listForPage( wrapIn.getApplication(), wrapIn.getInfoId(), wrapIn.getTitle(), selectTotal );
							if( hotPictureInfoList != null ){
								try {
									wraps_out = wrapout_copier.copy( hotPictureInfoList );
									SortTools.desc( wraps_out, "sequence" );
								} catch (Exception e) {
									check = false;
									result.error( e );
									result.setUserMessage( "系统在将信息列表转换为输出格式时发生异常" );
									logger.error( "system copy hotpic list to wraps got an exception!", e );
								}
							}
						} catch (Exception e) {
							check = false;
							result.error( e );
							result.setUserMessage( "根据ID信息查询版块信息时发生异常！" );
							logger.error( "system query all top subject info with section info got an exceptin.", e );
						}
					}
				}
				if( check ){
					int startIndex = ( page - 1 ) * count;
					int endIndex = page * count;
					int i = 0;
					for( i = 0; i< wraps_out.size(); i++ ){
						if( i >= startIndex && i < endIndex ){
							wraps_out.get( i ).setPictureBase64( null );
							wraps.add( wraps_out.get( i ) );
						}
					}
					cache.put( new Element(cacheKey1, wraps) );
					cache.put( new Element(cacheKey2, total.toString()) );
					result.setData( wraps );
					result.setCount( total );			
				}
			}
		}		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	/**
	 * 保存热图信息，登录用户访问
	 * @param request
	 * @return
	 */
	@HttpMethodDescribe(value = "创建新的热图信息或者更新热图信息.", request = WrapInHotPictureInfo.class, response = WrapOutId.class)
	@POST
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response save( @Context HttpServletRequest request, WrapInHotPictureInfo wrapIn ) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		Boolean check = true;
		HotPictureInfo hotPictureInfo = null;
		
		if( wrapIn == null ){
			check = false;
			result.error( new Exception("wrapIn is null, can not save info！") );
			result.setUserMessage( "系统传入的对象为空，无法进行数据保存！" );
		}
		if( check ){
			if( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty() ){
				check = false;
				result.error( new Exception("title is null!") );
				result.setUserMessage( "系统传入的[热图标题]为空，无法进行数据保存！" );
			}
		}
		if( check ){
			if( wrapIn.getUrl() == null || wrapIn.getUrl().isEmpty() ){
				check = false;
				result.error( new Exception("url is null!") );
				result.setUserMessage( "系统传入的[热图访问链接]为空，无法进行数据保存！" );
			}
		}
		if( check ){
			if( wrapIn.getPictureBase64() == null || wrapIn.getPictureBase64().isEmpty() ){
				check = false;
				result.error( new Exception("pictureLobValue is null!") );
				result.setUserMessage( "系统传入的[热图图片编码]为空，无法进行数据保存！" );
			}
		}
		if( check ){
			try {
				hotPictureInfo = wrapin_copier.copy( wrapIn );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在COPY传入的对象时发生异常！" );
				logger.error( "system copy wrapIn to hotPictureInfo got an exception!", e );
			}
		}
		if( check ){
			hotPictureInfo.setPicUrl("x_hotpic_assemble_control/jaxrs/user/hotpic/"+ hotPictureInfo.getId() );
			try {
				hotPictureInfo = hotPictureInfoService.save( hotPictureInfo );
				wrap = new WrapOutId( hotPictureInfo.getId() );
				result.setData( wrap );
				result.setUserMessage( "热图信息保存成功！" );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在保存热图信息时发生异常！" );
				logger.error( "system save hotPictureInfo got an exception!", e );
			}
			try {
				ApplicationCache.notify( HotPictureInfo.class );
			} catch (Exception e) {
				logger.error( "system notify application cache got an exception!", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	@HttpMethodDescribe( value = "根据ID删除指定的热图信息.", response = WrapOutId.class )
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		HotPictureInfo hotPictureInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() || "(0)".equals( id ) ){
				check = false;
				result.error( new Exception( "传入的参数ID为空，无法继续进行查询！" ) );
				result.setUserMessage( "传入的参数ID为空，无法继续进行查询" );
			}
		}
		if( check ){
			try{
				hotPictureInfo = hotPictureInfoService.get(id);
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询热图信息时发生异常！" );
				logger.error( "system query hotPictureInfo with id got an exception!id:" + id, e );
			}
		}
		if( check ){
			if( hotPictureInfo == null ){
				check = false;
				result.error( new Exception("热图信息不存在，无法继续进行删除操作！ID=" + id ) );
				result.setUserMessage( "热图信息不存在，无法继续进行删除操作！" );
			}
		}
		if( check ){
			try {
				hotPictureInfoService.delete( id );
				wrap = new WrapOutId( id );
				result.setData( wrap );
				result.setUserMessage( "成功删除热图信息！" );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在删除热图信息时发生异常" );
				logger.error( "system delete forum info got an exception!", e );
			}
			try {
				ApplicationCache.notify( HotPictureInfo.class );
			} catch (Exception e) {
				logger.error( "system notify application cache got an exception!", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	

	@HttpMethodDescribe( value = "根据应用类型以及信息ID删除热图信息.", response = WrapOutId.class )
	@DELETE
	@Path("{application}/{infoId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("application") String application, @PathParam("infoId") String infoId) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		List<HotPictureInfo> hotPictureInfos = null;
		Boolean check = true;
		
		if( check ){
			if( application == null || application.isEmpty()|| "(0)".equals( application ) ){
				check = false;
				result.error( new Exception( "application can not be null!" ) );
				result.setUserMessage( "传入的参数application为空，无法继续进行删除。" );
			}
		}
		if( check ){
			if( infoId == null || infoId.isEmpty()|| "(0)".equals( infoId ) ){
				check = false;
				result.error( new Exception( "infoId can not be null!" ) );
				result.setUserMessage( "传入的参数infoId为空，无法继续进行删除。" );
			}
		}
		if( check ){
			try{
				hotPictureInfos = hotPictureInfoService.listByApplicationInfoId( application, infoId );
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据ID查询热图信息时发生异常！" );
				logger.error( "system query hotPictureInfo with id got an exception!infoId:" + infoId, e );
			}
		}
		if( check ){
			if( hotPictureInfos == null || hotPictureInfos.isEmpty() ){
				check = false;
				result.error( new Exception("hot pic info not exists！infoId=" + infoId ) );
				result.setUserMessage( "热图信息不存在，无法继续进行删除操作！" );
			}
		}
		if( check ){
			for( HotPictureInfo hotPictureInfo : hotPictureInfos ){
				try {
					hotPictureInfoService.delete( hotPictureInfo.getId() );
					wrap = new WrapOutId( hotPictureInfo.getId() );
					result.setData( wrap );
					result.setUserMessage( "成功删除热图信息！" );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在删除热图信息时发生异常" );
					logger.error( "system delete forum info got an exception!", e );
				}
			}
			try {
				ApplicationCache.notify( HotPictureInfo.class );
			} catch (Exception e) {
				logger.error( "system notify application cache got an exception!", e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
}