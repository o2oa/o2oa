package com.x.file.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.file.assemble.control.factory.AttachmentFactory;
import com.x.file.assemble.control.factory.FileFactory;
import com.x.file.assemble.control.factory.FolderFactory;
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

	private FolderFactory folder;

	public FolderFactory folder() throws Exception {
		if (null == this.folder) {
			this.folder = new FolderFactory(this);
		}
		return folder;
	}

	private FileFactory file;

	public FileFactory file() throws Exception {
		if (null == this.file) {
			this.file = new FileFactory(this);
		}
		return file;
	}

	private Organization organization;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

}
