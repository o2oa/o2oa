<template>
  <!-- 微信菜单管理 -->
  <div class="systemconfig_area">
    <div
      class="systemconfig_item_info"
      v-html="lp._appTools.mpMenu.mpweixinInfo"
    ></div>
    <div class="menu_setting_area" ref="mpwxMenuNode">
      <!-- <div style="margin: 30px 0 30px 0;font-size:16px;font-weight: 400;">{{lp._appTools.mpMenu.mpweixinInfo}}</div> -->
      <!-- 编辑区 -->
      <div class="menu_preview_area">
        <div class="mobile_menu_preview">
          <div class="mobile_hd tc">{{ lp._appTools.mpMenu.mpweixin }}</div>
          <div class="mobile_bd">
            <ul class="pre_menu_list grid_line" ref="mpwxMenuListNode">
              <!-- 菜单展现 -->
              <li
                v-for="item in menuList"
                :key="item.id"
                :class="firstMenuClass(item)"
                @click="clickFirstMenu($event, item)"
              >
                <a class="pre_menu_link" href="javascript:void(0);"
                  ><span class="js_l1Title">{{ item.name }}</span></a
                >
                <!-- 子菜单 -->
                <div
                  class="sub_pre_menu_box js_l2TitleBox"
                  v-if="showSubId == item.id"
                >
                  <ul class="sub_pre_menu_list">
                    <!-- 新增按钮 -->
                    <li
                      class="js_addMenuBox"
                      @click="createSubMenu($event, item)"
                      v-if="showSubCreateMenuBtn(item)"
                    >
                      <a
                        class="jsSubView js_addL2Btn"
                        href="javascript:void(0);"
                      >
                        <span class="sub_pre_menu_inner js_sub_pre_menu_inner"
                          ><i class="icon14_menu_add"></i></span
                      ></a>
                    </li>
                    <li
                      v-for="subItem in item.sub_button"
                      :key="subItem.id"
                      :class="subMenuClass(subItem)"
                      @click="clickSubMenu($event, item.id, subItem)"
                    >
                      <a class="jsSubView" href="javascript:void(0);">
                        <span class="sub_pre_menu_inner js_sub_pre_menu_inner">
                          <i class="icon20_common sort_gray"></i>
                          <span class="js_l2Title">{{ subItem.name }}</span>
                        </span>
                      </a>
                    </li>
                  </ul>
                  <i class="arrow arrow_out" v-if="showSubId == item.id"></i>
                  <i class="arrow arrow_in" v-if="showSubId == item.id"></i>
                </div>
              </li>

              <!-- 第一层新增按钮 -->
              <li
                class="js_addMenuBox pre_menu_item grid_item no_extra"
                @click="createNewFirstMenuMock"
                v-if="menuList.length < 3"
              >
                <a class="pre_menu_link js_addL1Btn" href="javascript:void(0);">
                  <i class="icon14_menu_add"></i>
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div class="menu_form_area">
        <div class="portable_editor to_left">
          <div class="editor_inner">
            <div
              class="global_mod float_layout menu_form_hd js_second_title_bar"
              ref="mpwxMenuFormHeaderNode"
            >
              <!-- 菜单名称 和 删除按钮 -->
              <h4 class="global_info">{{ currentMenu.name || "" }}</h4>
              <button
              style="float:right;"
                class="mainColor_bg"
                @click="clickDeleteMenu($event)"
                v-if="currentMenu.id"
              >
                {{ lp._appTools.mpMenu.deleteMenuBtnTitle }}
              </button>
            </div>
            <div class="menu_form_bd" ref="mpwxMenuFormNode">
              <!-- 菜单表单内容 -->
              <!-- 名称 -->
              <div class="frm_control_group js_setNameBox">
                <label class="frm_label"
                  ><strong class="title js_menuTitle">{{
                    lp._appTools.mpMenu.formNameLabel
                  }}</strong></label
                >
                <div class="frm_controls">
                  <span class="frm_input_box with_counter counter_in append">
                    <input
                      type="text"
                      class="frm_input js_menu_name"
                      v-model="currentMenu.name"
                    />
                  </span>
                  <p class="frm_tips js_titleNolTips">{{ inputNameTips() }}</p>
                </div>
              </div>
              <!-- 排序 -->
              <div class="frm_control_group js_setNameBox">
                <label class="frm_label"
                  ><strong class="title js_menuTitle">{{
                    lp._appTools.mpMenu.formOrderLabel
                  }}</strong></label
                >
                <div class="frm_controls">
                  <span class="frm_input_box with_counter counter_in append">
                    <input
                      type="text"
                      class="frm_input js_menu_name"
                      v-model="currentMenu.order"
                    />
                  </span>
                  <p class="frm_tips js_titleNolTips">
                    {{ lp._appTools.mpMenu.formOrderTips }}
                  </p>
                </div>
              </div>
              <!-- 菜单类型 -->
              <div class="frm_control_group">
                <label class="frm_label"
                  ><strong class="title js_menuContent">{{
                    lp._appTools.mpMenu.formRadioLabel
                  }}</strong></label
                >
                <div class="frm_controls frm_vertical_pt">
                  <label class="frm_radio_label js_radio_sendMsg">
                    <i class="icon_radio"></i>
                    <span class="lbl_content">{{
                      lp._appTools.mpMenu.formRadioTypeMsg
                    }}</span>
                    <input
                      type="radio"
                      class="frm_radio"
                      value="click"
                      v-model="currentMenu.type"
                    />
                  </label>
                  <label class="frm_radio_label js_radio_sendMsg">
                    <i class="icon_radio"></i>
                    <span class="lbl_content">{{
                      lp._appTools.mpMenu.formRadioTypeUrl
                    }}</span>
                    <input
                      type="radio"
                      class="frm_radio"
                      value="view"
                      v-model="currentMenu.type"
                    />
                  </label>
                  <label class="frm_radio_label js_radio_sendMsg">
                    <i class="icon_radio"></i>
                    <span class="lbl_content">{{
                      lp._appTools.mpMenu.formRadioTypeMiniprogram
                    }}</span>
                    <input
                      type="radio"
                      class="frm_radio"
                      value="miniprogram"
                      v-model="currentMenu.type"
                    />
                  </label>
                </div>
              </div>
              <!-- 类型不同显示的具体内容不同 -->
              <div class="menu_content_container">
                <!-- click -->
                <div class="menu_content" v-if="currentMenu.type == 'click'">
                  <p class="menu_content_tips tips_global js_url_tips">
                    {{ lp._appTools.mpMenu.formTypeMsgTips }}
                  </p>
                  <div class="frm_control_group js_setNameBox">
                    <label class="frm_label">{{
                      lp._appTools.mpMenu.formTypeMsgLabel
                    }}</label>
                    <div class="frm_controls">
                      <span
                        class="frm_textarea_box with_counter counter_in append"
                      >
                        <textarea
                          class="frm_textarea js_menu_name"
                          v-model="currentMenu.content"
                        ></textarea>
                      </span>
                    </div>
                  </div>
                </div>
                <!-- view -->
                <div class="menu_content" v-if="currentMenu.type == 'view'">
                  <p class="menu_content_tips tips_global js_url_tips">
                    {{ lp._appTools.mpMenu.formTypeUrlTips }}
                  </p>
                  <div class="frm_control_group js_setNameBox">
                    <label class="frm_label">{{
                      lp._appTools.mpMenu.formTypeUrlLabel
                    }}</label>
                    <div class="frm_controls">
                      <span
                        class="frm_textarea_box with_counter counter_in append"
                      >
                        <input
                          type="text"
                          class="frm_input js_menu_name"
                          v-model="currentMenu.url"
                        />
                      </span>
                    </div>
                  </div>
                </div>
                <!-- 小程序 -->
                <div
                  class="menu_content"
                  v-if="currentMenu.type == 'miniprogram'"
                >
                  <p class="menu_content_tips tips_global js_url_tips">
                    {{ lp._appTools.mpMenu.formTypeMiniprogramTips }}
                  </p>
                  <div class="frm_control_group js_setNameBox">
                    <label class="frm_label">{{
                      lp._appTools.mpMenu.formTypeMiniprogramAppidLabel
                    }}</label>
                    <div class="frm_controls">
                      <span
                        class="frm_textarea_box with_counter counter_in append"
                      >
                        <input
                          type="text"
                          class="frm_input js_menu_name"
                          v-model="currentMenu.appid"
                        />
                      </span>
                    </div>
                  </div>
                  <div class="frm_control_group js_setNameBox">
                    <label class="frm_label">{{
                      lp._appTools.mpMenu.formTypeMiniprogramPathLabel
                    }}</label>
                    <div class="frm_controls">
                      <span
                        class="frm_textarea_box with_counter counter_in append"
                      >
                        <input
                          type="text"
                          class="frm_input js_menu_name"
                          v-model="currentMenu.pagepath"
                        />
                      </span>
                    </div>
                  </div>
                  <div class="frm_control_group js_setNameBox">
                    <label class="frm_label">{{
                      lp._appTools.mpMenu.formTypeMiniprogramUrlLabel
                    }}</label>
                    <div class="frm_controls">
                      <span
                        class="frm_textarea_box with_counter counter_in append"
                      >
                        <input
                          type="text"
                          class="frm_input js_menu_name"
                          v-model="currentMenu.url"
                        />
                      </span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="btn-box">
                <button class="mainColor_bg" @click="saveMenu">
                  {{ lp._appTools.mpMenu.subscribeMpweixin_save }}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 发布按钮 -->
      <div class="btn-box" style="justify-content: start; margin-top: 30px">
        <button class="mainColor_bg" @click="menuPublishToWeixin($event)">
          {{ lp._appTools.mpMenu.publishMpweixin }}
        </button>
        <!-- <span class="btn btn_input btn_primary" ref="mpwxPublishBtnNode"><button>{{lp._appTools.mpMenu.publishMpweixin}}</button></span> -->
      </div>

      <!-- 关注回复 -->
      <div
        style="
          height: 40px;
          padding: 20px 0;
          font-size: 24px;
          color: rgb(51, 51, 51);
          font-weight: bold;
          line-height: 40px;
        "
      >
        2. {{ lp._appTools.mpMenu.subscribeMpweixin }}
      </div>
      <div
        style="
          overflow: hidden;
          padding: 5px 0;
          font-size: 14px;
          color: rgb(153, 153, 153);
          clear: both;
        "
      >
        {{ lp._appTools.mpMenu.subscribeMpweixin_desc }}
      </div>
      <div class="frm_controls">
        <span class="frm_textarea_box with_counter counter_in append">
          <textarea
            class="frm_textarea js_menu_name"
            v-model="mpweixinSubscribe.content"
          >
          </textarea>
        </span>
      </div>
      <div class="btn-box" style="justify-content: start; margin-top: 30px">
        <button class="mainColor_bg" @click="saveSubscribe($event)">
          {{ lp._appTools.mpMenu.subscribeMpweixin_save }}
        </button>
        <!-- <span class="btn btn_input btn_primary" ref="mpwxSaveSubscribeBtnNode"><button>{{lp._appTools.mpMenu.subscribeMpweixin_save}}</button></span> -->
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { component, lp, o2 } from "@o2oa/component";
import { doMPWeixinMenuAction } from "@/util/acrions";

