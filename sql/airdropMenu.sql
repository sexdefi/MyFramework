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
