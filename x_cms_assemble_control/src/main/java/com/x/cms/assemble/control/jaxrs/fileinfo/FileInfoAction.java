package com.x.cms.assemble.control.jaxrs.fileinfo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.math.NumberUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.application.jaxrs.StandardJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.assemble.control.factory.FileInfoFactory;
import com.x.cms.assemble.control.service.FileInfoServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;

@Path("fileinfo")
public class FileInfoAction extends StandardJaxrsAction{
	
	private Logger logger = LoggerFactory.getLogger( FileInfoAction.class );
	private LogService logService = new LogService();
	private FileInfoServiceAdv fileInfoServiceAdv = new FileInfoServiceAdv();
	private BeanCopyTools<FileInfo, WrapOutFileInfo> copier = BeanCopyToolsBuilder.create( FileInfo.class, WrapOutFileInfo.class, null, WrapOutFileInfo.Excludes);
	
	@HttpMethodDescribe(value = "获取全部的文件或者附件列表", response = WrapOutFileInfo.class)
	@GET
	@Path("list/all")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listAllFileInfo( @Context HttpServletRequest request ) {		
		ActionResult<List<WrapOutFileInfo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutFileInfo> wraps = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);			
			//如判断用户是否有查看所有文件或者附件的权限，如果没权限不允许继续操作
			if (!business.fileInfoEditAvailable( request, currentPerson )) {
				throw new Exception("person{name:" + currentPerson.getName() + "} 用户没有查询全部文件或者附件的权限！");
			}			
			//如果有权限，继续操作
			FileInfoFactory fileInfoFactory  = business.getFileInfoFactory();
			List<String> ids = fileInfoFactory.listAll();//获取所有文件或者附件列表
			List<FileInfo> fileInfoList = fileInfoFactory.list( ids );//查询ID IN ids 的所有文件或者附件信息列表
			wraps = copier.copy( fileInfoList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort( wraps );//对查询的列表进行排序		
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "获取指定文档的全部附件信息列表", response = WrapOutFileInfo.class)
	@GET
	@Path("list/document/{documentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listFileInfoByDocumentId(@Context HttpServletRequest request, @PathParam("documentId")String documentId ) {		
		ActionResult<List<WrapOutFileInfo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WrapOutFileInfo> wraps = null;
		logger.debug( "[listFileInfoByDocumentId]用户["+currentPerson.getName()+"]尝试查询文档的所有附件......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			Business business = new Business(emc);			
			//如判断用户是否有查看所有文件或者附件的权限，如果没权限不允许继续操作
			if (!business.fileInfoEditAvailable( request, currentPerson )) {
				throw new Exception("[listFileInfoByDocumentId]person{name:" + currentPerson.getName() + "} 用户没有查询全部文件或者附件的权限！");
			}			
			//如果有权限，继续操作
			FileInfoFactory fileInfoFactory  = business.getFileInfoFactory();
			List<String> ids = fileInfoFactory.listByDocument( documentId );//获取指定文档的所有附件列表
			List<FileInfo> fileInfoList = fileInfoFactory.list( ids );//查询ID IN ids 的所有文件或者附件信息列表
			wraps = copier.copy( fileInfoList );//将所有查询出来的有状态的对象转换为可以输出的过滤过属性的对象
			Collections.sort( wraps );//对查询的列表进行排序		
			result.setData(wraps);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID获取fileInfo对象.", response = WrapOutFileInfo.class)
	@GET
	@Path("{id}/document/{documentId}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("documentId") String documentId ) {
		ActionResult<WrapOutFileInfo> result = new ActionResult<>();
		WrapOutFileInfo wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			//先查询附件是否该文档里的附件，从属关系是否正常
			Document document = emc.find( documentId, Document.class);
			if (null == document) {
				throw new Exception("document{id:" + documentId + "} not existed.");
			}
			if (!document.getAttachmentList().contains(id)) {
				throw new Exception("document{id" + documentId + "} not contian attachment{id:" + id + "}.");
			}
			
			FileInfo fileInfo = emc.find(id, FileInfo.class);
			if ( null == fileInfo ) {
				throw new Exception("[get]fileInfo{id:" + id + "} 信息不存在.");
			}
			
			//如果信息存在，则需要向客户端返回信息，先将查询出来的JPA对象COPY到一个普通JAVA对象里，再进行返回
			wrap = new WrapOutFileInfo();
			copier.copy(fileInfo, wrap);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除FileInfo应用信息对象.", response = WrapOutId.class)
	@DELETE
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(@Context HttpServletRequest request, @PathParam("id") String id) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		FileInfo fileInfo = null;
		Document document = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		logger.debug( "[delete]用户["+currentPerson.getName()+"]尝试删除附件 fileinfo{'id':'"+id+"'}......" );
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);			
			//先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			fileInfo = business.getFileInfoFactory().get(id);			
			if (null == fileInfo) {
				throw new Exception("fileInfo{id:" + id + "} 文件信息不存在，无法继续删除.");
			}
			logger.debug( "[delete]附件 fileinfo{'id':'"+id+"'}存在." );
			//判断文档信息是否存在
			document = business.getDocumentFactory().get( fileInfo.getDocumentId() );
			if (null == document) {
				throw new Exception("document{id:" + fileInfo.getDocumentId() + "} 文档信息不存在，无法继续删除.");
			}
			logger.debug( "[delete]附件所属的文档 document{'id':'"+fileInfo.getDocumentId()+"'}存在." );
			//如果信息存在，再判断用户是否有操作的权限，如果没权限不允许继续操作
			if (!business.fileInfoEditAvailable( request, currentPerson )) {
				throw new Exception("fileInfo{name:" + currentPerson.getName() + "} ，用户没有内容管理应用信息操作的权限！");
			}
			//删除文件，并且删除记录及文档的关联信息
			StorageMapping mapping = ThisApplication.storageMappings.get( StorageType.cms, fileInfo.getStorage());
			
			//从FTP上删除文件
			fileInfo.deleteContent( mapping );
			
			ApplicationCache.notify( FileInfo.class );
			
			emc.beginTransaction( FileInfo.class );
			emc.beginTransaction( Document.class);
			if( document != null && document.getAttachmentList() != null ){
				document.getAttachmentList().remove( fileInfo.getId() );
			}
			emc.remove( fileInfo, CheckRemoveType.all );
			emc.commit();
			
			//成功删除一个附件信息
			logService.log( emc, currentPerson.getName(), "成功删除一个附件信息", fileInfo.getAppId(), fileInfo.getId(), fileInfo.getDocumentId(), fileInfo.getId(), "FILE", "删除" );
			
			wrap = new WrapOutId( fileInfo.getId());
			result.setData( wrap );
			wrap = new WrapOutId( fileInfo.getId() );
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
			logger.error("系统在根据ID删除文件时发生异常！");
		}
		
		return ResponseFactory.getDefaultActionResultResponse( result );
	}
	
