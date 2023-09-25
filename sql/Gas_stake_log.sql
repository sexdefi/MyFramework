CREATE TABLE `gas_operate_log`
(
    `id`       int(11) NOT NULL AUTO_INCREMENT,
    `userAddr` varchar(100) DEFAULT NULL,
    `type`     varchar(10)  DEFAULT NULL,
    `amount`   varchar(255) DEFAULT NULL,
    `optime`   varchar(100) DEFAULT NULL,
    `remark`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1;

CREATE TABLE `gas_transfer_log`
(
    `id`       int(11) NOT NULL AUTO_INCREMENT,
    `userAddr` varchar(100) DEFAULT NULL,
    `amount`   varchar(255) DEFAULT NULL,
    `txhash`   varchar(255) DEFAULT NULL,
    `optime`   varchar(100) DEFAULT NULL,
    `remark`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1;

-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('gas领取操作记录表', '1', '1', '/gas/operate', 'C', '0', 'gas:operate:view', '#', 'admin', sysdate(), '', null, 'gas领取操作记录表菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('gas领取操作记录表查询', @parentId, '1',  '#',  'F', '0', 'gas:operate:list',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('gas领取操作记录表新增', @parentId, '2',  '#',  'F', '0', 'gas:operate:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('gas领取操作记录表修改', @parentId, '3',  '#',  'F', '0', 'gas:operate:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('gas领取操作记录表删除', @parentId, '4',  '#',  'F', '0', 'gas:operate:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('gas领取操作记录表导出', @parentId, '5',  '#',  'F', '0', 'gas:operate:export',       '#', 'admin', sysdate(), '', null, '');

-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('Gas领取空投记录', '1', '1', '/gas/transferLog', 'C', '0', 'gas:transferLog:view', '#', 'admin', sysdate(), '', null, 'Gas领取空投记录菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('Gas领取空投记录查询', @parentId, '1',  '#',  'F', '0', 'gas:transferLog:list',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('Gas领取空投记录新增', @parentId, '2',  '#',  'F', '0', 'gas:transferLog:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('Gas领取空投记录修改', @parentId, '3',  '#',  'F', '0', 'gas:transferLog:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('Gas领取空投记录删除', @parentId, '4',  '#',  'F', '0', 'gas:transferLog:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('Gas领取空投记录导出', @parentId, '5',  '#',  'F', '0', 'gas:transferLog:export',       '#', 'admin', sysdate(), '', null, '');
