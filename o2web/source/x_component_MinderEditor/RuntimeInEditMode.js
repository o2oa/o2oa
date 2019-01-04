
MWF.xApplication.MinderEditor.History = new Class({
    initialize : function( minder ){
        this.minder = minder;

        this.MAX_HISTORY = 100;

        this.lastSnap;
        this.patchLock;
        this.undoDiffs = [];
        this.redoDiffs = [];

        this.reset();
        minder.on('contentchange', this.changed.bind(this));
        minder.on('import', this.reset.bind(this));
        minder.on('patch', this.updateSelection.bind(this));
    },
    reset: function () {
        this.undoDiffs = [];
        this.redoDiffs = [];
        this.lastSnap = this.minder.exportJson();
    },

    makeUndoDiff: function() {
        var headSnap = this.minder.exportJson();
        var diff = MWF.xApplication.MinderEditor.JsonDiff(headSnap, this.lastSnap);
        if (diff.length) {
            this.undoDiffs.push(diff);
            while (this.undoDiffs.length > this.MAX_HISTORY) {
                this.undoDiffs.shift();
            }
            this.lastSnap = headSnap;
            return true;
        }
    },

    makeRedoDiff:function() {
        var revertSnap = this.minder.exportJson();
        this.redoDiffs.push(MWF.xApplication.MinderEditor.JsonDiff(revertSnap, this.lastSnap));
        this.lastSnap = revertSnap;
    },

    undo: function() {
        this.patchLock = true;
        var undoDiff = this.undoDiffs.pop();
        if (undoDiff) {
            this.minder.applyPatches(undoDiff);
            this.makeRedoDiff();
        }
        this.patchLock = false;
    },

    redo: function() {
        this.patchLock = true;
        var redoDiff = this.redoDiffs.pop();
        if (redoDiff) {
            this.minder.applyPatches(redoDiff);
            this.makeUndoDiff();
        }
        this.patchLock = false;
    },

    changed: function() {
        if (this.patchLock) return;
        if (this.makeUndoDiff()) this.redoDiffs = [];
    },

    hasUndo: function() {
        return !!this.undoDiffs.length;
    },

    hasRedo: function() {
        return !!this.redoDiffs.length;
    },

    updateSelection: function(e) {
        if (!this.patchLock) return;
        var patch = e.patch;
        switch (patch.express) {
            case 'node.add':
                this.minder.select(patch.node.getChild(patch.index), true);
                break;
            case 'node.remove':
            case 'data.replace':
            case 'data.remove':
            case 'data.add':
                this.minder.select(patch.node, true);
                break;
        }
    }
});

/**
 * @Desc: 新增一个用于处理系统ctrl+c ctrl+v等方式导入导出节点的MIMETYPE处理，如系统不支持clipboardEvent或者是FF则不初始化改class
 * @Editor: Naixor
 * @Date: 2015.9.21
 */
