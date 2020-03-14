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
        this.actions = this.app.restActions;

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
                this.app.showTips(this.topBarBackHomeIcon,{_html:"<div style='margin:2px 5px;'>"+this.lp.backProject+"</div>"},opt);
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
        this.createTaskContent();

    },
    createNaviContent:function(){
        if(this.naviLayout) this.naviLayout.destroy();
        this.naviLayout = new Element("div.naviLayout",{styles:this.css.naviLayout}).inject(this.contentLayout);
        this.app.setLoading(this.naviLayout);
        this.actions.projectNaviGet(this.data.id,function(json){
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
                this.app.showTips(this.naviTopTaskAdd,{_html:"<div style='margin:2px 5px;'>"+this.lp.taskAdd+"</div>"});
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
                }.bind(this))
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
                this.app.showTips(this.completeLine,{_html:"<div style='margin:2px 5px;'>"+this.lp.taskCompleteText+":"+this.currentProjectGroupData.completedTotal+"</div>"});
            }.bind(this)
        });
        this.overLine = new Element("div.overLine",{styles:this.css.overLine}).inject(this.naviTopTaskLine);
        this.overLine.addEvents({
            mouseover:function(){
                this.app.showTips(this.overLine,{_html:"<div style='margin:2px 5px;'>"+this.lp.taskCompleteText+":"+this.currentProjectGroupData.overtimeTotal+"</div>"});
            }.bind(this)
        });

        if(this.currentProjectGroupData){
            var taskTotal = this.currentProjectGroupData.taskTotal;
            var completedTotal = this.currentProjectGroupData.completedTotal;
            var overtimeTotal = this.currentProjectGroupData.overtimeTotal;

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
                this.naviItemMyMore = new Element("div.naviItemMore",{styles:this.css.naviItemMore}).inject(this.naviItemMyContainer);
                this.naviItemMyMore.addEvent(
                    "click",function(e){
                        this.openNaviViewMore(this.naviItemMyMore);
                        e.stopPropagation();
                    }.bind(this)
                );
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
                this.naviItemFlowMore = new Element("div.naviItemMore",{styles:this.css.naviItemMore}).inject(this.naviItemFlowContainer);
                this.naviItemFlowMore.addEvent(
                    "click",function(e){
                        this.openNaviViewMore(this.naviItemFlowMore);
                        e.stopPropagation();
                    }.bind(this)
                );
                this.naviItemFlowContainer.addEvents({
                    click:function(){
                        alert("open")
                    }.bind(this),
                    mouseenter:function(){ _self.naviItemChange(this,"enter")},
                    mouseleave:function(){_self.naviItemChange(this,"leave")}
                });
            }else if(json.name==this.lp.viewItemComplete){
                //已完成任务
                this.naviItemCompleteContainer = new Element("div.naviItemContainer",{styles:this.css.naviItemContainer}).inject(this.naviViewContainer);
                this.naviItemCompleteIcon = new Element("div.naviItemCompleteIcon",{styles:this.css.naviItemCompleteIcon}).inject(this.naviItemCompleteContainer);
                this.naviItemCompleteText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:this.lp.viewItemComplete}).inject(this.naviItemCompleteContainer);
                this.naviItemCompleteMore = new Element("div.naviItemMore",{styles:this.css.naviItemMore}).inject(this.naviItemCompleteContainer);
                this.naviItemCompleteMore.addEvent(
                    "click",function(e){
                        this.openNaviViewMore(this.naviItemCompleteMore);
                        e.stopPropagation();
                    }.bind(this)
                );
                this.naviItemCompleteContainer.addEvents({
                    click:function(){
                        alert("open")
                    }.bind(this),
                    mouseenter:function(){ _self.naviItemChange(this,"enter")},
                    mouseleave:function(){_self.naviItemChange(this,"leave")}
                });
            }else if(json.name==this.lp.viewItemOver){
                //已逾期任务
                this.naviItemOverContainer = new Element("div.naviItemContainer",{styles:this.css.naviItemContainer}).inject(this.naviViewContainer);
                this.naviItemOverIcon = new Element("div.naviItemOverIcon",{styles:this.css.naviItemOverIcon}).inject(this.naviItemOverContainer);
                this.naviItemOverText = new Element("div.naviItemText",{styles:this.css.naviItemText,text:this.lp.viewItemOver}).inject(this.naviItemOverContainer);
                this.naviItemOverMore = new Element("div.naviItemMore",{styles:this.css.naviItemMore}).inject(this.naviItemOverContainer);
                this.naviItemOverMore.addEvent(
                    "click",function(e){
                        this.openNaviViewMore(this.naviItemOverMore);
                        e.stopPropagation();
                    }.bind(this)
                );
                this.naviItemOverContainer.addEvents({
                    click:function(){
                        alert("open")
                    }.bind(this),
                    mouseenter:function(){ _self.naviItemChange(this,"enter")},
                    mouseleave:function(){_self.naviItemChange(this,"leave")}
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
            this.actions.taskGroupList(this.currentProjectGroupData.id,function(json){
                this.taskContentLayout.empty();
                json.data.each(function(data,i){
                    //if(i>0) return;
                    var taskGroupLayout = new Element("div.taskGroupLayout",{styles:this.css.taskGroupLayout,id:data.id}).inject(this.taskContentLayout);
                    taskGroupLayout.set("sortable",data.control.sortable); //控制是否能排序
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
        new Element("div.taskGroupItemTitleCount",{styles:this.css.taskGroupItemTitleCount,text:"("+data.taskCount+")"}).inject(taskGroupItemTitleContainer);
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
        var taskGroupItemContainer = new Element("div.taskGroupItemContainer",{styles:this.css.taskGroupItemContainer}).inject(node);
        var _h = node.getHeight().toInt()-taskGroupItemTitleContainer.getHeight().toInt() - 10 - 10;

        taskGroupItemContainer.setStyles({"height":_h+"px"});
        this.app.setScrollBar(taskGroupItemContainer);
        this.app.setLoading(taskGroupItemContainer);

        this.actions.taskListByListId(this.data.id,data.id,function(json){
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
                        //解决click和mousedown事件冲突
                        if(upTime>0){
                            this.openTask(d.id,function(){ taskItemContainer.destroy() }.bind(this));
                            upTime = 0
                        }
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
                    mouseup:function(){
                        upTime = new Date().getTime();

                    },
                    mousedown:function(e){
                        var position = taskItemContainer.getPosition();
                        var clone = taskItemContainer.clone(true,true);
                        clone.removeClass("dragin");
                        clone.setStyles({
                            "top":position.y+"px",
                            "left":position.x+"px",
                            "z-index":"9999",
                            "position":"absolute",
                            "cursor":"move"
                        });
                        var _height = clone.getHeight().toInt() - 2;

                        var myDrag = new Drag.Move(clone, {
                            container: this.taskContentLayout,
                            //handle: taskItemHover,
                            droppables: $$('.dragin'),
                            onStart:function(el){
                                if(upTime>0){
                                    myDrag.stop();
                                }else{
                                    el.inject($(document.body));
                                    taskItemContainer.setStyles({"border":"1px dotted #000000","opacity":"0.3"});
                                }
                            },
                            onLeave:function(el,dr){
                                var pre = dr.getPrevious();
                                if(pre && pre.get("class")=="taskItemInsertLine") {
                                    pre.destroy();
                                }
                            },
                            onEnter:function(el,dr){
                                var pre = dr.getPrevious();
                                if(!pre || (pre && pre.get("class")!="taskItemInsertLine")){
                                    if(el.get("id")!=dr.get("id")){
                                        new Element("div.taskItemInsertLine",{styles:_self.css.taskItemInsertLine}).inject(dr,"before");
                                    }
                                }
                            },
                            onDrop:function(el, dr, e){
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
                                    fx.start(["top"] ,el.getPosition().y+"px", taskItemContainer.getPosition().y+"px");
                                    var fx2 = new Fx.Tween(el,{duration:time});
                                    fx2.start(["left"] ,el.getPosition().x+"px", taskItemContainer.getPosition().x+"px");
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
                                        if(pre && pre.get("class")=="taskItemInsertLine") {
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
                                            _self.actions.taskChangeGroup(taskGroupInId,data,function(json){
                                                if(json.type == "success"){ //返回成功后再换位置

                                                    //fx fx2 动画clone到移入位置
                                                    var fx = new Fx.Tween(el,{duration:time});
                                                    fx.start(["top"] ,el.getPosition().y+"px", pre.getPosition().y+"px");
                                                    var fx2 = new Fx.Tween(el,{duration:time});
                                                    fx2.start(["left"] ,el.getPosition().x+"px", pre.getPosition().x+"px");
                                                    //利用pre撑大高度
                                                    pre.setStyles({"border":"0px","background-color":"#ffffff","height":(el.getHeight().toInt())+"px"});
                                                    //var fx3 = new Fx.Tween(pre,{duration:time});
                                                    //fx3.start(["height"] ,"0px", _height+"px");

                                                    //新建一个占用原来位置
                                                    var tmpdiv = taskItemContainer.clone();
                                                    tmpdiv.setStyles({"border":"0px"});
                                                    tmpdiv.empty();
                                                    tmpdiv.inject(taskItemContainer,"before");

                                                    //设置原有位置none
                                                    taskItemContainer.setStyles({"display":"none"});

                                                    //动画完成后插入真正的对象，恢复原位，并删除pre和clone
                                                    window.setTimeout(function(){
                                                        pre.destroy();
                                                        taskItemContainer.setStyles({"border":"1px solid #e6e6e6","height":_height+"px","display":"block","opacity":"1"});
                                                        taskItemContainer.inject(dr,"before");
                                                        //fx4把临时占用的位置删除
                                                        var fx4 = new Fx.Tween(tmpdiv,{duration:time});
                                                        fx4.start(["height"] ,_height+"px", "0px");
                                                        window.setTimeout(function(){
                                                            if(tmpdiv) tmpdiv.destroy()
                                                        },time);
                                                        //删除clone即el对象
                                                        el.destroy();
                                                    },time);

                                                    //刷新其他需要加载的位置，比如数量
                                                    var taskGroupFrom = taskItemContainer.getParent().getParent();
                                                    var taskGroupIn = dr.getParent().getParent();
                                                    _self.actions.taskGroupGet(_self.currentProjectGroupData.id,taskGroupFromId,function(json){
                                                        taskGroupFrom.getElement(".taskGroupItemTitleCount").set("text","("+json.data.taskCount +")")
                                                    })
                                                    _self.actions.taskGroupGet(_self.currentProjectGroupData.id,taskGroupInId,function(json){
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
                            }
                        });
                        //传递鼠标事件e
                        myDrag.start(e)
                    }.bind(this)
                });
            }.bind(this));

            //最后加一行占位
            var emptyDrag = new Element("div.empty.dragin",{name:"item",styles:{
                "width":"100%",
                    "height":"50px"
                }}).inject(taskGroupItemContainer);


        }.bind(this));
    },
    loadTaskNode:function(taskItemContainer,d){
        taskItemContainer.empty();

        var taskItemHover = new Element("div.taskItemHover",{styles:this.css.taskItemHover}).inject(taskItemContainer);
        if(d.priority == this.lp.urgency)taskItemHover.setStyle("background-color","#ffaf38");
        else if(d.priority == this.lp.emergency)taskItemHover.setStyle("background-color","#ff0000");
        var taskItemContent = new Element("div.taskItemContent",{styles:this.css.taskItemContent}).inject(taskItemContainer);
        var taskItemTitle = new Element("div.taskItemTitle",{styles:this.css.taskItemTitle,text:d.name}).inject(taskItemContent);
        taskItemHover.setStyles({"height":taskItemContainer.getHeight()});
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
            var task = new MWF.xApplication.TeamWork.Task(this,data,opt);
            task.open();
        }.bind(this));
    },
    openView:function(obj){
        this.naviTopMyTaskLayout.setStyles({"background-color":""});
        var _self = this;
        this.naviViewContainer.getElements(".naviItemContainer").each(function(d){
            this.naviItemChange(d,"leave")
        }.bind(this))
        this.naviItemChange(obj,"enter");

        if(this.viewContainer) delete this.viewContainer;
        if(this.viewListContainer) delete this.viewListContainer;
        this.taskContentLayout.empty();
        var viewContainer = this.viewContainer = new Element("div.viewContainer",{styles:this.css.viewContainer}).inject(this.taskContentLayout);
        viewContainer.addEvents({
            scroll:function(){
                var sTop = this.getScrollTop();
                var sHeight = this.getScrollHeight();
                var cHeight = viewContainer.getHeight();

                if(sHeight - sTop < cHeight+10){ //偏移量
                    if(_self.viewLoading){
                        _self.loadView(_self.curViewTaskId)
                    }
                }
            }
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
        this.actions.taskListNext("(0)",100,data,function(json){
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
    },
    loadView:function(id){
        this.viewListContainer.getElements(".viewNext").destroy();
        var data = {
            project:this.data.id
        };
        var tmpLoading = new Element("div.tmpLoading",{styles:{"background-color":"#ffffff"}}).inject(this.viewListContainer);
        this.app.setLoading(tmpLoading);
        this.viewLoading = false;
        this.actions.taskListNext(id||"(0)",10,data,function(json){
            this.viewListContainer.getElements(".tmpLoading").destroy();
            json.data.each(function(data){
                this.loadViewItem(data);
                this.curViewTaskId = data.id;
            }.bind(this));
            this.viewLoading = true;

            var sHeight = this.viewListContainer.getHeight();
            var cHeight = this.viewContainer.getHeight();

            if(sHeight<cHeight){
                var viewNext = new Element("div.viewNext",{styles:this.css.viewNext,text:"下一页"}).inject(this.viewListContainer);
                viewNext.addEvents({
                    click:function(){
                        this.loadView(this.curViewTaskId)
                    }.bind(this)
                })
            }

            // if(sHeight - sTop < cHeight+10){ //偏移量
            //     if(_self.viewLoading){
            //         _self.loadView(_self.curViewTaskId)
            //     }
            // }
        }.bind(this))
    },
    loadViewItem:function(data){
        var viewItem = new Element("div.viewItem",{styles:this.css.viewItem}).inject(this.viewListContainer);
        var viewName = new Element("div.viewName",{styles:this.css.viewName,text:data.name}).inject(viewItem);
        var viewStatus = new Element("div.viewStatus",{styles:this.css.viewStatus,text:data.priority}).inject(viewItem);
        var viewDuty = new Element("div.viewDuty",{styles:this.css.viewDuty}).inject(viewItem);
        var viewDutyIcon = new Element("div.viewDutyIcon",{styles:this.css.viewDutyIcon}).inject(viewDuty);
        if(data.executor && data.executor!=""){
            viewDutyIcon.set("text",data.executor.split("@")[0].substr(0,1));
            viewDutyIcon.set("title",data.executor.split("@")[0])
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
            var stat = new MWF.xApplication.TeamWork.Stat(this.contentLayout,this.app,this.data,{

                }
            );
            stat.load();
        }.bind(this));
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
        if(this.formTopTextNode) this.formTopTextNode.set("text",this.lp.newTaskTitle);
        //this.formTableArea
        this.titleContainer = new Element("div.titleContainer",{styles:this.css.titleContainer}).inject(this.formTableArea);
        this.titleValue = new Element("input.titleValue",{styles:this.css.titleValue,placeholder:this.lp.newTaskPlaceholder}).inject(this.titleContainer);

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
                this.actions.taskSave(data,function(json){
                    if(json.data.id){
                        var data = {
                            projectObj:this.data.projectObj || null,
                            taskId:json.data.id
                        };
                        var opt = {

                        };
                        MWF.xDesktop.requireApp("TeamWork", "Task", function(){
                            var task = new MWF.xApplication.TeamWork.Task(this,data,opt);
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
        this.explorer = this.data.projectObj;
        if(this.formTopTextNode) this.formTopTextNode.set("text",this.data.isNew?this.lp.taskGroupAdd:this.lp.taskGroupEdit);
        //this.formTableArea
        this.titleContainer = new Element("div.titleContainer",{styles:this.css.titleContainer}).inject(this.formTableArea);
        this.titleValue = new Element("input.titleValue",{styles:this.css.titleValue,placeholder:this.lp.newTaskPlaceholder}).inject(this.titleContainer);
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
                this.actions.taskGroupSave(data,function(json){
                    this.fireEvent("createTask");
                    this.close(json);
                    if(this.explorer){
                        if(this.data.node){
                            if(json.data.id){
                                this.actions.taskGroupGet(this.explorer.currentProjectGroupData.id,json.data.id,function(json){
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
                        _self.actions.taskGroupDelete(_self.data.id,function(json){
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

        this.actions.projectStarListNext("(0)",100,{},function(json){
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
            this.actions.projectListNext("(0)",100,{},function(json){
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