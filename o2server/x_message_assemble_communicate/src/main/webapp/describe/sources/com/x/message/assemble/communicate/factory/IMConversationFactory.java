package com.x.message.assemble.communicate.factory;

import com.x.message.assemble.communicate.AbstractFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class IMConversationFactory extends AbstractFactory {

	public IMConversationFactory(Business business) throws Exception {
		super(business);
	}

	/**
	 * 获取成员包含person的会话id 列表
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<String> listConversationWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMConversation.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<IMConversation> root = cq.from(IMConversation.class);
		Predicate p = cb.isMember(person, root.get(IMConversation_.personList));
		cq.select(root.get(IMConversation_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}


	/**
	 * 查询当前用户会话扩展
	 * @param person
	 * @param conversationId
	 * @return
	 * @throws Exception
	 */
	public IMConversationExt getConversationExt(String person, String conversationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMConversationExt.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<IMConversationExt> cq = cb.createQuery(IMConversationExt.class);
		Root<IMConversationExt> root = cq.from(IMConversationExt.class);
		Predicate p = cb.equal(root.get(IMConversationExt_.person), person);
		p = cb.and(p, cb.equal(root.get(IMConversationExt_.conversationId), conversationId));
		cq.select(root).where(p);
		List<IMConversationExt> list = em.createQuery(cq).getResultList();
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 未读消息数量
	 * @param ext
	 * @return
	 * @throws Exception
	 */
	public Long unreadNumber(IMConversationExt ext) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMMsg.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<IMMsg> root = cq.from(IMMsg.class);
		Predicate p = cb.equal(root.get(IMMsg_.conversationId), ext.getConversationId());
		p = cb.and(p, cb.notEqual(root.get(IMMsg_.createPerson), ext.getPerson()));
		if (ext.getLastReadTime() != null) {
			p = cb.and(p, cb.greaterThan(root.get(IMMsg_.createTime), ext.getLastReadTime()));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	/**
	 * 获取会话中的最后一条消息
	 * @param conversationId
	 * @return
	 * @throws Exception
	 */
	public IMMsg lastMessage(String conversationId)  throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMMsg.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<IMMsg> cq = cb.createQuery(IMMsg.class);
		Root<IMMsg> root = cq.from(IMMsg.class);
		Predicate p = cb.equal(root.get(IMMsg_.conversationId), conversationId);
		List<IMMsg> list =  em.createQuery(cq.select(root).where(p).orderBy(cb.desc(root.get(IMMsg_.createTime)))).getResultList();
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}else {
			return null;
		}
	}

	/**
	 * 分页查询会话中的聊天消息
	 * @param adjustPage
	 * @param adjustPageSize
	 * @param conversationId
	 * @return
	 * @throws Exception
	 */
	public List<IMMsg> listMsgWithConversationByPage(Integer adjustPage,
													 Integer adjustPageSize, String conversationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMMsg.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<IMMsg> cq = cb.createQuery(IMMsg.class);
		Root<IMMsg> root = cq.from(IMMsg.class);
		Predicate p = cb.equal(root.get(IMMsg_.conversationId), conversationId);
		cq.select(root).where(p).orderBy(cb.desc(root.get(IMMsg_.createTime)));
		return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
				.getResultList();
	}

	/**
	 * 查询会话聊天消息总数
	 * 分页查询需要
	 * @param conversationId
	 * @return
	 * @throws Exception
	 */
	public Long count(String conversationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMMsg.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<IMMsg> root = cq.from(IMMsg.class);
		Predicate p = cb.equal(root.get(IMMsg_.conversationId), conversationId);
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}
}