import { createApp } from 'vue';
import App from "./App.vue";
import {loadComponent} from '@o2oa/component';

loadComponent('systemconfig', (d, cb)=>{
    const app = createApp(App).mount(d);
    app.component("SystemInfor")
    cb();
}).then((c)=>{
    c.render();
});
