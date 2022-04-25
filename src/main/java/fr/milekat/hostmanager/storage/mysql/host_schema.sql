-- HostManager MySQL/MariaDB schema

START TRANSACTION;
SET time_zone = "+00:00";

-- TABLES setup

CREATE TABLE `{prefix}games` (
  `id` smallint(5) UNSIGNED NOT NULL,
  `name` varchar(32) NOT NULL COMMENT 'Game name',
  `create_date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Date when this line was added, UTC time',
  `enable` tinyint(1) NOT NULL DEFAULT 0 COMMENT 'Bool: if the game is enable and playable',
  `image` varchar(255) NOT NULL COMMENT 'Full image repo of this game',
  `requirements` smallint(5) UNSIGNED NOT NULL DEFAULT 2048 COMMENT 'amount of needed RAM to run this game (MB)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='List of all created games';
CREATE INDEX `{prefix}game_id` ON `{prefix}games` (`id`);

CREATE TABLE `{prefix}instances` (
  `id` tinyint(3) UNSIGNED NOT NULL,
  `name` varchar(16) NOT NULL,
  `available` tinyint(1) NOT NULL DEFAULT 1 COMMENT '1: available, 0: used',
  `server_uuid` varchar(36) DEFAULT NULL COMMENT 'UUID of pterodactyl server',
  `user` int(10) UNSIGNED DEFAULT NULL COMMENT 'Who use this instance (currently)',
  `game` smallint(5) UNSIGNED NOT NULL,
  `last_creation` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Last date of server creation',
  `last_deletion` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Last date of server deletion'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='This table list all instances, with their current state';
CREATE INDEX `{prefix}instances_name` ON `{prefix}instances` (`name`);

CREATE TABLE `{prefix}logs` (
  `id` int(10) UNSIGNED NOT NULL,
  `date` timestamp NOT NULL DEFAULT current_timestamp() COMMENT 'Log date',
  `instance` tinyint(3) UNSIGNED DEFAULT NULL COMMENT 'Id of targeted instance',
  `action` varchar(16) NOT NULL,
  `user` int(10) UNSIGNED NOT NULL COMMENT 'Who trigger this log (Should be an UUID)',
  `game` smallint(5) UNSIGNED NOT NULL COMMENT 'Game of this log'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='This table log every actions from host management system';

CREATE TABLE `{prefix}users` (
  `id` int(10) UNSIGNED NOT NULL,
  `uuid` varchar(36) NOT NULL COMMENT 'UUID of player',
  `last_name` varchar(16) NOT NULL COMMENT 'Name of player',
  `tickets` smallint(5) UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Amount of available host for this player'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='List of host users';
CREATE INDEX `{prefix}users_uuid` ON `{prefix}users` (`uuid`);

-- Index

ALTER TABLE `{prefix}games`
    ADD PRIMARY KEY (`id`),
    ADD UNIQUE KEY `unique_name` (`name`) USING BTREE;

ALTER TABLE `{prefix}instances`
    ADD PRIMARY KEY (`id`),
    ADD KEY `user` (`user`),
    ADD KEY `game` (`game`);

ALTER TABLE `{prefix}logs`
    ADD PRIMARY KEY (`id`),
    ADD KEY `user` (`user`),
    ADD KEY `game` (`game`),
    ADD KEY `instance` (`instance`);

ALTER TABLE `{prefix}users`
    ADD PRIMARY KEY (`id`);

-- AUTO_INCREMENT

ALTER TABLE `{prefix}games`
    MODIFY `id` smallint(5) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `{prefix}instances`
    MODIFY `id` tinyint(3) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `{prefix}logs`
    MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `{prefix}users`
    MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

-- FOREIGN KEY

ALTER TABLE `{prefix}instances`
    ADD CONSTRAINT `host_instances_ibfk_1` FOREIGN KEY (`user`) REFERENCES `{prefix}users` (`id`),
    ADD CONSTRAINT `host_instances_ibfk_2` FOREIGN KEY (`game`) REFERENCES `{prefix}games` (`id`);

ALTER TABLE `{prefix}logs`
    ADD CONSTRAINT `host_logs_ibfk_1` FOREIGN KEY (`user`) REFERENCES `{prefix}users` (`id`),
    ADD CONSTRAINT `host_logs_ibfk_2` FOREIGN KEY (`game`) REFERENCES `{prefix}games` (`id`),
    ADD CONSTRAINT `host_logs_ibfk_3` FOREIGN KEY (`instance`) REFERENCES `{prefix}instances` (`id`);

COMMIT;