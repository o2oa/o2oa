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
        this.dpi = this.getDPI();
    },
    getDPI: function(){
        debugger;
        var div = new Element("div", {"styles": {"width": "1in", "height": "1in"}}).inject(document.body);
        var dpi = div.offsetWidth.toInt();
        div.destroy();
        return dpi;
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
        var documentPromise = this.zip.file("word/document.xml").async("text").then(function(oo_string){
            return this.processWordDocument(oo_string, data);
        }.bind(this)).then(function(oo_str){
            this.zip.file("word/document.xml", oo_str).generateAsync({type:"blob"}).then(function(oo_content) {
                o2.saveAs(oo_content, "example.docx");
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
    processWordDocument: function(oo_string, data){
        wgxpath.install();

        var domparser = new DOMParser();
        var oo_doc = domparser.parseFromString(oo_string, "text/xml");
        var oo_body = oo_doc.evaluate("//w:document/w:body", oo_doc, this.nsResolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;

        var dom_div = new Element("div", {"styles": {
                "display": "block",
                "width": "442.2pt",
                "padding": "104.9pt 73.7pt 99.25pt 79.4pt",
            }}).set("html", data).inject(document.body);
        var dom_pageRule = this.getPageRule(dom_div.getElement("style").sheet.cssRules);
        if (dom_pageRule) this.processPageSection(dom_pageRule, oo_body);

        var dom_wordSection = dom_div.getElement(".WordSection1");
        if (dom_wordSection){
            this.processPageSection(dom_wordSection, oo_body);
            this.processDom(dom_wordSection, oo_body);



        }



        debugger;
        dom_div.destroy();


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
        return s.serializeToString(oo_doc);
    },
    processDom: function(dom, oo_body){
        var dom = dom.getFirst();
        while (dom){
            if (dom.tagName.toLowerCase() === "p"){
                this.processParagraph(dom, oo_body);
            }else if (dom.tagName.toLowerCase() === "hr") {
                this.processHr(dom, oo_body);
            }else if (dom.tagName.toLowerCase() === "table") {
                this.processTable(dom, oo_body);
            }else{
                this.processDom(dom, oo_body);
            }
            dom = dom.getNext();
        }
    },
    processParagraph: function(dom, oo_body){
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
        var oo_p = this.createParagraph(oo_body.ownerDocument, {"pPrs": pPrs});
        var spans = dom.getChildren("span");
        spans.each(function(span){
            this.processRun(span, oo_p)
        }.bind(this));

        // debugger;
        // var hrs = dom.getChildren("hr");
        // hrs.each(function(hr){
        //     this.processHr(hr, oo_p)
        // }.bind(this));



        var oo_sectPr = this.getEl(oo_body, "sectPr");
        if (oo_sectPr){
            this.insertSiblings(oo_sectPr, [oo_p], "beforebegin");
        }else{
            this.insertChildren(oo_body, [oo_p]);
        }
    },
    getTableTblW: function(table){
        var type = "auto";
        var w = table.get("width").toFloat();
        if (w) w = this.pxToPt(w);
        if (!w) w = table.getStyle("width").toFloat();
        if (!w){
            w = 0;
        }else{
            type = "dxa";
            w = w*20;
        }
        return {"w": w, "type": type};
    },
    getTableBorder: function(table, where){
        var attr = {
            "space": "0",
            "val": "single",
            "color": "auto",
            "sz": "0"
        }
        var sz = table.get("border").toFloat();
        if (sz) sz = this.pxToPt(b);
        if (!sz) sz = table.getStyle("border-"+where+"-width").toFloat();
        if (!sz) sz = 0;
        attr.sz = sz*20;

        var color = this.getColorHex(table.getStyle("border-"+where+"-color"));
        if (!color) color = "auto";
        attr.color = color;

        var style = table.getStyle("border-"+where+"-style");
        switch (style){
            case "dashed": case "dotted": case "double": attr.val = "double"; break;
            default: attr.val = "single";
        }

        var space = table.get("cellspacing").toFloat();
        if (space) attr.space = this.pxToPt(space)*20;

        return attr;
    },
    getTableTblGrid: function(table){
        var grids = [];
        var trs = table.getElements("tr");
        for (var i = 0; i < trs.length; i++){
            var idx = 0;
            tds = trs[i].cells;
            tds.each(function(td){
                var colspan = td.get("colspan");
                if (!colspan) {
                    while (grids.length<=idx) grids.push(0);
                    var pt = this.pxToPt(td.clientWidth);
                    if (pt>grids[idx]) grids[idx] = pt;
                }else{
                    idx = idx + (colspan.toInt()-1);
                    while (grids.length<=idx) grids.push(0);
                }
                idx++;
            });
        }
        return grids;
    },
    processTable: function(table, oo_body){
        var oo_doc = oo_body.ownerDocument;

        var oo_tbl = this.createEl(oo_doc, "tbl");
        var oo_tblPr = this.createEl(oo_doc, "tblPr");

        //表格宽度属性
        var oo_tblW = this.createEl(oo_doc, "tblW");
        var tblW = this.getTableTblW(table);
        this.setAttrs(oo_tblW, tblW, false);
        oo_tblPr.appendChild(oo_tblW);

        //表格边框属性
        var oo_tblBorders = this.createEl(oo_doc, "tblBorders");
        var oo_top = this.createEl(oo_doc, "top");
        this.setAttrs(oo_top, this.getTableBorder(table, "top"), false);
        var oo_left = this.createEl(oo_doc, "left");
        this.setAttrs(oo_left, this.getTableBorder(table, "left"), false);
        var oo_bottom = this.createEl(oo_doc, "bottom");
        this.setAttrs(oo_bottom, this.getTableBorder(table, "bottom"), false);
        var oo_right = this.createEl(oo_doc, "right");
        this.setAttrs(oo_right, this.getTableBorder(table, "right"), false);
        this.insertSiblings(oo_tblBorders, [oo_top, oo_left, oo_bottom, oo_right], "beforeend")
        oo_tblPr.appendChild(oo_tblBorders);


        var mar = table.get("cellpadding").toFloat();
        if (mar){
            mar = this.pxToPt(b)*20;
            var oo_tblCellMar = this.createEl(oo_doc, "tblCellMar");
            var oo_mar = this.createEl(oo_doc, "left");
            this.setAttrs(oo_mar, {"type": "dxa", "w": mar});
            oo_tblCellMar.appendChild(oo_mar);
            oo_mar = this.createEl(oo_doc, "right");
            this.setAttrs(oo_mar, {"type": "dxa", "w": mar});
            oo_tblCellMar.appendChild(oo_mar);
            oo_mar = this.createEl(oo_doc, "top");
            this.setAttrs(oo_mar, {"type": "dxa", "w": mar});
            oo_tblCellMar.appendChild(oo_mar);
            oo_mar = this.createEl(oo_doc, "bottom");
            this.setAttrs(oo_mar, {"type": "dxa", "w": mar});
            oo_tblCellMar.appendChild(oo_mar);
            oo_tblPr.appendChild(oo_tblCellMar);
        }


        // <w:tblGrid>
        //     <w:gridCol w:w="4597"/>
        //     <w:gridCol w:w="4237"/>
        // </w:tblGrid>
    },

    pxToPt: function(px){
        return (px.toFloat()/this.dpi)*72;
    },
    processHr: function(hr, oo_body){
        var oo_doc = oo_body.ownerDocument;

        var oo_p = this.createParagraph(oo_doc, {});
        var oo_sectPr = this.getEl(oo_body, "sectPr");
        if (oo_sectPr){
            this.insertSiblings(oo_sectPr, [oo_p], "beforebegin");
        }else{
            this.insertChildren(oo_body, [oo_p]);
        }

        var oo_run = this.createRun(oo_doc, {"rPrs": {"noProof":{}}});
        var oo_drawing = this.createEl(oo_doc, "drawing");
        var oo_anchor = this.createEl(oo_doc, "anchor", "wp");
        this.setAttrs(oo_anchor, {
            "distT": "0", "distB": "0", "distL": "0", "distR": "0", "simplePos": "false","behindDoc": "false","relativeHeight": "500", "locked": "false", "layoutInCell":"true", "allowOverlap": "false"
        }, false);
        oo_run.appendChild(oo_drawing);
        oo_drawing.appendChild(oo_anchor);

        var oo_simplePos = this.createEl(oo_doc, "simplePos", "wp");
        this.setAttrs(oo_simplePos, {"x": "0", "y": "0"}, false);

        var oo_positionH = this.createEl(oo_doc, "positionH", "wp");
        this.setAttrs(oo_positionH, {"relativeFrom": "column"}, false);
        var oo_posOffset = this.createEl(oo_doc, "posOffset", "wp");
        oo_posOffset.appendChild(oo_doc.createTextNode("0"));
        oo_positionH.appendChild(oo_posOffset);

        var oo_positionV = this.createEl(oo_doc, "positionV", "wp");
        this.setAttrs(oo_positionV, {"relativeFrom": "paragraph"}, false);
        var oo_posOffset = this.createEl(oo_doc, "posOffset", "wp");
        oo_posOffset.appendChild(oo_doc.createTextNode("161290"));  //此处需要根据行高来设置数值,暂时固定数值
        oo_positionV.appendChild(oo_posOffset);

        var oo_extent = this.createEl(oo_doc, "extent", "wp");
        var cx = this.pxToPt(hr.clientWidth)*12700;
        this.setAttrs(oo_extent, {"cx": cx, "cy": "0"}, false);   //cx为线长度（pt*12700）

        var oo_effectExtent = this.createEl(oo_doc, "effectExtent", "wp");
        this.setAttrs(oo_effectExtent, {"l": "0", "t": "0", "r": "0", "b": "0"}, false);

        var oo_wrapNone = this.createEl(oo_doc, "wrapNone", "wp");

        var oo_docPr = this.createEl(oo_doc, "docPr", "wp");
        id = (Math.random()*100).toInt();
        this.setAttrs(oo_docPr, {"id": id, "name": "Red Line"}, false);   //id设置随机整数

        var oo_cNvGraphicFramePr = this.createEl(oo_doc, "cNvGraphicFramePr", "wp");

        this.insertSiblings(oo_anchor, [oo_simplePos, oo_positionH, oo_positionV, oo_extent, oo_effectExtent, oo_wrapNone, oo_docPr, oo_cNvGraphicFramePr], "beforeend");

        var oo_graphic = this.createEl(oo_doc, "graphic", "a");
        var oo_graphicData = this.createEl(oo_doc, "graphicData", "a");
        this.setAttrs(oo_graphicData, {"uri": "http://schemas.microsoft.com/office/word/2010/wordprocessingShape"}, false);
        var oo_wsp = this.createEl(oo_doc, "wsp", "wps");

        this.insertChildren(oo_anchor, [oo_graphic, oo_graphicData, oo_wsp], "beforeend");

        var oo_cNvCnPr = this.createEl(oo_doc, "cNvCnPr", "wps");
        var oo_spPr = this.createEl(oo_doc, "spPr", "wps")

        var oo_xfrm = this.createEl(oo_doc, "xfrm", "a");
        var oo_off = this.createEl(oo_doc, "off", "a");
        this.setAttrs(oo_off, {"x": "0", "y": "0"}, false);
        var oo_ext = this.createEl(oo_doc, "ext", "a");
        this.setAttrs(oo_ext, {"cx": cx, "cy": "0"}, false);

        this.insertSiblings(oo_xfrm, [oo_off, oo_ext], "beforeend");

        var oo_prstGeom = this.createEl(oo_doc, "prstGeom", "a");
        this.setAttrs(oo_prstGeom, {"prst": "line"}, false);
        var oo_avLst = this.createEl(oo_doc, "avLst", "a");
        oo_prstGeom.appendChild(oo_avLst);

        var oo_ln = this.createEl(oo_doc, "ln", "a");

        debugger;
        var w = this.pxToPt(hr.clientHeight)*12700;
        this.setAttrs(oo_ln, {"w": w}, false);    //线的粗细 pt*12700
        var oo_solidFill = this.createEl(oo_doc, "solidFill", "a");
        var oo_srgbClr = this.createEl(oo_doc, "srgbClr", "a");

        debugger;
        var color = this.getColorHex(hr.get("color"));
        if (!color) color = this.getColorHex(hr.getStyle("background-color"));
        if (!color) color = "FF0000";
        this.setAttrs(oo_srgbClr, {"val": color}, false);    //line color
        oo_solidFill.appendChild(oo_srgbClr);
        oo_ln.appendChild(oo_solidFill);

        this.insertSiblings(oo_spPr, [oo_xfrm, oo_prstGeom, oo_ln], "beforeend");

        var oo_style = this.createEl(oo_doc, "style", "wps");
        //oo_style.innerHTML = '<a:lnRef idx="1"><a:schemeClr val="accent1"/></a:lnRef><a:fillRef idx="0"><a:schemeClr val="accent1"/></a:fillRef><a:effectRef idx="0"><a:schemeClr val="accent1"/></a:effectRef><a:fontRef idx="minor"><a:schemeClr val="tx1"/></a:fontRef>';

        var oo_lnRef = this.createEl(oo_doc, "lnRef", "a");
        this.setAttrs(oo_lnRef, {"idx": "1"}, false);
        var oo_schemeClr = this.createEl(oo_doc, "schemeClr", "a");
        this.setAttrs(oo_schemeClr, {"val": "accent1"}, false);
        oo_lnRef.appendChild(oo_schemeClr);

        var oo_fillRef = this.createEl(oo_doc, "fillRef", "a");
        this.setAttrs(oo_fillRef, {"idx": "0"}, false);
        var oo_schemeClr = this.createEl(oo_doc, "schemeClr", "a");
        this.setAttrs(oo_schemeClr, {"val": "accent1"}, false);
        oo_fillRef.appendChild(oo_schemeClr);

        var oo_effectRef = this.createEl(oo_doc, "effectRef", "a");
        this.setAttrs(oo_effectRef, {"idx": "0"}, false);
        var oo_schemeClr = this.createEl(oo_doc, "schemeClr", "a");
        this.setAttrs(oo_schemeClr, {"val": "accent1"}, false);
        oo_effectRef.appendChild(oo_schemeClr);

        var oo_fontRef = this.createEl(oo_doc, "fontRef", "a");
        this.setAttrs(oo_fontRef, {"idx": "minor"}, false);
        var oo_schemeClr = this.createEl(oo_doc, "schemeClr", "a");
        this.setAttrs(oo_schemeClr, {"val": "tx1"}, false);
        oo_fontRef.appendChild(oo_schemeClr);

        this.insertSiblings(oo_style, [oo_lnRef, oo_fillRef, oo_effectRef, oo_fontRef], "beforeend");

        var oo_bodyPr = this.createEl(oo_doc, "bodyPr", "wps");



        this.insertSiblings(oo_wsp, [oo_cNvCnPr, oo_spPr, oo_style, oo_bodyPr], "beforeend");


        // <a:graphic xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main">
        //     <a:graphicData uri="http://schemas.microsoft.com/office/word/2010/wordprocessingShape">
        //         <wps:wsp>
        //             <wps:cNvCnPr/>
        //             <wps:spPr>
        //                 <a:xfrm>
        //                     <a:off x="0" y="0"/>
        //                     <a:ext cx="5631180" cy="0"/>
        //                 </a:xfrm>
        //                 <a:prstGeom prst="line">
        //                     <a:avLst/>
        //                 </a:prstGeom>
        //                 <a:ln w="19050">
        //                     <a:solidFill>
        //                         <a:srgbClr val="FF0000"/>
        //                     </a:solidFill>
        //                 </a:ln>
        //             </wps:spPr>
        //             <wps:style>
        //                 <a:lnRef idx="1">
        //                     <a:schemeClr val="accent1"/>
        //                 </a:lnRef>
        //                 <a:fillRef idx="0">
        //                     <a:schemeClr val="accent1"/>
        //                 </a:fillRef>
        //                 <a:effectRef idx="0">
        //                     <a:schemeClr val="accent1"/>
        //                 </a:effectRef>
        //                 <a:fontRef idx="minor">
        //                     <a:schemeClr val="tx1"/>
        //                 </a:fontRef>
        //             </wps:style>
        //             <wps:bodyPr/>
        //         </wps:wsp>
        //     </a:graphicData>
        // </a:graphic>

        oo_p.appendChild(oo_run);
    },


    processRun: function(span, oo_p){
        var rPrs = {"noProof": {}};
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
                    rPrs.spacing = {"val": (span.style["letterSpacing"].toFloat()*20 || 0)};
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
        if (!text.trim()) text = "";
        var oo_run = this.createRun(oo_p.ownerDocument, {"rPrs": rPrs, "font": font, "text": text});
        oo_p.appendChild(oo_run);
    },


    processPageSection: function(dom_pageRule, oo_body){
        var oo_sectPr = this.getOrCreateEl(oo_body, "sectPr");
        if (oo_sectPr){
            for (var i = 0; i<dom_pageRule.style.length; i++){
                switch (dom_pageRule.style[i]){
                    case "size":
                        var v = dom_pageRule.style["size"].split(/\s/);
                        var w = v[0].toFloat()*20, h=v[1].toFloat()*20;
                        var oo_pgSz = this.getOrCreateEl(oo_sectPr, "pgSz");
                        this.setAttrs(oo_pgSz, {"w": w, "h": h});
                        break;
                    case "margin-top":
                    case "margin-right":
                    case "margin-bottom":
                    case "margin-left":
                        var p = dom_pageRule.style[i].split("-")[1];
                        var v = dom_pageRule.style["margin"+p.capitalize()].toFloat()*20;
                        var oo_pgMar = this.getOrCreateEl(oo_sectPr, "pgMar");
                        var attrs = {};
                        attrs[p] = v
                        this.setAttrs(oo_pgMar, attrs);
                        break;
                    case "line-height":
                    case "letter-spacing":
                        var oo_docGrid = this.getOrCreateEl(oo_sectPr, "docGrid");
                        var lh = dom_pageRule.style["lineHeight"].toFloat()*20;
                        var cs = dom_pageRule.style["letterSpacing"].toFloat()*4096;
                        var attrs = {"type": "linesAndChars"};
                        if (lh) attrs["linePitch"] = lh;
                        if (cs) attrs["charSpace"] = cs;
                        this.setAttrs(oo_docGrid, attrs);
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
        if (ns===false){
            node.setAttribute(name, value);
        }else{
            var n = ns || "w";
            node.setAttributeNS(this.nsResolver(n), name, value);
        }
    },
    setAttrs: function(node, attrs, ns){
        if (ns===false){
            Object.keys(attrs).forEach(function(key){
                node.setAttribute(key, attrs[key]);
            });
        }else{
            var n = this.nsResolver(ns || "w");
            Object.keys(attrs).forEach(function(key){
                node.setAttributeNS(n, key, attrs[key]);
            });
        }
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
            "wps": "http://schemas.microsoft.com/office/word/2010/wordprocessingShape",
            "a": "http://schemas.openxmlformats.org/drawingml/2006/main"
        };
        return ns[prefix] || null;
    },
    getColorHex: function(clr){
        if (!clr) return "";
        var colorKeys = {
            "black": "000000",
            "silver": "c0c0c0",
            "gray": "808080",
            "white": "ffffff",
            "maroon": "800000",
            "red": "ff0000",
            "purple": "800080",
            "fuchsia": "ff00ff",
            "green": "008000",
            "lime": "00ff00",
            "olive": "808000",
            "yellow": "ffff00",
            "navy": "000080",
            "blue": "0000ff",
            "teal": "008080",
            "aqua": "00ffff"
        }
        if (colorKeys[clr]) return colorKeys[clr];
        return clr.rgbToHex() || clr;
    }
});
