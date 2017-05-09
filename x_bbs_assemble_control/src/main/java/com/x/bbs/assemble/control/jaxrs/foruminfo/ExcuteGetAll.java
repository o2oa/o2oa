package com.x.bbs.assemble.control.jaxrs.foruminfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoProcessException;
import com.x.bbs.entity.BBSForumInfo;

public class ExcuteGetAll extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteGetAll.class );
	
	protected ActionResult<List<WrapOutForumInfo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<WrapOutForumInfo>> result = new ActionResult<>();
		List<WrapOutForumInfo> wraps = new ArrayList<>();
		List<BBSForumInfo> forumInfoList = null;
		Boolean check = true;
		
		if( check ){
			//从数据库查询论坛列表
			try {
				forumInfoList = forumInfoServiceAdv.listAll();
			} catch (Exception e) {
				Exception exception = new ForumInfoProcessException( e, "系统在获取所有BBS论坛分区信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}	
		}
		if( check ){
			if( forumInfoList != null && !forumInfoList.isEmpty() ){
				try {
					wraps = WrapTools.forumInfo_wrapout_copier.copy( forumInfoList );
				} catch (Exception e) {
					Exception exception = new ForumInfoProcessException( e, "系统在转换所有BBS论坛分区信息为输出对象时发生异常." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
				result.setData( wraps );
			}
		}
		return result;
	}

}