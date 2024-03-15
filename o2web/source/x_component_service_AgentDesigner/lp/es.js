MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.service = MWF.xApplication.service || {};
MWF.xApplication.service.AgentDesigner = MWF.xApplication.service.AgentDesigner || {};
MWF.xApplication.service.AgentDesigner.LP={
	"title": "Editar Agente",
	"newAgent": "Crear Nuevo Agente",
	"agentLibrary": "Biblioteca de Agentes",
	"property": "Propiedades",
	"include": "Incluir",
	"id": "Identificación",
	"name": "Nombre",
	"alias": "Alias",
	"description": "Descripción",
	"validated": "¿El formato del código es correcto?",
	"isEnable": "¿Está habilitado?",
	"cron": "Expresión cron para tareas programadas",
	"lastStartTime": "Última hora de inicio",
	"lastEndTime": "Última hora de finalización",
	"appointmentTime": "Próxima hora estimada de ejecución",
	"openLogViewer": "Abrir visor de registro",
	"debugger": "Depurar",
	"run": "Ejecutar ahora",
	"runSuccess": "Ejecutado con éxito",
	"true": "Sí",
	"false": "No",
	"enable": "Habilitar haciendo clic aquí",
	"disable": "Deshabilitar haciendo clic aquí",
	"notice": {
		"save_success": "¡Agente guardado exitosamente!",
		"deleteDataTitle": "Confirmación",
		"deleteData": "¿Está seguro que desea eliminar los datos actuales y sus subdatos?",
		"changeTypeTitle": "Confirmación",
		"changeTypeDeleteChildren": "Cambiar el tipo de datos eliminará todos los subdatos. ¿Está seguro que desea continuar?",
		"changeType": "Cambiar el tipo de datos cambiará su valor. ¿Está seguro que desea continuar?",
		"inputTypeError": "El tipo de dato que ingresó es inválido, por favor ingrese uno válido.",
		"sameKey": "El nombre del elemento que ingresó ya existe en el objeto, por favor ingrese otro nombre.",
		"emptyKey": "El nombre del elemento no puede estar vacío, por favor ingrese un nombre.",
		"numberKey": "El nombre del elemento no puede ser un número, por favor ingrese otro nombre.",
		"inputName": "Ingrese el nombre del agente.",
		"inputCron": "La expresión cron para tareas programadas no puede estar vacía."
	},
	"comment": {
		"entityManager": "Administrador de Entidades",
		"applications": "Acceso a servicios dentro del sistema",
		"organization": "Acceso a la organización",
		"org": "Métodos rápidos de acceso a la organización",
		"service": "Cliente de servicios web"
	},
	"formToolbar": {
		"save": "Guardar secuencia de comandos",
		"autoSave": "AutoGuardar",
		"fontSize": "Tamaño de fuente",
		"style": "estilo",
		"scriptEditor": "Editor de guiones",
		"viewAllVersions": "Ver todas las versiones de script"
	}
}
MWF.xApplication.service.AgentDesigner["lp."+o2.language] = MWF.xApplication.service.AgentDesigner.LP