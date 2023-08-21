import React from 'react';
import {component, lp, o2} from '@o2oa/component';

function VipFlag(props){
    if (props.vipApp){
       return <div className="application-baseinfo-vip">VIP</div>
    }
    return '';
}

export default class AppBase extends React.Component {
    constructor(props) {
        super(props);
        this.action = o2.Actions.load('x_program_center').MarketAction;
    }
    getInstalledStatus(item){
        //if (item.installStatus) return item.installStatus;
        let installStatus = '';
        if (item.vipApp && !this.props.isVip){
            installStatus = 'vip';
        }else if (!item.installedVersion){
            installStatus = 'notInstalled';
        }else{
            installStatus = (item.installedVersion===item.version) ? 'installed' : 'update';
        }
        //item.installStatus = installStatus;
        return installStatus;
    }
    getActionText(item){
        const o = {
            vip: lp.installVip,
            notInstalled: lp.install,
            installed: lp.installed,
            update: lp.update
        }
        return o[item.installStatus || this.getInstalledStatus(item)];
    }
    openCommunity(){
        o2.openWindow(lp.communityUrl);
    }
    download(){
        const id = this.props.data.id;
        const url = this.action.action.address+this.action.action.actions.download.uri;
        const address = url.replace("{id}", id);
        console.log(address);
        o2.openWindow(address);
    }
    installApp(event){
        const data = this.props.data;
        const status = this.getInstalledStatus(data);
        switch (status) {
            case 'vip':
                this.contactUs();
                break;
            case 'notInstalled':
                this.installOrUpdate(data, event.currentTarget, lp.installInfoTitle, lp.installInfo);
                break;
            default:
                this.installOrUpdate(data, event.currentTarget, lp.updateInfoTitle, lp.updateInfo);
        }
    }
    contactUs(){
        const node = new Element("div", {
            html: `<div class="appstore-contactus-text">${lp.contactUs}</div>
        <div class="appstore-contactus"></div>
        <div class="appstore-contactus-phone">${lp.phoneNumber}</div>`
        });
        var dlg = o2.DL.open({
            title: '',
            height: 400,
            content: node,
            container: component.content,
            maskNode: component.content
        });
    }
    installOrUpdate(item, e, title, info){
        const app = this;
        component.confirm('info', e, title, info, 380, 100, function(){
            e.set('text', lp.installing);
            app.action.installOrUpdate(item.id).then(()=>{
                component.notice(lp.installSuccess, 'success');
                e.set('text', lp.installed);
            });
            this.close()
        }, function(){
            this.close();
        }, null, component.content);
    }
    playVideo(){
        const node = new Element("div", {
            html: `<div class="appstore-video-dlg">
                <video controls autoplay><source src="${this.props.data.video}" /></video>
            </div>`
        });
        var dlg = o2.DL.open({
            title: '',
            height: 640,
            width: 1200,
            content: node,
            container: component.content,
            maskNode: component.content
        });

    }
    render() {
        const data = this.props.data;
        const indexPicStyle = {
            backgroundImage: 'url("'+data.indexPic+'")'
        };

        let price = '';
        if (!data.vipApp) price = parseInt(data.price) ? '￥'+data.price : 'Free';

        const downloadAction = this.getInstalledStatus(data)!=='vip' ? <div className="application-actions-download mainColor_color" onClick={(e)=>{this.download(e)}}>{lp.download}</div> : '';

        const play = (data.video) ? <div className="application-video-play" onClick={()=>{this.playVideo()}}/> : '';
        return (
            <div className="application-baseContent">
                <div className="application-indexpic" style={indexPicStyle}>
                    {play}
                </div>
                <div className="application-actions">
                    <div className="application-actions-price mainColor_color">{price}</div>
                    <div className="application-actions-button mainColor_bg" onClick={(e)=>{this.installApp(e)}}>{this.getActionText(data)}</div>
                    <div className="application-actions-button grayColor_bg" onClick={this.openCommunity}>{lp.community}</div>
                    <div className="application-actions-infotitle mainColor_color">TIP</div>
                    <div className="application-actions-communityInfo">{lp.communityInfo}</div>
                    {downloadAction}
                </div>
                <div className="application-baseinfo">
                    <div className="application-baseinfo-name">{data.name}</div>
                    <div className="application-baseinfo-version">
                        <div className="application-baseinfo-version-text">{data.version}</div>
                        <div className="application-baseinfo-o2version-text">{lp.o2Version}{data.o2Version}</div>
                        <VipFlag vipApp={data.vipApp}/>
                    </div>
                    <div className="application-baseinfo-category">{lp.updateTime}: {data.updateTime}</div>
                    <div className="application-baseinfo-category">{lp.category}: {data.category}</div>
                    <div className="application-baseinfo-abort">{data.abort}</div>
                </div>
            </div>
        );
    }
}
