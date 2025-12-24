package com.x.ai.assemble.control.quartz;

import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.ThisApplication;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.connection.HttpConnectionResponse;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.MD5Tool;
import com.x.base.core.project.x_ai_assemble_control;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.quartz.JobExecutionContext;

/**
 * 定时检测o2ai配置
 *
 * @author sword
 */
public class InitConfigTask extends AbstractJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitConfigTask.class);
    private static final Integer SUCCESS_CODE = 200;

    @Override
    public void schedule(JobExecutionContext jobExecutionContext) {
        try {
            AiConfig config = Business.getConfig();
            if (BooleanUtils.isNotTrue(config.getO2AiEnable()) && !"no".equals(config.getO2AiBaseUrl())) {
                LOGGER.info("检测到o2ai未配置，尝试检测并配置.");
                HttpConnectionResponse response = HttpConnection.get(
                        AiConfig.DEFAULT_O2_AI_URL + "/infra-auth/who", null);
                if (SUCCESS_CODE.equals(response.getResponseCode())) {
                    config.setO2AiBaseUrl(AiConfig.DEFAULT_O2_AI_URL);
                    File file = new File(Config.dir_config(), "o2.license");
                    if (file.exists()) {
                        String lic = FileUtils.readFileToString(file, DefaultCharset.charset);
                        String token = "sk-" + MD5Tool.md5(lic);
                        config.setO2AiToken(token);
                        config.setO2AiEnable(true);
                        ThisApplication.context().applications()
                                .postQuery(x_ai_assemble_control.class,
                                        Applications.joinQueryUri("config", "save"), config);
                    }
                }
            }


        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

}
