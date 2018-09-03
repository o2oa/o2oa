package o2.collect.assemble.jaxrs.module;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

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

import net.sf.ehcache.Element;
import o2.collect.assemble.Business;
import o2.collect.core.entity.Module;
import o2.collect.core.entity.Module_;

class ActionListCategory extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionListCategory.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			logger.debug(effectivePerson, "receive:{}.", wi);
			if (effectivePerson.isAnonymous()) {
				if (null == business.validateUnit(wi.getName(), wi.getPassword())) {
					throw new ExceptionValidateUnitError(wi.getName());
				}
			}
			ActionResult<List<Wo>> result = new ActionResult<>();
			List<Wo> wos = new ArrayList<>();
			String cacheKey = ApplicationCache.concreteCacheKey(this.getClass());
			Element element = business.moduleCache().get(cacheKey);
			if (null != element && (null != element.getObjectValue())) {
				result.setData((List<Wo>) element.getObjectValue());
			} else {
				wos = this.list(business);
				wos = wos.stream()
						.sorted(Comparator.comparing(Wo::getCategory, Comparator.nullsLast(String::compareTo)))
						.collect(Collectors.toList());
				business.moduleCache().put(new Element(cacheKey, wos));
				result.setData(wos);
			}
			return result;
		}
	}

	private List<Wo> list(Business business) throws Exception {
		List<Wo> wos = new ArrayList<>();
		EntityManager em = business.entityManagerContainer().get(Module.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Module> root = cq.from(Module.class);
		Expression<String> exp_category = root.get(Module_.category);
		cq.select(exp_category);
		List<String> os = em.createQuery(cq).getResultList();
		os.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).forEach(o -> {
					Wo wo = new Wo();
					wo.setCategory(o.getKey());
					wo.setCount(o.getValue());
					wos.add(wo);
				});
		return wos;

	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("用户名")
		private String name;

		@FieldDescribe("密码")
		private String password;

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

		@FieldDescribe("数量")
		private Long count;

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}

	}

}