<template>
	<div>
	  <div class="item_title">{{lp._queryConfig.touchWorkIndex}}</div>
	  <div class="item_info">{{lp._queryConfig.touchWorkIndexInfo}}</div>
	  <div class="item_info">
		  <button class="mainColor_bg" @click="touchWorkIndex">{{lp._queryConfig.touchWorkIndexAction}}</button>
	  </div>


	  <div class="item_title">{{lp._queryConfig.touchWorkCompletedIndex}}</div>
	  <div class="item_info">{{lp._queryConfig.touchWorkCompletedIndexInfo}}</div>
	  <div class="item_info">
		  <button class="mainColor_bg" @click="touchWorkCompletedIndex">{{lp._queryConfig.touchWorkCompletedIndexAction}}</button>
	  </div>


	  <div class="item_title">{{lp._queryConfig.touchDocumentIndex}}</div>
	  <div class="item_info">{{lp._queryConfig.touchDocumentIndexInfo}}</div>
	  <div class="item_info">
		  <button class="mainColor_bg" @click="touchDocumentIndex">{{lp._queryConfig.touchDocumentIndexAction}}</button>
	  </div>

	  <div class="item_title">{{lp._queryConfig.optimizeIndex}}</div>
	  <div class="item_info">{{lp._queryConfig.optimizeIndexInfo}}</div>
	  <div class="item_info">
		  <button class="mainColor_bg" @click="optimizeIndex">{{lp._queryConfig.optimizeIndexAction}}</button>
	  </div>

	</div>
</template>

<script setup>
import {lp, o2, component} from '@o2oa/component';

const action = o2.Actions.load('x_query_service_processing').TouchAction;

const touchWorkIndex = async (e) => {
    touch(e, 'lowFreqWorkTouch', lp._queryConfig.work);
}
const touchWorkCompletedIndex = async (e) => {
    touch(e, 'lowFreqWorkCompletedTouch', lp._queryConfig.workCompleted);
}
const touchDocumentIndex = async (e) => {
    touch(e, 'lowFreqDocumentTouch', lp._queryConfig.document);
}

const optimizeIndex = (e)=>{
    const dlg = component.confirm('info', e, lp._queryConfig.optimizeIndexConfirmTitle,  {html: lp._queryConfig.optimizeIndexConfirm}, 500, 100, async function () {
        const json = await action.optimizeIndex('(0)');
        if (json.data.value) {
            component.notice(lp._queryConfig.optimizeIndexSuccess, 'success');
            e.target.disabled = true;
        }
        this.close();
    }, function(){
        this.close();
    });
}

const touch = async (e, mothed, type)=>{
    const title = lp._queryConfig.indexActionConfirmTitle.replace(/{type}/, type);
    const confirm = lp._queryConfig.indexActionConfirm.replace(/{type}/, type);
    component.confirm('info', e, title,  {html: confirm}, 500, 100, async function () {
        const json = await action[mothed]('(0)');
        if (json.data.value) {
            component.notice(lp._queryConfig.indexActionSuccess.replace(/{type}/, type), 'success');
            e.target.disabled = true;
        }
        this.close();
    }, function(){
        this.close();
    });
}


</script>

<style scoped>
.item{
    padding: 10px 30px 10px 0;
}
hr {
    margin: 20px 0 10px 0;
}
</style>
