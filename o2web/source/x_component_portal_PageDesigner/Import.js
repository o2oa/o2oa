MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.portal = MWF.xApplication.portal || {};
MWF.xApplication.portal.PageDesigner = MWF.xApplication.portal.PageDesigner || {};
MWF.xApplication.portal.PageDesigner.Import = MWF.FormImport = MWF.PageImport = new Class({
    Implements: [Options, Events],
    options: {
        "style": "default",
        "type": "portal",
        "stylePath": "../x_component_portal_PageDesigner/$Import/{style}/style.css",
        "viewPath": "../x_component_portal_PageDesigner/$Import/{style}/view.html"
    },
    initialize: function(form, options){
        this.setOptions(options);
        this.path = "../x_component_portal_PageDesigner/$Import/";
        this.stylePath = this.options.stylePath.replace("{style}", this.options.style);
        this.viewPath = this.options.viewPath.replace("{style}", this.options.style);
        this.form = form;
        this.init();
        this.loadView();
    },
    init: function(){
        this.inforText = this.form.designer.lp.importO2_infor;
        this.panelTitle = this.form.designer.lp.importO2;
        this.panelWidth = 800;
        this.panelHeight = 660;
        this.editorMode = "json";
    },
    loadView: function(){
        MWF.require("MWF.widget.Panel", function(){
            this.node = new Element("div.importNode");
            this.node.set("load", {"onSuccess": function(){
                this.inforNode = this.node.getElement(".importInfor");
                this.contentNode = this.node.getElement(".importContent");
                this.bottomNode = this.node.getElement(".importBottom");
                this.okNode = this.node.getElement(".importOkButton");
                this.cancelNode = this.node.getElement(".importCancelButton");
                o2.loadCss(this.stylePath, this.node, function(){
                    this.load();
                }.bind(this));
            }.bind(this)}).load(this.viewPath);

            var node = this.form.designer.pageNode || this.form.designer.formNode;
            var position = node.getPosition(node.getOffsetParent());
            this.implodePanel = new MWF.widget.Panel(this.node, {
                "style": "wizard",
                "isResize": false,
                "isMax": false,
                "title": this.panelTitle,
                "width": this.panelWidth,
                "height": this.panelHeight,
                "top": position.y,
                "left": position.x+3,
                "isExpand": false,
                "target": this.form.designer.node
            });

            this.implodePanel.load();
        }.bind(this));
    },

    load: function(){
        this.loadContent();
        this.loadEditor();
        this.loadEvent();
    },
    loadContent: function(){
        this.inforNode.set("html", this.inforText);
        this.okNode.set("text", this.form.designer.lp.import_ok);
        this.cancelNode.set("text", this.form.designer.lp.import_cancel);
    },
    loadEditor: function(){
        MWF.require("MWF.widget.ScriptArea", function(){
            this.scriptArea = new MWF.widget.ScriptArea(this.contentNode, {
                "isload": true,
                "style": "page",
                "isbind": false,
                "mode": this.editorMode
            });
            this.scriptArea.load({"code": ""});
            //cssArea.loadEditor(cssContent);
        }.bind(this));
    },
    loadEvent: function(){
        this.cancelNode.addEvent("click", function(){
            this.implodePanel.closePanel();
        }.bind(this));
        this.okNode.addEvent("click", function(e){
            var str = this.scriptArea.editor.getValue();
            if (!str){
                this.form.designer.notice(this.form.designer.lp.implodeEmpty, "error", this.node);
                return false;
            }
            var _self = this;
            this.form.designer.confirm("warn", e, this.form.designer.lp.implodeConfirmTitle, this.form.designer.lp.implodeConfirmText, 400, 100, function(){
                _self.implode(str);
                this.close();
            }, function(){
                this.close();
            });
        }.bind(this));
    },

    implode: function(str){
        if (str){
            var data = JSON.decode(str);
            if (data && data.json && data.html){
                var json = data.json;
                data.id = this.form.data.id;
                data.isNewPage = this.form.data.isNewPage;
                json.id = this.form.json.id;
                json.name = this.form.json.name;
                json.application = this.form.json.application;
                json.applicationName = this.form.json.applicationName;
                this.form.reload(data);
                this.implodePanel.closePanel();
            }else{
                this.form.designer.notice(this.designer.lp.implodeError, "error", this.node);
            }
        }else{
            this.form.designer.notice(this.designer.lp.implodeEmpty, "error", this.node);
        }
    }
});
MWF.FormImport.O2 = new Class({
    Extends: MWF.FormImport
});
MWF.FormImport.Html = new Class({
    Extends: MWF.FormImport,
    options: {
        "stylePath": "../x_component_portal_PageDesigner/$Import/{style}/style_html.css",
        "viewPath": "../x_component_portal_PageDesigner/$Import/{style}/view_html.html"
    },
    init: function(){
        this.inforText = this.form.designer.lp.importHTML_infor;
        this.inforText2 = this.form.designer.lp.importHTML_infor2;
        this.panelTitle = this.form.designer.lp.importHTML;
        this.panelWidth = 800;
        this.panelHeight = 700;
        this.editorMode = "html";
    },
    loadContent: function(){
        this.inforTextNode = this.node.getElement(".importInforText");
        this.inforOptionsNode = this.node.getElement(".importInforOption");
        this.inforTextNode.set("html", this.inforText);

        this.inforText2Node = this.node.getElement(".importInforText2");
        this.inforText2Node.set("html", this.inforText2);

        this.contentHtml = this.node.getElement(".importContentHtml");
        this.contentCss = this.node.getElement(".importContentCss");


        var html = "<input type='checkbox'>"+this.form.designer.lp.import_option1;
        html += "<span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><input type='checkbox'>"+this.form.designer.lp.import_option2;
        this.inforOptionsNode.set("html", html);
        var inputs = this.inforOptionsNode.getElements("input");
        this.option1 = inputs[0];
        this.option2 = inputs[1];


        this.okNode.set("text", this.form.designer.lp.import_ok);
        this.cancelNode.set("text", this.form.designer.lp.import_cancel);
    },
    loadEditor: function(){
        MWF.require("MWF.widget.ScriptArea", function(){
            this.scriptArea = new MWF.widget.ScriptArea(this.contentHtml, {
                "isload": true,
                "style": "page",
                "isbind": false,
                "mode": this.editorMode
            });
            this.scriptArea.load({"code": ""});
            //cssArea.loadEditor(cssContent);
        }.bind(this));
        MWF.require("MWF.widget.ScriptArea", function(){
            this.cssArea = new MWF.widget.ScriptArea(this.contentCss, {
                "isload": true,
                "style": "page",
                "isbind": false,
                "mode": "css"
            });
            this.cssArea.load({"code": ""});
            //cssArea.loadEditor(cssContent);
        }.bind(this));
    },
    implode: function(str){
        MWF.require("MWF.widget.Mask", null, false);
        var maxIndex = this.implodePanel.container.getStyle("z-index").toInt();
        var mask = new MWF.widget.Mask({"zIndex": maxIndex});
        mask.loadNode(this.form.designer.content);

        try{
            var iframe = new Element("iframe").inject(document.body);
            var doc = iframe.contentWindow.document;
            debugger;
            o2.load("mootools", {"doc": doc}, function(){
                debugger;
                var oldNodeHtml = this.form.node.get("html");
                var oldModuleList = this.form.json.moduleList;
                var oldHtml = this.form.data.html;
                try{
                    doc.body.set("html", str);
                    doc.body.normalize();
                    var moduleList = {};
                    var readyDeleteNodes = [];

                    var styleNodes = doc.body.getElements("style");
                    if (styleNodes) this.loadStyles(styleNodes);

                    var css = "";
                    if (this.cssArea){
                        css = this.cssArea.editor.getValue();
                        if (css) this.form.json.css.code += this.parseImplodeCSS(css, doc);

                        //if (css) this.form.json.css.code += css;
                    }

                    this.parseImplodeHTML(doc.body, moduleList, doc, readyDeleteNodes);
                    while (readyDeleteNodes.length){
                        readyDeleteNodes.shift().destroy();
                    }

                    var html = doc.body.get("html");
                    this.form.node.empty();
                    var formHtml = this.form.node.outerHTML;
                    var arr = formHtml.split(/\>\s*\</);
                    html = arr[0]+">"+html+"<"+arr[1];

                    this.form.json.moduleList = moduleList;
                    this.form.data.html = html;
                    this.form.reload(this.form.data);

                    iframe.destroy();

                    this.implodePanel.closePanel();

                    mask.hide();
                }catch(e){
                    this.form.designer.notice(e.message, "error", this.node);
                    this.form.node.set("html", oldNodeHtml);
                    this.form.json.moduleList = oldModuleList;
                    this.form.data.html = oldHtml;
                    this.form.reload(this.form.data);
                    mask.hide();
                }finally{
                    oldNodeHtml = null;
                    oldModuleList = null;
                    oldModuleList = null;
                }

            }.bind(this));
        }catch(e){
            this.form.designer.notice(e.message, "error", this.node);
            mask.hide();
        }

    },

    parseImplodeCSS: function(css, doc, callback){
        var rex = /(url\(.*\))/g;
        var match;
        while ((match = rex.exec(css)) !== null) {
            var pic = match[0];
            var len = pic.length;
            var s = pic.substring(pic.length-2, pic.length-1);
            var n = (s==="'" || s==="\"") ? 2 : 1;
            pic = pic.substring(pic.lastIndexOf("/")+1, pic.length-n);
            var root = (this.options.type==="portal") ? "x_portal_assemble_surface" : "x_processplatform_assemble_surface";
            var url = root + o2.Actions.get(root).action.actions.readFile.uri;
            url = url.replace("{flag}", pic);
            url = url.replace("{applicationFlag}", this.form.json.application || this.form.json.portal);
            url = "url('"+url+"')";
            var len2 = url.length;

            css = css.substring(0, match.index) + url + css.substring(rex.lastIndex, css.length);
            rex.lastIndex = rex.lastIndex + (len2-len);
        }
        return css;
    },

    loadStyles: function(styleNodes){
        var cssText = "";
        styleNodes.each(function(node){
            cssText+=node.get("text");
        }.bind(this));
        styleNodes.destroy();
        this.form.json.css.code = cssText;
    },
    "getInnerStyles": function(node){
        var styles = {};
        style = node.get("style");
        if (style){
            var styleArr = style.split(/\s*\;\s*/g);
            styleArr.each(function(s){
                if (s){
                    var sarr = s.split(/\s*\:\s*/g);
                    styles[sarr[0]] = (sarr.length>1) ? sarr[1]: ""
                }
            }.bind(this));
        }
        return styles;
    },
    "getInnerProperties": function(node){
        var properties = {};
        if (node.attributes.length){
            for (var i=0; i<node.attributes.length; i++){
                var k = node.attributes[i].nodeName.toString().toLowerCase();
                if (k!=="mwftype" && k!=="id" && k!=="style") properties[k] = node.attributes[i].nodeValue;
            }
        }
        return properties;
    },
    getImplodeModuleJson: function(moduleList, className, moduleId, node, callback){
        var id = moduleId;
        var className = className;
        this.form.getTemplateData(className, function(data){
            var moduleData = Object.clone(data);
            var n = 1;
            while (moduleList[id]){
                id = moduleId+"_"+n;
                n++;
            }
            if (node.nodeType===Node.ELEMENT_NODE){
                moduleData.styles = this.getInnerStyles(node);
                moduleData.properties = this.getInnerProperties(node);
            }else if (node.nodeType===Node.TEXT_NODE){
                moduleData.styles.display = "inline";
            }
            if (className==="Label") moduleData.styles.display = "inline";

            moduleData.id = id;

            moduleList[id] = moduleData;
            if (callback) callback(id, moduleData);
        }.bind(this), false);
    },

    convertTextLabelNode: function(subNode, moduleList){
        if (subNode.nodeValue.trim()){
            this.getImplodeModuleJson(moduleList, "Label", "label", subNode, function(id, moduleData){
                moduleData.text = subNode.nodeValue;
                var textNode = new Element("div#"+id, {"mwftype": "label", "text": moduleData.text});
                if (subNode.replaceWith){
                    subNode.replaceWith(textNode);
                }else if (subNode.replaceNode){
                    subNode.replaceNode(textNode);
                }
                subNode = textNode;
            }.bind(this));
        }
        return subNode;
    },
    convertLabelNode: function(subNode, moduleList, tag){
        this.getImplodeModuleJson(moduleList, "Label", (subNode.get("id") || "label"), subNode, function(id, moduleData){
            moduleData.text = subNode.get("text");
            if (tag!=="div"){
                var node = new Element("div#"+id, {"mwftype": "label", "text": moduleData.text}).inject(subNode, "before");
                subNode.destroy();
                subNode = node;
            }else{
                subNode.set({"mwftype": "label", "id": id});
            }
        }.bind(this));
        return subNode;
    },
    convertDivNode: function(subNode, moduleList, tag){
        this.getImplodeModuleJson(moduleList, "Div", (subNode.get("id") || "div"), subNode, function(id, moduleData){
            if (tag==="p"){
                var node = new Element("div#"+id, {"mwftype": "div"}).inject(subNode, "before");
                node.set("html", subNode.get("html"));
                node.set("MWFOriginalTag", "p");
                subNode.destroy();
                subNode = node;
            }else{
                 subNode.set({"mwftype": "div", "id": id});
            }
        }.bind(this));
        return subNode;
    },
    convertTableNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Table", (subNode.get("id") || "table"), subNode, function(id, moduleData){
            moduleData.styles.display = "table";
            var tableNode = new Element("div#"+id, {"mwftype": "table"}).inject(subNode, "before");
            subNode.inject(tableNode);
            //subNode.set({"mwftype": "table", "id": id});
        }.bind(this));
        return subNode;
    },
    convertTdNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Table$Td", (subNode.get("id") || "table$Td"), subNode, function(id){
            subNode.set({"mwftype": "table$Td", "id": id});
        }.bind(this));
        return subNode;
    },
    convertImgNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Image", (subNode.get("id") || "image"), subNode, function(id, moduleData){
            debugger;
            var src = subNode.get("src");
            if (src){
                var root = (this.options.type==="portal") ? "x_portal_assemble_surface" : "x_processplatform_assemble_surface";
                var pic = src.substring(src.lastIndexOf("/")+1, src.length);
                var url = root + o2.Actions.get(root).action.actions.readFile.uri;
                url = url.replace("{flag}", pic);
                url = url.replace("{applicationFlag}", this.form.json.application || this.form.json.portal);
                moduleData.properties.src = url;
                subNode.set("src", url);
            }
            subNode.set({"mwftype": "img", "id": id});
        }.bind(this));
        return subNode;
    },
    convertInputImgNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Img", (subNode.get("id") || "img"), subNode, function(id){
            var imgNode = new Element("img#"+id, {"mwftype": "img"}).inject(subNode, "before");
            //subNode.set({"mwftype": "img", "id": id});
            subNode.destroy();
            subNode = imgNode;
        }.bind(this));
        return subNode;
    },
    convertButtonNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Button", (subNode.get("id") || "button"), subNode, function(id, moduleData){
            var value = subNode.get("value");
            if (value) moduleData.name = value;
            delete moduleData.properties.type;
            delete moduleData.properties.name;
            var buttonNode = new Element("div#"+id, {"mwftype": "button"}).inject(subNode, "before");
            subNode.inject(buttonNode);


        }.bind(this));
        return subNode;
    },
    convertIframeNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Iframe", (subNode.get("id") || "iframe"), subNode, function(id, moduleData){
            if (moduleData.properties.src){
                moduleData.src = moduleData.properties.src;
                delete moduleData.properties.src;
            }
            var iframeNode = new Element("div#"+id, {"mwftype": "iframe"}).inject(subNode, "before");
            subNode.destroy();
            subNode = iframeNode;
        }.bind(this));
        return subNode
    },
    convertTextareaNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Textarea", (subNode.get("id") || "textarea"), subNode, function(id, moduleData){
            var value = subNode.get("text");
            if (value){
                if (!moduleData.defaultValue) moduleData.defaultValue = {"code": "", "html": ""};
                var v = value.replace(/\"/g, "\\\"");
                moduleData.defaultValue.code = "return \""+v+"\"";
            }
            var textareaNode = new Element("div#"+id, {"mwftype": "textarea"}).inject(subNode, "before");
            subNode.destroy();
            subNode = textareaNode;
        }.bind(this));
        return subNode;
    },
    convertSelectNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Select", (subNode.get("id") || "select"), subNode, function(id, moduleData){
            var options = subNode.getElements("option");
            moduleData.itemValues = [];
            options.each(function(op){
                var text = op.get("text");
                var value = op.get("value") || text;
                moduleData.itemValues.push(text+"|"+value);
            }.bind(this));
            var selectNode = new Element("div#"+id, {"mwftype": "select"}).inject(subNode, "before");
            subNode.destroy();
            subNode = selectNode;
        }.bind(this));
        return subNode;
    },
    convertTextfieldNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Textfield", (subNode.get("id") || "textfield"), subNode, function(id, moduleData){
            var value = subNode.get("value");
            if (value){
                if (!moduleData.defaultValue) moduleData.defaultValue = {"code": "", "html": ""};
                var v = moduleData.properties.value.replace(/\"/g, "\\\"");
                moduleData.defaultValue.code = "return \""+v+"\"";
            }
            delete moduleData.properties.type;
            delete moduleData.properties.name;
            var fieldNode = new Element("div#"+id, {"mwftype": "textfield"}).inject(subNode, "before");
            subNode.destroy();
            subNode = fieldNode;
        }.bind(this));
        return subNode;
    },
    convertNumberNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Number", (subNode.get("id") || "number"), subNode, function(id, moduleData){
            var value = subNode.get("value");
            if (value){
                if (!moduleData.defaultValue) moduleData.defaultValue = {"code": "", "html": ""};
                var v = moduleData.properties.value.replace(/\"/g, "\\\"");
                moduleData.defaultValue.code = "return \""+v+"\"";
            }
            delete moduleData.properties.type;
            delete moduleData.properties.name;
            var fieldNode = new Element("div#"+id, {"mwftype": "number"}).inject(subNode, "before");
            subNode.destroy();
            subNode = fieldNode;
        }.bind(this));
        return subNode;
    },
    convertCalendarNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Calendar", (subNode.get("id") || "calendar"), subNode, function(id, moduleData){
            var value = subNode.get("value");
            if (value){
                if (!moduleData.defaultValue) moduleData.defaultValue = {"code": "", "html": ""};
                var v = moduleData.properties.value.replace(/\"/g, "\\\"");
                moduleData.defaultValue.code = "return \""+v+"\"";
            }
            if (t==="date") moduleData.selectType = "date";
            if (t==="time") moduleData.selectType = "time";

            delete moduleData.properties.type;
            delete moduleData.properties.name;
            var fieldNode = new Element("div#"+id, {"mwftype": "calendar"}).inject(subNode, "before");
            subNode.destroy();
            subNode = fieldNode;
        }.bind(this));
        return subNode;
    },
    convertRadioNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Radio", (subNode.get("id") || "radio"), subNode, function(id, moduleData){
            moduleData.itemValues = [];
            var textNode = subNode.nextSibling;
            if (textNode && textNode.nodeType===Node.TEXT_NODE){
                text = textNode.nodeValue;
                if (textNode.remove){
                    textNode.remove();
                }else if(textNode.removeNode){
                    textNode.removeNode();
                }
            }
            var value = subNode.get("value");
            moduleData.itemValues.push(text+"|"+value);

            delete moduleData.properties.type;
            delete moduleData.properties.name;
            delete moduleData.properties.value;
            var fieldNode = new Element("div#"+id, {"mwftype": "radio"}).inject(subNode, "before");
            subNode.destroy();
            subNode = fieldNode;
        }.bind(this));
        return subNode;
    },
    convertCheckboxNode: function(subNode, moduleList){
        this.getImplodeModuleJson(moduleList, "Checkbox", (subNode.get("id") || "checkbox"), subNode, function(id, moduleData){
            moduleData.itemValues = [];
            var textNode = subNode.nextSibling;
            if (textNode && textNode.nodeType===Node.TEXT_NODE){
                text = textNode.nodeValue;
                if (textNode.remove){
                    textNode.remove();
                }else if(textNode.removeNode){
                    textNode.removeNode();
                }
            }
            var value = subNode.get("value");
            moduleData.itemValues.push(text+"|"+value);

            delete moduleData.properties.type;
            delete moduleData.properties.name;
            delete moduleData.properties.value;
            var fieldNode = new Element("div#"+id, {"mwftype": "checkbox"}).inject(subNode, "before");
            subNode.destroy();
            subNode = fieldNode;
        }.bind(this));
        return subNode;
    },
    convertCommonTextNode: function(subNode, moduleList, tag){
        this.getImplodeModuleJson(moduleList, "Common", (subNode.get("id") || "common"), subNode, function(id, moduleData){
            //moduleData.styles.display = subNode.getStyle("display");
            moduleData.innerHTML = subNode.get("text");
            moduleData.tagName = tag;
            subNode.set({"mwftype": "common", "id": id});
            subNode.empty();
        }.bind(this));
        return subNode;
    },
    convertCommonNode: function(subNode, moduleList, tag){
        this.getImplodeModuleJson(moduleList, "Common", (subNode.get("id") || "common"), subNode, function(id, moduleData){
            moduleData.tagName = tag;
            subNode.set({"mwftype": "common", "id": id});
        }.bind(this));
        return subNode;
    },
    checkNodeEmpty: function(node){
        if (node.nodeType===Node.TEXT_NODE){
            if (node.nodeValue.trim()) return false;
            return true;
        }
        if (node.nodeType===Node.ELEMENT_NODE){
            if (node.getElements("input").length) return false;
            if (node.getElements("img").length) return false;
            if (node.getElements("button").length) return false;
            if (node.getElements("audio").length) return false;
            if (node.getElements("canvas").length) return false;
            if (node.getElements("iframe").length) return false;
            if (node.getElements("object").length) return false;
            if (node.getElements("select").length) return false;
            if (node.getElements("video").length) return false;
            if (node.get("text").trim()) return false;
            return true;
        }
        return false;
    },
    parseImplodeHTML: function(node, moduleList, doc, readyDeleteNodes){
        if (!node) return ;
        var nodes = node.childNodes;
        for (var i = 0; i<nodes.length; i++){
            var subNode = nodes[i];

            if (!subNode) continue;
            subNode.normalize();
            var nextNode = true;
            if (subNode.nodeType===Node.TEXT_NODE){
                subNode = this.convertTextLabelNode(subNode, moduleList);
                nextNode = false;
            }
            if (subNode.nodeType===Node.ELEMENT_NODE){
                var tag = subNode.tagName.toString().toLowerCase();
                if (this.option2.checked){
                    if (!(tag==="table" || tag==="td" || tag==="th" || tag==="tr" || tag==="tbody" || tag==="thead" || tag==="tfoot" ||
                            tag==="input" || tag==="img" || tag==="button" || tag==="audio" || tag==="canvas" || tag==="iframe" ||
                            tag==="object" || tag==="select" || tag==="video")){
                        if (this.checkNodeEmpty(subNode)){
                            subNode.readyDelete = true;
                            readyDeleteNodes.push(subNode);
                            // if (subNode.destroy){
                            //     subNode.destroy();
                            // }else if (subNode.remove){
                            //     subNode.remove();
                            // }
                            // subNode = null;
                        }
                    }
                }
                if (subNode.readyDelete) continue;
                switch (tag){
                    //case "figure":
                    //case "figcaption":
                    //case "p":
                    case "div":
                        if (subNode.childNodes.length===1 && subNode.childNodes[0].nodeType===Node.TEXT_NODE && subNode.childNodes[0].nodeValue.trim()){
                            subNode = this.convertLabelNode(subNode, moduleList, tag);
                            nextNode = false;
                        }else{
                            subNode = this.convertDivNode(subNode, moduleList, tag);
                        }
                        break;
                    case "table":
                        subNode = this.convertTableNode(subNode, moduleList);
                        break;
                    //case "caption":
                    case "colgroup":
                    case "col":
                    case "br":
                    case "tr": break;
                    case "th":
                    case "td":
                        if (this.option1.checked){
                            if (this.checkNodeEmpty(subNode)){
                                new Element("input", {"tyep": "text"}).inject(subNode);
                            }
                        }
                        //subNode = this.convertTdNode(subNode, moduleList);
                        break;
                    case "img":
                        subNode = this.convertImgNode(subNode, moduleList);
                        nextNode = false;
                        break;
                    case "button":
                        subNode = this.convertButtonNode(subNode, moduleList);
                        nextNode = false;
                        break;
                    case "iframe":
                        subNode = this.convertIframeNode(subNode, moduleList);
                        nextNode = false;
                        break;
                    case "textarea":
                        subNode = this.convertTextareaNode(subNode, moduleList);
                        nextNode = false;
                        break;
                    case "select":
                        subNode = this.convertSelectNode(subNode, moduleList);
                        nextNode = false;
                        break;
                    case "input":
                        var t = subNode.get("type").toString().toLowerCase();
                        switch (t){
                            case "text":
                                subNode = this.convertTextfieldNode(subNode, moduleList);
                                break;
                            case "number":
                                subNode = this.convertNumberNode(subNode, moduleList);
                                break;
                            case "reset":
                            case "submit":
                            case "button":
                                subNode = this.convertCommonNode(subNode, moduleList, tag);
                                //subNode = this.convertButtonNode(subNode, moduleList);
                                break;
                            case "datetime":
                            case "datetime-local":
                            case "time":
                            case "date":
                                subNode = this.convertCalendarNode(subNode, moduleList);
                                break;
                            case "radio":
                                subNode = this.convertRadioNode(subNode, moduleList);
                                break;
                            case "checkbox":
                                subNode = this.convertCheckboxNode(subNode, moduleList);
                                break;
                            case "file":

                                break;
                            case "image":
                                subNode = this.convertInputImgNode(subNode, moduleList);
                                break;
                            default:
                                subNode = this.convertTextfieldNode(subNode, moduleList);
                        }
                        nextNode = false;
                        break;
                    case "base":
                    case "tbody":
                    case "thead":
                    case "tfoot": break;
                    default:
                        debugger;
                        if (subNode.childNodes.length===1 && subNode.childNodes[0].nodeType===Node.TEXT_NODE && subNode.childNodes[0].nodeValue.trim()){
                            subNode = this.convertCommonTextNode(subNode, moduleList, tag);
                            nextNode = false;
                        }else{
                             subNode = this.convertCommonNode(subNode, moduleList, tag);
                        }
                }
            }
            if (nextNode) this.parseImplodeHTML(subNode, moduleList, doc, readyDeleteNodes);
        }
    }
});
MWF.FormImport.Office = new Class({
    Extends: MWF.FormImport.Html,
    options: {
        "stylePath": "../x_component_portal_PageDesigner/$Import/{style}/style_office.css"
    },
    init: function(){
        this.inforText = this.form.designer.lp.importOffice_infor;
        this.inforText2 = this.form.designer.lp.importOffice_infor2;
        this.panelTitle = this.form.designer.lp.importOffice;
        this.panelWidth = 800;
        this.panelHeight = 240;
        this.editorMode = "html";
    },
    loadEditor: function(){
        //this.contentHtml
        if (this.contentCss) this.contentCss.destroy();
        if (this.inforText2Node) this.inforText2Node.destroy();

        this.file = new Element("input.importFile", {
            "type": "file",
            "accept": ".doc,.docx,.xls,.xlsx"
        }).inject(this.contentHtml);
    },
    loadEvent: function(){
        this.cancelNode.addEvent("click", function(){
            this.implodePanel.closePanel();
        }.bind(this));
        this.okNode.addEvent("click", function(e){
            var files = this.file.files;
            if (!files.length){
                this.form.designer.notice(this.form.designer.lp.implodeOfficeEmpty, "error", this.node);
                return false;
            }
            var _self = this;
            this.form.designer.confirm("warn", e, this.form.designer.lp.implodeConfirmTitle, this.form.designer.lp.implodeConfirmText, 400, 100, function(){
                _self.implodeOffice(files);
                this.close();
            }, function(){
                this.close();
            });
        }.bind(this));
    },
    implodeOffice: function(files){
        var file = files.item(0);
        var formData = new FormData();
        formData.append('file', file);

        MWF.Actions.get("x_general_assemble_control").convertHtml(formData, file, function(json){
            var html = json.data.value;
            this.implode(html);
        }.bind(this));
    }
});
MWF.FormImport.create = function(type, form, options){
    return new MWF.FormImport[type.capitalize()](form, options);
};