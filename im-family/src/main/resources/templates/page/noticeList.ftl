<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>消息通知信息-列表</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/admin.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/family.css" media="all">

</head>
<body>

<div class="layui-fluid" id="LAY-app-message">
    <div class="layui-card">
        <div class="layui-tab layui-tab-brief">
            <ul class="layui-tab-title">
                <li style="margin: 0px 10px;" id="read">未读</li>
                <li style="margin: 0px 10px;" id="isRead">已读</li>
            </ul>
            <div class="layui-tab-content">

                <div class="layui-tab-item layui-show">

                    <div class="LAY-app-notice-btns" style="margin-bottom: 10px;">
                        <button class="layui-btn layui-btn-primary layui-btn-sm" data-type="read"
                                data-events="ready">标记已读
                        </button>
                        <button class="layui-btn layui-btn-primary layui-btn-sm" data-type="isRead"
                                data-events="readyAll">全部已读
                        </button>
                    </div>

                    <table id="LAY-app-notice_read" lay-filter="LAY-app-notice_read"></table>
                </div>
                <div class="layui-tab-item">
                    <table id="LAY-app-notice_is_read" lay-filter="LAY-app-notice_is_read"></table>
                </div>
            </div>
        </div>
    </div>

    <!-- 用户类型 -->
    <script type="text/html" id="noticeStatus">
        {{#  if(d.noticeStatus == '正常'){ }}
        <button class="layui-btn layui-bg-1 layui-btn-xs">正常</button>
        {{#  } else{ }}
        <button class="layui-btn layui-bg-2 layui-btn-xs">撤销</button>
        {{#  } }}
    </script>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'admin', 'table', 'util', 'family'], function () {
        var $ = layui.$,
            family = layui.family,
            admin = layui.admin,
            table = (layui.admin, layui.table),
            element = (layui.element, {
                read: {text: "未读", id: "LAY-app-notice_read"},
                isRead: {text: "已读", id: "LAY-app-notice_is_read"}
            }),
            detail = function (d) {
                var url = "<a href='detail?menuId=skip&noticeId=" + d.noticeId + "&isTranslate=1&readStatus=" + d.readStatusCode + "'>";
                    url += "<div>" + d.userId + '&nbsp;&nbsp;&nbsp;' + d.businessType + '&nbsp;&nbsp;&nbsp;' + d.businessAmount + "</div>";
                    url += "</a>";
                return url;
            };

        // 应用名称
        var appName = '${appName}';

        // 请求url
        var url = {
            page: appName + "/notice/selectPage",
            update: appName + "/notice/updateReadStatus",
            websocketUrl: '${requestUrl}'
        }

        // 阅读状态
        var readStatus = family.getUrlParameter("readStatus");

        // 列表字段
        var tableColumn = [[
            {type: "checkbox", fixed: "left"},
            {field: "noticeId", title: "消息通知序列号", sort: false, hide: true},
            {field: "businessData", title: "通知内容", minWidth: 300, templet: detail},
            {field: "noticeStatus", title: "通知状态", templet: "#noticeStatus"},
            {field: "modifyDate", title: "通知时间", width: 200, templet: "<div>{{ layui.util.timeAgo(d.modifyDate) }}</div>"}

        ]];

        family.initTable({
            elem: "#LAY-app-notice_read",
            url: url.page + '?readStatus=D012-1',
            cols: tableColumn
        });

        family.initTable({
            elem: "#LAY-app-notice_is_read",
            url: url.page + '?readStatus=D012-2',
            cols: tableColumn
        });

        var operate = {
            ready: function (e, t) {
                var select = element[t], selectTable = table.checkStatus(select.id), data = selectTable.data;
                if(0 === data.length){
                    family.msg(family.tips.warn.notSelect);
                    return;
                }
                var noticeIds = [];
                for (var i=0; i<data.length; i++){
                    noticeIds.push(data[i].noticeId);
                }
                admin.req({
                    url: url.update,
                    type: "post",
                    dataType: "json",
                    data: {isAll: '0', noticeIds : noticeIds.join(",")},
                    done: function (response) {
                        if (response.bizResult) {
                            table.reload(element['read'].id);
                            table.reload(element['isRead'].id);
                        } else {
                            family.msg(response.msg);
                        }
                    }
                });
            },
            readyAll: function (e, t) {
                var select = element[t], selectTable = table.checkStatus(select.id), data = selectTable.data;
                admin.req({
                    url: url.update,
                    type: "post",
                    dataType: "json",
                    data: {isAll: '1'},
                    done: function (response) {
                        if (response.bizResult) {
                            table.reload(element['read'].id);
                            table.reload(element['isRead'].id);
                        } else {
                            family.msg(response.msg);
                        }
                    }
                });
            }
        };
        $(".LAY-app-notice-btns .layui-btn").on("click", function () {
            var e = $(this),
                event = e.data("events"),
                type = e.data("type");
            operate[event] && operate[event].call(this, e, type);
        });

        // websocket订阅消息通知主题
        family.webSocket(url.websocketUrl, "notice", function (data) {
            if(!family.isBlank(data)){
                window.location.reload();
            }
        });

        if (readStatus == '2') {
            $("#isRead").click();
        } else {
            $("#read").click();
        }
    });
</script>
</body>
</html>