package com.x.bbs.assemble.control.jaxrs.foruminfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumCanNotDelete;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoIdEmpty;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoProcess;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionInsufficientPermissions;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		BBSForumInfo forumInfo = null;
		Long sectionCount = 0L;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();

		if (check) {
			//删除操作权限判断
			try {
				if ( !ThisApplication.isBBSManager(effectivePerson) ) {
					check = false;
					logger.warn("用户没有BBSManager角色，并且也不是系统管理员！USER：" + effectivePerson.getDistinguishedName());
					Exception exception = new ExceptionInsufficientPermissions(effectivePerson.getDistinguishedName(), ThisApplication.BBSMANAGER );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionInsufficientPermissions(effectivePerson.getDistinguishedName(), ThisApplication.BBSMANAGER );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionForumInfoIdEmpty();
				result.error(exception);
			}
		}
		if (check) { // 查询论坛信息是否存在
			try {
				forumInfo = forumInfoServiceAdv.get(id);
				if (forumInfo == null) {
					check = false;
					Exception exception = new ExceptionForumInfoNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionForumInfoProcess(e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			// 查询论坛是否仍存在版块信息
			try {
				sectionCount = sectionInfoServiceAdv.countMainSectionByForumId(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionForumInfoProcess(e, "系统在根据论坛ID查询版块信息数量时发生异常.ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if (sectionCount > 0) {
				check = false;
				logger.warn("论坛[" + forumInfo.getForumName() + "]中仍存在" + sectionCount + "个版块，无法继续进行删除操作！ID=" + id);
				Exception exception = new ExceptionForumCanNotDelete(
						"论坛[" + forumInfo.getForumName() + "]中仍存在" + sectionCount + "个版块，无法继续进行删除操作！");
				result.error(exception);
			}
		}
		
		if (check) {
			//已经没有任何版块了，可以删除论坛信息以及论坛的权限信息
			try {
				forumInfoServiceAdv.delete(id);
				
				wo.setId( forumInfo.getId() );
				
				CacheManager.notify( BBSForumInfo.class );
				CacheManager.notify( BBSSectionInfo.class );
				CacheManager.notify( BBSSubjectInfo.class );
				operationRecordService.forumOperation(effectivePerson.getDistinguishedName(), forumInfo, "DELETE", hostIp, hostName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionForumInfoProcess(e, "根据ID删除BBS论坛分区信息时发生异常.ID:" + id);
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