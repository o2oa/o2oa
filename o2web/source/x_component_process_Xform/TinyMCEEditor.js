MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class TinyMCEEditor HTML编辑器。
 * @example
 * //可以在脚本中获取该组件
 * //方法1：
 * var tinyMCEEditor = this.form.get("name"); //获取组件
 * //方法2
 * var tinyMCEEditor = this.target; //在组件事件脚本中获取
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.TinyMCEEditor = MWF.APPTinyMCEEditor = new Class(
    /** @lends MWF.xApplication.process.Xform.TinyMCEEditor# */
    {
        Extends: MWF.APP$Module,
        options: {
            /**
             * 组件异步加载后触发.
             * @event MWF.xApplication.process.Xform.TinyMCEEditor#afterLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|组件事件说明}
             */
            "moduleEvents": ["queryLoad", "load", "postLoad", "afterLoad"]
        },
        initialize: function (node, json, form, options) {
            this.node = $(node);
            this.node.store("module", this);
            this.json = json;
            this.form = form;
            this.field = true;
            this.fieldModuleLoaded = false;
        },
        load: function () {
            this._loadModuleEvents();
            if (this.fireEvent("queryLoad")) {
                this._queryLoaded();
                this._loadUserInterface();
                this._loadStyles();
                //this._loadEvents();

                this._afterLoaded();

                this.fireEvent("postLoad");
                this.fireEvent("load");
            }
        },

        _loadUserInterface: function () {
            debugger;
            this.node.empty();
            if (this.readonly || this.json.isReadonly) {
                // this.node.set("html", this._getBusinessData());
                this.node.setStyles({
                    "-webkit-user-select": "text",
                    "-moz-user-select": "text"
                });
                if (layout.mobile) {
                    this.loadLazyImage(function () { //图片懒加载
                        var images = this.node.getElements("img");
                        //移动端设置图片宽度为100%
                        images.each(function (img) {
                            if (img.hasClass("lozad")) {
                                img.setStyles({
                                    "max-width": "100%"
                                });
                            } else {
                                img.setStyles({
                                    "height": "auto",
                                    "max-width": "100%"
                                });
                            }
                        }.bind(this));
                        this.fireEvent("afterLoad");
                        this.fieldModuleLoaded = true;
                    }.bind(this))
                } else {
                    this.loadLazyImage(function () { //图片懒加载
                        if (this.json.enablePreview !== "n") {
                            this.loadImageViewer(); //PC端点击显示大图
                            this.fireEvent("afterLoad");
                            this.fieldModuleLoaded = true;
                        }
                    }.bind(this))
                }
            } else {
                var config = Object.clone(this.json.editorProperties);
                if (this.json.config) {
                    if (this.json.config.code) {
                        var obj = this.form.Macro.exec(this.json.config.code, this);
                        Object.each(obj, function (v, k) {
                            config[k] = v;
                        });
                    }
                }
                this.loadTinyMCEEditor(config);
            }
            //    this._loadValue();
        },
        loadLazyImage: function (callback) {
            o2.require("o2.widget.ImageLazyLoader", function () {
                var loadder = new o2.widget.ImageLazyLoader(this.node, this._getBusinessData());
                loadder.load(function () {
                    if (callback) callback();
                }.bind(this))
            }.bind(this));
        },
        loadImageViewer: function () {
            o2.require("o2.widget.ImageViewer", function () {
                var imageViewer = new o2.widget.ImageViewer(this.node);
                imageViewer.load();
            }.bind(this));
        },
        getDefaultConfig: function () {
            debugger;
            var config = {
                "branding": false,
                //skin:'oxide-dark',
                // language:'zh_CN',
                plugins: 'print preview searchreplace autolink directionality visualblocks visualchars fullscreen image link' +
                    ' media template code codesample table charmap hr pagebreak nonbreaking anchor insertdatetime' +
                    ' advlist lists wordcount o2imagetools textpattern help emoticons autosave' +
                    ' o2indent2em o2upimgs', //bdmap formatpainter autoresize
                toolbar: 'code undo redo | cut copy paste pastetext | forecolor backcolor bold italic underline strikethrough |' + //restoredraft
                    ' alignleft aligncenter alignright alignjustify outdent indent o2indent2em lineheight | table image o2upimgs link |' + //\\'+
                    ' styleselect formatselect fontselect fontsizeselect | bullist numlist | blockquote subscript superscript removeformat |' + // \\'+
                    ' media charmap emoticons anchor hr pagebreak insertdatetime print preview | fullscreen', //bdmap formatpainter
                height: 650, //编辑器高度
                min_height: 400,
                toolbar_mode: 'sliding',
                // toolbar_sticky: true,
                fontsize_formats: '12px 14px 16px 18px 24px 36px 48px 56px 72px',
                // importcss_append: true,
                //自定义文件选择器的回调内容
                file_picker_callback: function (callback, value, meta) {
                    //this 指向editor实例

                    debugger;
                	// if (meta.filetype === 'file') {
                	// 	callback('https://www.baidu.com/img/bd_logo1.png', { text: 'My text' });
                	// }
                	if (meta.filetype === 'image') {


                        var enablePreview = this.getParam('enablePreview', true);
                        var localImageMaxWidth = this.getParam('localImageMaxWidth', 2000);
                        var reference = this.getParam('reference');
                        var referenceType = this.getParam('referenceType');
                        if( !reference || !referenceType )return;

                        MWF.require("MWF.widget.Upload", function(){
                            var action =  new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_file_assemble_control");

                            var upload = new MWF.widget.Upload($(document.body), {
                                "data": null,
                                "parameter": {"reference" : reference, "referencetype": referenceType, "scale" : localImageMaxWidth || 2000 },
                                "action": action,
                                "method": "uploadImageByScale",
                                "accept": "image/*",
                                "onEvery": function(json, index, count, file){

                                    var attrs = {};
                                    if(file.name)attrs.alt = file.name;

                                    var id = json.data ? json.data.id : json.id;
                                    attrs["data-id"] = id;

                                    attrs["data-orgid"] = json.data ? json.data.origId : json.origId;

                                    var src = MWF.xDesktop.getImageSrc( id );
                                    callback( src, attrs )

                                }.bind(this)
                            });
                            upload.load();
                        }.bind(this));
                	}
                	// if (meta.filetype === 'media') {
                	// 	callback('movie.mp4', { source2: 'alt.ogg', poster: 'https://www.baidu.com/img/bd_logo1.png' });
                	// }
                },
                autosave_ask_before_unload: false,
                imagetools_toolbar: 'editimage imageoptions'
            };
            if (o2.language === "zh-cn") {
                config.language = 'zh_CN';
                config.font_formats = '微软雅黑=Microsoft YaHei,Helvetica Neue,PingFang SC,sans-serif;' +
                    '苹果苹方=PingFang SC,Microsoft YaHei,sans-serif;' +
                    '宋体=simsun,serif;' +
                    '仿宋体=FangSong,serif;' +
                    '黑体=SimHei,sans-serif;' +
                    'Arial=arial,helvetica,sans-serif;' +
                    'Arial Black=arial black,avant garde;' +
                    'Book Antiqua=book antiqua,palatino;';
            }
            return config;
        },
        getImageUploadOption: function(){
            return {
                localImageMaxWidth : 2000,
                reference: this.form.businessData.work.job,
                referenceType: "processPlatformJob"
            };
        },
        loadTinyMCEEditor: function (config) {
            o2.load("../o2_lib/tinymce/tinymce_5.9.2/tinymce.min.js", function(){
                var editorConfig = Object.merge(this.getDefaultConfig(), config || {});

                var id = this.form.json.id +"_"+this.json.id + "_div";
                editorConfig.selector = '#'+id;
                var editorDiv = new Element("div", {"id": id}).inject(this.node);

                var htmlData = this._getBusinessData();
                if (htmlData) {
                    editorDiv.set("html", htmlData);
                } else if (this.json.templateCode) {
                    editorDiv.set("html", this.json.templateCode);
                }
                // var height = this.node.getSize().y;


                if (this.form.json.mode === "Mobile") {
                    if (!editorConfig.toolbar && !editorConfig.toolbarGroups) {
                        editorConfig.toolbar = [
                            {
                                name: 'paragraph',
                                items: ['Bold', 'Italic', "-", 'TextColor', "BGColor", 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', "-", 'Undo', 'Redo']
                            },
                            {name: 'basicstyles', items: ['Styles', 'FontSize']},
                            {name: 'insert', items: ['Image']}
                        ];
                    }
                }

                editorConfig.base64Encode = (this.json.base64Encode === "y");
                editorConfig.enablePreview = (this.json.enablePreview !== "n");
                var options = this.getImageUploadOption();
                for(var key in options){
                    editorConfig[key] = options[key];
                }

                var init_instance_callback;
                if(editorConfig.init_instance_callback){
                    init_instance_callback = editorConfig.init_instance_callback;
                }

                editorConfig.init_instance_callback = function(editor) {
                    this.editor = editor;
                    this.editor.on("change", function () {
                        this._setBusinessData(this.getData());
                    }.bind(this));
                    if(init_instance_callback)init_instance_callback(editor);
                }.bind(this);

                // editorConfig.images_upload_handler = function (blobInfo, succuss, failure) {
                //     this.images_upload_handler(blobInfo, succuss, failure);
                // }.bind(this);

                tinymce.init(editorConfig);

                this._loadEvents();

                this.fireEvent("afterLoad");
                this.fieldModuleLoaded = true;

                //    this._loadEvents();
            }.bind(this));
        },
        _loadEvents: function (editorConfig) {
            Object.each(this.json.events, function (e, key) {
                if (e.code) {
                    this.editor.on(key, function (event) {
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this), this);
                }
            }.bind(this));

        },
        addModuleEvent: function (key, fun) {
            this.editor.on(key, function (event) {
                return (fun) ? fun(this, event) : null;
            }.bind(this), this);
        },
        _loadValue: function () {
            var data = this._getBusinessData();
        },
        /**
         * @summary 重置组件的值为默认值或置空。
         *  @example
         * this.form.get('fieldId').resetData();
         */
        resetData: function () {
            this.setData(this._getBusinessData());
        },
        /**
         * @summary 判断组件值是否为空.
         * @example
         * if( this.form.get('fieldId').isEmpty() ){
         *     this.form.notice('HTML编辑器不能为空', 'warn');
         * }
         * @return {Boolean} 值是否为空.
         */
        isEmpty: function () {
            return !this.getData().trim();
        },
        /**
         * 当表单上没有对应组件的时候，可以使用this.data[fieldId]获取值，但是this.form.get('fieldId')无法获取到组件。
         * @summary 获取组件值。
         * @example
         * var data = this.form.get('fieldId').getData();
         * @example
         *  //如果无法确定表单上是否有组件，需要判断
         *  var data;
         *  if( this.form.get('fieldId') ){ //判断表单是否有无对应组件
         *      data = this.form.get('fieldId').getData();
         *  }else{
         *      data = this.data['fieldId']; //直接从数据中获取字段值
         *  }
         * @return 组件的数据.
         */
        getData: function () {
            return this.editor ? this.editor.getContent() : this._getBusinessData();
        },
        /**
         * 当表单上没有对应组件的时候，可以使用this.data[fieldId] = data赋值。
         * @summary 为组件赋值。
         * @param data{String} .
         * @example
         *  this.form.get("fieldId").setData("test"); //赋文本值
         * @example
         *  //如果无法确定表单上是否有组件，需要判断
         *  if( this.form.get('fieldId') ){ //判断表单是否有无对应组件
         *      this.form.get('fieldId').setData( data );
         *  }else{
         *      this.data['fieldId'] = data;
         *  }
         */
        setData: function (data) {
            this._setBusinessData(data);
            if (this.editor) this.editor.setContent(data);
        },
        createErrorNode: function (text) {
            var node = new Element("div");
            var iconNode = new Element("div", {
                "styles": {
                    "width": "20px",
                    "height": "20px",
                    "float": "left",
                    "background": "url(" + "../x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
                }
            }).inject(node);
            var textNode = new Element("div", {
                "styles": {
                    "line-height": "20px",
                    "margin-left": "20px",
                    "color": "red",
                    "word-break": "keep-all"
                },
                "text": text
            }).inject(node);
            return node;
        },
        notValidationMode: function (text) {
            if (!this.isNotValidationMode) {
                this.isNotValidationMode = true;
                this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
                this.node.setStyle("border", "1px solid red");

                this.errNode = this.createErrorNode(text).inject(this.node, "after");
                this.showNotValidationMode(this.node);
                if (!this.node.isIntoView()) this.node.scrollIntoView();
            }
        },
        showNotValidationMode: function (node) {
            var p = node.getParent("div");
            if (p) {
                if (p.get("MWFtype") == "tab$Content") {
                    if (p.getParent("div").getStyle("display") == "none") {
                        var contentAreaNode = p.getParent("div").getParent("div");
                        var tabAreaNode = contentAreaNode.getPrevious("div");
                        var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
                        var tabNode = tabAreaNode.getLast().getFirst().getChildren()[idx];
                        tabNode.click();
                        p = tabAreaNode.getParent("div");
                    }
                }
                this.showNotValidationMode(p);
            }
        },
        validationMode: function () {
            if (this.isNotValidationMode) {
                this.isNotValidationMode = false;
                this.node.setStyles(this.node.retrieve("borderStyle"));
                if (this.errNode) {
                    this.errNode.destroy();
                    this.errNode = null;
                }
            }
        },

        validationConfigItem: function (routeName, data) {
            var flag = (data.status == "all") ? true : (routeName == data.decision);
            if (flag) {
                var n = this.getData();
                var v = (data.valueType == "value") ? n : n.length;
                switch (data.operateor) {
                    case "isnull":
                        if (!v) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "notnull":
                        if (v) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "gt":
                        if (v > data.value) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "lt":
                        if (v < data.value) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "equal":
                        if (v == data.value) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "neq":
                        if (v != data.value) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "contain":
                        if (v.indexOf(data.value) != -1) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "notcontain":
                        if (v.indexOf(data.value) == -1) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                }
            }
            return true;
        },
        validationConfig: function (routeName, opinion) {
            if (this.json.validationConfig) {
                if (this.json.validationConfig.length) {
                    for (var i = 0; i < this.json.validationConfig.length; i++) {
                        var data = this.json.validationConfig[i];
                        if (!this.validationConfigItem(routeName, data)) return false;
                    }
                }
                return true;
            }
            return true;
        },
        /**
         * @summary 根据组件的校验设置进行校验。
         *  @param {String} [routeName] - 可选，路由名称.
         *  @example
         *  if( !this.form.get('fieldId').validation() ){
         *      return false;
         *  }
         *  @return {Boolean} 是否通过校验
         */
        validation: function (routeName, opinion) {
            if (!this.validationConfig(routeName, opinion)) return false;

            if (!this.json.validation) return true;
            if (!this.json.validation.code) return true;

            this.currentRouteName = routeName;
            var flag = this.form.Macro.exec(this.json.validation.code, this);
            this.currentRouteName = "";

            if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
            if (flag.toString() != "true") {
                this.notValidationMode(flag);
                return false;
            }
            return true;
        }
    });