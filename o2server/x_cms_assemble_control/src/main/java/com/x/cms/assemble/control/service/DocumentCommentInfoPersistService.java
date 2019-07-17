package com.x.cms.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.core.entity.DocumentCommentInfo;
import com.x.cms.core.entity.tools.DateOperation;

/**
 * 对评论信息的持久化服务
 * 
 * @author O2LEE
 */
public class DocumentCommentInfoPersistService {

	private DocumentCommentInfoService documentCommentInfoService = new DocumentCommentInfoService();
	
	public void delete( String flag, EffectivePerson currentPerson ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty.");
		}
		Boolean hasDeletePermission = false;
		if( currentPerson.isManager() ) {
			hasDeletePermission = true;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			DocumentCommentInfo documentCommentInfo = documentCommentInfoService.get(emc, flag);
			//管理员可以删除，创建者可以删除
			if( !hasDeletePermission ) {
				//看看是不是评论信息创建者
				if( documentCommentInfo.getCreatorName().equalsIgnoreCase( currentPerson.getDistinguishedName() )) {
					hasDeletePermission = true;
				}
			}
			if( !hasDeletePermission ) {
				throw new Exception("documentCommentInfo delete permission denied.");
			}else {
				documentCommentInfoService.delete( emc, flag );
			}			
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 保存评论信息
	 * @param documentCommentInfo
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public DocumentCommentInfo save( DocumentCommentInfo documentCommentInfo, String content, EffectivePerson effectivePerson ) throws Exception {
		if ( documentCommentInfo == null) {
			throw new Exception("documentCommentInfo is null.");
		}
		if( StringUtils.isEmpty( documentCommentInfo.getTitle() )) {
			documentCommentInfo.setTitle("无标题评论信息("+ DateOperation.getNowDateTime() +")");
		}
		if( documentCommentInfo.getTitle().length() > 70 ) {
			documentCommentInfo.setTitle( documentCommentInfo.getTitle().substring(0, 70) + "..." );
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {			
			documentCommentInfo = documentCommentInfoService.save( emc, documentCommentInfo, content );			
		} catch (Exception e) {
			throw e;
		}
		return documentCommentInfo;
	}
}
