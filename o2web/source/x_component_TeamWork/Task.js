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
        "closeByClickMask" : false,
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
    close: function (data) {
        //愤愤愤愤
        this.fireEvent("queryClose");
        this._close();
        //if( this.form ){
        //    this.form.destroy();
        //}
        if(this.setFormNodeSizeFun && this.app && this.app.removeEvent ){
            this.app.removeEvent("resize",this.setFormNodeSizeFun);
        }
        if( this.formMaskNode )this.formMaskNode.destroy();
        if( this.formAreaNode )this.formAreaNode.destroy();
        this.fireEvent("postClose",[data]);
        delete this;
    },
    openTask:function(data){
        //this.data.taskId = data.id;
        //alert("id="+this.data.taskId)


        // this.formAreaNode.setStyles({"opacity":"1"});
        // var fx = new Fx.Tween(this.formAreaNode,{duration:400});
        // fx.start(["opacity"] ,"1", "0");

        var ef = new Fx.Morph(this.formAreaNode, {
            duration: 200,
            transition: Fx.Transitions.Sine.easeOut,
            onStart:function(){},
            onComplete:function(){
                this._createTableContent();
            }.bind(this)
        });

        ef.start({
            'opacity': [1, 0]
        });



        //this.formAreaNode.setStyles({"opacity":0});
        //this._createTableContent();
    },
    _createTableContent: function () {
        var _self = this;
        this.getTaskData(function(){
            this.formTableArea.empty();
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
                    var tm = new MWF.xApplication.TeamWork.Task.TaskMore(this.container, this.topIconMore, this.app, {data:this.taskData}, {
                        css:this.css, lp:this.lp, axis : "y",
                        position : { //node 固定的位置
                            x : "auto",
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
                            tm.node.setStyles({"opacity":"0","top":(tm.node.getStyle("top").toInt()+4)+"px"});
                            var fx = new Fx.Tween(tm.node,{duration:400});
                            fx.start(["opacity"] ,"0", "1");
                        },
                        onClose:function(rd){
                            if(!rd) return;
                            if(rd.act == "remove"){
                                this.close(rd);
                                if(this.data.projectObj){ //reload project
                                    this.data.projectObj.createTaskGroup()
                                }
                            }
                        }.bind(this)
                    },null,this);
                    tm.load();
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

            this.formAreaNode.setStyles({"opacity":1})
        }.bind(this));

    },
    createContentLayout:function(){
        var _self = this;
        this.taskInforContainer = new Element("div.taskInforContainer",{styles:this.css.taskInforContainer}).inject(this.contentLayout);

        this.taskActionBar = new Element("div.taskActionBar",{styles:this.css.taskActionBar}).inject(this.taskInforContainer);
        if(this.taskData.parent!="0"){
            this.taskActionParentContent = new Element("div.taskActionParentContent").inject(this.taskActionBar);
            this.taskActionParentIcon = new Element("div.taskActionParentIcon",{styles:this.css.taskActionParentIcon}).inject(this.taskActionParentContent);
            this.taskActionParentText = new Element("div.taskActionParentText",{styles:this.css.taskActionParentText,text:this.lp.taskBelongText+":"}).inject(this.taskActionParentContent);
            this.taskActionParentValue = new Element("div.taskActionParentValue",{styles:this.css.taskActionParentValue}).inject(this.taskActionParentContent);
            this.actions.taskGet(this.taskData.parent,function(json){
                this.taskActionParentValue.set("text",json.data.name);
                this.taskActionParentValue.addEvents({
                    click:function(){
                        this.data.taskId = json.data.id;
                        this.openTask()
                    }.bind(this)
                });
            }.bind(this))
        }
        // this.taskActionSave = new Element("div.taskActionSave",{styles:this.css.taskActionSave,text:this.lp.save}).inject(this.taskActionBar);
        // this.taskActionSave.addEvents({
        //     click:function(){
        //         this.saveTaskData();
        //     }.bind(this),
        //     mouseover:function(){
        //         this.setStyles({
        //             "color":"#ffffff",
        //             "background-color":"#4A90E2",
        //             "border":"1px solid #4A90E2"
        //         })
        //     },
        //     mouseout:function(){
        //         this.setStyles(_self.css.taskActionSave)
        //     }
        // });

        this.taskInforLayout = new Element("div.taskInforLayout",{styles:this.css.taskInforLayout}).inject(this.taskInforContainer);
        this.taskInforContent = new Element("div.taskInforContent",{styles:this.css.taskInforContent}).inject(this.taskInforLayout);
        this.app.setScrollBar(this.taskInforContent);

        //名称
        this.taskNameContainer = Element("div.taskNameContainer",{styles:this.css.taskNameContainer}).inject(this.taskInforContent);

        //状态
        this.taskStatusContainer = Element("div.taskStatusContainer",{styles:this.css.taskStatusContainer}).inject(this.taskInforContent);
        this.taskStatusIcon = Element("div.taskStatusIcon",{styles:this.css.taskStatusIcon}).inject(this.taskStatusContainer);
        this.taskStatusText = Element("div.taskStatusText",{styles:this.css.taskStatusText,text:this.lp.taskStatus}).inject(this.taskStatusContainer);
        this.taskStatusValue = Element("div.taskStatusValue",{styles:this.css.taskStatusValue}).inject(this.taskStatusContainer);

        //责任人
        this.taskDutyContainer = Element("div.taskDutyContainer",{styles:this.css.taskDutyContainer}).inject(this.taskInforContent);
        this.taskDutyIcon = Element("div.taskDutyIcon",{styles:this.css.taskDutyIcon}).inject(this.taskDutyContainer);
        this.taskDutyText = Element("div.taskDutyText",{styles:this.css.taskDutyText,text:this.lp.taskDuty}).inject(this.taskDutyContainer);
        this.taskDutyValue = Element("div.taskDutyValue",{styles:this.css.taskDutyValue}).inject(this.taskDutyContainer);

        //时间
        this.taskTimeContainer = Element("div.taskTimeContainer",{styles:this.css.taskTimeContainer}).inject(this.taskInforContent);
        this.taskTimeIcon = Element("div.taskTimeIcon",{styles:this.css.taskTimeIcon}).inject(this.taskTimeContainer);
        this.taskTimeText = Element("div.taskTimeText",{styles:this.css.taskTimeText,text:this.lp.taskTime}).inject(this.taskTimeContainer);
        this.taskTimeValue = Element("div.taskTimeValue",{styles:this.css.taskTimeValue}).inject(this.taskTimeContainer);

        //备注
        this.taskRemarkContainer = Element("div.taskRemarkContainer",{styles:this.css.taskRemarkContainer}).inject(this.taskInforContent);
        this.taskRemarkIcon = Element("div.taskRemarkIcon",{styles:this.css.taskRemarkIcon}).inject(this.taskRemarkContainer);
        this.taskRemarkText = Element("div.taskRemarkText",{styles:this.css.taskRemarkText,text:this.lp.taskRemark}).inject(this.taskRemarkContainer);
        this.taskRemarkValue = Element("div.taskRemarkValue",{styles:this.css.taskRemarkValue}).inject(this.taskRemarkContainer);

        //优先级
        this.taskPriorityContainer = Element("div.taskPriorityContainer",{styles:this.css.taskPriorityContainer}).inject(this.taskInforContent);
        this.taskPriorityIcon = Element("div.taskPriorityIcon",{styles:this.css.taskPriorityIcon}).inject(this.taskPriorityContainer);
        this.taskPriorityText = Element("div.taskPriorityText",{styles:this.css.taskPriorityText,text:this.lp.taskPriority}).inject(this.taskPriorityContainer);
        this.taskPriorityValue = Element("div.taskPriorityValue",{styles:this.css.taskPriorityValue}).inject(this.taskPriorityContainer);

        //扩展字段
        if(this.taskData.extFieldConfigs && this.taskData.extFieldConfigs.length>0){
            this.taskExtFieldContainer = new Element("div.taskExtFieldContainer",{styles:this.css.taskExtFieldContainer}).inject(this.taskInforContent);
            this.loadTaskExtField();
        }

        //标签
        this.taskTagContainer = Element("div.taskTagContainer",{styles:this.css.taskTagContainer}).inject(this.taskInforContent);
        this.taskTagIcon = Element("div.taskTagIcon",{styles:this.css.taskTagIcon}).inject(this.taskTagContainer);
        this.taskTagText = Element("div.taskTagText",{styles:this.css.taskTagText,text:this.lp.taskTag}).inject(this.taskTagContainer);
        this.taskTagValue = Element("div.taskTagValue",{styles:this.css.taskTagValue}).inject(this.taskTagContainer);





        //附件 -- 换成其他样式
        // this.taskAttachmentContainer = Element("div.taskAttachmentContainer",{styles:this.css.taskAttachmentContainer}).inject(this.taskInforContent);
        // this.taskAttachmentIcon = Element("div.taskAttachmentIcon",{styles:this.css.taskAttachmentIcon}).inject(this.taskAttachmentContainer);
        // this.taskAttachmentText = Element("div.taskAttachmentText",{styles:this.css.taskAttachmentText,text:this.lp.taskAttachment}).inject(this.taskAttachmentContainer);
        // this.taskAttachmentValue = Element("div.taskAtchatBarEmojitachmentValue",{styles:this.css.taskAttachmentValue}).inject(this.taskAttachmentContainer);

        //附件
        this.taskAttachmentListContainer = new Element("div.taskAttachmentListContainer",{styles:this.css.taskAttachmentListContainer}).inject(this.taskInforContent);
        this.loadTaskAttachmentListContainer();

        this.setTaskData();
        //this.setAuth();

        //子任务
        this.subTaskContainer = new Element("div.subTaskContainer",{styles:this.css.subTaskContainer}).inject(this.taskInforContent);
        this.loadSubTaskContainer();

        //占位
        new Element("div",{styles:{"width":"100%","height":"100px"}}).inject(this.taskInforContent)
    },
    loadTaskExtField:function(){
        this.taskData.extFieldConfigs.each(function(data){
            this.loadTaskExtFieldItem(data);
        }.bind(this))
    },
    loadTaskExtFieldItem:function(data){
        var taskExtFieldItem = new Element("div.taskExtFieldItem",{styles:this.css.taskExtFieldItem}).inject(this.taskExtFieldContainer);
        var taskExtFieldIcon = new Element("div.taskExtFieldIcon",{styles:this.css.taskExtFieldIcon}).inject(taskExtFieldItem);
        var taskExtFieldText = new Element("div.taskExtFieldText",{styles:this.css.taskExtFieldText}).inject(taskExtFieldItem);
        new Element("p",{styles:{
                "justify-content": "center",
                "align-items":"center"
            },text:data.displayName}).inject(taskExtFieldText);
        if(data.displayType.toUpperCase() == "TEXT"){

        }
    },
    createDetailLayout:function(){
        this.taskDetailLayout = new Element("div.taskDetailLayout",{styles:this.css.taskDetailLayout}).inject(this.contentLayout);
        this.taskParticipateContainer = new Element("div.taskParticipateContainer",{styles:this.css.taskParticipateContainer}).inject(this.taskDetailLayout);
        this.createParticipateContainer();
        this.taskDynamicContainer = new Element("div.taskDynamicContainer",{styles:this.css.taskDynamicContainer}).inject(this.taskDetailLayout);
        this.createDynamicContainer();
        this.taskChatContainer = new Element("div.taskChatContainer",{styles:this.css.taskChatContainer}).inject(this.taskDetailLayout);
        this.createChatContainer();

    },
    createParticipateContainer:function(){
        this.taskParticipateContainer.empty();
        this.participateTitle = new Element("div.participateTitle",{styles:this.css.participateTitle}).inject(this.taskParticipateContainer);
        this.participateTitleText = new Element("div.participateTitleText",{styles:this.css.participateTitleText}).inject(this.participateTitle);
        this.participateTitleIcon = new Element("div.participateTitleIcon",{styles:this.css.participateTitleIcon,title:this.lp.taskReaderAdd}).inject(this.participateTitle);
        this.participateTitleIcon.addEvents({
            click:function(){
                this.selectPerson(this.participateTitleIcon,null,["identity","unit"],0,
                    function(json){
                        if(json.length>0){
                            this.taskData.participantList = this.taskData.participantList.concat(json);
                            this.actions.updateParticipantList(this.taskData.id,{participantList:this.taskData.participantList},function(json){
                                if(json.data.dynamics){
                                    json.data.dynamics.each(function(dd){
                                        this.loadDynamicItem(dd,"bottom")
                                    }.bind(this))
                                }
                                this.dynamicContent.scrollTo(0,this.dynamicContent.getScrollSize().y);

                                this.createParticipateContainer();
                            }.bind(this))
                        }
                    }.bind(this)
                );
            }.bind(this)
        });

        this.participateValue = new Element("div.participateValue",{styles:this.css.participateValue}).inject(this.taskParticipateContainer);
        this.setScrollBar(this.participateValue);

        if(this.taskData.participantList){
            count = this.taskData.participantList.length;
            this.participateTitleText.set("text",this.lp.taskReader + " . " + count);
            this.taskData.participantList.each(function(d){
                var flag = true;
                if(this.taskData.executor){
                    if(d === this.taskData.executor) flag = false
                }
                this.loadparticipantPerson(this.participateValue,d,flag);
            }.bind(this));
        }
    },
    createDynamicContainer:function(){
        var _self = this;
        this.taskDynamicContainer.empty();
        this.dynamicBar = new Element("div.dynamicBar",{styles:this.css.dynamicBar}).inject(this.taskDynamicContainer);
        this.dynamicBar.addEvents({
            mouseover:function(){
                this.dynamicText.setStyle("color","#4a90e2");
                this.dynamicIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/icon_dw_click.png)")
            }.bind(this),
            mouseout:function(){
                this.dynamicText.setStyle("color","#666666");
                this.dynamicIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/icon_dw.png)")
            }.bind(this),
            click:function(){
                var pc = new MWF.xApplication.TeamWork.Task.DynamicType(this.container, this.dynamicBar, this.app, {data:this.taskData}, {
                    css:this.css, lp:this.lp, axis : "y",
                    position : { //node 固定的位置
                        x : "right",
                        y : "middle"
                    },
                    nodeStyles : {
                        "min-width":"150px",
                        "padding":"2px",
                        "border-radius":"5px",
                        "box-shadow":"0px 0px 4px 0px #999999",
                        "z-index" : "201"
                    },
                    onPostLoad:function(){
                        pc.node.setStyles({"opacity":"0","top":(pc.node.getStyle("top").toInt()-6)+"px","left":(pc.node.getStyle("left").toInt()+10)+"px"});
                        var fx = new Fx.Tween(pc.node,{duration:400});
                        fx.start(["opacity"] ,"0", "1");
                    },
                    onClose:function(rd){
                        if(!rd) return;
                        if(rd.value == "all"){
                            filter = {};
                            this.dynamicText.set("text",this.lp.dynamicAll)
                        }else if(rd.value == "chat"){
                            filter = {objectType:"CHAT"};
                            this.dynamicText.set("text",this.lp.dynamicChat)
                        }else if(rd.value == "attachment"){
                            this.dynamicText.set("text",this.lp.dynamicAttachment)
                            filter = {objectType:"ATTACHMENT"};
                        }
                        this.dynamicContent.empty();
                        this.dyncurCount = 0;
                        this.createDynamicContent(null,filter,function(){
                            this.dynamicContent.scrollTo(0,this.dynamicContent.getScrollSize().y);
                            this.getDynamicStatus = false;
                        }.bind(this))
                    }.bind(this)
                });
                pc.load();
            }.bind(this),
        });
        this.dynamicText = new Element("div.dynamicText",{styles:this.css.dynamicText,text:this.lp.dynamicAll}).inject(this.dynamicBar);
        this.dynamicIcon = new Element("div.dynamicIcon",{styles:this.css.dynamicIcon}).inject(this.dynamicBar);
        this.dynamicContent = new Element("div.dynamicContent",{styles:this.css.dynamicContent}).inject(this.taskDynamicContainer);
        var filter = {};
        var _y = this.dynamicContent.getScrollSize().y;
        this.dynamicContent.addEvents({
            scroll:function(){
                if(this.getScrollTop()==0){
                    //alert("top")
                    //加载下一页
                    if(_self.getDynamicStatus) return;
                    if(_self.dyncurCount>=_self.dyntotal) return;

                    _self.createDynamicContent(_self.curDynamicId,filter,function(){
                        _self.dynamicContent.scrollTo(0,_y);
                        _self.getDynamicStatus = false;
                    });
                }
            }
        });

        this.createDynamicContent(null,filter,function(){
            this.dynamicContent.scrollTo(0,this.dynamicContent.getScrollSize().y);
            this.getDynamicStatus = false;
        }.bind(this));

    },
    createDynamicContent:function(id,data,callback){
        this.getDynamicStatus = true;
        var _self = this, id = id || "(0)", count = 15, taskId = this.taskData.id;
        this.curDynamicId = id;

        //this.dyntotal = 0;
        this.dyncurCount = this.dyncurCount || 0;
        this.tmpdynamicLoading = new Element("div").inject(this.dynamicContent,"top");
        this.app.setLoading(this.tmpdynamicLoading);
        this.actions.taskDynamicListNext(id,count,taskId,data||{},function(json){
            if(this.tmpdynamicLoading)this.tmpdynamicLoading.destroy();
            //this.getDynamicStatus = false;
            if(json.type == "success"){
                this.dyntotal = json.count;
                json.data.each(function(d,i){
                    this.curDynamicId = d.id;
                    this.loadDynamicItem(d);
                }.bind(this))
                if(callback)callback(json)
            }
        }.bind(this))
    },
    loadDynamicItem:function(data,where){
        this.dyncurCount = this.dyncurCount + 1;
        var dynamicItem = new Element("div.dynamicItem",{styles:this.css.dynamicItem}).inject(this.dynamicContent,where||"top");
        var dynamicItemIcon = new Element("div.dynamicItemIcon",{styles:this.css.dynamicItemIcon}).inject(dynamicItem);
        var optType = data.optType.toUpperCase();
        var objectType = data.objectType.toUpperCase();
        if(objectType == "CHAT"){
            //if(optType.toUpperCase() == "PUBLISH"){
            dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_chat.png)")
            //}
        }else if(objectType == "ATTACHMENT"){
            if(optType == "DELETE"){  //UPLOAD、DOWNLOAD、DELETE
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_remove.png)")
            }else{
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_attachment.png)")
            }
        }else if(objectType == "TASK"){
            //UPDATE_NAME、UPDATE_EXECUTOR、UPDATE_STATUS、UPDATE_TIME、UPDATE_PROGRESS、ADD_TAGS、REMOVE_TAGS、
            //ADD_MANAGER、REMOVE_MANAGER、ADD_PARTICIPANTS、REMOVE_PARTICIPANTS、CREATE、DELETE
            if(optType=="SPLIT"){
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_subtask.png)")
            }else if(optType=="DELETE_SUBTASK"){
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_remove.png)")
            }else if(optType=="UPDATE_EXECUTOR"){
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_duty.png)")
            }else if(optType=="UPDATE_NAME"){
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_title.png)")
            }else if(optType=="UPDATE_WORKSTATUS"){
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_status.png)")
            }else if(optType=="UPDATE_WORKDATE"){
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_time.png)")
            }else if(optType=="UPDATE_DESCRIPTION"){
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_remark.png)")
            }else if(optType=="ADD_PARTICIPANTS" || optType == "REMOVE_PARTICIPANTS"){
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_participant.png)")
            }else if(optType=="CREATE"){
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/icon_jia.png)")
            }else{
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/icon_edit.png)")
            }

        }else if(objectType=="TASK_TAG"){
            if(optType=="REMOVE"){
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_remove.png)")
            }else{
                dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/dy_tag.png)")
            }
        }

        var dynamicItemText = new Element("div.dynamicItemText",{styles:this.css.dynamicItemText}).inject(dynamicItem);
        if(objectType == "CHAT"){
            new Element("div.dynamicItemUser",{styles:this.css.dynamicItemUser,text:data.operator.split("@")[0]}).inject(dynamicItemText);
            var chattext = data.description.split("\n").join("<br/>");
            //转换表情
            for(var item in this.app.lp.emoji){
                var val = this.app.lp.emoji[item]; //alert(val)
                chattext = chattext.split("["+val+"]").join('<img style="margin:0px 2px;" src="/x_component_TeamWork/$Emoji/default/icon/'+item+'.png" />');
            }
            new Element("div.dynamicItemUserChat",{styles:{"margin-top":"5px"},html:chattext}).inject(dynamicItemText);
        }else{
            dynamicItemText.set("text",data.description);
        }
        var dynamicItemTime = new Element("div.dynamicItemTime",{styles:this.css.dynamicItemTime}).inject(dynamicItem);
        var ct = Date.parse(data.createTime);
        var now = new Date();
        var sep = now.getTime()-ct.getTime();
        sep = sep/1000; //毫秒
        //一分钟内，刚刚，一小时内，多少分钟前，2小时内，显示一小时前，2小时到今天00：00：00 显示 今天几点，本周内，显示本周几，几点几分，其他显示几月几日
        var cttext = "";
        if(sep<60){
            cttext = "刚刚"
        }else if(sep<3600){
            cttext = Math.floor(sep/60)+"分钟前"
        }else if(sep<7200){
            cttext = "1小时前"
        }else if(sep>7200 && ct.getFullYear() == now.getFullYear() && ct.getMonth()==now.getMonth() && ct.getDate() == now.getDate()){
            cttext = "今天"+(ct.getHours()<10?("0"+ct.getHours()):ct.getHours())+":"+(ct.getMinutes()<10?"0"+ct.getMinutes():ct.getMinutes())
        }else if(ct.getFullYear() == now.getFullYear() && ct.getMonth()==now.getMonth() && ct.getDate() == now.getDate()-1){
            cttext = "昨天"+(ct.getHours()<10?("0"+ct.getHours()):ct.getHours())+":"+(ct.getMinutes()<10?"0"+ct.getMinutes():ct.getMinutes())
        }else{
            cttext = (ct.getMonth()+1) + "月"+ct.getDay()+"日"
        }
        dynamicItemTime.set("text",cttext);

        //最后加一层清除浮动
        new Element("div.dynamicItemTime",{styles:{"clear":"both"}}).inject(dynamicItem);
    },
    createChatContainer:function(){
        this.taskChatContainer.empty();
        var node = this.taskChatContainer;
        this.chatContent = new Element("div.chatContent",{styles:this.css.chatContent}).inject(node);
        this.chatTextarea = new Element("textarea.chatTextarea",{styles:this.css.chatTextarea,placeholder:this.lp.chatPlaceholder}).inject(this.chatContent);
        this.chatTextarea.addEvents({
            keypress:function(e){
                var keycode = (e.event.keyCode ? e.event.keyCode : e.event.which);
                if (e.event.ctrlKey && (keycode == 13 || keycode == 10)) {
                    this.chatBarSend.click();
                }
            }.bind(this)
        });

        this.chatBarContent = new Element("div.chatBarContent",{styles:this.css.chatBarContent}).inject(node);
        this.chatBarTool = new Element("div.chatBarTool",{styles:this.css.chatBarTool}).inject(this.chatBarContent);
        this.chatBarEmoji = new Element("div.chatBarEmoji",{styles:this.css.chatBarEmoji,title:this.lp.chatInsertEmoji}).inject(this.chatBarTool);
        this.chatBarEmoji.addEvents({
            mouseover:function(){this.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/icon_emoji_click.png)")},
            mouseout:function(){this.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/icon_emoji.png)")},
            click:function(){
                MWF.xDesktop.requireApp("TeamWork", "Emoji", function(){
                    var pc = new MWF.xApplication.TeamWork.Emoji(this,this.container, this.chatBarEmoji, this.app, {}, {
                        css:this.css, lp:this.lp, axis : "y",
                        position : { //node 固定的位置
                            x : "right",
                            y : "middle"
                        },
                        nodeStyles : {
                            "min-width":"200px",
                            "max-width":"210px",
                            "height":"80px",
                            "padding":"2px",
                            "border-radius":"5px",
                            "box-shadow":"0px 0px 4px 0px #999999",
                            "z-index" : "201"
                        },
                        onPostLoad:function(){
                            pc.node.setStyles({"opacity":"0","top":(pc.node.getStyle("top").toInt())+"px"});
                            var fx = new Fx.Tween(pc.node,{duration:400});
                            fx.start(["opacity"] ,"0", "1");
                        },
                        onClose:function(rd){
                            if(!rd) return;
                            if(rd.value && rd.value !=""){
                                this.chatTextarea.set("value",this.chatTextarea.get("value")+"["+rd.value+"]")
                            }
                        }.bind(this)
                    });
                    pc.load();
                }.bind(this))
            }.bind(this)
        });
        this.chatBarSend = new Element("div.chatBarSend",{styles:this.css.chatBarSend,text:this.lp.chatSend}).inject(this.chatBarContent);
        this.chatBarSend.addEvents({
            click:function(){
                if(this.chatTextarea.get("value").trim()=="") return;
                var data = {
                    taskId : this.taskData.id,
                    content : this.chatTextarea.get("value").trim()
                };

                this.actions.chatCreate(data,function(json){
                    if(json.data.id){
                        this.actions.chatGet(json.data.id,function(json){
                            var person = json.data.sender;
                            var content = json.data.content;

                            var dynamicItem = new Element("div.dynamicItem",{styles:this.css.dynamicItem}).inject(this.dynamicContent);
                            var dynamicItemIcon = new Element("div.dynamicItemIcon",{styles:this.css.dynamicItemIcon}).inject(dynamicItem);
                            dynamicItemIcon.setStyle("background-image","url(/x_component_TeamWork/$Task/default/icon/icon_chat.png)")
                            var dynamicItemText = new Element("div.dynamicItemText",{styles:this.css.dynamicItemText}).inject(dynamicItem);
                            new Element("div.dynamicItemUser",{styles:this.css.dynamicItemUser,text:person.split("@")[0]}).inject(dynamicItemText);

                            var chattext = content.split("\n").join("<br/>");
                            for(var item in this.app.lp.emoji){
                                var val = this.app.lp.emoji[item];
                                chattext = chattext.split("["+val+"]").join('<img style="margin:0 2px;" src="/x_component_TeamWork/$Emoji/default/icon/'+item+'.png" />');
                            }

                            new Element("div.dynamicItemUserChat",{styles:{"margin-top":"5px"},html:chattext}).inject(dynamicItemText);

                            var dynamicItemTime = new Element("div.dynamicItemTime",{styles:this.css.dynamicItemTime}).inject(dynamicItem);
                            dynamicItemTime.set("text","刚刚");
                            new Element("div.dynamicItemTime",{styles:{"clear":"both"}}).inject(dynamicItem);

                            this.dynamicContent.scrollTo(0,this.dynamicContent.getScrollSize().y);
                        }.bind(this))
                    }
                    this.createChatContainer()
                }.bind(this))
            }.bind(this)
        })
    },
    addDynamicItem:function(){

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
        //备注
        if(this.taskRemarkContainer){
            this.loadRemarkValue()
        }
        //优先级
        if(this.taskPriorityValue){
            this.loadPriorityValue()
        }
        //标签
        if(this.taskTagValue){
            this.loadTagValue()
        }
        //附件
        if(this.taskAttachmentValue){
            this.loadAttachment(this.taskAttachmentValue)
        }
    },
    saveTaskData:function(callback) {
        if(this.editor){
            this.taskData.description = encodeURI(this.editor.getData());
        }

        this.actions.taskSave(this.taskData,function(json){
            if(this.editor) delete this.editor;
            //alert(JSON.stringify(json));
            this.reload();
            //刷新project区域内的task，可能有更新，删除等 干脆直接载入区域
            var data = {
                "act":"save",
                "taskId":json.data.id
            };
            this.reloadOutContent(data);
        }.bind(this))
    },
    reloadOutContent:function(json){
        if(json.act == "save"){
            //保存操作，刷新task即可
            var taskNode = this.explorer.container.getElementById(json.taskId);
            if(!taskNode) return;
            this.actions.taskGet(json.taskId,function(d){
                this.explorer.loadTaskNode(taskNode,d.data)
            }.bind(this));
        }
    },
    updateSingleProperty:function(data,callback){
        this.actions.updateSingleProperty(data.taskId,data,function(json){
            if(json.data.dynamics){
                json.data.dynamics.each(function(dd){
                    this.loadDynamicItem(dd,"bottom")
                }.bind(this))
            }
            this.dynamicContent.scrollTo(0,this.dynamicContent.getScrollSize().y);
            if(callback)callback(json.data)
        }.bind(this))
    },
    loadNameValue:function(){  //名称
        var _self = this;
        this.taskNameContainer.set("text",this.taskData.name);
        if(true){ //权限修改
            var node = this.taskNameContainer;
            var nameEdit = false;
            var overStatus = null;
            node.addEvents({
                mouseenter:function(){
                    overStatus = window.setTimeout(function(){
                        var opt={ axis: "y", position : { x : "auto", y : "top"} };
                        //_self.app.showTips(node,{_html:"<div style='margin:2px 5px;'>"+_self.lp.editTip+"</div>"},opt);
                        node.set("title",_self.lp.editTip);
                    },300);

                    if(this.getElement("input")) return;
                    var name = this.get("text");
                    node.empty();
                    var input = new Element("input.taskNameInput",{type:"text",value:name,styles:_self.css.taskNameInput}).inject(node);
                    input.addEvents({
                        click:function(ev){
                            nameEdit = true;
                            if(_self.app.st&&_self.app.st.node)_self.app.st.node.destroy();
                            delete _self.app.st;
                            ev.stopPropagation()
                        },
                        blur:function(){
                            var v = this.get("value").trim();
                            if(v=="" || v == _self.taskData.name){
                                _self.taskNameContainer.empty();
                                _self.taskNameContainer.set("text",_self.taskData.name);
                                _self.taskNameContainer.setStyles({"background-color":""});
                                _self.taskData.name = v;
                            }else{
                                if(v != _self.taskData.name){
                                    var sd = {
                                        taskId:_self.taskData.id,
                                        property:"name",
                                        mainValue:v,
                                        secondaryValue:""
                                    }
                                    _self.updateSingleProperty(sd,function(json){
                                        _self.taskNameContainer.empty();
                                        _self.taskNameContainer.set("text",v);
                                        _self.taskNameContainer.setStyles({"background-color":""});
                                        _self.taskData.name = v;
                                        nameEdit = false;
                                    })
                                }
                            }
                        }
                    });
                },
                mouseleave:function(){
                    window.clearTimeout(overStatus);
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
        this.taskStatusValueText = new Element("div.taskStatusValueText",{styles:this.css.taskStatusValueText}).inject(this.taskStatusValueContainer);

        if(this.taskData.workStatus == "draft"){
            this.taskStatusValueContainer.setStyles({"color":"#666666"});
            this.taskStatusValueIcon.setStyles({"background-image":"url(/x_component_TeamWork/$Task/default/icon/icon_draft.png)"});
            this.taskStatusValueText.set("text",this.lp.status.draft);
        }else if(this.taskData.workStatus == "flow"){
            this.taskStatusValueContainer.setStyles({"color":"#666666"});
            this.taskStatusValueIcon.setStyles({"background-image":"url(/x_component_TeamWork/$Task/default/icon/icon_flow.png)"});
            this.taskStatusValueText.set("text",this.lp.status.flow);
        }else if(this.taskData.workStatus == "processing") {
            this.taskStatusValueContainer.setStyles({"color": "#666666"});
            this.taskStatusValueIcon.setStyles({"background-image": "url(/x_component_TeamWork/$Task/default/icon/icon_flow.png)"});
            this.taskStatusValueText.set("text",this.lp.status.processing);
        }else if(this.taskData.workStatus == "completed"){
            this.taskStatusValueContainer.setStyles({"color":"#69b439"});
            this.taskStatusValueIcon.setStyles({"background-image":"url(/x_component_TeamWork/$Task/default/icon/icon_complete.png)"});
            this.taskStatusValueText.set("text",this.lp.status.completed);
        }


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
                            if(rd.status){
                                if(rd.status != this.taskData.workStatus){
                                    var sd = {
                                        taskId:_self.taskData.id,
                                        property:"workStatus",
                                        mainValue:rd.status,
                                        secondaryValue:""
                                    };
                                    _self.updateSingleProperty(sd,function(){
                                        this.taskData.workStatus = rd.status;
                                        this.loadStatusValue();
                                    }.bind(this))
                                }
                            }
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
                    if(_self.taskData.workStatus == "processing")bgurl = "url(/x_component_TeamWork/$Task/default/icon/icon_flow.png)";
                    else if(_self.taskData.workStatus == "completed")bgurl = "url(/x_component_TeamWork/$Task/default/icon/icon_complete.png)";
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
                        this.selectPerson(this.taskDutyAddText,"identity",null,1,
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
                        var sd = {
                            taskId:_self.taskData.id,
                            property:"startTime",
                            secondaryValue:""
                        }
                        if(json.action == "ok"){
                            this.taskStartTime.set("text",json.dateString+":00");
                            sd.mainValue = json.dateString+":00"
                        }else if(json.action == "clear"){
                            this.taskStartTime.set("text",this.lp.taskTimeStart);
                            sd.mainValue = ""
                        }
                        this.updateSingleProperty(sd)

                    }.bind(this))
                }.bind(this)
            });
            this.taskEndTime.addEvents({
                click:function(){
                    var opt = {
                        type:"datetime"
                    };
                    this.app.selectCalendar(this.taskEndTime,this.container,opt,function(json){
                        var sd = {
                            taskId:_self.taskData.id,
                            property:"startTime",
                            secondaryValue:""
                        }
                        if(json.action == "ok"){
                            this.taskEndTime.set("text",json.dateString+":00");
                            sd.mainValue = json.dateString + ":00"
                        }else if(json.action == "clear"){
                            this.taskEndTime.set("text",this.lp.taskTimeEnd);
                            sd.mainValue = ""
                        }
                        this.updateSingleProperty(sd)
                    }.bind(this))
                }.bind(this)
            })
        }
    },
    loadRemarkValue:function(){
        var _self = this;
        var value = this.taskData.description || "";
        value = decodeURI(value);
        this.taskRemarkValue.set("html",value);
        if(this.taskRemarkValue.getFirst("p")){
            this.taskRemarkValue.getFirst("p").setStyles({"margin-top":"0px"})
        }
        if(this.editor) delete this.editor;
        if(true){ //权限
            if(value == ""){
                this.taskRemarkValue.set("text",this.lp.editTip);
            }
            this.taskRemarkValue.removeEvents(["click"])
            this.taskRemarkValue.addEvents({
                click:function(e){
                    this.taskRemarkValue.setStyles({"background-color":"","color":""});
                    this.taskRemarkValue.removeEvents(["mouseenter"]);
                    if(!this.editor)this.taskRemarkValue.empty();
                    this.loadRemarkEditor(this.taskRemarkValue,value,"taskRemark");
                    e.stopPropagation();
                }.bind(this),
                mouseenter:function(){
                    this.taskRemarkValue.setStyles({"background-color":"#f5f5f5","color":"#999999"})
                }.bind(this),
                mouseleave:function(){
                    this.taskRemarkValue.setStyles({"background-color":"","color":""})
                }.bind(this)
            });
        }

    },
    loadPriorityValue:function(){
        if(this.taskPriorityValue)this.taskPriorityValue.empty();
        var node = new Element("div.taskPriorityValueText",{styles:this.css.taskPriorityValueText}).inject(this.taskPriorityValue);
        var curColor = "#999999";
        if(this.taskData.priority){
            node.set("text",this.taskData.priority);
            if(this.taskData.priority == this.lp.priority.urgency) curColor = "#ffaf38";
            else if(this.taskData.priority == this.lp.priority.emergency) curColor = "#ff4f3e";

            node.setStyles({"color":curColor,"border":"1px solid "+curColor+""})
        }else{
            node.set("text",this.lp.priority.normal)
        }

        node.addEvents({
            click:function(){
                var pc = new MWF.xApplication.TeamWork.Task.PriorityCheck(this.container, node, this.app, {data:this.taskData}, {
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
                        pc.node.setStyles({"opacity":"0","top":(pc.node.getStyle("top").toInt()+4)+"px"});
                        var fx = new Fx.Tween(pc.node,{duration:400});
                        fx.start(["opacity"] ,"0", "1");
                    },
                    onClose:function(rd){
                        if(!rd) return;
                        if(rd.value){
                            if(rd.value != this.taskData.priority){
                                var sd = {
                                    taskId:this.taskData.id,
                                    property:"priority",
                                    mainValue:rd.value,
                                    secondaryValue:""
                                };
                                this.updateSingleProperty(sd,function(){
                                    this.taskData.priority = rd.value;
                                    this.loadPriorityValue();
                                }.bind(this))
                            }
                        }

                        this.loadPriorityValue();
                    }.bind(this)
                });
                pc.load();
            }.bind(this),
            mouseover:function(){
                this.setStyles({"border":"1px solid #4A90E2","color":"#4A90E2"})
            },
            mouseout:function(){
                this.setStyles({"border":"1px solid "+curColor+"","color":curColor})
            }
        })
    },
    loadTagValue:function(){
        if(this.taskTagValue)this.taskTagValue.empty();
        this.taskTagLayout = new Element("div.taskTagLayout",{styles:this.css.taskTagLayout}).inject(this.taskTagValue);
        this.taskTagAddContainer = new Element("div.taskTagAddContainer",{styles:this.css.taskTagAddContainer}).inject(this.taskTagValue);

        this.taskTagAddText = new Element("div.taskTagAddText",{styles:this.css.taskTagAddText,text:this.lp.taskTagAddText}).inject(this.taskTagAddContainer);
        this.taskTagAddText.addEvents({
            mouseover:function(){this.setStyles({"color":"#4A90E2"})},
            mouseout:function(){this.setStyles({"color":"#666666"})},
            click:function(){
                this.loadTaskTag(this.taskTagAddText)
            }.bind(this)
        });
        this.taskTagAdd = new Element("div.taskTagAdd",{styles:this.css.taskTagAdd}).inject(this.taskTagAddContainer);
        this.taskTagAdd.addEvents({
            mouseover:function(){this.setStyles({"background-image":"url(/x_component_TeamWork/$Task/default/icon/icon_subtask_add.png)"})},
            mouseout:function(){this.setStyles({"background-image":"url(/x_component_TeamWork/$Task/default/icon/icon_jia.png)"})},
            click:function(){
                this.loadTaskTag(this.taskTagAdd)
            }.bind(this)
        })


        if(!this.taskData.tags || this.taskData.tags.length==0){
            this.taskTagAdd.hide();
        }else{
            this.taskTagAddText.hide();
            this.taskData.tags.each(function(json){
                this.loadTagItem(json)
            }.bind(this))
        }
    },
    loadTagItem:function(data){
        var _self = this;
        var tagItemContainer = new Element("div.tagItemContainer",{styles:this.css.tagItemContainer,id:data.id}).inject(this.taskTagLayout);
        tagItemContainer.setStyles({"background-color":data.tagColor});
        tagItemContainer.addEvents({
            mouseenter:function(){
                var tagItemClose = new Element("div.tagItemClose",{styles:_self.css.tagItemClose}).inject(tagItemContainer);
                tagItemContainer.setStyles({"width":(tagItemContainer.getWidth().toInt()+2)+"px"});
                tagItemText.setStyles({"margin-right":"5px"});
                tagItemText.set("title",data.tag);
                tagItemClose.addEvents({
                    click:function(){
                        _self.actions.removeTagToTask(_self.taskData.id,data.id,function(json){
                            if(json.data.dynamics){
                                json.data.dynamics.each(function(dd){ 
                                    _self.loadDynamicItem(dd,"bottom")
                                })
                            }
                            _self.dynamicContent.scrollTo(0,_self.dynamicContent.getScrollSize().y);
                            tagItemContainer.destroy();
                            _self.taskData.tags.each(function(ddd){
                                if(ddd.id == data.id){
                                    _self.taskData.tags.erase(ddd)
                                }
                            })
                        })
                    }
                })
            },
            mouseleave:function(){
                //tagItemContainer.setStyles({"width":(tagItemContainer.getWidth().toInt()-this.getElement(".tagItemClose").getWidth()-2)+"px"});
                tagItemContainer.setStyles({"width":""});
                tagItemText.setStyles({"margin-right":"10px"});
                this.getElement(".tagItemClose").destroy();
            }
        });
        var tagItemText = new Element("div.tagItemText",{styles:this.css.tagItemText}).inject(tagItemContainer);
        tagItemText.set("text",data.tag);
    },
    loadTaskTag:function(node){
        MWF.xDesktop.requireApp("TeamWork", "TaskTag", function(){
            //alert(JSON.stringify(this.taskData.tags))
            var data = {
                projectId:this.taskData.project,
                taskId:this.taskData.id,
                taskTags:this.taskData.tags||[]
            };
            var pc = new MWF.xApplication.TeamWork.TaskTag(this.container, node, this.app, data, {
                axis : "y",
                position : { //node 固定的位置
                    x : "right",
                    y : "middle"
                },
                nodeStyles : {
                    "min-width":"200px",
                    "width":"210px",
                    //"height":"80px",
                    "padding":"2px",
                    "border-radius":"2px",
                    "box-shadow":"0px 0px 4px 0px #999999",
                    "z-index" : "201"
                },
                onPostLoad:function(){
                    pc.node.setStyles({"opacity":"0","top":(pc.node.getStyle("top").toInt())+"px"});
                    var fx = new Fx.Tween(pc.node,{duration:400});
                    fx.start(["opacity"] ,"0", "1");
                },
                onClose:function(rd){
                    if(!rd) return;
                    // if(rd.value && rd.value !=""){
                    //     this.chatTextarea.set("value",this.chatTextarea.get("value")+"["+rd.value+"]")
                    // }
                }.bind(this)
            },null,this);
            pc.load();
        }.bind(this))
    },
    loadRemarkEditor:function(parent,value,name){
        if(this.editor)return;
        COMMON.AjaxModule.load("ckeditor", function(){
            //CKEDITOR.disableAutoInline = true;
            var item = new Element("div",{
                "name" : name,
                "id" : name
            });
            //item.set( attr );
            if(parent)item.inject(parent);
            //if( value )item.set("html", value);

            var editorConfig = {
                "autoGrow_maxHeight": 400,
                //"autoGrow_minHeight": 300,
                "resize_enabled": true,
                //"resize_maxHeight": "3000",
                //"resize_minHeight": "200",
                "autoParagraph": true,
                "autoUpdateElement": true,
                "enterMode": 1,
                "height": 100,
                "width": 480,
                "readOnly": false,
                "startupFocus" : true,
                toolbar : [
                    //{ name: 'document', items : [ 'Preview' ] },
                    //{ name: 'clipboard', items : [ 'Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] },
                    { name: 'basicstyles', items : [ 'Bold','Underline','Strike','-','RemoveFormat' ] },
                    //{ name: 'paragraph', items : [ 'JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock' ] },
                    { name: 'styles', items : [ 'Font','FontSize' ] },
                    { name: 'colors', items : [ 'TextColor' ] },
                    { name: 'links', items : [ 'Link','Unlink' ] },
                    //{ name: 'insert', items : [ 'Image' ] },
                    { name: 'tools', items : [ 'Maximize','-' ] }
                ]
            };
            if( this.options.RTFConfig ){
                editorConfig = Object.merge( editorConfig, this.options.RTFConfig )
            }
            if( editorConfig.skin )editorConfig.skin = "moono-lisa";

            this.editor = CKEDITOR.replace(item, editorConfig);
            //this.items.push( this.editor );
            this.editor.setData(value);

            this.editor.on('instanceReady', function (e) {
                this.editor.focus();
                var range = this.editor.createRange();
                range.moveToElementEditEnd(this.editor.editable());
                this.editor.getSelection().selectRanges([range]);

                this.taskRemarkActionContainer = new Element("div.taskRemarkActionContainer",{styles:this.css.taskRemarkActionContainer}).inject(parent);
                this.taskRemarkActionOk = new Element("div.taskRemarkActionOk",{styles:this.css.taskRemarkActionOk,text:this.lp.save}).inject(this.taskRemarkActionContainer);
                this.taskRemarkActionOk.addEvents({
                    mouseover:function(){this.setStyles({"opacity":"0.9"})},
                    mouseout:function(){this.setStyles({"opacity":"1"})},
                    click:function(e){
                        var sd = {
                            taskId:this.taskData.id,
                            property:"description",
                            dataType:"RichText",
                            mainValue:encodeURI(this.editor.getData()),
                            secondaryValue:this.editor.document.getBody().getText()
                        }
                        this.updateSingleProperty(sd,function(json){
                            this.taskData.description = encodeURI(this.editor.getData());
                            this.loadRemarkValue()
                            delete this.editor;
                        }.bind(this))
                        e.stopPropagation();
                        //this.taskData.description = encodeURI(this.editor.getData());
                    }.bind(this)
                });
                this.taskRemarkActionCancel = new Element("div.taskRemarkActionCancel",{styles:this.css.taskRemarkActionCancel,text:this.lp.cancel}).inject(this.taskRemarkActionContainer);
                this.taskRemarkActionCancel.addEvents({
                    mouseover:function(){this.setStyles({"color":"#4A90E2"})},
                    mouseout:function(){this.setStyles({"color":"#999999"})},
                    click:function(e){
                        this.loadRemarkValue();
                        e.stopPropagation()
                    }.bind(this)
                })
            }.bind(this))

        }.bind(this));
    },
    loadAttachment: function( area ){
        MWF.xDesktop.requireApp("TeamWork", "TaskAttachment", function(){
            this.attachment = new MWF.xApplication.TeamWork.TaskAttachment( area, this.app, this.actions, this.app.lp, {
                size:"max",
                documentId : this.taskData.id,
                //isNew : this.options.isNew,
                //isEdited : this.options.isEdited,
                onQueryUploadAttachment : function(){
                    this.attachment.isQueryUploadSuccess = true;
                    if( !this.taskData.id || this.taskData.id=="" ){

                    }
                }.bind(this)
            });
            this.attachment.load();
        }.bind(this));
    },
    loadTaskAttachmentListContainer:function(){
        MWF.xDesktop.requireApp("TeamWork", "TaskAttachmentList", function(){
            var data = {
                id:this.taskData.id
            };
            var opt = {};
            this.attachment = new MWF.xApplication.TeamWork.TaskAttachmentList( this,this.taskAttachmentListContainer, data, opt);
            this.attachment.load();
        }.bind(this));
    },
    loadSubTaskContainer:function(){
        this.subTaskContainer.empty();
        this.subTaskTitleContent = new Element("div.subTaskTitleContent",{styles:this.css.subTaskTitleContent}).inject(this.subTaskContainer);
        this.subTaskTitleIcon = new Element("div.subTaskTitleIcon",{styles:this.css.subTaskTitleIcon}).inject(this.subTaskTitleContent);
        this.subTaskTitleText = new Element("div.subTaskTitleText",{styles:this.css.subTaskTitleText,text:this.lp.taskSubName+""}).inject(this.subTaskTitleContent);

        this.subTaskValueContent = new Element("div.subTaskValueContent",{styles:this.css.subTaskValueContent}).inject(this.subTaskContainer);

        this.subTaskAddContainer = new Element("div.subTaskAddContainer",{styles:this.css.subTaskAddContainer}).inject(this.subTaskValueContent);

        //子任务列表
        this.subTaskListContent = new Element("div.subTaskListContent",{styles:this.css.subTaskListContent}).inject(this.subTaskAddContainer);
        this.loadSubTask();

        //添加子任务
        this.subTaskNewContent = new Element("div.subTaskNewContent",{styles:this.css.subTaskNewContent}).inject(this.subTaskAddContainer);
        this.subTaskNewIcon = new Element("div.subTaskNewIcon",{styles:this.css.subTaskNewIcon}).inject(this.subTaskNewContent);
        this.subTaskNewText = new Element("div.subTaskNewText",{styles:this.css.subTaskNewText,text:this.lp.taskSubText}).inject(this.subTaskNewContent);
        this.subTaskNewContent.addEvents({
            click:function(){
                this.subTaskNewContent.hide();
                if(this.subTaskNewContainer)this.subTaskNewContainer.destroy();
                this.subTaskNewContainer = new Element("div.subTaskNewContainer",{styles:this.css.subTaskNewContainer}).inject(this.subTaskAddContainer);

                this.subTaskNewValue = new Element("div.subTaskNewValue",{styles:this.css.subTaskNewValue}).inject(this.subTaskNewContainer);
                this.subTaskNewInput = new Element("input.subTaskNewInput",{styles:this.css.subTaskNewInput,placeholder:this.lp.taskSubNamePlaceholder}).inject(this.subTaskNewValue);
                this.subTaskNewPerson = new Element("div.subTaskNewPerson",{styles:this.css.subTaskNewPerson}).inject(this.subTaskNewValue);

                this.subTaskNewPerson.addEvent("click",function(){
                    this.selectPerson(this.subTaskNewPerson,"identity",null,1,
                        function(json){
                            if(json.length>0){
                                this.taskSubNewPerson = json[0];
                                this.loadSubTaskPerson(this.subTaskNewPerson,json[0],true)
                            }
                        }.bind(this)
                    );
                }.bind(this));
                this.subTaskNewAction = new Element("div.subTaskNewAction",{styles:this.css.subTaskNewAction}).inject(this.subTaskNewContainer);
                this.subTaskNewCancel = new Element("div.subTaskNewCancel",{styles:this.css.subTaskNewCancel,text:this.lp.cancel}).inject(this.subTaskNewAction);
                this.subTaskNewCancel.addEvents({
                    click:function(){
                        this.subTaskNewContent.show();
                        this.subTaskNewContainer.destroy()
                    }.bind(this),
                    mouseover:function(){
                        this.setStyles({"color":"#4A90E2"})
                    },
                    mouseout:function(){
                        this.setStyles({"color":"#666666"})
                    }
                });
                this.subTaskNewOK = new Element("div.subTaskNewOK",{styles:this.css.subTaskNewOK,text:this.lp.save}).inject(this.subTaskNewAction);
                this.subTaskNewOK.addEvents({
                    click:function(){
                        if(this.subTaskNewInput.get("value").trim()=="") return;
                        var data = {
                            name:this.subTaskNewInput.get("value").trim(),
                            project:this.taskData.project,
                            parent:this.taskData.id,
                            executor:this.taskSubNewPerson || ""
                        };
                        this.actions.taskSave(data,function(json){
                            this.taskSubNewPerson = ""
                            if(json.data.id){
                                this.actions.taskGet(json.data.id,function(d){
                                    this.loadSubTaskItem(this.subTaskListContent,d.data);
                                }.bind(this))
                            }
                            this.subTaskNewContent.show();
                            this.subTaskNewContainer.destroy();

                            if(json.data.dynamics){
                                json.data.dynamics.each(function(dd){
                                    this.loadDynamicItem(dd,"bottom")
                                }.bind(this));
                            }
                            this.dynamicContent.scrollTo(0,this.dynamicContent.getScrollSize().y);

                        }.bind(this))
                    }.bind(this)
                })

            }.bind(this)
        });
    },
    loadSubTask:function(){
        var node = this.subTaskListContent;
        node.empty();
        this.actions.taskSubList(this.taskData.id,function(json){
            json.data.each(function(data){
                this.loadSubTaskItem(node,data);
            }.bind(this))
        }.bind(this))
    },
    loadSubTaskItem:function(node,data){
        var subTaskItemContainer = new Element("div.subTaskItemContainer",{styles:this.css.subTaskItemContainer,id:data.id}).inject(node);
        var subTaskItemText = new Element("div.subTaskItemText",{styles:this.css.subTaskItemText,text:data.name,title:this.lp.taskSubDes}).inject(subTaskItemContainer);
        subTaskItemText.addEvents({
            mouseover:function(){
                this.setStyles({"background-color":"#f5f5f5"})
            },
            mouseout:function(){
                this.setStyles({"background-color":""})
            },
            click:function(){
                var id = subTaskItemContainer.get("id");
                if(id && id!=""){
                    this.data.taskId = id;
                    this.openTask()
                }

            }.bind(this)
        });
        var subTaskItemPerson = new Element("div.subTaskItemPerson",{styles:this.css.subTaskItemPerson}).inject(subTaskItemContainer);
        // subTaskItemPerson.addEvent("click",function(){
        //     this.selectPerson(subTaskItemPerson,"identity",null,1,
        //         function(json){
        //             if(json.length>0){
        //                 this.loadSubTaskPerson(subTaskItemPerson,json[0],true)
        //             }
        //         }.bind(this)
        //     );
        // }.bind(this));
        if(data.executor && data.executor!=""){
            this.loadSubTaskPerson(subTaskItemPerson,data.executor)
        }
    },
    loadSubTaskPerson:function(node,identity,flag){
        if(flag==null) flag=false;
        node.empty();
        var taskSubNewPerson = identity;
        var _self = this
        var name = identity.split("@")[0];
        var container = new Element("div",{styles:{"height":"30px","padding":"0px 8px 0px 2px","border-radius":"4px","float":"left","position":"relative"}}).inject(node);
        container.set("title",name);
        var circleStyles={
            "width":"24px","height":"24px","border-radius":"20px","float":"left",
            "background-color":"#4A90E2","color":"#ffffff","line-height":"22px",
            "text-align":"center","margin-top":"3px","font-size":"12px"
        };

        var closeStyles={
            "width":"16px","height":"16px","position":"absolute","right":"-5px","top":"-2px",
            "background":"url(/x_component_TeamWork/$Task/default/icon/icon_off.png) no-repeat center"
        };
        var circleDiv = new Element("div",{styles:circleStyles,text:name.substr(0,1)}).inject(container);

        if(flag){ //权限
            container.addEvents({
                mouseenter:function(){
                    var closeIcon = new Element("div.closeIcon",{styles:closeStyles}).inject(this);
                    closeIcon.addEvents({
                        click:function(e){
                            node.empty();
                            node.setStyles(_self.css.subTaskNewPerson);
                            container.destroy();
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
                    this.selectPerson(this.subTaskNewPerson,"identity",null,1,
                        function(json){
                            if(json.length>0){
                                taskSubNewPerson = json[0];
                                this.loadSubTaskPerson(this.subTaskNewPerson,json[0],true)
                            }
                        }.bind(this)
                    );
                    e.stopPropagation();
                }.bind(this)
            })
        }
    },
    loadTaskPerson:function(node,identity,flag){
        var _self = this
        var name = identity.split("@")[0];
        var container = new Element("div",{styles:{"height":"30px","padding":"0 0px","border-radius":"4px","float":"left","position":"relative"}}).inject(node);
        var circleStyles={
            "width":"24px","height":"24px","border-radius":"20px","float":"left",
            "background-color":"#4A90E2","color":"#ffffff","line-height":"22px",
            "text-align":"center","margin-top":"3px","font-size":"12px"
        };
        var nameStyles={
            "height":"24px","float":"left","margin-left":"2px","margin-right":"10px",
            "color":"#333333","line-height":"22px","margin-top":"3px"
        };
        var closeStyles={
            "width":"16px","height":"16px","position":"absolute","right":"0px",
            "background":"url(/x_component_TeamWork/$Task/default/icon/icon_off.png) no-repeat center"
        };
        var circleDiv = new Element("div",{styles:circleStyles,text:name.substr(0,1)}).inject(container);
        var nameDiv = new Element("div",{styles:nameStyles,text:name}).inject(container);
        if(flag){ //权限
            container.addEvents({
                mouseenter:function(){
                    // var closeIcon = new Element("div.closeIcon",{styles:closeStyles}).inject(this);
                    // closeIcon.addEvents({
                    //     click:function(e){
                    //         // _self.taskData.executor = "";
                    //         // _self.loadDutyValue();
                    //
                    //         var sd = {
                    //             taskId:_self.taskData.id,
                    //             property:"executor",
                    //             mainValue:"",
                    //             secondaryValue:""
                    //         };
                    //         _self.updateSingleProperty(sd,function(){
                    //             _self.taskData.executor = "";
                    //             _self.loadDutyValue()
                    //         })
                    //
                    //         e.stopPropagation();
                    //     }
                    // });
                    this.setStyles({"background-color":"#efefef","cursor":"pointer"});
                },
                mouseleave:function(){
                    if(this.getElement(".closeIcon"))this.getElement(".closeIcon").destroy();
                    this.setStyles({"background-color":""})
                },
                click:function(e){
                    _self.selectPerson(_self.taskDutyAddText,"identity",null,1,
                        function(json){
                            if(json.length>0){
                                if(_self.taskData.executor!=json[0]){
                                    var sd = {
                                        taskId:_self.taskData.id,
                                        property:"executor",
                                        mainValue:json[0],
                                        secondaryValue:""
                                    };
                                    _self.updateSingleProperty(sd,function(){
                                        _self.taskData.executor = json[0];
                                        _self.loadDutyValue()
                                    })
                                }
                            }
                        }
                    );
                }
            })
        }
    },
    loadparticipantPerson:function(node,identity,flag){
        var _self = this;
        var name = identity.split("@")[0];
        var container = new Element("div",{styles:{"height":"30px","padding":"0 0px","border-radius":"4px","float":"left","position":"relative"}}).inject(node);
        var circleStyles={
            "width":"24px","height":"24px","border-radius":"20px","float":"left",
            "background-color":"#4A90E2","color":"#ffffff","line-height":"22px",
            "text-align":"center","margin-top":"3px","font-size":"12px"
        };
        var nameStyles={
            "height":"24px","float":"left","margin-left":"2px","margin-right":"16px",
            "color":"#333333","line-height":"22px","margin-top":"3px"
        };
        var closeStyles={
            "width":"16px","height":"16px","position":"absolute","right":"0px",
            "background":"url(/x_component_TeamWork/$Task/default/icon/icon_off.png) no-repeat center"
        };
        var circleDiv = new Element("div",{styles:circleStyles,text:name.substr(0,1)}).inject(container);
        var nameDiv = new Element("div",{styles:nameStyles,text:name}).inject(container);
        if(flag){ //权限
            container.addEvents({
                mouseenter:function(){
                    var closeIcon = new Element("div.closeIcon",{styles:closeStyles}).inject(this);
                    closeIcon.addEvents({
                        click:function(e){
                             var index = _self.taskData.participantList.indexOf(identity);
                             if(index>-1){
                                 _self.taskData.participantList.erase(identity)
                             }

                             _self.actions.updateParticipantList(_self.taskData.id,{participantList:_self.taskData.participantList},function(json){
                                 _self.createParticipateContainer();
                                 if(json.data.dynamics){
                                     json.data.dynamics.each(function(dd){
                                         _self.loadDynamicItem(dd,"bottom")
                                     })
                                 }
                                 _self.dynamicContent.scrollTo(0,_self.dynamicContent.getScrollSize().y);
                             })
                            e.stopPropagation();
                        }
                    });
                    this.setStyles({"background-color":"#efefef","cursor":"pointer"});
                },
                mouseleave:function(){
                    if(this.getElement(".closeIcon"))this.getElement(".closeIcon").destroy();
                    this.setStyles({"background-color":""})
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
    selectPerson: function( item, type,types,count,callback ) {
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        this.fireEvent("querySelect", this);
        var value = [];
        var options = {
            "type": type,
            "types":types,
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

MWF.xApplication.TeamWork.Task.TaskMore = new Class({
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
        //this.contentNode

        var topMoreTitle = new Element("div.topMoreTitle",{styles:this.css.topMoreTitle,text:this.lp.taskMenu}).inject(this.contentNode);

        var copyTask = new Element("div.copyTask",{styles:this.css.topMoreItem}).inject(this.contentNode);
        copyTask.addEvents({
            mouseenter:function(){this.setStyles({"background-color":"#F7F7F7"})},
            mouseleave:function(){this.setStyles({"background-color":""})}
        });
        var copyTaskIcon = new Element("div.copyTaskIcon",{styles:this.css.topMoreItemIcon}).inject(copyTask);
        copyTaskIcon.setStyles({"background":"url(/x_component_TeamWork/$Task/default/icon/taskcopy.png) no-repeat center"});
        var copyTaskText = new Element("div.copyTaskText",{styles:this.css.topMoreItemText,text:this.lp.taskCopy}).inject(copyTask);

        var moveTask = new Element("div.moveTask",{styles:this.css.topMoreItem}).inject(this.contentNode);
        moveTask.addEvents({
            mouseenter:function(){this.setStyles({"background-color":"#F7F7F7"})},
            mouseleave:function(){this.setStyles({"background-color":""})},
            click:function(){
                var data = this.data;
                var opt = {};
                MWF.xDesktop.requireApp("TeamWork", "TaskMove", function(){
                    var taskmove = new MWF.xApplication.TeamWork.TaskMove(this.explorer,data,opt);
                    taskmove.open();
                    var fx = new Fx.Tween(this.node,{duration:200});
                    fx.start(["opacity"] ,"1", "0").chain(function(){this.close()}.bind(this));
                }.bind(this));
            }.bind(this)
        });
        var moveTaskIcon = new Element("div.moveTaskIcon",{styles:this.css.topMoreItemIcon}).inject(moveTask);
        moveTaskIcon.setStyles({"background":"url(/x_component_TeamWork/$Task/default/icon/taskmove.png) no-repeat center"});
        var moveTaskText = new Element("div.moveTaskText",{styles:this.css.topMoreItemText,text:this.lp.taskMove}).inject(moveTask);

        var favTask = new Element("div.favTask",{styles:this.css.topMoreItem}).inject(this.contentNode);
        favTask.addEvents({
            mouseenter:function(){this.setStyles({"background-color":"#F7F7F7"})},
            mouseleave:function(){this.setStyles({"background-color":""})}
        });
        var favTaskIcon = new Element("div.favTaskIcon",{styles:this.css.topMoreItemIcon}).inject(favTask);
        favTaskIcon.setStyles({"background":"url(/x_component_TeamWork/$Task/default/icon/taskfav.png) no-repeat center"});
        var favTaskText = new Element("div.favTaskText",{styles:this.css.topMoreItemText,text:this.lp.taskFav}).inject(favTask);

        var subTask = new Element("div.subTask",{styles:this.css.topMoreItem}).inject(this.contentNode);
        subTask.addEvents({
            click:function(){
                var data = this.data;

                MWF.xDesktop.requireApp("TeamWork", "TaskSub", function(){
                    var opt = {
                        "onPostOpen":function(){
                            //tasksub.formAreaNode.setStyles({"height":"0px","width":"0px","overflow":"hidden"})
                            //tasksub.formAreaNode.setStyles({"top":"10px"});
                            var fx = new Fx.Tween(tasksub.formAreaNode,{duration:200});
                            fx.start(["top"] ,"10px", "100px");
                        }
                    };
                    var tasksub = new MWF.xApplication.TeamWork.TaskSub(this.explorer,data,opt);
                    tasksub.open();
                    var fx = new Fx.Tween(this.node,{duration:200});
                    fx.start(["opacity"] ,"1", "0").chain(function(){this.close()}.bind(this));
                }.bind(this));
            }.bind(this),
            mouseenter:function(){this.setStyles({"background-color":"#F7F7F7"})},
            mouseleave:function(){this.setStyles({"background-color":""})}
        });
        var subTaskIcon = new Element("div.subTaskIcon",{styles:this.css.topMoreItemIcon}).inject(subTask);
        subTaskIcon.setStyles({"background":"url(/x_component_TeamWork/$Task/default/icon/tasksub.png) no-repeat center"});
        var subTaskText = new Element("div.subTaskText",{styles:this.css.topMoreItemText,text:this.lp.taskSub}).inject(subTask);

        var removeTask = new Element("div.removeTask",{styles:this.css.topMoreItem}).inject(this.contentNode);
        removeTask.addEvents({
            click:function(e){
                _self.app.confirm("warn",e,_self.app.lp.common.confirm.removeTitle,_self.app.lp.common.confirm.removeContent,300,120,function(){
                    _self.actions.taskRemove(_self.data.data.id,function(){
                        var rd = {"act":"remove"};
                        _self.close(rd);
                        this.close()
                    }.bind(this))
                },function(){
                    this.close();
                });
            },
            mouseenter:function(){this.setStyles({"background-color":"#F7F7F7"})},
            mouseleave:function(){this.setStyles({"background-color":""})}
        });
        var removeTaskIcon = new Element("div.removeTaskIcon",{styles:this.css.topMoreItemIcon}).inject(removeTask);
        removeTaskIcon.setStyles({"background":"url(/x_component_TeamWork/$Task/default/icon/taskremove.png) no-repeat center"});
        var removeTaskText = new Element("div.removeTaskText",{styles:this.css.topMoreItemText,text:this.lp.taskRemove}).inject(removeTask);


        if(callback)callback();
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
        var flowText = new Element("div",{styles:text,text:this.lp.status.processing}).inject(flowContainer);
        flowText.setStyles({"background-color":"#f0f0f0"});
        if(this.data.data.workStatus == "processing"){
            var flowIcon = new Element("div",{styles:icon}).inject(flowContainer);
        }

        flowContainer.addEvents({
            click:function(){
                var data = {"status":"processing"};
                this.close(data)
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#f2f5f7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        var completeContainer = new Element("div",{styles:container}).inject(this.contentNode);
        var completeText = new Element("div",{styles:text,text:this.lp.status.completed}).inject(completeContainer);
        completeText.setStyles({"color":"#69b439","background-color":"#f1f9ec"});
        if(this.data.data.workStatus == "completed"){
            var completeIcon = new Element("div",{styles:icon}).inject(completeContainer);
        }

        completeContainer.addEvents({
            click:function(){
                var data = {"status":"completed"};
                this.close(data)
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#f2f5f7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        if(callback)callback();
    }

});

MWF.xApplication.TeamWork.Task.PriorityCheck = new Class({
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
        //this.contentNode

        var container = {
            "cursor":"pointer",
            "height":"40px",
            "width":"100%"
        };
        var text={
            "height":"25px","line-height":"25px","float":"left","width":"50px","text-align":"center",
            "margin-left":"6px","margin-top":"8px",
            "font-size":"13px","color":"#666666","border-radius":"2px"
        };
        var icon = {
            "float":"right","width":"24px","height":"24px",
            "margin-top":"6px","margin-right":"8px",
            "background":"url(/x_component_TeamWork/$Task/default/icon/icon_dagou.png) no-repeat center"
        };

        var normalContainer = new Element("div",{styles:container}).inject(this.contentNode);
        var normalText = new Element("div",{styles:text,text:this.lp.priority.normal}).inject(normalContainer);
        normalText.setStyles({"color":"#999999","border":"1px solid #999999"});
        if(this.data.data.priority == this.lp.priority.normal){
            new Element("div",{styles:icon}).inject(normalContainer);
        }
        normalContainer.addEvents({
            click:function(){
                var data = {"value":this.lp.priority.normal};
                this.close(data)
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#f2f5f7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        var urgencyContainer = new Element("div",{styles:container}).inject(this.contentNode);
        var urgencyText = new Element("div",{styles:text,text:this.lp.priority.urgency}).inject(urgencyContainer);
        urgencyText.setStyles({"color":"#ffaf38","border":"1px solid #ffaf38"});
        if(this.data.data.priority == this.lp.priority.urgency){
            new Element("div",{styles:icon}).inject(urgencyContainer);
        }
        urgencyContainer.addEvents({
            click:function(){
                var data = {"value":this.lp.priority.urgency};
                this.close(data)
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#f2f5f7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        var emergencyContainer = new Element("div",{styles:container}).inject(this.contentNode);
        var emergencyText = new Element("div",{styles:text,text:this.lp.priority.emergency}).inject(emergencyContainer);
        emergencyText.setStyles({"color":"#ff4f3e","border":"1px solid #ff4f3e"});
        if(this.data.data.priority == this.lp.priority.emergency){
            new Element("div",{styles:icon}).inject(emergencyContainer);
        }
        emergencyContainer.addEvents({
            click:function(){
                var data = {"value":this.lp.priority.emergency};
                this.close(data)
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#f2f5f7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        if(callback)callback();
    }

});

MWF.xApplication.TeamWork.Task.DynamicType = new Class({
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
        //this.contentNode

        var container = {
            "cursor":"pointer",
            "height":"40px",
            "width":"100%"
        };
        var text={
            "height":"25px","line-height":"25px","float":"left","width":"100%",
            "margin-left":"6px","margin-top":"8px",
            "font-size":"13px","color":"#666666","border-radius":"2px"
        };
        var icon = {
            "float":"right","width":"24px","height":"24px",
            "margin-top":"6px","margin-right":"8px",
            "background":"url(/x_component_TeamWork/$Task/default/icon/icon_dagou.png) no-repeat center"
        };

        var allContainer = new Element("div",{styles:container}).inject(this.contentNode);
        var allText = new Element("div",{styles:text,text:this.lp.dynamicAll}).inject(allContainer);
        allText.setStyles({"color":"#999999","border":"0px solid #999999"});

        allContainer.addEvents({
            click:function(){
                var data = {"value":"all"};
                this.close(data)
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#f2f5f7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        var attachmentContainer = new Element("div",{styles:container}).inject(this.contentNode);
        var attachmentText = new Element("div",{styles:text,text:this.lp.dynamicAttachment}).inject(attachmentContainer);
        attachmentText.setStyles({"color":"#999999","border":"0px solid #999999"});

        attachmentContainer.addEvents({
            click:function(){
                var data = {"value":"attachment"};
                this.close(data)
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#f2f5f7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        var chatContainer = new Element("div",{styles:container}).inject(this.contentNode);
        var chatText = new Element("div",{styles:text,text:this.lp.dynamicChat}).inject(chatContainer);
        chatText.setStyles({"color":"#999999","border":"0px solid #999999"});

        chatContainer.addEvents({
            click:function(){
                var data = {"value":"chat"};
                this.close(data)
            }.bind(this),
            mouseover:function(){this.setStyles({"background-color":"#f2f5f7"})},
            mouseout:function(){this.setStyles({"background-color":""})}
        });

        if(callback)callback();
    }

});