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
import com.x.mind.entity.MindBaseInfo;

/**
 * 查询所有我分享出去的脑图文件信息
 * @author O2LEE
 *
 */
public class ActionMyShareMindNextWithFilter extends BaseAction {
	
	private Logger logger = LoggerFactory.getLogger( ActionMyShareMindNextWithFilter.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String startId, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wi = null;
		List<MindBaseInfo> mindEnityies = null;
		String folderId = null;
		Boolean check = true;
		
		if( check ){
			try {
				wi = this.convertToWrapIn( jsonElement, Wi.class );
				if( wi != null ) {
					folderId = wi.getFolderId();
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
				mindEnityies = mindInfoService.listNextPageWithFilter(startId, count, wi.getKey(), folderId, true, effectivePerson.getDistinguishedName(), 
						null, wi.getSharePersons(), wi.getShareUnits(), wi.getShareGroups(), wi.getOrderField(), wi.getOrderType(), null );
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
		
		@FieldDescribe( "指定的目录ID" )
		private String folderId = null;
		
		@FieldDescribe( "关键字" )
		private String key = null;
		
		@FieldDescribe( "共享者列表" )
		private List<String> sharePersons = null;
		
		@FieldDescribe( "共享组织列表" )
		private List<String> shareUnits = null;

		@FieldDescribe( "共享角色列表" )
		private List<String> shareGroups = null;

		@FieldDescribe( "排序列：默认为sequence" )
		private String orderField =  JpaObject.sequence_FIELDNAME ;
		
		@FieldDescribe( "排序方式：DESC|ASC， 默认为DESC" )
		private String orderType = "DESC";
		
		public String getFolderId() {
			return folderId;
		}

		public void setFolderId(String folderId) {
			this.folderId = folderId;
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