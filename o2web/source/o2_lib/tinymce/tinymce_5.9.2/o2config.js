o2.TinyMCEConfig =  function ( mobile ) {
    debugger;
    if( typeof mobile !== "boolean" )mobile = layout.mobile;
    var config = {
        "branding": false,
        //skin:'oxide-dark',
        // language:'zh_CN',
        // toolbar_sticky: true,
        fontsize_formats: '12px 14px 16px 18px 24px 36px 48px 56px 72px',
        // importcss_append: true,
        autosave_ask_before_unload: false,
        //自定义文件选择器的回调内容
        image_advtab: true,
        file_picker_types: 'image', //file image media
        extended_valid_elements : 'img[class|src|border|alt|title|hspace|vspace|width|height|align|onmouseover|onmouseout|name'+
            '|style|data-id|data-orgid|data-height|data-width|onerror|data-prv|data-filename]',
        file_picker_callback: function (callback, value, meta) {
            //this 指向editor实例
            if (meta.filetype === 'image') { //'file', 'media'
                if( this.activeO2ImageDialog && this.activeO2ImageDialog.getData().base64enable ) {
                    var fileNode = new Element("input", {
                        "type" : "file",
                        "accept":"image/*",
                        "styles" : {"display":"none"}
                    });
                    fileNode.addEvent("change", function(event){
                        var file= fileNode.files[0];
                        if(!/image\/\w+/.test(file.type)){           //判断获取的是否为图片文件
                            MWF.xDesktop.notice('请选择图片格式文件',"error");
                            return false;
                        }
                        var reader=new FileReader();
                        reader.readAsDataURL(file);
                        reader.onload = function(e){
                            callback( this.result, {
                                "style": 'max-width:100%;', //width:' + width + 'px',
                                "alt": file.name || '',
                                "data-prv": 'true' //enablePreview ? 'true' : 'false'
                            })
                        }
                    }.bind( this ));
                    fileNode.click();
                }else{
                    var enablePreview = this.getParam('enablePreview', true);
                    var localImageMaxWidth = this.getParam('localImageMaxWidth', 2000);
                    var reference = this.getParam('reference');
                    var referenceType = this.getParam('referenceType');
                    if( !reference || !referenceType )return;

                    MWF.require("MWF.widget.Upload", function () {
                        var action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_file_assemble_control");
                        var upload = new MWF.widget.Upload($(document.body), {
                            "data": null,
                            "parameter": {
                                "reference": reference,
                                "referencetype": referenceType,
                                "scale": localImageMaxWidth || 2000
                            },
                            "action": action,
                            "method": "uploadImageByScale",
                            "accept": "image/*",
                            "onEvery": function (json, index, count, file) {
                                debugger;
                                var id = json.data ? json.data.id : json.id;
                                var src = MWF.xDesktop.getImageSrc( id );
                                new Element("img", {
                                    src : src,
                                    events : {
                                        load : function (ev) {
                                            var width = ev.target.naturalWidth;
                                            var height = ev.target.naturalHeight;

                                            //按最大宽度比率缩小
                                            if( localImageMaxWidth && localImageMaxWidth < width ){
                                                height = parseInt( height * (localImageMaxWidth / width) );
                                            }
                                            width = Math.min(width, localImageMaxWidth);

                                            var attributes = {
                                                "data-id": id,
                                                "data-orgid": json.data ? json.data.origId : json.origId,
                                                "height": ''+height,
                                                "width": ''+width,
                                                "data-height": ''+height,
                                                "data-width": ''+width,
                                                "data-filename": file ? ( file.name||"" ) : '',
                                                "style": 'max-width:100%; width:' + width + 'px',
                                                "onerror": 'MWF.xDesktop.setImageSrc()',
                                                "alt": file ? ( file.name||"" ) : '',
                                                "data-prv": 'true' //enablePreview ? 'true' : 'false'
                                            };
                                            callback( src, attributes )
                                        }
                                    }
                                });
                            }.bind(this)
                        });
                        upload.load();
                    }.bind(this));
                }
            }
        },
        init_instance_defaultCallback: function(editor) {
            var head_zindex = editor.getParam('head_zindex', 1);
            if(head_zindex !== 1 && editor.editorContainer && editor.editorContainer.getElementsByClassName){
                var list = editor.editorContainer.getElementsByClassName("tox-editor-header");
                if( list && list.length )list[0].setStyle("z-index", head_zindex);
            }
            editor.on("ObjectResized", function(ev){
                var element = ev.target;
                if(element.tagName && element.tagName.toUpperCase() === "IMG"){
                    var replaceStyles = function(str, object){
                        /*object 参数 {
                           "width" : "100px", //添加或替换
                           "height": "" //删除
                        }*/
                        var newArray = [];
                        Object.each(object, function (value, key) {
                            if(value)newArray.push( key + ":" + value )
                        });
                        var styles = str.split(/\s*;\s*/gi);
                        for(var j=0; j<styles.length; j++){
                            var arr = styles[j].split(/\s*:\s*/gi);
                            if( !object.hasOwnProperty( arr[0].toLowerCase() ) ){
                                newArray.push( styles[j] );
                            }
                        }
                        return newArray.join(";");
                    };

                    var width = ev.width, style = element.getAttribute("style") || "";
                    if(width && element.naturalHeight && element.naturalWidth){
                        element.setAttribute("data-width", ''+width);
                        element.setAttribute("style", replaceStyles(style, {"width" : width+"px"}));
                        element.removeAttribute("width");

                        var height = parseInt(width * ( element.naturalHeight / element.naturalWidth ));
                        element.setAttribute("data-height", ''+height);
                        element.removeAttribute("height");
                    }
                }
            });
        }
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
    if( mobile ){
        config.mobile = {
            menubar : false,
            toolbar_mode : 'wrap'
        };
        config.menubar = false;
        config.head_zindex = 0;
        config.height = 450; //编辑器高度
        config.min_height = 200;
        config.toolbar_mode = 'wrap';
        config.imagetools_toolbar = 'editimage';
        config.plugins = 'visualchars o2image wordcount'; //autolink directionality visualblocks
        config.toolbar = 'forecolor backcolor bold italic |' + //restoredraft
            ' alignleft aligncenter alignright alignjustify |' + //\\'+
            ' styleselect fontsizeselect | o2image';
    }else{
        config.head_zindex = 0;
        config.height = 650; //编辑器高度
        config.min_height = 400;
        config.toolbar_mode = 'sliding';
        config.imagetools_toolbar = 'editimage imageoptions';
        config.plugins = 'print preview searchreplace autolink directionality visualblocks visualchars fullscreen o2image link' +
        ' media template code codesample table charmap hr pagebreak nonbreaking anchor insertdatetime' +
        ' advlist lists wordcount o2imagetools textpattern help emoticons autosave' +
        ' o2indent2em o2upimgs'; //bdmap formatpainter autoresize
        config.toolbar = 'code undo redo | cut copy paste pastetext | forecolor backcolor bold italic underline strikethrough |' + //restoredraft
        ' alignleft aligncenter alignright alignjustify outdent indent o2indent2em lineheight | table o2image o2upimgs link |' + //\\'+
        ' styleselect formatselect fontselect fontsizeselect | bullist numlist | blockquote subscript superscript removeformat |' + // \\'+
        ' media charmap emoticons anchor hr pagebreak insertdatetime print preview | fullscreen'; //bdmap formatpainter
    }
    return config;
}