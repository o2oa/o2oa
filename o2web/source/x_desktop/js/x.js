/**
 * Created by TOMMY on 2015/11/14.
 */

layout = (window["layout"]) ? window["layout"] : {};
var locate = window.location;
layout.protocol = locate.protocol;
layout.session = layout.session || {};
layout["debugger"] = o2.session.isDebugger;

o2.addReady(function(){
    o2.loadLP(o2.language);

    $("browser_loading_area_text").set("text", o2.LP.desktop.loadding);
    $("browser_error_area_text").set("text", o2.LP.desktop.lowBrowser);
    $("browser_error_area_up_text").set("text", o2.LP.desktop.upgradeBrowser);

    var loadingNode = $("browser_loadding");
    var errorNode = $("browser_error");

    if (Browser.name==="ie" && Browser.version<9){
        if (loadingNode) loadingNode.setStyle("display", "none");
        if (errorNode) errorNode.setStyle("display", "block");
        return false;
    }else{
        if (Browser.name==="ie" && Browser.version<10){
            layout["debugger"] = true;
            o2.session.isDebugger = true;
        }
    }
    if (errorNode) errorNode.destroy();
    errorNode = null;

    //COMMON.setContentPath("/x_desktop");
    //COMMON.AjaxModule.load("ie_adapter", function(){
    o2.load(["../o2_lib/mootools/plugin/mBox.Notice.js", "../o2_lib/mootools/plugin/mBox.Tooltip.js"], {"sequence": true}, function(){
        //o2.load("../o2_lib/mootools/plugin/mBox.Tooltip.js", function(){
            //o2.load("mwf", function(){

            o2.JSON.get("res/config/config.json", function(config){
                layout.config = config;

                if (layout.config.app_protocol==="auto"){
                    layout.config.app_protocol = window.location.protocol;
                }
                layout.config.systemName = layout.config.systemName || layout.config.footer;
                layout.config.systemTitle = layout.config.systemTitle || layout.config.title;

                document.title = layout.config.title || layout.config.systemTitle || layout.config.footer || layout.config.systemName;

                MWF.require("MWF.xDesktop.Layout", function(){
                    layout.desktop = new MWF.xDesktop.Layout("layout", {
                        //"style": "newyear",
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

            }, false);
            //});
        //});
    });
    //});
});


