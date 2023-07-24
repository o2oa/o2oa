package com.x.file.assemble.control;

import java.util.Optional;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.file.assemble.control.factory.Attachment2Factory;
import com.x.file.assemble.control.factory.AttachmentFactory;
import com.x.file.assemble.control.factory.FileFactory;
import com.x.file.assemble.control.factory.Folder2Factory;
import com.x.file.assemble.control.factory.FolderFactory;
import com.x.file.assemble.control.factory.OriginFileFactory;
import com.x.file.assemble.control.factory.RecycleFactory;
import com.x.file.assemble.control.factory.ShareFactory;
import com.x.file.core.entity.open.FileConfig;
import com.x.organization.core.express.Organization;

public class Business {

	public final static String TOP_FOLD = "$$TOP_FOLD";

	public final static String SYSTEM_CONFIG = "systemConfig";

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private AttachmentFactory attachment;

	public AttachmentFactory attachment() throws Exception {
		if (null == this.attachment) {
			this.attachment = new AttachmentFactory(this);
		}
		return attachment;
	}

	private Attachment2Factory attachment2;

	public Attachment2Factory attachment2() throws Exception {
		if (null == this.attachment2) {
			this.attachment2 = new Attachment2Factory(this);
		}
		return attachment2;
	}

	private FolderFactory folder;

	public FolderFactory folder() throws Exception {
		if (null == this.folder) {
			this.folder = new FolderFactory(this);
		}
		return folder;
	}

	private Folder2Factory folder2;

	public Folder2Factory folder2() throws Exception {
		if (null == this.folder2) {
			this.folder2 = new Folder2Factory(this);
		}
		return folder2;
	}

	private FileFactory file;

	public FileFactory file() throws Exception {
		if (null == this.file) {
			this.file = new FileFactory(this);
		}
		return file;
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

	private RecycleFactory recycle;

	public RecycleFactory recycle() throws Exception {
		if (null == this.recycle) {
			this.recycle = new RecycleFactory(this);
		}
		return recycle;
	}

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

	public Integer verifyConstraint(String person, long usedSize) throws Exception{
		Cache.CacheCategory cacheCategory = new Cache.CacheCategory(FileConfig.class);
		Cache.CacheKey cacheKey = new Cache.CacheKey(FileConfig.class, Business.SYSTEM_CONFIG);
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
		FileConfig config = null;
		if(optional.isPresent()){
			config = (FileConfig)optional.get();
		}else{
			config = this.entityManagerContainer().firstEqual(FileConfig.class, FileConfig.person_FIELDNAME, Business.SYSTEM_CONFIG);
			if(config != null){
				this.entityManagerContainer().get(FileConfig.class).detach(config);
				CacheManager.put(cacheCategory, cacheKey, config);
			}
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

}
