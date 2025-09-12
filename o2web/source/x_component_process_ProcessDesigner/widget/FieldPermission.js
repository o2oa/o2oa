MWF.xApplication.process.ProcessDesigner.widget = MWF.xApplication.process.ProcessDesigner.widget || {};
MWF.xApplication.process.ProcessDesigner.widget.FieldPermission = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "maskNode": $(document.body),
        "height": null,
        "maxObj": null,
        "forceType": null,
        "type": "service"   //web or service
    },
    initialize: function(node, app, options){
        this.setOptions(options);
        this.node = $(node);
        this.app = app;
        this.pathData = {};

        this.currentProcess = this.app.process;
    
        this.path = "../x_component_process_ProcessDesigner/widget/$FieldPermission/";
        this.load();
    },

    load: function(){
        this.node.loadAll({
            html: [this.path+"view.html"],
            css: [this.path+"style.css"]
        }, {bind: {lp: this.app.designer.lp}, module: this}, () => {
            this.initEvents();
            // this.loadPermission();
        });
    },
    reload: function(){
        debugger;
        this.bodyNode.empty();
        this.loadPermission();
    },

    initEvents: function(){;
        o2.Actions.load("x_processplatform_assemble_designer").ProcessAction.listWithApplication(this.currentProcess.application, (json)=>{
            json.data.forEach((d)=>{
                const op = new Element('oo-option');
                op.setAttribute("value", d.id);
                op.setAttribute("text", d.name);
                this.selectProcessNode.appendChild(op);
            });
            this.selectProcessNode.value = this.currentProcess.id;
        });

        // this.selectPathButton.addEventListener("click", this.selectPath.bind(this));
    },

    _addPath: function(data, isNew=true){
        if (this.pathData[data.path]) return false;
        const readList = [];
        data.readList = (data.readActivityList?.map((a)=>{
            const {unique, id, name, type, process} = a;
            return name+'@'+id+'@'+unique+'@'+process+'@'+type+'@A';
        }) || []).concat(data.readerList || []);

        data.editList = (data.editActivityList?.map((a)=>{
            const {unique, id, name, type, process} = a;
            return name+'@'+id+'@'+unique+'@'+process+'@'+type+'@A';
        }) || []).concat(data.editoryList || []);

        this.bodyNode.loadHtmlText(this.lineHTML, {bind: data,  module: this});
        this.pathData[data.path] = data;
        const n = this.bodyNode.getLast('.fieldPermissions-body-line');
        if (n && isNew) n.addClass('newLine');
        return n;
    },
    _getNewPathData: function(path){
        return {
            itemCategoryId: this.currentProcess.id,
            path: path,
            readerList: null,
            readActivityList: null,
            editActivityList: null,
            editoryList: null
        }
    },
    _scrollToNode: function(node){
        const n = node || this.bodyNode.getLast('.fieldPermissions-body-line');
        n?.firstElementChild.scrollIntoView({ behavior: "smooth", block: "end", inline: "nearest" });
    },

    loadPermission: function(){
        o2.xhr_get(this.path+"line.html", ({responseText})=>{
            this.lineHTML = responseText;
            o2.Actions.load("x_processplatform_assemble_designer").ItemAccessAction.listWithProcess(this.currentProcess.id, (json)=>{
                json.data.forEach((d)=>{
                    const {unique, id, name, alias, type, process} = d;
                    const a = {
                        process: process,
                        id: id,
                        name: name,
                        alias: alias,
                        type: type,
                        unique: unique,
                        distinguishedName: name+'@'+id+'@'+unique+'@'+process+'@'+type+'@A'
                    }
                    this._addPath(d, false)
                });
                this._scrollToNode();
            });
        });
    },

    selectPath: function(){
        new o2.O2Selector(this.node, {
            "title": this.app.designer.lp.selectDataPath,
            "values": Object.keys(this.pathData),
            "type": "formField",
            "application": this.currentProcess.application,
            "style": "v10",
            "count": 0,
            "onComplete": function (items) {
                if (items.length) {
                    items.forEach((item)=>{
                        this._addPath(this._getNewPathData(item.data.name));
                    });
                }
            }.bind(this)
        });
    },
    addPath: function(){
        const content = new Element('div');
        content.set("html", `<oo-input style="width:30rem" required="true" label="${this.app.designer.lp.inputPath}"></oo-input>`); 

        $OOUI.dialog(this.app.designer.lp.inputPath, content, this.node, {
            events: {
                ok: (e)=>{
                    const path = content.querySelector('oo-input').value.trim();
                    if (path){
                        const n = this._addPath(this._getNewPathData(path));
                        if (n) this._scrollToNode(n);
                    }
                    e.target.close();
                }
            }
        });
    },

    selectPermissions: function(type, e, item){
        new o2.O2Selector(this.node, {
            "title": this.app.designer.lp.selectPermissions,
            "values": e.target.value,
            "types": ['activity', 'unit', 'group', 'identity', 'role'],
            // "type": 'processActivity',
            "application": this.currentProcess.application,
            "process": this.currentProcess.id,
            "style": "v10",
            "count": 0,
            "onComplete": function (items) {
                if (items.length) {
                    const v = items.map(item=>item.data.uniqueName || item.data.distinguishedName);
                    const o = items.map(item=>item.data);
                    e.target.value = v;
                    e.target.object = o;

                    const activitys = [];
                    const orgs = [];
                    o.forEach((d)=>{
                        if (d.distinguishedName.split('@').at(-1) === 'A'){
                            const {unique, id, name, alias, type, process} = d;
                            activitys.push({unique, id,name,alias,type,process})
                        }else{
                            orgs.push(d.distinguishedName);
                        }
                    });
                    if (type==='read'){
                        item.readerList = orgs;
                        item.readActivityList = activitys;
                    }
                    if (type==='edit'){
                        item.editoryList = orgs;
                        item.editActivityList = activitys;
                    }
                }
            }.bind(this)
        });
    },

    changeProcess: function(e){
        this.save().then(()=>{
            o2.Actions.load("x_processplatform_assemble_designer").ProcessAction.get(e.target.value).then((json)=>{
                this.currentProcess = json.data;
                this.pathData = {};
                this.reload();
            });
        });
    },

    deletePath: function(e, item){
        const info = this.app.designer.lp.deletePathInfo.replace('{path}', item.path);
        const line = e.target.closest('.fieldPermissions-body-line');
        line.addClass('deleting');
        $OOUI.confirm.warn(this.app.designer.lp.deletePathTitle, info, this.node.parentElement, e.target).then(({ dlg, status }) => {
            line.removeClass('deleting');
            if (status === 'ok') {
                if (item.id){
                    o2.Actions.load("x_processplatform_assemble_designer").ItemAccessAction.deleteWithProcessWithPath(item.itemCategoryId, item.path).then(()=>{
                        line.remove();
                    });
                }else{
                    line.remove();
                }
            }
            dlg.close();
        });
    },

    save: function(){
        return o2.Actions.load("x_processplatform_assemble_designer").ItemAccessAction.bachSave({
            itemAccessList: Object.values(this.pathData)
        });
    }

});
