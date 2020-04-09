package com.x.teamwork.assemble.control.jaxrs.chat;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionGetContent extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGetContent.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		String chatContent = null;
		Boolean check = true;
		
		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ChatIDEmptyException();
				result.error( exception );
			}
		}

		if( check ){
			try {
				chatContent = chatQueryService.getContent( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ChatQueryException( e, "系统根据ID查询指定工作评论信息内容时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				wrap = new Wo();
				wrap.setText( chatContent );
				result.setData( wrap );
			} catch (Exception e) {
				check = false;
				Exception exception = new ChatQueryException( e, "将查询结果转换为可以输出的信息时发生异常." );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoText  {
	}

}