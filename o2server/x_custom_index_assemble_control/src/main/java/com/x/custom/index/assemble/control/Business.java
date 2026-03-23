package com.x.custom.index.assemble.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.PointValues;
import org.apache.lucene.util.NumericUtils;

import com.google.common.collect.ImmutableMap;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.bean.tuple.Quadruple;
import com.x.base.core.project.bean.tuple.Triple;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.Document;
import com.x.custom.index.assemble.control.factory.AppInfoFactory;
import com.x.custom.index.assemble.control.factory.ApplicationFactory;
import com.x.custom.index.assemble.control.factory.CategoryInfoFactory;
import com.x.custom.index.assemble.control.factory.IndexFactory;
import com.x.custom.index.assemble.control.factory.PersonFactory;
import com.x.custom.index.assemble.control.factory.ProcessFactory;
import com.x.custom.index.core.entity.Reveal;
import com.x.organization.core.entity.Person;
import com.x.organization.core.express.Organization;
import com.x.processplatform.core.entity.content.TaskCompleted;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;
import com.x.processplatform.core.entity.element.Application;
import com.x.query.core.express.index.Indexs;
import com.x.query.core.express.index.WoField;

public class Business {

	private static final Logger LOGGER = LoggerFactory.getLogger(Business.class);

	private CacheCategory personCache;

	public CacheCategory personCache() {
		return this.personCache;
	}

	// 自定义应用配置文件名称，根据实际配置修改，文件要求为json格式
//	public static final String PATH_CONFIG_CUSTOM = "config/customIndex.json";
//	public static final String PATH_CONFIGSAMPLE_CUSTOM = "configSample/customIndex.json";

//	private static final String[] QUERY_IGNORES = new String[] { "[", "]", "*", "?" };
//	private static final String[] QUERY_IGNOREREPLACES = new String[] { "", "", "", "" };

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

