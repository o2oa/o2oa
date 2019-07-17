package com.x.teamwork.assemble.control.jaxrs.attachment;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.core.entity.Attachment;

public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Attachment attachment = null;
		StorageMapping mapping = null;
		Boolean check = true;	
		
		if( StringUtils.isEmpty( id ) ){
			check = false;
			Exception exception = new ExceptionAttachmentIdEmpty();
			result.error( exception );
		}
		
		if( check ){
			try{
				attachment = attachmentQueryService.get( id );
				if (null == attachment) {
					check = false;
					Exception exception = new ExceptionAttachmentNotExists( id );
					result.error( exception );
				}
			}catch(Exception e){
				check = false;
				Exception exception = new ExceptionAttachmentQueryById( e, id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				mapping = ThisApplication.context().storageMappings().get(Attachment.class, attachment.getStorage());
				
				attachment = emc.find( id, Attachment.class );
				emc.beginTransaction( Attachment.class );
				emc.remove( attachment, CheckRemoveType.all );
				attachment.deleteContent( mapping );
				emc.commit();

				Wo wo = new Wo();
				wo.setId( id );
				result.setData( wo );
				
			}catch(Exception e){
				check = false;
				Exception exception = new ExceptionAttachmentDelete( e, attachment.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				dynamicPersistService.deleteAttachment( attachment, effectivePerson );
			} catch (Exception e) {
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wo extends WoId {}
}