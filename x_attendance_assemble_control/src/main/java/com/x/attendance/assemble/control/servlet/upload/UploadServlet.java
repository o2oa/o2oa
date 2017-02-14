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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.application.servlet.FileUploadServletTools;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.http.annotation.HttpMethodDescribe;

@WebServlet("/servlet/upload/*")
@MultipartConfig
public class UploadServlet extends HttpServlet {

	private static final long serialVersionUID = 5628571943877405247L;
	private Logger logger = LoggerFactory.getLogger( UploadServlet.class );
	
	@HttpMethodDescribe(value = "上传附件 servlet/upload", response = WrapOutId.class)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ActionResult<List<WrapOutId>> result = new ActionResult<List<WrapOutId>>();
		List<WrapOutId> wraps = new ArrayList<WrapOutId>();
		try {
			request.setCharacterEncoding("UTF-8");
			if (!ServletFileUpload.isMultipartContent(request)) {
				throw new Exception("[UploadServlet]not mulit part request.");
			}
			EffectivePerson effectivePerson = FileUploadServletTools.effectivePerson(request);			
			/* 附件分类信息 */
			ServletFileUpload upload = new ServletFileUpload();
			FileItemIterator fileItemIterator = upload.getItemIterator(request);		
			FileItemStream item = null;
			AttendanceImportFileInfo importFile = null;
			String filename = null, extension = null;
			long length = 0;
			byte[] file_content = null;
			while ( fileItemIterator.hasNext() ) {
				item = fileItemIterator.next();
				file_content = inputStreamToByte( item.openStream() );
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
					logger.error( "system save import file got an exception.", ex );
				}
				wraps.add( new WrapOutId( importFile.getId()));
			}
			result.setData(wraps);
		} catch (Exception e) {
			e.printStackTrace();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		FileUploadServletTools.result( response, result );
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