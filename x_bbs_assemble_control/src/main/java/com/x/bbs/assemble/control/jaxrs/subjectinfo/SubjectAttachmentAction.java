package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

import com.x.base.core.application.jaxrs.AbstractJaxrsAction;
import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.HttpMediaType;
import com.x.base.core.http.ResponseFactory;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.project.server.StorageMapping;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.WrapOutSectionInfo;
import com.x.bbs.assemble.control.service.BBSSubjectInfoServiceAdv;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;


@Path("subjectattach")
public class SubjectAttachmentAction extends AbstractJaxrsAction {
	private Logger logger = LoggerFactory.getLogger( SubjectAttachmentAction.class );
	private BBSSubjectInfoServiceAdv subjectInfoServiceAdv = new BBSSubjectInfoServiceAdv();
	private BeanCopyTools< BBSSubjectAttachment, WrapOutSubjectAttachment > wrapout_copier = BeanCopyToolsBuilder.create( BBSSubjectAttachment.class, WrapOutSubjectAttachment.class, null, WrapOutSubjectAttachment.Excludes);
		
	@HttpMethodDescribe(value = "根据指定ID获取附件信息.", response = WrapOutSectionInfo.class)
	@GET
	@Path("{id}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response get( @Context HttpServletRequest request, @PathParam("id") String id ) {
		ActionResult<WrapOutSubjectAttachment> result = new ActionResult<>();
		WrapOutSubjectAttachment wrap = null;
		BBSSubjectAttachment attachmentInfo = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				result.error( new Exception("传入的参数ID为空，无法继续进行查询！") );
				result.setUserMessage( "传入的参数ID为空，无法继续进行查询" );
			}
		}
		
		if( check ){
			try {
				attachmentInfo = subjectInfoServiceAdv.getAttachment( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "系统在根据Id查询附件信息时发生异常" );
				logger.error( "system query attachment with id got an exception!", e );
			}
		}
		
		if( check ){
			if( attachmentInfo != null ){
				try {
					wrap = wrapout_copier.copy( attachmentInfo );
					result.setData( wrap );
				} catch (Exception e) {
					check = false;
					result.error( e );
					result.setUserMessage( "系统在将附件信息转换为输出格式时发生异常" );
					logger.error( "system copy attachment to wrap got an exception!", e );
				}
			}else{
				result.error( new Exception("附件信息不存在！") );
				result.setUserMessage( "附件信息不存在！" );
			}
		}
		
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据ID删除BBSSubjectAttachment数据对象.", response = WrapOutSubjectAttachment.class)
	@DELETE
	@Path( "{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete( @Context HttpServletRequest request, @PathParam( "id" ) String id ) {
		ActionResult<WrapOutSubjectAttachment> result = new ActionResult<>();
		BBSSubjectAttachment subjectAttachment = null;
		BBSSubjectInfo subjectInfo = null;
		StorageMapping mapping = null;
		boolean check = true;
		if( id == null || id.isEmpty() ){
			check = false;
			result.error( new Exception( "附件ID为空，无法进行删除操作。" ) );
			result.setUserMessage( "附件ID为空，无法进行删除操作。" );
			logger.error( "id is null, system can not delete any object." );
		}
		if( check ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				subjectAttachment = emc.find( id, BBSSubjectAttachment.class );
				if (null == subjectAttachment) {
					check = false;
					result.error( new Exception( "附件信息不存在，无法进行删除操作。" ) );
					result.setUserMessage( "附件信息不存在，无法进行删除操作。" );
					logger.error( "subjectAttachment{id:" + id + "} is not exists." );
				}
			}catch(Exception e){
				check = false;
				result.setUserMessage( "系统根据ID获取附件信息时发生异常。" );
				result.error( e );
				logger.error( "system get subjectAttachment{id:" + id + "} from database got an exception.", e );
			}
		}		
		if( check ){
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				subjectInfo = emc.find( subjectAttachment.getId(), BBSSubjectInfo.class );
				if ( null == subjectInfo ) {
					logger.warn( "subjectInfo{id:" + subjectAttachment.getSubjectId() + "} is not exists, anyone can delete the attachments." );
				}
			}catch(Exception e){
				check = false;
				result.error( new Exception( "系统根据ID获取主题基础信息时发生异常。" ) );
				result.setUserMessage( "系统根据ID获取主题基础信息时发生异常。" );
				result.error( e );
				logger.error( "system get subjectInfo{id:" + subjectAttachment.getId() + "} from database got an exception.", e );
			}
		}		
		if( check ){
			if( subjectAttachment != null ){
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					mapping = ThisApplication.storageMappings.get( StorageType.bbs, subjectAttachment.getStorage() );
					//对文件进行删除
					subjectAttachment.deleteContent( mapping );
					//对数据库记录进行删除
					subjectAttachment = emc.find( id, BBSSubjectAttachment.class );
					subjectInfo = emc.find( subjectAttachment.getSubjectId(), BBSSubjectInfo.class );
					emc.beginTransaction( BBSSubjectAttachment.class );
					emc.beginTransaction( BBSSubjectInfo.class);
					if( subjectInfo != null && subjectInfo.getAttachmentList() != null ){
						subjectInfo.getAttachmentList().remove( subjectAttachment.getId() );
						emc.check( subjectInfo, CheckPersistType.all );
					}
					emc.remove( subjectAttachment, CheckRemoveType.all );
					emc.commit();
					result.setUserMessage( "附件信息已经成功删除。" );
				}catch(Exception e){
					check = false;
					result.setUserMessage( "系统根据ID获取主题基础信息时发生异常。" );
					result.error( e );
					logger.error( "system get subjectInfo{id:" + subjectAttachment.getId() + "} from database got an exception.", e );
				}
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "根据主题ID获取BBSSubjectAttachment列表.", response = WrapOutSubjectAttachment.class)
	@GET
	@Path( "list/subject/{id}" )
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listByWorkId(@Context HttpServletRequest request, @PathParam( "id" ) String id) {
		ActionResult<List<WrapOutSubjectAttachment>> result = new ActionResult<List<WrapOutSubjectAttachment>>();
		List<WrapOutSubjectAttachment> wrapOutSubjectAttachmentList = null;
		List<BBSSubjectAttachment> fileInfoList = null;
		BBSSubjectInfo subjectInfo = null;
		if( id == null || id.isEmpty() ){
			logger.error( "id is null, system can not get any object." );
		}
		try {	
			subjectInfo = subjectInfoServiceAdv.get( id );
			if( subjectInfo != null ){
				if( subjectInfo.getAttachmentList() != null && subjectInfo.getAttachmentList().size() > 0 ){
					fileInfoList = subjectInfoServiceAdv.listAttachmentByIds( subjectInfo.getAttachmentList() );
				}else{
					fileInfoList = new ArrayList<BBSSubjectAttachment>();
				}
				wrapOutSubjectAttachmentList = wrapout_copier.copy( fileInfoList );
			}else{
				logger.error( "subjectInfo {'id':'"+id+"'} is not exsits. " );
			}
		} catch (Throwable th) {
			logger.error( "system get by id got an exception" );
			th.printStackTrace();
			result.error(th);
		}
		if( wrapOutSubjectAttachmentList == null ){
			wrapOutSubjectAttachmentList = new ArrayList<WrapOutSubjectAttachment>();
		}
		result.setData( wrapOutSubjectAttachmentList );
		return ResponseFactory.getDefaultActionResultResponse(result);
	}
	
	@HttpMethodDescribe(value = "将图片附件转为base64编码.", response = WrapOutString.class)
	@GET
	@Path("{id}/binary/base64/{size}")
	@Produces(HttpMediaType.APPLICATION_JSON_UTF_8)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response imageToBase64( @Context HttpServletRequest request, @PathParam("id") String id, @PathParam("size") String size ) {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = null;
		BBSSubjectAttachment fileInfo = null;
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
				fileInfo = subjectInfoServiceAdv.getAttachment( id );
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
		StorageMapping mapping = ThisApplication.storageMappings.get( StorageType.bbs, fileInfo.getStorage() );
		if( check ){
			try {
				fileInfo.readContent( mapping, output_for_ftp );
			} catch (Exception e) {
				check = false;
				result.error( e );
				result.setUserMessage( "从文件存储服务器中获取文件流时发生异常." );
				logger.error( "read content from storage got an exception." );
			}
		}
		if( check ){
			input = new ByteArrayInputStream( output_for_ftp.toByteArray() );
			if( input != null ){
				try {
					image = ImageIO.read( input );
				} catch (IOException e) {
					check = false;
					result.error( e );
					result.setUserMessage( "从文件存储服务器中获取文件流时发生异常." );
					logger.error( "read image from io got an exception." );
				}
			}
		}
		if( check ){
			try{	
				int width = image.getWidth();
				int height = image.getHeight();
				if ( sizeNum > 0 ) {
					if( width * height > sizeNum * sizeNum ){
						image = Scalr.resize( image, sizeNum );
					}
				}							
				ImageIO.write( image, "png", output );
				wrap = new WrapOutString();
				wrap.setValue( Base64.encodeBase64String( output.toByteArray() ));
				result.setData( wrap );
			}catch( Exception e ){
				check = false;
				result.error( e );
				result.setUserMessage( "根据ID查询附件信息发生异常." );
				logger.error( "system encode file info in base64 string got an exception.id:" + id, e );
			}
		}
		return ResponseFactory.getDefaultActionResultResponse(result);
	}

	private boolean isImage(BBSSubjectAttachment fileInfo) {
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