<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>随礼信息-修改</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
</head>
<body>

<div class="layui-form" style="padding: 20px 30px 0 0;">
    <input type="hidden" name="giftId"/>
    <div class="layui-form-item">
        <label class="layui-form-label">送礼人</label>
        <div class="layui-input-inline">
            <select name="giftSender" lay-verify="required"></select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">收礼人</label>
        <div class="layui-input-inline">
            <select name="giftReceiver" lay-verify="required"></select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">随礼类型</label>
        <div class="layui-input-inline">
            <select name="giftType" lay-verify="required"></select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">随礼金额</label>
        <div class="layui-input-inline">
            <input type="text" name="giftAmount" class="layui-input" lay-verify="required|number">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">随礼日期</label>
        <div class="layui-input-inline">
            <input type="text" name="giftDate" id="giftDate" class="layui-input" lay-verify="required" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">随礼备注</label>
        <div class="layui-input-inline">
            <textarea name="giftMemo" class="layui-textarea"></textarea>
        </div>
    </div>
    <div class="layui-form-item layui-hide">
        <input type="button" lay-submit lay-filter="LAY-app-gift-update" id="LAY-app-gift-update" />
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'form', 'laydate', 'admin', 'family'], function () {
        var $ = layui.$,
            form = layui.form,
            admin = layui.admin,
            laydate = layui.laydate,
            family = layui.family;

        // 应用名称
        var appName = '${appName}';

        var request = {
            giftId: family.getUrlParameter("giftId"),
            isTranslate: family.getUrlParameter("isTranslate")
        }

        var url = {
            init: appName + "/gift/selectInitData",
            load: appName + "/gift/selectOne?" + $.param(request)
        };

        // 初始化页面信息
        admin.req({
            url: url.init,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    family.setCondition("layui-form", response.data.condition);
                    form.render();

                    // 数据回填
                    admin.req({
                        url: url.load,
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
                } else {
                    family.msg(response.msg);
                }
            }
        });

        //年月选择器
        laydate.render({
            elem: '#giftDate',
            trigger: 'click'
        });

    })
</script>
</body>
</html>