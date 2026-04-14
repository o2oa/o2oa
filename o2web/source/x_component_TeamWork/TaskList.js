/*
    泳道taskList
 */

MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.TaskList = new Class({
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
        this.lp = this.app.lp.task;
        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.ProjectAction;
        
        this.path = "../x_component_TeamWork/$TaskList/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();

        this.projectId = data.projectId;
        this.groupId = data.groupId;
    },
    load: function () {
        
        //泳道--进行中
        this.laneProcessLayout = new Element("div.taskGroupLayout",{styles:this.css.taskGroupLayout}).inject(this.container);
        this.loadLane("processing");
                
        //泳道--已搁置
        this.laneDelayLayout = new Element("div.taskGroupLayout",{styles:this.css.taskGroupLayout}).inject(this.container);
        this.loadLane("delay");
        
        //泳道--已取消
        this.laneCanceledLayout = new Element("div.taskGroupLayout",{styles:this.css.taskGroupLayout}).inject(this.container);
        this.loadLane("canceled");
        
        //泳道--已完成
        this.laneCompletedLayout = new Element("div.taskGroupLayout",{styles:this.css.taskGroupLayout}).inject(this.container);
        this.loadLane("completed");
        		        
    },
    reload:function(){
        this.container.empty();
        this.load();
    },
    loadLane:function(status){
        var node = this.laneProcessLayout;
        if(status == "delay"){
            node = this.laneDelayLayout;
        }else if(status == "canceled"){
            node = this.laneCanceledLayout;
        }else if(status == "completed"){
            node = this.laneCompletedLayout;
        }

        node.empty();

        var taskGroupItemTitleContainer = new Element("div.taskGroupItemTitleContainer",{styles:this.css.taskGroupItemTitleContainer}).inject(node);
        node.set("status",status)
        new Element("div.taskGroupItemTitleText",{styles:this.css.taskGroupItemTitleText,text:this.lp.status[status]}).inject(taskGroupItemTitleContainer);
        var titleCount = new Element("div.taskGroupItemTitleCount",{styles:this.css.taskGroupItemTitleCount,text:"(-)"}).inject(taskGroupItemTitleContainer);
        
        var taskGroupItemTitleReload = new Element("div.taskGroupItemTitleReload",{styles:this.css.taskGroupItemTitleReload, title:this.lp.reload}).inject(taskGroupItemTitleContainer);
        taskGroupItemTitleReload.addEvents({
            click:function(){
                //this.createTaskGroupItemLayout(node,data);
                this.loadLane(status)
            }.bind(this),
            mouseover:function(){ this.setStyles({"background-image":"url(../x_component_TeamWork/$Project/default/icon/icon_reload_click.png)"}) },
            mouseout:function(){ this.setStyles({"background-image":"url(../x_component_TeamWork/$Project/default/icon/icon_reload.png)"}) }
        });

        var taskGroupItemContainer = new Element("div.taskGroupItemContainer",{styles:this.css.taskGroupItemContainer}).inject(node);
        var _h = node.getHeight().toInt()-taskGroupItemTitleContainer.getHeight().toInt() - 10 - 10;

        taskGroupItemContainer.setStyles({"height":_h+"px"});
        //taskGroupItemContainer.set("status",status);
        this.app.setScrollBar(taskGroupItemContainer);
        //this.app.setLoading(taskGroupItemContainer);

        this.rootActions.TaskAction.listPageWithFilter(1,200,{"project":this.projectId,"workStatus":status},function(json){
            titleCount.set("text","("+json.count+")")
            taskGroupItemContainer.empty();
            var taskListData = json.data;
            taskListData.each(function(d){
                //var taskItemContainerDrag = new Element("div.taskItemContainerDrag.dragin",{styles:this.css.taskItemContainerDrag}).inject(taskGroupItemContainer);
                var taskItemContainer = new Element("div.taskItemContainer",{styles:this.css.taskItemContainer}).inject(taskGroupItemContainer);
                taskItemContainer.set("id",d.id);
                taskItemContainer.set("status",status);
                this.loadTaskNode(taskItemContainer,d);

                var int;
                var upTime = 0;
                var time = 200;
                taskItemContainer.addEvents({
                    click:function(e){
                        this.openTask(d.id,function(){ taskItemContainer.destroy() }.bind(this));
                    }.bind(this),
                    mouseenter:function(){
                        this.setStyles({"box-shadow":"#999999 0px 1px 3px 0px"});
                        
                    },
                    mouseleave:function(){
                        this.setStyles({"box-shadow":"#999999 0px 0px 0px 0px"});
                        
                    },

                });
                
                if(d.control.edit){
                    //如果有编辑权限，可以移动
                    taskItemContainer.setStyles({"cursor":"move"});
                    var drag = new Drag(taskItemContainer, {
                        "compensateScroll": true,
                        "onStart": function(el, e){
                            this.dragMove(el,e);
                            drag.stop();
                        }.bind(this)
                    });
                }
                

            }.bind(this));

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
            "droppables": $$(".taskGroupLayout"),
            "onStart": function(){
                // this.topBarTabItemStat.set("text",JSON.stringify(el.getPosition()));
            }.bind(this),
            "onDrag": function(el,e){
                

                
            }.bind(this),
            "onEnter": function(el,dr){
                if(el.get("status") != dr.get("status")){
                    dr.setStyles({"border":"1px solid #4a90e2"});
                }       
            }.bind(this),
            "onLeave": function(el,dr){
                if(el.get("status") != dr.get("status")){
                    dr.setStyles({"border":"1px solid #ffffff"});
                }
                
            }.bind(this),
            "onDrop": function(el, dr, e){
                
                var taskId = el.get("id");
                el.setStyles({"cursor":""});
                
                if(!dr || el.get("status") == dr.get("status")){ 
                    //没有移入，还原
                    var fx = new Fx.Tween(el,{duration:time});
                    fx.start(["top"] ,(el.getPosition(this.container).y)+"px", taskItemContainer.getPosition(this.container).y+"px");
                    var fx2 = new Fx.Tween(el,{duration:time});
                    fx2.start(["left"] ,(el.getPosition(this.container).x + this.container.getScrollLeft())+"px", (taskItemContainer.getPosition(this.container).x +  this.container.getScrollLeft())+"px");
                    window.setTimeout(function(){
                        taskItemContainer.setStyles({"border":"1px solid #e6e6e6","opacity":"1"});
                        el.destroy()
                        dr.setStyles({"border":"1px solid #ffffff"});
                    },time)
                }else{
                    var inStatus = dr.get("status"); //移入的状态
                    var outStatus = el.get("status"); //移出的状态

                    var data = {
                        taskId:taskId,
                        property:"workStatus",
                        mainValue:inStatus,
                        secondaryValue:""
                    };

                    _self.updateSingleProperty(data,function(json){ 
                        if(json.type == "success"){
                            //修改成功后
                            //1、把克隆的dom移动到泳道的最下面位置,计算出这个位置
                            //2、移动结束后删除克隆的dom
                            //把原来的dom显示到泳道的最下面，修改status 属性


                            //计算出目的对象的位置
                            
                            var groupContainer = dr.getElement('.taskGroupItemContainer');
                            var _pos_group = groupContainer.getPosition(_self.container); 
                            //left 容器的left  290容器的宽度，260 item的宽度 因为是margin auto 左边距除以2
                            var _left = _pos_group.x + (290 - 260)/2 ;
                            //top计算规则,计算已经有几个item 计算出总高度，
                            var _itemList = groupContainer.getElements(".taskItemContainer");
                            var _top = 0;
                            for(var i=0;i<_itemList.length;i++){
                                _top = _top + _itemList[i].getHeight() + 10   ;  
                            }
                            
                            _top = _top + _pos_group.y + 10; //10为margin
                            
                            var fx = new Fx.Tween(el,{duration:time});
                            fx.start(["top"] ,el.getPosition(_self.container).y+"px", _top + "px");
                            var fx1 = new Fx.Tween(el,{duration:time});
                            fx1.start(["left"] ,el.getPosition(_self.container).x+"px", _left + "px");

                            //把原来的对象taskItemContainer先隐藏，等动画结束后插入到移动的泳道
                            window.setTimeout(function(){
                                
                                taskItemContainer.setStyles({
                                    "border":"1px solid #e6e6e6",
                                    "opacity":1
                                })

                                //刷新泳道里的任务数量
                                //也可以直接+1，减1
                                //移出的泳道
                                var countContain = taskItemContainer.getParent().getParent().getElement('.taskGroupItemTitleCount');
                                var count = countContain.get('text').replace('(','').replace(')','');
                                countContain.set('text','('+(parseInt(count) - 1) +')');
                                //移入的泳道
                                var _countContain = dr.getElement('.taskGroupItemTitleCount');
                                var _count = _countContain.get('text').replace('(','').replace(')','');
                                _countContain.set('text','('+(parseInt(_count) + 1) +')');
                                

                                taskItemContainer.set("status",inStatus);
                                taskItemContainer.inject(groupContainer);
                                el.destroy();

                                //刷新逻辑
                                //1、如果移入的是已取消
                                //1.1、如果当前任务有下级任务，需要刷新所有泳道
                                
                                if(inStatus == 'canceled' || inStatus == "completed"){
                                    _self.rootActions.TaskAction.listSubTaskWithTaskId(taskId,function(json){
                                        if(json.data.length>0){
                                            _self.reload();
                                        }
                                    })
                                }
                                
                            },time)
                            

                        }
                        if(json.type == "error"){
                            //后台报错
                            _self.app.notice(json.message,"error");
                            
                            //还原移动的
                            var fx = new Fx.Tween(el,{duration:time});
                            fx.start(["top"] ,(el.getPosition(this.container).y)+"px", taskItemContainer.getPosition(this.container).y+"px");
                            var fx2 = new Fx.Tween(el,{duration:time});
                            fx2.start(["left"] ,(el.getPosition(this.container).x + this.container.getScrollLeft())+"px", (taskItemContainer.getPosition(this.container).x +  this.container.getScrollLeft())+"px");
                            window.setTimeout(function(){
                                taskItemContainer.setStyles({"border":"1px solid #e6e6e6","opacity":"1"});
                                el.destroy()
                                dr.setStyles({"border":"1px solid #ffffff"});
                            },time)

                        }
                    }.bind(this))

                    dr.setStyles({"border":"1px solid #ffffff"});
                    
                }
            }.bind(this),
            // "onCancel": function(el, e){ this._drag_cancel(dragging); }.bind(this),
            //"onComplete": function(el, e){ if(scrInt) window.clearInterval(scrInt);}.bind(this),
        });
        drag.start(e);
    },
    loadTaskNode:function(taskItemContainer,d){
        taskItemContainer.empty();

        

        var taskItemColor = new Element("div.taskItemColor",{styles:this.css.taskItemColor}).inject(taskItemContainer);
        var taskItemRight = new Element("div.taskItemRight",{styles:this.css.taskItemRight}).inject(taskItemContainer)

        var _name = d.name;
        _name = _name.length>28 ? _name.substring(0,28) + '...' : _name;

        new Element("div.taskItemTitle",{styles:this.css.taskItemTitle,text:_name}).inject(taskItemRight);
        var taskItemTime = new Element("div.taskItemTime",{styles:this.css.taskItemTime}).inject(taskItemRight);
        new Element("div",{styles:this.css.taskItemTimeIcon}).inject(taskItemTime);
        new Element("div",{styles:this.css.taskItemTimeText,text:d.endTime}).inject(taskItemTime);

        var taskItemMore = new Element("div.taskItemMore",{styles:this.css.taskItemMore}).inject(taskItemRight);
        var taskItemMoreLeft = new Element("div.taskItemMoreLeft",{styles:this.css.taskItemMoreLeft}).inject(taskItemMore);
        //附件数量
        var taskItemMoreAttachment = new Element("div.taskItemMoreAttachment",{styles:this.css.taskItemMoreAttachment}).inject(taskItemMoreLeft);
        new Element("div.taskItemMoreAttachmentIcon",{styles:this.css.taskItemMoreAttachmentIcon}).inject(taskItemMoreAttachment);
        new Element("div.taskItemMoreAttachmentText",{styles:this.css.taskItemMoreAttachmentText,text:d.attCount||0}).inject(taskItemMoreAttachment);
        //子任务数量
        var taskItemMoreSub = new Element("div.taskItemMoreSub",{styles:this.css.taskItemMoreSub}).inject(taskItemMoreLeft);
        new Element("div.taskItemMoreSubIcon",{styles:this.css.taskItemMoreSubIcon}).inject(taskItemMoreSub);
        new Element("div.taskItemMoreSubText",{styles:this.css.taskItemMoreAttachmentText,text:d.subTaskCount||0}).inject(taskItemMoreSub);
        //负责人
        var taskItemExecutor = new Element("div.taskItemExecutor",{styles:this.css.taskItemExecutor}).inject(taskItemMore);
        var executor = d.executor;
        taskItemExecutor.set("text",executor.split("@")[0]);

        //设置对应四象限的颜色值
        var quadrantColor = ['#BD444E','#CBB840','#30A1CC','#7EAD47'];
        var _high = this.lp.importantStatus.high;
        var _low = this.lp.importantStatus.low;
        var _important = d.important || _high;
        var _urgency = d.urgency || _low;
        var _color=''
        if(_important == _high && _urgency == _high){
            _color = quadrantColor[0]
        }else if(_important == _high && _urgency == _low){
            _color = quadrantColor[1]
        }else if(_important == _low && _urgency == _high){
            _color = quadrantColor[2]
        }else if(_important == _low && _urgency == _low){
            _color = quadrantColor[3]
        }
        taskItemColor.setStyles({"background-color":_color})


    },
    updateSingleProperty:function(data,callback){
        
        this.rootActions.TaskAction.updateSingleProperty(data.taskId,data,function(json){
            
            if(callback)callback(json)
        }.bind(this),function(xhr,text,error){ 
            var errorMessage = xhr.responseText;
            var json = JSON.parse(errorMessage);
            if(callback)callback(json)
        })
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

