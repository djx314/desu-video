package utils

import java.nio.charset.Charset
import java.nio.file.{Files, Path, Paths}
import java.text.DecimalFormat

import akka.stream.scaladsl.{FileIO, Source}
import org.slf4j.LoggerFactory
import play.api.libs.ws.WSClient
import play.api.mvc.MultipartFormData.{DataPart, FilePart}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure

class EncoderInfoSend(ws: WSClient, hentaiConfig: HentaiConfig)(implicit defaultExecutionContext: ExecutionContext) {

  //val wsClientConfig: WSClientConfig = WSClientConfig(connectionTimeout = Duration.Inf)
  //val ws1 = NingWSClient(NingWSClientConfig(wsClientConfig = wsClientConfig))

  val logger = LoggerFactory.getLogger(classOf[EncoderInfoSend])
  //implicit val _ec = defaultExecutionContext
  def uploadVideo(fileStr: Path): Future[String] =
    Future {
      val path = Paths.get(hentaiConfig.rootPath)
      //val parentFile = Paths.get(path, fileStr)
      val sourceFile = fileStr
      //val parentUrl = parentFile.toURI.toString
      //val currentUrl = new URI(parentUrl + fileStr)
      //val sourceFile = new File(currentUrl)
      val key = s"里番-${sourceFile.getFileName}"

      val fileExists = Files.exists(sourceFile)

      logger.info(s"""开始发送里番文件
         |文件名:${sourceFile.getFileName}
         |文件是否存在于文件系统:${if (fileExists) "是" else "否"}""".stripMargin)

      val fileSize          = Files.size(sourceFile)
      val decimalFormat     = new DecimalFormat(",###")
      val fileFormattedSize = decimalFormat.format(fileSize)

      //(ws.url(hentaiConfig.encoderUrl): StandaloneWSRequest)
      ws.url(hentaiConfig.encoderUrl)
        .addHttpHeaders()
        .post(
          Source(
            FilePart(
              "video_0",
              new String(sourceFile.getFileName.toString.getBytes("utf-8"), "gbk"),
              Option("""multipart/form-data; charset=UTF-8"""),
              FileIO.fromPath(sourceFile)
            ) ::
              DataPart("videoKey", key) ::
              DataPart("videoInfo", sourceFile.toUri.toString.drop(path.toUri.toString.size)) ::
              DataPart("returnPath", hentaiConfig.selfUrl) ::
              //DataPart("encodeType", "FormatFactoryEncoder") ::
              DataPart("encodeType", "ffmpegEncoder") ::
              DataPart("videoLength", 1.toString) ::
              Nil
          )
        )
        .map { wsResult =>
          val resultModel = if (wsResult.status == 200) {
            logger.info(s"""上传文件成功
               |文件名:${sourceFile.getFileName}
               |文件路径:${sourceFile}
               |文件大小:${fileFormattedSize}字节
             """.stripMargin)
            wsResult.body
          } else {
            val errorStr =
              s"""上传文件返回异常代码:${wsResult.status}
               |文件路径:${sourceFile}
               |错误内容:\n${wsResult.body}
               |文件大小:${fileFormattedSize}字节""".stripMargin
            val errorStr1 =
              s"""上传文件返回异常代码:${wsResult.status}
               |文件路径:${sourceFile}
               |文件大小:${fileFormattedSize}字节""".stripMargin
            logger.error(errorStr1)
            wsResult.body
          }
          resultModel
        }
        .andThen {
          case Failure(e) =>
            logger.error(
              s"""上传文件失败
                          |文件名:${sourceFile.getFileName}
                          |文件路径:${sourceFile}
                          |文件大小:${fileFormattedSize}字节""".stripMargin,
              e
            )
        }
    }.flatMap(identity)

  @deprecated("已不再使用，目前使用 javascript 实现的前端字幕代替", "0.0.1")
  def uploadVideoWithAss(videoFile: Path, assFile: Path): Future[String] =
    Future {
      val path            = hentaiConfig.rootPath
      val parentFile      = Paths.get(path)
      val parentUrl       = parentFile.toUri.toString
      val videoPath       = videoFile
      val assPath         = assFile
      val videoPathExists = Files.exists(videoPath)
      val assPathExists   = Files.exists(assPath)

      val key = s"里番-${videoPath.getFileName}"

      val decimalFormat          = new DecimalFormat(",###")
      val videoFileSize          = Files.size(videoPath)
      val videoFileFormattedSize = decimalFormat.format(videoFileSize)
      Charset.defaultCharset()

      val assFileSize          = Files.size(assPath)
      val assFileFormattedSize = decimalFormat.format(assFileSize)

      logger.info(s"""开始发送里番文件
         |视频文件名:${videoPath.getFileName}
         |视频文件是否存在于文件系统:${if (videoPathExists) "是" else "否"}
         |字幕文件名:${assPath.getFileName}
         |字幕文件是否存在于文件系统:${if (assPathExists) "是" else "否"}""".stripMargin)

      ws.url(hentaiConfig.encoderUrl)
        .post(
          Source(
            FilePart("video_0", videoPath.getFileName.toString, Option("text/plain"), FileIO.fromPath(videoPath)) ::
              FilePart("video_1", assPath.getFileName.toString, Option("text/plain"), FileIO.fromPath(assPath)) ::
              DataPart("videoKey", key) ::
              DataPart("videoInfo", videoPath.toUri.toString.drop(parentUrl.size)) ::
              DataPart("returnPath", hentaiConfig.selfUrl) ::
              //DataPart("encodeType", "FormatFactoryEncoder") ::
              DataPart("encodeType", "ffmpegEncoderWithAss") ::
              DataPart("videoLength", 2.toString) ::
              Nil
          )
        )
        .map { wsResult =>
          val resultModel = if (wsResult.status == 200) {
            //RequestInfo(true, wsResult.body)
            logger.info(s"""上传文件成功
               |视频文件名:${videoPath.getFileName}
               |字幕文件名:${assPath.getFileName}
               |视频文件路径:${videoPath}
               |字幕文件路径:${assPath}
               |视频文件大小:${videoFileFormattedSize}字节
               |字幕文件大小:${assFileFormattedSize}字节
              """.stripMargin)
            wsResult.body
          } else {
            val errorStr =
              s"""上传文件返回异常代码:${wsResult.status}
               |视频文件名:${videoPath.getFileName}
               |字幕文件名:${assPath.getFileName}
               |视频文件路径:${videoPath}
               |字幕文件路径:${assPath}
               |视频文件大小:${videoFileFormattedSize}字节
               |字幕文件大小:${assFileFormattedSize}字节
               |错误内容:\n${wsResult.body}""".stripMargin
            //RequestInfo(false, errorStr)
            logger.error(errorStr)
            wsResult.body
          }
          resultModel
        }
        .andThen {
          case Failure(e) =>
            logger.error(
              s"""上传文件失败
                        |视频文件名:${videoPath.getFileName}
                        |字幕文件名:${assPath.getFileName}
                        |视频文件路径:${videoPath}
                        |字幕文件路径:${assPath}
                        |视频文件大小:${videoFileFormattedSize}字节
                        |字幕文件大小:${assFileFormattedSize}字节""".stripMargin,
              e
            )
        }
    }.flatMap(identity)

}
