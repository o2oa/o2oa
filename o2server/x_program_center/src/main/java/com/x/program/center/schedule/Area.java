package com.x.program.center.schedule;

import java.net.URLEncoder;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.general.core.entity.area.District;
import com.x.general.core.entity.area.District_;
import com.x.program.center.Business;

public class Area extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(Area.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			for (WrapProvince o : this.list()) {
				WrapProvince wrapProvince = this.get(o.getName());
				String sha = DigestUtils.sha256Hex(XGsonBuilder.toJson(wrapProvince));
				District province = this.getProvinceDistrict(business, wrapProvince.getName());
				if (null != province && StringUtils.equals(sha, province.getSha())) {
					logger.debug("{} 无需更新.", wrapProvince.getName());
					continue;
				}
				if (null != province) {
					logger.debug("删除 {}.", wrapProvince.getName());
					this.removeProvince(business, province);
				}
				logger.debug("更新 {}.", wrapProvince.getName());
				this.saveProvince(business, wrapProvince, sha);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	private void saveProvince(Business business, WrapProvince wrapProvince, String sha) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(District.class);
		District province = new District();
		province.setLevel(District.LEVEL_PROVINCE);
		province.setName(wrapProvince.getName());
		province.setCenter(wrapProvince.getCenter());
		province.setZipCode(wrapProvince.getZipCode());
		province.setCityCode(wrapProvince.getCityCode());
		province.setSha(sha);
		emc.persist(province, CheckPersistType.all);
		for (WrapCity c : wrapProvince.getCityList()) {
			District city = new District();
			city.setLevel(District.LEVEL_CITY);
			city.setName(c.getName());
			city.setCenter(c.getCenter());
			city.setZipCode(c.getZipCode());
			city.setCityCode(c.getCityCode());
			city.setProvince(province.getId());
			emc.persist(city, CheckPersistType.all);
			for (WrapDistrict d : c.getDistrictList()) {
				District district = new District();
				district.setLevel(District.LEVEL_DISTRICT);
				district.setName(d.getName());
				district.setCenter(d.getCenter());
				district.setZipCode(d.getZipCode());
				district.setCityCode(d.getCityCode());
				district.setProvince(province.getId());
				district.setCity(city.getId());
				emc.persist(district, CheckPersistType.all);
				for (WrapStreet s : d.getStreetList()) {
					District street = new District();
					street.setLevel(District.LEVEL_STREET);
					street.setName(s.getName());
					street.setCenter(s.getCenter());
					street.setZipCode(s.getZipCode());
					street.setCityCode(s.getCityCode());
					street.setProvince(province.getId());
					street.setCity(city.getId());
					street.setDistrict(district.getId());
					emc.persist(street, CheckPersistType.all);
				}
			}
		}
		emc.commit();
	}

	private District getProvinceDistrict(Business business, String name) throws Exception {
		EntityManager em = business.entityManagerContainer().get(District.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<District> cq = cb.createQuery(District.class);
		Root<District> root = cq.from(District.class);
		Predicate p = cb.equal(root.get(District_.level), District.LEVEL_PROVINCE);
		p = cb.and(p, cb.equal(root.get(District_.name), name));
		cq.select(root).where(p);
		List<District> os = em.createQuery(cq).getResultList();
		if (os.isEmpty()) {
			return null;
		} else {
			return os.get(0);
		}
	}

	private void removeProvince(Business business, District province) throws Exception {
		EntityManagerContainer emc = business.entityManagerContainer();
		emc.beginTransaction(District.class);
		emc.deleteEqual(District.class, District.province_FIELDNAME, province.getId());
		emc.remove(province, CheckRemoveType.all);
		emc.commit();
		emc.flush();
	}

	private List<WrapProvince> list() throws Exception {
		ActionResponse response = ConnectionAction.get(Config.collect().url(ADDRESS_AREA_LIST), null);
		return response.getDataAsList(WrapProvince.class);
	}

	private WrapProvince get(String name) throws Exception {
		ActionResponse response = ConnectionAction.get(
				Config.collect().url(ADDRESS_AREA_GET + "/name/" + URLEncoder.encode(name, DefaultCharset.name)), null);
		return response.getData(WrapProvince.class);
	}

	public static class WrapProvince extends DistractObject {

		List<WrapCity> cityList;

		public List<WrapCity> getCityList() {
			return cityList;
		}

		public void setCityList(List<WrapCity> cityList) {
			this.cityList = cityList;
		}

	}

	public static class WrapCity extends DistractObject {
		List<WrapDistrict> districtList;

		public List<WrapDistrict> getDistrictList() {
			return districtList;
		}

		public void setDistrictList(List<WrapDistrict> districtList) {
			this.districtList = districtList;
		}

	}

	public static class WrapDistrict extends DistractObject {

		List<WrapStreet> streetList;

		public List<WrapStreet> getStreetList() {
			return streetList;
		}

		public void setStreetList(List<WrapStreet> streetList) {
			this.streetList = streetList;
		}

	}

	public static class WrapStreet extends DistractObject {

	}

	public static class DistractObject extends GsonPropertyObject {

		@FieldDescribe("名称.")
		private String name;

		@FieldDescribe("邮编.")
		private String zipCode;

		@FieldDescribe("城市区号.")
		private String cityCode;

		@FieldDescribe("级别.")
		private String level;

		@FieldDescribe("中心坐标.")
		private String center;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getZipCode() {
			return zipCode;
		}

		public void setZipCode(String zipCode) {
			this.zipCode = zipCode;
		}

		public String getCityCode() {
			return cityCode;
		}

		public void setCityCode(String cityCode) {
			this.cityCode = cityCode;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public String getCenter() {
			return center;
		}

		public void setCenter(String center) {
			this.center = center;
		}

	}

}