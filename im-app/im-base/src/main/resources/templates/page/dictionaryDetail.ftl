<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>字典信息-详情</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/im.css" media="all">
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
    </div>
    <div class="layui-form-item dictionary"></div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'admin', 'im'], function () {
        var $ = layui.$,
            form = layui.form,
            admin = layui.admin,
            im = layui.im;

        // 应用名称
        var appName = '${appName}';

        var dictionaryCode = im.getUrlParameter("dictionaryCode");
        var dictionaryCaption = decodeURI(im.getUrlParameter("dictionaryCaption"));
        var userList = [];

        var request = {
            dictionaryCode: dictionaryCode,
            isTranslate: im.getUrlParameter("isTranslate")
        }

        var url = {
            load: appName + "/dictionary/selectOne?" + $.param(request)
        };

        // 初始化页面信息
        admin.req({
            url: url.load,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    addDictionary(response.data);
                } else {
                    im.msg(response.msg);
                }
            }
        });

        // 绑定新增事件
        $(document).on('click', 'button.layuiadmin-btn-dictionary-add', function () {
            $(".dictionary").append(addItem(userList, '', '', ''));
            form.render();
        });

        // 绑定删除事件
        $(document).on('click', 'button.layuiadmin-btn-dictionary-delete', function () {
            $(this).parent().remove();
        });

        // 添加字典项
        function addDictionary(data) {
            if (!$.isEmptyObject(data)) {
                userList = data.user;
                for (var i = 0; i < data.dictionary.length; i++) {
                    $(".dictionary").append(addItem(data.user, data.dictionary[i].userId, data.dictionary[i].dictionaryItem, data.dictionary[i].dictionaryCaption));
                    form.render();
                }
            }
            $("input[name='dictionaryCode']").val(dictionaryCode);
            $("input[name='dictionaryCodeCaption']").val(dictionaryCaption);
            $("input[name]").addClass("layui-detail");
            $("input[name]").attr("disabled", true);
            form.render();
        }

        // 添加元素
        function addItem(userList, userId, dictionaryItem, dictionaryCaption) {
            var item = '';
            item = '<div class="layui-form-item layui-form-item-dictionary">';
            item += '   <label class="layui-form-label" style="display:none;">字典选值</label>';
            item += '   <div class="layui-input-inline">';
            item += '       <input type="hidden" class="layui-input" name="dictionaryItem" value="' + dictionaryItem + '">';
            item += '   </div>';
            item += '   <label class="layui-form-label">选值描述</label>';
            item += '   <div class="layui-input-inline">';
            item += '       <input type="text" class="layui-input" name="dictionaryCaption" value="' + dictionaryCaption + '">';
            item += '   </div>';
            item += '   <label class="layui-form-label">选值用户</label>';
            item += '   <div class="layui-input-inline">';
            item += '       <input type="text" class="layui-input" name="userId" value="' + im.value(userId) + '">';
            item += '   </div>';
            item += '</div>';
            return item;
        }
    })
</script>
</body>
</html>