const menuList = ref([]); // 菜单列表
const showSubId = ref(""); // 哪个菜单的子菜单需要显示
const mpweixinSubscribe = ref({}); // 订阅
const currentMenu = ref({}); // 选中的菜单

const load = () => {
  loadMenuList();
  loadSubscribeInfo();
};

const loadMenuList = () => {
  doMPWeixinMenuAction("menuWeixinList").then((data) => {
    let menus = data.button || [];
    //去除 subscribe
    let subscribe = null;
    for (let index = 0; index < menus.length; index++) {
      const element = menus[index];
      if (element.type && element.type === "subscribe") {
        subscribe = element;
      }
    }
    if (subscribe) {
      menus.erase(subscribe);
    }
    menuList.value = menus;
    if (menus.length > 0) {
      if (showSubId.value === "") {
        showSubId.value = menus[0].id; //初始化 showSubId 表示哪个子菜单是显示的
      }
      if (!currentMenu.value.id || currentMenu.value.id === "") {
        currentMenu.value = menus[0]; //初始化 currentMenuId 表示当前选中的菜单ID
      }
    }
  });
};
const loadSubscribeInfo = () => {
  doMPWeixinMenuAction("menuWeixinSubscribe").then((data) => {
    if (data && data.name && data.name !== "") {
      mpweixinSubscribe.value = data;
    } else {
      mpweixinSubscribe.value = {
                    id: "mock_new",
                    name: "subscribe",
                    type: "subscribe",
                    content: ""
                };
    }
    
  });
};

