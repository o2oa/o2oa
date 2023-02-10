import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import style from "./style.scope.css";
import template from "./temp.html";

export default content({
  style,
  template,
  autoUpdate: true,
  bind() {
    return {
      lp,
      pageList: ["1"],
      pagerData: {
        // 父组件通过oo-prop传入这个对象 主要是需要传入 size，totalCount
        page: 1, //页码
        totalPage: 1,
        totalCount: 0,
        size: 15, // 条目数
      },
    };
  },
  afterRender() {
    // todo 初始化数据
    this.numberCalc();
  },
  loadDataEvent() {
    this.component.dispatchEvent("refresh-data");
  },
  // 计算用，缺少一个计算
  numberCalc() {
    let totalPage = parseInt(
      this.bind.pagerData.totalCount / this.bind.pagerData.size
    );
    const m = this.bind.pagerData.totalCount % this.bind.pagerData.size;
    if (m > 0) {
      totalPage += 1;
    }
    if (totalPage == 0) {
      totalPage = 1; // 最少是1
    }
    this.bind.pagerData.totalPage = totalPage;
    let pageList = [];
    if (totalPage > 11) {
      // 最多11条
      if (this.bind.pagerData.page < 6) {
        for (let index = 0; index < 11; index++) {
          pageList.push(`${index + 1}`);
        }
      } else {
        const start = this.bind.pagerData.page - 5;
        let end = this.bind.pagerData.page + 5;
        if (end > totalPage) {
          end = totalPage;
        }
        for (let index = start; index < end - start + 1; index++) {
          pageList.push(`${index}`);
        }
      }
    } else {
      for (let index = 0; index < totalPage; index++) {
        pageList.push(`${index + 1}`);
      }
    }
    this.bind.pageList = pageList;
    return ""; // 没用
  },
  clickNumberPage(number) {
    this.bind.pagerData.page = parseInt(number);
    this.loadDataEvent();
  },
  clickPrePage() {
    if (this.bind.pagerData.page > 1) {
      this.bind.pagerData.page -= 1;
    }
    this.loadDataEvent();
  },
  clickNextPage() {
    if (this.bind.pagerData.page < this.bind.pagerData.totalPage) {
      this.bind.pagerData.page += 1;
    }
    this.loadDataEvent();
  },
  clickFirstPage() {
    this.bind.pagerData.page = 1;
    this.loadDataEvent();
  },
  clickLastPage() {
    this.bind.pagerData.page = this.bind.pagerData.totalPage;
    this.loadDataEvent();
  },
});
