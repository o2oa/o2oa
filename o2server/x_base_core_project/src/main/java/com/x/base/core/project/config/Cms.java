package com.x.base.core.project.config;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 内容管理配置
 * @author sword
 */
public class Cms extends ConfigObject {

    private static final long serialVersionUID = 7570903222604660224L;

    public static Cms defaultInstance() {
        return new Cms();
    }

    public Cms() {
    }

    @FieldDescribe("事件扩充.")
    private ExtensionEvents extensionEvents;

    public ExtensionEvents getExtensionEvents() {
        if (null == extensionEvents) {
            this.extensionEvents = new ExtensionEvents();
        }
        return extensionEvents;
    }

    public void save() throws Exception {
        File file = new File(Config.base(), Config.PATH_CONFIG_PROCESSPLATFORM);
        FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
    }

    public static class ExtensionEvents {

        @FieldDescribe("文档附件上传.")
        private DocExtensionEvents docAttachmentUploadEvents = new DocExtensionEvents();
        @FieldDescribe("文档附件下载.")
        private DocExtensionEvents docAttachmentDownloadEvents = new DocExtensionEvents();

        public DocExtensionEvents getDocAttachmentUploadEvents() {
            if(docAttachmentUploadEvents == null){
                this.docAttachmentUploadEvents = new DocExtensionEvents();
            }
            return docAttachmentUploadEvents;
        }

        public DocExtensionEvents getDocAttachmentDownloadEvents() {
            if(docAttachmentDownloadEvents == null){
                this.docAttachmentDownloadEvents = new DocExtensionEvents();
            }
            return docAttachmentDownloadEvents;
        }
    }

    public static class DocExtensionEvents extends ArrayList<DocExtensionEvent> {

        private static final long serialVersionUID = -1039702937747301254L;

        public Optional<DocExtensionEvent> bind(String application, String category) {
            return this.stream().filter(o -> BooleanUtils.isTrue(o.getEnable()))
                    .filter(o -> (ListTools.contains(o.getApplications(), application)
                            && ListTools.contains(o.getCategories(), category))
                            || (ListTools.isEmpty(o.getApplications())
                                    && ListTools.contains(o.getCategories(), category))
                            || (ListTools.contains(o.getApplications(), application)
                                    && ListTools.isEmpty(o.getCategories()))
                            || (ListTools.isEmpty(o.getApplications()) && ListTools.isEmpty(o.getCategories())))
                    .sorted((x, y) -> {
                        if (x.getCategories().contains(category)) {
                            return 1;
                        } else if (y.getCategories().contains(category)) {
                            return -1;
                        } else if (x.getApplications().contains(application)) {
                            return 1;
                        } else if (y.getApplications().contains(application)) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }).findFirst();
        }

    }

    public static class DocExtensionEvent {

        private Boolean enable;

        private List<String> applications;
        private List<String> categories;

        private String url;

        private String custom;

        public Boolean getEnable() {
            return enable;
        }

        public List<String> getApplications() {
            return applications;
        }

        public List<String> getCategories() {
            return categories;
        }

        public String getUrl() {
            return url;
        }

        public String getCustom() {
            return custom;
        }

    }
}
