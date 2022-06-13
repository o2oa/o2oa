import {o2} from '@o2oa/component';
const root = 'x_program_center';
const actionName = 'ConfigAction';
const action = o2.Actions.load(root)[actionName];

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
    const json = await action.open(body);
    configs[name] = JSON.parse(json.data.fileContent);
    return configs[name];
}

async function getConfig(name) {
    if (configs[name]) return configs[name];
    return await loadConfig(name);
}

async function saveConfig(name, path, value) {
    const config = (configs[name]) ? configs[name] : (await loadConfig(name));
    exec(`this.${path} = value`, config, {value});
    console.log(config[path]);
}




export {getConfig, saveConfig};
