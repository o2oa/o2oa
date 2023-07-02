package com.x.query.service.processing.index;

import java.util.List;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.query.core.entity.Item;

class ExceptionWorkUpdate extends LanguagePromptException {

    private static final long serialVersionUID = -9089355008820123519L;

    ExceptionWorkUpdate(Throwable th, String work, List<Item> items) {
        super(th, "update work:{} error, items:{}.", work, items);
    }
}
