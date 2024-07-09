MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.service = MWF.xApplication.service || {};
MWF.xApplication.service.DictionaryDesigner = MWF.xApplication.service.DictionaryDesigner || {};
MWF.xApplication.service.DictionaryDesigner.LP={
	"title": "Edición del diccionario de datos",
	"newDictionary": "Nuevo diccionario de datos",
	"property": "Atributos",
	"item": "Proyecto",
	"type": "Tipo",
	"value": "Valor",
	"dictionary": "Diccionario de datos",
	"search": "Buscar",
	"next": "Siguiente",
	"id": "Logotipo",
	"name": "Nombre",
	"alias": "Alias",
	"description": "Descripción",
	"projectionType": "Tipo de mapeo",
	"projectionProcess": "Proceso correspondiente",
	"design": "Diseño",
	"script": "Guion",
	"notice": {
		"save_success": "¡¡ el diccionario de datos se salvó con éxito!",
		"deleteDataTitle": "Eliminar la confirmación de datos",
		"deleteData": "¿¿ está seguro de eliminar los datos actuales y sus subdata?",
		"changeTypeTitle": "Cambiar la confirmación del tipo de datos",
		"changeTypeDeleteChildren": "¿Cambiar el tipo de datos eliminará todos los subdatos, ¿ está seguro de que quiere ejecutar?",
		"changeType": "¿Cambiar el tipo de datos cambiará el valor de los datos, ¿ está seguro de que quiere ejecutarse?",
		"inputTypeError": "El tipo de datos que introdujo es incorrecto, vuelva a ingresar",
		"sameKey": "El nombre del proyecto que ha introducido ya existe en el objeto, vuelva a introducirlo",
		"emptyKey": "El nombre del proyecto no puede estar vacío, vuelva a ingresar",
		"numberKey": "El nombre del proyecto no puede ser un número, vuelva a ingresar",
		"sameObjectKey": "Se repite el nombre del proyecto",
		"emptyObjectKey": "El nombre del proyecto no puede estar vacío",
		"numberObjectKey": "El nombre del proyecto no puede ser un número",
		"editorNotValidated": "Por favor, corrija el error del editor primero.",
		"inputName": "Introduzca el nombre y el alias del diccionario de datos",
		"noModifyName": "No se puede modificar el nombre o el alias",
		"jsonParseError": "El formato json es incorrecto, por favor corrija primero"
	},
	"isSave": "Se está guardando, por favor Espere...",
	"formToolbar": {
		"save": "Guardar diccionario de datos",
		"saveas": "El diccionario de datos se guarda como",
		"help": "Ayudar",
		"search": "Buscar",
		"autoSave": "Guardar automáticamente"
	}
}
MWF.xApplication.service.DictionaryDesigner["lp."+o2.language] = MWF.xApplication.service.DictionaryDesigner.LP