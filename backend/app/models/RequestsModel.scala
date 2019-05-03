package models

import data.Log
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.MySQLProfile

import scala.concurrent.{ExecutionContext, Future}

/**
  * @author Louis Vialar
  */
class RequestsModel @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends HasDatabaseConfigProvider[MySQLProfile] {

  import profile.api._


  /**
    * Get all the requests opened by a user (or all requests, if no userId is provided)
    *
    * @param userId the user to search for
    * @return all the requests opened by the user, or all requests
    */
  def getRequests(userId: Option[Int]): Future[Seq[data.Request]] = {
    if (userId.isEmpty) db.run(requests.result)
    else db.run(requests.filter(r => r.userId.nonEmpty && r.userId === userId.get).result)
  }

  def getRequest(requestId: Int): Future[Option[data.Request]] = {
    db.run(requests.filter(_.id === requestId).result.headOption)
  }

  /**
    * Get a request content given a request id
    *
    * @param requestId the id of the request to get
    */
  def getRequestContent(requestId: Int): Future[Seq[(data.Field, String)]] = {
    db.run(
      requests.filter(_.id === requestId)
        // Get field values
        .flatMap(r => r.fieldValues)
        .result
    )
  }

  /**
    * Get a request logs given a request id
    *
    * @param requestId the id of the request to get
    */
  def getRequestLogs(requestId: Int): Future[Seq[Log]] = {
    db.run(
      requestLogs.filter(_.entityId === requestId).result
    )
  }

}
