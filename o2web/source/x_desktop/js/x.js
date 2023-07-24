layout.addReady(function(){
    (function(layout){
        layout.inBrowser = false;
        layout.desktop.type = "layout";
        layout.app = false;
        layout.apps = [];
        var loadingNode = $("browser_loading");

        var _load = function(){
            MWF.xDesktop.getUserLayout(function(){
                layout.userLayout = layout.userLayout || {};
                if (!layout.userLayout.scale || isNaN(layout.userLayout.scale)){
                    layout.userLayout.scale = 1;
                }

                var uri = new URI(window.location.href);
                var viewMode = uri.getData("view");
                var flatStyle = uri.getData("style");
                if (flatStyle) layout.userLayout.flatStyle = flatStyle;
                if (!viewMode) viewMode = (layout.userLayout && layout.userLayout.viewMode) ? layout.userLayout.viewMode : "homepage";
                viewMode = viewMode.toLowerCase();
                //viewMode = (["flat", "home", "homepage", "default"].indexOf(viewMode)!==-1) ? "Default" : "Layout";
                viewMode = (["layout", "desktop"].indexOf(viewMode)!==-1) ? "Layout" : "Default";
                layout.viewMode = viewMode.capitalize();

                //var layoutClass = "Homepage";
                $("appContent").destroy();
                MWF.require("MWF.xDesktop."+layout.viewMode, function(){
                    layout.desktop = new MWF.xDesktop[layout.viewMode]("layout_main", {});
                    layout.desktop.load();
                    if (!layout.desktop.openApplication) layout.desktop.openApplication = layout.openApplication;
                    if (!layout.desktop.refreshApp) layout.desktop.refreshApp = layout.refreshApp;
                });
                if (loadingNode){
                    new Fx.Tween(loadingNode).start("opacity", 0).chain(function(){
                        loadingNode.destroy();
                        loadingNode = null;
                    });
                }
            });
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
