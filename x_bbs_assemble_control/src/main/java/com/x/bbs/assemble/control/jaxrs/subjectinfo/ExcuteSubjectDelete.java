package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectNotExistsException;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.SubjectQueryByIdException;
import com.x.bbs.entity.BBSSubjectInfo;

public class ExcuteSubjectDelete extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSubjectDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		BBSSubjectInfo subjectInfo = null;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Boolean check = true;		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new SubjectIdEmptyException();
				result.error( exception );
			}
		}		
		//判断主题信息是否存在
		if( check ){
			try {
				subjectInfo = subjectInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new SubjectQueryByIdException( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}		
		if( check ){
			if( subjectInfo == null ){
				check = false;
				Exception exception = new SubjectNotExistsException( id );
				result.error( exception );
			}
		}		
		try {
			subjectInfoServiceAdv.delete( id );//删除主题同时要将所有的回复内容全部删除
			result.setData( new WrapOutId(id) );
			//记录操作日志
			operationRecordService.subjectOperation( effectivePerson.getName(), subjectInfo, "DELETE", hostIp, hostName );
		} catch ( Exception e ) {
			check = false;
			Exception exception = new SubjectInfoProcessException( e, "根据指定ID删除主题信息时发生异常.ID:" + id );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		return result;
	}

}