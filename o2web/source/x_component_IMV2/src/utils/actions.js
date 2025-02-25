import {o2} from '@o2oa/component';

/**
 * 查询公共配置
 * @param {*} name
 * @returns
 */
function getPublicData(name) {
    return new Promise((resolve) => {
        o2.UD.getPublicData(name, (dData) => resolve(dData));
    });
}
/**
 * 设置公共配置
 * @param {*} name
 * @param {*} value
 * @returns
 */
function putPublicData(name, value) {
    return new Promise((resolve) => {
        o2.UD.putPublicData(name, value, (dData) => resolve(dData));
    });
}

/**
 * 调用后端api 只返回data
 * @param {*} content
 * @param {*} action
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
async function doAction(content, action, method, ...args) {
    const m = o2.Actions.load(content)[action][method];
    const json = await m.apply(m, ...args);
    return json.data;
}
/**
 * 调用后端api 返回整个response对象
 * @param {*} content
 * @param {*} action
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
async function doActionBackResult(content, action, method, ...args) {
    const m = o2.Actions.load(content)[action][method];
    return await m.apply(m, ...args);
}

/**
 * 组织管理 人员 API
 * x_organization_assemble_control
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function personAction(method, ...args) {
    return doAction('x_organization_assemble_control', 'PersonAction', method, args);
}

/**
 * 个人 API
 * x_organization_assemble_personal
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function personalAction(method, ...args) {
    return doAction('x_organization_assemble_personal', 'PersonAction', method, args);
}

/**
 * 聊聊 API
 * @param {*} method
 * @param  {...any} args
 * @returns
 */
function imAction(method, ...args) {
    return doAction('x_message_assemble_communicate', 'ImAction', method, args);
}

/**
 * 聊聊上传 Promise
 * @param conversationId
 * @param type
 * @param formData
 * @param file 文件对象
 * @param progressCallback 进度返回函数
 * @returns {Promise<unknown>}
 */
function imUploadFile(conversationId, type, formData, file, progressCallback) {
    return new Promise((res, rej) => {
        const action = o2.Actions.load('x_message_assemble_communicate').ImAction;
        action.action.targetModule = {
            module: {
                addFormDataMessage: (file) => {
                    console.log('创建进度条', file);
                    //返回一个对象，用于处理进度条
                    return {
                        //在上传完成，失败或取消时，清除进度条
                        clearMessageProgress: () => {
                            console.log('清除进度条', file);
                        },
                        //更新进度条
                        updateProgress: (progress) => {
                            console.log('更新进度条', file, progress);
                            if (progressCallback) {
                                progressCallback(progress)
                            }
                        },
                        //更新进度条标题
                        setMessageTitle: (title) => {
                            console.log('更新进度条标题', file, title);
                        },
                        //更新进度条文本
                        setMessageText: (text) => {
                            console.log('更新进度条文本', file, text);
                        },
                        //开始上传
                        transferStart: () => {
                            console.log('开始上传', file);
                        },

                        //上传失败
                        transferFailed: () => {
                            console.log('上传失败', file);
                        },

                        //上传取消
                        transferCanceled: () => {
                            console.log('上传取消', file);
                        },

                        //上传完成
                        transferComplete: () => {
                            console.log('上传完成', file);
                        },

                        //这个data为了兼容，防止代码报错，实际上并没有用到
                        data: {id: ''},
                    };
                },
            },
            file: file,
        };

        action.uploadFile(
            conversationId,
            type,
            formData,
            file,
            (json) => {
                if (json.data) {
                    res(json.data);
                } else {
                    res(undefined);
                }
            },
            (error) => {
                console.error(error);
                rej(error);
            },
        );

        // o2.Actions.load("x_message_assemble_communicate").ImAction.uploadFile(conversationId, type, formData, file,  (json)=> {
        //   if (json.data) {
        //     res(json.data)
        //   } else {
        //     res(undefined)
        //   }
        // },  (error)=> {
        //   console.error(error)
        //   rej(error)
        // })
    });
}

/**
 * 获取头像 url
 * 默认是群聊头像
 * @param person
 * @returns {*}
 */
function getAvatarUrl(person) {
    const orgAction = o2.Actions.get('x_organization_assemble_control');
    return person ? orgAction.getPersonIcon(person) : new URL('../assets/group.png', import.meta.url).href;
}

/**
 * 会话头像 url
 * @param conversationId
 * @returns {string}
 */
function conversationIconUrl(conversationId) {
    const action = o2.Actions.get('x_message_assemble_communicate').action;
    let url = action.getAddress() + '/jaxrs/im/conversation/{id}/icon';
    url = url.replace('{id}', encodeURIComponent(conversationId));
    url += '?' + new Date().getTime();
    return url;
}

//图片 根据大小 url
const getImFileUrlWithWH = (id, width, height) => {
    const action = o2.Actions.get('x_message_assemble_communicate').action;
    let url = action.getAddress() + action.actions.imgFileDownloadWithWH.uri;
    url = url.replace('{id}', encodeURIComponent(id));
    url = url.replace('{width}', encodeURIComponent(width));
    url = url.replace('{height}', encodeURIComponent(height));
    return url;
};
//file 下载的url
const getImFileDownloadUrl = (id) => {
    var action = o2.Actions.get('x_message_assemble_communicate').action;
    var url = action.getAddress() + action.actions.imgFileDownload.uri;
    url = url.replace('{id}', encodeURIComponent(id));
    return url;
};

//百度地图打开地址
const getBaiduMapUrl = (lat, longt, address, content) => {
    return (
        'https://api.map.baidu.com/marker?location=' +
        lat +
        ',' +
        longt +
        '&title=' +
        address +
        '&content=' +
        content +
        '&output=html&src=net.o2oa.map'
    );
};

export {
    doAction,
    doActionBackResult,
    getPublicData,
    putPublicData,
    personAction,
    personalAction,
    imAction,
    imUploadFile,
    getAvatarUrl,
    getImFileUrlWithWH,
    getImFileDownloadUrl,
    getBaiduMapUrl,
    conversationIconUrl,
};
