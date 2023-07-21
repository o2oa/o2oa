package com.x.mind.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionFolderWrapInConvert;
import com.x.mind.assemble.control.jaxrs.exception.ExceptionMindQuery;
import com.x.mind.entity.MindBaseInfo;

/**
 * 查询所有我收到的分享文件信息
 * @author O2LEE
 *
 */
public class ActionMyReciveMindNextWithFilter extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMyReciveMindNextWithFilter.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String startId, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wi = null;
		List<String> mindIds_recived = null;
		List<MindBaseInfo> mindEnityies = null;
		List<String> personNames = new ArrayList<>();
		List<String> unitNames = null;
		List<String> groupNames = null;
		String source = null;
		Boolean check = true;
		String personName = effectivePerson.getDistinguishedName();
		
		if( check ){
			try {
				wi = this.convertToWrapIn( jsonElement, Wi.class );
				if( wi != null ) {
					source = wi.getSource();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFolderWrapInConvert(e, jsonElement == null?"None":XGsonBuilder.instance().toJson(jsonElement));
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		//先查询在mindIds_inFolder的过滤下，获取我所有的组织和群组
		if( check ){
			try {
				unitNames = userManagerService.listUnitNamesWithPerson(effectivePerson.getDistinguishedName());
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e,  "系统在查询用户所属的所有组织名称列表时发生异常。", effectivePerson.getDistinguishedName() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
				
		if( check ){
			try {
				groupNames = userManagerService.listGroupNamesByPerson(effectivePerson.getDistinguishedName());
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e,  "系统在查询用户所属的所有组织名称列表时发生异常。", effectivePerson.getDistinguishedName() );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
				
		if( check ){
			//先查询在mindIds_inFolder的过滤下，我所有共享给其他用户的脑图文件ID
			if( StringUtils.isNotEmpty( source )) {
				try {
					List<String> targetList = new ArrayList<>();
					targetList.add( personName );
					if(  ListTools.isNotEmpty( unitNames )) {
						targetList.addAll(unitNames);
					}
					if(  ListTools.isNotEmpty( groupNames )) {
						targetList.addAll(groupNames);
					}
					mindIds_recived = mindInfoService.listSharedMindIdsFromRecord( source, targetList, null );
				}catch( Exception e ) {
					check = false;
					Exception exception = new ExceptionMindQuery( e,  "系统在查询用户共享给其他人的脑图信息时发生异常。", effectivePerson.getDistinguishedName() );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		personNames.add( effectivePerson.getDistinguishedName() );
		if( check ){
			try {
				mindEnityies = mindInfoService.listNextPageWithFilter( startId, count, wi.getKey(), null, true, null, null, 
						personNames, unitNames, groupNames, wi.getOrderField(), wi.getOrderType(), mindIds_recived );
				if( ListTools.isNotEmpty( mindEnityies )) {
					wos = Wo.copier.copy(mindEnityies);
					SortTools.desc(wos, "updateTime");
					result.setData( wos );
				}
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionMindQuery( e, "系统根据ID列表查询脑图信息时发生异常！" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi{
		
		@FieldDescribe( "分享者" )
		private String source = null;
		
		@FieldDescribe( "关键字" )
		private String key = null;

		@FieldDescribe( "排序列：默认为sequence" )
		private String orderField =  JpaObject.sequence_FIELDNAME ;
		
		@FieldDescribe( "排序方式：DESC|ASC， 默认为DESC" )
		private String orderType = "DESC";
		
		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getOrderField() {
			return orderField;
		}

		public String getOrderType() {
			return orderType;
		}

		public void setOrderField(String orderField) {
			this.orderField = orderField;
		}

		public void setOrderType(String orderType) {
			this.orderType = orderType;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
		
	}
	
	public static class Wo extends MindBaseInfo  {		
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<MindBaseInfo, Wo> copier = WrapCopierFactory.wo( MindBaseInfo.class, Wo.class, null,Wo.Excludes);
	}
}