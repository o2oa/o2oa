MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.portal = MWF.xApplication.portal || {};
MWF.xApplication.portal.ScriptDesigner = MWF.xApplication.portal.ScriptDesigner || {};
MWF.xApplication.portal.ScriptDesigner.LP={
	"title": "Edición de script",
	"newScript": "Crear nuevo script",
	"scriptLibrary": "Biblioteca de scripts",
	"property": "Propiedades",
	"include": "Incluir",
	"id": "Identificador",
	"name": "Nombre",
	"alias": "Alias",
	"description": "Descripción",
	"notice": {
		"save_success": "¡El script se ha guardado correctamente!",
		"deleteDataTitle": "Confirmación",
		"deleteData": "¿Está seguro de que desea eliminar los datos actuales y sus subdatos?",
		"changeTypeTitle": "Confirmación",
		"changeTypeDeleteChildren": "Cambiar el tipo de datos eliminará todos los subdatos. ¿Está seguro de que desea continuar?",
		"changeType": "Cambiar el tipo de datos cambiará el valor del dato. ¿Está seguro de que desea continuar?",
		"inputTypeError": "El tipo de datos que ha ingresado es incorrecto. Por favor ingrese nuevamente.",
		"sameKey": "El nombre del proyecto que ha ingresado ya existe en el objeto. Por favor ingrese uno nuevo.",
		"emptyKey": "El nombre del proyecto no puede estar vacío. Por favor ingrese uno nuevo.",
		"numberKey": "El nombre del proyecto no puede ser un número. Por favor, ingrese uno nuevo.",
		"inputName": "Ingrese el nombre del script"
	},
	"version": {
		"title": "Ver historial de versiones del script",
		"close": "Cerrar",
		"no": "N°",
		"updateTime": "Tiempo de actualización",
		"op": "Operación",
		"resume": "Reanudar",
		"resumeConfirm": "Confirmación de reanudación",
		"resumeInfo": "¿Está seguro de que desea restaurar el script? Después de la confirmación, el script actual se actualizará y deberá guardarlo manualmente para que surta efecto.",
		"resumeSuccess": "¡Restaurado con éxito!"
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
MWF.xApplication.portal.ScriptDesigner["lp."+o2.language] = MWF.xApplication.portal.ScriptDesigner.LP