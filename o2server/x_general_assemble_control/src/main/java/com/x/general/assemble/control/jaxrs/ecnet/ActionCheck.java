package com.x.general.assemble.control.jaxrs.ecnet;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCheck extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheck.class);

	private static final String ADDRESS = "/o2_collect_assemble/jaxrs/ecnet/check";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResponse response = ConnectionAction.post(Config.collect().url(ADDRESS), null, wi);
		Wo wo = response.getData(Wo.class);
		result.setData(wo);
		return result;

	}

	@Schema(name = "com.x.general.assemble.control.jaxrs.ecnet.ActionCheck$Wi")
	public static class Wi extends WrapString {

		private static final long serialVersionUID = 965883153862679625L;
	}

	@Schema(name = "com.x.general.assemble.control.jaxrs.ecnet.ActionCheck$Wo")
	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 7549440867585619008L;

		@FieldDescribe("位置.")
		@Schema(description = "位置.")
		private Integer pos = 0;

		@FieldDescribe("原始值.")
		@Schema(description = "原始值.")
		private String origin = "";

		@FieldDescribe("纠正值.")
		@Schema(description = "纠正值.")
		private String correct = "";

		@FieldDescribe("纠正项.")
		@Schema(description = "纠正项.")
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

	@Schema(name = "com.x.general.assemble.control.jaxrs.ecnet.ActionCheck$WoItem")
	public static class WoItem extends GsonPropertyObject {

		private static final long serialVersionUID = 8709219948318947232L;

		@FieldDescribe("原始值.")
		@Schema(description = "原始值.")
		private String origin;

		@FieldDescribe("纠正值.")
		@Schema(description = "纠正值.")
		private String correct;

		@FieldDescribe("开始位置.")
		@Schema(description = "开始位置.")
		private Integer begin;

		@FieldDescribe("结束位置.")
		@Schema(description = "结束位置.")
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