const firstMenuClass = (menu) => {
  let menuClass =
    "jsMenu pre_menu_item grid_item jslevel1 ui-sortable ui-sortable-disabled size1of3";
  if (
    menu &&
    menu.id &&
    currentMenu.value.id &&
    currentMenu.value.id === menu.id
  ) {
    menuClass =
      "jsMenu pre_menu_item grid_item jslevel1 ui-sortable ui-sortable-disabled size1of3 current";
  }
  return menuClass;
};
const subMenuClass = (menu) => {
  let menuClass = "jslevel2";
  if (
    menu &&
    menu.id &&
    currentMenu.value.id &&
    currentMenu.value.id === menu.id
  ) {
    menuClass = "jslevel2 current";
  }
  return menuClass;
};
const inputNameTips = () => {
  let nameTips = lp._appTools.mpMenu.formNameTips4;
  let isFirstLevel = false;
  for (let index = 0; index < menuList.value.length; index++) {
    const element = menuList.value[index];
    if (currentMenu.value.id && currentMenu.value.id === element.id) {
      isFirstLevel = true;
      break;
    }
  }
  if (isFirstLevel) {
    nameTips = lp._appTools.mpMenu.formNameTips4;
  } else {
    nameTips = lp._appTools.mpMenu.formNameTips6;
  }
  return nameTips;
};
// 第一层新增按钮
const createNewFirstMenuMock = () => {
  //最多可创建3个
  if (menuList.value.length >= 3) {
    component.notice(lp._appTools.mpMenu.msgFirstMaxLen, "error");
    return;
  }
  // 排序号生成
  let order = "000001";
  if (menuList.value.length > 0) {
    let bigestOrder = menuList.value[menuList.value.length - 1].order; //最后一条是最大的号码
    console.log("最大的order " + bigestOrder);
    const intOrder = parseInt(bigestOrder);
    if (!isNaN(intOrder)) {
      let o = intOrder + 1;
      let os = "" + o;
      for (let len = os.length; len < 6; len = os.length) {
        os = "0" + os;
      }
      order = os;
    }
    console.log("最大的order " + intOrder);
  }
  // id mock_开头 正式保存的时候知道是新增的 可以清除 后台生成id
  let body = {
    id: "mock_" + o2.uuid(),
    name: lp._appTools.mpMenu.defaultNewName,
    type: "click", //默认文字消息
    order: order,
    o2Level: "1", // 1第一层级菜单
  };
  menuList.value.push(body);
  currentMenu.value = body; // 当前菜单
  showSubId.value = body.id; //
};
// 点击第一层菜单
const clickFirstMenu = (ev, menu) => {
  currentMenu.value = menu; // 当前菜单
  showSubId.value = menu.id; //
  ev.stopPropagation();
};
// 点击子菜单
const clickSubMenu = (ev, parentId, menu) => {
  currentMenu.value = menu; // 当前菜单
  showSubId.value = parentId; //
  ev.stopPropagation();
};
// 子菜单是否显示新增按钮
const showSubCreateMenuBtn = (menu) => {
  return !menu.sub_button || menu.sub_button.length < 5;
};
// 子菜单新增按钮点击
const createSubMenu = (ev, parentMenu) => {
  console.debug("点击了新增，属于 " + parentMenu.name);
  ev.stopPropagation();
  let sub = parentMenu.sub_button;
  //最多可创建5个
  if (sub && sub.length >= 5) {
    component.notice(lp._appTools.mpMenu.menuMsgSubMaxLen, "error");
    return;
  }
  if (parentMenu.id.startsWith("mock_")) {
    component.notice(lp._appTools.mpMenu.menuMsgParentNotSave, "error");
    return;
  }
  // 排序号生成
  let order = "000001";
  if (sub && sub.length > 0) {
    let bigestOrder = sub[0].order; //第一条 因为上面已经排序过了 所以第一条是最大的
    const intOrder = parseInt(bigestOrder);
    if (!isNaN(intOrder)) {
      let o = intOrder + 1;
      let os = "" + o;
      for (let len = os.length; len < 6; len = os.length) {
        os = "0" + os;
      }
      order = os;
    }
  }
  // id mock_开头 正式保存的时候知道是新增的 可以清除 后台生成id
  let body = {
    id: "mock_" + o2.uuid(),
    name: lp._appTools.mpMenu.defaultNewName,
    parentId: parentMenu.id,
    type: "click", //默认文字消息
    order: order,
    o2Level: "2", // 2第二层级菜单
  };
  if (sub) {
    parentMenu.sub_button.push(body);
  } else {
    parentMenu.sub_button = [body];
  }
  for (let index = 0; index < menuList.value.length; index++) {
    const element = menuList.value[index];
    if (element.id === parentMenu.id) {
      menuList.value[index] = parentMenu;
    }
  }
  currentMenu.value = body; // 当前菜单
  showSubId.value = parentMenu.id; //
};
const removeMenuData = (menu) => {
  if (menuList.value.length > 0) {
    for (let index = 0; index < menuList.value.length; index++) {
      const element = menuList.value[index];
      if (element.id === menu.id) {
        menuList.value.erase(menu);
        console.log("删除成功。。。。。上级");
        break;
      }
      var flag = false;
      if (element.sub_button && element.sub_button.length > 0) {
        for (let i = 0; i < element.sub_button.length; i++) {
          const child = element.sub_button[i];
          if (child.id === menu.id) {
            flag = true;
            element.sub_button.erase(menu);
            console.log("删除成功。。。。子级。");
          }
        }
      }
      if (flag) {
        menuList.value[index] = element;
        break;
      }
    }
  }
  currentMenu.value = {};
  showSubId.value = "";
  if (menuList.value.length > 0) {
    if (showSubId.value === "") {
      showSubId.value = menuList.value[0].id; //初始化 showSubId 表示哪个子菜单是显示的
    }
    if (!currentMenu.value.id || currentMenu.value.id === "") {
      currentMenu.value = menuList.value[0]; //初始化 currentMenuId 表示当前选中的菜单ID
    }
  }
};

