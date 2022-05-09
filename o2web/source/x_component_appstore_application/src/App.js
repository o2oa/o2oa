import './App.css';
import React from 'react';
import AppBase from './components/AppBase.js';
import AppDescribe from './components/AppDescribe.js';
import AppInstallSteps from './components/AppInstallSteps.js';

import {o2, component, lp} from '@o2oa/component';

export default class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            navi: 'describe',
            isVip: false
        };
        if (!component.options.appid) component.options.appid = 'e87b59f0-2c57-4e24-9cf4-60c9a05361f2';
    }
    componentDidMount() {
        const action = o2.Actions.load('x_program_center').MarketAction;
        action.get(component.options.appid).then((json)=>{
            component.setTitle(lp.title+'-'+json.data.name);
            this.setState({data:json.data});
        });
        action.cloudUnitIsVip().then((json)=>{
            this.setState({isVip: json.data.value});
        });
    }
    changeNavi(navi){
        this.setState({navi});
    }
    render() {
        const appBase = (this.state.data) ? <AppBase data={this.state.data} isVip={this.state.isVip} /> : '';

        let content, describeClass, installStepsClass;
        if (this.state.navi==='describe'){
            content = <AppDescribe data={this.state.data}/>;
            describeClass = 'application-navi-item mainColor_color mainColor_border';
            installStepsClass = 'application-navi-item';
        }else{
            content = <AppInstallSteps data={this.state.data}/>;
            installStepsClass = 'application-navi-item mainColor_color mainColor_border';
            describeClass = 'application-navi-item';
        }

        return (
            <div className="application-content application-root">
                {appBase}
                <div className="application-navi">
                    <div className={describeClass} onClick={()=>{this.changeNavi('describe')}}>{lp.describe}</div>
                    <div className={installStepsClass} onClick={()=>{this.changeNavi('installSteps')}}>{lp.installSteps}</div>
                    <div className={installStepsClass} onClick={()=>{this.changeNavi('installSteps')}}>{lp.installSteps}</div>
                </div>
                {content}
            </div>
        );
    }
}
