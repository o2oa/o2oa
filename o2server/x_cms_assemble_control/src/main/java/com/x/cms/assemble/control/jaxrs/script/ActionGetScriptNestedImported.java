package com.x.cms.assemble.control.jaxrs.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.Script;

class ActionGetScriptNestedImported extends BaseAction {

//	private static Logger logger = LoggerFactory.getLogger(ScriptAction.class);

//	@SuppressWarnings("deprecation")
	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String uniqueName,
			String flag) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		Boolean check = true;

		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

				Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), uniqueName, flag );
				Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

				if (optional.isPresent()) {
					wrap = (Wo) optional.get();
				} else {
					Business business = new Business(emc);
					AppInfo appInfo = business.getAppInfoFactory().flag(flag);
					if (null == appInfo) {
						throw new Exception("appInfo{'flag':" + flag + "} not existed.");
					}
					List<Script> list = new ArrayList<>();
					for (Script o : business.getScriptFactory()
							.listScriptNestedWithAppInfoWithUniqueName(appInfo.getId(), uniqueName)) {
						list.add(o);
					}
					StringBuffer buffer = new StringBuffer();
					List<String> imported = new ArrayList<>();
					for (Script o : list) {
						buffer.append(o.getText());
						buffer.append(System.lineSeparator());
						imported.add(o.getId());
						imported.add(o.getName());
						imported.add(o.getAlias());
					}
					wrap = new Wo();
					wrap.setImportedList(imported);
					wrap.setText(buffer.toString());
					wrap.setAppId(appInfo.getId());
					wrap.setAppName(appInfo.getAppName());
					wrap.setAppAlias(appInfo.getAppAlias());
					CacheManager.put(cacheCategory, cacheKey, wrap );
				}
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
			result.setData(wrap);
		}
		return result;
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("导入的脚本ID.")
		private List<String> importedList;

		public List<String> getImportedList() {
			return importedList;
		}

		public void setImportedList(List<String> importedList) {
			this.importedList = importedList;
		}

	}

	public static class Wo extends GsonPropertyObject {

		private String text;

		private List<String> importedList;

		private String appId;

		private String appName;

		private String appAlias;

		public List<String> getImportedList() {
			return importedList;
		}

		public void setImportedList(List<String> importedList) {
			this.importedList = importedList;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}

		public String getAppAlias() {
			return appAlias;
		}

		public void setAppAlias(String appAlias) {
			this.appAlias = appAlias;
		}
	}
}