// 删除当前菜单
const clickDeleteMenu = (ev) => {
  ev.stopPropagation();
  if (currentMenu.value.id && currentMenu.value.id.startsWith("mock_")) {
    removeMenuData(currentMenu.value);
  } else {
    component.confirm(
      "warn",
      ev,
      lp._appTools.appPack.messageAlertTitle,
      { html: lp._appTools.mpMenu.menuDeleteAlertMsg },
      560,
      230,
      function () {
        doMPWeixinMenuAction("menuDelete", currentMenu.value.id).then(
          (data) => {
            removeMenuData(currentMenu.value);
            component.notice(lp._appTools.mpMenu.menuDeleteSuccess, "success");
          }
        );
        this.close();
      },
      function () {
        this.close();
      },
      null,
      component.content
    );
  }
};
// 更新list中的数据对象
const setMenuData = (menu) => {
      if (menuList.value.length > 0) {
          for (let index = 0; index < menuList.value.length; index++) {
              const element = menuList.value[index];
              if (element.id === menu.id) {
                  menuList.value[index] = menu;
                  showSubId.value = menu.id;
                  break;
              }
              var flag = false;
              if (element.sub_button && element.sub_button.length > 0) {
                  for (let i = 0; i < element.sub_button.length; i++) {
                      const child = element.sub_button[i];
                      if (child.id === menu.id) {
                          flag = true;
                          element.sub_button[i] = menu;
                      }
                  }
              }
              if (flag) {
                  menuList.value[index] = element;
                  showSubId.value = element.id;
                  break;
              }
          }
          currentMenu.value = menu;
      }
  };
