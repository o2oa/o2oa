package com.x.teamwork.assemble.control.jaxrs.chat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Chat;

public class ActionGet extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		Chat chat = null;
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
				chat = chatQueryService.get( id );
				if( chat == null ) {
					check = false;
					Exception exception = new ChatNotExistsException(id);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ChatQueryException( e, "系统根据ID查询指定工作评论信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				wrap = Wo.copier.copy( chat );
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

	public static class Wo extends Chat  {
		private static final long serialVersionUID = -5076990764713538973L;		
		public static List<String> Excludes = new ArrayList<String>();		
		public static WrapCopier<Chat, Wo> copier = WrapCopierFactory.wo( Chat.class, Wo.class, null,Wo.Excludes);
	}

}