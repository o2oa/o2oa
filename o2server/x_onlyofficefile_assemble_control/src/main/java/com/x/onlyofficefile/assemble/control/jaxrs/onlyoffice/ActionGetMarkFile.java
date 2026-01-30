package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.x_organization_assemble_express;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.onlyofficefile.assemble.control.ThisApplication;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.ConfigManager;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocbuilderUtility;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.FileUtility;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
public class ActionGetMarkFile extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetFile.class);

	ActionResult<Wo> execute(HttpServletRequest request,EffectivePerson effectivePerson, String id, String version) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			DocumentManager.Init(request);

		    EntityManager em = emc.get(OnlyOfficeFile.class);

		    OnlyOfficeFile record = em.find(OnlyOfficeFile.class, id);

			String fileName = "";
			String filePath = "";

			if(StringUtils.isBlank(version)) {
				version = "0" ;
			}

			if(record != null) {
				fileName = record.getId()+ record.getExtension();
	            filePath = record.path();
	            String serverPath= "";
	            if(version.equalsIgnoreCase("0")) {
	            	serverPath = filePath;
	            }else {
	            	 serverPath = DocumentManager.getFilePathById(fileName,version);
	            	 serverPath = serverPath +  File.separator +  "prev" + record.getExtension() ;
	            }

	            String mark = effectivePerson.getDistinguishedName();

	            String path = "person/list/object";
	            String  para= "{\"personList\":[\""+effectivePerson.getDistinguishedName()+ "\"]}";

	            JsonParser parser = new JsonParser();
	    		JsonObject jsonObj  = parser.parse(para).getAsJsonObject();

	    		ActionResponse resp =  ThisApplication.context().applications()
						.postQuery(x_organization_assemble_express.class, path, jsonObj);
	    		 JsonElement data = resp.getData();
	    		 JsonArray jsonArray = data.getAsJsonArray();
	    		 if(jsonArray.size() > 0) {
	    			 JsonElement qq = jsonArray.get(0);
		    	     JsonElement mobile = qq.getAsJsonObject().get("mobile");
		    	     mark = mark + "  " +  mobile.getAsString();
	    		 }else {
	    			 mark = mark + "  " +  new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	    		 }


	             String docbuilderFilePath = DocumentManager.markFilesRootPath() + record.getId()+ ".docbuilder";
				 String docFilePath= serverPath;

				 fileName = record.getFileName();
		         fileName = fileName.substring(0, fileName.lastIndexOf("."));

				String pdfMarkFilePath =  DocumentManager.markFilesRootPath() + record.getId()+ ".pdf";
				String docMarkFilePath = null;
				String docbuilderExePath =  ConfigManager.init(Config.base()).getDocbuilderEXEPath();
				if(DocbuilderUtility.CreateDocbuilder(docbuilderFilePath,docFilePath,docMarkFilePath,pdfMarkFilePath,mark)) {
					DocbuilderUtility.ExcuteDocbuilder(docbuilderExePath,docbuilderFilePath);
				}

				File file = new File(pdfMarkFilePath);
	            byte[] bt = FileUtility.File2byte(file);

				Wo wo = new Wo(bt, this.contentType(true, fileName+ ".pdf"), this.contentDisposition(true, fileName+ ".pdf"));
				result.setData(wo);
	            }else {
		            logger.info("没有找到id=" + id );
		           return result;
			    }
			return result;
		}
	}


	public static class Wo extends WoFile {

		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}

	}

	public static class Wi extends GsonPropertyObject {

	}
}
