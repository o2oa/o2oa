MWF.SES = MWF.xApplication.ScriptEditor = MWF.xApplication.ScriptEditor || {};
MWF.xApplication.ScriptEditor.statement = MWF.xApplication.ScriptEditor.statement || {};
MWF.xApplication.ScriptEditor.statement.variable = MWF.xApplication.ScriptEditor.statement.variable || {};
MWF.xApplication.ScriptEditor.statement.control = MWF.xApplication.ScriptEditor.statement.control || {};
MWF.xApplication.ScriptEditor.statement.function = MWF.xApplication.ScriptEditor.statement.control || {};
MWF.xApplication.ScriptEditor.statement.operator = MWF.xApplication.ScriptEditor.statement.operator || {};
MWF.xApplication.ScriptEditor.statement.object = MWF.xApplication.ScriptEditor.statement.object || {};
MWF.xApplication.ScriptEditor.statement.string = MWF.xApplication.ScriptEditor.statement.string || {};
MWF.xApplication.ScriptEditor.statement.date = MWF.xApplication.ScriptEditor.statement.date || {};
MWF.xApplication.ScriptEditor.statement.array = MWF.xApplication.ScriptEditor.statement.array || {};
MWF.xApplication.ScriptEditor.statement.number = MWF.xApplication.ScriptEditor.statement.number || {};
MWF.xApplication.ScriptEditor.statement.json = MWF.xApplication.ScriptEditor.statement.json || {};
MWF.xApplication.ScriptEditor.statement.dom = MWF.xApplication.ScriptEditor.statement.dom || {};
MWF.xApplication.ScriptEditor.statement.ajax = MWF.xApplication.ScriptEditor.statement.ajax || {};

MWF.xApplication.ScriptEditor.statement.form = MWF.xApplication.ScriptEditor.statement.form || {};

MWF.xDesktop.requireApp("ScriptEditor", "statement.$Statement", null, false);
MWF.xDesktop.requireApp("ScriptEditor", "statement.Link", null, false);
MWF.xDesktop.requireApp("ScriptEditor", "statement.Mortise", null, false);

//控制/语句
MWF.xApplication.ScriptEditor.statement.control.MainFunction = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Top
});
MWF.xDesktop.requireApp("ScriptEditor", "statement.control.DefineFunction", null, false);
MWF.xApplication.ScriptEditor.statement.control.Function = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.control.Call = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.control.Execute = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xDesktop.requireApp("ScriptEditor", "statement.control.If", null, false);
MWF.xApplication.ScriptEditor.statement.control.IfElse = new Class({ //todo
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Around
});
MWF.xApplication.ScriptEditor.statement.control.Each = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Around
});
MWF.xApplication.ScriptEditor.statement.control.For = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Around
});
MWF.xApplication.ScriptEditor.statement.control.While = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Around
});
MWF.xApplication.ScriptEditor.statement.control.Break = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.control.Continue = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.control.Try = new Class({ //todo
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Around
});
MWF.xApplication.ScriptEditor.statement.control.Throw = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.control.Return = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});

//函数
MWF.xApplication.ScriptEditor.statement.function.Eval = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.function.IsNaN = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.function.ParseFloat = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.function.ParseInt = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.function.EncodeURI = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.function.EncodeURIComponent = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.function.DecodeURI = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.function.DecodeURIComponent = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});

//变量
MWF.xDesktop.requireApp("ScriptEditor", "statement.variable.Var", null, false);
MWF.xDesktop.requireApp("ScriptEditor", "statement.variable.Set", null, false);
MWF.xDesktop.requireApp("ScriptEditor", "statement.variable.Variable", null, false);
MWF.xApplication.ScriptEditor.statement.variable.New = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.variable.This = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.variable.Typeof = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.variable.Instanceof = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.variable.In = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.variable.NaN = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.variable.Delete = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});

//运算符
MWF.xApplication.ScriptEditor.statement.operator.Assign = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.operator.AddAssign = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.operator.SubAssign = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.operator.MulAssign = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.operator.DivAssign = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.operator.ExpAssign = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});
MWF.xApplication.ScriptEditor.statement.operator.ModAssign = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Operation
});

MWF.xApplication.ScriptEditor.statement.operator.Add = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Sub = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Mul = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Div = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Increment = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Decrement = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Exponentiation = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});

MWF.xApplication.ScriptEditor.statement.operator.Gt = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.GtEqual = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Lt = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.LtEqual = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Equal = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Inequal = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.StrictEqual = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.StrictInequal = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});

MWF.xApplication.ScriptEditor.statement.operator.And = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Or = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Not = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});

MWF.xApplication.ScriptEditor.statement.operator.Grouping = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Ternary = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Random = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.operator.Dot = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});

//Object
MWF.xApplication.ScriptEditor.statement.object.New = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.Each = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Around
});
MWF.xApplication.ScriptEditor.statement.object.Merge = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.Clone = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.Append = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.Subset = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.Map = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.Filter = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.Every = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.Some = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.Keys = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.Values = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.GetLength = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.KeyOf = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.Contains = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.ToQueryString = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});
MWF.xApplication.ScriptEditor.statement.object.ToString = new Class({
    Extends: MWF.xApplication.ScriptEditor.statement.$Statement.$Expression
});