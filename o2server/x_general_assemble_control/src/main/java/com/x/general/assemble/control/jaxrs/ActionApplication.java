package com.x.general.assemble.control.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.Version;
import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.general.assemble.control.jaxrs.area.AreaAction;
import com.x.general.assemble.control.jaxrs.ecnet.EcnetAction;
import com.x.general.assemble.control.jaxrs.generalfile.GeneralFileAction;
import com.x.general.assemble.control.jaxrs.office.OfficeAction;
import com.x.general.assemble.control.jaxrs.qrcode.QrCodeAction;
import com.x.general.assemble.control.jaxrs.upgrade.UpgradeAction;
import com.x.general.assemble.control.jaxrs.worktime.WorkTimeAction;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(servers = {
        @Server(url = "../../x_general_assemble_control", description = "current server.") }, info = @Info(title = "公共模块", version = Version.VALUE, description = "o2server x_general_assemble_control", license = @License(name = "AGPL-3.0", url = "https://www.o2oa.net/license.html"), contact = @Contact(url = "https://www.o2oa.net", name = "o2oa", email = "admin@o2oa.net")))
@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

    @Override
    public Set<Class<?>> getClasses() {
        classes.add(AreaAction.class);
        classes.add(EcnetAction.class);
        classes.add(OfficeAction.class);
        classes.add(WorkTimeAction.class);
        classes.add(GeneralFileAction.class);
        classes.add(UpgradeAction.class);
        classes.add(QrCodeAction.class);
        return classes;
    }

}
