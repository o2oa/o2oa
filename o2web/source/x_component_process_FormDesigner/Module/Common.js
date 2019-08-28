MWF.xApplication.process.FormDesigner.Module.Common = MWF.FCCommon = new Class({
	Extends: MWF.FCDiv,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_process_FormDesigner/Module/Common/common.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_process_FormDesigner/Module/Common/";
		this.cssPath = "/x_component_process_FormDesigner/Module/Common/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "common";
		
		this.Node = null;
		this.form = form;
	},

    _setNodeProperty: function(){

        this.node.store("module", this);
        if (this.form.moduleList.indexOf(this)==-1) this.form.moduleList.push(this);
        if (this.form.moduleNodeList.indexOf(this.node)==-1) this.form.moduleNodeList.push(this.node);
        if (this.form.moduleContainerNodeList.indexOf(this.node)==-1) this.form.moduleContainerNodeList.push(this.node);
        this.node.store("module", this);

        this._setEditStyle_custom("innerHTML");
    },

    _setEditStyle_custom: function(name, obj, oldValue){
        if (name==="tagName"){
            var tagName = this.json.tagName.toString().toLowerCase();
            var nodeTag = this.node.tagName.toString().toLowerCase();
            if (tagName !== nodeTag){
            	var node = new Element(tagName).inject(this.node, "before");

            	var nodes = this.node.childNodes;
				for (var i = 0; i < nodes.length; i++){
                    node.appendChild(nodes[i]);
				}
				this.node.destroy();
				this.node = node;

                this.node.set("mwftype", "common");
                this.node.set("id", this.json.id);
                this.isSetEvents = false;
                this._initModule();

                var title = this.json.name || this.json.id;
                var text = text = this.json.tagName+"(Common)";
                this.treeNode.setText("<"+text+"> "+title);
			}
        }
        if (name==="innerHTML"){
            try{
                if (this.json.innerHTML){
                    var nodes = this.node.childNodes;
                    for (var i=0; i<nodes.length; i++){
                        if (nodes[i].nodeType===Node.ELEMENT_NODE){
                            if (!nodes[i].get("MWFtype")){
                                nodes[i].destroy();
                                i--;
                            }
                        }else{
                            if (nodes[i].removeNode){
                                nodes[i].removeNode();
                            }else{
                                nodes[i].parentNode.removeChild(nodes[i]);
                            }
                            i--;
                            //nodes[i]
                        }
                    }
                    this.node.appendHTML(this.json.innerHTML);
                }
            }catch(e){}
        }
    },
    setPropertiesOrStyles: function(name, oldData){
        if (name=="styles"){
            try{
                this.setCustomStyles();
            }catch(e){}
        }
        if (name==="properties"){
            try{
            	if (oldData){
                    Object.each(oldData, function(v,k){
                        this.node.removeProperty(k);
                    }.bind(this));
				}
                Object.each(this.json.properties, function(v,k){
                	if (k.toString().toLowerCase()==="href"){
                        this.node.setProperty(k, "#");
					}else if (k.toString().toLowerCase()==="target") {
                        this.node.removeProperty("target");
					}else{
                        this.node.setProperty(k, v);
                    }
				}.bind(this));

            }catch(e){}
        }
    },

    setCustomStyles: function(){
        var border = this.node.getStyle("border");
        this.node.clearStyles();
        var styles = this.node.getStyles("display", "padding");
        this.node.setStyles(this.css.moduleNode);
        var style = Object.clone(this.json.styles);
        //style = Object.merge(style, styles);
        if (styles.display.toString().toLowerCase()==="inline"){
            if (!style.display) style.display = "inline-block";
            if (!style.padding && !style["padding-left"] && !style["padding-right"]) style.padding = "0px 2px";
        }
        if (this.json.tagName==="button"){
            if (!style["min-height"]) style["min-height"] = "20px";
        }

        if (this.initialStyles) this.node.setStyles(this.initialStyles);
        this.node.setStyle("border", border);

        if (style) Object.each(style, function(value, key){
            if ((value.indexOf("x_processplatform_assemble_surface")!=-1 || value.indexOf("x_portal_assemble_surface")!=-1)){
                var host1 = MWF.Actions.getHost("x_processplatform_assemble_surface");
                var host2 = MWF.Actions.getHost("x_portal_assemble_surface");
                if (value.indexOf("/x_processplatform_assemble_surface")!==-1){
                    value = value.replace("/x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }else if (value.indexOf("x_processplatform_assemble_surface")!==-1){
                    value = value.replace("x_processplatform_assemble_surface", host1+"/x_processplatform_assemble_surface");
                }
                if (value.indexOf("/x_portal_assemble_surface")!==-1){
                    value = value.replace("/x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }else if (value.indexOf("x_portal_assemble_surface")!==-1){
                    value = value.replace("x_portal_assemble_surface", host2+"/x_portal_assemble_surface");
                }
            }

            var reg = /^border\w*/ig;
            if (!key.test(reg)){
                if (key){
                    if (key.toString().toLowerCase()==="display"){
                        if (value.toString().toLowerCase()==="none"){
                            this.node.setStyle("opacity", 0.3);
                        }else{
                            this.node.setStyle("opacity", 1);
                            this.node.setStyle(key, value);
                        }
                    }else{
                        this.node.setStyle(key, value);
                    }
                }
            }
        }.bind(this));
    }
});
