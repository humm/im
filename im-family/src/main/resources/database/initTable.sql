-- 初始化表结构

-- 字段说明
-- 主键 number(30)
-- 备注 varchar2(500)
-- 排序 number(10)
-- 是否 varchar2(1)
-- 金额 number(20, 2)
-- 其他 varchar2(50)

-- 用户信息
call drop_table('sys_user');
create table sys_user
(
    user_id       number(30) primary key,
    user_code     varchar2(50) not null,
    user_name     varchar2(50) not null,
    user_password varchar2(50) not null,
    user_status   varchar2(50)  not null,
    user_memo     varchar2(500) default '',
    create_date   timestamp(6) default sysdate,
    modify_date   timestamp(6) default sysdate,
    create_user   varchar2(50),
    modify_user   varchar2(50)
);
comment on column sys_user.user_id
    is '用户ID';
comment on column sys_user.user_code
    is '用户代码';
comment on column sys_user.user_name
    is '用户名称';
comment on column sys_user.user_password
    is '用户密码';
comment on column sys_user.user_status
    is '用户状态';
comment on column sys_user.create_date
    is '创建时间';
comment on column sys_user.modify_date
    is '修改时间';
comment on column sys_user.create_user
    is '创建人';
comment on column sys_user.modify_user
    is '修改人';
comment on column sys_user.user_memo
    is '备注';

-- 收入信息
call drop_table('sys_income');
create table sys_income
(
    income_id      number(30) primary key,
    user_id        number(30)    not null,
    income_type    varchar2(50)  not null,
    income_date    date          not null,
    income_company varchar2(50)  not null,
    income_amount  number(20, 2) not null,
    income_memo    varchar2(500) default '',
    create_date    timestamp(6) default sysdate,
    modify_date    timestamp(6) default sysdate,
    create_user    varchar2(50),
    modify_user    varchar2(50)
);
comment on column sys_income.income_id
    is '收入ID';
comment on column sys_income.user_id
    is '收入人';
comment on column sys_income.income_type
    is '收入类型';
comment on column sys_income.income_date
    is '收入日期';
comment on column sys_income.income_company
    is '收入来源';
comment on column sys_income.income_amount
    is '收入金额';
comment on column sys_income.income_memo
    is '收入备注';
comment on column sys_income.create_date
    is '创建时间';
comment on column sys_income.modify_date
    is '修改时间';
comment on column sys_income.create_user
    is '创建人';
comment on column sys_income.modify_user
    is '修改人';

-- 字典信息
call drop_table('sys_dictionary');
create table sys_dictionary
(
    dictionary_code    varchar2(50)  not null,
    dictionary_item    varchar2(50)  not null,
    dictionary_caption varchar2(100) not null,
    item_order         number(10),
    code_order         number(10),
    user_id            number(30),
    is_open            varchar2(1),
    is_show            varchar2(1),
    constraint pk_sys_dictionary primary key (dictionary_code, dictionary_item)
);

comment on column sys_dictionary.dictionary_code
    is '字典代码';
comment on column sys_dictionary.dictionary_item
    is '字典选值';
comment on column sys_dictionary.dictionary_caption
    is '字典描述';
comment on column sys_dictionary.item_order
    is '选值排序';
comment on column sys_dictionary.code_order
    is '代码排序';
comment on column sys_dictionary.user_id
    is '用户ID';
comment on column sys_dictionary.is_open
    is '是否开放';
comment on column sys_dictionary.is_show
    is '是否显示';

-- 通知信息
call drop_table('sys_notice');
create table sys_notice
(
    notice_id           number(30)  primary key,
    user_id             number(30)  not null,
    business_id         number(30)  not null,
    business_type       varchar2(50) not null,
    business_sub_type   varchar2(50) not null,
    business_date       timestamp(6) default sysdate,
    business_amount     number(20, 2) not null,
    notice_status       varchar2(50) not null,
    notice_type         varchar2(50) not null,
    read_status         varchar2(6) not null,
    create_date         timestamp(6) default sysdate,
    modify_date         timestamp(6) default sysdate,
    create_user         varchar2(50),
    modify_user         varchar2(50)
);

comment on column sys_notice.notice_id
    is '通知ID';
comment on column sys_notice.user_id
    is '用户ID';
comment on column sys_notice.business_id
    is '业务ID';
comment on column sys_notice.business_type
    is '业务类型';
