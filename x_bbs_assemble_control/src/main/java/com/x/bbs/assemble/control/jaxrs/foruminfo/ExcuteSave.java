package com.x.bbs.assemble.control.jaxrs.foruminfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumNameEmptyException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumTypeCategoryEmptyException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumTypeCategoryInvalidException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.InsufficientPermissionsException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.PersonNotExistsException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.PersonQueryException;
import com.x.bbs.entity.BBSForumInfo;
import com.x.organization.core.express.wrap.WrapPerson;

public class ExcuteSave extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInForumInfo wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		Boolean check = true;
		String[] names = null;
		String[] typeCategory = null;
		WrapPerson person = null;
		BBSForumInfo forumInfo_old = null;
		BBSForumInfo forumInfo = new BBSForumInfo();
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
		
		//校验论坛名称
		if( check ){
			if( wrapIn.getForumName() == null || wrapIn.getForumName().isEmpty() ){
				check = false;
				Exception exception = new ForumNameEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		//校验论坛分类:信息|问题|投票,只能是这三类中的
		if( check ){
			if( wrapIn.getTypeCategory() == null || wrapIn.getTypeCategory().isEmpty() ){
				check = false;
				Exception exception = new ForumTypeCategoryEmptyException();
				result.error( exception );
				//logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			typeCategory =  wrapIn.getTypeCategory().split("\\|");
			if( typeCategory != null && typeCategory.length > 0 ){
				for( String category : typeCategory ){
					if( !"信息".equals( category ) && !"问题".equals( category ) && !"投票".equals( category )){
						check = false;
						Exception exception = new ForumTypeCategoryInvalidException( category );
						result.error( exception );
						//logger.error( e, effectivePerson, request, null);
					}
				}
			}
		}
		if( check ){
			if( wrapIn.getSubjectType() == null || wrapIn.getSubjectType().isEmpty() ){
				wrapIn.setSubjectType( "新闻|讨论" );
			}
		}
		if( check ){
			if( wrapIn.getForumManagerName() != null && !wrapIn.getForumManagerName().isEmpty() ){
				//判断指定的用户是否存在
				names = wrapIn.getForumManagerName().split( "," );
				for( String name : names ){
					try {
						person = userManagerService.getUserByFlag( name );
						if( person == null ){
							check = false;
							Exception exception = new PersonNotExistsException( name );
							result.error( exception );
							//logger.error( e, effectivePerson, request, null);
							break;
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new PersonQueryException( e, name );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
						break;
					}
				}				
			}else{
				wrapIn.setForumManagerName( effectivePerson.getName() );
			}
		}
		if( check ){
			wrapIn.setCreatorName( effectivePerson.getName() );
		}
		if( check ){
			try {
				forumInfo = WrapTools.forumInfo_wrapin_copier.copy( wrapIn );
			} catch (Exception e) {
				check = false;
				Exception exception = new ForumInfoProcessException( e, "将用户传入的信息转换为一个论坛分区信息对象时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( forumInfo.getId() != null && !forumInfo.getId().isEmpty() ){
				try {
					forumInfo_old = forumInfoServiceAdv.get( forumInfo.getId() );
				} catch (Exception e) {
					check = false;
					Exception exception = new ForumInfoProcessException( e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + forumInfo.getId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			try {
				forumInfo = forumInfoServiceAdv.save( forumInfo );
				wrap = new WrapOutId( forumInfo.getId() );
				result.setData( wrap );
				if( forumInfo_old != null ){
					operationRecordService.forumOperation( effectivePerson.getName(), forumInfo, "MODIFY", hostIp, hostName );
				}else{
					operationRecordService.forumOperation( effectivePerson.getName(), forumInfo, "CREATE", hostIp, hostName );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ForumInfoProcessException( e, "系统在保存BBS论坛分区信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				//论坛信息添加成功，继续添加权限和角色信息
				permissionInfoService.createForumPermission( forumInfo );
			} catch (Exception e) {
				check = false;
				logger.warn( "system create forum permission info got an exception!" );
				logger.error(e);
			}
		}
		if( check ){
			try {
				//论坛信息添加成功，继续添加权限和角色信息
				roleInfoService.createForumRole( forumInfo );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "system create forum role info got an exception!" );
				logger.error(e);
			}
		}
		if( check ){//检查论坛管理员权限的设置
			try {
				forumInfoServiceAdv.checkForumManager( forumInfo );
			} catch (Exception e) {
				logger.warn( "system bind role for forum manager got an exception!" );
				logger.error(e);
			}
		}
		return result;
	}

}