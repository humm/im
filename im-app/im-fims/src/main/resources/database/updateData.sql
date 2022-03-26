-- 升级脚本
-- 发布版本修改点: sys_version新增记录  pom.xml修改版本  常量SYSTEM_VERSION修改
-- 修订信息
-- 1:功能 2:优化 3:修复 4:发版

call add_version('20190000000063', '日期控件加载不出来(一闪而过)', '2020-02-23', 310, '3');

delete from sys_parameter where parameter_code = 'uploadLocation';

call add_version('20190000000064', '随礼信息：送礼人、收礼人修改为模糊查询', '2020-02-23', 315, '2');
call add_version('20190000000065', '自动化系统升级', '2020-02-24', 320, '1');
call add_version('20190000000066', 'Excel备份', '2020-02-25', 325, '1');

call add_version('20190000000068', '邮件信息：业务日期格式错误重复提醒', '2020-02-26', 330, '3');

update sys_parameter set parameter_caption = '年度开始日期' where parameter_code = 'yearStartDate';

update sys_dictionary set dictionary_item = '2', item_order = '2' where dictionary_code = 'D010' and dictionary_item = '0';

call add_parameter('backupMode', '系统备份模式', 'sql', 'checkbox', 'sql,dmp,xlsx', '1', '1', 36);
call add_version('20190000000069', '系统备份：新增备份模式参数', '2020-02-26', 335, '2');

delete from sys_dictionary where dictionary_code = 'D013';
insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D013', '#', '是否标识', null, 13, 20190000000001, '0', '1');

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D013', '1', '是', 1, null, 20190000000001, null, null);

insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D013', '0', '否', 2, null, 20190000000001, null, null);

call add_parameter('weChatWelcome', '欢迎语', '智慧家庭,畅享生活', 'text', null, '1', '1', 70);
call add_parameter('weChatKey', '密钥', 'fims', 'text', null, '1', '1', 75);
call add_parameter('weChatOpen', '对外开放状态', '2', 'switch', null, '1', '1', 80);
call add_parameter('weChatOperateTime', '操作时间间隔(秒)', '30', 'number', null, '1', '1', 85);
call add_parameter('weChatOperateBack', '操作后返回主菜单', '1', 'switch', null, '1', '1', 90);

delete from sys_dictionary where dictionary_code = 'D008' and dictionary_item = '3';
insert into sys_dictionary (dictionary_code, dictionary_item, dictionary_caption, item_order, code_order, user_id, is_open, is_show)
values ('D008', '3', '微信', 3, null, 20190000000001, null, null);

