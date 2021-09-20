/*
 * MIT License
 *
 * Copyright (c) 2021 PS Coetsee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

DELIMITER $$

CREATE DATABASE IF NOT EXISTS `settings_service`
$$

CREATE TABLE IF NOT EXISTS `settings_service`.`services`
(
    `id`            BIGINT(20)    NOT NULL AUTO_INCREMENT,
    `name`          VARCHAR(256)  NOT NULL,
    `password`      VARCHAR(2048) NOT NULL,
    `admin`         TINYINT(1)    NOT NULL DEFAULT FALSE,
    `creation_time` BIGINT(20)    NOT NULL,

    CONSTRAINT `pk_services_id` PRIMARY KEY (`id`),
    INDEX `idx_services_name` (`name`)
)
    Engine = InnoDB
$$

CREATE TABLE IF NOT EXISTS `settings_service`.`settings`
(
    `id`             BIGINT(20)    NOT NULL AUTO_INCREMENT,
    `service_id`     BIGINT(20)    NOT NULL,
    `name`           VARCHAR(256)  NOT NULL,
    `value`          VARCHAR(4096) NOT NULL,
    `date_last_used` DATE,

    CONSTRAINT `pk_settings_id` PRIMARY KEY (`id`),
    CONSTRAINT `fk_settings_service_id_service_id` FOREIGN KEY (`service_id`) REFERENCES `services` (`id`),
    CONSTRAINT `uk_service_id_setting_name` UNIQUE KEY (`service_id`, `name`)
)
    Engine = InnoDB
$$