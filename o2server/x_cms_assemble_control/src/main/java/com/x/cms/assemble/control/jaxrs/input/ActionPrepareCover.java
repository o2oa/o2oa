package com.x.cms.assemble.control.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapPair;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.File;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.wrap.WrapAppDict;
import com.x.cms.core.entity.element.wrap.WrapCategoryInfo;
import com.x.cms.core.entity.element.wrap.WrapCms;
import com.x.cms.core.entity.element.wrap.WrapFile;
import com.x.cms.core.entity.element.wrap.WrapForm;
import com.x.cms.core.entity.element.wrap.WrapScript;

class ActionPrepareCover extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPrepareCover.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			List<Wo> wos = this.adjustForCover(business, wi);
			result.setData(wos);
			return result;
		}
	}

	private List<Wo> adjustForCover(Business business, Wi wi) throws Exception {
		List<Wo> wos = new ArrayList<>();
		AppInfo exist = this.getAppInfo(business, wi.getId(), wi.getAppName(), wi.getAppAlias());
		if (null == exist) {
			throw new ExceptionAppInfoNotExistForCover(wi.getId(), wi.getAppName(), wi.getAppAlias());
		}
		if (!StringUtils.equals(wi.getId(), exist.getId())) {
			wos.add(new Wo(wi.getId(), exist.getId()));
		}
		for (MatchElement<WrapForm, Form> m : this.match(business, wi.getFormList(), ListUtils.union(
				this.listWithIds(business, wi.getFormList(), Form.class), 
				business.getFormFactory().listFormByAppId(exist.getId())))){
			if ((null != m.getW()) && (null != m.getT())) {
				if (!StringUtils.equals(m.getW().getId(), m.getT().getId())) {
					if (StringUtils.equals(m.getW().getAppId(), m.getT().getAppId())) {
						wos.add(new Wo(m.getW().getId(), m.getT().getId()));
					} else {
						wos.add(new Wo(m.getW().getId(), JpaObject.createId()));
					}
				}
			}
		}
		
		for (MatchElement<WrapFile, File> m : this.match(business, wi.getFileList(),
				ListUtils.union(this.listWithIds(business, wi.getFileList(), File.class),
						business.fileFactory().listWithApplicationObject(exist.getId())))) {
			if ((null != m.getW()) && (null != m.getT())) {
				if (!StringUtils.equals(m.getW().getId(), m.getT().getId())) {
					if (StringUtils.equals(m.getW().getAppId(), m.getT().getAppId())) {
						wos.add(new Wo(m.getW().getId(), m.getT().getId()));
					} else {
						wos.add(new Wo(m.getW().getId(), JpaObject.createId()));
					}
				}
			}
		}
		
		for (MatchElement<WrapScript, Script> m : this.match(business, wi.getScriptList(),
				ListUtils.union(this.listWithIds(business, wi.getScriptList(), Script.class),
						business.getScriptFactory().listScriptWithApp(exist.getId())))) {
			if ((null != m.getW()) && (null != m.getT())) {
				if (!StringUtils.equals(m.getW().getId(), m.getT().getId())) {
					if (StringUtils.equals(m.getW().getAppId(), m.getT().getAppId())) {
						wos.add(new Wo(m.getW().getId(), m.getT().getId()));
					} else {
						wos.add(new Wo(m.getW().getId(), JpaObject.createId()));
					}
				}
			}
		}
		for (MatchElement<WrapAppDict, AppDict> m : this.match(business, wi.getAppDictList(),
				ListUtils.union(this.listWithIds(business, wi.getAppDictList(), AppDict.class),
						business.getAppDictFactory().listDictWithAppInfo(exist.getId())))) {
			if ((null != m.getW()) && (null != m.getT())) {
				if (!StringUtils.equals(m.getW().getId(), m.getT().getId())) {
					if (StringUtils.equals(m.getW().getAppId(), m.getT().getAppId())) {
						wos.add(new Wo(m.getW().getId(), m.getT().getId()));
					} else {
						wos.add(new Wo(m.getW().getId(), JpaObject.createId()));
					}
				}
			}
		}
		for (MatchElement<WrapCategoryInfo, CategoryInfo> m : this.match(business, wi.getCategoryInfoList(),
				ListUtils.union(this.listWithIds(business, wi.getCategoryInfoList(), CategoryInfo.class),
						business.getCategoryInfoFactory().listCategoryByAppId(exist.getId(), "全部", 1000000)))) {
			if ((null != m.getW()) && (null != m.getT())) {
				if (!StringUtils.equals(m.getW().getId(), m.getT().getId())) {
					if (StringUtils.equals(m.getW().getAppId(), m.getT().getAppId())) {
						wos.add(new Wo(m.getW().getId(), m.getT().getId()));
					} else {
						wos.add(new Wo(m.getW().getId(), JpaObject.createId()));
					}
				}
			}
		}
		return wos;
	}

	private <W extends JpaObject, T extends JpaObject> List<T> listWithIds(Business business, List<W> list,
			Class<T> cls) throws Exception {
		EntityManager em = business.entityManagerContainer().get(cls);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(cls);
		Root<T> root = cq.from(cls);
		Predicate p = root.get(JpaObject.id_FIELDNAME)
				.in(ListTools.extractField(list, JpaObject.id_FIELDNAME, String.class, true, true));
		cq.select(root).where(p);
		List<T> os = em.createQuery(cq).getResultList();
		return os;
	}

	private <W extends JpaObject, T extends JpaObject> List<MatchElement<W, T>> match(Business business, List<W> ws,
			List<T> ts) throws Exception {
		List<MatchElement<W, T>> list = new ArrayList<>();
		List<W> findWs = new ArrayList<>();
		List<T> findTs = new ArrayList<>();
		loop: for (W w : ListTools.nullToEmpty(ws)) {
			if (!findWs.contains(w)) {
				for (T t : ListTools.nullToEmpty(ts)) {
					if (!findTs.contains(t)) {
						if (StringUtils.equals(w.getId(), t.getId())) {
							MatchElement<W, T> m = new MatchElement<>();
							m.setW(w);
							m.setT(t);
							list.add(m);
							findWs.add(w);
							findTs.add(t);
							continue loop;
						}
					}
				}
			}
		}
		for (W w : ListTools.nullToEmpty(ws)) {
			if (!findWs.contains(w)) {
				MatchElement<W, T> m = new MatchElement<>();
				m.setW(w);
				m.setT(null);
				list.add(m);
			}
		}
		for (T t : ListTools.nullToEmpty(ts)) {
			if (!findTs.contains(t)) {
				MatchElement<W, T> m = new MatchElement<>();
				m.setW(null);
				m.setT(t);
				list.add(m);
			}
		}
		return list;
	}

	public static class MatchElement<W, T> {
		private W w;
		private T t;

		public W getW() {
			return w;
		}

		public void setW(W w) {
			this.w = w;
		}

		public T getT() {
			return t;
		}

		public void setT(T t) {
			this.t = t;
		}

	}

	public static class Wi extends WrapCms {

		private static final long serialVersionUID = -4612391443319365035L;

	}

	public static class Wo extends WrapPair {

		public Wo(String value, String replaceValue) {
			this.setFirst(value);
			this.setSecond(replaceValue);
		}

	}

}