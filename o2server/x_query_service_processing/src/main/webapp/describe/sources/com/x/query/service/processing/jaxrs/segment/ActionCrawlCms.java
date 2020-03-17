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

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.segment.Entry;
import com.x.query.core.entity.segment.Word;
import com.x.query.service.processing.Business;

class ActionCrawlCms extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCrawlCms.class);

	private static DataItemConverter<Item> converter = new DataItemConverter<Item>(Item.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			this.cleanExistEntry(business, id);
			Document document = emc.find(id, Document.class);
			if (null != document) {
				String title = title(document);
				String body = this.body(business, document);
				String attachment = this.attachment(business, document);
				emc.beginTransaction(Entry.class);
				emc.beginTransaction(Word.class);
				Entry entry = this.createEntry(business, document);
				this.setSummary(entry, body, attachment);
				this.keywordPhraseToWord(business, title, body, attachment, entry);
				this.titleToWord(business, title, entry);
				this.bodyToWord(business, body, entry);
				this.attachmentToWord(business, attachment, entry);
				emc.beginTransaction(Entry.class);
				emc.persist(entry, CheckPersistType.all);
				emc.commit();
			}
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private String title(Document document) {
		return StringUtils.deleteWhitespace(document.getTitle());
	}

	private String body(Business business, Document document) throws Exception {
		String value = converter.text(business.entityManagerContainer().listEqualAndEqual(Item.class,
				Item.itemCategory_FIELDNAME, ItemCategory.cms, Item.bundle_FIELDNAME, document.getId()), true, true,
				true, true, true, ",");
		return StringUtils.deleteWhitespace(value);
	}

	private String attachment(Business business, Document document) throws Exception {
		StringBuffer buffer = new StringBuffer();
		for (FileInfo o : business.entityManagerContainer().listEqual(FileInfo.class, FileInfo.documentId_FIELDNAME,
				document.getId())) {
			if (!Config.query().getCrawlCms().getExcludeAttachment().contains(o.getName())) {
				buffer.append(this.storageObjectToText(o));
			}
		}
		return StringUtils.deleteWhitespace(buffer.toString());
	}

	private Entry createEntry(Business business, Document document) {
		Entry entry = new Entry();
		entry.setType(Entry.TYPE_CMS);
		entry.setReference(document.getId());
		entry.setTitle(document.getTitle());
		entry.setBundle(document.getId());
		entry.setAppId(document.getAppId());
		entry.setAppName(document.getAppName());
		entry.setCategoryId(document.getCategoryId());
		entry.setCategoryName(document.getCategoryName());
		entry.setCreatorPerson(document.getCreatorPerson());
		entry.setCreatorUnit(document.getCreatorUnitName());
		return entry;
	}

	private void cleanExistEntry(Business business, String workId) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		List<Entry> os = emc.listEqualAndEqual(Entry.class, Entry.type_FIELDNAME, Entry.TYPE_CMS,
				Entry.reference_FIELDNAME, workId);
		if (!os.isEmpty()) {
			for (Entry en : os) {
				emc.beginTransaction(Entry.class);
				emc.beginTransaction(Word.class);
				for (Word w : emc.listEqual(Word.class, Word.entry_FIELDNAME, en.getId())) {
					emc.remove(w);
				}
				emc.remove(en);
				emc.commit();
			}
		}
	}

	public static class Wo extends WrapBoolean {
	}
}