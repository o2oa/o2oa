package com.x.base.core.project.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.utils.SystemUtils;
import org.quartz.CronExpression;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.NumberTools;

@Deprecated(forRemoval = true)
public class Query extends ConfigObject {

    public static Query defaultInstance() {
        return new Query();
    }

    public Query() {
        this.crawlWorkCompleted = new CrawlWorkCompleted();
        this.crawlWork = new CrawlWork();
        this.crawlCms = new CrawlCms();
        this.extractOffice = DEFAULT_EXTRACTOFFICE;
        this.extractPdf = DEFAULT_EXTRACTPDF;
        this.extractText = DEFAULT_EXTRACTTEXT;
        this.extractImage = DEFAULT_EXTRACTIMAGE;
        this.tessLanguage = DEFAULT_TESSLANGUAGE;
    }

    @FieldDescribe("已完成工作收集器设置.")
    private CrawlWorkCompleted crawlWorkCompleted;

    @FieldDescribe("工作收集器设置.")
    private CrawlWork crawlWork;

    @FieldDescribe("内容管理收集器设置.")
    private CrawlCms crawlCms;

    @FieldDescribe("抽取office中的文本.")
    private Boolean extractOffice = true;

    @FieldDescribe("抽取pdf中的文本.")
    private Boolean extractPdf = true;

    @FieldDescribe("抽取文本中的文本.")
    private Boolean extractText = true;

    @FieldDescribe("抽取图像中的文本.")
    private Boolean extractImage = false;

    @FieldDescribe("tess使用语言.")
    private String tessLanguage = "chi_sim";

    @FieldDescribe("查询批次大小.")
    private Integer planQueryBatchSize = DEFAULT_PLANQUERYBATCHSIZE;

    public static final Boolean DEFAULT_EXTRACTOFFICE = true;
    public static final Boolean DEFAULT_EXTRACTPDF = true;
    public static final Boolean DEFAULT_EXTRACTTEXT = true;
    public static final Boolean DEFAULT_EXTRACTIMAGE = false;
    public static final String DEFAULT_TESSLANGUAGE = "chi_sim";
    public static final Integer DEFAULT_PLANQUERYBATCHSIZE = 500;

    public Integer getPlanQueryBatchSize() {
        return (this.planQueryBatchSize == null || planQueryBatchSize < 1) ? DEFAULT_PLANQUERYBATCHSIZE
                : this.planQueryBatchSize;
    }

    public Boolean getExtractOffice() {
        return BooleanUtils.isTrue(extractOffice);
    }

    public Boolean getExtractPdf() {
        return BooleanUtils.isTrue(extractPdf);
    }

    public Boolean getExtractText() {
        return BooleanUtils.isTrue(extractText);
    }

    public Boolean getExtractImage() {
        return SystemUtils.IS_OS_WINDOWS && BooleanUtils.isTrue(extractImage);
    }

    public String getTessLanguage() {
        return StringUtils.isNotEmpty(this.tessLanguage) ? this.tessLanguage : DEFAULT_TESSLANGUAGE;
    }

    public CrawlCms getCrawlCms() {
        return this.crawlCms == null ? new CrawlCms() : this.crawlCms;
    }

    public CrawlWork getCrawlWork() {
        return this.crawlWork == null ? new CrawlWork() : this.crawlWork;
    }

    public CrawlWorkCompleted getCrawlWorkCompleted() {
        return this.crawlWorkCompleted == null ? new CrawlWorkCompleted() : this.crawlWorkCompleted;
    }

    public void save() throws Exception {
        File file = new File(Config.base(), Config.PATH_CONFIG_QUERY);
        FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
    }

    public static class CrawlCms extends ConfigObject {

        public static CrawlCms defaultInstance() {
            CrawlCms o = new CrawlCms();
            return o;
        }

        public final static Boolean DEFAULT_ENABLE = false;

        public final static String DEFAULT_CRON = "30 30 9,12,15,18 * * ?";

        public final static Integer DEFAULT_CONUT = 30;

        public final static Integer DEFAULT_MAXATTACHMENTSIZE = 1024 * 1024 * 5;

        @FieldDescribe("是否启用")
        private Boolean enable = DEFAULT_ENABLE;

        @FieldDescribe("定时cron表达式.")
        private String cron = DEFAULT_CRON;

