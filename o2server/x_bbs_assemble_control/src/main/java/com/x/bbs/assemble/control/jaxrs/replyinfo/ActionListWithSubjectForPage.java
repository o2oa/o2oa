package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionCountEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionPageEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyInfoProcess;
import com.x.bbs.entity.BBSReplyInfo;

import net.sf.ehcache.Element;

public class ActionListWithSubjectForPage extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionListWithSubjectForPage.class );
	
	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, Integer page, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionReplyInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if( check ) {
			String cacheKey = wrapIn.getSubjectId() + "#" + page + "#" + count;
			Element element = cache.get( cacheKey );
			
			if ((null != element) && (null != element.getObjectValue())) {
				ActionResult<List<Wo>> result_cache = (ActionResult<List<Wo>>) element.getObjectValue();
				result.setData( result_cache.getData() );
				result.setCount( result_cache.getCount() );
			} else {
				result = getReplyQueryResult(wrapIn, request, effectivePerson, page, count);
				cache.put(new Element(cacheKey, result ));
			}
		}
		return result;
	}

	public ActionResult<List<Wo>> getReplyQueryResult( Wi wrapIn, HttpServletRequest request, EffectivePerson effectivePerson, Integer page, Integer count ) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSReplyInfo> replyInfoList = null;
		List<BBSReplyInfo> replyInfoList_out = new ArrayList<BBSReplyInfo>();
		Long total = 0L;
		Boolean check = true;
		
		if( check ){
			if( page == null ){
				check = false;
				Exception exception = new ExceptionPageEmpty();
				result.error( exception );
			}
		}
		if( check ){
			if( count == null ){
				check = false;
				Exception exception = new ExceptionCountEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try{
				total = replyInfoService.countWithSubjectForPage( wrapIn.getSubjectId() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess( e, "根据主题ID查询主题内所有的回复数量时发生异常。Subject:" + wrapIn.getSubjectId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( total > 0 ){
				try{
					replyInfoList = replyInfoService.listWithSubjectForPage( wrapIn.getSubjectId(), page * count );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReplyInfoProcess( e, "根据主题ID查询主题内所有的回复列表时发生异常。Subject:" + wrapIn.getSubjectId() );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( page <= 0 ){
				page = 1;
			}
			if( count <= 0 ){
				count = 20;
			}
			int startIndex = ( page - 1 ) * count;
			int endIndex = page * count;
			for( int i=0; replyInfoList != null && i< replyInfoList.size(); i++ ){
				if( i < replyInfoList.size() && i >= startIndex && i < endIndex ){
					replyInfoList_out.add( replyInfoList.get( i ) );
				}
			}
			if( ListTools.isNotEmpty( replyInfoList_out ) ){
				try {
					wraps = Wo.copier.copy( replyInfoList_out );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReplyInfoProcess( e, "将查询结果转换成可以输出的数据信息时发生异常。" );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			if( ListTools.isNotEmpty( wraps ) ) {
				for( Wo wo : wraps ) {
					if( StringUtils.isNotEmpty( wo.getCreatorName() ) ) {
						wo.setCreatorNameShort( wo.getCreatorName().split( "@" )[0]);
					}
					if( StringUtils.isNotEmpty( wo.getAuditorName() ) ) {
						wo.setAuditorNameShort( wo.getAuditorName().split( "@" )[0]);
					}
				}
			}
		}
		result.setData( wraps );
		result.setCount( total );
		return result;
	}
	
	public static class Wi{
		private String subjectId = null;		
		public static List<String> Excludes = new ArrayList<String>( JpaObject.FieldsUnmodify );
		public String getSubjectId() {
			return subjectId;
		}
		public void setSubjectId(String subjectId) {
			this.subjectId = subjectId;
		}
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