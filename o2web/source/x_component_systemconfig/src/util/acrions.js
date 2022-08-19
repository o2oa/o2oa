import {o2} from '@o2oa/component';
const configs = {};

function exec(text, bind, arg){
    const b = bind || this;
    const p = (arg) ? Object.values(arg) : [];
    const k = (arg) ? Object.keys(arg).join(',') : '';
    try {
        return Function('return function('+k+'){' + text + '}')().apply(b, p);
    }catch(e){
        console.error(e);
        return '';
    }
}

async function loadConfig(name) {
    const body = {
        fileName: name + '.json'
    }
    const json = await o2.Actions.load('x_program_center').ConfigAction.open(body);
    if (json.data.fileContent) {
        configs[name] = JSON.parse(json.data.fileContent);
        return configs[name];
    }
    return null;
}
async function loadRuntimeConfig(name) {
    const body = {
        fileName: name + '.json'
    }
    const json = await o2.Actions.load('x_program_center').ConfigAction.openRuntimeConfig(body);
    return (json.data.fileContent) ? JSON.parse(json.data.fileContent) : null;
}

async function getConfig(name, path) {
    const data = configs[name] || (await loadConfig(name));
    return data ? data[path] : null;
}
async function getConfigData(name, reload) {
    if (configs[name] && !reload) return configs[name];
    return await loadConfig(name);
}
async function saveConfig(name, path, value) {
    const config = (configs[name]) ? configs[name] : (await loadConfig(name));
    let configData = config;
    const paths = path.split('.');
    const key = paths.pop();
    paths.forEach((p)=>{
        // if (!config[p]) config[p] = {};
        configData = configData[p] || (configData[p] = {});
    });
    configData[key] = value;
    o2.Actions.load('x_program_center').ConfigAction.save({
        fileName: `${name}.json`,
        fileContent: JSON.stringify(config, null, "\t")
    });
}

async function saveConfigData(name, data) {
    const config = (configs[name]) ? configs[name] : (await loadConfig(name));
    Object.assign(config, data);
    console.log(config);
    o2.Actions.load('x_program_center').ConfigAction.save({
        fileName: `${name}.json`,
        fileContent: JSON.stringify(config, null, "\t")
    });
}

async function loadComponents() {
    const components = await o2.Actions.load("x_component_assemble_control").ComponentAction.listAll();
    return components.data;
}
async function removeComponent(id) {
    const components = await o2.Actions.load("x_component_assemble_control").ComponentAction.delete(id);
    return components.data;
}
async function getComponent(id) {
    const components = await o2.Actions.load("x_component_assemble_control").ComponentAction.get(id);
    return components.data;
}
async function saveComponent(data) {
    var action = o2.Actions.load("x_component_assemble_control").ComponentAction;
    if (data.id){
        const components = await action.edit(data.id, data);
        return components.data;
    }else{
        const components = await action.create(data);
        return components.data;
    }
}

async function dispatchComponentFile(file) {
    var action = o2.Actions.load("x_program_center").ModuleAction;
    const formData = new FormData();
    formData.append('file', file);
    formData.append('fileName', file.name);
    formData.append('filePath', '');
    const result = await action.dispatchResource(false, formData, file);
    return result.data;
}
async function deployWebResource(data) {
    var action = o2.Actions.load("x_program_center").ModuleAction;
    const formData = new FormData();
    const file = data.file[0];
    formData.append('file', file);
    formData.append('fileName', file.name);
    formData.append('filePath', data.path);
    const result = await action.dispatchResource(data.overwrite, formData, file);
    return result.data;
}

function getPublicData(name){
    return new Promise((resolve)=>{
        o2.UD.getPublicData(name, dData=>resolve(dData));
    });
}
function clearPublicData(name){
    return new Promise((resolve)=>{
        o2.UD.deletePublicData(name, dData=>resolve(dData));
    });
}
async function doAction(content, action, method, ...args) {
    const m = o2.Actions.load(content)[action][method];
    const json = await m.apply(m, args);
    return json.data;
}

async function loadProcessApplication() {
    const json = await o2.Actions.load("x_processplatform_assemble_surface").ApplicationAction.listWithPerson();
    return json.data;
}
async function loadPortalApplication() {
    const json = await o2.Actions.load("x_portal_assemble_surface").PortalAction.list();
    return json.data;
}
async function loadInforApplication() {
    const json = await o2.Actions.load("x_cms_assemble_control").AppInfoAction.listWhatICanView_AllType();
    return json.data;
}
async function loadQueryApplication() {
    const json = await o2.Actions.load("x_query_assemble_surface").QueryAction.listWithPerson();
    return json.data;
}

async function changePassword(credential, oldPassword, newPassword) {
    const json = await o2.Actions.load("x_program_center").ConfigAction.changePassword({credential, oldPassword, newPassword});
    return json.data;
}

async function loadPortals() {
    const json = await o2.Actions.load("x_portal_assemble_surface").PortalAction.list();
    return json.data;
}

async function getServers() {
    return doAction('x_program_center', 'CommandAction', 'getNodeInfoList');
}

async function getModules() {
    return doAction('x_program_center', 'ApplicationsAction', 'get');
}
async function connectCollect() {
    return doAction('x_program_center', 'CollectAction', 'connect');
}
async function validateCollect() {
    return doAction('x_program_center', 'CollectAction', 'validate');
}
async function loginToCollect(data) {
    return doAction('x_program_center', 'CollectAction', 'validateDirect', data);
}
async function checkCollectName(name) {
    return doAction('x_program_center', 'CollectAction', 'exist', name);
}
async function checkCollectPass(password) {
    return doAction('x_program_center', 'CollectAction', 'validatePassword', {password});
}
async function sendCode(mobile) {
    return doAction('x_program_center', 'CollectAction', 'code', mobile);
}
async function registCollect(data) {
    return doAction('x_program_center', 'CollectAction', 'regist', data);
}
async function disconnectCollect() {
    return doAction('x_program_center', 'CollectAction', 'disconnect');
}
async function deleteCollect(name, mobile, code) {
    return doAction('x_program_center', 'CollectAction', 'delete', name, mobile, code);
}
async function resetPasswordCollect(data) {
    return doAction('x_program_center', 'CollectAction', 'resetPassword', data);
}

async function getApplicationModules() {
    return doAction('x_program_center', 'ConfigAction', 'listApplication');
}
async function getDataEntrys() {
    return doAction('x_program_center', 'ConfigAction', 'listEntity');
}

export {
    getConfig,
    getConfigData,
    saveConfig,
    saveConfigData,
    loadComponents,
    removeComponent,
    getComponent,
    saveComponent,
    dispatchComponentFile,
    deployWebResource,
    getPublicData,
    clearPublicData,
    loadProcessApplication,
    loadPortalApplication,
    loadInforApplication,
    loadQueryApplication,
    changePassword,
    loadPortals,
    getServers,
    loadRuntimeConfig,
    getModules,
    connectCollect,
    validateCollect,
    loginToCollect,
    checkCollectName,
    checkCollectPass,
    sendCode,
    registCollect,
    disconnectCollect,
    deleteCollect,
    resetPasswordCollect,
    getApplicationModules,
    getDataEntrys
};
