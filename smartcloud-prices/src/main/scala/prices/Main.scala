package prices

import cats.effect.{IO, IOApp}
import org.http4s.ember.client.EmberClientBuilder
import prices.config.Config

object Main extends IOApp.Simple {
  def run: IO[Unit] = Config.load[IO].flatMap { config =>
    EmberClientBuilder.default[IO].build.use { client =>
      Server.serve(config, client).compile.drain
    }
  }
}
