/*
    this.data //project的数据
    this.currentProjectGroupData //当前taskgroup的数据
 */

MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.Project = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (container, app, data, options) {
        this.setOptions(options);
        this.container = container;

        this.app = app;
        this.lp = this.app.lp.project;
        //this.actions = this.app.restActions;
        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.ProjectAction;
        //this.taskActions = this.rootActions.TaskAction;

        this.path = "/x_component_TeamWork/$Project/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        this.data = data;
    },
    load: function () {
        this.container.setStyles({display:"flex"});
        this.container.empty();
        this.createTopBarLayout();
        this.createContentLayout();

        this.topBarTabItemTask.click();
    },
    createTopBarLayout:function(){
        var _self = this;
        this.topBarLayout = new Element("div.topBarLayout",{styles:this.css.topBarLayout}).inject(this.container);
        this.topBarBackContainer = new Element("div.topBarBackContainer",{styles:this.css.topBarBackContainer}).inject(this.topBarLayout);
        this.topBarBackHomeIcon = new Element("div.topBarBackHomeIcon",{styles:this.css.topBarBackHomeIcon}).inject(this.topBarBackContainer);
        this.topBarBackHomeIcon.addEvents({
            click:function(){
                var pl = new MWF.xApplication.TeamWork.ProjectList(this.container,this.app,this.actions,{});
                pl.load();
            }.bind(this),
            mouseover:function(){
                var opt={
                    axis: "y"      //箭头在x轴还是y轴上展现
                };
                //this.app.showTips(this.topBarBackHomeIcon,{_html:"<div style='margin:2px 5px;'>"+this.lp.backProject+"</div>"},opt);
                //this.app.tips(this.topBarBackHomeIcon,this.lp.backProject);
            }.bind(this)
        });
        this.topBarBackHomeNext = new Element("div.topBarBackHomeNext",{styles:this.css.topBarBackHomeNext}).inject(this.topBarBackContainer);
        this.topBarBackHomeTextContainer = new Element("div.topBarBackHomeTextContainer",{styles:this.css.topBarBackHomeTextContainer}).inject(this.topBarBackContainer);
        this.topBarBackHomeText = new Element("div.topBarBackHomeText",{styles:this.css.topBarBackHomeText,text:this.data.title}).inject(this.topBarBackHomeTextContainer);
        this.topBarBackHomeArrow = new Element("div.topBarBackHomeArrow",{styles:this.css.topBarBackHomeArrow}).inject(this.topBarBackHomeTextContainer);
        this.topBarBackHomeTextContainer.addEvents({
            click:function(){
                var plist = new MWF.xApplication.TeamWork.Project.ProjectList(this.container, this.topBarBackHomeTextContainer, this.app, this.data, {
                    css:this.css,
                    lp:this.lp,
                    axis : "y",
                    position : { //node 固定的位置
                        x : "right", //x轴上left center right,  auto 系统自动计算
                        y : "auto" //y 轴上top middle bottom, auto 系统自动计算
                    },
                    priorityOfAuto :{
                        x : [ "center", "right", "left" ], //当position x 为 auto 时候的优先级
                        y : [ "middle", "bottom", "top" ] //当position y 为 auto 时候的优先级
                    },
                    nodeStyles : {
                        "min-width":"300px",
                        "padding":"2px",
                        "border-radius":"5px",
                        "z-index" : "101"
                    }
                });
                plist.load();
            }.bind(this),
            mouseover:function(){this.getElements("div").setStyles({"color":"#4a90e2"})},
            mouseout:function () { this.getElements("div").setStyles({"color":"#333333"}) }
        });

        this.topBarTabsContainer = new Element("div.topBarTabsContainer",{styles:this.css.topBarTabsContainer}).inject(this.topBarLayout);
        this.topBarTabItemTask = new Element("div.topBarTabItemTask",{styles:this.css.topBarTabItemTask,text:this.lp.task}).inject(this.topBarTabsContainer);
        this.topBarTabItemTask.addEvents({
            click:function(){
                this.topBarTabItemTask.setStyles({"color":"#4A90E2","border-bottom":"2px solid #4A90E2"});
                this.topBarTabItemStat.setStyles({"color":"","border-bottom":"0px"});
                this.createTaskLayout();
            }.bind(this)
        });
        this.topBarTabItemStat = new Element("div.topBarTabItemStat",{styles:this.css.topBarTabItemStat,text:this.lp.stat}).inject(this.topBarTabsContainer);
        this.topBarTabItemStat.addEvents({
            click:function(){
                this.topBarTabItemTask.setStyles({"color":"","border-bottom":"0px"});
                this.topBarTabItemStat.setStyles({"color":"#4A90E2","border-bottom":"2px solid #4A90E2"});
                this.createStatLayout();
            }.bind(this)
        });


        //********************************






        //************************************




        this.topBarSettingContainer = new Element("div.topBarSettingContainer",{styles:this.css.topBarSettingContainer}).inject(this.topBarLayout);
        this.topBarSettingMenuContainer = new Element("div.topBarSettingMenuContainer",{styles:this.css.topBarSettingMenuContainer}).inject(this.topBarSettingContainer);
        this.topBarSettingMenuContainer.addEvents({
            click:function(){
                MWF.xDesktop.requireApp("TeamWork", "ProjectSetting", function(){
                    var ps = new MWF.xApplication.TeamWork.ProjectSetting(this,this.data,
                        {"width": "800","height": "80%",
                            onPostOpen:function(){
                                ps.formAreaNode.setStyles({"top":"10px"});
                                var fx = new Fx.Tween(ps.formAreaNode,{duration:200});
                                fx.start(["top"] ,"10px", "100px");
                            },
                            onPostClose:function(json){

                            }
                        },{
                            container : this.container,
                            lp : this.app.lp.projectSetting,
                            css:_self.css

                        }
                    );
                    ps.open();
                }.bind(this));
            }.bind(this),
            mouseover:function(){
                this.topBarSettingMenuContainer.getElements(".topBarSettingMenuIcon").setStyles({
                    "background-image":"url(/x_component_TeamWork/$Project/default/icon/icon_caidan_click.png)"
                });
                this.topBarSettingMenuContainer.getElements(".topBarSettingMenuText").setStyles({
                    "color":"#4A90E2"
                });
            }.bind(this),
            mouseout:function(){
                this.topBarSettingMenuContainer.getElements(".topBarSettingMenuIcon").setStyles({
                    "background-image":"url(/x_component_TeamWork/$Project/default/icon/icon_caidan.png)"
                });
                this.topBarSettingMenuContainer.getElements(".topBarSettingMenuText").setStyles({
                    "color":"#666666"
                });
            }.bind(this)
        });
        this.topBarSettingMenuIcon = new Element("div.topBarSettingMenuIcon",{styles:this.css.topBarSettingMenuIcon}).inject(this.topBarSettingMenuContainer);
        this.topBarSettingMenuText = new Element("div.topBarSettingMenuText",{styles:this.css.topBarSettingMenuText,text:this.lp.setting}).inject(this.topBarSettingMenuContainer);

    },
    createContentLayout:function(){
        this.contentLayout = new Element("div.contentLayout",{styles:this.css.contentLayout}).inject(this.container);
    },
    createTaskLayout:function(){
        if(this.contentLayout) this.contentLayout.empty();
        this.createNaviContent();
        this.createFoldContainer();
        this.createTaskContent();

    },
    createFoldContainer:function(){
        if(this.foldContainer) this.foldContainer.destroy();
        this.foldContainer = new Element("div.foldContainer",{styles:this.css.foldContainer}).inject(this.contentLayout);
        this.foldIcon = new Element("div.foldIcon",{styles:this.css.foldIcon,text:"<"}).inject(this.foldContainer);
        this.app.tips(this.foldIcon,"折叠");
        this.foldIcon.addEvents({
            mouseover:function(){
                this.naviLayout.setStyles({"box-shadow": "1px 2px 6px 0px #1b9aee"});
                this.foldIcon.setStyles({"background-color":"#1b9aee","color":"#ffffff"});
            }.bind(this),
            mouseout:function(){
                this.naviLayout.setStyles({"box-shadow": "0px 2px 4px 0 #888888"});
                this.foldIcon.setStyles({"background-color":"#ffffff","color":""});
            }.bind(this),
            click:function(){
                if(this.naviFold){
                    var fx = new Fx.Tween(this.naviLayout,{duration:100});
                    this.naviLayout.show();
                    fx.start(["width"] ,"0", "300")
                        .chain(function(){
                            this.foldIcon.set("text","<");
                            this.naviFold = false;
                            this.app.tips(this.foldIcon,"折叠11");
                        }.bind(this));

                }else{ //aaaaa
                    var fx = new Fx.Tween(this.naviLayout,{duration:100});
                    fx.start(["width"] ,"300", "0")
                        .chain(function(){
                            this.naviLayout.hide();
                            this.foldIcon.set("text",">");
                            this.naviFold = true;
                            //console.log("zzzzzzzzzzzzzzzzzzzzzk");
                            this.app.tips(this.foldIcon,"展开22");
                        }.bind(this));

                }
            }.bind(this)
        })
        var _margin_height = (this.foldContainer.getHeight())/2 - (this.foldIcon.getHeight())/2;
        this.foldIcon.setStyles({"margin-top":_margin_height+"px"});
    },
    createNaviContent:function(){
        if(this.naviLayout) this.naviLayout.destroy();
        this.naviLayout = new Element("div.naviLayout",{styles:this.css.naviLayout}).inject(this.contentLayout);
        this.app.setLoading(this.naviLayout);
        this.rootActions.TaskAction.statiticMyProject(this.data.id,function(json){
            this.projectGroupData = json.data;
            if(this.projectGroupData.groups && this.projectGroupData.groups.length>0){
                this.currentProjectGroupData = this.projectGroupData.groups[0]; //默认只有一个分组
            }
            this.naviLayout.empty();
            this.createNaviTask();
            this.createNaviView();

            this.naviTopMyTaskLayout.click();
        }.bind(this));
    },
    createNaviTask:function(){
        var _self = this;
        if(!this.currentProjectGroupData) return;
        if(this.naviTop) this.naviTop.destroy();
        this.naviTop = new Element("div.naviTop",{styles:this.css.naviTop}).inject(this.naviLayout);

        this.naviTopSearchContainer = new Element("div.naviTopSearchContainer",{styles:this.css.naviTopSearchContainer}).inject(this.naviTop);
        this.naviTopSearchIn = new Element("input.naviTopSearchIn",{styles:this.css.naviTopSearchIn,placeholder:this.lp.searchTask}).inject(this.naviTopSearchContainer);
        this.naviTopSearchIn.addEvents({
            keypress:function(e){
                this.searchLoading = true;
                var keycode = (e.event.keyCode ? e.event.keyCode : e.event.which);
                if (keycode == 13 || keycode == 10) {
                    var key = this.naviTopSearchIn.get("value").trim();
                    if(key=="") return;
                    if(this.searchLoading) this.openSearch(key)
                }
            }.bind(this),
            focus:function(){
                this.naviTopSearchContainer.setStyles({"border":"1px solid #4A90E2"})
            }.bind(this),
            blur:function(){
                this.naviTopSearchContainer.setStyles({"border":"1px solid #DEDEDE"})
            }.bind(this)
        });

        this.naviTopTaskContainer = new Element("div.naviTopTaskContainer",{styles:this.css.naviTopTaskContainer}).inject(this.naviTop);
        this.naviTopTaskText = new Element("div.naviTopTaskText",{styles:this.css.naviTopTaskText,text:this.lp.task}).inject(this.naviTopTaskContainer);
        this.naviTopTaskAdd = new Element("div.naviTopTaskAdd",{styles:this.css.naviTopTaskAdd}).inject(this.naviTopTaskContainer);
        this.naviTopTaskAdd.addEvents({
            click:function(){
                var data = {
                    projectObj:this,
                    taskGroupId:this.currentProjectGroupData.id,
                    //projectId:this.data.id,
                    taskListIds:[]
                };
                var opt = {
                    onCreateTask:function(){
                        this.createTaskGroup();
                    }.bind(this)
                };
                var newTask = new MWF.xApplication.TeamWork.Project.NewTask(this,data,opt,{});
                newTask.open();

            }.bind(this),
            mouseover:function(){
                this.naviTopTaskAdd.setStyles({
                    "background-image":"url(/x_component_TeamWork/$Project/default/icon/icon_zengjia_blue2_click.png)"
                });
                //this.app.showTips(this.naviTopTaskAdd,{_html:"<div style='margin:2px 5px;'>"+this.lp.taskAdd+"</div>"});
                this.app.tips(this.naviTopTaskAdd,this.lp.taskAdd)
            }.bind(this),
            mouseout:function(){
                this.naviTopTaskAdd.setStyles({
                    "background-image":"url(/x_component_TeamWork/$Project/default/icon/icon_jia.png)"
                });
            }.bind(this)
        });

        this.naviTopMyTaskLayout = new Element("div.naviTopMyTaskLayout",{styles:this.css.naviTopMyTaskLayout}).inject(this.naviTop);
        this.naviTopMyTaskLayout.addEvents({
            click:function(){
                this.curNaviItem = "group";
                this.naviTopMyTaskLayout.setStyles({"background-color":"#F2F5F7"});
                this.naviViewContainer.getElements(".naviItemContainer").each(function(d){
                    this.naviItemChange(d,"leave")
                }.bind(this));
                this.createTaskGroup();
            }.bind(this),
            mouseover:function(){ if(_self.curNaviItem!="group") this.setStyles({"background-color":"#F2F5F7"}) },
            mouseout:function(){ if(_self.curNaviItem!="group") this.setStyles({"background-color":""}) }
        });
        this.naviTopMyTaskContainer = new Element("div.naviTopMyTaskContainer",{styles:this.css.naviTopMyTaskContainer}).inject(this.naviTopMyTaskLayout);
        this.naviTopMyTaskText = new Element("div.naviTopMyTaskText",{styles:this.css.naviTopMyTaskText,text:this.lp.myTask}).inject(this.naviTopMyTaskContainer);
        this.naviTopMyTaskCount = new Element("div.naviTopMyTaskCount",{styles:this.css.naviTopMyTaskCount,text:"("+this.currentProjectGroupData.completedTotal+"/"+this.currentProjectGroupData.taskTotal+")"}).inject(this.naviTopMyTaskContainer);

        this.naviTopTaskLineContainer = new Element("div.naviTopTaskLineContainer",{styles:this.css.naviTopTaskLineContainer}).inject(this.naviTopMyTaskLayout);
        this.naviTopTaskLine = new Element("div.naviTopTaskLine",{styles:this.css.naviTopTaskLine}).inject(this.naviTopTaskLineContainer);
        this.loadTaskLine();

        //this.naviTopMyTaskLayout.click();
    },
    loadTaskLine:function(){
        this.completeLine = new Element("div.completeLine",{styles:this.css.completeLine}).inject(this.naviTopTaskLine);
        this.completeLine.addEvents({
            mouseover:function(){
                //this.app.showTips(this.completeLine,{_html:"<div style='margin:2px 5px;'>"+this.lp.taskCompleteText+":"+this.currentProjectGroupData.completedTotal+"</div>"});
                this.app.tips(this.completeLine,this.lp.taskCompleteText + ": " + this.currentProjectGroupData.completedTotal);
            }.bind(this)
        });
        this.overLine = new Element("div.overLine",{styles:this.css.overLine}).inject(this.naviTopTaskLine);
        this.overLine.addEvents({
            mouseover:function(){
                //this.app.showTips(this.overLine,{_html:"<div style='margin:2px 5px;'>"+this.lp.taskCompleteText+":"+this.currentProjectGroupData.overtimeTotal+"</div>"});
                //alert(this.currentProjectGroupData.overtimeTotal)
                this.app.tips(this.overLine,this.lp.taskovertimeText + ": " + this.currentProjectGroupData.overtimeTotal)
            }.bind(this)
        });

        if(this.currentProjectGroupData){
            var taskTotal = this.currentProjectGroupData.taskTotal;
            var completedTotal = this.currentProjectGroupData.completedTotal;
            var overtimeTotal = this.currentProjectGroupData.overtimeTotal;

            //alert(taskTotal);alert(completedTotal);alert(overtimeTotal);
            if(taskTotal){
                var _width = this.naviTopTaskLine.getWidth();
                var completePixel =_width * (completedTotal / taskTotal);
                var overPixel =_width * (overtimeTotal / taskTotal);
                this.completeLine.setStyles({"width":completePixel+"px"});
                this.overLine.setStyles({"width":overPixel+"px"});
            }
        }
    },
    createNaviView:function(){
        if(this.naviView) this.naviView.destroy();
        this.naviView = new Element("div.naviView",{styles:this.css.naviView}).inject(this.naviLayout);
        this.naviViewTitleContainer = new Element("div.naviViewTitleContainer",{styles:this.css.naviViewTitleContainer}).inject(this.naviView);
        this.naviViewTitle = new Element("div.naviViewTitle",{styles:this.css.naviViewTitle,text:this.lp.viewTitle}).inject(this.naviViewTitleContainer);

        /*  新增按钮
        this.naviViewAdd = new Element("div.naviViewAdd",{styles:this.css.naviViewAdd}).inject(this.naviViewTitleContainer);
        this.naviViewAdd.addEvents({
            click:function(){

            }.bind(this),
            mouseover:function(){
                this.naviViewAdd.setStyles({
                    "background-image":"url(/x_component_TeamWork/$Project/default/icon/icon_zengjia_blue2_click.png)"
                });
                this.app.showTips(this.naviViewAdd,{_html:"<div style='margin:2px 5px;'>"+this.lp.viewAdd+"</div>"});
            }.bind(this),
            mouseout:function(){
                this.naviViewAdd.setStyles({
                    "background-image":"url(/x_component_TeamWork/$Project/default/icon/icon_jia.png)"
                });
            }.bind(this)
        });
        */

        this.createNaviViewItem();
    },
    createNaviViewItem:function(){
        var _self = this;
        if(this.naviViewContainer) this.naviViewContainer.destroy();
        this.naviViewContainer = new Element("div.naviViewContainer",{styles:this.css.naviViewContainer}).inject(this.naviView);

        this.viewData = this.viewData || this.projectGroupData.views;
        this.viewData.each(function(json){
            if(json.name==this.lp.viewItemAll){//所有任务
                this.naviItemAllContainer = new Element("div.naviItemContainer",{styles:this.css.naviItemContainer}).inject(this.naviViewContainer);
                this.naviItemAllIcon = new Element("div.naviItemAllIcon",{styles:this.css.naviItemAllIcon}).inject(this.naviItemAllContainer);
                this.naviItemAllText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:this.lp.viewItemAll}).inject(this.naviItemAllContainer);
                this.naviItemAllContainer.addEvents({
                    click:function(){
                        this.curNaviItem = json.name;
                        this.openView(this.naviItemAllContainer);
                    }.bind(this),
                    mouseenter:function(){ if(_self.curNaviItem != json.name) _self.naviItemChange(this,"enter")},
                    mouseleave:function(){ if(_self.curNaviItem != json.name) _self.naviItemChange(this,"leave")}
                });
            }else if(json.name==this.lp.viewItemMy){
                //我的任务
                this.naviItemMyContainer = new Element("div.naviItemContainer",{styles:this.css.naviItemContainer}).inject(this.naviViewContainer);
                this.naviItemMyIcon = new Element("div.naviItemMyIcon",{styles:this.css.naviItemMyIcon}).inject(this.naviItemMyContainer);
                this.naviItemMyText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:this.lp.viewItemMy}).inject(this.naviItemMyContainer);
                // this.naviItemMyMore = new Element("div.naviItemMore",{styles:this.css.naviItemMore}).inject(this.naviItemMyContainer);
                // this.naviItemMyMore.addEvent(
                //     "click",function(e){
                //         this.openNaviViewMore(this.naviItemMyMore);
                //         e.stopPropagation();
                //     }.bind(this)
                // );
                this.naviItemMyContainer.addEvents({
                    click:function(){
                        this.curNaviItem = json.name;
                        this.openView(this.naviItemMyContainer);
                    }.bind(this),
                    mouseenter:function(){ if(_self.curNaviItem != json.name) _self.naviItemChange(this,"enter")},
                    mouseleave:function(){ if(_self.curNaviItem != json.name) _self.naviItemChange(this,"leave")}
                });
            }else if(json.name==this.lp.viewItemFlow){
                //未完成的任务
                this.naviItemFlowContainer = new Element("div.naviItemContainer",{styles:this.css.naviItemContainer}).inject(this.naviViewContainer);
                this.naviItemFlowIcon = new Element("div.naviItemFlowIcon",{styles:this.css.naviItemFlowIcon}).inject(this.naviItemFlowContainer);
                this.naviItemFlowText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:this.lp.viewItemFlow}).inject(this.naviItemFlowContainer);
                //this.naviItemFlowMore = new Element("div.naviItemMore",{styles:this.css.naviItemMore}).inject(this.naviItemFlowContainer);
                // this.naviItemFlowMore.addEvent(
                //     "click",function(e){
                //         this.openNaviViewMore(this.naviItemFlowMore);
                //         e.stopPropagation();
                //     }.bind(this)
                // );
                this.naviItemFlowContainer.addEvents({
                    click:function(){
                        this.curNaviItem = json.name;
                        this.openView(this.naviItemFlowContainer);
                    }.bind(this),
                    mouseenter:function(){ if(_self.curNaviItem != json.name) _self.naviItemChange(this,"enter")},
                    mouseleave:function(){ if(_self.curNaviItem != json.name) _self.naviItemChange(this,"leave")}
                });
            }else if(json.name==this.lp.viewItemComplete){
                //已完成任务
                this.naviItemCompleteContainer = new Element("div.naviItemContainer",{styles:this.css.naviItemContainer}).inject(this.naviViewContainer);
                this.naviItemCompleteIcon = new Element("div.naviItemCompleteIcon",{styles:this.css.naviItemCompleteIcon}).inject(this.naviItemCompleteContainer);
                this.naviItemCompleteText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:this.lp.viewItemComplete}).inject(this.naviItemCompleteContainer);
                //this.naviItemCompleteMore = new Element("div.naviItemMore",{styles:this.css.naviItemMore}).inject(this.naviItemCompleteContainer);
                // this.naviItemCompleteMore.addEvent(
                //     "click",function(e){
                //         this.openNaviViewMore(this.naviItemCompleteMore);
                //         e.stopPropagation();
                //     }.bind(this)
                // );
                this.naviItemCompleteContainer.addEvents({
                    click:function(){
                        this.curNaviItem = json.name;
                        this.openView(this.naviItemCompleteContainer);
                    }.bind(this),
                    mouseenter:function(){ if(_self.curNaviItem != json.name) _self.naviItemChange(this,"enter")},
                    mouseleave:function(){ if(_self.curNaviItem != json.name) _self.naviItemChange(this,"leave")}
                });
            }else if(json.name==this.lp.viewItemOver){
                //已逾期任务
                this.naviItemOverContainer = new Element("div.naviItemContainer",{styles:this.css.naviItemContainer}).inject(this.naviViewContainer);
                this.naviItemOverIcon = new Element("div.naviItemOverIcon",{styles:this.css.naviItemOverIcon}).inject(this.naviItemOverContainer);
                this.naviItemOverText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:this.lp.viewItemOver}).inject(this.naviItemOverContainer);
                // this.naviItemOverMore = new Element("div.naviItemMore",{styles:this.css.naviItemMore}).inject(this.naviItemOverContainer);
                // this.naviItemOverMore.addEvent(
                //     "click",function(e){
                //         this.openNaviViewMore(this.naviItemOverMore);
                //         e.stopPropagation();
                //     }.bind(this)
                // );
                this.naviItemOverContainer.addEvents({
                    click:function(){
                        this.curNaviItem = json.name;
                        this.openView(this.naviItemOverContainer);
                    }.bind(this),
                    mouseenter:function(){ if(_self.curNaviItem != json.name) _self.naviItemChange(this,"enter")},
                    mouseleave:function(){ if(_self.curNaviItem != json.name) _self.naviItemChange(this,"leave")}
                });
            }else{
                //自定义视图

            }
        }.bind(this));

    },
    naviItemChange:function(node,action){
        if(action == "enter"){
            node.setStyles({"background-color":"#F2F5F7"});
            divs = node.getElements("div");
            divs[0].setStyles({"background-image":divs[0].getStyle("background-image").replace(".png","_click.png")});
            divs[1].setStyles({"color":"#4A90E2"});
            if(divs[2]){
                divs[2].setStyles({"background-image":divs[2].getStyle("background-image").replace(".png","_click.png")});
            }
        }else if(action == "leave"){
            node.setStyles({"background-color":""});
            divs = node.getElements("div");
            divs[0].setStyles({"background-image":divs[0].getStyle("background-image").replace("_click.png",".png")});
            divs[1].setStyles({"color":"#2A2A2A"});
            if(divs[2]){
                divs[2].setStyles({"background-image":divs[2].getStyle("background-image").replace("_click.png",".png")});
            }
        }
    },
    openNaviViewMore:function(node){
        var tooltip = new MWF.xApplication.TeamWork.Project.NaviViewTip(this.container, node, this.app, {}, {
            css:this.css,
            lp:this.lp,
            axis : "y",
            nodeStyles : {
                "min-width":"100px",
                "padding":"2px",
                "border-radius":"5px",
                "z-index" : "101"
            },
            onPostLoad:function(){
                tooltip.node.setStyles({"opacity":"0"});
                var fx = new Fx.Tween(tooltip.node,{duration:200});
                fx.start(["opacity"] ,"0", "1");

            },
            onClose:function(rd){
                // customGroupItemContainer.set("atIn","no");
                // if(rd)_self.reloadProjectGroup();
            }
        });
        tooltip.load();
    },
    createTaskContent:function(){
        this.taskContentLayout = new Element("div.taskContentLayout",{styles:this.css.taskContentLayout}).inject(this.contentLayout);
    },
    createTaskGroup:function(){  //右侧内容
        if(this.currentProjectGroupData && this.currentProjectGroupData.id){
            this.app.setLoading(this.taskContentLayout);
            //this.actions.taskGroupList(this.currentProjectGroupData.id,function(json){
            this.rootActions.TaskListAction.listWithTaskGroup(this.currentProjectGroupData.id,function(json){
                this.taskContentLayout.empty();
                json.data.each(function(data,i){
                    //if(i>0) return;
                    var taskGroupLayout = new Element("div.taskGroupLayout",{styles:this.css.taskGroupLayout,id:data.id}).inject(this.taskContentLayout);
                    taskGroupLayout.set("sortable",data.control.sortable); //控制是否能排序
                    // if(!data.control.sortable){
                    //     taskGroupLayout.set()
                    // }
                    this.createTaskGroupItemLayout(taskGroupLayout,data);
                }.bind(this));
                //新建任务列表按钮
                this.newTaskGroupContainer = new Element("div.newTaskGroupContainer",{styles:this.css.newTaskGroupContainer}).inject(this.taskContentLayout);
                this.newTaskGroupContainer.addEvents({
                    click:function(){
                        var data = {
                            isNew:true,
                            taskGroupId:this.currentProjectGroupData.id,
                            projectId:this.data.id
                        };
                        var opt = {
                            onCreateTask:function(){
                                this.createTaskGroup();
                            }.bind(this)
                        };
                        var newTaskGroup = new MWF.xApplication.TeamWork.Project.NewTaskGroup(this,data,opt,{});
                        newTaskGroup.open();
                    }.bind(this),
                    mouseover:function(){
                        this.newTaskGroupIcon.setStyles({"background-image":"url(/x_component_TeamWork/$Project/default/icon/icon_jia_20_click.png)"});
                        this.newTaskGroupText.setStyles({"color":"#4A90E2","font-size":"16px"});
                    }.bind(this),
                    mouseout:function(){
                        this.newTaskGroupIcon.setStyles({"background-image":"url(/x_component_TeamWork/$Project/default/icon/icon_jia.png)"});
                        this.newTaskGroupText.setStyles({"color":"#999999","font-size":"12px"});
                    }.bind(this)
                });
                this.newTaskGroupIcon = new Element("div.newTaskGroupIcon",{styles:this.css.newTaskGroupIcon}).inject(this.newTaskGroupContainer);
                this.newTaskGroupText = new Element("div.newTaskGroupText",{styles:this.css.newTaskGroupText,text:this.lp.taskGroupAdd}).inject(this.newTaskGroupContainer);
            }.bind(this))
        }
    },
    createTaskGroupItemLayout:function(node,data){
        var _self = this;
        node.empty();
        var taskGroupItemTitleContainer = new Element("div.taskGroupItemTitleContainer",{styles:this.css.taskGroupItemTitleContainer}).inject(node);
        new Element("div.taskGroupItemTitleText",{styles:this.css.taskGroupItemTitleText,text:data.name}).inject(taskGroupItemTitleContainer);
        var titleCount = new Element("div.taskGroupItemTitleCount",{styles:this.css.taskGroupItemTitleCount,text:"(-)"}).inject(taskGroupItemTitleContainer);
        if(data.control.sortable){
            var taskGroupItemTitleMore = new Element("div.taskGroupItemTitleMore",{styles:this.css.taskGroupItemTitleMore}).inject(taskGroupItemTitleContainer);
            taskGroupItemTitleMore.addEvents({
                click:function(){
                    data.projectObj = this;
                    data.node = node;
                    var menu = new MWF.xApplication.TeamWork.Project.TaskGroupMenu(this.container, taskGroupItemTitleMore, this.app, data, {
                        css:this.css,
                        lp:this.lp,
                        axis : "y",
                        nodeStyles : {
                            "min-width":"100px",
                            "padding":"2px",
                            "border-radius":"5px",
                            "z-index" : "101"
                        },
                        onPostLoad:function(){
                            menu.node.setStyles({"opacity":"0","top":(menu.node.getStyle("top").toInt()-10)+"px"});
                            var fx = new Fx.Tween(menu.node,{duration:200});
                            fx.start(["opacity"] ,"0", "1");
                        },
                        onClose:function(rd){
                            if(!rd)return;
                            _self.createTaskGroup()
                        }
                    });
                    menu.load();
                }.bind(this),
                mouseover:function(){this.setStyles({"background-image":"url(/x_component_TeamWork/$Project/default/icon/icon_more_click.png)"})},
                mouseout:function(){this.setStyles({"background-image":"url(/x_component_TeamWork/$Project/default/icon/icon_more.png)"})}
            });
        }

        var taskGroupItemTitleAdd = new Element("div.taskGroupItemTitleAdd",{styles:this.css.taskGroupItemTitleAdd}).inject(taskGroupItemTitleContainer);
        if(!data.control.sortable) taskGroupItemTitleAdd.setStyle("margin-right","20px");
        taskGroupItemTitleAdd.addEvents({
            click:function(){
                var pdata = {
                    projectObj:this,
                    taskGroupId:this.currentProjectGroupData.id,
                    //projectId:this.data.id,
                    taskListIds:[data.id]
                };

                var opt = {
                    onCreateTask:function(){
                        this.createTaskGroup();
                    }.bind(this)
                };
                var newTask = new MWF.xApplication.TeamWork.Project.NewTask(this,pdata,opt,{});
                newTask.open();
            }.bind(this),
            mouseover:function(){this.setStyles({"background-image":"url(/x_component_TeamWork/$Project/default/icon/icon_zengjia_blue2_click.png)"})},
            mouseout:function(){this.setStyles({"background-image":"url(/x_component_TeamWork/$Project/default/icon/icon_jia.png)"})}
        });

        var taskGroupItemTitleReload = new Element("div.taskGroupItemTitleReload",{styles:this.css.taskGroupItemTitleReload, title:this.lp.reload}).inject(taskGroupItemTitleContainer);
        //if(!data.control.sortable) taskGroupItemTitleAdd.setStyle("margin-right","20px");
        taskGroupItemTitleReload.addEvents({
            click:function(){
                this.createTaskGroupItemLayout(node,data)
            }.bind(this)
        });


        var taskGroupItemContainer = new Element("div.taskGroupItemContainer",{styles:this.css.taskGroupItemContainer}).inject(node);
        var _h = node.getHeight().toInt()-taskGroupItemTitleContainer.getHeight().toInt() - 10 - 10;

        taskGroupItemContainer.setStyles({"height":_h+"px"});
        this.app.setScrollBar(taskGroupItemContainer);
        this.app.setLoading(taskGroupItemContainer);

        //this.actions.taskListByListId(this.data.id,data.id,function(json){
        this.rootActions.TaskAction.listAllTaskWithTaskListId(this.data.id,data.id,function(json){
            titleCount.set("text","("+json.count+")")
            taskGroupItemContainer.empty();
            var taskListData = json.data;
            //alert(JSON.stringify(taskListData));
            taskListData.each(function(d){
                //var taskItemContainerDrag = new Element("div.taskItemContainerDrag.dragin",{styles:this.css.taskItemContainerDrag}).inject(taskGroupItemContainer);
                var taskItemContainer = new Element("div.taskItemContainer.dragin",{styles:this.css.taskItemContainer}).inject(taskGroupItemContainer);
                taskItemContainer.set("id",d.id);
                this.loadTaskNode(taskItemContainer,d);

                var int;
                var upTime = 0;
                var time = 200;
                taskItemContainer.addEvents({
                    click:function(e){
                        this.openTask(d.id,function(){ taskItemContainer.destroy() }.bind(this));
                    }.bind(this),
                    mouseenter:function(){
                        int = window.setInterval(function(){
                            if(taskItemContainer.getElement(".taskItemHover").getWidth() == 10) return;
                            taskItemContainer.getElement(".taskItemHover").setStyles({
                                "width":(taskItemContainer.getElement(".taskItemHover").getWidth()+1)+"px"
                            });
                        }.bind(this),30)
                    },
                    mouseleave:function(){
                        window.clearInterval(int);
                        taskItemContainer.getElement(".taskItemHover").setStyles({"width":"5px"});
                    }.bind(this),

                });

                var drag = new Drag(taskItemContainer, {
                    "compensateScroll": true,
                    //preventDefault:true,
                    //stopPropagation:true,
                    //container:this.contentLayout,
                    //container: this.taskContentLayout,
                    "onStart": function(el, e){
                        this.dragMove(el,e);
                        drag.stop();
                    }.bind(this)
                });

            }.bind(this));

            //最后加一行占位
            var emptyDrag = new Element("div.empty.dragin",{name:"item",styles:{
                    "width":"100%", "height":"50px"
                }}).inject(taskGroupItemContainer);

        }.bind(this));

    },

    dragMove:function(el,e){
        var _self = this;
        var taskItemContainer = el;
        var time = 200;
        //this.cloneTaskItem = new Element("").inject()
        var position = el.getPosition(this.taskContentLayout);
        //alert(JSON.stringify(position))
        //var clone = this.cloneTaskItem = el.clone(true,true).inject($(document.body));
        var clone = this.cloneTaskItem = el.clone(true,true).inject(this.taskContentLayout);
        this.cloneTaskItem.removeClass("dragin");
        this.cloneTaskItem.setStyles({
            "top":position.y+"px",
            "left":(position.x + this.taskContentLayout.getScrollLeft())+"px",
            "z-index":"9999",
            "margin":"0px",
            "position":"absolute",
            "cursor":"move"
        });
        el.setStyles({"border":"1px dotted #000000","opacity":"0.3"});
        var _height = this.cloneTaskItem.getHeight().toInt() - 2;

        this.ccc = 0;
        var drag = new Drag.Move(this.cloneTaskItem, {
            container: this.taskContentLayout,
            //container: this.contentLayout,
            //"stopPropagation": true,
            "compensateScroll": true,
            "droppables": $$(".dragin"),
            "onStart": function(){
                // this.topBarTabItemStat.set("text",JSON.stringify(el.getPosition()));
            }.bind(this),
            "onDrag": function(el,e){
                // this.topBarTabItemStat.set("text",JSON.stringify(el.getPosition()));
                //if(el.getPosition().x + el.getWidth() >1900){
                    //this.topBarTabItemStat.set("text",JSON.stringify("treeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"));
                    //drag.cancel();
                    //e.stopPropagation();return;
                    //this.taskContentLayout.scrollTo( this.taskContentLayout.getScrollLeft() +500,0);
                    //el.setStyles({"margin-right":"-500px"})
                    //this.topBarTabItemStat.set("text","000000000000000000");
                //}
                // var _width = el.getWidth().toInt();
                //
                // var  _l= el.getPosition().x;
                // var _sl = this.taskContentLayout.getScrollLeft();
                // var _w = this.container.getWidth().toInt() + _sl;
                //
                //
                // //this.topBarTabItemStat.set("text",_width+p.x-_w_w);
                //
                // //this.taskContentLayout.scrollTo(this.taskContentLayout.getScrollLeft()+2,0);
                //
                // this.topBarTabItemStat.set("text",_l);
                // var p1 = this.taskContentLayout.getPosition();
                //
                // this.ccc ++;
                // //this.topBarTabItemStat.set("text",this.ccc);
                //
                // if(_l + _width == _w){ this.topBarTabItemStat.set("text","AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                //     //this.taskContentLayout.scrollTo(this.taskContentLayout.getScrollLeft()+100,0);
                //     // scrInt =  window.setInterval(function(){
                //     //     this.taskContentLayout.scrollTo(this.taskContentLayout.getScrollLeft()+500,0);
                //     //
                //     //     this.ccc ++;
                //     //     this.topBarTabItemStat.set("text",this.ccc);
                //     // }.bind(this),20)
                // }


            }.bind(this),
            "onEnter": function(el,dr){
                var pre = dr.getPrevious();
                if(!pre || (pre && pre.get("class")!="taskItemInsertLine")){
                    if(el.get("id")!=dr.get("id")){
                        new Element("div.taskItemInsertLine",{styles:_self.css.taskItemInsertLine}).inject(dr,"before");
                    }
                }
            }.bind(this),
            "onLeave": function(el,dr){
                var pre = dr.getPrevious();
                if(pre && pre.get("class")=="taskItemInsertLine") {
                    pre.destroy();
                }
            }.bind(this),
            "onDrop": function(el, dr, e){

                var taskId = el.get("id");
                var taskInId = null;
                var taskGroupInId = null;
                var taskGroupFromId = null;
                var taskGroupFromSortable = null;
                var taskGroupInSortable = null;
                el.setStyles({"cursor":""});
                if(!dr){
                    //没有移入，还原
                    var fx = new Fx.Tween(el,{duration:time});
                    fx.start(["top"] ,(el.getPosition(this.taskContentLayout).y)+"px", taskItemContainer.getPosition(this.taskContentLayout).y+"px");
                    var fx2 = new Fx.Tween(el,{duration:time});
                    fx2.start(["left"] ,(el.getPosition(this.taskContentLayout).x + this.taskContentLayout.getScrollLeft())+"px", (taskItemContainer.getPosition(this.taskContentLayout).x +  this.taskContentLayout.getScrollLeft())+"px");
                    window.setTimeout(function(){
                        taskItemContainer.setStyles({"border":"1px solid #e6e6e6","opacity":"1"});
                        el.destroy()
                    },time)
                }else{
                    if(el.get("id")==dr.get("id")){ //如果移动的是本身的拷贝,恢复
                        taskItemContainer.setStyles({"border":"1px solid #e6e6e6","opacity":"1"});
                        el.destroy();
                    }else{
                        taskInId = dr.get("id");
                        taskGroupFromId = taskItemContainer.getParent().getParent().get("id");
                        taskGroupInId = dr.getParent().getParent().get("id");
                        taskGroupFromSortable = taskItemContainer.getParent().getParent().get("sortable");
                        taskGroupInSortable = dr.getParent().getParent().get("sortable");
                        var pre = dr.getPrevious();
                        if(pre && pre.get("class")=="taskItemInsertLine") {  //this.topBarTabItemStat.set("text","change");
                            //alert("taskid="+taskId+",taskInId="+taskInId+",taskgroupFromid="+taskGroupFromId+",taskGroupInId="+taskGroupInId);
                            if(taskGroupInSortable == "false"){
                                //未分类视图不允许移入，还原并删除el对象
                                _self.app.notice("未分类列表不允许移入","error");
                                taskItemContainer.setStyles({"border":"1px solid #e6e6e6","opacity":"1"});
                                if(pre && pre.get("class")=="taskItemInsertLine"){
                                    pre.destroy();
                                    el.destroy();
                                }
                                return ;
                            }
                            var data = {
                                taskId:taskId,
                                behindTaskId:taskInId
                            };
                            _self.rootActions.TaskListAction.addTask2ListWithBehindTask(taskGroupInId,data,function(json){
                                if(json.type == "success"){ //返回成功后再换位置

                                    //fx fx2 动画clone到移入位置
                                    var fx = new Fx.Tween(el,{duration:time});
                                    fx.start(["top"] ,el.getPosition(_self.taskContentLayout).y+"px", pre.getPosition(_self.taskContentLayout).y+"px");
                                    var fx2 = new Fx.Tween(el,{duration:time});
                                    fx2.start(["left"] ,(el.getPosition(_self.taskContentLayout).x + _self.taskContentLayout.getScrollLeft())+"px", (pre.getPosition(_self.taskContentLayout).x +  _self.taskContentLayout.getScrollLeft())+"px");
                                    //利用pre撑大高度
                                    pre.setStyles({"border":"0px","background-color":"#ffffff"});
                                    //var fx3 = new Fx.Tween(pre,{duration:time});
                                    //fx3.start(["height"] ,"0px", _height+"px");

                                    //新建一个占用原来位置
                                    var tmpdiv = taskItemContainer.clone();
                                    tmpdiv.setStyles({"border":"0px","margin":"0px","height":(el.getHeight())+"px"});
                                    tmpdiv.empty();
                                    tmpdiv.inject(taskItemContainer,"before");

                                    //设置原有位置none
                                    taskItemContainer.setStyles({"display":"none"});

                                    var __height = tmpdiv.getHeight().toInt() - 2;
                                    //动画完成后插入真正的对象，恢复原位，并删除pre和clone
                                    window.setTimeout(function(){
                                        pre.destroy();
                                        taskItemContainer.setStyles({"border":"1px solid #e6e6e6","display":"block","opacity":"1"});
                                        taskItemContainer.inject(dr,"before");
                                        //fx4把临时占用的位置删除
                                        var fx4 = new Fx.Tween(tmpdiv,{duration:time});
                                        fx4.start(["height"] ,__height+"px", "0px");
                                        window.setTimeout(function(){
                                            if(tmpdiv) tmpdiv.destroy()
                                        },time);
                                        //删除clone即el对象
                                        el.destroy();
                                    },time);

                                    //刷新其他需要加载的位置，比如数量
                                    var taskGroupFrom = taskItemContainer.getParent().getParent();
                                    var taskGroupIn = dr.getParent().getParent();
                                    //_self.actions.taskGroupGet(_self.currentProjectGroupData.id,taskGroupFromId,function(json){
                                    _self.rootActions.TaskListAction.get(_self.currentProjectGroupData.id,taskGroupFromId,function(json){
                                        taskGroupFrom.getElement(".taskGroupItemTitleCount").set("text","("+json.data.taskCount +")")
                                    });
                                    _self.rootActions.TaskListAction.get(_self.currentProjectGroupData.id,taskGroupInId,function(json){
                                        taskGroupIn.getElement(".taskGroupItemTitleCount").set("text","("+json.data.taskCount +")")
                                    })
                                }else{
                                    //返回失败，还原并删除el对象
                                    taskItemContainer.setStyles({"border":"1px solid #e6e6e6","opacity":"1"});
                                    if(pre && pre.get("class")=="taskItemInsertLine"){
                                        pre.destroy();
                                        el.destroy();
                                    }
                                }
                            },function(xhr,text,error){
                                _self.app.showErrorMessage(xhr,text,error);
                                taskItemContainer.setStyles({"border":"1px solid #e6e6e6","opacity":"1"});
                                if(pre && pre.get("class")=="taskItemInsertLine"){
                                    pre.destroy();
                                    el.destroy();
                                }
                            });
                        }
                    }
                }
            }.bind(this),
            // "onCancel": function(el, e){ this._drag_cancel(dragging); }.bind(this),
            //"onComplete": function(el, e){ if(scrInt) window.clearInterval(scrInt);}.bind(this),
        });
        drag.start(e);
    },




    loadTaskNode:function(taskItemContainer,d){
        taskItemContainer.empty();

        var taskItemHover = new Element("div.taskItemHover",{styles:this.css.taskItemHover}).inject(taskItemContainer);
        if(d.priority == this.lp.urgency)taskItemHover.setStyle("background-color","#ffaf38");
        else if(d.priority == this.lp.emergency)taskItemHover.setStyle("background-color","#ff0000");
        var taskItemContent = new Element("div.taskItemContent",{styles:this.css.taskItemContent}).inject(taskItemContainer);
        var taskItemTitle = new Element("div.taskItemTitle",{styles:this.css.taskItemTitle}).inject(taskItemContent);
        var taskItemName = new Element("div.taskItemName",{styles:this.css.taskItemName,text:d.name}).inject(taskItemTitle);

        var executor = d.executor.split("@")[0];
        var shortE = executor.substring(0,1);
        var taskItemExecutor = new Element("div",{styles:this.css.taskItemExecutor,text:shortE,title:executor}).inject(taskItemTitle);

        var taskItemDetail = new Element("div.taskItemDetail",{styles:this.css.taskItemDetail}).inject(taskItemContent);
        //时限
        var taskItemLimit = new Element("div.taskItemLimit",{styles:this.css.taskItemLimit}).inject(taskItemDetail);
        taskItemLimit.set("text",this.app.formatDate(d.startTime) + " - " + this.app.formatDate(d.endTime));
        var intd = (this.app.compareWithNow(d.endTime)).intervalDay;

        //alert(d.endTime + "**************" + intd);
        if(intd==-1){
            taskItemLimit.setStyle("background-color","#E6240E");
        }else if(intd==0){
            taskItemLimit.setStyle("background-color","#fa8c15");
        }else{
            taskItemLimit.setStyle("background-color","#1b9aee");
        }
        //标签 taskTag等

        var taskItemTags = new Element("div.taskItemTags",{styles:this.css.taskItemTags}).inject(taskItemDetail);
        this.rootActions.TaskTagAction.listWithTask(d.id,function(json){
            json.data.each(function(data){
                var taskTagContent = new Element("div.taskTagContent",{styles:this.css.taskTagContent}).inject(taskItemTags);
                var taskTagColor = new Element("span",{styles:this.css.taskTagColor}).inject(taskTagContent);
                taskTagColor.setStyle("background-color",data.tagColor);
                new Element("span",{styles:this.css.taskTagText,text:data.tag}).inject(taskTagContent);
            }.bind(this));
        }.bind(this),null,false);


        if(d.workStatus=="completed"){ //如果已完成
            new Element("div.",{styles:this.css.taskItemComplete,title:this.lp.taskCompleteText}).inject(taskItemTitle,"top");
            //taskItemContainer.setStyles({"opacity":"0.5"});
            taskItemContainer.setStyles({"color":"grey"});
            taskItemLimit.destroy();

            taskItemHover.setStyles({"background-color":"#DEDEDE"});
            taskItemExecutor.setStyles({"opacity":"0.5"})
        }

        taskItemHover.setStyles({"height":taskItemContainer.getHeight()});

        // var drag = new Drag(taskItemContainer, {
        //     "stopPropagation": true,
        //     "compensateScroll": true,
        //     "onStart": function(el, e){
        //         this.doDragMove(e,taskItemContainer);
        //         drag.stop();
        //     }.bind(this)
        // });

    },

    openTask:function(id,callback){
        var data = {
            taskId:id
        };
        var opt = {
            "onPostClose":function(dd){
                if(!dd) return;
                if(dd.act == "remove"){
                    if(callback){
                        callback()
                    }
                    //this.createTaskGroup();
                }
            }.bind(this)
        };
        MWF.xDesktop.requireApp("TeamWork", "Task", function(){
            var task = new MWF.xApplication.TeamWork.Task(this,data,opt,{projectObj:this});
            task.open();
        }.bind(this));
    },
    openView:function(obj){
        this.naviTopMyTaskLayout.setStyles({"background-color":""});
        var _self = this;
        this.naviViewContainer.getElements(".naviItemContainer").each(function(d){
            this.naviItemChange(d,"leave")
        }.bind(this));
        this.naviItemChange(obj,"enter");

        if(this.viewContainer) delete this.viewContainer;
        if(this.viewListContainer) delete this.viewListContainer;
        if(this.viewSearchInput) delete this.viewSearchInput;
        this.taskContentLayout.empty();
        var viewContainer = this.viewContainer = new Element("div.viewContainer",{styles:this.css.viewContainer}).inject(this.taskContentLayout);
        viewContainer.addEvents({
            scroll:function(){
                var sTop = this.getScrollTop();
                var sHeight = this.getScrollHeight();
                var cHeight = viewContainer.getHeight();

                if(sHeight - sTop < cHeight+10 ){ //偏移量
                    if(_self.viewLoading){
                        _self.loadView(_self.curViewTaskId)
                    }
                }
            }
        });
        var viewSearchContainer = new Element("div.viewSearchContainer",{styles:this.css.viewSearchContainer}).inject(viewContainer);
        var viewSearchContent = new Element("div.viewSearchContent",{styles:this.css.viewSearchContent}).inject(viewSearchContainer);
        var viewSearchInput = this.viewSearchInput = new Element("input",{styles:this.css.viewSearchInput,"name":"viewSearchInput","placeholder":this.lp.searchTask}).inject(viewSearchContent);
        viewSearchInput.addEvents({
            keypress:function(e){
                this.searchLoading = true;
                var keycode = (e.event.keyCode ? e.event.keyCode : e.event.which);
                if (keycode == 13 || keycode == 10) {
                    var key = viewSearchInput.get("value").trim();
                    if(key=="") return;
                    //alert(key)
                    viewSearchReset.show();
                    if(this.searchLoading) this.loadView()
                }
            }.bind(this),
            focus:function(){
                viewSearchContent.setStyles({"border":"1px solid #4A90E2"})
            }.bind(this),
            blur:function(){
                viewSearchContent.setStyles({"border":"1px solid #DEDEDE"})
            }.bind(this)
        })

        var viewSearchSearch = new Element("div",{styles:this.css.viewSearchSearch}).inject(viewSearchContent);
        viewSearchSearch.addEvents({
            click:function(){
                if(viewSearchInput.get("value").trim()=="") return;
                viewSearchReset.show();
                this.loadView();
            }.bind(this)
        });
        var viewSearchReset = new Element("div",{styles:this.css.viewSearchReset}).inject(viewSearchContent);
        viewSearchReset.addEvents({
            click:function(){
                viewSearchReset.hide();
                viewSearchInput.set("value","");
                this.loadView();
            }.bind(this)
        });

        var viewListContainer = this.viewListContainer = new Element("div.viewListContainer",{styles:this.css.viewListContainer}).inject(viewContainer);
        this.loadView()
    },
    openSearch:function(key){
        var _self = this;
        if(this.viewListContainer) delete this.viewListContainer;
        this.taskContentLayout.empty();
        var viewContainer = new Element("div.viewContainer",{styles:this.css.viewContainer}).inject(this.taskContentLayout);
        var viewListContainer  = this.viewListContainer = new Element("div.viewListContainer",{styles:this.css.viewListContainer}).inject(viewContainer);
        //this.loadView()
        var data = {
            project:this.data.id,
            title:key
        };
        var tmpLoading = new Element("div.tmpLoading",{styles:{"background-color":"#ffffff"}}).inject(viewListContainer);
        this.app.setLoading(tmpLoading);
        this.searchLoading = false;

        var filter = {
            title:key
        };

        this.rootActions.TaskAction.listAllTaskNextWithFilter("(0)",100,this.data.id,filter,function(json){
            viewListContainer.empty();
            json.data.each(function(data){
                this.loadViewItem(data)
            }.bind(this));
            if(json.count==0){
                new Element("div.none",{styles:{
                        "height":"100px",
                        "line-height":"100px",
                        "width":"100%",
                        "text-align":"center",
                        "background-color":"#ffffff",
                        "font-size":"16px"
                    },text:"未查找到数据"}).inject(this.viewListContainer);
            }
            this.searchLoading = true;
        }.bind(this))


        //this.actions.taskListNext("(0)",100,data,function(json){
            // viewListContainer.empty();
            // json.data.each(function(data){
            //     this.loadViewItem(data)
            // }.bind(this));
            // if(json.count==0){
            //     new Element("div.none",{styles:{
            //             "height":"100px",
            //             "line-height":"100px",
            //             "width":"100%",
            //             "text-align":"center",
            //             "background-color":"#ffffff",
            //             "font-size":"16px"
            //         },text:"未查找到数据"}).inject(this.viewListContainer);
            // }
            // this.searchLoading = true;
        //}.bind(this))
    },
    loadView:function(id){
        //this.curNaviItem  所有工作，我负责的任务。。。。。
        //key 搜索关键字

        if(!(id)) {
            this.viewListContainer.empty();
            this.curCount = 0;
        }

        this.viewListContainer.getElements(".viewNext").destroy();
        var data = {
            project:this.data.id
        };
        if(this.viewSearchInput.get("value").trim()!=""){
            data.title = this.viewSearchInput.get("value").trim();
        }
        var tmpLoading = new Element("div.tmpLoading",{styles:{"background-color":"#ffffff"}}).inject(this.viewListContainer);
        this.app.setLoading(tmpLoading);
        this.viewLoading = false;
        // var filter = {
        //     //"title":this.viewSearchInput.get("value")||""
        // }

        var action = "listAllTaskNextWithFilter";
        if(this.curNaviItem == this.lp.viewItemMy){ //我负责的任务
            action = "listMyExecutTaskNextWithFilter"
        }else if(this.curNaviItem == this.lp.viewItemFlow){ //未完成的任务
            action = "listUncompletedTaskNextWithFilter"
        }else if(this.curNaviItem == this.lp.viewItemComplete){ //已完成的任务
            action = "listCompletedTaskNextWithFilter"
        }else if(this.curNaviItem == this.lp.viewItemOver){ //逾期的任务
            action = "listOverTimeTaskNextWithFilter"
        }


        this.rootActions.TaskAction[action](id||"(0)",20,this.data.id,data,function(json){
            this.viewListContainer.getElements(".tmpLoading").destroy();
            json.data.each(function(data){
                this.loadViewItem(data);
                this.curViewTaskId = data.id;
                this.curCount = this.curCount + 1;
            }.bind(this));
            this.viewLoading = true;

            if(json.count == 0){
                new Element("div",{styles:this.css.taskSearchEmpty,text:this.lp.taskSearchEmpty}).inject(this.viewListContainer);
            }

            var sHeight = this.viewListContainer.getHeight();
            var cHeight = this.viewContainer.getHeight();

            if(sHeight<cHeight && this.curCount < json.count){
                var viewNext = new Element("div.viewNext",{styles:this.css.viewNext,text:"下一页"}).inject(this.viewListContainer);
                viewNext.addEvents({
                    click:function(){
                        this.loadView(this.curViewTaskId)
                    }.bind(this)
                })
            }
        }.bind(this))
    },
    loadViewItem:function(data){
        var viewItem = new Element("div.viewItem",{styles:this.css.viewItem}).inject(this.viewListContainer);
        var viewColor = new Element("div.viewColor",{styles:this.css.viewColor}).inject(viewItem);

        if(data.priority==this.lp.urgency){
            //紧急
            viewColor.setStyles({"background-color":"#fa8c15"});
        }else if(data.priority==this.lp.emergency){
            //特级
            viewColor.setStyles({"background-color":"#E6240E"});
        }

        var viewName = new Element("div.viewName",{styles:this.css.viewName,text:data.name}).inject(viewItem);
        var viewStatus = new Element("div.viewStatus",{styles:this.css.viewStatus,text:data.priority}).inject(viewItem);
        var viewDuty = new Element("div.viewDuty",{styles:this.css.viewDuty}).inject(viewItem);
        var viewDutyIcon = new Element("div.viewDutyIcon",{styles:this.css.viewDutyIcon}).inject(viewDuty);
        if(data.executor && data.executor!=""){
            viewDutyIcon.set("text",data.executor.split("@")[0].substr(0,1));
            viewDutyIcon.set("title",data.executor.split("@")[0])
        }

        if(data.workStatus == "completed"){
            var completeIcon = new Element("div",{styles:this.css.taskItemComplete,title:this.lp.taskCompleteText}).inject(viewName,"top");
            completeIcon.setStyles({"margin-top":"16px"});
            viewItem.setStyles({"color":"#DEDEDE"});
            viewColor.destroy();
            viewDutyIcon.setStyles({"opacity":"0.5"});
        }

        viewItem.addEvents({
            mouseover:function(){ this.setStyles({"background-color":"#F2F5F7"}) },
            mouseout:function(){ this.setStyles({"background-color":"#ffffff"}) },
            click:function(){
                this.openTask(data.id,function(){ viewItem.destroy() }.bind(this))
            }.bind(this)
        })
    },
    createStatLayout:function(){
        MWF.xDesktop.requireApp("TeamWork", "Stat", function(){
            var stat = new MWF.xApplication.TeamWork.Stat(this.contentLayout,this.app,this.data,{ });
            stat.load();
        }.bind(this));
    },
    reloadTaskGroup:function(id){
        var node;
        if(id) node = this.taskContentLayout.getElementById(id);
        if(!node)node = this.taskContentLayout.getElement("div[sortable='false']");

        if(node){ //alert(id)
            //console.log("id=============="+id);
            this.rootActions.TaskListAction.get(this.currentProjectGroupData.id,id,function(json){
                this.createTaskGroupItemLayout(node,json.data);
            }.bind(this))
        }

    },

    reloadInfor:function(){
        //刷新页面上的一些信息，比如数量等未想好

    },
    test:function(){

    }

});

