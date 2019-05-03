
import pdi.jwt.JwtSession._
import play.api.Configuration
import play.api.mvc._
import data.UserSession

import scala.concurrent.{ExecutionContext, Future}

/**
  * Utility class to remove some boilerplate regarding authentication on the endpoints
  * @author Louis Vialar
  */
object AuthPostfix {

  /**
    * Result of an authorization request
    * @param authorized true if the user is allowed to access the resource
    * @param returnedError a result to return in case the user is not authorized. Ignored otherwise
    */
  case class AuthorizationResult(authorized: Boolean, returnedError: Option[Result])

  /**
    * Defines a handler of authentication.
    * It takes a potential user and returns a boolean: true if the user is authorized, false if not. It can also
    * provide a result that will be returned in case the user is not authorized.
    */
  abstract class AuthorizationHandler extends Function[Option[UserSession], AuthorizationResult] {
    def andAlso(other: AuthorizationHandler): AuthorizationHandler = (user: Option[UserSession]) => {
      val self = this(user)
      if (self.authorized) other(user) // This handler authorized the user, check that the next authorizes it too
      else self // This handler refused the user, return its result
    }
  }

  object AuthorizationHandler {
    val ensuringAuthentication: AuthorizationHandler = (user: Option[UserSession]) =>
      AuthorizationResult(user.isDefined, Some(Results.Unauthorized))

    def ensuringGroup(group: String): AuthorizationHandler = ensuringAuthentication andAlso (user => AuthorizationResult(
      user.get.groups(group), Some(Results.Forbidden)))

    val ensuringApp: AuthorizationHandler = ensuringAuthentication andAlso (user => AuthorizationResult(
      user.get.isApp, Some(Results.Forbidden)))

    val ensuringUser: AuthorizationHandler = ensuringAuthentication andAlso (user => AuthorizationResult(
      !user.get.isApp, Some(Results.Forbidden)))
  }


  case class AuthenticationAction[T](action: Action[T], handler: AuthorizationHandler)(implicit conf: Configuration) extends Action[T] {
    override def apply(request: Request[T]): Future[Result] = {
      val user = request.optUser
      val result = handler(user)

      if (result.authorized) action(request)  // call the parent action, knowing we are authenticated
      else Future.successful(result.returnedError.getOrElse(Results.Unauthorized)) // return an error
    }

    override def parser: BodyParser[T] = action.parser

    override def executionContext: ExecutionContext = action.executionContext
  }

  implicit class AuthenticationPostfix[T](action: Action[T]) {
    def requiresAuthentication(implicit conf: Configuration): Action[T] = AuthenticationAction(action, AuthorizationHandler.ensuringAuthentication)

    def requiresGroup(group: String)(implicit conf: Configuration): Action[T] = AuthenticationAction(action, AuthorizationHandler.ensuringGroup(group))
    def requiresApp(implicit conf: Configuration): Action[T] = AuthenticationAction(action, AuthorizationHandler.ensuringApp)
    def requiresUser(implicit conf: Configuration): Action[T] = AuthenticationAction(action, AuthorizationHandler.ensuringUser)

    def requiresAuthorizationCheck(authorization: AuthorizationHandler)(implicit conf: Configuration): Action[T] = AuthenticationAction(action, authorization)
  }

  implicit class UserRequestHeader(request: RequestHeader)(implicit conf: Configuration) {
    def optUser: Option[UserSession] = request.jwtSession.getAs[UserSession]("user")

    def user: UserSession = optUser.get
  }

}