comment on column sys_notice.business_sub_type
    is '业务子类型';
comment on column sys_notice.business_date
    is '业务日期';
comment on column sys_notice.business_amount
    is '业务金额';
comment on column sys_notice.notice_status
    is '通知状态';
comment on column sys_notice.notice_type
    is '通知类型';
comment on column sys_notice.read_status
    is '阅读状态';
comment on column sys_notice.create_date
    is '创建时间';
comment on column sys_notice.modify_date
    is '修改时间';
comment on column sys_notice.create_user
    is '创建人';
comment on column sys_notice.modify_user
    is '修改人';

-- 随礼信息
call drop_table('sys_gift');
create table sys_gift
(
    gift_id       number(30)  primary key,
    gift_type     varchar2(50) not null,
    gift_sender   varchar2(50) not null,
    gift_receiver varchar2(50) not null,
    gift_date     date not null,
    gift_amount   number(20,2) not null,
    gift_memo     varchar2(500) default '',
    create_date   timestamp(6) default sysdate,
    modify_date   timestamp(6) default sysdate,
    create_user   varchar2(50),
    modify_user   varchar2(50)
);

comment on column sys_gift.gift_id
    is '随礼ID';
comment on column sys_gift.gift_type
    is '随礼类型';
comment on column sys_gift.gift_sender
    is '送礼人';
comment on column sys_gift.gift_receiver
    is '收礼人';
comment on column sys_gift.gift_date
    is '随礼日期';
comment on column sys_gift.gift_amount
    is '随礼金额';
comment on column sys_gift.gift_memo
    is '随礼备注';
comment on column sys_gift.create_date
    is '创建时间';
comment on column sys_gift.modify_date
    is '修改时间';
comment on column sys_gift.create_user
    is '创建人';
comment on column sys_gift.modify_user
    is '修改人';

-- 菜单信息
call drop_table('sys_menu');
create table sys_menu
(
    menu_id       number(30) primary key,
    menu_title    varchar2(50) not null,
    menu_icon     varchar2(50),
    menu_url      varchar2(500),
    parent_id     number(30),
    menu_order    number(10, 2) not null,
    is_enable     varchar2(1) not null,
    menu_type     varchar2(50) not null,
    create_date   timestamp(6) default sysdate,
    modify_date   timestamp(6) default sysdate,
    create_user   varchar2(50),
    modify_user   varchar2(50)
);

comment on column sys_menu.menu_id
    is '菜单ID';
comment on column sys_menu.menu_title
    is '菜单名称';
comment on column sys_menu.menu_icon
    is '菜单图标';
comment on column sys_menu.menu_url
    is '菜单地址';
comment on column sys_menu.parent_id
    is '父级菜单ID';
comment on column sys_menu.menu_order
    is '菜单排序';
comment on column sys_menu.is_enable
    is '是否启用';
comment on column sys_menu.menu_type
    is '菜单类型';
comment on column sys_menu.create_date
    is '创建时间';
comment on column sys_menu.modify_date
    is '修改时间';
comment on column sys_menu.create_user
    is '创建人';
comment on column sys_menu.modify_user
    is '修改人';

-- 角色信息
call drop_table('sys_role');
create table sys_role
(
    role_id     number(30) primary key,
    role_code   varchar2(50) not null,
    role_name   varchar2(50) not null,
    role_memo   varchar2(500) default '',
    create_date timestamp(6) default sysdate,
    modify_date timestamp(6) default sysdate,
    create_user varchar2(50),
    modify_user varchar2(50)
);

comment on column sys_role.role_id
    is '角色ID';
comment on column sys_role.role_code
    is '角色代码';
comment on column sys_role.role_name
    is '角色名称';
comment on column sys_role.role_memo
    is '角色备注';
comment on column sys_role.create_date
    is '创建时间';
comment on column sys_role.modify_date
    is '修改时间';
comment on column sys_role.create_user
    is '创建人';
comment on column sys_role.modify_user
    is '修改人';

-- 用户角色信息
call drop_table('sys_user_role');
create table sys_user_role
(
    user_role_id  number(30) primary key,
    user_id number(30) not null,
    role_id number(30) not null
);

comment on column sys_user_role.user_role_id
    is '用户角色ID';
comment on column sys_user_role.user_id
    is '用户ID';
comment on column sys_user_role.role_id
    is '角色ID';

