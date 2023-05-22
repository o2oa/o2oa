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
      // pagerData: {
      //   // 父组件通过oo-prop传入这个对象 主要是需要传入 size，totalCount
      //   page: 1, //页码
      //   totalPage: 1,
      //   totalCount: content.watch(function() {
      //     console.debug("watch totalCount .....");
      //     console.debug(this.bind);
      //     this.numberCalc();
      //   },0),
      //   size: 15, // 条目数
      // },
    };
  },
  afterRender() {
    // todo 初始化数据
    this.numberCalc();
  },
  loadDataEvent() {
    this.component.dispatchEvent("refresh-data");
  },
  // 计算用，传入参数无用 刷新页面用
  numberCalc() {
    let totalPage = parseInt(
      this.bind.totalCount / this.bind.size
    );
    const m = this.bind.totalCount % this.bind.size;
    if (m > 0) {
      totalPage += 1;
    }
    if (totalPage == 0) {
      totalPage = 1; // 最少是1
    }
    this.bind.totalPage = totalPage;
    let pageList = [];
    if (totalPage > 11) {
      // 最多11条
      if (this.bind.page < 6) {
        for (let index = 0; index < 11; index++) {
          pageList.push(`${index + 1}`);
        }
      } else {
        const start = this.bind.page - 5;
        let end = this.bind.page + 5;
        if (end > totalPage) {
          end = totalPage;
        }
        for (let index = start; index < end + 1; index++) {
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
    this.bind.page = parseInt(number);
    this.loadDataEvent();
  },
  clickPrePage() {
    if (this.bind.page > 1) {
      this.bind.page -= 1;
    }
    this.loadDataEvent();
  },
  clickNextPage() {
    if (this.bind.page < this.bind.totalPage) {
      this.bind.page += 1;
    }
    this.loadDataEvent();
  },
  clickFirstPage() {
    this.bind.page = 1;
    this.loadDataEvent();
  },
  clickLastPage() {
    this.bind.page = this.bind.totalPage;
    this.loadDataEvent();
  },
});
