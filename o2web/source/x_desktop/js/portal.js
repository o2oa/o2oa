layout.addReady(function(){
    (function(layout){
        var uri = new URI(window.location.href);
        var appNames = "portal.Portal";
        var id = uri.getData("id");
        var page = uri.getData("page");
        var widget = uri.getData("widget");
        var parameters = uri.getData("parameters");
        var statusObj = null;
        var options = {"portalId": id, "pageId": page, "parameters": parameters, "widgetId":widget };

        var _load = function(){
            //o2.require("MWF.xDesktop.MessageMobile", function(){
            // layout.message = new MWF.xDesktop.MessageMobile();
            // layout.message.load();
            //}.bind(this));
            layout.apps = [];
            //layout.node = $("layout");
            layout.node = $("layout") || $("appContent") || document.body;
            var appName=appNames, m_status=statusObj, option=options;

            var topWindow = window.opener;
            if (topWindow){
                try{
                    if (!appName) appName = topWindow.layout.desktop.openBrowserApp;
                    if (!m_status && !option) m_status = topWindow.layout.desktop.openBrowserStatus;
                    if (!option && !m_status)  option = topWindow.layout.desktop.openBrowserOption;
                }catch(e){}
            }

            layout.openApplication(null, appName, option||{}, m_status);

            if (!layout.session.user || layout.session.user.name === "anonymous"){
                o2.loadCss("../o2_core/o2/xDesktop/$Default/blue/style-skin.css");
            }else{
                o2.xDesktop.getUserLayout(function(){
                    var style = layout.userLayout.flatStyle;
                    o2.loadCss("../o2_core/o2/xDesktop/$Default/"+style+"/style-skin.css");
                });
            }
        };


        // _load();

        if (layout.session && layout.session.user){
            _load();
        }else{
            if (layout.sessionPromise){
                Promise.resolve(layout.sessionPromise).then(function(json){
                    _load();
                },function(){});
            }else{
                _load();
            }
        }

        // if(!o2.portalPopstate)o2.portalPopstate = function (event) {
        //     uri = new URI(document.location.href);
        //     id = uri.getData("id");
        //     page = uri.getData("page");
        //     parameters = uri.getData("parameters");
        //     if (event.state){
        //         id = event.state.id;
        //         page = event.state.page;
        //         parameters = event.state.parameters;
        //     }
        //     // var appName = "portal.Portal";
        //     // var option = {"portalId": id, "pageId": page, "widgetId":widget };
        //     layout.app.toPortal(id, page, parameters, true);
        // }.bind(this);

        // window.addEventListener('popstate', o2.portalPopstate);
    })(layout);
});
