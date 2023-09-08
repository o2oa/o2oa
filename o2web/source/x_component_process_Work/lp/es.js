MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xApplication.process.Work.LP = {
	"title": "Trabajo",
    "selectRoute": "Seleccionar decisión",
    "inputOpinion": "Escribir opinión",
    "selectPerson" : "Seleccionar persona",
    "cancel": "Cancelar",
    "ok": "Enviar",
    "close": "Cerrar",
    "saveWrite": "Guardar",
    "inputText": "Por favor, escriba su opinión aquí",

    "mustSelectRoute": "Por favor, seleccione una decisión primero",
    "mustSelectRouteGroup" : "Por favor, seleccione un grupo de decisiones primero",
    "opinionRequired" : "Por favor, escriba su opinión",

    "searchKey": "Ingrese la palabra clave",

    "task": "Centro de tareas pendientes",
    "done": "Centro de tareas completadas",
    "draft": "Borradores",
    "myfile": "Mis archivos",
    "reset": "Restablecer responsable",
    "reroute": "Reenrutar",
    "addSplit": "Agregar bifurcación",
    "rollback": "Retorno del flujo de trabajo",
    "goBack": "volver",

    "phone": "Teléfono móvil",
    "mail": "Correo electrónico",
    "save": "Guardar",
    "process": "Continuar el flujo de trabajo",
    "flowWork": "manejar trabajo",
    "handwriting": "Escritura a mano",
    "audioRecord": "Grabación de audio",

    "noAppendTaskIdentityConfig" : "No se ha configurado un responsable para la transferencia, por favor contacte al administrador",
    "selectAppendTaskIdentityNotice" : "Por favor seleccione un responsable para la transferencia",
    "routeValidFailure" : "Fallo en la validación de la ruta",
    "loadedOrgCountUnexpected" : "La página de selección de personas no se ha cargado completamente, por favor espere...",

    "taskCompletedPerson": "Responsable",
    "readPerson": "Lector",
    "systemFlow": "Procesamiento automático del sistema",

    "openWorkError": "No tienes permiso para ver este documento o ha sido eliminado.",

    "rollbackConfirmTitle": "Confirmación",
    "rollbackConfirmContent": "¿Está seguro de que desea volver el flujo de trabajo al estado “{log}”? (El retorno del flujo de trabajo borrará toda la información posterior a este estado)",

    "recoverFileConfirmTitle": "Confirmación",
    "recoverFileConfirmContent": "¿Está seguro de que desea recuperar el archivo a la versión “{att}”? (Después de la recuperación, los archivos temporales guardados serán eliminados y no podrán ser recuperados nuevamente)",

    "notRecoverFileConfirmTitle": "Confirmación",
    "notRecoverFileConfirmContent": "¿Está seguro de que desea cancelar la recuperación del archivo? (Después de la cancelación, los archivos temporales guardados serán eliminados y no podrán ser recuperados nuevamente)",

    "closePageCountDownText" : "La página se cerrará en {second} segundos!",
    "closePage" : "Cerrar página",

    "selectRouteGroup" : "Seleccionar grupo de decisiones",
    "defaultDecisionOpinionName" : "Otro",
    "routeGroupOrderList" : ["Aprobar","Rechazar","Otro","Otros"],

    "selectWork": "El archivo que desea abrir tiene múltiples bifurcaciones, por favor seleccione una para ver:",
    "currentActivity": "Actividad actual: ",
    "currentUsers": "Responsable actual: ",
    "completedWork": "El flujo de trabajo del archivo se ha completado",

    "managerProcessNotice" : "Nota: La función de procesamiento rápido se aplica en las siguientes situaciones, de lo contrario puede haber errores:<br\>1. Los campos obligatorios ya han sido completados en el formulario.<br\>2. No es necesario seleccionar personas para enviar la solicitud.<br\>3. No hay contenido basado en la identidad del usuario.<br\>Usted es un administrador y puede simular la entrada de un responsable de tareas para enviar la solicitud. Haga clic en el siguiente enlace para llevar a cabo la simulación.",
    "managerLogin" : "Simulación de ingreso y apertura de archivo",
    "managerLoginConfirmTitle" : "Ingresar mediante simulación",
    "managerLoginConfirmContent" : "¿Está seguro de que desea ingresar y abrir el archivo como {user}? Después de presionar Aceptar, deberá cerrar sesión e iniciar sesión nuevamente para volver al usuario actual.",
    "managerLoginSuccess" : "Se ha cambiado con éxito a {user}",

    "selectIdentity": "Seleccionar la identidad responsable para esta tarea pendiente",
    "selectIdentityInfo": "Se detectó que tiene múltiples tareas pendientes con diferentes identidades. Por favor seleccione una identidad para procesar esta tarea",

    "org": "Organización",
    "duty": "Posición",

    "flowActions": {
        "addTask": "Agregar firma",
        "restablecer": "restablecer",
        "proceso": "enviar",
    },
    "opinion": "opinión",
    "addTaskPerson": "Añadido",
    "inputOpinionNote": "Por favor escribe tu opinión aquí",
    "resetTo": "restablecer a",
    "keepTask": "Guardar mis tareas pendientes",
    "quickSelect": "Selección Rápida",
    "empowerTo": "autorizar a",
    "selectAll": "Seleccionar todo",
    "noQuickSelectDataNote": "El sistema no ha registrado los datos que usted seleccionó en el nodo actual, seleccione manualmente esta vez."
};
MWF.xApplication.process.Work["lp."+o2.language] = MWF.xApplication.process.Work.LP;
