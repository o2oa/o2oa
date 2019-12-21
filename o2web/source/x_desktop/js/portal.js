layout.addReady(function(){
    (function(layout){
        var uri = new URI(window.location.href);
        var appNames = "portal.Portal";
        var id = uri.getData("id");
        var page = uri.getData("page");
        var widget = uri.getData("widget");
        var statusObj = null;
        var options = {"portalId": id, "pageId": page, "widgetId":widget };

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
                    if (!m_status && !option) m_status = topWindow.layout.desktop.openBrowserStatus;
                    if (!option && !m_status)  option = topWindow.layout.desktop.openBrowserOption;
                }catch(e){}
            }

            layout.openApplication(null, appName, option||{}, m_status);
        };
        _load();

        window.addEventListener('popstate', function (event) {
            debugger
            uri = new URI(document.location.href);
            id = uri.getData("id");
            page = uri.getData("page");
            if (event.state){
                id = event.state.id;
                page = event.state.page;
            }
            // var appName = "portal.Portal";
            // var option = {"portalId": id, "pageId": page, "widgetId":widget };
            layout.app.toPortal(id, page, null, true);
        }.bind(this));
    })(layout);
});