-- 角色菜单信息
call drop_table('sys_role_menu');
create table sys_role_menu
(
    role_menu_id  number(30) primary key,
    role_id number(30),
    menu_id number(30)
);

comment on column sys_role_menu.role_menu_id
    is '角色菜单ID';
comment on column sys_role_menu.role_id
    is '角色ID';
comment on column sys_role_menu.menu_id
    is '菜单ID';

-- 参数信息
call drop_table('sys_parameter');
create table sys_parameter
(
    parameter_code    varchar2(50) primary key,
    parameter_caption varchar2(50),
    parameter_value   varchar2(500),
    parameter_type    varchar2(50),
    parameter_ext     varchar2(50),
    is_show           varchar2(1),
    is_edit           varchar2(1),
    parameter_order   number(10)
);

comment on column sys_parameter.parameter_code
    is '参数代码';
comment on column sys_parameter.parameter_caption
    is '参数描述';
comment on column sys_parameter.parameter_value
    is '参数值';
comment on column sys_parameter.parameter_type
    is '参数类型';
comment on column sys_parameter.parameter_ext
    is '扩展参数';
comment on column sys_parameter.is_show
    is '是否显示';
comment on column sys_parameter.is_edit
    is '是否可编辑';
comment on column sys_parameter.parameter_order
    is '参数排序';

-- 登录日志信息
call drop_table('sys_login_log');
create table sys_login_log
(
    log_id        number(30) primary key,
    user_id       number(30),
    login_date    timestamp(6) default sysdate,
    logout_date   timestamp(6),
    login_status  varchar2(50),
    login_message varchar2(50)
);

comment on column sys_login_log.log_id
    is '日志ID';
comment on column sys_login_log.user_id
    is '用户ID';
comment on column sys_login_log.login_date
    is '登录时间';
comment on column sys_login_log.logout_date
    is '登出时间';
comment on column sys_login_log.login_status
    is '登录状态';
comment on column sys_login_log.login_message
    is '登录信息';

-- 修订信息
call drop_table('sys_version');
create table sys_version
(
    version_id        number(30) primary key,
    version_content   varchar2(500),
    version_date      date,
    version_order     number(10),
    version_type      varchar2(50)
);

comment on column sys_version.version_id
    is '修订ID';
comment on column sys_version.version_content
    is '修订内容';
comment on column sys_version.version_date
    is '修订日期';
comment on column sys_version.version_order
    is '修订排序';
comment on column sys_version.version_type
    is '修订类型(1:功能 2:优化 3:修复 4:发版)';

-- 配置信息
call drop_table('sys_config');
create table sys_config
(
    module_group_code   varchar2(50),
    module_group_name   varchar2(50),
    module_code         varchar2(50),
    module_name         varchar2(50),
    module_status       varchar2(50),
    module_ext          varchar2(500),
    constraint sys_config_pk primary key(module_group_code , module_code)
);

comment on column sys_config.module_group_code
    is '模块组代码';
comment on column sys_config.module_group_name
    is '模块组名称';
comment on column sys_config.module_code
    is '模块代码';
comment on column sys_config.module_name
    is '模块名称';
comment on column sys_config.module_status
    is '模块状态';
comment on column sys_config.module_ext
    is '模块扩展参数';


-- 接口信息
call drop_table('sys_interface');
create table sys_interface
(
    interface_id        number(30) primary key,
    request_id          varchar2(50),
    request_data        varchar2(500),
    request_result      varchar2(50),
    request_message     varchar2(500),
    feedback_status     varchar2(500),
    create_date timestamp(6) default sysdate,
    modify_date timestamp(6) default sysdate,
    create_user varchar2(50),
    modify_user varchar2(50)
);

comment on column sys_interface.interface_id
    is '接口ID';
comment on column sys_interface.request_id
    is '申请ID';
comment on column sys_interface.request_data
    is '申请数据';
comment on column sys_interface.request_result
    is '处理结果';
comment on column sys_interface.request_message
    is '提示信息';
comment on column sys_interface.feedback_status
    is '反馈状态';
comment on column sys_role.create_date
    is '创建时间';
comment on column sys_role.modify_date
    is '修改时间';
comment on column sys_role.create_user
    is '创建人';
comment on column sys_role.modify_user
    is '修改人';

-- sys_parameter添加parameter_group字段
call add_column('sys_parameter', 'parameter_group', 'varchar2(50)', ''' ''');
comment on column sys_parameter.parameter_group
    is '参数分组';