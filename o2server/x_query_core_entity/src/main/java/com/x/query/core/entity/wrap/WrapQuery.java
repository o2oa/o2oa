package com.x.query.core.entity.wrap;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.core.entity.Query;

public class WrapQuery extends Query {

	private static final long serialVersionUID = -1136967220697699885L;

	public static WrapCopier<Query, WrapQuery> outCopier = WrapCopierFactory.wo(Query.class, WrapQuery.class, null,
			JpaObject.FieldsInvisible);

	public static WrapCopier<WrapQuery, Query> inCopier = WrapCopierFactory.wi(WrapQuery.class, Query.class, null,
			JpaObject.FieldsUnmodifyExcludeId);

	@FieldDescribe("视图")
	private List<WrapView> viewList = new ArrayList<>();

	@FieldDescribe("统计")
	private List<WrapStat> statList = new ArrayList<>();

	@FieldDescribe("展现")
	private List<WrapReveal> revealList = new ArrayList<>();

	public List<String> listViewId() throws Exception {
		return ListTools.extractProperty(this.getViewList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<String> listStatId() throws Exception {
		return ListTools.extractProperty(this.getStatList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<String> listRevealId() throws Exception {
		return ListTools.extractProperty(this.getRevealList(), JpaObject.id_FIELDNAME, String.class, true, true);
	}

	public List<WrapView> getViewList() {
		return viewList;
	}

	public void setViewList(List<WrapView> viewList) {
		this.viewList = viewList;
	}

	public List<WrapStat> getStatList() {
		return statList;
	}

	public void setStatList(List<WrapStat> statList) {
		this.statList = statList;
	}

	public List<WrapReveal> getRevealList() {
		return revealList;
	}

	public void setRevealList(List<WrapReveal> revealList) {
		this.revealList = revealList;
	}

}
