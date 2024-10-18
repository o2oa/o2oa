layout.addReady(function(){
    (function(layout){
        debugger;
        var uri = new URI(window.location.href);
        var appNames = uri.getData("app");
        var optionsStr = uri.getData("option");
        var statusStr = uri.getData("status");
        var options = (optionsStr) ? JSON.decode(optionsStr) : null;
        var statusObj = (statusStr) ? JSON.decode(statusStr) : null;

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
                    if (!m_status) m_status = topWindow.layout.desktop.openBrowserStatus;
                    if (!option)  option = topWindow.layout.desktop.openBrowserOption;
                }catch(e){}
            }

            MWF.require("MWF.xDesktop.WebSocket", function(){
                if (!layout.desktop) {
                    layout.desktop = {}
                }
                layout.desktop.socket = new MWF.xDesktop.WebSocket();
                layout.openApplication(null, appName, option||{}, m_status);
            }.bind(this));

            if (layout.session.user.name === "anonymous"){
                o2.loadCss("../o2_core/o2/xDesktop/$Default/blue/style-skin.css");
            }else{
                o2.xDesktop.getUserLayout(function(){
                    var style = layout.userLayout.flatStyle;
                    o2.loadCss("../o2_core/o2/xDesktop/$Default/"+style+"/style-skin.css");
                });
            }



        };

        if (layout.session && layout.session.user){
            _load();


        }else{
            if (layout.sessionPromise){
                layout.sessionPromise.then(function(){
                    _load();
                },function(){});
            }
        }
        //_load();
    })(layout);
});