        @FieldDescribe("每次处理的数量,默认每小时处理所以默认为30,同时每次将重爬最旧的50%,按时间轮询50%.")
        private Integer count = DEFAULT_CONUT;

        @FieldDescribe("忽略附件名称.")
        private List<String> excludeAttachment = new ArrayList<>();

        @FieldDescribe("最大附件大小.")
        private Integer maxAttachmentSize = DEFAULT_MAXATTACHMENTSIZE;

        public String getCron() {
            if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
                return this.cron;
            } else {
                return DEFAULT_CRON;
            }
        }

        public List<String> getExcludeAttachment() {
            return excludeAttachment;
        }

        public Integer getMaxAttachmentSize() {
            return this.maxAttachmentSize == null ? DEFAULT_MAXATTACHMENTSIZE : this.maxAttachmentSize;
        }

        public Boolean getEnable() {
            return BooleanUtils.isTrue(this.enable);
        }

        public Integer getCount() {
            return ((null == this.count) || (count < 0)) ? DEFAULT_CONUT : this.count;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public void setExcludeAttachment(List<String> excludeAttachment) {
            this.excludeAttachment = excludeAttachment;
        }

        public void setMaxAttachmentSize(Integer maxAttachmentSize) {
            this.maxAttachmentSize = maxAttachmentSize;
        }
    }

    public static class CrawlWork extends ConfigObject {

        public static CrawlWork defaultInstance() {
            CrawlWork o = new CrawlWork();
            return o;
        }

        public final static Boolean DEFAULT_ENABLE = false;

        public final static String DEFAULT_CRON = "40 40 10,12,14,16 * * ?";

        /* 由于每小时运行,那么每次更新100份 */
        public final static Integer DEFAULT_CONUT = 50;

        public final static Integer DEFAULT_MAXATTACHMENTSIZE = 1024 * 1024 * 5;

        @FieldDescribe("是否启用")
        private Boolean enable = DEFAULT_ENABLE;

        @FieldDescribe("定时cron表达式.")
        private String cron = DEFAULT_CRON;

        @FieldDescribe("每次处理的数量,默认每小时处理所以默认为50,同时每次将重爬最旧的50%,按时间轮询50%.")
        private Integer count = DEFAULT_CONUT;

        @FieldDescribe("忽略附件名称.")
        private List<String> excludeAttachment = new ArrayList<>();

        @FieldDescribe("忽略附件位置.")
        private List<String> excludeSite = new ArrayList<>();

        @FieldDescribe("最大附件大小.")
        private Integer maxAttachmentSize = DEFAULT_MAXATTACHMENTSIZE;

        public String getCron() {
            if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
                return this.cron;
            } else {
                return DEFAULT_CRON;
            }
        }

        public List<String> getExcludeAttachment() {
            return excludeAttachment;
        }

        public List<String> getExcludeSite() {
            return excludeSite;
        }

        public Integer getMaxAttachmentSize() {
            return this.maxAttachmentSize == null ? DEFAULT_MAXATTACHMENTSIZE : this.maxAttachmentSize;
        }

        public Boolean getEnable() {
            return BooleanUtils.isTrue(this.enable);
        }

        public Integer getCount() {
            return ((null == this.count) || (count < 0)) ? DEFAULT_CONUT : this.count;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public void setExcludeAttachment(List<String> excludeAttachment) {
            this.excludeAttachment = excludeAttachment;
        }

        public void setExcludeSite(List<String> excludeSite) {
            this.excludeSite = excludeSite;
        }

        public void setMaxAttachmentSize(Integer maxAttachmentSize) {
            this.maxAttachmentSize = maxAttachmentSize;
        }

    }

    public static class CrawlWorkCompleted extends ConfigObject {

        public static CrawlWorkCompleted defaultInstance() {
            CrawlWorkCompleted o = new CrawlWorkCompleted();
            return o;
        }

        public final static Boolean DEFAULT_ENABLE = false;

        public final static String DEFAULT_CRON = "50 50 21 * * ?";

        public final static Integer DEFAULT_CONUT = 500;

        public final static Integer DEFAULT_MAXATTACHMENTSIZE = 1024 * 1024 * 5;

        @FieldDescribe("是否启用")
        private Boolean enable = DEFAULT_ENABLE;

        @FieldDescribe("定时cron表达式.")
        private String cron = DEFAULT_CRON;

        @FieldDescribe("每次处理的数量,默认每小时处理所以默认为500,同时每次将重爬最旧的25%,按时间轮询25%.")
        private Integer count = DEFAULT_CONUT;

