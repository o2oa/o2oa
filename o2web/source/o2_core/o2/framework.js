layout.app = true;
layout.apps = [];
layout.addReady(function(){
    // MWF.require("MWF.xScript.Environment", null, false);
    // MWF.require("MWF.xScript.PageEnvironment", null, false);
    MWF.require("MWF.xScript.Macro", null, false);


    var page = {
        "businessData": {},
        "json": {
            "application": ""
        },
        "options": {},
        "confirm": o2.xApplication.Common.Main.prototype.confirm,
        "alert": function(type, title, text, width, height){
            var p = o2.getCenterPosition(document.body, width, height);
            var e = {
                "event": {
                    "x": p.x,
                    "y": p.y,
                    "clientX": p.x,
                    "clientY": p.y
                }
            };
            o2.xApplication.Common.Main.prototype.alert(type, e, title, text, width, height);
        },
        "notice": function(content, type, target, where, offset, option){
            o2.xDesktop.notice(type, where, content, target, offset, option);
        },
        "app": {
            "desktop": layout,
            "content": document.body,
            "toPortal": function(portal, page, par, nohis){
                var url = "../x_desktop/portal.html?id="+portal;
                if (page) url += "&page="+page;
                if (par){
                    if (o2.typeOf(par)==="object"){
                        url += "&parameters="+JSON.stringify(par);
                    }else{
                        url += "&parameters="+par.toString();
                    }

                }
                var a = document.createElement("a");
                a.setAttribute("href", o2.filterUrl(url));
                a.setAttribute("target", "_blank1");
                a.click();
                if (a.remove) a.remove();
            },
            "toPage": function(){}
        },
        addEvent: function(){}
    };
    var environment = {
        "form": page,
        "forms": page.forms,
        "all": page.all,
        "data": page.businessData.data,
        "status": page.businessData.status,
        "pageInfor": page.businessData.pageInfor,
        "target": null,
        "event": null
    };
    o2.env = new MWF.xScript.PageEnvironment(environment);
});
