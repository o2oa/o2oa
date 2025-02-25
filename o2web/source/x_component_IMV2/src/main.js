import { createApp } from 'vue';
import './style.css';
import App from './App.vue';
import { createPinia } from 'pinia';
import loadingPlugin from './plugins/loading.js';
import {eventBus} from "./utils/eventBus.js";

import {loadComponent, component} from '@o2oa/component';

loadComponent('IMV2', (d, cb)=>{
    const app = createApp(App, {options: component.options});
    app.use(createPinia());
    app.use(loadingPlugin);
    app.provide('eventBus', eventBus());
    app.mount(d);
    cb();
}).then((c)=>{
    c.render();
});
