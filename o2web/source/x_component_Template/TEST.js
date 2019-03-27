this.define("loadBranchGrid", function (dateList) {
    return;
    var field = this.form.get("branchTaskPerson");
    var branchList = field.getData();
    var unit = this.data.county || this.workContext.getWork().creatorDepartment;
    MWF.xDesktop.requireApp("Template", "MGrid", function () {
        this.dateGrid = new MGrid(this.form.get("branchGridContainer").node, dateList || null, {
            style: "report",
            isEdited: this.isEdited || this.isNew,
            hasOperation: true,
            minTrCount: 1,
            tableAttributes: {width: "550px", border: "0", cellpadding: "5", cellspacing: "0"},
            itemTemplate: {
                branch: {
                    type: "innerText"
                },
                person: {
                    type: "org",
                    orgType: "identity",
                    unit: unit,
                    notEmpty: true
                }
            }
        })
    }, this.app);
    this.dateGrid.load();
})


