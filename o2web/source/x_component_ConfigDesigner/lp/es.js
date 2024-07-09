MWF.xApplication = MWF.xApplication || {};
MWF.xApplication.ConfigDesigner = MWF.xApplication.ConfigDesigner || {};
MWF.xApplication.ConfigDesigner.LP={
	"title": "Configuración de la plataforma",
	"newScript": "Nueva configuración",
	"scriptLibrary": "Lista de configuraciones",
	"property": "Propiedad",
	"include": "Incluir",
	"id": "Identificación",
	"name": "Nombre",
	"node": "Nodo",
	"alias": "Alias",
	"description": "Descripción",
	"notice": {
		"save_success": "¡La configuración se ha guardado con éxito!",
		"deleteDataTitle": "Confirmar",
		"deleteData": "¿Está seguro de que desea eliminar estos datos y sus subdatos?",
		"changeTypeTitle": "Confirmar",
		"changeTypeDeleteChildren": "Cambiar el tipo de datos eliminará todos los subdatos, ¿está seguro de que desea continuar?",
		"changeType": "Cambiar el tipo de datos cambiará el valor de los datos, ¿está seguro de que desea continuar?",
		"inputTypeError": "El tipo de datos que ha ingresado es incorrecto, por favor ingrese uno válido.",
		"sameKey": "El nombre del elemento que ha ingresado ya existe en el objeto, por favor ingrese uno diferente.",
		"emptyKey": "El nombre del elemento no puede estar vacío, por favor ingrese uno válido.",
		"numberKey": "El nombre del elemento no puede ser un número, por favor ingrese uno válido.",
		"inputName": "Ingrese el nombre de la configuración"
	},
	"formToolbar": {
		"save": "Guardar secuencia de comandos",
		"autoSave": "AutoGuardar",
		"fontSize": "Tamaño de fuente",
		"style": "estilo",
		"scriptEditor": "Editor de guiones"
	}
}
MWF.xApplication.ConfigDesigner["lp."+o2.language] = MWF.xApplication.ConfigDesigner.LP