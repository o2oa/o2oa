import { component as content } from "@o2oa/oovm";
import { lp, component as app } from "@o2oa/component";
import { appealInfoActionListByPaging, configAction, appealInfoAction, processAction, jobAction } from "../../utils/actions";
import oPager from "../../components/o-pager";
import template from "./template.html";

export default content({
  template,
  components: { oPager },
  autoUpdate: true,
  bind() {
    return {
      lp,
      appealList: [],
      pagerData: {
        page: 1,
        totalCount: 0,
        totalPage: 1,
        size: 15, // 每页条目数
      },
    };
  },
  afterRender() {
    this.loadAppealList();
  },
  loadData(e) {
    if (
      e &&
      e.detail &&
      e.detail.module &&
      e.detail.module.bind
    ) {
      this.bind.pagerData.page = e.detail.module.bind.page || 1;
      this.loadAppealList();
    }
  },
  async loadAppealList() {
    const json = await appealInfoActionListByPaging(
      this.bind.pagerData.page,
      this.bind.pagerData.size,
      {}
    );
    if (json) {
      this.bind.appealList = json.data || [];
      const count = json.count || 0;
      this.bind.pagerData.totalCount = count;
    }
  },
  formatRecordResultClass(record) {
    let span = "";
    if (record.fieldWork) {
      span = "color-fieldWork";
    } else {
      const result = record.checkInResult;
      if (result === 'PreCheckIn') {
        span = "";
      } else if (result === 'NotSigned') {
        span =   "color-nosign";
      }  else if (result === 'Normal') {
        span =   "color-normal";
      } else if (result === 'Early') {
        span =    "color-early";
      } else if (result === 'Late') {
        span =    "color-late";
      } else if (result === 'SeriousLate') {
        span =    "color-serilate";
      } else { 
        span = "" ;
      }
    }
    return span;
  },
  formatRecordResult(record) {
    let span = "";
    if (record.fieldWork) {
      span = lp.appeal.fieldWork;
    } else {
      const result = record.checkInResult;
      if (result === 'PreCheckIn') {
        span = "";
      } else if (result === 'NotSigned') {
        span =  lp.appeal.notSigned;
      }  else if (result === 'Normal') {
        span =   lp.appeal.normal;
      } else if (result === 'Early') {
        span =   lp.appeal.early;
      } else if (result === 'Late') {
        span =   lp.appeal.late;
      } else if (result === 'SeriousLate') {
        span =   lp.appeal.seriousLate;
      } else { 
        span = "" ;
      }
    }
    return span;
  },
  formatAppealStatus(appeal) {
    if (appeal) {
      if (appeal.status === 0) {
        return  lp.appeal.status0;
      } else if (appeal.status=== 1) {
        return  lp.appeal.status1;
      } else if (appeal.status === 2) {
        return lp.appeal.status2;
      } else if (appeal.status === 3) {
        return lp.appeal.status3;
      } else if (appeal.status === 4) {
        let name = appeal.updateStatusAdminPerson;
        if (name && name.indexOf("@") > -1) {
          name =  name.split("@")[0];
        }
        return lp.appeal.status4 + " ["+ name + "]";
      }  
    }
    return "";
  },
  async startProcess(appeal) {
    // 查询配置
    const json = await configAction("get");
    if (json && json.appealEnable && json.processId) {
      // 检查是否能够申诉 有可能超过限制次数
      const checkResult = await appealInfoAction("startCheck", appeal.id);
      if (checkResult) {
        const process = await processAction("get", json.processId);
        MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function(){
          var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(process, app, {
              "latest" : false,
              "workData" : { "appealId": appeal.id, "record": appeal.record }, // 把id和打卡记录传给流程
              "onStarted": function(data, title, processName){
                  this._afterStartProcess(data, appeal.id);
              }.bind(this)
          });
          starter.load();
        }.bind(this));
      }
    } else {
      o2.api.page.notice(lp.appeal.startProcessNoConfigError, 'error');
    }
  },
  // 流程启动后 打开工作文档并更新数据
  async _afterStartProcess(data, id){
    var currentTask = [];
    var jobIds = [];
    data.each(function(work){
        if (work.currentTaskIndex > -1) {
          jobIds.push( work.job || "" );
          currentTask.push(work.taskList[work.currentTaskIndex].work);
        }
    });
    // 打开流程工作文档
    if (currentTask.length > 0){
        o2.api.page.openWork(currentTask[0]);
    }
    // 更新数据
    var body = {
      job: (jobIds[0] || "")
    };
    const json = await appealInfoAction("startProcess", id, body);
    console.debug('更新成功', json);
    this.loadAppealList();
  },
  // 打开流程工作
  async openJob(jobId, id) {
    if (jobId) {
      // 先查询 工作是否存在 有可能新建检查后待办工作已经删除
      const result = await jobAction("findWorkWorkCompleted", jobId);
      var workList = result.workList || [];
      var workCompletedList =  result.workCompletedList || [];
      if ((workList.length + workCompletedList.length) > 0 ) {
        o2.api.page.openJob(jobId);
      } else { // 没有 work  是否已经删除？
        var _self = this;
        o2.api.page.confirm(
          "warn",
          lp.alert,
          lp.appeal.notfoundJobError,
          300,
          100,
          function () {
            _self.resetStatus(id);
            this.close();
          },
          function () {
            this.close();
          }
        );
      }
    }
  },
  // 还原数据状态
  async resetStatus(id) {
    const json = await appealInfoAction("resetStatus", id);
    console.debug('还原状态成功', json);
    this.loadAppealList();
  },
  // 关闭表单页面
  closeGroup() {
    // if (this.addGroupVm) {
    //   this.addGroupVm.destroy();
    // }
    this.loadAppealList();
  },
});
