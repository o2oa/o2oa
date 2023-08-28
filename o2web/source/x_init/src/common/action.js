import {notice} from './notice.js';

//password
const password_check = '/jaxrs/secret/check';
const password_set = '/jaxrs/secret/set';       //post  {"secret":"o2oa@2022"}

const password_cancel = '/jaxrs/secret/cancel';

//server
const server_execute = '/jaxrs/server/execute';         //get 执行服务器任务,执行完成后将停止init服务器,随后正常启动.
const server_stop = '/jaxrs/server/stop';               //get 停止init服务器
const server_status = '/jaxrs/server/execute/status';   //get 取初始服务器任务执行状态
const server_echo = '/x_desktop/res/config/config.json';  //get 检查O2OA服务器是否启动成功


//database
// const database_check = '/jaxrs/externaldatasources/check';          //get 检查是否可以设置外部数据源
const database_check = '/jaxrs/externaldatasources/check'   //get 获取已经配置的数据库信息
const database_h2_check = '/jaxrs/h2/check'   //get 获取是否已存在h2数据库文件
const database_list = '/jaxrs/externaldatasources/list';            //get 列示可用的外部数据源配置样例
const database_set = '/jaxrs/externaldatasources/set';              //post 设置外部数据源
/**
{
    "externalDataSources": [{
        "enable": true,
        "url": "jdbc:sqlserver://127.0.0.1:1433;DatabaseName\u003dX;selectMethod\u003dcursor;sendStringParametersAsUnicode\u003dfalse",
        "username": "sa",
        "password":
        "password",
        "includes": [],
        "excludes": [],
        "logLevel": "ERROR",
        "autoCommit": false,
        "schema": "X"
    }]
}
*/


const database_cancel = '/jaxrs/externaldatasources/set/cancel';    //get 取消设置外部数据源
const database_test = '/jaxrs/externaldatasources/validate';        //post 测试数据库链接
/** {
    "externalDataSources": [{
        "enable": true,
        "url": "jdbc:sqlserver://127.0.0.1:1433;DatabaseName\u003dX;selectMethod\u003dcursor;sendStringParametersAsUnicode\u003dfalse",
        "username": "sa",
        "password": "password",
        "includes": [],
        "excludes": [],
        "logLevel": "ERROR",
        "autoCommit": false,
        "schema": "X"
    }]
}
* */


const h2_check = '/jaxrs/h2/check';             //get 检查h2服务服务器是否需要升级
const h2_upgrade = '/jaxrs/h2/upgrade';         //get 确认需要进行升级
const h2_cancel = '/jaxrs/h2/upgrade/cancel';   //get 取消升级

//restore
const restore_upload = '/jaxrs/restore/upload'; //post 上传zip格式数据包,字段名file
const restore_cancel = '/jaxrs/restore/upload/cancel'; //get 上传zip格式数据包,字段名file

async function get(url, noerror) {
    try {
        const res = await fetch(url, {cache: 'no-cache'});
        if (res.ok) {
            const json = await res.json();
            return json.data;
        } else {
            const o = await res.json();
            const e = new Error(o.message);
            if (!noerror) notice.error(`Get ${url} Status Code: ${res.status} (${res.statusText})`, e.message, {err: e});
            throw e;
        }
    } catch (e) {
        if (!noerror) notice.error(`Get ${url} Failed`, e.message, {err: e});
        throw e;
    }
}
async function post(url, body, contentType) {
    try {
        const req = {
            method: 'POST',
            body
        }
        if (contentType){
            req.headers = {
                'Content-Type': contentType || 'application/json'
            }
        }
        const res = await fetch(url, req);
        if (res.ok) {
            const json = await res.json();
            return json.data;
        } else {
            const o = await res.json();
            const e = new Error(o.message);
            notice.error(`Post ${url} Status Code: ${res.status} (${res.statusText})`, e.message, {err: e});
            throw e;
        }
    } catch (e) {
        notice.error(`Post ${url} Failed`, e.message, {err: e});
        throw e;
    }
}
async function checkPassword() {
    return (await get(password_check));
}

async function setPassword(password) {
    const o={
        "secret": password
    }
    return await post(password_set, JSON.stringify(o), 'application/json');
}

async function cancelPassword() {
    return (await get(password_cancel));
}

async function getServerStatus() {
    return (await get(server_status));
}


async function listDatabase() {
    return (await get(database_list));
}
async function testDatabase(o) {
    const db={
        "externalDataSources": [o]
    }
    return await post(database_test, JSON.stringify(db), 'application/json');
}
async function setDatabase(o) {
    const db={
        "externalDataSources": [o]
    }
    return await post(database_set, JSON.stringify(db), 'application/json');
}
async function checkDatabase() {
    const [edb, h2db] = await Promise.all([get(database_check), get(database_h2_check)]);
    return (edb.configured || h2db.configured);
}


async function h2Check() {
    return (await get(h2_check));
}
async function h2Upgrade() {
    return (await get(h2_upgrade));
}
async function h2Cancel() {
    return (await get(h2_cancel));
}

async function uploadRestore(o) {
    return await post(restore_upload, o);
}
async function cancelRestore() {
    return (await get(restore_cancel));
}

async function stopServer() {
    return (await get(server_stop));
}
async function executeServer() {
    return (await get(server_execute));
}
async function serverStatus() {
    try{
        return (await get(server_status, true));
    }catch(e){
        return {
            status: 'starting',
        }
    }
}
async function echoServer() {
    try{
        await get(server_echo, true);
        return {
            status: 'started',
        }
    }catch(e){
        return {
            status: 'starting',
        }
    }
}


// const server_execute = '/jaxrs/server/execute';         //get 执行服务器任务,执行完成后将停止init服务器,随后正常启动.
// const server_stop = '/jaxrs/server/stop';               //get 停止init服务器

export {
    checkPassword,
    setPassword,
    cancelPassword,
    getServerStatus,
    listDatabase,
    testDatabase,
    setDatabase,
    checkDatabase,
    h2Check,
    h2Upgrade,
    h2Cancel,
    uploadRestore,
    cancelRestore,
    stopServer,
    executeServer,
    serverStatus,
    echoServer
};
