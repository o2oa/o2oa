package com.x.bbs.assemble.control.jaxrs.foruminfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumCanNotDeleteException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoNotExistsException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.InsufficientPermissionsException;
import com.x.bbs.entity.BBSForumInfo;

public class ExcuteDelete extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteDelete.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		BBSForumInfo forumInfo = null;
		Long sectionCount = 0L;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		
		if( check ){
			try {
				if( !userManagerService.isHasRole( effectivePerson.getName(), "BBSSystemAdmin") && !effectivePerson.isManager() ){
					check = false;
					logger.warn("用户没有BBSSystemAdmin角色，并且也不是系统管理员！USER：" + effectivePerson.getName() );
					Exception exception = new InsufficientPermissionsException( effectivePerson.getName(), "BBSSystemAdmin" );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new InsufficientPermissionsException( effectivePerson.getName(), "BBSSystemAdmin" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ForumInfoIdEmptyException();
				result.error( exception );
			}
		}
		if( check ){ //查询论坛信息是否存在
			try{
				forumInfo = forumInfoServiceAdv.get(id);
			}catch( Exception e ){
				check = false;
				Exception exception = new ForumInfoProcessException( e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( forumInfo == null ){
				check = false;
				Exception exception = new ForumInfoNotExistsException( id );
				result.error( exception );
			}
		}
		if( check ){
			//查询论坛是否仍存在版块信息
			try{
				sectionCount = sectionInfoServiceAdv.countMainSectionByForumId( id );
			}catch( Exception e ){
				check = false;
				Exception exception = new ForumInfoProcessException( e, "系统在根据论坛ID查询版块信息数量时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( sectionCount > 0 ){
				check = false;
				logger.warn( "论坛["+forumInfo.getForumName()+"]中仍存在"+ sectionCount+"个主版块，无法继续进行删除操作！ID=" + id  );
				Exception exception = new ForumCanNotDeleteException( "论坛["+forumInfo.getForumName()+"]中仍存在"+ sectionCount+"个版块，无法继续进行删除操作！" );
				result.error( exception );
			}
		}
		if( check ){
			try {
				forumInfoServiceAdv.delete( id );
				wrap = new WrapOutId( id );
				result.setData( wrap );
				operationRecordService.forumOperation( effectivePerson.getName(), forumInfo, "DELETE", hostIp, hostName );
			} catch (Exception e) {
				check = false;
				Exception exception = new ForumInfoProcessException( e, "根据ID删除BBS论坛分区信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){//检查论坛管理员权限的设置
			try {
				forumInfoServiceAdv.deleteForumManager( id );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "system delete role for forum manager got an exception!" );
				logger.error(e);
			}
		}
		return result;
	}

}