package com.x.portal.assemble.designer.jaxrs.designer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.enums.DesignerType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WiDesigner;
import com.x.base.core.project.jaxrs.WrapDesigner;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.PropertyTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.ThisApplication;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Widget;

class ActionSearch extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSearch.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		if (!effectivePerson.isManager()) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		logger.debug("{}开始门户设计搜索，关键字：{}", effectivePerson.getDistinguishedName(), wi.getKeyword());
		if (StringUtils.isBlank(wi.getKeyword())) {
			throw new ExceptionFieldEmpty("keyword");
		}
		ActionResult<List<Wo>> result = new ActionResult<>();

		List<Wo> resWos = new ArrayList<>();
		List<CompletableFuture<List<Wo>>> list = new ArrayList<>();
		Map<String, List<String>> designerMap = wi.getAppDesigner();
		List<String> appList = wi.getAppIdList();
		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.page.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.page.toString()))) {
			list.add(searchPage(wi, appList, designerMap.get(DesignerType.page.toString())));
		}
		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.script.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.script.toString()))) {
			list.add(searchScript(wi, appList, designerMap.get(DesignerType.script.toString())));
		}
		if ((wi.getDesignerTypes().isEmpty() || wi.getDesignerTypes().contains(DesignerType.widget.toString()))
				&& (designerMap.isEmpty() || designerMap.containsKey(DesignerType.widget.toString()))) {
			list.add(searchWidget(wi, appList, designerMap.get(DesignerType.widget.toString())));
		}
		for (CompletableFuture<List<Wo>> cf : list) {
			if (resWos.size() < 50) {
				resWos.addAll(cf.get(60, TimeUnit.SECONDS));
			}
		}
		if (resWos.size() > 50) {
			resWos = resWos.subList(0, 50);
		}
		result.setData(resWos);
		result.setCount((long) resWos.size());
		return result;
	}

	private CompletableFuture<List<Wo>> searchScript(final Wi wi, final List<String> appIdList,
			final List<String> designerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<WoScript> woScripts;
				if (ListTools.isNotEmpty(designerIdList)) {
					woScripts = emc.fetchIn(Script.class, WoScript.copier, Script.id_FIELDNAME, designerIdList);
				} else if (ListTools.isNotEmpty(appIdList)) {
					woScripts = emc.fetchIn(Script.class, WoScript.copier, Script.portal_FIELDNAME, appIdList);
				} else {
					woScripts = emc.fetchAll(Script.class, WoScript.copier);
				}

				for (WoScript woScript : woScripts) {
					Map<String, String> map = PropertyTools.fieldMatchKeyword(WoScript.copier.getCopyFields(), woScript,
							wi.getKeyword(), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
					if (!map.isEmpty()) {
						Wo wo = new Wo();
						Portal portal = emc.fetch(woScript.getPortal(), Portal.class,
								ListTools.toList(Portal.id_FIELDNAME, Portal.name_FIELDNAME));
						if (portal != null) {
							wo.setAppId(portal.getId());
							wo.setAppName(portal.getName());
						}
						wo.setDesignerId(woScript.getId());
						wo.setDesignerName(woScript.getName());
						wo.setDesignerType(DesignerType.script.toString());
						wo.setUpdateTime(woScript.getUpdateTime());
						wo.setPatternList(map);
						resWos.add(wo);
					}
				}
				woScripts.clear();
				woScripts = null;
			} catch (Exception e) {
				logger.error(e);
			}
			return resWos;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<Wo>> searchPage(final Wi wi, final List<String> appIdList,
			final List<String> designerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> ids = designerIdList;
				if (ListTools.isEmpty(ids)) {
					ids = business.page().listWithPortals(appIdList);
				}
				for (List<String> partIds : ListTools.batch(ids, 100)) {
					List<WoPage> wos = emc.fetchIn(Page.class, WoPage.copier, Page.id_FIELDNAME, partIds);
					for (WoPage wopage : wos) {
						Map<String, String> map = PropertyTools.fieldMatchKeyword(WoPage.copier.getCopyFields(), wopage,
								wi.getKeyword(), wi.getCaseSensitive(), wi.getMatchWholeWord(), wi.getMatchRegExp());
						if (!map.isEmpty()) {
							Wo wo = new Wo();
							Portal portal = emc.fetch(wopage.getPortal(), Portal.class,
									ListTools.toList(Portal.id_FIELDNAME, Portal.name_FIELDNAME));
							if (portal != null) {
								wo.setAppId(portal.getId());
								wo.setAppName(portal.getName());
							}
							wo.setDesignerId(wopage.getId());
							wo.setDesignerName(wopage.getName());
							wo.setDesignerType(DesignerType.page.toString());
							wo.setUpdateTime(wopage.getUpdateTime());
							wo.setPatternList(map);
							resWos.add(wo);
						}
					}
					wos.clear();
					wos = null;
				}

			} catch (Exception e) {
				logger.error(e);
			}
			return resWos;
		}, ThisApplication.forkJoinPool());
	}

	private CompletableFuture<List<Wo>> searchWidget(final Wi wi, final List<String> appIdList,
			final List<String> designerIdList) {
		return CompletableFuture.supplyAsync(() -> {
			List<Wo> resWos = new ArrayList<>();
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				List<String> ids = designerIdList;
				if (ListTools.isEmpty(ids)) {
					ids = business.widget().listWithPortals(appIdList);
				}
				for (List<String> partIds : ListTools.batch(ids, 100)) {
					List<WoWidget> wos = emc.fetchIn(Widget.class, WoWidget.copier, WoWidget.id_FIELDNAME, partIds);
					for (WoWidget woWidget : wos) {
						Map<String, String> map = PropertyTools.fieldMatchKeyword(WoWidget.copier.getCopyFields(),
								woWidget, wi.getKeyword(), wi.getCaseSensitive(), wi.getMatchWholeWord(),
								wi.getMatchRegExp());
						if (!map.isEmpty()) {
							Wo wo = new Wo();
							Portal portal = emc.fetch(woWidget.getPortal(), Portal.class,
									ListTools.toList(Portal.id_FIELDNAME, Portal.name_FIELDNAME));
							if (portal != null) {
								wo.setAppId(portal.getId());
								wo.setAppName(portal.getName());
							}
							wo.setDesignerId(woWidget.getId());
							wo.setDesignerName(woWidget.getName());
							wo.setDesignerType(DesignerType.widget.toString());
							wo.setUpdateTime(woWidget.getUpdateTime());
							wo.setPatternList(map);
							resWos.add(wo);
						}
					}
					wos.clear();
					wos = null;
				}

			} catch (Exception e) {
				logger.error(e);
			}
			return resWos;
		}, ThisApplication.forkJoinPool());
	}

	public static class Wi extends WiDesigner {

		private static final long serialVersionUID = -7152350989100117879L;

	}

	public static class Wo extends WrapDesigner {

		private static final long serialVersionUID = -7129213500959112033L;

	}

	public static class WoScript extends Script {

		private static final long serialVersionUID = -8330747166580465899L;
		static WrapCopier<Script, WoScript> copier = WrapCopierFactory.wo(Script.class, WoScript.class,
				JpaObject.singularAttributeField(Script.class, true, false), null);

	}

	public static class WoPage extends Page {

		private static final long serialVersionUID = -3964257854306385058L;
		static WrapCopier<Page, WoPage> copier = WrapCopierFactory.wo(Page.class, WoPage.class,
				JpaObject.singularAttributeField(WoPage.class, true, false), null);

	}

	public static class WoWidget extends Widget {

		private static final long serialVersionUID = 7475514889463657235L;
		static WrapCopier<Widget, WoWidget> copier = WrapCopierFactory.wo(Widget.class, WoWidget.class,
				JpaObject.singularAttributeField(WoWidget.class, true, false), null);

	}

}
