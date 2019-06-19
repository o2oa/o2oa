package com.x.teamwork.assemble.control.jaxrs.chat;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.core.entity.Chat;

public class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Chat chat = null;
		Boolean check = true;

		if ( StringUtils.isEmpty( id ) ) {
			check = false;
			Exception exception = new ChatFlagForQueryEmptyException();
			result.error( exception );
		}

		if (check) {
			try {
				chat = chatQueryService.get( id );
				if ( chat == null) {
					check = false;
					Exception exception = new ChatNotExistsException( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ChatQueryException(e, "根据指定flag查询工作交流信息对象时发生异常。ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				chatPersistService.delete( id, effectivePerson );				
				// 更新缓存
				ApplicationCache.notify( Chat.class );
				
				Wo wo = new Wo();
				wo.setId( chat.getId() );
				result.setData( wo );
			} catch (Exception e) {
				check = false;
				Exception exception = new ChatQueryException(e, "根据指定flag删除工作交流信息对象时发生异常。ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {					
				dynamicPersistService.save( chat, "DELETE", effectivePerson, null );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}	
		}
		return result;
	}

	public static class Wo extends WoId {
	}
}