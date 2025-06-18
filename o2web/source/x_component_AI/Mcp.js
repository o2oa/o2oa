MWF.require("MWF.widget.ScriptArea", null, false);
MWF.xApplication.AI.Mcp = new Class({
    Extends: MWF.xApplication.AI.Setting,
    Implements: [Options, Events],
    options: {
        "view" : "mcp.html",
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
        this.action.ConfigAction.listMcpConfigPaging(1,100, function( json ){
            this.container.loadHtml(this.viewPath, {"bind": {"modelList":json.data,"lp": this.app.lp,"config":this.app.config}, "module": this}, function(){

            }.bind(this));
        }.bind(this));
    },
    addMcpBodyParameter : function (){
        const row = new Element("div.form-row").inject(this.mcpBodyParameterListNode);
        new Element("oo-input.key",{"label":"key"}).inject(row);
        new Element("oo-input.value",{"label":"value"}).inject(row);
        let removeBtn = new Element("oo-button",{"text":"-"}).inject(row);
        removeBtn.addEvent("click",function(){
            row.destroy();
        })
    },
    addMcpParameter : function () {
        const row = new Element("div.form-row").inject(this.mcpListNode);
        new Element("oo-input.key", {"label": "key"}).inject(row);
        new Element("oo-input.value", {"label": "value"}).inject(row);

        const group = new Element("oo-radio-group.required",{"label":"是否必填"}).inject(row);

        new Element("oo-radio", {"text": "是","value":"true"}).inject(group);
        new Element("oo-radio", {"text": "否","value":"false"}).inject(group);
        const select = new Element("oo-select.type",{"label":"字段类型"}).inject(row);

        select.set("html",`<oo-option value="string">string</oo-option>
                    <oo-option value="array">array</oo-option>`)

        let removeBtn = new Element("oo-button", {"text": "-"}).inject(row);
        removeBtn.addEvent("click", function () {
            row.destroy();
        })
    },
    reload: function () {
        this.rootNode.destroy();
        this.load();
    },
    removeMcp: function (id, ev) {
        $OOUI.confirm.warn('删除确认', '您确定要删除吗？', null, ev.target, 'left top').then(({dlg, status}) => {
            if (status === 'ok') {
                this.action.ConfigAction.deleteMcpConfig(id, function (json) {
                    this.reload();
                    dlg.close();
                }.bind(this));
            }
        });
    },
    removeMcpParameter : function (ev){
        ev.target.getParent().destroy();
    },
    newMcp : function (type,id){

        const node = new Element("div");
        const url = this.app.path + this.options.style + "/dlg/mcp.html";

        const _self = this;
        let data = {
            "extra" : {

            }
        };
        if(type === "edit"){

            this.action.ConfigAction.getMcpConfig(id, function( json ){
                data = json.data;
                const obj = data.httpOption.bodyMap;
                data.mcpBodyParameterList = Object.keys(obj).reduce((acc, key) => {
                    acc.push({ key, value: obj[key] });
                    return acc;
                }, []);
            }.bind(this),null,false);
        }
debugger
        node.loadHtml(url, {"bind": {"lp": _self.lp,"data" : data||{}}, "module": this}, function () {

            const templateEditor = new o2.widget.ScriptArea(this.msgTemplateNode, { "option": { "mode": "markdown" } });
            templateEditor.load({"code":data.extra.template?data.extra.template:""});
            const scriptEditor = new o2.widget.ScriptArea(this.msgScriptNode, { "option": { "mode": "javascript" } });
            scriptEditor.load({"code":data.extra.script?data.extra.script:""});

            //scriptEditor.load({"code":""});


            var dlg = o2.DL.open({
                "title": type === "edit"?"修改MCP":"新建MCP",
                "width": "1200px",
                "height": "700px",
                "mask": true,
                "isMax" : true,
                "content": node,
                "container": null,
                "positionNode": this.app.content,
                "onQueryClose": function () {
                    node.destroy();
                }.bind(this),
                "buttonList": [
                    {
                        "text": "保存",
                        "action": function () {
                            const name = node.querySelector("[name='name']");
                            const category = node.querySelector("[name='category']");
                            const desc = node.querySelector("[name='desc']");
                            const enable = node.querySelector("[name='enable']");
                            const url = node.querySelector("[name='url']");


                            let mcpParameterList = [];
                            let bodyMap = {};

                            _self.mcpBodyParameterListNode.getElements(".form-row").each(function (node){
                                const key = node.getElement(".key").get("value");
                                const value = node.getElement(".value").get("value");
                                if(key!==""){
                                    bodyMap[key] = value;
                                }
                            })


                            let httpOption = {
                                "url": url.get("value"),
                                "method": "post",
                                "headerMap": {
                                    "Content-Type": "application/json"
                                },
                                "bodyMap": bodyMap,
                                "internalEnable": true
                            };
                            _self.mcpListNode.getElements(".form-row").each(function (node){
                                const key = node.getElement(".key").get("value");
                                const value = node.getElement(".value").get("value");
                                const required = node.getElement(".required").get("value");
                                const type = node.getElement(".type").get("value");
                                if(key!==""){
                                    mcpParameterList.push({
                                        "name": key,
                                        "desc": value,
                                        "type": type,
                                        "required": required
                                    });
                                }
                            })
                            let newData = {
                                "name" : name.get("value"),
                                "category" : category.get("value"),
                                "desc" : desc.get("value"),
                                "enable" :enable.get("value"),
                                "httpOption" :httpOption,
                                "mcpParameterList" :mcpParameterList,
                                "extra" :{
                                    "template" : templateEditor.getData(),
                                    "script" : scriptEditor.getData()
                                }
                            }

                            if(type === "edit"){
                                _self.action.ConfigAction.updateMcpConfig(data.id,newData, function( json ){
                                    this.close();
                                    _self.reload();
                                }.bind(this));
                            }else{
                                _self.action.ConfigAction.createMcpConfig(newData, function( json ){
                                    this.close();
                                    _self.reload();
                                }.bind(this));
                            }

                        }
                    },
                    {
                        "text": "关闭",
                        "action": function () {
                            dlg.close();
                        }.bind(this)
                    }
                ],
                "onPostShow": function () {
                    dlg.reCenter();
                }.bind(this)
            });
        }.bind(this));

    }
});
