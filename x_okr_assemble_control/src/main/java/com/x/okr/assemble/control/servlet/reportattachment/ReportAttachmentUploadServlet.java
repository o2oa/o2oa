package com.x.okr.assemble.control.servlet.reportattachment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.StorageMapping;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrWorkReportBaseInfo;

/**
 * 附件上传服务
 * 
 * @author LIYI
 *
 */
@WebServlet(urlPatterns = "/servlet/upload/report/*")
@MultipartConfig
public class ReportAttachmentUploadServlet extends AbstractServletAction {

	private static final long serialVersionUID = 5628571943877405247L;
	private Logger logger = LoggerFactory.getLogger(ReportAttachmentUploadServlet.class);

	@HttpMethodDescribe(value = "上传附件 servlet/upload/report/{id}", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionResult<List<WrapOutId>> result = new ActionResult< List<WrapOutId> >();
		List<WrapOutId> wraps = new ArrayList<>();
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		WrapOutId wrap = null;
		OkrWorkReportBaseInfo okrWorkReportBaseInfo = null;
		EffectivePerson effectivePerson = null;
		ServletFileUpload upload = null;
		FileItemIterator fileItemIterator = null;
		FileItemStream item = null;
		InputStream input = null;
		String reportId = null;
		String name = null;
		String site = null;
		boolean check = true;

		request.setCharacterEncoding("UTF-8");
		
		if (!ServletFileUpload.isMultipartContent(request)) {
			check = false;
			result.error( new Exception("请求不是Multipart，无法获取文件信息") );
			logger.warn("not mulit part request.");
		}

		// 从请求对象里获取操作用户信息
		if (check) {
			try {
				effectivePerson = this.effectivePerson(request);
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system get effectivePerson from request got an exception." );
				logger.error(e);
			}
		}
		// 从URL里获取okrWorkReportBaseInfo id
		if (check) {
			try {
				reportId = this.getURIPart(request.getRequestURI(), "report");
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system get work id from url got an exception.");
				logger.error(e);
			}
		}

		// 根据reportId获取工作汇报信息
		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkReportBaseInfo = emc.find( reportId, OkrWorkReportBaseInfo.class);
				if (null == okrWorkReportBaseInfo) {
					check = false;
					Exception exception = new WorkReportNotExistsException( reportId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkReportQueryByIdException( e, reportId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				upload = new ServletFileUpload();
				fileItemIterator = upload.getItemIterator(request);
				while (fileItemIterator.hasNext()) {
					item = fileItemIterator.next();
					name = item.getFieldName();
					try {
						input = item.openStream();
						if (item.isFormField()) {
							String str = Streams.asString(input);
							if (StringUtils.equals(name, "site")) {
								site = str;
							}
						} else {
							wrap = saveAttachmetFile( effectivePerson.getName(), okrWorkReportBaseInfo, item, site, input );
							wraps.add( wrap );
						}
					} finally {
						input.close();
					}
				}
				if( wraps != null && !wraps.isEmpty() && site!=null && !site.isEmpty() ){
					for( WrapOutId _wrap : wraps ){
						try( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();){
							okrAttachmentFileInfo = emc.find( _wrap.getId(), OkrAttachmentFileInfo.class);
							emc.beginTransaction( OkrAttachmentFileInfo.class );
							okrAttachmentFileInfo.setSite( site );
							emc.check( okrAttachmentFileInfo, CheckPersistType.all);
							emc.commit();
						}
					}
				}
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				Exception exception = new ReportAttachmentUploadException( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		this.result(response, result);
	}

	private WrapOutId saveAttachmetFile( String personName, OkrWorkReportBaseInfo okrWorkReportBaseInfo, FileItemStream item, String site, InputStream input ) throws Exception {
		WrapOutId wrap = null;
		String name = null;
		OkrAttachmentFileInfo fileInfo = null;
		EntityManagerContainer emc = null;
		StorageMapping mapping = null;
	
		if( item != null ){
			
			emc = EntityManagerContainerFactory.instance().create();			
			okrWorkReportBaseInfo = emc.find( okrWorkReportBaseInfo.getId(), OkrWorkReportBaseInfo.class);

			emc.beginTransaction( OkrAttachmentFileInfo.class );
			emc.beginTransaction( OkrWorkReportBaseInfo.class );
			mapping = ThisApplication.context().storageMappings().random( OkrAttachmentFileInfo.class );
			
			fileInfo = concreteAttachment( personName, okrWorkReportBaseInfo, mapping, this.getFileName( item.getName() ), site );
			name = fileInfo.getName();
			
			//先检查对象是否能够被保存，如果能保存，再进行文件存储
			emc.check( fileInfo, CheckPersistType.all);
			
			fileInfo.saveContent( mapping, input, item.getName() );					
			
			if( okrWorkReportBaseInfo.getAttachmentList() == null ){
				okrWorkReportBaseInfo.setAttachmentList( new ArrayList<>() );
			}
			if( !okrWorkReportBaseInfo.getAttachmentList().contains( fileInfo.getId() ) ){
				okrWorkReportBaseInfo.getAttachmentList().add( fileInfo.getId() );
			}
			emc.check( okrWorkReportBaseInfo, CheckPersistType.all);
			fileInfo.setName( name );
			emc.persist( fileInfo, CheckPersistType.all );
			wrap = new WrapOutId( fileInfo.getId());
			
			emc.commit();
			ApplicationCache.notify( OkrAttachmentFileInfo.class );
			ApplicationCache.notify( OkrWorkReportBaseInfo.class );
		}
		return wrap;
	}
	
	private OkrAttachmentFileInfo concreteAttachment( String person, OkrWorkReportBaseInfo okrWorkReportBaseInfo, StorageMapping storage, String name, String site ) throws Exception {
		String fileName = UUID.randomUUID().toString();
		String extension = FilenameUtils.getExtension( name );
		OkrAttachmentFileInfo attachment = new OkrAttachmentFileInfo();
		
		if ( StringUtils.isEmpty(extension) ) {
			throw new Exception("file extension is empty.");
		}else{
			fileName = fileName + "." + extension;
		}
		
		attachment.setCreateTime( new Date() );
		attachment.setLastUpdateTime( new Date() );
		attachment.setExtension( extension );
		attachment.setName( name );
		attachment.setFileName( fileName );
		attachment.setStorage( storage.getName() );
		attachment.setWorkInfoId( okrWorkReportBaseInfo.getId() );
		attachment.setCenterId( okrWorkReportBaseInfo.getCenterId() );
		attachment.setStatus( "正常" );
		attachment.setParentType("工作汇报");
		attachment.setCreatorUid( person );
		attachment.setSite( site );
		attachment.setFileHost( "" );
		attachment.setFilePath( "" );
		attachment.setKey( okrWorkReportBaseInfo.getId() );
		
		return attachment;
	}
}