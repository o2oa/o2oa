package com.x.program.center.jaxrs.deploy;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

/**
 * @author sword
 */
public class ActionDeployO2Server extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDeployO2Server.class);
    private static final Set<String> set = Set.of("zip");

    ActionResult<Wo> execute(EffectivePerson effectivePerson, Wi wi,
            final FormDataBodyPart filePart) throws Exception {
        if (BooleanUtils.isNotTrue(Config.general().getDeployWarEnable())) {
            throw new ExceptionDeployDisable();
        }
        String fileName = wi.getName();
        if (StringUtils.isBlank(fileName)) {
            fileName = this.fileName(filePart.getFormDataContentDisposition());
        }
        LOGGER.info("{}操作升级产品:{}.", effectivePerson.getDistinguishedName(), fileName);
        String ext = FilenameUtils.getExtension(fileName);
        if (!StringTools.isFileName(fileName) || StringUtils.isBlank(ext) || !set.contains(ext)) {
            throw new ExceptionIllegalFile(fileName);
        }
        File file = filePart.getValueAs(File.class);
        ActionResult<Wo> result = new ActionResult<>();
        List<String> list = Config.nodes().keySet().stream().filter(o -> {
            try {
                return !StringUtils.equals(o, Config.node());
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return false;
        }).collect(Collectors.toList());
        list.add(Config.node());
        List<String> resList = new ArrayList<>();
        for (String node : list) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                resList.add(
                        executeCommand("serverZip", node, Config.nodes().get(node).nodeAgentPort(),
                                inputStream, fileName));
            }
        }
        Wo wo = new Wo();
        String message = StringUtils.join(resList);
        if (message.contains("success")) {
            wi.setType("o2server");
            wo.setId(saveLog(wi, effectivePerson));
        } else {
            throw new ExceptionDeployFail();
        }
        result.setData(wo);
        Files.deleteIfExists(file.toPath());
        return result;
    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = 6597732235155964397L;

    }

}
