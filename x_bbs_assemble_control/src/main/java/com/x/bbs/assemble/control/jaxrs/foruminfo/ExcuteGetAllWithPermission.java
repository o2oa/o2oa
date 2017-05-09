package com.x.bbs.assemble.control.jaxrs.foruminfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.MethodExcuteResult;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoProcessException;
import com.x.bbs.entity.BBSForumInfo;

public class ExcuteGetAllWithPermission extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteGetAllWithPermission.class );
	
	protected ActionResult<List<WrapOutForumInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutForumInfo>> result = new ActionResult<>();
		List<WrapOutForumInfo> wraps = new ArrayList<>();
		List<BBSForumInfo> forumInfoList = null;
		List<String> ids = null;
		Boolean check = true;
		MethodExcuteResult methodExcuteResult = null;

		if( check ){
			methodExcuteResult = userManagerService.getViewForumIdsFromUserPermission( effectivePerson );
			if( methodExcuteResult.getSuccess() ){
				if( methodExcuteResult.getBackObject() != null ){
					ids = (List<String>)methodExcuteResult.getBackObject();
				}else{
					ids = new ArrayList<String>();
				}
			}else{
				result.error( methodExcuteResult.getError() );
				logger.error( methodExcuteResult.getError(), effectivePerson, request, null);
			}
		}
		if( check ){//从数据库查询论坛列表
			try {
				forumInfoList = forumInfoServiceAdv.listAllViewAbleForumWithUserPermission( ids );
				if( forumInfoList == null ){
					forumInfoList = new ArrayList<BBSForumInfo>();
				}
			} catch (Exception e) {
				Exception exception = new ForumInfoProcessException( e, "根据ID列表查询论坛信息列表时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){//转换论坛列表为输出格式
			if( forumInfoList != null && !forumInfoList.isEmpty() ){
				try {
					wraps = WrapTools.forumInfo_wrapout_copier.copy( forumInfoList );
				} catch (Exception e) {
					Exception exception = new ForumInfoProcessException( e, "系统将论坛信息对象转换为输出数据时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
				result.setData( wraps );
			}
		}
		return result;
	}

}