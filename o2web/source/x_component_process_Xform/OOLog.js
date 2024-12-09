MWF.xDesktop.requireApp("process.Xform", "Log", null, false);

MWF.xApplication.process.Xform.OOLog = MWF.APPOOLog =  new Class({
    Extends: MWF.APPLog,

    textStyles:{
        "default": `<div class="logItem">
                        <div style="flex: 0 0 4em">
                            <div class="logItemIcon" style="background-image: url('/x_organization_assemble_control/jaxrs/person/{person}/icon');"></div>
                        </div>
                        <div style="flex: 1;     display: flex; justify-content: space-between; align-items: center;">
                            <div>
                            <div><font style='color:#ff5400;'>{person}</font>（{department}）选择<font style='color: var(--oo-color-main)'>【{route}】</font></div>
                            <div>处理意见：<font>{opinion}</font></div>
                            </div>
                            <div><b>{activity}</b></div>
                        </div>
                        
                        <div style="flex: 0 0 11.85em; display: block">{img}</div>
                        <div>{time}</div>
                    </div>`

    },

    getRecordTaskLineTextStyle: function(){
        return this.textStyles.default;
    },
    loadRecordTaskLine_default_currentTask: function (task, textNode, iconNode) {

        var person = (task.person) ? task.person.substring(0, task.person.indexOf("@")) : "";
        if (task.properties.empowerFromIdentity) {
            var ep = o2.name.cn(task.properties.empowerFromIdentity);
            person = person + " " + MWF.xApplication.process.Xform.LP.replace + " " + ep;
        }
        var html = this.getRecordTaskLineTextStyle();
        html = html.replace(/{person}/g, person)
        .replace(/{department}/g, o2.name.cn(task.unit))
        .replace(/{activity}/g, task.fromActivityName)
        .replace(/{route}/g, MWF.xApplication.process.Xform.LP.processing+' ...')
        .replace(/{img}/g, '')
        .replace(/{time}/g, task.properties.startTime)

        textNode.set("html", html);
        if (iconNode) iconNode.setStyle("background-image", "url(" + "../x_component_process_Xform/$Form/" + this.form.options.style + "/icon/rightRed.png)");
    }

});
