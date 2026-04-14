    MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
    MWF.xDesktop.requireApp("TeamWork", "Common", null, false);
    
    MWF.xApplication.TeamWork.MobileProject = new Class({
        Extends: MWF.widget.Common,
        Implements: [Options, Events],
        options: {
            "style": "default"
        },
        initialize: function (container, app, mobile, options ) {
            this.setOptions(options);
            this.container = container;
    
            this.app = app;
            this.mobile = mobile;
            this.lp = this.app.lp.mobile.project;
            this.rootActions = this.app.rootActions;
            this.path = "../x_component_TeamWork/$MobileProject/";

            this.projectId = this.options.id;
            this.mobile.setRouter("project","",this.projectId);
            this.load();
        },
        historyBack:function(){
            this.mobile.back();
        },
        load: function () {
            this.container.empty();
            
            this.rootActions.ProjectAction.get(this.projectId,function(json){
                this.data = json.data;
                this.loadTop();
                this.content = new Element("div.tw-project-content").inject(this.container);
                this.loadContent();
            }.bind(this))
            
        },
        
        loadContent:function(){
            
            var _self = this;
            this.searchLayout = new Element("div.tw-home-search").inject(this.content);
            this.tabLayout = new Element("div.tw-project-tab").inject(this.content);
            this.listContent = new Element("div.tw-project-list").inject(this.content);

            this.listContent.addEvents({
                scroll:function(){
                    var clientHeight = this.clientHeight;
                    var scrollTop = this.scrollTop;
                    var scrollHeight = this.scrollHeight;
                    if (clientHeight + scrollTop >= (scrollHeight - 10) && !_self.listStatus) { 
                        _self.loadTaskListNext();
                    }
                }
            })
            
            this.loadSearchLayout();
            this.loadTabLayout();
            this.loadAddTask();

            //this.projectTabNode.click();
        },
        loadTop:function(){
            var node = new Element("div.tw-common-top").inject(this.container);
            var back = new Element("div.tw-common-top-back.o2-tw-jiantou-you").inject(node);
            new Element("div.tw-common-top-title",{text:this.data.title||""}).inject(node);
            var right = new Element("div.tw-common-top-right").inject(node);
            var ps = new Element("div.tw-common-top-icon.o2-tw-gengduo").inject(right);

            back.addEvent('click',function(){
                this.historyBack()
            }.bind(this))

            ps.addEvent('click',function(){
                MWF.xDesktop.requireApp("TeamWork", "MobileProjectSetting", function(){ 
                    new MWF.xApplication.TeamWork.MobileProjectSetting(this.container,this.app,this.mobile,{id:this.projectId});
                }.bind(this))
            }.bind(this))
        },
        loadSearchLayout:function(){
            var left = new Element('div.tw-home-search-left').inject(this.searchLayout);
            new Element('div.o2-tw-sousuo').inject(left);
            this.searchKey = new Element("input").inject(left);
            var right = new Element('div.tw-home-search-right',{text:this.lp.search}).inject(this.searchLayout);
            right.addEvent("click",function(){
                var key =  this.searchKey.get('value');
                if(key == "") return;
                this.searchFilter(key)
            }.bind(this))
        },
        searchFilter:function(key){
            this.searchItem = {};
            this.searchItem.title = key;
            this.curTab = "all"; 
            this.filterData = {
                project:this.projectId,
                orderField:"serialNumber",
                orderType:"ASC"
            }
            this.tabLayout.getElements('.tw-project-tab-item').removeClass('tw-project-tab-item-current');
            this.tabLayout.getElement('.tw-project-tab-item').addClass('tw-project-tab-item-current');
            this.loadTaskList();
        },
        setSearchEmpty:function(){
            this.searchItem = {};
            this.filterData = {
                project:this.projectId,
                orderField:"serialNumber",
                orderType:"ASC"
            }
            this.searchKey.set("value",'');
        },
        loadTabLayout:function(){ 
            var _html = this.path+this.options.style+"/tabList.html";
            var data = {
                "type":this.cutTab||"all",
                "data":[
                    {name:this.lp.all,"value":"all"},
                    {name:this.lp.my,"value":"my"},
                    {name:this.lp.flow,"value":"flow"},
                    {name:this.lp.delay,"value":"delay"},
                    {name:this.lp.canceled,"value":"canceled"},
                    {name:this.lp.completed,"value":"completed"},
                ]
            }
            this.tabLayout.loadAll({"html":_html},{"bind": {"data":data}, "module": this},function(){
                this.tabLayout.getElement('.tw-project-tab-item').click();
            }.bind(this));

        },
        openTab:function(curTab,event){
            this.curTab = curTab || "all";
            var target = event.currentTarget;
            this.tabLayout.getElements('.tw-project-tab-item').removeClass('tw-project-tab-item-current');
            target.addClass('tw-project-tab-item-current');
            
            this.setSearchEmpty();
            
            this.loadTaskList()
        },
        loadTaskList:function(){
            if(this.searchItem && this.searchItem.title && this.searchItem.title!='' ){
                this.filterData.title = this.searchItem.title
            }

            const tabActions = {
                'my': { actionType: 'listMyTaskPaging' },
                'flow': { actionType: 'listPageWithFilter', workStatus: 'processing' },
                'delay': { actionType: 'listPageWithFilter', workStatus: 'delay' },
                'canceled': { actionType: 'listPageWithFilter', workStatus: 'canceled' },
                'completed': { actionType: 'listPageWithFilter', workStatus: 'completed' }
            };

            const actionInfo = tabActions[this.curTab] || { actionType: 'listPageWithFilter' };
            this.actionType = actionInfo.actionType;
            this.filterData.workStatus = actionInfo.workStatus || null;

            /* this.actionType = 'listPageWithFilter';
            if(this.curTab == 'my'){
                this.actionType = 'listMyTaskPaging'
            }else if(this.curTab == 'flow'){
                this.filterData.workStatus = 'processing';
            }else if(this.curTab == 'delay'){
                this.filterData.workStatus = 'delay'
            }else if(this.curTab == 'canceled'){
                this.filterData.workStatus = 'canceled'
            }else if(this.curTab == 'completed'){
                this.filterData.workStatus = 'completed'
            } */
            
            this.listContent.empty();
            this.curPage = 1;
            this.pageSize = 20;
            this.rootActions.TaskAction[this.actionType](this.curPage,this.pageSize,this.filterData,function(json){ 
                this.pageTotal = json.count; 
                if(this.pageTotal == 0){
                    this.setEmptyNode(this.listContent);
                    return;
                }
                var data = json.data;
                
                var _html = this.path+this.options.style+"/taskList.html";
                this.listContent.loadAll({"html":_html},{"bind": {"data":data,"lp": this.lp}, "module": this});

                if(this.curPage * this.pageSize > this.pageTotal) this.listStatus = true;
                else this.listStatus = false;
            }.bind(this))

        },
        loadTaskListNext:function(){
            this.loadingNode = new Element("div.tw-home-loading",{text:this.app.lp.mobile.common.loading + "..."}).inject(this.listContent);
            this.listStatus = true;
            this.curPage ++;
            this.rootActions.TaskAction[this.actionType](this.curPage,this.pageSize,this.filterData,function(json){
                this.loadingNode.destroy();
                this.pageTotal = json.count; 
                var data = json.data;

                var _html = this.path+this.options.style+"/taskList.html";
                this.listContent.loadAll({"html":_html},{"bind": {"data":data,"lp": this.lp}, "module": this});

                if(this.curPage * this.pageSize > this.pageTotal) this.listStatus = true;
                else this.listStatus = false;
            }.bind(this))
        },
        loadAddTask:function(){
            var addTaskLayout = new Element("div.tw-project-add-task.o2-tw-xinzeng").inject(this.container);
            addTaskLayout.addEvent('click',function(){
                this.maskCover = new Element("div.tw-project-cover").inject(this.container);
                var newTaskLayout = new Element("div.tw-common-pop.tw-project-newtask").inject(this.container)
                var newTaskName = new Element("input",{placeHolder:"任务名称"}).inject(newTaskLayout);
                var newTaskAction = new Element("div.tw-project-newtask-action").inject(newTaskLayout);

                var confirm = new Element("div.tw-common-bottom-item-ok.tw-common-bottom-item-two",{text:this.app.lp.mobile.common.ok}).inject(newTaskAction);
                var close = new Element("div.tw-common-bottom-item-close.tw-common-bottom-item-two",{text:this.app.lp.mobile.common.cancel}).inject(newTaskAction);

                confirm.addEvent('click',function(){
                    var name = newTaskName.get("value");
                    if(name =="") return;
                   this.rootActions.TaskAction.statiticMyProject(this.projectId,function(json){
                        if(json.data.groups && json.data.groups.length>0){
                            var taskGroupId = json.data.groups[0].id;
                            var data = {
                                "name":name,
                                "important":"高",
                                "urgency":"高",
                                "taskGroupId":taskGroupId
                           }
                           this.mobile.setMobileCover();
                           this.rootActions.TaskAction.save(data,function(d){
                                this.mobile.clearMobileCover();
                                var taskId = d.data.id;
                                MWF.xDesktop.requireApp("TeamWork", "MobileTasks", function(){ 
                                    new MWF.xApplication.TeamWork.MobileTasks(this.container,this.app,this.mobile,{id:taskId});
                                }.bind(this))
                           }.bind(this))

                        }
                   }.bind(this))
                }.bind(this));

                close.addEvent('click',function(){
                    newTaskLayout.destroy();
                    this.maskCover.destroy();
                }.bind(this))

                
            }.bind(this))
        },
        openTask:function(id){
            MWF.xDesktop.requireApp("TeamWork", "MobileTasks", function(){ 
                new MWF.xApplication.TeamWork.MobileTasks(this.container,this.app,this.mobile,{id:id});
            }.bind(this))
        },
        setEmptyNode:function(node){
            new Element("div.tw-home-list-empty",{text:this.app.lp.mobile.common.noData}).inject(node);
        },

    
    });
