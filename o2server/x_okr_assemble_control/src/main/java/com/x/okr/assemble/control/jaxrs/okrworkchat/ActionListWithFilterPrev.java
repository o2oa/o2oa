package com.x.okr.assemble.control.jaxrs.okrworkchat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionGetOkrUserCache;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionUserNoLogin;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkChatFilter;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkIdEmpty;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkNotExists;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWorkQueryById;
import com.x.okr.assemble.control.jaxrs.okrworkchat.exception.ExceptionWrapInConvert;
import com.x.okr.entity.OkrWorkBaseInfo;
import com.x.okr.entity.OkrWorkChat;

public class ActionListWithFilterPrev extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListWithFilterPrev.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request,EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<List<Wo>>();
		List<Wo> wrapOutOkrWorkChatList = null;
		List<OkrWorkChat> chatList = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		Long total = 0L;
		boolean check = true;
		Wi wrapIn = null;
		OkrUserCache  okrUserCache  = null;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ){
			try {
				okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName( effectivePerson.getDistinguishedName() );
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionGetOkrUserCache( e, effectivePerson.getDistinguishedName()  );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check && ( okrUserCache == null || okrUserCache.getLoginIdentityName() == null ) ){
			check = false;
			Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName()  );
			result.error( exception );
		}
		if( count == null ){
			count = 20;
		}
		
		if( check ){
			//对wrapIn里的信息进行校验
			if( okrUserCache.getLoginUserName() == null ){
				check = false;
				Exception exception = new ExceptionUserNoLogin( effectivePerson.getDistinguishedName()  );
				result.error( exception );
			}
		}
		
		if( check ){
			//对wrapIn里的信息进行校验
			if( wrapIn.getWorkId() == null || wrapIn.getWorkId().isEmpty() ){
				check = false;
				Exception exception = new ExceptionWorkIdEmpty();
				result.error( exception );
			}
		}
		
		if( check ){
			try {
				okrWorkBaseInfo = okrWorkBaseInfoService.get( wrapIn.getWorkId() );
				if( okrWorkBaseInfo == null ){
					check = false;
					Exception exception = new ExceptionWorkNotExists( wrapIn.getWorkId() );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkQueryById( e, wrapIn.getWorkId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try{
				chatList = okrWorkChatService.listChatPrevWithFilter( id, count, wrapIn.getWorkId(), wrapIn.getSequenceField(), wrapIn.getOrder() );
				total = okrWorkChatService.getChatCountWithFilter( wrapIn.getWorkId() );
				wrapOutOkrWorkChatList = Wo.copier.copy(chatList);	
				result.setData( wrapOutOkrWorkChatList );
				result.setCount( total );
			}catch(Exception e){
				Exception exception = new ExceptionWorkChatFilter( e );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
public static class Wi extends GsonPropertyObject {
		
		@FieldDescribe( "用于查询的具体工作项ID." )
		private String workId;
		
		@FieldDescribe( "用于列表排序的属性." )
		private String sequenceField =  JpaObject.sequence_FIELDNAME;
		
		@FieldDescribe( "用于列表排序的方式." )
		private String order = "DESC";

		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}

		public String getSequenceField() {
			return sequenceField;
		}

		public void setSequenceField(String sequenceField) {
			this.sequenceField = sequenceField;
		}

		public String getOrder() {
			return order;
		}

		public void setOrder(String order) {
			this.order = order;
		}
	}

	public static class Wo extends OkrWorkChat{

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<OkrWorkChat, Wo> copier = WrapCopierFactory.wo( OkrWorkChat.class, Wo.class, null,JpaObject.FieldsInvisible);
		
		private Long rank = 0L;

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}
	}
}