MWF.xApplication.TeamWork.Project.NewTask = new Class({
    Extends: MPopupForm,
    options : {
        "style": "default",
        "width": 400,
        "height": 250,
        "top": null,
        "left": null,
        "bottom" : null,
        "right" : null,
        "minWidth" : 300,
        "minHeight" : 220,
        "closeByClickMask" : true,
        "hasTopContent" : true,
        "hasTop":true,
        "hasBottom": false,
        "hasIcon": false,
        "title":""
    },
    _createTableContent:function(){
        this.rootActions = this.app.rootActions;
        if(this.formTopTextNode) this.formTopTextNode.set("text",this.lp.newTaskTitle);
        //this.formTableArea
        this.titleContainer = new Element("div.titleContainer",{styles:this.css.titleContainer}).inject(this.formTableArea);
        this.titleValue = new Element("input.titleValue",{styles:this.css.titleValue,placeholder:this.lp.newTaskPlaceholder}).inject(this.titleContainer);
        this.titleValue.addEvents({
            keyup:function(e){
                var keycode = (e.event.keyCode ? e.event.keyCode : e.event.which);
                //console.log(keycode)
                if (keycode == 13 || keycode == 10) {
                    this.okAction.click();
                }else if(keycode == 27){
                    this.closeAction.click();
                }
            }.bind(this),
        });

        this.actionContainer = new Element("div.actionContainer",{styles:this.css.actionContainer}).inject(this.formTableArea);
        this.okAction = new Element("div.okAction",{styles:this.css.okAction,text:this.lp.newTaskOk}).inject(this.actionContainer);
        this.okAction.addEvents({
            click:function(){
                if(this.titleValue.get("value").trim()=="") return;
                var data={
                    taskGroupId:this.data.taskGroupId,
                    taskListIds:this.data.taskListIds || [],
                    name: this.titleValue.get("value").trim()
                };
                this.rootActions.TaskAction.save(data,function(json){
                    if(json.data.id){
                        var data = {
                            projectObj:this.data.projectObj || null,
                            taskId:json.data.id
                        };
                        var opt = {

                        };
                        MWF.xDesktop.requireApp("TeamWork", "Task", function(){
                            var task = new MWF.xApplication.TeamWork.Task(this,data,opt,{projectObj:this.explorer});
                            task.open();
                        }.bind(this));
                        this.fireEvent("createTask");
                        this.close();
                    }
                }.bind(this))
            }.bind(this)
        });
        this.closeAction = new Element("div.closeAction",{styles:this.css.closeAction,text:this.lp.newTaskClose}).inject(this.actionContainer);
        this.closeAction.addEvents({
            click:function(){
                this.close();
            }.bind(this)
        });

    }

});

