package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ForumCanNotCreateSectionException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ForumIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ForumInfoNotExistsException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.PersonNotExistsException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionInfoProcessException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionInsufficientPermissionException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionNameEmptyException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionNotExistsException;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.SectionTypeCategoryInvalidException;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.organization.core.express.wrap.WrapPerson;

public class ExcuteSave extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteSave.class );
	
	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInSectionInfo wrapIn ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		Boolean check = true;
		String[] names = null;
		String[] typeCategory = null;
		WrapPerson person = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo_old = null;
		BBSSectionInfo sectionInfo_sub = null;
		BBSSectionInfo sectionInfo = new BBSSectionInfo();
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		
		//校验版块名称
		if( check ){
			if( wrapIn.getSectionName() == null || wrapIn.getSectionName().isEmpty() ){
				check = false;
				Exception exception = new SectionNameEmptyException();
				result.error( exception );
			}
		}
		//校验论坛信息是否有效，并且补充论坛名称
		if (check) {
			if ( wrapIn.getForumId() == null || wrapIn.getForumId().isEmpty() ) {
				check = false;
				Exception exception = new ForumIdEmptyException();
				result.error( exception );
			}
		}
		if (check) {
			try{
				forumInfo = forumInfoServiceAdv.get( wrapIn.getForumId() );
			}catch( Exception e ){
				check = false;
				Exception exception = new SectionInfoProcessException( e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + wrapIn.getForumId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if( forumInfo == null ){
				//论坛信息不存在
				check = false;
				Exception exception = new ForumInfoNotExistsException( wrapIn.getForumId() );
				result.error( exception );
			}else{
				//补充论坛名称
				wrapIn.setForumName( forumInfo.getForumName() );
			}
		}
		//判断论坛是否允许用户创建版块
		if (check) {
			if ( !forumInfo.getSectionCreateAble() ) {
				check = false;
				Exception exception = new ForumCanNotCreateSectionException( forumInfo.getForumName() );
				result.error( exception );
			}
		}
		//判断用户是否是论坛管理员
		if ( check ) {
			if( forumInfo.getForumManagerName() == null || forumInfo.getForumManagerName().isEmpty() ){
				check = false;
				Exception exception = new SectionInsufficientPermissionException( effectivePerson.getName(), forumInfo.getForumName() );
				result.error( exception );
			}else{
				String[] array = forumInfo.getForumManagerName().split(",");
				if( array != null ){
					Boolean isManager = false;
					for( String name : array ){
						if( effectivePerson.getName().equals( name )){
							isManager = true;
						}
					}
					if( !isManager ){
						check = false;
						Exception exception = new SectionInsufficientPermissionException( effectivePerson.getName(), forumInfo.getForumName() );
						result.error( exception );
					}
				}
			}
		}
		if( check ){
			if( wrapIn.getSubjectType() == null || wrapIn.getSubjectType().isEmpty() ){
				wrapIn.setSubjectType( forumInfo.getSubjectType() );
			}
			if( wrapIn.getSubjectType() == null || wrapIn.getSubjectType().isEmpty() ){
				wrapIn.setSubjectType( "新闻|讨论" );
			}
		}
		if( check ){
			if( wrapIn.getTypeCategory() != null && !wrapIn.getTypeCategory().isEmpty() ){
				typeCategory =  wrapIn.getTypeCategory().split("\\|");
				if( typeCategory != null && typeCategory.length > 0 ){
					for( String category : typeCategory ){
						if( !"信息".equals( category ) && !"问题".equals( category ) && !"投票".equals( category )){
							check = false;
							Exception exception = new SectionTypeCategoryInvalidException( category );
							result.error( exception );
						}
					}
				}
			}else{
				wrapIn.setTypeCategory( forumInfo.getTypeCategory() );
			}
		}
		//校验版块管理员（版主）是否存在
		if( check ){
			if( wrapIn.getModeratorNames() != null && !wrapIn.getModeratorNames().isEmpty() ){
				//判断指定的用户是否存在
				names = wrapIn.getModeratorNames().split( "," );
				for( String name : names ){
					try {
						person = userManagerService.getUserByFlag( name );
						if( person == null ){
							check = false;
							Exception exception = new PersonNotExistsException( name );
							result.error( exception );
							break;
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new SectionInfoProcessException( e, "系统根据人员唯一标识查询人员信息时发生异常.Name:" + name );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
						break;
					}
				}				
			}else{
				wrapIn.setModeratorNames( effectivePerson.getName() );
			}
		}
		if( check ){
			wrapIn.setCreatorName( effectivePerson.getName() );
		}
		if( check ){
			try {
				sectionInfo = WrapTools.sectionInfo_wrapin_copier.copy( wrapIn );
				if( sectionInfo.getId() == null || sectionInfo.getId().isEmpty() ){
					sectionInfo.setId( BBSSectionInfo.createId() );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new SectionInfoProcessException( e, "将用户传入的信息转换为一个版块信息对象时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try{
				sectionInfo_old = sectionInfoServiceAdv.get( sectionInfo.getId() );
			}catch( Exception e ){
				check = false;
				Exception exception = new SectionInfoProcessException( e, "根据指定ID查询版块信息时发生异常.ID:" + sectionInfo.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			//如果主版的ID为空，或者主版ID与当前ID一致，则版块为一级版块，主版块
			if( sectionInfo.getMainSectionId() == null || sectionInfo.getMainSectionId().isEmpty()  || sectionInfo.getId().equals( sectionInfo.getMainSectionId() ) ){
				sectionInfo.setMainSectionId( sectionInfo.getId() );
				sectionInfo.setMainSectionName( sectionInfo.getSectionName() );
				sectionInfo.setSectionLevel( "主版块" );
			}else{
				try{
					sectionInfo_sub = sectionInfoServiceAdv.get( sectionInfo.getMainSectionId() );
				}catch( Exception e ){
					check = false;
					Exception exception = new SectionInfoProcessException( e, "根据指定主版ID查询子版块信息时发生异常.MainId:" + sectionInfo.getMainSectionId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
				if( check ){
					if( sectionInfo_sub != null ){
						sectionInfo.setMainSectionId( sectionInfo_sub.getId() );
						sectionInfo.setMainSectionName( sectionInfo_sub.getSectionName() );
						sectionInfo.setSectionLevel( "子版块" );
					}else{
						check = false;
						Exception exception = new SectionNotExistsException( sectionInfo.getMainSectionId() );
						result.error( exception );
					}
				}
			}
		}
		
		if( check ){
			try {
				sectionInfo = sectionInfoServiceAdv.save( sectionInfo );
				result.setData( new WrapOutId( sectionInfo.getId() ) );
				if( sectionInfo_old != null ){
					operationRecordService.sectionOperation( effectivePerson.getName(), sectionInfo, "MODIFY", hostIp, hostName );
				}else{
					operationRecordService.sectionOperation( effectivePerson.getName(), sectionInfo, "CREATE", hostIp, hostName );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new SectionInfoProcessException( e, "保存版块信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				//论坛信息添加成功，继续添加权限和角色信息
				permissionInfoService.createSectionPermission( sectionInfo );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "system create forum permission info got an exception!" );
				logger.error(e);
				
			}
		}
		
		if( check ){
			try {
				//论坛信息添加成功，继续添加权限和角色信息
				roleInfoService.createSectionRole( sectionInfo );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "system create section role info got an exception!" );
				logger.error(e);
			}
		}
		if( check ){
			if( sectionInfo.getMainSectionId() != null && !sectionInfo.getMainSectionId().equalsIgnoreCase( sectionInfo.getId() )){
				try {
					//如果该版块是子版块，那么主版块的角色也需要重新维护
					roleInfoService.createSectionRole( sectionInfo.getMainSectionId() );
				} catch (Exception e) {
					check = false;
					result.error( e );
					logger.warn( "system create main section role info got an exception!" );
					logger.error(e);
				}
			}
		}
		if( check ){
			try {
				//该版块所处的论坛的角色需要重新维护
				roleInfoService.createForumRole( sectionInfo.getForumId() );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "system create forum role info got an exception!" );
				logger.error(e);
			}
		}
		if( check ){//检查版主权限的设置
			try {
				sectionInfoServiceAdv.checkSectionManager( sectionInfo );
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "system bind role for section manager got an exception!" );
				logger.error(e);
			}
		}
		if( check ){
			if( sectionInfo.getMainSectionId() != null && !sectionInfo.getMainSectionId().equalsIgnoreCase( sectionInfo.getId() )){
				try {
					sectionInfoServiceAdv.checkSectionManager( sectionInfo.getMainSectionId() );
				} catch (Exception e) {
					check = false;
					result.error( e );
					logger.warn( "system bind role for section manager got an exception!" );
					logger.error(e);
				}
			}
		}
		if( check ){
			try {
				forumInfoServiceAdv.checkForumManager(forumInfo);
			} catch (Exception e) {
				check = false;
				result.error( e );
				logger.warn( "system bind role for section manager got an exception!" );
				logger.error(e);
			}
		}
		return result;
	}

}