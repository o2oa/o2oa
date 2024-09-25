package com.x.message.assemble.communicate.factory;

import com.x.base.core.entity.JpaObject;
import com.x.message.core.entity.IMMsgCollection;
import com.x.message.core.entity.IMMsgCollection_;
import com.x.organization.core.entity.Person_;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.message.assemble.communicate.AbstractFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMConversationExt;
import com.x.message.core.entity.IMConversationExt_;
import com.x.message.core.entity.IMConversation_;
import com.x.message.core.entity.IMMsg;
import com.x.message.core.entity.IMMsg_;

public class IMConversationFactory extends AbstractFactory {

	public IMConversationFactory(Business business) throws Exception {
		super(business);
	}


	/**
	 * 根据 businessId 获取会话列表 并且成员包含person
	 * @param person
	 * @param businessId
	 * @return
	 * @throws Exception
	 */
	public List<IMConversation> listConversationWithPersonAndBusinessId(String person, String businessId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMConversation.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<IMConversation> cq = cb.createQuery(IMConversation.class);
		Root<IMConversation> root = cq.from(IMConversation.class);
		Predicate p = cb.isMember(person, root.get(IMConversation_.personList));
		p = cb.and(p, cb.equal(root.get(IMConversation_.businessId), businessId));
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}


	/**
	 * 获取成员包含person的会话id 列表
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<IMConversation> listConversationWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMConversation.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<IMConversation> cq = cb.createQuery(IMConversation.class);
		Root<IMConversation> root = cq.from(IMConversation.class);
		Predicate p = cb.isMember(person, root.get(IMConversation_.personList));
		cq.select(root).where(p);
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
	 * 根据 id 列表获取消息对象列表
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<IMMsg> listMsgObject(List<String> ids) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMMsg.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<IMMsg> cq = cb.createQuery(IMMsg.class);
		Root<IMMsg> root = cq.from(IMMsg.class);
		Predicate p;
		if(ids.size() > 1){
			p = root.get(JpaObject.id_FIELDNAME).in(ids);
		}else{
			p = cb.equal(root.get(JpaObject.id_FIELDNAME), ids.get(0));
		}
		return  em.createQuery(cq.select(root).where(p).orderBy(cb.desc(root.get(IMMsg_.createTime)))).getResultList();
	}


	/**
	 * 查询会话中的聊天消息
	 * 分页查询需要
	 * @param adjustPage
	 * @param adjustPageSize
	 * @param conversationId
	 * @return
	 * @throws Exception
	 */
	public List<IMMsg> listMsgWithConversationByPage(Integer adjustPage,
													 Integer adjustPageSize, String conversationId, Date lastDeleteTime) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMMsg.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<IMMsg> cq = cb.createQuery(IMMsg.class);
		Root<IMMsg> root = cq.from(IMMsg.class);
		Predicate p = cb.equal(root.get(IMMsg_.conversationId), conversationId);
		if (lastDeleteTime != null) {
			p = cb.and(p, cb.greaterThan(root.get(IMMsg_.createTime),lastDeleteTime));
		}
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
	public Long count(String conversationId, Date lastDeleteTime) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMMsg.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<IMMsg> root = cq.from(IMMsg.class);
		Predicate p = cb.equal(root.get(IMMsg_.conversationId), conversationId);
		if (lastDeleteTime != null) {
			p = cb.and(p, cb.greaterThan(root.get(IMMsg_.createTime),lastDeleteTime));
		}
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}



	/**
	 * 根据会话id查询所有的消息id
	 * 清空聊天记录用
	 * @param conversationId 会话id
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllMsgIdsWithConversationId(String conversationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMMsg.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<IMMsg> root = cq.from(IMMsg.class);
		Predicate p = cb.equal(root.get(IMMsg_.conversationId), conversationId);
		return em.createQuery(cq.select(root.get(IMMsg_.id)).where(p)).getResultList();
	}

	/**
	 * 根据会话id查询所有关联的会话扩展
	 * 删除会话扩展用
	 * @param conversationId 会话id
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllConversationExtIdsWithConversationId(String conversationId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMConversationExt.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<IMConversationExt> root = cq.from(IMConversationExt.class);
		Predicate p = cb.equal(root.get(IMConversationExt_.conversationId), conversationId);
		return em.createQuery(cq.select(root.get(IMConversationExt_.id)).where(p)).getResultList();
	}



	/**
	 * 查询收藏消息
	 * @param person 收藏人
	 * @param msgId 消息 id
	 * @return
	 * @throws Exception
	 */
	public List<IMMsgCollection> listCollectionByPersonAndMsgId(String person, String msgId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMMsgCollection.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		Predicate predicate = cb.conjunction();
		CriteriaQuery<IMMsgCollection> cq = cb.createQuery(IMMsgCollection.class);
		Root<IMMsgCollection> root = cq.from(IMMsgCollection.class);
		predicate = cb.and(predicate, cb.equal(root.get(IMMsgCollection_.createPerson), person));
		predicate = cb.and(predicate, cb.equal(root.get(IMMsgCollection_.messageId), msgId));
		return  em.createQuery(cq.select(root).where(predicate)).getResultList();
	}

	/**
	 * 分页查询收藏消息
	 * @param adjustPage
	 * @param adjustPageSize
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public List<IMMsgCollection> listCollectionWithPersonByPage(Integer adjustPage, Integer adjustPageSize, String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMMsgCollection.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<IMMsgCollection> cq = cb.createQuery(IMMsgCollection.class);
		Root<IMMsgCollection> root = cq.from(IMMsgCollection.class);
		Predicate p = cb.equal(root.get(IMMsgCollection_.createPerson), person);
		cq.select(root).where(p).orderBy(cb.desc(root.get(IMMsgCollection_.createTime)));
		return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize)
				.getResultList();
	}

	/**
	 * 收藏消息数量
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public Long listCollectionCount(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(IMMsgCollection.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<IMMsgCollection> root = cq.from(IMMsgCollection.class);
		Predicate p = cb.equal(root.get(IMMsgCollection_.createPerson), person);
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
	}

}