MWF.xApplication.TeamWork.Project.NewTaskGroup = new Class({
    Extends: MPopupForm,
    options : {
        "style": "default",
        "width": 400,
        "height": 250,
        "top": null,
        "left": null,
        "bottom" : null,
        "right" : null,
        "minWidth" : 300,
        "minHeight" : 220,
        "closeByClickMask" : true,
        "hasTopContent" : true,
        "hasTop":true,
        "hasBottom": false,
        "hasIcon": false,
        "title":""
    },
    _createTableContent:function(){
        this.rootActions = this.app.rootActions;
        this.explorer = this.data.projectObj;
        if(this.formTopTextNode) this.formTopTextNode.set("text",this.data.isNew?this.lp.taskGroupAdd:this.lp.taskGroupEdit);
        //this.formTableArea
        this.titleContainer = new Element("div.titleContainer",{styles:this.css.titleContainer}).inject(this.formTableArea);
        this.titleValue = new Element("input.titleValue",{styles:this.css.titleValue,placeholder:this.lp.newTaskListPlaceholder}).inject(this.titleContainer);
        var val = this.data.isNew?"":this.data.name;
        this.titleValue.set("value",val);

        this.actionContainer = new Element("div.actionContainer",{styles:this.css.actionContainer}).inject(this.formTableArea);
        this.okAction = new Element("div.okAction",{styles:this.css.okAction,text:this.lp.newTaskOk}).inject(this.actionContainer);
        this.okAction.addEvents({
            click:function(){
                if(this.titleValue.get("value").trim()=="") return;
                var data={
                    id:this.data.id || "",
                    taskGroup:this.data.taskGroupId,
                    project:this.data.projectId,
                    name: this.titleValue.get("value").trim()
                };
                this.rootActions.TaskListAction.save(data,function(json){
                    this.fireEvent("createTask");
                    this.close(json);
                    if(this.explorer){
                        if(this.data.node){
                            if(json.data.id){
                                this.rootActions.TaskListAction.get(this.explorer.currentProjectGroupData.id,json.data.id,function(json){
                                    this.explorer.createTaskGroupItemLayout(this.data.node,json.data)
                                }.bind(this));
                            }else{
                                this.explorer.createTaskGroup()
                            }
                        }else{
                            this.explorer.createTaskGroup()
                        }
                    }
                }.bind(this))
            }.bind(this)
        });
        this.closeAction = new Element("div.closeAction",{styles:this.css.closeAction,text:this.lp.newTaskClose}).inject(this.actionContainer);
        this.closeAction.addEvents({
            click:function(){
                this.close();
            }.bind(this)
        });
    }

});

