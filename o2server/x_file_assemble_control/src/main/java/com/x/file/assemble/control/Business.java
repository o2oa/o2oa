package com.x.file.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.file.assemble.control.factory.*;
import com.x.organization.core.express.Organization;

public class Business {

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

}
