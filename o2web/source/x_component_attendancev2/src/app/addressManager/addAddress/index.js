import {component as content} from '@o2oa/oovm';
import {lp, o2} from '@o2oa/component';
import { isPositiveInt, isEmpty } from '../../../utils/common';
import { attendanceWorkPlaceV2Action } from '../../../utils/actions';
import template from './temp.html';
import style from "./style.scope.css";
import oInput from '../../../components/o-input';
import oTextarea from '../../../components/o-textarea';
import baiduMap from './baidu-map'; // 百度地图
import amap from './amap'; // 高德地图


export default content({
    style,
    template,
    components: {oInput, oTextarea, baiduMap, amap},
    autoUpdate: true,
    bind(){
        return {
            lp,
            fTitle: lp.workAddressAdd,
            form: {
              placeName: "",
              errorRange: "200",
              longitude: "",
              latitude: "",
              description: "",
              isView: false,
              positionType: "amap" // baidu amap
            },
        };
    },
    // 如果有数据过来
    beforeRender() {
        // 有值 表示是查看
        if (this.bind.form.id) {
            this.bind.form.isView = true;
            this.bind.fTitle = lp.workAddressView;
        }
    },
    close() {
        this.$topParent.publishEvent('address', {});
        this.$parent.closeFormVm();
    },
    async submitAdd() {
        let myForm = this.bind.form;
        if (isEmpty(myForm.longitude) || isEmpty(myForm.latitude)) {
            o2.api.page.notice(lp.workAddressForm.lnglatNotEmpty, 'error');
            return ;
        }
        if (isEmpty(myForm.placeName)) {
            o2.api.page.notice(lp.workAddressForm.titleNotEmpty, 'error');
            return ;
        }
        if (isEmpty(myForm.errorRange)) {
            o2.api.page.notice(lp.workAddressForm.rangeNotEmpty, 'error');
            return ;
        }
        if (!isPositiveInt(myForm.errorRange)) {
            o2.api.page.notice(lp.workAddressForm.rangeNeedNumber, 'error');
            return ;
        }
        const json = await attendanceWorkPlaceV2Action("post", myForm);
        console.debug('新增成功', json);
        o2.api.page.notice(lp.workAddressForm.success, 'success');
        this.close();
    },
   
});
