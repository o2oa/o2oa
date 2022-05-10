import React from 'react';
import {lp, o2} from '@o2oa/component';

export default class AppInstallSteps extends React.Component {
    constructor(props) {
        super(props);
        this.setHtmlRef = element => {
            this.props.setLinkTargetFun(element);
        };
    }
    render() {
        if (this.props.data){
            return (
                <div className="application-content-area">
                    <div className="application-content-rtf" ref={this.setHtmlRef} dangerouslySetInnerHTML={{__html: this.props.data.installSteps}}/>
                </div>
            );
        }
        return '';
    }
}
