package com.x.organization.assemble.control.servlet.loginrecord;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.application.servlet.AbstractServletAction;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.annotation.HttpMethodDescribe;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.utils.DateTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Person_;

@WebServlet(urlPatterns = "/servlet/loginrecord")
@MultipartConfig
public class LoginRecordAction extends AbstractServletAction {

	private static final long serialVersionUID = 4202924267632769560L;

	private static Logger logger = LoggerFactory.getLogger(LoginRecordAction.class);

	@HttpMethodDescribe(value = "获取所有人员的最后登录信息.")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Workbook workbook = new XSSFWorkbook()) {
			this.setCharacterEncoding(request, response);
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
			response.setHeader("Content-Type", "application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ URLEncoder.encode("loginRecord_" + DateTools.formatDate(new Date()) + ".xlsx", "utf-8"));
			workbook.write(response.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
			ActionResult<Object> result = new ActionResult<>();
			result.error(e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			this.result(response, result);
		}
	}

	private List<Person> list(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Person> root = cq.from(Person.class);
		cq.select(root.get(Person_.id));
		List<String> ids = em.createQuery(cq).getResultList();
		return business.entityManagerContainer().fetchAttribute(ids, Person.class, "name", "lastLoginTime",
				"lastLoginAddress", "lastLoginClient");
	}
}