        @FieldDescribe("忽略附件名称.")
        private List<String> excludeAttachment = new ArrayList<>();

        @FieldDescribe("忽略附件位置.")
        private List<String> excludeSite = new ArrayList<>();

        @FieldDescribe("最大附件大小.")
        private Integer maxAttachmentSize = DEFAULT_MAXATTACHMENTSIZE;

        public String getCron() {
            if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
                return this.cron;
            } else {
                return DEFAULT_CRON;
            }
        }

        public List<String> getExcludeAttachment() {
            return excludeAttachment;
        }

        public Integer getMaxAttachmentSize() {
            return this.maxAttachmentSize == null ? DEFAULT_MAXATTACHMENTSIZE : this.maxAttachmentSize;
        }

        public List<String> getExcludeSite() {
            return excludeSite;
        }

        public Boolean getEnable() {
            return BooleanUtils.isTrue(this.enable);
        }

        public Integer getCount() {
            return ((null == this.count) || (count < 0)) ? DEFAULT_CONUT : this.count;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public void setEnable(Boolean enable) {
            this.enable = enable;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public void setExcludeAttachment(List<String> excludeAttachment) {
            this.excludeAttachment = excludeAttachment;
        }

        public void setExcludeSite(List<String> excludeSite) {
            this.excludeSite = excludeSite;
        }

        public void setMaxAttachmentSize(Integer maxAttachmentSize) {
            this.maxAttachmentSize = maxAttachmentSize;
        }

    }

    public void setCrawlWorkCompleted(CrawlWorkCompleted crawlWorkCompleted) {
        this.crawlWorkCompleted = crawlWorkCompleted;
    }

    public void setCrawlWork(CrawlWork crawlWork) {
        this.crawlWork = crawlWork;
    }

    public void setCrawlCms(CrawlCms crawlCms) {
        this.crawlCms = crawlCms;
    }

    public void setExtractOffice(Boolean extractOffice) {
        this.extractOffice = extractOffice;
    }

    public void setExtractPdf(Boolean extractPdf) {
        this.extractPdf = extractPdf;
    }

    public void setExtractText(Boolean extractText) {
        this.extractText = extractText;
    }

    public void setExtractImage(Boolean extractImage) {
        this.extractImage = extractImage;
    }

    public void setTessLanguage(String tessLanguage) {
        this.tessLanguage = tessLanguage;
    }

    private Index index;

    public Index index() {
        return this.index == null ? new Index() : this.index;
    }

    public static class Index {

        public static final String FIELD_HIGHLIGHTING = "highlighting";
        public static final String READERS_SYMBOL_ALL = "ALL";

        public static final String DIRECTORY_SEARCH = "search";

        public static final String MODE_LOCALDIRECTORY = "localDirectory";
        public static final String MODE_HDFSDIRECTORY = "hdfsDirectory";
        public static final String MODE_SHAREDDIRECTORY = "sharedDirectory";

        public static final String DEFAULT_HDFSDIRECTORYDEFAULTFS = "hdfs://127.0.0.1:9000";
        public static final String DEFAULT_DIRECTORYPATH = "/repository/index";
        public static final String DEFAULT_MODE = MODE_LOCALDIRECTORY;
        public static final Integer DEFAULT_DATASTRINGTHRESHOLD = 50;
        public static final Integer DEFAULT_SUMMARYLENGTH = 250;
        public static final Integer DEFAULT_ATTACHMENTMAXSIZE = 5;

        public static final Boolean DEFAULT_LOWFREQUENCYWORKCOMPLETEDENABLE = true;
        public static final String DEFAULT_LOWFREQUENCYWORKCOMPLETEDCRON = "40 15 21 * * ?";
        public static final Integer DEFAULT_LOWFREQUENCYWORKCOMPLETEDBATCHSIZE = 200;
        public static final Integer DEFAULT_LOWFREQUENCYWORKCOMPLETEDMAXCOUNT = 50000;
        public static final Integer DEFAULT_LOWFREQUENCYWORKCOMPLETEDMAXMINUTES = 120;
        public static final Integer DEFAULT_WORKCOMPLETEDCLEANUPTHRESHOLDDAYS = 180;

        public static final Boolean DEFAULT_LOWFREQUENCYDOCUMENTENABLE = true;
        public static final String DEFAULT_LOWFREQUENCYDOCUMENTCRON = "40 15 20 * * ?";
        public static final Integer DEFAULT_LOWFREQUENCYDOCUMENTBATCHSIZE = 50;
        public static final Integer DEFAULT_LOWFREQUENCYDOCUMENTMAXCOUNT = 10000;
        public static final Integer DEFAULT_LOWFREQUENCYDOCUMENTMAXMINUTES = 60;
        public static final Integer DEFAULT_DOCUMENTCLEANUPTHRESHOLDDAYS = 180;

        public static final Boolean DEFAULT_HIGHFREQUENCYWORKCOMPLETEDENABLE = true;
        public static final String DEFAULT_HIGHFREQUENCYWORKCOMPLETEDCRON = "40 0/10 7-19 * * ?";
        public static final Integer DEFAULT_HIGHFREQUENCYWORKCOMPLETEDBATCHSIZE = 50;
        public static final Integer DEFAULT_HIGHFREQUENCYWORKCOMPLETEDMAXCOUNT = 200;
        public static final Integer DEFAULT_HIGHFREQUENCYWORKCOMPLETEDMAXMINUTES = 5;

        public static final Boolean DEFAULT_HIGHFREQUENCYDOCUMENTENABLE = true;
        public static final String DEFAULT_HIGHFREQUENCYDOCUMENTCRON = "55 0/15 7-19 * * ?";
        public static final Integer DEFAULT_HIGHFREQUENCYDOCUMENTBATCHSIZE = 50;
        public static final Integer DEFAULT_HIGHFREQUENCYDOCUMENTMAXCOUNT = 200;
        public static final Integer DEFAULT_HIGHFREQUENCYDOCUMENTMAXMINUTES = 5;

        public static final Boolean DEFAULT_SEARCHENABLE = true;

        public static final Float DEFAULT_SEARCHTITLEBOOST = 4.0f;
        public static final Float DEFAULT_SEARCHSUMMARYBOOST = 3.0f;
        public static final Float DEFAULT_SEARCHBODYBOOST = 2.0f;
        public static final Float DEFAULT_SEARCHATTACHMENTBOOST = 1.0f;

        public static final Integer DEFAULT_SEARCHSIZE = 20;
        public static final Integer DEFAULT_SEARCHMAXSIZE = 1000;

        public static final Integer DEFAULT_SEARCHMAXHITS = 1000000;

        public static final Integer DEFAULT_FACETMAXGROUPS = 100;

        public static final String FACETGROUPORDER_KEYASC = "keyAsc";
        public static final String FACETGROUPORDER_KEYDESC = "keyDesc";
        public static final String FACETGROUPORDER_COUNTASC = "countAsc";
        public static final String FACETGROUPORDER_COUNTDESC = "countDesc";

        public static final String DEFAULT_FACETGROUPORDER = FACETGROUPORDER_KEYASC;

        public static final String DEFAULT_HIGHLIGHTPRE = "<em>";
        public static final String DEFAULT_HIGHLIGHTPOST = "</em>";
        public static final Integer DEFAULT_HIGHLIGHTFRAGMENTSIZE = 64;
        public static final Integer DEFAULT_HIGHLIGHTFRAGMENTCOUNT = 3;

        public static final Integer DEFAULT_MLTSIZE = 10;

        private String mode;
        private String hdfsDirectoryDefaultFS;
        private String directoryPath;
        private Integer dataStringThreshold;
        private Integer summaryLength;
        private Integer attachmentMaxSize;

        private Boolean lowFrequencyWorkCompletedEnable;
        private String lowFrequencyWorkCompletedCron;

        private Integer lowFrequencyWorkCompletedBatchSize;
        private Integer lowFrequencyWorkCompletedMaxCount;
        private Integer lowFrequencyWorkCompletedMaxMinutes;

        private Boolean lowFrequencyDocumentEnable;
        private String lowFrequencyDocumentCron;
        private Integer lowFrequencyDocumentBatchSize;
        private Integer lowFrequencyDocumentMaxCount;
        private Integer lowFrequencyDocumentMaxMinutes;

        private Boolean highFrequencyWorkCompletedEnable;
        private String highFrequencyWorkCompletedCron;
        private Integer highFrequencyWorkCompletedBatchSize;
        private Integer highFrequencyWorkCompletedMaxCount;
        private Integer highFrequencyWorkCompletedMaxMinutes;

        private Boolean highFrequencyDocumentEnable;
        private String highFrequencyDocumentCron;
        private Integer highFrequencyDocumentBatchSize;
        private Integer highFrequencyDocumentMaxCount;
        private Integer highFrequencyDocumentMaxMinutes;

        private Integer workCompletedCleanupThresholdDays;
        private Integer documentCleanupThresholdDays;

        private Boolean searchEnable;

        private Float searchTitleBoost;
        private Float searchSummaryBoost;
        private Float searchBodyBoost;
        private Float searchAttachmentBoost;

        private Integer searchSize;
        private Integer searchMaxSize;
        private Integer searchMaxHits;

        private Integer facetMaxGroups;
        private String facetGroupOrder;

        @FieldDescribe("高亮返回片段长度.")
        private Integer highlightFragmentSize;
        @FieldDescribe("高亮返回片段数量.")
        private Integer highlightFragmentCount;
        @FieldDescribe("高亮前缀.")
        private String highlightPre;
        @FieldDescribe("高亮后缀.")
        private String highlightPost;

        private Integer mltSize;

        public Integer getMltSize() {
            return NumberTools.nullOrLessThan(this.mltSize, 1) ? DEFAULT_MLTSIZE
                    : this.mltSize;
        }

        public Integer getHighlightFragmentSize() {
            return NumberTools.nullOrLessThan(this.highlightFragmentSize, 1) ? DEFAULT_HIGHLIGHTFRAGMENTSIZE
                    : this.highlightFragmentSize;
        }

        public Integer getHighlightFragmentCount() {
            return NumberTools.nullOrLessThan(this.highlightFragmentCount, 1) ? DEFAULT_HIGHLIGHTFRAGMENTCOUNT
                    : this.highlightFragmentCount;
        }

        public String getHighlightPre() {
            return StringUtils.isEmpty(this.highlightPre) ? DEFAULT_HIGHLIGHTPRE : this.highlightPre;
        }

        public String getHighlightPost() {
            return StringUtils.isEmpty(this.highlightPost) ? DEFAULT_HIGHLIGHTPOST : this.highlightPost;
        }

        public String getFacetGroupOrder() {
            return StringUtils.isBlank(facetGroupOrder) ? DEFAULT_FACETGROUPORDER
                    : facetGroupOrder;
        }

        public Integer getFacetMaxGroups() {
            return NumberTools.nullOrLessThan(this.facetMaxGroups, 1) ? DEFAULT_FACETMAXGROUPS
                    : this.facetMaxGroups;
        }

        public Integer getSearchMaxHits() {
            return NumberTools.nullOrLessThan(searchMaxHits, -1) ? DEFAULT_SEARCHMAXHITS : this.searchMaxHits;
        }

        public Integer getSearchSize() {
            return NumberTools.nullOrLessThan(searchSize, 1) ? DEFAULT_SEARCHSIZE : searchSize;
        }

        public Integer getSearchMaxSize() {
            return NumberTools.nullOrLessThan(searchMaxSize, 1) ? DEFAULT_SEARCHMAXSIZE : searchMaxSize;
        }

        public Float getSearchTitleBoost() {
            return NumberTools.nullOrLessThan(searchTitleBoost, 0) ? DEFAULT_SEARCHTITLEBOOST : this.searchTitleBoost;
        }

        public Float getSearchSummaryBoost() {
            return NumberTools.nullOrLessThan(searchSummaryBoost, 0) ? DEFAULT_SEARCHSUMMARYBOOST
                    : this.searchSummaryBoost;
        }

        public Float getSearchBodyBoost() {
            return NumberTools.nullOrLessThan(searchBodyBoost, 0) ? DEFAULT_SEARCHBODYBOOST : this.searchBodyBoost;
        }

        public Float getSearchAttachmentBoost() {
            return NumberTools.nullOrLessThan(searchAttachmentBoost, 0) ? DEFAULT_SEARCHATTACHMENTBOOST
                    : this.searchAttachmentBoost;
        }

        public Boolean getSearchEnable() {
            return null == searchEnable ? DEFAULT_SEARCHENABLE : this.searchEnable;
        }

        public String getLowFrequencyWorkCompletedCron() {
            if (StringUtils.isNotEmpty(this.lowFrequencyWorkCompletedCron)
                    && CronExpression.isValidExpression(this.lowFrequencyWorkCompletedCron)) {
                return this.lowFrequencyWorkCompletedCron;
            } else {
                return DEFAULT_LOWFREQUENCYWORKCOMPLETEDCRON;
            }
        }

        public String getLowFrequencyDocumentCron() {
            if (StringUtils.isNotEmpty(this.lowFrequencyDocumentCron)
                    && CronExpression.isValidExpression(this.lowFrequencyDocumentCron)) {
                return this.lowFrequencyDocumentCron;
            } else {
                return DEFAULT_LOWFREQUENCYDOCUMENTCRON;
            }
        }

        public String getHighFrequencyWorkCompletedCron() {
            if (StringUtils.isNotEmpty(this.highFrequencyWorkCompletedCron)
                    && CronExpression.isValidExpression(this.highFrequencyWorkCompletedCron)) {
                return this.highFrequencyWorkCompletedCron;
            } else {
                return DEFAULT_HIGHFREQUENCYWORKCOMPLETEDCRON;
            }
        }

        public String getHighFrequencyDocumentCron() {
            if (StringUtils.isNotEmpty(this.highFrequencyDocumentCron)
                    && CronExpression.isValidExpression(this.highFrequencyDocumentCron)) {
                return this.highFrequencyDocumentCron;
            } else {
                return DEFAULT_HIGHFREQUENCYDOCUMENTCRON;
            }
        }

        public String getDirectoryPath() {
            return StringUtils.isBlank(directoryPath)
                    ? DEFAULT_DIRECTORYPATH
                    : adjustDirectoryPath();
        }

        private String adjustDirectoryPath() {
            return StringUtils.startsWith(this.directoryPath, "/") ? this.directoryPath
                    : "/" + directoryPath;
        }

        public Integer getWorkCompletedCleanupThresholdDays() {
            return NumberTools.nullOrLessThan(workCompletedCleanupThresholdDays, 1)
                    ? DEFAULT_WORKCOMPLETEDCLEANUPTHRESHOLDDAYS
                    : this.workCompletedCleanupThresholdDays;
        }

        public Integer getLowFrequencyWorkCompletedMaxMinutes() {
            return NumberTools.nullOrLessThan(lowFrequencyWorkCompletedMaxMinutes, 1)
                    ? DEFAULT_LOWFREQUENCYWORKCOMPLETEDMAXMINUTES
                    : this.lowFrequencyWorkCompletedMaxMinutes;
        }

        public Integer getLowFrequencyWorkCompletedMaxCount() {
            return NumberTools.nullOrLessThan(lowFrequencyWorkCompletedMaxCount, 1)
                    ? DEFAULT_LOWFREQUENCYWORKCOMPLETEDMAXCOUNT
                    : this.lowFrequencyWorkCompletedMaxCount;
        }

        public Integer getLowFrequencyWorkCompletedBatchSize() {
            return NumberTools.nullOrLessThan(lowFrequencyWorkCompletedBatchSize, 1)
                    ? DEFAULT_LOWFREQUENCYWORKCOMPLETEDBATCHSIZE
                    : this.lowFrequencyWorkCompletedBatchSize;
        }

        public Boolean getLowFrequencyWorkCompletedEnable() {
            return null == lowFrequencyWorkCompletedEnable ? DEFAULT_LOWFREQUENCYWORKCOMPLETEDENABLE
                    : this.lowFrequencyWorkCompletedEnable;
        }

        public Integer getDocumentCleanupThresholdDays() {
            return NumberTools.nullOrLessThan(documentCleanupThresholdDays, 1)
                    ? DEFAULT_DOCUMENTCLEANUPTHRESHOLDDAYS
                    : this.documentCleanupThresholdDays;
        }

        public Integer getLowFrequencyDocumentMaxMinutes() {
            return NumberTools.nullOrLessThan(this.lowFrequencyDocumentMaxMinutes, 1)
                    ? DEFAULT_LOWFREQUENCYDOCUMENTMAXMINUTES
                    : this.lowFrequencyDocumentMaxMinutes;
        }

        public Integer getLowFrequencyDocumentMaxCount() {
            return NumberTools.nullOrLessThan(lowFrequencyDocumentMaxCount, 1)
                    ? DEFAULT_LOWFREQUENCYDOCUMENTMAXCOUNT
                    : this.lowFrequencyDocumentMaxCount;
        }

        public Integer getLowFrequencyDocumentBatchSize() {
            return NumberTools.nullOrLessThan(lowFrequencyDocumentBatchSize, 1)
                    ? DEFAULT_LOWFREQUENCYDOCUMENTBATCHSIZE
                    : this.lowFrequencyDocumentBatchSize;
        }

        public Boolean getLowFrequencyDocumentEnable() {
            return null == lowFrequencyDocumentEnable ? DEFAULT_LOWFREQUENCYDOCUMENTENABLE
                    : this.lowFrequencyDocumentEnable;
        }

        public Integer getHighFrequencyDocumentMaxMinutes() {
            return NumberTools.nullOrLessThan(this.highFrequencyDocumentMaxMinutes, 1)
                    ? DEFAULT_HIGHFREQUENCYDOCUMENTMAXMINUTES
                    : this.highFrequencyDocumentMaxMinutes;
        }

        public Integer getHighFrequencyDocumentMaxCount() {
            return NumberTools.nullOrLessThan(highFrequencyDocumentMaxCount, 1)
                    ? DEFAULT_HIGHFREQUENCYDOCUMENTMAXCOUNT
                    : this.highFrequencyDocumentMaxCount;
        }

        public Integer getHighFrequencyDocumentBatchSize() {
            return NumberTools.nullOrLessThan(highFrequencyDocumentBatchSize, 1)
                    ? DEFAULT_HIGHFREQUENCYDOCUMENTBATCHSIZE
                    : this.highFrequencyDocumentBatchSize;
        }

        public Boolean getHighFrequencyDocumentEnable() {
            return null == highFrequencyDocumentEnable ? DEFAULT_HIGHFREQUENCYDOCUMENTENABLE
                    : this.highFrequencyDocumentEnable;
        }

        public Integer getHighFrequencyWorkCompletedMaxMinutes() {
            return NumberTools.nullOrLessThan(this.highFrequencyWorkCompletedMaxMinutes, 1)
                    ? DEFAULT_HIGHFREQUENCYWORKCOMPLETEDMAXMINUTES
                    : this.highFrequencyWorkCompletedMaxMinutes;
        }

        public Integer getHighFrequencyWorkCompletedMaxCount() {
            return NumberTools.nullOrLessThan(highFrequencyWorkCompletedMaxCount, 1)
                    ? DEFAULT_HIGHFREQUENCYWORKCOMPLETEDMAXCOUNT
                    : this.highFrequencyWorkCompletedMaxCount;
        }

        public Integer getHighFrequencyWorkCompletedBatchSize() {
            return NumberTools.nullOrLessThan(highFrequencyWorkCompletedBatchSize, 1)
                    ? DEFAULT_HIGHFREQUENCYWORKCOMPLETEDBATCHSIZE
                    : this.highFrequencyWorkCompletedBatchSize;
        }

        public Boolean getHighFrequencyWorkCompletedEnable() {
            return null == highFrequencyWorkCompletedEnable ? DEFAULT_HIGHFREQUENCYWORKCOMPLETEDENABLE
                    : this.highFrequencyWorkCompletedEnable;
        }

        public Integer getAttachmentMaxSize() {
            return NumberTools.nullOrLessThan(this.attachmentMaxSize, 0) ? DEFAULT_ATTACHMENTMAXSIZE
                    : this.attachmentMaxSize;
        }

        public Integer getSummaryLength() {
            return NumberTools.nullOrLessThan(this.summaryLength, 1) ? DEFAULT_SUMMARYLENGTH : this.summaryLength;
        }

        public Integer getDataStringThreshold() {
            return NumberTools.nullOrLessThan(this.dataStringThreshold, 1) ? DEFAULT_DATASTRINGTHRESHOLD
                    : this.dataStringThreshold;
        }

        public String getMode() {
            if (StringUtils.equalsIgnoreCase(MODE_HDFSDIRECTORY, this.mode)) {
                return MODE_HDFSDIRECTORY;
            }
            if (StringUtils.equalsIgnoreCase(MODE_SHAREDDIRECTORY, this.mode)) {
                return MODE_SHAREDDIRECTORY;
            } else {
                return MODE_LOCALDIRECTORY;
            }
        }

        public String getHdfsDirectoryDefaultFS() {
            return StringUtils.isEmpty(this.hdfsDirectoryDefaultFS) ? DEFAULT_HDFSDIRECTORYDEFAULTFS
                    : this.hdfsDirectoryDefaultFS;
        }

    }
}