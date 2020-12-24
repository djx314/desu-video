package sttp.tapir.server.http4s.ztapir {
  class ZIOMonadError1[R] extends ZIOMonadError[R]
};

package utils.tapir {

  import sttp.tapir.server.http4s.ztapir.ZIOMonadError1

  class ZIOTapirMonadError[R] extends ZIOMonadError1[R]
}
