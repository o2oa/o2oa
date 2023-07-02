package com.x.query.service.processing.index;

import java.util.List;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.query.core.entity.Item;

class ExceptionDocumentUpdate extends LanguagePromptException {

    private static final long serialVersionUID = -9089355008820123519L;

    ExceptionDocumentUpdate(Throwable th, String document, List<Item> items) {
        super(th, "update document:{} error, items:{}.", document, items);
    }
}
