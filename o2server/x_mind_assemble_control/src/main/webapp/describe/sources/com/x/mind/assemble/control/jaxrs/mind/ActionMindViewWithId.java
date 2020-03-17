package com.x.mind.assemble.control.jaxrs.mind;

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
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindNotExists;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindQuery;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindWrapOutConvert;
import com.x.mind.entity.MindBaseInfo;

/**
 * 查询脑图信息
 * @author O2LEE
 *
 */
public class ActionMindViewWithId extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMindViewWithId.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, String id, EffectivePerson effectivePerson ) {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		MindBaseInfo mindBaseInfo = null;
		Boolean check = true;

		if( check ){
			try {
				mindBaseInfo = mindInfoService.getMindBaseInfo(id);
				if( mindBaseInfo == null ) {
					check = false;
					Exception exception = new ExceptionMindNotExists( id );
					result.error(exception);
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e, "系统在根据ID查询指定的脑图信息时发生异常。" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				wo = Wo.copier.copy(mindBaseInfo);
				if( effectivePerson.getDistinguishedName().equals( mindBaseInfo.getCreator() )) {
					wo.setEditable( true );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionMindWrapOutConvert( e, "将数据库实体对象转换为输出对象时发生异常！" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if( check ){
			String content = null;
			try {
				content = mindInfoService.getContent(id);
				if( StringUtils.isNotEmpty(content)) {
					wo.setContent(content);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionMindQuery( e, "系统在根据ID查询指定的脑图内容信息时发生异常。" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData(wo);
		return result;
	}
	
	public static class Wo extends MindBaseInfo  {		
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		{
			Excludes.add( JpaObject.sequence_FIELDNAME);
			Excludes.add("creatorUnit_sequence");
			Excludes.add("shared_sequence");
			Excludes.add("folder_sequence");
			Excludes.add("creator_sequence");
		}
		public static WrapCopier<MindBaseInfo, Wo> copier = WrapCopierFactory.wo( MindBaseInfo.class, Wo.class, null,Wo.Excludes);
		
		@FieldDescribe( "脑图内容" )
		private String content = null;
		
		@FieldDescribe( "是否可编辑" )
		private Boolean editable = false;
		
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public Boolean getEditable() {
			return editable;
		}
		public void setEditable(Boolean editable) {
			this.editable = editable;
		}		
	}
}