import { component as content } from "@o2oa/oovm";
import { lp, o2 } from "@o2oa/component";
import template from "./temp.html";

export default content({
  template,
  autoUpdate: true,
  bind() {
    return {
      lp,
    };
  },
   
});