	private static final Map<String, String> PROCESSPLATFORM_FIELDNAME = ImmutableMap.<String, String>builder()
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN + TaskCompleted.completed_FIELDNAME, "已完成")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.creatorUnitLevelName_FIELDNAME, "创建者部门层级名")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.application_FIELDNAME, "应用标识")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.applicationName_FIELDNAME, "应用名称")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.applicationAlias_FIELDNAME, "应用别名")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.process_FIELDNAME, "流程标识")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.processName_FIELDNAME, "流程名称")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.processAlias_FIELDNAME, "流程别名")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.job_FIELDNAME, "任务")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + WorkCompleted.serial_FIELDNAME, "编号")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN + WorkCompleted.expired_FIELDNAME, "超时")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING + Work.activityName_FIELDNAME, "活动环节")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATE + WorkCompleted.expireTime_FIELDNAME, "截至时间")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS + Indexs.FIELD_ROCESSPLATFORM_TASKPERSONNAMES, "当前处理人")
			.put(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS + Indexs.FIELD_ROCESSPLATFORM_PREVTASKPERSONNAMES, "前序处理人")
			.build();

	private static final Map<String, String> CMS_FIELDNAME = ImmutableMap.<String, String>builder()
			.put(Indexs.PREFIX_FIELD_CMS_STRING + Document.appId_FIELDNAME, "栏目标识")
			.put(Indexs.PREFIX_FIELD_CMS_STRING + Document.appName_FIELDNAME, "栏目名称")
			.put(Indexs.PREFIX_FIELD_CMS_STRING + Document.appAlias_FIELDNAME, "栏目别名")
			.put(Indexs.PREFIX_FIELD_CMS_STRING + Document.categoryId_FIELDNAME, "分类标识")
			.put(Indexs.PREFIX_FIELD_CMS_STRING + Document.categoryName_FIELDNAME, "分类名称")
			.put(Indexs.PREFIX_FIELD_CMS_STRING + Document.categoryAlias_FIELDNAME, "分类别名")
			.put(Indexs.PREFIX_FIELD_CMS_STRING + Document.description_FIELDNAME, "说明")
			.put(Indexs.PREFIX_FIELD_CMS_DATE + Document.publishTime_FIELDNAME, "发布时间")
			.put(Indexs.PREFIX_FIELD_CMS_DATE + Document.modifyTime_FIELDNAME, "修改时间").build();

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) {
		this.emc = emc;
		this.personCache = new CacheCategory(Person.class);
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	// 组织架构管理相关的工厂服务类
	private Organization organization;

	public Organization organization() {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	private AppInfoFactory appInfo;

	public AppInfoFactory appInfo() throws Exception {
		if (null == this.appInfo) {
			this.appInfo = new AppInfoFactory(this);
		}
		return appInfo;
	}

	private CategoryInfoFactory categoryInfo;

	public CategoryInfoFactory categoryInfo() throws Exception {
		if (null == this.categoryInfo) {
			this.categoryInfo = new CategoryInfoFactory(this);
		}
		return categoryInfo;
	}

	private ProcessFactory process;

	public ProcessFactory process() throws Exception {
		if (null == this.process) {
			this.process = new ProcessFactory(this);
		}
		return process;
	}

	private ApplicationFactory application;

	public ApplicationFactory application() throws Exception {
		if (null == this.application) {
			this.application = new ApplicationFactory(this);
		}
		return application;
	}

	private IndexFactory index;

	public IndexFactory index() throws Exception {
		if (null == this.application) {
			this.index = new IndexFactory(this);
		}
		return index;
	}

	private PersonFactory person;

	public PersonFactory person() throws Exception {
		if (null == this.person) {
			this.person = new PersonFactory(this);
		}
		return person;
	}

	private static final CacheCategory applicationAppInfoCacheCategory = new CacheCategory(Application.class,
			AppInfo.class);
//	private static final CacheCategory organizationCacheCategory = new CacheCategory(Person.class, Group.class,
//			Unit.class);
	private static final CacheCategory revealCacheCategory = new CacheCategory(Reveal.class);

	private static final CacheKey applicationCacheKey = new CacheKey(Application.class);
	private static final CacheKey appInfoCacheKey = new CacheKey(AppInfo.class);
	private static final CacheKey revealCacheKey = new CacheKey(Reveal.class);

	@SuppressWarnings("unchecked")
	public Pair<List<Application>, List<AppInfo>> listApplicationAppInfo() throws Exception {
		Optional<?> applicationOptional = CacheManager.get(applicationAppInfoCacheCategory, applicationCacheKey);
		Optional<?> appInfoOptional = CacheManager.get(applicationAppInfoCacheCategory, appInfoCacheKey);
		List<Application> applications = null;
		List<AppInfo> appInfos = null;
		if (applicationOptional.isPresent()) {
			applications = (List<Application>) applicationOptional.get();
		} else {
			applications = list(Application.class);
			CacheManager.put(applicationAppInfoCacheCategory, applicationCacheKey, applications);
		}
		if (appInfoOptional.isPresent()) {
			appInfos = (List<AppInfo>) appInfoOptional.get();
		} else {
			appInfos = list(AppInfo.class);
			CacheManager.put(applicationAppInfoCacheCategory, appInfoCacheKey, appInfos);
		}
		return Pair.of(applications, appInfos);
	}

	@SuppressWarnings("unchecked")
	public Triple<List<Application>, List<AppInfo>, List<Reveal>> listApplicationAppInfoReveal() throws Exception {
		Optional<?> revealOptional = CacheManager.get(revealCacheCategory, revealCacheKey);
		List<Reveal> list;
		if (revealOptional.isPresent()) {
			list = (List<Reveal>) revealOptional.get();
		} else {
			list = list(Reveal.class);
			CacheManager.put(revealCacheCategory, revealCacheKey, list);
		}
		return Triple.of(listApplicationAppInfo(), list);
	}

	private <T extends JpaObject> List<T> list(Class<T> cls) throws Exception {
		EntityManager em = this.entityManagerContainer().get(cls);
		List<T> os = this.entityManagerContainer().listAll(cls);
		List<T> list = new ArrayList<>();
		for (T o : os) {
			em.detach(o);
			list.add(o);
		}
		return ListUtils.unmodifiableList(list);
	}

//	@SuppressWarnings("unchecked")
//	public Quadruple<List<String>, List<String>, List<String>, List<String>> listIdentityUnitGroupRole(
//			EffectivePerson effectivePerson) throws Exception {
//		CacheKey identityCacheKey = new CacheKey(effectivePerson.getDistinguishedName(), "identity");
//		CacheKey unitCacheKey = new CacheKey(effectivePerson.getDistinguishedName(), "unit");
//		CacheKey groupCacheKey = new CacheKey(effectivePerson.getDistinguishedName(), "group");
//		CacheKey roleCacheKey = new CacheKey(effectivePerson.getDistinguishedName(), "role");
//		Optional<?> identityOptional = CacheManager.get(organizationCacheCategory, identityCacheKey);
//		Optional<?> unitOptional = CacheManager.get(organizationCacheCategory, unitCacheKey);
//		Optional<?> groupOptional = CacheManager.get(organizationCacheCategory, groupCacheKey);
//		Optional<?> roleOptional = CacheManager.get(organizationCacheCategory, roleCacheKey);
//		List<String> identities;
//		List<String> units;
//		List<String> groups;
//		List<String> roles;
//		if (identityOptional.isPresent()) {
//			identities = (List<String>) identityOptional.get();
//		} else {
//			identities = this.organization().identity().listWithPerson(effectivePerson);
//			CacheManager.put(organizationCacheCategory, identityCacheKey, identities);
//		}
//		if (unitOptional.isPresent()) {
//			units = (List<String>) unitOptional.get();
//		} else {
//			units = this.organization().unit().listWithPerson(effectivePerson);
//			CacheManager.put(organizationCacheCategory, unitCacheKey, units);
//		}
//		if (groupOptional.isPresent()) {
//			groups = (List<String>) groupOptional.get();
//		} else {
//			groups = this.organization().group().listWithPerson(effectivePerson);
//			CacheManager.put(organizationCacheCategory, groupCacheKey, groups);
//		}
//		if (roleOptional.isPresent()) {
//			roles = (List<String>) roleOptional.get();
//		} else {
//			roles = this.organization().role().listWithPerson(effectivePerson);
//			CacheManager.put(organizationCacheCategory, roleCacheKey, roles);
//		}
//		return Quadruple.of(identities, units, groups, roles);
//	}

	public Pair<List<Application>, List<AppInfo>> filterEditable(EffectivePerson effectivePerson,
			Pair<List<Application>, List<AppInfo>> pair,
			Quadruple<List<String>, List<String>, List<String>, List<String>> identityUnitGroupRole) {
		if (effectivePerson.isManager() || effectivePerson.isCipher()) {
			return pair;
		}
		List<Application> applications = filterApplicationEditable(effectivePerson, pair.first(),
				identityUnitGroupRole);
		List<AppInfo> appInfos = filterAppInfoEditable(effectivePerson, pair.second(), identityUnitGroupRole);
		return Pair.of(applications, appInfos);
	}

	private List<Application> filterApplicationEditable(EffectivePerson effectivePerson, List<Application> list,
			Quadruple<List<String>, List<String>, List<String>, List<String>> identityUnitGroupRole) {
		if (CollectionUtils.containsAny(identityUnitGroupRole.fourth(),
				OrganizationDefinition.ProcessPlatformManager)) {
			return list;
		}
		return list.stream()
				.filter(o -> CollectionUtils.containsAny(o.getControllerList(), effectivePerson.getDistinguishedName()))
				.collect(Collectors.toList());
	}

	private List<AppInfo> filterAppInfoEditable(EffectivePerson effectivePerson, List<AppInfo> list,
			Quadruple<List<String>, List<String>, List<String>, List<String>> identityUnitGroupRole) {
		if (CollectionUtils.containsAny(identityUnitGroupRole.fourth(), OrganizationDefinition.CMSManager)) {
			return list;
		}
		return list.stream().filter(
				o -> CollectionUtils.containsAny(o.getManageablePersonList(), effectivePerson.getDistinguishedName())
						|| CollectionUtils.containsAny(identityUnitGroupRole.second(), o.getManageableUnitList())
						|| CollectionUtils.containsAny(identityUnitGroupRole.third(), o.getManageableGroupList()))
				.collect(Collectors.toList());
	}

	/**
	 * 根据输入的类型processPlatform ,cms 或者两者都包含确定输出的固定字段
	 * 
	 * @param list
	 * @return
	 */
	public static List<WoField> listFixedField(List<String> list) {
		List<WoField> woFields = new ArrayList<>();
		if (list.contains(Indexs.CATEGORY_PROCESSPLATFORM)) {
			woFields.addAll(FIXEDFIELD_APPLICATION);
		} else if (list.contains(Indexs.CATEGORY_CMS)) {
			woFields.addAll(FIXEDFIELD_APPINFO);
		}
		return ListTools.trim(woFields, true, true);
	}

	/**
	 * 获取动态字段
	 * 
	 * @param categories
	 * @param reader
	 * @return
	 */
	public static List<WoField> listDynamicField(List<String> categories, IndexReader reader) {
		String[] names = listFieldNamesWithCategory(categories);
		List<WoField> list = org.apache.lucene.luke.models.util.IndexUtils.getFieldNames(reader).stream()
				.filter(o -> StringUtils.startsWithAny(o, names)).distinct()
				.map(o -> org.apache.lucene.luke.models.util.IndexUtils.getFieldInfo(reader, o)).map(o -> {
					// LegacyNumericUtils.
					WoField woField = new WoField();
					woField.setField(o.getName());
					setDynamicFieldType(woField);
					return woField;
				}).collect(Collectors.toList());
		List<WoField> os = new ArrayList<>();
		list.stream()
				.filter(o -> StringUtils.equalsAny(o.getFieldType(), Indexs.FIELD_TYPE_NUMBER, Indexs.FIELD_TYPE_DATE))
				.forEach(o -> setMinMax(reader, o));
		list.stream().map(o -> {
			Integer order = null;
			if (StringUtils.startsWith(o.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM)) {
				order = 0;
			} else {
				order = StringUtils.startsWith(o.getField(), Indexs.PREFIX_FIELD_CMS) ? 1 : 2;
			}
			return Pair.of(order, o);
		}).collect(Collectors.groupingBy(Pair::first)).entrySet().stream()
				.sorted(Comparator.comparing(Map.Entry::getKey))
				.forEach(o -> o.getValue().stream().map(Pair::second).map(p ->
				// 排序要考虑是否是汉字,避免汉字,拉丁字母混排,先分组排序再合并
				(StringUtils.isNotEmpty(p.getName()) && Character.UnicodeBlock
						.of(p.getName().charAt(0)) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) ? Pair.of(0, p)
								: Pair.of(1, p))
						.collect(Collectors.groupingBy(Pair::first)).entrySet().stream()
						.sorted(Comparator.comparing(Map.Entry::getKey))
						.forEach(p -> p.getValue().stream().map(Pair::second)
								.sorted(Comparator.nullsLast(Comparator.comparing(WoField::getName)))
								.forEach(os::add)));
		return os;
	}

	private static void setDynamicFieldType(WoField woField) {
		if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA)) {
			setDynamicFieldTypeData(woField);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM)) {
			setDynamicFieldTypeProcessPlatform(woField);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS)) {
			setDynamicFieldTypeCms(woField);
		}
	}

	private static void setMinMax(IndexReader reader, WoField o) {
		try {
			Long maxLong = null;
			Long minLong = null;
			byte[] max = PointValues.getMaxPackedValue(reader, o.getField());
			if (null != max) {
				maxLong = org.apache.lucene.util.NumericUtils.sortableBytesToLong(max, 0);
			}
			byte[] min = PointValues.getMinPackedValue(reader, o.getField());
			if (null != min) {
				minLong = org.apache.lucene.util.NumericUtils.sortableBytesToLong(min, 0);
			}
			if ((null != maxLong) || (null != minLong)) {
				if (StringUtils.equalsIgnoreCase(o.getFieldType(), Indexs.FIELD_TYPE_NUMBER)) {
					setMinMaxNumber(o, maxLong, minLong);
				}
				if (StringUtils.equalsIgnoreCase(o.getFieldType(), Indexs.FIELD_TYPE_DATE)) {
					setMinMaxDate(o, maxLong, minLong);
				}
			}
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}

	private static void setMinMaxDate(WoField o, Long maxLong, Long minLong) {
		if (null != maxLong) {
			o.setMax(DateTools.format(new Date(maxLong)));
		}
		if (null != minLong) {
			o.setMin(DateTools.format(new Date(minLong)));
		}
	}

	private static void setMinMaxNumber(WoField o, Long maxLong, Long minLong) {
		if (null != maxLong) {
			o.setMax(NumericUtils.sortableLongToDouble(maxLong));
		}
		if (null != minLong) {
			o.setMin(NumericUtils.sortableLongToDouble(minLong));
		}
	}

	private static void setDynamicFieldTypeData(WoField woField) {
		if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_STRING)) {
			woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_STRING));
			woField.setFieldType(Indexs.FIELD_TYPE_STRING);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_STRINGS)) {
			woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_STRINGS));
			woField.setFieldType(Indexs.FIELD_TYPE_STRINGS);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_BOOLEAN)) {
			woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_BOOLEAN));
			woField.setFieldType(Indexs.FIELD_TYPE_BOOLEAN);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_BOOLEANS)) {
			woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_BOOLEANS));
			woField.setFieldType(Indexs.FIELD_TYPE_BOOLEANS);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_NUMBER)) {
			woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_NUMBER));
			woField.setFieldType(Indexs.FIELD_TYPE_NUMBER);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_NUMBERS)) {
			woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_NUMBERS));
			woField.setFieldType(Indexs.FIELD_TYPE_NUMBERS);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_DATE)) {
			woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_DATE));
			woField.setFieldType(Indexs.FIELD_TYPE_DATE);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_DATA_DATES)) {
			woField.setName(StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_DATA_DATES));
			woField.setFieldType(Indexs.FIELD_TYPE_DATES);
		}
	}

	private static void setDynamicFieldTypeProcessPlatform(WoField woField) {
		if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING)) {
			woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING)));
			woField.setFieldType(Indexs.FIELD_TYPE_STRING);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS)) {
			woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS)));
			woField.setFieldType(Indexs.FIELD_TYPE_STRINGS);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN)) {
			woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN)));
			woField.setFieldType(Indexs.FIELD_TYPE_BOOLEAN);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEANS)) {
			woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEANS)));
			woField.setFieldType(Indexs.FIELD_TYPE_BOOLEANS);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBER)) {
			woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBER)));
			woField.setFieldType(Indexs.FIELD_TYPE_NUMBER);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBERS)) {
			woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBERS)));
			woField.setFieldType(Indexs.FIELD_TYPE_NUMBERS);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATE)) {
			woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATE)));
			woField.setFieldType(Indexs.FIELD_TYPE_DATE);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATES)) {
			woField.setName(PROCESSPLATFORM_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATES)));
			woField.setFieldType(Indexs.FIELD_TYPE_DATES);
		}
	}

	private static void setDynamicFieldTypeCms(WoField woField) {
		if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_STRING)) {
			woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_STRING)));
			woField.setFieldType(Indexs.FIELD_TYPE_STRING);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_STRINGS)) {
			woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_STRINGS)));
			woField.setFieldType(Indexs.FIELD_TYPE_STRINGS);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_BOOLEAN)) {
			woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_BOOLEAN)));
			woField.setFieldType(Indexs.FIELD_TYPE_BOOLEAN);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_BOOLEANS)) {
			woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_BOOLEANS)));
			woField.setFieldType(Indexs.FIELD_TYPE_BOOLEANS);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_NUMBER)) {
			woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_NUMBER)));
			woField.setFieldType(Indexs.FIELD_TYPE_NUMBER);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_NUMBERS)) {
			woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_NUMBERS)));
			woField.setFieldType(Indexs.FIELD_TYPE_NUMBERS);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_DATE)) {
			woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_DATE)));
			woField.setFieldType(Indexs.FIELD_TYPE_DATE);
		} else if (StringUtils.startsWith(woField.getField(), Indexs.PREFIX_FIELD_CMS_DATES)) {
			woField.setName(CMS_FIELDNAME.getOrDefault(woField.getField(),
					StringUtils.substringAfter(woField.getField(), Indexs.PREFIX_FIELD_CMS_DATES)));
			woField.setFieldType(Indexs.FIELD_TYPE_DATES);
		}
	}

	private static String[] listFieldNamesWithCategory(List<String> categories) {
		List<String> names = new ArrayList<>();
		if (categories.contains(Indexs.CATEGORY_PROCESSPLATFORM)) {
			names.addAll(Arrays.asList(Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRING,
					Indexs.PREFIX_FIELD_PROCESSPLATFORM_STRINGS, Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEAN,
					Indexs.PREFIX_FIELD_PROCESSPLATFORM_BOOLEANS, Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBER,
					Indexs.PREFIX_FIELD_PROCESSPLATFORM_NUMBERS, Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATE,
					Indexs.PREFIX_FIELD_PROCESSPLATFORM_DATES));
		}
		if (categories.contains(Indexs.CATEGORY_CMS)) {
			names.addAll(Arrays.asList(Indexs.PREFIX_FIELD_CMS_STRING, Indexs.PREFIX_FIELD_CMS_STRINGS,
					Indexs.PREFIX_FIELD_CMS_BOOLEAN, Indexs.PREFIX_FIELD_CMS_BOOLEANS, Indexs.PREFIX_FIELD_CMS_NUMBER,
					Indexs.PREFIX_FIELD_CMS_NUMBERS, Indexs.PREFIX_FIELD_CMS_DATE, Indexs.PREFIX_FIELD_CMS_DATES));
		}
		names.addAll(Arrays.asList(Indexs.PREFIX_FIELD_DATA_STRING, Indexs.PREFIX_FIELD_DATA_STRINGS,
				Indexs.PREFIX_FIELD_DATA_BOOLEAN, Indexs.PREFIX_FIELD_DATA_BOOLEANS, Indexs.PREFIX_FIELD_DATA_NUMBER,
				Indexs.PREFIX_FIELD_DATA_NUMBERS, Indexs.PREFIX_FIELD_DATA_DATE, Indexs.PREFIX_FIELD_DATA_DATES));
		return names.toArray(new String[] {});
	}

	public static List<String> categories(List<com.x.query.core.express.index.Directory> dirs) {
		if (ListTools.isEmpty(dirs)) {
			return new ArrayList<>();
		}
		return dirs.stream().map(com.x.query.core.express.index.Directory::getCategory).filter(StringUtils::isNotBlank)
				.collect(Collectors.toList());
	}

}
