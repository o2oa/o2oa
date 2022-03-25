package com.x.bbs.assemble.control.jaxrs.sectioninfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionCanNotDelete;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionInsufficientPermission;
import com.x.bbs.assemble.control.jaxrs.sectioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo = null;
		Long count = 0L;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();

		if (check) {
			if ( StringUtils.isEmpty( id ) ) {
				check = false;
				Exception exception = new ExceptionSectionIdEmpty();
				result.error(exception);
			}
		}

		// 查询版块信息，判断版块信息是否存在
		if (check) {
			try {
				sectionInfo = sectionInfoServiceAdv.get(id);
				if ( sectionInfo == null ) {
					check = false;
					Exception exception = new ExceptionSectionNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e, "根据指定ID查询版块信息时发生异常.ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				forumInfo = forumInfoServiceAdv.get( sectionInfo.getForumId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + sectionInfo.getForumId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 判断用户是否有权限进行版块删除：1、系统管理员 2、论坛设置的管理员	
		if ( check ) {
			if ( !ThisApplication.isForumManager( effectivePerson, forumInfo ) ) {//无权限进行版块删除操作
				check = false;
				String forumName = "论坛不存在或者已删除";
				if( forumInfo != null ) {
					forumName = forumInfo.getForumName();
				}
				Exception exception = new ExceptionSectionInsufficientPermission( effectivePerson.getDistinguishedName(), forumName );
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}

		// 判断版块内是否含有子版块
		if (check) {
			try {
				count = sectionInfoServiceAdv.countSubSectionByMainSectionId( id );
				if (count > 0) {
					check = false;
					Exception exception = new ExceptionSectionCanNotDelete("版块中仍存在"+ count +"个子版块，无法继续进行删除操作！ID=" + id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e, "系统在根据版块ID查询主题信息数量时发生异常.ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
				
		// 判断版块内是否含有主题
		if (check) {
			try {
				count = subjectInfoServiceAdv.countByMainAndSubSectionId(id, true);
				if ( count > 0 ) {
					check = false;
					Exception exception = new ExceptionSectionCanNotDelete("版块中仍存在"+ count +"个主题，无法继续进行删除操作！ID=" + id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e, "系统在根据版块ID查询主题信息数量时发生异常.ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}		

		if (check) {
			try {
				// 已经没有子版块和任何贴子信息了，所以只需要删除相应的权限信息即可
				sectionInfoServiceAdv.delete( id );
				wo.setId( id );

				CacheManager.notify( BBSForumInfo.class );
				CacheManager.notify( BBSSectionInfo.class );
				CacheManager.notify( BBSSubjectInfo.class );
				
				operationRecordService.sectionOperation(effectivePerson.getDistinguishedName(), sectionInfo, "DELETE", hostIp, hostName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSectionInfoProcess(e, "根据指定ID删除版块信息时发生异常.ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData( wo );
		return result;
	}

	public static class Wo extends WoId {

	}
}