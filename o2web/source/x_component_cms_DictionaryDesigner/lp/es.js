MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.DictionaryDesigner = MWF.xApplication.cms.DictionaryDesigner || {};
MWF.xApplication.cms.DictionaryDesigner.LP={
	"title": "Edición de diccionario de datos",
	"newDictionary": "Crear nuevo diccionario de datos",
	"property": "Propiedad",
	"item": "Elemento",
	"type": "Tipo",
	"value": "Valor",
	"dictionary": "Diccionario de datos",
	"search": "Buscar",
	"next": "Siguiente",
	"id": "Identificación",
	"name": "Nombre",
	"alias": "Alias",
	"description": "Descripción",
	"projectionType": "Tipo de mapeo",
	"projectionProcess": "Proceso correspondiente",
	"design": "Diseño",
	"script": "Script",
	"notice": {
		"save_success": "¡Se ha guardado el diccionario de datos con éxito!",
		"deleteDataTitle": "Confirmación",
		"deleteData": "¿Está seguro de que desea eliminar los datos actuales y sus subdatos?",
		"changeTypeTitle": "Confirmación",
		"changeTypeDeleteChildren": "Cambiar el tipo de datos eliminará todos los subdatos. ¿Está seguro de que desea continuar?",
		"changeType": "Cambiar el tipo de datos modificará el valor del dato. ¿Está seguro de que desea continuar?",
		"inputTypeError": "El tipo de datos que ingresó es incorrecto, intente nuevamente",
		"sameKey": "El nombre del elemento que ingresó ya existe en el objeto, ingrese uno nuevo",
		"emptyKey": "El nombre del elemento no puede estar vacío, ingrese uno nuevo",
		"numberKey": "El nombre del elemento no puede ser un número, ingrese uno nuevo",
		"sameObjectKey": "El nombre del elemento se repite",
		"emptyObjectKey": "El nombre del elemento no puede estar vacío",
		"numberObjectKey": "El nombre del elemento no puede ser un número",
		"editorNotValidated": "Por favor corrija primero los errores reportados por el editor",
		"inputName": "Ingrese el nombre del diccionario de datos",
		"inputAlias": "Ingrese el alias del diccionario de datos",
		"noModifyName": "No puede modificar el nombre o el alias.",
		"jsonParseError": "Error en el formato JSON. Por favor, corrijalo primero."
	},
	"isSave": "Guardando, por favor espere...",
	"selectOrganizationByDblclick": "Haga doble clic para seleccionar una organización.",
	"formToolbar": {
		"save": "Guardar diccionario de datos",
		"saveas": "Guardar diccionario de datos como",
		"help": "ayuda",
		"search": "Buscar",
		"autoSave": "AutoGuardar"
	}
}
MWF.xApplication.cms.DictionaryDesigner["lp."+o2.language] = MWF.xApplication.cms.DictionaryDesigner.LP