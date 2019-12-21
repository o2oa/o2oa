layout.addReady(function(){
    (function(layout){
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
            layout.node = $("layout");
            var appName=appNames, m_status=statusObj, option=options;

            var topWindow = window.opener;
            if (topWindow){
                try{
                    if (!appName) appName = topWindow.layout.desktop.openBrowserApp;
                    if (!m_status) m_status = topWindow.layout.desktop.openBrowserStatus;
                    if (!option)  option = topWindow.layout.desktop.openBrowserOption;
                }catch(e){}
            }
            layout.openApplication(null, appName, option||{}, m_status);
        };
        _load();
    })(layout);
});