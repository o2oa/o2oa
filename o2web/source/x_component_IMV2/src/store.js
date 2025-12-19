import {defineStore} from 'pinia';
import {imAction, imUploadFile} from "./utils/actions.js";
import {canUseWebP, ymdhms} from "./utils/common.js";
import {lp} from "@o2oa/component";
import {uuid} from "@o2oa/util";

export const windowState = defineStore('windowState', {
    state: () => ({
        windowWidth: window.innerWidth, // 窗口宽度
        isMobile: false
    }),
    actions: {
        updateWindowWidth() {
            this.windowWidth = window.innerWidth;
            this.isMobile = this.windowWidth < 768
            console.debug('=====> update ' + this.isMobile + ' windowWidth' + this.windowWidth);
        }
    }
})

export const useLoadingStore = defineStore('loading', {
    state: () => ({
        isLoading: false,
        loadingText: '加载中...'
    }),
    actions: {
        showLoading(text) {
            this.isLoading = true;
            if (text) {
                this.loadingText = text;
            }
        },
        hideLoading() {
            this.isLoading = false;
        }
    }
});

export const firstOpen = defineStore('firstOpen', {
    state: () => ({})
})

export const imConfig = defineStore('imConfig', {
    state: () => ({
        enableClearMsg: false,
        enableRevokeMsg: false,
        enableOnlyOfficePreview: false,
        enableGroupMemberQuitSelf: false,
        revokeOutMinute: 2,
        conversationCheckInvoke: '',
    }),
    actions: {
        setImConfig(config) {
            this.enableClearMsg = config.enableClearMsg ?? false;
            this.enableRevokeMsg = config.enableRevokeMsg ?? false;
            this.enableOnlyOfficePreview= config.enableOnlyOfficePreview ?? false;
            this.enableGroupMemberQuitSelf= config.enableGroupMemberQuitSelf ?? false;
            this.revokeOutMinute= config.revokeOutMinute ?? 2;
            this.conversationCheckInvoke= config.conversationCheckInvoke ?? '';
        }
    }
})

export const imGlobalOptions = defineStore('imGlobalOptions', {
    state: () => ({
        hideSide: false,
        firstOpenConversation: true,
    }),
    actions: {
        setOptions(options)  {
            this.hideSide = options.hideSide ?? false
        },
        loadedConversation() {
            this.firstOpenConversation = false
        }
    }
})

export const uploadFileList = defineStore('uploadFileList', {
    state: () => ({
        uploadFileList: [],
    }),
    actions: {
        async addUploadFileAndSendMessage (file, conversationId)  {
            if (!file || !conversationId) {
                console.error('没有传入 file 或者 conversationId')
                return
            }
            const formData = new FormData();
            formData.append('file', file);
            formData.append('fileName', file.name);
            const fileExt = file.name.substring(file.name.lastIndexOf('.'));
            // 图片消息
            let type;
            if (fileExt.toLowerCase() === '.webp' && canUseWebP()) {
                type = 'image';
            } else if (
                fileExt.toLowerCase() === '.bmp' ||
                fileExt.toLowerCase() === '.jpeg' ||
                fileExt.toLowerCase() === '.png' ||
                fileExt.toLowerCase() === '.jpg'
            ) {
                type = 'image';
            } else {
                // 文件消息
                type = 'file';
            }
            const fileId = `${uuid()}`
            this.uploadFileList.push({
                id: fileId,
                name: file.name,
                progress: 0
            })
            // this.mockProgress(fileId)

            //上传文件
            const res = await imUploadFile(conversationId, type, formData, file, (progress)=> {
                // 更新进度条
                const data = this.uploadFileList.find((m) => m.id === fileId);
                if (data) {
                    data.progress = progress
                }
            });
            // 删除进度条
            const i = this.uploadFileList.findIndex((m) => m.id === fileId);
            if (i > -1) {
                this.uploadFileList.splice(i, 1);
            }
            console.debug(res);
            if (res) {
                const body = {
                    body: type === 'image' ? lp.msgTypeImage : lp.file,
                    type: type,
                    fileId: res.id,
                    fileExtension: res.fileExtension,
                    fileName: res.fileName,
                };
                const message = {
                    id: `${uuid()}`,
                    conversationId: conversationId,
                    body: JSON.stringify(body),
                    createPerson: layout.session.user.distinguishedName,
                    createTime: ymdhms(new Date()),
                    sendStatus: 1,
                };
                const result = await imAction('msgCreate', message);
                if (result) {
                    console.log('发送消息成功', result);
                }
            }
        },
        mockProgress(fileId) {
            let mockProgress = 0
            let time = setInterval(()=> {

                if (mockProgress >= 100 ) {
                    const i = this.uploadFileList.findIndex((m) => m.id === fileId);
                    if (i > -1) {
                        this.uploadFileList.splice(i, 1);
                    }
                    clearInterval(time)
                    return
                }
                mockProgress += 10;
                const data = this.uploadFileList.find((m) => m.id === fileId);
                if (data) {
                    data.progress = mockProgress
                }
            }, 300)
        }
    }
})