package o2.collect.assemble.jaxrs.module;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

import net.sf.ehcache.Element;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Module;
import o2.collect.core.entity.Module_;

class ActionList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			logger.debug(effectivePerson, "receive:{}.", wi);
//			if (effectivePerson.isAnonymous()) {
//				if (null == business.validateUnit(wi.getName(), wi.getPassword())) {
//					throw new ExceptionValidateUnitError(wi.getName());
//				}
//			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass(),
					StringUtils.trimToEmpty(StringUtils.join(wi.getCategoryList(), ",")));
			Element element = business.moduleCache().get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				wos = this.list(business, wi);
				Collator collatorChinese = Collator.getInstance(java.util.Locale.CHINA);
				for (Wo wo : wos) {
					wo.setModuleList(wo.getModuleList().stream().sorted(new Comparator<BriefModule>() {
						public int compare(BriefModule o1, BriefModule o2) {
							return collatorChinese.compare(o1.getName(), o2.getName());
						}
					}).collect(Collectors.toList()));
				}
				wos = wos.stream().sorted(new Comparator<Wo>() {
					public int compare(Wo o1, Wo o2) {
						return collatorChinese.compare(o1.getCategory(), o2.getCategory());
					}
				}).collect(Collectors.toList());
				business.moduleCache().put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			return result;
		}
	}

	private List<Wo> list(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Module.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
		Root<Module> root = cq.from(Module.class);
		Expression<String> exp_category = root.get(Module_.category);
		Expression<String> exp_brief = root.get(Module_.brief);
		Expression<String> exp_icon = root.get(Module_.icon);
		Predicate p = cb.conjunction();
		if (ListTools.isNotEmpty(wi.getCategoryList())) {
			p = cb.and(p, root.get(Module_.category).in(wi.getCategoryList()));
		}
		cq.multiselect(exp_category, exp_brief, exp_icon).where(p);
		List<Tuple> list = em.createQuery(cq).getResultList();
		for (Tuple o : list) {
			Wo wo = null;
			String category = o.get(exp_category);
			for (Wo w : wos) {
				if (StringUtils.equals(category, w.getCategory())) {
					wo = w;
					break;
				}
			}
			if (null == wo) {
				wo = new Wo();
				wo.setCategory(category);
				wos.add(wo);
			}
			BriefModule briefModule = gson.fromJson(o.get(exp_brief), BriefModule.class);
			/** 设置图标,如果没有图标设置空值 */
			if (StringUtils.isNotEmpty(o.get(exp_icon))) {
				briefModule.setIcon(o.get(exp_icon));
			} else {
				briefModule.setIcon("");
			}
			wo.getModuleList().add(briefModule);
		}
		return wos;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("分类")
		private List<String> categoryList;

		@FieldDescribe("用户名")
		private String name;

		@FieldDescribe("密码")
		private String password;

		public List<String> getCategoryList() {
			return categoryList;
		}

		public void setCategoryList(List<String> categoryList) {
			this.categoryList = categoryList;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("分类")
		private String category;

		@FieldDescribe("模块")
		private List<BriefModule> moduleList = new ArrayList<>();

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public List<BriefModule> getModuleList() {
			return moduleList;
		}

		public void setModuleList(List<BriefModule> moduleList) {
			this.moduleList = moduleList;
		}

	}

}