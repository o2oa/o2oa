import {component as content} from "@o2oa/oovm";
import {lp, o2} from "@o2oa/component";
import ExcelJS from "exceljs";

import {
    formatPersonName,
    getAllDatesInMonth,
    formatDate,
    formatMonth,
    isEmpty,
    showLoading,
    hideLoading,
    replaceCustomString,
    lpFormat,
    generateExcelColumnNames,
    chooseSingleFile
} from "../../../utils/common";
import {groupScheduleAction} from "../../../utils/actions";
import selectShiftMultiple from "../../shiftManager/selectShiftMultiple";
import style from "./style.scope.css";
import template from "./temp.html";


export default content({
    style,
    template,
    components: {selectShiftMultiple},
    autoUpdate: true,
    bind() {
        return {
            lp,
            fTitle: lp.scheduleForm.title,
            trueParticipantList: [], // 排班人员
            month: "",
            groupId: "",
            dateList: [], // 日期对象
            //  班次选择器使用
            shiftSelectorOpen: false,
            shiftSelector: {
                shiftSelected: []
            },
            // 排班数据
            scheduleList: [],
            // 排班周期
            shiftCycleList: [], // 班次 id
        };
    },
    // 先查询数据
    async beforeRender() {
        // 初始化月份日期数据
        const now = new Date();
        now.setDate(1);// 设置每月的 1 号
        this.bind.month = formatMonth(now);
        this.bind.currentDate = now;
        // 班次和排班周期等配置数据
        await this.loadConfigData();
        // 日期人员表格
        this.loadDateTable();
        // 排班数据 根据月份查询
        this.loadMonthScheduleList();

    },
    afterRender() {
        this.initUI();
        // 加载月份选择器
        this.loadMonthPicker();
    },
    // 初始化界面
    initUI() {
        const mask = this.dom.querySelector("#scheduleFormBox");
        mask.style["z-index"] = "1999";
        const dialog = this.dom.querySelector("#scheduleFormDialog");
        let width = mask.clientWidth;
        if (width > 1300) {
            width = 1300;
        }
        let height = mask.clientHeight - 200;
        if (height < 500) {
            height = mask.clientHeight;
        }
        let left = mask.clientWidth - width;
        if (left < 0) {
            left = 0;
        }
        left = left / 2;
        let top = mask.clientHeight - height;
        if (top < 0) {
            top = 0;
        }
        top = top / 2;
        dialog.style.width = width + "px";
        dialog.style.height = height + "px";
        dialog.style.left = left + "px";
        dialog.style.top = top + "px";
    },
    loadMonthPicker() {
        MWF.require("MWF.widget.Calendar", function () {
            var options = {
                "style": "xform",
                "secondEnable": false,
                "timeSelectType": "select",
                "clearEnable": false,
                "isTime": false,
                "timeOnly": false,
                "monthOnly": true,
                "yearOnly": false,
                "defaultDate": null,
                "defaultView": "month",
                "target": document.body,
                "onComplate": function (formateDate, date) {
                    this.changeMonthValue(date);
                }.bind(this)

            };
            if (this.bind.month && this.bind.month != "") {
                options.baseDate = new Date(this.bind.month);
            }
            let bindDom = this.dom.querySelector(".input");
            if (!bindDom) {
                bindDom = this.dom;
            }
            this.calendar = new MWF.widget.Calendar(bindDom, options);
        }.bind(this));
    },
    // 切换月份
    async changeMonthValue(date) {
        date.setDate(1);
        const newMonth = formatMonth(date);
        if (newMonth !== this.bind.month) {
            // 先保存当前月份数据
            await this.postMonthData(false);
            // 然后再更新月份刷新页面
            this.bind.month = newMonth
            this.bind.currentDate = date;
            this.loadDateTable()
            // 处理刷新
            this.loadMonthScheduleList();
        }
    },
    // 班次和排班周期等配置数据
    async loadConfigData() {
        const config = await groupScheduleAction("configByGroupId", this.bind.groupId);
        if (config && config.scheduleConfigJson) {
            const jsonObj = JSON.parse(config.scheduleConfigJson);
            this.bind.shiftSelector.shiftSelected = jsonObj.shiftSelected || [];
            this.bind.shiftCycleList = jsonObj.shiftCycleList || [];
        }
    },
    // 根据日期展现表格
    loadDateTable() {
        // 人员日期表格数据
        let dateList = [];
        const currentMonthDateList = getAllDatesInMonth(this.bind.currentDate);
        if (currentMonthDateList && currentMonthDateList.length > 0) {
            for (let index = 0; index < currentMonthDateList.length; index++) {
                const element = currentMonthDateList[index];
                const dName = this.dayName(element.getDay());
                dateList.push({
                    date: element,
                    text: `${element.getDate()}`,
                    day: dName,
                    dateString: formatDate(element)
                });
            }
        }
        this.bind.dateList = dateList;
    },
    // 获取月份数据
    async loadMonthScheduleList() {
        await showLoading(this);
        console.log("开始获取月份数据", this.bind.month, this.bind.groupId);
        try {
            const list = await groupScheduleAction("listMonth", this.bind.groupId, this.bind.month);
            const scheduleList = list || [];
            this.loadShift(scheduleList);
            this.bind.scheduleList = scheduleList;
        } catch (e) {
            console.error(e);
        }
        await hideLoading(this);
    },
    // 如果本地缓存中没有班次数据，根据返回的排班结果中获取班次数据
    loadShift(scheduleList) {
        // 已经有班次数据 不需要处理了
        if (this.bind.shiftSelector.shiftSelected.length === 0 && scheduleList.length > 0) {
            let shiftList = [];
            for (let index = 0; index < scheduleList.length; index++) {
                const element = scheduleList[index].shift;
                const existingItemIndex = shiftList.findIndex(item => item.id === element.id);
                if (existingItemIndex === -1) {
                    shiftList.push(element);
                }
            }
            this.bind.shiftSelector.shiftSelected = shiftList;
        }
    },
    // 班次列表的颜色样式
    shiftIndexClassName(index) {
        return "shift_color_item shift_color_" + index;
    },
    // 获取星期的中文名
    dayName(index) {
        switch (index) {
            case 0 :
                return lp.day.Sunday;
            case 1 :
                return lp.day.Monday;
            case 2 :
                return lp.day.Tuesday;
            case 3 :
                return lp.day.Wednesday;
            case 4 :
                return lp.day.Thursday;
            case 5 :
                return lp.day.Friday;
            case 6 :
                return lp.day.Saturday;
            default:
                return "";
        }
    },
    // 方格内班次名显示
    scheduleDateShow(date, person, list) {
        for (let index = 0; index < this.bind.scheduleList.length; index++) {
            const element = this.bind.scheduleList[index];
            if (element.scheduleDateString === date && element.userId === person) {
                return element.shift.shiftName;
            }
        }
        return "";
    },
    // 方格内班次 样式
    scheduleDateClass(date, person) {
        let className = "schedule_cell";
        let shiftId = "";
        for (let index = 0; index < this.bind.scheduleList.length; index++) {
            const element = this.bind.scheduleList[index];
            if (element.scheduleDateString === date && element.userId === person) {
                shiftId = element.shift.id;
                break;
            }
        }
        if (!isEmpty(shiftId)) {
            const index = this.bind.shiftSelector.shiftSelected.findIndex(function (e) {
                return e.id === shiftId;
            });
            if (index !== -1) {
                className = "schedule_cell shift_color_" + index;
            }
        }
        return className;
    },
    // 关闭当前页面
    closeSelf() {
        this.component.destroy();
    },
    // 提交按钮
    submit() {
        this.postMonthData(true);
    },
    // 从上个月复制排班数据
    copyFromLastMonth() {
        const year = this.bind.currentDate.getFullYear();
        const month = this.bind.currentDate.getMonth();
        // 计算上个月的年份和月份
        const lastMonthYear = month === 0 ? year - 1 : year;
        const lastMonth = month === 0 ? 11 : month - 1;
        const lastMonthDate = new Date(lastMonthYear, lastMonth, 1);
        const lastMonthString = formatMonth(lastMonthDate);
        var _self = this;
        const c = lpFormat(lp, "scheduleForm.copyConfirmInfo", {month: lastMonthString});
        o2.api.page.confirm(
            "warn",
            lp.alert,
            c,
            300,
            100,
            function () {
                this.close();
                _self.loadLastMonthDataAndCopy(lastMonthString);
            },
            function () {
                this.close();
            }
        );
    },
    // 查询月份数据 并把数据替换成当前的月份
    async loadLastMonthDataAndCopy(month) {
        await showLoading(this);
        console.log(this.bind.month, month);
        try {
            const list = await groupScheduleAction("listMonth", this.bind.groupId, month);
            const scheduleList = list || [];
            if (scheduleList.length > 0) {
                // 处理数据 把月份改成 当前月份
                let newScheduleList = [];
                for (let index = 0; index < scheduleList.length; index++) {
                    const element = scheduleList[index];
                    element.scheduleDateString = replaceCustomString(element.scheduleDateString, month, this.bind.month);
                    newScheduleList.push(element);
                }
                this.bind.scheduleList = newScheduleList;
            }
        } catch (e) {
            console.error(e);
        }
        await hideLoading(this);
    },
    // 提交排班数据
    async postMonthData(close) {
        if (this.bind.scheduleList.length === 0) {
            if (close) {
                this.closeSelf();
            }
            return;
        }
        await showLoading(this);
        try {
            // 排班班次列表和周期列表的数据作为配置存到后台，方便后续使用
            const scheduleConfigJson = {
                shiftSelected: this.bind.shiftSelector.shiftSelected,
                shiftCycleList: this.bind.shiftCycleList
            };
            const body = {
                groupId: this.bind.groupId,
                month: this.bind.month,
                scheduleList: this.bind.scheduleList,
                scheduleConfigJson: JSON.stringify(scheduleConfigJson)
            };
            const result = await groupScheduleAction("postMonth", body);
            console.log(result);
        } catch (e) {
            console.error(e);
        }
        await hideLoading(this);
        if (close) {
            this.closeSelf();
        }
    },
    personName(person) {
        return formatPersonName(person);
    },
    // 打开班次选择器
    async openShiftSelector() {
        this.bind.shiftSelectorOpen = true;
    },
    // 关闭班次选择器
    closeMultipleShiftSelector() {
        this.bind.shiftSelectorOpen = false;
    },
    // 点击排班方格
    clickScheduleBox(date, person, cycle, e) {
        // 绑定当前点击日期和人员
        this.bind.clickDate = date;
        this.bind.clickPerson = person;
        // 班次周期
        this.bind.clickForCycle = cycle;
        // 选择框
        const target = e.currentTarget;
        const divWidth = target.offsetWidth; // 获取 div 元素的宽度
        const divHeight = target.offsetHeight; // 获取 div 元素的高度
        const containerRect = this.dom.querySelector("#scheduleContainer").getBoundingClientRect(); // 获取上级元素(container)的位置信息
        const divRect = target.getBoundingClientRect(); // 获取 div 元素的位置信息
        const offsetX = divRect.left - containerRect.left; // 计算 div 元素相对于上级元素的横向偏移
        const offsetY = divRect.top - containerRect.top; // 计算 div 元素相对于上级元素的纵向偏移
        this._openChooseBox(divWidth, divHeight, offsetX, offsetY);
    },
    // 打开下拉选择班次的框
    _openChooseBox(divWidth, divHeight, offsetX, offsetY) {
        const selectedBox = this.dom.querySelector("#selectedFrame");
        selectedBox.style.width = divWidth + "px";
        selectedBox.style.height = divHeight + "px";
        selectedBox.style.left = offsetX + "px";
        selectedBox.style.top = offsetY + "px";
        selectedBox.style.display = "";
        const tPopup = this.dom.querySelector(".t-popup");
        if (offsetX < 500) {
            tPopup.style.inset = "0px auto auto 0px ";
            tPopup.style.transform = "translate3d(" + divWidth + "px, 5px, 0px)";
        } else {
            tPopup.style.inset = "0px 0px auto auto";
            tPopup.style.transform = "translate3d(-" + divWidth + "px, 5px, 0px)";
        }
        this.hideEvent = this._chooseBoxOutHide.bind(this);
        this.dom.addEventListener('mousedown', this.hideEvent);
    },
    // 关闭下拉选择班次的框
    _closeChooseBox() {
        const selectedBox = this.dom.querySelector("#selectedFrame");
        selectedBox.style.display = "none";
        this.dom.removeEventListener('mousedown', this.hideEvent);
    },
    // 鼠标点击外部关闭
    _chooseBoxOutHide(e) {
        // 计算下拉框的范围，范围外点击隐藏
        const targetElement = this.dom.querySelector("#selectedFrame");
        var clickedElement = e.target;
        var isInsideTarget = false;
        // 从点击的目标元素开始向上遍历DOM层级
        while (clickedElement) {
            if (clickedElement === targetElement) {
                isInsideTarget = true;
                break;
            }
            clickedElement = clickedElement.parentNode;
        }
        if (!isInsideTarget) {
            this._closeChooseBox();
        }
    },
    // 选择班次
    chooseShiftOnDate(shift) {
        debugger;
        if (this.bind.clickForCycle) { // 班次周期
            this.bind.shiftCycleList.push(shift);
        } else {
            this.putInScheduleList(shift, this.bind.clickDate, this.bind.clickPerson);
        }
        this._closeChooseBox();
    },
    // 放入排班列表
    putInScheduleList(shift, date, person) {
        let exist = false;
        for (let index = 0; index < this.bind.scheduleList.length; index++) {
            const element = this.bind.scheduleList[index];
            if (element.scheduleDateString === date && element.userId === person) {
                element.shift = shift;
                this.bind.scheduleList[index] = element;
                exist = true;
            }
        }
        if (!exist) {
            this.bind.scheduleList.push({
                scheduleDateString: date,
                userId: person,
                shift: shift,
                shiftId: shift.id
            });
        }
    },
    // 按照排班周期排班
    scheduleByCycle() {
        if (this.bind.clickDate && this.bind.shiftCycleList.length > 0) {
            const date = new Date(this.bind.clickDate);
            const dateList = this._getMonthDates(date);
            const cycleList = this.bind.shiftCycleList;
            let cycleIndex = 0;
            for (let index = 0; index < dateList.length; index++) {
                const element = dateList[index];
                const forDate = formatDate(element);
                const shift = cycleList[cycleIndex];
                const scheduleIndex = this.bind.scheduleList.findIndex((item) => item.scheduleDateString === forDate && item.userId === this.bind.clickPerson);
                if (shift.id === "rest") {
                    if (scheduleIndex > -1) {
                        this.bind.scheduleList.splice(scheduleIndex, 1);
                    }
                } else {
                    if (scheduleIndex > -1) {
                        this.bind.scheduleList[scheduleIndex].shift = shift;
                        this.bind.scheduleList[scheduleIndex].shiftId = shift.id;
                    } else {
                        this.bind.scheduleList.push({
                            scheduleDateString: forDate,
                            userId: this.bind.clickPerson,
                            shift: shift,
                            shiftId: shift.id
                        });
                    }
                }

                if (cycleIndex >= cycleList.length - 1) {
                    cycleIndex = 0;
                } else {
                    cycleIndex++;
                }
            }
        }
        this._closeChooseBox();
    },
    // date 日期到所在月最后一天的所有日期数据
    _getMonthDates(date) {
        // 获取给定日期的年份和月份
        const year = date.getFullYear();
        const month = date.getMonth();
        // 创建一个新的Date对象，将日期设置为下个月的第一天
        const nextMonth = new Date(year, month + 1, 1);
        // 使用循环生成该月的所有日期数据
        const dates = [];
        let currentDate = date;
        while (currentDate < nextMonth) {
            dates.push(new Date(currentDate));
            currentDate.setDate(currentDate.getDate() + 1);
        }
        return dates;
    },
    // 清除班次 或者 班次周期中的休息
    clearShiftOnDate() {
        if (this.bind.clickForCycle) { // 班次周期
            this.bind.shiftCycleList.push({
                id: "rest",
                shiftName: lp.scheduleForm.restShift
            });
        } else { // 清除班次
            this.deleteScheduleListByDatePerson(this.bind.clickDate, this.bind.clickPerson)
        }
        this._closeChooseBox();
    },
    // 删除排班列表中的一条数据
    deleteScheduleListByDatePerson(date, person) {
        const existingItemIndex = this.bind.scheduleList.findIndex(item => item.scheduleDateString === date && item.userId === person);
        if (existingItemIndex > -1) {
            this.bind.scheduleList.splice(existingItemIndex, 1);
        }
    },
    // 删除排班周期的一条数据
    deleteCycleShift(shift) {
        if (!shift) {
            return;
        }
        const existingItemIndex = this.bind.shiftCycleList.findIndex(item => item.id === shift.id);
        if (existingItemIndex > -1) {
            this.bind.shiftCycleList.splice(existingItemIndex, 1);
        }
    },
    // 下载排班的 excel模板
    async downloadScheduleTemplate() {
        if (this.bind.shiftSelector.shiftSelected.length === 0) {
            o2.api.page.notice(lp.scheduleForm.downloadExcelTempEmptyShift, 'error');
            return;
        }
        if (this.bind.trueParticipantList.length === 0) {
            console.error("没有参与人");
            return;
        }
        // 开始创建 excel 文件
        const _shifts = this.bind.shiftSelector.shiftSelected.map((item) => item.shiftName) // 班次列表
        const people = this.bind.trueParticipantList.map((item) => formatPersonName(item)) // 参与人列表
        const monthDayNum = this.bind.dateList.length + 1 // 月份天数 第一列是姓名
        _shifts.push(lp.scheduleForm.excelEmptyShift)
        const _workbook = new ExcelJS.Workbook()
        // 添加选择班次用的工作表
        const _sheet2 = _workbook.addWorksheet('sheet2')
        _sheet2.addRow(_shifts)
        _sheet2.state = 'hidden' // 隐藏，不需要显示
        // 根据班次数量生成对应的列标识
        const _sheet2ColumnNames = generateExcelColumnNames(_shifts.length)
        // 这个是下拉选择班次的列表数据表达式
        const formulae = 'sheet2!$' + _sheet2ColumnNames[0] + '$1:$' + _sheet2ColumnNames[_sheet2ColumnNames.length - 1] + '$1'
        console.log(formulae)
        // 用户操作的工作表
        const _sheet1 = _workbook.addWorksheet('sheet1')
        // 根据这个月的日期数量生成对应的列标识
        const columnNames = generateExcelColumnNames(monthDayNum);
        console.log(columnNames)
        //第一行 表头 合并居中 填充颜色
        _sheet1.addRow([lpFormat(lp, 'scheduleForm.excelTitle', {month: this.bind.month})])
        _sheet1.mergeCells('A1:' + columnNames[columnNames.length - 1] + '1')
        _sheet1.getCell('A1').alignment = {vertical: 'middle', horizontal: 'center'}
        _sheet1.getCell('A1').fill = {
            type: 'pattern',
            pattern: 'solid',
            fgColor: {argb: 'FFCCFFFF'}
        }
        // 第二行 日期
        let header1 = [] // 放星期
        let header2 = [] // 放日期
        for (let i = 0; i < monthDayNum; i++) {
            if (i === 0) {
                header1.push(lp.detailTable.person)
                header2.push("")
            } else {
                const _date = this.bind.dateList[i - 1]
                header1.push(_date.day)
                header2.push(_date.text)
            }
        }
        _sheet1.addRow(header1)
        _sheet1.addRow(header2)
        // 填充颜色
        const headerFill = {
            type: 'pattern',
            pattern: 'solid',
            fgColor: {argb: 'FFCCFFCC'}
        }
        for (let i = 0; i < columnNames.length; i++) {
            _sheet1.getCell(columnNames[i] + '2').fill = headerFill
            _sheet1.getCell(columnNames[i] + '3').fill = headerFill
        }
        // A2A3 放姓名字段  合并居中
        _sheet1.mergeCells('A2:A3')
        _sheet1.getCell('A2').alignment = {vertical: 'middle', horizontal: 'center'}

        // 第四行开始 后面的行 根据人员添加行
        for (let i = 0; i < people.length; i++) {
            _sheet1.addRow([people[i]])
        }
        //第一列是人员名称 从 1 开始 给对应的单元格添加班次下拉数据
        for (let i = 1; i < columnNames.length; i++) {
            const column = columnNames[i]
            for (let j = 0; j < people.length; j++) {
                const cellName = column + (j + 4) // 第四行开始才是人员班次选择
                _sheet1.getCell(cellName).dataValidation = {
                    type: 'list',
                    allowBlank: true,
                    formulae: [formulae]
                }
            }
        }
        // 给所有单元格添加边框
        const border = {
            top: {style: 'thin'},
            left: {style: 'thin'},
            bottom: {style: 'thin'},
            right: {style: 'thin'}
        }
        _sheet1.eachRow(function (row, rowNumber) {
            row.eachCell({includeEmpty: true}, function (cell, colNumber) {
                cell.border = border
            });
            row.commit();
        });
        // 下载文件
        const buffer = await _workbook.xlsx.writeBuffer()
        const excelLink = document.createElement('a')
        excelLink.href = window.URL.createObjectURL(new Blob([buffer]))
        excelLink.download = lpFormat(lp, 'scheduleForm.excelFileName', {month: this.bind.month})
        excelLink.click()
    },
    // 从 excel 文件读取数据
    async excelJsReadFile(file) {
        const workbook = new ExcelJS.Workbook();  // new一下
        await workbook.xlsx.load(file);  // 读文件
        const worksheet = workbook.getWorksheet('sheet1');  // 读sheet1 跟上面的导出模板一致
        const monthDayNum = this.bind.dateList.length + 1 // 月份天数 第一列是姓名
        const columnNames = generateExcelColumnNames(monthDayNum);
        this.bind.trueParticipantList.forEach((participant, j) => {
            const rowNum = 4 + j
            columnNames.forEach((columnName, i) => {
                if (i !== 0) {
                    const cellValue = worksheet.getCell(columnName + rowNum).value
                    let shift = null
                    const date = this.bind.dateList[i - 1]
                    if (cellValue && cellValue !== "") {
                        shift = this.bind.shiftSelector.shiftSelected.find(item => item.shiftName === cellValue)
                    }
                    if (shift) {
                        this.putInScheduleList(shift, date.dateString, participant)
                    } else {
                        this.deleteScheduleListByDatePerson(date.dateString, participant)
                    }
                }
            })
        })
    },
    // 上传排班好的 excel模板 把数据导入到系统中
    uploadScheduleTemplate() {
        if (this.bind.shiftSelector.shiftSelected.length === 0) {
            o2.api.page.notice(lp.scheduleForm.downloadExcelTempEmptyShift, 'error');
            return;
        }
        chooseSingleFile((file) => {
            // 读取文件
            const reader = new FileReader();
            reader.onloadend = () => {
                const arrayBuffer = reader.result
                this.excelJsReadFile(arrayBuffer)
            }
            reader.readAsArrayBuffer(file)
        })
    },
});
