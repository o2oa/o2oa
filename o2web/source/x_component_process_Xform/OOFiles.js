MWF.xDesktop.requireApp('process.Xform', 'OOInput', null, false);
MWF.xApplication.process.Xform.OOFiles = MWF.APPOOFiles = new Class({
    Implements: [Events],
    Extends: MWF.APPOOInput,
    iconStyle: 'textFieldIcon',
    options: {
        moduleEvents: ['load', 'queryLoad', 'postLoad'],
    },
    _loadNodeOtherAttr: function () {
        if (this.json.attachmentExtType && this.json.attachmentExtType.length) {
            accept = this._getAccept(this.json.attachmentExtType);
            this.node.setAttribute('accept', accept);
        }

        if (this.json.selectFileTitle) {
            this.node.setAttribute('select-file', this.json.selectFileTitle);
        } 
        if (this.json.uploadButtonStyle) {
            this.node.setAttribute('file-button-style', this.json.uploadButtonStyle);
        } 
        if (this.json.uploadButtonIcon) {
            this.node.setAttribute('file-button-icon', this.json.uploadButtonIcon);
        } 

        this.node.setAttribute('multiple', 'true');

        const max = this.json.attachmentCount?.toInt() || 0;
        if (max) this.node.setAttribute('max', max);

        const min = this.json.attachmentMinCount?.toInt() || 0;
        if (max) this.node.setAttribute('min', min);

        const size = this.json.attachmentSize?.toInt() || 0;
        if (max) this.node.setAttribute('size', size);

        //为oo-files对象提供 upload 和 remove 方法。
        Object.defineProperties(this.node, {
            uploadFile: {
                value: (file, fileNode) => {
                    this._appendPrograsseMethods(fileNode);
                    return this._uploadFile(fileNode);
                },
            },
            removeFile: {
                value: (file, fileNode) => {
                    return this._deleteFile(file, fileNode);
                },
            },
        });

        this.node.addEventListener('upload', () => {
            debugger;
            this._saveDoc();
        });
        this.node.addEventListener('removefile', () => {
            this._saveDoc();
        });

        this.env = this._fileInProcess() ? 'process' : this._fileInCms() ? 'cms' : '';
        this.restfulActions =
            this.env === 'process' ? this.form.workAction.action : this.env === 'cms' ? this.form.documentAction.action : null;

        this.node.setStyle('pointer-events', 'unset');
    },

    _saveDoc(){
        if (this.env === 'process') {
            this.form.saveFormData();
        }
        if (this.env === 'cms') {
            var modifedData = {};
            modifedData[this.json.id] = this.getData();
            modifedData.id = this.form.businessData.document.id;
            
            this.form.documentAction.saveData(null, function(){return true}, this.form.businessData.document.id, modifedData, false);
        }
    },
    _deleteFile: function (file, fileNode) {
        return new Promise((resolve, reject) => {
            try {
                this.restfulActions.invoke({
                    name: 'deleteAttachment',
                    async: true,
                    parameter: {id: file.id, workid: this.form.businessData.work?.id},
                    success: (json) => {
                        resolve({json, file});
                    },
                    failure: (xhr) => {
                        var json = JSON.decode(xhr.responseText);
                        if (json && json.prompt === 'com.x.base.core.project.exception.ExceptionEntityNotExist') {
                            resolve({json, file});
                        } else {
                            this.fireEvent('removefileerror', [{file, fileNode}]);
                            const e = json && json.message ? new Error(json.message) : xhr;
                            reject(e, file);
                        }
                    },
                });
            } catch (e) {
                throw e;
            }
        });
    },

    _uploadFile: function (node) {
        node.setAttribute('status', 'uploading');
        const file = node.file;
        return this._uploadFileToServer(file, node)
            .then(({json}) => {
                if (json) {
                    const id = json.data.id;
                    const addr = this.restfulActions.getAddress();
                    const previewUrl =
                        this.env === 'process'
                            ? `${addr}/jaxrs/attachment/download/${id}`
                            : `${addr}/jaxrs/fileinfo/download/document/${id}`;
                    const url = `${previewUrl}/stream`;
                    node.file.id = id;
                    node.file.url = url;
                    node.file.previewUrl = previewUrl;
                    node.setAttribute('url', url);
                    node.setAttribute('preview-url', previewUrl);
                    node.setAttribute('status', 'uploaded');
                }
                this.fireEvent('addfile', [{file, node}]);
                return json;
            })
            .catch((xhr) => {
                debugger;
                var json = JSON.decode(xhr.responseText);
                this.fireEvent('addfileerror', [{file, node}]);
                throw json && json.message ? new Error(json.message) : xhr;
            });
    },

    _noticeError: function (msg) {
        $OOUI.notice.failed('Upload Error', msg, {
            container: this.node._elements.box,
            duration: 3000,
        });
    },

    _uploadFileToServer: function (file, node) {
        //先判断是流程还是内容管理
        if (this._fileInProcess()) {
            //流程
            return this._uploadFileToProcess(file, node);
        }
        if (this._fileInCms()) {
            //内容管理
            return this._uploadFileToCms(file, node);
        }
        //都不是，可能是门户
        return Promise.resolve();
    },
    _uploadFileToProcess: function (file, node) {
        return this._uploadFileTo(file, node, this.form.businessData.work.id);
    },
    _uploadFileToCms: function (file, node) {
        return this._uploadFileTo(file, node, this.form.businessData.document.id);
    },

    _uploadFileTo(file, node, id) {
        var formData = new FormData();
        formData.append('site', this.json.id);
        formData.append('file', file);

        return new Promise((resolve, reject) => {
            try {
                this.restfulActions.targetModule = {module: node, file: file};
                this.restfulActions.invoke({
                    name: 'uploadAttachment',
                    async: true,
                    data: formData,
                    file: file,
                    parameter: {id},
                    success: (json) => {
                        resolve({json, file});
                    },
                    failure: (xhr) => {
                        reject(xhr, file);
                    },
                });
            } catch (e) {
                reject(e);
            }
        });
    },

    _fileInProcess: function () {
        return (
            (this.form.businessData.work && Object.keys(this.form.businessData.work).length) ||
            (this.form.businessData.workCompleted && Object.keys(this.form.businessData.workCompleted).length)
        );
    },
    _fileInCms: function () {
        return this.form.businessData.document && Object.keys(this.form.businessData.document).length;
    },
    _getAccept: function (types) {
        var files = {
            word: '.doc,.docx',
            excel: '.xls,.xlsx',
            ppt: '.ppt,.pptx',
            txt: '.txt',
            pic: '.jpg,.jpeg,.png,.gif,.bmp,image/*',
            pdf: '.pdf',
            zip: '.zip,.rar,.7z',
            audio: 'audio/*',
            video: 'video/*',
            other: this.json.attachmentExtOtherType,
        };

        return types
            .map((t) => {
                return files[t];
            })
            .join(',');
    },

    _appendPrograsseMethods(node) {
        node.addFormDataMessage = (file) => {
            return {
                data: {},
                updateProgress: (percent) => {
                    debugger;
                    node.setAttribute('progress', percent);
                },
            };
        };
    },

    getInputData: function () {
        return this.node.value;
    },

    checkValue: function(value, att){
        if (!value || !value.length){
            return true;
        }
        const i = value.findIndex((v)=>{
            v.id === att.id
        });
        return i===-1;
    },
    getValue: function () {
        debugger;
        if (!this.isReadable) return '';
        if (this.moduleValueAG) return this.moduleValueAG;
        var value = this._getBusinessData();
        if (o2.typeOf(value) === 'null' || value === '') value = this._computeValue();

        //如果有设置 site，循环所有附件，将匹配site的附件添加进来。
        debugger;
        if (this.json.fileSite){
            const siteList = this.json.fileSite.split(/.*,.*/g);
            const addr = this.restfulActions.getAddress();
            this.form.businessData.attachmentList.each(function (att) {
                if (siteList.includes(att.site) && att.control.allowRead){
                    if (this.checkValue(value, att)){
                        const previewUrl =
                            this.env === 'process'
                                ? `${addr}/jaxrs/attachment/download/${att.id}`
                                : `${addr}/jaxrs/fileinfo/download/document/${att.id}`;
                        const url = `${previewUrl}/stream`;

                        const file = {
                            id: att.id,
                            url: url,
                            previewUrl: previewUrl,
                            lastModified: att.lastUpdateTime,
                            lastModifiedDate: att.lastUpdateTime,
                            name: att.name,
                            size: att.length,
                            type: att.extension,
                        }

                        if (!value || !value.length) value = [];
                        value.push(file);
                    }
                }
            }.bind(this));
        }

        return value ?? '';
    },
});
