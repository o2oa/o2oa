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
import com.x.query.core.entity.segment.Word;
import com.x.query.service.processing.Business;

class ActionCleanWithTypeWork extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCleanWithTypeWork.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (!business.isManager(effectivePerson)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			emc.close();
		}
		ActionResult<Wo> result = new ActionResult<>();
		Job job = new Job();
		job.start();
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public class Job extends Thread {
		@Override
		public void run() {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				List<String> ids = emc.idsEqual(Entry.class, Entry.type_FIELDNAME, Entry.TYPE_WORK);
				int batch = 1;
				for (List<String> list : ListTools.batch(ids, batchSize)) {
					logger.debug("开始清空分词数据, 数据类型: {}, 数据数量: {},  批次大小: {}, 批次: {}.", Entry.TYPE_WORK, ids.size(),
							batchSize, batch++);
					emc.beginTransaction(Word.class);
					for (Word wd : emc.listIn(Word.class, Word.entry_FIELDNAME, list)) {
						emc.remove(wd, CheckRemoveType.all);
					}
					emc.commit();
					emc.beginTransaction(Entry.class);
					for (Entry en : emc.list(Entry.class, list)) {
						emc.remove(en, CheckRemoveType.all);
					}
					emc.commit();
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	public static class Wo extends WrapBoolean {
	}
}