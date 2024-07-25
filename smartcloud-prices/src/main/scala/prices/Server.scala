package prices

import cats.effect._
import com.comcast.ip4s._
import fs2.Stream
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger
import prices.config.Config
import prices.routes.InstanceKindRoutes
import prices.services.SmartcloudInstanceKindService

object Server {

  def serve(config: Config, client: Client[IO]): Stream[IO, ExitCode] = {
        val instanceKindService = SmartcloudInstanceKindService.make[IO](
        SmartcloudInstanceKindService.Config(
          config.smartcloud.baseUri,
          config.smartcloud.token
        ),
        client
      )


    val httpApp = (
      InstanceKindRoutes[IO](instanceKindService).routes
    ).orNotFound

    Stream
      .eval(
        EmberServerBuilder
          .default[IO]
          .withHost(Host.fromString(config.app.host).get)
          .withPort(Port.fromInt(config.app.port).get)
          .withHttpApp(Logger.httpApp(true, true)(httpApp))
          .build
          .useForever
      )
  }

}
