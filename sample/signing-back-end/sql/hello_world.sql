SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tbl_cent_reg
-- ----------------------------
DROP TABLE IF EXISTS `tbl_cent_reg`;
CREATE TABLE `tbl_cent_reg`
(
  `id`          int(11) NOT NULL AUTO_INCREMENT,
  `user_name`   varchar(255) DEFAULT NULL,
  `password`    varchar(255) DEFAULT NULL,
  `ontid`       varchar(255) DEFAULT NULL,
  `owner`       varchar(255) DEFAULT NULL,
  `create_time` datetime     DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_ontid` (`ontid`),
  KEY `idx_user_name` (`user_name`),
  KEY `idx_owner` (`owner`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Table structure for tbl_claim
-- ----------------------------
DROP TABLE IF EXISTS `tbl_claim`;
CREATE TABLE `tbl_claim`
(
  `id`          varchar(255) NOT NULL COMMENT '主键',
  `ontid`       varchar(255) DEFAULT NULL COMMENT 'ontid',
  `claim`       longtext COMMENT '用户calim',
  `state`       int(2)       DEFAULT NULL COMMENT '是否认证成功;0-失败，1-成功',
  `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ontid` (`ontid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Table structure for tbl_invoke
-- ----------------------------
DROP TABLE IF EXISTS `tbl_invoke`;
CREATE TABLE `tbl_invoke`
(
  `id`          varchar(255) NOT NULL,
  `params`      text,
  `ontid`       varchar(255) DEFAULT NULL,
  `ontid_index` int(11)      DEFAULT NULL,
  `tx_hash`     varchar(255) DEFAULT NULL,
  `state`       int(2)       DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Table structure for tbl_login
-- ----------------------------
DROP TABLE IF EXISTS `tbl_login`;
CREATE TABLE `tbl_login`
(
  `id`          varchar(255) NOT NULL COMMENT '主键',
  `ontid`       varchar(255) DEFAULT NULL COMMENT 'ontid',
  `user_name`   varchar(255) DEFAULT NULL COMMENT '用户名',
  `state`       int(2)       DEFAULT NULL COMMENT '是否认证成功;0-失败，1-成功，2-未注册',
  `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ontid` (`ontid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Table structure for tbl_register
-- ----------------------------
DROP TABLE IF EXISTS `tbl_register`;
CREATE TABLE `tbl_register`
(
  `id`          varchar(255) NOT NULL COMMENT '主键',
  `ontid`       varchar(255) DEFAULT NULL COMMENT 'ontid',
  `user_name`   varchar(255) DEFAULT NULL COMMENT '用户名',
  `state`       int(2)       DEFAULT NULL COMMENT '是否认证成功;0-失败，1-成功，2-已存在',
  `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_ontid` (`ontid`),
  KEY `idx_user_name` (`user_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;