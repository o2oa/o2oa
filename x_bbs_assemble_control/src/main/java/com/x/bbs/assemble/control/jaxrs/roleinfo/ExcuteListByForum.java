package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ForumIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ForumInfoNotExistsException;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.RoleInfoProcessException;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSRoleInfo;

public class ExcuteListByForum extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteListByForum.class );
	
	protected ActionResult<List<WrapOutRoleInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, WrapInFilter wrapIn ) throws Exception {
		ActionResult<List<WrapOutRoleInfo>> result = new ActionResult<>();
		List<WrapOutRoleInfo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		BBSForumInfo forumInfo = null;
		Boolean check = true;
		if (check) {
			if (wrapIn.getForumId() == null || wrapIn.getForumId().isEmpty()) {
				check = false;
				Exception exception = new ForumIdEmptyException();
				result.error( exception );
			}
		}
		if (check) {
			try {
				forumInfo = forumInfoServiceAdv.get(wrapIn.getForumId());
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + wrapIn.getForumId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (forumInfo == null) {
				check = false;
				Exception exception = new ForumInfoNotExistsException( wrapIn.getForumId() );
				result.error( exception );
			}
		}
		if (check) {
			try {
				roleInfoList = roleInfoService.listRoleByForumId(wrapIn.getForumId());
			} catch (Exception e) {
				check = false;
				Exception exception = new RoleInfoProcessException( e, "系统在根据论坛分区ID查询角色信息列表时发生异常.Forum:" + wrapIn.getForumId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (roleInfoList != null) {
				try {
					wraps = WrapTools.roleInfo_wrapout_copier.copy(roleInfoList);
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new RoleInfoProcessException( e, "系统在转换所有BBS角色信息为输出对象时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

}