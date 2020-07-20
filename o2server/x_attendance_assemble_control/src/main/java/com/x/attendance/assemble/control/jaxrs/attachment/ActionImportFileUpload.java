package com.x.attendance.assemble.control.jaxrs.attachment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.attendance.assemble.common.excel.reader.ExcelReaderUtil;
import com.x.attendance.assemble.common.excel.reader.IRowReader;
import com.x.attendance.assemble.common.excel.reader.ImportExcelReader;
import com.x.attendance.assemble.control.processor.monitor.StatusSystemImportOpt;
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

public class ActionImportFileUpload {
	private static Logger logger = LoggerFactory.getLogger(ActionImportFileUpload.class);

	public static class Wo extends WoId {

	}

	protected ActionResult<WrapOutId> execute(HttpServletRequest request, EffectivePerson effectivePerson, byte[] bytes, FormDataContentDisposition disposition) {
		ActionResult<WrapOutId> result = new ActionResult<>();
		AttendanceImportFileInfo importFile = null;
		String fileName = null;
		Boolean check = true;
		
		try {
			fileName = FilenameUtils.getName(new String(disposition.getFileName().getBytes(DefaultCharset.name_iso_8859_1), DefaultCharset.name));
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionGetFileName(e);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		
		if( check ) {
			/** 禁止不带扩展名的文件上传 */
			if (StringUtils.isEmpty(fileName)) {
				check = false;
				Exception exception = new ExceptionEmptyExtension(fileName);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			} 
		}
		
		if( check ) {
			importFile = new AttendanceImportFileInfo();
			importFile.setId( AttendanceImportFileInfo.createId() );
			importFile.setExtension( FilenameUtils.getExtension(fileName) );
			importFile.setFileBody( bytes );
			importFile.setFileName( fileName );
			importFile.setName(fileName);
			importFile.setCreatorUid( effectivePerson.getDistinguishedName() );
			importFile.setCreateTime( new Date() );
			importFile.setLastUpdateTime( new Date() );
			importFile.setLength( Long.parseLong(bytes.length+"") );
		}
		
		if( check ) {
			try {
				checkDataInFile( importFile.getId(), fileName, bytes, effectivePerson );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCheckImportData(e);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}

		if( check ) {
			try {
				saveFile( importFile );
				result.setData(new WrapOutId( importFile.getId()));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCheckImportData(e);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}		
		return result;
	}
	
	private void saveFile( AttendanceImportFileInfo importFile ) throws Exception {		
		// 将所有的附件信息存储到数据库里
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			emc.beginTransaction(AttendanceImportFileInfo.class);
			emc.persist( importFile, CheckPersistType.all );
			emc.commit();
		} catch (Exception ex) {
			throw ex;
		}
	}
	
	private void checkDataInFile( String id, String fileName, byte[] content, EffectivePerson effectivePerson ) throws ExceptionFileImportProcess {
		// 将文件到应用服务器形成本地文件
		String importFilePath = "./servers/applicationServer/work/x_attendance_assemble_control/temp/";
		String importFileName = "import_" + ( new Date() ).getTime() + "_" + fileName;
		FileOutputStream output = null;
		try {
			System.out.println("aaaaaaaaaaaaaaaaaaaa---0");
			File dir = new File( importFilePath );
			System.out.println("aaaaaaaaaaaaaaaaaaaa---1");
			if ( !dir.exists() ) {
				dir.mkdir();
			}
			System.out.println("aaaaaaaaaaaaaaaaaaaa---2");
			File file = new File(importFilePath + importFileName);
			System.out.println("aaaaaaaaaaaaaaaaaaaa---3");
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			if(file.exists()){
				System.out.println("aaaaaaaaaaaaaaaaaaaa---4");
			}
			
			try {
				output = new FileOutputStream(importFilePath + importFileName);
				System.out.println("aaaaaaaaaaaaaaaaaaaa---5");
				output.write( content );
				output.flush();
				System.out.println("aaaaaaaaaaaaaaaaaaaa---6");
			} catch (Exception e) {
				logger.warn( "将文件写入到本地文件时发生异常.ID:" + id + ", FileName:" + fileName );
				logger.error( e );
			} finally {
				output.close();
			}
		} catch (Exception e) {
			logger.warn( "将文件写入到本地文件时发生异常.");
			logger.error( e );
		}
		
		StatusSystemImportOpt.getInstance().cleanCacheImportFileStatus( id );
		StatusSystemImportOpt.getInstance().getCacheImportFileStatus( id ).setFilePath( importFilePath + importFileName );
		
		// 然后进行数据检查
		IRowReader reader = new ImportExcelReader();
		try {
			ExcelReaderUtil.readExcel( reader, importFilePath + importFileName, id, 1 );
		} catch (Exception e) {
			logger.warn( "解析本地Excle文件时发生异常.ID:" + id + ", FileName:" + importFilePath + importFileName );
			logger.error( e );
		}
	}

}
