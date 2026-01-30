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

        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.ProjectAction;

        this.path = "../x_component_TeamWork/$Project/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        this.data = data;
    },
    load: function () {
        
        this.container.setStyles({display:"flex"});
        this.container.empty();
        
        this.actions.get(this.data.id,function(json){
            this.data = json.data;
            
            this.createTopBarLayout();
            this.createContentLayout();
            
            //this.topBarTabItemTask.click();
            this.topBarQuadrantContainer.click();
        }.bind(this))
        // this.topBarTabItemTask.click();
    },
    createTopBarLayout:function(){
        //头部区域
        var _self = this;
        this.topBarLayout = new Element("div.topBarLayout",{styles:this.css.topBarLayout}).inject(this.container);
        this.topBarBackContainer = new Element("div.topBarBackContainer",{styles:this.css.topBarBackContainer}).inject(this.topBarLayout);
        this.topBarBackHomeIcon = new Element("div.topBarBackHomeIcon",{styles:this.css.topBarBackHomeIcon}).inject(this.topBarBackContainer);
        this.topBarBackHomeIcon.addEvents({
            click:function(){
                this.app.reload()
            }.bind(this),
            mouseover:function(){
                var opt={
                    axis: "y"      //箭头在x轴还是y轴上展现
                };
                this.app.showTips(this.topBarBackHomeIcon,{_html:"<div style='margin:2px 5px;'>"+this.lp.backProject+"</div>"},opt);
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
                //this.createTaskLayout();
            }.bind(this)
        });
        

        //if(this.data.control && this.data.control.founder){
        this.topBarSettingContainer = new Element("div.topBarSettingContainer",{styles:this.css.topBarSettingContainer}).inject(this.topBarLayout);
        //四象限
        this.topBarQuadrantContainer = new Element("div.topBarQuadrantContainer.naviItem",{styles:this.css.topBarQuadrantContainer,name:"quadrant"}).inject(this.topBarSettingContainer);
        this.topBarQuadrantIcon = new Element("div.naviItemImg",{styles:this.css.topBarQuadrantIcon}).inject(this.topBarQuadrantContainer);
        this.topBarQuadrantText = new Element("div.topBarQuadrantText",{styles:this.css.topBarQuadrantText,text:this.lp.naviQuadrant}).inject(this.topBarQuadrantContainer);
        this.topBarQuadrantContainer.addEvents({
            click:function(){
                this.topBarQuadrantContainer.setStyles({"background-color":"#ffffff"});
                this.topBarQuadrantIcon.setStyles({"background-image":"url('../x_component_TeamWork/$Project/default/icon/icon_quadrant_click.png')"});
                this.topBarQuadrantText.setStyles({"color":"#4A90E2"});
                this.topBarLaneContainer.setStyles({"background-color":""});
                this.topBarLaneIcon.setStyles({"background-image":"url('../x_component_TeamWork/$Project/default/icon/icon_lane.png')"});
                this.topBarLaneText.setStyles({"color":"#666666"});

                this.setNaviItem(this.topBarQuadrantContainer)
                this.createTaskLayout();
                
            }.bind(this)
        })

        //泳道
        this.topBarLaneContainer = new Element("div.topBarLaneContainer.naviItem",{styles:this.css.topBarLaneContainer,name:"lane"}).inject(this.topBarSettingContainer);
        this.topBarLaneIcon = new Element("div.naviItemImg",{styles:this.css.topBarLaneIcon}).inject(this.topBarLaneContainer);
        this.topBarLaneText = new Element("div.topBarLaneText",{styles:this.css.topBarLaneText,text:this.lp.naviLane}).inject(this.topBarLaneContainer);

        this.topBarLaneContainer.addEvents({
            click:function(){
                this.topBarLaneContainer.setStyles({"background-color":"#ffffff"});
                this.topBarLaneIcon.setStyles({"background-image":"url('../x_component_TeamWork/$Project/default/icon/icon_lane_click.png')"});
                this.topBarLaneText.setStyles({"color":"#4A90E2"});
                this.topBarQuadrantContainer.setStyles({"background-color":""});
                this.topBarQuadrantIcon.setStyles({"background-image":"url('../x_component_TeamWork/$Project/default/icon/icon_lane.png')"});
                this.topBarQuadrantText.setStyles({"color":"#666666"});

                this.setNaviItem(this.topBarLaneContainer);
                this.createTaskLayout();
                
            }.bind(this)
        })

        //项目详情
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
                    "background-image":"url(../x_component_TeamWork/$Project/default/icon/icon_caidan_click.png)"
                });
                this.topBarSettingMenuContainer.getElements(".topBarSettingMenuText").setStyles({
                    "color":"#4A90E2"
                });
            }.bind(this),
            mouseout:function(){
                this.topBarSettingMenuContainer.getElements(".topBarSettingMenuIcon").setStyles({
                    "background-image":"url(../x_component_TeamWork/$Project/default/icon/icon_caidan.png)"
                });
                this.topBarSettingMenuContainer.getElements(".topBarSettingMenuText").setStyles({
                    "color":"#666666"
                });
            }.bind(this)
        });
        this.topBarSettingMenuIcon = new Element("div.topBarSettingMenuIcon",{styles:this.css.topBarSettingMenuIcon}).inject(this.topBarSettingMenuContainer);
        this.topBarSettingMenuText = new Element("div.topBarSettingMenuText",{styles:this.css.topBarSettingMenuText,text:this.lp.setting}).inject(this.topBarSettingMenuContainer);
        //}
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
        //中间展开折叠区域
        if(this.foldContainer) this.foldContainer.destroy();
        this.foldContainer = new Element("div.foldContainer",{styles:this.css.foldContainer}).inject(this.contentLayout);
        this.foldIcon = new Element("div.foldIcon",{styles:this.css.foldIcon,text:"<"}).inject(this.foldContainer);

        this.foldIcon.addEvents({
            mouseover:function(){
                this.naviLayout.setStyles({"border-right": "1px solid #1b9aee"});
                //this.foldContainer.setStyles({"border-left":"1px solid #1b9aee"});
                this.foldIcon.setStyles({"background-color":"#1b9aee","color":"#ffffff"});
            }.bind(this),
            mouseout:function(){
                this.naviLayout.setStyles({"border-right": "1px solid #cccccc"});
                this.foldIcon.setStyles({"background-color":"#ffffff","color":""});
            }.bind(this),
            click:function(){
                if(this.naviFold){
                    var fx = new Fx.Tween(this.naviLayout,{duration:100});
                    //this.naviLayout.show();
                    fx.start(["width"] ,"0", "300")
                        .chain(function(){
                            this.foldIcon.set("text","<");
                            this.naviFold = false;
                        }.bind(this));

                }else{ //aaaaa
                    var fx1 = new Fx.Tween(this.naviLayout,{duration:100});
                    fx1.start(["width"] ,"300", "0")
                        .chain(function(){
                            //this.naviLayout.hide();
                            this.foldIcon.set("text",">");
                            this.naviFold = true;
                        }.bind(this));

                }
            }.bind(this)
        })
        var _margin_height = (this.foldContainer.getHeight())/2 - (this.foldIcon.getHeight())/2;
        this.foldIcon.setStyles({"margin-top":_margin_height+"px"});
    },
    createNaviContent:function(){
        //左侧导航
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

            //this.naviTopMyTaskLayout.click();
            //this.quadrantTaskContainer.click();
        }.bind(this));
    },
    createNaviTask:function(){
        //左侧导航-任务，四象限等
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
                        //this.naviTopMyTaskLayout.click();
                        window.setTimeout(function(){
                            this.curNaviItem.click();
                        }.bind(this),1000)
                        
                        //刷新数量
                        //this.reloadTaskCountInfor()
                    }.bind(this)
                };
                var newTask = new MWF.xApplication.TeamWork.Project.NewTask(this,data,opt,{});
                newTask.open();

            }.bind(this),
            mouseover:function(){
                this.naviTopTaskAdd.setStyles({
                    "background-image":"url(../x_component_TeamWork/$Project/default/icon/icon_zengjia_blue2_click.png)"
                });
                //this.app.showTips(this.naviTopTaskAdd,{_html:"<div style='margin:2px 5px;'>"+this.lp.taskAdd+"</div>"});
                this.app.tips(this.naviTopTaskAdd,this.lp.taskAdd)
            }.bind(this),
            mouseout:function(){
                this.naviTopTaskAdd.setStyles({
                    "background-image":"url(../x_component_TeamWork/$Project/default/icon/icon_jia.png)"
                });
            }.bind(this)
        });

        if(this.data.control && this.data.control.taskCreate == false){
            this.naviTopTaskAdd.destroy();
            //delete  this.naviTopTaskAdd;
        }

        //任务四象限
        // this.quadrantTaskContainer = new Element("div.quadrantTaskContainer.naviItem",{text:this.lp.quadrantTask,styles:this.css.quadrantTaskContainer}).inject(this.naviTop);
        // this.quadrantTaskContainer.addEvents({
        //     "click":function(){
        //         this.createQuadrant();
        //         this.setNaviItem(this.quadrantTaskContainer);
        //     }.bind(this),
        //     "mouseover":function(){
        //         if(_self.curNaviItem != this) this.setStyles({"background-color":"#F2F5F7"})   
        //     },
        //     "mouseout":function(){
        //         if(_self.curNaviItem != this) this.setStyles({"background-color":"#ffffff"})   
        //     }
        // })


        this.naviTopMyTaskLayout = new Element("div.naviTopMyTaskLayout",{styles:this.css.naviTopMyTaskLayout}).inject(this.naviTop);
        this.naviTopMyTaskLayout.addEvents({
            click:function(){ 
                if(this.curNaviName == "quadrant"){
                    this.createQuadrant(); //四象限模式
                }else if(this.curNaviName == "lane"){
                    this.createTaskGroup(); //泳道模式
                }else{
                    this.topBarQuadrantContainer.click()
                }
                
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
    createQuadrant:function(){
        //右侧四象限区域
        this.taskContentLayout.empty();
        MWF.xDesktop.requireApp("TeamWork", "Quadrant", function(){
            var quadrant = new MWF.xApplication.TeamWork.Quadrant(this.taskContentLayout,this.app,this.data );
            quadrant.load();
        }.bind(this));
    },
    reloadTaskCountInfor:function(){
        //刷新任务数量区域
        window.setTimeout(function(){
            this.rootActions.TaskAction.statiticMyProject(this.data.id,function(json){
                this.projectGroupData = json.data;
                if(this.projectGroupData.groups && this.projectGroupData.groups.length>0){
                    this.currentProjectGroupData = this.projectGroupData.groups[0]; //默认只有一个分组
                    this.naviTopMyTaskCount.set("text","("+this.currentProjectGroupData.completedTotal+"/"+this.currentProjectGroupData.taskTotal+")")
                    this.loadTaskLine();
                }

            }.bind(this));
        }.bind(this),1000)

    },
    loadTaskLine:function(){
        //我的任务下面的进度条
        if(this.completeLine) this.completeLine.destroy()
        this.completeLine = new Element("div.completeLine",{styles:this.css.completeLine}).inject(this.naviTopTaskLine);
        this.completeLine.addEvents({
            mouseover:function(){
                this.app.showTips(this.completeLine,{_html:"<div style='margin:2px 5px;'>"+this.lp.taskCompleteText+":"+this.currentProjectGroupData.completedTotal+"</div>"});
                //this.app.tips(this.completeLine,this.lp.taskCompleteText + ": " + this.currentProjectGroupData.completedTotal);
            }.bind(this)
        });
        this.overLine = new Element("div.overLine",{styles:this.css.overLine}).inject(this.naviTopTaskLine);
        this.overLine.addEvents({
            mouseover:function(){
                this.app.showTips(this.overLine,{_html:"<div style='margin:2px 5px;'>"+this.lp.taskovertimeText+":"+this.currentProjectGroupData.overtimeTotal+"</div>"});
                ////alert(this.currentProjectGroupData.overtimeTotal)
                //this.app.tips(this.overLine,this.lp.taskovertimeText + ": " + this.currentProjectGroupData.overtimeTotal)
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
        // 视图导航区域
        if(this.naviView) this.naviView.destroy();
        this.naviView = new Element("div.naviView",{styles:this.css.naviView}).inject(this.naviLayout);
        this.naviViewTitleContainer = new Element("div.naviViewTitleContainer",{styles:this.css.naviViewTitleContainer}).inject(this.naviView);
        this.naviViewTitle = new Element("div.naviViewTitle",{styles:this.css.naviViewTitle,text:this.lp.viewTitle}).inject(this.naviViewTitleContainer);

        this.createNaviViewItem();
    },
    createNaviViewItem:function(){
        // 视图内容区域
        if(this.naviViewContainer) this.naviViewContainer.destroy();
        this.naviViewContainer = new Element("div.naviViewContainer",{styles:this.css.naviViewContainer}).inject(this.naviView);

        var _html = this.path + this.options.style+"/naviview.html";
        var _css = this.path + this.options.style+"/style.css";
        this.naviViewContainer.loadAll({"html":_html,"css":_css},{"bind":{"lp":this.lp,"data":this.data},"module":this},function(){

        }.bind(this))

    },
    openView:function(type,e){
        //打开视图导航
        this.taskContentLayout.empty();

        var options={
            projectId:this.data.id,
            key:this.searchKey,
            type:type
        }
        MWF.xDesktop.requireApp("TeamWork", "TaskView", function(){
            var taskView = new MWF.xApplication.TeamWork.TaskView( this.taskContentLayout,this,options );
            taskView.load();
            this.searchKey = ''
        }.bind(this));

        this.setNaviItem(e.currentTarget);
    },
    setNaviItem:function(target){
        //设置左侧导航点击样式
        var items = this.container.getElements('.naviItem');
        items.setStyles({"background-color":"#ffffff"});
        target.setStyles({"background-color":"#f2f5f7"});
        
        items.forEach(function(item){
            var imgIcon = item.getElement('.naviItemImg');
            if(imgIcon){ 
                item.removeClass("curNaviItem");
                var _name = item.get('name');
                if(_name == 'all') imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_all.png")'})
                else if(_name == 'admin') imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_admin.png")'});
                else if(_name == 'my') imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_wdxm_xm.png")'});
                else if(_name == 'flow') imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_rw_wwc.png")'});
                else if(_name == 'delay') imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_delay.png")'});
                else if(_name == 'canceled') imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_canceled.png")'});
                else if(_name == 'completed') imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_renwu_ywc.png")'});
                else if(_name == "quadrant"){
                    item.setStyles({"background-color":""});
                    item.getElements("div").setStyles({"color":"#666666"});
                    imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_quadrant.png")'});
                }else if(_name == "lane"){
                    item.setStyles({"background-color":""});
                    item.getElements("div").setStyles({"color":"#666666"});
                    imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_lane.png")'});
                }
            }
        });

        var _imgIcon = target.getElement('.naviItemImg');
        var _name = target.get('name');
        if(_imgIcon){
            target.addClass("curNaviItem");
            
            if(_name == 'all') _imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_all_click.png")'})
            else if(_name == 'admin') _imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_admin_click.png")'})
            else if(_name == 'my') _imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_wdxm_xm_click.png")'})
            else if(_name == 'flow') _imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_rw_wwc_click.png")'})
            else if(_name == 'delay') _imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_delay_click.png")'})
            else if(_name == 'canceled') _imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_canceled_click.png")'})
            else if(_name == 'completed') _imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_renwu_ywc_click.png")'})
            else if(_name == "quadrant"){
                target.setStyles({"background-color":"#ffffff"});
                target.getElements("div").setStyles({"color":"#4A90E2"});
                _imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_quadrant_click.png")'});
            }else if(_name == "lane"){
                target.setStyles({"background-color":"#ffffff"});
                target.getElements("div").setStyles({"color":"#4A90E2"});
                _imgIcon.setStyles({"background-image":'url("../x_component_TeamWork/$Project/default/icon/icon_lane_click.png")'});
            }
        }
        this.curNaviName = _name;
        this.curNaviItem = target;
    },
    
    createTaskContent:function(){
        //创建右侧内容区域
        this.taskContentLayout = new Element("div.taskContentLayout",{styles:this.css.taskContentLayout}).inject(this.contentLayout);
    },
 
    createTaskGroup:function(){
        //右侧泳道区域
        this.taskContentLayout.empty();
        MWF.xDesktop.requireApp("TeamWork", "TaskList", function(){
            var data = {
                "projectId":this.data.id,
                "groupId":this.currentProjectGroupData.id
            }
            var taskList = new MWF.xApplication.TeamWork.TaskList(this,this.taskContentLayout,this.app,data);
            taskList.load();
        }.bind(this));
        
    },

    openTask:function(id,callback){
        //打开任务
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
                    this.reloadTaskCountInfor()
                }
            }.bind(this)
        };
        MWF.xDesktop.requireApp("TeamWork", "Tasks", function(){
            var task = new MWF.xApplication.TeamWork.Tasks(this,data,opt,{projectObj:this});
            task.open();
        }.bind(this));
    },
    openSearch:function(key){
        //点击搜索框
        if(this.naviItemAll){
            this.searchKey = key;
            this.naviItemAll.click();
        }
    },
    

    reloadInfor:function(){
        //刷新页面上的一些信息，比如数量等未想好

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
                    //this.okAction.click();
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
                var _high = this.app.lp.task.importantStatus.high;
                var _low = this.app.lp.task.importantStatus.low;
                var data={
                    taskGroupId:this.data.taskGroupId,
                    taskListIds:this.data.taskListIds || [],
                    name: this.titleValue.get("value").trim(),
                    important:_high,
                    urgency:_high
                };
                //debugger
                this.rootActions.TaskAction.save(data,function(json){ 
                    if(json.data.id){
                        var data = {
                            projectObj:this.data.projectObj || null,
                            taskId:json.data.id
                        };
                        var opt = {

                        };
                        MWF.xDesktop.requireApp("TeamWork", "Tasks", function(){
                            var task = new MWF.xApplication.TeamWork.Tasks(this,data,opt,{projectObj:this.explorer});
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

        this.rootActions.ProjectAction.listStarNextWithFilter(1,100,{},function(json){
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
            this.rootActions.ProjectAction.listPageWithFilter(1,200,{},function(json){
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