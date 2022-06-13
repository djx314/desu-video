package zdesu.handle

import zio._
import zdesu.model.{DesuResult, FileNotConfirmException, RootFileNameRequest}
import zdesu.service.{FileFinder, FileService}

object UserHandle {

  def user(i: Unit) = {

    val response = for (files <- FileFinder.rootPathFiles) yield DesuResult.data(true, files)

    def errorHandle(e: Throwable) = {
      val handle = e match {
        case FileNotConfirmException(message) => ZIO.logError(s"文件找不到：$message")
        case e                                => ZIO.logErrorCause(s"查找文件出现错误", Cause.fail(e))
      }
      for (_ <- handle) yield DesuResult.message(false, "查找文件出现错误")
    }
    response.flatMapError(errorHandle)

  }.provideLayer(FileFinder.live)

  def rootPathFile(i: RootFileNameRequest) = {

    val response = FileService.rootPathRequestFileId(i.fileName)

    def errorHandle(e: Throwable) = {
      val handle = e match {
        case FileNotConfirmException(message) => ZIO.logError(s"文件找不到：$message")
        case e                                => ZIO.logErrorCause(s"查找文件出现错误", Cause.fail(e))
      }
      for (_ <- handle) yield DesuResult.message(false, "查找文件出现错误")
    }
    response.flatMapError(errorHandle)

  }.provideLayer(FileService.live)

}
