import org.http4s._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir._
import zio.interop.catz._
import cats.syntax.all._
import clients.{MainClient, UserClient}
import handler.MyRouter
import pureconfig.generic.auto._
import sttp.capabilities.WebSockets
import sttp.capabilities.zio.ZioStreams
import sttp.client3.SttpBackend
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import zio.{Scope, Task, ULayer, URLayer, ZIO, ZLayer}

object Main extends zio.ZIOAppDefault {

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
    )

  def getEndpoints(router: MyRouter):  List[ZServerEndpoint[Any, Any]] = {
//    List(
//      router.reg(),
//      router.get(),
//      router.delete(),
//      router.log(),
//      router.update()
//    ).map(_.tag("User")) ++
    List(
      router.createRoute,
//      router.updateRoute,
//      router.joinRoute,
//      router.getRouteCards,
//      router.getUserRoutes,
//      router.getRoute,
//      router.getRouteDetailsByDay,
//      router.getRouteMembers,
//      router.addPointToRoute,
//      router.deletePointFromRoute,
//      router.movePointInRoute,
//      router.getUserRouteCards
    )
      .map(_.tag("Route"))
//      List(
//        router.createCard,
//        router.getCard,
//        router.updateCard,
//        router.deleteCard,
//        router.getFavoriteCards,
//        router.addToFavorite
//      ).map(_.tag("Card")) ++
//      List(
//        router.createVote,
//        router.updateVote,
//        router.getVote,
//        router.deleteVote,
//        router.getCardsWithUserVote,
//        router.getCardsForVote
//      )
//        .map(_.tag("Vote"))
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