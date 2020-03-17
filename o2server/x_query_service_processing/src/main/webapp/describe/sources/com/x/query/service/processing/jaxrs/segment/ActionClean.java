/** ***** BEGIN LICENSE BLOCK *****
 * |------------------------------------------------------------------------------|
 * | O2OA 活力办公 创意无限    o2.js                                                 |
 * |------------------------------------------------------------------------------|
 * | Distributed under the AGPL license:                                          |
 * |------------------------------------------------------------------------------|
 * | Copyright © 2018, o2oa.net, o2server.io O2 Team                              |
 * | All rights reserved.                                                         |
 * |------------------------------------------------------------------------------|
 *
 *  This file is part of O2OA.
 *
 *  O2OA is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  O2OA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ******/
package com.x.query.service.processing.jaxrs.segment;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.core.entity.segment.Entry;
import com.x.query.core.entity.segment.Entry_;
import com.x.query.core.entity.segment.Word;
import com.x.query.service.processing.Business;

class ActionClean extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionClean.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, Boolean cleanWork, Boolean cleanWorkCompleted,
			Boolean cleanCms) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (!business.isManager(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.close();
		}
		if (BooleanUtils.isTrue(cleanWork) || BooleanUtils.isTrue(cleanWorkCompleted)
				|| BooleanUtils.isTrue(cleanCms)) {
			Job job = new Job(cleanWork, cleanWorkCompleted, cleanCms);
			job.start();
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public class Job extends Thread {

		private Boolean cleanWork;
		private Boolean cleanWorkCompleted;
		private Boolean cleanCms;

		public Job(Boolean cleanWork, Boolean cleanWorkCompleted, Boolean cleanCms) {
			this.cleanWork = cleanWork;
			this.cleanWorkCompleted = cleanWorkCompleted;
			this.cleanCms = cleanCms;
		}

		@Override
		public void run() {
			List<String> ids = null;
			do {
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business(emc);
					ids = list(business, cleanWork, cleanWorkCompleted, cleanCms);
					if (ListTools.isNotEmpty(ids)) {
						emc.beginTransaction(Entry.class);
						emc.beginTransaction(Word.class);
						for (String id : ids) {
							Entry entry = emc.find(id, Entry.class);
							if (null != entry) {
								for (Word word : emc.listEqual(Word.class, Word.entry_FIELDNAME, entry.getId())) {
									emc.remove(word, CheckRemoveType.all);
								}
								emc.remove(entry, CheckRemoveType.all);
							}
						}
						emc.commit();
					}
				} catch (Exception e) {
					logger.error(e);
				}
			} while (!ListTools.isEmpty(ids));
		}
	}

	private List<String> list(Business business, Boolean cleanWork, Boolean cleanWorkCompleted, Boolean cleanCms)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(Entry.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Entry> root = cq.from(Entry.class);
		Predicate p = cb.disjunction();
		if (BooleanUtils.isTrue(cleanWork)) {
			p = cb.or(p, cb.equal(root.get(Entry_.type), Entry.TYPE_WORK));
		}
		if (BooleanUtils.isTrue(cleanWorkCompleted)) {
			p = cb.or(p, cb.equal(root.get(Entry_.type), Entry.TYPE_WORKCOMPLETED));
		}
		if (BooleanUtils.isTrue(cleanCms)) {
			p = cb.or(p, cb.equal(root.get(Entry_.type), Entry.TYPE_CMS));
		}
		cq.select(root.get(Entry_.id)).where(p);
		List<String> os = em.createQuery(cq).setMaxResults(BATCHSIZE).getResultList();
		return os;
	}

	public static class Wo extends WrapBoolean {
	}
}