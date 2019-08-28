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

        this.app.addEvent("resize", function(){
            this.resize();
        }.bind(this));
    },
    resize:function(){
        //alert("resize")

        //taskGroupItemContainer
        //taskGroupLayout，taskGroupItemContainer  自定义高度


        this.container.getElements(".taskGroupItemContainer").each(function(d){
           var pe =  d.getParent();
           var pr_w = pe.getElement(".taskGroupItemTitleContainer").getHeight().toInt();

           var _h = pe.getHeight().toInt() - pr_w -10-10;
           d.setStyles({"height":_h+"px"})

        });
    },
    createTopBarLayout:function(){
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
                this.createTaskLayout();
            }.bind(this)
        });
        this.topBarTabItemStat = new Element("div.topBarTabItemStat",{styles:this.css.topBarTabItemStat,text:this.lp.stat}).inject(this.topBarTabsContainer);
        this.topBarTabItemStat.addEvents({
            click:function(){
                this.createStatLayout();
            }.bind(this)
        });
        this.topBarSettingContainer = new Element("div.topBarSettingContainer",{styles:this.css.topBarSettingContainer}).inject(this.topBarLayout);
        this.topBarSettingMenuContainer = new Element("div.topBarSettingMenuContainer",{styles:this.css.topBarSettingMenuContainer}).inject(this.topBarSettingContainer);
        this.topBarSettingMenuContainer.addEvents({
            click:function(){
                alert("open")
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
        this.topBarSettingMenuText = new Element("div.topBarSettingMenuText",{styles:this.css.topBarSettingMenuText,text:this.lp.menu}).inject(this.topBarSettingMenuContainer);

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
                this.currentProjectGroupData = this.projectGroupData.groups[0];
            }
            this.naviLayout.empty();
            this.createNaviTask();
            this.createNaviView();
        }.bind(this));
    },
    createNaviTask:function(){
        if(!this.currentProjectGroupData) return;
        if(this.naviTop) this.naviTop.destroy();
        this.naviTop = new Element("div.naviTop",{styles:this.css.naviTop}).inject(this.naviLayout);

        this.naviTopSearchContainer = new Element("div.naviTopSearchContainer",{styles:this.css.naviTopSearchContainer}).inject(this.naviTop);
        this.naviTopSearchIn = new Element("input.naviTopSearchIn",{styles:this.css.naviTopSearchIn,placeholder:this.lp.searchTask}).inject(this.naviTopSearchContainer);

        this.naviTopTaskContainer = new Element("div.naviTopTaskContainer",{styles:this.css.naviTopTaskContainer}).inject(this.naviTop);
        this.naviTopTaskText = new Element("div.naviTopTaskText",{styles:this.css.naviTopTaskText,text:this.lp.task}).inject(this.naviTopTaskContainer);
        this.naviTopTaskAdd = new Element("div.naviTopTaskAdd",{styles:this.css.naviTopTaskAdd}).inject(this.naviTopTaskContainer);
        this.naviTopTaskAdd.addEvents({
            click:function(){
                var data = {
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
                this.createTaskGroup();
            }.bind(this)
        });
        this.naviTopMyTaskContainer = new Element("div.naviTopMyTaskContainer",{styles:this.css.naviTopMyTaskContainer}).inject(this.naviTopMyTaskLayout);
        this.naviTopMyTaskText = new Element("div.naviTopMyTaskText",{styles:this.css.naviTopMyTaskText,text:this.lp.myTask}).inject(this.naviTopMyTaskContainer);
        this.naviTopMyTaskCount = new Element("div.naviTopMyTaskCount",{styles:this.css.naviTopMyTaskCount,text:"("+this.currentProjectGroupData.completedTotal+"/"+this.currentProjectGroupData.taskTotal+")"}).inject(this.naviTopMyTaskContainer);

        this.naviTopTaskLineContainer = new Element("div.naviTopTaskLineContainer",{styles:this.css.naviTopTaskLineContainer}).inject(this.naviTopMyTaskLayout);
        this.naviTopTaskLine = new Element("div.naviTopTaskLine",{styles:this.css.naviTopTaskLine}).inject(this.naviTopTaskLineContainer);
        this.loadTaskLine();

        this.naviTopMyTaskLayout.click();
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

                    },
                    mouseenter:function(){ _self.naviItemChange(this,"enter")},
                    mouseleave:function(){_self.naviItemChange(this,"leave")}
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
                        alert("open")
                    }.bind(this),
                    mouseenter:function(){ this.naviItemChange(this.naviItemMyContainer,"enter")}.bind(this),
                    mouseleave:function(){ this.naviItemChange(this.naviItemMyContainer,"leave")}.bind(this)
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
                    mouseenter:function(){ this.naviItemChange(this.naviItemFlowContainer,"enter")}.bind(this),
                    mouseleave:function(){ this.naviItemChange(this.naviItemFlowContainer,"leave")}.bind(this)
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
                    mouseenter:function(){ this.naviItemChange(this.naviItemCompleteContainer,"enter")}.bind(this),
                    mouseleave:function(){ this.naviItemChange(this.naviItemCompleteContainer,"leave")}.bind(this)
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
                    mouseenter:function(){ this.naviItemChange(this.naviItemOverContainer,"enter")}.bind(this),
                    mouseleave:function(){ this.naviItemChange(this.naviItemOverContainer,"leave")}.bind(this)
                });
            }else{
                //自定义视图

            }
        }.bind(this));

    },
    naviItemChange:function(node,action){
        if(action == "enter"){
            node.setStyles({"background-color":"#F2F5F7"});
            var divs = node.getElements("div");
            divs[0].setStyles({"background-image":divs[0].getStyle("background-image").replace(".png","_click.png")});
            divs[1].setStyles({"color":"#4A90E2"});
            if(divs[2]){
                divs[2].setStyles({"background-image":divs[2].getStyle("background-image").replace(".png","_click.png")});
            }
        }else if(action == "leave"){
            node.setStyles({"background-color":""});
            var divs = node.getElements("div");
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
    createTaskGroup:function(){
        if(this.currentProjectGroupData && this.currentProjectGroupData.id){
            this.app.setLoading(this.taskContentLayout);
            this.actions.taskGroupList(this.currentProjectGroupData.id,function(json){
                this.taskContentLayout.empty();
                json.data.each(function(data,i){
                    //if(i>0) return;
                    var taskGroupLayout = new Element("div.taskGroupLayout",{styles:this.css.taskGroupLayout,id:data.id}).inject(this.taskContentLayout);
                    this.createTaskGroupItemLayout(taskGroupLayout,data);
                }.bind(this));
            }.bind(this))
        }
    },
    createTaskGroupItemLayout:function(node,data){
        var taskGroupItemTitleContainer = new Element("div.taskGroupItemTitleContainer",{styles:this.css.taskGroupItemTitleContainer}).inject(node);
        new Element("div.taskGroupItemTitleText",{styles:this.css.taskGroupItemTitleText,text:data.name}).inject(taskGroupItemTitleContainer);
        new Element("div.taskGroupItemTitleCount",{styles:this.css.taskGroupItemTitleCount,text:"("+"0"+")"}).inject(taskGroupItemTitleContainer);
        var taskGroupItemTitleMore = new Element("div.taskGroupItemTitleMore",{styles:this.css.taskGroupItemTitleMore}).inject(taskGroupItemTitleContainer);
        taskGroupItemTitleMore.addEvents({
            click:function(){}.bind(this)
        });
        var taskGroupItemTitleAdd = new Element("div.taskGroupItemTitleAdd",{styles:this.css.taskGroupItemTitleAdd}).inject(taskGroupItemTitleContainer);
        taskGroupItemTitleAdd.addEvents({
            click:function(){}.bind(this)
        });
        var taskGroupItemContainer = new Element("div.taskGroupItemContainer",{styles:this.css.taskGroupItemContainer}).inject(node);
        var _h = node.getHeight().toInt()-taskGroupItemTitleContainer.getHeight().toInt() - 10 - 10;

        taskGroupItemContainer.setStyles({"height":_h+"px"});
        this.app.setScrollBar(taskGroupItemContainer);
        //this.app.setLoading(taskGroupItemContainer);
        this.actions.taskListByListId(data.id,function(json){
            taskGroupItemContainer.empty();
            // var taskListData = [
            //     {
            //         "name":"任务一",
            //         "remark":"备注信息",
            //         "status":"flow",
            //         "person":"金飞"
            //     },
            //
            //
            //
            //
            //     {
            //         "name":"任务一",
            //         "remark":"备注信息",
            //         "status":"flow",
            //         "person":"金飞"
            //     }
            //
            // ];
            // if(node.get("id")=="ed426176-dd36-4151-8b90-87e6c16e91eb") taskListData = [];
            var taskListData = json.data;
            //alert(JSON.stringify(taskListData));
            taskListData.each(function(d){
                var taskItemContainer = new Element("div.taskItemContainer",{styles:this.css.taskItemContainer}).inject(taskGroupItemContainer);
                var int;
                taskItemContainer.addEvents({
                    click:function(){
                        this.openTask(d.id)
                    }.bind(this),
                    mouseover:function(){

                        int = window.setInterval(function(){
                            if(taskItemHover.getWidth() == 10) return;
                            taskItemHover.setStyles({
                                "width":(taskItemHover.getWidth()+1)+"px"
                            });
                        }.bind(this),20)
                    },
                    mouseout:function(){
                        window.clearInterval(int);
                        taskItemHover.setStyles({"width":"0px"});
                    }.bind(this)
                });
                var taskItemHover = new Element("div.taskItemHover",{styles:this.css.taskItemHover}).inject(taskItemContainer);
                var taskItemContent = new Element("div.taskItemContent",{styles:this.css.taskItemContent}).inject(taskItemContainer);
                var taskItemTitle = new Element("div.taskItemTitle",{styles:this.css.taskItemTitle,text:d.name}).inject(taskItemContent);

                taskItemHover.setStyles({"height":taskItemContainer.getHeight()})
            }.bind(this))

        }.bind(this));
    },
    openTask:function(id){
        var data = {
            taskId:id
        };
        var opt = {};
        MWF.xDesktop.requireApp("TeamWork", "Task", function(){
            var task = new MWF.xApplication.TeamWork.Task(this,data,opt);
            task.open();
        }.bind(this));
    },
    createStatLayout:function(){

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
                    name: this.titleValue.get("value").trim()
                };
                this.actions.taskSave(data,function(json){
                    if(json.data.id){
                        var data = {
                            taskId:json.data.id
                        };
                        var opt = {};
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

        this.naviViewTipLayout = new Element("div.naviViewTipLayout",{styles:this.css.naviViewTipLayout}).inject(this.contentNode);
        this.naviViewTipEditContainer = new Element("div.naviViewTipContainer",{styles:this.css.naviViewTipContainer}).inject(this.naviViewTipLayout);
        this.naviViewTipEditIcon = new Element("div.naviViewTipEditIcon",{styles:this.css.naviViewTipEditIcon}).inject(this.naviViewTipEditContainer);
        this.naviViewTipEditText = new Element("div.naviViewTipText",{styles:this.css.naviViewTipText,text:this.lp.viewEdit}).inject(this.naviViewTipEditContainer);
        this.naviViewTipEditContainer.addEvents({
            click:function(){

            }.bind(this),
            mouseover:function(){this.naviViewTipEditContainer.setStyles({"background-color":"#F7F7F7"})}.bind(this),
            mouseout:function(){this.naviViewTipEditContainer.setStyles({"background-color":""})}.bind(this)
        });

        this.naviViewTipRemoveContainer = new Element("div.naviViewTipContainer",{styles:this.css.naviViewTipContainer}).inject(this.naviViewTipLayout);
        this.naviViewTipRemoveIcon = new Element("div.naviViewTipRemoveIcon",{styles:this.css.naviViewTipRemoveIcon}).inject(this.naviViewTipRemoveContainer);
        this.naviViewTipRemoveText = new Element("div.naviViewTipText",{styles:this.css.naviViewTipText,text:this.lp.viewRemove}).inject(this.naviViewTipRemoveContainer);
        this.naviViewTipRemoveContainer.addEvents({
            click:function(e){
                _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){


                },function(){
                    this.close();

                });
            }.bind(this),
            mouseover:function(){this.naviViewTipRemoveContainer.setStyles({"background-color":"#F7F7F7"})}.bind(this),
            mouseout:function(){this.naviViewTipRemoveContainer.setStyles({"background-color":""})}.bind(this)
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
            if(json.data.length>0){
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
            this.actions.projectListNext("(0)",100,{},function(json){
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

        this.naviViewTipLayout = new Element("div.naviViewTipLayout",{styles:this.css.naviViewTipLayout}).inject(this.contentNode);
        this.naviViewTipEditContainer = new Element("div.naviViewTipContainer",{styles:this.css.naviViewTipContainer}).inject(this.naviViewTipLayout);
        this.naviViewTipEditIcon = new Element("div.naviViewTipEditIcon",{styles:this.css.naviViewTipEditIcon}).inject(this.naviViewTipEditContainer);
        this.naviViewTipEditText = new Element("div.naviViewTipText",{styles:this.css.naviViewTipText,text:this.lp.viewEdit}).inject(this.naviViewTipEditContainer);
        this.naviViewTipEditContainer.addEvents({
            click:function(){

            }.bind(this),
            mouseover:function(){this.naviViewTipEditContainer.setStyles({"background-color":"#F7F7F7"})}.bind(this),
            mouseout:function(){this.naviViewTipEditContainer.setStyles({"background-color":""})}.bind(this)
        });

        this.naviViewTipRemoveContainer = new Element("div.naviViewTipContainer",{styles:this.css.naviViewTipContainer}).inject(this.naviViewTipLayout);
        this.naviViewTipRemoveIcon = new Element("div.naviViewTipRemoveIcon",{styles:this.css.naviViewTipRemoveIcon}).inject(this.naviViewTipRemoveContainer);
        this.naviViewTipRemoveText = new Element("div.naviViewTipText",{styles:this.css.naviViewTipText,text:this.lp.viewRemove}).inject(this.naviViewTipRemoveContainer);
        this.naviViewTipRemoveContainer.addEvents({
            click:function(e){
                _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){


                },function(){
                    this.close();

                });
            }.bind(this),
            mouseover:function(){this.naviViewTipRemoveContainer.setStyles({"background-color":"#F7F7F7"})}.bind(this),
            mouseout:function(){this.naviViewTipRemoveContainer.setStyles({"background-color":""})}.bind(this)
        });


        if(callback)callback();
    }

});