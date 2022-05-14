import React from 'react';
import * as ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import {loadComponent, component} from '@o2oa/component';

loadComponent('appstore.application', (content, cb)=>{
    // const root = createRoot(content);
    // root.render(
    //     <React.StrictMode>
    //         <App/>
    //     </React.StrictMode>
    // );
    component.recordStatus = function(){
        return {"appId": this.options.appId,"appName":this.options.appName};
    }
    if (component.status) {
        component.options.appId = component.status.appId;
        component.options.appName = component.status.appName;
    }
    ReactDOM.render(
        <React.StrictMode>
            <App/>
        </React.StrictMode>,
        content,
        cb
    );
}).then((c)=>{
    c.render();
});


// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
