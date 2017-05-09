package com.x.portal.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static final int portal_name_length = JpaObject.length_255B;

	public static class Portal {
		public static final String table = "PTL_PORTAL";
	}

	public static class Page {
		public static final String table = "PTL_PAGE";
	}

	public static class TemplatePage {
		public static final String table = "PTL_TEMPLATEPAGE";
	}

	public static class Menu {
		public static final String table = "PTL_MENU";
	}

	public static class Source {
		public static final String table = "PTL_SOURCE";
	}

	public static class Script {
		public static final String table = "PTL_SCRIPT";
	}

}
