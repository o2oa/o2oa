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

import io.swagger.v3.oas.annotations.media.Schema;

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
        this.index = new Index();

    }

    @Deprecated(forRemoval = true)
    @FieldDescribe("已完成工作收集器设置.")
    private CrawlWorkCompleted crawlWorkCompleted;

    @Deprecated(forRemoval = true)
    @FieldDescribe("工作收集器设置.")
    private CrawlWork crawlWork;

    @Deprecated(forRemoval = true)
    @FieldDescribe("内容管理收集器设置.")
    private CrawlCms crawlCms;

    @Deprecated(forRemoval = true)
    @FieldDescribe("抽取office中的文本.")
    private Boolean extractOffice = true;

    @Deprecated(forRemoval = true)
    @FieldDescribe("抽取pdf中的文本.")
    private Boolean extractPdf = true;

    @Deprecated(forRemoval = true)
    @FieldDescribe("抽取文本中的文本.")
    private Boolean extractText = true;

    @Deprecated(forRemoval = true)
    @FieldDescribe("抽取图像中的文本.")
    private Boolean extractImage = false;

    @Deprecated(forRemoval = true)
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

    @FieldDescribe("tess使用语言.")
    private String tessLanguage = "chi_sim";

    @FieldDescribe("检索配置.")
    @Schema(description = "检索配置.")
    private Index index;

    public Index index() {
        return this.index == null ? new Index() : this.index;
    }

    public static class Index extends ConfigObject {

        public static Index defaultInstance() {
            return new Index();
        }

        public static final String FIELD_HIGHLIGHTING = "highlighting";
        public static final String READERS_SYMBOL_ALL = "ALL";

        public static final String DIRECTORY_SEARCH = "search";

        public static final String MODE_LOCALDIRECTORY = "localDirectory";
        public static final String MODE_HDFSDIRECTORY = "hdfsDirectory";
        public static final String MODE_SHAREDDIRECTORY = "sharedDirectory";

        public static final String DEFAULT_HDFSDIRECTORYDEFAULTFS = "hdfs://127.0.0.1:9000";
        public static final String DEFAULT_HDFSDIRECTORYPATH = "/";
        public static final String DEFAULT_SHAREDDIRECTORYPATH = "/repository/index";
        public static final String DEFAULT_MODE = MODE_LOCALDIRECTORY;
        public static final Integer DEFAULT_MAXSEGMENTS = 1;
        public static final Integer DEFAULT_CLEANUPTHRESHOLDDAYS = 180;

        public static final Integer DEFAULT_DATASTRINGTHRESHOLD = 50;
        public static final Integer DEFAULT_SUMMARYLENGTH = 250;
        public static final Integer DEFAULT_ATTACHMENTMAXSIZE = 5;
        public static final Boolean DEFAULT_WORKINDEXATTACHMENT = false;
        public static final Boolean DEFAULT_WORKCOMPLETEDINDEXATTACHMENT = true;
        public static final Boolean DEFAULT_DOCUMENTINDEXATTACHMENT = true;

        public static final Boolean DEFAULT_LOWFREQDOCUMENTENABLE = true;
        public static final String DEFAULT_LOWFREQDOCUMENTCRON = "40 15 19 * * ?";
        public static final Integer DEFAULT_LOWFREQDOCUMENTBATCHSIZE = 50;
        public static final Integer DEFAULT_LOWFREQDOCUMENTMAXCOUNT = 50000;
        public static final Integer DEFAULT_LOWFREQDOCUMENTMAXMINUTES = 60;

        public static final Boolean DEFAULT_LOWFREQWORKENABLE = true;
        public static final String DEFAULT_LOWFREQWORKCRON = "40 15 20 * * ?";
        public static final Integer DEFAULT_LOWFREQWORKBATCHSIZE = 50;
        public static final Integer DEFAULT_LOWFREQWORKMAXCOUNT = 50000;
        public static final Integer DEFAULT_LOWFREQWORKMAXMINUTES = 60;

        public static final Boolean DEFAULT_LOWFREQWORKCOMPLETEDENABLE = true;
        public static final String DEFAULT_LOWFREQWORKCOMPLETEDCRON = "40 15 21 * * ?";
        public static final Integer DEFAULT_LOWFREQWORKCOMPLETEDBATCHSIZE = 200;
        public static final Integer DEFAULT_LOWFREQWORKCOMPLETEDMAXCOUNT = 500000;
        public static final Integer DEFAULT_LOWFREQWORKCOMPLETEDMAXMINUTES = 120;

        public static final Boolean DEFAULT_OPTIMIZEINDEXENABLE = true;
        public static final String DEFAULT_OPTIMIZEINDEXCRON = "40 15 23 * * ?";

        public static final Boolean DEFAULT_HIGHFREQWORKENABLE = true;
        public static final String DEFAULT_HIGHFREQWORKCRON = "40 0/3 7-19 * * ?";
        public static final Integer DEFAULT_HIGHFREQWORKBATCHSIZE = 50;
        public static final Integer DEFAULT_HIGHFREQWORKMAXCOUNT = 200;
        public static final Integer DEFAULT_HIGHFREQWORKMAXMINUTES = 2;

        public static final Boolean DEFAULT_HIGHFREQWORKCOMPLETEDENABLE = true;
        public static final String DEFAULT_HIGHFREQWORKCOMPLETEDCRON = "40 0/10 7-19 * * ?";
        public static final Integer DEFAULT_HIGHFREQWORKCOMPLETEDBATCHSIZE = 50;
        public static final Integer DEFAULT_HIGHFREQWORKCOMPLETEDMAXCOUNT = 500;
        public static final Integer DEFAULT_HIGHFREQWORKCOMPLETEDMAXMINUTES = 5;

        public static final Boolean DEFAULT_HIGHFREQDOCUMENTENABLE = true;
        public static final String DEFAULT_HIGHFREQDOCUMENTCRON = "55 0/10 7-19 * * ?";
        public static final Integer DEFAULT_HIGHFREQDOCUMENTBATCHSIZE = 50;
        public static final Integer DEFAULT_HIGHFREQDOCUMENTMAXCOUNT = 500;
        public static final Integer DEFAULT_HIGHFREQDOCUMENTMAXMINUTES = 5;

        public static final Boolean DEFAULT_SEARCHENABLE = true;

        public static final Float DEFAULT_SEARCHTITLEBOOST = 4.0f;
        public static final Float DEFAULT_SEARCHSUMMARYBOOST = 3.0f;
        public static final Float DEFAULT_SEARCHBODYBOOST = 2.0f;
        public static final Float DEFAULT_SEARCHATTACHMENTBOOST = 1.0f;

        public static final Integer DEFAULT_SEARCHPAGESIZE = 20;
        public static final Integer DEFAULT_SEARCHMAXPAGESIZE = 5000;

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

        public static final Integer DEFAULT_MORELIKETHISSIZE = 10;
        public static final Integer DEFAULT_MORELIKETHISMAXSIZE = 100;
        public static final Integer DEFAULT_MORELIKETHISMINTERMFREQ = 2;
        public static final Integer DEFAULT_MORELIKETHISMINDOCFREQ = 2;
        public static final Float DEFAULT_MORELIKETHISSCORETHRESHOLD = 8.0f;

        @FieldDescribe("索引模式:localDirectory(本地文件系统),hdfsDirectory(hadoop),sharedDirectory(共享文件系统目录).")
        @Schema(description = "索引模式:localDirectory(本地文件系统),hdfsDirectory(hadoop),sharedDirectory(共享文件系统目录).")
        private String mode;

        @FieldDescribe("hadoop文件系统地址.")
        @Schema(description = "hadoop文件系统地址.")
        private String hdfsDirectoryDefaultFS;

        @FieldDescribe("hadoop文件系统目录.")
        @Schema(description = "hadoop文件系统目录.")
        private String hdfsDirectoryPath;

        @FieldDescribe("共享文件系统目录.")
        @Schema(description = "共享文件系统目录.")
        private String sharedDirectoryPath;

        @FieldDescribe("索引分段数量.")
        @Schema(description = "索引分段数量.")
        private Integer maxSegments;

        @FieldDescribe("是否启用优化索引.")
        @Schema(description = "是否启用优化索引.")
        private Boolean optimizeIndexEnable;

        @FieldDescribe("优化索引定时配置.")
        @Schema(description = "优化索引定时配置.")
        private String optimizeIndexCron;

        @FieldDescribe("业务数据最大文本长度阈值,超过此阈值将忽略写入到索引.")
        @Schema(description = "业务数据最大文本长度阈值,超过此阈值将忽略写入到索引.")
        private Integer dataStringThreshold;

        @FieldDescribe("摘要长度.")
        @Schema(description = "摘要长度.")
        private Integer summaryLength;

        @FieldDescribe("附件索引阈值(兆),超过此值的附件不进行索引.")
        @Schema(description = "附件索引阈值(兆),超过此值的附件不进行索引.")
        private Integer attachmentMaxSize;

        @FieldDescribe("是否对流转中工作进行索引.")
        @Schema(description = "是否对流转中工作进行索引.")
        private Boolean workIndexAttachment;

        @FieldDescribe("是否对已完成工作进行索引.")
        @Schema(description = "是否对已完成工作进行索引.")
        private Boolean workCompletedIndexAttachment;

        @FieldDescribe("是否对内容管理文档进行索引.")
        @Schema(description = "是否对内容管理文档进行索引.")
        private Boolean documentIndexAttachment;

        @FieldDescribe("是否启用流转中工作低频索引.")
        @Schema(description = "是否启用流转中工作低频索引.")
        private Boolean lowFreqWorkEnable;

        @FieldDescribe("流转中工作低频索引定时配置.")
        @Schema(description = "流转中工作低频索引定时配置.")
        private String lowFreqWorkCron;

        @FieldDescribe("流转中工作低频索引批量获取大小.")
        @Schema(description = "流转中工作低频索引批量获取大小.")
        private Integer lowFreqWorkBatchSize;

        @FieldDescribe("流转中工作低频索单次最大处理数量.")
        @Schema(description = "流转中工作低频索单次最大处理数量.")
        private Integer lowFreqWorkMaxCount;

        @FieldDescribe("流转中工作低频索单次最大处理时长(分钟).")
        @Schema(description = "流转中工作低频索单次最大处理时长(分钟).")
        private Integer lowFreqWorkMaxMinutes;

        @FieldDescribe("是否启用已完成工作低频索引.")
        @Schema(description = "是否启用已完成工作低频索引.")
        private Boolean lowFreqWorkCompletedEnable;

        @FieldDescribe("已完成工作低频索引定时配置.")
        @Schema(description = "已完成工作低频索引定时配置.")
        private String lowFreqWorkCompletedCron;

        @FieldDescribe("流转中工作低频索引批量获取大小.")
        @Schema(description = "流转中工作低频索引批量获取大小.")
        private Integer lowFreqWorkCompletedBatchSize;

        @FieldDescribe("流转中工作低频索引单次最大处理数量.")
        @Schema(description = "流转中工作低频索引单次最大处理数量.")
        private Integer lowFreqWorkCompletedMaxCount;

        @FieldDescribe("流转中工作低频索引单次最大处理时长(分钟).")
        @Schema(description = "流转中工作低频索引单次最大处理时长(分钟).")
        private Integer lowFreqWorkCompletedMaxMinutes;

        @FieldDescribe("是否启用内容管理文档低频索引.")
        @Schema(description = "是否启用内容管理文档低频索引.")
        private Boolean lowFreqDocumentEnable;

        @FieldDescribe("内容管理文档低频索引定时配置.")
        @Schema(description = "内容管理文档低频索引定时配置.")
        private String lowFreqDocumentCron;

        @FieldDescribe("内容管理文档低频索引批量获取大小.")
        @Schema(description = "内容管理文档低频索引批量获取大小.")
        private Integer lowFreqDocumentBatchSize;

        @FieldDescribe("内容管理文档低频索引单次最大处理数量.")
        @Schema(description = "内容管理文档低频索引单次最大处理数量.")
        private Integer lowFreqDocumentMaxCount;

        @FieldDescribe("内容管理文档低频索引单次最大处理时长(分钟).")
        @Schema(description = "内容管理文档低频索引单次最大处理时长(分钟).")
        private Integer lowFreqDocumentMaxMinutes;

        @FieldDescribe("是否启用流转中工作高频索引.")
        @Schema(description = "是否启用流转中工作高频索引.")
        private Boolean highFreqWorkEnable;

        @FieldDescribe("流转中工作高频索引定时配置.")
        @Schema(description = "流转中工作高频索引定时配置.")
        private String highFreqWorkCron;

        @FieldDescribe("流转中工作高频索引批量获取大小.")
        @Schema(description = "流转中工作高频索引批量获取大小.")
        private Integer highFreqWorkBatchSize;

        @FieldDescribe("流转中工作高频索引单次最大处理数量.")
        @Schema(description = "流转中工作高频索引单次最大处理数量.")
        private Integer highFreqWorkMaxCount;

        @FieldDescribe("流转中工作高频索引单次最大处理时长(分钟).")
        @Schema(description = "流转中工作高频索引单次最大处理时长(分钟).")
        private Integer highFreqWorkMaxMinutes;

        @FieldDescribe("是否启用已完成工作高频索引.")
        @Schema(description = "是否启用已完成工作高频索引.")
        private Boolean highFreqWorkCompletedEnable;

        @FieldDescribe("已完成工作高频索引定时配置.")
        @Schema(description = "已完成工作高频索引定时配置.")
        private String highFreqWorkCompletedCron;

        @FieldDescribe("已完成工作高频索引批量获取大小.")
        @Schema(description = "已完成工作高频索引批量获取大小.")
        private Integer highFreqWorkCompletedBatchSize;

        @FieldDescribe("已完成工作高频索引单次最大处理数量.")
        @Schema(description = "已完成工作高频索引单次最大处理数量.")
        private Integer highFreqWorkCompletedMaxCount;

        @FieldDescribe("已完成工作高频索引单次最大处理时长(分钟).")
        @Schema(description = "已完成工作高频索引单次最大处理时长(分钟).")
        private Integer highFreqWorkCompletedMaxMinutes;

        @FieldDescribe("是否启用内容管理文档高频索引.")
        @Schema(description = "是否启用内容管理文档高频索引.")
        private Boolean highFreqDocumentEnable;

        @FieldDescribe("内容管理文档高频索引定时配置.")
        @Schema(description = "内容管理文档高频索引定时配置.")
        private String highFreqDocumentCron;

        @FieldDescribe("内容管理文档高频索引批量获取大小.")
        @Schema(description = "内容管理文档高频索引批量获取大小.")
        private Integer highFreqDocumentBatchSize;

        @FieldDescribe("内容管理文档高频索引单次最大处理数量.")
        @Schema(description = "内容管理文档高频索引单次最大处理数量.")
        private Integer highFreqDocumentMaxCount;

        @FieldDescribe("内容管理文档高频索引单次最大处理时长(分钟).")
        @Schema(description = "内容管理文档高频索引单次最大处理时长(分钟).")
        private Integer highFreqDocumentMaxMinutes;

        @FieldDescribe("检索内容清理阈值(天).")
        @Schema(description = "检索内容清理阈值(天).")
        private Integer cleanupThresholdDays;

        @FieldDescribe("是否启用搜索.")
        @Schema(description = "是否启用搜索.")
        private Boolean searchEnable;

        @FieldDescribe("搜索标题加权.")
        @Schema(description = "搜索标题加权.")
        private Float searchTitleBoost;

        @FieldDescribe("搜索摘要加权.")
        @Schema(description = "搜索摘要加权.")
        private Float searchSummaryBoost;

        @FieldDescribe("搜索内容加权.")
        @Schema(description = "搜索内容加权.")
        private Float searchBodyBoost;

        @FieldDescribe("搜索附件加权.")
        @Schema(description = "搜索附件加权.")
        private Float searchAttachmentBoost;

        @FieldDescribe("搜索分页大小.")
        @Schema(description = "搜索分页大小.")
        private Integer searchPageSize;

        @FieldDescribe("搜索最大分页大小.")
        @Schema(description = "搜索最大分页大小.")
        private Integer searchMaxPageSize;

        @FieldDescribe("搜索最大命中数量.")
        @Schema(description = "搜索最大命中数量.")
        private Integer searchMaxHits;

        @FieldDescribe("维度最大分组数量.")
        @Schema(description = "维度最大分组数量.")
        private Integer facetMaxGroups;

        @FieldDescribe("维度分组排序方式:keyAsc(依据分组值升序排序),keyDesc(依据分组值奖序排序),countAsc(依据分组数量升序排序),countDesc(依据分组数量降序排序).")
        @Schema(description = "流转中工作低频索引定时配置.")
        private String facetGroupOrder;

        @FieldDescribe("高亮返回片段长度.")
        @Schema(description = "高亮返回片段长度.")
        private Integer highlightFragmentSize;
        @FieldDescribe("高亮返回片段数量.")
        @Schema(description = "高亮返回片段数量.")
        private Integer highlightFragmentCount;
        @FieldDescribe("高亮前缀,默认<em>.")
        @Schema(description = "高亮前缀,默认<em>.")
        private String highlightPre;
        @FieldDescribe("高亮后缀,默认</em>.")
        @Schema(description = "高亮后缀,默认</em>.")
        private String highlightPost;

        @FieldDescribe("相似检索返回文档数量.")
        @Schema(description = "相似检索返回文档数量.")
        private Integer moreLikeThisSize;

        @FieldDescribe("相似检索最大返回文档数量.")
        @Schema(description = "相似检索最大返回文档数量.")
        private Integer moreLikeThisMaxSize;

        @FieldDescribe("相似检索中检索项最小出现多少次即作为有效检索项.")
        @Schema(description = "相似检索中检索项最小出现多少次即作为有效检索项.")
        private Integer moreLikeThisMinTermFreq;

        @FieldDescribe("相似检索中检索项最小出现在多少个文档中即作为有效检索项.")
        @Schema(description = "相似检索中检索项最小出现在多少个文档中即作为有效检索项.")
        private Integer moreLikeThisMinDocFreq;

        @FieldDescribe("相似检索得分阈值,低于此分数则忽略.")
        @Schema(description = "相似检索得分阈值,低于此分数则忽略.")
        private Float moreLikeThisScoreThreshold;

        public Boolean getOptimizeIndexEnable() {
            return null == this.optimizeIndexEnable ? DEFAULT_OPTIMIZEINDEXENABLE : this.optimizeIndexEnable;
        }

        public String getOptimizeIndexCron() {
            if (StringUtils.isNotEmpty(this.optimizeIndexCron)
                    && CronExpression.isValidExpression(this.optimizeIndexCron)) {
                return this.optimizeIndexCron;
            } else {
                return DEFAULT_OPTIMIZEINDEXCRON;
            }
        }

        public String getHdfsDirectoryPath() {
            return StringUtils.isBlank(hdfsDirectoryPath)
                    ? DEFAULT_HDFSDIRECTORYPATH
                    : adjustHdfsDirectoryPath();
        }

        private String adjustHdfsDirectoryPath() {
            return StringUtils.startsWith(this.hdfsDirectoryPath, "/") ? this.hdfsDirectoryPath
                    : "/" + hdfsDirectoryPath;
        }

        public Integer getMaxSegments() {
            return NumberTools.nullOrLessThan(this.maxSegments, 1) ? DEFAULT_MAXSEGMENTS
                    : this.maxSegments;
        }

        public Boolean getWorkIndexAttachment() {
            return (null == this.workIndexAttachment) ? DEFAULT_WORKINDEXATTACHMENT : this.workIndexAttachment;
        }

        public Boolean getWorkCompletedIndexAttachment() {
            return (null == this.workCompletedIndexAttachment) ? DEFAULT_WORKCOMPLETEDINDEXATTACHMENT
                    : this.workCompletedIndexAttachment;
        }

        public Boolean getDocumentIndexAttachment() {
            return (null == this.documentIndexAttachment) ? DEFAULT_DOCUMENTINDEXATTACHMENT
                    : this.documentIndexAttachment;
        }

        public Boolean getHighFreqWorkEnable() {
            return null == highFreqWorkEnable ? DEFAULT_HIGHFREQWORKENABLE : this.highFreqWorkEnable;
        }

        public String getHighFreqWorkCron() {
            if (StringUtils.isNotEmpty(this.highFreqWorkCron)
                    && CronExpression.isValidExpression(this.highFreqWorkCron)) {
                return this.highFreqWorkCron;
            } else {
                return DEFAULT_HIGHFREQWORKCRON;
            }
        }

        public Integer getHighFreqWorkBatchSize() {
            return NumberTools.nullOrLessThan(this.highFreqWorkBatchSize, 1) ? DEFAULT_HIGHFREQWORKBATCHSIZE
                    : this.highFreqWorkBatchSize;
        }

        public Integer getHighFreqWorkMaxCount() {
            return NumberTools.nullOrLessThan(this.highFreqWorkMaxCount, 1) ? DEFAULT_HIGHFREQWORKMAXCOUNT
                    : this.highFreqWorkMaxCount;
        }

        public Integer getHighFreqWorkMaxMinutes() {
            return NumberTools.nullOrLessThan(this.highFreqWorkMaxMinutes, 1) ? DEFAULT_HIGHFREQWORKMAXMINUTES
                    : this.highFreqWorkMaxMinutes;
        }

        public Boolean getLowFreqWorkEnable() {
            return null == lowFreqWorkEnable ? DEFAULT_LOWFREQWORKENABLE : this.lowFreqWorkEnable;
        }

        public String getLowFreqWorkCron() {
            if (StringUtils.isNotEmpty(this.lowFreqWorkCron)
                    && CronExpression.isValidExpression(this.lowFreqWorkCron)) {
                return this.lowFreqWorkCron;
            } else {
                return DEFAULT_LOWFREQWORKCRON;
            }
        }

        public Integer getLowFreqWorkBatchSize() {
            return NumberTools.nullOrLessThan(this.lowFreqWorkBatchSize, 1) ? DEFAULT_LOWFREQWORKBATCHSIZE
                    : this.lowFreqWorkBatchSize;
        }

        public Integer getLowFreqWorkMaxCount() {
            return NumberTools.nullOrLessThan(this.lowFreqWorkMaxCount, 1) ? DEFAULT_LOWFREQWORKMAXCOUNT
                    : this.lowFreqWorkMaxCount;
        }

        public Integer getLowFreqWorkMaxMinutes() {
            return NumberTools.nullOrLessThan(this.lowFreqWorkMaxMinutes, 1) ? DEFAULT_LOWFREQWORKMAXMINUTES
                    : this.lowFreqWorkMaxMinutes;
        }

        public Integer getMoreLikeThisSize() {
            return NumberTools.nullOrLessThan(this.moreLikeThisSize, 1) ? DEFAULT_MORELIKETHISSIZE
                    : this.moreLikeThisSize;
        }

        public Integer getMoreLikeThisMaxSize() {
            return NumberTools.nullOrLessThan(this.moreLikeThisMaxSize, 1) ? DEFAULT_MORELIKETHISMAXSIZE
                    : this.moreLikeThisMaxSize;
        }

        public Integer getMoreLikeThisMinTermFreq() {
            return NumberTools.nullOrLessThan(this.moreLikeThisMinTermFreq, 1) ? DEFAULT_MORELIKETHISMINTERMFREQ
                    : this.moreLikeThisMinTermFreq;
        }

        public Integer getMoreLikeThisMinDocFreq() {
            return NumberTools.nullOrLessThan(this.moreLikeThisMinDocFreq, 1) ? DEFAULT_MORELIKETHISMINDOCFREQ
                    : this.moreLikeThisMinDocFreq;
        }

        public Float getMoreLikeThisScoreThreshold() {
            return NumberTools.nullOrLessThan(this.moreLikeThisScoreThreshold, 0f) ? DEFAULT_MORELIKETHISSCORETHRESHOLD
                    : this.moreLikeThisScoreThreshold;
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

        public Integer getSearchPageSize() {
            return NumberTools.nullOrLessThan(searchPageSize, 1) ? DEFAULT_SEARCHPAGESIZE : searchPageSize;
        }

        public Integer getSearchMaxPageSize() {
            return NumberTools.nullOrLessThan(searchMaxPageSize, 1) ? DEFAULT_SEARCHMAXPAGESIZE : searchMaxPageSize;
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

        public String getLowFreqWorkCompletedCron() {
            if (StringUtils.isNotEmpty(this.lowFreqWorkCompletedCron)
                    && CronExpression.isValidExpression(this.lowFreqWorkCompletedCron)) {
                return this.lowFreqWorkCompletedCron;
            } else {
                return DEFAULT_LOWFREQWORKCOMPLETEDCRON;
            }
        }

        public String getLowFreqDocumentCron() {
            if (StringUtils.isNotEmpty(this.lowFreqDocumentCron)
                    && CronExpression.isValidExpression(this.lowFreqDocumentCron)) {
                return this.lowFreqDocumentCron;
            } else {
                return DEFAULT_LOWFREQDOCUMENTCRON;
            }
        }

        public String getHighFreqWorkCompletedCron() {
            if (StringUtils.isNotEmpty(this.highFreqWorkCompletedCron)
                    && CronExpression.isValidExpression(this.highFreqWorkCompletedCron)) {
                return this.highFreqWorkCompletedCron;
            } else {
                return DEFAULT_HIGHFREQWORKCOMPLETEDCRON;
            }
        }

        public String getHighFreqDocumentCron() {
            if (StringUtils.isNotEmpty(this.highFreqDocumentCron)
                    && CronExpression.isValidExpression(this.highFreqDocumentCron)) {
                return this.highFreqDocumentCron;
            } else {
                return DEFAULT_HIGHFREQDOCUMENTCRON;
            }
        }

        public String getSharedDirectoryPath() {
            return StringUtils.isBlank(sharedDirectoryPath)
                    ? DEFAULT_SHAREDDIRECTORYPATH
                    : adjustSharedDirectoryPath();
        }

        private String adjustSharedDirectoryPath() {
            return StringUtils.startsWith(this.sharedDirectoryPath, "/") ? this.sharedDirectoryPath
                    : "/" + sharedDirectoryPath;
        }

        public Integer getCleanupThresholdDays() {
            return NumberTools.nullOrLessThan(cleanupThresholdDays, 1)
                    ? DEFAULT_CLEANUPTHRESHOLDDAYS
                    : this.cleanupThresholdDays;
        }

        public Integer getLowFreqWorkCompletedMaxMinutes() {
            return NumberTools.nullOrLessThan(lowFreqWorkCompletedMaxMinutes, 1)
                    ? DEFAULT_LOWFREQWORKCOMPLETEDMAXMINUTES
                    : this.lowFreqWorkCompletedMaxMinutes;
        }

        public Integer getLowFreqWorkCompletedMaxCount() {
            return NumberTools.nullOrLessThan(lowFreqWorkCompletedMaxCount, 1)
                    ? DEFAULT_LOWFREQWORKCOMPLETEDMAXCOUNT
                    : this.lowFreqWorkCompletedMaxCount;
        }

        public Integer getLowFreqWorkCompletedBatchSize() {
            return NumberTools.nullOrLessThan(lowFreqWorkCompletedBatchSize, 1)
                    ? DEFAULT_LOWFREQWORKCOMPLETEDBATCHSIZE
                    : this.lowFreqWorkCompletedBatchSize;
        }

        public Boolean getLowFreqWorkCompletedEnable() {
            return null == lowFreqWorkCompletedEnable ? DEFAULT_LOWFREQWORKCOMPLETEDENABLE
                    : this.lowFreqWorkCompletedEnable;
        }

        public Integer getLowFreqDocumentMaxMinutes() {
            return NumberTools.nullOrLessThan(this.lowFreqDocumentMaxMinutes, 1)
                    ? DEFAULT_LOWFREQDOCUMENTMAXMINUTES
                    : this.lowFreqDocumentMaxMinutes;
        }

        public Integer getLowFreqDocumentMaxCount() {
            return NumberTools.nullOrLessThan(lowFreqDocumentMaxCount, 1)
                    ? DEFAULT_LOWFREQDOCUMENTMAXCOUNT
                    : this.lowFreqDocumentMaxCount;
        }

        public Integer getLowFreqDocumentBatchSize() {
            return NumberTools.nullOrLessThan(lowFreqDocumentBatchSize, 1)
                    ? DEFAULT_LOWFREQDOCUMENTBATCHSIZE
                    : this.lowFreqDocumentBatchSize;
        }

        public Boolean getLowFreqDocumentEnable() {
            return null == lowFreqDocumentEnable ? DEFAULT_LOWFREQDOCUMENTENABLE
                    : this.lowFreqDocumentEnable;
        }

        public Integer getHighFreqDocumentMaxMinutes() {
            return NumberTools.nullOrLessThan(this.highFreqDocumentMaxMinutes, 1)
                    ? DEFAULT_HIGHFREQDOCUMENTMAXMINUTES
                    : this.highFreqDocumentMaxMinutes;
        }

        public Integer getHighFreqDocumentMaxCount() {
            return NumberTools.nullOrLessThan(highFreqDocumentMaxCount, 1)
                    ? DEFAULT_HIGHFREQDOCUMENTMAXCOUNT
                    : this.highFreqDocumentMaxCount;
        }

        public Integer getHighFreqDocumentBatchSize() {
            return NumberTools.nullOrLessThan(highFreqDocumentBatchSize, 1)
                    ? DEFAULT_HIGHFREQDOCUMENTBATCHSIZE
                    : this.highFreqDocumentBatchSize;
        }

        public Boolean getHighFreqDocumentEnable() {
            return null == highFreqDocumentEnable ? DEFAULT_HIGHFREQDOCUMENTENABLE
                    : this.highFreqDocumentEnable;
        }

        public Integer getHighFreqWorkCompletedMaxMinutes() {
            return NumberTools.nullOrLessThan(this.highFreqWorkCompletedMaxMinutes, 1)
                    ? DEFAULT_HIGHFREQWORKCOMPLETEDMAXMINUTES
                    : this.highFreqWorkCompletedMaxMinutes;
        }

        public Integer getHighFreqWorkCompletedMaxCount() {
            return NumberTools.nullOrLessThan(highFreqWorkCompletedMaxCount, 1)
                    ? DEFAULT_HIGHFREQWORKCOMPLETEDMAXCOUNT
                    : this.highFreqWorkCompletedMaxCount;
        }

        public Integer getHighFreqWorkCompletedBatchSize() {
            return NumberTools.nullOrLessThan(highFreqWorkCompletedBatchSize, 1)
                    ? DEFAULT_HIGHFREQWORKCOMPLETEDBATCHSIZE
                    : this.highFreqWorkCompletedBatchSize;
        }

        public Boolean getHighFreqWorkCompletedEnable() {
            return null == highFreqWorkCompletedEnable ? DEFAULT_HIGHFREQWORKCOMPLETEDENABLE
                    : this.highFreqWorkCompletedEnable;
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