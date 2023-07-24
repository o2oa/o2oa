package com.x.query.core.express.index;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.NumericUtils;
import org.apache.solr.hdfs.store.HdfsDirectory;

import com.x.base.core.project.bean.tuple.Triple;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Query;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.NumberTools;

public class Indexs {

	private Indexs() {
		// nothing
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(Indexs.class);

	public static final String CATEGORY_PROCESSPLATFORM = "processPlatform";
	public static final String CATEGORY_CMS = "cms";
	public static final String CATEGORY_SEARCH = "search";

	public static final String TYPE_WORKCOMPLETED = "workCompleted";
	public static final String TYPE_WORK = "work";
	public static final String TYPE_DOCUMENT = "document";

	public static final String KEY_ENTIRE = "entire";

	public static final String FIELD_ID = "id";
	public static final String FIELD_CATEGORY = "category";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_KEY = "key";
	public static final String FIELD_INDEXTIME = "indexTime";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_SUMMARY = "summary";
	public static final String FIELD_BODY = "body";
	public static final String FIELD_ATTACHMENT = "attachment";
	public static final String FIELD_CREATETIME = "createTime";
	public static final String FIELD_UPDATETIME = "updateTime";
	public static final String FIELD_CREATETIMEMONTH = "createTimeMonth";
	public static final String FIELD_UPDATETIMEMONTH = "updateTimeMonth";
	public static final String FIELD_READERS = "readers";
	public static final String FIELD_CREATORPERSON = "creatorPerson";
	public static final String FIELD_CREATORUNIT = "creatorUnit";
	public static final String FIELD_CREATORUNITLEVELNAME = "creatorUnitLevelName";
	public static final String FIELD_APPLICATION = "processPlatform_string_application";
	public static final String FIELD_APPLICATIONNAME = "processPlatform_string_applicationName";
	public static final String FIELD_APPLICATIONALIAS = "applicationAlias";
	public static final String FIELD_PROCESS = "processPlatform_string_process";
	public static final String FIELD_PROCESSNAME = "processPlatform_string_processName";
	public static final String FIELD_PROCESSALIAS = "processPlatform_string_processAlias";
	public static final String FIELD_SERIAL = "processPlatform_string_serial";
	public static final String FIELD_JOB = "job";
	public static final String FIELD_EXPIRED = "expired";
	public static final String FIELD_EXPIRETIME = "expireTime";
	public static final String FIELD_COMPLETED = "processPlatform_boolean_completed";

	public static final String FIELD_APPID = "appId";
	public static final String FIELD_APPNAME = "cms_string_appName";
	public static final String FIELD_APPALIAS = "appAlias";
	public static final String FIELD_CATEGORYID = "categoryId";
	public static final String FIELD_CATEGORYNAME = "cms_string_categoryName";
	public static final String FIELD_CATEGORYALIAS = "categoryAlias";
	public static final String FIELD_DESCRIPTION = "description";
	public static final String FIELD_PUBLISHTIME = "publishTime";
	public static final String FIELD_MODIFYTIME = "modifyTime";

	public static final String FIELD_HIGHLIGHTING = "highlighting";
	public static final String READERS_SYMBOL_ALL = "*";

	public static final String BOOLEAN_TRUE_STRING_VALUE = "true";
	public static final String BOOLEAN_FALSE_STRING_VALUE = "false";

	public static final List<String> FACET_FIELDS = Stream.<String>of(FIELD_CATEGORY, FIELD_CREATETIMEMONTH,
			FIELD_UPDATETIMEMONTH, FIELD_APPLICATIONNAME, FIELD_PROCESSNAME, FIELD_APPNAME, FIELD_CATEGORYNAME,
			FIELD_CREATORPERSON, FIELD_CREATORUNIT, FIELD_COMPLETED).collect(Collectors.toUnmodifiableList());

	private static final List<String> FIXED_DATE_FIELDS = Stream
			.<String>of(FIELD_INDEXTIME, FIELD_CREATETIME, FIELD_UPDATETIME).collect(Collectors.toUnmodifiableList());

	public static final String FIELD_TYPE_STRING = "string";
	public static final String FIELD_TYPE_STRINGS = "strings";
	public static final String FIELD_TYPE_BOOLEAN = "boolean";
	public static final String FIELD_TYPE_BOOLEANS = "booleans";
	public static final String FIELD_TYPE_NUMBER = "number";
	public static final String FIELD_TYPE_NUMBERS = "numbers";
	public static final String FIELD_TYPE_DATE = "date";
	public static final String FIELD_TYPE_DATES = "dates";

	public static final String PREFIX_FIELD_DATA = "data_";
	public static final String PREFIX_FIELD_DATA_STRING = PREFIX_FIELD_DATA + FIELD_TYPE_STRING + "_";
	public static final String PREFIX_FIELD_DATA_STRINGS = PREFIX_FIELD_DATA + FIELD_TYPE_STRINGS + "_";
	public static final String PREFIX_FIELD_DATA_BOOLEAN = PREFIX_FIELD_DATA + FIELD_TYPE_BOOLEAN + "_";
	public static final String PREFIX_FIELD_DATA_BOOLEANS = PREFIX_FIELD_DATA + FIELD_TYPE_BOOLEANS + "_";
	public static final String PREFIX_FIELD_DATA_NUMBER = PREFIX_FIELD_DATA + FIELD_TYPE_NUMBER + "_";
	public static final String PREFIX_FIELD_DATA_NUMBERS = PREFIX_FIELD_DATA + FIELD_TYPE_NUMBERS + "_";
	public static final String PREFIX_FIELD_DATA_DATE = PREFIX_FIELD_DATA + FIELD_TYPE_DATE + "_";
	public static final String PREFIX_FIELD_DATA_DATES = PREFIX_FIELD_DATA + FIELD_TYPE_DATES + "_";

	public static final String PREFIX_FIELD_PROCESSPLATFORM = "processPlatform_";

	public static final String FIELD_ROCESSPLATFORM_TASKPERSONNAMES = "taskPersonNames";
	public static final String FIELD_ROCESSPLATFORM_PREVTASKPERSONNAMES = "prevTaskPersonNames";

	public static final String PREFIX_FIELD_PROCESSPLATFORM_STRING = PREFIX_FIELD_PROCESSPLATFORM + FIELD_TYPE_STRING
			+ "_";
	public static final String PREFIX_FIELD_PROCESSPLATFORM_STRINGS = PREFIX_FIELD_PROCESSPLATFORM + FIELD_TYPE_STRINGS
			+ "_";
	public static final String PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN = PREFIX_FIELD_PROCESSPLATFORM + FIELD_TYPE_BOOLEAN
			+ "_";
	public static final String PREFIX_FIELD_PROCESSPLATFORM_BOOLEANS = PREFIX_FIELD_PROCESSPLATFORM
			+ FIELD_TYPE_BOOLEANS + "_";
	public static final String PREFIX_FIELD_PROCESSPLATFORM_NUMBER = PREFIX_FIELD_PROCESSPLATFORM + FIELD_TYPE_NUMBER
			+ "_";
	public static final String PREFIX_FIELD_PROCESSPLATFORM_NUMBERS = PREFIX_FIELD_PROCESSPLATFORM + FIELD_TYPE_NUMBERS
			+ "_";
	public static final String PREFIX_FIELD_PROCESSPLATFORM_DATE = PREFIX_FIELD_PROCESSPLATFORM + FIELD_TYPE_DATE + "_";
	public static final String PREFIX_FIELD_PROCESSPLATFORM_DATES = PREFIX_FIELD_PROCESSPLATFORM + FIELD_TYPE_DATES
			+ "_";

	public static final String PREFIX_FIELD_CMS = "cms_";
	public static final String PREFIX_FIELD_CMS_STRING = PREFIX_FIELD_CMS + FIELD_TYPE_STRING + "_";
	public static final String PREFIX_FIELD_CMS_STRINGS = PREFIX_FIELD_CMS + FIELD_TYPE_STRINGS + "_";
	public static final String PREFIX_FIELD_CMS_BOOLEAN = PREFIX_FIELD_CMS + FIELD_TYPE_BOOLEAN + "_";
	public static final String PREFIX_FIELD_CMS_BOOLEANS = PREFIX_FIELD_CMS + FIELD_TYPE_BOOLEANS + "_";
	public static final String PREFIX_FIELD_CMS_NUMBER = PREFIX_FIELD_CMS + FIELD_TYPE_NUMBER + "_";
	public static final String PREFIX_FIELD_CMS_NUMBERS = PREFIX_FIELD_CMS + FIELD_TYPE_NUMBERS + "_";
	public static final String PREFIX_FIELD_CMS_DATE = PREFIX_FIELD_CMS + FIELD_TYPE_DATE + "_";
	public static final String PREFIX_FIELD_CMS_DATES = PREFIX_FIELD_CMS + FIELD_TYPE_DATES + "_";

	protected static final List<WoField> FIXEDFIELD_APPLICATION = ListUtils
			.unmodifiableList(Arrays.asList(new WoField(Indexs.FIELD_TITLE, "标题", Indexs.FIELD_TYPE_STRING),
					new WoField(Indexs.FIELD_CREATORPERSON, "创建者", Indexs.FIELD_TYPE_STRING),
					new WoField(Indexs.FIELD_CREATORUNIT, "部门", Indexs.FIELD_TYPE_STRING),
					new WoField(Indexs.FIELD_CREATETIME, "创建时间", Indexs.FIELD_TYPE_DATE),
					new WoField(Indexs.FIELD_UPDATETIME, "更新时间", Indexs.FIELD_TYPE_DATE),
					new WoField(Indexs.FIELD_SERIAL, "文号", Indexs.FIELD_TYPE_STRING),
					new WoField(Indexs.FIELD_PROCESSNAME, "流程", Indexs.FIELD_TYPE_STRING)));

	protected static final List<WoField> FIXEDFIELD_APPINFO = ListUtils
			.unmodifiableList(Arrays.asList(new WoField(Indexs.FIELD_TITLE, "标题", Indexs.FIELD_TYPE_STRING),
					new WoField(Indexs.FIELD_CREATORPERSON, "创建者", Indexs.FIELD_TYPE_STRING),
					new WoField(Indexs.FIELD_CREATORUNIT, "部门", Indexs.FIELD_TYPE_STRING),
					new WoField(Indexs.FIELD_CREATETIME, "创建时间", Indexs.FIELD_TYPE_DATE),
					new WoField(Indexs.FIELD_UPDATETIME, "更新时间", Indexs.FIELD_TYPE_DATE),
					new WoField(Indexs.FIELD_CATEGORYNAME, "分类", Indexs.FIELD_TYPE_STRING),
					new WoField(Indexs.FIELD_DESCRIPTION, "说明", Indexs.FIELD_TYPE_STRING)));

	private static final String[] QUERY_IGNORES = new String[] { ":", "\"", "-", "+", "[", "]", "*", "?", "\\", "/",
			"(", ")", "~", "!", "{", "}", "^" };
	private static final String[] QUERY_IGNOREREPLACES = new String[] { "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "" };

	public static final String DIRECTORY_SEARCH = "search";

	public static final Integer DEFAULT_MAX_HITS = 1000000;

	public static String alignQuery(String query) {
		return StringUtils.replaceEach(query, QUERY_IGNORES, QUERY_IGNOREREPLACES);
	}

	public static Integer rows(Integer size) throws Exception {
		if (NumberTools.nullOrLessThan(size, 1)) {
			return Config.query().index().getSearchPageSize();
		} else {
			return (NumberTools.nullOrGreaterThan(size, Config.query().index().getSearchMaxPageSize())
					? Config.query().index().getSearchMaxPageSize()
					: size);
		}
	}

	public static Integer start(Integer page, int rows) {
		return (NumberTools.nullOrLessThan(page, 1) ? 0 : page - 1) * rows;
	}

	public static boolean deleteDirectory(String category, String key) {
		return deleteDirectory(category + "_" + key);
	}

	public static boolean deleteDirectory(String dir) {
		try {
			if (StringUtils.equals(Config.query().index().getMode(), Query.Index.MODE_HDFSDIRECTORY)) {
				org.apache.hadoop.conf.Configuration configuration = hdfsConfiguration();
				org.apache.hadoop.fs.Path path = hdfsBase();
				path = new org.apache.hadoop.fs.Path(path, dir);
				try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem.get(configuration)) {
					if (fileSystem.exists(path) && fileSystem.getFileStatus(path).isDirectory()) {
						return fileSystem.delete(path, true);
					}
				}
			} else {
				java.nio.file.Path path = null;
				if (StringUtils.equals(Config.query().index().getMode(), Query.Index.MODE_SHAREDDIRECTORY)) {
					path = Paths.get(Config.query().index().getSharedDirectoryPath()).resolve(dir);
				} else {
					path = Config.path_local_repository_index(true).resolve(dir);
				}
				if (Files.exists(path) && Files.isDirectory(path)) {
					Files.walkFileTree(path, new SimpleFileVisitor<java.nio.file.Path>() {
						@Override
						public FileVisitResult postVisitDirectory(java.nio.file.Path dir, IOException exc)
								throws IOException {
							Files.delete(dir);
							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult visitFile(java.nio.file.Path file, BasicFileAttributes attrs)
								throws IOException {
							Files.delete(file);
							return FileVisitResult.CONTINUE;
						}
					});
					return true;
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public static Optional<Directory> directory(String dir, boolean checkExists) {
		try {
			if (StringUtils.equals(Config.query().index().getMode(), Query.Index.MODE_HDFSDIRECTORY)) {
				org.apache.hadoop.conf.Configuration configuration = hdfsConfiguration();
				org.apache.hadoop.fs.Path path = hdfsBase();
				path = new org.apache.hadoop.fs.Path(path, dir);
				if (checkExists) {
					try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem
							.get(configuration)) {
						if (!fileSystem.exists(path)) {
							return Optional.empty();
						}
					}
				}
				return Optional.of(new HdfsDirectory(path, configuration));
			} else {
				java.nio.file.Path path = null;
				if (StringUtils.equals(Config.query().index().getMode(), Query.Index.MODE_SHAREDDIRECTORY)) {
					path = Paths.get(Config.query().index().getSharedDirectoryPath()).resolve(dir);

				} else {
					path = Config.path_local_repository_index(true).resolve(dir);
				}
				if (checkExists && (!Files.exists(path))) {
					return Optional.empty();
				}
				return Optional.of(FSDirectory.open(path));
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return Optional.empty();
	}

	public static Optional<Directory> directory(String category, String key, boolean checkExists) {
		return directory(category + "_" + key, checkExists);
	}

	private static org.apache.hadoop.conf.Configuration hdfsConfiguration() throws Exception {
		org.apache.hadoop.conf.Configuration configuration = new org.apache.hadoop.conf.Configuration();
		configuration.set(CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY,
				Config.query().index().getHdfsDirectoryDefaultFS());
		return configuration;
	}

	private static org.apache.hadoop.fs.Path hdfsBase() throws IllegalArgumentException, Exception {
		return new org.apache.hadoop.fs.Path(Config.query().index().getHdfsDirectoryPath());
	}

	public static List<String> directories(String category) {
		return directories().stream().filter(o -> StringUtils.startsWith(o, category + "_"))
				.collect(Collectors.toList());
	}

	public static List<String> directories() {
		try {
			if (StringUtils.equals(Config.query().index().getMode(), Query.Index.MODE_HDFSDIRECTORY)) {
				return directoriesHdfs();
			} else {
				java.nio.file.Path path = null;
				if (StringUtils.equals(Config.query().index().getMode(), Query.Index.MODE_SHAREDDIRECTORY)) {
					path = Paths.get(Config.query().index().getSharedDirectoryPath());
				} else {
					path = Config.path_local_repository_index(true);
				}
				if (Files.exists(path)) {
					return directoriesPath(path);
				}
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return new ArrayList<>();
	}

	private static List<String> directoriesPath(final java.nio.file.Path path) throws IOException {
		List<String> list = new ArrayList<>();
		try (Stream<java.nio.file.Path> stream = Files.walk(path, 1)) {
			stream.filter(o -> {
				try {
					return !Files.isSameFile(o, path);
				} catch (IOException e) {
					LOGGER.error(e);
				}
				return false;
			}).filter(Files::isDirectory).map(path::relativize).map(java.nio.file.Path::toString).forEach(list::add);
		}
		return list;
	}

	private static List<String> directoriesHdfs() throws Exception {
		List<String> list = new ArrayList<>();
		org.apache.hadoop.conf.Configuration configuration = hdfsConfiguration();
		try (org.apache.hadoop.fs.FileSystem fileSystem = org.apache.hadoop.fs.FileSystem.get(configuration)) {
			org.apache.hadoop.fs.Path path = hdfsBase();
			if (fileSystem.exists(path)) {
				RemoteIterator<LocatedFileStatus> fileStatusListIterator = fileSystem.listLocatedStatus(path);
				while (fileStatusListIterator.hasNext()) {
					LocatedFileStatus locatedFileStatus = fileStatusListIterator.next();
					if (locatedFileStatus.isDirectory()) {
						list.add(locatedFileStatus.getPath().getName());
					}
				}
			}
		}
		return list;
	}

	public static Optional<org.apache.lucene.search.Query> readersQuery(Collection<String> readers) {
		if ((null == readers) || readers.isEmpty()) {
			return Optional.empty();
		}
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		readers.stream().filter(StringUtils::isNotBlank).map(o -> new TermQuery(new Term(FIELD_READERS, o)))
				.forEach(o -> builder.add(o, BooleanClause.Occur.SHOULD));
		return Optional.of(builder.build());
	}

	public static List<org.apache.lucene.search.Query> filterQueries(List<Filter> filters) {
		List<org.apache.lucene.search.Query> list = new ArrayList<>();
		if (ListTools.isEmpty(filters)) {
			return list;
		}
		filters.stream().map(Indexs::fitlerQuery).filter(Optional::isPresent).forEach(o -> list.add(o.get()));
		return list;
	}

	private static Optional<org.apache.lucene.search.Query> fitlerQuery(Filter filter) {
		if (ListTools.isEmpty(filter.getValueList()) && StringUtils.isEmpty(filter.getMin())
				&& StringUtils.isEmpty(filter.getMax())) {
			return Optional.empty();
		}
		if (filter.getField().startsWith(PREFIX_FIELD_DATA_DATE)) {
			if (!(StringUtils.isEmpty(filter.getMin()) && StringUtils.isEmpty(filter.getMax()))) {
				return Optional.of(LongPoint.newRangeQuery(filter.getField(),
						stringOfDateToLongElseMin(filter.getMin()), stringOfDateToLongElseMax(filter.getMax())));
			} else {
				return Optional.empty();
			}
		}
		if (filter.getField().startsWith(PREFIX_FIELD_DATA_NUMBER)) {
			if (!(StringUtils.isEmpty(filter.getMin()) && StringUtils.isEmpty(filter.getMax()))) {
				return Optional.of(LongPoint.newRangeQuery(filter.getField(),
						stringOfNumberToLongElseMin(filter.getMin()), stringOfNumberToLongElseMax(filter.getMax())));
			} else {
				return Optional.empty();
			}
		}
		if (filter.getField().startsWith(PREFIX_FIELD_DATA_BOOLEAN)) {
			String value = StringUtils.equalsIgnoreCase(filter.getValueList().get(0), Indexs.BOOLEAN_TRUE_STRING_VALUE)
					? BOOLEAN_TRUE_STRING_VALUE
					: BOOLEAN_FALSE_STRING_VALUE;
			return Optional.of(new TermQuery(new Term(filter.getField(), value)));
		}
		BooleanQuery.Builder builder = new BooleanQuery.Builder();
		filter.getValueList().stream().filter(StringUtils::isNotBlank)
				.map(o -> new TermQuery(new Term(filter.getField(), o)))
				.forEach(o -> builder.add(o, BooleanClause.Occur.SHOULD));
		return Optional.of(builder.build());
	}

	private static Long stringOfDateToLongElseMin(String text) {
		try {
			Date date = DateTools.parse(text);
			return date.getTime();
		} catch (Exception e) {
			LOGGER.error(new IllegalArgumentException("text:" + text + " can not parse to date.", e));
		}
		return Long.MIN_VALUE;
	}

	private static Long stringOfDateToLongElseMax(String text) {
		try {
			Date date = DateTools.parse(text);
			return date.getTime();
		} catch (Exception e) {
			LOGGER.error(new IllegalArgumentException("text:" + text + " can not parse to date.", e));
		}
		return Long.MAX_VALUE;
	}

	private static Long stringOfNumberToLongElseMin(String text) {
		Double value = NumberUtils.toDouble(text, Double.MIN_VALUE);
		return org.apache.lucene.util.NumericUtils.doubleToSortableLong(value.doubleValue());
	}

	private static Long stringOfNumberToLongElseMax(String text) {
		Double value = NumberUtils.toDouble(text, Double.MAX_VALUE);
		return org.apache.lucene.util.NumericUtils.doubleToSortableLong(value.doubleValue());
	}

	public static List<String> adjustFacetField(List<String> categories, List<String> filters) {
		List<String> list = FACET_FIELDS.stream().filter(o -> (!filters.contains(o))).collect(Collectors.toList());
		if (filters.contains(FIELD_PROCESSNAME)) {
			list.removeAll(Arrays.asList(FIELD_APPLICATIONNAME, FIELD_PROCESSNAME, FIELD_APPNAME, FIELD_CATEGORYNAME));
		}
		if (filters.contains(FIELD_APPLICATIONNAME)) {
			list.removeAll(Arrays.asList(FIELD_APPLICATIONNAME, FIELD_APPNAME, FIELD_CATEGORYNAME));
		}
		if (filters.contains(FIELD_CATEGORYNAME)) {
			list.removeAll(Arrays.asList(FIELD_APPNAME, FIELD_CATEGORYNAME, FIELD_APPLICATIONNAME, FIELD_PROCESSNAME));
		}
		if (filters.contains(FIELD_APPNAME)) {
			list.removeAll(Arrays.asList(FIELD_APPNAME, FIELD_APPLICATIONNAME, FIELD_PROCESSNAME));
		}
		if (filters.contains(FIELD_COMPLETED)) {
			list.remove(FIELD_COMPLETED);
		}
		if (!ListTools.contains(categories, CATEGORY_PROCESSPLATFORM)) {
			list.removeAll(Arrays.asList(FIELD_APPLICATIONNAME, FIELD_PROCESSNAME, FIELD_COMPLETED));
		}
		if (!ListTools.contains(categories, CATEGORY_CMS)) {
			list.removeAll(Arrays.asList(FIELD_APPNAME, FIELD_CATEGORYNAME));
		}
		return list;
	}

	/**
	 * 判断字段属性.
	 * 
	 * @param field
	 * @return 名称,显示名称,类型
	 */

	public static Triple<String, String, String> judgeField(String field) {
		if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_DATA)) {
			return judgeFieldData(field);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM)) {
			return judgeFieldProcessPlatform(field);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_CMS)) {
			return judgeFieldCms(field);
		} else if (FIXED_DATE_FIELDS.contains(field)) {
			return Triple.of(field, field, Indexs.FIELD_TYPE_DATE);
		} else {
			return Triple.of(field, field, Indexs.FIELD_TYPE_STRING);
		}
	}

	private static Triple<String, String, String> judgeFieldData(String field) {
		if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_DATA_STRING)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_DATA_STRING),
					Indexs.FIELD_TYPE_STRING);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_DATA_DATE)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_DATA_DATE),
					Indexs.FIELD_TYPE_DATE);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_DATA_NUMBER)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_DATA_NUMBER),
					Indexs.FIELD_TYPE_NUMBER);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_DATA_BOOLEAN)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_DATA_BOOLEAN),
					Indexs.FIELD_TYPE_BOOLEAN);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_DATA_STRINGS)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_DATA_STRINGS),
					Indexs.FIELD_TYPE_STRINGS);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_DATA_DATES)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_DATA_DATES),
					Indexs.FIELD_TYPE_DATES);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_DATA_NUMBERS)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_DATA_NUMBERS),
					Indexs.FIELD_TYPE_NUMBERS);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_DATA_BOOLEANS)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_DATA_BOOLEANS),
					Indexs.FIELD_TYPE_BOOLEANS);
		}
		return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_DATA_STRING),
				Indexs.FIELD_TYPE_STRING);
	}

	private static Triple<String, String, String> judgeFieldProcessPlatform(String field) {
		if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING),
					Indexs.FIELD_TYPE_STRING);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATE)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATE),
					Indexs.FIELD_TYPE_DATE);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBER)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBER),
					Indexs.FIELD_TYPE_NUMBER);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN),
					Indexs.FIELD_TYPE_BOOLEAN);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS),
					Indexs.FIELD_TYPE_STRINGS);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATES)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATES),
					Indexs.FIELD_TYPE_DATES);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBERS)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBERS),
					Indexs.FIELD_TYPE_NUMBERS);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEANS)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEANS),
					Indexs.FIELD_TYPE_BOOLEANS);
		}
		return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING),
				Indexs.FIELD_TYPE_STRING);
	}

	private static Triple<String, String, String> judgeFieldCms(String field) {
		if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_CMS_STRING)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_CMS_STRING),
					Indexs.FIELD_TYPE_STRING);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_CMS_DATE)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_CMS_DATE),
					Indexs.FIELD_TYPE_DATE);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_CMS_NUMBER)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_CMS_NUMBER),
					Indexs.FIELD_TYPE_NUMBER);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_CMS_BOOLEAN)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_CMS_BOOLEAN),
					Indexs.FIELD_TYPE_BOOLEAN);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_CMS_STRINGS)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_CMS_STRINGS),
					Indexs.FIELD_TYPE_STRINGS);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_CMS_DATES)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_CMS_DATES),
					Indexs.FIELD_TYPE_DATES);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_CMS_NUMBERS)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_CMS_NUMBERS),
					Indexs.FIELD_TYPE_NUMBERS);
		} else if (StringUtils.startsWith(field, Indexs.PREFIX_FIELD_CMS_BOOLEANS)) {
			return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_CMS_BOOLEANS),
					Indexs.FIELD_TYPE_BOOLEANS);
		}
		return Triple.of(field, StringUtils.substringAfter(field, Indexs.PREFIX_FIELD_CMS_STRING),
				Indexs.FIELD_TYPE_STRING);
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param <T>
	 * @param indexableFields 同名的所有字段列表,不能为空数组.
	 * @param fileType        字段类型
	 * @return
	 */
	public static <T> T indexableFieldValue(IndexableField[] indexableFields, String fileType) {
		if (StringUtils.equalsIgnoreCase(Indexs.FIELD_TYPE_DATES, fileType)) {
			return (T) Stream.of(indexableFields).map(IndexableField::numericValue).filter(o -> !Objects.isNull(o))
					.<Date>map(o -> new Date(o.longValue())).collect(Collectors.toList());
		} else if (StringUtils.equalsIgnoreCase(Indexs.FIELD_TYPE_NUMBERS, fileType)) {
			return (T) Stream.of(indexableFields).map(IndexableField::numericValue).filter(o -> !Objects.isNull(o))
					.<Double>map(o -> Double.valueOf(NumericUtils.sortableLongToDouble(o.longValue())))
					.collect(Collectors.toList());
		} else if (StringUtils.equalsIgnoreCase(Indexs.FIELD_TYPE_BOOLEANS, fileType)) {
			return (T) Stream.of(indexableFields).map(IndexableField::stringValue).filter(o -> !Objects.isNull(o))
					.<Boolean>map(o -> Boolean.valueOf(StringUtils.equalsIgnoreCase(o, BOOLEAN_TRUE_STRING_VALUE)))
					.collect(Collectors.toList());
		} else if (StringUtils.equalsIgnoreCase(Indexs.FIELD_TYPE_STRINGS, fileType)) {
			return (T) Stream.of(indexableFields).<String>map(IndexableField::stringValue)
					.filter(o -> !Objects.isNull(o)).collect(Collectors.toList());
		} else if (StringUtils.equalsIgnoreCase(Indexs.FIELD_TYPE_DATE, fileType)) {
			Number number = indexableFields[0].numericValue();
			return (null != number) ? (T) new Date(number.longValue()) : null;
		} else if (StringUtils.equalsIgnoreCase(Indexs.FIELD_TYPE_NUMBER, fileType)) {
			Number number = indexableFields[0].numericValue();
			return (null != number) ? (T) Double.valueOf(NumericUtils.sortableLongToDouble(number.longValue())) : null;
		} else if (StringUtils.equalsIgnoreCase(Indexs.FIELD_TYPE_BOOLEAN, fileType)) {
			String str = indexableFields[0].stringValue();
			return (T) Boolean.valueOf(StringUtils.equalsIgnoreCase(str, BOOLEAN_TRUE_STRING_VALUE));
		} else {
			String str = indexableFields[0].stringValue();
			return (null != str) ? (T) str : null;
		}
	}

	/**
	 * 根据传入的分类值,processPlatform 或者 cms 或者同时包含确定输出的固定字段.
	 * 
	 * @param list
	 * @return
	 */
	public List<WoField> getFixedFieldList(List<String> list) {
		List<WoField> woFields = new ArrayList<>();
		if (list.contains(Indexs.CATEGORY_PROCESSPLATFORM)) {
			woFields.addAll(FIXEDFIELD_APPLICATION);
		} else if (list.contains(Indexs.CATEGORY_CMS)) {
			woFields.addAll(FIXEDFIELD_APPINFO);
		}
		return ListTools.trim(woFields, true, true);
	}

	/**
	 * 根据directory对象获取reader
	 * 
	 * @param dirs
	 * @return
	 */
	public static IndexReader[] indexReaders(List<com.x.query.core.express.index.Directory> dirs) {
		return dirs.stream().map(o -> Indexs.directory(o.getCategory(), o.getKey(), true)).filter(Optional::isPresent)
				.map(Optional::get).map(o -> {
					try {
						return DirectoryReader.open(o);
					} catch (IOException e) {
						LOGGER.error(e);
					}
					return null;
				}).filter(o -> !Objects.isNull(o)).toArray(s -> new IndexReader[s]);
	}

}
