package com.x.teamwork.assemble.control.jaxrs.attachment;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.Attachment;
import com.x.teamwork.core.entity.Task;

public class ActionListWithTask extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListWithTask.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String taskId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
		List<Wo> wrapOutAttachmentList = null;
		List<Attachment> attachments = null;
		Task task = null;
		if( StringUtils.isEmpty( taskId )){
			Exception exception = new ExceptionProjectIdEmpty();
			result.error( exception );
		}else{
			try {	
				task = taskQueryService.get( taskId );
				if( task != null ){
					attachments = attachmentQueryService.listAttachmentWithTask(taskId);
					if( ListTools.isNotEmpty( attachments)) {
						wrapOutAttachmentList = Wo.copier.copy( attachments );
					}
				}
			} catch (Exception e) {
				Exception exception = new ExceptionQueryProjectById( e, taskId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if( wrapOutAttachmentList == null ){
				wrapOutAttachmentList = new ArrayList<Wo>();
			}
			result.setData( wrapOutAttachmentList );
		}
		return result;
	}
	
	public static class Wo extends Attachment{

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Attachment, Wo> copier = WrapCopierFactory.wo( Attachment.class, Wo.class, null,JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
		
	}
}