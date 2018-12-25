package com.x.okr.assemble.control.jaxrs.okrworkchat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkChatIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkChatNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkChatQueryById;
import com.x.okr.entity.OkrWorkChat;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		OkrWorkChat okrWorkChat = null;
		if( id == null || id.isEmpty() ){
			Exception exception = new ExceptionWorkChatIdEmpty();
			result.error( exception );
		}else{
			try {
				okrWorkChat = okrWorkChatService.get( id );
				if( okrWorkChat != null ){
					wrap = Wo.copier.copy( okrWorkChat );
					result.setData(wrap);
				}else{
					Exception exception = new ExceptionWorkChatNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				Exception exception = new ExceptionWorkChatQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends OkrWorkChat{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrWorkChat, Wo> copier = WrapCopierFactory.wo( OkrWorkChat.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}