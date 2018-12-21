package com.x.program.center.jaxrs.agent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.core.entity.Agent;

abstract class BaseAction extends StandardJaxrsAction {
	private static String COMMENT = "";

	private static final Pattern COMMENT_REGEX = Pattern.compile("^\\/\\*(\\s|.)*?\\*\\/");

	static {
		COMMENT = "/*" + StringUtils.LF;
		COMMENT += "* resources.getEntityManagerContainer() // 实体管理容器." + StringUtils.LF;
		COMMENT += "* resources.getContext() //上下文根." + StringUtils.LF;
		COMMENT += "* resources.getOrganization() //组织访问接口." + StringUtils.LF;
		COMMENT += "* requestText //请求内容." + StringUtils.LF;
		COMMENT += "* request //请求对象." + StringUtils.LF;
		COMMENT += "*/" + StringUtils.LF;
	}

	protected void addComment(Agent agent) {
		if (StringUtils.isEmpty(agent.getText())) {
			agent.setText(COMMENT);
		} else {
			Matcher m = COMMENT_REGEX.matcher(agent.getText());
			if (m.find()) {
				agent.setText(COMMENT + m.replaceFirst(""));
			} else {
				agent.setText(COMMENT + agent.getText());
			}
		}
	}

}
