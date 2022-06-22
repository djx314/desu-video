# desu-video
## 概要
一个局域网的影音播放前后台，本 repo 为后台部分。
## 特色
使用多种方式实现同一功能。

### 本体
backend/desu-video-akka-http：scala 3 + akka-http(http) + scala Future(effect) + quill(db) + macwire(inject) + zio-json(json)

backend/desu-video-zio：scala 2.13 + (tapir + zio-http)(http) + zio(effect) + slick(db) + ZLayer(inject) + Tethys JSON(json)

backend/desu-video-http4s(计划中)：scala 2.13 + http4s(http) + cats-effect(effect) + doobie(db) + distage(inject 待定) + circe(json)

backend/desu-video-finch(计划中)：scala 2.13 + finch(http) + cats-effect(effect) + anorm(db) + distage(inject 待定) + play-json(json)

### 测试
backend/desu-video-test(test case)：scala 3 + (tapir + sttp + zio-http)(http client) + zio-test(test framework) + zio(effect) + quill(db) + ZLayer(inject) + Jsoniter(json)

desu-video-test 负责 3 个不同实现的 http 接口测试，3 个实现的功能都能跑通同一个 test case

### 其他
flyway, slick codegen, quill codegen