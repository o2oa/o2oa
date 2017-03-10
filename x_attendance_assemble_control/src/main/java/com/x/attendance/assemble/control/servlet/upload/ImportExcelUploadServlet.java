package com.x.attendance.assemble.control.servlet.upload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.x.attendance.assemble.common.excel.reader.ExcelReaderUtil;
import com.x.attendance.assemble.common.excel.reader.IRowReader;
import com.x.attendance.assemble.control.ApplicationGobal;
import com.x.attendance.assemble.control.jaxrs.fileimport.AttendancePersonExcelReader;
import com.x.attendance.assemble.control.jaxrs.fileimport.CacheImportFileStatus;
import com.x.attendance.assemble.control.jaxrs.fileimport.CacheImportRowDetail;
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;

@WebServlet("/servlet/import/*")
@MultipartConfig
public class ImportExcelUploadServlet extends AbstractServletAction {

	private static final long serialVersionUID = 5628571943877405247L;
	private Logger logger = LoggerFactory.getLogger( ImportExcelUploadServlet.class );
	
	@HttpMethodDescribe( value = "上传附件 servlet/import", response = WrapOutId.class )
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<List<WrapOutId>> result = new ActionResult<List<WrapOutId>>();
		List<WrapOutId> wraps = new ArrayList<WrapOutId>();
		try {
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("[UploadServlet]not mulit part request.");
			}
			EffectivePerson effectivePerson = this.effectivePerson(request);
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator fileItemIterator = upload.getItemIterator(request);		
			FileItemStream item = null;
			AttendanceImportFileInfo importFile = null;
			String filename = null, extension = null;
			InputStream is = null;
			long length = 0;
			byte[] file_content = null;
			while ( fileItemIterator.hasNext() ) {
				is = item.openStream();
				item = fileItemIterator.next();
				file_content = inputStreamToByte( is );
				filename = item.getName();
				extension = FilenameUtils.getExtension( filename );
				length = file_content.length;
				importFile = new AttendanceImportFileInfo();
				importFile.setExtension( extension );
				importFile.setFileBody( file_content );
				importFile.setFileName( filename );
				importFile.setName( filename );
				importFile.setCreatorUid( effectivePerson.getName() );
				importFile.setCreateTime( new Date() );
				importFile.setLastUpdateTime( new Date() );
				importFile.setLength( length );
				//将所有的附件信息存储到数据库里
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {	
					emc.beginTransaction( AttendanceImportFileInfo.class);
					emc.persist( importFile, CheckPersistType.all);
					emc.commit();
					logger.info( "import file upload completed." );
				}catch(Exception ex){
					ex.printStackTrace();
				}
				wraps.add( new WrapOutId( importFile.getId() ));
				
				//直接解析所有的数据，保存正确和错误的数据信息到两个Blob列里
				IRowReader reader = new AttendancePersonExcelReader();
				try {
					ExcelReaderUtil.readExcel2003( reader, is, importFile.getId(), 1 );
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				CacheImportFileStatus cacheImportFileStatus = ApplicationGobal.importFileCheckResultMap.get( importFile.getId() );
				
				List<CacheImportRowDetail> detailList = cacheImportFileStatus.getDetailList();
				List<CacheImportRowDetail> detailList_ok = new ArrayList<CacheImportRowDetail>();
				List<CacheImportRowDetail> detailList_error = new ArrayList<CacheImportRowDetail>();
				if( detailList != null ){
					for( CacheImportRowDetail detail : detailList ){
						if( "success".equals( detail.getCheckStatus() ) ){
							detailList_ok.add( detail );
						}else{
							detailList_error.add( detail );
						}
					}
				}
				
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					emc.find( importFile.getId(), AttendanceImportFileInfo.class );
					Gson gson = XGsonBuilder.instance();
					emc.beginTransaction( AttendanceImportFileInfo.class);
					
					if( detailList_ok != null ){
						importFile.setDataContent( gson.toJson(detailList_ok) );
					}
					if( detailList_error != null ){
						importFile.setErrorContent( gson.toJson(detailList_error) );
					}
					
					emc.persist( importFile, CheckPersistType.all );
					emc.commit();
					logger.info( "import file upload completed." );
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			result.setData(wraps);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		this.result( response, result );
	}

	private byte [] inputStreamToByte(InputStream is) throws IOException { 
        ByteArrayOutputStream bAOutputStream = new ByteArrayOutputStream(); 
        int ch; 
        while((ch = is.read() ) != -1){ 
            bAOutputStream.write(ch); 
        } 
        byte data [] =bAOutputStream.toByteArray(); 
        bAOutputStream.close(); 
        return data; 
    }
}