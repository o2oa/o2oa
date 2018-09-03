/**
 * Created by TOMMY on 2015/11/14.
 */
layout = (window["layout"]) ? window["layout"] : {};
var href = window.location.href;
if (href.indexOf("debugger")!=-1) layout["debugger"] = true;
COMMON.DOM.addReady(function(){
    var loadingNode = $("browser_loadding");
    var errorNode = $("browser_error");

    if (Browser.name=="ie" && Browser.version<10){
        if (loadingNode) loadingNode.setStyle("display", "none");
        if (errorNode) errorNode.setStyle("display", "block");
    }else if (!WebSocket || !FormData || !HTMLCanvasElement){
        if (loadingNode) loadingNode.setStyle("display", "none");
        if (errorNode) errorNode.setStyle("display", "block");
    }else{
        if (errorNode) errorNode.destroy();
        COMMON.AjaxModule.load("../res/framework/mootools/plugin/mBox.Notice.js", function(){
            COMMON.AjaxModule.load("../res/framework/mootools/plugin/mBox.Tooltip.js", function(){
                COMMON.setContentPath("/x_desktop");
                COMMON.AjaxModule.load("mwf", function(){
                    MWF.getJSON("../res/config/config.json", function(config){
                        layout.config = config;
                        document.title = layout.config.systemTitle || layout.config.systemName;

                        MWF.defaultPath = "/x_desktop"+MWF.defaultPath;
                        MWF.loadLP("zh-cn");
                        MWF.require("MWF.xDesktop.Layout", function(){
							
							MWF.require("MWF.xDesktop.Authentication", null, false);
							MWF.xDesktop.Authentication.implement({
									"loadLogin": function(){
										//http://strmgtuat.hk.chinamobile.com/x_desktop/index.html 
										var _myServerName = "http://strmgtuat.hk.chinamobile.com";
										var _casServer = "https://casdev.hk.chinamobile.com";
										if(href.indexOf("ticket=")<0){
											window.location.replace(_casServer+"/cas/login?service="+_myServerName+"/x_desktop/hkmobile/index.html");
										}else{
											window.location.replace(_myServerName+"/x_desktop/hkmobile/sso.html?ticket="+GetQueryString("ticket"));
										}	
									}
							})
							
                            layout.desktop = new MWF.xDesktop.Layout("layout", {
                                "style": "default",
                                "onLoad": function(){
                                    if (loadingNode){
                                        new Fx.Tween(loadingNode).start("opacity", 0).chain(function(){
                                            loadingNode.destroy();
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
                });
            });
        });
    }
});

function GetQueryString(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  unescape(r[2]); return null;
}
