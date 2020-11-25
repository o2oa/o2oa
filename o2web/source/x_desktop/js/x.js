/**
 * Created by TOMMY on 2015/11/14.
 */
layout.addReady(function(){
    (function(layout){
        layout.inBrowser = false;
        layout.desktop.type = "layout";
        var loadingNode = $("browser_loading");

        var _load = function(){
            MWF.xDesktop.getUserLayout(function(){
                layout.userLayout = layout.userLayout || {};
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

/*


layout = (window["layout"]) ? window["layout"] : {};
var locate = window.location;
layout.protocol = locate.protocol;
layout.session = layout.session || {};
layout["debugger"] = o2.session.isDebugger;



o2.addReady(function(){
    o2.loadLP(o2.language);
    var loadingNode = $("browser_loading");

    o2.require(["o2.widget.Common","o2.xDesktop.Common"], function(){
        o2.require([
            "o2.xDesktop.UserData",
            "o2.xDesktop.Actions.RestActions",
            "o2.xAction.RestActions",
            "o2.xDesktop.Authentication",
            "o2.widget.UUID",
            ["Common", ""]
        ], function(){
            MWF.xDesktop.loadService(function(){
                document.title = layout.config.title || layout.config.systemTitle || layout.config.footer || layout.config.systemName;


                debugger;
                MWF.xDesktop.checkLogin(function(){

                    var layoutClass = "Layout";
                    //var layoutClass = "Homepage";
                    MWF.require("MWF.xDesktop."+layoutClass, function(){
                        layout.desktop = new MWF.xDesktop[layoutClass]("layout", {
                            "onLoad": function(){
                                if (loadingNode){
                                    new Fx.Tween(loadingNode).start("opacity", 0).chain(function(){
                                        loadingNode.destroy();
                                        loadingNode = null;
                                    });
                                }
                            },
                            "onLogin": function(){
                                if (loadingNode){
                                    new Fx.Tween(loadingNode).start("opacity", 0).chain(function(){
                                        loadingNode.destroy();
                                    });
                                }
                            }
                        });
                    });

                });
            });
        });
    });
    o2.load("../o2_lib/mootools/plugin/mBox-all.js");
});


*/
