<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>字典信息-修改</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/im.css" media="all">
    <style type="text/css">
        button {
            margin-top: 8px;
        }
    </style>
</head>
<body>

<div class="layui-form" style="padding: 20px 30px 0 0;">
    <div class="layui-form-item">
        <label class="layui-form-label">字典代码</label>
        <div class="layui-input-inline">
            <input type="text" class="layui-input layui-detail" disabled="disabled" name="dictionaryCode">
        </div>
        <label class="layui-form-label">字典描述</label>
        <div class="layui-input-inline">
            <input type="text" class="layui-input layui-detail" disabled="disabled" name="dictionaryCodeCaption">
        </div>
        <button class="layui-btn layui-btn-xs layuiadmin-btn-dictionary-add">
            <i class="layui-icon layui-icon-addition"></i>
        </button>
    </div>
    <div class="layui-form-item dictionary"></div>
    <div class="layui-form-item layui-hide">
        <input type="button" lay-submit lay-filter="LAY-app-dictionary-update" id="LAY-app-dictionary-update"/>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'laydate', 'admin', 'im'], function () {
        var $ = layui.$,
            form = layui.form,
            admin = layui.admin,
            im = layui.im;

        // 应用名称
        var appName = '${appName}';

        var dictionaryCode = im.getUrlParameter("dictionaryCode");
        var dictionaryCaption = decodeURI(im.getUrlParameter("dictionaryCaption"));
        var isOpen = im.getUrlParameter("isOpen");
        var userList = [];

        var request = {
            dictionaryCode: dictionaryCode,
            isTranslate: im.getUrlParameter("isTranslate")
        }

        var url = {
            load: appName + "/dictionary/selectOne?" + $.param(request)
        };

        // 初始化页面信息
        var loading = layer.load(2, {shade: false});
        admin.req({
            url: url.load,
            type: "get",
            dataType: "json",
            done: function (response) {
                form.render();
                if (response.bizResult) {
                    addDictionary(response.data);
                } else {
                    im.msg(response.msg);
                }
                layer.close(loading);
            }
        });

        // 绑定新增事件
        $(document).on('click', 'button.layuiadmin-btn-dictionary-add', function () {
            $(".dictionary").prepend(addItem(userList, '', getItemValue(), '', getItemOrder()));
            form.render();
        });

        // 绑定删除事件
        $(document).on('click', 'button.layuiadmin-btn-dictionary-delete', function () {
            $(this).parent().remove();
        });

        // 获取字典项最大值
        function getItemValue() {
            var value = 0;
            $(".dictionary .layui-form-item-dictionary input[name='dictionaryItem']").each(function () {
                var val = $(this).val();
                if(val.length != 14){
                    if(parseInt(val) > value){
                        value = parseInt(val);
                    }
                }
            });
            return ++value;
        }

        // 获取字典项最大排序
        function getItemOrder() {
            var value = 0;
            $(".dictionary .layui-form-item-dictionary input[name='itemOrder']").each(function () {
                var val = $(this).val();
                if(val.length != 14){
                    if(parseInt(val) > value){
                        value = parseInt(val);
                    }
                }
            });
            return ++value;
        }

        // 添加字典项
        function addDictionary(data) {
            if (!$.isEmptyObject(data)) {
                userList = data.user;
                for (var i = 0; i < data.dictionary.length; i++) {
                    $(".dictionary").append(addItem(data.user, data.dictionary[i].userId, data.dictionary[i].dictionaryItem, data.dictionary[i].dictionaryCaption, data.dictionary[i].itemOrder));
                    form.render();
                }
            }
            $("input[name='dictionaryCode']").val(dictionaryCode);
            $("input[name='dictionaryCodeCaption']").val(dictionaryCaption);
            // 是否开放状态按钮状态控制
            if ("0" == isOpen) {
                $("button.layuiadmin-btn-dictionary-add").hide();
                $("button.layuiadmin-btn-dictionary-delete").hide();
                $("input[name='dictionaryItem']").addClass("layui-detail");
                $("input[name='dictionaryItem']").attr("disabled", true);
                $("select[name='userId']").parent().prev().hide();
                $("select[name='userId']").parent().hide();
            }
            // 系统用户随礼人信息不能修改删除
            $(".dictionary .layui-form-item-dictionary").each(function () {
                var dictionaryItem = $(this).find("input[name='dictionaryItem']").val();
                for (var i = 0; i < userList.length; i++) {
                    var userId = userList[i].userId;
                    if (userId == dictionaryItem) {
                        $(this).find("button.layuiadmin-btn-dictionary-delete").hide();
                        $(this).find("[name]").addClass("layui-detail");
                        $(this).find("[name]").attr("disabled", true);
                    }
                }
                form.render();
            });
        }

        // 添加元素
        function addItem(userList, userId, dictionaryItem, dictionaryCaption, dictionaryOrder) {
            var item = '';
            item = '<div class="layui-form-item layui-form-item-dictionary">';
            item += '   <label class="layui-form-label" style="display:none;">字典选值</label>';
            item += '   <div class="layui-input-inline">';
            item += '       <input type="hidden" class="layui-input layui-detail" disabled="disabled" lay-verify="required|number"';
            item += '        name="dictionaryItem" value="' + dictionaryItem + '">';
            item += '   </div>';
            item += '   <label class="layui-form-label">选值描述</label>';
            item += '   <div class="layui-input-inline">';
            item += '       <input type="text" class="layui-input" name="dictionaryCaption" value="' + dictionaryCaption + '">';
            item += '   </div>';
            item += '   <label class="layui-form-label">选值用户</label>';
            item += '   <div class="layui-input-inline">';
            item += '       <select name="userId">';
            item += '           <option value=""></option>';
            for (var i = 0; i < userList.length; i++) {
                if (userId == userList[i].userId) {
                    item += '   <option selected = true value="' + userList[i].userId + '">' + userList[i].userName + '</option>';
                } else {
                    item += '   <option value="' + userList[i].userId + '">' + userList[i].userName + '</option>';
                }
            }
            item += '       </select>';
            item += '   </div>';
            item += '   <input type="hidden" name="itemOrder" value="' + dictionaryOrder + '">';
            item += '   <button class="layui-btn layui-btn-danger layui-btn-xs layuiadmin-btn-dictionary-delete">';
            item += '       <i class="layui-icon layuiadmin-button-btn layui-icon-close"></i>';
            item += '   </button>';
            item += '</div>';
            return item;
        }
    })
</script>
</body>
</html>