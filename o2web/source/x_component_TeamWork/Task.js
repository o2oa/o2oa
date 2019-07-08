MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xApplication.TeamWork.Task = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "width": 1000,
        "height": "90%",
        "top": null,
        "left": null,
        "bottom" : null,
        "right" : null,
        "minWidth" : 300,
        "minHeight" : 220,

        "isLimitSize": true,
        "ifFade": false,
        "hasTop": false,
        "hasTopIcon" : false,
        "hasTopContent" : false,
        "hasIcon": false,
        "hasBottom": false,
        "hasMask" : true,
        "closeByClickMask" : true,
        "hasScroll" : false,
        "scrollType" : "",

        "title": "",
        "draggable": false,
        "resizeable" : false,
        "maxAction" : false,
        "closeAction": false,

        "relativeToApp" : true,
        "sizeRelateTo" : "app", //desktop
        "resultSeparator" : ","
    },
    initialize: function (explorer, data, options, para) {
        this.setOptions(options);
        this.explorer = explorer;
        if( para ){
            if( this.options.relativeToApp ){
                this.app = para.app || this.explorer.app;
                this.container = para.container || this.app.content;
                this.lp = para.lp || this.explorer.lp || this.app.lp;
                this.css = para.css || this.explorer.css || this.app.css;
                this.actions = para.actions || this.explorer.actions || this.app.actions || this.app.restActions;
            }else{
                this.container = para.container;
                this.lp = para.lp || this.explorer.lp;
                this.css = para.css || this.explorer.css;
                this.actions = para.actions || this.explorer.actions;
            }
        }else{
            if( this.options.relativeToApp ){
                this.app = this.explorer.app;
                this.container = this.app.content;
                this.lp = this.explorer.lp || this.app.lp;
                this.css = this.explorer.css || this.app.css;
                this.actions = this.explorer.actions || this.app.actions || this.app.restActions;
            }else{
                this.container = window.document.body;
                this.lp = this.explorer.lp;
                this.css = this.explorer.css;
                this.actions = this.explorer.actions;
            }
        }


        this.data = data || {};

        this.css = {};
        this.cssPath = "/x_component_TeamWork/$Task/"+this.options.style+"/css.wcss";

        this.load();
        this.lp = this.app.lp.task;
    },

    _createTableContent: function () {
        var _self = this;
        this.getTaskData(function(){
            this.topLayout = new Element("div.topLayout",{styles:this.css.topLayout}).inject(this.formTableArea);
            //this.topImage = new Element("div.topImage",{styles:this.css.topImage}).inject(this.topLayout);
            this.topContent = new Element("div.topContent",{styles:this.css.topContent,text:this.taskData.name}).inject(this.topLayout);
            if(this.isNew){
                this.topContent.set("text",this.lp.newTask)
            }
            this.topIconContainer = new Element("div.topIconContainer",{styles:this.css.topIconContainer}).inject(this.topLayout);

            //更多
            this.topIconMore = new Element("div.topIconMore",{styles:this.css.topIconMore,title:this.lp.more}).inject(this.topIconContainer);
            this.topIconMore.addEvents({
                click:function(){

                }.bind(this),
                mouseover:function(){
                    this.setStyles({"background-image":"url(/x_component_TeamWork/$Task/default/icon/icon_more_click.png)"});
                },
                mouseout:function(){
                    this.setStyles(_self.css.topIconMore)
                }
            });
            //关闭
            this.topIconClose = new Element("div.topIconClose",{styles:this.css.topIconClose,title:this.lp.close}).inject(this.topIconContainer);
            this.topIconClose.addEvents({
                click:function(){
                    this.close()
                }.bind(this),
                mouseover:function(){
                    this.setStyles({"background-image":"url(/x_component_TeamWork/$Task/default/icon/icon_off_click.png)"});
                },
                mouseout:function(){
                    this.setStyles(_self.css.topIconClose)
                }
            });



            this.contentLayout = new Element("div.contentLayout",{styles:this.css.contentLayout}).inject(this.formTableArea);

            this.createContentLayout();
            this.createDetailLayout();

            // this.bottomLayout = new Element("div.bottomLayout",{styles:this.css.bottomLayout}).inject(this.formTableArea);
            // this.okAction = new Element("div.okAction",{styles:this.css.okAction,text:"确定"}).inject(this.bottomLayout);
            // this.okAction.addEvents({
            //     click:function(){
            //         this.saveTaskData();
            //     }.bind(this)
            // })
        }.bind(this));

    },
    createContentLayout:function(){
        var _self = this;
        this.taskInforContainer = new Element("div.taskInforContainer",{styles:this.css.taskInforContainer}).inject(this.contentLayout);
        this.taskActionBar = new Element("div.taskActionBar",{styles:this.css.taskActionBar}).inject(this.taskInforContainer);
        this.taskActionSave = new Element("div.taskActionSave",{styles:this.css.taskActionSave,text:this.lp.save}).inject(this.taskActionBar);
        this.taskActionSave.addEvents({
            click:function(){

            }.bind(this),
            mouseover:function(){
                this.setStyles({
                    "color":"#ffffff",
                    "background-color":"#4A90E2",
                    "border":"1px solid #4A90E2"
                })
            },
            mouseout:function(){
                this.setStyles(_self.css.taskActionSave)
            }
        });

        this.app.setScrollBar(this.taskInforContainer);

        this.taskNameContainer = Element("div.taskNameContainer",{styles:this.css.taskNameContainer}).inject(this.taskInforContainer);


        //状态
        this.taskStatusContainer = Element("div.taskStatusContainer",{styles:this.css.taskStatusContainer}).inject(this.taskInforContainer);
        this.taskStatusIcon = Element("div.taskStatusIcon",{styles:this.css.taskStatusIcon}).inject(this.taskStatusContainer);
        this.taskStatusText = Element("div.taskStatusText",{styles:this.css.taskStatusText,text:this.lp.taskStatus}).inject(this.taskStatusContainer);
        this.taskStatusValue = Element("div.taskStatusValue",{styles:this.css.taskStatusValue}).inject(this.taskStatusContainer);

        //责任人
        this.taskDutyContainer = Element("div.taskDutyContainer",{styles:this.css.taskDutyContainer}).inject(this.taskInforContainer);
        this.taskDutyIcon = Element("div.taskDutyIcon",{styles:this.css.taskDutyIcon}).inject(this.taskDutyContainer);
        this.taskDutyText = Element("div.taskDutyText",{styles:this.css.taskDutyText,text:this.lp.taskDuty}).inject(this.taskDutyContainer);
        this.taskDutyValue = Element("div.taskDutyValue",{styles:this.css.taskDutyValue}).inject(this.taskDutyContainer);

        //时间
        this.taskTimeContainer = Element("div.taskTimeContainer",{styles:this.css.taskTimeContainer}).inject(this.taskInforContainer);
        this.taskTimeIcon = Element("div.taskTimeIcon",{styles:this.css.taskTimeIcon}).inject(this.taskTimeContainer);
        this.taskTimeText = Element("div.taskTimeText",{styles:this.css.taskTimeText,text:this.lp.taskTime}).inject(this.taskTimeContainer);
        this.taskTimeValue = Element("div.taskTimeValue",{styles:this.css.taskTimeValue}).inject(this.taskTimeContainer);

        //备注
        this.taskRemarkContainer = Element("div.taskRemarkContainer",{styles:this.css.taskRemarkContainer}).inject(this.taskInforContainer);
        this.taskRemarkIcon = Element("div.taskRemarkIcon",{styles:this.css.taskRemarkIcon}).inject(this.taskRemarkContainer);
        this.taskRemarkText = Element("div.taskRemarkText",{styles:this.css.taskRemarkText,text:this.lp.taskRemark}).inject(this.taskRemarkContainer);
        this.taskRemarkValue = Element("div.taskRemarkValue",{styles:this.css.taskRemarkValue}).inject(this.taskRemarkContainer);

        //优先级
        this.taskPriorityContainer = Element("div.taskPriorityContainer",{styles:this.css.taskPriorityContainer}).inject(this.taskInforContainer);
        this.taskPriorityIcon = Element("div.taskPriorityIcon",{styles:this.css.taskPriorityIcon}).inject(this.taskPriorityContainer);
        this.taskPriorityText = Element("div.taskPriorityText",{styles:this.css.taskPriorityText,text:this.lp.taskPriority}).inject(this.taskPriorityContainer);
        this.taskPriorityValue = Element("div.taskPriorityValue",{styles:this.css.taskPriorityValue}).inject(this.taskPriorityContainer);

        this.setTaskData();
        //this.setAuth();
    },
    createDetailLayout:function(){
        this.taskDetailLayout = new Element("div.taskDetailLayout",{styles:this.css.taskDetailLayout}).inject(this.contentLayout);
    },
    getTaskData:function(callback){
        if(this.data.taskId){
            this.actions.taskGet(this.data.taskId,function(json){
                if(json.data) {
                    this.taskData = json.data;
                    if(callback)callback()
                }
            }.bind(this))
        }
    },
    setTaskData:function(){
        var _self = this;
        //名称
        if(this.taskNameContainer){
            this.loadNameValue();
        }
        //状态
        if(this.taskStatusValue){
            this.loadStatusValue()
        }
        //负责人
        if(this.taskDutyValue){
            this.loadDutyValue()
        }
        //时间
        if(this.taskTimeContainer){
            this.loadTimeValue()
        }

    },
    saveTaskData:function() {

    },
    loadNameValue:function(){  //名称
        var _self = this;
        this.taskNameContainer.set("text",this.taskData.name);
        if(true){ //权限修改
            var node = this.taskNameContainer;
            var nameEdit = false;
            node.addEvents({
                mouseenter:function(){
                    if(this.getElement("input")) return;
                    var name = this.get("text");
                    node.empty();
                    var input = new Element("input.taskNameInput",{type:"text",value:name,styles:_self.css.taskNameInput}).inject(node);
                    input.addEvents({
                        click:function(ev){
                            nameEdit = true;
                            ev.stopPropagation()
                        },
                        blur:function(){
                            var v = this.get("value");
                            _self.taskNameContainer.empty();
                            _self.taskNameContainer.set("text",v);
                            _self.taskNameContainer.setStyles({"background-color":""});
                            nameEdit = false;
                        }
                    });
                },
                mouseleave:function(){
                    if(!nameEdit){
                        if(this.getElement("input")){
                            var n = this.getElement("input").get("value");
                            this.empty();
                            this.set("text",n);
                        }
                        this.setStyles({"background-color":""})
                    }
                }
            });
        }
    },
    loadStatusValue:function(){ //状态
        var _self = this;
        if(this.taskStatusValue) this.taskStatusValue.empty();
        this.taskStatusValueContainer = new Element("div.taskStatusValueContainer",{styles:this.css.taskStatusValueContainer}).inject(this.taskStatusValue);
        this.taskStatusValueIcon = new Element("div.taskStatusValueIcon",{styles:this.css.taskStatusValueIcon}).inject(this.taskStatusValueContainer);
        if(this.taskData.workStatus == this.lp.status.draft){
            this.taskStatusValueContainer.setStyles({"color":"#666666"});
            this.taskStatusValueIcon.setStyles({"background-image":"url(/x_component_TeamWork/$Task/default/icon/icon_draft.png)"});
        }else if(this.taskData.workStatus == this.lp.status.flow){
            this.taskStatusValueContainer.setStyles({"color":"#666666"});
            this.taskStatusValueIcon.setStyles({"background-image":"url(/x_component_TeamWork/$Task/default/icon/icon_flow.png)"});
        }else if(this.taskData.workStatus == this.lp.status.complete){
            this.taskStatusValueContainer.setStyles({"color":"#69b439"});
            this.taskStatusValueIcon.setStyles({"background-image":"url(/x_component_TeamWork/$Task/default/icon/icon_complete.png)"});
        }
        this.taskStatusValueText = new Element("div.taskStatusValueText",{styles:this.css.taskStatusValueText,text:this.taskData.workStatus}).inject(this.taskStatusValueContainer);

        if(true){ //权限
            this.taskStatusValueContainer.addEvents({
                click:function(){
                    var sc = new MWF.xApplication.TeamWork.Task.StatusCheck(this.container, this.taskStatusValueContainer, this.app, {data:this.taskData}, {
                        css:this.css, lp:this.lp, axis : "y",
                        position : { //node 固定的位置
                            x : "right",
                            y : "middle"
                        },
                        nodeStyles : {
                            "min-width":"200px",
                            "padding":"2px",
                            "border-radius":"5px",
                            "box-shadow":"0px 0px 4px 0px #999999",
                            "z-index" : "201"
                        },
                        onPostLoad:function(){
                            sc.node.setStyles({"opacity":"0","top":(sc.node.getStyle("top").toInt()+4)+"px"});
                            var fx = new Fx.Tween(sc.node,{duration:400});
                            fx.start(["opacity"] ,"0", "1");
                        },
                        onClose:function(rd){
                            if(!rd) return;
                            if(rd.status == "flow") this.taskData.workStatus = this.lp.status.flow;
                            else if(rd.status == "complete") this.taskData.workStatus = this.lp.status.complete;
                            this.loadStatusValue();
                        }.bind(this)
                    });
                    sc.load();
                }.bind(this),
                mouseover:function(){
                    this.setStyles({"background-color":"#efefef"});
                    this.getElement(".taskStatusValueIcon").setStyles({ "background-image":"url(/x_component_TeamWork/$Task/default/icon/icon_down.png)"})
                },
                mouseout:function(){
                    this.setStyles(_self.css.taskStatusValueContainer);
                    var bgurl = "url(/x_component_TeamWork/$Task/default/icon/icon_draft.png)";
                    if(_self.taskData.workStatus == _self.lp.status.flow)bgurl = "url(/x_component_TeamWork/$Task/default/icon/icon_flow.png)";
                    else if(_self.taskData.workStatus == _self.lp.status.complete)bgurl = "url(/x_component_TeamWork/$Task/default/icon/icon_complete.png)";
                    this.getElement(".taskStatusValueIcon").setStyles({"background-image":bgurl})
                }
            });
        }
    },
    loadDutyValue:function(){
        var _self = this;
        if(this.taskDutyValue) this.taskDutyValue.empty();
        this.taskDutyValueContainer = new Element("div.taskDutyValueContainer",{styles:this.css.taskDutyValueContainer}).inject(this.taskDutyValue);
        if(true){//权限
            if(this.taskData.executor==""){
                this.taskDutyAddIcon = new Element("div.taskDutyAddIcon",{styles:this.css.taskDutyAddIcon}).inject(this.taskDutyValueContainer);
                this.taskDutyAddText = new Element("div.taskDutyAddText",{styles:this.css.taskDutyAddText,text:this.lp.addDuty}).inject(this.taskDutyValueContainer);
                this.taskDutyAddText.addEvents({
                    mouseover:function(){this.setStyles({"color":"#4a90e2"})},
                    mouseout:function(){this.setStyles({"color":"#666666"})},
                    click:function(){
                        this.selectPerson(this.taskDutyAddText,"identity",1,
                            function(json){
                                if(json.length>0){
                                    this.taskData.executor = json[0];
                                    this.loadDutyValue()
                                }
                            }.bind(this));
                    }.bind(this)
                })
            }else{
                this.loadTaskPerson(this.taskDutyValueContainer,this.taskData.executor,true);
            }
        }else{
            if(this.taskData.executor!=""){
                this.loadTaskPerson(this.taskDutyValueContainer,this.taskData.executor);
            }
        }

    },
    loadTimeValue:function(){
        var _self = this;


        // this.timee = new Element("input",{styles:{"width":"200px","height":"30px"}}).inject(this.taskTimeContainer);
        // this.timee.addEvents({
        //     click:function(){
        //         var opt = {
        //             type:"datetime",
        //             onClear:function(){}
        //         };
        //         this.app.selectCalendar(this.timee,this.container,opt,function(dateString){
        //             //this.timee.set("value",dateString);
        //         }.bind(this))
        //     }.bind(this)
        // });
        // return;
        //taskTimeContainer
        this.taskStartTime = new Element("div.taskStartTime",{styles:this.css.taskStartTime}).inject(this.taskTimeContainer);
        this.taskTimeLine = new Element("div.taskTimeLine",{styles:this.css.taskTimeLine,text:"-"}).inject(this.taskTimeContainer);
        this.taskEndTime = new Element("div.taskEndTime",{styles:this.css.taskEndTime}).inject(this.taskTimeContainer);
        if(this.taskData.startTime && this.taskData.startTime!=""){
            this.taskStartTime.set("text",this.taskData.startTime);
            this.taskStartTime.setStyles({"color":"#333333"});
        }else{
            this.taskStartTime.set("text",this.lp.taskTimeStart);
        }
        if(this.taskData.endTime && this.taskData.endTime!=""){
            this.taskEndTime.set("text",this.taskData.endTime);
            this.taskEndTime.setStyles({"color":"#333333"});
        }else{
            this.taskEndTime.set("text",this.lp.taskTimeEnd);
        }
        if(true){ //权限
            this.taskStartTime.setStyles({"background-color":"#f5f5f5","cursor":"pointer"});
            this.taskEndTime.setStyles({"background-color":"#f5f5f5","cursor":"pointer"});
            this.taskStartTime.addEvents({
                click:function(){
                    var opt = {
                        type:"datetime"
                    };
                    this.app.selectCalendar(this.taskStartTime,this.container,opt,function(json){
                        if(json.action == "ok"){
                            this.taskStartTime.set("text",json.dateString);
                            this.taskData.startTime = json.dateString
                        }else if(json.action == "clear"){
                            this.taskStartTime.set("text",this.lp.taskTimeStart);
                            this.taskData.startTime = json.dateString
                        }
                    }.bind(this))
                }.bind(this)
            });
            this.taskEndTime.addEvents({
                click:function(){
                    var opt = {
                        type:"datetime"
                    };
                    this.app.selectCalendar(this.taskEndTime,this.container,opt,function(json){
                        if(json.action == "ok"){
                            this.taskEndTime.set("text",json.dateString);
                            this.taskData.endTime = json.dateString
                        }else if(json.action == "clear"){
                            this.taskEndTime.set("text",this.lp.taskTimeEnd);
                            this.taskData.endTime = json.dateString
                        }
                    }.bind(this))
                }.bind(this)
            })
        }
    },

    loadTaskPerson:function(node,identity,flag){
        var _self = this;
        var name = identity.split("@")[0];
        var container = new Element("div",{styles:{"height":"30px","padding":"0 5px","border-radius":"4px"}}).inject(node);
        var circleStyles={
            "width":"24px","height":"24px","border-radius":"20px","float":"left",
            "background-color":"#4A90E2","color":"#ffffff","line-height":"22px",
            "text-align":"center","margin-top":"3px","font-size":"12px"
        };
        var nameStyles={
            "height":"24px","float":"left","margin-left":"2px",
            "color":"#333333","line-height":"22px","margin-top":"3px"
        };
        var closeStyles={
            "width":"16px","height":"16px","float":"left","margin-left":"2px",
            "background":"url(/x_component_TeamWork/$Task/default/icon/icon_off.png) no-repeat center"
        };
        var circleDiv = new Element("div",{styles:circleStyles,text:name.substr(0,1)}).inject(container);
        var nameDiv = new Element("div",{styles:nameStyles,text:name}).inject(container);
        if(flag){
            container.addEvents({
                mouseenter:function(){
                    var closeIcon = new Element("div.closeIcon",{styles:closeStyles}).inject(this);
                    closeIcon.addEvents({
                        click:function(e){
                            _self.taskData.executor = "";
                            _self.loadDutyValue();
                            e.stopPropagation();
                        }
                    });
                    this.setStyles({"background-color":"#efefef","cursor":"pointer"});
                },
                mouseleave:function(){
                    if(this.getElement(".closeIcon"))this.getElement(".closeIcon").destroy();
                    this.setStyles({"background-color":""})
                },
                click:function(e){
                    _self.selectPerson(_self.taskDutyAddText,"identity",1,
                        function(json){
                            if(json.length>0){
                                _self.taskData.executor = json[0];
                                _self.loadDutyValue()
                            }
                        }
                    );
                }
            })
        }
    },
    setAuth:function(){
        var _self = this;
        //名称

    },
    reload : function( keepData ){
        // if( keepData ){
        //     this.data = this.form.getResult(false, this.options.resultSeparator, false, false, true);
        // }
        this.formTopNode = null;
        if(this.setFormNodeSizeFun && this.app && this.app.removeEvent){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        if( this.formAreaNode )this.formAreaNode.destroy();
        if( this.isNew ){
            this.create();
        }else if( this.isEdited ){
            this.edit();
        }else{
            this.open();
        }
    },
    selectPerson: function( item, type,count , callback) {
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        this.fireEvent("querySelect", this);
        var value = [];
        var options = {
            "type": type,
            "title": "选人",
            "count": count,
            "values": value || [],
            "onComplete": function (items) {
                var arr = [];
                items.each(function (item) {
                    arr.push(item.data.distinguishedName);
                }.bind(this));
                if(callback)callback(arr);
            }.bind(this)
        };

        var selector = new MWF.O2Selector(this.app.content, options);
    }


});


