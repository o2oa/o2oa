package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyIdEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyInfoProcess;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyNotExists;
import com.x.bbs.entity.BBSReplyInfo;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionGet.class );

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();

		Wo wrap = null;
		BBSReplyInfo replyInfo = null;
		Boolean check = true;

		if( check ){
			if( id == null || id.isEmpty() ){
				check = false;
				Exception exception = new ExceptionReplyIdEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				replyInfo = replyInfoService.get( id );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess( e, "根据指定ID查询回复信息时发生异常.ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( replyInfo != null ){
				try {
					wrap = Wo.copier.copy( replyInfo );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReplyInfoProcess( e, "将查询结果转换成可以输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
				if(StringUtils.isBlank(wrap.getNickName())){
					wrap.setNickName(wrap.getCreatorName());
				}
				if( wrap != null && StringUtils.isNotEmpty( wrap.getCreatorName() ) ) {
					wrap.setCreatorNameShort( wrap.getCreatorName().split( "@" )[0]);
				}
				if( wrap != null && StringUtils.isNotEmpty( wrap.getAuditorName() ) ) {
					wrap.setAuditorNameShort( wrap.getAuditorName().split( "@" )[0]);
				}
			}else{
				Exception exception = new ExceptionReplyNotExists( id );
				result.error( exception );
			}
		}
		result.setData( wrap );
		return result;
	}

	public static class Wo extends BBSReplyInfo{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier< BBSReplyInfo, Wo > copier = WrapCopierFactory.wo( BBSReplyInfo.class, Wo.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe( "创建人姓名" )
		private String creatorNameShort = "";

		@FieldDescribe( "审核人姓名" )
		private String auditorNameShort = "";

		public String getCreatorNameShort() {
			return creatorNameShort;
		}

		public String getAuditorNameShort() {
			return auditorNameShort;
		}

		public void setCreatorNameShort(String creatorNameShort) {
			this.creatorNameShort = creatorNameShort;
		}

		public void setAuditorNameShort(String auditorNameShort) {
			this.auditorNameShort = auditorNameShort;
		}
	}
}