// 保持菜单
const saveMenu = () => {
  if (!currentMenu.value.name || currentMenu.value.name === "") {
    component.notice(lp._appTools.mpMenu.formNameErrorEmpty, "error");
    return;
  }
  const isFirst = currentMenu.id === showSubId.value;
  if (isFirst) {
      if (currentMenu.value.name.length > 4) {
          component.notice(lp._appTools.mpMenu.formNameErrorMaxLen4, "error");
          return;
      }
  } else {
       if (currentMenu.value.name.length > 6) {
          component.notice(lp._appTools.mpMenu.formNameErrorMaxLen6, "error");
          return;
      }
  }
  if (!currentMenu.value.order || currentMenu.value.order === "") {
    component.notice(lp._appTools.mpMenu.formOrderErrorEmpty, "error");
    return;
  }
  //数字
  var reg = /^[\d]+$/;
  var s = reg.test(currentMenu.value.order);
  if (s === false) {
    component.notice(lp._appTools.mpMenu.formOrderErrorNotNumber, "error");
    return;
  }
  if (currentMenu.value.order.length > 6) {
    component.notice(lp._appTools.mpMenu.formOrderErrorMaxLen, "error");
    return;
  }
  if (!currentMenu.value.sub_button || currentMenu.value.sub_button.length == 0) {
    const type = currentMenu.value.type;
    if (type === "view") { //网页
        if (!currentMenu.value.url || currentMenu.value.url === "") {
           component.notice(lp._appTools.mpMenu.formTypeUrlErrorEmpty, "error");
            return;
        }
    } else if (type === "miniprogram") { //小程序
        if (!currentMenu.value.appid || currentMenu.value.appid === "") {
            component.notice(lp._appTools.mpMenu.formTypeMiniprogramAppidErrorEmpty, "error");
            return;
        }
        if (!currentMenu.value.pagepath || currentMenu.value.pagepath === "") {
            component.notice(lp._appTools.mpMenu.formTypeMiniprogramPathErrorEmpty, "error");
            return;
        }
        if (!currentMenu.value.url || currentMenu.value.url === "") {
           component.notice(lp._appTools.mpMenu.formTypeMiniprogramUrlErrorEmpty, "error");
            return;
        }
         
    } else { //消息
        if (!currentMenu.value.content || currentMenu.value.content === "") {
          component.notice(lp._appTools.mpMenu.formTypeMsgErrorEmpty, "error");
            return;
        }
        //新建的菜单 click类型需要设置key 
        currentMenu.value.key = currentMenu.value.id;
    }
  }

  let newMenu = currentMenu.value;
  if (newMenu.id.startsWith('mock_')) {
      delete newMenu.id; //新增要删除id
  }
  //写入数据 远程写入 还有 上级数组中
  if (!newMenu.id) { //新增
      doMPWeixinMenuAction("menuAdd", newMenu).then(
          (data) => {
            newMenu.id = data.id;//更新id
            setMenuData(newMenu);
            component.notice(lp._appTools.mpMenu.menuSaveSuccess, "success");
          }
        );
  }else { // 更新
       doMPWeixinMenuAction("menuUpdate", newMenu.id, newMenu).then(
          (data) => {
            setMenuData(newMenu);
            component.notice(lp._appTools.mpMenu.menuSaveSuccess, "success");
          }
        );
  }
};
// 发布菜单到微信公众号
const menuPublishToWeixin = (e) => {
  if (menuList.value.length > 0) {
    component.confirm(
      "warn",
      ev,
      lp._appTools.appPack.messageAlertTitle,
      { html: lp._appTools.mpMenu.publishToWxmp },
      560,
      230,
      function () {
        doMPWeixinMenuAction("menuCreate2Weixin").then(
          (data) => {
            component.notice(lp._appTools.mpMenu.publishSuccess, "success");
          }
        );
        this.close();
      },
      function () {
        this.close();
      },
      null,
      component.content
    );
  }
};
// 保持订阅回复信息
const saveSubscribe = (e) => {
  if (!mpweixinSubscribe.value.content || mpweixinSubscribe.value.content === "") {
      component.notice(lp._appTools.mpMenu.subscribeContentErrorEmpty, "error");
      return;
  }
  let newSubs = mpweixinSubscribe.value;
  //新建的菜单 click类型需要设置key 
  if (newSubs.id.startsWith('mock_')) {
      delete newSubs.id; //新增要删除id
  }
  //写入数据 远程写入 还有 上级数组中
  if (!newSubs.id) { //新增
      doMPWeixinMenuAction("menuAdd", newSubs).then(
          (data) => {
            newSubs.id = data.id; //更新id
            mpweixinSubscribe.value = newSubs;
            component.notice(lp._appTools.mpMenu.menuSaveSuccess, "success");
          }
        );
       
  }else { // 更新
    doMPWeixinMenuAction("menuUpdate", newSubs.id, newSubs).then(
          (data) => {
            component.notice(lp._appTools.mpMenu.menuSaveSuccess, "success");
          }
        );
  }
};

