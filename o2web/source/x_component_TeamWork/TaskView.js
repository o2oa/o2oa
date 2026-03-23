/*
    this.data //project的数据
 */

MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.TaskView = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
		"mvcStyle": "style.css"

    },
    initialize: function (container, explorer,  options) {
        this.setOptions(options);
        this.container = container;
        this.explorer = explorer;

        this.app = this.explorer.app || this.explorer;
        this.lp = this.app.lp.taskView;
        this.rootActions = this.app.rootActions;
        this.actions = this.rootActions.TaskAction;
        //this.taskActions = this.rootActions.TaskAction;

        this.path = "../x_component_TeamWork/$TaskView/";
        this.cssPath = this.path+this.options.style+"/css.wcss";
        this._loadCss();
        this.projectId = this.options.projectId||"";
        this.type = this.options.type;
        this.keyword = this.options.key;

    },
    load: function () {
        var _self = this;
        this.container.empty();
        var content = new Element("div",{styles:{
            "width":"100%",
            "height":"100%"
        }}).inject(this.container);

        this.actionType = 'listPageWithFilter';
        if(this.projectId == ""){
            this.filterData = {}
        }else{
            this.filterData = {
                project:this.projectId,
                orderField:"serialNumber",
                orderType:"ASC"
            }
        }
        
        if(this.type == 'all'){
            //this.actionType = 'listPageWithFilter';
        }else if(this.type == 'admin'){
            //this.actionType = 'listPageWithFilter';
            this.filterData.parentId = '0'
        }else if(this.type == 'my'){
            this.actionType = 'listMyTaskPaging'
            this.filterData.workStatus = 'processing'
        }else if(this.type == 'flow'){
            //this.actionType = 'listPageWithFilter';
            this.filterData.workStatus = 'processing';
        }else if(this.type == 'delay'){
            //this.actionType = 'listPageWithFilter';
            this.filterData.workStatus = 'delay'
        }else if(this.type == 'canceled'){
            //this.actionType = 'listPageWithFilter';
            this.filterData.workStatus = 'canceled'
        }else if(this.type == 'completed'){
            //this.actionType = 'listPageWithFilter';
            this.filterData.workStatus = 'completed'
        }
        
        var _html = this.path+this.options.style+"/view.html";
        this._css = this.path+this.options.style+"/style.css"; 
        content.loadAll({"html":_html,css:this._css},{"bind": {"lp": this.lp}, "module": this},function(){ 
            this.taskName.set("value",this.keyword);
            this.filterData.title = this.keyword; 

            if(this.type == 'my'){
                this.workStatus.set('text','进行中')
            }
            this.loadTaskList();

            this.taskViewContent.addEvents({
                scroll:function(){
                    var clientHeight = this.clientHeight;
                    var scrollTop = this.scrollTop;
                    var scrollHeight = this.scrollHeight;
       
                    if (clientHeight + scrollTop >= (scrollHeight - 10) && !_self.listStatus) { 
                        _self.loadTaskListNext()
                    }
                }
            })
            
        }.bind(this))

    },
    reload:function(){
        this.load()
    },
    loadTaskList:function(){
        this.curPage = 1;
        this.pageSize = 50;
        this.actions[this.actionType](this.curPage,this.pageSize,this.filterData,function(json){
            this.pageTotal = json.count; 
            var data = json.data;
            
            this.displayTaskList(data);
            //this.filterData.workStatus = "";

            if(this.curPage * this.pageSize > this.pageTotal) this.listStatus = true;
            else this.listStatus = false;
        }.bind(this))
    },
    loadTaskListNext:function(){ 
        this.listStatus = true;
        this.curPage ++;
        this.actions[this.actionType](this.curPage,this.pageSize,this.filterData,function(json){
            this.pageTotal = json.count; 
            var data = json.data;
            
            var _html = this.path + this.options.style+"/list_single.html";
            this._css = this.path + this.options.style+"/style.css"; 
            this.taskList_body.loadAll({"html":_html,css:this._css},{"bind": {"lp": this.lp,"data":data,"projectId":this.projectId}, "module": this},function(){ 
                if(this.curPage * this.pageSize > this.pageTotal) this.listStatus = true;
                else this.listStatus = false;
            }.bind(this))
        }.bind(this))

    },
    displayTaskList:function(data,callback){ 
        var _self = this;
        
        var _view_html = this.path+this.options.style+"/list.html";
        this.taskViewContent.empty();
        this.taskViewContent.loadAll({"html":_view_html,css:this._css},{"bind": {"lp": this.lp,"data":data,"projectId":this.projectId}, "module": this},function(){ 

        })

    },
    selectExecutor:function(e){
        var target = e.currentTarget;
        this.app.selectPerson("person",1,[],function(personList){
            if(personList.length > 0){
                var person = personList[0];
                target.set('text',person.split('@')[0]);
                this.filterData.executor = person;
                this.filterSearch();
            }
            // this.projectData.manageablePersonList = personList;
            // var _person = [];
            // this.projectData.manageablePersonList.forEach(function(p){
            //     _person.push(p.split("@")[0])
            // })
            // this.manageablePersonList.set("text",_person.join(','));

        }.bind(this))
    },
    loadSelect:function(el,e){
        var target = e.currentTarget;
        var value = target.get("text");
        var chosen = [];
        if(el == "workStatus"){
            chosen.push(this.lp.processing);
            chosen.push(this.lp.delay);
            chosen.push(this.lp.canceled);
            chosen.push(this.lp.completed);
        }else{
            chosen.push(this.app.lp.task.importantStatus.high);
            chosen.push(this.app.lp.task.importantStatus.low);
        }
                
        var select = new MWF.xApplication.TeamWork.TaskView.Select(this.container, target, this.app, {chosen:chosen}, {
            css:this.css, lp:this.lp, axis : "y",
            position : { //node 固定的位置
                x : "right",
                y : "middle"
            },
            nodeStyles : {
                "min-width":"160px",
                "padding":"2px",
                "border-radius":"5px",
                "box-shadow":"0px 0px 4px 0px #999999",
                "z-index" : "201"
            },
            onPostLoad:function(){
                select.node.setStyles({"opacity":"0","top":(select.node.getStyle("top").toInt()+4)+"px"});
                var fx = new Fx.Tween(select.node,{duration:400});
                fx.start(["opacity"] ,"0", "1");
            },
            onClose:function(rd){ 
                if(!rd) return;
                if(rd.value){
                    target.set("text",rd.value);
                    if(el == "workStatus"){ 
                        var _value = 'processing';
                        if(rd.value == this.lp.processing) _value = 'processing'
                        else if(rd.value == this.lp.delay) _value = 'delay'
                        else if(rd.value == this.lp.completed) _value = 'completed'
                        else if(rd.value == this.lp.canceled) _value = 'canceled'
                        this.filterData[el] = _value;
                        
                    }else{
                        this.filterData[el] = rd.value;
                    }
                    this.filterSearch();
                }
            }.bind(this)
        });
        select.load();
    },
    filterSearch:function(){
        this.filterData.title = this.taskName.get("value");
        this.actions[this.actionType](1,this.pageSize,this.filterData,function(json){
            var data = json.data;
            
            this.displayTaskList(data);
            
        }.bind(this))
    },
    filterReset:function(){
        if(this.taskName){
            this.taskName.set("value","");
            if(this.keyword) this.keyword=''
            //this.filterData.title = ''
        } 
        if(this.important){
            this.important.set("text","");
            this.filterData.important=''
        } 
        if(this.workStatus){
            this.workStatus.set("text","");
            this.filterData.workStatus=''
        } 
        if(this.urgency){
            this.urgency.set("text","");
            this.filterData.urgency=''
        } 
        this.reload();
    },
    filterExport:function(){
        this.filterData.title = this.taskName.get("value");
        var filter = this.filterData
        if(this.type == "my"){
            filter.justMy = true
        }
        
        this.actions.exportWithFilter(filter,function(json){
            var id = json.data.id;
            
            var uri = this.actions.action.actions.exportResult.uri;
            uri = uri.replace("{flag}", id);
            uri = o2.filterUrl( this.actions.action.address + uri );
            var a = new Element("a", {"href": uri, "target":"_blank"});
            a.click();
            a.destroy();

        }.bind(this))
    },


    openTask:function(id){ 
        var data = {
            taskId:id
        };
        var opt = {
            "onPostClose":function(){ 
                //刷新
            }.bind(this)
        };
        MWF.xDesktop.requireApp("TeamWork", "Tasks", function(){
            var task = new MWF.xApplication.TeamWork.Tasks(this,data,opt,{projectObj:this});
            task.open();
        }.bind(this));
    },
    reload:function(){ 
        this.container.empty();
        this.load();
    }
    
});


