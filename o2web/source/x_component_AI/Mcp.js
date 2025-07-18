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
        this.action.ConfigAction.listMcpConfigPaging(1,1000, function( json ){

            this.categoryList = ["all", ...new Set(json.data.map(item => item.category).filter(category => category !== ""))];
            this.container.loadHtml(this.viewPath, {"bind": {"categoryList":this.categoryList,"modelList":json.data,"lp": this.app.lp,"config":this.app.config}, "module": this}, function(){
                this.categoryList.each(function (d){
                    const el = new Element("div.model-category-item",{"text":d=="all"?this.lp.common.all:d}).inject(this.categoryNode);
                    el.addEvent("click",function (){
                        this.loadList(d);
                        el.addClass("model-category-item-c");
                        el.getSiblings().removeClass("model-category-item-c");
                    }.bind(this));
                    if(d === "all"){
                        el.addClass("model-category-item-c");
                    }
                }.bind(this))
            }.bind(this));
        }.bind(this));
    },
    loadList : function (name){
        if(name === "all") {
            this.modelNode.getElements(".model-item").show();
            return;
        }
        this.modelNode.getElements(".model-item").each(function(node){
            if(node.get("category")==name){
                node.show();
            }else{
                node.hide();
            }
        }.bind(this))
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

        const group = new Element("oo-radio-group.required",{"label":this.lp.mcp.required}).inject(row);

        new Element("oo-radio", {"text": this.lp.common.yes,"value":"true"}).inject(group);
        new Element("oo-radio", {"text": this.lp.common.no,"value":"false"}).inject(group);
        const select = new Element("oo-select.type",{"label":this.lp.mcp.fieldtype}).inject(row);

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
        $OOUI.confirm.warn(this.lp.common.removetitle, this.lp.common.removeconfirm, null, ev.target, 'left top').then(({dlg, status}) => {
            if (status === 'ok') {
                this.action.ConfigAction.deleteMcpConfig(id, function (json) {
                    this.reload();
                    dlg.close();
                }.bind(this));
            }
        });
    },
    importMcp : function () {
        const _this = this;
        const fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.accept = '.json';
        fileInput.style.display = 'none';
        document.body.appendChild(fileInput);
        fileInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            document.body.removeChild(fileInput);
            if (!file) {
                $OOUI.notice.warn(this.lp.common.tip, this.lp.common.unselectfile);
                return;
            }
            if (file.type !== 'application/json') {
                $OOUI.notice.warn(this.lp.common.tip, `The file type is incorrect; expected application/json, but got ${file.type}`);
                return;
            }
            const reader = new FileReader();
            reader.onload = function(e) {
                try {
                    const jsonData = JSON.parse(e.target.result);
                    _this.action.ConfigAction.updateMcpConfig(jsonData.id,jsonData, function( json ){
                        $OOUI.notice.success(this.lp.common.tip, "success");
                        _this.reload();
                    });
                } catch (error) {
                    $OOUI.notice.warn(this.lp.common.tip, `JSON Error: ${error.message}`);
                }
            };
            reader.onerror = function() {
                $OOUI.notice.warn(this.lp.common.tip, `File Read Error: ${reader.error.message}`);
            };
            reader.readAsText(file);
        });
        fileInput.click();
    },
    downloadJSON : function (jsonString, fileName = 'data.json') {
        const blob = new Blob([jsonString], { type: 'application/json' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(link.href);
    },

    exportMcp : function (id, ev){
        $OOUI.confirm.warn(this.lp.common.export, this.lp.common.exportconfirm, null, ev.target, 'left top').then(({dlg, status}) => {
            if (status === 'ok') {
                this.action.ConfigAction.getMcpConfig(id, function( json ){
                    const jsonStr = JSON.stringify(json.data);
                    this.downloadJSON(jsonStr,json.data.name + ".json")
                    dlg.close();
                }.bind(this),null,false);
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
        node.loadHtml(url, {"bind": {"lp": _self.lp,"data" : data||{}}, "module": this}, function () {
            let template = "",script = "";
            if(o2.typeOf(data.extra.template) === "object"){
                template = ""
            }else{
                template = data.extra.template;
            }
            if(o2.typeOf(data.extra.script) === "object"){
                script = ""
            }else{
                script = data.extra.script;
            }
            const templateEditor = new o2.widget.ScriptArea(this.msgTemplateNode, { "option": { "mode": "markdown" } });
            templateEditor.load({"code":template});
            const scriptEditor = new o2.widget.ScriptArea(this.msgScriptNode, { "option": { "mode": "javascript" } });
            scriptEditor.load({"code":script});

            var dlg = o2.DL.open({
                "title": type === "edit"?this.lp.modifymcp:this.lp.newmcp,
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
                        "text": this.lp.common.save,
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
                                    "template" : o2.typeOf(templateEditor.getData()) === "object"?"" : templateEditor.getData(),
                                    "script" : o2.typeOf(scriptEditor.getData()) === "object"?"" : scriptEditor.getData()
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
                        "text": this.lp.common.close,
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
