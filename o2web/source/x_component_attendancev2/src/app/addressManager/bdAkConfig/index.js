import {component as content} from '@o2oa/oovm';
import {lp, o2} from '@o2oa/component';
import { getPublicData, putPublicData } from '../../../utils/actions';
import { isEmpty } from '../../../utils/common';
import template from './temp.html';
import oInput from '../../../components/o-input';


export default content({
    template,
    components: {oInput},
    autoUpdate: true,
    bind(){
        return {
            lp,
            mapConfig: {
                mapType: "baidu", //  amap baidu 
                baiduAccountKey: "",
                aMapAccountKey: "",
            },
            mapTypeList: [
                {
                    name: "百度地图",
                    key: "baidu"
                },
                {
                    name: "高德地图",
                    key: "amap"
                },
            ]
        };
    },
    close() {
        this.$topParent.publishEvent('address', {});
        this.$parent.closeFormVm();
    },
    chooseMapType(e) {
        const value = e.target.value;
        this.bind.mapConfig.mapType = value;
        console.debug("设置类型： " + value);
    },
    async submitAdd() {
        debugger;
        if (this.bind.mapConfig.mapType === "baidu" && isEmpty(this.bind.mapConfig.baiduAccountKey)) {
            o2.api.page.notice(lp.workAddressBDSecretTitlePlaceholder, 'error');
            return ;
        }
        if (this.bind.mapConfig.mapType === "amap" && isEmpty(this.bind.mapConfig.aMapAccountKey)) {
            o2.api.page.notice(lp.workAddressBDSecretTitlePlaceholder, 'error');
            return ;
        }
        const bdKey = await putPublicData("attendanceMapConfig", this.bind.mapConfig);
        console.debug('配置成功', bdKey);
        this.close();
    },
   
});