MWF.xApplication.TeamWork.TaskView.Select = new Class({
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
        this.rootActions = this.app.rootActions;
        
        var container = {
            "cursor":"pointer",
            "height":"40px",
            "width":"100%"
        };
        var text={
            "height":"25px","line-height":"25px","float":"left","text-align":"center",
            "margin-left":"6px","margin-top":"8px",
            "font-size":"13px","color":"#666666","border-radius":"2px","padding-left":"10px","padding-right":"10px"
        };
        var icon = {
            "float":"right","width":"24px","height":"24px",
            "margin-top":"6px","margin-right":"8px",
            "background":"url(../x_component_TeamWork/$Tasks/default/icon/icon_dagou.png) no-repeat center"
        };
        
        //if(!this.data.data.important) return;

        var _data = this.data.chosen; 
        
        _data.forEach(function(item){
            var vContainer = new Element("div",{styles:container}).inject(this.contentNode);
            value = new Element("div",{styles:text,text:item}).inject(vContainer);
            
            vContainer.addEvents({
                click:function(){
                    var d = {"value":item};
                    this.close(d)
                }.bind(this),
                mouseover:function(){this.setStyles({"background-color":"#f2f5f7"})},
                mouseout:function(){this.setStyles({"background-color":""})}
            });
        }.bind(this))
             

        if(callback)callback();
    }

});