MWF.xApplication.TeamWork.Project.TaskGroupMenu = new Class({
    Extends: MWF.xApplication.TeamWork.Common.ToolTips,
    options : {
        // displayDelay : 300,
        hasArrow:false,
        event:"click"
    },
    _loadCustom : function( callback ){
        var _self = this;
        this.css = this.options.css;
        this.lp = this.options.lp;
        //this.data

        this.menuTipLayout = new Element("div.menuTipLayout",{styles:this.css.menuTipLayout}).inject(this.contentNode);
        this.menuTipTitle = new Element("div.menuTipTitle",{styles:this.css.menuTipTitle,text:this.lp.taskGroup}).inject(this.menuTipLayout);
        this.menuTipEditContainer = new Element("div.menuTipContainer",{styles:this.css.menuTipContainer}).inject(this.menuTipLayout);
        this.menuTipEditIcon = new Element("div.menuTipEditIcon",{styles:this.css.menuTipEditIcon}).inject(this.menuTipEditContainer);
        this.menuTipEditText = new Element("div.menuTipEditText",{styles:this.css.menuTipText,text:this.lp.taskGroupEdit}).inject(this.menuTipEditContainer);
        this.menuTipEditContainer.addEvents({
            click:function(){
                var data = this.data;
                data.isNew = false;

                var opt = {

                };
                var newTaskGroup = new MWF.xApplication.TeamWork.Project.NewTaskGroup(this,data,opt,{});
                newTaskGroup.open();
                this.close();
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#F7F7F7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        this.menuTipRemoveContainer = new Element("div.menuTipContainer",{styles:this.css.menuTipContainer}).inject(this.menuTipLayout);
        this.menuTipRemoveIcon = new Element("div.menuTipRemoveIcon",{styles:this.css.menuTipRemoveIcon}).inject(this.menuTipRemoveContainer);
        this.menuTipRemoveText = new Element("div.menuTipRemoveText",{styles:this.css.menuTipText,text:this.lp.taskGroupRemove}).inject(this.menuTipRemoveContainer);
        this.menuTipRemoveContainer.addEvents({
            click:function(e){
                _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){
                    if(_self.data.id){
                        _self.rootActions.TaskListAction.delete(_self.data.id,function(json){
                            this.close();
                            _self.close(json)
                        }.bind(this))
                    }
                },function(){
                    this.close();
                });
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#F7F7F7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        if(callback)callback();
    }

});

MWF.xApplication.TeamWork.Project.NaviViewTip = new Class({
    Extends: MWF.xApplication.TeamWork.Common.ToolTips,
    options : {
        // displayDelay : 300,
        hasArrow:false,
        event:"click"
    },
    _loadCustom : function( callback ){
        var _self = this;
        this.css = this.options.css;
        this.lp = this.options.lp;
        //this.data

        this.menuTipLayout = new Element("div.menuTipLayout",{styles:this.css.menuTipLayout}).inject(this.contentNode);
        this.menuTipTitle = new Element("div.menuTipTitle",{styles:this.css.menuTipTitle,text:this.lp.view}).inject(this.menuTipLayout);
        this.menuTipEditContainer = new Element("div.menuTipContainer",{styles:this.css.menuTipContainer}).inject(this.menuTipLayout);
        this.menuTipEditIcon = new Element("div.menuTipEditIcon",{styles:this.css.menuTipEditIcon}).inject(this.menuTipEditContainer);
        this.menuTipEditText = new Element("div.menuTipEditText",{styles:this.css.menuTipText,text:this.lp.viewEdit}).inject(this.menuTipEditContainer);
        this.menuTipEditContainer.addEvents({
            click:function(){

            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#F7F7F7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        this.menuTipRemoveContainer = new Element("div.menuTipContainer",{styles:this.css.menuTipContainer}).inject(this.menuTipLayout);
        this.menuTipRemoveIcon = new Element("div.menuTipRemoveIcon",{styles:this.css.menuTipRemoveIcon}).inject(this.menuTipRemoveContainer);
        this.menuTipRemoveText = new Element("div.menuTipRemoveText",{styles:this.css.menuTipText,text:this.lp.viewRemove}).inject(this.menuTipRemoveContainer);
        this.menuTipRemoveContainer.addEvents({
            click:function(e){
                _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){

                },function(){
                    this.close();
                });
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#F7F7F7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });


        if(callback)callback();
    }

});

MWF.xApplication.TeamWork.Project.ProjectList = new Class({
    Extends: MWF.xApplication.TeamWork.Common.ToolTips,
    options : {
        // displayDelay : 300,
        hasArrow:false,
        event:"click"
    },

    _loadCustom : function(callback){
        var _self = this;
        this.css = this.options.css;
        this.lp = this.options.lp;
        //this.data
        //this.contentNode

        this.itemLayout = new Element("div.itemLayout",{styles:this.css.itemLayout}).inject(this.contentNode);
        this.app.setLoading(this.itemLayout);

        this.rootActions.ProjectAction.listStarNextWithFilter("(0)",100,{},function(json){
            this.app.setScrollBar(this.itemLayout);
            this.itemLayout.empty();
            if(json.data && json.data.length>0){
                this.starText = new Element("div.starText",{styles:this.css.itemText,text:this.lp.starItem}).inject(this.itemLayout);
                json.data.each(function(data){
                    var projectListContainer = new Element("div.projectListContainer",{styles:this.css.projectListContainer}).inject(this.itemLayout);
                    var projectListIcon = new Element("div.projectListIcon",{styles:this.css.projectListIcon}).inject(projectListContainer);
                    if(data.icon && data.icon!=""){
                        projectListIcon.setStyles({"background-image":"url('"+MWF.xDesktop.getImageSrc( data.icon )+"')"});
                    }
                    new Element("div.projectListText",{styles:this.css.projectListText,text:data.title}).inject(projectListContainer);
                    if(this.data.id == data.id){
                        new Element("div.projectListSelect",{styles:this.css.projectListSelect}).inject(projectListContainer);
                    }
                    projectListContainer.addEvents({
                        click:function(){
                            MWF.xDesktop.requireApp("TeamWork", "Project", function(){
                                var p = new MWF.xApplication.TeamWork.Project(this.container,this.app,data,{});
                                p.load();
                            }.bind(this));
                        }.bind(this),
                        mouseover:function () { this.setStyles({"background-color":"#F2F5F7"})},
                        mouseout:function () { this.setStyles({"background-color":""})}
                    });
                }.bind(this))
            }
            var tmpContainer = new Element("div.tmpContainer").inject(this.itemLayout);
            this.app.setLoading(tmpContainer);
            this.rootActions.ProjectAction.listNextWithFilter("(0)",100,{},function(json){
                tmpContainer.destroy();
                this.allItemText = new Element("div.allItemText",{styles:this.css.itemText,text:this.lp.allItem}).inject(this.itemLayout);
                json.data.each(function(data){
                    var projectListContainer = new Element("div.projectListContainer",{styles:this.css.projectListContainer}).inject(this.itemLayout);
                    var projectListIcon = new Element("div.projectListIcon",{styles:this.css.projectListIcon}).inject(projectListContainer);
                    if(data.icon && data.icon!=""){
                        projectListIcon.setStyles({"background-image":"url('"+MWF.xDesktop.getImageSrc( data.icon )+"')"});
                    }
                    new Element("div.projectListText",{styles:this.css.projectListText,text:data.title}).inject(projectListContainer);
                    if(this.data.id == data.id){
                        new Element("div.projectListSelect",{styles:this.css.projectListSelect}).inject(projectListContainer);
                    }
                    projectListContainer.addEvents({
                        click:function(){
                            MWF.xDesktop.requireApp("TeamWork", "Project", function(){
                                var p = new MWF.xApplication.TeamWork.Project(this.container,this.app,data,{});
                                p.load();
                            }.bind(this));
                        }.bind(this),
                        mouseover:function () { this.setStyles({"background-color":"#F2F5F7"})},
                        mouseout:function () { this.setStyles({"background-color":""})}
                    });
                }.bind(this));

            }.bind(this))
        }.bind(this));


        if(callback)callback();
    }
});