package com.x.query.service.processing.jaxrs.index;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;

import com.google.gson.JsonElement;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.core.express.index.Indexs;
import com.x.query.core.express.service.processing.jaxrs.index.ActionCountWi;
import com.x.query.core.express.service.processing.jaxrs.index.ActionCountWo;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionDirectoryDocumentCount extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDirectoryDocumentCount.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
        ActionResult<Wo> result = new ActionResult<>();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        String category = wi.getCategory();
        String key = wi.getKey();

        Optional<Directory> optional = Optional.empty();

        if (StringUtils.isAllBlank(category, key)) {
            optional = Indexs.directory(Indexs.CATEGORY_SEARCH, Indexs.KEY_ENTIRE, true);
        } else {
            optional = Indexs.directory(category, key, true);
        }

        Wo wo = new Wo();
        if (optional.isPresent()) {
            try (DirectoryReader reader = DirectoryReader.open(optional.get())) {
                wo.setCount((long) reader.numDocs());
            }
            wo.setCategory(category);
            wo.setKey(key);
            wo.setExists(true);
        } else {
            wo.setCategory(category);
            wo.setKey(key);
            wo.setExists(false);
            wo.setCount(0L);
        }
        result.setData(wo);
        return result;
    }

    @Schema(name = "com.x.query.service.processing.jaxrs.index.ActionDirectoryDocumentCount$Wo")
    public static class Wo extends ActionCountWo {

        private static final long serialVersionUID = 902681475422445346L;

    }

    @Schema(name = "com.x.query.service.processing.jaxrs.index.ActionDirectoryDocumentCount$Wi")
    public class Wi extends ActionCountWi {

        private static final long serialVersionUID = -4646809016933808952L;

    }

}
