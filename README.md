Dash doc [![Build Status](https://travis-ci.org/chriswk/dashdoc.svg?branch=master)](https://travis-ci.org/chriswk/dashdoc)
==================

Config variables
================
* elasticsearch.url
* elasticsearch.port
* elasticsearch.cluster
* artifact.path


'Simple' startup
================
```
play console
```

```scala
import akka.actor._
import actors._
import model._
import ModelHelper._
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._
import play.api.Play.current
import play.api.libs.concurrent.Akka
import java.nio.file.Paths
new play.core.StaticApplication(new java.io.File("."))
val folderIndexer = Akka.system.actorOf(Props[FolderIndexer])
val fileIndexer = Akka.system.actorOf(Props[FileIndexer])
val folderWatcher = Akka.system.actorOf(Props[FolderWatcher])
val downloader = Akka.system.actorOf(Props[Downloader])
val elastic = Akka.system.actorOf(Props[ElasticIndexer])
val x = Paths.get("files")
```

To index .m2 folder

```
folderIndexer ! IndexPath(Paths.get("~/.m2/repository")) //Expand ~ to your home folder
```