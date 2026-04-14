MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.MobileProjectSetting = new Class({
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
        this.lp = this.app.lp.mobile.projectSetting;
        this.rootActions = this.app.rootActions;
        this.path = "../x_component_TeamWork/$MobileProjectSetting/";
        
        this.projectId = this.options.id;
        this.curTab = this.options.tab || "base";
        
        this.mobile.setRouter("projectSetting",this.curTab,this.projectId);
        this.load();
    },

    historyBack:function(){
        this.mobile.back();
    },
    load: function () {
        this.container.empty();
        this.rootActions.ProjectAction.get(this.projectId,function(json){
            this.data = json.data; 
            this.getControl();
            this.loadTop();
            this.content = new Element("div.tw-ps-content").inject(this.container);
            this.loadContent();
        }.bind(this))
    },
    reload:function(){
        this.load();
    },
    getControl:function(){
        this.control = {};
        this.control.isEdit = this.data.control.edit; 
        this.control.isSysAdmin = this.data.control.sysAdmin;
    },
    
    loadContent:function(){
        var _self = this;
        this.tabLayout = new Element("div.tw-ps-tab").inject(this.content);
        this.taskContentLayout = new Element("div.tw-ps-layout").inject(this.content);

        this.loadTabLayout();
    },
    loadTop:function(){ 
        var node = new Element("div.tw-common-top").inject(this.container);
        var back = new Element("div.tw-common-top-back.o2-tw-jiantou-you").inject(node);
        new Element("div.tw-common-top-title",{text:this.data.title||""}).inject(node);
        new Element("div.tw-common-top-right").inject(node);
        
        back.addEvent('click',function(){
            this.historyBack();
        }.bind(this))
    },
    
    loadTabLayout:function(){ 
        var _this = this;
        this.baseTab = new Element("div.tw-ps-tab-item",{text:this.lp.base}).inject(this.tabLayout);
        this.dynamicTab = new Element("div.tw-ps-tab-item",{text:this.lp.dynamic}).inject(this.tabLayout);
        this.statusTab = new Element("div.tw-ps-tab-item",{text:this.lp.status}).inject(this.tabLayout);

        this.baseTab.addEvent('click',function(){ 
            _this.curTab = "base";
            _this.setTabCurrent(this);
            _this.loadBaseLayout();
        })
        this.dynamicTab.addEvent('click',function(){ 
            _this.curTab = "dynamic";
            _this.setTabCurrent(this);
            _this.loadDynamicLayout();
        })
        this.statusTab.addEvent('click',function(){
            _this.curTab = "status";
            _this.setTabCurrent(this);
            _this.loadStatusLayout();
        })
                
        if(this.curTab == "base") this.baseTab.click();
        else if(this.curTab == "status") this.statusTab.click();
        else if(this.curTab == "dynamic") this.dynamicTab.click();
    },
    setTabCurrent:function(node){ 
        this.tabLayout.getElements('.tw-ps-tab-item').removeClass('tw-ps-tab-item-current');
        node.addClass('tw-ps-tab-item-current');

        //var _thisRouter = this.app.mobileRouterList[this.app.mobileRouterList.length - 1];
        var _thisRouter = this.mobile.router[this.mobile.router.length - 1];
        if(_thisRouter && _thisRouter.title == "projectSetting" && _thisRouter.id == this.projectId){
            _thisRouter.tab = this.curTab;
        }
        
    },
    loadBaseLayout:function(){ 
        this.taskContentLayout.empty();
        var _html = this.path+this.options.style+"/base.html"; 
        this.taskContentLayout.loadAll({"html":_html},{"bind": {"lp": this.lp,"data":this.data}, "module": this},function(){
            this.setProjectData();
        }.bind(this));
    },
    loadDynamicLayout:function(){
        new MWF.xApplication.TeamWork.MobileProjectSetting.Dynamic(this.taskContentLayout,this);
    },
    loadStatusLayout:function(){
        this.taskContentLayout.empty();
        var _html = this.path+this.options.style+"/status.html"; 
        this.taskContentLayout.loadAll({"html":_html},{"bind": {"data":this.data,"lp": this.lp}, "module": this},function(){
            var _status = this.data.workStatus;
            if(_status == "processing"){
                this.statusProcessing.setStyles({"background-color":"#4A90E2","color":"#ffffff"})
            }else if(_status == "delay"){
                this.statusDelay.setStyles({"background-color":"#8A8A8A","color":"#ffffff"})
            }else if(_status == "completed"){
                this.statusCompleted.setStyles({"background-color":"#69b439","color":"#ffffff"})
            }else if(_status == "archived"){
                this.statusArchived.setStyles({"background-color":"#129de6","color":"#ffffff"})
            }

            //1、如果是管理员全放开
            //2、如果是负责人
            //2.1 如果已归档，隐藏所有，显示已归档
            //2.2、如果未归档，显示所有，
            //3、普通权限，显示当前的状态
            if(this.control.isSysAdmin){
                
            }else if(this.control.isEdit){
                if(_status == "archived"){
                    this.statusProcessing.destroy();
                    this.statusDelay.destroy();
                    this.statusCompleted.destroy();
                    this.statusArchived.removeEvents("click");
                }
            }else{
                this.statusProcessing.destroy();
                this.statusDelay.destroy();
                this.statusCompleted.destroy();
                this.statusArchived.removeEvents("click");
            }
        }.bind(this));
    },
    setStatus:function(status){
        if((this.control.isSysAdmin || this.control.isEdit) && this.data.workStatus != status){
            if(this.data.workStatus != status){
                this.rootActions.ProjectAction.updateProjectStatus(this.projectId,{workStatus:status},function(){ 
                    this.loadStatusLayout()

                    this.rootActions.ProjectAction.get(this.projectId,function(json){
                        this.data = json.data; 
                        this.getControl()
                    }.bind(this))

                }.bind(this))
            }
        }
    },
    setProjectData:function(){
        var _this = this;
        
        //项目名称
        if(this.control.isEdit){
            this.projectName.removeAttribute("readonly");
        }

        //项目周期，结束时间
        if(this.control.isEdit){
            this.projectEndTime.addEvent('click',function(){
                var opt = { type:"date" };
                this.mobile.selectCalendar(this.projectEndTime,opt,function(json){
                    this.data.endTime = json.dateString;
                }.bind(this))
            }.bind(this))
        }

        //项目负责人
        if(this.projectExecutor){
            this.projectExecutor.empty();
            var _node = this.projectExecutor.getParent();
            this.mobile.setUserIcon(this.data.executor).inject(this.projectExecutor);
            
            if(this.control.isEdit){ 
                _node.addEvent('click',function(){ 
                    var idList = this.mobile.getIdentityByPerson([this.data.executor]);
                    this.mobile.selectPerson("identity",1,idList,function(personList){
                        if(personList.length>0){
                            var person = personList[0];
                            this.data.executor = person;
                            this.projectExecutor.empty();
                            this.mobile.setUserIcon(person).inject(this.projectExecutor);
                        }
                    }.bind(this))
                }.bind(this))
            }else{
                _node.getElement('.tw-ps-base-item-value-icon').destroy();
            }
        }
        
        //项目参与者
        if(this.projectParticipant){ 
            this.projectParticipant.empty();
            var _node = this.projectParticipant.getParent();
            this.data.participantList.each(function(d){
                this.mobile.setUserIcon(d).inject(this.projectParticipant);
            }.bind(this));

            if(this.control.isEdit){ 
                _node.addEvent('click',function(){ 
                    var idList = this.mobile.getIdentityByPerson(this.data.participantList);
                    this.mobile.selectPerson("identity",0,idList,function(personList){
                        this.data.participantList = personList;
                        this.projectParticipant.empty();
                        personList.each(function(d){
                            this.mobile.setUserIcon(d).inject(this.projectParticipant);
                        }.bind(this))
                    }.bind(this))
                }.bind(this))
            }else{
                _node.getElement('.tw-ps-base-item-value-icon').destroy();
            }
        }

        //项目来源
        if(this.control.isEdit){
            this.projectSource.removeAttribute("readonly")
        }
        //项目目标
        if(this.projectObjective){
            if(this.control.isEdit){
                this.projectObjective.setStyles({"height":(this.projectObjective.scrollHeight)+"px"});
                this.projectObjective.addEvent('input',function(){ 
                    var v = _this.projectObjective.get("value");
                    this.style.height = 'auto';
                    this.style.height = (this.scrollHeight) + 'px';
                });
            }else{
                var pnode = this.projectObjective.getParent();
                pnode.empty();
                pnode.set('text',this.data.objective);
            }
        }
        //项目背景及问题
        if(this.projectDescription){
            if(this.control.isEdit){
                this.projectDescription.setStyles({"height":(this.projectDescription.scrollHeight)+"px"});
                this.projectDescription.addEvent('input',function(){ 
                    var v = _this.projectDescription.get("value");
                    this.style.height = 'auto';
                    this.style.height = (this.scrollHeight) + 'px';
                });
            }else{
                var pnode = this.projectDescription.getParent();
                pnode.empty();
                pnode.set('text',this.data.description);
            }
        }

        //保存操作
        if(!this.control.isEdit){
            this.projectSave.destroy();
        }
    },
    save:function(){ 
        this.data.title = this.projectName.get('value').trim();
        this.data.source = this.projectSource.get('value').trim();
        this.data.objective = this.projectObjective.get('value').trim();
        this.data.description = this.projectDescription.get('value').trim();
        
        if(this.data.title.trim() == ""){
            this.app.notice(this.lp.nameNotEmpty,"error");
            return;
        }
        if(this.data.executor == ""){
            this.app.notice(this.lp.executorNotEmpty,"error");
            return;
        }
        this.rootActions.ProjectAction.save(this.data,function(json){
            if(json.type=='success'){
                this.app.notice(this.lp.modifySuccess);
                this.reload()
            }
        }.bind(this))
    },
   
    loadTaskStatus:function(){ 
        if(!this.tasksStatus) return;
        this.tasksStatus.set('text',this.lp.common.workStatus[this.data.workStatus]);
        if(this.control.isEdit){ 
            var _node = this.tasksStatus.getParent();
            _node.removeEvents('click').addEvent('click',function(){ 
                new MWF.xApplication.TeamWork.MobileTasks.StatusCheck(this.container,this);
            }.bind(this))
        }
    }

});

