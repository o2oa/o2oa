package com.x.organization.assemble.control.jaxrs.loginrecord;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

class ActionLoginRecord extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLoginRecord.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, Boolean stream) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Workbook workbook = new XSSFWorkbook();
				ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Sheet sheet = workbook.createSheet("loginRecord");
			Row row = sheet.createRow(0);
			row.createCell(0).setCellValue("name");
			row.createCell(1).setCellValue("lastLoginTime");
			row.createCell(2).setCellValue("lastLoginAddress");
			row.createCell(3).setCellValue("lastLoginClient");
			int line = 0;
			Cell cell = null;
			CellStyle dateCellStyle = workbook.createCellStyle();
			CreationHelper createHelper = workbook.getCreationHelper();
			dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat(DateTools.format_yyyyMMdd));
			for (Person o : this.list(business)) {
				row = sheet.createRow(++line);
				row.createCell(0).setCellValue(o.getName());
				cell = row.createCell(1);
				if (null == o.getLastLoginTime()) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(o.getLastLoginTime());
					cell.setCellStyle(dateCellStyle);
				}
				row.createCell(2).setCellValue(o.getLastLoginAddress());
				row.createCell(3).setCellValue(o.getLastLoginClient());
			}
			String name = "loginRecord_" + DateTools.formatDate(new Date()) + ".xlsx";
			workbook.write(output);
			Wo wo = new Wo(output.toByteArray(), this.contentType(stream, name), this.contentDisposition(stream, name));
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

	private List<Person> list(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		cq.select(root.get(Person_.id));
		List<String> ids = em.createQuery(cq).getResultList();
		return business.entityManagerContainer().fetch(ids, Person.class, ListTools.toList(Person.name_FIELDNAME,
				Person.lastLoginTime_FIELDNAME, Person.lastLoginAddress_FIELDNAME, Person.lastLoginClient_FIELDNAME));
	}

}