MWF.xApplication.AI.options.multitask = true;
MWF.xApplication.AI.Setting = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "view" : "setting.html",
        "style": "default",
    },
    initialize: function(app, container, options){
        this.setOptions(options);
        this.app = app;
        this.container = container;
        this.viewPath = this.app.path+this.app.options.style+"/view/"+this.options.view;

        this.lp = this.app.lp;
        this.action = o2.Actions.load("x_ai_assemble_control");

        this.load();
    },
    load: async function(){

        const config = await this.action.ConfigAction.getConfig();
        this.config = config.data;
        this.config.appIconUrl = this.config.appIconUrl || "../x_component_AI/$Main/default/bot.png";
        this.config.appName = this.config.appName || "O2OA";
        this.config.title = this.config.title || this.lp.config.title;
        this.config.desc = this.config.desc || this.lp.config.desc;
        this.config.knowledgeIndexApp = this.config.knowledgeIndexAppList.map((d)=>{
            return d.split("|")[1];
        }).join();
        this.config.knowledgeIndexAppValue = this.config.knowledgeIndexAppList.join();

        this.config.questionsIndexApp = this.config.questionsIndexAppList.map((d)=>{
            return d.split("|")[1];
        }).join();
        this.config.questionsIndexAppValue = this.config.questionsIndexAppList.join();


        this.container.loadHtml(this.viewPath, {"bind": {"lp": this.app.lp,"config":this.config}, "module": this}, function(){

        }.bind(this));
    },
    openIndex : function (){
        this.rootNode.destroy();
        this.app.rootNode.show();
    },
    openKnowledge : function (){
        this.rootNode.destroy();
        o2.requireApp("AI", "Knowledge", function(){
            new MWF.xApplication.AI.Knowledge(this.app, this.container);
        }.bind(this));
    },
    openModel : function (){
        this.rootNode.destroy();
        o2.requireApp("AI", "Model", function(){
            new MWF.xApplication.AI.Model(this.app, this.container);
        }.bind(this));
    },
    openMcp : function (){
        if(!this.config.o2AiEnable){
            $OOUI.notice.warn(this.lp.common.tip, this.lp.o2AiEnableTip);
            return;
        }
        this.rootNode.destroy();
        o2.requireApp("AI", "Mcp", function(){
            new MWF.xApplication.AI.Mcp(this.app, this.container);
        }.bind(this));
    },
    openSetting : function (){
        this.rootNode.destroy();
        o2.requireApp("AI", "Setting", function(){
            new MWF.xApplication.AI.Setting(this.app, this.container);
        }.bind(this));
    },
    selectCMS :function (ev){
        const node = ev.target;
        const opt = {
            "types": ["CMSApplication"],
            "count": 0,
            "title": this.lp.common.select,
            "values":[],
            "onComplete": function (items) {
                console.log(items)
                let values = [];
                let names = [];
                if(items.length>0){
                    items.forEach((item)=>{
                        values.push(item.data.name);
                        names.push(item.data.id + "|" + item.data.name);
                    })
                    node.value = values.join();
                    node.set("v",names.join());
                }

            }.bind(this)
        };
        o2.xDesktop.requireApp("Selector", "package", function(){
            new o2.O2Selector(this.app.content, opt);
        }.bind(this), false);
    },
    setAiEnable : function (){
        const node = this.rightNode;
        const o2AiEnable = node.querySelector("[name='o2AiEnable']");
        console.log(o2AiEnable.get("value"))
        if(o2AiEnable.get("value") !== "true"){
            this.aiEnableNode.show();
        }else{
            this.aiEnableNode.hide();
        }
    },
    save : function (){
        const node = this.rightNode;
        const appName = node.querySelector("[name='appName']");
        const title = node.querySelector("[name='title']");
        const desc = node.querySelector("[name='desc']");
        const appIconUrl = node.querySelector("[name='appIconUrl']");
        const knowledgeIndexAppList = node.querySelector("[name='knowledgeIndexAppList']");
        const questionsIndexAppList = node.querySelector("[name='questionsIndexAppList']");
        const o2AiBaseUrl = node.querySelector("[name='o2AiBaseUrl']");
        const o2AiToken = node.querySelector("[name='o2AiToken']");
        const o2AiEnable = node.querySelector("[name='o2AiEnable']");

        this.action.ConfigAction.saveConfig({
            "appName" : appName.get("value"),
            "title" : title.get("value"),
            "desc" : desc.get("value"),
            "appIconUrl" : appIconUrl.get("value"),
            "o2AiBaseUrl" : o2AiBaseUrl.get("value"),
            "o2AiToken" : o2AiToken.get("value"),
            "o2AiEnable" : o2AiEnable.get("value"),
            "knowledgeIndexAppList" :knowledgeIndexAppList.get("v")!==""?knowledgeIndexAppList.get("v").split(","):[],
            "questionsIndexAppList" :questionsIndexAppList.get("v")!==""?questionsIndexAppList.get("v").split(","):[]
        }, function( json ){
            $OOUI.notice.success(this.lp.common.tip, this.lp.common.savesuccess);
        }.bind(this));
    }
});
