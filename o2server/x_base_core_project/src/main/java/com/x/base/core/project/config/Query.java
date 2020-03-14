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

	public static final Boolean DEFAULT_EXTRACTOFFICE = true;
	public static final Boolean DEFAULT_EXTRACTPDF = true;
	public static final Boolean DEFAULT_EXTRACTTEXT = true;
	public static final Boolean DEFAULT_EXTRACTIMAGE = false;
	public static final String DEFAULT_TESSLANGUAGE = "chi_sim";

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

		public final static Boolean DEFAULT_ENABLE = true;

		public final static String DEFAULT_CRON = "30 30 7-21 * * ?";

		public final static Integer DEFAULT_CONUT = 100;

		public final static Integer DEFAULT_MAXATTACHMENTSIZE = 1024 * 1024 * 5;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式.")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("每次处理的数量,默认每小时处理所以默认为100,同时每次将重爬最旧的50%,按时间轮询50%.")
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

		public final static Boolean DEFAULT_ENABLE = true;

		public final static String DEFAULT_CRON = "40 40 7-21 * * ?";

		/* 由于每小时运行,那么每次更新100份 */
		public final static Integer DEFAULT_CONUT = 100;

		public final static Integer DEFAULT_MAXATTACHMENTSIZE = 1024 * 1024 * 5;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式.")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("每次处理的数量,默认每小时处理所以默认为100,同时每次将重爬最旧的50%,按时间轮询50%.")
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

		public final static Boolean DEFAULT_ENABLE = true;

		public final static String DEFAULT_CRON = "50 50 22 * * ?";

		public final static Integer DEFAULT_CONUT = 2000;

		public final static Integer DEFAULT_MAXATTACHMENTSIZE = 1024 * 1024 * 5;

		@FieldDescribe("是否启用")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("定时cron表达式.")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("每次处理的数量,默认每小时处理所以默认为2000,同时每次将重爬最旧的25%,按时间轮询25%.")
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
}