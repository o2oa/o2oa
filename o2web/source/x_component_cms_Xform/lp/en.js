MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Xform = MWF.xApplication.process.Xform || {};
MWF.xDesktop.requireApp("process.Xform", "lp."+MWF.language, null, false);
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Xform = MWF.xApplication.cms.Xform || {};
MWF.xApplication.cms.Xform.LP = Object.merge({},  MWF.xApplication.process.Xform.LP, {
    "dataSaved": "Data saved successfully",
    "documentPublished": "Published successfully",


    "noSelectRange": "Cannot determine the selection range",

    "begin": "begin",
    "end": "end",
    "none": "None",

    "person": "Personnel Name",
    "department": "Unit",
    "firstDate": "First reading time",
    "readDate": "Recent reading time",
    "readCount": "Number of Readings",
    "startTime": "Received time",
    "completedTime": "Processing Time",
    "opinion": "Opinion",

    "systemProcess": "System Process",

    "deleteAttachmentTitle":"Delete attachment confirmation",
    "deleteAttachment": "Are you sure you want to delete the attachment you selected?",

    "replaceAttachmentTitle": "Replace Attachment Confirmation",
    "replaceAttachment": "Are you sure you want to replace the attachment you selected?",
    "uploadMore": "You can only upload up to {n} attachments",
    "notValidation": "Data validation failed",

    "deleteDocumentTitle": "Confirm Delete Document",
    "deleteDocumentText": {"html": "<div style='color: red;'>Note: You are deleting this document, and the document cannot be retrieved after deletion. Are you sure you want to delete this document?</div>"},
    "documentDelete": "Document has been deleted",

    "readerFieldNotice": "If not selected, all members will be visible",

    "readedLogTitle": "Reading Log",
    "readedCountText": "Total {person} people, {count} reads",
    "defaultReadedLogText": "<font style='color:#00F;'>{person}</font>({department}) read in <font style='color:#00F'>{date}</font>, A total of <font style='color:#00F'>{count}</font> times",

    "reply": "Comment",
    "commentTitle": "Comment Area",
    "commentCountText": "Total {count} comments",

    "saveComment": "Comment",
    "saveCommentSuccess": "Save Comment Success",
    "deleteCommentTitle": "Delete Comment Confirmation",
    "deleteCommentText": "After deleting a comment, it cannot be restored. Are you sure you want to delete this comment?",
    "deleteCommentSuccess": "Comment deleted successfully",
    "commentFormTitle": "Edit Comment",
    "createCommentSuccess": "Comment created successfully",
    "updateSuccess": "Update successful",
    "save": "Save",

    "attachmentArea": "Attachment Area",
    "selectAttachment": "Select Attachment",

    "yesterday": "yesterday",
    "theDayBeforeYesterday": "The Day Before yesterday",
    "severalWeekAgo": "{count}weeks ago",
    "severalDayAgo": "{count} days ago",
    "severalHourAgo": "{count}hours ago",
    "severalMintuesAgo": "{count} minutes ago",
    "justNow": "JustNow",
    "form": {
        "close":"Close",
        "closeTitle": "Close Document",
        "edit": "Edit",
        "editTitle": "Edit Document",
        "save": "Save",
        "saveTitle": "Save Document",
        "publish": "Publish",
        "publishTitle": "Publish Document",
        "saveDraft": "Save Draft",
        "saveDraftTitle": "Save Draft",
        "popular": "Set focus document",
        "popularTitle": "Set as focus document",
        "delete": "Delete",
        "deleteTitle": "Delete Document",
        "print": "Print",
        "printTitle": "Print Document"
    }
});
MWF.xApplication.cms.Xform["lp."+o2.language] = MWF.xApplication.cms.Xform.LP;