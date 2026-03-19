package com.x.pan.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.express.Organization;
import com.x.pan.assemble.control.factory.*;
import com.x.pan.core.entity.FileConfig3;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 应用业务服务类
 * @author sword
 */
public class Business {

	private static final Logger logger = LoggerFactory.getLogger(Business.class);

	private final Cache.CacheCategory userCacheCategory = new Cache.CacheCategory(Unit.class, Identity.class, Group.class);

	public final Cache.CacheCategory configCacheCategory = new Cache.CacheCategory(FileConfig3.class);

	public static final String TEMP_FOLD = "docToPdf";

	public final static String TOP_FOLD = "$$TOP_FOLD";

	public final static String SYSTEM_CONFIG = "systemConfig";

	public static final String PERSON_SPLIT_FLAG = "@";

	public static final Integer ONLY_OFFICE_ERROR_CODE = 10001;

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Attachment2Factory attachment2;

	public Attachment2Factory attachment2() throws Exception {
		if (null == this.attachment2) {
			this.attachment2 = new Attachment2Factory(this);
		}
		return attachment2;
	}

	private Attachment3Factory attachment3;

	public Attachment3Factory attachment3() throws Exception {
		if (null == this.attachment3) {
			this.attachment3 = new Attachment3Factory(this);
		}
		return attachment3;
	}

	private Folder2Factory folder2;

	public Folder2Factory folder2() throws Exception {
		if (null == this.folder2) {
			this.folder2 = new Folder2Factory(this);
		}
		return folder2;
	}

	private Folder3Factory folder3;

	public Folder3Factory folder3() throws Exception {
		if (null == this.folder3) {
			this.folder3 = new Folder3Factory(this);
		}
		return folder3;
	}

	private OriginFileFactory originFile;

	public OriginFileFactory originFile() throws Exception {
		if (null == this.originFile) {
			this.originFile = new OriginFileFactory(this);
		}
		return originFile;
	}

	private ShareFactory share;

	public ShareFactory share() throws Exception {
		if (null == this.share) {
			this.share = new ShareFactory(this);
		}
		return share;
	}

	private Recycle3Factory recycle;

	public Recycle3Factory recycle() throws Exception {
		if (null == this.recycle) {
			this.recycle = new Recycle3Factory(this);
		}
		return recycle;
	}

