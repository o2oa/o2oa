MWF.SES = MWF.xApplication.ScriptEditor = MWF.xApplication.ScriptEditor || {};
MWF.xApplication.ScriptEditor.block = MWF.xApplication.ScriptEditor.block || {};
MWF.xApplication.ScriptEditor.block.control = MWF.xApplication.ScriptEditor.block.control || {};
MWF.xApplication.ScriptEditor.block.function = MWF.xApplication.ScriptEditor.block.function || {};
MWF.xApplication.ScriptEditor.block.variable = MWF.xApplication.ScriptEditor.block.variable || {};
MWF.xApplication.ScriptEditor.block.operator = MWF.xApplication.ScriptEditor.block.operator || {};
MWF.xApplication.ScriptEditor.block.object = MWF.xApplication.ScriptEditor.block.object || {};
MWF.xApplication.ScriptEditor.block.string = MWF.xApplication.ScriptEditor.block.string || {};
MWF.xApplication.ScriptEditor.block.date = MWF.xApplication.ScriptEditor.block.date || {};
MWF.xApplication.ScriptEditor.block.array = MWF.xApplication.ScriptEditor.block.array || {};
MWF.xApplication.ScriptEditor.block.number = MWF.xApplication.ScriptEditor.block.number || {};
MWF.xApplication.ScriptEditor.block.json = MWF.xApplication.ScriptEditor.block.json || {};
MWF.xApplication.ScriptEditor.block.dom = MWF.xApplication.ScriptEditor.block.dom || {};
MWF.xApplication.ScriptEditor.block.ajax = MWF.xApplication.ScriptEditor.block.ajax || {};

MWF.xApplication.ScriptEditor.block.form = MWF.xApplication.ScriptEditor.block.form || {};

MWF.xDesktop.requireApp("ScriptEditor", "block.$Block", null, false);
//MWF.xDesktop.requireApp("ScriptEditor", "block.control.Function", null, false);
//MWF.xDesktop.requireApp("ScriptEditor", "block.control.If", null, false);

// MWF.xDesktop.requireApp("ScriptEditor", "block.variable.Var", null, false);
// MWF.xDesktop.requireApp("ScriptEditor", "block.variable.Set", null, false);
// MWF.xDesktop.requireApp("ScriptEditor", "block.variable.Variable", null, false);
//控制/语句
MWF.xApplication.ScriptEditor.block.control.MainFunction = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Top
});
MWF.xApplication.ScriptEditor.block.control.DefineFunction = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Top
});
MWF.xApplication.ScriptEditor.block.control.Call = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.control.Execute = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.control.Function = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.control.If = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Around
});
MWF.xApplication.ScriptEditor.block.control.IfElse = new Class({ //todo
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Around
});
MWF.xApplication.ScriptEditor.block.control.Each = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Around
});
MWF.xApplication.ScriptEditor.block.control.For = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Around
});
MWF.xApplication.ScriptEditor.block.control.While = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Around
});
MWF.xApplication.ScriptEditor.block.control.Break = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.control.Continue = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.control.Try = new Class({ //todo
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Around
});
MWF.xApplication.ScriptEditor.block.control.Throw = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.control.Return = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});

//函数
MWF.xApplication.ScriptEditor.block.function.Eval = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.function.IsNaN = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.function.ParseFloat = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.function.ParseInt = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.function.EncodeURI = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.function.EncodeURIComponent = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.function.DecodeURI = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.function.DecodeURIComponent = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});

//变量
MWF.xApplication.ScriptEditor.block.variable.Var = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.variable.Set = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.variable.Variable = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.variable.New = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.variable.This = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.variable.Typeof = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.variable.Instanceof = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.variable.In = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.variable.NaN = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.variable.Delete = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});


//运算符
MWF.xApplication.ScriptEditor.block.operator.Assign = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.operator.AddAssign = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.operator.SubAssign = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.operator.MulAssign = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.operator.DivAssign = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.operator.ExpAssign = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});
MWF.xApplication.ScriptEditor.block.operator.ModAssign = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Operation
});

MWF.xApplication.ScriptEditor.block.operator.Add = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Sub = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Mul = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Div = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Increment = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Decrement = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Exponentiation = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});

MWF.xApplication.ScriptEditor.block.operator.Gt = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.GtEqual = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Lt = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.LtEqual = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Equal = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Inequal = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.StrictEqual = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.StrictInequal = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});

MWF.xApplication.ScriptEditor.block.operator.And = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Or = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Not = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});

MWF.xApplication.ScriptEditor.block.operator.Grouping = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Ternary = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Random = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.operator.Dot = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});

//Object
MWF.xApplication.ScriptEditor.block.object.New = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.object.Each = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Around
});
MWF.xApplication.ScriptEditor.block.object.Merge = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.object.Clone = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.object.Append = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.object.Subset = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
// MWF.xApplication.ScriptEditor.block.object.Map = new Class({
//     Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
// });
// MWF.xApplication.ScriptEditor.block.object.Filter = new Class({
//     Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
// });
// MWF.xApplication.ScriptEditor.block.object.Every = new Class({
//     Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
// });
// MWF.xApplication.ScriptEditor.block.object.Some = new Class({
//     Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
// });
MWF.xApplication.ScriptEditor.block.object.Keys = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.object.Values = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.object.GetLength = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.object.KeyOf = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.object.Contains = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.object.ToQueryString = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
MWF.xApplication.ScriptEditor.block.object.ToString = new Class({
    Extends: MWF.xApplication.ScriptEditor.block.$Block.$Expression
});
//MWF.xDesktop.requireApp("ScriptEditor", "block.form.Get", null, false);

MWF.xDesktop.requireApp("ScriptEditor", "block.ajax.NewO2", null, false);