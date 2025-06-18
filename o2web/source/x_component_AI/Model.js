MWF.xApplication.AI.options.multitask = true;
MWF.xApplication.AI.Model = new Class({
    Extends: MWF.xApplication.AI.Setting,
    Implements: [Options, Events],
    options: {
        "view" : "model.html",
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
        this.action.ConfigAction.listModelPaging(1,100, function( json ){
            this.container.loadHtml(this.viewPath, {"bind": {"modelList":json.data,"lp": this.app.lp,"config":this.app.config}, "module": this}, function(){

            }.bind(this));
        }.bind(this));

    },

    reload : function (){
        this.rootNode.destroy();
        this.load();
    },
    removeModel : function (id,ev){
        $OOUI.confirm.warn('删除确认', '您确定要删除该文档吗？', null, ev.target, 'left top').then(({ dlg, status }) => {
            if (status === 'ok') {
                this.action.ConfigAction.deleteModel(id, function( json ){
                    this.reload();
                    dlg.close();
                }.bind(this));
            }
        });
    },
    newModel : function (type,id){

        const node = new Element("div");
        const url = this.app.path + this.options.style + "/dlg/model.html";

        const _self = this;
        let data = {};
        if(type === "edit"){

            this.action.ConfigAction.getModel(id, function( json ){
                data = json.data;
            }.bind(this),null,false);
        }

        node.loadHtml(url, {"bind": {"lp": _self.lp,"data" : data||{}}, "module": this}, function () {

            $OOUI.dialog(type === "edit"?"修改模型":"新建模型", node, this.container, {
                buttons: 'ok, cancel', canMove: false,
                events: {
                    "ok": function () {
                        const name = node.querySelector("[name='name']");
                        const desc = node.querySelector("[name='desc']");
                        const moduletype = node.querySelector("[name='type']");
                        const model = node.querySelector("[name='model']");
                        const completionUrl = node.querySelector("[name='completionUrl']");
                        const apiKey = node.querySelector("[name='apiKey']");
                        const asDefault = node.querySelector("[name='asDefault']");
                        let newData = {
                            "name" : name.get("value"),
                            "type" : moduletype.get("value"),
                            "model" : model.get("value"),
                            "completionUrl" : completionUrl.get("value"),
                            "apiKey" : apiKey.get("value"),
                            "desc" : desc.get("value"),
                            "asDefault" :asDefault.get("value")
                        }

                        if(type === "edit"){
                            _self.action.ConfigAction.updateModel(data.id,newData, function( json ){
                                this.close();
                                _self.reload();
                            }.bind(this));
                        }else{
                            _self.action.ConfigAction.createModel(newData, function( json ){
                                this.close();
                                _self.reload();
                            }.bind(this));
                        }



                    }
                }

            });

        }.bind(this));

    },
    selectCMS :function (ev){
        const node = ev.target;
        const opt = {
            "types": ["CMSApplication"],
            "count": 0,
            "title": "选择",
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
    save : function (){
        const node = this.rightNode;
        const appName = node.querySelector("[name='appName']");
        const title = node.querySelector("[name='title']");
        const desc = node.querySelector("[name='desc']");
        const appIconUrl = node.querySelector("[name='appIconUrl']");
        const knowledgeIndexAppList = node.querySelector("[name='knowledgeIndexAppList']");


        this.action.ConfigAction.saveConfig({
            "appName" : appName.get("value"),
            "title" : title.get("value"),
            "desc" : desc.get("value"),
            "appIconUrl" : appIconUrl.get("value"),
            "knowledgeIndexAppList" :knowledgeIndexAppList.get("v")!==""?knowledgeIndexAppList.get("v").split(","):[]
        }, function( json ){
            alert("success")
        }.bind(this));
    }
});
