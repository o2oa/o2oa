MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.MobileTasks = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (container, app,mobile,options ) {
        this.setOptions(options);
        this.container = container;

        this.app = app;
        this.mobile = mobile;
        this.lp = this.app.lp.mobile.task;
        this.rootActions = this.app.rootActions;
        this.path = "../x_component_TeamWork/$MobileTasks/";
        
        this.taskId = this.options.id;
        this.curTab = this.options.tab || "base";
        
        this.mobile.setRouter("task",this.curTab,this.taskId);
        this.load();
    },
    historyBack:function(){
        this.mobile.back();
    },
    load: function () {
        this.container.empty();
        this.rootActions.TaskAction.get(this.taskId,function(json){
            this.data = json.data;
            this.getControl();
            this.loadTop();
            this.content = new Element("div.tw-tasks-content").inject(this.container);
            this.loadContent();
        }.bind(this))
    },
    reload:function(){
        this.load();
    },
    getControl:function(){
        this.control = {};
        this.control.isEdit = this.data.control.edit; 
        this.control.isDelete = this.data.control.delete;
        this.control.isFounder = this.data.control.founder; //是否是上级管理员
        this.control.isCreate = true;
        this.control.comment = true;

        this.working = this.data.workStatus != "completed" && this.data.workStatus != "canceled";

        /* 
        字段编辑3种权限
        1、如果edit = false 只能看
        2、如果edit = true 
        2.1、如果状态是已完成或已取消 只能改状态字段
        2.2、如果funder = false 只能改进度等部分字段
        2.3、如果funder = true 能改全部字段
    */

    },
    
    loadContent:function(){
        var _self = this;
        this.tabLayout = new Element("div.tw-tasks-tab").inject(this.content);
        this.taskContentLayout = new Element("div.tw-tasks-layout").inject(this.content);

        this.loadTabLayout();
    },
    loadTop:function(){
        var node = new Element("div.tw-common-top").inject(this.container);
        var back = new Element("div.tw-common-top-back.o2-tw-jiantou-you").inject(node);
        new Element("div.tw-common-top-title",{text:this.data.name||""}).inject(node);
        var right = new Element("div.tw-common-top-right").inject(node);
        
        back.addEvent('click',function(){
            this.historyBack()
        }.bind(this))
    },
    
    loadTabLayout:function(){ 
        var _this = this;
        this.baseTab = new Element("div.tw-tasks-tab-item",{text:this.lp.base}).inject(this.tabLayout);
        this.subTab = new Element("div.tw-tasks-tab-item",{text:this.lp.subTask}).inject(this.tabLayout);
        this.dynamicTab = new Element("div.tw-tasks-tab-item",{text:this.lp.dynamic}).inject(this.tabLayout);

        this.baseTab.addEvent('click',function(){ 
            _this.curTab = "base";
            _this.setTabCurrent(this);
            _this.loadBaseLayout();
        })
        this.subTab.addEvent('click',function(){ 
            _this.curTab = "sub";
            _this.setTabCurrent(this);
            _this.loadSubLayout();
        })
        this.dynamicTab.addEvent('click',function(){
            _this.curTab = "dynamic";
            _this.setTabCurrent(this);
            _this.loadDynamicLayout();
        })
        
        if(this.curTab == "base") this.baseTab.click();
        else if(this.curTab == "sub") this.subTab.click();
        else if(this.curTab == "dynamic") this.dynamicTab.click();
    },
    setTabCurrent:function(node){ 
        this.tabLayout.getElements('.tw-tasks-tab-item').removeClass('tw-tasks-tab-item-current');
        node.addClass('tw-tasks-tab-item-current');

        //var _thisRouter = this.app.mobileRouterList[this.app.mobileRouterList.length - 1];
        var _thisRouter = this.mobile.router[this.mobile.router.length - 1];
        if(_thisRouter && _thisRouter.title == "task" && _thisRouter.id == this.taskId){
            _thisRouter.tab = this.curTab;
        }
        
    },
    loadBaseLayout:function(){
        this.taskContentLayout.empty();
        var _html = this.path+this.options.style+"/base.html"; 
        this.taskContentLayout.loadAll({"html":_html},{"bind": {"lp": this.lp}, "module": this},function(){
            this.setTaskData();
        }.bind(this));
    },
    loadSubLayout:function(){
        new MWF.xApplication.TeamWork.MobileTasks.SubTask(this.taskContentLayout,this);
    },
    loadDynamicLayout:function(){
        new MWF.xApplication.TeamWork.MobileTasks.Dynamic(this.taskContentLayout,this);
    },

    setTaskData:function(){
        var _this = this;
        // this.working = this.data.workStatus != "completed" && this.data.workStatus != "canceled";
        
        //任务名称
        this.loadTaskName();
        
        //任务状态
        this.loadTaskStatus();

        //任务责任人
        this.loadTaskDuty();

        //参与者
        this.loadTaskParticipantList();

        //任务时间
        this.loadTaskTime();
        
        //任务来源
        this.loadTaskSource();

        //任务说明
        this.loadTasksDescription();

        //工作进展
        this.loadTasksDetail();

        //重要程度
        this.loadTasksImportant();

        //紧急程度
        this.loadTasksUrgency();

        //工作进度
        this.loadTasksProgress();

        //附件
        this.loadTasksAttachment();
        
    },
    loadTaskName:function(){
        if(!this.tasksName) return;
        this.tasksName.set('value',this.data.name); 
        if(this.control.isEdit && this.control.isFounder && this.working){ 
            this.tasksName.removeAttribute('readonly');
            this.tasksName.addEvent('blur',function(){ 
                var v = this.get("value").trim();
                if(v=="") return;
                if(v != _this.data.name){
                    var _data = {
                        property:"name",
                        mainValue:v,
                        secondaryValue:""
                    }
                    _this.updateSingleProperty(_data)
                }
            })
        }
    },
    loadTaskStatus:function(){ 
        if(!this.tasksStatus) return;
        //this.tasksStatus.set('text',this.lp.common.workStatus[this.data.workStatus]);
        this.tasksStatus.set('text',this.app.lp.mobile.common.workStatus[this.data.workStatus]);
        if(this.control.isEdit){ 
            var _node = this.tasksStatus.getParent();
            _node.removeEvents('click').addEvent('click',function(){ 
                new MWF.xApplication.TeamWork.MobileTasks.StatusCheck(this.container,this);
            }.bind(this))
        }
    },
    loadTaskDuty:function(){ 
        if(!this.tasksDuty || this.data.executor == "") return;
        this.tasksDuty.empty();
        var _node = this.tasksDuty.getParent();
        var user = this.data.executor.split("@")[0];
        var userLayout = new Element("div.tw-common-user-layout").inject(this.tasksDuty);
        new Element("div.tw-common-user-icon",{text:user.substring(0,1)}).inject(userLayout);
        new Element("div",{text:user}).inject(userLayout);

        if(this.control.isEdit && this.control.isFounder && this.working){ 
            _node.removeEvents("click").addEvent('click',function(){
                var idList = this.mobile.getIdentityByPerson([this.data.executor]);
                this.mobile.selectPerson("identity",1,idList,function(personList){
                    if(personList.length>0){
                        var person = personList[0];
                        if(person != this.data.executor){
                            this.data.executor = person;
                            var _data = {
                                property:"executor",
                                mainValue:person
                            }
                            this.updateSingleProperty(_data,function(){
                                this.loadTaskDuty()
                            }.bind(this))
                        }
                    }
                }.bind(this))
            }.bind(this))
        }else{
            _node.getElement('.tw-tasks-base-item-value-icon').destroy();
        }
    },
    loadTaskParticipantList:function(){ 
        if(!this.participant) return;
        this.participant.empty();
        var _node = this.participant.getParent();
        this.data.participantList.each(function(d){
            const user = d.split("@")[0];
            var tempNode = new Element("div.tw-common-user-layout").inject(this.participant);
            new Element("div.tw-common-user-icon",{text:user.substring(0,1)}).inject(tempNode);
            new Element("div",{text:user}).inject(tempNode);
        }.bind(this));

        if(this.control.isEdit && this.control.isFounder && this.working){ 
            _node.removeEvents("click").addEvent('click',function(){
                var idList = this.mobile.getIdentityByPerson(this.data.participantList);
                this.mobile.selectPerson("identity",0,idList,function(personList){ 
                    this.rootActions.TaskAction.updateParticipant(this.taskId,{participantList:personList},function(json){ 
                        this.data.participantList = personList;
                        this.loadTaskParticipantList()
                    }.bind(this))
                }.bind(this))
            }.bind(this))
        }else{
            _node.getElement('.tw-tasks-base-item-value-icon').destroy();
        }
    },
    loadTaskTime:function(){ 
        this.tasksStartTime.set('value',this.data.startTime ? this.data.startTime.split(' ')[0]:"");
        this.tasksEndTime.set('value',this.data.endTime ? this.data.endTime.split(' ')[0]:"");

        if(this.control.isEdit && this.control.isFounder && this.working){ 
            this.tasksStartTime.removeEvents('click').addEvent('click',function(){
                var opt = { type:"date" };
                this.mobile.selectCalendar(this.tasksStartTime,opt,function(json){
                    if(json.action == "ok"){
                        this.data.startTime = json.dateString;
                    }else if(json.action == "clear"){
                        this.data.startTime = '';
                    }
                    this.updateSingleProperty({property:"startTime",mainValue:this.data.startTime},function(){
                        this.loadTaskTime();
                    }.bind(this))

                }.bind(this))
            }.bind(this))
            this.tasksEndTime.removeEvents('click').addEvent('click',function(){
                var opt = { type:"date" };
                this.mobile.selectCalendar(this.tasksEndTime,opt,function(json){
                    if(json.action == "ok"){
                        this.data.endTime = json.dateString;
                    }else if(json.action == "clear"){
                        this.data.endTime = '';
                    }
                    this.updateSingleProperty({property:"endTime",mainValue:this.data.endTime},function(){
                        this.loadTaskTime();
                    }.bind(this))
                }.bind(this))
            }.bind(this))
        }else{
            var _nextIcon = this.tasksEndTime.getParent().getNext();
            if(_nextIcon) _nextIcon.destroy();
        }
    },
    loadTaskSource:function(){ 
        var _this = this;
        if(!this.tasksSource) return;
        this.tasksSource.set('value',this.data.source); 
        if(this.control.isEdit && this.control.isFounder && this.working){ 
            this.tasksSource.removeAttribute('readonly');
            this.tasksSource.addEvent('blur',function(){ 
                var v = this.get("value").trim();
                if(v != _this.data.source){
                    var _data = {
                        property:"source",
                        mainValue:v
                    }
                    _this.updateSingleProperty(_data);
                }
            })
        }
    },
    loadTasksDescription:function(){
        var _this = this;
        this.tasksDescription.set("value",this.data.description);
        this.tasksDescription.setStyles({"height":(this.tasksDescription.scrollHeight)+"px"});
        if(this.control.isEdit && this.control.isFounder && this.working){ 
            this.tasksDescription.removeAttribute('readonly');
            
            this.tasksDescription.addEvent('input',function(){ 
                var v = _this.tasksDescription.get("value");
                this.style.height = 'auto';
                this.style.height = (this.scrollHeight) + 'px';

            });
            this.tasksDescription.addEvent('blur',function(){ 
                var v = this.get("value").trim();
                if(v != _this.data.description){
                    var _data = {
                        property:"description",
                        mainValue:v,
                        secondaryValue:v
                    }
                    _this.updateSingleProperty(_data,function(){
                        _this.data.description = v
                    });
                }
            })
        }else{
            this.tasksDescription.removeAttribute('placeHolder');
        }
    },
    loadTasksDetail:function(){
        var _this = this;
        this.tasksDetail.set("value",this.data.detail);
        this.tasksDetail.setStyles({"height":(this.tasksDetail.scrollHeight)+"px"});
        if(this.control.isEdit && this.control.isFounder && this.working){ 
            this.tasksDetail.removeAttribute('readonly');
            this.tasksDetail.addEvent('input',function(){ 
                var v = _this.tasksDetail.get("value");
                this.style.height = 'auto';
                this.style.height = (this.scrollHeight) + 'px';
            });
            this.tasksDetail.addEvent('blur',function(){ 
                var v = this.get("value").trim();
                if(v != _this.data.detail){
                    var _data = {
                        property:"detail",
                        mainValue:v,
                        secondaryValue:v
                    }
                    _this.updateSingleProperty(_data,function(){
                        _this.data.detail = v
                    });
                }
            })
        }else{
            this.tasksDetail.removeAttribute('placeHolder');
        }
    },
    loadTasksImportant:function(){ 
        this.loadTasksRadioByProperty("important",this.tasksImportant);
    },
    loadTasksUrgency:function(){ 
        this.loadTasksRadioByProperty("urgency",this.tasksUrgency);
    },
    loadTasksProgress:function(){
        var _this = this;
        var value = this.data.progress; 
        if(this.control.isEdit && this.working){
            this.progressSelect = new Element("select").inject(this.tasksProgress);
            for(var i=0;i<=100;i++){
                new Element("option",{"text":i,"value":i}).inject(this.progressSelect);
            }
            this.progressSelect.set("value",value);
            new Element("div",{text:"%"}).inject(this.tasksProgress);

            this.progressSelect.addEvent("change",function(){
                var v = this.get("value");

                var _data = {
                    property:"progress",
                    mainValue:parseInt(v),
                }
                _this.updateSingleProperty(_data,function(){
                    if(v == "100"){
                        _this.reload();
                    }
                });

            });

        }else{
            this.tasksProgress.set("text",value+"%")
        }
        
    },
    loadTasksAttachment:function(){
        if(!this.tasksAttachment) return;
        this.tasksAttachment.empty();
        var _html = this.path+this.options.style+"/attachment.html"; 
        
        this.rootActions.AttachmentAction.listByTaskId(this.taskId,function(json){
            var data = json.data; 
            this.tasksAttachment.loadAll({"html":_html},{"bind": {"lp": this.lp,data:data}, "module": this},function(){
                if(!(this.control.isEdit && this.control.isFounder && this.working)){
                    this.tasksAttachment.getElements('.tw-tasks-base-attachment-item-remove').destroy();
                }
            }.bind(this));
        }.bind(this));
    },



    removeAttachment: function (id,event) {
        var _this = this;
        this.app.confirm("warn",event,this.app.lp.common.confirm.removeTitle,this.app.lp.common.confirm.removeContent,300,120,function(){
            _this.rootActions.AttachmentAction.delete(id, function (json) {
                _this.loadTasksAttachment()
                this.close();
            }.bind(this))
            
        },function(){
            this.close();
        });
    },
    downloadAttachment: function (id) {
        var address = this.rootActions.AttachmentAction.action.address;
        var url = this.rootActions.AttachmentAction.action.actions.downLoad.uri;
        url = url.replace("{id}", encodeURIComponent(id));
        window.open(address+url);
    },

    loadTasksRadioByProperty:function(property, tasksElement) {
        var value = this.data[property];
        if(!(this.control.isEdit && this.working)){
            tasksElement.set('text',value);
            return;
        }
        var items = tasksElement.getElements(".tw-tasks-base-item-yuan");
        items.each(function(item) {
          var status = item.getAttribute("data-status");
          var itemElement = item.getElement('div');
      
          itemElement.removeClass('o2-tw-wancheng').removeClass('o2-tw-yuan').addClass('o2-tw-yuan');
          itemElement.setStyles({ "color": "#333333" });
      
          if (status == value) {
            itemElement.removeClass('o2-tw-yuan').addClass('o2-tw-wancheng');
            itemElement.setStyles({ "color": "#4A90E2" });
          }
      
          item.removeEvents('click').addEvent('click', function() {
            if (value != status) {
              var _data = {
                property: property,
                mainValue: status
              };
              this.updateSingleProperty(_data, function() {
                this.data[property] = status;
                this.loadTasksRadioByProperty(property, tasksElement);
              }.bind(this));
            }
          }.bind(this));
        }.bind(this));
    },
    updateSingleProperty:function(data,callback){
        this.rootActions.TaskAction.updateSingleProperty(this.taskId,data,function(json){
            if(callback)callback(json.data)
        }.bind(this))
    }

});

