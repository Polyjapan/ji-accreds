import java.sql.Timestamp

import data._
import slick.lifted.ForeignKeyQuery

/**
  * @author Louis Vialar
  */
package object models {

  import slick.jdbc.MySQLProfile.api._

  private[models] trait Identified {
    def id: Rep[Int]
  }

  private[models] class Editions(tag: Tag) extends Table[Edition](tag, "editions") with Identified {
    def id = column[Int]("id")
    def endDate = column[Timestamp]("end_date")
    def name = column[String]("name")

    def * =
      (id.?, endDate, name).shaped <> (Edition.tupled, Edition.unapply)
  }

  private[models] val editions = TableQuery[Editions]


  private[models] class Fields(tag: Tag) extends Table[Field](tag, "fields") with Identified {
    def id = column[Int]("id")
    def name = column[String]("name")
    def label = column[String]("label")
    def helpText = column[Option[String]]("help_text")
    def required = column[Boolean]("reduired")
    def `type` = column[String]("type")

    def * =
      (id.?, name, label, helpText, required, `type`).shaped <> (Field.tupled, Field.unapply)

    def additional = fieldAdditionals.filter(_.fieldId === id)
  }

  private[models] val fields = TableQuery[Fields]

  private[models] class FieldAdditionals(tag: Tag) extends Table[(String, String)](tag, "fields_additional") {
    def fieldId = column[Int]("field")
    def key = column[String]("key")
    def value = column[String]("value")

    def * = (key, value)

    def field = foreignKey("", fieldId, fields)(_.id)
  }

  private[models] val fieldAdditionals = TableQuery[FieldAdditionals]


  private[models] class RequestTypes(tag: Tag) extends Table[RequestType](tag, "fields") with Identified  {
    def id = column[Int]("id")
    def editionId = column[Int]("edition")
    def internalName = column[String]("internal_name")
    def name = column[String]("name")
    def requiredGroup = column[Option[String]]("required_group")
    def hidden = column[Boolean]("hidden")

    def edition = foreignKey("", editionId, editions)(_.id)

    def * =
      (id.?, editionId, internalName, name, requiredGroup, hidden).shaped <> (RequestType.tupled, RequestType.unapply)

    def fields = requestTypeFields.filter(_.requestTypeId == id).flatMap(_.field)
  }

  private[models] val requestTypes = TableQuery[RequestTypes]

  private[models] class Requests(tag: Tag) extends Table[Request](tag, "requests") with Identified  {
    def id = column[Int]("id")
    def userId = column[Option[Int]]("user_id")
    def claimCode = column[Option[String]]("claim_code")
    def requestTypeId = column[Int]("request_type")
    def state = column[String]("state")

    def * =
      (id.?, userId, claimCode, requestTypeId, state).shaped <> (Request.tupled, Request.unapply)

    def requestType: ForeignKeyQuery[RequestTypes, RequestType] = foreignKey("", requestTypeId, requestTypes)(_.id)

    def fieldValues =
      requestContents.filter(_.entityId === id).flatMap(c => c.field.map(f => (f, c.value)))
  }

  private[models] val requests = TableQuery[Requests]

  private[models] class Logs[A, B <: Table[A] with Identified](name: String, idName: String, idTable: TableQuery[B])(tag: Tag) extends Table[Log](tag, name) {
    def entityId: Rep[Int] = column[Int](idName)

    def entity: ForeignKeyQuery[B, A] = foreignKey("", entityId, idTable)(_.id)

    def fromState = column[String]("from_state")
    def toState = column[String]("to_state")
    def reason = column[String]("reason")
    def changedBy = column[Int]("changed_by")
    def timestamp = column[Timestamp]("timestamp")

    def * =
      (entityId, fromState, toState, reason, timestamp, changedBy).shaped <> (Log.tupled, Log.unapply)
  }

  private[models] val requestLogs = TableQuery[Logs[Request, Requests]](tag => new Logs[Request, Requests]("request_logs", "request_id", requests)(tag))


  private[models] class AccredTypes(tag: Tag) extends Table[AccredType](tag, "accred_types") with Identified  {
    def id = column[Int]("id")
    def editionId = column[Int]("edition")
    def internalName = column[String]("internal_name")
    def name = column[String]("name")
    def selfService = column[Boolean]("is_self_service")
    def printable = column[Boolean]("is_printable")

    def edition = foreignKey("", editionId, editions)(_.id)

    def * =
      (id.?, editionId, internalName, name, selfService, printable).shaped <> (AccredType.tupled, AccredType.unapply)

    def fields = accredTypeFields.filter(_.accredTypeId == id).flatMap(_.field)
  }

  private[models] val accredTypes = TableQuery[AccredTypes]

  private[models] class Accreds(tag: Tag) extends Table[Accred](tag, "accreds") with Identified  {
    def id = column[Int]("id")
    def requestId = column[Int]("request_id")
    def accredTypeId = column[Int]("accred_type")
    def state = column[String]("state")

    def * =
      (id.?, requestId, accredTypeId, state).shaped <> (Accred.tupled, Accred.unapply)

    def accredType = foreignKey("", accredTypeId, accredTypes)(_.id)
    def request = foreignKey("", requestId, requests)(_.id)

    def fieldValues =
      accredContents.filter(_.entityId === id).flatMap(c => c.field.map(f => (f, c.value)))
  }

  private[models] val accreds = TableQuery[Accreds]

  private[models] val accredLogs = TableQuery[Logs[Accred, Accreds]](tag => new Logs[Accred, Accreds]("accred_logs", "accred_id", accreds)(tag))

  private[models] class RequestTypeFields(tag: Tag) extends Table[(Int, Int, Boolean)](tag, "request_type_fields") {
    def fieldId = column[Int]("field_id")
    def requestTypeId = column[Int]("request_type_id")
    def hidden = column[Boolean]("hidden")

    def * = (fieldId, requestTypeId, hidden)

    def field = foreignKey("", fieldId, fields)(_.id)
    def requestType = foreignKey("", requestTypeId, requests)(_.id)
  }

  private[models] val requestTypeFields = TableQuery[RequestTypeFields]

  private[models] class AccredTypeFields(tag: Tag) extends Table[(Int, Int, Boolean)](tag, "accred_type_fields") {
    def fieldId = column[Int]("field_id")
    def accredTypeId = column[Int]("accred_type_id")
    def userEditable = column[Boolean]("user_editable")

    def * = (fieldId, accredTypeId, userEditable)

    def field = foreignKey("", fieldId, fields)(_.id)
    def accredType = foreignKey("", accredTypeId, accreds)(_.id)
  }

  private[models] val accredTypeFields = TableQuery[AccredTypeFields]


  private[models] class Contents[A, B <: Table[A] with Identified](name: String, idName: String, idTable: TableQuery[B], tag: Tag) extends Table[(Int, String)](tag, name) {
    def entityId: Rep[Int] = column[Int](idName)

    def entity: ForeignKeyQuery[B, A] = foreignKey("", entityId, idTable)(_.id)
    def field = foreignKey("", fieldId, fields)(_.id)

    def fieldId = column[Int]("field_id")
    def value = column[String]("value")

    def * = (fieldId, value)
  }

  private[models] val requestContents = TableQuery[Contents[Request, Requests]](tag => new Contents[Request, Requests]("request_contents", "request_id", requests, tag))
  private[models] val accredContents = TableQuery[Contents[Accred, Accreds]](tag => new Contents[Accred, Accreds]("accred_contents", "accred_id", accreds, tag))
}
