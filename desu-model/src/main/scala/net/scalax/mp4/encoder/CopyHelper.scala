package org.xarcher.nodeWeb.modules

import java.io.{File, InputStream}
import java.net.{JarURLConnection, URL}
import java.nio.file.{Files, Path, Paths}
import java.util.Date
import java.util.jar.JarFile

import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.collection.JavaConverters._

object CopyHelper {

  val logger = LoggerFactory.getLogger(getClass)

  def copyFromClassPath(paths: List[String], targetRoot: Path)(implicit ec: ExecutionContext): Future[Boolean] = Future {
    Files.createDirectories(targetRoot)
    val classPathStr = paths.mkString("/")
    val sourURLs     = getClass.getClassLoader.getResources(classPathStr).asScala.toStream
    sourURLs.map { sourURL =>
      val date = new Date()
      sourURL match {
        case s: URL if "file" == s.getProtocol =>
          FileUtils.copyDirectory(new File(sourURL.toURI), targetRoot.toFile)
        case s: URL if "jar" == s.getProtocol =>
          val jarFile = s.openConnection().asInstanceOf[JarURLConnection].getJarFile
          copyFilesFromJarFile(jarFile, classPathStr, targetRoot)
      }
      val waste = new Date().getTime - date.getTime
      logger.info(s"由\n${sourURL}\n复制文件到\n${targetRoot.toUri.toURL}\n以初始化 node 环境，复制耗时${waste}ms")
    }.toList
    true
  }

  def doCopyFile(input: InputStream, path: Path): Long = {
    Files.createDirectories(path.getParent)
    Files.copy(input, path)
  }

  def copyFilesFromJarFile(jarFile: JarFile, prefix: String, targetRoot: Path) = {
    val entries      = jarFile.entries()
    val scalaEntries = entries.asScala.toStream
    scalaEntries
      .filter { s =>
        s.getName.startsWith(prefix) && (!s.isDirectory)
      }
      .map { entry =>
        var inputS: InputStream = null
        try {
          inputS = getClass.getClassLoader.getResourceAsStream(entry.getName)
          val entryPath = Paths.get(targetRoot.toFile.getCanonicalPath, entry.getName.drop(prefix.size))
          doCopyFile(inputS, entryPath)
        } finally {
          if (inputS ne null) {
            inputS.close()
          }
        }
      }
      .toList
  }

}