//子任务
MWF.xApplication.TeamWork.MobileTasks.SubTask = new Class({
    initialize: function (content, explorer ) {
        this.content = content;
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.mobile = this.explorer.mobile;
        this.data = this.explorer.data;
        this.rootActions = this.app.rootActions;
        this.load();
    },
    load:function(){ 
        this.content.empty();

        this.rootActions.TaskAction.listSubTaskWithTaskId(this.explorer.taskId,function(json){
            var data = json.data;
            var _html = this.explorer.path+this.explorer.options.style+"/subTask.html"; 
            this.content.loadAll({"html":_html},{"bind": {"lp": this.lp,"data":data}, "module": this},function(){
                if(!(this.explorer.control.isEdit && this.explorer.control.isFounder && this.explorer.working)){ 
                    this.subTaskAdd.destroy();
                }
            }.bind(this));
        }.bind(this))

    },
    addSubTask:function(){ 
        MWF.xDesktop.requireApp("TeamWork", "MobileTasksCreate", function(){ 
            new MWF.xApplication.TeamWork.MobileTasksCreate(this.explorer.container,this.app,this.mobile,this.data);
        }.bind(this))
    },
    openTask:function(id){
        MWF.xDesktop.requireApp("TeamWork", "MobileTasks", function(){ 
            new MWF.xApplication.TeamWork.MobileTasks(this.explorer.container,this.app,this.mobile,{id:id});
        }.bind(this))
    }
})