	/**
	 * 组织架构管理相关的工厂服务类
	 */
	private Organization organization;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	public boolean controlAble(EffectivePerson effectivePerson) throws Exception {
		boolean result = false;
		if (effectivePerson.isManager()
				|| (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.FileManager))) {
			result = true;
		}
		return result;
	}

	/**
	 * 仅可读
	 * @param effectivePerson
	 * @param zoneId
	 * @return
	 * @throws Exception
	 */
	public boolean zoneViewable(EffectivePerson effectivePerson, String zoneId) throws Exception {
		boolean result = this.controlAble(effectivePerson);
		if(!result){
			result = this.folder3().isZoneViewer(zoneId, effectivePerson.getDistinguishedName());
		}
		return result;
	}

	/**
	 * 可读，可下载
	 * @param effectivePerson
	 * @param zoneId
	 * @return
	 * @throws Exception
	 */
	public boolean zoneReadable(EffectivePerson effectivePerson, String zoneId) throws Exception {
		boolean result = this.controlAble(effectivePerson);
		if(!result){
			result = this.folder3().isZoneReader(zoneId, effectivePerson.getDistinguishedName());
		}
		return result;
	}

	/**
	 * 上传权限
	 * @param effectivePerson
	 * @param zoneId
	 * @return
	 * @throws Exception
	 */
	public boolean zoneEditableToCreate(EffectivePerson effectivePerson, String zoneId) throws Exception {
		boolean result = this.controlAble(effectivePerson);
		if(!result){
			result = this.folder3().isZoneEditor(zoneId, effectivePerson.getDistinguishedName());
		}
		return result;
	}

	/**
	 * 增删改查权限
	 * @param effectivePerson
	 * @param zoneId
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean zoneEditable(EffectivePerson effectivePerson, String zoneId, String person) throws Exception {
		boolean result = this.controlAble(effectivePerson);
		if(!result){
			result = this.folder3().isZoneEditor(zoneId, effectivePerson.getDistinguishedName());
			if(result && StringUtils.isNotBlank(person)){
				boolean isZoneAdmin = this.folder3().isZoneAdmin(zoneId, effectivePerson.getDistinguishedName());
				if(!isZoneAdmin && !effectivePerson.getDistinguishedName().equals(person)){
					result = false;
				}
			}
		}
		return result;
	}

	public FileConfig3 getSystemConfig() throws Exception{
		Cache.CacheKey cacheKey = new Cache.CacheKey(FileConfig3.class, Business.SYSTEM_CONFIG);
		Optional<?> optional = CacheManager.get(configCacheCategory, cacheKey);
		FileConfig3 config;
		if(optional.isPresent()){
			config = (FileConfig3)optional.get();
		}else{
			config = entityManagerContainer().firstEqual(FileConfig3.class, FileConfig3.person_FIELDNAME, Business.SYSTEM_CONFIG);
			if(config != null){
				this.entityManagerContainer().get(FileConfig3.class).detach(config);
			}else{
				config = new FileConfig3();
				config.setProperties(null);
			}
			CacheManager.put(configCacheCategory, cacheKey, config);
		}
		return config;
	}

	public Integer verifyConstraint(String person, long usedSize) throws Exception{
		FileConfig3 config = null;
		Cache.CacheKey personCacheKey = new Cache.CacheKey(FileConfig3.class, person, Business.SYSTEM_CONFIG);
		Optional<?> optional = CacheManager.get(configCacheCategory, personCacheKey);
		if(optional.isPresent()){
			config = (FileConfig3)optional.get();
		}else{
			config = this.entityManagerContainer().firstEqual(FileConfig3.class, FileConfig3.person_FIELDNAME, person);
			if(config != null){
				this.entityManagerContainer().get(FileConfig3.class).detach(config);
			}else{
				Cache.CacheKey cacheKey = new Cache.CacheKey(FileConfig3.class, Business.SYSTEM_CONFIG);
				optional = CacheManager.get(configCacheCategory, cacheKey);
				if(optional.isPresent()){
					config = (FileConfig3)optional.get();
				}else{
					config = this.entityManagerContainer().firstEqual(FileConfig3.class, FileConfig3.person_FIELDNAME, Business.SYSTEM_CONFIG);
					if(config != null){
						this.entityManagerContainer().get(FileConfig3.class).detach(config);
					}else{
						config = new FileConfig3();
					}
					CacheManager.put(configCacheCategory, cacheKey, config);
				}
			}
			CacheManager.put(configCacheCategory, personCacheKey, config);
		}
		if (config != null){
			if(config.getCapacity()!=null && config.getCapacity()>0) {
				long usedCapacity = usedSize / (1024 * 1024);
				if (usedCapacity > config.getCapacity()) {
					return config.getCapacity();
				}
			}
		}

		return 0;
	}

	public List<String> getUserInfo(String person) throws Exception{
		Cache.CacheKey cacheKey = new Cache.CacheKey(this.getClass(), person);
		Optional<?> optional = CacheManager.get(userCacheCategory, cacheKey);
		if (optional.isPresent()) {
			return (List<String>)optional.get();
		}
		List<String> list = new ArrayList<>();
		list.add(person);
		list.addAll(this.organization().unit().listWithPersonSupNested(person));
		list.addAll(this.organization().group()
				.listWithPersonReference(ListTools.toList(person), true, true, false));
		CacheManager.put(userCacheCategory, cacheKey, list);
		return list;
	}

	public String adjustDate(String name){
		if(StringUtils.isBlank(name)){
			return name;
		}
		String base = FilenameUtils.getBaseName(name);
		String extension = FilenameUtils.getExtension(name);
		return base + DateTools.compact(new Date()) + (StringUtils.isBlank(extension) ? "" : "." + extension);
	}

}
