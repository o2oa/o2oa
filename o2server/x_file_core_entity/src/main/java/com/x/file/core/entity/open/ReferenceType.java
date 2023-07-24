package com.x.file.core.entity.open;

import com.x.base.core.entity.JpaObject;

/**
 * pagePortal <br/>
 * cmsDocument<br/>
 * forumDocument<br/>
 *
 */
public enum ReferenceType {

	processPlatformJob, processPlatformForm, mindInfo, portalPage, cmsDocument, forumDocument, forumReply, component, teamworkProject;
	public static final int length = JpaObject.length_64B;
}