	@HttpMethodDescribe(value = "将图片附件转为base64编码.", response = WrapOutString.class)
	@GET
	@Path("{id}/binary/base64/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response imageToBase64(@Context HttpServletRequest request, @PathParam("id") String id, @PathParam("size") String size ) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = null;
		FileInfo fileInfo = null;
		Integer sizeNum = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("id can not be null!") );
				result.setUserMessage( "参数附件ID为空,无法进行查询" );
			}
		}
		if( check ){
			if( size != null && !size.isEmpty() ){
				if ( NumberUtils.isNumber( size ) ) {
					sizeNum = Integer.parseInt( size );
				}else{
					check = false;
					result.error( new Exception("size must be number.size=" + size ) );
					result.setUserMessage( "请求参数size格式不合法, 要求为数字." );
				}
			}else{
				sizeNum = 800;
			}
		}
		if( check ){
			try {
				fileInfo = fileInfoServiceAdv.get( id );
				if( fileInfo == null ){
					check = false;
					result.error( new Exception("file info is not exists.") );
					result.setUserMessage( "根据ID无法查询到任何文件信息." );
				}
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "根据ID查询附件信息发生异常." );
				logger.error( "system find file info with id got an exception.id:" + id );
			}
			
		}
		if( check ){
			if ( !isImage( fileInfo ) ){
				check = false;
				result.error( new Exception("file info is not image.") );
				result.setUserMessage( "文件并不是图片格式,无法转换." );
			}
		}
		BufferedImage image = null;
		ByteArrayInputStream input = null;
		ByteArrayOutputStream output_for_ftp = new ByteArrayOutputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		StorageMapping mapping = ThisApplication.storageMappings.get( StorageType.cms, fileInfo.getStorage());
		try{
			fileInfo.readContent( mapping, output_for_ftp );
			input = new ByteArrayInputStream( output_for_ftp.toByteArray() );
			image = ImageIO.read( input );
			int width = image.getWidth();
			int height = image.getHeight();
			if ( sizeNum > 0 ) {
				if( width * height > sizeNum * sizeNum ){
					image = Scalr.resize( image, sizeNum );
				}
			}							
			ImageIO.write( image, "png", output );
			wrap = new WrapOutString();
			wrap.setValue(Base64.encodeBase64String( output.toByteArray() ));
			result.setData( wrap );
		}catch( Exception e ){
			check = false;
			result.error( e );
			result.setUserMessage( "根据ID查询附件信息发生异常." );
			logger.error( "system encode file info in base64 string got an exception.id:" + id );
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private boolean isImage(FileInfo fileInfo) {
		if( fileInfo == null || fileInfo.getExtension() == null || fileInfo.getExtension().isEmpty() ){
			return false;
		}
		if( "jpg".equalsIgnoreCase( fileInfo.getExtension() )){
			return true;
		}else if( "png".equalsIgnoreCase( fileInfo.getExtension() )){
			return true;
		}else if( "jpeg".equalsIgnoreCase( fileInfo.getExtension() )){
			return true;
		}else if( "tiff".equalsIgnoreCase( fileInfo.getExtension() )){
			return true;
		}else if( "gif".equalsIgnoreCase( fileInfo.getExtension() )){
			return true;
		}else if( "bmp".equalsIgnoreCase( fileInfo.getExtension() )){
			return true;
		}
		return false;
	}
}