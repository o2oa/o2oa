
const viewLookup = async (view, callback)=>{
    return await this.Actions.load('x_query_assemble_surface').ViewAction.executeWithQueryV2(
        view.view,
        view.application,
        view.page || 1,
        view.pageSize || 2000,
        {
            filterList: view.filter || [],
            orderList: view.orderList
        },
        function(json){
            var data = {
                "grid": json.data.grid || json.data.groupGrid,
                "groupGrid": json.data.groupGrid
            };
            if (callback) callback(data);
            return data;
        }
    )
}

const queryPositionView = async (filterList = [])=>{
    return await viewLookup({
        view: 'positionQuery',
        application: "org-unit",
        filter: [{
            "logic": "and",
            "path": "status",
            "comparison":"=",
            "value": '有效',
            "formatType": "textValue"
        },
            ...filterList]
    });
};

const queryPositionStatement = async (parameter = {})=>{
    return await this.statement.execute({
        "name" : "queryPositionTree",
        "mode" : "data",
        "page" : 1,
        "pageSize" : 2000,
        parameter : parameter
    })
};

const queryPositionByUnit = async (unitDn)=>{
    return await queryPositionView([{
        "logic": "and",
        "path": "unit.*",
        "comparison":"=",
        "value": unitDn,
        "formatType": "textValue"
    }])
}

const queryAllPositionsByUnit = async (unitDn)=>{
    const unitList = this.org.listSubUnit( unitDn, true ).map(u=>u.distinguishedName);
    unitList.unshift(unitDn);
    return await queryPositionView([{
        "logic": "and",
        "path": "unit.*",
        "comparison":"in",
        "value": unitList.join(','),
        "formatType": "textValue"
    }])
}

const queryPositionTree = async (parentPositionId, includeSelf)=>{
    let deep = 1;
    const maxDeep = 5;
    const result = [];

    const getSelf = async ()=>{
        const json = await queryPositionStatement({"id" : parentPositionId})
        result.push(...json.data);
    }

    const getChildren = async (id)=>{
        const json = await queryPositionStatement({"superiorPositionId" : id});
        return json.data;
    }

    // 递归获取整棵树（安全深度限制）
    const getTree = async (parentId, currentDeep) => {
        if (currentDeep > maxDeep) return;

        const children = await getChildren(parentId);
        if (!children || children.length === 0) return;

        // 把当前层节点加入结果
        result.push(...children);

        // 继续递归下一层
        children.forEach(async node => {
            await getTree(node.id, currentDeep + 1);
        });
    };

    // 开始执行
    if(includeSelf)await getSelf();
    await getTree(parentPositionId, deep);

    return result;
}

this.define('queryPositionByUnit', async (unitDn)=>{
    return await queryPositionByUnit(unitDn);
})

this.define('queryAllPositionsByUnit', async (unitDn)=>{
    return await queryAllPositionsByUnit(unitDn);
})

this.define('queryPositionTree',async (parentPositionId, includeSelf)=>{
    return await queryPositionTree(parentPositionId, includeSelf);
})

// this.define('disablAllPositionByUnit', async (unitDn)=>{
//     const positions = await queryAllPositionsByUnit(unitDn);

// })