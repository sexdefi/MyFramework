/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:8087
 Source Schema         : ry

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 14/08/2023 13:53:29
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for airdrop
-- ----------------------------
DROP TABLE IF EXISTS `airdrop`;
CREATE TABLE `airdrop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `address` varchar(100)  DEFAULT NULL,
  `gas` varchar(255) DEFAULT NULL,
  `nowday` varchar(100) DEFAULT NULL,
  `txhash` varchar(255) DEFAULT NULL,
  `result` varchar(100) DEFAULT NULL,
  `start` varchar(100) DEFAULT NULL,
  `end` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ;

SET FOREIGN_KEY_CHECKS = 1;


CREATE TABLE `airdrop_batch_log` (
     `id` int NOT NULL AUTO_INCREMENT,
     `batch_no` varchar(20) DEFAULT NULL COMMENT '批次号',
     `address` varchar(100) DEFAULT NULL COMMENT '地址',
     `token_address` varchar(100) DEFAULT NULL COMMENT '快照token地址',
     `token_amount` varchar(255) DEFAULT NULL COMMENT '快照token数量',
     `amount` varchar(255) DEFAULT NULL COMMENT '空投数量',
     `airdrop_time` varchar(100) DEFAULT NULL COMMENT '空投时间',
     `snapshot_time` varchar(100) DEFAULT NULL COMMENT '快照时间',
     `ad_status` varchar(10) DEFAULT NULL COMMENT '空投状态（0：未空投，1：空投中，2：空投成功，3：空投失败）',
     `airdrop_hash` varchar(100) DEFAULT NULL COMMENT '空投hash',
     `batch_index` varchar(20) DEFAULT NULL COMMENT '空投ID',
     `remark` varchar(255) DEFAULT NULL,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='空投批次快照表';

CREATE TABLE `profit_log` (
      `id` INT NOT NULL AUTO_INCREMENT,
      `clear_date` VARCHAR ( 100 ) DEFAULT NULL COMMENT '日期',
      `tx_count` VARCHAR ( 100 ) DEFAULT NULL COMMENT '交易笔数',
      `total_fee` VARCHAR ( 255 ) DEFAULT NULL COMMENT '总交易手续费',
      `fee_return` VARCHAR ( 255 ) DEFAULT NULL COMMENT '交易费返还',
      `success_fee` VARCHAR ( 255 ) DEFAULT NULL COMMENT '成功交易的手续费',
      `fail_fee` VARCHAR ( 255 ) DEFAULT NULL COMMENT '交易失败的手续费',
      `profit` VARCHAR ( 255 ) DEFAULT NULL COMMENT '利润',
      `remark` VARCHAR ( 255 ) DEFAULT NULL COMMENT '备注',
      PRIMARY KEY ( `id` )
) ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COMMENT = '当天交易数据表';