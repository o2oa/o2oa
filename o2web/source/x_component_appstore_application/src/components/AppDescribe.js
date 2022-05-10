import React from 'react';
import {lp, o2, component} from '@o2oa/component';

function Pic(props) {
    // const img = {backgroundImage: 'url("'+props.url+'")'}
    return (
        <img className="application-content-pic" src={props.url} data-original={props.url} />
    );
}

export default class AppDescribe extends React.Component {
    constructor(props) {
        super(props);
        this.setPicRef = element => {
            this.picAreaNode = element;
            this.loadImgView();
        };
        this.setHtmlRef = element => {
            this.props.setLinkTargetFun(element);
        };
    }

    loadImgView(){
        if(this.viewer) this.viewer.destroy();
        if (this.picAreaNode){
            o2.loadCss("../o2_lib/viewer/viewer.css", component.content, function(){
                o2.load("../o2_lib/viewer/viewer.js", function(){
                    this.viewer = new window.Viewer(this.picAreaNode, {
                        url: 'data-original'
                    });
                }.bind(this));
            }.bind(this));
        }
    }
    render() {
        if (this.props.data){
            const piclist = this.props.data.picList.map((pic)=>
                <Pic url={pic} key={pic} />
            );

            return (
                <div className="application-content-area">
                    <div className="application-content-rtf" ref={this.setHtmlRef} dangerouslySetInnerHTML={{__html: this.props.data.describe}}/>
                    <div className="application-content-title">{lp.appPic}</div>
                    <div ref={this.setPicRef}>{piclist}</div>
                </div>
            );
        }
        return '';
    }
}
