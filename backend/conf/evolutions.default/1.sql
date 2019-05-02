# --- !Ups


# General stuff

create table `editions`
(
    `id`       INTEGER      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `end_date` DATETIME     NOT NULL,
    `name`     VARCHAR(100) NOT NULL
);

create table `fields`
(
    `id`        INTEGER                                                                                  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name`      VARCHAR(100)                                                                             NOT NULL,
    `label`     VARCHAR(150)                                                                             NOT NULL,
    `help_text` TEXT                                                                                     NULL,
    `required`  BOOLEAN,
    `type`      SET ('text', 'long_text', 'email', 'date', 'checkbox', 'select', 'file', 'image', 'url') NOT NULL
);

create table `fields_additional`
(
    `field` INTEGER      NOT NULL,
    `key`   VARCHAR(200) NOT NULL,
    `value` VARCHAR(200) NOT NULL,

    PRIMARY KEY (`field`, `key`),
    FOREIGN KEY (`field`) REFERENCES `fields` (`id`)
);


# Requests stuff

create table `request_types`
(
    `id`             INTEGER      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `edition`        INTEGER      NOT NULL,
    `name`           VARCHAR(100) NOT NULL,
    `required_group` VARCHAR(100) NULL,
    `hidden`         BOOLEAN,

    FOREIGN KEY (`edition`) REFERENCES `editions` (`id`)
);


create table `request_type_fields`
(
    `field_id`     INTEGER NOT NULL PRIMARY KEY,
    `request_type` INTEGER NOT NULL,
    `hidden`       BOOLEAN,


    FOREIGN KEY (`field_id`) REFERENCES `fields` (`id`),
    FOREIGN KEY (`request_type`) REFERENCES `request_types` (`id`)
);


create table `requests`
(
    `id`           INTEGER                                                           NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id`      INTEGER                                                           NULL,
    `claim_code`   VARCHAR(100)                                                      NULL,
    `request_type` INTEGER                                                           NOT NULL,
    `state`        SET ('draft', 'sent', 'requested_changes', 'accepted', 'refused') NOT NULL DEFAULT 'draft',

    FOREIGN KEY (`request_type`) REFERENCES `request_types` (`id`)
);


create table `request_logs`
(
    `request_id` INTEGER                                                           NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `from_state` SET ('draft', 'sent', 'requested_changes', 'refused')             NOT NULL,
    `to_state`   SET ('draft', 'sent', 'requested_changes', 'accepted', 'refused') NOT NULL,
    `reason`     VARCHAR(250)                                                      NULL,
    `timestamp`  TIMESTAMP                                                         NOT NULL DEFAULT current_timestamp,
    `changed_by` INT                                                               NOT NULL,

    FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`)
);


create table `request_contents`
(
    `request_id` INTEGER NOT NULL,
    `field_id`   INTEGER NOT NULL,
    `value`      TEXT    NOT NULL,

    PRIMARY KEY (`request_id`, `field_id`),
    FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`),
    FOREIGN KEY (`field_id`) REFERENCES `fields` (`id`)
);


# Accreds stuff

create table `accred_types`
(
    `id`              INTEGER      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `edition`         INTEGER      NOT NULL,
    `name`            VARCHAR(100) NOT NULL,
    `is_self_service` BOOLEAN,
    `is_printable`    BOOLEAN,

    FOREIGN KEY (`edition`) REFERENCES `editions` (`id`)
);

create table `accred_type_fields`
(
    `field_id`      INTEGER NOT NULL PRIMARY KEY,
    `accred_type`   INTEGER NOT NULL,
    `user_editable` BOOLEAN,


    FOREIGN KEY (`field_id`) REFERENCES `fields` (`id`),
    FOREIGN KEY (`accred_type`) REFERENCES `accred_types` (`id`)
);

create table `accreds`
(
    `id`          INTEGER                                                                        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `request_id`  INTEGER                                                                        NOT NULL,
    `accred_type` INTEGER                                                                        NOT NULL,
    `state`       SET ('draft', 'sent', 'requested_changes', 'accepted', 'printed', 'delivered') NOT NULL DEFAULT 'draft',

    FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`),
    FOREIGN KEY (`accred_type`) REFERENCES `accred_types` (`id`)
);


create table `accred_logs`
(
    `accred_id` INTEGER                                                                        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `from_state` SET ('draft', 'sent', 'requested_changes', 'accepted', 'printed')              NOT NULL,
    `to_state`   SET ('draft', 'sent', 'requested_changes', 'accepted', 'printed', 'delivered') NOT NULL,
    `reason`     VARCHAR(250)                                                                   NULL,
    `timestamp`  TIMESTAMP                                                                      NOT NULL DEFAULT current_timestamp,
    `changed_by` INT                                                                            NOT NULL,

    FOREIGN KEY (`accred_id`) REFERENCES `accreds` (`id`)
);


create table `accred_contents`
(
    `accred_id` INTEGER NOT NULL,
    `field_id`   INTEGER NOT NULL,
    `value`      TEXT    NOT NULL,

    PRIMARY KEY (`accred_id`, `field_id`),
    FOREIGN KEY (`accred_id`) REFERENCES `accred_types` (`id`),
    FOREIGN KEY (`field_id`) REFERENCES `fields` (`id`)
);

# --- !Downs

drop table accred_contents;

drop table accred_logs;

drop table accred_type_fields;

drop table accreds;

drop table accred_types;

drop table fields_additional;

drop table request_contents;

drop table request_logs;

drop table request_type_fields;

drop table fields;

drop table requests;

drop table request_types;

drop table editions;

