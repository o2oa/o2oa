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
        }
    }

    private void update(UpdateMessage updateMessage) {
        updateMessage.getWrapList().stream().collect(Collectors.groupingBy(Doc::getCategory)).entrySet().stream()
                .forEach(o -> {
                    String category = o.getKey();
                    o.getValue().stream().collect(Collectors.groupingBy(Doc::getType)).entrySet().stream()
                            .forEach(p -> {
                                p.getValue().stream().collect(Collectors.groupingBy(Doc::getKey)).entrySet().stream()
                                        .forEach(q -> {
                                            String key = q.getKey();
                                            Optional<Directory> optional = Indexs.directory(category,
                                                    key, false);
                                            if (optional.isPresent()) {
                                                update(optional.get(), q.getValue(), true);
                                            }
                                        });
                            });
                });
        if (BooleanUtils.isTrue(updateMessage.getUpdateSearch())) {
            Optional<Directory> optional = Indexs.directory(Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE, false);
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
                    indexWriter.updateDocument(new Term(Indexs.FIELD_ID, o.get(Indexs.FIELD_ID)), o);
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            });
            indexWriter.forceMerge(1);
            indexWriter.commit();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    private void clean(CleanMessage cleanMessage) {
        Optional<Directory> optional = Indexs.directory(cleanMessage.category,
                cleanMessage.key, true);
        if (optional.isPresent()) {
            clean(optional.get(), cleanMessage.getThreshold());
        }
        if (BooleanUtils.isTrue(cleanMessage.getCleanSearch())) {
            optional = Indexs.directory(Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE, true);
            if (optional.isPresent()) {
                clean(optional.get(), cleanMessage.getThreshold());
            }
        }
    }

    private void clean(Directory directory, Date threshold) {
        try {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
            indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
            try (IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig)) {
                Query rangeQuery = LongPoint.newRangeQuery(Indexs.FIELD_INDEXTIME, Long.MIN_VALUE,
                        threshold.getTime());
                indexWriter.deleteDocuments(rangeQuery);
                indexWriter.forceMerge(1);
                indexWriter.commit();
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private void check(CheckMessage checkMessage) {
        if (StringUtils.equalsIgnoreCase(Indexs.CATEGORY_PROCESSPLATFORM,
                checkMessage.getCategory())
                && StringUtils.equalsIgnoreCase(Indexs.TYPE_WORKCOMPLETED,
                        checkMessage.getType())) {
            checkIfDeleteProcessPlatformDirectory(checkMessage.getKey());
        } else if (StringUtils.equalsIgnoreCase(Indexs.CATEGORY_CMS,
                checkMessage.getCategory())
                && StringUtils.equalsIgnoreCase(Indexs.TYPE_DOCUMENT,
                        checkMessage.getType())) {
            checkIfDeleteCmsDirectory(checkMessage.getKey());
        }
    }

    private void checkIfDeleteProcessPlatformDirectory(String key) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Application application = emc.find(key, Application.class);
            if (null == application) {
                Indexs.deleteDirectory(Indexs.CATEGORY_PROCESSPLATFORM,
                        Indexs.TYPE_WORKCOMPLETED,
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
                Indexs.deleteDirectory(Indexs.CATEGORY_CMS,
                        Indexs.TYPE_DOCUMENT, key);
            }
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    private void delete(DeleteMessage deleteMessage) {
        Optional<Directory> optional = Indexs.directory(deleteMessage.getCategory(), deleteMessage.getKey(), true);
        if (optional.isPresent()) {
            delete(deleteMessage.getIdList(), optional.get());
        }
        if (BooleanUtils.isTrue(deleteMessage.getDeleteSearch())) {
            Optional<Directory> optionalSearch = Indexs.directory(Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE, true);
            delete(deleteMessage.getIdList(), optionalSearch.get());
        }
    }

    private void delete(List<String> ids, Directory directory) {
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
            indexWriter.forceMerge(1);
            indexWriter.commit();
        } catch (IOException e) {
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

    public static class DeleteMessage implements Message {

        public DeleteMessage(List<String> idList, String category, String key, Boolean deleteSearch) {
            this.idList = idList;
            this.category = category;
            this.key = key;
            this.deleteSearch = deleteSearch;
        }

        private List<String> idList;
        private String category;
        private String key;
        private Boolean deleteSearch;

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

        public Boolean getDeleteSearch() {
            return deleteSearch;
        }

        public void setDeleteSearch(Boolean deleteSearch) {
            this.deleteSearch = deleteSearch;
        }

    }

    public static class CleanMessage implements Message {

        private String category;
        private String key;
        private Boolean cleanSearch;
        private Date threshold;

        public CleanMessage(String category, String type, String key, boolean cleanSearch, Date threshold) {
            this.category = category;
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