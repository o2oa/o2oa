package com.x.pan.assemble.control.jaxrs.attachment3;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.file.core.entity.open.OriginFile;
import com.x.file.core.entity.personal.Attachment2;
import com.x.pan.assemble.control.Business;
import com.x.pan.assemble.control.ThisApplication;
import com.x.pan.assemble.control.entities.FileModel;
import com.x.pan.core.entity.Attachment3;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.core.Response;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

class ActionEditFileInfoWopi extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionEditFileInfoWopi.class);
    private static final String MODE_WRITE = "write";
    private static final String USER_SPLIT = "@";

    public Response execute(EffectivePerson effectivePerson, String id, String mode) throws Exception {
        logger.debug(effectivePerson.getDistinguishedName());
        if(effectivePerson.isAnonymous()){
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            String name, fileId, owner;
            Long version, size;
            Attachment3 attachment3 = emc.find(id, Attachment3.class);
            Business business = new Business(emc);
            if(attachment3 != null) {
                String zoneId = business.getSystemConfig().getReadPermissionDown() ? attachment3.getFolder() : attachment3.getZoneId();
                if (!business.zoneReadable(effectivePerson, zoneId)) {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }
                name = attachment3.getName();
                version = Long.valueOf(attachment3.getFileVersion());
                size = attachment3.getLength();
                fileId = attachment3.getOriginFile();
                owner = attachment3.getPerson();
            }else {
                Attachment2 attachment2 = emc.find(id, Attachment2.class);
                if(attachment2 != null) {
                    if (!business.controlAble(effectivePerson) && !StringUtils.equals(effectivePerson.getDistinguishedName(), attachment2.getPerson())) {
                        return Response.status(Response.Status.FORBIDDEN).build();
                    }
                    name = attachment2.getName();
                    version = attachment2.getUpdateTime().getTime();
                    size = attachment2.getLength();
                    fileId = attachment2.getOriginFile();
                    owner = attachment2.getPerson();
                }else{
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
            }
            FileModel fileInfo = new FileModel();
            fileInfo.setBaseFileName(name);
            fileInfo.setVersion(String.valueOf(version));
            fileInfo.setSize(size);
            String uid = owner.indexOf(USER_SPLIT) > -1 ? owner.split(USER_SPLIT)[1] : owner;
            fileInfo.setOwnerId(uid);
            fileInfo.setUserCanWrite(false);
            if(MODE_WRITE.equals(mode)){
                fileInfo.setUserCanWrite(true);
                if (attachment3 != null && !business.zoneEditable(effectivePerson, attachment3.getFolder(), "")) {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }
            }
            fileInfo.setSupportsLocks(true);
            String user = effectivePerson.getDistinguishedName();
            user = user.indexOf(USER_SPLIT) > -1 ? user.split(USER_SPLIT)[1] : user;
            fileInfo.setUserId(user);
            fileInfo.setUserFriendlyName(effectivePerson.getName());
            OriginFile originFile = emc.find(fileId, OriginFile.class);
            if(originFile != null){
                StorageMapping mapping = ThisApplication.context().storageMappings().get(OriginFile.class,
                        originFile.getStorage());
                try(InputStream input = new ByteArrayInputStream(originFile.readContent(mapping))){
                    fileInfo.setSha256(getHash256(input));
                }
            }
            return Response.ok(gson.toJson(fileInfo)).build();
        }
    }


    /**
     * Get SHA-256 value of file
     *
     * @param fis
     * @return
     */
    private String getHash256(InputStream fis) throws IOException, NoSuchAlgorithmException {
        String value = "";
        byte[] buffer = new byte[1024];
        int numRead;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                digest.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        value = new String(Base64.encodeBase64(digest.digest()));
        return value;
    }




}
