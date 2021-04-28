layout.addReady(function(){
    (function(layout){
        var uri = new URI(window.location.href);
        var options = uri.get("data");
        var form = uri.getData("form");
        if(form)options.printFormId = form;

        var documentId = uri.getData("documentid");
        if(documentId)options.documentId = documentId;

        var appNames = "cms.Document";
        var statusObj = null;

        var _load = function(){
            // layout.message = new MWF.xDesktop.MessageMobile();
            // layout.message.load();

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

            layout.openApplication(null, appName, option||{}, m_status);

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
    })(layout);
});
