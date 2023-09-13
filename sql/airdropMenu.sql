-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('airdrop', '1', '1', '/airdropLog/airdrop', 'C', '0', 'airdropLog:airdrop:view', '#', 'admin', sysdate(), '', null, 'airdrop菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('airdrop查询', @parentId, '1',  '#',  'F', '0', 'airdropLog:airdrop:list',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('airdrop新增', @parentId, '2',  '#',  'F', '0', 'airdropLog:airdrop:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('airdrop修改', @parentId, '3',  '#',  'F', '0', 'airdropLog:airdrop:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('airdrop删除', @parentId, '4',  '#',  'F', '0', 'airdropLog:airdrop:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('airdrop导出', @parentId, '5',  '#',  'F', '0', 'airdropLog:airdrop:export',       '#', 'admin', sysdate(), '', null, '');


-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('空投批次快照', '1', '1', '/batch/batch', 'C', '0', 'batch:batch:view', '#', 'admin', sysdate(), '', null, '空投批次快照菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('空投批次快照查询', @parentId, '1',  '#',  'F', '0', 'batch:batch:list',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('空投批次快照新增', @parentId, '2',  '#',  'F', '0', 'batch:batch:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('空投批次快照修改', @parentId, '3',  '#',  'F', '0', 'batch:batch:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('空投批次快照删除', @parentId, '4',  '#',  'F', '0', 'batch:batch:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('空投批次快照导出', @parentId, '5',  '#',  'F', '0', 'batch:batch:export',       '#', 'admin', sysdate(), '', null, '');


-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('当天交易数据', '1', '1', '/Profit/Profit', 'C', '0', 'Profit:Profit:view', '#', 'admin', sysdate(), '', null, '当天交易数据菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('当天交易数据查询', @parentId, '1',  '#',  'F', '0', 'Profit:Profit:list',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('当天交易数据新增', @parentId, '2',  '#',  'F', '0', 'Profit:Profit:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('当天交易数据修改', @parentId, '3',  '#',  'F', '0', 'Profit:Profit:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('当天交易数据删除', @parentId, '4',  '#',  'F', '0', 'Profit:Profit:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('当天交易数据导出', @parentId, '5',  '#',  'F', '0', 'Profit:Profit:export',       '#', 'admin', sysdate(), '', null, '');
