MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Xform = MWF.xApplication.process.Xform || {};
MWF.xDesktop.requireApp("process.Xform", "lp."+MWF.language, null, false);
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Xform = MWF.xApplication.cms.Xform || {};
MWF.xApplication.cms.Xform.LP = Object.merge({}, MWF.xApplication.process.Xform.LP, {
    "dataSaved": "ບັນທຶກຂໍ້ມູນສຳເລັດ",
    "documentPublished" : "ປະກາດສຳເລັດ" ,
    "documentDelayedPublished": "ເວລາປະກາດສຳເລັດ",

    "noSelectRange": "ບໍ່ສາມາດກຳນົດຂອບເຂດທີ່ເລືອກໄດ້",

    "begin": "ເລີ່ມ",
    "end": "ຈົບ",
    "none": "ບໍ່ມີ",

    "person": "ຊື່ພະນັກງນ",
    "department": "ໜ່ວຍງານ",
    "firstDate": "ເວລາອ່ານທຳອິດ",
    "readDate": "ເວລາອ່ານຄັ້ງສຸດທ້າຍ",
    "readCount" : "ຈຳນວນອ່ານ",
    "startTime": "ເວລາທີ່ໄດ້ຮັບ",
    "completedTime": "ເວລາປະມວນຜົນ",
    "opinion": "ຄຳຄິດເຫັນ",

    "systemProcess": "ການປະມວນຜົນຂອງລະບົບ",

    "deleteAttachmentTitle":"ຢືນຢັນລົບເອກະສານ",
    "deleteAttachment": "ເຈົ້າແນ່ໃຈ ຫຼືບໍ ວ່າຕ້ອງການລົບເອກະສານ？",

    "replaceAttachmentTitle":"ຢືນຢັນແທນທີ່ເອກະສານ",
    "replaceAttachment": "ເຈົ້າແນ່ໃຈ ຫຼືບໍ ວ່າຕ້ອງການຢືນຢັນແທນທີ່ເອກະສານ？",
    "uploadMore": "ທ່ານໄດ້ຮັບອະນຸຍາດໃຫ້ອັບໂລດເອກະສານໄດ້ສູງສຸດ {n} ລາຍການ",
    "notValidation": "ຂໍ້ມູນລົ້ມເລວ",

    "deleteDocumentTitle": "ຢືນຢັນລົບເອກະສານ",
    "deleteDocumentText": {"html": "<div style='color: red;'>ໝາຍເຫດ：ເຈົ້າກຳລັງຈະລົບເອກະສານນີ້，ຫຼັງຈາກເຈົ້າລົບແລ້ວບໍ່ສາມາດກູ້ຄືນໄດ້，ເຈົ້າແນ່ໃຈບໍວ່າຈະລົບມັນ？</div>"},
    "documentDelete": "ລົບເອກະສານສຳເລັດ",

    "readerFieldNotice" : "ຖ້າບໍ່ເລືອກ ສະມາຊິກທຸກຄົນສາມາດເຫັນໄດ້",

    "readedLogTitle" : "ປະຫວັດການອ່ານ",
    "readedCountText" : "ທັງໝົດ{person}ຄົນ、{count}ຄັ້ງອ່ານ",
    "defaultReadedLogText" : "<font style='color:#00F;'>{person}</font>（{department}） ອ່ານ<font style='color:#00F'>{date}</font>，ທັງໝົດ<font style='color:#00F'>{count}</font>ຄັ້ງ",

    "commendLogTitle" : "ບັນທຶກການຊື່ນຊອບ",
    "commendLogPerson" : "ພະນັກງານທີ່ຊື່ນຊອບ",
    "commendLogTime" : "ເວລາກົດຊື່ນຊອບ",
    "commendCountText" : "ທັງ{count}ຄັ້ງ",
    "defaultCommendLogText" : "<font style='color:#00F;'>{person}</font> ຊື່ນຊອບ<font style='color:#00F'>{date}</font>",


    "reply" : "ຄວາມຄິດເຫັນ",
    "commentTitle" : "ພື້ນທີ່ຄວາມຄິດເຫັນ",
    "commentCountText" : "ທັງໝົດ{count}ລາຍການ",

    "saveComment" : "ບັນທຶກຄວາມຄິດເຫັນ",
    "saveCommentSuccess" : "ບັນທຶກຄວາມຄິດເຫັນສຳເລັດ",
    "deleteCommentTitle" : "ຢືນຢັນລົບຄວາມຄິດເຫັນ",
    "deleteCommentText" : "ຫຼັງຈາກລົບແລ້ວຈະບໍ່ສາມາດກູ້ຄືນໄດ້，ເຈົ້າແນ່ໃຈຈະລົບບໍ？",
    "deleteCommentSuccess" : "ລົບຄວາມຄິດເຫັນສຳເລັດ",
    "commentFormTitle" : "ແກ້ໄຂຄວາມຄິດເຫັນ",
    "createCommentSuccess" : "ສ້າງຄວາມຄິດເຫັນສຳເລັດ",
    "updateSuccess" : "ອັບເດດສຳເລັດ",
    "save" : "ບັນທຶກ",

    "setTopTitle": "ຢືນຢັນການປັກປຸດ",
    "setTopText": "ເຈົ້າແນ່ໃຈ ຫຼື ບໍວ່າຕ້ອງການປັກມຸດເອກະສານໄວ້ເທິງສຸດ？",
    "setTopSuccess": "ປັັກມຸດສຳເລັດ",
    "cancelTopTitle": "ຍົກເລີກຢືນຢັນການປັກມຸດ",
    "cancelTopText": "ເຈົ້າແນ່ໃຈ ຫຼື ບໍວ່າຕ້ອງການຍົກເລີກປັກມຸດເອກະສານ？",
    "cancelTopSuccess": "ຍົກເລີກການປັກມຸດສຳເລັດ",

    "attachmentArea" : "ພື້ນທີ່ເອກະສານ",
    "selectAttachment" : "ເລືອກເອກະສານ",

    "yesterday" : "ມື້ວານ",
    "theDayBeforeYesterday" : "2ມື້ກ່ອນ",
    "severalWeekAgo" : "{count}ອາທິດທີ່ແລ້ວ",
    "severalDayAgo" : "{count}ມື້ທີ່ຜ່ານມາ",
    "severalHourAgo" : "{count}ຊົ່ວໂມງທີ່ແລ້ວ",
    "severalMintuesAgo" : "{count}ນາທີທີ່ແລ້ວ",
    "justNow" : "Just Now",
    "commend": {
        "do": "ຊື່ນຊອບ",
        "undo": "ຍົກເລີກຊື່ນຊອບ"
    },
    "form": {
        "close":"ປິດ",
        "closeTitle": "ປິດເອກະສານ",
        "edit": "ແກ້ໄຂ",
        "editTitle": "ຕັ້ງຄ່າເອກະສານ",
        "save": "ບັນທຶກ",
        "saveTitle": "ບັນທຶກເອກະສານ",
        "publish": "ເຜີຍແຜ່",
        "publishTitle": "ເຜີຍແຜ່ເອກະສາານ",
        "publishDelayed": "ກຳນົດເວລາເຜີແຜ່",
        "publishDelayedTitle": "ກຳນົດເວລາເຜີແຜ່ເອກະສານ",
        "saveDraft": "ບັນທຶກຮ່າງຕົ້ນສະບັບ",
        "saveDraftTitle": "ບັນທຶກຕົ້ນສະບັບ",
        "popular": "ຕັ້ງຄ່າຄວາມຊື່ນຊອບ",
        "popularTitle": "ຕັ້ງຄ່າຄວາມຊື່ນຊອບ",
        "delete": "ລົບ",
        "deleteTitle": "ລົບເອກະສານ",
        "print": "print",
        "printTitle": "print ເອກະສານ",
        "setTop": "ປັກມຸດ",
        "setTopTitle": "ປັກມຸດເອະສານ",
        "cancelTop": "ຍົກເລີກປັກມຸດ",
        "cancelTopTitle": "ຍົກເລີກການປັກມຸດເອກະສານ"
    }

    //"at" : "ອ່ານທີ່",
    //"readdDocument" : "，",
    //"historyRead" : "ທັງໝົດ",
    //"times" : "ຄັ້ງ"
});
MWF.xApplication.cms.Xform["lp."+o2.language] = MWF.xApplication.cms.Xform.LP;
