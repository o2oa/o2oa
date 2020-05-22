MWF.xApplication.MinderEditor.Commands = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "type": "common",
        "style": "default"
    },
    initialize: function ( editor, options) {
        this.setOptions(options);
        this.app = editor;
        this.lp = MWF.xApplication.MinderEditor.LP;
        //this.actions = this.app.restActions;
        this.editor = editor;
        this.minder = editor.minder;
        if( !editor.debug ){
            MWF.xDesktop.requireApp("MinderEditor", "Tools", null, false);
            MWF.xDesktop.requireApp("MinderEditor", "RuntimeInCommon", null, false);
            MWF.xDesktop.requireApp("MinderEditor", "WidgetInCommon", null, false);
            this.editor.debug = new MWF.xApplication.MinderEditor.Debug(true);
        }
        if( !editor.fsm ){
            this.editor.fsm = new MWF.xApplication.MinderEditor.FSM('normal');
        }
        if( !editor.key ){
            this.editor.key = new MWF.xApplication.MinderEditor.Key();
        }
        if( !editor.receiver ){
            this.editor.receiver = new MWF.xApplication.MinderEditor.Receiver(this.editor);
        }
        this.fsm = this.editor.fsm;
        this.receiver = this.editor.receiver;
        //this.input = this.editor.input;
        //this.drag = this.editor.drag;
        this.history = this.editor.history;
        //this.key = this.editor.key;

        this.path = "../x_component_MinderEditor/$Commands/";
        this.cssPath = this.path+this.options.style+"/css.wcss";

        this.commands = {
            undo: {
                icon: "undo",
                locale: "撤销",
                modle : ["edit"],
                key: "Ctrl + Z",
                disable: function () {
                    return this.history.hasUndo() == false;
                }.bind(this),
                action: function () {
                    this.history.hasUndo() == false || this.history.undo();
                }.bind(this)
            },
            redo: {
                icon: "redo",
                locale: "重做",
                modle : ["edit"],
                key: "Ctrl + Y",
                disable: function () {
                    return this.history.hasRedo() == false;
                }.bind(this),
                action: function () {
                    this.history.hasRedo() == false || this.history.redo();
                }.bind(this)
            },
            appendChild: {
                icon: "insert_sub",
                modle : ["edit"],
                locale: "插入下级主题",
                key: ["Tab", "Insert"],
                isDefaultKey: true,
                disable: function () {
                    return this.minder.queryCommandState('AppendChildNode') === -1;
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('AppendChildNode') === -1 || this.minder.execCommand('AppendChildNode')
                }.bind(this)
            },
            appendParent: {
                icon: "insert_par",
                modle : ["edit"],
                locale: "插入上级主题",
                key: "Shit + Tab",
                disable: function () {
                    return this.minder.queryCommandState('AppendParentNode') === -1;
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('AppendParentNode') === -1 || this.minder.execCommand('AppendParentNode')
                }.bind(this)
            },
            appendSibling: {
                icon: "insert_sibling",
                modle : ["edit"],
                locale: "插入同级主题",
                key: "Enter",
                isDefaultKey: true,
                disable: function () {
                    return this.minder.queryCommandState('AppendSiblingNode') === -1;
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('AppendSiblingNode') === -1 || this.minder.execCommand('AppendSiblingNode')
                }.bind(this)
            },
            arrangeUp: {
                icon: "up",
                modle : ["edit"],
                locale: "上移",
                key: "Alt + Up",
                isDefaultKey: true,
                disable: function () {
                    return this.minder.queryCommandState('ArrangeUp') === -1
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('ArrangeUp') === -1 || this.minder.execCommand('ArrangeUp')
                }.bind(this)
            },
            arrangeDown: {
                icon: "down",
                modle : ["edit"],
                locale: "下移",
                key: "Alt + Down",
                isDefaultKey: true,
                disable: function () {
                    return this.minder.queryCommandState('ArrangeDown') === -1
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('ArrangeDown') === -1 || this.minder.execCommand('ArrangeDown')
                }.bind(this)
            },
            edit: {
                icon: "edit",
                modle : ["edit"],
                locale: "编辑",
                key: "F2",
                disable: function () {
                    return this.minder.queryCommandState('text') === -1
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('text') === -1 || this.editNode()
                }.bind(this)
            },
            remove: {
                icon: "delete",
                modle : ["edit"],
                locale: "删除",
                key: "Delete",
                isDefaultKey: true,
                disable: function () {
                    return this.minder.queryCommandState('RemoveNode') === -1
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('RemoveNode') === -1 || this.minder.execCommand('RemoveNode');
                }.bind(this)
            },
            hyperLink: {
                icon: "link",
                modle : ["edit"],
                locale: "链接",
                key: "Alt + L",
                disable: function () {
                    return this.minder.queryCommandState('HyperLink') === -1
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('HyperLink') === -1 || this.openHyperLinkForm("hyperLink");
                }.bind(this)
            },
            image: {
                icon: "image",
                modle : ["edit"],
                locale: "图片",
                key: "Alt + M",
                disable: function () {
                    return this.minder.queryCommandState('Image') === -1
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('Image') === -1 || this.openImageForm("image");
                }.bind(this)
            },
            note: {
                title: "备注",
                modle : ["edit"],
                disable: function () {
                    return this.minder.queryCommandState('note') === -1
                }.bind(this),
                init: function (container, command) {
                    this.createNoteEditor(container, "note");
                }.bind(this),
                setDisable: function () {
                    this.setNoteDisable()
                }.bind(this),
                setNormal: function () {
                    this.setNoteNormal()
                }.bind(this)
            },
            priority: {
                icon: "priority",
                modle : ["edit"],
                locale: "优先级",
                key: "Alt + G",
                disable: function () {
                    return this.minder.queryCommandState("priority") === -1
                }.bind(this),
                keyAction: function () {
                    this.prioritySelector.showTooltip()
                }.bind(this),
                init: function (container, command) {
                    this.initPriority(container, command)
                }.bind(this)
            },
            progress: {
                icon: "progress",
                modle : ["edit"],
                locale: "进度",
                key: "Alt + P",
                disable: function () {
                    return this.minder.queryCommandState("progress") === -1
                }.bind(this),
                keyAction: function () {
                    this.progressSelector.showTooltip()
                }.bind(this),
                init: function (container, command) {
                    this.initProgress(container, command)
                }.bind(this)
            },
            resource: {
                title: "标签",
                modle : ["edit"],
                disable: function () {
                    return this.minder.queryCommandState('resource') === -1
                }.bind(this),
                init: function (container, command) {
                    this.initResource(container, command)
                }.bind(this),
                setDisable: function () {
                    this.setResourceDisable()
                }.bind(this),
                setNormal: function () {
                    this.setResourceNormal()
                }.bind(this)
            },
            template: {
                icon: "template",
                modle : ["edit","read"],
                title: "模板",
                disable: function () {
                    return this.minder.queryCommandState('template') === -1
                }.bind(this),
                init: function (container, command) {
                    this.initTemplate(container, command)
                }.bind(this)
            },
            theme: {
                icon: "theme",
                modle : ["edit","read"],
                title: "主题",
                disable: function () {
                    return this.minder.queryCommandState('theme') === -1
                }.bind(this),
                init: function (container, command) {
                    this.initTheme(container, command)
                }.bind(this)
            },

            resetlayout: {
                icon: "resetlayout",
                modle : ["edit","read"],
                title: "整理布局",
                key: "Ctrl + Shift + L",
                isDefaultKey: true,
                disable: function () {
                    return this.minder.queryCommandState('resetlayout') === -1
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('resetlayout') === -1 || this.minder.execCommand('resetlayout');
                }.bind(this)
            },
            clearstyle: {
                icon: "clear",
                modle : ["edit"],
                locale: "清除样式",
                disable: function () {
                    return this.minder.queryCommandState('clearstyle') === -1
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('clearstyle') === -1 || this.minder.execCommand('clearstyle');
                }.bind(this)
            },
            copystyle: {
                icon: "copystyle",
                modle : ["edit"],
                locale: "拷贝样式",
                disable: function () {
                    return this.minder.queryCommandState('copystyle') === -1
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('copystyle') === -1 || this.minder.execCommand('copystyle');
                }.bind(this)
            },
            pastestyle: {
                icon: "pastestyle",
                modle : ["edit"],
                locale: "粘贴样式",
                disable: function () {
                    return this.minder.queryCommandState('pastestyle') === -1
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('pastestyle') === -1 || this.minder.execCommand('pastestyle');
                }.bind(this)
            },

            fontfamily: {
                title: "字体",
                modle : ["edit"],
                //key : "Alt + T",
                disable: function () {
                    return this.minder.queryCommandState('fontfamily') === -1
                }.bind(this),
                keyAction: function () {
                    this.fontfamilySelector.showTooltip()
                }.bind(this),
                init: function (container, command) {
                    this.initFontFamily(container, command)
                }.bind(this)
            },
            fontsize: {
                title: "字号",
                modle : ["edit"],
                //key : "Alt + H",
                disable: function () {
                    return this.minder.queryCommandState('fontsize') === -1
                }.bind(this),
                keyAction: function () {
                    this.fontsizeSelector.showTooltip()
                }.bind(this),
                init: function (container, command) {
                    this.initFontSize(container, command)
                }.bind(this)
            },
            italic: {
                icon: "italic",
                modle : ["edit"],
                title: "斜体",
                key: "Ctrl + I",
                isDefaultKey: true,
                disable: function () {
                    return this.minder.queryCommandState('italic') === -1
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('italic') === -1 || this.minder.execCommand('italic');
                }.bind(this)
            },
            bold: {
                icon: "bold",
                modle : ["edit"],
                title: "加粗",
                key: "Ctrl + B",
                isDefaultKey: true,
                disable: function () {
                    return this.minder.queryCommandState('bold') === -1
                }.bind(this),
                action: function () {
                    this.minder.queryCommandState('bold') === -1 || this.minder.execCommand('bold');
                }.bind(this)
            },
            forecolor: {
                icon: "forecolor",
                modle : ["edit"],
                title: "字体颜色",
                action: function () {
                    ( this.minder.queryCommandState('forecolor') === -1 || !this.currentForecolor ) || this.minder.execCommand('forecolor', this.currentForecolor);
                }.bind(this),
                disable: function () {
                    return this.minder.queryCommandState('forecolor') === -1
                }.bind(this),
                init: function (container, command) {
                    this.initFontColor(container, command)
                }.bind(this)
            },
            background: {
                icon: "background",
                modle : ["edit"],
                title: "背景颜色",
                action: function () {
                    ( this.minder.queryCommandState('background') === -1 || !this.currentBackgroundcolor ) || this.minder.execCommand('background', this.currentBackgroundcolor);
                }.bind(this),
                disable: function () {
                    return this.minder.queryCommandState('background') === -1
                }.bind(this),
                init: function (container, command) {
                    this.initBackground(container, command)
                }.bind(this)
            },

            expandLevel: {
                icon: "expand",
                modle : ["edit","read"],
                title: "展开节点",
                //key : "Alt + N",
                disable: function () {
                    return false;
                }.bind(this),
                keyAction: function () {
                    this.expandlevelSelector.showTooltip()
                }.bind(this),
                init: function (container, command) {
                    this.initExpandLevel(container, command)
                }.bind(this)
            },
            selectAll: {
                icon: "select",
                modle : ["edit","read"],
                title: "选择",
                //key : "Alt + S",
                disable: function () {
                    return false;
                }.bind(this),
                keyAction: function () {
                    this.selectallSelector.showTooltip()
                }.bind(this),
                init: function (container, command) {
                    this.initSelectAll(container, command)
                }.bind(this)
            },
            zoom : {
                module : ["edit","read"],
                title : "缩放",
                disable : function(){ return false },
                init : function( container, command ){
                    this.initZoomArea(container, command);
                }.bind(this)
            },
            camera : {
                icon: "camera",
                module : ["edit","read"],
                title : "根节点居中",
                disable: function () {
                    return false;
                }.bind(this),
                action: function () {
                    this.minder.execCommand('camera', this.minder.getRoot(), 600);
                }.bind(this)
            },
            preview : {
                icon: "preview",
                module : ["edit","read"],
                title : "预览",
                active : true,
                disable: function () {
                    return false;
                }.bind(this),
                action: function () {
                    this.toggleOpenPreViewer();
                }.bind(this),
                setDisable: function () {
                }.bind(this),
                setNormal: function () {
                }.bind(this),
                init: function (container, command) {
                    this.initPreViewer(container)
                }.bind(this)
            },
            move : {
                icon: "move",
                module : ["edit","read"],
                title : "允许拖拽",
                disable: function () {
                    return false;
                }.bind(this),
                active : false,
                setDisable: function () {
                }.bind(this),
                setNormal: function () {
                }.bind(this),
                action: function () {
                    this.minder.execCommand('hand');
                }.bind(this)
            },
            search: {
                icon: "search",
                module : ["edit","read"],
                title : "搜索",
                key : "Ctrl + F",
                active : false,
                setDisable: function () {
                }.bind(this),
                setNormal: function () {
                }.bind(this),
                disable: function () {
                    return false;
                }.bind(this),
                action : function(){
                  this.toggerSearch();
                }.bind(this),
                init: function ( container ) {
                    this.initSearch( container );
                }.bind(this)
            },
            help : {
                icon: "help",
                modle : ["edit","read"],
                title: "帮助",
                key : "Ctrl + Alt + H",
                disable: function () {
                    return false;
                }.bind(this),
                keyAction: function () {
                    this.helpTootips.load()
                }.bind(this),
                init: function (container, command) {
                    this.initHelp(container, command)
                }.bind(this)
            },
            save : {
                icon: "save",
                modle : ["edit"],
                locale: "保存",
                key : "Ctrl + S",
                disable: function () {
                    return false;
                }.bind(this),
                action: function () {
                    this.save()
                }.bind(this),
                init: function (container, command) {
                    this.initSaveTooltip(container, command)
                }.bind(this)
            },
            export : {
                icon: "export",
                modle : ["edit"],
                locale: "导出",
                disable: function () {
                    return false;
                }.bind(this),
                init: function (container, command) {
                    this.initExportTooltip(container, command)
                }.bind(this)
            },
            menu : {
                icon: "menu",
                modle : ["edit"],
                locale: "菜单",
                disable: function () {
                    return false;
                }.bind(this),
                action: function (ev, node, command) {
                    this.initMainMenu( node, command)
                }.bind(this),
                init: function (container, command) {
                    if( this.app.options.menuAction ){
                        this.initMainMenu(container, command)
                    }
                }.bind(this)
            }
        };
        for (var name in this.commands) {
            this.commands[name].name = name;
        }

        this.setKeyCommand();

        this.tooltipOptions = {
            displayDelay : 300,
            "onPostCreate": function (tooltips) {
                tooltips.node.addEvents({
                    mouseenter : function(){
                        var command = tooltips.command;
                        if (!command.disable()) {
                            this.setActiveStye(tooltips.target, command)
                        } else {
                            this.setDisableStye(tooltips.target, command)
                        }
                    }.bind(this)
                })
            }.bind(this),
            "onHide": function (tooltips) {
                var command = tooltips.command;
                if (!command.disable()) {
                    this.setNormalStye(tooltips.target, command)
                } else {
                    this.setDisableStye(tooltips.target, command)
                }
            }.bind(this)
        };

        this.selectOptions = {
            "tooltipsOptions": {
                displayDelay : 300,
                "onPostCreate": function (tooltips) {
                    var selector = tooltips.selector;
                    tooltips.node.addEvents({
                        mouseenter : function(){
                            var command = selector.command;
                            if (!command.disable()) {
                                this.setActiveStye(selector.container, command)
                            } else {
                                this.setDisableStye(selector.container, command)
                            }
                        }.bind(this)
                    })
                }.bind(this),
                "onQueryLoad": function (tooltips) {
                    var selector = tooltips.selector;
                    if ( selector.command ) {
                        tooltips.disable = selector.command.disable();
                    }
                }.bind(this),
                "onPostLoad": function (tooltips) {
                    var selector = tooltips.selector;
                    if (selector.selectArrowNode)selector.selectArrowNode.setStyles(selector.css.selectArrowNode_up);
                    this.activeTooltip = tooltips;
                }.bind(this),
                "onHide": function (tooltips) {
                    var selector = tooltips.selector;
                    if (this.activeTooltip == tooltips)this.activeTooltip = null;
                    if (selector.selectArrowNode) selector.selectArrowNode.setStyles(selector.css.selectArrowNode);
                    var command = selector.command;
                    if (!command.disable()) {
                        this.setNormalStye(selector.container, command)
                    } else {
                        this.setDisableStye(selector.container, command)
                    }

                }.bind(this),
                event: "mouseenter" //事件类型，有target 时有效， mouseenter对应mouseleave，click 对应 container 的  click
            }
        }
    },
    load: function () {
        this._loadCss();
        this.containerObject = {};
        this.cssObject = {};
        this.itemNodeObject = {};

        this.fsm.when('normal -> normal', function (exit, enter, reason, e) {
            if (reason == 'shortcut-handle') {
                var handleResult = this.dispatchKey(e);
                if (handleResult) {
                    e.preventDefault();
                } else {
                    this.minder.dispatchKeyEvent(e);
                }
            }
        }.bind(this));

        this.minder.on('interactchange', function () {
            if (this.queryInteractchange()) {
                if (this.activeTooltip)this.activeTooltip.hide();
                this.checkStatus();
            }
        }.bind(this));
    },
    addContainer : function( name, container, css ){
        this.containerObject[name] = container;
        this.cssObject[ name ] = css;
        this.loadItemsByContainer( name );
    },
    loadItemsByContainer : function( name ){
        var container = this.containerObject[name];
        var css = this.cssObject[ name ];
        container.getElements("[item]").each(function (node) {
            if (node.get("lazyLoading") == "true" )return;
            this.loadItemNode(node, css);
        }.bind(this));
    },
    loadItemsByNameList : function(nameList,containerName ){
        var container = this.containerObject[containerName];
        var css = this.cssObject[ containerName ];
        container.getElements("[item]").each(function (node) {
            var name = node.get("item");
            if( nameList.contains( name ) ){
                this.loadItemNode( node, css);
            }
        }.bind(this));
    },
    loadItemByName: function (name , containerName ) {
        var container = this.containerObject[containerName];
        var css = this.cssObject[ containerName ];
        var node = container.getElement("[item='" + name + "']");
        this.loadItemNode( node, css);
    },
    getItemNode : function( name, containerName ){
        var container = this.containerObject[containerName];
        var node = container.getElement("[item='" + name + "']");
        return node;
    },
    loadItemNode: function (node, css) {
        var _self = this;
        node.store( "css", css );
        var name = node.get("item");
        var subtype = node.get("subtype");
        var event = node.get("itemevent");

        if (this.commands[name]) {
            var command = this.commands[name];

            if( !subtype || subtype=="button" ){
                this.itemNodeObject[name] = node;
                if( command.active ){
                    node.setStyles( css[node.get("styles") ]);
                    this.setActiveStye(node, command);
                    node.store("active",true);
                }else if (command.disable()) {
                    node.setStyles( css[node.get("styles") ]);
                    this.setDisableStye(node, command)
                } else {
                    this.setNormalStye(node, command)
                }

                var title = (command.title || "");  //+ ( command.key ? (" 快捷键:" + command.key) : "" );
                node.set("title", title);

                if( !event || event=="mouseover" ){
                    node.addEvents({
                        mouseover : function () {
                            if (!command.disable()) {
                                this.setActiveStye(node, command)
                            } else {
                                this.setDisableStye(node, command)
                            }
                        }.bind(this),
                        mouseleave : function () {
                            if (!command.disable()) {
                                this.setNormalStye(node, command)
                            } else {
                                this.setDisableStye(node, command)
                            }
                        }.bind(this)
                    });
                }else{
                    node.addEvents({
                        click: function () {
                            if ( !node.retrieve("active")) {
                                this.setActiveStye(node, command);
                                node.store("active",true)
                            } else {
                                this.setNormalStye(node, command);
                                node.store("active",false)
                            }
                        }.bind(this)
                    });
                }

                var div = new Element("div", {
                    "text": command.locale || null
                }).inject(node);
                if (command.action) {
                    node.addEvent("click", function (ev) {
                        if (!command.disable()) {
                            command.action( ev, node, command );
                            _self.fireEvent("postExecCommand", [_self, this])
                        }
                    }.bind(name))
                }
            }

            if( !subtype || subtype=="container" ){
                if (command.init) {
                    command.init(node, command);
                    if( command.active === false ){
                        node.setStyle("display","none")
                    }
                }
            }
        }
    },
    setDisableStye: function (node, command) {
        var css = node.retrieve("css");
        node.setStyles(css[node.get("styles") + "_disable"]);
        if (command.icon) {
            node.setStyle("background-image", "url(../x_component_MinderEditor/$Main/" + this.options.style + "/icon/" + command.icon + "_disable.png)");
        }
    },
    setActiveStye: function (node, command) {
        var css = node.retrieve("css");
        node.setStyles(css[node.get("styles") + "_over"]);
        if (command.icon) {
            node.setStyle("background-image", "url(../x_component_MinderEditor/$Main/" + this.options.style + "/icon/" + command.icon + "_active.png)")
        }
    },
    setNormalStye: function (node, command) {
        var css = node.retrieve("css");
        node.setStyles(css[node.get("styles")]);
        if (command.icon) {
            node.setStyle("background-image", "url(../x_component_MinderEditor/$Main/" + this.options.style + "/icon/" + command.icon + "_normal.png)")
        }
    },
    checkStatus: function () {
        for (var name in this.itemNodeObject) {
            var node = this.itemNodeObject[name];
            var name = node.get("item");
            if (this.commands[name]) {
                var command = this.commands[name];
                if (command.disable()) {
                    command.setDisable ? command.setDisable() : this.setDisableStye(node, command);
                } else {
                    command.setNormal ? command.setNormal() : this.setNormalStye(node, command)
                }
            }
        }
    },
    setKeyCommand: function () {
        this.keyCommands = {};
        this.defaultKeyCommands = {};
        for (var name in this.commands) {
            var command = this.commands[name];
            if (command.key) {
                var commands = command.isDefaultKey ? this.defaultKeyCommands : this.keyCommands;
                if (typeOf(command.key) == "array") {
                    for (var i = 0; i < command.key.length; i++) {
                        commands[command.key[i]] = command;
                    }
                } else {
                    commands[command.key] = command;
                }
            }
        }
    },
    getKey: function (e) {
        var controlKeys = [];
        if (e.ctrlKey)controlKeys.push("Ctrl");
        if (e.shiftKey)controlKeys.push("Shift");
        if (e.altKey)controlKeys.push("Alt");
        if (![16, 17, 18].contains(e.keyCode)) { //16 是 Shift, 17 是 Ctrl， 18 是Alt
            return controlKeys.length == 0 ? e.key.capitalize() : ( controlKeys.join(" + ") + " + " + e.key.capitalize() );
        } else {
            return controlKeys.join(" + ");
        }
    },
    dispatchKey: function (e, callback) {
        var key = this.getKey(e);
        var command = this.keyCommands[key];
        if (command && !command.disable() && this.itemNodeObject[command.name]) {
            if (command.action) {
                command.action();
                if (callback)callback(e, true);
                return true;
            } else if (command.keyAction) {
                if (this.activeTooltip)this.activeTooltip.hide();
                command.keyAction();
                if (callback)callback(e, true);
                return true;
            } else {
                if (callback)callback(e, true);
                return false;
            }
        } else {
            if (callback)callback(e, false);
            return false;
        }
    },
    save : function(){
        this.editor.save();
    },
    initSelectAll: function (container, command) {
        var selector = this.selectallSelector = new MWF.xApplication.MinderEditor.SelectAll(container, Object.merge(Object.clone(this.selectOptions), {
            "onSelectItem": function (itemNode, itemData) {
                this.fireEvent("postExecCommand", [this, command])
            }.bind(this)
        }), this.app, null, this.app.node);
        selector.command = command;
        selector.load();
    },
    initExpandLevel: function (container, command) {
        var selector = this.expandlevelSelector = new MWF.xApplication.MinderEditor.ExpandLevel(container, Object.merge(Object.clone(this.selectOptions), {
            "onSelectItem": function (itemNode, itemData) {
                this.minder.execCommand('ExpandToLevel', itemData.value);
                this.fireEvent("postExecCommand", [this, command])
            }.bind(this)
        }), this.app, null, this.app.node);
        selector.command = command;
        selector.load();
    },
    initBackground: function (container, command) {
        var selector = this.backgroundSelector = new MWF.xApplication.Template.widget.ColorPicker(this.app.node, container, this.app, {},
            Object.merge( this.tooltipOptions, {
                "onQueryLoad": function (tooltips) {
                    tooltips.disable = this.commands.background.disable();
                }.bind(this),
                "onSelect": function (color) {
                    this.currentBackgroundcolor = color;
                    this.minder.queryCommandState('background') === -1 || this.minder.execCommand('background', color);
                    this.fireEvent("postExecCommand", [this, command])
                }.bind(this)
            } ));
        selector.command = command;
    },
    initFontColor: function (container, command) {
        var selector = this.forecolorSelector = new MWF.xApplication.Template.widget.ColorPicker(this.app.node, container, this.app, {},
            Object.merge( this.tooltipOptions, {
                "onQueryLoad": function (tooltips) {
                    tooltips.disable = this.commands.forecolor.disable();
                }.bind(this),
                "onSelect": function (forecolor) {
                    this.currentForecolor = forecolor;
                    this.minder.queryCommandState('forecolor') === -1 || this.minder.execCommand('forecolor', forecolor);
                    this.fireEvent("postExecCommand", [this, command])
                }.bind(this)
            })
        );
        selector.command = command;
    },
    initFontFamily: function (container, command) {
        var selector = this.fontfamilySelector = new MWF.xApplication.MinderEditor.FontFamily(container, Object.merge(Object.clone(this.selectOptions), {
            "containerIsTarget": false,
            "onPostLoad" : function( selector ){
                selector.selectValueNode.addEvent( "click", function(){
                    if( selector.currentItemData ){
                        this.minder.queryCommandState('fontfamily') === -1 || this.minder.execCommand('fontfamily', selector.currentItemData.val);
                    }
                }.bind(this))
            }.bind(this),
            "onSelectItem": function (itemNode, itemData) {
                this.minder.queryCommandState('fontfamily') === -1 || this.minder.execCommand('fontfamily', itemData.val);
                this.fireEvent("postExecCommand", [this, command])
            }.bind(this)
        }), this.app, null, this.app.node);
        selector.command = command;
        selector.load();
    },
    initFontSize: function (container, command) {
        var selector = this.fontsizeSelector = new MWF.xApplication.MinderEditor.FontSize(container, Object.merge(Object.clone(this.selectOptions), {
            "containerIsTarget": false,
            "onPostLoad" : function( selector ){
                selector.selectValueNode.addEvent( "click", function(){
                    if( selector.currentItemData ){
                        this.minder.queryCommandState('fontsize') === -1 || this.minder.execCommand('fontsize', selector.currentItemData.value);
                    }
                }.bind(this))
            }.bind(this),
            "onSelectItem": function (itemNode, itemData) {
                this.minder.queryCommandState('fontsize') === -1 || this.minder.execCommand('fontsize', itemData.value);
                this.fireEvent("postExecCommand", [this, command])
            }.bind(this)
        }), this.app, null, this.app.node);
        selector.command = command;
        selector.load();
    },
    initTheme: function (container, command) {
        var _self = this;
        var selector = this.themeSelector = new MWF.xApplication.MinderEditor.Theme(container, Object.merge(Object.clone(this.selectOptions), {
            "style": "minderTheme",
            "containerIsTarget": true,
            "onPostCreateItem": function (itemNode, itemData) {
                itemNode.setStyles(_self.getThemeThumbStyle(itemData.command));
            },
            "onSelectItem": function (itemNode, itemData) {
                this.minder.queryCommandState('theme') === -1 || this.minder.execCommand('theme', itemData.command);
                this.fireEvent("postExecCommand", [this, command])
            }.bind(this)
        }), this.app, null, this.app.node);
        selector.command = command;
        selector.load();
    },
    initTemplate: function (container, command) {
        var _self = this;
        var selector = this.templateSelector = new MWF.xApplication.MinderEditor.Template(container, Object.merge(Object.clone(this.selectOptions), {
            "style": "minderTemplate",
            "containerIsTarget": true,
            "onSelectItem": function (itemNode, itemData) {
                this.minder.execCommand('template', itemData.command);
                this.fireEvent("postExecCommand", [this, command])
            }.bind(this)
        }), this.app, null, this.app.node);
        selector.command = command;
        selector.load();
    },
    initPriority: function (container, command) {
        var _self = this;
        var selector = this.prioritySelector = new MWF.xApplication.MinderEditor.PriorityImage(container, Object.merge(Object.clone(this.selectOptions), {
            "containerIsTarget": true,
            "onSelectItem": function (itemNode, itemData) {
                var val = itemData.command == "0" ? null : parseInt(itemData.command);
                this.minder.execCommand('priority', val);
                this.fireEvent("postExecCommand", [this, command])
            }.bind(this)
        }), this.app, null, this.app.node);
        selector.command = command;
        selector.load();
        this.minder.on('interactchange', function () {
            if (this.queryInteractchange()) {
                var enabled = this.minder.queryCommandState('priority') != -1;
                if (enabled) {
                    var priority = this.minder.queryCommandValue('priority') || "0";
                    selector.setValue(priority);
                }
            }
        }.bind(this));
    },
    initProgress: function (container, command) {
        var _self = this;
        var selector = this.progressSelector = new MWF.xApplication.MinderEditor.ProgressImage(container, Object.merge(Object.clone(this.selectOptions), {
            "containerIsTarget": true,
            "onSelectItem": function (itemNode, itemData) {
                var val = itemData.command == "0" ? null : parseInt(itemData.command);
                this.minder.execCommand('progress', val);
                this.fireEvent("postExecCommand", [this, command])
            }.bind(this)
        }), this.app, null, this.app.node);
        selector.command = command;
        selector.load();
        this.minder.on('interactchange', function () {
            if (this.queryInteractchange()) {
                var enabled = this.minder.queryCommandState('progress') != -1;
                if (enabled) {
                    var progress = this.minder.queryCommandValue('progress') || "0";
                    selector.setValue(progress);
                }
            }
        }.bind(this));
    },
    getThemeThumbStyle: function (theme) {
        var themeList = this.themeList = this.themeList || kityminder.Minder.getThemeList();
        var themeObj = themeList[theme];
        if (!themeObj) {
            return;
        }
        var style = {
            'color': themeObj['root-color'],
            'border-radius': themeObj['root-radius'] / 2
        };

        if (themeObj['root-background']) {
            style['background'] = themeObj['root-background'].toString();
        }

        return style;
    },
    toggerSearch : function(){
        if( !this.isSearchbarActive ){
            this.searchBar.show();
            //this.isSearchbarActive = true;
        }else{
            this.searchBar.hide();
            //this.isSearchbarActive = false;
        }
    },
    initSearch : function(container){
        this.minder.on('searchNode', function() {
            this.itemNodeObject.search.click();
        }.bind(this));
        this.searchBar = new MWF.xApplication.MinderEditor.SearchBar( container, this.minder, this.app, this.css, {
            onShow : function(){
                this.setActiveStye( this.itemNodeObject.search, "search" );
                this.isSearchbarActive = true;
            }.bind(this),
            onHide : function(){
                this.setNormalStye( this.itemNodeObject.search, "search" );
                this.isSearchbarActive = false;
            }.bind(this)
        } );
        this.isSearchbarActive = false;
        this.searchBar.load();
    },
    initHelp: function (container, command) {
        var _self = this;
        this.helpTootips = new MWF.xApplication.MinderEditor.Help(this.app.node, container, this.app, {},
            this.tooltipOptions );
        this.helpTootips.commands = this;
        this.helpTootips.command = command;
    },
    initSaveTooltip : function( container, command ){
       this.savetooltip = new MWF.xApplication.MinderEditor.SaveTooltips(this.app.node, container, this.app, {},
           this.tooltipOptions);
        this.savetooltip.command = command;
    },
    initExportTooltip : function( container, command ){
        this.exporttooltip = new MWF.xApplication.MinderEditor.ExportTooltips(this.app.node, container, this.app, {},
            this.tooltipOptions);
        this.exporttooltip.command = command;
    },
    initMainMenu : function( container, command ){
        var _self = this;
        if( !this.mainMenu ){
            MWF.xDesktop.requireApp("MinderEditor", "MainMenu", null, false);
            this.mainMenu = new MWF.xApplication.MinderEditor.MainMenu(this.app.content, this.app, {
                "onShow": function () {
                    if (!this.command.disable()) {
                        _self.setActiveStye(this.container, this.command)
                    } else {
                        _self.setDisableStye(this.container, this.command)
                    }
                }.bind({ container : container, command : command }),
                "onHide": function () {
                    if (!this.command.disable()) {
                        _self.setNormalStye(this.container, this.command)
                    } else {
                        _self.setDisableStye(this.container, this.command)
                    }
                }.bind({ container : container, command : command })
            });
            this.mainMenu.command = command;
        }else{
            if( this.mainMenu.isHidden ){
                this.mainMenu.show();
            }else{
                this.mainMenu.hide();
            }
        }
    },
    setResourceDisable: function () {
        if (!this.resourceMaskNode) {
            this.resourceMaskNode = new Element("div.maskNode", {
                "styles": this.css.resourceMaskNode
            }).inject(this.resourceNode);
        } else {
            this.resourceMaskNode.setStyle("display", "");
        }
        this.addResourceInput.placeholder = "选择节点编辑标签";
    },
    setResourceNormal: function () {
        if (this.resourceMaskNode)this.resourceMaskNode.setStyle("display", "none");
        this.addResourceInput.placeholder = "输入文字添加标签";
    },
    initResource: function (container, command) {
        this.resourceNode = new Element("div.resourceNode", {
            styles: this.css.resourceNode
        }).inject(container);
        var inputContainer = new Element("div", {styles: {overflow: "hidden"}}).inject(this.resourceNode);
        this.addResourceInput = new Element("input", {
            placeholder: "输入文字添加标签", styles: this.css.addResourceInput
        }).inject(inputContainer);
        this.addResourceBotton = new Element("div", {
            text: "添加",
            styles: this.css.addResourceBotton,
            events: {
                click: function () {
                    var resourceName = this.addResourceInput.get("value");
                    var origin = this.minder.queryCommandValue('resource');
                    if (!resourceName || !/\S/.test(resourceName)) return;

                    if (origin.indexOf(resourceName) == -1) {
                        origin.push(resourceName);
                        this.minder.execCommand('resource', origin);
                        this.addResourceCheckbox(resourceName)
                    }
                    this.addResourceInput.set("value", "");
                    this.fireEvent("postExecCommand", [this, command])
                }.bind(this)
            }
        }).inject(inputContainer);


        this.resourceUsedContainer = new Element("div", {
            styles: this.css.resourceUsedContainer
        }).inject(this.resourceNode);
        this.setScrollBar(this.resourceUsedContainer);
        this.usedResource = this.minder.getUsedResource() || [];
        this.usedResource.each(function (resource) {
            this.addResourceCheckbox(resource)
        }.bind(this));

        this.minder.on('interactchange', function () {
            if (this.queryInteractchange()) {
                this.setResourceCheckbox();
            }
        }.bind(this));

        if( this.minder.queryCommandState('resource') === -1 ){
            this.setResourceDisable();
        }
    },
    setResourceCheckbox: function () {
        var enabled = this.minder.queryCommandState('resource') != -1;
        var origin = enabled ? this.minder.queryCommandValue('resource') : [];
        (  this.resourceCheckbox || [] ).each(function (c) {
            if (origin.contains(c.get("value"))) {
                c.set("checked", true);
            } else {
                c.set("checked", false);
            }
        });
    },
    addResourceCheckbox: function (resourceName) {
        this.resourceCheckbox = this.resourceCheckbox || [];
        var lable = new Element("lable", {
            text: resourceName,
            styles: {
                "float": "left",
                "margin-right": "5px",
                "padding": "5px",
                "background-color": this.minder.getResourceColor(resourceName).toHEX()
            }
        }).inject(this.resourceUsedContainer);
        var checkbox = new Element("input", {
            type: "checkbox",
            value: resourceName,
            checked: true,
            events: {
                change: function () {
                    var checked = [];
                    this.resourceCheckbox.each(function (c) {
                        if (c.get("checked")) {
                            checked.push(c.get("value"))
                        }
                    }.bind({resourceName: resourceName}));
                    this.minder.execCommand('resource', checked);
                }.bind(this)
            }
        }).inject(lable, "top");
        this.resourceCheckbox.push(checkbox);
    },
    editNode: function () {
        this.receiver.element.innerText = this.minder.queryCommandValue('text') || "";
        this.fsm.jump('input', 'input-request');
        this.receiver.selectAll();
    },
    openHyperLinkForm: function (command) {
        var form = new MWF.xApplication.MinderEditor.HyperLinkForm(this, {}, {}, {
            app: this.app
        });
        form.edit();
        this.fireEvent("postExecCommand", [this, command])
    },
    openImageForm: function (command) {
        var form = new MWF.xApplication.MinderEditor.ImageForm(this, {}, {}, {
            app: this.app
        });
        form.edit();
        this.fireEvent("postExecCommand", [this, command])
    },
    openNoteForm: function (command) {
        var form = new MWF.xApplication.MinderEditor.NoteForm(this, {}, {}, {
            app: this.app
        });
        form.edit();
        this.fireEvent("postExecCommand", [this, command])
    },
    setNoteDisable: function () {
        if (this.noteTextNode)this.noteTextNode.setStyle("display", "");
    },
    setNoteNormal: function () {
        if (this.noteTextNode)this.noteTextNode.setStyle("display", "none");
        if (!this.codeMirrorEditor)this.createNoteEditor();
    },
    createNoteEditor: function (container) {
        if (container) {
            this.noteContainer = container;
        } else {
            container = this.noteContainer;
        }
        var command = this.commands["note"];
        if (!this.noteTextNode) {
            var titleNode = new Element("div", {
                "text": "备注",
                "styles": this.css.noteTitleNode
            }).inject(container);

            new Element("a", {
                text: "支持GFM语法",
                href: "../x_component_MinderEditor/$Commands/GFMDescription.html",
                "target": "_blank",
                "styles": this.css.noteLinkNode
            }).inject(titleNode);

            this.noteTextNode = new Element("div", {
                text: "请选择节点添加备注",
                "styles": this.css.noteTextNode
            }).inject(container);
        }

        if (this.minder.queryCommandState('note') === -1)return;

        this.noteTextNode.setStyle("display", "none");

        var contentNode = this.noteContentNode = new Element("div", {
            "overflow": "hidden"
        }).inject(container);

        this.isNoteEditing = false;

        this.editor.loadCodeMirror(function () {
            this.noteTextarea = new Element("textarea").inject(contentNode);
            this.codeMirrorEditor = CodeMirror.fromTextArea(this.noteTextarea, {
                value: (this.minder.queryCommandState('note') === -1 || this.minder.queryCommandValue('note')),
                theme: "default",
                gfm: true,
                breaks: true,
                lineWrapping: true,
                mode: 'gfm',
                dragDrop: false,
                lineNumbers: true
            });
            this.codeMirrorEditor.on("change", function () {
                var enabled = this.minder.queryCommandState('note') != -1;
                var content = this.codeMirrorEditor.getValue();
                if (enabled) {
                    this.isNoteEditing = true;
                    this.minder.execCommand('note', content);
                    if (this.noteTimeout)clearTimeout(this.noteTimeout);
                    this.noteTimeout = setTimeout(function () {
                        this.isNoteEditing = false;
                    }.bind(this), 100);
                }
            }.bind(this));

            this.codeMirrorEditor.setSize("100%", container.getSize().y - 30 - 3);

            //this.previewer = new Element("div").inject(container);
            this.minder.on('interactchange', function () {
                if (this.queryInteractchange()) {
                    var enabled = this.minder.queryCommandState('note') != -1;
                    var noteValue = this.minder.queryCommandValue('note') || '';
                    if (enabled) {
                        this.codeMirrorEditor.setValue(noteValue);
                    }
                }
            }.bind(this));
            //var str = "月份|收入|支出\n"+
            //    "----|----|---\n"+
            //    "8   |1000|500\n"+
            //    "9   |1200|600\n"+
            //    "10  |1400|650\n";
            //this.previewer.set("html",marked(str));
        }.bind(this));
        this.fireEvent("postExecCommand", [this, command])
    },
    toggleOpenPreViewer: function(){
        this.previewOpened = !this.previewOpened;
        this.preview.toggleOpen( this.previewOpened );
    },
    initPreViewer: function( container ){
        this.preview = new MWF.xApplication.MinderEditor.Preview( container, this.minder, this.app, this.css );
        this.preview.load();
        this.previewOpened = true;
    },
    initZoomArea : function( container ){
        this.editor.contentNode.addEvent("mousewheel", function (ev) { //鼠标滚轮事件
            if (ev.wheel > 0) { //向上滚动，放大
                this.minder.execCommand('zoomIn');
                if ( this.zoompanIndicator) {
                    var marginTop = parseInt(this.zoompanIndicator.getStyle("margin-top"));
                    if (marginTop > 0) {
                        this.zoompanIndicator.setStyle("margin-top", (marginTop - 10) + "px");
                    }
                }
            } else if(ev.wheel < 0)  {  //向下滚动，缩小
                this.minder.execCommand('zoomOut');
                if ( this.zoompanIndicator) {
                    var totalHeight = parseInt(this.zoompan.getStyle("height"));
                    var marginTop = parseInt(this.zoompanIndicator.getStyle("margin-top"));
                    if (marginTop < totalHeight) {
                        this.zoompanIndicator.setStyle("margin-top", (marginTop + 10 ) + "px");
                    }
                }
            }
        }.bind(this));

        this.zoomIn = new Element("div",{ "styles" : this.css.zoomButton , "title" : this.lp.zoomin }).inject(container);
        new Element("div",{ "styles" : this.css.zoominIcon  }).inject(this.zoomIn);
        this.zoomIn.addEvent("click",function(){
            this.minder.execCommand('zoomIn');
            var marginTop = parseInt( this.zoompanIndicator.getStyle("margin-top"));
            if( marginTop > 0 ){
                this.zoompanIndicator.setStyle( "margin-top", (marginTop - 10)  + "px" );
            }
        }.bind(this));

        this.zoompan = new Element("div",{ "styles" : this.css.zoompan  }).inject(container);
        this.zoompanOrigin = new Element("div",{ "styles" : this.css.zoompanOrigin  }).inject(this.zoompan);
        this.zoompanOrigin.addEvent("click",function(){
            this.minder.execCommand('zoom', 100);
            this.zoompanIndicator.setStyle( "margin-top", "30px" );
        }.bind(this));
        this.zoompanIndicator = new Element("div",{ "styles" : this.css.zoompanIndicator  }).inject(this.zoompan);

        this.zoomout = new Element("div",{ "styles" : this.css.zoomButton , "title" : this.lp.zoomout }).inject(container);
        new Element("div",{ "styles" : this.css.zoomoutIcon  }).inject(this.zoomout);
        this.zoomout.addEvent("click",function(){
            this.minder.execCommand('zoomOut');
            var totalHeight = parseInt( this.zoompan.getStyle("height") );
            var marginTop = parseInt( this.zoompanIndicator.getStyle("margin-top"));
            if( marginTop < totalHeight ){
                this.zoompanIndicator.setStyle( "margin-top", (marginTop + 10 ) + "px" );
            }
        }.bind(this));
    },
    queryInteractchange: function () {
        return !this.isNoteEditing;
    },
    setSizes : function(){
        if( this.codeMirrorEditor && this.noteContainer ){
            this.codeMirrorEditor.setSize("100%", this.noteContainer.getSize().y - 30 - 3 );
        }
    }
});