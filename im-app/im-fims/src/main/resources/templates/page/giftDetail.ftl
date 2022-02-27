<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>随礼信息-详情</title>
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
        <label class="layui-form-label">送礼人</label>
        <div class="layui-input-inline">
            <input type="text" name="giftSender" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">收礼人</label>
        <div class="layui-input-inline">
            <input type="text" name="giftReceiver" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">随礼类型</label>
        <div class="layui-input-inline">
            <input type="text" name="giftType" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">随礼金额</label>
        <div class="layui-input-inline">
            <input type="text" name="giftAmount" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">随礼日期</label>
        <div class="layui-input-inline">
            <input type="text" name="giftDate" class="layui-input layui-detail" disabled="disabled" />
        </div>
    </div>

    <div class="layui-form-item">
        <label class="layui-form-label">随礼备注</label>
        <div class="layui-input-inline">
            <textarea name="giftMemo" class="layui-textarea layui-detail" disabled="disabled"></textarea>
        </div>
    </div>
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

        var request = {
            giftId: im.getUrlParameter("giftId"),
            isTranslate: im.getUrlParameter("isTranslate")
        }

        // 请求url
        var url = appName + "/gift/selectOne?" + $.param(request);

        // 初始化页面信息
        admin.req({
            url: url,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    im.setValue("layui-form", response.data);
                } else {
                    im.msg(response.msg);
                }
            }
        });

    })
</script>
</body>
</html>