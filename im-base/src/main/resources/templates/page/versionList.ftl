<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>修订信息</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${appName}/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${appName}/layuiadmin/style/admin.css" media="all">
</head>
<body>

<style>
    #LAY-component-timeline .layui-card-body {
        padding: 15px;
    }
    .layui-timeline-item-im {
        position: relative;
        padding-bottom: 20px;
    }
    .layui-timeline-content {
        padding-left: 30px;
    }
</style>

<div class="layui-fluid" id="LAY-component-timeline">
    <div class="layui-row layui-col-space15">
        <div class="layui-col-md12">
            <div class="layui-card">
                <div class="layui-card-header">
                    <span>当前版本：<b>${version}</b></span>
                    <span style="padding-left:80px;" id="time"></span>
                </div>
                <div class="layui-card-body">
                    <ul class="layui-timeline">
                    </ul>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${appName}/layuiadmin/layui/layui.js"></script>
<script>
    layui.config({
        base: '${appName}/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'admin', 'im'], function () {
        var $ = layui.$,
            admin = layui.admin,
            im = layui.im;

        // 应用名称
        var appName = '${appName}';
        
        var url = {
            init: appName + "/version/selectList"
        };
        
        // 初始化页面信息
        admin.req({
            url: url.init,
            type: "get",
            dataType: "json",
            done: function (response) {
                if (response.bizResult) {
                    initVersion(response.data);
                }
            }
        });
        
        // 初始化修订信息
        var initVersion = function (data) {
            if(!$.isEmptyObject(data)){
                var time = data[data.length - 1].versionDate + " - " + data[0].versionDate;
                $("#time").text(time);
                for (var i=0; i<data.length; i++) {
                    var versionContent = data[i].versionContent;
                    var versionDate = data[i].versionDate;
                    var versionType = data[i].versionType;
                    if (im.isBlank($("." + versionDate).html())) {
                        var item  = '<li class="layui-timeline-item-im">';
                            item += '   <i class="layui-icon layui-timeline-axis"></i>';
                            item += '   <div class="layui-timeline-content layui-text">';
                            item += '       <h3 class="layui-timeline-title ' + versionDate + '">' + versionDate + '</h3>';
                            item += '       <ul>';
                            item += addItem(versionType, versionContent);
                            item +='        </ul>';
                            item += '   </div>';
                            item += '</li>';
                        $(".layui-timeline").append(item);
                    } else {
                        $("." + versionDate).next().append(addItem(versionType, versionContent));
                    }
                }
            }
        }

        // 添加修订信息
        var addItem = function (type, content) {
            var item  = '<li>';
                if (type == '1') {
                    item += '<span class="layui-btn-im layui-btn-xs-im layui-bg-orange">&nbsp;功能&nbsp;</span>';
                } else if (type == '2') {
                    item += '<span class="layui-btn-im layui-btn-xs-im layui-bg-blue">&nbsp;优化&nbsp;</span>';
                } else if (type == '3') {
                    item += '<span class="layui-btn-im layui-btn-xs-im layui-bg-gray">&nbsp;修复&nbsp;</span>';
                }else if (type == '4') {
                    item += '<span class="layui-btn-im layui-btn-xs-im layui-bg-red">&nbsp;发版&nbsp;</span>';
                }
                item += '&nbsp;&nbsp;&nbsp;';
                item += content;
                item += '</li>';
            return item;
        }
    });
</script>
</body>
</html>