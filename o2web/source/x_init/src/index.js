// import '@o2oa/ui';
import index from './content/index.js';
import {serverStatus} from './common/action.js';

const loadScript = (url) => {
    return new Promise((resolve, reject) => {
        const script = document.createElement('script');
        script.src = url;
        script.onload = () => resolve();
        script.onerror = () => reject(new Error(`Failed to load script ${url}`));
        document.head.appendChild(script);
    });
};

const load = async () => {
    const status = await serverStatus();
    // if (status.status === 'starting') {
    // window.location = `/?${new Date().getTime()}`;
    // } else {
    await loadScript('../src/assets/ooui.iife.js');
    $OOUI.defineComponent();
    index.generate(document.body);
    // }
};
load();
