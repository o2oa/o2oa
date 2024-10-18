MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.workcenter = MWF.xApplication.process.workcenter || {};
MWF.xApplication.process.workcenter.LP={
	"title": "Centro de oficina",
	"task": "Pendiente",
	"read": "Por leer",
	"taskCompleted": "Completado",
	"readCompleted": "Leído",
	"draft": "Borrador",
	"createProcess": "Nuevo flujo de trabajo",
	"all": "Todos",
	"byType": "Por categoría",
	"expire1": "Fecha límite de la tarea: {time}",
	"expire2": "La tarea está a punto de expirar, fecha límite: {time}",
	"expire3": "La tarea ha expirado, fecha límite: {time}",
	"firstPage": "Primera",
	"lastPage": "Última",
	"process": "Continuar el flujo de trabajo",
	"processStarted": "El archivo se ha iniciado",
	"taskProcessed": "El archivo se ha enviado",
	"taskProcessedMessage": "Ha procesado una tarea pendiente:",
	"nextActivity": "Actividad siguiente:",
	"nextUser": "Responsable:",
	"processStartedMessage": "Ha iniciado un nuevo trabajo:",
	"deal": "Procesar",
	"processing": "Procesando",
	"workCompleted": "El flujo de trabajo del archivo se ha completado",
	"completed": "Completado",
	"workProcess": "Continuar el flujo de trabajo",
	"next_etc": "y otros {count} más",
	"processTaskCompleted": "La tarea pendiente se ha procesado",
	"arrivedActivity": "La tarea ha llegado a la actividad:",
	"rapidEditor": "Procesar rápidamente la tarea pendiente",
	"setReadCompleted": "Marcar como leído",
	"setReaded": "Marcar como leído",
	"setReadedConfirmContent": "¿Está seguro de que desea marcar “{title}” como leído?",
	"setReadedConfirmTitle": "Confirmación de marcar como leído",
	"readOpinion": "Opinión de lectura",
	"processInfo": "Detalles del flujo de trabajo",
	"opinion": "Opinión",
	"time": "Fecha",
	"starttime": "Hora de llegada",
	"workFlowTo": "El archivo se ha enviado a:",
	"taskPerson": "Responsable:",
	"open": "Abrir",
	"select": "Seleccionar",
	"processActivity": "Actividad de procesamiento",
	"processActivityInfo": "Estado de la actividad al procesar el archivo",
	"readActivity": "Actividad por leer",
	"readActivityInfo": "Estado de la actividad cuando recibió el archivo por leer",
	"filter": "Filtrar",
	"search": "Buscar",
	"filterPlaceholder": "Ingrese palabras clave",
	"commonUseProcess": "Flujos de trabajo comunes",
	"searchProcessResault": "Resultados de búsqueda para ”{key}“",
	"filterStartPlaceholder": "Buscar flujos de trabajo iniciables",
	"filterCategoryList": [
		{
			"key": "applicationList",
			"name": "Nombre de la aplicación"
		},
		{
			"key": "processList",
			"name": "Nombre del flujo de trabajo"
		},
		{
			"key": "activityNameList",
			"name": "Actividad de procesamiento"
		},
		{
			"key": "creatorUnitList",
			"name": "Departamento creador"
		},
		{
			"key": "startTimeMonthList",
			"name": "Mes de recepción"
		},
		{
			"key": "completedTimeMonthList",
			"name": "Mes de procesamiento"
		},
		{
			"key": "completedList",
			"name": "Completado o no"
		},
		{
			"key": "key",
			"name": "Palabra clave"
		}
	],
	"filterCategoryShortList": [
		{
			"key": "applicationList",
			"name": "Aplicación"
		},
		{
			"key": "processList",
			"name": "Flujo de trabajo"
		},
		{
			"key": "activityNameList",
			"name": "Actividad"
		},
		{
			"key": "creatorUnitList",
			"name": "Departamento"
		},
		{
			"key": "startTimeMonthList",
			"name": "Recepción mensual"
		},
		{
			"key": "completedTimeMonthList",
			"name": "Procesamiento mensual"
		},
		{
			"key": "completedList",
			"name": "Completado"
		},
		{
			"key": "key",
			"name": "Palabra clave"
		}
	],
	"noTask": "No hay pendientes para procesar",
	"noTaskCompleted": "No hay archivos procesados",
	"noRead": "No hay archivos por leer",
	"noReadCompleted": "No hay archivos leídos",
	"noDraft": "No hay borradores de archivo",
	"createWork": "Crear un nuevo archivo de flujo de trabajo",
	"readConfirm" : "¿Está seguro de que desea marcar los archivos seleccionados como leídos?",

	"batch": "Procesamiento por lotes",
	"selectBatch": "Seleccione múltiples tareas pendientes en la misma actividad para procesarlas por lotes",
	"cannotSelectBatch": "No se puede procesar por lotes las tareas pendientes en diferentes actividades",
	"unnamed": "Sin título",
	"review": "Ver",
	"myCreated": "Mi Redacción",
	"noReview": "Sin consultar",
	"noMyCreated": "No hay archivos que haya creado",
	"filterCategoryShortListReview": [
		{
			"key": "applicationList",
			"name": "Aplicación"
		},
		{
			"key": "processList",
			"name": "Proceso"
		},
		{
			"key": "activityNameList",
			"name": "Actividades"
		},
		{
			"key": "creatorUnitList",
			"name": "Departamento"
		},
		{
			"key": "startTimeMonthList",
			"name": "Crear mes"
		},
		{
			"key": "completedList",
			"name": "Completado"
		},
		{
			"key": "key",
			"name": "Palabras clave"
		}
	],
	"filterCategoryListReview": [
		{
			"key": "applicationList",
			"name": "Nombre de la aplicación"
		},
		{
			"key": "processList",
			"name": "Nombre del proceso"
		},
		{
			"key": "activityNameList",
			"name": "Tramitación de las actividades"
		},
		{
			"key": "creatorUnitList",
			"name": "Crear un departamento"
		},
		{
			"key": "startTimeMonthList",
			"name": "Crear mes"
		},
		{
			"key": "completedList",
			"name": "Si se completa la circulación"
		},
		{
			"key": "key",
			"name": "Palabras clave"
		}
	]
}
MWF.xApplication.process.workcenter["lp."+o2.language] = MWF.xApplication.process.workcenter.LP
