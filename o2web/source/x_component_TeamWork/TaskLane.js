/*
    泳道taskList
 */

MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.TaskLane = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
		"mvcStyle": "style.css"

    },
    initialize: function (explorer,container, app, data, options) {
        this.setOptions(options);
        this.explorer = explorer;
        this.container = container;

        this.app = app;
        this.lp = this.app.lp.Lane;
        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.ProjectAction;
        
        this.path = "../x_component_TeamWork/$TaskLane/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        this.projectId = data.projectId;
        this.groupId = data.groupId;
    },
    load: function () {
        
        var _html  = this.path + this.options.style + "/view.html";
        this.cssFile = this.path + this.options.style  + "/style.css";
        this.container.loadAll({"html":_html,css:this.cssFile},{"bind": {"lp": this.lp,"data":this.projectData}, "module": this},function(){ 
            
            // var func = function(p){
            //     console.log("==============="+p)
            // }

            ['processing','completed'].map(function(d){
                this.loadLane(d)
            }.bind(this))

            

            // var navparm = 'task'
            // var maps = {
            //     "read" : this.loadRead,
            //     "taskCompleted":this.loadTaskDone,
            //     "carelist":this.loadCareList,
            //     "draftlist":this.loadDraftList,
            //     "myCreatelist":this.loadMyCreateWork,
            //     "flowlist":this.loadFlowWork,
            //     "archivelist":this.loadArchiveWork,
            //     "startlist":this.startFileList,
            //     "task":this.loadTask
            // }

            // maps[navparm].apply(this);
            

        }.bind(this))

        // this.rootActions.TaskListAction.listWithTaskGroup(this.groupId,function(json){
        //     json.data.each(function(data,i){
        //         var taskGroupLayout = new Element("div.taskGroupLayout",{styles:this.css.taskGroupLayout,id:data.id}).inject(this.container);
        //         taskGroupLayout.set("sortable",data.control.sortable); //控制是否能排序
        //         this.createTaskGroupItemLayout(taskGroupLayout,data);
        //     }.bind(this));
            
        // }.bind(this))

		        
    },
    loadLane:function(lane){
        
        var maps = {
            "processing":this.container.getElementById('processingLane'),
            "completed":this.container.getElementById('completedLane')
        }
        
        var laneContent = maps[lane];
        laneContent.empty();
        this.app.setLoading(laneContent);
        var _html  = this.path + this.options.style + "/lane.html";
        this.rootActions.TaskAction.listPageWithFilter(1,100,{"project":this.projectId},function(json){
            laneContent.empty();
            laneContent.loadAll({"html":_html,css:this.cssFile},{"bind": {"lp": this.lp,"data":json.data}, "module": this},function(){
                
                var _laneItem = laneContent.getElements('.laneItem');
                debugger
                _laneItem.map(function(item){
                    var drag = new Drag(item, {
                        "compensateScroll": true,
                        "onStart": function(el, e){
                            this.dragMove(el,e);
                            drag.stop();
                        }.bind(this)
                    });
                }.bind(this))

                
            }.bind(this))
        }.bind(this))
        
    },

    dragMove:function(el,e){
        var _self = this;
        var taskItemContainer = el;
        var time = 200;
        //this.cloneTaskItem = new Element("").inject()
        var position = el.getPosition(this.container);
        //alert(JSON.stringify(position))
        //var clone = this.cloneTaskItem = el.clone(true,true).inject($(document.body));
        var clone = this.cloneTaskItem = el.clone(true,true).inject(this.container);
        this.cloneTaskItem.removeClass("dragin");
        this.cloneTaskItem.setStyles({
            "top":position.y+"px",
            "left":(position.x + this.container.getScrollLeft())+"px",
            "z-index":"9999",
            "margin":"0px",
            "position":"absolute",
            "cursor":"move"
        });
        el.setStyles({"border":"1px dotted #000000","opacity":"0.3"});
        var _height = this.cloneTaskItem.getHeight().toInt() - 2;

        this.ccc = 0;
        var drag = new Drag.Move(this.cloneTaskItem, {
            container: this.container,
            "compensateScroll": true,
            "droppables": $$(".laneItem"),
            "onStart": function(){
                // this.topBarTabItemStat.set("text",JSON.stringify(el.getPosition()));
            }.bind(this),
            "onDrag": function(el,e){
                

                
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

                return;
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
                    fx.start(["top"] ,(el.getPosition(this.container).y)+"px", taskItemContainer.getPosition(this.container).y+"px");
                    var fx2 = new Fx.Tween(el,{duration:time});
                    fx2.start(["left"] ,(el.getPosition(this.container).x + this.container.getScrollLeft())+"px", (taskItemContainer.getPosition(this.container).x +  this.container.getScrollLeft())+"px");
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
                                //_self.app.notice("未分类列表不允许移入","error");
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
                                    fx.start(["top"] ,el.getPosition(_self.container).y+"px", pre.getPosition(_self.container).y+"px");
                                    var fx2 = new Fx.Tween(el,{duration:time});
                                    fx2.start(["left"] ,(el.getPosition(_self.container).x + _self.container.getScrollLeft())+"px", (pre.getPosition(_self.container).x +  _self.container.getScrollLeft())+"px");
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
                                    _self.rootActions.TaskListAction.get(_self.groupId,taskGroupFromId,function(json){
                                        taskGroupFrom.getElement(".taskGroupItemTitleCount").set("text","("+json.data.taskCount +")")
                                    });
                                    _self.rootActions.TaskListAction.get(_self.groupId,taskGroupInId,function(json){
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



    createTaskGroupItemLayout:function(node,data){
        var _self = this;
        node.empty();
        var taskGroupItemTitleContainer = new Element("div.taskGroupItemTitleContainer",{styles:this.css.taskGroupItemTitleContainer}).inject(node);
        new Element("div.taskGroupItemTitleText",{styles:this.css.taskGroupItemTitleText,text:data.name}).inject(taskGroupItemTitleContainer);
        var titleCount = new Element("div.taskGroupItemTitleCount",{styles:this.css.taskGroupItemTitleCount,text:"(-)"}).inject(taskGroupItemTitleContainer);
        
        var taskGroupItemTitleReload = new Element("div.taskGroupItemTitleReload",{styles:this.css.taskGroupItemTitleReload, title:this.lp.reload}).inject(taskGroupItemTitleContainer);
        //if(!data.control.sortable) taskGroupItemTitleAdd.setStyle("margin-right","20px");
        taskGroupItemTitleReload.addEvents({
            click:function(){
                this.createTaskGroupItemLayout(node,data);
            }.bind(this),
            mouseover:function(){ this.setStyles({"background-image":"url(../x_component_TeamWork/$Project/default/icon/icon_reload_click.png)"}) },
            mouseout:function(){ this.setStyles({"background-image":"url(../x_component_TeamWork/$Project/default/icon/icon_reload.png)"}) }
        });


        var taskGroupItemContainer = new Element("div.taskGroupItemContainer",{styles:this.css.taskGroupItemContainer}).inject(node);
        var _h = node.getHeight().toInt()-taskGroupItemTitleContainer.getHeight().toInt() - 10 - 10;

        taskGroupItemContainer.setStyles({"height":_h+"px"});
        this.app.setScrollBar(taskGroupItemContainer);
        this.app.setLoading(taskGroupItemContainer);

        //this.actions.taskListByListId(this.data.id,data.id,function(json){
        this.rootActions.TaskAction.listAllTaskWithTaskListId(this.projectId,data.id,function(json){
            titleCount.set("text","("+json.count+")")
            taskGroupItemContainer.empty();
            var taskListData = json.data;
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
        var position = el.getPosition(this.container);
        //alert(JSON.stringify(position))
        //var clone = this.cloneTaskItem = el.clone(true,true).inject($(document.body));
        var clone = this.cloneTaskItem = el.clone(true,true).inject(this.container);
        this.cloneTaskItem.removeClass("dragin");
        this.cloneTaskItem.setStyles({
            "top":position.y+"px",
            "left":(position.x + this.container.getScrollLeft())+"px",
            "z-index":"9999",
            "margin":"0px",
            "position":"absolute",
            "cursor":"move"
        });
        el.setStyles({"border":"1px dotted #000000","opacity":"0.3"});
        var _height = this.cloneTaskItem.getHeight().toInt() - 2;

        this.ccc = 0;
        var drag = new Drag.Move(this.cloneTaskItem, {
            container: this.container,
            "compensateScroll": true,
            "droppables": $$(".dragin"),
            "onStart": function(){
                // this.topBarTabItemStat.set("text",JSON.stringify(el.getPosition()));
            }.bind(this),
            "onDrag": function(el,e){
                

                
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
                    fx.start(["top"] ,(el.getPosition(this.container).y)+"px", taskItemContainer.getPosition(this.container).y+"px");
                    var fx2 = new Fx.Tween(el,{duration:time});
                    fx2.start(["left"] ,(el.getPosition(this.container).x + this.container.getScrollLeft())+"px", (taskItemContainer.getPosition(this.container).x +  this.container.getScrollLeft())+"px");
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
                                //_self.app.notice("未分类列表不允许移入","error");
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
                                    fx.start(["top"] ,el.getPosition(_self.container).y+"px", pre.getPosition(_self.container).y+"px");
                                    var fx2 = new Fx.Tween(el,{duration:time});
                                    fx2.start(["left"] ,(el.getPosition(_self.container).x + _self.container.getScrollLeft())+"px", (pre.getPosition(_self.container).x +  _self.container.getScrollLeft())+"px");
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
                                    _self.rootActions.TaskListAction.get(_self.groupId,taskGroupFromId,function(json){
                                        taskGroupFrom.getElement(".taskGroupItemTitleCount").set("text","("+json.data.taskCount +")")
                                    });
                                    _self.rootActions.TaskListAction.get(_self.groupId,taskGroupInId,function(json){
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
        var color = d.priority ? d.priority.split("||")[1] || "#DEDEDE" : "#DEDEDE";
        //var color = d.priority.split("||")[1] || "#DEDEDE";
        var taskItemHover = new Element("div.taskItemHover",{styles:this.css.taskItemHover}).inject(taskItemContainer);
        taskItemHover.setStyles({"background-color":color});
        // if(d.priority == this.lp.urgency)taskItemHover.setStyle("background-color","#ffaf38");
        // else if(d.priority == this.lp.emergency)taskItemHover.setStyle("background-color","#ff0000");
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
                    //this.reloadTaskCountInfor()
                }
            }.bind(this)
        };
        MWF.xDesktop.requireApp("TeamWork", "Tasks", function(){
            var task = new MWF.xApplication.TeamWork.Tasks(this,data,opt,{projectObj:this});
            task.open();
        }.bind(this));
    }
    
});