load();
</script>

<style scoped>
a {
  color: #44b549;
  text-decoration: none;
  background-color: transparent;
  outline: none;
  cursor: pointer;
  -webkit-transition: color 0.3s;
  transition: color 0.3s;
  -webkit-text-decoration-skip: objects;
}
h1,
h2,
h3,
h4,
h5,
h6 {
  margin-top: 0;
  margin-bottom: 0.5em;
  color: rgba(0, 0, 0, 0.85);
  font-weight: 500;
}

ul,
ol {
  padding-left: 0;
  list-style-type: none;
}
ol,
ul,
dl {
  margin-top: 0;
  margin-bottom: 0;
}

p {
  margin-top: 0;
  margin-bottom: 0;
}
input[type="radio"],
input[type="checkbox"] {
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  padding: 0;
}
input[type="text"],
input[type="password"],
input[type="number"],
textarea {
  -webkit-appearance: none;
}

.menu_setting_area {
  margin: 14px 30px 0;
}

.menu_preview_area {
  float: left;
  margin-right: 12px;
  position: relative;
}

.menu_form_area {
  display: table-cell;
  vertical-align: top;
  float: none;
  width: auto;
  min-width: 500px;
}

.menu_setting_area:after {
  content: "\200B";
  display: block;
  height: 0;
  clear: both;
}

.mobile_menu_preview {
  width: 294px;
  background-size: contain;
}

.mobile_menu_preview {
  position: relative;
  width: 317px;
  height: 580px;
  background: transparent url(../../../assets/bg_mobile_head_default49d02c.png)
    no-repeat 0 0;
  background-position: 0 0;
  border: 1px solid #e7e7eb;
}

.mobile_menu_preview .mobile_hd {
  color: #fff;
  text-align: center;
  padding-top: 30px;
  font-size: 15px;
  width: auto;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  word-wrap: normal;
  margin: 0 30px;
}

