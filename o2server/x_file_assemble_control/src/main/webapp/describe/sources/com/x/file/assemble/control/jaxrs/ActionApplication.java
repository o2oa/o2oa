package com.x.file.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.file.assemble.control.jaxrs.attachment.AttachmentAction;
import com.x.file.assemble.control.jaxrs.complex.ComplexAction;
import com.x.file.assemble.control.jaxrs.editor.EditorAction;
import com.x.file.assemble.control.jaxrs.file.FileAction;
import com.x.file.assemble.control.jaxrs.folder.FolderAction;
import com.x.file.assemble.control.jaxrs.share.ShareAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(AttachmentAction.class);
		classes.add(FolderAction.class);
		classes.add(ComplexAction.class);
		classes.add(ShareAction.class);
		classes.add(EditorAction.class);
		classes.add(FileAction.class);
		return classes;
	}

}
