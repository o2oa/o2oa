package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ForumInfoNotExistsException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionCanNotDeleteException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionInsufficientPermissionException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionNotExistsException;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ExcuteDelete extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo  = null;
		Long subjectCount = 0L;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new SectionIdEmptyException();
				result.error( exception );
			}
		}
		
		//查询版块信息，判断版块信息是否存在
		if( check ){
			try{
				sectionInfo = sectionInfoServiceAdv.get(id);
			}catch( Exception e ){
				check = false;
				Exception exception = new SectionInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( sectionInfo == null ){
				check = false;
				Exception exception = new SectionNotExistsException( id );
				result.error( exception );
			}
		}
		
		if (check) {
			try{
				forumInfo = forumInfoServiceAdv.get( sectionInfo.getForumId() );
			}catch( Exception e ){
				check = false;
				Exception exception = new SectionInfoProcessException( e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + sectionInfo.getForumId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if( forumInfo == null ){
				//论坛信息不存在
				check = false;
				Exception exception = new ForumInfoNotExistsException( sectionInfo.getForumId() );
				result.error( exception );
			}
		}
		
		//判断用户是否是论坛管理员
		if (check) {
			if (forumInfo.getForumManagerName() == null || forumInfo.getForumManagerName().isEmpty()) {
				check = false;
				Exception exception = new SectionInsufficientPermissionException(effectivePerson.getName(), forumInfo.getForumName());
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			} else {
				String[] array = forumInfo.getForumManagerName().split(",");
				if (array != null) {
					Boolean isManager = false;
					for (String name : array) {
						if (effectivePerson.getName().equals(name)) {
							isManager = true;
						}
					}
					if (!isManager) {
						check = false;
						Exception exception = new SectionInsufficientPermissionException(effectivePerson.getName(),
								forumInfo.getForumName());
						result.error(exception);
						logger.error(exception, effectivePerson, request, null);
					}
				}
			}
		}
		
		//判断版块内是否含有主题
		if (check) {
			try{
				subjectCount = subjectInfoServiceAdv.countByMainAndSubSectionId( id, true );
			}catch( Exception e){
				check = false;
				Exception exception = new SectionInfoProcessException( e, "系统在根据版块ID查询主题信息数量时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if( subjectCount > 0 ){
				check = false;
				Exception exception = new SectionCanNotDeleteException( "版块中仍存在子版块或者主题，无法继续进行删除操作！ID=" + id );
				result.error( exception );
			}
		}
				
		if( check ){
			try {
				sectionInfoServiceAdv.delete( id );
				wrap = new WrapOutId( id );
				result.setData( wrap );
				operationRecordService.sectionOperation( effectivePerson.getName(), sectionInfo, "DELETE", hostIp, hostName );
			} catch (Exception e) {
				check = false;
				Exception exception = new SectionInfoProcessException( e, "根据指定ID删除版块信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

}