.menu_preview_area .pre_menu_list {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  border-top: 1px solid #e7e7eb;
  background: transparent url(../../../assets/bg_mobile_foot_default49d02c.png)
    no-repeat 0 0;
  background-position: 0 0;
  background-repeat: no-repeat;
  padding-left: 43px;
}

.pre_menu_item {
  position: relative;
  float: left;
  line-height: 50px;
  text-align: center;
}

.size1of3 {
  width: 33.33%;
}
.menu_preview_area .pre_menu_item:first-child .pre_menu_link {
  border-left-width: 0;
}
.pre_menu_item a {
  display: block;
  width: auto;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  word-wrap: normal;
  color: #616161;
  text-decoration: none;
}
.pre_menu_link {
  border-left: 1px solid #e7e7eb;
}
.no_extra.grid_item {
  float: none;
  width: auto;
  overflow: hidden;
}
.sub_pre_menu_box {
  position: absolute;
  left: 0;
  width: 100%;
  border: 1px solid #d0d0d0;
  bottom: 60px;
  background-color: #fafafa;
  border-top-width: 0;
}
.menu_preview_area .sub_pre_menu_inner {
  display: block;
  border-top: 1px solid #e7e7eb;
  width: auto;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  word-wrap: normal;
  cursor: pointer;
}
.sub_pre_menu_list li a {
  padding: 0 0.5em;
}
.pre_menu_item a {
  display: block;
  width: auto;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  word-wrap: normal;
  color: #616161;
  text-decoration: none;
}

.menu_preview_area .pre_menu_item.current .pre_menu_link {
  border: 1px solid #07c160;
  line-height: 48px;
  background-color: #fff;
  color: #07c160;
}
.menu_preview_area .icon14_menu_add {
  background: url(../../../assets/index_z49d030.png) 0 0 no-repeat;
  width: 14px;
  height: 14px;
  vertical-align: middle;
  display: inline-block;
  margin-top: -2px;
}
.menu_preview_area .sub_pre_menu_box {
  bottom: 60px;
  background-color: #fafafa;
  border-top-width: 0;
}
.menu_preview_area .sub_pre_menu_list li:first-child {
  border-top: 1px solid #d0d0d0;
}
.menu_preview_area .sub_pre_menu_list li {
  line-height: 44px;
  border: 1px solid transparent;
  margin: 0 -1px -1px;
}
.menu_preview_area .sub_pre_menu_list li.current {
  background-color: #fff;
  border: 1px solid #07c160;
  position: relative;
  z-index: 1;
  line-height: 45px;
}
.menu_preview_area .sub_pre_menu_list li.current a {
  color: #07c160;
}
.sub_pre_menu_box .arrow {
  position: absolute;
  left: 50%;
  margin-left: -6px;
}
.sub_pre_menu_box .arrow_in {
  bottom: -5px;
  display: inline-block;
  width: 0;
  height: 0;
  border-width: 6px;
  border-style: dashed;
  border-color: transparent;
  border-bottom-width: 0;
  border-top-color: #fafafa;
  border-top-style: solid;
}
.sub_pre_menu_box .arrow_out {
  bottom: -6px;
  display: inline-block;
  width: 0;
  height: 0;
  border-width: 6px;
  border-style: dashed;
  border-color: transparent;
  border-bottom-width: 0;
  border-top-color: #d0d0d0;
  border-top-style: solid;
}
.grid_line:after {
  content: "\200B";
  display: block;
  height: 0;
  clear: both;
}

.menu_setting_area:after {
  content: "\200B";
  display: block;
  height: 0;
  clear: both;
}
.portable_editor.to_left {
  padding-left: 12px;
}
.portable_editor {
  position: relative;
}
.menu_form_area .editor_inner {
  min-height: 560px;
  padding-bottom: 20px;
}
.portable_editor .editor_inner {
  padding: 0 20px 5px;
  background-color: #f4f5f9;
  border: 1px solid #e7e7eb;
  border-radius: 0;
  -moz-border-radius: 0;
  -webkit-border-radius: 0;
  box-shadow: none;
  -moz-box-shadow: none;
  -webkit-box-shadow: none;
}
.menu_form_hd {
  padding: 9px 0;
  border-bottom: 1px solid #e7e7eb;
}
.global_mod.float_layout .global_info {
  float: left;
}
.menu_form_hd h4 {
  font-weight: 400;
}
.global_mod .global_extra {
  text-align: right;
}
.global_mod.float_layout:after {
  content: "\200B";
  display: block;
  height: 0;
  clear: both;
}
.frm_textarea {
  height: 80px;
  margin: 4px 0;
}
.frm_input {
  height: 22px;
  margin: 4px 0;
}
.frm_input,
.frm_textarea {
  width: 100%;
  background-color: transparent;
  border: 0;
  outline: 0;
}
.portable_editor .frm_control_group {
  margin-bottom: 10px;
}
.portable_editor .frm_control_group {
  margin-top: 30px;
  margin-bottom: 30px;
  padding-bottom: 0;
}
.menu_form_area .frm_label {
  width: 5em;
}
.menu_content .frm_control_group {
  margin-top: 0;
}

