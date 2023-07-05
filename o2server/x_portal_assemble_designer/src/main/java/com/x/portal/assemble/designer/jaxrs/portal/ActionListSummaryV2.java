package com.x.portal.assemble.designer.jaxrs.portal;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class ActionListSummaryV2 extends BaseAction {

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			List<Portal> portalList = this.listEditableObj(business, effectivePerson, wi.getName(), wi.getPortalCategory());
			List<Wo> wos = Wo.copier.copy(portalList);
			/* 由于有多值字段所以需要全部取出 */
			for (Wo wo : wos) {
				wo.setPageCount(emc.countEqual(Page.class, Page.portal_FIELDNAME, wo.getId()));
				wo.setWidgetCount(emc.countEqual(Widget.class, Widget.portal_FIELDNAME, wo.getId()));
				wo.setScriptCount(emc.countEqual(Script.class, Script.portal_FIELDNAME, wo.getId()));
				wo.setFileCount(emc.countEqual(File.class, File.portal_FIELDNAME, wo.getId()));
			}
			if(BooleanUtils.isTrue(wi.getDescOrder())){
				SortTools.desc(wos, wi.getOrderBy());
			}else{
				SortTools.asc(wos, wi.getOrderBy());
			}
			result.setData(wos);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject{

		private static final long serialVersionUID = 9112168190097470362L;
		@FieldDescribe("名称(模糊查询)")
		private String name;
		@FieldDescribe("分类名称")
		private String portalCategory;
		@FieldDescribe("排序字段：lastUpdateTime|createTime|name(默认)")
		private String orderBy;
		@FieldDescribe("是否倒叙排序，默认false")
		private Boolean descOrder;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPortalCategory() {
			return portalCategory;
		}

		public void setPortalCategory(String portalCategory) {
			this.portalCategory = portalCategory;
		}

		public String getOrderBy() {
			return StringUtils.isBlank(orderBy) ? Portal.name_FIELDNAME : orderBy;
		}

		public void setOrderBy(String orderBy) {
			this.orderBy = orderBy;
		}

		public Boolean getDescOrder() {
			return descOrder;
		}

		public void setDescOrder(Boolean descOrder) {
			this.descOrder = descOrder;
		}
	}

	public static class Wo extends Portal {

		private static final long serialVersionUID = -4436126455534548272L;

		static WrapCopier<Portal, Wo> copier = WrapCopierFactory.wo(Portal.class, Wo.class,
				JpaObject.singularAttributeField(Portal.class, true, false), null);


		@FieldDescribe("页面数量")
		private Long pageCount;

		@FieldDescribe("部件数量")
		private Long widgetCount;

		@FieldDescribe("脚本数量")
		private Long scriptCount;

		@FieldDescribe("资源数量")
		private Long fileCount;

		public Long getPageCount() {
			return pageCount;
		}

		public void setPageCount(Long pageCount) {
			this.pageCount = pageCount;
		}

		public Long getWidgetCount() {
			return widgetCount;
		}

		public void setWidgetCount(Long widgetCount) {
			this.widgetCount = widgetCount;
		}

		public Long getScriptCount() {
			return scriptCount;
		}

		public void setScriptCount(Long scriptCount) {
			this.scriptCount = scriptCount;
		}

		public Long getFileCount() {
			return fileCount;
		}

		public void setFileCount(Long fileCount) {
			this.fileCount = fileCount;
		}
	}

	public static class WoPage extends Page {

		private static final long serialVersionUID = -9051259904153066895L;
		static WrapCopier<Page, WoPage> copier = WrapCopierFactory.wo(Page.class, WoPage.class,
				JpaObject.singularAttributeField(Page.class, true, true), null);
	}

}
