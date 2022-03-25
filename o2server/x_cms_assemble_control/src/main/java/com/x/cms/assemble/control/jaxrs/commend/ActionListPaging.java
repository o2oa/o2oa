package com.x.cms.assemble.control.jaxrs.commend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommend;
import com.x.cms.core.entity.DocumentCommend_;

class ActionListPaging extends BaseAction {
	private static Logger logger = LoggerFactory.getLogger(ActionListPaging.class);
	private static final String COMMEND_TYPE_ALL = "all";

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<List<Wo>> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			EntityManager em = emc.get(DocumentCommend.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<DocumentCommend> cq = cb.createQuery(DocumentCommend.class);
			Root<DocumentCommend> root = cq.from(DocumentCommend.class);
			Predicate p = cb.conjunction();
			if (StringUtils.isNotBlank(wi.getDocumentId())){
				p = cb.and(p, cb.equal(root.get(DocumentCommend_.documentId), wi.getDocumentId()));
			}
			if (StringUtils.isNotBlank(wi.getCommentId())){
				p = cb.and(p, cb.equal(root.get(DocumentCommend_.commentId), wi.getCommentId()));
			}
			if (StringUtils.isNotBlank(wi.getCommendPerson())){
				String person = business.organization().person().get(wi.getCommendPerson());
				if(StringUtils.isBlank(person)){
					person = wi.getCommendPerson();
				}
				p = cb.and(p, cb.equal(root.get(DocumentCommend_.commendPerson), person));
			}
			if (StringUtils.isNotBlank(wi.getCreatorPerson())){
				String person = business.organization().person().get(wi.getCreatorPerson());
				if(StringUtils.isBlank(person)){
					person = wi.getCreatorPerson();
				}
				p = cb.and(p, cb.equal(root.get(DocumentCommend_.creatorPerson), person));
			}
			if(StringUtils.isBlank(wi.getType())){
				p = cb.and(p, cb.equal(root.get(DocumentCommend_.type), DocumentCommend.COMMEND_TYPE_DOCUMENT));
			}else if(!wi.getType().equals(COMMEND_TYPE_ALL)){
				p = cb.and(p, cb.equal(root.get(DocumentCommend_.type), wi.getType()));
			}
			List<Wo> wos = emc.fetchDescPaging(DocumentCommend.class, Wo.copier, p, page, size, DocumentCommend.sequence_FIELDNAME);
			if(BooleanUtils.isTrue(wi.getReturnDocIndexPic())){
				List<String> docIds = ListTools.extractField(wos, DocumentCommend.documentId_FIELDNAME, String.class, true, true);
				List<Document> documentList = emc.fetch(docIds, Document.class, ListTools.toList(Document.id_FIELDNAME, Document.indexPics_FIELDNAME));
				final Map<String, String> map = documentList.stream().filter(t -> StringUtils.isNoneBlank(t.getIndexPics()))
						.collect(Collectors.toMap(Document::getId, Document::getIndexPics,(k1,k2)->k2));
				final List empty = new ArrayList<>();
				wos.stream().forEach(wo -> {
					if(map.containsKey(wo.getDocumentId())){
						wo.setIndexPicList(ListTools.toList(map.get(wo.getDocumentId()).split(",")));
					}else{
						wo.setIndexPicList(empty);
					}
				});
			}
			result.setData(wos);
			result.setCount(emc.count(DocumentCommend.class, p));
			return result;
		}
	}

	public static class Wi extends DocumentCommend{

		private static final long serialVersionUID = 8042740393049682505L;

		static WrapCopier<Wi, DocumentCommend> copier = WrapCopierFactory.wi(Wi.class, DocumentCommend.class, null,
				ListTools.toList(JpaObject.FieldsUnmodify, DocumentCommend.title_FIELDNAME, DocumentCommend.commentTitle_FIELDNAME));

		@FieldDescribe("是否返回点赞对应文档的首页图片，默认为false.")
		private Boolean returnDocIndexPic;

		public Boolean getReturnDocIndexPic() {
			return returnDocIndexPic;
		}

		public void setReturnDocIndexPic(Boolean returnDocIndexPic) {
			this.returnDocIndexPic = returnDocIndexPic;
		}
	}

	public static class Wo extends DocumentCommend {

		private static final long serialVersionUID = -1828627584254370972L;

		static WrapCopier<DocumentCommend, Wo> copier = WrapCopierFactory.wo(DocumentCommend.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		@FieldDescribe("点赞对应文档的首页图片列表.")
		private List<String> indexPicList;

		public List<String> getIndexPicList() {
			return indexPicList;
		}

		public void setIndexPicList(List<String> indexPicList) {
			this.indexPicList = indexPicList;
		}
	}
}