//动态
MWF.xApplication.TeamWork.MobileTasks.Dynamic = new Class({
    initialize: function (content, explorer ) {
        this.content = content;
        this.explorer = explorer;
        this.lp = this.explorer.lp;
        this.app = this.explorer.app;
        this.mobile = this.explorer.mobile;
        this.data = this.explorer.data;
        this.rootActions = this.app.rootActions;
        this.taskId = this.explorer.taskId;
        this.load();
    },
    load:function(){
        const _this = this;
        this.content.empty();
        this.dynamicNode = new Element("div.tw-tasks-dy").inject(this.content);
        this.chatNode = new Element("div.tw-tasks-dy-chat").inject(this.content);

        this.dynamicNode.addEvents({
            scroll:function(){
                if(this.getScrollTop()==0 && !_this.listStatus){  
                    _this.loadDynamicListNext()
                }
            }
        });

        this.loadDynamicList()
        this.loadChat()
    },
    loadChat:function(){
        this.chatNodeLeft = new Element("div.tw-tasks-dy-chat-left").inject(this.chatNode);
        var chatNodeInputNode = new Element("div.tw-tasks-dy-chat-input").inject(this.chatNodeLeft);
        new Element("div.o2-tw-bianji").inject(chatNodeInputNode);
        this.chatInput = new Element("input").inject(chatNodeInputNode);
        this.chatNodeRight = new Element("div.tw-tasks-dy-chat-right").inject(this.chatNode);
        this.chatSend = new Element("div.tw-tasks-dy-chat-send",{text:this.lp.send}).inject(this.chatNodeRight);
        this.chatSend.addEvent('click',function(){
            var v = this.chatInput.get("value").trim();
            if(v == "") return;
            var data = {
                taskId : this.taskId,
                content : v
            };

            this.rootActions.ChatAction.create(data,function(json){
                if(json.data.id){
                    this.rootActions.ChatAction.get(json.data.id,function(json){
                        var person = json.data.sender;
                        var content = json.data.content;
                        var time = json.data.createTime;
                        content = content.split("\n").join("<br/>");

                        var _div = new Element("div").inject(this.dynamicNode);
                        var item = new Element("div.tw-tasks-dy-item").inject(_div)
                        new Element("div.tw-tasks-dy-item-icon.o2-tw-chat").inject(item);
                        var itemContent = new Element(".tw-tasks-dy-item-content").inject(item);
                        new Element("div.tw-tasks-dy-item-user",{text:person.split("@")[0]}).inject(itemContent);
                        new Element("div.tw-tasks-dy-item-value",{text:content}).inject(itemContent);
                        new Element("div.tw-tasks-dy-item-time",{text:time}).inject(itemContent);

                        this.dynamicNode.scrollTo(0,this.dynamicNode.getScrollSize().y);
                    }.bind(this))
                }
                this.chatInput.set("value","")
            }.bind(this))


        }.bind(this))
    },
    loadDynamicList:function(){
        
        this.page = 1;
        this.count = 10;
        this.htmlFile = this.explorer.path+this.explorer.options.style+"/dynamicList.html";
        this.filter = {
            "orderField":"createTime",
            "orderType":"DESC"
        };
        this.loadStatus = true;
        this.explorer.rootActions.DynamicAction.listNextWithTask(this.page,this.count,this.taskId,this.filter,function(json){
            var data = json.data; 
            this.total = json.count;
            data.reverse();
            this.dynamicNode.loadAll({"html":this.htmlFile},{"bind": {"data":data}, "module": this},function(){
                var _y = this.dynamicNode.getScrollSize().y;
                this.dynamicNode.scrollTo(0,_y);
                if(this.page * this.count > this.total) this.listStatus = true;
                else this.listStatus = false;

            }.bind(this));
        }.bind(this))
    },
    loadDynamicListNext:function(){
        this.page ++ ;
        this.loadStatus = true;
        this.explorer.rootActions.DynamicAction.listNextWithTask(this.page,this.count,this.taskId,this.filter,function(json){
            var data = json.data; 
            this.total = json.count;
            data.reverse();
            //var _y = this.dynamicNode.getScrollSize().y;
            this.dynamicNode.loadAll({"html":this.htmlFile},{"bind": {"data":data},"position":"afterBegin", "module": this},function(){
                this.dynamicNode.scrollTo(0,200);
                if(this.page * this.count > this.total) this.listStatus = true;
                else this.listStatus = false;
                
            }.bind(this));
        }.bind(this))
    }
})

