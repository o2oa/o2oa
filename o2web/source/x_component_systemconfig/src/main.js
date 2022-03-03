import { createApp } from 'vue';
import App from "./App.vue";
import {loadComponent} from '@o2oa/component';

loadComponent('systemconfig', (d, cb)=>{
    createApp(App).mount(d);
    cb();
}).then((c)=>{
    c.render();
});
