MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Xform = MWF.xApplication.process.Xform || {};
MWF.xDesktop.requireApp("process.Xform", "lp."+MWF.language, null, false);
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Xform = MWF.xApplication.cms.Xform || {};
MWF.xApplication.cms.Xform.LP = Object.merge({}, MWF.xApplication.process.Xform.LP, {
    "dataSaved": "Datos guardados exitosamente",
    "documentPublished" : "Publicación exitosa" ,
    "documentDelayedPublished": "Publicación programada exitosa",

    "noSelectRange": "No se puede determinar el rango seleccionado",

    "begin": "Inicio",
    "end": "Fin",
    "none": "Ninguno",

    "person": "Nombre de la persona",
    "department": "Departamento",
    "firstDate": "Fecha de primer lectura",
    "readDate": "Fecha de última lectura",
    "readCount" : "Número de lecturas",
    "startTime": "Hora de recepción",
    "completedTime": "Hora de procesamiento",
    "opinion": "Opinión",

    "systemProcess": "Proceso del sistema",

    "deleteAttachmentTitle":"Confirmación de eliminación de archivo adjunto",
    "deleteAttachment": "¿Está seguro de que desea eliminar el archivo adjunto seleccionado?",

    "replaceAttachmentTitle":"Confirmación de reemplazo de archivo adjunto",
    "replaceAttachment": "¿Está seguro de que desea reemplazar el archivo adjunto seleccionado?",
    "uploadMore": "Solo se permiten cargar hasta {n} archivos adjuntos como máximo",
    "notValidation": "La validación de datos no pasó",

    "deleteDocumentTitle": "Confirmación de eliminación de documento",
    "deleteDocumentText": {"html": "<div style='color: red;'>Atención: Está a punto de eliminar este documento. Una vez eliminado, no podrá recuperarse. ¿Está seguro de que desea eliminar este archivo?</div>"},
	"documentDelete": "Documento eliminado",

    "readerFieldNotice" : "Si no se selecciona, estará disponible para todos los usuarios",

    "readedLogTitle" : "Historial de lectura",
    "readedCountText" : "{person} personas y {count} veces leído en total",
    "defaultReadedLogText" : "<font style='color:#00F;'>{person}</font> ({department}) leyó por última vez el <font style='color:#00F'>{date}</font>, con un total de <font style='color:#00F'>{count}</font> veces.",

    "commendLogTitle" : "Historial de 'Me gusta'",
    "commendLogPerson" : "Persona que dio 'Me gusta'",
    "commendLogTime" : "Hora de 'Me gusta'",
    "commendCountText" : "Se ha dado 'Me gusta' {count} veces en total",
    "defaultCommendLogText" : "<font style='color:#00F;'>{person}</font> dio 'Me gusta' el <font style='color:#00F'>{date}</font>",


    "reply" : "Respuesta",
    "commentTitle" : "Área de comentarios",
    "commentCountText" : "{count} comentarios en total",

    "saveComment" : "Publicar comentario",
    "saveCommentSuccess" : "Comentario publicado con éxito",
    "deleteCommentTitle" : "Confirmación de eliminación de comentario",
    "deleteCommentText" : "Una vez eliminado el comentario no se podrá recuperar. ¿Está seguro de que desea eliminar este comentario?",
    "deleteCommentSuccess" : "Comentario eliminado con éxito",
    "commentFormTitle" : "Editar comentario",
    "createCommentSuccess" : "Comentario creado con éxito",
    "updateSuccess" : "Actualización exitosa",
    "save" : "Guardar",

    "setTopTitle": "Confirmación de fijar en la parte superior",
    "setTopText": "¿Está seguro de que desea fijar este documento en la parte superior?",
    "setTopSuccess": "Fijado en la parte superior con éxito",
    "cancelTopTitle": "Confirmación de cancelar fijación en la parte superior",
    "cancelTopText": "¿Está seguro de que desea cancelar la fijación en la parte superior de este documento?",
    "cancelTopSuccess": "Fijación en la parte superior cancelada con éxito",

    "attachmentArea" : "Área de archivos adjuntos",
    "selectAttachment" : "Seleccionar archivo adjunto",

    "yesterday" : "Ayer",
    "theDayBeforeYesterday" : "Anteayer",
    "severalWeekAgo" : "Hace {count} semanas",
    "severalDayAgo" : "Hace {count} días",
    "severalHourAgo" : "Hace {count} horas",
    "severalMintuesAgo" : "Hace {count} minutos",
    "justNow" : "Ahora mismo",
    "commend": {
        "do": "Me gusta",
        "undo": "Cancelar 'Me gusta'"
    },
    "form": {
        "close":"Cerrar",
        "closeTitle": "Cerrar documento",
        "edit": "Editar",
        "editTitle": "Editar documento",
        "save": "Guardar",
        "saveTitle": "Guardar documento",
        "publish": "Publicar",
        "publishTitle": "Publicar documento",
        "publishDelayed": "Publicación programada",
        "publishDelayedTitle": "Programar publicación del documento",
        "saveDraft": "Guardar borrador",
        "saveDraftTitle": "Guardar como borrador",
        "popular": "Establecer como popular",
        "popularTitle": "Establecer como documento popular",
        "delete": "Eliminar",
        "deleteTitle": "Eliminar documento",
        "print": "Imprimir",
        "printTitle": "Imprimir documento",
        "setTop": "Fijar en la parte superior",
        "setTopTitle": "Fijar este documento en la parte superior",
        "cancelTop": "Cancelar fijación en la parte superior",
        "cancelTopTitle": "Cancelar la fijación en la parte superior de este documento"
    }

    //"at" : "阅于",
    //"readdDocument" : "，",
    //"historyRead" : "共",
    //"times" : "次"
});
MWF.xApplication.cms.Xform["lp."+o2.language] = MWF.xApplication.cms.Xform.LP;
