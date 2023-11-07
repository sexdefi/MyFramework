CREATE TABLE `ad_config` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `title` varchar(255) DEFAULT NULL,
                             `des` varchar(255) DEFAULT NULL,
                             `jump_url` varchar(255) DEFAULT NULL,
                             `image` varchar(255) DEFAULT NULL,
                             `classification` varchar(255) DEFAULT NULL,
                             `weights` int(11) DEFAULT NULL,
                             `data_status` int(11) DEFAULT NULL,
                             `create_time` datetime DEFAULT NULL,
                             `update_time` datetime DEFAULT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;