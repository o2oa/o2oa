package com.x.okr.assemble.control.jaxrs.okrworkchat;

import com.x.base.core.bean.BeanCopyTools;
import com.x.base.core.bean.BeanCopyToolsBuilder;
import com.x.okr.assemble.control.service.OkrUserInfoService;
import com.x.okr.assemble.control.service.OkrWorkBaseInfoQueryService;
import com.x.okr.assemble.control.service.OkrWorkChatService;
import com.x.okr.assemble.control.service.OkrWorkDynamicsService;
import com.x.okr.entity.OkrWorkChat;

public class ExcuteBase {

	protected BeanCopyTools<OkrWorkChat, WrapOutOkrWorkChat> wrapout_copier = BeanCopyToolsBuilder.create( OkrWorkChat.class, WrapOutOkrWorkChat.class, null, WrapOutOkrWorkChat.Excludes);
	protected OkrWorkChatService okrWorkChatService = new OkrWorkChatService();
	protected OkrWorkBaseInfoQueryService okrWorkBaseInfoService = new OkrWorkBaseInfoQueryService();
	protected OkrWorkDynamicsService okrWorkDynamicsService = new OkrWorkDynamicsService();
	protected OkrUserInfoService okrUserInfoService = new OkrUserInfoService();
}
