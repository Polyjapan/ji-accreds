package models

import java.sql.Timestamp

import data.{Edition, RequestType}
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.MySQLProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Louis Vialar
  */
class RequestTypesModel @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[MySQLProfile] {

  import profile.api._

  /**
    * Get all the request types a user can see
    *
    * @param groups the groups of the user
    * @return all the request types that can be seen
    */
  def getAvailableRequestTypes(groups: Set[String]): Future[Seq[(RequestType, Edition)]] = {
    val now: Timestamp = new Timestamp(System.currentTimeMillis())

    db.run(
      requestTypes
        // Only visible ones
        .filterNot(rt => rt.hidden)
        // Only not restricted & visible restricted
        .filter(rt => rt.requiredGroup.isEmpty || rt.requiredGroup.get.inSet(groups))
        // Join with edition
        .flatMap(rt => rt.edition.filter(_.endDate >= now).map(ed => (rt, ed)))
        .result
    )
  }

  /**
    * Get all the request types available
    *
    * @return all the request types registered in the system
    */
  def getAllRequestTypes: Future[Seq[(RequestType, Edition)]] = {
    db.run(
      requestTypes
        .flatMap(rt => rt.edition.map(ed => (rt, ed)))
        .result
    )
  }

  /**
    * Get a requestType
    *
    * @param typeId the id of the request type to get
    */
  def getRequestType(typeId: Int): Future[Option[RequestType]] =
    db.run(requestTypes.filter(_.id === typeId).result.headOption)


  def getRequestTypeForm(typeId: Int): Future[Map[data.Field, Seq[(String, String)]]] = {
    db.run(
      requestTypes.filter(_.id === typeId)
        .flatMap(rt => rt.fields
          .flatMap(field => field.additional
            .map(fieldAdditional => (field, fieldAdditional))
          )).result)

      .map(res =>
        res.groupBy(_._1) // field as key
          .mapValues(_.map(_._2)) // remove field from the second list
      )
  }

}
