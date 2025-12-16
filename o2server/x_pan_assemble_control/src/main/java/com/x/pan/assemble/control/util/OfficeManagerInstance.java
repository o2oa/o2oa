package com.x.pan.assemble.control.util;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.pan.assemble.control.Business;
import com.x.pan.core.entity.FileConfig3;
import org.apache.commons.lang3.StringUtils;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.office.LocalOfficeManager;


public class OfficeManagerInstance {

    private static Logger logger = LoggerFactory.getLogger( OfficeManagerInstance.class );

    private static OfficeManager INSTANCE = null;

    private static String[] portNumbers = {"20014","20015","20016"};

    private static String officeHome = "";
//    private static String officeHome = "/Applications/LibreOffice.app/Contents";

    public static synchronized void startInit() {
        if(INSTANCE != null) {
            stop();
        }
        FileConfig3 config = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            config = emc.firstEqual(FileConfig3.class, FileConfig3.person_FIELDNAME, Business.SYSTEM_CONFIG);
            if(config != null){
                emc.get(FileConfig3.class).detach(config);
            }
        } catch (Exception e){
            logger.debug(e.getMessage());
        }
        if(config!=null && config.getProperties()!=null && StringUtils.isNotBlank(config.getProperties().getOfficeHome())){
            try {
                officeHome = config.getProperties().getOfficeHome();
                portNumbers = config.getProperties().getPortNumbers().trim().split(",");
                init();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public static synchronized void start() {
        if(INSTANCE == null) {
            if(StringUtils.isBlank(officeHome)){
                startInit();
            }else {
                init();
            }
        }else{
            officeManagerStart();
        }
    }

    public static synchronized void stop() {
        if(INSTANCE!=null && INSTANCE.isRunning()){
            try {
                INSTANCE.stop();
                INSTANCE = null;
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private static void init() {
        try {
            int[] ports = new int[portNumbers.length];

            for (int i = 0; i < portNumbers.length; i++) {
                ports[i] = Integer.parseInt(portNumbers[i]);
            }

            LocalOfficeManager.Builder builder = LocalOfficeManager.builder().install();
            builder.officeHome(officeHome);
            builder.portNumbers(ports);
            builder.taskExecutionTimeout(Long.valueOf( 3 * 1000 * 60 ));
            builder.taskQueueTimeout(Long.valueOf(6) * 1000 * 60);
            INSTANCE = builder.build();
            officeManagerStart();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private static void officeManagerStart() {
        if (INSTANCE.isRunning()) {
            return;
        }
        try {
            INSTANCE.start();
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
