package com.x.okr.assemble.control.service;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.okr.assemble.control.Business;
import com.x.okr.entity.OkrWorkChat;

/**
 * 类 名：OkrWorkChatService<br/>
 * 实体类：OkrWorkChat<br/>
 * 作 者：Liyi<br/>
 * 单 位：O2 Team<br/>
 * 日 期：2016-05-20 17:17:27
 **/
public class OkrWorkChatService {

	private static Logger logger = LoggerFactory.getLogger(OkrWorkChatService.class);

	/**
	 * 根据传入的ID从数据库查询OkrWorkChat对象
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public OkrWorkChat get(String id) throws Exception {
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null, return null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.find(id, OkrWorkChat.class);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 根据传入的ID从数据库查询OkrWorkChat对象
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public List<String> listByWorkId(String workId) throws Exception {
		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null, return null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkChatFactory().listByWorkId(workId);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 向数据库保存OkrWorkChat对象
	 * 
	 * @param wrapIn
	 */
	public OkrWorkChat save(OkrWorkChat wrapIn) throws Exception {
		OkrWorkChat okrWorkChat = null;
		if (wrapIn.getId() != null && wrapIn.getId().trim().length() > 20) {
			// 根据ID查询信息是否存在，如果存在就update，如果不存在就create
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkChat = new OkrWorkChat();
				emc.beginTransaction(OkrWorkChat.class);
				wrapIn.copyTo(okrWorkChat);
				okrWorkChat.setId(wrapIn.getId());// 使用参数传入的ID作为记录的ID
				emc.persist(okrWorkChat, CheckPersistType.all);
				emc.commit();
			} catch (Exception e) {
				logger.warn("OkrWorkChat update/ got a error!");
				throw e;
			}
		} else {// 没有传入指定的ID
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkChat = new OkrWorkChat();
				emc.beginTransaction(OkrWorkChat.class);
				wrapIn.copyTo(okrWorkChat);
				emc.persist(okrWorkChat, CheckPersistType.all);
				emc.commit();
			} catch (Exception e) {
				logger.warn("OkrWorkChat create got a error!");
				logger.error(e);
				throw e;
			}
		}
		return okrWorkChat;
	}

	/**
	 * 根据ID从数据库中删除OkrWorkChat对象
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void delete(String id) throws Exception {
		OkrWorkChat okrWorkChat = null;
		if (id == null || id.isEmpty()) {
			throw new Exception("id is null, system can not delete any object.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 先判断需要操作的应用信息是否存在，根据ID进行一次查询，如果不存在不允许继续操作
			okrWorkChat = emc.find(id, OkrWorkChat.class);
			if (null == okrWorkChat) {
				throw new Exception("object is not exist {'id':'" + id + "'}");
			} else {
				emc.beginTransaction(OkrWorkChat.class);
				emc.remove(okrWorkChat, CheckRemoveType.all);
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询下一页的信息数据，直接调用Factory里的方法
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkChat> listChatNextWithFilter(String id, Integer count, String workId, String sequenceField,
			String order) throws Exception {

		Business business = null;
		Object sequence = null;
		if (workId == null) {
			throw new Exception("workId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if (id != null && !"(0)".equals(id) && id.trim().length() > 20) {
				if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty(emc.find(id, OkrWorkChat.class),  JpaObject.sequence_FIELDNAME);
				}
			}
			return business.okrWorkChatFactory().listNextWithFilter(id, count, sequence, workId, sequenceField, order);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询上一页的信息数据，直接调用Factory里的方法
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public List<OkrWorkChat> listChatPrevWithFilter(String id, Integer count, String workId, String sequenceField,
			String order) throws Exception {
		Business business = null;
		Object sequence = null;
		if (workId == null) {
			throw new Exception("workId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if (id != null && !"(0)".equals(id) && id.trim().length() > 20) {
				if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
					sequence = PropertyUtils.getProperty(emc.find(id, OkrWorkChat.class),  JpaObject.sequence_FIELDNAME);
				}
			}
			return business.okrWorkChatFactory().listPrevWithFilter(id, count, sequence, workId, sequenceField, order);

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 查询符合条件的数据总数
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public Long getChatCountWithFilter(String workId) throws Exception {
		Business business = null;
		if (workId == null || workId.isEmpty()) {
			throw new Exception("workId is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.okrWorkChatFactory().getCountWithFilter(workId);
		} catch (Exception e) {
			throw e;
		}
	}
}
