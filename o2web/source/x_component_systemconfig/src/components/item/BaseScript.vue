<template>
  <div class="item" ref="itemNode">
    <div class="item_input_area">
      <div class="" ref="scriptNode"></div>
    </div>
  </div>
</template>

<script setup>
import {o2} from '@o2oa/component';
import {ref, onMounted, onUpdated} from "vue";

const emit = defineEmits(['update:value', 'change', 'blur', 'save']);

const props = defineProps({
  label: String,
  value: String,
  inputType: {
    type: String,
    default: 'text'
  }
});
const scriptNode = ref();

const changeValue = (e)=>{
  emit('change', e);
  emit('update:value', e);
}

let scriptArea = null;
let loaddintScript = false;
const createEditor = ()=>{
  loaddintScript = true;
  const content = scriptNode.value.getParent('.content')
  const systemconfigArea = scriptNode.value.getParent('.systemconfig_area')
  o2.require('o2.widget.ScriptArea', ()=>{
    scriptArea = new o2.widget.ScriptArea(scriptNode.value, {
      title: props.label,
      maxObj: content,
      style: 'o2',
      type: props.inputType.type,
      onChange: ()=>{
        if (scriptArea.editor){
          changeValue(scriptArea.editor.getValue());
        }
      },
      onBlur: ()=>{
        if (scriptArea.editor){
          emit('blur', scriptArea.editor.getValue());
        }
      },
      onMaxSize: ()=>{
        if (systemconfigArea) systemconfigArea.hide();
      },
      onReturnSize: ()=>{
        if (systemconfigArea) systemconfigArea.show();
      },
      onSave: ()=>{
        if (scriptArea.editor){
          emit('save', scriptArea.editor.getValue());
        }
      }
    });
    scriptArea.maxSize = function(){

      var obj = this.options.maxObj;
      var coordinates = obj.getCoordinates(obj.getOffsetParent());

      this.container.store("size", {"height": this.container.getStyle("height"), "width": this.container.getStyle("width")});

      this.jsEditor.showLineNumbers();
      this.jsEditor.max();
      this.container.inject(obj, "top");
      this.container.setStyles({
        "position": "absolute",
        // "top": coordinates.top,
        // "left": coordinates.left,
        //"top": coordinates.top+"px",
        //"left": coordinates.left+"px",
        //"width": coordinates.width,
        "width": "100%",
        "height": coordinates.height-2,
        "z-index": 20001
      });
      this.resizeContentNodeSize();
      this.titleActionNode.setStyle("background", "url("+this.path+this.options.style+"/icon/return.png) center center no-repeat");
      this.titleActionNode.store("status", "return");

      this.jsEditor.focus();

    };
    scriptArea.load({code: props.value});
    loaddintScript = false;
  });
}

const destroyEditor = ()=>{
  if (scriptArea){
    scriptArea.destroy();
    scriptArea = null;
  }
}

defineExpose({createEditor, destroyEditor});

onUpdated(()=>{
  if (scriptArea && !loaddintScript) destroyEditor();
  if (!scriptArea && !loaddintScript) createEditor();
});
onMounted(()=>{
  if (!scriptArea && !loaddintScript) createEditor();
});
// const setEditorValue = (value)=>{
//   if (scriptArea){
//     scriptArea.editor.
//   }
// }
//
// onUpdated(()=>{
//   if (scriptArea){
//
//   }
// })
// onMounted(()=>{
//   createEditor();
// });

</script>

<style scoped>
.item{
  overflow: hidden;
  padding: 10px 20px;
  font-size: 14px;
  color: #666666;
  clear: both;
}
.item_label{
  text-align: left;
  overflow: hidden;
  font-size: 14px;
  color: #333333;
  clear: both;
  display: block;
  float: left;
  width: 80px;
  height: 32px;
  line-height: 32px;
}
.item_input_area{
  padding: 0;
  font-size: 14px;
}
button {
  border-radius: 100px;
  border: 0;
  padding: 6px 20px;
  cursor: pointer;
  margin-left: 10px;
}
</style>
