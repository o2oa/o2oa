package com.x.okr.assemble.control.jaxrs.export;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionStatisticReportContentExport extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionStatisticReportContentExport.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson,  String flag ) {
		ActionResult<Wo> result = new ActionResult<>();
		File dir = new File("download/temp");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		byte[] byteArray = getByteFromFile("download/temp/export_" + flag + ".xls");
		try {
			Wo wo = new Wo( byteArray,  this.contentType(false, "汇报内容统计.xls"),  this.contentDisposition(false, "汇报内容统计.xls"));
			result.setData( wo );
		} catch (Exception e) {
			logger.warn("system export file got an exception");
			logger.error(e);
		}
		return result;
	}


	private byte[] getByteFromFile(String fileFullName) {
		ByteArrayOutputStream buffer = null;
		BufferedInputStream input = null;
		
		File file = new File(fileFullName);
		if (file.exists()) {
			try {
				input = new BufferedInputStream(new FileInputStream(file));
				buffer = new ByteArrayOutputStream();
			    int nRead;
			    byte[] data = new byte[1024];
			    while ((nRead = input.read(data, 0, data.length)) != -1) {
			        buffer.write(data, 0, nRead);
			    }
			    buffer.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					buffer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					buffer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (file.exists()) {
					file.delete();
				}
			}	
		    return buffer.toByteArray();
		}
		return null;
	}


	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}
}
