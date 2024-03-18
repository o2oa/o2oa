<template>
  <div class="systemconfig_area">
    <div v-if="appStyle">
      <BaseItem
        :title="lp._appConfig.mobileIndex"
        :info="lp._appConfig.mobileIndexInfo"
        :config="mobileIndex"
        :allowEditor="true"
        type="select"
        :options="portalList"
        @changeConfig="saveMobileIndex"
      ></BaseItem>

      <div class="item_title">{{ lp._appConfig.appIndexCenteredTitle }}</div>
      <div class="item_info" v-html="lp._appConfig.appIndexCenteredInfo"></div>
      <BaseBoolean
        v-model:value="appStyle.indexCentered"
        @change="
          (value) => {
            appStyle.indexCentered = value;
            saveAppStyle(appStyle);
          }
        "
      ></BaseBoolean>

      <div class="item_title" v-if="appStyle.indexCentered == false">
        {{ lp._appConfig.appIndexPage }}
      </div>
      <div
        class="item_info"
        v-if="appStyle.indexCentered == false"
        v-html="lp._appConfig.appIndexPageInfo"
      ></div>
      <div class="item_input_area" v-if="appStyle.indexCentered == false">
        <el-checkbox v-model="appIndexPagesCheckValues.home" disabled>{{
          lp._appConfig.appIndexPageHome
        }}</el-checkbox
        ><br />
        <el-checkbox
          @change="saveAppIndexPagesCheckValues"
          v-model="appIndexPagesCheckValues.im"
          >{{ lp._appConfig.appIndexPageIM }}</el-checkbox
        ><br />
        <el-checkbox
          @change="saveAppIndexPagesCheckValues"
          v-model="appIndexPagesCheckValues.contact"
          >{{ lp._appConfig.appIndexPageContact }}</el-checkbox
        ><br />
        <el-checkbox
          @change="saveAppIndexPagesCheckValues"
          v-model="appIndexPagesCheckValues.app"
          >{{ lp._appConfig.appIndexPageApp }}</el-checkbox
        ><br />
        <el-checkbox v-model="appIndexPagesCheckValues.settings" disabled>{{
          lp._appConfig.appIndexPageSettings
        }}</el-checkbox>
      </div>

      <!-- 首页列表过滤 -->
      <div class="item_title">{{ lp._appConfig.appIndexCmsFilterTitle }}</div>
      <div
        class="item_info"
        v-html="lp._appConfig.appIndexCmsFilterCategoryInfo"
      ></div>
      <div class="item_info">
        <div class="item_value mainColor_color" style="height: auto">
          {{ categroyListText }}
        </div>
        <button class="mainColor_bg" @click="chooseCategory()">
          {{ lp._appConfig.appIndexCmsFilterCategroySelectorTitle }}
        </button>
      </div>

      <div class="item_title">{{ lp._appConfig.appIndexTaskFilterTitle }}</div>
      <div
        class="item_info"
        v-html="lp._appConfig.appIndexTaskFilterProcessInfo"
      ></div>
      <div class="item_info">
        <div class="item_value mainColor_color" style="height: auto">
          {{ processListText }}
        </div>
        <button class="mainColor_bg" @click="chooseProcess()">
          {{ lp._appConfig.appIndexTaskFilterProcessSelectorTitle }}
        </button>
      </div>

      <div class="item_title">{{ lp._appConfig.systemMessageSwitch }}</div>
      <div
        class="item_info"
        v-html="lp._appConfig.systemMessageSwitchInfo"
      ></div>
      <BaseBoolean
        v-model:value="appStyle.systemMessageSwitch"
        @change="
          (value) => {
            appStyle.systemMessageSwitch = value;
            saveAppStyle(appStyle);
          }
        "
      ></BaseBoolean>
      <div v-if="appStyle.systemMessageSwitch == true">
        <div
          class="item_info"
          v-html="lp._appConfig.systemMessageCanClickInfo"
        ></div>
        <BaseBoolean
          v-model:value="appStyle.systemMessageCanClick"
          @change="
            (value) => {
              appStyle.systemMessageCanClick = value;
              saveAppStyle(appStyle);
            }
          "
        ></BaseBoolean>
      </div>

      <!-- <BaseItem
          :title="lp._appConfig.appExitAlert"
          :info="lp._appConfig.appExitAlertInfo"
          :config="appStyle.appExitAlert"
          :allowEditor="true"
          @changeConfig="(value)=>{appStyle.appExitAlert = value; saveAppStyle(appStyle)}"
      ></BaseItem>

      <BaseItem
          :title="lp._appConfig.contactPermissionView"
          :info="lp._appConfig.contactPermissionViewInfo"
          :config="appStyle.contactPermissionView"
          :allowEditor="true"
          @changeConfig="(value)=>{appStyle.contactPermissionView = value; saveAppStyle(appStyle)}"
      ></BaseItem> -->

      <div class="item_title">{{ lp._appConfig.nativeAppList }}</div>
      <div class="item_info" v-html="lp._appConfig.nativeAppListInfo"></div>
      <div v-for="m in appStyle.nativeAppList">
        <div class="native_app">
          <BaseBoolean
            :label="m.name"
            v-model:value="m.enable"
            @change="
              (value) => {
                m.enable = value;
                saveAppStyle(appStyle);
              }
            "
            :label-style="{ fontWeight: 'bold' }"
          ></BaseBoolean>

          <BaseItem
                :config="m.displayName"
                :allowEditor="true"
                @changeConfig="
                  (value) => {
                    m.displayName = value;
                    saveAppStyle(appStyle);
                  }
                "
              />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { lp } from "@o2oa/component";
