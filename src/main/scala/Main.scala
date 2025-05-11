import cats.syntax.all._
import ch.qos.logback.classic.{Level, Logger => LogbackLogger}
import clients.{MainClient, RecommendationClient, UserClient}
import handler.MyRouter
import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import org.slf4j.LoggerFactory
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import zio.interop.catz._
import zio.{Scope, Task, ZIO, ZLayer}

object Main extends zio.ZIOAppDefault {
  val asyncLogger = LoggerFactory.getLogger("org.asynchttpclient").asInstanceOf[LogbackLogger]
  asyncLogger.setLevel(Level.WARN)

  val asyncNettyLogger = LoggerFactory.getLogger("org.asynchttpclient.netty").asInstanceOf[LogbackLogger]
  asyncNettyLogger.setLevel(Level.WARN)

  val channelPoolLogger =
    LoggerFactory.getLogger("org.asynchttpclient.netty.channel.DefaultChannelPool").asInstanceOf[LogbackLogger]
  channelPoolLogger.setLevel(Level.WARN)

  type EnvIn = MyRouter

  def swaggerRoutes(routes: List[ZServerEndpoint[Any, Any]]): HttpRoutes[Task] =
    ZHttp4sServerInterpreter()
      .from(
        SwaggerInterpreter()
          .fromServerEndpoints(routes, "Travel Designer Api GateWay", "1.1")
      )
      .toRoutes

  def makeLayer: ZLayer[Any, Throwable, EnvIn] =
    ZLayer.make[EnvIn](
      AsyncHttpClientZioBackend.layer(),
      UserClient.live,
      MainClient.live,
      MyRouter.live,
      RecommendationClient.live
    )

  def getEndpoints(router: MyRouter): List[ZServerEndpoint[Any, Any]] = {
    List(
      router.reg(),
      router.get(),
      router.delete(),
      router.log(),
      router.update()
    ).map(_.tag("User")) ++
      List(
        router.createRoute,
        router.addSettings,
        router.updateRoute,
        router.joinRoute,
        router.getRouteCards,
        router.getUserRoutes,
        router.getRoute,
        router.getRouteDetailsByDay,
        router.addPointToRoute,
        router.deletePointFromRoute,
        router.movePointInRoute,
        router.getUserRouteCards,
        router.copyRoute
      )
        .map(_.tag("Route")) ++
      List(
        router.getRouteMembers,
        router.addRouteMember,
        router.deleteRouteMember
      ).map(_.tag("Members")) ++
      List(
        router.createCard,
        router.copyCard,
        router.getCard,
        router.updateCard,
        router.deleteCard,
        router.getFavoriteCards,
        router.addToFavorite,
        router.deleteFavorite
      ).map(_.tag("Card")) ++
      List(
        router.createVote,
        router.updateVote,
        router.getVote,
        router.deleteVote,
        router.getVotesForCard,
        router.getCardsForVote
      )
        .map(_.tag("Vote")) ++
      List(
        router.getRecPlaces,
        router.getRecRoutes
      ).map(_.tag("Recommendation"))
  }

  def run: ZIO[Environment with Scope, Any, Any] =
    (for {
      mainRouter <- ZIO.service[MyRouter]
      endpoints = getEndpoints(mainRouter)
      routes: HttpRoutes[Task] = ZHttp4sServerInterpreter()
        .from(endpoints)
        .toRoutes
      _ <-
        ZIO.executor.flatMap(executor =>
          BlazeServerBuilder[Task]
            .withExecutionContext(executor.asExecutionContext)
            .bindHttp(8083, "0.0.0.0")
            .withHttpApp(Router("/" -> (routes <+> swaggerRoutes(endpoints))).orNotFound)
            .serve
            .compile
            .drain
        )
    } yield ())
      .provideLayer(makeLayer)
}
