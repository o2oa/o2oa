package com.x.attendance.assemble.control;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.cache.CacheManager;

/**
 * 缓存管理帮助类
 *
 */
public class CacheUtil {
	
	public static <T extends JpaObject> void notify( Class<T> clz ) throws Exception {
		CacheManager.notify( clz );
	}
	
}