import {
  getAppStyle,
  loadPortals,
  saveAppStyle,
  categoryInfoAction,
  processAction,
} from "@/util/acrions";
import BaseItem from "@/components/item/BaseItem.vue";
import BaseBoolean from "@/components/item/BaseBoolean";

const appStyle = ref();
const portalList = ref({});
const mobileIndex = computed(() => {
  return appStyle.value && appStyle.value.indexType === "portal"
    ? appStyle.value.indexPortal
    : "default";
});
const appIndexPagesCheckValues = ref({
  home: true,
  im: true,
  contact: true,
  app: true,
  settings: true,
});
const processFilterList = ref([]);
const categroyFilterList = ref([]);

const processListText = computed(() => {
  return `[${processFilterList.value.map((e) => e.name).join(",")}]`;
});
const categroyListText = computed(() => {
  return `[${categroyFilterList.value.map((e) => e.categoryAlias).join(",")}]`;
});

const saveAppIndexPagesCheckValues = () => {
  let pages = [];
  pages.push("home");
  if (appIndexPagesCheckValues.value.im) {
    pages.push("im");
  }
  if (appIndexPagesCheckValues.value.contact) {
    pages.push("contact");
  }
  if (appIndexPagesCheckValues.value.app) {
    pages.push("app");
  }
  pages.push("settings");
  appStyle.value.appIndexPages = pages;
  saveAppStyle(appStyle.value);
};

const saveMobileIndex = (v) => {
  if (v === "default") {
    appStyle.value.indexType = "default";
  } else {
    appStyle.value.indexType = "portal";
    appStyle.value.indexPortal = v;
  }
  saveAppStyle(appStyle.value);
};

// 流程选择
const chooseProcess = () => {
  MWF.requireApp(
    "Selector",
    "package",
    function () {
      // const ids = processFilterList.value.map((e)=>e.id)
      var options = {
        type: "process",
        count: 0,
        expand: false,
        title: lp._appConfig.appIndexTaskFilterProcessSelectorTitle,
        values: processFilterList.value,
        onComplete: function (items) {
          if (items) {
            let newValue = [];
            let newIds = [];
            items.forEach((element) => {
              if (element.data) {
                newValue.push(element.data);
                if (element.data.id) {
                  newIds.push(element.data.id);
                }
              }
            });
            processFilterList.value = newValue;
            appStyle.value.processFilterList = newIds;
            saveAppStyle(appStyle.value);
          }
        }.bind(this),
      };
      new MWF.O2Selector(document.body, options);
    }.bind(this)
  );
};
// 分类选择
const chooseCategory = () => {
  MWF.requireApp(
    "Selector",
    "package",
    function () {
      var options = {
        type: "CMSCategory",
        count: 0,
        expand: true,
        title: lp._appConfig.appIndexCmsFilterCategroySelectorTitle,
        values: categroyFilterList.value,
        onComplete: function (items) {
          if (items) {
            let newValue = [];
            let newIds = [];
            items.forEach((element) => {
              if (element.data) {
                newValue.push(element.data);
                if (element.data.id) {
                  newIds.push(element.data.id);
                }
              }
            });
            categroyFilterList.value = newValue;
            appStyle.value.cmsCategoryFilterList = newIds;
            saveAppStyle(appStyle.value);
          }
        }.bind(this),
      };
      new MWF.O2Selector(document.body, options);
    }.bind(this)
  );
};
//
const loadProcessList = () => {
  const processIds = appStyle.value.processFilterList || [];
  if (processIds) {
    const body = {
      processList: processIds,
    };
    processAction("ListWithIds", body).then((data) => {
      processFilterList.value = data || [];
    });
  }
};
const loadCategoryList = () => {
  const ids = appStyle.value.cmsCategoryFilterList || [];
  if (ids) {
    const body = {
      categoryIdList: ids,
    };
    categoryInfoAction("listObjectsByIds", body).then((data) => {
      const list = data || [];
      // 选择器必须有 name
      categroyFilterList.value = list.map((e) => {
        e.name = e.categoryName;
        return e;
      });
    });
  }
};

const load = () => {
  getAppStyle().then((data) => {
    appStyle.value = data;
    //     {
    //   indexPortal: data.indexPortal,
    //   indexType: data.indexType,
    //   nativeAppList: data.nativeAppList,
    //   simpleMode: data.simpleMode,
    //   systemMessageSwitch: data.systemMessageSwitch,
    //   indexCentered: data.indexCentered,
    //   systemMessageCanClick: data.systemMessageCanClick,
    //   appExitAlert: data.appExitAlert,
    //   contactPermissionView: data.contactPermissionView,
    //   processFilterList: data.processFilterList,
    //   cmsCategoryFilterList: data.cmsCategoryFilterList,
    // };
    if (data.appIndexPages && data.appIndexPages.length > 0) {
      appIndexPagesCheckValues.value.im = data.appIndexPages.indexOf("im") > -1;
      appIndexPagesCheckValues.value.contact =
        data.appIndexPages.indexOf("contact") > -1;
      appIndexPagesCheckValues.value.app =
        data.appIndexPages.indexOf("app") > -1;
    }
    loadProcessList();
    loadCategoryList();
  });
  loadPortals().then((data) => {
    const o = { default: lp.default };
    data.forEach((d) => {
      o[d.id] = d.name;
    });
    portalList.value = o;
  });
};
load();
</script>

<style scoped>
.item_input_area {
  padding: 0 10px;
  font-size: 14px;
  margin-right: 20px;
  margin-left: 80px;
}
.native_app {
  display: flex;
  flex-direction: row;
}
</style>
