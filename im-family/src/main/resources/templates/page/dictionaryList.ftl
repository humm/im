<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>字典信息-列表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/admin.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/family.css" media="all">

</head>
<body>
<div class="layui-fluid">
    <div class="layui-card">
        <div class="layui-form layui-card-header layuiadmin-card-header-auto">
            <div class="layui-form-item">
                <!-- 查询条件 -->
                <div class="layui-inline">
                    <label class="layui-form-label">字典代码</label>
                    <div class="layui-input-block">
                        <input type="text" class="layui-input" name="dictionaryCode">
                    </div>
                </div>

                <div class="layui-inline">
                    <label class="layui-form-label">字典描述</label>
                    <div class="layui-input-block">
                        <input type="text" class="layui-input" name="dictionaryCaption">
                    </div>
                </div>


                <!-- 查询按钮 -->
                <div class="layui-inline layui-inline-button">
                    <button class="layui-btn layuiadmin-btn-dictionary-list" lay-submit
                            lay-filter="LAY-app-dictionarylist-search">
                        <i class="layui-icon layui-icon-search layuiadmin-button-btn"></i>
                    </button>
                </div>

                <!-- 重置按钮 -->
                <div class="layui-inline layui-inline-button">
                    <button class="layui-btn layuiadmin-btn-dictionary-list" lay-submit
                            lay-filter="LAY-app-dictionarylist-refresh">
                        <i class="layui-icon layui-icon-refresh-1 layuiadmin-button-btn"></i>
                    </button>
                </div>

            </div>
        </div>

        <div class="layui-card-body">
            <!-- 头部操作按钮 -->
            <div style="padding-bottom: 10px;" id="LAY-app-dictionary-list-button">
                <button class="layui-btn layuiadmin-btn-dictionary-list" data-type="update">修改</button>
                <button class="layui-btn layuiadmin-btn-dictionary-list" data-type="refresh">刷新</button>
            </div>

            <!-- 列表数据 -->
            <table id="LAY-app-dictionary-list" lay-filter="LAY-app-dictionary-list"></table>

            <!-- 用户类型 -->
            <script type="text/html" id="isOpen">
                {{#  if(d.isOpen == '1'){ }}
                <button class="layui-btn layui-bg-1 layui-btn-xs">开放</button>
                {{#  } else{ }}
                <button class="layui-btn layui-bg-2 layui-btn-xs">封闭</button>
                {{#  } }}
            </script>

            <script type="text/html" id="table-dictionary-list">
                {{#  if(d.isOpen == '1'){ }}
                <a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="update"><i class="layui-icon layui-icon-edit"></i>编辑</a>
                {{#  } else{ }}
                {{#  } }}
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
    }).use(['index', 'table', 'laydate', 'admin', 'family'], function () {
        var $ = layui.$,
            form = layui.form,
            table = layui.table,
            admin = layui.admin,
            family = layui.family;

        // 应用名称
        var appName = '${appName}';

        // 业务类型
        var businessType = "dictionary";

        // 请求url
        var url = {
            page: appName + "/dictionary/selectPage",
            save: appName + "/dictionary/save",
            update: appName + "/dictionary/update",
            detail: appName + "/dictionary/detail",
            refresh: appName + "/dictionary/refresh"
        }

        // 列表字段
        var tableColumn = [[
            {type: "checkbox", fixed: "left"},
            {field: "dictionaryCode", title: "字典代码", sort: true,},
            {field: "dictionaryCaption", title: "字典描述", sort: true},
            {field: "userId", title: "字典用户", hide: true},
            {field: "isOpen", title: "开放状态", align: "center", templet: "#isOpen", sort: true},
            {title:"操作", width:100, align:"center", fixed:"right", toolbar:"#table-" + businessType + "-list"}
        ]];

        // 数据刷新
        var refresh = function (data) {
            admin.req({
                url: url.refresh,
                type: "get",
                dataType: "json",
                done: function (response) {
                    family.msg(response.msg);
                }
            });
        }

        // 数据删除
        var del = function (data) {

        }

        // 数据新增
        var add = function (data) {

        }

        // 数据修改
        var update = function (data) {
            if('0' == data.isOpen ){
                family.msg(family.tips.msg.closeDictionaryNotUpdate);
                return;
            }
            var request = {
                dictionaryCode: data.dictionaryCode,
                dictionaryCaption: encodeURI(data.dictionaryCaption),
                isOpen: data.isOpen,
                isTranslate: "0"
            }
            family.open({
                type: 2,
                title: family.tips.title.update,
                content: url.update + "?" + $.param(request),
                area: [family.size.three, family.size.four],
                btn: [family.tips.btn.save, family.tips.btn.cancel],
                resize: family.set.resize,
                yes: function (e, t) {
                    save(e, t, family.operate.update, data);
                }
            });
        }

        // 数据详情
        var detail = function (data) {
            var request = {
                dictionaryCode: data.dictionaryCode,
                dictionaryCaption: encodeURI(data.dictionaryCaption),
                isOpen: data.isOpen,
                isTranslate: "1"
            }
            family.open({
                type: 2,
                title: family.tips.title.detail,
                content: url.detail + "?" + $.param(request),
                area: [family.size.seven, family.size.four],
                resize: family.set.resize
            });
        }

        // 数据保存
        var save = function (e, t, type, data) {
            var iframe = window["layui-layer-iframe" + e],
                button = t.find("iframe").contents().find("#LAY-app-" + businessType + "-" + type);
            var elements = [];
            var flag = false;
            t.find("iframe").contents().find(".layui-form-item-dictionary").each(function (index) {
                if (flag) {
                    return;
                }
                var that = this;
                var item = {};
                $(that).find("[name]").each(function () {
                    var name = $(this).attr("name");
                    var value = $(this).val();
                    item[name] = value;
                    if ((name == "userId" && data.isOpen == '0') || family.isBlank(name)) {
                        // 选值用户 未开放状态 不校验
                    } else if (family.isBlank(value)) {
                        var title = $(this).parent().prev().html();
                        family.msg(title + " " + family.tips.msg.notEmpty);
                        flag = true;
                        return;
                    }
                });
                item["dictionaryCode"] = data.dictionaryCode;
                item["isOpen"] = data.isOpen;
                item["itemOrder"] = index + 1;
                elements.push(item);
            });
            if (flag) {
                return;
            }
            // 若为空添加默认字典代码
            if($.isEmptyObject(elements)){
                var item = {
                    dictionaryCode: data.dictionaryCode,
                    isOpen: data.isOpen
                }
                elements.push(item);
            }
            iframe.layui.form.on("submit(LAY-app-" + businessType + "-" + type + ")", function (data) {
                admin.req({
                    url: url.save,
                    type: "post",
                    contentType:"application/json",
                    data: JSON.stringify(elements),
                    done: function (response) {
                        if (response.bizResult) {
                            setTimeout(function () {
                                reloadData(family.getValue("layui-form-item"));
                                layer.close(e);
                                family.msg(response.msg);
                            }, 500);
                        } else {
                            family.msg(response.msg);
                        }
                    }
                });
            }), button.trigger("click");
        }

        // 列表数据渲染
        family.initTable({
            elem: "#LAY-app-" + businessType + "-list",
            url: url.page,
            cols: tableColumn
        });

        // 行数据按钮事件绑定 暂未启用
        table.on("tool(LAY-app-" + businessType + "-list)", function (obj) {
            // 获取数据值
            var data = obj.data;
            switch (obj.event) {
                case family.operate.delete:
                    var convertData = new Array();
                    convertData.push(data);
                    del(convertData);
                    break;
                case family.operate.detail:
                    detail(data);
                    break;
                case family.operate.add:
                    add(data);
                    break;
                case family.operate.update:
                    update(data);
                    break;
                default:
                    family.msg(family.tips.msg.notSupportEvent);
                    break;
            }
        });

        // table按钮事件监听
        $(".layui-btn.layuiadmin-btn-" + businessType + "-list").on("click", function () {

            // 获取选中的数据
            var checkData = table.checkStatus("LAY-app-" + businessType + "-list").data;

            var type = $(this).data("type");
            if (family.isBlank(type)) {
                return;
            }
            switch (type) {
                case family.operate.add:
                    add();
                    break;
                case family.operate.update:
                    if (checkData.length === 0) {
                        return family.msg(family.tips.warn.notSelect);
                    }
                    if (checkData.length > 1) {
                        return family.msg(family.tips.warn.selectOne);
                    }
                    update(checkData[0]);
                    break;
                case family.operate.delete:
                    if (checkData.length === 0) {
                        return family.msg(family.tips.warn.notSelect);
                    }
                    del(checkData);
                    break;
                case family.operate.refresh:
                    refresh();
                    break;
                default:
                    family.msg(family.tips.msg.notSupportEvent);
            }
        });

        // 监听查询
        form.on("submit(LAY-app-" + businessType + "list-search)", function (data) {
            //执行重载
            reloadData(family.getValue("layui-form-item"));
        });

        // 监听重置
        form.on("submit(LAY-app-" + businessType + "list-refresh)", function (data) {
            family.clearValue("layui-form-item");
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
                }, family.clearBlank(family.getValue("layui-form-item")))
            });
        });

        // 监听双击事件
        table.on("rowDouble(LAY-app-" + businessType + "-list)", function (obj) {
            detail(obj.data);
        });

        // 重载列表数据
        var reloadData = function (data) {
            table.reload("LAY-app-" + businessType + "-list", {
                where: family.clearBlank(data)
            });
        }

    });
</script>
</body>
</html>
