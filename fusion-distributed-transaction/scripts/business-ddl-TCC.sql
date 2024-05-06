CREATE database if NOT EXISTS `seata_storage` default character set utf8 collate utf8_unicode_ci;
use seata_storage;
DROP TABLE IF EXISTS `storage_tcc_tbl`;
CREATE TABLE `storage_tcc_tbl` (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `commodity_code` varchar(255) DEFAULT NULL,
                               `count` int(11) DEFAULT 0,
                               `pre_count` int(11) default 0,
                               PRIMARY KEY (`id`),
                               UNIQUE KEY (`commodity_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE database if NOT EXISTS `seata_order` default character set utf8 collate utf8_unicode_ci;
use seata_order;
DROP TABLE IF EXISTS `order_tcc_tbl`;
CREATE TABLE `order_tcc_tbl` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `user_id` varchar(255) DEFAULT NULL,
                             `commodity_code` varchar(255) DEFAULT NULL,
                             `count` int(11) DEFAULT 0,
                             `money` int(11) DEFAULT 0,
                             `status` varchar(2) DEFAULT NULL,
                             `create_time` datetime DEFAULT NULL,
                             `update_time` datetime DEFAULT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE database if NOT EXISTS `seata_account` default character set utf8 collate utf8_unicode_ci;
use seata_account;
DROP TABLE IF EXISTS `account_tcc_tbl`;
CREATE TABLE `account_tcc_tbl` (
                               `id` int(11) NOT NULL AUTO_INCREMENT,
                               `user_id` varchar(255) DEFAULT NULL,
                               `money` int(11) DEFAULT 0,
                               `pre_money` int(11) default 0,
                               PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;