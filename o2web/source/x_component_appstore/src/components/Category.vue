<template>
  <div class="category-area">
    <div class="category-item" :class="{ selected: current==='all' }" @click="listApplicationByCategory('')">{{lp.all}}</div>
    <div v-for="item in categorys" :class="{ selected: current===item }" :key="item" class="category-item" @click="listApplicationByCategory(item)">{{ item }}</div>
    <div class="search-area">
      <div class="category-search-icon icon_search" @click="listApplicationByKeyword"></div>
      <div class="category-search-icon clear" :class="{icon_clear: (!!searchKey)}"  @click="clearKeyword"></div>

      <input v-model="searchKey" :placeholder="lp.searchPlaceholder" @keydown="keydownSearch"/>
    </div>
  </div>
</template>

<script>
import {o2, lp, component} from '@o2oa/component';

export default {
  name: 'Category',
  data(){
    return {
      categorys: [],
      lp: lp,
      current: 'all',
      searchKey: ''
    }
  },
  inject: ['filterCategory', 'filterKeyword'],
  created(){
    o2.Actions.load('x_program_center').MarketAction.listCategory().then((json)=>{
      this.categorys = json.data.valueList;
    });
  },
  methods: {
    listApplicationByCategory(category){
      this.current = category || 'all';
      this.filterCategory(category);
    },
    listApplicationByKeyword(){
      this.filterKeyword(this.searchKey);
    },
    clearKeyword(){
      this.searchKey = '';
      this.filterKeyword('');
    },
    keydownSearch(e){
      if (e.keyCode===13){
        this.listApplicationByKeyword();
      }
    }
  }
}
</script>

<style scoped>
.category-area{
  height: 40px;
  text-align: center;
  display: flex;
  justify-content: center;
  word-break: keep-all;
}
.category-item{
  height: 40px;
  line-height: 40px;
  font-size: 18px;
  color: #333333;

  margin: 0 20px;
  cursor: pointer;
  border-bottom: 2px solid transparent;
}
.selected{
  color: #4A90E2;
  border-bottom: 2px solid #4A90E2;
}
.search-area{
  height: 32px;
  min-width: 228px;
  border-radius: 20px;
  border: 1px solid #999999;
  margin: 5px 0 0 20px;
}
.search-area input{
  height: 30px;
  border: 0;
  background: transparent;
  min-width: 100px;
}
.category-search-icon{
  height: 32px;
  width: 20px;
  margin-right: 10px;
  float: right;
  background-position: center;
  background-repeat: no-repeat;
  cursor: pointer;
}
.clear{
  width: 20px;
  margin-right: 0;
}
</style>
