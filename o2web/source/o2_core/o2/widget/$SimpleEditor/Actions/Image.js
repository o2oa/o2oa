o2.widget.SimpleEditor.Actions = o2.widget.SimpleEditor.Actions || {};
o2.widget.SimpleEditor.Actions.Image = new Class({
    Implements: [Options, Events],
    options: {
        style: "default",
        title: o2.LP.widget.SimpleEditor.insertImage
    },
    initialize: function (options, editor, toolbar, button) {
        this.setOptions(options);
        this.editor = editor;
        this.toolbar = toolbar;
        this.button = button;
    },
    command: function (e) {
        this.fireEvent("queryCommand");

        var html = ''
        if (!this.webImageNode || this.webImageNode.get('value') == '') {
            this.closeDialog();
            return;
        } else {
            html = '<img src="' + _escape(this.webImageNode.get('value')) + '" data-ke-src="' + _escape(this.webImageNode.get('value')) + '" ';
            if (this.imageWidthNode && this.imageWidthNode.get('value') != '') html += 'width="' + _escape(this.imageWidthNode.get('value')) + '" ';
            if (this.imageHeightNode && this.imageHeightNode.get('value') != '') html += 'height="' + _escape(this.imageHeightNode.get('value')) + '" ';
            if (this.imageTitleNode && this.imageTitleNode.get('value') != '') {
                html += 'title="' + _escape(this.imageTitleNode.get('value')) + '" ';
                html += 'alt="' + _escape(this.imageTitleNode.get('value')) + '" ';
            }
            html += '/>';
        }

        this.closeDialog();
        if (this.editor.selectionRange) {
            this.editor.selection.setRange(this.editor.selectionRange);
        } else {
            this.editor.editareaNode.focus();
        }
        this.editor.selection.insertContent(html);
        this.editor.selectionRange = this.editor.selection.getRange();

        if (this.editor.iframe && (this.editor.options.overFlow == "visible" || this.editor.options.overFlow == "max")) this.editor.setIframeHeight();

        this.fireEvent("postCommand");

        function _escape(val) {
            return val.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
        }
    },
    closeDialog: function () {
        this.fireEvent("queryCloseDialog");
        this.dialog.node.destroy();
        this.dialog.node = null;
        if (this.button)this.button.isActive = false;
        if (this.editor.selectionRange)this.editor.selection.setRange(this.editor.selectionRange);
        this.fireEvent("postCloseDialog");
    },
    showDialog: function () {
        var self = this;
        var originalWidth = 0, originalHeight = 0;
        var sep = 0 //为了防止缓存...
        this.fireEvent("queryOpenDialog");

        var html = "";
        html += "<center>";
        html += "  <table width='100%' style='padding:2px;'>";
        html += "    <tr>";
        html += "       <td width='50' style='padding:2px;'>"
        html += "         <span style='font-size:12px;color:#888;'>图片地址</span>"
        html += "      </td>"
        html += "      <td width='210' style='padding:2px;'>"
        html += "         <input type='text' style='width:200px;border:1px solid #ccc;font-size:12px;' name='webImage' id='webImage' value='http://'>"
        html += "      </td>"
        html += "    </tr>";
        html += "    <tr>";
        html += "       <td width='50' style='padding:2px;'>"
        html += "         <span style='font-size:12px;color:#888;'>图片大小</span>"
        html += "      </td>"
        html += "      <td width='210' style='padding:2px;'>"
        html += "         <label style='font-size:12px;color:#888;display:inline-block;'>宽</label>"
        html += "         <input type='text' style='width:40px;border:1px solid #ccc;font-size:12px;display:inline-block;' name='imageWidth' id='imageWidth' value=''>"
        html += "         <label style='font-size:12px;color:#888;display:inline-block;'>高</label>"
        html += "         <input type='text' style='width:40px;border:1px solid #ccc;font-size:12px;display:inline-block;' name='imageHeight' id='imageHeight' value=''>"
        html += "         <image src='" + o2.session.path + "/widget/$SimpleEditor/default/img/refresh.png' style='cursor:pointer;' id='imageRefresh' title='重置大小'>"
        html += "      </td>"
        html += "    </tr>";
        html += "    <tr>";
        html += "       <td width='50' style='padding:2px;'>"
        html += "         <span style='font-size:12px;color:#888;'>图片说明</span>"
        html += "      </td>"
        html += "      <td width='210' style='padding:2px;'>"
        html += "         <input type='text' style='width:200px;border:1px solid #ccc;font-size:12px;' name='imageTitle' id='imageTitle' value=''>"
        html += "      </td>"
        html += "    </tr>";
        html += "    <tr>";
        html += "  </table>";
        html += "</center>";

        //var top = self.button ? self.button.node.getCoordinates().bottom : 130 ;
        //var left = self.button ? self.button.node.getCoordinates().left : 10 ;

        if (self.editor.iframe) {
            var container = self.editor.iframe.getParent();
            var top = container.getCoordinates().top + ( self.button ? self.button.node.getCoordinates().bottom : 130 );
            var left = container.getCoordinates().left + ( self.button ? self.button.node.getCoordinates().left - 15 : 10 );
        } else {
            var top = self.button ? self.button.node.getCoordinates().bottom : 130;
            var left = self.button ? self.button.node.getCoordinates().left : 10;
            var container = self.editor.doc.body
        }

        this.dialog = new o2.widget.SimpleEditor.Dialog(container, {
            "style": self.options.style,
            "html": html,
            "width": 300,
            "height": 120,
            "top": top,
            "left": left,
            "fromTop": top,
            "fromLeft": left,
            "isMax": false,
            "isClose": true,
            "isResize": false,
            "isMove": false,
            "mark": false,
            "buttons": {
                "确定": function () {
                    self.command.call(self)
                },
                "取消": function () {
                    self.closeDialog.call(self);
                }
            },
            "onPostLoad": function () {
                this.button.setStyle('display', 'block');
                //this.node.getElement(".MWF_dialod_close").addEvent("click", self.closeDialog.bind(self) );
                //this.node.getElements(".MWF_editor_emotion").addEvent("click", self.command.bind(self) );
            },
            "onQueryInjectNode": function () {
                //chrom  在iframe中，不能对已经存在的对象插入东东,所以不能在onPostLoad 做这件事
                this.node.getElement(".MWF_dialod_close").addEvent("click", self.closeDialog.bind(self));
                this.node.getElement("#imageRefresh").addEvent("click", function () {
                    refresh.call(this, true);
                });
                self.webImageNode = this.node.getElement('#webImage');
                self.imageWidthNode = this.node.getElement('#imageWidth');
                self.imageHeightNode = this.node.getElement('#imageHeight');
                self.imageTitleNode = this.node.getElement('#imageTitle');
                //self.webImageNode.addEvent('change',refresh.bind(this));
                self.webImageNode.addEvents({
                    change: function () {
                        refresh.call(this, false);
                    },
                    focus: function (e) {
                        if (this.value == 'http://')this.value = '';
                    }
                });
                self.imageWidthNode.addEvents({
                    keyup: function (e) {
                        this.value = this.value.replace(/[^0-9_]/g, '');
                        if (originalWidth > 0) {
                            if (this.value != "") {
                                self.imageHeightNode.set("value", Math.round(originalHeight / originalWidth * parseInt(this.value, 10)));
                            } else {
                                self.imageHeightNode.set("value", "")
                            }
                        }
                    }
                });
                self.imageHeightNode.addEvents({
                    keyup: function (e) {
                        this.value = this.value.replace(/[^0-9_]/g, '');
                        if (originalWidth > 0 && this.value != "") {
                            if (this.value != "") {
                                self.imageHeightNode.set("value", Math.round(originalHeight / originalWidth * parseInt(this.value, 10)));
                            } else {
                                self.imageHeightNode.set("value", "")
                            }
                        }
                    }
                });
            },
            "onPostShow": function () {
                this.node.setStyle("height", 180);
            }
        });
        this.dialog.show();
        if (this.button)this.button.isActive = true;

        function setSize(width, height, flag) {
            if (flag) {
                self.imageWidthNode.set('value', width);
                self.imageHeightNode.set('value', height);
            }
            originalWidth = width;
            originalHeight = height;
        }

        function refresh(flag) {
            var tempImg = new Element("img", {
                "id": "testaaaa",
                "src": self.webImageNode.get('value') + "&sep=" + sep++,
                "styles": {
                    position: 'absolute',
                    visibility: 'hidden',
                    top: 0,
                    left: '-1000px'
                },
                "events": {
                    "load": function () {
                        setSize(tempImg.getSize().x, tempImg.getSize().y, flag);
                        tempImg.dispose();
                    }
                }
            }).inject($(document.body));
        }

        this.fireEvent("postOpenDialog");
    }
});