//任务状态选择
MWF.xApplication.TeamWork.MobileTasks.StatusCheck = new Class({
    initialize: function (container, explorer ) {
        this.container = container;
        this.explorer = explorer;
        this.mobile = this.explorer.mobile;
        this.data = this.explorer.data;
        this.lp = this.explorer.lp;
        this.open();
    },
    open:function(){ 
        var _this = this;
        var _data = {
            property:"workStatus"
        };
        var _styles = {"background":"rgba(0,0,0,0.5)"};
        this.mobile.setMobileCover(_styles);
        this.popLayout = new Element("div.tw-common-pop").inject(this.container);

        const statusData = [
            { value: 'processing', text: this.explorer.app.lp.mobile.common.workStatus.processing, iconClass: 'o2-tw-jinxing' },
            { value: 'delay', text: this.explorer.app.lp.mobile.common.workStatus.delay, iconClass: 'o2-tw-gezhi' },
            { value: 'canceled', text: this.explorer.app.lp.mobile.common.workStatus.canceled, iconClass: 'o2-tw-quxiao' },
            { value: 'completed', text: this.explorer.app.lp.mobile.common.workStatus.completed, iconClass: 'o2-tw-wancheng' }
        ];

        const popLayout = this.popLayout;
        statusData.forEach((status) => {
            const statusItem = new Element('div.tw-common-pop-item').inject(popLayout);
            const title = new Element('div.tw-common-pop-item-title').inject(statusItem);
            new Element('div.' + status.iconClass).inject(title);
            new Element('div', { text: status.text }).inject(title);
            statusItem.setAttribute('data-status', status.value); // Set data-status attribute
            statusItem.addEvent('click',function(event){
                _data.mainValue = status.value;
                this.updateStatus(_data);
            }.bind(this))
        });

        const workStatus = this.data.workStatus;
        const currentItem = statusData.find((status) => status.value === workStatus);
        if (currentItem) {
            const currentStatusItem = popLayout.querySelector(`[data-status="${currentItem.value}"]`);
            if (currentStatusItem) {
                currentStatusItem.addClass('tw-common-pop-item-current');
                new Element('div.tw-common-pop-item-dagou.o2-tw-dagou').inject(currentStatusItem);
            }
        }
        
        this.mobile.mobileCoverNode.addEvent('click',function(){
            this.close()
        }.bind(this))
    },
    updateStatus:function(data){  
        if(this.data.workStatus == data.mainValue){
            this.close();
            return;
        }
        
        this.explorer.updateSingleProperty(data,function(){ 
            //this.explorer.data.workStatus = data.mainValue;
            //this.explorer.loadTaskStatus();
            this.explorer.reload();
            this.close();
        }.bind(this))
    },
    close:function(){
        this.popLayout.destroy();
        if(this.mobile.mobileCoverNode){
            this.mobile.mobileCoverNode.destroy();
        }
    }
})