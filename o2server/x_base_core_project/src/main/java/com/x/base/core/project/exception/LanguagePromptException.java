package com.x.base.core.project.exception;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.logger.MessageFormatter;
import com.x.base.core.project.tools.LanguageTools;

public abstract class LanguagePromptException extends PromptException {

    private static final long serialVersionUID = -1212029031489695352L;

    private Object[] argArray = null;

    private String languageKey = null;

    public LanguagePromptException() {
        super();
    }

    public LanguagePromptException(String message) {
        super(message);
    }

    public LanguagePromptException(String message, Object... os) {
        super(format(message, os));
        this.argArray = os;
    }

    public LanguagePromptException(Throwable cause) {
        super(cause);
    }

    public LanguagePromptException(Throwable cause, String message) {
        super(cause, message);
    }

    public LanguagePromptException(Throwable cause, String message, Object... os) {
        super(cause, format(message, os));
        this.argArray = os;
    }

    public String getFormatMessage(String key, String language) {
        if (StringUtils.isNotBlank(language)) {
            language = StringUtils.split(language, ",")[0].trim();
        }
        if (StringUtils.isNotBlank(this.languageKey)) {
            key = this.languageKey;
        }
        String languageString = LanguageTools.getValue(key, language);
        if (this.argArray != null && StringUtils.isNotBlank(languageString)) {
            languageString = MessageFormatter.arrayFormat(languageString, this.argArray).getMessage();
        }
        return languageString;
    }

    public String getLanguageKey() {
        return languageKey;
    }

    public void setLanguageKey(String languageKey) {
        this.languageKey = languageKey;
    }

}
