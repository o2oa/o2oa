package com.x.portal.assemble.designer.jaxrs.input;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.general.core.entity.ApplicationDict;
import com.x.general.core.entity.wrap.WrapApplicationDict;
import org.apache.commons.beanutils.BeanUtils;
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
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.File;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Script;
import com.x.portal.core.entity.Widget;
import com.x.portal.core.entity.wrap.WrapFile;
import com.x.portal.core.entity.wrap.WrapPage;
import com.x.portal.core.entity.wrap.WrapPortal;
import com.x.portal.core.entity.wrap.WrapScript;
import com.x.portal.core.entity.wrap.WrapWidget;

class ActionPrepareCover extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPrepareCover.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		// logger.debug(effectivePerson, "jsonElement:{}.", jsonElement);
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
		Portal exist = this.getPortal(business, wi.getId(), wi.getName(), wi.getAlias());
		if (null == exist) {
			throw new ExceptionPortalNotExistForCover(wi.getId(), wi.getName(), wi.getAlias());
		}
		if (!StringUtils.equals(wi.getId(), exist.getId())) {
			wos.add(new Wo(wi.getId(), exist.getId()));
		}
		for (MatchElement<WrapWidget, Widget> m : this.match(business, wi.getWidgetList(),
				ListUtils.union(this.listWithIds(business, wi.getWidgetList(), Widget.class),
						business.widget().listObjectWithPortal(exist.getId())))) {
			if ((null != m.getW()) && (null != m.getT())) {
				if (!StringUtils.equals(m.getW().getId(), m.getT().getId())) {
					if (StringUtils.equals(m.getW().getPortal(), m.getT().getPortal())) {
						wos.add(new Wo(m.getW().getId(), m.getT().getId()));
					} else {
						wos.add(new Wo(m.getW().getId(), JpaObject.createId()));
					}
				}
			}
		}
		for (MatchElement<WrapPage, Page> m : this.match(business, wi.getPageList(),
				ListUtils.union(this.listWithIds(business, wi.getPageList(), Page.class),
						business.page().listObjectWithPortal(exist.getId())))) {
			if ((null != m.getW()) && (null != m.getT())) {
				if (!StringUtils.equals(m.getW().getId(), m.getT().getId())) {
					if (StringUtils.equals(m.getW().getPortal(), m.getT().getPortal())) {
						wos.add(new Wo(m.getW().getId(), m.getT().getId()));
					} else {
						wos.add(new Wo(m.getW().getId(), JpaObject.createId()));
					}
				}
			}
		}
		for (MatchElement<WrapScript, Script> m : this.match(business, wi.getScriptList(),
				ListUtils.union(this.listWithIds(business, wi.getScriptList(), Script.class),
						business.script().listObjectWithPortal(exist.getId())))) {
			if ((null != m.getW()) && (null != m.getT())) {
				if (!StringUtils.equals(m.getW().getId(), m.getT().getId())) {
					if (StringUtils.equals(m.getW().getPortal(), m.getT().getPortal())) {
						wos.add(new Wo(m.getW().getId(), m.getT().getId()));
					} else {
						wos.add(new Wo(m.getW().getId(), JpaObject.createId()));
					}
				}
			}
		}
		for (MatchElement<WrapFile, File> m : this.match(business, wi.getFileList(),
				ListUtils.union(this.listWithIds(business, wi.getFileList(), File.class),
						business.file().listWithPortalObject(exist.getId())))) {
			if ((null != m.getW()) && (null != m.getT())) {
				if (!StringUtils.equals(m.getW().getId(), m.getT().getId())) {
					if (StringUtils.equals(m.getW().getPortal(), m.getT().getPortal())) {
						wos.add(new Wo(m.getW().getId(), m.getT().getId()));
					} else {
						wos.add(new Wo(m.getW().getId(), JpaObject.createId()));
					}
				}
			}
		}
		for (MatchElement<WrapApplicationDict, ApplicationDict> m : this.match(business, wi.getApplicationDictList(),
				ListUtils.union(this.listWithIds(business, wi.getApplicationDictList(), ApplicationDict.class),
						business.applicationDict().listWithApplicationObject(exist.getId())))) {
			if ((null != m.getW()) && (null != m.getT())) {
				if (!StringUtils.equals(m.getW().getId(), m.getT().getId())) {
					if (StringUtils.equals(m.getW().getApplication(), m.getT().getApplication())) {
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
		loop: for (W w : ListTools.nullToEmpty(ws)) {
			if (!findWs.contains(w)) {
				for (T t : ListTools.nullToEmpty(ts)) {
					if (!findTs.contains(t)) {
						if (StringUtils.isNotEmpty(BeanUtils.getProperty(w, "name")) && StringUtils
								.equals(BeanUtils.getProperty(w, "name"), BeanUtils.getProperty(t, "name"))) {
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
		loop: for (W w : ListTools.nullToEmpty(ws)) {
			if (!findWs.contains(w)) {
				for (T t : ListTools.nullToEmpty(ts)) {
					if (!findTs.contains(t)) {
						if (StringUtils.isNotEmpty(BeanUtils.getProperty(w, "alias")) && StringUtils
								.equals(BeanUtils.getProperty(w, "alias"), BeanUtils.getProperty(t, "alias"))) {
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

	public static class Wi extends WrapPortal {

		private static final long serialVersionUID = -4612391443319365035L;

	}

	public static class Wo extends WrapPair {

		public Wo(String value, String replaceValue) {
			this.setFirst(value);
			this.setSecond(replaceValue);
		}

	}

}
