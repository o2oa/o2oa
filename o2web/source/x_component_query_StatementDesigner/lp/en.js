MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.StatementDesigner = MWF.xApplication.query.StatementDesigner || {};
if(!MWF.APPDSMD)MWF.APPDSMD = MWF.xApplication.query.StatementDesigner;
MWF.xApplication.query.ViewDesigner = MWF.xApplication.query.ViewDesigner || {};
MWF.xDesktop.requireApp("query.ViewDesigner", "lp."+MWF.language, null, false);
MWF.xApplication.query.StatementDesigner.LP = Object.merge( MWF.xApplication.query.ViewDesigner.LP, {
    "title": "Query Design",
    "newStatement": "New Statement",
    "unCategory": "Uncategorized",
    "statement": "Statement",
    "property": "Property",
    "run": "Run",
    "runTest": "Test Statement",
    "statementType": "Statement Type",
    "statementTable": "Data Table",
    "selectTable": "Select Data Table",
    "save_success": "The query configuration was saved successfully!",
    "inputStatementName": "Please enter the query configuration name",
    "inputStatementData": "Please edit the statement",
    "saveStatementNotice": "Please save!",
    "noViewNotice": "The view has not been created, please create the view!",
    "previewNotSelectStatementNotice": "Only when the statement type is ‘Select’ can it be previewed",
    "field": "Field",
    "fileldSelectNote": "-Insert a field after select-",

    "statementFormat": "How to create a statement:",
    "statementJpql": "JPQL",
    "statementScript": "JPQL Script",
    "nativeSql": "SQL",
    "nativeSqlScript": "SQL Script",

    "statementCategory": "Access Object Type",
    "scriptTitle": "Create JPQL by script",
    "sqlScriptTitle": "Create SQL by script",
    "countMethod": "Count Statement",

    "jpqlType": "JPQL Type",
    "jpqlFromResult": "Query start entry",
    "jpqlMaxResult": "Maximum return result",
    "jpqlSelectTitle": "JPQL Statement",
    "inputWhere": "You can enter the Where clause in the edit box below",
    "jpqlRunSuccess": "JPQL executed successfully",
    "newLineSuccess": "Insert data successfully",
    "newLineJsonError": "Insert data error, data format is wrong",
    "queryStatement": "Query Statement",
    "countStatement": "Count Statement",

    "currentPerson":"currentPerson",
    "currentIdentity":"Current Identity",
    "currentPersonDirectUnit":"The current person's direct organization",
    "currentPersonAllUnit":"All organizations where the current person belongs",
    "currentPersonGroupList": "The current person's group",
    "currentPersonRoleList": "Roles owned by the current person",
    "defaultCondition": "Automatic assignment condition:",

    "ignore": "Ignore",
    "auto": "Auto",
    "assign": "Assign",

    "mastInputParameter": "Please enter a parameter",
    "pathExecption": "The path is written as \"table alias. Field name\", the format is incorrect",

    "systemTable":"System Table",
    "customTable":"self-built table",
    "taskInstance":"Process Task",
    "taskCompletedInstance": "Process TaskCompleted",
    "readInstance":"Process Read",
    "readedInstance":"Process Readed",
    "workInstance":"Process Work",
    "workCompletedInstance":"Process workCompleted",
    "reviewInstance":"Process Review",
    "documentInstance":"CMS Document",

    "propertyTemplate": {

        "idPath": "idPath",
        "idPathNote": "Note: Refers to the path of the Id (cms document id/process work id) relative to a single piece of data, used to open the document.",
        "selectPath": "Select Path",
        "selectPathNote": "Note: Fill in the query statement correctly, and then test the statement or refresh the view data to display (refresh) the selected path.",
        "dataPathNote": "Note: Refers to the path of the column relative to a single data. For example, 0, title, or 0.title",
        "executionAuthority": "Execution Authority",
        "anonymousAccess": "Anonymous Access",
        "allowed": "Allowed",
        "disAllowed": "Not allowed",
        "executePerson": "executor",
        "executeUnit": "Executive Unit",

        "hidden": "Hidden",
        "orderNumber":"Order Number",

        "parameter":"Parameter",
        "parameterNote":"Note: The parameter corresponds to the where condition in the query statement and the total number statement like \":field\", fill in \"field\".",
        "pathNote":"Note: The path is written as \"table alias. field name\", such as: o.title",
        "userInput":"User Input",

        "export": "Export",
        "exportWidth": "Width",
        "exportEnable": "Allow export",
        "isTime": "Time Type",
        "isNumber": "Number Type"
    }
});