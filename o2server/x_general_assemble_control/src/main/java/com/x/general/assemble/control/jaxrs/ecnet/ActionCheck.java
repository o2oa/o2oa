package com.x.general.assemble.control.jaxrs.ecnet;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;

public class ActionCheck extends BaseAction {

	private static final String ADDRESS = "/o2_collect_assemble/jaxrs/ecnet/check";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			ActionResponse response = ConnectionAction.post(Config.collect().url(ADDRESS), null, wi);
			Wo wo = response.getData(Wo.class);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends WrapString {
	}

	public static class Wo extends GsonPropertyObject {

		private Integer pos = 0;

		private String origin = "";

		private String correct = "";

		private List<WoItem> itemList = new ArrayList<>();

		public String getOrigin() {
			return origin;
		}

		public void setOrigin(String origin) {
			this.origin = origin;
		}

		public String getCorrect() {
			return correct;
		}

		public void setCorrect(String correct) {
			this.correct = correct;
		}

		public List<WoItem> getItemList() {
			return itemList;
		}

		public void setItemList(List<WoItem> itemList) {
			this.itemList = itemList;
		}

		public Integer getPos() {
			return pos;
		}

		public void setPos(Integer pos) {
			this.pos = pos;
		}

	}

	public static class WoItem extends GsonPropertyObject {

		private String origin;

		private String correct;

		private Integer begin;

		private Integer end;

		public String getOrigin() {
			return origin;
		}

		public void setOrigin(String origin) {
			this.origin = origin;
		}

		public String getCorrect() {
			return correct;
		}

		public void setCorrect(String correct) {
			this.correct = correct;
		}

		public Integer getBegin() {
			return begin;
		}

		public void setBegin(Integer begin) {
			this.begin = begin;
		}

		public Integer getEnd() {
			return end;
		}

		public void setEnd(Integer end) {
			this.end = end;
		}

	}

}
