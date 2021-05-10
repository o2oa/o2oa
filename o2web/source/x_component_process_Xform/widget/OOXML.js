o2.xApplication.process.Xform.widget = o2.xApplication.process.Xform.widget || {};
o2.xApplication.process.Xform.widget.OOXML = o2.xApplication.process.Xform.widget.OOXML || {};
o2.OOXML = o2.OOXML || {};
o2.xApplication.process.Xform.widget.OOXML.WordprocessingML = o2.OOXML.WML = new Class({
    Implements: [Options, Events],
    options: {
        "AppVersion": "16.0000",
        "Application": "o2oa",
        "Company": "",
        "ScaleCrop": "false",
        "LinksUpToDate": "false",
        "SharedDoc": "false",
        "HyperlinksChanged": "false"
    },
    initialize: function(container, worklog, processid, options){
        this.setOptions(options);
        this.path = "../x_component_process_Xform/widget/$OOXML/WordprocessingML/";
    },
    getZipTemplate: function(){
        return fetch(this.path+"template.zip").then(function(res){
            return res.blob().then(JSZip.loadAsync);
        });
    },
    load: function(data){
        o2.load(["/o2_lib/jszip/jszip.min.js", "/o2_lib/jszip/FileSaver.js", "/o2_lib/xml/wgxpath.install.js"], function(){
            this.getZipTemplate().then(function(zip){
                //console.log(zip.files);
                this.zip = zip;
                this.processDocument(data);
            }.bind(this));
        }.bind(this));

        //
        // var zip = new JSZip();
        // zip.file("Hello.txt", "Hello World\n");
        // var img = zip.folder("images");
        // img.file("smile.gif", imgData, {base64: true});
        // zip.generateAsync({type:"blob"}).then(function(content) {
        //     // see FileSaver.js
        //     saveAs(content, "example.zip");
        // });
    },
    processDocument: function(data){
        var documentPromise = this.zip.file("word/document.xml").async("text").then(function(xmlString){
            return this.processWordDocument(xmlString, data);
        }.bind(this)).then(function(xmlstr){
            this.zip.file("word/document.xml", xmlstr).generateAsync({type:"blob"}).then(function(content) {
                o2.saveAs(content, "example.docx");
            });
        }.bind(this));
        //var documentXml = processWordDocument(xmlString);

    },

    getPageRule: function(cssRules){
        var pageRule = null;
        if (cssRules){
            for (var i=0; i<cssRules.length; i++){
                if (cssRules[i].type===CSSRule.PAGE_RULE){
                    pageRule = cssRules[i];
                    break;
                }
            }
        }
        return pageRule;
    },
    processWordDocument: function(xmlString, data){
        wgxpath.install();

        var domparser = new DOMParser();
        var xmlDoc = domparser.parseFromString(xmlString, "text/xml");
        var body = xmlDoc.evaluate("//w:document/w:body", xmlDoc, this.nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;

        var tmpDiv = new Element("div", {"styles": {"display": "none"}}).set("html", data).inject(document.body);
        var pageRule = this.getPageRule(tmpDiv.getElement("style").sheet.cssRules);
        if (pageRule) this.processPageSection(pageRule, body);

        var wordSection = tmpDiv.getElement(".WordSection1");
        if (wordSection){
            this.processPageSection(wordSection, body);
            this.processDom(wordSection, body);



        }



        debugger;
        tmpDiv.destroy();


        // var p = this.createParagraph(xmlDoc);
        // var r = this.createRun(xmlDoc, {
        //     "text": "份数文本",
        //     "font": {
        //         "hint": "eastAsia",
        //         "eastAsia": "黑体",
        //         "other": "Times New Roman"
        //     },
        //     "rPrs": {
        //         "b": { val: "true" },
        //         "color": { val: "FF0000" }
        //     }
        // });
        // this.insertChildren(body, [p, r], "afterbegin");
        //
        var s = new XMLSerializer();
        return s.serializeToString(xmlDoc);
    },
    processDom: function(dom, body){
        var dom = dom.getFirst();
        while (dom){
            if (dom.tagName.toLowerCase() === "p"){
                this.processParagraph(dom, body);
            }else if (dom.tagName.toLowerCase() === "table") {

            }else{
                this.processDom(dom, body);
            }
            dom = dom.getNext();
        }
    },
    processParagraph: function(dom, body){
        var pPrs = {};
        for (var i = 0; i<dom.style.length; i++){
            switch (dom.style[i]){
                case "text-align":
                    var align = dom.style["textAlign"].toLowerCase();
                    var jc = "start"
                    switch (align){
                        case "center": jc = "center"; break;
                        case "right":
                        case "end": jc = "end"; break;
                        case "justify": jc = "both"; break;
                    }
                    pPrs.jc = {"val": jc};
                    break;
                default:
                //nothing
            }

        }
        var p = this.createParagraph(body.ownerDocument, {"pPrs": pPrs});
        var spans = dom.getChildren("span");
        spans.each(function(span){
            this.processRun(span, p)
        }.bind(this));

        var sectPr = this.getEl(body, "sectPr");
        if (sectPr){
            this.insertSiblings(sectPr, [p], "beforebegin");
        }else{
            this.insertChildren(body, [p]);
        }
    },
    processRun: function(span, p){
        var rPrs = {};
        var font = null;
        for (var i = 0; i<span.style.length; i++){
            switch (span.style[i]){
                case "font-size":
                    rPrs.sz = {"val": span.style["fontSize"].toFloat()*2};
                    break;
                case "color":
                    rPrs.color = {"val": span.style["color"]};
                    break;
                case "letter-spacing":
                    rPrs.spacing = {"val": span.style["letterSpacing"].toFloat()*20};
                    break;
                case "font-weight":
                    var b = span.style["fontWeight"];
                    if (b.toLowerCase()=="normal"){
                        //nothing
                    }else if (b.toLowerCase()=="bold") {
                        rPrs.b = {"val": "true"};
                    }else{
                        var n = b.toFloat();
                        if (n>=600) rPrs.b = {"val": "true"};
                    }
                    break;
                case "font-family":
                    var fonts = span.style["fontFamily"].split(/,\s*/);
                    font = {
                        "hint": "eastAsia",
                        "eastAsia": fonts[0]
                    }
                    if (fonts.length>1){
                        font.other = fonts[1];
                    }
                    break;
                default:
                //nothing
            }
        }
        var text = span.get("text");
        var run = this.createRun(p.ownerDocument, {"rPrs": rPrs, "font": font, "text": text});
        p.appendChild(run);
    },


    processPageSection: function(pageRule, xmlBody){
        var sectPr = this.getOrCreateEl(xmlBody, "sectPr");
        if (sectPr){
            for (var i = 0; i<pageRule.style.length; i++){
                switch (pageRule.style[i]){
                    case "size":
                        var v = pageRule.style["size"].split(/\s/);
                        var w = v[0].toFloat()*20, h=v[1].toFloat()*20;
                        var pgSz = this.getOrCreateEl(sectPr, "pgSz");
                        this.setAttrs(pgSz, {"w": w, "h": h});
                        break;
                    case "margin-top":
                    case "margin-right":
                    case "margin-bottom":
                    case "margin-left":
                        var p = pageRule.style[i].split("-")[1];
                        var v = pageRule.style["margin"+p.capitalize()].toFloat()*20;
                        var pgMar = this.getOrCreateEl(sectPr, "pgMar");
                        var attrs = {};
                        attrs[p] = v
                        this.setAttrs(pgMar, attrs);
                        break;
                    case "line-height":
                    case "letter-spacing":
                        var docGrid = this.getOrCreateEl(sectPr, "docGrid");
                        var lh = pageRule.style["lineHeight"].toFloat()*20;
                        var cs = pageRule.style["letterSpacing"].toFloat()*4096;
                        var attrs = {"type": "linesAndChars"};
                        if (lh) attrs["linePitch"] = lh;
                        if (cs) attrs["charSpace"] = cs;
                        this.setAttrs(docGrid, attrs);
                        break;
                    default:
                    //nothing
                }

            }
        }

    },

    getOrCreateEl: function(el, tag){
        var node = this.getEl(el, tag);
        if (!node){
            node = this.createEl(el.ownerDocument, tag);
            el.appendChild(node);
        }
        return node;
    },

    createParagraph: function(xmlDoc, options){
        var p = this.createEl(xmlDoc,"p");
        var pPr = this.createEl(xmlDoc,"pPr");

        /*
        * //如：对齐方式描述如下
        * {
        *   "jc": { val: "both" },
        * }
        * */
        if (options && options.pPrs){
            Object.keys(options.pPrs).each(function(k){
                var node = this.createEl(xmlDoc, k);
                this.setAttrs(node, options.pPrs[k]);
                pPr.appendChild(node);
            }.bind(this));
        }

        p.appendChild(pPr);
        return p;
    },
    createRun: function(xmlDoc, options){
        var r = this.createEl(xmlDoc, "r");
        var rPr = this.createEl(xmlDoc,"rPr");
        r.appendChild(rPr);

        if (options && options.text){
            var t = this.createEl(xmlDoc,"t");
            t.appendChild(xmlDoc.createTextNode(options.text));
            r.appendChild(t);
        }
        if (options && options.font){
            var rFonts = this.createEl(xmlDoc,"rFonts");
            var font = {
                "eastAsia": options.font.eastAsia || options.font.font,
                "ascii": options.font.ascii || options.font.other || options.font.eastAsia || options.font.font,
                "hAnsi": options.font.hAnsi || options.font.other || options.font.eastAsia || options.font.font,
            }
            if (options.font.hint) font.hint = options.font.hint;
            this.setAttrs(rFonts, font);
            rPr.appendChild(rFonts);
        }
        /*
        * //如：粗体和字体颜色描述如下
        * {
        *   "b": { val: "true" },
        *   "color": { val: "FF0000" }
        * }
        * */
        if (options && options.rPrs){
            Object.keys(options.rPrs).each(function(k){
                var node = this.createEl(xmlDoc, k);
                this.setAttrs(node, options.rPrs[k]);
                rPr.appendChild(node);
            }.bind(this));
        }

        return r;
    },

    insertChildren: function(p, els, position){
        this.insertAdjacent(p, els, position, "beforeend");
    },
    insertSiblings: function(p, els, position){
        this.insertAdjacent(p, els, position, "afterend");
    },
    insertAdjacent: function(p, els, posFirst, posNext){
        var pos = posFirst || "beforeend"
        els.each(function(e){
            p.insertAdjacentElement(pos, e);
            p = e;
            pos = posNext || "afterend";
        });
    },
    getEl: function(el, tag, ns){
        return this.getEls(el, tag, ns)[0];
    },
    getEls: function(el, tag, ns){
        var n = ns || "w";
        return el.getElementsByTagNameNS(this.nsResolver(n), tag);
    },
    createEl: function(xmlDoc, tag, ns){
        var n = ns || "w";
        return xmlDoc.createElementNS(this.nsResolver(n), tag);
    },
    setAttr: function(node, name, value, ns){
        var n = ns || "w";
        node.setAttributeNS(this.nsResolver(n), name, value);
    },
    setAttrs: function(node, attrs, ns){
        var n = this.nsResolver(ns || "w");
        Object.keys(attrs).forEach(function(key){
            node.setAttributeNS(n, key, attrs[key]);
        });
    },
    nsResolver: function(prefix){
        var ns = {
            "w": "http://schemas.openxmlformats.org/wordprocessingml/2006/main",
            "wpc": "http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas",
            "cx": "http://schemas.microsoft.com/office/drawing/2014/chartex",
            "cx1": "http://schemas.microsoft.com/office/drawing/2015/9/8/chartex",
            "cx2": "http://schemas.microsoft.com/office/drawing/2015/10/21/chartex",
            "cx3": "http://schemas.microsoft.com/office/drawing/2016/5/9/chartex",
            "cx4": "http://schemas.microsoft.com/office/drawing/2016/5/10/chartex",
            "cx5": "http://schemas.microsoft.com/office/drawing/2016/5/11/chartex",
            "cx6": "http://schemas.microsoft.com/office/drawing/2016/5/12/chartex",
            "cx7": "http://schemas.microsoft.com/office/drawing/2016/5/13/chartex",
            "cx8": "http://schemas.microsoft.com/office/drawing/2016/5/14/chartex",
            "mc": "http://schemas.openxmlformats.org/markup-compatibility/2006",
            "aink": "http://schemas.microsoft.com/office/drawing/2016/ink",
            "am3d": "http://schemas.microsoft.com/office/drawing/2017/model3d",
            "o": "urn:schemas-microsoft-com:office:office",
            "r": "http://schemas.openxmlformats.org/officeDocument/2006/relationships",
            "m": "http://schemas.openxmlformats.org/officeDocument/2006/math",
            "v": "urn:schemas-microsoft-com:vml",
            "wp14": "http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing",
            "wp": "http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing",
            "w10": "urn:schemas-microsoft-com:office:word",
            "w14": "http://schemas.microsoft.com/office/word/2010/wordml",
            "w15": "http://schemas.microsoft.com/office/word/2012/wordml",
            "w16cex": "http://schemas.microsoft.com/office/word/2018/wordml/cex",
            "w16cid": "http://schemas.microsoft.com/office/word/2016/wordml/cid",
            "w16": "http://schemas.microsoft.com/office/word/2018/wordml",
            "w16sdtdh": "http://schemas.microsoft.com/office/word/2020/wordml/sdtdatahash",
            "w16se": "http://schemas.microsoft.com/office/word/2015/wordml/symex",
            "wpg": "http://schemas.microsoft.com/office/word/2010/wordprocessingGroup",
            "wpi": "http://schemas.microsoft.com/office/word/2010/wordprocessingInk",
            "wne": "http://schemas.microsoft.com/office/word/2006/wordml",
            "wps": "http://schemas.microsoft.com/office/word/2010/wordprocessingShape"
        };
        return ns[prefix] || null;
    }
});
