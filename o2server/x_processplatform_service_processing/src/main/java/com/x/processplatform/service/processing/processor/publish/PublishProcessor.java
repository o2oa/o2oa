package com.x.processplatform.service.processing.processor.publish;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.Applications;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.scripting.JsonScriptingExecutor;
import com.x.base.core.project.scripting.ScriptingFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.x_query_service_processing;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.element.Publish;
import com.x.processplatform.core.entity.element.PublishTable;
import com.x.processplatform.core.entity.element.Route;
import com.x.processplatform.core.entity.log.Signal;
import com.x.processplatform.service.processing.Business;
import com.x.processplatform.service.processing.ThisApplication;
import com.x.processplatform.service.processing.WrapScriptObject;
import com.x.processplatform.service.processing.processor.AeiObjects;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据发布节点处理器
 * @author sword
 */
public class PublishProcessor extends AbstractPublishProcessor {

	public static final Logger LOGGER = LoggerFactory.getLogger(PublishProcessor.class);

	public PublishProcessor(EntityManagerContainer entityManagerContainer) throws Exception {
		super(entityManagerContainer);
	}

	@Override
	protected Work arriving(AeiObjects aeiObjects, Publish publish) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.publishArrive(aeiObjects.getWork().getActivityToken(), publish));
		return aeiObjects.getWork();
	}

	@Override
	protected void arrivingCommitted(AeiObjects aeiObjects, Publish publish) throws Exception {
		// Do nothing
	}

	@Override
	protected List<Work> executing(AeiObjects aeiObjects, Publish publish) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.publishExecute(aeiObjects.getWork().getActivityToken(), publish));
		List<Work> results = new ArrayList<>();
		boolean passThrough = false;
		switch (publish.getPublishTarget()) {
			case Publish.PUBLISH_TARGET_CMS:
				// 可以根据返回脚本判断时候流转
				passThrough = true;
				break;
			case Publish.PUBLISH_TARGET_TABLE:
				// 可以根据返回脚本判断时候流转
				passThrough = this.publishToTable(aeiObjects, publish);
				break;
			default:
				break;
		}
		if (passThrough) {
			results.add(aeiObjects.getWork());
		} else {
			LOGGER.info("work title:{}, id:{} public return false, stay in the current activity.",
					() -> aeiObjects.getWork().getTitle(), () -> aeiObjects.getWork().getId());
		}
		if (passThrough) {
			results.add(aeiObjects.getWork());
		}
		return results;
	}

	private boolean publishToTable(AeiObjects aeiObjects, Publish publish) throws Exception {
		List<AssignTable> list = this.evalTableBody(aeiObjects, publish);
		boolean flag = true;
		for (AssignTable assignTable : list){
			WrapBoolean resp = ThisApplication.context().applications().postQuery(x_query_service_processing.class,
					Applications.joinQueryUri("table", assignTable.getTableName(), "update", aeiObjects.getWork().getJob()), assignTable.getData())
					.getData(WrapBoolean.class);
			LOGGER.debug("publish to table：{}, result：{}",assignTable.getTableName(),resp.getValue());
			if(BooleanUtils.isFalse(resp.getValue())){
				flag = false;
			}
		}
		return flag;
	}

	private List<AssignTable> evalTableBody(AeiObjects aeiObjects, Publish publish) throws Exception {
		List<AssignTable> list = new ArrayList<>();
		if(ListTools.isNotEmpty(publish.getPublishTableList())){
			for (PublishTable publishTable : publish.getPublishTableList()){
				AssignTable assignTable = new AssignTable();
				assignTable.setTableName(publishTable.getTableName());
				if(PublishTable.TABLE_DATA_BY_PATH.equals(publishTable.getQueryTableDataBy())){
					if(StringUtils.isNotBlank(publishTable.getQueryTableDataPath())){
						Object o = aeiObjects.getData().find(publishTable.getQueryTableDataPath());
						if(o!=null){
							assignTable.setData(gson.toJsonTree(o));
						}
					}
				}else {
					WrapScriptObject assignBody = new WrapScriptObject();
					if (hasTableAssignDataScript(publishTable)) {
						ScriptContext scriptContext = aeiObjects.scriptContext();
						CompiledScript cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
								publishTable.getTargetAssignDataScript(), publishTable.getTargetAssignDataScriptText());
						scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).put(ScriptingFactory.BINDING_NAME_JAXRSBODY,
								assignBody);
						JsonScriptingExecutor.jsonElement(cs, scriptContext, o -> {
							if (!o.isJsonNull()) {
								assignTable.setData(o);
							}
						});
					}
				}
				if(assignTable.getData() == null){
					assignTable.setData(gson.toJsonTree(aeiObjects.getData()));
				}
				list.add(assignTable);
			}
		}
		return list;
	}

	private String evalCmsBody(AeiObjects aeiObjects, Publish publish) throws Exception {
		WrapScriptObject assignBody = new WrapScriptObject();
		if (hasCmsAssignDataScript(publish)) {
			ScriptContext scriptContext = aeiObjects.scriptContext();
			CompiledScript cs = aeiObjects.business().element().getCompiledScript(aeiObjects.getApplication().getId(),
					aeiObjects.getActivity(), Business.EVENT_PUBLISHCMSBODY);
			scriptContext.getBindings(ScriptContext.ENGINE_SCOPE).put(ScriptingFactory.BINDING_NAME_JAXRSBODY,
					assignBody);
			JsonScriptingExecutor.jsonElement(cs, scriptContext, o -> {
				if (!o.isJsonNull()) {
					assignBody.set(gson.toJson(o));
				}
			});
		}
		return assignBody.get();
	}

	public class AssignTable {

		private String tableName;

		private JsonElement data;

		public String getTableName() {
			return tableName;
		}

		public void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}
	}

	@Override
	protected void executingCommitted(AeiObjects aeiObjects, Publish publish, List<Work> works) throws Exception {
		// Do nothing
	}

	@Override
	protected List<Route> inquiring(AeiObjects aeiObjects, Publish publish) throws Exception {
		// 发送ProcessingSignal
		aeiObjects.getProcessingAttributes()
				.push(Signal.publishInquire(aeiObjects.getWork().getActivityToken(), publish));
		List<Route> results = new ArrayList<>();
		results.add(aeiObjects.getRoutes().get(0));
		return results;
	}

	@Override
	protected void inquiringCommitted(AeiObjects aeiObjects, Publish publish) throws Exception {
		// Do nothing
	}

	private boolean hasCmsAssignDataScript(Publish publish) {
		return StringUtils.isNotEmpty(publish.getTargetAssignDataScript())
				|| StringUtils.isNotEmpty(publish.getTargetAssignDataScriptText());
	}

	private boolean hasTableAssignDataScript(PublishTable publishTable) {
		return StringUtils.isNotEmpty(publishTable.getTargetAssignDataScript())
				|| StringUtils.isNotEmpty(publishTable.getTargetAssignDataScriptText());
	}
}
