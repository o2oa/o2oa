$(function () {

    $('#add_btn').click(function () {
        methods.addHandle()
    })

    $('#show_tbody').on('click','.edit', function () {
        trIndex = $('.edit', '#show_tbody').index($(this));
        addEnter = false;
        $(this).parents('tr').addClass('has_case');
        methods.editHandle(trIndex);
    })

    $('#search_btn').click(function () {
        methods.seachName();
    })

    $('#back_btn').click(function () {
        $('#Ktext').val(' ');
        methods.resectList();
    })

    $('.del').click(function () {
        $(this).parents('tr').remove();
    })

    $('#renyuan').on('hide.bs.modal',function() {
        addEnter = true;
        $('#show_tbody tr').removeClass('has_case');
        $('#xztb input').val(' ');
        $('#xztb select').find('option:first').prop('selected', true)
    });

})

var addEnter = true,
    noRepeat = true,
    jobArr = [],
    phoneArr = [],
    tdStr = '',
    trIndex,
    hasNullMes = false,
    tarInp = $('#xztb input'),
    tarSel = $('#xztb select');

var methods = {

    addHandle: function (the_index) {
        hasNullMes = false;
        methods.checkMustMes();
        if (hasNullMes) {
            return;
        }
        if (addEnter) {
            methods.checkRepeat();
            if (noRepeat) {
                methods.setStr();
                $('#show_tbody').append('<tr>' + tdStr + '</tr>');
                $('#renyuan').modal('hide');
            }
        }else{
            methods.setStr();
            $('#show_tbody tr').eq(trIndex).empty().append(tdStr);
            $('#renyuan').modal('hide');
        }
    },
    editHandle: function (the_index) {

        var tar = $('#show_tbody tr').eq(the_index);
        var nowConArr = [];
        for (var i=0; i<tar.find('td').length-1;i++) {
            var a = tar.children('td').eq(i).html();
            nowConArr.push(a);
        }

        $('#renyuan').modal('show');

        for (var j=0;j<tarInp.length;j++) {
            tarInp.eq(j).val(nowConArr[j])
        }
        for (var p=0;p<tarSel.length;p++) {
            var the_p = p+tarInp.length;
            tarSel.eq(p).val(nowConArr[the_p]);
        }

    },
    setStr: function () {

        tdStr = '';
        for (var a=0; a<tarInp.length; a++) {
            tdStr+= '<td>' + tarInp.eq(a).val() + '</td>'
        }
        for (var b=0; b<tarSel.length; b++) {
            tdStr+= '<td>' + tarSel.eq(b).val() + '</td>'
        }
        tdStr+= '<td><a href="#" class="edit">编辑</a> <a href="#" class="del">删除</a></td>';

    },
    seachName: function () {

        var a = $('#show_tbody tr');
        var nameVal = $('#Ktext').val().trim();
        var nameStr = '',
            nameArr = [];

        if (nameVal==='') {
            bootbox.alert({
                title: "来自火星的提示",
                message: "搜索内容不能为空",
                closeButton:false
            })
            return;
        }

        for (var c=0;c<a.length;c++) {
            var txt = $('td:first', a.eq(c)).html().trim();
            nameArr.push(txt);
        }

        a.hide();
        for (var i=0;i<nameArr.length;i++) {
            if (nameArr[i].indexOf(nameVal)>-1) {
                a.eq(i).show();
            }
        }
    },
    resectList: function () {
        $('#show_tbody tr').show();
    },
    checkMustMes: function () {

        if ($('.userName').val().trim()==='') {
            bootbox.alert({
                title: "来自火星的提示",
                message: "姓名为必选项，请填写",
                closeButton:false
            })
            hasNullMes = true;
            return
        }
        if ($('.jobNum').val().trim()==='') {
            bootbox.alert({
                title: "来自火星的提示",
                message: "工号为必选项，请填写",
                closeButton:false
            })
            hasNullMes = true;
            return
        }
        if ($('.phoneNum').val().trim()==='') {
            bootbox.alert({
                title: "来自火星的提示",
                message: "手机号为必选项，请填写",
                closeButton:false
            })
            hasNullMes = true;
            return
        }

    },
    checkRepeat: function () {

        jobArr = [], phoneArr = [];

        for (var i = 0; i<$('#show_tbody tr:not(".has_case")').length;i++) {
            var par = '#show_tbody tr:not(".has_case"):eq(' + i + ')';
            var a = $('td:eq(1)', par).html().trim(),
                b = $('td:eq(2)', par).html().trim();
            jobArr.push(a);
            phoneArr.push(b);
        }
        var jobNum = $('.jobNum').val().trim(),
            phoneNum = $('.phoneNum').val().trim();

        if (jobArr.indexOf(jobNum)>-1) {
            noRepeat = false;
            bootbox.alert({
                title: "来自火星的提示",
                message: "工号重复了，请重新输入",
                closeButton:false
            })
            return;
        }
        if (phoneArr.indexOf(phoneNum)>-1) {
            noRepeat = false;
            bootbox.alert({
                title: "来自火星的提示",
                message: "手机号码重复了，请重新输入",
                closeButton:false
            })
            return;
        }
        noRepeat = true;
    }
}