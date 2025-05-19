MWF.xApplication.AI.options.multitask = true;
MWF.xApplication.AI.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style1": "default",
        "style": "default",
        "name": "AI",
        "mvcStyle": "style.css",
        "icon": "icon.png",
        "title": MWF.xApplication.AI.LP.title
    },
    onQueryLoad:  function () {
        this.lp = MWF.xApplication.AI.LP;
        this.message = [];
        this.generateType = "auto";
        this.sessionId = "";
        this.action = o2.Actions.load("x_ai_assemble_control");
        this.isComposing = false;
        o2.loadCss("../x_component_AI/$Main/default/markdown.css");
    },
    loadApplication: async function (callback) {

        const config = await this.action.ConfigAction.getConfig();
        this.config = config.data;
        this.config.appIconUrl = this.config.appIconUrl || "../x_component_AI/$Main/default/bot.png";
        this.config.appName = this.config.appName || "O2OA";
        this.config.title = this.config.title || this.lp.config.title;
        this.config.desc = this.config.desc || this.lp.config.desc;

        var url = this.path + this.options.style + "/view.html";
        o2.load("../o2_lib/marked/lib/marked.js", function () {
            this.content.loadHtml(url, {"bind": {"lp": this.lp,"config":this.config}, "module": this}, function () {
            }.bind(this));
        }.bind(this));
    },
    loadNew : function (){
        this.sessionId = "";
        this.rightNode.empty();
        this.rightNode.loadHtml(this.path + this.options.style + "/new.html", {"bind": {"lp": this.lp,"config":this.config}, "module": this}, function () {

            this.bindEvent();

        }.bind(this));
    },
    copyToClipboard: function () {
        if (navigator.clipboard) {
            // 使用现代 Clipboard API
            navigator.clipboard.writeText(text)
                .then(() => {
                    alert(this.lp.copysuccess);
                })
                .catch(err => {
                    console.error('无法复制文本:', err);
                });
        } else {
            // 使用旧方法
            // 创建一个临时的 textarea 元素
            const textarea = document.createElement('textarea');
            textarea.value = text;
            textarea.style.position = 'fixed'; // 避免滚动到输入框
            document.body.appendChild(textarea);

            // 选中文本
            textarea.select();
            textarea.setSelectionRange(0, textarea.value.length); // 兼容移动设备

            try {
                // 执行复制命令
                const success = document.execCommand('copy');
                if (success) {
                    alert(this.lp.copysuccess);
                } else {
                    throw new Error('复制失败');
                }
            } catch (err) {
                console.error('无法复制文本:', err);
            } finally {
                // 移除临时的 textarea 元素
                document.body.removeChild(textarea);
            }
        }
    },
    bindEvent: function (flag) {

        var chatNode = this.chatNode;
        chatNode.addEventListener('compositionstart', function (event) {
            this.isComposing = true;
        }.bind(this));

        chatNode.addEventListener('compositionend', function (event) {
            this.isComposing = false;
        }.bind(this));

        chatNode.addEventListener('keydown', function (event) {
            if (this.isComposing) {
                return; // 输入法正在输入，不处理回车键
            }
            if (event.shiftKey && event.key === 'Enter') {
                // 插入换行符
                this.data.chat += "\n";

                // 阻止默认行为
                event.preventDefault();
            } else if (event.key === 'Enter' || event.keyCode === 13) {
                // 触发发送操作
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
            let itemDate = new Date(item.createDateTime).getTime(); // 将日期转换为时间戳

            if (itemDate >= todayStart) {
                categories.today.push(item);
            } else if (itemDate >= sevenDaysAgo && itemDate < todayStart) { // 注意这里的条件
                categories.lastSevenDays.push(item);
            } else if (itemDate >= thirtyDaysAgo && itemDate < sevenDaysAgo) { // 确保不重叠
                categories.lastThirtyDays.push(item);
            } else {
                categories.older.push(item);
            }
        });

        return categories;
    },
    loadHistory: function () {
        this.historyListNode.empty();
        const p = this.action.ChatAction.listPaging(1, 1000);
        const _this = this;

        p.then((json) => {
            const categories = this.categorizeDates(json.data);

            if (json.data.length === 0) {
                this.chatListNode.empty();
            }

            // 定义一个函数来处理每个分类的数据
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

            // 调用函数处理每个分类
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
    loadChat: function (chat) {

        const _this = this;

        this.rightNode.empty();
        this.rightNode.loadHtml(this.path + this.options.style + "/list.html", {"bind": {"lp": this.lp}, "module": this}, function () {

            this.bindEvent(true);

            this.chatListNode.empty();
            this.sessionId = chat.id;
            const p = this.action.ChatAction.listCompletionPaging(chat.id, 1, 1000);
            this.titleNode.set("text", chat.title ? chat.title : this.lp.newchat);
            p.then(function (json) {


                json.data.forEach((msg) => {


                    let html;
                    let el;
                    html = `
 
                    <div class="chat-list-r">
                        <div>${msg.input}</div>
                        <img src="../x_organization_assemble_personal/jaxrs/icon/${layout.user.id}" class="imgicon">
                    </div>
                `;
                    el = new Element("div", {"html": html});
                    el.getFirst().inject(_this.chatListNode);

                    try {
                        msg.content = marked.parse(msg.content);
                    } catch (e) {

                    }


                    html = `

        
                    <div class="chat-list-l">

                        <div><img src="${_this.config.appIconUrl}" class="imgicon"></div>
                        <div><div class="markdown-body">
                            ${msg.content}
                        </div></div>
                    </div>        
        
        
        
            `;


                    el = new Element("div", {"html": html});
                    el.getFirst().inject(_this.chatListNode);


                })


                _this.chatListWrapNode.scrollTop = _this.chatListWrapNode.scrollHeight;
                _this.copyCode();


            })


        }.bind(this));




    },
    copyCode: function (el) {
        return false;
        // 查找所有的代码块并添加复制按钮
        const codeBlocks = el ? el.querySelectorAll('pre code') : this.chatListNode.querySelectorAll('pre code');
        codeBlocks.forEach(codeBlock => {
            const copyButton = document.createElement('button');
            copyButton.className = 'copy-button';
            copyButton.textContent = 'Copy';
            copyButton.addEventListener('click', () => {
                const range = document.createRange();
                range.selectNode(codeBlock);
                window.getSelection().removeAllRanges();
                window.getSelection().addRange(range);
                document.execCommand('copy');
                window.getSelection().removeAllRanges();
                alert('已拷贝');
            });

            // 将复制按钮添加到代码块的父元素中
            codeBlock.parentNode.classList.add('code-block');
            codeBlock.parentNode.insertBefore(copyButton, codeBlock);
        });
    },
    sendNew : function (){
        const msg = this.chatNode.get("value");
        this.rightNode.empty();
        this.rightNode.loadHtml(this.path + this.options.style + "/list.html", {"bind": {"lp": this.lp}, "module": this}, function () {

            this.bindEvent(true);

            this.chatListNode.empty();

            this.titleNode.set("text",msg);
            this.send(msg);

        }.bind(this));
    },
    send: function (text) {

        let msg = this.chatNode.get("value");

        if (text) msg = text;
        if (msg === "") return;

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
        const _self = this;
        abortController = new AbortController();
        let isUserScrolled = false;
        const scrollThreshold = 50;

        const collapsethingking = this.lp.collapsethingking

        const html = `

            <div class="chat-list-l">
            

                    <div class="thinking-container">
                        <div class="thinking-toggle">
                            <span class="toggle-arrow">▼</span> ${collapsethingking}
                        </div>
                        <div class="thinking">
                        </div>
                    </div>
            
           
                <div><img src="${_self.config.appIconUrl}" class="imgicon" class="imgicon"></div>
                
                
                <div>
                
                     <div class="loading-container">
                        <img src="../x_component_AI/$Main/default/loadding.gif" style="height:1.6rem;">
                        <!--<div class="spinner"></div>
                        <div></div>-->
                    </div>
                
                <div class="markdown-body">
                </div>
                
                <div class="tools-container">
                        <div class="tools">
                            <div class="ooicon-window-max"></div>
                            <div class="ooicon-reset"></div>
                        </div>
                    </div>
                
                </div>
            </div>

        `;

        const el = new Element("div", {"html": html});
        const answerNode = el.getElement(".markdown-body");
        const thinkingNode = el.getElement(".thinking");
        const thinkingToggle = el.getElement(".thinking-toggle");
        const thinkingContainer = el.getElement(".thinking-container");
        const toggleArrow = el.getElement(".toggle-arrow");
        const loadingNode = el.getElement(".loading-container");
        const toolNode = el.getElement(".tools-container");
        const copyNode = el.getElement(".ooicon-window-max");
        const _this = this;

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

        // 初始默认展开思考过程
        thinkingContainer.hide();
        thinkingNode.show();

        thinkingToggle.addEvent("click", function () {
            const isVisible = thinkingNode.isVisible();
            if (isVisible) {
                thinkingNode.hide();
                thinkingToggle.set("html", "<span class='toggle-arrow'>▶</span> "+this.lp.expandthingking);
            } else {
                thinkingNode.show();
                thinkingToggle.set("html", "<span class='toggle-arrow'>▼</span> "+this.lp.collapsethingking);
            }
        });

        function run() {
            _this.message.push({"role": "user", "content": msg});
            let lastMessage = _this.message.length >= 5 ? _this.message.slice(5 * -1) : _this.message.slice();
            const isKnowledgeBase = false;

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
                "generateType": this.generateType
            });
            //                "generateType": _this.chatType.get("value")

            loadingNode.show();
            fetch(requestOptions.url, requestOptions)
                .then(async response => {
                    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                    const contentType = response.headers.get('content-type');
                    loadingNode.hide();
                    //thinkingContainer.show();

                    if (contentType && contentType.includes('text/event-stream')) {
                        if (isKnowledgeBase) {
                            const reader = response.body.getReader();
                            const decoder = new TextDecoder();
                            await processCustomStream(reader, decoder);
                        } else {
                            await processStandardStream(response.body.getReader());
                        }
                    } else {
                        const data = await response.json();
                        const fullResponse = data.output.text;
                        answerNode.set("html", marked.parse(fullResponse));
                        _this.done(fullResponse);
                    }
                })
                .catch(error => {
                    if (error.name !== 'AbortError') {
                        console.error("Error:", error);
                        answerNode.set("html", "Error: " + error.message);
                        _this.copyCode(answerNode);
                    }
                    _this.form.get("chat").node.getFirst().set("disabled", false);
                    _this.form.get("btnSend").node.show();
                    _this.form.get("btnCancel").node.hide();
                });
        }

        async function processStandardStream(reader) {
            const decoder = new TextDecoder();
            let fullResponse = "";
            let isThinking = false;
            let thingkingFlag = false;
            let thinkingContent = "";

            while (true) {
                const {done, value} = await reader.read();
                if (done || abortController.signal.aborted) break;

                const chunk = decoder.decode(value, {stream: true});
                const lines = chunk.split('\n').filter(l => l.trim());

                for (const line of lines) {
                    if (line.startsWith('data:')) {
                        try {
                            const message = line.replace(/^data: /, '').trim();
                            if (message === '[DONE]') {
                                _this.done(fullResponse);
                                return;
                            }

                            const parsed = JSON.parse(message);
                            if (parsed.choices?.[0]?.delta || parsed.choices?.[0]?.message) {
                                //const reasoning = parsed.choices[0].delta.reasoning_content;
                                const content = parsed.choices[0].delta ? parsed.choices[0].delta.content : parsed.choices[0].message.content;

                                // if (reasoning) {
                                //     isThinking = true;
                                //     thinkingContent += reasoning;
                                //     thinkingNode.set("html", marked.parse(thinkingContent));
                                //     thinkingContainer.show();
                                // }

                                if (content) {
                                    if (!thingkingFlag) isThinking = false;

                                    if (content === '<think>') {
                                        isThinking = true;
                                        thingkingFlag = true;
                                        thinkingContent = "";
                                        thinkingContainer.show();
                                    } else if (content === '</think>') {
                                        isThinking = false;
                                        thinkingNode.set("html", marked.parse(thinkingContent));
                                        thinkingContent = "";
                                    } else if (isThinking) {
                                        thinkingContent += content;
                                        thinkingNode.set("html", marked.parse(thinkingContent));
                                    } else {
                                        fullResponse += content;
                                        answerNode.set("html", marked.parse(fullResponse));
                                    }
                                }
                            } else {
                                console.log(parsed)

                                if (_this.sessionId === "") {
                                    if (parsed.path && parsed.path === "clueId") {
                                        _this.sessionId = parsed.data;
                                        _this.loadHistory();
                                    }
                                }


                            }
                        } catch (e) {
                            console.error('Parse error:', e, 'Original message:', line);
                        }
                    }
                }

                _this.copyCode(answerNode);
                autoScroll(); // 修改后的滚动控制
            }
            _this.done(fullResponse);
        }

        async function processCustomStream(reader, decoder) {
            let fullResponse = "";
            let isThinking = false;
            let thinkingContent = "";

            while (true) {
                const {done, value} = await reader.read();
                if (done || abortController.signal.aborted) break;

                const chunk = decoder.decode(value);
                const lines = chunk.split('\n').filter(l => l.trim());

                for (const line of lines) {
                    if (line.startsWith('data:')) {
                        try {
                            const message = line.replace(/^data:/, '');
                            const parsed = JSON.parse(message);
                            const text = parsed.output.text;
                            const reasoning = parsed.output.thoughts;

                            if (reasoning?.length > 0) {
                                isThinking = true;
                                thinkingContent += reasoning[1].response;
                                thinkingNode.set("html", marked.parse(thinkingContent));
                                thinkingContainer.show();
                            }

                            fullResponse += text;
                            answerNode.set("html", marked.parse(fullResponse));
                        } catch (e) {
                            console.error('Parse error:', e);
                        }
                    }
                }

                _this.copyCode(answerNode);
                autoScroll(); // 修改后的滚动控制
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
        this.message.push({"role": "assistant", "content": fullResponse});

        this.chatNode.set("disabled", false);
        this.chatNode.focus();
        this.btnSendNode.show();
        this.btnCancelNode.hide();
        // toolNode.show();
        //
        // copyNode.addEvent("click", function () {
        //     _this.copyToClipboard(fullResponse);
        // });
    },
    setting: function () {
        const node = new Element("div");
        const url = this.path + this.options.style + "/setting.html";

        const _self = this;

        Promise.all([
            this.action.ConfigAction.listModelPaging(1, 100),
            this.action.ConfigAction.getConfig()
        ]).then(([result1, result2]) => {

            result2.data.knowledgeIndexApp = result2.data.knowledgeIndexAppList.map((d)=>{
                return d.split("|")[1];
            }).join();
            result2.data.knowledgeIndexAppValue = result2.data.knowledgeIndexAppList.join();

            node.loadHtml(url, {"bind": {"lp": _self.lp,"models" : result1.data,"config":result2.data}, "module": this}, function () {
                result1.data.forEach((d)=>{
                    new Element("oo-radio",{"text" : d.name,"value":d.name}).inject(this.modelsNode);
                })
                $OOUI.dialog("系统设置", node, this.content, {
                    buttons: 'ok, cancel', canMove: false,
                    events: {
                        "ok": function () {
                            const appName = node.querySelector("[name='appName']");
                            const title = node.querySelector("[name='title']");
                            const desc = node.querySelector("[name='desc']");
                            const appIconUrl = node.querySelector("[name='appIconUrl']");
                            const aiModel = node.querySelector("[name='aiModel']");
                            const knowledgeIndexAppList = node.querySelector("[name='knowledgeIndexAppList']");


                            _self.action.ConfigAction.saveConfig({
                                "appName" : appName.get("value"),
                                "title" : title.get("value"),
                                "desc" : desc.get("value"),
                                "appIconUrl" : appIconUrl.get("value"),
                                "aiModel" : aiModel.get("value"),
                                "knowledgeIndexAppList" :knowledgeIndexAppList.get("v")!==""?knowledgeIndexAppList.get("v").split(","):[]
                            }, function( json ){
                                this.close();
                            }.bind(this));


                        }
                    }

                });

            }.bind(this));
        }).catch(error => {

        });
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
            new o2.O2Selector(this.content, opt);
        }.bind(this), false);
    },
    showGenerateType: function(ev) {
        ev.stopPropagation();
        const node = ev.target;
        if (node.menu) return;

        const options = [
            { icon: 'networking_click', label: this.lp.types.auto + " ｜ " + this.lp.types.auto_text, type: "auto", text1: this.lp.types.auto, text2: this.lp.types.auto_text },
            { icon: 'message', label: this.lp.types.chat + " ｜ "+this.lp.types.chat_text, type: "chat", text1: this.lp.types.chat, text2: this.lp.types.chat_text },
            { icon: 'renwu', label: this.lp.types.task + " ｜ " + this.lp.types.task_text, type: "mcp", text1: this.lp.types.task, text2: this.lp.types.task_text },
            { icon: 'canyue', label: this.lp.types.knowledge + " ｜ " + this.lp.types.knowledge_text, type: "rag", text1: this.lp.types.knowledge, text2: this.lp.types.knowledge_text }
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
    speech : function (){

        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        const recognition = new SpeechRecognition();
        recognition.lang = 'zh-CN';
        recognition.interimResults = true;

        recognition.start();

        recognition.addEventListener('result', (event) => {
            console.log("Xxxxxxxx")
            const transcript = Array.from(event.results)
                .map(result => result[0])
                .map(result => result.transcript)
                .join('');

            this.chatNode.set("value",transcript)
        });

        recognition.addEventListener('end', () => {

        });
    }
});
