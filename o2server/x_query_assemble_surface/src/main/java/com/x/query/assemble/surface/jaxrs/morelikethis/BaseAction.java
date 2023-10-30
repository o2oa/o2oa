package com.x.query.assemble.surface.jaxrs.morelikethis;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import com.hankcs.hanlp.HanLP;
import com.x.base.core.entity.dataitem.DataItem;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.NumberTools;
import com.x.cms.core.entity.Document;
import com.x.processplatform.core.entity.content.Data;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.query.assemble.surface.Business;
import com.x.query.core.entity.Item;
import com.x.query.core.express.index.Indexs;

abstract class BaseAction extends StandardJaxrsAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

	private static final DataItemConverter<Item> CONVERTER = new DataItemConverter<>(Item.class);

	protected Optional<Pair<String, String>> summary(Business business, String category, String flag) {
		try {
			if (StringUtils.equalsIgnoreCase(category, Indexs.CATEGORY_PROCESSPLATFORM)) {
				return this.summaryProcessPlatformWorkOrWorkCompleted(business, flag);
			} else if (StringUtils.equalsIgnoreCase(category, Indexs.CATEGORY_CMS)) {
				return this.summaryCmsDocument(business, flag);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return Optional.empty();
	}

	protected Optional<Pair<String, String>> summaryProcessPlatformWorkOrWorkCompleted(Business business, String flag) {
		try {
			WorkCompleted workCompleted = business.entityManagerContainer().find(flag, WorkCompleted.class);
			List<Item> items = null;
			String job = null;
			if (null != workCompleted) {
				job = workCompleted.getJob();
				if (BooleanUtils.isTrue(workCompleted.getMerged())) {
					Data data = workCompleted.getData();
					items = CONVERTER.disassemble(gson.toJsonTree(data));
				} else {
					items = business.entityManagerContainer().listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME,
							workCompleted.getJob(), DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
				}
			} else {
				Work work = business.entityManagerContainer().find(flag, Work.class);
				if (null != work) {
					job = work.getJob();
					items = business.entityManagerContainer().listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME,
							work.getJob(), DataItem.itemCategory_FIELDNAME, ItemCategory.pp);
				}
			}
			if (ListTools.isNotEmpty(items)) {
				String body = DataItemConverter.ItemText.text(items, true, true, true, true, true, ",");
				return Optional.of(Pair.of(job, HanLP.getSummary(body, Config.query().index().getSummaryLength())));
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return Optional.empty();
	}

	protected Optional<Pair<String, String>> summaryCmsDocument(Business business, String flag) {
		try {
			Document document = business.entityManagerContainer().find(flag, Document.class);
			List<Item> items = null;
			if (null != document) {
				items = business.entityManagerContainer().listEqualAndEqual(Item.class, DataItem.bundle_FIELDNAME,
						document.getId(), DataItem.itemCategory_FIELDNAME, ItemCategory.cms);
			}
			if (ListTools.isNotEmpty(items)) {
				String body = DataItemConverter.ItemText.text(items, true, true, true, true, true, ",");
				return Optional.of(
						Pair.of(document.getId(), HanLP.getSummary(body, Config.query().index().getSummaryLength())));
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return Optional.empty();
	}

	protected Optional<Query> idQuery(String id) {
		return Optional.of(new TermQuery(new Term(Indexs.FIELD_ID, id)));
	}

	protected Optional<Query> filterCategoryQuery(String filterCategory) {
		if (StringUtils.isNotBlank(filterCategory)) {
			return Optional.of(new TermQuery(new Term(Indexs.FIELD_CATEGORY, filterCategory)));
		} else {
			return Optional.empty();
		}
	}

	protected Optional<Query> filterTypeQuery(String filterType) {
		if (StringUtils.isNotBlank(filterType)) {
			return Optional.of(new TermQuery(new Term(Indexs.FIELD_TYPE, filterType)));
		} else {
			return Optional.empty();
		}
	}

	protected Integer moreLikeThisCount(Integer count) throws Exception {
		if (NumberTools.nullOrLessThan(count, 1)) {
			return Config.query().index().getMoreLikeThisSize();
		} else if (NumberTools.greaterThan(count, Config.query().index().getMoreLikeThisMaxSize())) {
			return Config.query().index().getMoreLikeThisMaxSize();
		} else {
			return count;
		}
	}

	protected Optional<Query> filterKeyQuery(String filterKey) {
		if (StringUtils.isNotBlank(filterKey)) {
			return Optional.of(new TermQuery(new Term(Indexs.FIELD_KEY, filterKey)));
		} else {
			return Optional.empty();
		}
	}
}