MWF.xApplication.MinderEditor.ClipboardMimeType = new Class({
    initialize : function(){
        this.SPLITOR = '\uFEFF';
        this.MIMETYPE = {
            'application/km': '\uFFFF'
        };
        this.SIGN = {
            '\uFEFF': 'SPLITOR',
            '\uFFFF': 'application/km'
        };
    },
    /**
     * 用于将一段纯文本封装成符合其数据格式的文本
     * @method process 			private
     * @param  {MIMETYPE} mimetype 数据格式
     * @param  {String} text     原始文本
     * @return {String}          符合该数据格式下的文本
     * @example
     * 			var str = "123";
     * 			str = process('application/km', str); // 返回的内容再经过MimeType判断会读取出其数据格式为application/km
     * 			process('text/plain', str); // 若接受到一个非纯文本信息，则会将其转换为新的数据格式
     */
    process: function(mimetype, text) {
        text = text || "";
        if (!this.isPureText(text)) {
            var _mimetype = this.whichMimeType(text);
            if (!_mimetype) {
                throw new Error('unknow mimetype!');
            }
            text = this.getPureText(text);
        }
        if (mimetype === false) {
            return text;
        }
        return mimetype + this.SPLITOR + text;
    },

    /**
     * 注册数据类型的标识
     * @method registMimeTypeProtocol  	public
     * @param  {String} type 数据类型
     * @param  {String} sign 标识
     */
    registMimeTypeProtocol : function(type, sign) {
        if (sign && this.SIGN[sign]) {
            throw new Error('sing has registed!');
        }
        if (type && !!this.MIMETYPE[type]) {
            throw new Error('mimetype has registed!');
        }
        this.SIGN[sign] = type;
        this.MIMETYPE[type] = sign;
    },

    /**
     * 获取已注册数据类型的协议
     * @method getMimeTypeProtocol  	public
     * @param  {String} type 数据类型
     * @param  {String} text|undefiend  文本内容或不传入
     * @return {String|Function}
     * @example
     * 			text若不传入则直接返回对应数据格式的处理(process)方法
     * 			若传入文本则直接调用对应的process方法进行处理，此时返回处理后的内容
     * 			var m = new MimeType();
     * 			var kmprocess = m.getMimeTypeProtocol('application/km');
     * 			kmprocess("123") === m.getMimeTypeProtocol('application/km', "123");
     *
     */
    getMimeTypeProtocol : function(type, text) {
        var mimetype = this.MIMETYPE[type] || false;

        if (text === undefined) {
            return this.process(mimetype);
        };

        return this.process(mimetype, text);
    },
    getSpitor : function() {
        return this.SPLITOR;
    },

    getMimeType : function(sign) {
        if (sign !== undefined) {
            return this.SIGN[sign] || null;
        }
        return this.MIMETYPE;
    },

    isPureText : function(text) {
        if( !text )return true;
        return !(~text.indexOf(this.getSpitor()));
    },

    getPureText : function(text) {
        if (this.isPureText(text)) {
            return text;
        };
        return text.split(this.getSpitor())[1];
    },

    whichMimeType : function(text) {
        if (this.isPureText(text)) {
            return null;
        };
        return this.getMimeType(text.split(this.getSpitor())[0]);
    }
});