MWF.xApplication.TeamWork.Task.StatusCheck = new Class({
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

        var container = {
            "cursor":"pointer",
            "height":"40px",
            "width":"100%"
        };
        var text={
            "height":"30px","line-height":"30px","float":"left",
            "padding-left":"10px","padding-right":"10px","margin-left":"6px","margin-top":"5px",
            "font-size":"13px","color":"#666666","border-radius":"2px"
        };
        var icon = {
            "float":"right","width":"24px","height":"24px",
            "margin-top":"6px","margin-right":"8px",
            "background":"url(/x_component_TeamWork/$Task/default/icon/icon_dagou.png) no-repeat center"
        };
        var flowContainer = new Element("div",{styles:container}).inject(this.contentNode);
        var flowText = new Element("div",{styles:text,text:this.lp.status.flow}).inject(flowContainer);
        flowText.setStyles({"background-color":"#f0f0f0"});
        if(this.data.data.workStatus == this.lp.status.flow){
            var flowIcon = new Element("div",{styles:icon}).inject(flowContainer);
        }

        flowContainer.addEvents({
            click:function(){
                var data = {"status":"flow"};
                this.close(data)
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#f2f5f7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        var completeContainer = new Element("div",{styles:container}).inject(this.contentNode);
        var completeText = new Element("div",{styles:text,text:this.lp.status.complete}).inject(completeContainer);
        completeText.setStyles({"color":"#69b439","background-color":"#f1f9ec"});
        if(this.data.data.workStatus == this.lp.status.complete){
            var completeIcon = new Element("div",{styles:icon}).inject(completeContainer);
        }

        completeContainer.addEvents({
            click:function(){
                var data = {"status":"complete"};
                this.close(data)
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#f2f5f7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        // this.naviViewTipLayout = new Element("div.naviViewTipLayout",{styles:this.css.naviViewTipLayout}).inject(this.contentNode);
        // this.naviViewTipEditContainer = new Element("div.naviViewTipContainer",{styles:this.css.naviViewTipContainer}).inject(this.naviViewTipLayout);
        // this.naviViewTipEditIcon = new Element("div.naviViewTipEditIcon",{styles:this.css.naviViewTipEditIcon}).inject(this.naviViewTipEditContainer);
        // this.naviViewTipEditText = new Element("div.naviViewTipText",{styles:this.css.naviViewTipText,text:this.lp.viewEdit}).inject(this.naviViewTipEditContainer);
        // this.naviViewTipEditContainer.addEvents({
        //     click:function(){
        //
        //     }.bind(this),
        //     mouseover:function(){this.naviViewTipEditContainer.setStyles({"background-color":"#F7F7F7"})}.bind(this),
        //     mouseout:function(){this.naviViewTipEditContainer.setStyles({"background-color":""})}.bind(this)
        // });
        //
        // this.naviViewTipRemoveContainer = new Element("div.naviViewTipContainer",{styles:this.css.naviViewTipContainer}).inject(this.naviViewTipLayout);
        // this.naviViewTipRemoveIcon = new Element("div.naviViewTipRemoveIcon",{styles:this.css.naviViewTipRemoveIcon}).inject(this.naviViewTipRemoveContainer);
        // this.naviViewTipRemoveText = new Element("div.naviViewTipText",{styles:this.css.naviViewTipText,text:this.lp.viewRemove}).inject(this.naviViewTipRemoveContainer);
        // this.naviViewTipRemoveContainer.addEvents({
        //     click:function(e){
        //         _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){
        //
        //
        //         },function(){
        //             this.close();
        //
        //         });
        //     }.bind(this),
        //     mouseover:function(){this.naviViewTipRemoveContainer.setStyles({"background-color":"#F7F7F7"})}.bind(this),
        //     mouseout:function(){this.naviViewTipRemoveContainer.setStyles({"background-color":""})}.bind(this)
        // });


        if(callback)callback();
    }

});