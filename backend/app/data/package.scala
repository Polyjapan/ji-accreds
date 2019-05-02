import java.sql.Timestamp

/**
  * @author Louis Vialar
  */
package object data {

  case class Edition(id: Option[Int], endDate: Timestamp, name: String)

  /*
create table `fields`
(
    `id`        INTEGER                                                                                  NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name`      VARCHAR(100)                                                                             NOT NULL,
    `label`     VARCHAR(150)                                                                             NOT NULL,
    `help_text` TEXT                                                                                     NULL,
    `required`  BOOLEAN,
    `type`      SET ('text', 'long_text', 'email', 'date', 'checkbox', 'select', 'file', 'image', 'url') NOT NULL
);
   */
  case class Field(id: Option[Int], name: String, label: String, helpText: Option[String], required: Boolean, fieldType: String)


  /*
create table `request_types`
(
    `id`             INTEGER      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `edition`        INTEGER      NOT NULL,
    `internal_name`  VARCHAR(100) NOT NULL,
    `name`           VARCHAR(100) NOT NULL,
    `required_group` VARCHAR(100) NULL,
    `hidden`         BOOLEAN,

    FOREIGN KEY (`edition`) REFERENCES `editions` (`id`),
    UNIQUE KEY (`edition`, `internal_name`)
);
   */
  case class RequestType(id: Option[Int], edition: Int, internalName: String, name: String, requiredGroup: Option[String], hidden: Boolean)

  /*
  create table `requests`
  (
    `id`           INTEGER                                                           NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id`      INTEGER                                                           NULL,
    `claim_code`   VARCHAR(100)                                                      NULL,
    `request_type` INTEGER                                                           NOT NULL,
    `state`        SET ('draft', 'sent', 'requested_changes', 'accepted', 'refused') NOT NULL DEFAULT 'draft',

  FOREIGN KEY (`request_type`) REFERENCES `request_types` (`id`)
  );
   */
  case class Request(id: Option[Int], userId: Option[Int], claimCode: Option[String], requestType: Int, state: String)

  /*
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

   create table `accred_logs`
  (
    `accred_id`  INTEGER                                                                        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `from_state` SET ('draft', 'sent', 'requested_changes', 'accepted', 'printed')              NOT NULL,
  `to_state`   SET ('draft', 'sent', 'requested_changes', 'accepted', 'printed', 'delivered') NOT NULL,
  `reason`     VARCHAR(250)                                                                   NULL,
  `timestamp`  TIMESTAMP                                                                      NOT NULL DEFAULT current_timestamp,
  `changed_by` INT                                                                            NOT NULL,

  FOREIGN KEY (`accred_id`) REFERENCES `accreds` (`id`)
  );
   */
  case class Log(request: Int, fromState: String, toState: String, reason: String, timestamp: Timestamp, changedBy: Int)


  /*create table `accred_types`
  (
    `id`              INTEGER      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `edition`         INTEGER      NOT NULL,
    `internal_name`   VARCHAR(100) NOT NULL,
    `name`            VARCHAR(100) NOT NULL,
    `is_self_service` BOOLEAN,
    `is_printable`    BOOLEAN,

    FOREIGN KEY (`edition`) REFERENCES `editions` (`id`),
    UNIQUE KEY (`edition`, `internal_name`)
  );*/
  case class AccredType(id: Option[Int], edition: Int, internalName: String, name: String,
                        selfService: Boolean, printable: Boolean)

  /*
  create table `accreds`
  (
    `id`          INTEGER                                                                        NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `request_id`  INTEGER                                                                        NOT NULL,
    `accred_type` INTEGER                                                                        NOT NULL,
    `state`       SET ('draft', 'sent', 'requested_changes', 'accepted', 'printed', 'delivered') NOT NULL DEFAULT 'draft',

  FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`),
  FOREIGN KEY (`accred_type`) REFERENCES `accred_types` (`id`)
  );*/
  case class Accred(id: Option[Int], requestId: Int, accredType: Int, state: String)




  /*
  # --- !Ups
# General stuff



# Requests stuff


create table `fields_additional`
(
    `field` INTEGER      NOT NULL,
    `key`   VARCHAR(200) NOT NULL,
    `value` VARCHAR(200) NOT NULL,

    PRIMARY KEY (`field`, `key`),
    FOREIGN KEY (`field`) REFERENCES `fields` (`id`)
);


create table `request_type_fields`
(
    `field_id`     INTEGER NOT NULL PRIMARY KEY,
    `request_type` INTEGER NOT NULL,
    `hidden`       BOOLEAN,


    FOREIGN KEY (`field_id`) REFERENCES `fields` (`id`),
    FOREIGN KEY (`request_type`) REFERENCES `request_types` (`id`)
);

create table `accred_type_fields`
(
    `field_id`      INTEGER NOT NULL PRIMARY KEY,
    `accred_type`   INTEGER NOT NULL,
    `user_editable` BOOLEAN,


    FOREIGN KEY (`field_id`) REFERENCES `fields` (`id`),
    FOREIGN KEY (`accred_type`) REFERENCES `accred_types` (`id`)
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



  create table `accred_contents`
  (
    `accred_id` INTEGER NOT NULL,
    `field_id`  INTEGER NOT NULL,
    `value`     TEXT    NOT NULL,

    PRIMARY KEY (`accred_id`, `field_id`),
    FOREIGN KEY (`accred_id`) REFERENCES `accred_types` (`id`),
    FOREIGN KEY (`field_id`) REFERENCES `fields` (`id`)
  );

# Accreds stuff

   */

}
