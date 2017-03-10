package com.x.attendance.assemble.control.jaxrs.fileimport;

import com.x.base.core.exception.PromptException;

class AttendanceImportFileDataCacheNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;
	
	public AttendanceImportFileDataCacheNotExistsException( String id ) {
		super("全局数据检查缓存中文件[ID="+id+"]的检查数据结果为空，无法继续导入数据，需要重新进行数据检查." );
	}
}
