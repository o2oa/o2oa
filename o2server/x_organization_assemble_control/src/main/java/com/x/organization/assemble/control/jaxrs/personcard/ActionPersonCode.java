package com.x.organization.assemble.control.jaxrs.personcard;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.codec.binary.Base64;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.tools.DateTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;




class ActionPersonCode extends BaseAction {

	//组织里管理_个人生成二维码。
	ActionResult<Wo> personcode(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Person personCard = emc.find(flag, Person.class);
			if (null == personCard) {
				throw new ExceptionPersonNotExist(flag);
			}
			
			String fname = personCard.getId()+".png";
			String content = "BEGIN:VCARD\n" +
	        	    "VERSION:3.0\n" +
	        	    "N:"+personCard.getName()+"\n";
	        	    
			if(personCard.getOfficePhone()!=null && !personCard.getOfficePhone().equals("")){
				content = content+"TEL:"+personCard.getOfficePhone()+"\n";
			}
			if(personCard.getMobile()!=null && !personCard.getMobile().equals("")){
				content = content+"TEL;CELL:"+personCard.getMobile()+"\n";
			}
			/*if(!personCard.getAddress().equals("")){
				content = content+"ADR;HOME;POSTAL:;;"+personCard.getAddress()+";;;;\n";
			}*/
			if(personCard.getMail()!=null && !personCard.getMail().equals("")){
				content = content+"EMAIL:"+personCard.getMail()+"\n";
			}
			if(personCard.getQq()!=null && !personCard.getQq().equals("")){
				content = content+"X-QQ:"+personCard.getQq()+"\n";
			}
			if(personCard.getWeixin()!=null && !personCard.getWeixin().equals("")){
				content = content+"NOTE:微信:"+personCard.getWeixin()+"\n";
			}
			if(personCard.getBirthday()!=null && !personCard.getBirthday().equals("")){
				content = content+"BDAY:"+DateTools.formatDate(personCard.getBirthday())+"\n";
			}
			if(personCard.getId()!=null && !personCard.getId().equals("")){
				List<Identity> identityList = referenceIdentity(business,flag);
				if(identityList.isEmpty()){
					if(!personCard.getTopUnitList().isEmpty()){
						String TopUnitId = personCard.getTopUnitList().get(0);
						Unit unit = emc.find(flag, Unit.class);
						if (null == unit) {
							throw new ExceptionEntityNotExist(flag);
						}
						content = content+"ORG:"+unit.getName()+"\n";
					}
				}else{
					String orgString = "";
					String titleString = "";
					for (Identity identity : identityList) {
						orgString = orgString.equals("")?identity.getUnitLevelName():orgString +";"+identity.getUnitLevelName();
						List<UnitDuty> unitdutyList = referenceUnitduty(business,identity.getId());
						if(!unitdutyList.isEmpty()){
							for(UnitDuty unitduty : unitdutyList){
								if(null != unitduty){
									titleString = titleString.equals("")?unitduty.getName():titleString +";"+unitduty.getName();
								}
							}
						}
					}
					if(!orgString.equals("")){
						content = content+"ORG:"+orgString+"\n";
					}
					if(!titleString.equals("")){
						content = content+"TITLE:"+titleString+"\n";
					}
				}	
			}
			 
			content = content+"END:VCARD";
			/*
			File logoFile = null;
			if(personCard.getIconMdpi()!=null && !personCard.getIconMdpi().equals("")){
				logoFile = CodeUtil.decoderBase64File(logopath,personCard.getIconMdpi());
			}
			
			 File QrCodeFile = new File(path);
			CodeUtil.drawLogoQRCode(logoFile, QrCodeFile, content, "");*/
			
			BufferedImage logoFile = null;
			ByteArrayOutputStream QrCodeFile = new ByteArrayOutputStream();
			if(personCard.getIconMdpi()!=null && !personCard.getIconMdpi().equals("")){
				//byte[] buffer = new BASE64Decoder().decodeBuffer(personCard.getIconMdpi());  
				byte[] buffer = Base64.decodeBase64(personCard.getIconMdpi());
				ByteArrayInputStream in = new ByteArrayInputStream(buffer);    //将b作为输入流；
				logoFile = ImageIO.read(in);     //将in作为输入流，读取图片存入image中，而这里in可以为ByteArrayInputStream();
			}
			Wo wo = new Wo(CodeUtil.drawLogoQRCodeByte(logoFile, QrCodeFile, content, ""), this.contentType(false, fname), this.contentDisposition(false, fname));
			result.setData(wo);
			return result;
		}
	}
	 
	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}
	
	public List<Identity> referenceIdentity(Business business,String flag) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.equal(root.get(Identity_.person), flag);
		return em.createQuery(cq.select(root).where(p)).getResultList();
	}
	public List<UnitDuty> referenceUnitduty(Business business,String flag) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.isMember(flag, root.get(UnitDuty_.identityList));
		return em.createQuery(cq.select(root).where(p)).getResultList();
	}

}
