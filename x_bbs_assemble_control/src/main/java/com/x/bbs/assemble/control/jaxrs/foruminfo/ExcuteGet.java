package com.x.bbs.assemble.control.jaxrs.foruminfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.WrapTools;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoIdEmptyException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoNotExistsException;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ForumInfoProcessException;
import com.x.bbs.entity.BBSForumInfo;

public class ExcuteGet extends ExcuteBase {
	
	private Logger logger = LoggerFactory.getLogger( ExcuteGet.class );
	
	protected ActionResult<WrapOutForumInfo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutForumInfo> result = new ActionResult<>();
		WrapOutForumInfo wrap = null;
		BBSForumInfo forumInfo = null;
		Boolean check = true;
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ForumInfoIdEmptyException();
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				forumInfo = forumInfoServiceAdv.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ForumInfoProcessException( e, "系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			if( forumInfo != null ){
				try {
					wrap = WrapTools.forumInfo_wrapout_copier.copy( forumInfo );
					result.setData( wrap );
				} catch (Exception e) {
					check = false;
					Exception exception = new ForumInfoProcessException( e, "系统将论坛信息对象转换为输出数据时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}else{
				Exception exception = new ForumInfoNotExistsException( id );
				result.error( exception );
			}
		}
		return result;
	}

}