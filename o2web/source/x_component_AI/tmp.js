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
    onQueryLoad: function () {
        this.lp = MWF.xApplication.AI.LP;
        this.message = [];
        this.sessionId = "";
        this.action = o2.Actions.load("x_ai_assemble_control");
        this.isComposing = false;
        botIcon = "../x_component_AI/$Main/appicon.png";


    },
    loadApplication: function (callback) {
        var url = this.path + this.options.style + "/view.html";
        o2.load("../o2_lib/marked/lib/marked.js",function(){
            this.content.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function () {
                this.bindEvent();
                this.getHistory();
            }.bind(this));
        }.bind(this));
    },
    resizeDiv: function (handler, sendbox, property = 'width', minValue = 100, direction = 'right-to-left') {
        let isResizing = false;
        let startY = 0;
        let startX = 0;
        let startValue = 0;

        // 鼠标按下时开始调整大小
        handler.addEventListener('mousedown', function (e) {
            isResizing = true;
            if (property === 'height') {
                startY = e.clientY; // 如果是垂直调整，则使用clientY
            } else { // 默认处理宽度调整
                startX = e.clientX;
            }
            startValue = sendbox.style[property] ?
                parseInt(sendbox.style[property]) :
                sendbox.getBoundingClientRect()[property];
            e.preventDefault();
        });

        // 鼠标移动时调整大小
        document.addEventListener('mousemove', function (e) {
            if (!isResizing) return;

            let delta;
            if (property === 'height') {
                // 垂直调整大小
                delta = e.clientY - startY;
            } else { // 默认处理宽度调整
                delta = e.clientX - startX;
            }

            // 根据属性和方向计算变化量
            let newValue;
            if (property === 'width' && direction === 'right-to-left') {
                // 如果是从右向左调整宽度，则需要基于原始宽度加上鼠标移动的距离
                newValue = startValue + delta;
            } else { // 默认所有其他情况（包括从左到右调整宽度和任何高度调整）
                newValue = startValue - delta;
            }

            // 应用新的值并限制最小值
            if (newValue >= minValue) {
                sendbox.style[property] = `${newValue}px`;
            } else {
                sendbox.style[property] = `${minValue}px`;
            }
        });

        // 鼠标释放时停止调整大小
        document.addEventListener('mouseup', function () {
            isResizing = false;
        });
    },
    copyToClipboard: function () {
        if (navigator.clipboard) {
            // 使用现代 Clipboard API
            navigator.clipboard.writeText(text)
                .then(() => {
                    console.log('文本已成功复制到剪贴板:', text);
                    alert("已拷贝到剪切板");
                })
                .catch(err => {
                    console.error('无法复制文本:', err);
                    // 如果 Clipboard API 失败，回退到旧方法
                    copyToClipboardFallback(text);
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
                    console.log('文本已成功复制到剪贴板:', text);
                    alert("已拷贝到剪切板");
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
    bindEvent: function () {

        this.searchNode.addEvent("keyup",function (){
            this.search();
        }.bind(this));

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
                this.send();
            }
        }.bind(this));

        this.resizeDiv(this.yHandlerNode, this.chatContainerNode, "height", 100);
        this.resizeDiv(this.handerNode, this.leftNode, "width", 150);

    },
    getHistory: function () {
        this.historyListNode.empty();

        const p = this.action.ChatAction.listPaging(1,1000);

        const _this = this;

        p.then((json) => {

            if (json.data.length == 0) this.chatListNode.empty();

            json.data.forEach((d, index) => {

                const htmlTemplate = `
                    <div class="list-item">
                        <div class="list-item-text">${d.title}</div>
                        <div class="ooicon-delete list-item-op"></div>
                    </div>
            `
                const listItemNode = new Element("div", {"html": htmlTemplate}).getFirst()
                listItemNode.inject(this.historyListNode);
                listItemNode.addEvent("click", function (ev) {
                    const itemNode = ev.target.getParent(".list-item") ? ev.target.getParent(".list-item") : ev.target;
                    itemNode.addClass("list-item-c");
                    itemNode.getSiblings().removeClass("list-item-c");
                    _this.loadChat(d);
                })
                if (index === 0) {
                    _this.loadChat(d);
                    listItemNode.addClass("list-item-c");
                }

                const op = listItemNode.getElement(".list-item-op");
                op.addEvent("click", function (ev) {
                    _this.removeChat(d.id);
                })
            })
            _this.chatListNode.scrollTop = _this.chatListNode.scrollHeight;
        })
    },
    newChat: function () {
        this.message = [];
        this.sessionId = "";
        this.chatListNode.empty();
        const html = `
        <div>
        <div class="chat-list-r-warp">
            <div style="width: 50px; height: 50px;">
                <img src="${botIcon}" style="width: 100%;border-radius:50%;">
            </div>
            <div class="chat-list-r">
                <div  class="chat-list-r-text markdown-body">您好，请问有什么可以帮您的？

                <ul class="chat-list-r-list">
                </ul>
                </div>
            </div>
        </div></div>
    `;
        const el = new Element("div", {"html": html});
        el.getFirst().inject(this.chatListNode);

        // this.chatListNode.getElements("li").addEvent("click", function (ev) {
        //
        //     this.send(ev.target.get("text"));
        // }.bind(this));

    },
    removeChat: function (id) {
        const _this = this;
        const p = this.action.ChatAction.delete(id);
        p.then((json) => {
            _this.getHistory();
        })
    },
    loadChat: function (chat) {

        const _this = this;
        this.chatListNode.empty();
        this.sessionId = chat.id;
        const p = this.action.ChatAction.listCompletionPaging(chat.id,1,1000);
        this.titleNode.set("text",chat.title?chat.title:"新建会话");
        p.then(function (json){


            json.data.forEach((msg) => {


                let html;
                let el;
                html = `
                    <div class="chat-list-l-wrap">
                        <div class="chat-list-l">
                            <div class="chat-list-l-text ">${msg.input}</div></div> 
                    </div>
                `;
                el = new Element("div", {"html": html});
                el.getFirst().inject(_this.chatListNode);

                try{
                    msg.content = marked.parse(msg.content);
                }catch (e) {

                }



                html = `
        <div class="chat-list-r-warp">
            <div style="width: 50px; height: 50px;">
                <img src="${botIcon}" style="width: 100%;border-radius:50%;">
            </div>
            <div class="chat-list-r">
                <div  class="chat-list-r-text markdown-body">${msg.content}
                </div>
            </div>
        </div>
            `;


                el = new Element("div", {"html": html});
                el.getFirst().inject(_this.chatListNode);






            })



            _this.chatListNode.scrollTop = _this.chatListNode.scrollHeight;
            _this.copyCode();


        })


    },
    copyCode: function (el) {

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
    createHistoryItem : function (sessionId,msg){
        const _this = this;
        // 保存会话历史
        const htmlTemplate = `
            <div class="list-item  list-item-c">
                <div class="list-item-text">${msg}</div>
                <div class="ooicon-delete list-item-op"></div>
            </div>
        `;
        const listItemNode = new Element("div", {"html": htmlTemplate}).getFirst();

        const first = this.historyListNode.getFirst();

        if (first) {
            listItemNode.inject(first, 'before');
        } else {
            listItemNode.inject(this.historyListNode);
        }

        listItemNode.getSiblings().removeClass("list-item-c");
        listItemNode.store("d", {
            "id": this.sessionId
        })

        listItemNode.addEvent("click", function (ev) {
            const itemNode = ev.target.getParent(".list-item") ? ev.target.getParent(".list-item") : ev.target;

            listItemNode.getSiblings().removeClass("list-item-c");
            itemNode.addClass("list-item-c");

            _this.loadChat(itemNode.retrieve("d"));
        });

        const op = listItemNode.getElement(".list-item-op");
        op.addEvent("click", function (ev) {
            _this.removeChat(sessionId);
        });
    },
    send: function (text) {
        let msg = this.chatNode.get("value");

        if (text) msg = text;
        if (msg === "") return;

        const html = `<div class="chat-list-l-wrap">
            <div class="chat-list-l">
                <div class="chat-list-l-text">${msg}</div></div> 
        </div>
        `;
        const el = new Element("div", {"html": html});

        el.getFirst().inject(this.chatListNode);

        this.repl(msg);

        this.chatNode.set('value',"");
    },
    search: function () {
        var key = this.searchNode.get("value");

        MWF.require("MWF.widget.PinYin", function () {

            var pinyin = key.toPY().toLowerCase();
            var firstPY = key.toPYFirst().toLowerCase();

            this.historyListNode.getElements(".list-item").each(function (menu) {
                var menuItemText = menu.getElement(".list-item-text").get("text");

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

        abortController = new AbortController();
        let isUserScrolled = false;
        const scrollThreshold = 50;

        const html = `
            <div class="chat-list-r-warp">
                <div style="width: 50px; height: 50px;">
                    <img src="${botIcon}" style="width: 100%;border-radius:50%;">
                </div>
                <div class="chat-list-r">
                    <div class="loading-container">
                        <div class="spinner"></div>
                        <div></div>
                    </div>
                    <div class="thinking-container">
                        <div class="thinking-toggle">
                            <span class="toggle-arrow">▼</span> 收起思考过程
                        </div>
                        <div class="thinking">
                        </div>
                    </div>
                    <div class="chat-list-r-text markdown-body">
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
        const answerNode = el.getElement(".chat-list-r-text");
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
        const chatList = this.chatListNode;
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
                thinkingToggle.set("html", "<span class='toggle-arrow'>▶</span> 展开思考过程");
            } else {
                thinkingNode.show();
                thinkingToggle.set("html", "<span class='toggle-arrow'>▼</span> 收起思考过程");
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
                "clueId" : _this.sessionId,
                "generateType" : _this.chatType.get("value")
            });

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
                            if (parsed.choices?.[0]?.delta) {
                                const reasoning = parsed.choices[0].delta.reasoning_content;
                                const content = parsed.choices[0].delta.content;

                                if (reasoning) {
                                    isThinking = true;
                                    thinkingContent += reasoning;
                                    thinkingNode.set("html", marked.parse(thinkingContent));
                                    thinkingContainer.show();
                                }

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
                            }else{
                                console.log(parsed)

                                if(_this.sessionId === ""){
                                    if(parsed.path && parsed.path === "clueId"){
                                        _this.sessionId = parsed.data;
                                        _this.createHistoryItem(_this.sessionId,msg);
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
    stop : function (){
        abortController.abort();
        abortController = new AbortController();
        this.chatNode.set("disabled", false);
        this.btnSendNode.show();
        this.btnCancelNode.hide();
    },
    done : function (fullResponse){
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
    setting : function (){
        const node = new Element("div");
        const url = this.path + this.options.style + "/setting.html";


        node.loadHtml(url, {"bind": {"lp": this.lp}, "module": this}, function () {
            $OOUI.dialog("系统设置", node, this.content, {
                buttons: 'ok, cancel', canMove: false,
                events : {
                    "ok" : function (){
                        const els = node.getElements("oo-input");
                        els.each(function (el){
                            console.log(el.get("value"))
                            console.log(el.get("name"))
                        })
                        this.close();
                    }
                }

            });
        }.bind(this));

    }
});
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
              <div class="thinking"></div>
            </div>
            <div>
              <img src="${_self.config.appIconUrl}" class="imgicon">
            </div>
            <div style="display: flex">
              <div class="loading-container">
                <div style="display:flex; align-items: center; justify-content: center;">
                  <img src="../x_component_AI/$Main/default/loadding.gif" style="height:1.6rem;margin-top: .8em;">
                  <span class="shining-animation">
                    正在努力思考中
                    <span class="loading-dot">.</span>
                    <span class="loading-dot">.</span>
                    <span class="loading-dot">.</span>
                  </span>
                </div>
              </div>
              <div class="aitype"></div>
              
              <div class="msg-container">
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
    const thinkingNode = el.getElement(".thinking");
    const thinkingToggle = el.getElement(".thinking-toggle");
    const thinkingContainer = el.getElement(".thinking-container");
    const toggleArrow = el.getElement(".toggle-arrow");
    const loadingNode = el.getElement(".loading-container");
    const toolNode = el.getElement(".tools-container");
    const copyNode = el.getElement(".ooicon-window-max");
    const aitypeNode = el.getElement(".aitype");
    const _this = this;

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
            "generateType": _this.generateType,
            "materialIdList":_this.attId
        });

        loadingNode.show();
        fetch(requestOptions.url, requestOptions)
            .then(async response => {
                //console.log(response)
                if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                const contentType = response.headers.get('content-type');
                // loadingNode.hide();
                //thinkingContainer.show();

                if (contentType && contentType.includes('text/event-stream')) {
                    await processStandardStream(response.body.getReader());
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
                if(line.startsWith("event: status")){
                    loadingNode.hide();
                }
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

                                if (content.indexOf("$$mcp$$")>-1){
                                    const mcpData = JSON.parse(JSON.parse(content));
                                    console.log(mcpData)
                                    answerNode.set("html", marked.parse(mcpData.template));

                                    if(mcpData.script){
                                        eval("(function(node,data) { " + (mcpData.script + " }.bind(_self))(answerNode,mcpData.data)"));
                                    }
                                    _this.done(fullResponse);
                                    autoScroll();
                                    return;
                                }


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
                            if (_this.sessionId === "") {
                                if (parsed.generateType && parsed.clueId) {
                                    _this.sessionId = parsed.clueId;
                                    _this.loadHistory();
                                }
                            }

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

            _this.copyCode(answerNode);
            autoScroll(); // 修改后的滚动控制
        }
        _this.done(fullResponse);
    }
    run();
},
