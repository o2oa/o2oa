    MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
    MWF.xDesktop.requireApp("TeamWork", "Common", null, false);
    
    MWF.xApplication.TeamWork.MobileHome = new Class({
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
            this.lp = this.app.lp.mobile.mobileHome;
            
            this.rootActions = this.app.rootActions;
            this.actions = this.rootActions.ProjectAction;
            
            this.path = "../x_component_TeamWork/$MobileHome/";
            this.cssPath = this.path+this.options.style+"/css.wcss";
            this.stylePath = this.path + this.options.style + "/style.css";
            this.iconPath = this.path + this.options.style + "/";
            this._loadCss();
            o2.loadCss(this.stylePath,{url:true});

            this.mobile.setRouter("home","task");
            this.load();
        },
        load: function () { 
            this.container.empty();
            this.pageSize = 20;
            this.content = new Element("div.tw-home-content").inject(this.container);
            this.bottom = new Element("div.tw-home-bottom").inject(this.container);
            this.loadContent();
            this.loadBottom();
        },
        reload:function(){
            this.load();
        },
        loadContent:function(){
            var _self = this;
            this.searchLayout = new Element("div.tw-home-search").inject(this.content);
            this.tabLayout = new Element("div.tw-home-tab").inject(this.content);
            this.listContent = new Element("div.tw-home-list").inject(this.content);

            this.listContent.addEvents({
                scroll:function(){
                    var clientHeight = this.clientHeight;
                    var scrollTop = this.scrollTop;
                    var scrollHeight = this.scrollHeight;
       
                    if (clientHeight + scrollTop >= (scrollHeight - 10) && !_self.listStatus) { 
                        if(_self.curTab == "task"){
                            _self.loadTaskListNext()
                        }else{
                            _self.loadProjectListNext()
                        }
                    }
                }
            })
            
            this.loadSearchLayout();
            this.loadTabLayout();

            this.taskTabNode.click();
            //this.projectTabNode.click();
        },
        loadSearchLayout:function(){
            var left = new Element('div.tw-home-search-left').inject(this.searchLayout);
            new Element('div.o2-tw-sousuo').inject(left);
            this.searchKey = new Element("input").inject(left);
            var right = new Element('div.tw-home-search-right',{text:this.lp.search}).inject(this.searchLayout);
            right.addEvent("click",function(){ 
                var key =  this.searchKey.get('value');
                if(key == "") return;
                this.searchFilter(key);
            }.bind(this))
        },
        searchFilter:function(key){
            this.searchItem = {};
            this.searchItem.title = key;
            if(this.curTab == "task"){
                
                this.loadTaskList();
            }else{
                this.loadProjectList();
            }
        },
        searchEmpty:function(){
            this.searchItem = {};
            this.searchKey.set('value','')
        },
        loadTabLayout:function(){
            var left = new Element('div.tw-home-tab-left').inject(this.tabLayout);
            this.taskTabNode = new Element('div.tw-home-tab-item',{text:this.lp.task}).inject(left);
            this.projectTabNode = new Element('div.tw-home-tab-item',{text:this.lp.project}).inject(left);
            var right = new Element('div.tw-home-tab-right').inject(this.tabLayout);
            new Element("div.tw-home-tab-right-text",{text:this.lp.filter}).inject(right);
            new Element("div.tw-home-tab-right-icon.o2-tw-shaixuan").inject(right);
            this.filterLayout = new Element('div.tw-home-tab-filter',{tabindex:-1}).inject(right);
            this.filterLayout.addEvents({
                "focusout":function(e){ 
                    //return; 
                    var event = e.event||e
                    this.filterLayoutHide(event);
                    
                }.bind(this)
            });
            right.addEvent('click',function(){
                if(this.expandFilterLayout) return;
                this.loadFilterLayout();
            }.bind(this))

            this.taskTabNode.addEvent('click',function(){ 
                this.taskTabNode.removeClass('tw-home-tab-item-current').addClass('tw-home-tab-item-current');
                this.projectTabNode.removeClass('tw-home-tab-item-current');
                
                this.searchEmpty();
                this.curTab = "task";
                this.loadTaskList('my');

            }.bind(this));
            this.projectTabNode.addEvent('click',function(){
                this.projectTabNode.removeClass('tw-home-tab-item-current').addClass('tw-home-tab-item-current');
                this.taskTabNode.removeClass('tw-home-tab-item-current');

                this.searchEmpty();
                this.curTab = "project";
                this.loadProjectList();


            }.bind(this));
        },
        filterLayoutHide:function(e){
            if (this.filterLayout.contains(e.relatedTarget)) return;
            this.expandFilterLayout = false;
            if(this.maskCover) this.maskCover.destroy();
            this.filterLayout.setStyles({"height":"0px"});
        },
        openFilter:function(type,e){
            this.searchEmpty();
            this.filterLayoutHide(e);
            if(this.curTab == "task"){
                this.loadTaskList(type);
            }else{
                this.loadProjectList(type)
            }
            e.stopPropagation();
        },
        loadBottom:function(){
            this.home = new Element("div.tw-home-bottom-item.tw-home-bottom-item-current").inject(this.bottom);
            new Element("div.o2-tw-shouye").inject(this.home);
            new Element("div",{text:this.lp.home}).inject(this.home);
            this.home.addEvent('click',function(){
                //this.reload();
            }.bind(this))
            this.addProject = new Element("div.tw-home-bottom-item").inject(this.bottom);
            new Element("div.o2-tw-xinzeng").inject(this.addProject);
            new Element("div",{text:this.lp.addProject}).inject(this.addProject);
            this.addProject.addEvent('click',function(){
                MWF.xDesktop.requireApp("TeamWork", "MobileProjectCreate", function(){ 
                    new MWF.xApplication.TeamWork.MobileProjectCreate(this.container,this.app,this.mobile);
                }.bind(this))
            }.bind(this))
        },
        loadFilterLayout:function(){
            this.expandFilterLayout = true;
            this.loadMaskCover();
            this.filterLayout.focus();
            var type = this.curTab;
            this.filterLayout.setStyles({"height":"250px"});
            this.filterLayout.empty();
            var _html = this.path+this.options.style+"/filterList.html";
            if(type == "task"){ 
                var data = {
                    "type":this.type,
                    "data":[
                        {name:this.lp.tasks.all,"value":"all"},
                        {name:this.lp.tasks.my,"value":"my"},
                        {name:this.lp.tasks.flow,"value":"flow"},
                        {name:this.lp.tasks.delay,"value":"delay"},
                        {name:this.lp.tasks.canceled,"value":"canceled"},
                        {name:this.lp.tasks.completed,"value":"completed"},
                    ]
                }
                //this.filterLayout.loadAll({"html":_html},{"bind": {"data":data}, "module": this});
            }else{
                var data = {
                    "type":this.type,
                    "data":[
                        {name:this.lp.projects.all,"value":"all"},
                        {name:this.lp.projects.star,"value":"star"},
                        {name:this.lp.projects.my,"value":"my"},
                        {name:this.lp.projects.delay,"value":"delay"},
                        {name:this.lp.projects.completed,"value":"completed"},
                        {name:this.lp.projects.archived,"value":"archived"},
                    ]
                } 
            }
            this.filterLayout.loadAll({"html":_html},{"bind": {"data":data}, "module": this});
        },
        loadMaskCover:function(node){
            this.maskCover = new Element("div.tw-home-list-cover").inject(this.content);
        },

        loadTaskList:function(type){ 

            this.filterData = {};
            if(this.searchItem && this.searchItem.title && this.searchItem.title!='' ){
                this.filterData.title = this.searchItem.title
            }

            const typeActions = {
                'all': { actionType: 'listPageWithFilter' },
                'my': { actionType: 'listMyTaskPaging' },
                'flow': { actionType: 'listPageWithFilter', workStatus: 'processing' },
                'delay': { actionType: 'listPageWithFilter', workStatus: 'delay' },
                'canceled': { actionType: 'listPageWithFilter', workStatus: 'canceled' },
                'completed': { actionType: 'listPageWithFilter', workStatus: 'completed' },
            };
            
            this.type = type || "all";
            this.actionType = typeActions[this.type].actionType || 'listPageWithFilter';
            this.filterData.workStatus = typeActions[this.type].workStatus || null;
            

            /* this.type = type||"all";
            this.actionType = 'listPageWithFilter';

            if(this.type == 'my'){
                this.actionType = 'listMyTaskPaging'
            }else if(this.type == 'flow'){
                this.filterData.workStatus = 'processing';
            }else if(this.type == 'delay'){
                this.filterData.workStatus = 'delay'
            }else if(this.type == 'canceled'){
                this.filterData.workStatus = 'canceled'
            }else if(this.type == 'completed'){
                this.filterData.workStatus = 'completed'
            } */

            this.listContent.empty();
            this.curPage = 1;
            // this.pageSize = 50;
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
            this.loadingNode = new Element("div.tw-home-loading",{text:this.mobile.lp.common.loading + "..."}).inject(this.listContent);
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

        loadProjectList:function(type){

            this.type = type||"all";
            this.actionType = 'listPageWithFilter';

            this.filterData = {"statusList":[this.type]};
            //this.filterData.statusList = [this.type]
            
            switch (this.type) {
                case 'all':
                  this.filterData = {};
                  break;
                case 'star':
                  this.actionType = "listStarNextWithFilter";
                  this.filterData = {};
                  break;
                case 'my':
                  this.actionType = "listMyNextWithFilter";
                  this.filterData = {};
                  break;
                case 'delay':
                  
                  break;
                case 'completed':
                  
                  break;
                case 'archived':
                  
                  break;
            }

            if(this.searchItem && this.searchItem.title && this.searchItem.title!='' ){
                this.filterData.title = this.searchItem.title
            }


            this.curPage = 1;
            this.listContent.empty();

            this.rootActions.ProjectAction[this.actionType](this.curPage,this.pageSize,this.filterData,function(json){
                this.pageTotal = json.count; 
                if(this.pageTotal == 0){
                    this.setEmptyNode(this.listContent);
                    return;
                }
                var data = json.data;
                
                var _html = this.path+this.options.style+"/projectList.html";
                this.listContent.loadAll({"html":_html},{"bind": {"data":data,"lp": this.lp}, "module": this});

                if(this.curPage * this.pageSize > this.pageTotal) this.listStatus = true;
                else this.listStatus = false;
            }.bind(this))
        },
        loadProjectListNext:function(){
            this.loadingNode = new Element("div.tw-home-loading",{text:this.mobile.lp.common.loading + "..."}).inject(this.listContent);
            this.listStatus = true;
            this.curPage ++;
            this.rootActions.ProjectAction[this.actionType](this.curPage,this.pageSize,this.filterData,function(json){
                this.loadingNode.destroy();
                this.pageTotal = json.count; 
                var data = json.data;

                var _html = this.path+this.options.style+"/projectList.html";
                this.listContent.loadAll({"html":_html},{"bind": {"data":data,"lp": this.lp}, "module": this});

                if(this.curPage * this.pageSize > this.pageTotal) this.listStatus = true;
                else this.listStatus = false;
            }.bind(this))
        },
        setEmptyNode:function(node){
            new Element("div.tw-home-list-empty",{text:this.mobile.lp.common.noData}).inject(node)
        },
        openProject:function(id){
            MWF.xDesktop.requireApp("TeamWork", "MobileProject", function(){ 
                new MWF.xApplication.TeamWork.MobileProject(this.container,this.app,this.mobile,{id:id});
            }.bind(this))
        },
        openTask:function(id){
            MWF.xDesktop.requireApp("TeamWork", "MobileTasks", function(){ 
                new MWF.xApplication.TeamWork.MobileTasks(this.container,this.app,this.mobile,{id:id});
            }.bind(this))
        }
    
    });
