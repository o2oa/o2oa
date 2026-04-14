package com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice;

import com.hankcs.hanlp.corpus.io.IOUtil;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.onlyofficefile.assemble.control.Business;
import com.x.onlyofficefile.assemble.control.ThisApplication;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileModel;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.entities.FileType;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.DocumentManager;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.FileUtility;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.HttpClient;
import com.x.onlyofficefile.assemble.control.jaxrs.onlyoffice.utility.ServiceConverter;
import com.x.onlyofficefile.core.entity.OnlyOfficeFile;
import java.io.InputStream;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

/**
 * @author sword
 */
public class ActionUpload extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionUpload.class);

    ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String fileName, String relevanceId, String category,
                             String docId, InputStream fileInputStream, FormDataContentDisposition disposition) throws Exception{
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setRequestStatus(true);
            result.setData(wo);

            OnlyOfficeFile record = new OnlyOfficeFile();
            record.setRelevanceId(relevanceId);
            record.setDocId(docId);
            record.setCategory(StringUtils.isBlank(category) ? Business.TEMPLATE_APP : category);
            Date date = new Date();
            record.setCreateTime(date);
            DocumentManager.Init(request);
            try {
                if (StringUtils.isEmpty(fileName)) {
                    fileName = this.fileName(disposition);
                }
                String curExt = FileUtility.getFileExtension(fileName);
                //换成id文件名

                byte[] fileByte = IOUtil.readBytesFromOtherInputStream(fileInputStream);
                StorageMapping gfMapping = ThisApplication.context().storageMappings().random(OnlyOfficeFile.class);
                record.saveContent(gfMapping, fileByte, fileName);

                String fileNameId = record.getId() + curExt;
                FileModel fileModel = new FileModel(fileNameId, FileModel.DEFAULT_LANG, effectivePerson.getDistinguishedName(), effectivePerson.getName());
                fileModel.changeType(null, null);
                fileModel.document.title = fileName;

                record.setKey(fileModel.document.key);
                record.setFileVersion("1");
                fileModel.document.url = DocumentManager.getFileUriById(record.getId()) + "/0";
                fileModel.document.key = ServiceConverter.GenerateRevisionId(fileModel.document.url);
                if (DocumentManager.tokenEnabled()) {
                    fileModel.BuildToken();
                    String tokenSecret = DocumentManager.getTokenSecret();
                    String token = Crypto.encrypt(String.valueOf(record.getLength()), tokenSecret);
                    record.setFileToken(token);
                }

                record.setCreator(effectivePerson.getDistinguishedName());
                record.setFileVersion("1");
                record.setKey(fileModel.document.key);
                record.setStatus("normal");

                record.setFileModel(gson.toJson(fileModel));

                record.setFileName(fileName);

                emc.beginTransaction(OnlyOfficeFile.class);
                emc.persist(record, CheckPersistType.all);
                emc.commit();

                wo.setId(record.getId());
                wo.setFileModel(fileModel);
                wo.setRequestStatus(true);
                wo.setFileSize(String.valueOf(record.getLength()));
            } catch (Exception e) {
                logger.error(e);
                wo.setRequestStatus(false);
                wo.setMessage(e.getMessage());
            }

            return result;
        }
    }


    public Boolean convert(OnlyOfficeFile record) {
        try {
            String fileUri = DocumentManager.getFileUriById(record.getId()) + "/0";
            String fileExt = record.getExtension();
            FileType fileType = FileUtility.GetFileType(record.getFileName());
            String internalFileExt = DocumentManager.getInternalExtension(fileType);

            if (DocumentManager.getConvertExts().contains("." + fileExt)) {
                String key = ServiceConverter.GenerateRevisionId(fileUri);

                if (DocumentManager.tokenEnabled()) {
                    fileUri = fileUri + "?xtoken=" + record.getFileToken();
                }

                String newFileUri = ServiceConverter.getConvertedUri(record.getId() + "." + record.getExtension(), fileUri, fileExt, internalFileExt, key, false);

                if (newFileUri.isEmpty()) {
                    return false;
                }

                String fileName = record.getFileName();
                fileName = fileName.substring(0, fileName.lastIndexOf('.')) + internalFileExt;
                record.setFileName(fileName);

                String fileContent = HttpClient.doGet(newFileUri);
                StorageMapping mapping = ThisApplication.context().storageMappings().get(OnlyOfficeFile.class,
                        record.getStorage());
                record.updateContent(mapping, fileContent.getBytes());
            }
        } catch (Exception ex) {
            logger.error(ex);
            return false;
        }

        return true;
    }


    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 1L;

        private boolean requestStatus;
        private String id;
        private FileModel fileModel;
        private String fileSize;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }


        public boolean isRequestStatus() {
            return requestStatus;
        }

        public void setRequestStatus(boolean requestStatus) {
            this.requestStatus = requestStatus;
        }

        public FileModel getFileModel() {
            return fileModel;
        }

        public void setFileModel(FileModel fileModel) {
            this.fileModel = fileModel;
        }

        public String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getFileSize() {
            return fileSize;
        }

        public void setFileSize(String fileSize) {
            this.fileSize = fileSize;
        }
    }
}
