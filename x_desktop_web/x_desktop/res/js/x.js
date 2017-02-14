/**
 * Created by TOMMY on 2015/11/14.
 */
layout = window.layout || {};
var href = window.location.href;
if (href.indexOf("debugger")!=-1) layout.debugger = true;
COMMON.DOM.addReady(function(){
    COMMON.AjaxModule.load("res/framework/mootools/plugin/mBox.Notice.js", function(){
        COMMON.AjaxModule.load("res/framework/mootools/plugin/mBox.Tooltip.js", function(){
            COMMON.setContentPath("/x_desktop");
            COMMON.AjaxModule.load("mwf", function(){
                MWF.getJSON("res/config/config.json", function(config){
                    layout.config = config;
                    MWF.defaultPath = "/x_desktop"+MWF.defaultPath;
                    MWF.loadLP("zh-cn");
                    MWF.require("MWF.xDesktop.Layout", function(){
                        layout.desktop = new MWF.xDesktop.Layout("layout", {"style": "default"});
                    });
                }, false);
                //	MWF.require("MWF.process.RestActions", function(){
                //		layout.restActions = new MWF.process.RestActions(Properties.action);
                //	});
            });
        });
    });
    //COMMON.setContentPath(Properties.path);
});