MWF.xApplication.MinderEditor.Clipboard = new Class({
    initialize : function( editor ){
        this.editor = editor;
        this.minder = editor.minder;
        this.Data = window.kityminder.data;

        if (!this.minder.supportClipboardEvent || kity.Browser.gecko) {
            return;
        }

        this.fsm = this.editor.fsm;
        this.receiver = this.editor.receiver;
        this.MimeType = this.editor.MimeType;

        this.kmencode = this.MimeType.getMimeTypeProtocol('application/km');
        this.decode = this.Data.getRegisterProtocol('json').decode;
        this._selectedNodes = [];


        /**
         * 由editor的receiver统一处理全部事件，包括clipboard事件
         * @Editor: Naixor
         * @Date: 2015.9.24
         */
        document.addEventListener('copy', this.beforeCopy.bind(this));
        document.addEventListener('cut', this.beforeCut.bind(this));
        document.addEventListener('paste', this.beforePaste.bind(this));
    },
    /*
     * 增加对多节点赋值粘贴的处理
     */
    encode: function (nodes) {
        var _nodes = [];
        for (var i = 0, l = nodes.length; i < l; i++) {
            _nodes.push( this.minder.exportNode(nodes[i]));
        }
        return kmencode(  this.Data.getRegisterProtocol('json').encode(_nodes));
    },

    beforeCopy : function (e) {
        if (document.activeElement == this.receiver.element) {
            var clipBoardEvent = e;
            var state = this.fsm.state();

            switch (state) {
                case 'input': {
                    break;
                }
                case 'normal': {
                    var nodes = [].concat(this.minder.getSelectedNodes());
                    if (nodes.length) {
                        // 这里由于被粘贴复制的节点的id信息也都一样，故做此算法
                        // 这里有个疑问，使用node.getParent()或者node.parent会离奇导致出现非选中节点被渲染成选中节点，因此使用isAncestorOf，而没有使用自行回溯的方式
                        if (nodes.length > 1) {
                            var targetLevel;
                            nodes.sort(function(a, b) {
                                return a.getLevel() - b.getLevel();
                            });
                            targetLevel = nodes[0].getLevel();
                            if (targetLevel !== nodes[nodes.length-1].getLevel()) {
                                var plevel, pnode,
                                    idx = 0, l = nodes.length, pidx = l-1;

                                pnode = nodes[pidx];

                                while (pnode.getLevel() !== targetLevel) {
                                    idx = 0;
                                    while (idx < l && nodes[idx].getLevel() === targetLevel) {
                                        if (nodes[idx].isAncestorOf(pnode)) {
                                            nodes.splice(pidx, 1);
                                            break;
                                        }
                                        idx++;
                                    }
                                    pidx--;
                                    pnode = nodes[pidx];
                                }
                            };
                        };
                        var str = encode(nodes);
                        clipBoardEvent.clipboardData.setData('text/plain', str);
                    }
                    e.preventDefault();
                    break;
                }
            }
        }
    },

    beforeCut : function (e) {
        if (document.activeElement == this.receiver.element) {
            if (this.minder.getStatus() !== 'normal') {
                e.preventDefault();
                return;
            };

            var clipBoardEvent = e;
            var state = this.fsm.state();

            switch (this.state) {
                case 'input': {
                    break;
                }
                case 'normal': {
                    var nodes = this.minder.getSelectedNodes();
                    if (nodes.length) {
                        clipBoardEvent.clipboardData.setData('text/plain', encode(nodes));
                        this.minder.execCommand('removenode');
                    }
                    e.preventDefault();
                    break;
                }
            }
        }
    },

    beforePaste : function(e) {
        if (document.activeElement == this.receiver.element) {
            if (this.minder.getStatus() !== 'normal') {
                e.preventDefault();
                return;
            };

            var clipBoardEvent = e;
            var state = this.fsm.state();
            var textData = clipBoardEvent.clipboardData.getData('text/plain');

            switch (state) {
                case 'input': {
                    // input状态下如果格式为application/km则不进行paste操作
                    if (!this.MimeType.isPureText(textData)) {
                        e.preventDefault();
                        return;
                    };
                    break;
                }
                case 'normal': {
                    /*
                     * 针对normal状态下通过对选中节点粘贴导入子节点文本进行单独处理
                     */
                    var sNodes = this.minder.getSelectedNodes();

                    if (this.MimeType.whichMimeType(textData) === 'application/km') {
                        var nodes = this.decode(this.MimeType.getPureText(textData));
                        var _node;
                        sNodes.forEach(function(node) {
                            // 由于粘贴逻辑中为了排除子节点重新排序导致逆序，因此复制的时候倒过来
                            for (var i = nodes.length-1; i >= 0; i--) {
                                _node = this.minder.createNode(null, node);
                                this.minder.importNode(_node, nodes[i]);
                                this._selectedNodes.push(_node);
                                node.appendChild(_node);
                            }
                        });
                        this.minder.select(this._selectedNodes, true);
                        this._selectedNodes = [];

                        this.minder.refresh();
                    }else if (clipBoardEvent.clipboardData && clipBoardEvent.clipboardData.items[0].type.indexOf('image') > -1) {
                        //var imageFile = clipBoardEvent.clipboardData.items[0].getAsFile();
                        //var serverService = angular.element(document.body).injector().get('server');
                        //
                        //return serverService.uploadImage(imageFile).then(function (json) {
                        //    var resp = json.data;
                        //    if (resp.errno === 0) {
                        //        this.minder.execCommand('image', resp.data.url);
                        //    }
                        //});
                    }
                    else {
                        sNodes.forEach(function(node) {
                            this.minder.Text2Children(node, textData);
                        });
                    }
                    e.preventDefault();
                    break;
                }
            }
        }
    }

});

