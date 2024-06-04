const r=`* {\r
    box-sizing: border-box;\r
}\r
.container{\r
    /*position: absolute;*/\r
    /*min-width: 300px;*/\r
    /*min-height: 70px;*/\r
    padding: 0.357em;\r
    background: var(--oo-color-info-bg);\r
    border-radius: var(--oo-area-radius);\r
    border-width: 1px;\r
    border-style: solid;\r
    border-color: var(--oo-color-info-border);\r
    transition: all 0.5s, width 0s;\r
    display: flex;\r
    justify-content: space-between;\r
    align-items: flex-start;\r
    font-size: 0.857em;\r
}\r
.content{\r
    display: flex;\r
    width: inherit;\r
    justify-content: flex-start;\r
    align-items: flex-start;\r
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
\r
.icon {\r
    color: var(--oo-color-info);\r
    font-size: var(--oo-font-size-largest);\r
    width: 1.2em;\r
    text-align: center;\r
    padding: 0.2em;\r
}\r
.message {\r
    padding: 0.357em;\r
}\r
.title {\r
    font-weight: bold;\r
    color: var(--oo-color-text);\r
    padding: 0 0.357em 0.357em 0.357em;\r
}\r
.text{\r
    color: var(--oo-color-text2);\r
    padding: 0 0.357em;\r
}\r
.close{\r
    cursor: pointer;\r
}\r
.hide{\r
    display: none;\r
}\r
\r
.error.container{\r
    background: var(--oo-color-error-bg);\r
    border-color: var(--oo-color-error-border);\r
}\r
.error .icon{\r
    color: var(--oo-color-error);\r
}\r
\r
.warn.container{\r
    background: var(--oo-color-warn-bg);\r
    border-color: var(--oo-color-warn-border);\r
}\r
.warn .icon{\r
    color: var(--oo-color-warn);\r
}\r
\r
.success.container{\r
    background: var(--oo-color-success-bg);\r
    border-color: var(--oo-color-success-border);\r
}\r
.success .icon{\r
    color: var(--oo-color-success);\r
}\r
`;export{r as default};
