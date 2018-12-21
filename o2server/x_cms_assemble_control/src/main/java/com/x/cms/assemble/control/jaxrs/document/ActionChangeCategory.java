package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;

public class ActionChangeCategory extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionChangeCategory.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, JsonElement jsonElement, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<Document> documents = null;
		List<String> ids = null;
		String categoryId = null;
		CategoryInfo categoryInfo = null;
		Boolean check = true;
		Wo wo = new Wo();
		Wi wi = null;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( e, "系统在将JSON信息转换为对象时发生异常。");
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if (check) {
			if( ListTools.isEmpty( wi.getIds() )) {
				check = false;
				Exception exception = new ExceptionDocumentIdEmpty();
				result.error( exception );
			}
			ids = wi.getIds();
		}
		
		if (check) {
			if( StringUtils.isEmpty( wi.getCategoryId() )) {
				check = false;
				Exception exception = new ExceptionCategoryIdEmpty();
				result.error( exception );
			}
			categoryId = wi.getCategoryId();
		}

		if (check) {	
			//先查询分类是否存在
			try {
				categoryInfo = categoryInfoServiceAdv.get( categoryId );
				if (categoryInfo == null) {
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists(categoryId);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在根据ID查询分类信息时发生异常！ID：" + categoryId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {	
			//先查询分类是否存在
			wo.setTotal( ids.size() );
			try {
				documents = documentInfoServiceAdv.list( ids );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "系统在根据ID列表查询文档信息时发生异常！");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if( ListTools.isNotEmpty( documents )) {
				List<String> success = new ArrayList<>();
				List<String> failture = new ArrayList<>();
				for( Document document : documents ) {
					//修改每个document的分类信息
					if ( documentInfoServiceAdv.changeCategory( document, categoryInfo ) ) {
						success.add( document.getId() );
					}else {
						failture.add( document.getId() );
					}
				}
				wo.setFailtureList( failture );
				wo.setSuccessList( success );
				ApplicationCache.notify(Document.class);
			}
		}
		result.setCount(Long.parseLong(  wo.getTotal().toString() ) );
		result.setData( wo );
		return result;
	}

	public static class Wi {
		
		@FieldDescribe( "需要调整分类的文档ID列表." )
		private List<String> ids = null;
		
		@FieldDescribe( "目标分类ID." )
		private String categoryId = null;

		public List<String> getIds() {
			return ids;
		}

		public String getCategoryId() {
			return categoryId;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}

		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}
	}

	public static class Wo {
		
		@FieldDescribe( "需要调整分类的文档ID总数量." )
		private Integer total =0;
		
		@FieldDescribe( "成功调整分类的文档ID列表." )
		private List<String> successList = null;
		
		@FieldDescribe( "调整分类失败的文档ID列表." )
		private List<String> failtureList = null;
		
		public List<String> getSuccessList() {
			return successList;
		}
		public List<String> getFailtureList() {
			return failtureList;
		}
		public void setSuccessList(List<String> successList) {
			this.successList = successList;
		}
		public void setFailtureList(List<String> failtureList) {
			this.failtureList = failtureList;
		}
		public Integer getTotal() {
			return total;
		}
		public void setTotal(Integer total) {
			this.total = total;
		}
	}
}