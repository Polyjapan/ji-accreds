import ch.japanimpact.auth.api.AuthApi
import com.google.inject.{AbstractModule, Provides}
import net.codingwell.scalaguice.ScalaModule
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

/**
  * @author Louis Vialar
  */
class AppModule extends AbstractModule with ScalaModule {

  /** Module configuration + binding */
  override def configure(): Unit = {}

  @Provides
  def provideAuthClient(ws: WSClient)(implicit ec: ExecutionContext, config: Configuration): AuthApi = AuthApi(ws)

}