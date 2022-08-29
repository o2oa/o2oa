import { createApp } from 'vue';
import App from "./App.vue";
import {loadComponent} from '@o2oa/component';
import ElementPlus from 'element-plus';
import './assets/element.css';

loadComponent('systemconfig', (d, cb)=>{
    const app = createApp(App).use(ElementPlus, { size: 'middle'}).mount(d);
    cb();
}).then((c)=>{
    c.render();
});
