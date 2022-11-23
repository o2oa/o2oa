package com.x.query.service.processing.index;

import java.util.List;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.query.core.entity.Item;

class ExceptionWorkCompletedUpdate extends LanguagePromptException {

    private static final long serialVersionUID = -9089355008820123519L;

    ExceptionWorkCompletedUpdate(Throwable th, String workCompketed, List<Item> items) {
        super(th, "update workCompketed:{} error, items:{}.", workCompketed, items);
    }
}
