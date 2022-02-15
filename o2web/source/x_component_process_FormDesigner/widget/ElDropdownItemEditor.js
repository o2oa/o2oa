// o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
// o2.require("o2.widget.Tree", null, false);
o2.requireApp("process.FormDesigner", "widget.ElTreeEditor", null, false);
MWF.xApplication.process.FormDesigner.widget.ElDropdownItemEditor = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.ElTreeEditor,
	createContent: function(content){
		this.contentNode = new Element("div", {
			"styles": this.css.contentNode
		}).inject(this.container);
		
		this.data = content;
		
		this.resizeContentNodeSize();
		
		this.tree = new MWF.xApplication.process.FormDesigner.widget.ElDropdownItemEditor.Tree(this, this.contentNode, {"style": "editor"});
		this.tree.data = this.data;
		this.tree.load();
		
	}
});

MWF.xApplication.process.FormDesigner.widget.ElDropdownItemEditor.Tree = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.ElTreeEditor.Tree,
	nodejson: {
		"label": "[none]",
		"command": "",
		"disabled": false,
		"divided": false,
		"icon":""
	},
	appendChild: function(obj){
		var treeNode = new MWF.xApplication.process.FormDesigner.widget.ElDropdownItemEditor.Tree.Node(this, obj);
		
		if (this.children.length){
			treeNode.previousSibling = this.children[this.children.length-1];
			treeNode.previousSibling.nextSibling = treeNode;
		}else{
			this.firstChild = treeNode;
		}
		treeNode.level = 0;
		
		treeNode.load();
		treeNode.node.inject(this.node);
		this.children.push(treeNode);
		return treeNode;
	}
});

