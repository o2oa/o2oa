package com.x.query.assemble.surface.jaxrs.search;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionTest.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String q) throws Exception {

        ActionResult<Wo> result = new ActionResult<>();

//        Optional<Directory> optional = Indexs.directory(Indexs.CATEGORY_PROCESSPLATFORM, Indexs.TYPE_WORKCOMPLETED,
//                "8d217e98-0eef-4701-b9a0-05cdfdf7a993", true);
//        if (optional.isPresent()) {
//            try (IndexReader reader = DirectoryReader.open(optional.get())) {
//                for (LeafReaderContext lrc : reader.leaves()) {
//                    Terms terms = lrc.reader().terms(Indexs.FIELD_ID);
//                    TermsEnum iterator = terms.iterator();
//                    BytesRef byteRef = null;
//                    while ((byteRef = iterator.next()) != null) {
//                        String value = new String(byteRef.bytes, byteRef.offset, byteRef.length);
//                        System.out.println(term);
//                    }
//                }
//
//            }
//        }
        return result;
    }

    // @Schema(name =
    // "com.x.custom.index.assemble.control.jaxrs.touch.ActionTest$Wo")
    public static class Wo extends WrapString {

        private static final long serialVersionUID = -6815067359344499966L;

    }

}