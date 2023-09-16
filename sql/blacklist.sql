CREATE TABLE `blacklist` (
     `id` int(11) NOT NULL AUTO_INCREMENT,
     `addr` varchar(100) NOT NULL,
     `remark` varchar(255) DEFAULT NULL,
     `enable` int(11) DEFAULT '0',
     PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ;