package desu.video.test.cases.model

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker.*
import desu.video.test.model.*

trait JsonCodec:

  private type DesuAlias1 = DesuResult[Option[String]]
  private type DesuAlias2 = DesuResult[RootPathFiles]
  private type DesuAlias3 = List[String]
  private type DesuAlias4 = RootFileNameRequest
  private type DesuAlias5 = DirId

  given JsonValueCodec[DesuAlias1] = make
  given JsonValueCodec[DesuAlias2] = make
  given JsonValueCodec[DesuAlias3] = make
  given JsonValueCodec[DesuAlias4] = make
  given JsonValueCodec[DesuAlias5] = make

end JsonCodec

object JsonCodec extends JsonCodec
