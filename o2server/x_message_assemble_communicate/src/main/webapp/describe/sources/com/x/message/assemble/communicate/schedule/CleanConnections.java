//package com.x.message.assemble.communicate.schedule;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map.Entry;
//
//import javax.websocket.Session;
//
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.message.assemble.communicate.ws.collaboration.ActionCollaboration;
//
//public class CleanConnections implements Job {
//
//	private static Logger logger = LoggerFactory.getLogger(CleanConnections.class);
//
//	@Override
//	/* 定时清理session已经关闭的用户 */
//	public void execute(JobExecutionContext arg0) throws JobExecutionException {
//		try {
//			List<Session> removes = new ArrayList<>();
//			for (Entry<Session, String> entry : ActionCollaboration.clients.entrySet()) {
//				if (!entry.getKey().isOpen()) {
//					removes.add(entry.getKey());
//				}
//			}
//			for (Session session : removes) {
//				ActionCollaboration.clients.remove(session);
//			}
//			logger.debug("clean {} websocket session, {} online.", removes.size(), ActionCollaboration.clients.size());
//		} catch (Exception e) {
//			logger.error(e);
//			throw new JobExecutionException(e);
//		}
//	}
//
//}