.frm_label {
  float: left;
  width: 5em;
  margin-top: 0.3em;
  margin-right: 1em;
  font-size: 14px;
}
.portable_editor .frm_label .title {
  font-weight: 400;
  font-style: normal;
}
.frm_controls {
  display: table-cell;
  vertical-align: top;
  float: none;
  width: auto;
}
.frm_vertical_pt {
  padding-top: 0.3em;
}
.frm_input_box.counter_in {
  width: 228px;
  padding-right: 60px;
}
.frm_textarea_box.counter_in {
  width: 228px;
  padding-right: 60px;
}
.frm_textarea_box {
  display: inline-block;
  position: relative;
  line-height: 30px;
  vertical-align: middle;
  width: 278px;
  font-size: 14px;
  padding: 0 10px;
  border: 1px solid #e7e7eb;
  box-shadow: none;
  -moz-box-shadow: none;
  -webkit-box-shadow: none;
  border-radius: 0;
  -moz-border-radius: 0;
  -webkit-border-radius: 0;
  background-color: #fff;
}
.frm_input_box {
  display: inline-block;
  position: relative;
  height: 30px;
  line-height: 30px;
  vertical-align: middle;
  width: 278px;
  font-size: 14px;
  padding: 0 10px;
  border: 1px solid #e7e7eb;
  box-shadow: none;
  -moz-box-shadow: none;
  -webkit-box-shadow: none;
  border-radius: 0;
  -moz-border-radius: 0;
  -webkit-border-radius: 0;
  background-color: #fff;
}
.menu_form_area .frm_tips,
.menu_form_area .frm_msg {
  width: auto;
}

.frm_radio_label,
.frm_checkbox_label {
  display: inline-block;
  text-align: left;
  cursor: pointer;
  margin-right: 1em;
}
.icon_radio {
  background: url(../../../assets/base_z49d030.png) 0 -140px no-repeat;
  width: 16px;
  height: 16px;
  vertical-align: middle;
  display: inline-block;
}
.icon_radio.selected,
.selected .icon_radio {
  background: url(../../../assets/base_z49d030.png) 0 -160px no-repeat;
}
.icon_radio,
.icon_checkbox {
  margin-right: 3px;
  margin-top: -2px;
  *margin-top: 0;
}
.frm_radio,
.frm_checkbox {
  position: absolute;
  left: -999em;
}

.frm_tips {
  position: relative;
}
.frm_tips,
.frm_msg {
  padding-top: 4px;
  width: 300px;
}
.frm_tips {
  color: #9a9a9a;
}
.frm_msg.fail {
  color: #fa5151;
}
.menu_content_tips {
  padding-bottom: 10px;
}
.tips_global {
  color: #9a9a9a;
}
.dn {
  display: none;
}

.menu_content {
  padding: 16px 20px;
  border: 1px solid #e7e7eb;
  background-color: #fff;
}

.btn-box {
  padding: 20px 0 30px 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.btn_primary {
  background-color: #07c160;
  background-image: -webkit-gradient(
    linear,
    left top,
    left bottom,
    color-stop(0, #07c160),
    to(#07c160)
  );
  background-image: linear-gradient(to bottom, #07c160 0, #07c160 100%);
  border-color: #07c160;
  color: #fff;
}
.btn {
  display: inline-block;
  overflow: visible;
  margin-left: 0.5em;
  margin-right: 0.5em;
  min-width: 80px !important;
  padding: 0;
  height: 30px;
  line-height: 30px;
  vertical-align: middle;
  text-align: center;
  text-decoration: none;
  border-radius: 3px;
  -moz-border-radius: 3px;
  -webkit-border-radius: 3px;
  font-size: 14px;
  border-width: 1px;
  border-style: solid;
  cursor: pointer;
}

.btn button {
  color: #fff;
  width: 90%;
  display: block;
  height: 100%;
  background-color: transparent;
  border: 0;
  outline: 0;
  overflow: visible;
  cursor: pointer;
}
</style>