MWF.xApplication.MinderEditor.Input = new Class({
    initialize : function( editor ){
        this.editor = editor;
        this.fsm = editor.fsm;
        this.minder = editor.minder;
        this.popmenu = editor.popmenu;
        this.receiver = editor.receiver;
        this.receiverElement = this.receiver.element;
        this.isGecko = window.kity.Browser.gecko;
        this.debug = this.editor.debug;
        this.setupReciverElement();
        this.setupFsm();
        this.setupPopmenu();
    },
    setupFsm: function () {

        // when jumped to input mode, enter
        this.fsm.when('* -> input', this.enterInputMode.bind(this));

        // when exited, commit or exit depends on the exit reason
        this.fsm.when('input -> *', function(exit, enter, reason) {
            switch (reason) {
                case 'input-cancel':
                    return this.exitInputMode();
                case 'input-commit':
                default:
                    return this.commitInputResult();
            }
        }.bind(this));

        // lost focus to commit
        this.receiver.onblur(function (e) {
            if (this.fsm.state() == 'input') {
                this.fsm.jump('normal', 'input-commit');
            }
        }.bind(this));

        this.minder.on('beforemousedown', function () {
            if (this.fsm.state() == 'input') {
                this.fsm.jump('normal', 'input-commit');
            }
        }.bind(this));

        this.minder.on('dblclick', function() {
            if (this.minder.getSelectedNode() && this.minder._status !== 'readonly') {
                this.editText();
            }
        }.bind(this));
    },


// let the receiver follow the current selected node position
    setupReciverElement: function () {
        if (this.debug.flaged) {
            this.receiverElement.classList.add('debug');
        }

        this.receiverElement.onmousedown = function(e) {
            e.stopPropagation();
        };

        this.minder.on('layoutallfinish viewchange viewchanged selectionchange', function(e) {
            // viewchange event is too frequenced, lazy it
            if (e.type == 'viewchange' && this.fsm.state() != 'input') return;

            this.updatePosition();
        }.bind(this));

        this.updatePosition();
    },

// edit entrance in popmenu
    setupPopmenu: function () {
        //popmenu.state('main').button({
        //    position: 'center',
        //    label: '编辑',
        //    key: 'F2',
        //    enable: function() {
        //        return minder.queryCommandState('text') != -1;
        //    },
        //    action: editText
        //});
    },


    /**
     * 增加对字体的鉴别，以保证用户在编辑状态ctrl/cmd + b/i所触发的加粗斜体与显示一致
     * @editor Naixor
     * @Date 2015-12-2
     */
// edit for the selected node
    editText: function() {
        var node = this.minder.getSelectedNode();
        if (!node) {
            return;
        }
        var textContainer = this.receiverElement;
        this.receiverElement.innerText = "";

        if (node.getData('font-weight') === 'bold') {
            var b = document.createElement('b');
            textContainer.appendChild(b);
            textContainer = b;
        }
        if (node.getData('font-style') === 'italic') {
            var i = document.createElement('i');
            textContainer.appendChild(i);
            textContainer = i;
        }
        textContainer.innerText = this.minder.queryCommandValue('text') || "";

        if (this.isGecko) {
            this.receiver.fixFFCaretDisappeared();
        }
        this.fsm.jump('input', 'input-request');
        this.receiver.selectAll();
    },

    /**
     * 增加对字体的鉴别，以保证用户在编辑状态ctrl/cmd + b/i所触发的加粗斜体与显示一致
     * @editor Naixor
     * @Date 2015-12-2
     */
    enterInputMode: function() {
        var node = this.minder.getSelectedNode();
        var receiverElement = this.receiverElement;
        //receiverElement.contentEditable = true;
        if (node) {
            var fontSize = node.getData('font-size') || node.getStyle('font-size');
            receiverElement.style.fontSize = fontSize + 'px';
            receiverElement.style.minWidth = 0;
            receiverElement.style.display = "";
            receiverElement.style.minWidth = receiverElement.clientWidth + 'px';
            receiverElement.style.fontWeight = node.getData('font-weight') || '';
            receiverElement.style.fontStyle = node.getData('font-style') || '';
            receiverElement.classList.add('input');
            receiverElement.focus();
        }
    },

    /**
     * 按照文本提交操作处理
     * @Desc: 从其他节点复制文字到另一个节点时部分浏览器(chrome)会自动包裹一个span标签，这样试用一下逻辑出来的就不是text节点二是span节点因此导致undefined的情况发生
     * @Warning: 下方代码使用[].slice.call来将HTMLDomCollection处理成为Array，ie8及以下会有问题
     * @Editor: Naixor
     * @Date: 2015.9.16
     */
    commitInputText: function(textNodes) {
        var text = '';
        var TAB_CHAR = '\t',
            ENTER_CHAR = '\n',
            STR_CHECK = /\S/,
            SPACE_CHAR = '\u0020',
        // 针对FF,SG,BD,LB,IE等浏览器下SPACE的charCode存在为32和160的情况做处理
            SPACE_CHAR_REGEXP = new RegExp('(\u0020|' + String.fromCharCode(160) + ')'),
            BR = document.createElement('br');
        var isBold = false,
            isItalic = false;

        for (var str,
                 _divChildNodes,
                 space_l, space_num, tab_num,
                 i = 0, l = textNodes.length; i < l; i++) {
            str = textNodes[i];

            switch (Object.prototype.toString.call(str)) {
                // 正常情况处理
                case '[object HTMLBRElement]': {
                    text += ENTER_CHAR;
                    break;
                }
                case '[object Text]': {
                    // SG下会莫名其妙的加上&nbsp;影响后续判断，干掉！
                    /**
                     * FF下的wholeText会导致如下问题：
                     *     |123| -> 在一个节点中输入一段字符，此时TextNode为[#Text 123]
                     *     提交并重新编辑，在后面追加几个字符
                     *     |123abc| -> 此时123为一个TextNode为[#Text 123, #Text abc]，但是对这两个任意取值wholeText均为全部内容123abc
                     * 上述BUG仅存在在FF中，故将wholeText更改为textContent
                     */
                    str = str.textContent.replace("&nbsp;", " ");

                    if (!STR_CHECK.test(str)) {
                        space_l = str.length;
                        while (space_l--) {
                            if (SPACE_CHAR_REGEXP.test(str[space_l])) {
                                text += SPACE_CHAR;
                            } else if (str[space_l] === TAB_CHAR) {
                                text += TAB_CHAR;
                            }
                        }
                    } else {
                        text += str;
                    }
                    break;
                }
                // ctrl + b/i 会给字体加上<b>/<i>标签来实现黑体和斜体
                case '[object HTMLElement]': {
                    switch (str.nodeName) {
                        case "B": {
                            isBold = true;
                            break;
                        }
                        case "I": {
                            isItalic = true;
                            break;
                        }
                        default: {}
                    }
                    [].splice.apply(textNodes, [i, 1].concat([].slice.call(str.childNodes)));
                    l = textNodes.length;
                    i--;
                    break;
                }
                // 被增加span标签的情况会被处理成正常情况并会推交给上面处理
                case '[object HTMLSpanElement]': {
                    [].splice.apply(textNodes, [i, 1].concat([].slice.call(str.childNodes)));
                    l = textNodes.length;
                    i--;
                    break;
                }
                // 若标签为image标签，则判断是否为合法url，是将其加载进来
                case '[object HTMLImageElement]': {
                    if (str.src) {
                        if (/http(|s):\/\//.test(str.src)) {
                            minder.execCommand("Image", str.src, str.alt);
                        } else {
                            // data:image协议情况
                        }
                    };
                    break;
                }
                // 被增加div标签的情况会被处理成正常情况并会推交给上面处理
                case '[object HTMLDivElement]': {
                    _divChildNodes = [];
                    for (var di = 0, l = str.childNodes.length; di < l; di++) {
                        _divChildNodes.push(str.childNodes[di]);
                    }
                    _divChildNodes.push(BR);
                    [].splice.apply(textNodes, [i, 1].concat(_divChildNodes));
                    l = textNodes.length;
                    i--;
                    break;
                }
                default: {
                    if (str && str.childNodes.length) {
                        _divChildNodes = [];
                        for (var di = 0, l = str.childNodes.length; di < l; di++) {
                            _divChildNodes.push(str.childNodes[di]);
                        }
                        _divChildNodes.push(BR);
                        [].splice.apply(textNodes, [i, 1].concat(_divChildNodes));
                        l = textNodes.length;
                        i--;
                    } else {
                        if (str && str.textContent !== undefined) {
                            text += str.textContent;
                        } else {
                            text += "";
                        }
                    }
                    // // 其他带有样式的节点被粘贴进来，则直接取textContent，若取不出来则置空
                }
            }
        };

        text = text.replace(/^\n*|\n*$/g, '');
        text = text.replace(new RegExp('(\n|\r|\n\r)(\u0020|' + String.fromCharCode(160) + '){4}', 'g'), '$1\t');

        this.minder.getSelectedNode().setText(text);
        if (isBold) {
            this.minder.queryCommandState('bold') || this.minder.execCommand('bold');
        } else {
            this.minder.queryCommandState('bold') && this.minder.execCommand('bold');
        }

        if (isItalic) {
            this.minder.queryCommandState('italic') || this.minder.execCommand('italic');
        } else {
            this.minder.queryCommandState('italic') && this.minder.execCommand('italic');
        }
        this.exitInputMode();
        return text;
    },

    /**
     * 判断节点的文本信息是否是
     * @Desc: 从其他节点复制文字到另一个节点时部分浏览器(chrome)会自动包裹一个span标签，这样使用以下逻辑出来的就不是text节点二是span节点因此导致undefined的情况发生
     * @Notice: 此处逻辑应该拆分到 kityminder-core/core/data中去，单独增加一个对某个节点importJson的事件
     * @Editor: Naixor
     * @Date: 2015.9.16
     */
    commitInputNode: function (node, text) {
        try {
            this.minder.decodeData('text', text).then(function(json) {
                function importText(node, json, minder) {
                    var data = json.data;

                    node.setText(data.text || '');

                    var childrenTreeData = json.children || [];
                    for (var i = 0; i < childrenTreeData.length; i++) {
                        var childNode = minder.createNode(null, node);
                        importText(childNode, childrenTreeData[i], minder);
                    }
                    return node;
                }
                importText(node, json, this.minder);
                this.minder.fire("contentchange");
                this.minder.getRoot().renderTree();
                this.minder.layout(300);
            }.bind(this));
        } catch (e) {
            this.minder.fire("contentchange");
            this.minder.getRoot().renderTree();

            // 无法被转换成脑图节点则不处理
            if (e.toString() !== 'Error: Invalid local format') {
                throw e;
            }
        }
    },

    commitInputResult: function() {
        /**
         * @Desc: 进行如下处理：
         *             根据用户的输入判断是否生成新的节点
         *        fix #83 https://github.com/fex-team/kityminder-editor/issues/83
         * @Editor: Naixor
         * @Date: 2015.9.16
         */
        var textNodes = [].slice.call(this.receiverElement.childNodes);

        /**
         * @Desc: 增加setTimeout的原因：ie下receiverElement.innerHTML=""会导致后
         * 		  面commitInputText中使用textContent报错，不要问我什么原因！
         * @Editor: Naixor
         * @Date: 2015.12.14
         */
        setTimeout(function () {
            // 解决过大内容导致SVG窜位问题
            this.receiverElement.innerHTML = "";
        }.bind(this), 0);
        var node = this.minder.getSelectedNode();

        textNodes = this.commitInputText(textNodes);
        this.commitInputNode(node, textNodes);

        if (node.type == 'root') {
            var rootText = this.minder.getRoot().getText();
            this.minder.fire('initChangeRoot', {text: rootText});
        }
    },

    exitInputMode: function () {
        //this.receiverElement.contentEditable = false;
        this.receiverElement.style.cursor = "default";
        this.receiverElement.classList.remove('input');
        this.receiver.selectAll();
    },

    updatePosition: function() {
        var focusNode = this.minder.getSelectedNode();
        if (!focusNode) return;

        if (!this.timer) {
            this.timer = setTimeout(function() {
                var box = focusNode.getRenderBox('TextRenderer');
                this.receiverElement.style.left = Math.round(box.x) + 'px';
                this.receiverElement.style.top = (this.debug.flaged ? Math.round(box.bottom + 30) : Math.round(box.y)) + 'px';
                //receiverElement.focus();
                this.timer = 0;
            }.bind(this));
        }
    }
});

