package com.x.query.service.processing.jaxrs.index;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.tika.Tika;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hankcs.lucene.HanLPAnalyzer;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.dataitem.DataItemConverter;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.tuple.Quadruple;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.query.core.entity.Item;
import com.x.query.core.express.index.Indexs;
import com.x.query.service.processing.ThisApplication;
import com.x.query.service.processing.index.Doc;

class ActionUpdateExtraDocument extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateExtraDocument.class);

	private static final AtomicReference<Date> LASTCLEAN = new AtomicReference<>(new Date());

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		check(wi);
		Optional<Directory> optional = Indexs.directory(Indexs.CATEGORY_EXTRA, wi.getKey(), true);
		if (optional.isPresent()) {
			Doc doc = wrapExtra(wi);
			if (null != doc) {
				this.update(optional.get(), doc);
			}
		}
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	private void check(Wi wi) throws ExceptionEmptyField {

		if (StringUtils.isBlank(wi.getType())) {
			throw new ExceptionEmptyField("type");
		}

		if (StringUtils.isBlank(wi.getKey())) {
			throw new ExceptionEmptyField("key");
		}

		if (StringUtils.isBlank(wi.getId())) {
			throw new ExceptionEmptyField("id");
		}

		if (null == wi.getCreateTime()) {
			throw new ExceptionEmptyField("createTime");
		}

		if (null == wi.getUpdateTime()) {
			throw new ExceptionEmptyField("updateTime");
		}

		if (null == wi.getCreatorPerson()) {
			throw new ExceptionEmptyField("creatorPerson");
		}

		if (null == wi.getCreatorUnit()) {
			throw new ExceptionEmptyField("creatorUnit");
		}

	}

	private Doc wrapExtra(Wi wi) {
		try {
			Doc doc = new Doc();
			doc.setReaders(
					ListTools.isEmpty(wi.getReaderList()) ? List.of(Indexs.READERS_SYMBOL_ALL) : wi.getReaderList());
			doc.setId(wi.getId());
			doc.setCategory(Indexs.CATEGORY_EXTRA);
			doc.setType(wi.getType());
			doc.setKey(wi.getKey());
			doc.setTitle(wi.getTitle());
			doc.setCreateTime(wi.getCreateTime());
			doc.setUpdateTime(wi.getUpdateTime());
			doc.setCreateTimeMonth(DateTools.format(wi.getCreateTime(), DateTools.format_yyyyMM));
			doc.setUpdateTimeMonth(DateTools.format(wi.getUpdateTime(), DateTools.format_yyyyMM));
			doc.setCreatorPerson(OrganizationDefinition.name(wi.getCreatorPerson()));
			if (StringUtils.isEmpty(doc.getCreatorPerson())) {
				doc.setCreatorPerson(Config.query().index().getCreatorPersonUnknown());
			}
			if (StringUtils.equals(EffectivePerson.CIPHER, doc.getCreatorPerson())) {
				doc.setCreatorPerson(Config.query().index().getCreatorPersonCipher());
			}
			doc.setCreatorUnit(OrganizationDefinition.name(wi.getCreatorUnit()));
			if (StringUtils.isEmpty(doc.getCreatorUnit())) {
				doc.setCreatorUnit(Config.query().index().getCreatorUnitUnknown());
			}
			if (null != wi.getData()) {
				DataItemConverter<Item> converter = new DataItemConverter<>(Item.class);
				List<Item> items = converter.disassemble(wi.getData());
				doc.setBody(DataItemConverter.ItemText.text(items, true, true, true, true, true, ","));
			}
			doc.setAttachment(this.attachment(wi.getStorageList()));
			return doc;
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return null;
	}

	private void update(Directory directory, Doc doc) {
		try (directory; Analyzer analyzer = new HanLPAnalyzer()) {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			try (IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig)) {
				LOGGER.debug("update extra, directory:{}.", directory.toString());
				indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
				Document document = doc.toDocument(false);
				indexWriter.updateDocument(new Term(Indexs.FIELD_ID, document.get(Indexs.FIELD_ID)), document);
				clean(indexWriter);
				indexWriter.commit();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private String attachment(List<Quadruple<String, StorageType, String, String>> storageList) throws Exception {
		List<String> list = new ArrayList<>();
		Tika tika = new Tika();
		for (Quadruple<String, StorageType, String, String> entry : storageList) {
			try {
				StorageMapping mapping = ThisApplication.context().storageMappings().get(entry.second(), entry.third());
				if (null != mapping) {
					StorageObject storageObject = new StorageObject() {

						private static final long serialVersionUID = -469076029526530187L;

						@Override
						public String path() throws Exception {
							return entry.first();
						}

						@Override
						public String getStorage() {
							return null;
						}

						@Override
						public void setStorage(String storage) {

						}

						@Override
						public Long getLength() {
							return null;
						}

						@Override
						public void setLength(Long length) {

						}

						@Override
						public String getName() {
							return null;
						}

						@Override
						public void setName(String name) {
						}

						@Override
						public String getExtension() {
							return null;
						}

						@Override
						public void setExtension(String extension) {
						}

						@Override
						public Date getLastUpdateTime() {
							return null;
						}

						@Override
						public void setLastUpdateTime(Date lastUpdateTime) {
						}

						@Override
						public Boolean getDeepPath() {
							return null;
						}

						@Override
						public void setDeepPath(Boolean deepPath) {
						}

						@Override
						public void onPersist() throws Exception {
						}

						@Override
						public String getId() {
							return null;
						}

						@Override
						public void setId(String id) {
						}

					};
					list.add(entry.fourth());
					byte[] bytes = storageObject.readContent(mapping);
					if (bytes.length > 0
							&& bytes.length < Config.query().index().getAttachmentMaxSize() * 1024 * 1024) {
						try (ByteArrayInputStream input = new ByteArrayInputStream(bytes)) {
							list.add(tika.parseToString(input));
						}
					}
				} else {
					LOGGER.warn("storageMapping is null, storageType:{}, name:{}.", entry.second(), entry.third());
				}
			} catch (Throwable th) {
				// 需要Throwable,tika可能抛出Error
				LOGGER.warn("error extract attachment text, message:{}.", th.getMessage());
			}
		}
		return StringUtils.join(list, ",");
	}

	private void clean(IndexWriter indexWriter) throws Exception {
		if ((new Date()).getTime() - LASTCLEAN.get().getTime() > 1000 * 60 * 10) {
			final Date threshold = DateUtils.addDays(new Date(), -Config.query().index().getCleanupThresholdDays());
			Query rangeQuery = LongPoint.newRangeQuery(Indexs.FIELD_INDEXTIME, Long.MIN_VALUE, threshold.getTime());
			indexWriter.deleteDocuments(rangeQuery);
			LASTCLEAN.set(new Date());
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -7574944254138274754L;

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 8642852156734924824L;

		@FieldDescribe("文档分类,在扩展中可以保持为空")
		private String type = "";

		@FieldDescribe("索引仓库名称,最终将以EXTRA_{key}的形式创建索引目录")
		private String key = "";

		@FieldDescribe("文档标识")
		private String id = "";

		@FieldDescribe("业务数据")
		private JsonObject data;

		@FieldDescribe("存储路径,存储类型,存储名称,附件标题")
		private List<Quadruple<String, StorageType, String, String>> storageList = new ArrayList<>();

		@FieldDescribe("可阅读人员列表")
		private List<String> readerList = new ArrayList<>();

		@FieldDescribe("标题")
		private String title = "";

		@FieldDescribe("创建时间")
		private Date createTime;

		@FieldDescribe("更新时间")
		private Date updateTime;

		@FieldDescribe("创建人")
		private String creatorPerson = "";

		@FieldDescribe("创建身份所在组织")
		private String creatorUnit = "";

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public JsonObject getData() {
			return data;
		}

		public void setData(JsonObject data) {
			this.data = data;
		}

		public List<Quadruple<String, StorageType, String, String>> getStorageList() {
			return storageList;
		}

		public void setStorageList(List<Quadruple<String, StorageType, String, String>> storageList) {
			this.storageList = storageList;
		}

		public List<String> getReaderList() {
			return readerList;
		}

		public void setReaderList(List<String> readerList) {
			this.readerList = readerList;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public Date getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(Date updateTime) {
			this.updateTime = updateTime;
		}

		public String getCreatorPerson() {
			return creatorPerson;
		}

		public void setCreatorPerson(String creatorPerson) {
			this.creatorPerson = creatorPerson;
		}

		public String getCreatorUnit() {
			return creatorUnit;
		}

		public void setCreatorUnit(String creatorUnit) {
			this.creatorUnit = creatorUnit;
		}

	}

}
