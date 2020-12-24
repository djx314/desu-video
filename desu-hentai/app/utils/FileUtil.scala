package utils

import java.nio.file.{Files, Path}
import javax.inject.{Inject, Singleton}

import models.{DateTimeFormat, TempFileInfo}
import scala.jdk.CollectionConverters._

trait FileUtil {

  def tempFileExists(file: Path, tempDirectoryName: String, encodeInfoSuffix: String)(implicit format: DateTimeFormat): (Path, Boolean) = {
    val tempDirectory = file.getParent.resolve(tempDirectoryName)
    Files.createDirectories(tempDirectory)

    val tempInfoFile = tempDirectory.resolve(file.getFileName.toString + "." + encodeInfoSuffix)
    val tempInfo     = TempFileInfo.fromUnknowPath(tempInfoFile)
    val tempFile     = tempDirectory.resolve(file.getFileName.toString + "." + tempInfo.encodeSuffix)
    tempFile -> Files.exists(tempFile)
  }

  def canEncode(fileName: String, suffix: Seq[String]): Boolean = {
    if (fileName.lastIndexOf('.') >= 0) {
      val fileSuffix = fileName.takeRight(fileName.size - fileName.lastIndexOf('.') - 1)
      //println(fileSuffix)
      suffix.exists(_ == fileSuffix)
    } else {
      false
    }
  }

  def comparePath(rootPath: Path, subPath: Path): Option[CompareInfo] = {
    val p  = rootPath.toRealPath().iterator().asScala.to(List)
    val pp = subPath.toRealPath().iterator().asScala.to(List)
    val (info, pp1, success) = p.foldLeft((CompareInfo(List.empty, List.empty), pp, true)) { (ppM, pM) =>
      ppM._2 match {
        case head :: tail =>
          if (head.equals(pM)) {
            println(1111)
            (ppM._1.copy(rootPath = head :: ppM._1.rootPath, ppM._1.subPath), tail, ppM._3)
          } else {
            (ppM._1.copy(rootPath = List.empty, subPath = List.empty), List.empty, false)
          }
        case Nil =>
          (ppM._1.copy(rootPath = ppM._1.rootPath, subPath = pM :: ppM._1.subPath), Nil, false)
      }
    }

    if (success) {
      val i = info.copy(subPath = pp1 ::: info.subPath)
      Option(i.copy(rootPath = i.rootPath, subPath = i.subPath))
    } else {
      Option.empty
    }
  }

}

case class CompareInfo(rootPath: List[Path], subPath: List[Path])

@Singleton
class FileUtilImpl @Inject() () extends FileUtil
