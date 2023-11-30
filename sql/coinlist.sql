create table if not exists rate_coin
(
    id            bigint auto_increment comment '主键id'
        primary key,
    coin_name     varchar(20)   null comment '币地址',
    token_address varchar(80)   null comment '钱包地址',
    data_status   int default 1 null comment '数据状态:1，有效，0，无效',
    create_time   datetime      null comment '创建时间',
    update_time   datetime      null comment '更新时间',
    chain_type varchar(50) null comment '链',
    constraint rate_coin_token_address_uindex
        unique (token_address)
)
    comment '有费率的币' charset = utf8;

-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('有费率的币', '1', '1', '/swap/coinlist', 'C', '0', 'swap:coinlist:view', '#', 'admin', sysdate(), '', null, '有费率的币菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('有费率的币查询', @parentId, '1',  '#',  'F', '0', 'swap:coinlist:list',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('有费率的币新增', @parentId, '2',  '#',  'F', '0', 'swap:coinlist:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('有费率的币修改', @parentId, '3',  '#',  'F', '0', 'swap:coinlist:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('有费率的币删除', @parentId, '4',  '#',  'F', '0', 'swap:coinlist:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('有费率的币导出', @parentId, '5',  '#',  'F', '0', 'swap:coinlist:export',       '#', 'admin', sysdate(), '', null, '');
