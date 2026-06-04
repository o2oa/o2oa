import { component as content } from '@o2oa/oovm';
import { lp, o2, component as app } from '@o2oa/component';
import {
    appealInfoAction,
    appealInfoActionListByPaging,
    configAction,
    jobAction,
    processAction
} from '../../utils/actions';
import template from './template.html';
import style from './style.scope.css';

export default content({
    template,
    style,
    autoUpdate: true,
    bind() {
        return {
            lp,
            appealList: [],
            page: 1,
            size: 10,
            totalCount: 0,
            totalPage: 1,
            appealEnable: false,
            loading: false,
            emptyText: '暂无考勤异常数据'
        };
    },
    afterRender() {
        this.loadConfig();
    },
    async loadConfig() {
        this.imConfig = await configAction('get');
        this.bind.appealEnable = !!(this.imConfig && this.imConfig.appealEnable && this.imConfig.processId);
        this.loadAppealList();
    },
    async loadAppealList() {
        this.bind.loading = true;
        try {
            const json = await appealInfoActionListByPaging(
                this.bind.page,
                this.bind.size,
                {}
            );
            if (json) {
                const list = json.data || [];
                const count = json.count || 0;
                this.bind.appealList = list.map((appeal) => this.buildAppealItem(appeal));
                this.bind.totalCount = count;
                this.bind.totalPage = Math.max(1, Math.ceil(count / this.bind.size));
                this.bind.emptyText = '暂无考勤异常数据';
            }
        } catch (err) {
            console.error('查询考勤异常数据失败', err);
            this.bind.appealList = [];
            this.bind.emptyText = '考勤异常数据加载失败';
            o2.api.page.notice('考勤异常数据加载失败，请重试！', 'error');
        } finally {
            this.bind.loading = false;
        }
    },
    buildAppealItem(appeal) {
        const record = appeal.record || {};
        const resultClass = this.formatRecordResultClass(record);
        const statusClass = this.formatAppealStatusClass(appeal);
        return Object.assign({}, appeal, {
            record,
            dutyTypeText: record.checkInType === 'OnDuty' ? lp.onDuty : lp.offDuty,
            recordResultText: this.formatRecordResult(record),
            recordResultClass: resultClass ? `appeal-result ${resultClass}` : 'appeal-result',
            statusText: this.formatAppealStatus(appeal),
            statusClass: statusClass ? `appeal-status ${statusClass}` : 'appeal-status',
            canStartProcess: this.bind.appealEnable && appeal.status === 0,
            canOpenJob: !!appeal.jobId
        });
    },
    formatRecordResultClass(record) {
        let span = '';
        if (record.fieldWork) {
            span = 'color-fieldWork';
        } else {
            const result = record.checkInResult;
            if (result === 'PreCheckIn') {
                span = '';
            } else if (result === 'NotSigned') {
                span = 'color-nosign';
            } else if (result === 'Normal') {
                span = 'color-normal';
            } else if (result === 'Early') {
                span = 'color-early';
            } else if (result === 'Late') {
                span = 'color-late';
            } else if (result === 'SeriousLate') {
                span = 'color-serilate';
            } else {
                span = '';
            }
        }
        return span;
    },
    formatRecordResult(record) {
        let span = '';
        if (record.fieldWork) {
            span = lp.appeal.fieldWork;
        } else {
            const result = record.checkInResult;
            if (result === 'PreCheckIn') {
                span = '';
            } else if (result === 'NotSigned') {
                span = lp.appeal.notSigned;
            } else if (result === 'Normal') {
                span = lp.appeal.normal;
            } else if (result === 'Early') {
                span = lp.appeal.early;
            } else if (result === 'Late') {
                span = lp.appeal.late;
            } else if (result === 'SeriousLate') {
                span = lp.appeal.seriousLate;
            } else {
                span = '';
            }
        }
        return span;
    },
    formatAppealStatus(appeal) {
        if (appeal) {
            if (appeal.status === 0) {
                return lp.appeal.status0;
            } else if (appeal.status === 1) {
                return lp.appeal.status1;
            } else if (appeal.status === 2) {
                return lp.appeal.status2;
            } else if (appeal.status === 3) {
                return lp.appeal.status3;
            } else if (appeal.status === 4) {
                let name = appeal.updateStatusAdminPerson;
                if (name && name.indexOf('@') > -1) {
                    name = name.split('@')[0];
                }
                return `${lp.appeal.status4} [${name || ''}]`;
            } else if (appeal.status === 5) {
                return lp.appeal.status5;
            }
        }
        return '';
    },
    formatAppealStatusClass(appeal) {
        if (!appeal) {
            return '';
        }
        if (appeal.status === 0) {
            return 'waiting';
        }
        if (appeal.status === 4) {
            return 'done';
        }
        if (appeal.status === 5) {
            return 'reject';
        }
        return 'processing';
    },
    prevPage() {
        if (this.bind.page <= 1 || this.bind.loading) {
            return;
        }
        this.bind.page = this.bind.page - 1;
        this.loadAppealList();
    },
    nextPage() {
        if (this.bind.page >= this.bind.totalPage || this.bind.loading) {
            return;
        }
        this.bind.page = this.bind.page + 1;
        this.loadAppealList();
    },
    refresh() {
        if (this.bind.loading) {
            return;
        }
        this.loadAppealList();
    },
    async startProcess(appeal) {
        if (this.bind.appealEnable) {
            const checkResult = await appealInfoAction('startCheck', appeal.id);
            if (checkResult && this.imConfig.processId) {
                const process = await processAction('get', this.imConfig.processId);
                MWF.xDesktop.requireApp('process.TaskCenter', 'ProcessStarter', function () {
                    const starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(process, app, {
                        latest: false,
                        workData: { appealId: appeal.id, record: appeal.record },
                        onStarted: function (data) {
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
    async _afterStartProcess(data, id) {
        const currentTask = [];
        const jobIds = [];
        data.each(function (work) {
            if (work.currentTaskIndex > -1) {
                jobIds.push(work.job || '');
                currentTask.push(work.taskList[work.currentTaskIndex].work);
            }
        });
        const body = {
            job: jobIds[0] || ''
        };
        const json = await appealInfoAction('startProcess', id, body);
        console.debug('更新成功', json);
        this.loadAppealList();
        if (currentTask.length > 0) {
            o2.api.page.openWork(currentTask[0]);
        }
    },
    async openJob(jobId, id) {
        if (jobId) {
            const result = await jobAction('findWorkWorkCompleted', jobId);
            const workList = result.workList || [];
            const workCompletedList = result.workCompletedList || [];
            if ((workList.length + workCompletedList.length) > 0) {
                o2.api.page.openJob(jobId);
            } else {
                const _self = this;
                o2.api.page.confirm(
                    'warn',
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
    async resetStatus(id) {
        const json = await appealInfoAction('resetStatus', id);
        console.debug('还原状态成功', json);
        this.loadAppealList();
    }
});
