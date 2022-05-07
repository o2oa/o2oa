import React from 'react';
import {lp, o2} from '@o2oa/component';

export default class AppInstallSteps extends React.Component {
    constructor(props) {
        super(props);
    }
    // async componentDidMount() {
    //
    // }
    // getInstalledStatus(item){
    //     if (item.installStatus) return item.installStatus;
    //     let installStatus = '';
    //     if (item.vipApp && !this.isVip){
    //         installStatus = 'vip';
    //     }else if (!item.installedVersion){
    //         installStatus = 'notInstalled';
    //     }else{
    //         installStatus = (item.installedVersion===item.version) ? 'installed' : 'update';
    //     }
    //     item.installStatus = installStatus;
    //     return installStatus;
    // }
    // getActionText(item){
    //     const o = {
    //         vip: lp.installVip,
    //         notInstalled: lp.install,
    //         installed: lp.installed,
    //         update: lp.update
    //     }
    //     return o[item.installStatus || this.getInstalledStatus(item)];
    // }
    render() {
        // const data = this.props.data;
        // const indexPicStyle = {
        //     backgroundImage: 'url("'+data.indexPic+'")'
        // };
        // const price = parseInt(data.price) ? '￥'+data.price : "Free";
        // //const installText = this.getActionText(data)
        if (this.props.data){
            return (
                <div className="application-content-area">
                    <div className="application-content-rtf" dangerouslySetInnerHTML={{__html: this.props.data.installSteps}}/>
                </div>
            );
        }
        return '';
    }
}
