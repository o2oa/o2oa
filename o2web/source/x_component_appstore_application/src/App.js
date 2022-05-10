import './App.css';
import React from 'react';
import AppBase from './components/AppBase.js';
import AppDescribe from './components/AppDescribe.js';
import AppInstallSteps from './components/AppInstallSteps.js';
import AppVideo from './components/AppVideo.js';

import {o2, component, lp} from '@o2oa/component';

export default class App extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            navi: 'describe',
            isVip: false,
            playVideo: false
        };
    }
    componentDidMount() {
        const action = o2.Actions.load('x_program_center').MarketAction;
        action.get(component.options.appId).then((json)=>{
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
    getClassName(type){
        return this.state.navi===type ? 'application-navi-item mainColor_color mainColor_border' : 'application-navi-item';
    }
    playVideo(){
        this.setState({playVideo: true});
        this.changeNavi('video');
    }
    getContent(){
        const contents = {
            describe: <AppDescribe data={this.state.data} setLinkTargetFun={this.setLinkTarget}/>,
            installSteps: <AppInstallSteps data={this.state.data} setLinkTargetFun={this.setLinkTarget}/>,
            video:(this.state.data && this.state.data.video) ? <AppVideo data={this.state.data} playVideo={this.state.playVideo}/> : ''
        }
        return contents[this.state.navi];
    }
    setLinkTarget(node){
        if (node){
            const nodes = node.querySelectorAll('a');
            nodes.forEach((a)=>{
                a.set('target', '_blank');
            });
        }
    }
    render() {
        const appBase = (this.state.data) ? <AppBase data={this.state.data} isVip={this.state.isVip} parent={this} /> : '';

        const videoNavi = (this.state.data && this.state.data.video) ? <div className={this.getClassName('video')} onClick={()=>{this.changeNavi('video')}}>{lp.video}</div> : '';
        return (
            <div className="application-content application-root">
                {appBase}
                <div className="application-navi">
                    <div className={this.getClassName('describe')} onClick={()=>{this.changeNavi('describe')}}>{lp.describe}</div>
                    <div className={this.getClassName('installSteps')} onClick={()=>{this.changeNavi('installSteps')}}>{lp.installSteps}</div>
                    {videoNavi}
                </div>
                {this.getContent()}
            </div>
        );
    }
}
