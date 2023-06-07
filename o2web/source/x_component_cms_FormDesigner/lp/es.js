MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.FormDesigner = MWF.xApplication.process.FormDesigner || {};
MWF.xDesktop.requireApp("process.FormDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.cms.FormDesigner.LP = Object.merge( {}, MWF.xApplication.process.FormDesigner.LP, {
    "selectApplication" : "Seleccionar aplicación",
    "formType": {
        "empty": "Formulario en blanco",
        "publishEdit": "Formulario de edición de publicación",
        "publishRead": "Formulario de lectura de publicación",
        "publishEditGreen": "Formulario de edición de publicación (verde)",
        "publishReadGreen": "Formulario de lectura de publicación (verde)",
        "dataInput": "Formulario de entrada de datos",
    },
    "validation" : {
        "publish": "Al publicar"
    },
    "modules": {
        "reader": "Lector",
        "commend": "Me gusta",
        "author": "Autor",
        "log": "Registro",
        "comment": "Comentario",
        "logCommend" : "Registro de likes",
        "group_cms": "Contenido"
    },
    "formStyle":{
        "noneStyle": "Sin estilo",
        "defaultStyle": "Estilo clásico",
        "redSimple": "Simplicidad rojo",
        "blueSimple": "Simplicidad azul",
        "greenFlat": "Plano verde",
        "defaultMobileStyle": "Estilo para móvil",
        "banner": "Banner",
        "title": "Título",
        "sectionTitle": "Título de sección",
        "section": "Sección"
    },
    "propertyTemplate": {
        "setPopular": "Establecer como popular",

        "commentPerPage": "Comentarios por página",
        "tiao": "unidades",
        "allowModifyComment": "Permitir la modificación después de publicar",
        "allowComment": "Permitir comentarios",
        "editor": "Editor",
        "editorTitle": "Secuencia de comandos de configuración de CKEditor",
        "editorConfigNote": "Devuelve el objeto Config de CKEditor para la inicialización del editor.",
        "editorConfigLinkNote": "Para obtener más ayuda sobre las propiedades, consulte:",

        "table": "Tabla",
        "text": "Texto",
        "format": "Formato",

        "validationSave": "Validación de guardado",
        "validationPublish": "Validación de publicación",

        "notice" : "Mensaje",
        "noticeInfo": "Nota: El interruptor general para enviar notificaciones se establece en la configuración de categoría.",
        "noticeRange": "Alcance",
        "noticeByReader": "Según el alcance de lectura",
        "noticeByCustom": "Personalizado",
        "notifyCreatePerson": "Notificar al creador",
        "blankToAllNotify": "Notificar al alcance de lectura cuando el lector (alcance de publicación) está vacío",
        "blankNotToAllNotify": "No notificar cuando el alcance de publicación está vacío",
        "specificValue": "Valor específico",
        "formField": "Campo del formulario"
    },
    "actionBar": {
        "close":"Cerrar",
        "closeTitle": "Cerrar documento",
        "edit": "Editar",
        "editTitle": "Editar documento",
        "save": "Guardar",
        "saveTitle": "Guardar documento",
        "publish": "Publicar",
        "publishTitle": "Publicar documento",
        "publishDelayed": "Publicación programada",
        "publishDelayedTitle": "Publicar documento programado",
        "saveDraft": "Guardar borrador",
        "saveDraftTitle": "Guardar borrador de documento",
        "popular": "Establecer como popular",
        "popularTitle": "Establecer documento como popular",
        "delete": "Eliminar",
        "deleteTitle": "Eliminar documento",
        "print": "Imprimir",
        "printTitle": "Imprimir documento",
        "setTop": "Fijar en la parte superior",
        "setTopTitle": "Fijar el documento en la parte superior",
        "cancelTop": "Cancelar fijación en la parte superior",
        "cancelTopTitle": "Cancelar la fijación del documento en la parte superior",
        "downloadAll": "Descargar todo",
        "downloadAllTitle": "Descargar todo el documento"
    }
});
