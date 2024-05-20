package com.x.query.service.processing;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;

import com.hankcs.lucene.HanLPAnalyzer;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.cms.core.entity.AppInfo;
import com.x.processplatform.core.entity.element.Application;
import com.x.query.core.express.index.Indexs;
import com.x.query.service.processing.index.Doc;

public class IndexWriteQueue extends AbstractQueue<IndexWriteQueue.Message> {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexWriteQueue.class);

	@Override
	protected void execute(Message message) throws Exception {
		if (UpdateMessage.class.isAssignableFrom(message.getClass())) {
			update((UpdateMessage) message);
		} else if (CleanMessage.class.isAssignableFrom(message.getClass())) {
			clean((CleanMessage) message);
		} else if (CheckMessage.class.isAssignableFrom(message.getClass())) {
			check((CheckMessage) message);
		} else if (DeleteMessage.class.isAssignableFrom(message.getClass())) {
			delete((DeleteMessage) message);
		} else if (MergeMessage.class.isAssignableFrom(message.getClass())) {
			merge((MergeMessage) message);
		}
	}

	private void update(UpdateMessage updateMessage) {
		Optional<Directory> optional = Indexs.directory(updateMessage.getCategory(), updateMessage.getKey(), false);
		if (optional.isPresent()) {
			update(optional.get(), updateMessage.getWrapList(), updateMessage.getConvertData());
		}
	}

	private void update(Directory directory, List<Doc> list, boolean convertData) {
		Analyzer analyzer = new HanLPAnalyzer();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		try (IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig)) {
			LOGGER.debug("update index, directory:{}, size:{}.", directory.toString(), list.size());
			indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			list.stream().map(o -> o.toDocument(convertData)).forEach(o -> {
				try {
					indexWriter.updateDocument(new Term(Indexs.FIELD_ID, o.get(Indexs.FIELD_ID)), o);
				} catch (IOException e) {
					LOGGER.error(e);
				}
			});
			indexWriter.commit();
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	private void clean(CleanMessage cleanMessage) {
		Optional<Directory> optional = Indexs.directory(cleanMessage.getDir(), true);
		if (optional.isPresent()) {
			clean(optional.get(), cleanMessage.getThreshold());
		}
	}

	private void clean(Directory directory, Date threshold) {
		try {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
			indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			try (IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig)) {
				Query rangeQuery = LongPoint.newRangeQuery(Indexs.FIELD_INDEXTIME, Long.MIN_VALUE, threshold.getTime());
				indexWriter.deleteDocuments(rangeQuery);
				indexWriter.commit();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void check(CheckMessage checkMessage) {
		if (StringUtils.equalsIgnoreCase(Indexs.CATEGORY_PROCESSPLATFORM, checkMessage.getCategory())) {
			checkIfDeleteProcessPlatformDirectory(checkMessage.getKey());
		} else if (StringUtils.equalsIgnoreCase(Indexs.CATEGORY_CMS, checkMessage.getCategory())) {
			checkIfDeleteCmsDirectory(checkMessage.getKey());
		}
	}

	private void checkIfDeleteProcessPlatformDirectory(String key) {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Application application = emc.find(key, Application.class);
			if (null == application) {
				Indexs.deleteDirectory(Indexs.CATEGORY_PROCESSPLATFORM, key);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void checkIfDeleteCmsDirectory(String key) {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find(key, AppInfo.class);
			if (null == appInfo) {
				Indexs.deleteDirectory(Indexs.CATEGORY_CMS, key);
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private void delete(DeleteMessage deleteMessage) {
		Optional<Directory> optional = Indexs.directory(deleteMessage.getCategory(), deleteMessage.getKey(), true);
		if (optional.isPresent()) {
			if (null == deleteMessage.getQuery()) {
				delete(optional.get(), deleteMessage.getIdList());
			} else {
				delete(optional.get(), deleteMessage.getIdList(), deleteMessage.getQuery());
			}
		}
	}

	private void delete(Directory directory, List<String> ids, Query query) {
		Analyzer analyzer = new HanLPAnalyzer();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		try (IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig)) {
			indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			ids.stream().forEach(o -> {
				try {
					BooleanQuery.Builder builder = new BooleanQuery.Builder();
					Query idQuery = new TermQuery(new Term(Indexs.FIELD_ID, o));
					builder.add(query, BooleanClause.Occur.MUST);
					builder.add(idQuery, BooleanClause.Occur.MUST);
					BooleanQuery q = builder.build();
					indexWriter.deleteDocuments(q);
				} catch (IOException e) {
					LOGGER.error(e);
				}
			});
			indexWriter.commit();
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	private void delete(Directory directory, List<String> ids) {
		Analyzer analyzer = new HanLPAnalyzer();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		try (IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig)) {
			indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
			ids.stream().forEach(o -> {
				try {
					indexWriter.deleteDocuments(new Term(Indexs.FIELD_ID, o));
				} catch (IOException e) {
					LOGGER.error(e);
				}
			});
			indexWriter.commit();
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	private void merge(MergeMessage mergeMessage) throws Exception {
		Optional<Directory> optional = Indexs.directory(mergeMessage.getDir(), true);
		if (optional.isPresent()) {
			merge(optional.get(), mergeMessage.getMaxSegments());
		}
	}

	private void merge(Directory directory, Integer maxSegments) {
		Analyzer analyzer = new HanLPAnalyzer();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
		try (IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig)) {
			indexWriter.forceMerge(maxSegments);
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	public static interface Message {

	}

	public static class UpdateMessage implements Message {

		public UpdateMessage(List<Doc> wrapList, String category, String key, Boolean convertData) {
			this.wrapList = wrapList;
			this.category = category;
			this.key = key;
			this.convertData = convertData;
		}

		private String category;
		private String key;
		private List<Doc> wrapList;
		private Boolean convertData;

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public List<Doc> getWrapList() {
			return wrapList;
		}

		public void setWrapList(List<Doc> wrapList) {
			this.wrapList = wrapList;
		}

		public Boolean getConvertData() {
			return convertData;
		}

		public void setConvertData(Boolean convertData) {
			this.convertData = convertData;
		}
	}

	public static class DeleteMessage implements Message {

		public DeleteMessage(List<String> idList, String category, String key, Query query) {
			this.idList = idList;
			this.category = category;
			this.key = key;
			this.query = query;
		}

		private List<String> idList;
		private String category;
		private String key;
		private Query query;

		public Query getQuery() {
			return query;
		}

		public void setQuery(Query query) {
			this.query = query;
		}

		public List<String> getIdList() {
			return idList;
		}

		public void setIdList(List<String> idList) {
			this.idList = idList;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

	}

	public static class CleanMessage implements Message {

		private String dir;
		private Date threshold;

		public CleanMessage(String dir, Date threshold) {
			this.dir = dir;
			this.threshold = threshold;
		}

		public Date getThreshold() {
			return threshold;
		}

		public void setThreshold(Date threshold) {
			this.threshold = threshold;
		}

		public String getDir() {
			return dir;
		}

		public void setDir(String dir) {
			this.dir = dir;
		}
	}

	public static class CheckMessage implements Message {

		private String category;
		private String key;

		public CheckMessage(String category, String key) {
			this.category = category;
			this.key = key;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}
	}

	public static class MergeMessage implements Message {

		public MergeMessage(String dir, Integer maxSegments) {
			this.dir = dir;
			this.maxSegments = maxSegments;
		}

		private String dir;

		private Integer maxSegments;

		public String getDir() {
			return dir;
		}

		public void setDir(String dir) {
			this.dir = dir;
		}

		public Integer getMaxSegments() {
			return maxSegments;
		}

		public void setMaxSegments(Integer maxSegments) {
			this.maxSegments = maxSegments;
		}

	}

}