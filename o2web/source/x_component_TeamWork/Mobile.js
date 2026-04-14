MWF.xApplication.TeamWork = MWF.xApplication.TeamWork || {};
MWF.xDesktop.requireApp("TeamWork", "Common", null, false);

MWF.xApplication.TeamWork.Mobile = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default"
    },
    initialize: function (container, app,options ) {
        this.setOptions(options);
        this.container = container;
        this.app = app;
        this.lp = this.app.lp.mobile;
        this.open();
    },
    open: function () {
        this.fitComponent();
        this.router = []; //路由链
        this.forward = true; //true 正向，false 反向
        
        window.addEventListener("popstate", function(e) { 
            if(this.reset) return;
            this.forward = false;
            this.render();
            this.forward = true;
        }.bind(this), false); 

        this.openHome();
    },
    render:function(){ 
        //历史记录返回跳转到上页
        this.router.pop(); //删除路由最后一条
        var thisRouter = this.router[this.router.length - 1]; 
        if(thisRouter){
            //document.getElementsByTagName('title')[0].innerText = curRouter.title;
        }else{
            //document.getElementsByTagName('title')[0].innerText = 'empty';
        }

        if(thisRouter){
            switch(thisRouter.title){
                case "home":
                    this.openHome();
                    break;
                case "project":
                    this.openProject(thisRouter.id);
                    break;
                case "task":
                    this.openTask(thisRouter.id,thisRouter.tab);
                    break;
                default:
                    this.reopen();
                    break;
            }
        }else{
            window.history.replaceState(null, '');
            //this.openHome();
        } 
    },
    setRouter:function(path,tab,id){ 
        
        var state = { 
            title: path, 
            tab:tab||"",
            id:id||"",
            url: "#"
        }; 
        
        if(this.forward){ //如果是正向，添加历史记录和路由记录
            var lastRouter = this.router[this.router.length - 1]; //获取上一页
            if(lastRouter){
                if(lastRouter.title !== path){
                    if(this.displayType !== "component") window.history.pushState(state,path); 
                    this.router.push(state);
                }else {
                    if(lastRouter.id !== id){
                        if(this.displayType !== "component") window.history.pushState(state,path); 
                        this.router.push(state);
                    }
                }
            }else{
                if(this.displayType !== "component") window.history.pushState(state,path); 
                this.router.push(state);
            }
        } 
        
    },
    back:function(){ 
        //window.history.back();
        if(this.displayType == "component") this.render()
        else window.history.back();
    },
    reopen:function(){ 
        this.reset = true;
        this.router.each(function(){
            this.back()
        }.bind(this))

        window.setTimeout(function(){
            this.reset = false;
            this.router = [];
            this.forward = true;
            this.openHome();
        }.bind(this),100);
    },
    openHome:function(){
        MWF.xDesktop.requireApp("TeamWork", "MobileHome", function(){ 
            new MWF.xApplication.TeamWork.MobileHome(this.container,this.app,this);
        }.bind(this))
    },
    openProject:function(id){
        MWF.xDesktop.requireApp("TeamWork", "MobileProject", function(){ 
            new MWF.xApplication.TeamWork.MobileProject(this.container,this.app,this,{id:id});
        }.bind(this))
    },
    openTask:function(id,tab){
        MWF.xDesktop.requireApp("TeamWork", "MobileTasks", function(){ 
            new MWF.xApplication.TeamWork.MobileTasks(this.container,this.app,this,{id:id,tab:tab});
        }.bind(this))
    },
    setMobileHeightAuto:function(node,str,row,height){
        var row = row || 25;
        var height = height || 20;
        var r = str.length/row; //每行25字（12px），行高20px
        node.setStyles({"height":((parseInt(r)+1)*height)+"px"})
    },
    setUserIcon:function(user){
        var person = user.split("@")[0];
        var userLayout = new Element("div.tw-common-user-layout");
        new Element("div.tw-common-user-icon",{text:person.substring(0,1)}).inject(userLayout);
        new Element("div",{text:person}).inject(userLayout); 
        return userLayout
    },

    selectPerson: function( type,count,value,callback ) { 
        MWF.xDesktop.requireApp("Selector", "package", null, false);
        this.fireEvent("querySelect", this);
        var options = {
            "type": type,
            "title": "选人",
            "count": count,
            "values": value || [],
            "resultType":"person",
            "onComplete": function (items) { 
                var personList = [];
                items.each(function (item) {
                    personList.push(item.data.distinguishedName);
                });
                if(callback)callback(personList);
            }.bind(this)
        };
        
        var selector = new MWF.O2Selector(this.container, options);
    },
    selectCalendar:function(target,options,callback){
        var type = options.type ||"datetime";
        MWF.require("MWF.widget.Calendar", function(){
            var calendarOptions = {
                "style" : "xform",
                "isTime":  type == "time" || type.toLowerCase() == "datetime",
                "timeOnly": type == "time",
                "target": this.container,
                "onQueryComplate" : function( dateString ,date ){ 
                    var json={
                        "action":"ok",
                        "dateString":dateString,
                        "date":date
                    };
                    if( callback )callback( json );
                }.bind(this),
                "onClear":function(){ 
                    var json={
                        "action":"clear",
                        "dateString":""
                    };
                    if(callback) callback(json);
                    //if(this.calendar) delete this.calendar;
                }.bind(this),
                "onHide":function(){
    
                }.bind(this)
            };
            if( options.calendarOptions ){
                calendarOptions = Object.merge( calendarOptions, options.calendarOptions )
            }

            this.calendar = new MWF.widget.Calendar( target, calendarOptions);
            this.calendar.show();

        }.bind(this));
    },
    getIdentityByPerson:function(persons){
        //persons 数组
        var idList = []; 
        this.app.orgActions.IdentityAction.listMajorWithPersonObject({personList:persons},function(json){
            var data = json.data;
            data.forEach((d) => {
                idList.push(d.distinguishedName||"");
            })
        }.bind(this),null,false);
        return idList
    },
    setMobileCover:function(styles){
        if(this.mobileCoverNode) this.mobileCoverNode.destroy();
        this.mobileCoverNode = new Element("div.tw-common-cover").inject(this.container);
        if(styles)this.mobileCoverNode.setStyles(styles);
    },
    clearMobileCover:function(){
        if(this.mobileCoverNode) this.mobileCoverNode.destroy();
    },
    fitComponent:function(){
        //兼容html样式，如果是嵌入到平台应用的组件中。设置application form以及form的上层div设置100%
        this.container.setStyles({"height":"100%","background-color":"#ffffff"});
        var pnode = this.container.getParent(); 
        if(pnode && pnode.getAttribute("mwftype") == "form"){
            this.displayType = "component";
            pnode.setStyle("height","100%");
            var ppnode = pnode.getParent();
            if(ppnode){
                ppnode.setStyle("height","100%");
            }
        }
    }
});