call create_table('sys_wechat_user', 'create table sys_wechat_user
(
    wechat_user_id       varchar2(100) primary key,
    wechat_public_id     varchar2(100) not null,
    is_auth             varchar2(50) default ''0'',
    user_id              number(30),
    create_date          timestamp(6) default sysdate,
    modify_date          timestamp(6) default sysdate,
    create_user          varchar2(50),
    modify_user          varchar2(50)
)');

comment on column sys_wechat_user.wechat_user_id
    is '微信用户ID';
comment on column sys_wechat_user.wechat_public_id
    is '微信公众号ID';
comment on column sys_wechat_user.is_auth
    is '是否身份验证';
comment on column sys_wechat_user.user_id
    is '用户ID';
comment on column sys_wechat_user.create_date
    is '创建时间';
comment on column sys_wechat_user.modify_date
    is '修改时间';
comment on column sys_wechat_user.create_user
    is '创建人';
comment on column sys_wechat_user.modify_user
    is '修改人';

call create_table('sys_wechat_flow', 'create table sys_wechat_flow
(
    flow_id              number(30) primary key,
    flow_num             varchar2(50),
    flow_code            varchar2(50),
    flow_describe        varchar2(500),
    flow_tips            varchar2(500),
    flow_type            varchar2(50),
    flow_order           number(10),
    is_show              varchar2(50),
    create_date          timestamp(6) default sysdate,
    modify_date          timestamp(6) default sysdate,
    create_user          varchar2(50),
    modify_user          varchar2(50)
)');
comment on column sys_wechat_flow.flow_id
    is '流程步骤ID';
comment on column sys_wechat_flow.flow_num
    is '流程步骤序号';
comment on column sys_wechat_flow.flow_code
    is '流程步骤代码';
comment on column sys_wechat_flow.flow_describe
    is '流程步骤描述';
comment on column sys_wechat_flow.flow_tips
    is '流程步骤提示信息';
comment on column sys_wechat_flow.flow_order
    is '流程步骤排序';
comment on column sys_wechat_flow.is_show
    is '流程步骤是否显示';
comment on column sys_wechat_flow.flow_type
    is '流程步骤类型';
comment on column sys_wechat_flow.create_date
    is '创建时间';
comment on column sys_wechat_flow.modify_date
    is '修改时间';
comment on column sys_wechat_flow.create_user
    is '创建人';
comment on column sys_wechat_flow.modify_user
    is '修改人';

delete from sys_wechat_flow where flow_id <= 20200000000017;
insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000001, '1', 'income-month', '收入查询 - 月度', null, '1', 1, to_timestamp('2020-02-29 16:11:46', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 16:11:46', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000002, '3', 'income-year', '收入查询 - 年度', null, '1', 3, to_timestamp('2020-02-29 16:13:19', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 16:13:19', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000003, '2', 'income-year-current', '收入查询 - 本年度', null, '1', 2, to_timestamp('2020-02-29 16:13:20', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 16:13:20', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000004, '4', 'income-all', '收入查询 - 总收入', null, '1', 4, to_timestamp('2020-02-29 16:13:20', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 16:13:20', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000005, '5', 'income-add', '收入新增', '请按如下格式输入收入信息

收入用户: 中文名称
收入来源: 中文名称
收入日期: yyyyMMdd
收入类型: 中文名称
收入金额: 支持两位小数
收入备注: 最大150字符', '1', 5, to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 ' || '16:21:18', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000006, '6', 'income-delete', '收入删除', '请输入业务流水号', '1', 6, to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000008, '7', 'more', '更多服务', null, '1', 7, to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000009, '8', 'gift-free', '随礼查询 - 自由查询', '请输入送礼人或收礼人名称', '2', 8, to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000010, '9', 'gift-last', '随礼查询 - 最近一次', null, '2', 9, to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000011, '11', 'gift-year', '随礼查询 - 年度', null, '2', 11, to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000012, '10', 'gift-year-current', '随礼查询 - 本年度', null, '2', 10, to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000013, '12', 'gift-all', '随礼查询 - 总随礼', null, '2', 12, to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000014, '13', 'git-add', '随礼新增', '请按如下格式输入随礼信息

送礼用户: 中文名称
收礼用户: 中文名称
随礼日期: yyyyMMdd
随礼类型: 中文名称
随礼金额: 支持两位小数
随礼备注: 最大150字符', '2', 13, to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 ' || '16:21:18', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000016, '14', 'gift-delete', '随礼删除', '请输入业务流水号', '2', 14, to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 16:21:18', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000017, '99', 'main', '返回主菜单', null, '2', 99, to_timestamp('2020-02-29 10:27:32', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-02-29 10:27:32', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

call add_version('20190000000071', '微信公众号业务查询', '2020-03-05', 345, '1');

call add_column('sys_parameter', 'parameter_group', 'varchar2(50)', ''' ''');
comment on column sys_parameter.parameter_group is '参数分组';
update sys_parameter set parameter_group = '微信参数', parameter_order = '80' where parameter_code = 'weChatWelcome';
update sys_parameter set parameter_group = '微信参数', parameter_order = '85' where parameter_code = 'weChatKey';
update sys_parameter set parameter_group = '微信参数', parameter_order = '90' where parameter_code = 'weChatOpen';
update sys_parameter set parameter_group = '微信参数', parameter_order = '95' where parameter_code = 'weChatOperateTime';
update sys_parameter set parameter_group = '微信参数', parameter_order = '100' where parameter_code = 'weChatOperateBack';

update sys_parameter set parameter_group = '提示参数', parameter_order = '50' where parameter_code = 'startConsoleOutput';
update sys_parameter set parameter_group = '提示参数', parameter_order = '55' where parameter_code = 'consoleOutputLogRequestTag';
update sys_parameter set parameter_group = '提示参数', parameter_order = '60' where parameter_code = 'consoleOutputLogRequestParameter';
update sys_parameter set parameter_group = '提示参数', parameter_order = '65' where parameter_code = 'mindFill';
update sys_parameter set parameter_group = '提示参数', parameter_order = '70' where parameter_code = 'messageTip';

update sys_parameter set parameter_group = '系统参数', parameter_order = '10' where parameter_code = 'startBackup';
update sys_parameter set parameter_group = '系统参数', parameter_order = '15' where parameter_code = 'backupMode';
update sys_parameter set parameter_group = '系统参数', parameter_order = '20' where parameter_code = 'backupLocation';
update sys_parameter set parameter_group = '系统参数', parameter_order = '25' where parameter_code = 'startMail';
update sys_parameter set parameter_group = '系统参数', parameter_order = '30' where parameter_code = 'yearStartDate';
update sys_parameter set parameter_group = '系统参数', parameter_order = '35' where parameter_code = 'sessionTimeout';
update sys_parameter set parameter_group = '系统参数', parameter_order = '40' where parameter_code = 'cookieTimeout';
update sys_parameter set parameter_group = '系统参数', parameter_order = '45' where parameter_code = 'userDefaultPassword';
update sys_parameter set parameter_group = '系统参数', parameter_order = '999' where parameter_code = 'version';

call add_version('20190000000073', '系统参数分组显示', '2020-03-10', 355, '2');

delete from sys_config where module_group_code = 'console' and module_code = 'register';
insert into sys_config (MODULE_GROUP_CODE, MODULE_GROUP_NAME, MODULE_CODE, MODULE_NAME, MODULE_STATUS, MODULE_EXT)
values ('console', '首页信息', 'register', '注册信息', '1', null);
call add_version('20190000000074', '首页用户注册信息模块', '2020-03-11', 360, '1');
call add_version('20190000000075', '微信交互提示', '2020-03-11', 365, '2');

delete from sys_dictionary where dictionary_code = 'D006';
update sys_dictionary set dictionary_caption = '随礼用户' where dictionary_code = 'D009' and dictionary_item = '#';
call add_version('20190000000077', '页面弹窗大小调整为百分比模式', '2020-03-19', 370, '2');
call add_version('20190000000078', '首页页面跳转权限控制', '2020-03-21', 375, '1');
call add_version('20190000000079', '收入信息、随礼信息菜单权限控制首页模式显示', '2020-03-22', 380, '1');
call add_version('20190000000080', '页面弹窗大小自适应', '2020-03-22', 380, '2');

call add_version('20190000000082', '页面通知消息权限控制', '2020-04-04', 390, '2');
-- 菜单信息
truncate table sys_menu;
insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000000, '数据权限', null, null, null, 0.00, '1', '4', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000001, '收入信息', 'layui-icon-flag', 'income/list', null, 10.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000002, '查询权限', null, null, 20190000000001, 15.00, '1', '2', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000003, '设置权限', null, null, 20190000000001, 20.00, '1', '3', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000004, '随礼信息', 'layui-icon-transfer', 'gift/list', null, 25.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000005, '查询权限', null, null, 20190000000004, 30.00, '1', '2', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000006, '设置权限', null, null, 20190000000004, 35.00, '1', '3', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000007, '统计分析', 'layui-icon-chart', '#', null, 60.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000008, '收入分析', null, 'report/income', 20190000000007, 65.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000009, '送礼分析', null, 'report/giftSend', 20190000000007, 70.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000010, '收礼分析', null, 'report/giftReceive', 20190000000007, 75.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000011, '系统设置', 'layui-icon-engine', '#', null, 80.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000012, '用户信息', null, 'user/list', 20190000000011, 85.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000013, '角色信息', null, 'role/list', 20190000000011, 90.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000014, '字典信息', null, 'dictionary/list', 20190000000011, 100.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000015, '图标信息', null, 'icon/list', 20190000000011, 999999.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000016, '参数信息', null, 'parameter/list', 20190000000011, 95.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000017, '消息通知', 'layui-icon-notice', 'notice/list', null, 61.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000018, '查询权限', null, null, 20190000000017, 45.00, '1', '2', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000019, '登录日志', 'layui-icon-log', 'loginLog/list', null, 62.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000020, '查询权限', null, null, 20190000000019, 55.00, '1', '2', sysdate, sysdate, '20190000000001', '20190000000001');

insert into sys_menu (menu_id, menu_title, menu_icon, menu_url, parent_id, menu_order, is_enable, menu_type, create_date, modify_date, create_user, modify_user)
values (20190000000021, '修订信息', null, 'version/list', 20190000000011, 110.00, '1', '1', sysdate, sysdate, '20190000000001', '20190000000001');

call add_parameter('backupToMail', '邮件保存应用备份文件', '1', 'switch', null, '1', '1', 50, '系统参数');

call add_version('20190000000083', '页面目录结构重构', '2020-04-04', 395, '2');
call add_version('20190000000084', '字典项信息倒序添加', '2020-04-04', 400, '2');
call add_version('20190000000085', '列表行操作事件', '2020-04-04', 405, '1');
call add_version('20190000000086', '系统定时备份、邮件保存备份文件', '2020-04-05', 410, '1');

call add_column('sys_config', 'user_id', 'number(30)', '0');
comment on column sys_config.user_id is '用户ID';
alter table sys_config drop primary key;
alter table sys_config add primary key (module_group_code, module_code, user_id);
call add_version('20190000000088', '首页模块控制绑定用户信息', '2020-04-12', 420, '2');

update sys_parameter set parameter_group = '邮件参数', parameter_order = 100 where parameter_code = 'startMail';
update sys_parameter set parameter_group = '邮件参数', parameter_order = 105 where parameter_code = 'backupToMail';

call add_parameter('mailFrom', '发送邮箱', '', 'text', null, '1', '1', 110, '邮件参数');
call add_parameter('mailUsername', '发送邮件用户名', '', 'text', null, '1', '1', 115, '邮件参数');
call add_parameter('mailPassword', '发送邮件密码', '', 'text', null, '1', '1', 120, '邮件参数');
call add_parameter('mailHost', '发送邮件服务器', 'smtp.qq.com', 'text', null, '1', '1', 125, '邮件参数');
call add_parameter('mailProtocol', '发送邮件传输协议', 'smtp', 'text', null, '1', '1', 130, '邮件参数');
call add_parameter('mailDebug', '邮件发送调试模式', '2', 'switch', null, '1', '1', 135, '邮件参数');
call add_parameter('mailEncoding', '邮件发送编码格式', 'UTF-8', 'text', null, '1', '1', 140, '邮件参数');
call add_parameter('mailReceiveUsername', '读取邮件用户名', '', 'text', null, '1', '1', 145, '邮件参数');
call add_parameter('mailReceivePassword', '读取邮件密码', '', 'text', null, '1', '1', 150, '邮件参数');
call add_parameter('mailSubject', '读取邮件主题', '家庭信息平台', 'text', null, '1', '1', 155, '邮件参数');
call add_parameter('mailReceiveFolder', '读取邮件类型', 'INBOX', 'text', null, '1', '0', 160, '邮件参数');
call add_parameter('mailReceiveHost', '读取邮件服务器', 'imap.qq.com', 'text', null, '1', '1', 165, '邮件参数');
call add_parameter('mailReceiveProtocol', '读取邮件传输协议', 'imap', 'text', null, '1', '1', 170, '邮件参数');

call add_version('20190000000089', '邮件配置信息移至数据库', '2020-04-18', 425, '2');

call add_version('20190000000091', '字典项增删后排序错乱', '2020-07-04', 435, '3');

alter table sys_wechat_flow modify flow_order number(12,2);
update sys_wechat_flow set flow_num = flow_num + 1 where flow_num > 5 and flow_num != 99;
delete from sys_wechat_flow where flow_id = 20200000000018;
insert into sys_wechat_flow (flow_id, flow_num, flow_code, flow_describe, flow_tips, flow_type, flow_order, create_date, modify_date, create_user, modify_user, is_show)
values (20200000000018, '6', 'income-add-same', '收入新增 - 同上一笔', '请按如下格式输入收入信息

收入金额: 支持两位小数
收入备注: 最大150字符', '1', 5.1, to_timestamp('2020-07-17 21:00:00', 'yyyy-MM-dd hh24:mi:ss'), to_timestamp('2020-07-17 21:00:00', 'yyyy-MM-dd hh24:mi:ss'), '20190000000001', '20190000000001', 'D013-1');

commit;

call add_version('20190000000092', '微信：收入新增同上一笔模式', '2020-07-17', 440, '1');

update sys_parameter set parameter_group = replace(parameter_group, '参数', '信息');
commit;

call add_version('20190000000095', '首页最近一次登入时间显示错误', '2021-01-31', 455, '3');
call add_version('20190000000096', '消息通知-全部已读 更新历史已读信息状态', '2021-01-31', 460, '3');
call add_version('20190000000097', '发布版本：1.0.0.0', '2021-01-31', 465, '4');
call update_system_version('1.0.0.0');
commit;




