o2.requireApp("AI", "AttachmenPreview",null,false);
MWF.xApplication.AI.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style1": "default",
        "style": "default",
        "name": "AI",
        "mvcStyle": "style.css",
        "icon": "icon.png",
        "mode" : "pc",
        "msg" : "",
        "initMsg" : "",
        "attId" : "",
        "jars" : "",
        "title": MWF.xApplication.AI.LP.title
    },
    onQueryLoad:  function () {

    },
    loadApplication: async function (callback) {
        this.lp = MWF.xApplication.AI.LP;

        if(layout.mobile || this.options.mode === "simple"){
            o2.requireApp("AI", "Chat", function(){
                new MWF.xApplication.AI.Chat(this, this.content,this.options);
            }.bind(this));

            return;
        }
        this.isThinking = false;
        this.generateType = "auto";
        this.sessionId = "";
        this.action = o2.Actions.load("x_ai_assemble_control");
        this.isComposing = false;
        this.selectedFiles = {};
        o2.loadCss("../x_component_process_FormDesigner/Module/Form/skin/v10/form.css");


        const aiTypeList = await this.action.ConfigAction.listEnableModel();
        this.aiTypeList = aiTypeList.data;

        this.aiType = this.aiTypeList.find(model => model.asDefault)?.name;

        const config = await this.action.ConfigAction.getBaseConfig();
        this.config = config.data;
        this.config.appIconUrl = this.config.appIconUrl || "../x_component_AI/$Main/default/bot.png";
        this.config.appName = this.config.appName || "O2OA";
        this.config.title = this.config.title || this.lp.config.title;
        this.config.desc = this.config.desc || this.lp.config.desc;

        if(this.config.o2AiEnable){
            const mcpList = await this.action.ConfigAction.listMcpConfigPaging(1,100);
            this.mcpList = mcpList.data
                .filter(item => item.extra && item.extra.indexEnable === "true")
                .slice(0, 20);

            console.log(this.mcpList)
        }


        var url = this.path + this.options.style + "/view.html";
        o2.load("../o2_lib/marked/lib/marked.js", function () {
            this.content.loadHtml(url, {"bind": {"lp": this.lp,"config":this.config,"user":layout.user}, "module": this}, function () {
            }.bind(this));
        }.bind(this));
    },
    setThinking : function (){
        if(this.isThinking){

            this.isThinking = false;
            this.thinkingNode.removeClass("chat-think-cur");
        }else{

            this.isThinking = true;
            this.thinkingNode.addClass("chat-think-cur");
        }
    },
    expandReasoning : function (){
        if(this.reasoningTool.getFirst().hasClass("ooicon-zhankai")){
            this.reasoningTool.getFirst().removeClass("ooicon-zhankai");
            this.reasoningTool.getFirst().addClass("ooicon-zhedie");
            this.reasoningContentNode.hide();
        }else {
            this.reasoningTool.getFirst().addClass("ooicon-zhankai");
            this.reasoningTool.getFirst().removeClass("ooicon-zhedie");
            this.reasoningContentNode.show();
        }
    },
    loadNew : function (){
debugger
        if(this.options.jars!==""){
            this.initMsg();
            return;
        }

        this.sessionId = "";
        this.rightNode.empty();
        this.rightNode.loadHtml(this.path + this.options.style + "/new.html", {"bind": {"lp": this.lp,"config":this.config,"aiType" : this.aiType,"mcpList":this.mcpList}, "module": this}, function () {
            this.bindEvent();
            if(this.options.initMsg!==""){
                this.chatNode.set("text",this.options.initMsg);
            }
        }.bind(this));
    },

    initMsg : function (){
        const msg = this.options.msg;

        this.selectedFiles[this.options.attName] = {
            "id" : this.options.attId,
            "name" : this.options.attName
        }
        this.rightNode.empty();
        this.rightNode.loadHtml(this.path + this.options.style + "/list.html", {"bind": {"lp": this.lp,"generateType":this.generateType,"config":this.config,"aiType" : this.aiType}, "module": this}, function () {
            this.bindEvent(true);
            this.chatListNode.empty();
            this.titleNode.set("text",msg.length>30?msg.substring(0,30):msg);
            this.send(msg);
        }.bind(this));
    },

    copyToClipboard: function (text) {
        if (navigator.clipboard) {
            navigator.clipboard.writeText(text)
                .then(() => {
                    $OOUI.notice.success(this.lp.common.tip, this.lp.copysuccess);
                })
                .catch(err => {
                    console.error(this.lp.copyerror + ':', err);
                });
        } else {
            const textarea = document.createElement('textarea');
            textarea.value = text;
            textarea.style.position = 'fixed';
            document.body.appendChild(textarea);
            textarea.select();
            textarea.setSelectionRange(0, textarea.value.length);
            try {
                const success = document.execCommand('copy');
                if (success) {
                    alert(this.lp.copysuccess);
                } else {
                    throw new Error(this.lp.copyerror);
                }
            } catch (err) {
                console.error(this.lp.copyerror + ':', err);
            } finally {
                document.body.removeChild(textarea);
            }
        }
    },

    bindEvent: function (flag) {

        this.chatNode.addEventListener('paste', function (e) {
            const clipboardData = e.clipboardData || window.clipboardData;
            // 检查是否包含图片
            const items = clipboardData.items;
            let hasImage = false;

            for (let i = 0; i < items.length; i++) {
                if (items[i].type.indexOf('image') !== -1) {
                    hasImage = true;
                    break;
                }
            }

            if (hasImage) {
                e.preventDefault(); // 阻止默认粘贴行为
                this.addAtt("paste",clipboardData);
            }
        }.bind(this));

        this.chatNode.addEventListener('compositionstart', function (event) {
            this.isComposing = true;
        }.bind(this));

        this.chatNode.addEventListener('compositionend', function (event) {
            this.isComposing = false;
        }.bind(this));

        this.chatNode.addEventListener('keydown', function (event) {
            if (this.isComposing) {
                return;
            }
            if (event.shiftKey && event.key === 'Enter') {
                this.data.chat += "\n";
                event.preventDefault();
            } else if (event.key === 'Enter' || event.keyCode === 13) {
                event.preventDefault();
                if(flag){
                    this.send();
                }else{
                    this.sendNew();
                }
            }
        }.bind(this));
    },
    categorizeDates: function (jsonArray) {
        const now = new Date(); // 当前时间
        const todayStart = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime(); // 今天开始的时间戳
        const sevenDaysAgo = todayStart - 7 * 24 * 60 * 60 * 1000; // 7天前的时间戳（不包括今天）
        const thirtyDaysAgo = todayStart - 30 * 24 * 60 * 60 * 1000; // 30天前的时间戳（不包括今天）
        let categories = {
            today: [],
            lastSevenDays: [],
            lastThirtyDays: [],
            older: []
        };
        jsonArray.forEach(item => {
            let itemDate = new Date(item.createDateTime).getTime();

            if (itemDate >= todayStart) {
                categories.today.push(item);
            } else if (itemDate >= sevenDaysAgo && itemDate < todayStart) {
                categories.lastSevenDays.push(item);
            } else if (itemDate >= thirtyDaysAgo && itemDate < sevenDaysAgo) {
                categories.lastThirtyDays.push(item);
            } else {
                categories.older.push(item);
            }
        });
        return categories;
    },
    loadHistory: function () {
        if(!this.historyListNode) return;
        this.historyListNode.empty();
        const p = this.action.ChatAction.listPaging(1, 1000);
        const _this = this;
        p.then((json) => {
            const categories = this.categorizeDates(json.data);
            if (json.data.length === 0) {
                //this.chatListNode.empty();
            }
            const renderCategory = (category, title) => {
                if (category.length > 0) {
                    new Element("div.history-title", { "text": title }).inject(this.historyListNode);
                    category.forEach((d, index) => {
                        const htmlTemplate = `
                        <div class="history-item">
                            <div class="history-item-text">${d.title}</div>
                            <div class="ooicon-delete history-item-op"></div>
                        </div>
                    `;
                        const listItemNode = new Element("div", { "html": htmlTemplate }).getFirst();
                        listItemNode.inject(this.historyListNode);

                        if(d.id === _this.sessionId){
                            listItemNode.addClass("history-item-c");
                        }
                        listItemNode.addEvent("click", function (ev) {
                            const itemNode = ev.target.getParent(".history-item") ? ev.target.getParent(".history-item") : ev.target;
                            itemNode.addClass("history-item-c");
                            itemNode.getSiblings().removeClass("history-item-c");
                            _this.loadChat(d);
                        });
                        const op = listItemNode.getElement(".history-item-op");
                        op.addEvent("click", function (ev) {
                            _this.removeChat(d.id);
                        });
                    });
                }
            };
            renderCategory(categories.today, this.lp.today);
            renderCategory(categories.lastSevenDays, this.lp.lastSevenDays);
            renderCategory(categories.lastThirtyDays, this.lp.lastThirtyDays);
            renderCategory(categories.older, this.lp.older);

        });
    },
    removeChat: function (id) {
        const _this = this;
        const p = this.action.ChatAction.delete(id);
        p.then((json) => {
            _this.loadHistory();
        })
    },
    getMcpExtra : function (flag){
        let data = {};
        this.action.ConfigAction.getMcpExt(flag, function( json ){
            data = json.data.extra;
        }.bind(this),null,false);
        return data;
    },
    renderTemplate :function(templateString, data) {
        if(templateString === "") return "";
        return templateString.replace(/\$\{([^}]+)\}/g, (match, expr) => {
            try {
                const func = new Function(...Object.keys(data), `return (${expr})`);
                return func(...Object.values(data));
            } catch (e) {
                console.error("Error evaluating expression:", e);
                return match;
            }
        });
    },
    loadChat: function (chat) {

        const _this = this;

        this.rightNode.empty();
        this.rightNode.loadHtml(this.path + this.options.style + "/list.html", {"bind": {"lp": this.lp,"generateType":this.generateType,"config":this.config,"aiType" : this.aiType}, "module": this}, function () {

            this.bindEvent(true);

            this.chatListNode.empty();
            this.sessionId = chat.id;
            const p = this.action.ChatAction.listCompletionPaging(chat.id, 1, 1000);
            this.titleNode.set("text", chat.title ? chat.title : this.lp.newchat);
            p.then(function (json) {
                json.data.forEach((msg) => {
                    let html;
                    let el;
                    if(msg.referenceIdList){
                        const chatListAttNode = new Element("div.chat-list-att").inject(_this.chatListNode);
                        msg.referenceIdList.each(function (fileId){
                            _this.action.FileAction.get(fileId,function (json){
                                let attItem;
                                const file = json.data;

                                const fileType = _this.getFileIcon(file.name);

                                let atthtml;
                                if("picture" === fileType){
                                    attItem = new Element("div.chat-att-img").inject(chatListAttNode);
                                    atthtml = `<img src="../x_ai_assemble_control/jaxrs/file/${file.id}/download/scale">`;
                                }else{
                                    attItem = new Element("div.chat-att-item").inject(chatListAttNode);
                                    atthtml = `
                                    <div class="chat-att-item-icon ooicon-${_this.getFileIcon(file.name)}"></div>
                                    <div class="chat-att-item-content">
                                        <div class="chat-att-item-title">${file.name}</div>
                                        <div class="chat-att-item-size">${_this.formatBytes(file.length)}</div>
                                    </div>
                                `;
                                }

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

                    if ( msg.toolCallList && msg.toolCallList.length>0 && msg.content.indexOf("output")>-1) {
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

                        if(mcpExtra.css){
                            answerNode.loadCssText(mcpExtra.css);
                        }
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
        this.rightNode.empty();
        this.rightNode.loadHtml(this.path + this.options.style + "/list.html", {"bind": {"lp": this.lp,"generateType":this.generateType,"config":this.config,"aiType" : this.aiType}, "module": this}, function () {
            this.bindEvent(true);
            this.chatListNode.empty();
            this.titleNode.set("text",msg.length>30?msg.substring(0,30):msg);
            this.send(msg);
        }.bind(this));
    },
    send: function (text) {
        this.options.initMsg = "";
        let msg = this.chatNode.get("value");
        const _this = this;
        if (text) msg = text;
        if (msg === "") return;

        this.attId = [];
        if(Object.keys(this.selectedFiles).length>0){
            Object.keys(this.selectedFiles).each(function(key,index) {

                if(this.selectedFiles[key]  instanceof File){
                    const formData = new FormData();
                    formData.append("file", this.selectedFiles[key]);
                    formData.append("fileName", key);

                    this.action.FileAction.upload(formData,{},function (json){

                        this.attId.push(json.data.id);

                    }.bind(this),null,false);
                }else {

                    this.action.FileAction.copyFile({
                        "id":this.selectedFiles[key].id,
                        "name":this.selectedFiles[key].name,
                        "copyFrom" : this.options.jars!==""?this.options.jars:"x_pan_assemble_control"
                    },function (json){

                        this.attId.push(json.data.id);

                    }.bind(this),null,false);

                }

            }.bind(this));


            const chatListAttNode = new Element("div.chat-list-att").inject(_this.chatListNode);
            this.attId.each(function (fileId){
                _this.action.FileAction.get(fileId,function (json){
                    let attItem ;
                    const file = json.data;

                    const fileType = _this.getFileIcon(file.name);
                    let atthtml;
                    if("picture" === fileType){
                        attItem = new Element("div.chat-att-img").inject(chatListAttNode);
                        atthtml = `<img src="../x_ai_assemble_control/jaxrs/file/${file.id}/download/scale">`;
                    }else{
                        attItem = new Element("div.chat-att-item").inject(chatListAttNode);
                        atthtml = `
                                    <div class="chat-att-item-icon ooicon-${_this.getFileIcon(file.name)}"></div>
                                    <div class="chat-att-item-content">
                                        <div class="chat-att-item-title">${file.name}</div>
                                        <div class="chat-att-item-size">${_this.formatBytes(file.length)}</div>
                                    </div>
                                `;
                    }

                    attItem.set("html",atthtml);

                    attItem.addEvent("click",function(){
                        new MWF.xApplication.AI.AttachmenPreview(file,this);
                    })
                },null,false);
            });


        }

        this.attUplodListNode.empty();
        this.selectedFiles = {};
        this.options.jars = "";

        const html = `
            <div class="chat-list-r">
                <div>${msg}</div>
                <img src="../x_organization_assemble_personal/jaxrs/icon/${layout.user.id}" class="imgicon">
            </div>
        
        `;
        const el = new Element("div", {"html": html});

        el.getFirst().inject(this.chatListNode);


        this.repl(msg);
        this.chatNode.set('value', "");
    },
    search: function () {
        var key = this.searchNode.get("value");
        MWF.require("MWF.widget.PinYin", function () {
            var pinyin = key.toPY().toLowerCase();
            var firstPY = key.toPYFirst().toLowerCase();

            this.historyListNode.getElements(".history-item").each(function (menu) {
                var menuItemText = menu.getElement(".history-item-text").get("text");
                var menuPinyin = menuItemText.toPY().toLowerCase();
                var menuFirstPY = menuItemText.toPYFirst().toLowerCase();
                if (menuItemText.indexOf(key) > -1 || menuPinyin.indexOf(pinyin) > -1 || menuFirstPY.indexOf(firstPY) > -1) {
                    menu.show();
                } else {
                    menu.hide();
                }
            }.bind(this));
        }.bind(this));
    },
    repl: function (msg) {

        const _this = this;
        abortController = new AbortController();
        let isUserScrolled = false;
        const scrollThreshold = 50;

        const html = `
          <div class="chat-list-l">
            <div>
              <img src="${_this.config.appIconUrl}" class="imgicon">
            </div>
            <div style="display: flex;flex-direction: column;">

              <div class="loading-container">
                <div style="display:flex; align-items: center;">
                  <img src="../x_component_AI/$Main/default/loadding.gif" style="height:1.6rem;margin-top: .8em;">
                  <span class="shining-animation">
                    <span>${_this.lp.thinking}</span>
                    <span class="loading-dot">.</span>
                    <span class="loading-dot">.</span>
                    <span class="loading-dot">.</span>
                  </span>
                </div>
              </div>
              <div class="aitype"></div>
              <div class="msg-container">
                  <div class="reasoning">
                    <div class="reasoning-tool" ><i class="ooicon-zhankai"></i><span>深度思考</span> </div>
                    <div class="reasoning-content"></div>
                   </div>
                  <div class="markdown-body"></div>
                  <div class="tools-container">
                    <div class="tools">
                      <div class="ooicon-window-max"></div>
                      <div class="ooicon-reset hide"></div>
                    </div>
                  </div>  
              </div>
            </div>
          </div>
        `;

        const el = new Element("div", {"html": html});
        const msgNode = el.getElement(".msg-container");
        const answerNode = el.getElement(".markdown-body");

        const reasoningContentNode = el.getElement(".reasoning-content");
        this.reasoningContentNode = reasoningContentNode;

        const reasoningNode = el.getElement(".reasoning");
        reasoningNode.hide();

        const loadingNode = el.getElement(".loading-container");
        const toolNode = el.getElement(".tools-container");
        const copyNode = el.getElement(".ooicon-window-max");
        const aitypeNode = el.getElement(".aitype");
        aitypeNode.hide();

        const reasoningTool = el.getElement(".reasoning-tool");
        this.reasoningTool = reasoningTool;

        reasoningTool.addEvent("click",function(){
            this.expandReasoning();
        }.bind(this));


        this.toolNode = toolNode;
        this.copyNode = copyNode;

        toolNode.hide();

        el.getFirst().inject(this.chatListNode);

        // 添加滚动监听
        const chatList = this.chatListWrapNode;
        chatList.scrollTop = chatList.scrollHeight;
        chatList.addEvent('scroll', function () {
            const distanceToBottom = chatList.scrollHeight - chatList.scrollTop - chatList.clientHeight;
            isUserScrolled = distanceToBottom > scrollThreshold;
        });

        function autoScroll() {
            if (!isUserScrolled) {
                chatList.scrollTop = chatList.scrollHeight;
            }
        }

        this.chatNode.set("disabled", true);
        this.btnSendNode.hide();
        this.btnCancelNode.show();

        function run() {

            let requestOptions = {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: null,
                signal: abortController.signal
            };
            requestOptions.url = "../x_ai_assemble_control/jaxrs/chat/completion";
            requestOptions.body = JSON.stringify({
                "input": msg,
                "clueId": _this.sessionId,
                "generateType": _this.generateType,
                "endpointName" : _this.aiType,
                "referenceIdList":_this.attId
            });

            fetch(requestOptions.url, requestOptions)
                .then(async response => {
                    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                    const contentType = response.headers.get('content-type');
                    await processStandardStream(response.body.getReader());
                })
                .catch(error => {
                    if (error.name !== 'AbortError') {
                        console.error("Error:", error);
                        answerNode.set("html", "Error: " + error.message);
                    }
                    _this.stop();
                });
        }

        async function processStandardStream(reader) {
            const decoder = new TextDecoder();
            let fullResponse = "";
            let fullReasoningResponse = "";

            while (true) {
                const {done, value} = await reader.read();
                if (done || abortController.signal.aborted) break;

                const chunk = decoder.decode(value, {stream: true});
                const lines = chunk.split('\n').filter(l => l.trim());

                let messageType = "message";
                for (const line of lines) {

                    if(line.startsWith("event: ")){
                        messageType = line.replace("event: ","");
                    }

                    // if(line.startsWith("event: status")){
                    //     loadingNode.hide();
                    // }

                    if (line.startsWith('data:')) {
                        try {
                            const message = line.replace(/^data: /, '').trim();
                            if (message === '[DONE]') {
                                _this.done(fullResponse);
                                return;
                            }

                            const parsed = JSON.parse(message);

                            if(messageType === "status" && parsed.clueId){
                                _this.completionId = parsed.id;
                                _this.sessionId = parsed.clueId;
                                _this.loadHistory();
                            }


                            if(messageType === "toolCall"){

                                loadingNode.getElement(".shining-animation").getFirst().set("text","正在调用工具" + parsed.name)
                                loadingNode.show();


                            }else if(messageType === "output"){

                                answerNode.set("id",_this.completionId);
                                const mcpData = parsed;

                                mcpExtra = _this.getMcpExtra(mcpData.name);

                                const template = _this.renderTemplate(mcpExtra.template,mcpData.data);

                                answerNode.set("html", marked.parse(template));
                                if(mcpExtra.css){
                                    answerNode.loadCssText(mcpExtra.css);
                                }
                                if(mcpExtra.script){
                                    eval("(function(node,data) { " + (mcpExtra.script + " }.bind(_this))(answerNode,mcpData.data)"));
                                }
                                _this.done(fullResponse);
                                autoScroll();

                                loadingNode.hide();
                                return;
                            }else if (parsed.choices?.[0]?.delta || parsed.choices?.[0]?.message) {

                                const content = parsed.choices[0].delta ? parsed.choices[0].delta.content : parsed.choices[0].message.content;

                                const reasoning_content = parsed.choices[0].delta ? parsed.choices[0].delta.reasoning_content : parsed.choices[0].message.reasoning_content;
                                if (content) {
                                    loadingNode.hide();
                                    fullResponse += content;
                                    answerNode.set("html", marked.parse(fullResponse));
                                }

                                if(reasoning_content){

                                    loadingNode.getElement(".shining-animation").getFirst().set("text","深度思考中")
                                    loadingNode.show();

                                    reasoningNode.show();

                                    fullReasoningResponse += reasoning_content;
                                    reasoningContentNode.set("html", marked.parse(fullReasoningResponse));
                                }

                            } else {
                                // if (_this.sessionId === "") {
                                //     if (parsed.clueId) {
                                //         _this.sessionId = parsed.clueId;
                                //
                                //         _this.loadHistory();
                                //     }
                                // }


                                if (parsed.generateType){
                                    aitypeNode.set("html",`<div class = "ooicon-${_this.getIcon(parsed.generateType)}"></div>`);
                                    answerNode.set("id",parsed.id);
                                }
                            }
                        } catch (e) {
                            console.log(line)
                            console.error('Parse error:', e, 'Original message:', line);
                        }
                    }
                }
                autoScroll();
            }
            _this.done(fullResponse);
        }
        run();
    },
    stop: function () {
        abortController.abort();
        abortController = new AbortController();
        this.chatNode.set("disabled", false);
        this.btnSendNode.show();
        this.btnCancelNode.hide();
    },
    done: function (fullResponse) {
        this.chatNode.set("disabled", false);
        this.chatNode.focus();
        this.btnSendNode.show();
        this.btnCancelNode.hide();
        //this.toolNode.show();
        this.copyNode.addEvent("click", function () {
            this.copyToClipboard(fullResponse);
        }.bind(this));
    },
    setting: function () {
        this.rootNode.hide();
        o2.requireApp("AI", "Setting", function(){
            new MWF.xApplication.AI.Setting(this, this.content);
        }.bind(this));
    },
    quickChat : function (msg){

        this.chatNode.set("value",msg);
    },
    getIcon : function (type){
        let icon ;
        switch (type) {
            case "auto":
                icon = "networking_click"
                break
            case "chat":
                icon = "message"
                break
            case "mcp":
                icon = "renwu"
                break
            case "searchKnowledgeBase":
                icon = "canyue"
                break
        }
        return icon;
    },
    getTypeName : function (type){
        let name ;
        switch (type) {
            case "auto":
                name = this.lp.types.auto
                break
            case "chat":
                name = this.lp.types.chat
                break
            case "mcp":
                name = this.lp.types.task
                break
            case "searchKnowledgeBase":
                name = this.lp.types.knowledge
                break
        }
        return name;
    },
    showAiType : function (ev){
        ev.stopPropagation();

        this.action.ConfigAction.listEnableModel(function( json ){
            data = json.data;

            const node = ev.target.getParent(".chat-ai-type")?ev.target.getParent(".chat-ai-type"):ev.target;
            let options = [];
            if (node.menu) return;

            json.data.each(function(d){
                options.push({
                    "label" : d.name,
                    "title" : d.desc
                })
            })
            node.menu = new $OOUI.Menu(node, {
                area: this.content,
                styles: {},
                items: options.map(option => ({
                    icon: option.icon,
                    label: option.label,
                    command: () => this.setAiType(option.label)
                }))
            });
            node.menu.show();

        }.bind(this));

    },
    setAiType : function (aiType){
        this.aiType = aiType;
        this.currentAiNode.set("text",aiType);
    },
    showGenerateType: function(ev) {
        ev.stopPropagation();
        if(!this.config.o2AiEnable) return;
        const node = ev.target;
        if (node.menu) return;
        // { icon: 'message', label: this.lp.types.chat + " ｜ "+this.lp.types.chat_text, type: "chat", text1: this.lp.types.chat, text2: this.lp.types.chat_text },
        // { icon: 'renwu', label: this.lp.types.task + " ｜ " + this.lp.types.task_text, type: "mcp", text1: this.lp.types.task, text2: this.lp.types.task_text },

        const options = [
            { icon: 'networking_click', label: this.lp.types.auto + " ｜ " + this.lp.types.auto_text, type: "auto", text1: this.lp.types.auto, text2: this.lp.types.auto_text },
            { icon: 'canyue', label: this.lp.types.knowledge + " ｜ " + this.lp.types.knowledge_text, type: "searchKnowledgeBase", text1: this.lp.types.knowledge, text2: this.lp.types.knowledge_text }
        ];
        node.menu = new $OOUI.Menu(node, {
            area: this.content,
            styles: {},
            items: options.map(option => ({
                icon: option.icon,
                label: option.label,
                command: () => this.updateToolSettings(option.type, option.text1, option.text2,option.icon)
            }))
        });
        node.menu.show();
    },
    updateToolSettings: function(type, text1, text2,icon) {
        this.generateType = type;
        this.tool1Node.set("class", `chat-tools-cur ooicon-${icon}`);
        this.tool2Node.set("text", text1);
        this.tool3Node.set("text", text2);
    },

    addAtt : function (type,clipboardData){
        const _this = this;
        const fileDisplay = this.attUplodListNode;
        const fileInputs = [];

        function createFileInput() {
            const fileInput = new Element('input', {
                'type': 'file',
                'multiple': true,
                'accept': 'image/jpeg,image/png,image/gif,image/bmp,image/webp,' +
                    '.doc,.docx,.wps,.ppt,.pptx,.pub,.vsd,.xls,.xlsx,' +
                    '.html,.htm,.txt,.md,.pdf,.ofd',
                'styles': {
                    'display': 'none'
                }
            });

            fileInput.addEvent('change', function(event) {
                handleFileSelection(this.files);
            });

            document.body.grab(fileInput);
            fileInputs.push(fileInput);
            return fileInput;
        }

        function handleFileSelection(files) {
            Array.from(files).forEach(function(file) {
                if (!_this.selectedFiles[file.name]) {
                    displayFileItem(file);
                    _this.selectedFiles[file.name] = file;
                }
            });
        }

        function displayFileItem(file) {
            const fileItem = new Element('div.chat-att-item').inject(fileDisplay);
            const html = `
                <div class="chat-att-item-icon ooicon-${_this.getFileIcon(file.name)}"></div>
                <div class="chat-att-item-content">
                    <div class="chat-att-item-title">${file.name}</div>
                    <div class="chat-att-item-size">${_this.formatBytes(file.size ? file.size : file.length)}</div>
                </div>
                <div class="chat-att-item-action ooicon-close"></div>
            `;
            fileItem.set("html", html);

            const deleteButton = fileItem.getElement(".ooicon-close");
            deleteButton.addEvent('click', function (event) {
                event.stopPropagation();
                removeFileItem(file.name);
                fileItem.dispose();
            });
        }

        function removeFileItem(fileName) {
            if (_this.selectedFiles[fileName]) {
                delete _this.selectedFiles[fileName];
            }
        }
        if(type === "paste"){
            const items = clipboardData.items;

            for (let i = 0; i < items.length; i++) {
                if (items[i].type.indexOf('image') !== -1) {
                    const blob = items[i].getAsFile();
                    if (blob) {
                        handleFileSelection([blob]);
                    }

                }
            }
        } else if(type==="drive"){
            o2.xDesktop.requireApp('Selector', 'package', function () {

                new MWF.O2Selector( layout.mobile ? $(document.body) : this.content, Object.assign( {
                    title: "",
                    type: 'PanFile',
                    onComplete: function ( selectedItemList ) {
                        files = selectedItemList.map( function(item){
                            return item.data;
                        });
                        console.log(files)
                        handleFileSelection(files)
                    }.bind(this)
                },  {}));
            }.bind(this));
        }else {
            const fileInput = createFileInput();
            fileInput.click();
        }

    },
    formatBytes : function (bytes){
        if (bytes === 0) return '0 B';
        const k = 1024;
        const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },
    getFileIcon : function(filename){
        const getFileExt = (filename) => filename.split('.').pop() || '';
        const type = getFileExt(filename).toLowerCase();
        if(["docx","doc"].contains(type)){
            return "word"
        }
        if(["xlsx","xls"].contains(type)){
            return "excel"
        }
        if(["md"].contains(type)){
            return "m1"
        }
        if(["pdf"].contains(type)){
            return "pdf2"
        }
        if(["ofd"].contains(type)){
            return "ofd2"
        }
        if(["ppt","pptx"].contains(type)){
            return "ppt"
        }
        if(["jpeg","jpg","gif","webp","png"].contains(type)){
            return "picture"
        }
        return "fujian";
    }
});
