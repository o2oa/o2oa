layout.addReady(function(){
    if (layout.desktop.type!=="layout" && layout.desktop.type!=="app") layout.app = true;
    if (!o2.env){
        MWF.require("MWF.xScript.Macro", null, false);

        var getPage = function(){
            return {
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
        };

        var page = getPage();

        function createEnvironment(page){
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
            return new MWF.xScript.PageEnvironment(environment);
        }
        o2.env = createEnvironment(page);
        o2.apis = {};

        o2.defineProperties(o2, {
            "api": {"get": function(){
                var app = layout.desktop.currentApp || layout.app;
                if (app){
                    if (app.unique && o2.apis[app.unique]) return o2.apis[app.unique];
                    var _page = getPage();
                    var tmpApp = _page.app;
                    _page.app = app;
                    if( !_page.app.toPortal )_page.app.toPortal = tmpApp.toPortal;
                    var api = createEnvironment(_page);
                    if (!app.unique) app.unique = (new Date()).getTime().toString();
                    o2.apis[app.unique] = api;
                    return api;
                }
                return o2.env;
            }}
        });

        o2.getApi = function(app){
            if (app){
                if (app.unique && o2.apis[app.unique]) return o2.apis[app.unique];
                var _page = getPage();
                var tmpApp = _page.app;
                _page.app = app;
                if( !_page.app.toPortal )_page.app.toPortal = tmpApp.toPortal;
                var api = createEnvironment(_page);
                if (!app.unique) app.unique = (new Date()).getTime().toString();
                o2.apis[app.unique] = api;
                return api;
            }
            return o2.env;
        }
    }
});
