-- 菜单 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('代币余额表', '1', '1', '/token/token', 'C', '0', 'token:token:view', '#', 'admin', sysdate(), '', null, '代币余额表菜单');

-- 按钮父菜单ID
SELECT @parentId := LAST_INSERT_ID();

-- 按钮 SQL
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('代币余额表查询', @parentId, '1',  '#',  'F', '0', 'token:token:list',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('代币余额表新增', @parentId, '2',  '#',  'F', '0', 'token:token:add',          '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('代币余额表修改', @parentId, '3',  '#',  'F', '0', 'token:token:edit',         '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('代币余额表删除', @parentId, '4',  '#',  'F', '0', 'token:token:remove',       '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_by, create_time, update_by, update_time, remark)
values('代币余额表导出', @parentId, '5',  '#',  'F', '0', 'token:token:export',       '#', 'admin', sysdate(), '', null, '');