//项目动态
MWF.xApplication.TeamWork.MobileProjectSetting.Dynamic = new Class({
    initialize: function (content, explorer ) {
        this.content = content;
        this.explorer = explorer;
        this.app = this.explorer.app;
        this.lp = this.explorer.lp;
        this.mobile = this.explorer.mobile;
        this.data = this.explorer.data;
        this.rootActions = this.app.rootActions;
        this.load();
    },
    load:function(){
        var _this = this;
        this.content.empty();
        this.layout = new Element("div.tw-ps-dy").inject(this.content);
        this.layout.addEvents({
            scroll:function(){
                var clientHeight = this.clientHeight;
                var scrollTop = this.scrollTop;
                var scrollHeight = this.scrollHeight;
                if (clientHeight + scrollTop >= (scrollHeight - 10) && !_this.listStatus) { 
                    _this.loadDynamicListNext();
                }
            }
        })

        this.loadDynamicList();
    },
    loadDynamicList:function(){
        this.curPage = 1;
        this.pageSize = 10;
        this._html = this.explorer.path+this.explorer.options.style+"/dynamicList.html"; 
        this.rootActions.DynamicAction.listNextWithProject(this.curPage,this.pageSize,this.explorer.projectId,{},function(json){
            this.pageTotal = json.count; 
            if(this.pageTotal == 0){
                this.setEmptyNode(this.layout);
                return;
            }
            var data = json.data;
            
            this.layout.loadAll({"html":this._html},{"bind": {"data":data,"lp": this.lp}, "module": this});

            if(this.curPage * this.pageSize > this.pageTotal) this.listStatus = true;
            else this.listStatus = false;
            
        }.bind(this))
    },
    loadDynamicListNext:function(){
        this.curPage ++;
        this.rootActions.DynamicAction.listNextWithProject(this.curPage,this.pageSize,this.explorer.projectId,{},function(json){
            this.pageTotal = json.count; 
            if(this.pageTotal == 0){
                this.setEmptyNode(this.layout);
                return;
            }
            var data = json.data;
            this.layout.loadAll({"html":this._html},{"bind": {"data":data,"lp": this.lp}, "module": this});
            if(this.curPage * this.pageSize > this.pageTotal) this.listStatus = true;
            else this.listStatus = false;
            
        }.bind(this))
    }
})