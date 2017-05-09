package com.x.file.core.entity.open;

import com.x.base.core.entity.JpaObject;

/**
 * pagePortal <br/>
 * cmsDocument<br/>
 * forumDocument<br/>
 *
 */
public enum ReferenceType {

	processPlatformJob, processPlatformForm, portalPage, cmsDocument, forumDocument, forumReply;
	public static final int length = JpaObject.length_64B;
}
