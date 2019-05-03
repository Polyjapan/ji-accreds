import java.sql.Timestamp

/**
  * @author Louis Vialar
  */
package object data {

  case class Edition(id: Option[Int], endDate: Timestamp, name: String)

  case class Field(id: Option[Int], name: String, label: String, helpText: Option[String], required: Boolean, fieldType: String)

  case class RequestType(id: Option[Int], edition: Int, internalName: String, name: String, description: String, requiredGroup: Option[String], hidden: Boolean)

  case class Request(id: Option[Int], userId: Option[Int], claimCode: Option[String], requestType: Int, state: String)

  case class Log(request: Int, fromState: String, toState: String, reason: String, timestamp: Timestamp, changedBy: Int)

  case class AccredType(id: Option[Int], edition: Int, internalName: String, name: String,
                        selfService: Boolean, printable: Boolean)

  case class Accred(id: Option[Int], requestId: Int, accredType: Int, state: String)

}