//根据按键控制状态机的跳转
MWF.xApplication.MinderEditor.JumpingInEditMode = function( editor ) {
/**
 * @Desc: 下方使用receiver.enable()和receiver.disable()通过
 *        修改div contenteditable属性的hack来解决开启热核后依然无法屏蔽浏览器输入的bug;
 *        特别: win下FF对于此种情况必须要先blur在focus才能解决，但是由于这样做会导致用户
 *             输入法状态丢失，因此对FF暂不做处理
 * @Editor: Naixor
 * @Date: 2015.09.14
 */
    var fsm = editor.fsm;
    var minder = editor.minder;
    var receiver = editor.receiver;
    var container = editor.contentNode;
    var receiverElement = receiver.element;
    var popmenu = editor.popmenu;

    // Nice: http://unixpapa.com/js/key.html
    function isIntendToInput(e) {
        if (e.ctrlKey || e.metaKey || e.altKey) return false;

        // a-zA-Z
        if (e.keyCode >= 65 && e.keyCode <= 90) return true;

        // 0-9 以及其上面的符号
        if (e.keyCode >= 48 && e.keyCode <= 57) return true;

        // 小键盘区域 (除回车外)
        if (e.keyCode != 108 && e.keyCode >= 96 && e.keyCode <= 111) return true;

        // 小键盘区域 (除回车外)
        // @yinheli from pull request
        if (e.keyCode != 108 && e.keyCode >= 96 && e.keyCode <= 111) return true;

        // 输入法
        if (e.keyCode == 229 || e.keyCode === 0) return true;

        return false;
    }

    // normal -> *
    receiver.listen('normal', function(e) {
        //console.log(  "receiver.listen('normal'" );
        // 为了防止处理进入edit模式而丢失处理的首字母,此时receiver必须为enable
        receiver.enable();
        // normal -> popmenu
        if (e.is('Space')) {
            e.preventDefault();
            // safari下Space触发popmenu,然而这时Space已在receiver上留下作案痕迹,因此抹掉
            if (kity.Browser.safari) {
                receiverElement.innerHTML = '';
            }
            return fsm.jump('popmenu', 'space-trigger');
        }

        /**
         * check
         * @editor Naixor
         * @Date 2015-12-2
         */
        switch (e.type) {
            case 'keydown': {
                if (minder.getSelectedNode()) {
                    if (isIntendToInput(e)) {
                        return fsm.jump('input', 'user-input');
                    }
                } else {
                    receiverElement.innerHTML = '';
                }
                // normal -> normal shortcut
                fsm.jump('normal', 'shortcut-handle', e);
                break;
            }
            case 'keyup': {
                break;
            }
            default: {}
        }
    });

    // popmenu -> normal
    receiver.listen('popmenu', function(e) {
        //console.log(  "receiver.listen('popmenu')" );
        receiver.disable();
        e.preventDefault();
        var handleResult = popmenu.dispatch(e);
        if (popmenu.state() == Popmenu.STATE_IDLE && fsm.state() == 'popmenu') {
            return fsm.jump('normal', 'popmenu-idle');
        }
        //else{
        //    return fsm.jump('normal', 'shortcut-handle', e);
        //}
    });

    // input => normal
    receiver.listen('input', function(e) {
        //console.log(  "receiver.listen('input'" );
        receiver.enable();
        if (e.type == 'keydown') {
            if (e.is('Enter')) {
                e.preventDefault();
                return fsm.jump('normal', 'input-commit');
            }
            if (e.is('Esc')) {
                e.preventDefault();
                return fsm.jump('normal', 'input-cancel');
            }
            if (e.is('Tab') || e.is('Shift + Tab')) {
                e.preventDefault();
            }
        } else if (e.type == 'keyup' && e.is('Esc')) {
            e.preventDefault();
            return fsm.jump('normal', 'input-cancel');
        }
    });


    //////////////////////////////////////////////
    /// 右键呼出热盒
    /// 判断的标准是：按下的位置和结束的位置一致
    //////////////////////////////////////////////
    var downX, downY;
    var MOUSE_RB = 2; // 右键

    container.addEventListener('mousedown', function(e) {
        if (e.button == MOUSE_RB) {
            e.preventDefault();
        }

        if (fsm.state() == 'popmenu') {
            popmenu.active(Popmenu.STATE_IDLE);
            fsm.jump('normal', 'blur');
            downX = e.clientX;
            downY = e.clientY;
        } else if (fsm.state() == 'normal' && e.button == MOUSE_RB) {
            downX = e.clientX;
            downY = e.clientY;
        }
    }, false);

    container.addEventListener('mousewheel', function(e) {
        if (fsm.state() == 'popmenu') {
            popmenu.active(Popmenu.STATE_IDLE);
            fsm.jump('normal', 'mousemove-blur');
        }
    }, false);

    container.addEventListener('contextmenu', function(e) {
        e.preventDefault();
    });

    container.addEventListener('mouseup', function(e) {
        if (fsm.state() != 'normal' ) {
            return;
        }
        if (e.button != MOUSE_RB || e.clientX != downX || e.clientY != downY) {
            return;
        }
        if (!minder.getSelectedNode()) {
            return;
        }
        fsm.jump('popmenu', 'content-menu');
    }, false);

    // 阻止热盒事件冒泡，在热盒正确执行前导致热盒关闭
    //popmenu.$element.addEventListener('mousedown', function(e) {
    //    e.stopPropagation();
    //});
};

//
//MWF.xApplication.MinderEditor.Popmenu = function( editor ){
//    var fsm = editor.fsm;
//    var minder = editor.minder;
//    var receiver = editor.receiver;
//    var container = editor.contentNode;
//
//    var popmenu = new Hotbox(container);
//
//    popmenu.setParentFSM(fsm);
//
//    fsm.when('normal -> popmenu', function(exit, enter, reason) {
//        var node = minder.getSelectedNode();
//        var position;
//        if (node) {
//            var box = node.getRenderBox();
//            position = {
//                x: box.cx,
//                y: box.cy
//            };
//        }
//        popmenu.active('main', position);
//    });
//
//    fsm.when('normal -> normal', function(exit, enter, reason, e) {
//        if (reason == 'shortcut-handle') {
//            var handleResult = popmenu.dispatch(e);
//            if (handleResult) {
//                e.preventDefault();
//            } else {
//                minder.dispatchKeyEvent(e);
//            }
//        }
//    });
//
//    fsm.when('modal -> normal', function(exit, enter, reason, e) {
//        if (reason == 'import-text-finish') {
//            receiver.element.focus();
//        }
//    });
//}
