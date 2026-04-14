/*
    
 */

MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.ProjectListV2 = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
		"mvcStyle": "style.css"

    },
    initialize: function (container, explorer, options) {
        this.setOptions(options);
        this.container = container;
        this.explorer = explorer;
        this.app = explorer;

        this.lp = this.app.lp;
        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.ProjectAction;
        //this.taskActions = this.rootActions.TaskAction;

        this.path = "../x_component_TeamWork/$ProjectListV2/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this.cssFile = this.path + this.options.style + '/style.css'
        
        this._loadCss();

        this.type = this.options.type;  
    },
    load: function () {
        var _self = this;
        this.container.empty();

        this.listType = "block";

        var name = this.lp.projectAll;
        this._actionName = 'listPageWithFilter';
        this._filterData = {"statusList":[this.type]};
        if(this.type == 'all'){
            this._filterData = {};
        }
        else if(this.type == 'star') {
            this._actionName = 'listStarNextWithFilter';
            name = this.lp.projectStar;
            this._filterData = {};
        }
        else if(this.type == 'my'){
            this._actionName = 'listMyNextWithFilter';
            name = this.lp.projectMy;
            this._filterData = {};
        } 
        else if(this.type == 'delay') {
            name = this.lp.projectDelay;
        }
        else if(this.type == 'completed') {
            name = this.lp.projectCompleted
        }
        else if(this.type == 'archived') {
            name = this.lp.projectArchived
        }
        
        this.layout = new Element("div",{styles:this.css.layout}).inject(this.container);

        this.titleLayout = new Element("div.titleLayout",{styles:this.css.titleLayout}).inject(this.layout);
        this.titleName = new Element("div.titleName",{styles:this.css.titleName}).inject(this.titleLayout);
        new Element('div',{text:name}).inject(this.titleName);
        this.titleCount = new Element('div',{styles:this.css.titleCount,text:'(-)'}).inject(this.titleName);

        this.titleAction = new Element("div.titleAction",{styles:this.css.titleAction}).inject(this.titleLayout);
        this.titleActionExport = new Element("div.titleActionExport",{styles:this.css.titleActionExport,title:this.lp.ProjectList.content.export}).inject(this.titleAction);
        this.titleActionExport.addEvents({
            mouseover:function(){
                this.setStyles({ "background-image":"url(../x_component_TeamWork/$ProjectListV2/default/icon/icon_export_click.png)" })
            },
            mouseout:function(){
                this.setStyles({ "background-image":"url(../x_component_TeamWork/$ProjectListV2/default/icon/icon_export.png)" })
            },
            click:function(){
                _self.export()
            }
        })

        this.titleActionList = new Element("div.titleActionList",{styles:this.css.titleActionList,title:this.lp.ProjectList.content.listTip}).inject(this.titleAction);
        this.titleActionList.addEvents({
            mouseover:function(){
                if(_self.listType == 'list') return;
                this.setStyles({ "background-image":"url(../x_component_TeamWork/$ProjectListV2/default/icon/icon_liebiao_click.png)" })
            },
            mouseout:function(){
                if(_self.listType == 'list') return;
                this.setStyles({ "background-image":"url(../x_component_TeamWork/$ProjectListV2/default/icon/icon_liebiao.png)" })
            },
            click:function(){
                this.setStyles({ "background-image":"url(../x_component_TeamWork/$ProjectListV2/default/icon/icon_liebiao_click.png)" })
                _self.titleActionBlock.setStyles({ "background-image":"url(../x_component_TeamWork/$ProjectListV2/default/icon/icon_tubiao.png)" })
                _self.loadProjectList('list');
            }
        });
        this.titleActionBlock = new Element("div.titleActionBlock",{styles:this.css.titleActionBlock,title:this.lp.ProjectList.content.blockTip}).inject(this.titleAction);
        this.titleActionBlock.addEvents({
            mouseover:function(){
                if(_self.listType == 'block') return;
                this.setStyles({ "background-image":"url(../x_component_TeamWork/$ProjectListV2/default/icon/icon_tubiao_click.png)" })
            },
            mouseout:function(){
                if(_self.listType == 'block') return;
                this.setStyles({ "background-image":"url(../x_component_TeamWork/$ProjectListV2/default/icon/icon_tubiao.png)" })
            },
            click:function(){
                this.setStyles({ "background-image":"url(../x_component_TeamWork/$ProjectListV2/default/icon/icon_tubiao_click.png)" })
                _self.titleActionList.setStyles({ "background-image":"url(../x_component_TeamWork/$ProjectListV2/default/icon/icon_liebiao.png)" })
                _self.loadProjectList('block');
            }
        })


        this.listLayout = new Element("div",{styles:this.css.listLayout}).inject(this.layout);
        
        this.listLayout.addEvents({
            scroll:function(){
                var clientHeight = this.clientHeight;
                var scrollTop = this.scrollTop;
                var scrollHeight = this.scrollHeight;

                if (clientHeight + scrollTop >= (scrollHeight - 10) && !_self.listStatus) { 
                    _self.loadProjectListNext()
                }

            }
        })

        this.titleActionBlock.click();
        //this.titleActionList.click();
		        
    },
    loadProjectList:function(t){ 
        this.listType = t;
        this.listLayout.empty();
        this.curPage = 1;
        this.pageSize = 100;
        this.actions[this._actionName](this.curPage,this.pageSize,this._filterData,function(json){
            var data = json.data;
            this.pageTotal = json.count; //总数
            this.titleCount.set("text","(" + json.count + ")");

            var _html = this.path+this.options.style+"/block.html";
            if(this.listType == 'list') _html = this.path+this.options.style+"/list.html";
            this.listLayout.loadAll({"html":_html,"css":this.cssFile},{"bind":{"lp":this.lp,"data":data},"module":this},function(){
                if(this.listType == "block"){
                    var allItems = this.listLayout.getElements(".blockItem");
                    allItems.forEach(function(item){
                        item.getParent().setStyles({"float":"left"})
                    })
                }
                if(this.curPage * this.pageSize > this.pageTotal) this.listStatus = true;
                else this.listStatus = false;
            }.bind(this))

        }.bind(this))
    },
    loadProjectListNext:function(){
        if(this.addProject) this.addProject.destroy();
        this.listStatus = true;
        this.curPage ++;
        this.actions[this._actionName](this.curPage,this.pageSize,this._filterData,function(json){
            var data = json.data;
            
            var _html = this.path+this.options.style+"/block.html";
            if(this.listType == 'list') _html = this.path+this.options.style+"/list.html";
            this.listLayout.loadAll({"html":_html,"css":this.cssFile},{"bind":{"lp":this.lp,"data":data},"module":this},function(){
                if(this.listType == "block"){
                    var allItems = this.listLayout.getElements(".blockItem");
                    allItems.forEach(function(item){
                        item.getParent().setStyles({"float":"left"})
                    })
                }
                if(this.curPage * this.pageSize > this.pageTotal) this.listStatus = true;
                else this.listStatus = false;
            }.bind(this))

        }.bind(this))
    },
    createProject:function(e){
        //this.loadProjectListNext()
        this.explorer.createProject(e)
    },
    loadBgImage:function(icon,e){
        if(!icon) return;
        var _target = e.currentTarget;
        
        // _target.setStyles({
        //     "background-image":"url('"+MWF.xDesktop.getImageSrc( icon )+"')"
        // });
        _target.getElement('.blockItemImg').set('src',MWF.xDesktop.getImageSrc( icon ))
    },
    //设置星标
    setStar:function(id,e){
        var _target = e.currentTarget;
        var status = _target.get("status");
        if(status == 'star'){
            this.actions.unStar(id,function(d){
                _target.set("status","unstar");
                _target.set('src','../x_component_TeamWork/$ProjectListV2/default/icon/icon_wdxx_2.png');
                //刷新导航数量
                this.explorer.loadCount();
            }.bind(this))
        }else if(status == 'unstar'){
            this.actions.star(id,function(d){
                _target.set("status","star");
                _target.set('src','../x_component_TeamWork/$ProjectListV2/default/icon/icon_wdxx_click.png');
                //刷新导航数量
                this.explorer.loadCount();
            }.bind(this))
        }
        e.stopPropagation()
    },
    openSetting:function(id,e){
        var _self = this;
        var d = {id:id};
        MWF.xDesktop.requireApp("TeamWork", "ProjectSetting", function(){
            var ps = new MWF.xApplication.TeamWork.ProjectSetting(_self,d,
                {"width": "800","height": "80%",
                    onPostOpen:function(){
                        ps.formAreaNode.setStyles({"top":"10px"});
                        var fx = new Fx.Tween(ps.formAreaNode,{duration:200});
                        fx.start(["top"] ,"10px", "100px");
                    },
                    onPostClose:function(json){
                        if(json)_self.openItem({type:_self.currentNavi});
                    }
                },{
                    container : _self.app.content,
                    lp : _self.lp.projectSetting,
                    css:{}
                }
            );
            ps.open();
        });

        e.stopPropagation();
    },
    openProject:function(id){
        var d = {
            id:id
        }
        MWF.xDesktop.requireApp("TeamWork", "Project", function(){
            var p = new MWF.xApplication.TeamWork.Project(this.app.content,this.app,d,{

                }
            );
            p.load();
        }.bind(this));
    },
    export:function(){ 
        var filter = {};
        if(this.type == 'all'){
            
        }
        else if(this.type == 'star') {
            filter.isStar = true
        }
        else if(this.type == 'my'){ 
            filter.executor = this.app.distinguishedName
        } 
        else if(this.type == 'delay') {
            filter.statusList = ['delay']
        }
        else if(this.type == 'completed') {
            filter.statusList = ['completed']
        }
        else if(this.type == 'archived') {
            filter.statusList = ['archived']
        }

        this.actions.exportWithFilter(filter,function(json){
            var id = json.data.id;
            //debugger
            var uri = this.actions.action.actions.exportResult.uri;
            uri = uri.replace("{flag}", id);
            uri = o2.filterUrl( this.actions.action.address + uri );
            var a = new Element("a", {"href": uri, "target":"_blank"});
            a.click();
            a.destroy();
        }.bind(this))
    }
    
});

