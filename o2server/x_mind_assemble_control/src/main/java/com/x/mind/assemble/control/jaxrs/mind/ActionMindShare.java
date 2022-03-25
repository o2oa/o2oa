package com.x.mind.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.ThisApplication;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderWrapInConvert;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindNotExists;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindQuery;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindShare;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindShareTargetEmpty;
import com.x.mind.entity.MindBaseInfo;
import com.x.mind.entity.MindShareRecord;

/**
 * 将脑图信息分享给其他用户或者组织
 * @author O2LEE
 *
 */
public class ActionMindShare extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMindShare.class );

	@AuditLog(operation = "分享脑图文件")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String mindId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		Wi wi = null;
		List<MindShareRecord> mindShareRecords = new ArrayList<>();
		MindBaseInfo mindBaseInfo = null;
		Boolean check = true;
		
		if( check ){
			try {
				wi = this.convertToWrapIn( jsonElement, Wi.class );
				if( wi == null || 
						( ListTools.isEmpty( wi.getSharePersons() ) && ListTools.isEmpty( wi.getShareUnits() ) && ListTools.isEmpty( wi.getShareGroups() ) )) {
					check = false;
					Exception exception = new ExceptionMindShareTargetEmpty();
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFolderWrapInConvert(e, jsonElement == null?"None":XGsonBuilder.instance().toJson(jsonElement));
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if( check ){
			try {
				mindBaseInfo = mindInfoService.getMindBaseInfo( mindId );
				if( mindBaseInfo == null ) {
					check = false;
					Exception exception = new ExceptionMindNotExists( mindId );
					result.error(exception);
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e,  "系统在根据ID查询脑图信息时发生异常。", mindId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			MindShareRecord mindShareRecord = null;
			for( String targetName : wi.getSharePersons() ) {
				mindShareRecord = new MindShareRecord();
				mindShareRecord.setSource( effectivePerson.getDistinguishedName() );
				mindShareRecord.setFileId( mindBaseInfo.getId() );
				mindShareRecord.setFileStatus( "正常" );
				mindShareRecord.setFileType("MIND");
				mindShareRecord.setName( mindBaseInfo.getName() );
				mindShareRecord.setId( MindShareRecord.createId() );
				mindShareRecord.setTarget( targetName );
				mindShareRecord.setTargetType( "PERSON" );
				mindShareRecords.add( mindShareRecord );
			}
			
			for( String unitName : wi.getShareUnits() ) {
				mindShareRecord = new MindShareRecord();
				mindShareRecord.setSource( effectivePerson.getDistinguishedName() );
				mindShareRecord.setFileId( mindBaseInfo.getId() );
				mindShareRecord.setFileStatus( "正常" );
				mindShareRecord.setFileType("MIND");
				mindShareRecord.setName( mindBaseInfo.getName() );
				mindShareRecord.setId( MindShareRecord.createId() );
				mindShareRecord.setTarget( unitName );
				mindShareRecord.setTargetType( "UNIT" );
				mindShareRecords.add( mindShareRecord );
			}

			for( String groupName : wi.getShareGroups() ) {
				mindShareRecord = new MindShareRecord();
				mindShareRecord.setSource( effectivePerson.getDistinguishedName() );
				mindShareRecord.setFileId( mindBaseInfo.getId() );
				mindShareRecord.setFileStatus( "正常" );
				mindShareRecord.setFileType("MIND");
				mindShareRecord.setName( mindBaseInfo.getName() );
				mindShareRecord.setId( MindShareRecord.createId() );
				mindShareRecord.setTarget( groupName );
				mindShareRecord.setTargetType( "GROUP" );
				mindShareRecords.add( mindShareRecord );
			}
		}
		
		if( check ){
			try {
				mindInfoService.share( mindBaseInfo, mindShareRecords );
				wo.setId( mindId );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindShare( e, "{‘id’:'"+mindId+"'}" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if( check ){
			try {
				ThisApplication.queueShareNotify.send( mindBaseInfo );
			}catch( Exception e ) {
				Exception exception = new ExceptionMindShare( e, "{‘id’:'"+mindId+"'}" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi {
		
		@FieldDescribe( "分享目标人员列表" )
		private List<String> sharePersons = null;
		
		@FieldDescribe( "分享目标组织列表" )
		private List<String> shareUnits = null;
		
		@FieldDescribe( "分享目标群组列表" )
		private List<String> shareGroups = null;

		public List<String> getSharePersons() {
			return sharePersons;
		}

		public List<String> getShareUnits() {
			return shareUnits;
		}

		public List<String> getShareGroups() {
			return shareGroups;
		}

		public void setSharePersons(List<String> sharePersons) {
			this.sharePersons = sharePersons;
		}

		public void setShareUnits(List<String> shareUnits) {
			this.shareUnits = shareUnits;
		}

		public void setShareGroups(List<String> shareGroups) {
			this.shareGroups = shareGroups;
		}		
	}
	
	public static class Wo extends WoId {

	}
}