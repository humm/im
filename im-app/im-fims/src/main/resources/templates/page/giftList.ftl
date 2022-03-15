<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>随礼信息-列表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/admin.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/im.css" media="all">

</head>
<body>
<div class="layui-fluid">
    <div class="layui-card">
        <div class="layui-form layui-card-header layuiadmin-card-header-auto">
            <div class="layui-form-item">
                <!-- 查询条件 -->
                <div class="layui-inline">
                    <label class="layui-form-label">送礼人</label>
                    <div class="layui-input-block">
                        <input type="text" class="layui-input" name="giftSender">
                    </div>
                </div>

                <div class="layui-inline">
                    <label class="layui-form-label">收礼人</label>
                    <div class="layui-input-block">
                        <input type="text" class="layui-input" name="giftReceiver">
                    </div>
                </div>

                <div class="layui-inline">
                    <label class="layui-form-label">随礼类型</label>
                    <div class="layui-input-block">
                        <select name="giftType"></select>
                    </div>
                </div>

                <div class="layui-inline">
                    <label class="layui-form-label">随礼日期</label>
                    <div class="layui-input-block">
                        <input type="text" class="layui-input" name="giftDate" id="giftDate">
                    </div>
                </div>

            </div>
        </div>

        <div class="layui-card-body">
            <!-- 头部操作按钮 -->
            <div style="padding-bottom: 10px;" id="LAY-app-gift-list-button">
                <button class="layui-btn layuiadmin-btn-gift-list" lay-submit
                        lay-filter="LAY-app-giftlist-search">
                    <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                </button>
                <button class="layui-btn layuiadmin-btn-gift-list" lay-submit
                        lay-filter="LAY-app-giftlist-refresh">
                    <i class="layui-icon layui-icon-refresh-1 layuiadmin-button-btn"></i>
                </button>
                <button class="layui-btn layuiadmin-btn-gift-list" data-type="add">新增</button>
                <button class="layui-btn layuiadmin-btn-gift-list" data-type="update">修改</button>
                <button class="layui-btn layuiadmin-btn-gift-list" data-type="delete">删除</button>
            </div>

            <!-- 列表数据 -->
            <table id="LAY-app-gift-list" lay-filter="LAY-app-gift-list"></table>

            <!-- 随礼类型 -->
            <script type="text/html" id="giftType">
                <button class="layui-btn layui-btn-xs layui-bg-{{ d.giftTypeCode }}">{{ d.giftType }}</button>
            </script>

            <script type="text/html" id="table-gift-list">
                <a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="update"><i class="layui-icon layui-icon-edit"></i>编辑</a>
                <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="delete"><i class="layui-icon layui-icon-delete"></i>删除</a>
            </script>

        </div>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'table', 'laydate', 'admin', 'im'], function () {
        var $ = layui.$,
            form = layui.form,
            table = layui.table,
            admin = layui.admin,
            im = layui.im,
            laydate = layui.laydate;

        // 应用名称
        var appName = '${appName}';

        // 是否按钮权限
        var hasButton = ${hasButton?string('true','false')};

        // 业务类型
        var businessType = "gift";

        // 登录用户信息
        var sessionBean = {};

        // 请求url
        var url = {
            init: appName + "/gift/selectInitData",
            page: appName + "/gift/selectPage",
            del: appName + "/gift/delete",
            save: appName + "/gift/save",
            add: appName + "/gift/add",
            update: appName + "/gift/update",
            detail: appName + "/gift/detail"
        }

        // 列表字段
        var tableColumn = [[
            {type: "checkbox", fixed: "left"},
            {field: "giftId", title: "随礼序列号", sort: false, hide: true},
            {field: "giftSender", title: "送礼人", sort: true},
            {field: "giftReceiver", title: "收礼人", sort: true},
            {field: "giftType", title: "随礼类型", templet: "#giftType", sort: true},
            {field: "giftDate", title: "随礼日期", align: "center", sort: true},
            {field: "giftAmount", title: "随礼金额", sort: true},
            {field: "giftMemo", title: "随礼备注"},
            {title:"操作", width:150, align:"center", fixed:"right", toolbar:"#table-" + businessType + "-list"}
        ]];

        // 年月选择器
        laydate.render({
            elem: '#giftDate',
            type: 'month',
            trigger: 'click'
        });

        // 权限按钮设置
        im.setAuthority(hasButton, "LAY-app-" + businessType + "-list-button");

        // 初始化页面信息
        admin.req({
            url: url.init,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    // 设置查询条件
                    im.setCondition("layui-form-item", response.data.condition);
                    // 设置登录人信息
                    sessionBean = response.data.sessionBean;
                } else {
                    im.msg(response.msg);
                }
            }
        });

        // 数据删除
        var del = function (data) {
            var giftIds = [];
            for (var i = 0; i < data.length; i++) {
                giftIds.push(data[i].giftId);
            }
            layer.confirm(im.tips.warn.confirmDel, function (index) {
                admin.req({
                    url: url.del,
                    type: "post",
                    data: {giftIds: giftIds.join(",")},
                    done: function (response) {
                        if (response.bizResult) {
                            setTimeout(function () {
                                layer.close(index);
                                reloadData(im.getValue("layui-form-item"));
                                im.msg(response.msg);
                            }, 500);
                        } else {
                            im.msg(response.msg);
                        }
                    }
                });
            });
        }

        // 数据新增
        var add = function (data) {
            im.open({
                type: 2,
                title: im.tips.title.add,
                content: url.add,
                area: [im.size.one, im.size.two],
                btn: [im.tips.btn.save, im.tips.btn.cancel],
                resize: im.set.resize,
                yes: function (e, t) {
                    save(e, t, im.operate.add, data);
                }
            });
        }

        // 数据修改
        var update = function (data) {
            var request = {
                giftId: data.giftId,
                isTranslate: "0"
            }
            im.open({
                type: 2,
                title: im.tips.title.update,
                content: url.update + "?" + $.param(request),
                area: [im.size.one, im.size.two],
                btn: [im.tips.btn.save, im.tips.btn.cancel],
                resize: im.set.resize,
                yes: function (e, t) {
                    save(e, t, im.operate.update, data);
                }
            });
        }

        // 数据详情
        var detail = function (data) {
            var request = {
                giftId: data.giftId,
                isTranslate: "1"
            }
            im.open({
                type: 2,
                title: im.tips.title.detail,
                content: url.detail + "?" + $.param(request),
                area: [im.size.one, im.size.two],
                resize: im.set.resize
            });
        }

        // 数据保存
        var save = function (e, t, type, data) {
            var iframe = window["layui-layer-iframe" + e],
                button = t.find("iframe").contents().find("#LAY-app-" + businessType + "-" + type);
            iframe.layui.form.on("submit(LAY-app-" + businessType + "-" + type + ")", function (data) {
                var param = im.clearBlank(data.field);
                if (param.giftSender == param.giftReceiver) {
                    im.msg(im.tips.msg.notSameOne);
                    return;
                }
                var isLoginUser = param.giftSender.indexOf(sessionBean.userId) == -1 && param.giftReceiver.indexOf(sessionBean.userId) == -1;
                if (isLoginUser && !sessionBean.isAdminData) {
                    im.msg(im.tips.msg.isLoginOne);
                    return;
                }
                admin.req({
                    url: url.save,
                    type: "post",
                    data: param,
                    done: function (response) {
                        if (response.bizResult) {
                            setTimeout(function () {
                                reloadData(im.getValue("layui-form-item"));
                                layer.close(e);
                                im.msg(response.msg);
                            }, 500);
                        } else {
                            im.msg(response.msg);
                        }
                    }
                });
            }), button.trigger("click");
        }

        // 列表数据渲染
        im.initTable({
            elem: "#LAY-app-" + businessType + "-list",
            url: url.page,
            cols: tableColumn
        });

        // 行数据按钮事件绑定 暂未启用
        table.on("tool(LAY-app-" + businessType + "-list)", function (obj) {
            // 获取数据值
            var data = obj.data;
            switch (obj.event) {
                case im.operate.delete:
                    var convertData = new Array();
                    convertData.push(data);
                    del(convertData);
                    break;
                case im.operate.detail:
                    detail(data);
                    break;
                case im.operate.add:
                    add(data);
                    break;
                case im.operate.update:
                    update(data);
                    break;
                default:
                    im.msg(im.tips.msg.notSupportEvent);
                    break;
            }
        });

        // table按钮事件监听
        $(".layui-btn.layuiadmin-btn-" + businessType + "-list").on("click", function () {

            // 获取选中的数据
            var checkData = table.checkStatus("LAY-app-" + businessType + "-list").data;

            var type = $(this).data("type");
            if (im.isBlank(type)) {
                return;
            }
            switch (type) {
                case im.operate.add:
                    add();
                    break;
                case im.operate.update:
                    if (checkData.length === 0) {
                        return im.msg(im.tips.warn.notSelect);
                    }
                    if (checkData.length > 1) {
                        return im.msg(im.tips.warn.selectOne);
                    }
                    update(checkData[0]);
                    break;
                case im.operate.delete:
                    if (checkData.length === 0) {
                        return im.msg(im.tips.warn.notSelect);
                    }
                    del(checkData);
                    break;
                default:
                    im.msg(im.tips.msg.notSupportEvent);
            }
        });

        // 监听查询
        form.on("submit(LAY-app-" + businessType + "list-search)", function (data) {
            //执行重载
            reloadData(im.getValue("layui-form-item"));
        });

        // 监听重置
        form.on("submit(LAY-app-" + businessType + "list-refresh)", function (data) {
            im.clearValue("layui-form-item");
            //执行重载
            reloadData({});
        });

        // 监听列表排序
        table.on("sort(LAY-app-" + businessType + "-list)", function (data) {
            table.reload("LAY-app-" + businessType + "-list", {
                initSort: data,
                where: $.extend({
                    sort: data.field,
                    order: data.type
                }, im.clearBlank(im.getValue("layui-form-item")))
            });
        });

        // 监听双击事件
        table.on("rowDouble(LAY-app-" + businessType + "-list)", function (obj) {
            detail(obj.data);
        });

        // 重载列表数据
        var reloadData = function (data) {
            table.reload("LAY-app-" + businessType + "-list", {
                where: im.clearBlank(data)
            });
        }

    });
</script>
</body>
</html>
