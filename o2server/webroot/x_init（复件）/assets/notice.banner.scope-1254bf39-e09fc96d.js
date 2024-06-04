const r=`* {\r
    box-sizing: border-box;\r
}\r
.container{\r
    /*position: fixed;*/\r
    width: 100%;\r
    top: 0;\r
    left: 0;\r
    padding: 0.714em;\r
    min-height: 5em;\r
    transition: all 0.5s;\r
    display: flex;\r
    justify-content: space-between;\r
    align-items: flex-start;\r
}\r
.content{\r
    display: flex;\r
    width: inherit;\r
    justify-content: center;\r
    align-items: center;\r
}\r
.align_left{\r
    justify-content: flex-start;\r
}\r
.align_right{\r
    justify-content: flex-end;\r
}\r
.align_center{\r
    justify-content: center;\r
}\r
\r
.valign_top{\r
    align-items: flex-start;\r
}\r
.valign_bottom{\r
    align-items: flex-end;\r
}\r
.valign_center{\r
    align-items: center;\r
}\r
.icon {\r
    color: var(--oo-color-text-white);\r
    font-size: calc(var(--oo-font-size-largest)*2);\r
    text-align: center;\r
    padding: 0.1em;\r
}\r
.message {\r
    padding: 0.357em;\r
}\r
.title {\r
    font-weight: bold;\r
    color: var(--oo-color-text-white);\r
    padding: 0 0.357em 0.357em 0.357em;\r
}\r
.text{\r
    color: var(--oo-color-text-white);\r
    padding: 0 0.357em;\r
}\r
.close{\r
    cursor: pointer;\r
    color: var(--oo-color-text-white);\r
}\r
\r
.info.container{\r
    background: var(--oo-color-info);\r
}\r
.error.container{\r
    background: var(--oo-color-error);\r
}\r
.warn.container{\r
    background: var(--oo-color-warn);\r
}\r
.success.container{\r
    background: var(--oo-color-success);\r
}\r
`;export{r as default};
