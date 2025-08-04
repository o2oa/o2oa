o2.requireApp("AI", "O2Select",null,false);

MWF.xApplication.AI.Chat = new Class({
    Implements: [Options, Events],
    Extends: MWF.xApplication.AI.Main,
    options: {
        "view": "view.html",
        "style": "default",
    },
    initialize: async function (app, container, options) {
        this.setOptions(options);
        this.app = app;
        this.container = container;
        this.viewPath = this.app.path + this.app.options.style + "/mobile/" + this.options.view;
        this.historyPath = this.app.path + this.app.options.style + "/mobile/history.html";

        this.lp = this.app.lp;
        this.action = o2.Actions.load("x_ai_assemble_control");

        this.generateType = "auto";
        this.sessionId = "";
        this.isComposing = false;
        this.selectedFiles = {};

        const config = await this.action.ConfigAction.getBaseConfig();
        this.config = config.data;
        this.config.appIconUrl = this.config.appIconUrl || "../x_component_AI/$Main/default/bot.png";
        this.config.appName = this.config.appName || "O2OA";
        this.config.title = this.config.title || this.lp.config.title;
        this.config.desc = this.config.desc || this.lp.config.desc;
        this.load();
    },
    load : function (callback){
        o2.load("../o2_lib/marked/lib/marked.js", function () {
            this.container.loadHtml(this.viewPath, {"bind": {"lp": this.lp,"config":this.config}, "module": this}, function () {
                this.bindEvent();
                if(callback) callback();
            }.bind(this));
        }.bind(this));
    },
    bindEvent: function (flag) {
        // this.chatNode.addEventListener('compositionstart', function (event) {
        //     this.isComposing = true;
        // }.bind(this));
        //
        // this.chatNode.addEventListener('compositionend', function (event) {
        //     this.isComposing = false;
        // }.bind(this));
        //
        // this.chatNode.addEventListener('keydown', function (event) {
        //     if (this.isComposing) {
        //         return;
        //     }
        //     if (event.key === 'Enter') {
        //         this.data.chat += "\n";
        //         event.preventDefault();
        //     }
        // }.bind(this));
    },
    showHistory : function (){
        this.container.empty();
        this.container.loadHtml(this.historyPath, {"bind": {"lp": this.lp,"config":this.config}, "module": this}, function () {
            this.loadHistory();
        }.bind(this));
    },
    back: function () {

        if(this.sessionId === ""){
            this.container.empty();
            this.load();
        }else{

            this.loadChat({
                id : this.sessionId,
                title : this.titleNode.get("text")
            });
        }

    },
    loadChat : function (chat){
        const _this = this;
        this.container.empty();
        this.load(function (){
            this.chatDescNode.hide();
            this.sessionId = chat.id;
            const p = this.action.ChatAction.listCompletionPaging(chat.id, 1, 1000);
            this.titleNode.set("text", chat.title ? chat.title : this.lp.newchat);
            p.then(function (json) {
                json.data.forEach((msg) => {
                    let html;
                    let el;
                    if(msg.materialIdList){
                        const chatListAttNode = new Element("div.chat-list-att").inject(_this.chatListNode);
                        msg.materialIdList.each(function (fileId){
                            _this.action.FileAction.get(fileId,function (json){
                                const attItem = new Element("div.chat-att-item").inject(chatListAttNode);
                                const file = json.data;
                                const atthtml = `
                                    <div class="chat-att-item-icon ooicon-${_this.getFileIcon(file.name)}"></div>
                                    <div class="chat-att-item-content">
                                        <div class="chat-att-item-title">${file.name}</div>
                                        <div class="chat-att-item-size">${_this.formatBytes(file.length)}</div>
                                    </div>
                                `
                                attItem.set("html",atthtml);
                                attItem.addEvent("click",function(){
                                    new MWF.xApplication.AI.AttachmenPreview(file,this);
                                })
                            },null,false);
                        });
                    }

                    html = `
                        <div class="chat-list-r">
                            <div>${msg.input}</div>
                            <img src="../x_organization_assemble_personal/jaxrs/icon/${layout.user.id}" class="imgicon">
                        </div>
                    `;

                    el = new Element("div", {"html": html});
                    el.getFirst().inject(_this.chatListNode);

                    msg.icon = _this.getIcon(msg.generateType);
                    msg.typeName = _this.getTypeName(msg.generateType);

                    if ( msg.content.indexOf("$$mcp$$")>-1) {
                        let mcpData;
                        let mcpExtra;
                        try{
                            mcpData = JSON.parse(msg.content);
                            console.log(mcpData)
                            mcpData.data.extra = msg.extra;

                            mcpExtra = _this.getMcpExtra(mcpData.name);

                        }catch (e){
                            mcpExtra = {
                                template : msg.content
                            }
                        }
                        const template = _this.renderTemplate(mcpExtra.template,mcpData.data);
                        html = `
                            <div class="chat-list-l">
        
                                <div><img src="${_this.config.appIconUrl}" class="imgicon"></div>
                                <div style="display: flex;">
                                    <div class="aitype"><i class = "ooicon-${msg.icon}"> </i></div>
                                    <div class="markdown-body">
                                        ${marked.parse(template)}
                                    </div>
                                </div>
                            </div>        
                    `;

                        el = new Element("div", {"html": html});
                        answerNode = el.getFirst();
                        answerNode.inject(_this.chatListNode);

                        markdownBody = answerNode.getElement(".markdown-body");

                        if(mcpExtra.script){
                            debugger
                            eval("(function(node,data) { " + (mcpExtra.script + " }.bind(_this))(markdownBody,mcpData.data)"));
                        }

                    }else {
                        try {
                            msg.content = marked.parse(msg.content);
                        } catch (e) {}


                        html = `
                            <div class="chat-list-l">
        
                                <div><img src="${_this.config.appIconUrl}" class="imgicon"></div>
                                <div style="display: flex;">
                                    <div class="aitype"><i class = "ooicon-${msg.icon}"> </i></div>
                                    <div class="markdown-body">
                                        ${msg.content}
                                    </div>
        
                                </div>
                            </div>        
                    `;
                        el = new Element("div", {"html": html});
                        el.getFirst().inject(_this.chatListNode);
                    }

                })
                _this.chatListWrapNode.scrollTop = _this.chatListWrapNode.scrollHeight;
            })
        }.bind(this));

    },
    sendNew : function (){
        const msg = this.chatNode.get("value");
        this.chatDescNode.hide();
        this.titleNode.set("text",msg.length>30?msg.substring(0,30):msg);
        this.send(msg);
    },
    loadNew : function (){
        this.sessionId = "";
        this.container.empty();
        this.load();
    },
    showGenerateType: function(ev) {
        ev.stopPropagation();
        if(!this.config.o2AiEnable) return;
        const options = [
            { icon: 'networking_click', label: this.lp.types.auto + " ｜ " + this.lp.types.auto_text, type: "auto", text1: this.lp.types.auto, text2: this.lp.types.auto_text },
            { icon: 'message', label: this.lp.types.chat + " ｜ "+this.lp.types.chat_text, type: "chat", text1: this.lp.types.chat, text2: this.lp.types.chat_text },
            { icon: 'renwu', label: this.lp.types.task + " ｜ " + this.lp.types.task_text, type: "mcp", text1: this.lp.types.task, text2: this.lp.types.task_text },
            { icon: 'canyue', label: this.lp.types.knowledge + " ｜ " + this.lp.types.knowledge_text, type: "rag", text1: this.lp.types.knowledge, text2: this.lp.types.knowledge_text }
        ];
        new MWF.xApplication.AI.O2Select({
            selectValueList: ["auto","chat","mcp","rag"],
            selectTextList: [this.lp.types.auto,this.lp.types.chat,this.lp.types.task,this.lp.types.knowledge],
            onChange : function(value){

                const option = options.filter(d=>{
                    return d.type === value
                })[0]
                this.updateToolSettings(option.type, option.text1, option.text2,option.icon)
            }.bind(this)
        }).load();

    },

})
