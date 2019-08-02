package com.x.teamwork.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.teamwork.core.entity.Chat;

public class ChatPersistService {

	private ChatService chatService = new ChatService();
	
	/**
	 * 删除工作交流信息（管理员可删除）
	 * @param id
	 * @param effectivePerson
	 * @throws Exception
	 */
	public void delete( String id, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( id )) {
			throw new Exception("id is empty.");
		}
		Boolean hasDeletePermission = false;
		if( effectivePerson.isManager() ) {
			hasDeletePermission = true;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			if( !hasDeletePermission ) {
				throw new Exception("chat delete permission denied.");
			}else {
				chatService.delete( emc, id );
			}			
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存工作交流信息
	 * @param chat
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Chat create( EffectivePerson effectivePerson, Chat object, String lobContent  ) throws Exception {
		if ( object == null) {
			throw new Exception("chat object is null.");
		}
		if ( effectivePerson == null ) {
			throw new Exception("effectivePerson is null.");
		}

		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			return  chatService.create( emc, object, lobContent );
		} catch (Exception e) {
			throw e;
		}
	}
}
