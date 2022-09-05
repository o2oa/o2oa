package com.x.general.assemble.control.jaxrs.search;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.x.base.core.project.Applications;
import com.x.base.core.project.x_cms_assemble_control;
import com.x.base.core.project.x_processplatform_assemble_surface;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.general.assemble.control.ThisApplication;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionSearch extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSearch.class);

	private static final String INDEX_APPLICATION = "com.x.custom.index.assemble.control.x_custom_index_assemble_control";

	private static final String SEARCH = "search";
	private static final String REVIEW = "review";
	private static final String V2 = "v2";

	ActionResult<JsonElement> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("effectivePerson: {}.", effectivePerson.getDistinguishedName());
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<JsonElement> result = new ActionResult<>();
		String person = this.getPerson(effectivePerson);
		String query = this.getQuery(wi.getQuery());

		if (BooleanUtils.isTrue(wi.getForceDefaultSearch())
				|| ListTools.isEmpty(ThisApplication.context().applications().get(INDEX_APPLICATION))) {
			// default search
			result.setData(this.defaultSearch(person, query, wi.getPage(), wi.getSize()));
		} else {
			// index search
			result.setData(this.indexSearch(person, query, wi.getPage(), wi.getSize()));
		}
		return result;
	}

	private JsonElement defaultSearch(String person, String query, Integer page, Integer size)
			throws InterruptedException, ExecutionException, TimeoutException {
		Req req = new Req();
		req.setPerson(person);
		req.setQuery(query);
		req.setPage(page);
		req.setSize(size);
		CompletableFuture<Optional<JsonElement>> processPlatform = this.defaultSearchProcessPlatform(req);
		CompletableFuture<Optional<JsonElement>> cms = this.defaultSearchCms(req);
		JsonObject value = new JsonObject();
		value.add("processPlatform", processPlatform.get(300, TimeUnit.SECONDS).orElse(null));
		value.add("cms", cms.get(300, TimeUnit.SECONDS).orElse(null));
		return value;
	}

	private JsonElement indexSearch(String person, String query, Integer page, Integer size) throws Exception {
		Req req = new Req();
		req.setQuery(person);
		req.setQuery(query);
		req.setSize(size);
		req.setPage(page);
		return ThisApplication.context().applications().postQuery(INDEX_APPLICATION, SEARCH, req).getData();
	}

	private CompletableFuture<Optional<JsonElement>> defaultSearchProcessPlatform(Req req) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				ActionResponse response = ThisApplication.context().applications().postQuery(
						x_processplatform_assemble_surface.class, Applications.joinQueryUri(REVIEW, V2, SEARCH), req);
				JsonObject value = new JsonObject();
				value.add("count", new JsonPrimitive(response.getCount()));
				value.add("data", response.getData());
				return Optional.of(value);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return Optional.empty();
		}, ThisApplication.threadPool());
	}

	private CompletableFuture<Optional<JsonElement>> defaultSearchCms(Req req) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				ActionResponse response = ThisApplication.context().applications()
						.postQuery(x_cms_assemble_control.class, Applications.joinQueryUri(REVIEW, V2, SEARCH), req);
				JsonObject value = new JsonObject();
				value.add("count", new JsonPrimitive(response.getCount()));
				value.add("data", response.getData());
				return Optional.of(value);
			} catch (Exception e) {
				LOGGER.error(e);
			}
			return Optional.empty();
		}, ThisApplication.threadPool());
	}

	private String getPerson(EffectivePerson effectivePerson) {
		return (!effectivePerson.isManager()) && (!effectivePerson.isCipher()) ? effectivePerson.getDistinguishedName()
				: "";
	}

	private String getQuery(String query) {
		return StringUtils.remove(StringUtils.remove(query, "%"), "_");
	}

	public static class Req extends GsonPropertyObject {

		private static final long serialVersionUID = -9109885719352645660L;

		private String id;
		private String type;
		private String query;
		private Integer page;
		private Integer size;
		private String person;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public Integer getPage() {
			return page;
		}

		public void setPage(Integer page) {
			this.page = page;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

	}

	@Schema(name = "com.x.general.assemble.control.jaxrs.search.ActionPost$Wi")
	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 1360655000630283661L;

		@FieldDescribe("核心标识.")
		@Schema(description = "核心标识.")
		private String id;

		@FieldDescribe("核心类型.")
		@Schema(description = "核心类型.")
		private String type;

		@FieldDescribe("搜索内容.")
		@Schema(description = "搜索内容.")
		private String query;

		@FieldDescribe("分页.")
		@Schema(description = "分页.")
		private Integer page;

		@FieldDescribe("数量.")
		@Schema(description = "数量.")
		private Integer size;

		@FieldDescribe("强制使用默认搜索.")
		@Schema(description = "强制使用默认搜索.")
		private Boolean forceDefaultSearch;

		public Boolean getForceDefaultSearch() {
			return forceDefaultSearch;
		}

		public void setForceDefaultSearch(Boolean forceDefaultSearch) {
			this.forceDefaultSearch = forceDefaultSearch;
		}

		public String getQuery() {
			return query;
		}

		public void setQuery(String query) {
			this.query = query;
		}

		public Integer getPage() {
			return page;
		}

		public void setPage(Integer page) {
			this.page = page;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

	}

}
