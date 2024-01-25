MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xDesktop.requireApp("process.FormDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.cms.FormDesigner.LP = Object.merge( {}, MWF.xApplication.process.FormDesigner.LP, {
    "selectApplication" : "选择应用",
    "formType": {
        "empty": "ຟອມວ່າງ",
        "publishEdit": "ແກ້ໄຂຟອມ",
        "publishRead": "ອ່ານຟອມ",
        "publishEditGreen": "ແກ້ໄຂຟອມ(ສີຂຽວ)",
        "publishReadGreen": "ອ່ານຟອມ(ສີຂຽວ)",
        "dataInput": "ນຳຂໍ້ມູນເຂົ້າຟອມ"
    },
    "validation" : {
        "publish" : "ເວລາປະກາດ"
    },
    "modules": {
        "reader": "ຄົນອ່ານ",
        "commend": "ຊື່ນຊົມ",
        "author": "ຜູ້ຂຽນ",
        "log": "ບັນທຶກການອ່ານ",
        "comment": "ຄວາມຄິດເຫັນ",
        "logCommend" : "ບັນທຶກຊື່ນຊົມ",
        "group_cms": "ກຸ່ມບໍລິຫານເນື້ອໃນ"
    },
    "formStyle":{
        "noneStyle": "None",
        "defaultStyle": "ຮູບແບບດັງເດີມ",
        "redSimple": "ສີແດງ",
        "blueSimple": "ສີຟ້າ",
        "greenFlat": "ສີຂຽວ",
        "defaultMobileStyle": "ຮູບແບບໂທລະສັບ",
        "banner": "Banner",
        "title": "ຫົວຂໍ້",
        "sectionTitle": "ຫົວຂໍ້ Section",
        "section": "Section"
    },
    "propertyTemplate": {
        "setPopular": "ຕັ້ງຄ່າຄວາມນິຍົມ",

        "commentPerPage": "ໜ້າຄວມຄິດເຫັນ",
        "tiao": "ໜ້າ",
        "allowModifyComment": "ອະນຸຍາດໃຫ້ແກ້ໄຂຫຼັງຈາກເຜີຍແຜ່",
        "allowComment": "ອະນຸຍດສະແດງຄຳຄິດເຫັນ",
        "editor": "ຜູ້ແກ້ໄຂ",
        "editorTitle": "CKEditor Config ສະບັບລາຍງນ",
        "editorConfigNote": "Returns the Config object of CKEditor for editor initialization",
        "editorConfigLinkNote": "ມີຊ່ວຍເພີ່ມຕື່ມ ກະລຸນາເບິ່ງ",

        "table": "ຕາຕະລາງ",
        "text": "Text",
        "format": "ຮູບແບບ",

        "validationSave": "ບັນທຶກກວດສອບ",
        "validationPublish": "ກວດກາເຜີຍແຜ່",

        "notice" : "ຂໍ້ມູນ",
        "noticeInfo": "ໝາຍເຫດ：ສະວິດສົ່ງຂໍ້ຄວາມແມ່ນຖືກຈັດໄວ້ໃນປະເພດການຕັ້ງຄ່າ。",
        "noticeRange": "ຊ່ວງໄລຍະ",
        "noticeByReader": "ຂໍ້ມູນໄລຍະການອ່ານ",
        "noticeByCustom": "ປັບແຕ່ງ",
        "notifyCreatePerson": "ແຈ້ງຜູ້ສ້າງ",
        "blankToAllNotify": "ແຈ້ງໄລຍະການອ່ານເວລາຜູ້ອ່ານ（ໄລຍະເຜີຍແຜ່）ວ່າງເປົ່າ",
        "blankNotToAllNotify": "ບໍ່ມີແຈ້ງການເມື່ອຜູ້ອ່ານ（ໄລຍະເຜີຍແຜ່）ວ່າງເປົ່າ",
        "specificValue": "ລະບຸ",
        "formField": "ຂອບເຂດຟອມ"
    },
    "actionBar": {
        "close":"ປິດ",
        "closeTitle": "ປິດເອກະສານ",
        "edit": "ແກ້ໄຂ",
        "editTitle": "ແກ້ໄຂເອກະສານ",
        "save": "ບັນທຶກ",
        "saveTitle": "ບັນທຶກເອກະສານ",
        "publish": "ເຜີຍແຜ່",
        "publishTitle": "ເຜີຍແຜ່ເອກະສານ",
        "publishDelayed": "ກຳນົດເວລາເຜີຍແຜ່ຢ",
        "publishDelayedTitle": "ກຳນົດເວລາເຜີຍແຜ່ເອກະສານ",
        "saveDraft": "ບັນທຶກຮ່າງ",
        "saveDraftTitle": "ບັນທຶກຮ່າງ",
        "popular": "ຕັ້ງຄ່າຄວາມນິຍົມ",
        "popularTitle": "ຕັ້ງຄ່າຄວາມນິຍົມ",
        "delete": "ລົບ",
        "deleteTitle": "ລົບເອກະສານ",
        "print": "Print",
        "printTitle": "Print ເອກະສານ",
        "setTop": "ປັກມຸດ",
        "setTopTitle": "ປັກມຸດເອກະສານ",
        "cancelTop": "ຍົກເລີກການປັກມຸດ",
        "cancelTopTitle": "ຍົກເລີກການປັກມຸດເອກະສານ",
        "downloadAll": "ດາວໂລດທັງໝົດ",
        "downloadAllTitle": "ດາວໂລດທັງໝົດ"
    }
});
