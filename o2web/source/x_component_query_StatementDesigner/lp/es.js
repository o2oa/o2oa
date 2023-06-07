MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatementDesigner = MWF.xApplication.query.StatementDesigner || {};
if(!MWF.APPDSMD)MWF.APPDSMD = MWF.xApplication.query.StatementDesigner;
MWF.xApplication.query.ViewDesigner = MWF.xApplication.query.ViewDesigner || {};
MWF.xDesktop.requireApp("query.ViewDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.query.StatementDesigner.LP = Object.merge( MWF.xApplication.query.ViewDesigner.LP, {
    "title": "Diseño de consulta",  
    "newStatement": "Nueva configuración de consulta",  
    "unCategory": "Sin categoría",  
    "statement": "Configuración de consulta",  
    "property": "Propiedad",  
    "run": "Ejecutar",  
    "runTest": "Probar la sentencia",  
    "statementType": "Tipo de sentencia",  
    "statementTable": "Tabla de datos",  
    "selectTable": "Seleccionar tabla de datos",  
    "save_success": "La configuración de consulta se guardó correctamente.",  
    "inputStatementName": "Ingresa el nombre de la configuración de consulta",  
    "saveStatementNotice" : "¡Guarda primero!",  
    "cannotDisabledViewNotice": "La vista no está habilitada",  
    "noViewNotice" : "No se ha creado una vista. ¡Crea una vista primero! ",  
    "previewNotSelectStatementNotice" : "Sólo se puede previsualizar si el tipo de sentencia es 'Select'",  
    "field" : "Campo",  
    "fileldSelectNote" : "-Selecciona un campo para insertarlo en la sentencia-",

    "statementFormat": "Formato de la sentencia:",  
    "statementJpql": "JPQL",  
    "statementScript": "Script JPQL",  
    "nativeSql": "SQL nativo",  
    "nativeSqlScript": "Script SQL nativo",  

    "statementCategory": "Tipo de objeto de acceso",  
    "scriptTitle": "Crear JPQL mediante script",  
    "sqlScriptTitle": "Crear SQL mediante script",  
    "countMethod": "Sentencia de conteo",  

    "jpqlType": "Tipo de JPQL",  
    "jpqlFromResult": "Entrada de consulta",  
    "jpqlMaxResult": "Número máximo de resultados",  
    "jpqlSelectTitle": "Sentencia JPQL",  
    "inputWhere": "Puedes ingresar la cláusula 'Where' en el cuadro de edición a continuación.",  
    "jpqlRunSuccess": "La ejecución de JPQL fue exitosa",  
    "newLineSuccess": "Los datos se han insertado correctamente.",  
    "newLineJsonError": "Error al insertar datos. El formato de los datos no es válido.",  
    "queryStatement": "Sentencia de consulta",  
    "countStatement": "Sentencia de conteo",

    "currentPerson":"Persona actual",  
    "currentIdentity":"Identidad actual",  
    "currentPersonDirectUnit":"Organización directa en la que está la persona actual",  
    "currentPersonAllUnit":"Todas las organizaciones a las que pertenece la persona actual",  
    "currentPersonGroupList": "Grupos a los que pertenece la persona actual",  
    "currentPersonRoleList": "Roles que posee la persona actual",  
    "defaultCondition": "Condiciones de asignación automática:",  

    "ignore": "Ignorar",  
    "auto": "Automático",  
    "assign": "Asignar",  

    "mastInputParameter" : "Ingrese el parámetro",  
    "pathExecption" : "El formato del camino es \"alias de tabla.nombre del campo\" y no es correcto.",  

    "modifyViewFilterNote": "El formato de la sentencia ha cambiado, modifique la condición de filtro de la vista.",  

    "systemTable":"Tabla del sistema",  
    "customTable":"Tabla de datos personalizada",

    "taskInstance": "Tarea pendiente (Task)",  
    "taskCompletedInstance": "Tarea completada (TaskCompleted)",  
    "readInstance": "Lectura pendiente (Read)",  
    "readedInstance": "Lectura completada (ReadCompleted)",  
    "workInstance": "Instancia del flujo de trabajo (Work)",  
    "workCompletedInstance": "Instancia de flujo de trabajo completada (WorkCompleted)",  
    "reviewInstance": "Lectura disponible (Review)",  
    "documentInstance": "Documento de gestión de contenido (Document)",  

    "taskInstanceSql":"Tarea pendiente (PP_C_TASK)",  
    "taskCompletedInstanceSql": "Tarea completada (PP_C_TASKCOMPLETED)",  
    "readInstanceSql":"Lectura pendiente  (PP_C_READ)",  
    "readedInstanceSql":"Lectura completada (PP_C_READCOMPLETED)",  
    "workInstanceSql":"Instancia de flujo de trabajo (PP_C_WORK)",  
    "workCompletedInstanceSql":"Instancia de flujo de trabajo completada (PP_C_WORKCOMPLETED)",  
    "reviewInstanceSql":"Lectura disponible (PP_C_REVIEW)",  
    "documentInstanceSql":"Documento de gestión de contenido (CMS_DOCUMENT)",

    "propertyTemplate": {
        // "statementFormat": "如何创建语句：",
        // "statementJpql": "直接编写JPQL创建语句",
        // "statementScript": "通过脚本创建语句",
        // "statementCategory": "访问对象类型",

        "idPath":  "Ruta de ID",  
        "idPathNote": "Nota: Se refiere a la ruta del ID (ID del documento CMS/ID del trabajo de flujo) con respecto a un solo conjunto de datos, se utiliza para abrir documentos.",  
        "selectPath": "Seleccionar ruta",  
        "selectPathNote": "Nota: Debe ingresar correctamente la sentencia de consulta. Después de probar la sentencia o actualizar los datos de la vista, puede mostrar(seleccionar) la ruta.",  
        "dataPathNote": "Nota: Se refiere a la ruta relativa a un solo conjunto de datos en esta columna. Por ejemplo: 0, título o 0.título.",  
        "executionAuthority": "Permiso de ejecución",  
        "anonymousAccess": "Acceso anónimo",  
        "allowed": "Permitido",  
        "disAllowed": "No permitido",  
        "executePerson": "Persona que ejecuta",  
        "executeUnit": "Organización que ejecuta",  

        "hidden": "Oculto",  
        "orderNumber":"Número de ordenamiento",

        // "systemTable":"系统表",
        // "customTable":"自建数据表",
        // "taskInstance":"待办",
        // "taskCompletedInstance": "已办",
        // "readInstance":"待阅",
        // "readedInstance":"已阅",
        // "workInstance":"流程实例",
        // "workCompletedInstance":"已完成流程实例",
        // "reviewInstance":"可阅读",
        // "documentInstance":"内容管理文档",

        "parameter":"Parámetro",  
        "parameterNote":"Nota: Corresponde al parámetro en la sentencia de consulta y en la sentencia de conteo;\nPor ejemplo, para la cláusula 'where' ':field', ingrese 'field';\nPara la cláusula 'where' '?1', ingrese '?1'.",  
        "pathNote":"Nota: El formato del camino es \"alias de tabla.nombre del campo\", por ejemplo: o.title.",  
        "userInput":"Entrada del usuario",  

        "export": "Exportar",  
        "exportWidth": "Ancho",  
        "exportEnable": "Permitir exportación",  
        "isTime": "Tipo de tiempo",  
        "isNumber": "Tipo de número",  
        "viewEnable": "Habilitar vista"

    }
});