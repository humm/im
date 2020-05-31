<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>收入信息-详情</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/family.css" media="all">
</head>
<body>

<div class="layui-form" style="padding: 20px 30px 0 0;">
    <div class="layui-form-item">
        <label class="layui-form-label">收入人</label>
        <div class="layui-input-inline">
            <input type="text" name="userId" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">收入来源</label>
        <div class="layui-input-inline">
            <input type="text" name="incomeCompany" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">收入类型</label>
        <div class="layui-input-inline">
            <input type="text" name="incomeType" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">收入金额</label>
        <div class="layui-input-inline">
            <input type="text" name="incomeAmount" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">收入日期</label>
        <div class="layui-input-inline">
            <input type="text" name="incomeDate" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>

    <div class="layui-form-item">
        <label class="layui-form-label">收入备注</label>
        <div class="layui-input-inline">
            <textarea name="incomeMemo" class="layui-textarea layui-detail" disabled="disabled"></textarea>
        </div>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'admin', 'family'], function () {
        var $ = layui.$,
            form = layui.form,
            admin = layui.admin,
            family = layui.family;

        // 应用名称
        var appName = '${appName}';

        var request = {
            incomeId: family.getUrlParameter("incomeId"),
            isTranslate: family.getUrlParameter("isTranslate")
        }

        // 请求url
        var url = appName + "/income/selectOne?" + $.param(request);

        // 初始化页面信息
        admin.req({
            url: url,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    family.setValue("layui-form", response.data);
                } else {
                    family.msg(response.msg);
                }
            }
        });

    })
</script>
</body>
</html>