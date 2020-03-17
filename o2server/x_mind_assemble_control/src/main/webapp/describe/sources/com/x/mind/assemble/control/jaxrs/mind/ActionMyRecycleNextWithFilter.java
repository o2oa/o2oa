package com.x.mind.assemble.control.jaxrs.mind;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.x.mind.entity.MindRecycleInfo;

/**
 * 查询所有回收站里的文件信息
 * @author O2LEE
 *
 */
public class ActionMyRecycleNextWithFilter extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMyRecycleNextWithFilter.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String startId, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wi = null;
		List<MindRecycleInfo> recylceMindEnityies = null;
		Boolean check = true;
		
		if( check ){
			try {
				wi = this.convertToWrapIn( jsonElement, Wi.class );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionFolderWrapInConvert(e, jsonElement == null?"None":XGsonBuilder.instance().toJson(jsonElement));
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ){
			try {
				recylceMindEnityies = mindInfoService.listRecycleNextPageWithFilter(startId, count, wi.getKey(), wi.getFolderId(), wi.getShared(), 
						effectivePerson.getDistinguishedName(), null, wi.getOrderField(), wi.getOrderType(), null );
				if( ListTools.isNotEmpty( recylceMindEnityies )) {
					wos = Wo.copier.copy(recylceMindEnityies);
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
		
		@FieldDescribe( "文件原始目录" )
		private String folderId = null;
		
		@FieldDescribe( "是否已分享" )
		private Boolean shared = null;

		@FieldDescribe( "关键字" )
		private String key = null;
		
		@FieldDescribe( "排序列：默认为sequence" )
		private String orderField =  JpaObject.sequence_FIELDNAME ;
		
		@FieldDescribe( "排序方式：DESC|ASC， 默认为DESC" )
		private String orderType = "DESC";

		public Boolean getShared() {
			return shared;
		}

		public void setShared(Boolean shared) {
			this.shared = shared;
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

		public String getFolderId() {
			return folderId;
		}

		public void setFolderId(String folderId) {
			this.folderId = folderId;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
		
	}
	
	public static class Wo extends MindRecycleInfo  {		
		private static final long serialVersionUID = -5076990764713538973L;
		public static List<String> Excludes = new ArrayList<String>();
		public static WrapCopier<MindRecycleInfo, Wo> copier = WrapCopierFactory.wo( MindRecycleInfo.class, Wo.class, null,Wo.Excludes);
	}
}