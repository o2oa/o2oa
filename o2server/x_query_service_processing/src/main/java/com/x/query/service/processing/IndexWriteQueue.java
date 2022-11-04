package com.x.query.service.processing;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;

import com.hankcs.lucene.HanLPAnalyzer;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.cms.core.entity.AppInfo;
import com.x.processplatform.core.entity.element.Application;
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
        }
    }

    private void update(UpdateMessage updateMessage) {
        updateMessage.getWrapList().stream().collect(Collectors.groupingBy(Doc::getCategory)).entrySet().stream()
                .forEach(o -> {
                    String category = o.getKey();
                    o.getValue().stream().collect(Collectors.groupingBy(Doc::getType)).entrySet().stream()
                            .forEach(p -> {
                                String type = p.getKey();
                                p.getValue().stream().collect(Collectors.groupingBy(Doc::getKey)).entrySet().stream()
                                        .forEach(q -> {
                                            String key = q.getKey();
                                            Optional<Directory> optional = Business.Index.directory(category, type,
                                                    key, false);
                                            if (optional.isPresent()) {
                                                update(optional.get(), q.getValue(), true);
                                            }
                                        });
                            });
                });
        if (BooleanUtils.isTrue(updateMessage.getUpdateSearch())) {
            Optional<Directory> optional = Business.Index.searchDirectory(false);
            if (optional.isPresent()) {
                update(optional.get(), updateMessage.getWrapList(), false);
            }
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
                    indexWriter.updateDocument(new Term(Business.Index.FIELD_ID, o.get(Business.Index.FIELD_ID)), o);
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
        Optional<Directory> optional = Business.Index.directory(cleanMessage.category, cleanMessage.type,
                cleanMessage.key, true);
        if (optional.isPresent()) {
            clean(optional.get(), cleanMessage.getThreshold());
        }
        if (BooleanUtils.isTrue(cleanMessage.getCleanSearch())) {
            optional = Business.Index.searchDirectory(true);
            if (optional.isPresent()) {
                clean(optional.get(), cleanMessage.getThreshold());
            }
        }
    }

    private void clean(Directory directory, Date threshold) {
        try {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
            indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
            try (IndexWriter writer = new IndexWriter(directory, indexWriterConfig)) {
                Query rangeQuery = LongPoint.newRangeQuery(Business.Index.FIELD_INDEXTIME, Long.MIN_VALUE,
                        threshold.getTime());
                writer.deleteDocuments(rangeQuery);
                writer.commit();
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private void check(CheckMessage checkMessage) {
        if (StringUtils.equalsIgnoreCase(Business.Index.CATEGORY_PROCESSPLATFORM,
                checkMessage.getCategory())
                && StringUtils.equalsIgnoreCase(Business.Index.TYPE_WORKCOMPLETED,
                        checkMessage.getType())) {
            checkIfDeleteProcessPlatformDirectory(checkMessage.getKey());
        } else if (StringUtils.equalsIgnoreCase(Business.Index.CATEGORY_CMS,
                checkMessage.getCategory())
                && StringUtils.equalsIgnoreCase(Business.Index.TYPE_DOCUMENT,
                        checkMessage.getType())) {
            checkIfDeleteCmsDirectory(checkMessage.getKey());
        }
    }

    private void checkIfDeleteProcessPlatformDirectory(String key) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Application application = emc.find(key, Application.class);
            if (null == application) {
                Business.Index.deleteDirectory(Business.Index.CATEGORY_PROCESSPLATFORM,
                        Business.Index.TYPE_WORKCOMPLETED,
                        key);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private void checkIfDeleteCmsDirectory(String key) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AppInfo appInfo = emc.find(key, AppInfo.class);
            if (null == appInfo) {
                Business.Index.deleteDirectory(Business.Index.CATEGORY_CMS,
                        Business.Index.TYPE_DOCUMENT, key);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static interface Message {

    }

    public static class UpdateMessage implements Message {

        public UpdateMessage(List<Doc> wrapList, Boolean updateSearch) {
            this.wrapList = wrapList;
            this.updateSearch = updateSearch;
        }

        private List<Doc> wrapList;
        private Boolean updateSearch;

        public List<Doc> getWrapList() {
            return wrapList;
        }

        public void setWrapList(List<Doc> wrapList) {
            this.wrapList = wrapList;
        }

        public Boolean getUpdateSearch() {
            return updateSearch;
        }

        public void setUpdateSearch(Boolean updateSearch) {
            this.updateSearch = updateSearch;
        }

    }

    public static class CleanMessage implements Message {

        private String category;
        private String type;
        private String key;
        private Boolean cleanSearch;
        private Date threshold;

        public CleanMessage(String category, String type, String key, boolean cleanSearch, Date threshold) {
            this.category = category;
            this.type = type;
            this.key = key;
            this.cleanSearch = cleanSearch;
            this.threshold = threshold;
        }

        public Date getThreshold() {
            return threshold;
        }

        public void setThreshold(Date threshold) {
            this.threshold = threshold;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

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

        public Boolean getCleanSearch() {
            return cleanSearch;
        }

        public void setCleanSearch(Boolean cleanSearch) {
            this.cleanSearch = cleanSearch;
        }

    }

    public static class CheckMessage implements Message {

        private String category;
        private String type;
        private String key;

        public CheckMessage(String category, String type, String key) {
            this.category = category;
            this.type = type;
            this.key = key;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

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
    }

}