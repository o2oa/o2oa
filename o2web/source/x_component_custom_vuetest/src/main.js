import { createApp } from 'vue';
import App from "./App.vue";
import {loadComponent} from '@o2oa/component';

loadComponent('custom.vuetest', (d, cb)=>{
    createApp(App).mount(d);
    layout.mobile = true;
    cb();
}).then((c)=>{
    c.render();
});
