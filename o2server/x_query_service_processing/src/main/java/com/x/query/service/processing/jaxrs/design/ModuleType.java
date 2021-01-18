package com.x.query.service.processing.jaxrs.design;

import com.x.base.core.entity.JpaObject;

/**
 * processPlatform（流程管理平台）
 * portal（门户管理平台）
 * cms（内容管理平台）
 * query (数据中心平台)
 * service（服务管理平台）
 */
public enum ModuleType {

	processPlatform, portal, cms, query, service;
	public static final int length = JpaObject.length_64B;
}
