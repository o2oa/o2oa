import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import { isEmpty } from "../../../utils/common";
import { leaveManagerAction } from "../../../utils/actions";
import template from "./template.html";
import oInput from "../../../components/o-input";

export default content({
  template,
  components: { oInput },
  autoUpdate: true,

  bind() {
    return {
      lp,
      fTitle: lp.leaveManagerV2.addType,
      form: {
        name: "",
        quotaType: "UNLIMITED", // QUOTA | UNLIMITED
        unit: "DAY", // DAY | HOUR
        active: true,
        isPaid: true,
      }
    }
    },
    // 先查询数据
  async beforeRender() {
    if (this.bind.updateId) {
      const leaveType = await leaveManagerAction("typeGet", this.bind.updateId);
      if (leaveType) {
        this.bind.form = leaveType;
        this.bind.fTitle = lp.leaveManagerV2.editType;
      }
    }
  },
  afterRender() {
    console.debug("  leave type form after render", this.bind);
  },
  clickChangeQuotaType(type) {
    this.bind.form.quotaType = type;
  },
  async submit() {
    const form = this.bind.form;
    if (isEmpty(form.name)) {
      o2.api.page.notice(lp.leaveManagerV2.type.namePlaceholder, 'error');
      return;
    }
    const result = await leaveManagerAction("typePost", form);
    console.log(result);
    o2.api.page.notice(lp.saveSuccess, 'success');
    this.close();
  },
  // 关闭当前窗口
  close() {
    this.$parent.publishEvent('leaveType', {});
    this.$parent.closeFormVm();
  },
});