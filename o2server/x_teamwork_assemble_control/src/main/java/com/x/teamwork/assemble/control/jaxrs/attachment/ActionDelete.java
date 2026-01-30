package com.x.teamwork.assemble.control.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.teamwork.assemble.control.ThisApplication;
import com.x.teamwork.core.entity.Attachment;
import com.x.teamwork.core.entity.Dynamic;
import com.x.teamwork.core.entity.ProjectStatusEnum;
import com.x.teamwork.core.entity.Task;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sword
 */
public class ActionDelete extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionDelete.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Attachment attachment = attachmentQueryService.get( id );
		if (null == attachment) {
			throw new ExceptionAttachmentNotExists( id );
		}
		if(StringUtils.isNotBlank(attachment.getTaskId())){
			Task task = taskQueryService.get( attachment.getTaskId() );
			if (null == task) {
				throw new ExceptionTaskNotExists( attachment.getTaskId() );
			}

			if(!this.isManager(task.getId(), effectivePerson)){
				throw new ExceptionAccessDenied(effectivePerson);
			}

			if(ProjectStatusEnum.isEndStatus(task.getWorkStatus())){
				throw new ExceptionAttachmentOperate("当前任务不允许删除附件!");
			}
		}
		Wo wo = new Wo();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			StorageMapping mapping = ThisApplication.context().storageMappings().get(Attachment.class, attachment.getStorage());
			Attachment att = emc.find( id, Attachment.class );
			emc.beginTransaction( Attachment.class );
			att.deleteContent( mapping );
			emc.remove( att, CheckRemoveType.all );
			emc.commit();
		}

		try {
			Dynamic dynamic = dynamicPersistService.deleteAttachment( attachment, effectivePerson );
			if( dynamic != null ) {
				List<WoDynamic> dynamics = new ArrayList<>();
				dynamics.add( WoDynamic.copier.copy( dynamic ) );
				wo.setDynamics(dynamics);
			}
		} catch (Exception e) {
			logger.error(e, effectivePerson, request, null);
		}
		wo.setId( id );
		result.setData( wo );
		return result;
	}

	public static class Wo extends WoId {

		@FieldDescribe("操作引起的动态内容")
		List<WoDynamic> dynamics = new ArrayList<>();

		public List<WoDynamic> getDynamics() {
			return dynamics;
		}

		public void setDynamics(List<WoDynamic> dynamics) {
			this.dynamics = dynamics;
		}
	}

	public static class WoDynamic extends Dynamic{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Dynamic, WoDynamic> copier = WrapCopierFactory.wo( Dynamic.class, WoDynamic.class, null, JpaObject.FieldsInvisible);

		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}
