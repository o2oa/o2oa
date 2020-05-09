package com.x.attendance.assemble.control.jaxrs.attachment;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;

/**
 * 导入的文件没有用到文件存储器，是直接放在数据库中的BLOB列
 *
 */
public class ActionImportFileDownload extends BaseAction {
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, Boolean stream ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceImportFileInfo  file = importFileInfoServiceAdv.get(id);
		if ( null == file ) {
			throw new Exception("文件信息不存在。id:" + id ) ;
		}else {
			Wo wo = new Wo(file.getFileBody(), 
					this.contentType(stream, file.getName()), 
					this.contentDisposition(stream, file.getName()));
			result.setData(wo);
		}
		return result;
	}

	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}

}