MWF.xApplication.process.FormDesigner.widget.ElDropdownItemEditor.Tree.Node = new Class({
	Extends: MWF.xApplication.process.FormDesigner.widget.ElTreeEditor.Tree.Node,
	options: {
		"expand": true
	},
	createItemActionNode: function(){
		this.actionNode = new Element("div", {
			"styles": this.tree.css.itemActionNode
		}).inject(this.itemNode);
		
		var deleteAction = new Element("div", {
			"styles": this.tree.css.itemDeleteActionNode,
			"title": o2.LP.process.formAction["delete"],
			"events": {
				"click": function(e){
					this.deleteItem(e);
					e.stopPropagation();
				}.bind(this)
			}
		}).inject(this.actionNode);
	},
	editItemProperties: function(){
		if (this.tree.currentEditNode!=this){
			if (this.tree.currentEditNode) this.tree.currentEditNode.completeItemProperties();

			this.itemNode.setStyle("background", "#DDD");
			if (!this.propertyArea){
				this.propertyArea = new Element("div", {
					style : "border-bottom:1px solid #666"
				}).inject(this.itemNode, "after");

				this.propertyTable = new Element("table", {
					"width": "100%",
					"border": "0",
					"cellpadding":"5",
					"cellspacing":"0",
					"class": "editTable"
				}).inject(this.propertyArea);


				var tr = new Element("tr").inject(this.propertyTable);
				var td = new Element("td", { text: "文本" }).inject(tr);
				td = new Element("td").inject(tr);
				this.labelInput = new Element("input", {
					value: this.data.label || "[none]",
					events: {
						blur: function () {
							this.data.label = this.labelInput.get("value");
							this.textNode.getElement("div").set("text", this.data.label);
						}.bind(this)
					}
				}).inject(td);

				tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { text: "指令" }).inject(tr);
				td = new Element("td").inject(tr);
				this.idCommand = new Element("input", {
					value: this.data.command || "",
					events: {
						blur: function () {
							this.data.command = this.idCommand.get("value");
						}.bind(this)
					}
				}).inject(td);

				tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { text: "禁用" }).inject(tr);
				td = new Element("td").inject(tr);
				var div = new Element( "div").inject(td);
				var radio_disabled_1 = new Element( "input", {
					"type" : "radio",
					"checked" : !!this.data.disabled,
					"events" : {
						"click": function () {
							this.data.disabled = true;
							radio_disabled_2.checked = false;
						}.bind(this)
					}
				}).inject( div );
				new Element( "span", { "text" : "是" }).inject(div);
				var radio_disabled_2 = new Element( "input", {
					"type" : "radio",
					"checked" : !this.data.disabled,
					"events" : {
						"click": function () {
							this.data.disabled = false;
							radio_disabled_1.checked = false;
						}.bind(this)
					}
				}).inject( div );
				new Element( "span", { "text" : "否" }).inject(div);

				tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { text: "显示分割线" }).inject(tr);
				td = new Element("td").inject(tr);
				div = new Element( "div").inject(td);
				var radio_divided_1 = new Element( "input", {
					"type" : "radio",
					"checked" : !!this.data.divided,
					"events" : {
						"click": function(){
							this.data.divided = true;
							radio_divided_2.checked = false;
						}.bind(this)
					}
				}).inject( div );
				new Element( "span", { "text" : "是" }).inject(div);
				var radio_divided_2 = new Element( "input", {
					"type" : "radio",
					"checked" : !this.data.divided,
					"events" : {
						"click": function () {
							this.data.divided = false;
							radio_divided_1.checked = false;
						}.bind(this)
					}
				}).inject( div );
				new Element( "span", { "text" : "否" }).inject(div);

				tr = new Element("tr").inject(this.propertyTable);
				td = new Element("td", { text: "图标" }).inject(tr);
				td = new Element("td").inject(tr);
				var div = new Element("div", {
					style: "overflow:hidden;height:30px;line-height:30px;"
				}).inject(td);
				this.iconNode = new Element("div",{
					class : this.data.icon,
					style: "float:left; width:30px;height:30px;font-size:20px;margin-top:2px;"
				}).inject(div);
				new Element("div",{
					text : "选择图标",
					style: "float:left; padding:0px 20px; height:24px;line-height:24px; border:1px solid #ccc; border-radius:5px;cursor:pointer",
					events: {
						click: this.loadElSelectIcon.bind(this)
					}
				}).inject(div);

			}

			this.propertyArea.setStyle("display", "block");
			this.propertyArea.scrollIntoView();
			this.setActionPosition();

			this.isEditProperty = true;
			this.tree.currentEditNode = this;
		}else{
			this.completeItemProperties();
		}
	},
	_loadVue: function(callback){
		if (!window.Vue){
			o2.loadAll({"css": "../o2_lib/vue/element/index.css", "js": ["vue", "elementui"]}, { "sequence": true }, callback);
		}else{
			if (callback) callback();
		}
	},
	loadElSelectIcon: function(){
		var _self = this;
		var icons = ["el-icon-platform-eleme","el-icon-eleme","el-icon-delete-solid","el-icon-delete","el-icon-s-tools","el-icon-setting","el-icon-user-solid","el-icon-user","el-icon-phone","el-icon-phone-outline","el-icon-more","el-icon-more-outline","el-icon-star-on","el-icon-star-off","el-icon-s-goods","el-icon-goods","el-icon-warning","el-icon-warning-outline","el-icon-question","el-icon-info","el-icon-remove","el-icon-circle-plus","el-icon-success","el-icon-error","el-icon-zoom-in","el-icon-zoom-out","el-icon-remove-outline","el-icon-circle-plus-outline","el-icon-circle-check","el-icon-circle-close","el-icon-s-help","el-icon-help","el-icon-minus","el-icon-plus","el-icon-check","el-icon-close","el-icon-picture","el-icon-picture-outline","el-icon-picture-outline-round","el-icon-upload","el-icon-upload2","el-icon-download","el-icon-camera-solid","el-icon-camera","el-icon-video-camera-solid","el-icon-video-camera","el-icon-message-solid","el-icon-bell","el-icon-s-cooperation","el-icon-s-order","el-icon-s-platform","el-icon-s-fold","el-icon-s-unfold","el-icon-s-operation","el-icon-s-promotion","el-icon-s-home","el-icon-s-release","el-icon-s-ticket","el-icon-s-management","el-icon-s-open","el-icon-s-shop","el-icon-s-marketing","el-icon-s-flag","el-icon-s-comment","el-icon-s-finance","el-icon-s-claim","el-icon-s-custom","el-icon-s-opportunity","el-icon-s-data","el-icon-s-check","el-icon-s-grid","el-icon-menu","el-icon-share","el-icon-d-caret","el-icon-caret-left","el-icon-caret-right","el-icon-caret-bottom","el-icon-caret-top","el-icon-bottom-left","el-icon-bottom-right","el-icon-back","el-icon-right","el-icon-bottom","el-icon-top","el-icon-top-left","el-icon-top-right","el-icon-arrow-left","el-icon-arrow-right","el-icon-arrow-down","el-icon-arrow-up","el-icon-d-arrow-left","el-icon-d-arrow-right","el-icon-video-pause","el-icon-video-play","el-icon-refresh","el-icon-refresh-right","el-icon-refresh-left","el-icon-finished","el-icon-sort","el-icon-sort-up","el-icon-sort-down","el-icon-rank","el-icon-loading","el-icon-view","el-icon-c-scale-to-original","el-icon-date","el-icon-edit","el-icon-edit-outline","el-icon-folder","el-icon-folder-opened","el-icon-folder-add","el-icon-folder-remove","el-icon-folder-delete","el-icon-folder-checked","el-icon-tickets","el-icon-document-remove","el-icon-document-delete","el-icon-document-copy","el-icon-document-checked","el-icon-document","el-icon-document-add","el-icon-printer","el-icon-paperclip","el-icon-takeaway-box","el-icon-search","el-icon-monitor","el-icon-attract","el-icon-mobile","el-icon-scissors","el-icon-umbrella","el-icon-headset","el-icon-brush","el-icon-mouse","el-icon-coordinate","el-icon-magic-stick","el-icon-reading","el-icon-data-line","el-icon-data-board","el-icon-pie-chart","el-icon-data-analysis","el-icon-collection-tag","el-icon-film","el-icon-suitcase","el-icon-suitcase-1","el-icon-receiving","el-icon-collection","el-icon-files","el-icon-notebook-1","el-icon-notebook-2","el-icon-toilet-paper","el-icon-office-building","el-icon-school","el-icon-table-lamp","el-icon-house","el-icon-no-smoking","el-icon-smoking","el-icon-shopping-cart-full","el-icon-shopping-cart-1","el-icon-shopping-cart-2","el-icon-shopping-bag-1","el-icon-shopping-bag-2","el-icon-sold-out","el-icon-sell","el-icon-present","el-icon-box","el-icon-bank-card","el-icon-money","el-icon-coin","el-icon-wallet","el-icon-discount","el-icon-price-tag","el-icon-news","el-icon-guide","el-icon-male","el-icon-female","el-icon-thumb","el-icon-cpu","el-icon-link","el-icon-connection","el-icon-open","el-icon-turn-off","el-icon-set-up","el-icon-chat-round","el-icon-chat-line-round","el-icon-chat-square","el-icon-chat-dot-round","el-icon-chat-dot-square","el-icon-chat-line-square","el-icon-message","el-icon-postcard","el-icon-position","el-icon-turn-off-microphone","el-icon-microphone","el-icon-close-notification","el-icon-bangzhu","el-icon-time","el-icon-odometer","el-icon-crop","el-icon-aim","el-icon-switch-button","el-icon-full-screen","el-icon-copy-document","el-icon-mic","el-icon-stopwatch","el-icon-medal-1","el-icon-medal","el-icon-trophy","el-icon-trophy-1","el-icon-first-aid-kit","el-icon-discover","el-icon-place","el-icon-location","el-icon-location-outline","el-icon-location-information","el-icon-add-location","el-icon-delete-location","el-icon-map-location","el-icon-alarm-clock","el-icon-timer","el-icon-watch-1","el-icon-watch","el-icon-lock","el-icon-unlock","el-icon-key","el-icon-service","el-icon-mobile-phone","el-icon-bicycle","el-icon-truck","el-icon-ship","el-icon-basketball","el-icon-football","el-icon-soccer","el-icon-baseball","el-icon-wind-power","el-icon-light-rain","el-icon-lightning","el-icon-heavy-rain","el-icon-sunrise","el-icon-sunrise-1","el-icon-sunset","el-icon-sunny","el-icon-cloudy","el-icon-partly-cloudy","el-icon-cloudy-and-sunny","el-icon-moon","el-icon-moon-night","el-icon-dish","el-icon-dish-1","el-icon-food","el-icon-chicken","el-icon-fork-spoon","el-icon-knife-fork","el-icon-burger","el-icon-tableware","el-icon-sugar","el-icon-dessert","el-icon-ice-cream","el-icon-hot-water","el-icon-water-cup","el-icon-coffee-cup","el-icon-cold-drink","el-icon-goblet","el-icon-goblet-full","el-icon-goblet-square","el-icon-goblet-square-full","el-icon-refrigerator","el-icon-grape","el-icon-watermelon","el-icon-cherry","el-icon-apple","el-icon-pear","el-icon-orange","el-icon-coffee","el-icon-ice-tea","el-icon-ice-drink","el-icon-milk-tea","el-icon-potato-strips","el-icon-lollipop","el-icon-ice-cream-square","el-icon-ice-cream-round"];
		var area = new Element("div", {
			"styles": {
				"height": "390px",
				 "overflow": "auto",
				"font-size": "24px",
				"opacity": 0
			}
		}).inject($(document.body));
		icons.forEach(function(i){
			if (_self.data.icon==i){
				area.appendHTML("<i style=\"background-color: #999999; padding:5px}\" @click='selected' data-icon=\""+i+"\" class=\""+i+" mainColor_bg\"></i>");
			}else{
				area.appendHTML("<i style='cursor: pointer; padding:5px' @click='selected' data-icon=\""+i+"\" class='"+i+"'></i>");
			}
		});
		var dlg = o2.DL.open({
			"title": "选择图标",
			"isTitle": true,
			"width": 400,
			"height": 500,
			"content": area,
			"buttonList": [
				{
					"type": "ok",
					"text": "关闭",
					"action": function(){this.close();}
				}
			],
			"onPostLoad": function () {
				area.setStyle("opacity", 1);
				this._loadVue(function(){
					new Vue({
						methods:{
							selected: function(e){
								var iNode = (e.target || e.srcElement);
								if (iNode && iNode.hasClass("mainColor_bg")){
									iNode.removeClass("mainColor_bg");
									_self.iconNode.removeClass(_self.data.icon);
									_self.data.icon = "";
									dlg.close()
								}else{
									this.$el.getElements("i").forEach(function(el){
										if (el.hasClass("mainColor_bg")) el.removeClass("mainColor_bg");
									});
									if (iNode){
										iNode.addClass("mainColor_bg");
										var iconName = iNode.dataset["icon"];
										_self.iconNode.removeClass(_self.data.icon).addClass(iconName);
										_self.data.icon = iconName;
										dlg.close();
									}
								}
							}
						}
					}).$mount(area);
				}.bind(this));
			}.bind(this)
		})
	},
	editItem: function(node, okCallBack){
		var text = node.get("text");
		node.set("html", "");
		
		var div = new Element("div", {
			"styles": this.tree.css.editInputDiv,
		});
		var input = new Element("input", {
			"styles": this.tree.css.editInput,
			"type": "text",
			"value": text
		}).inject(div);
		var w = o2.getTextSize(text+"a").x;
		input.setStyle("width", w);
		div.setStyle("width", w);

		div.inject(node);
		input.select();
		
		input.addEvents({
			"keydown": function(e){
				var x = o2.getTextSize(input.get("value")+"a").x;
				e.target.setStyle("width", x);
				e.target.getParent().setStyle("width", x);
				if (e.code==13){
					this.isEnterKey = true;
					e.target.blur();
				}
			}.bind(this),
			"blur": function(e){
				var flag = this.editItemComplate(node, e.target);
				if (okCallBack) okCallBack(flag);
			}.bind(this),
			"click": function(e){
				e.stopPropagation();
			}.bind(this)
		});
		
	},
	editItemComplate: function(node, input){
		var text = input.get("value");
	//	if (node == this.keyNode){
			if (!text){
				text = "[none]";
			}
			
			this.data.label = text;
	//	}

		var addNewItem = false;
		if (this.isEnterKey){
			if (this.isNewItem){
				addNewItem = true;
			}
			this.editOkAddNewItem = false;
		}
		this.isNewItem = false;

		node.set("html", text);

		if( this.labelInput )this.labelInput.set("value", text);

		this.tree.editor.fireEvent("change");
		
		return true;
	}

});
