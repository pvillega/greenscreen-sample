// *****************************************************************************
// Projects
// *****************************************************************************

val wartRemoverExclusions = List(Wart.NonUnitStatements)

lazy val greenscreen =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, GitVersioning, SbtTwirl, sbtdocker.DockerPlugin, AshScriptPlugin)
    .settings(settings)
    .settings(
      wartremoverErrors ++= Warts.unsafe.filterNot(wartRemoverExclusions.contains),
      libraryDependencies ++= Seq(
        library.alpn,
        library.cats,
        library.circe("-core"),
        library.circe("-generic"),
        library.circe("-literal"),
        library.circe("-parser"),
        library.circe("-refined"),
        library.doobie("-core-cats"),
        library.doobie("-postgres-cats"),
        library.dwMetrics("-core"),
        library.dwMetrics("-json"),
        library.fluentLogger,
        library.flywayDb,
        library.http4s("-blaze-server"),
        library.http4s("-circe"),
        library.http4s("-dsl"),
        library.http4s("-server-metrics"),
        library.http4s("-twirl"),
        library.logback,
        library.nscala,
        library.pureConfig,
        library.refined(""),
        library.refined("-pureconfig"),
        library.doobie("-scalatest-cats") % Test,
        library.scalaCheck                % Test,
        library.scalaTest                 % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val alpn       = "8.1.11.v20170118"
      val cats       = "0.9.0"
      val circe      = "0.7.1"
      val doobie     = "0.4.1"
      val dwMetrics  = "3.2.2"
      val fluentLogger = "0.7.0"
      val flywayDb   = "4.1.2"
      val http4s     = "0.15.8"
      val logback    = "1.2.3"
      val nscala = "2.16.0"
      val pureConfig = "0.7.0"
      val refined    = "0.8.0"
      val scalaCheck = "1.13.5"
      val scalaTest  = "3.0.1"
    }
    // Enables Http/2 in Java 8 - http://eclipse.org/jetty/documentation/current/alpn-chapter.html
    val alpn: ModuleID = "org.mortbay.jetty.alpn" % "alpn-boot" % Version.alpn
    // Provides abstractions for functional programming - http://typelevel.org/cats/
    val cats: ModuleID = "org.typelevel" %% "cats" % Version.cats
    // JSON library for scala - https://circe.github.io/circe/
    def circe(stuff: String): ModuleID = "io.circe" %% s"circe$stuff" % Version.circe
    // JDBC layer for scala - https://github.com/tpolecat/doobie
    def doobie(stuff: String): ModuleID = "org.tpolecat" %% s"doobie$stuff" % Version.doobie
    // Adds Dropwizard metrics to the application - https://github.com/dropwizard/metrics
    def dwMetrics(stuff: String): ModuleID = "io.dropwizard.metrics" % s"metrics$stuff" % Version.dwMetrics
    // Library for Fluentd logging, required for GCP - https://github.com/fluent/fluentd
    val fluentLogger: ModuleID = "org.fluentd" %% "fluent-logger-scala" % Version.fluentLogger
    // Database migrations tool - https://flywaydb.org/getstarted/why
    val flywayDb: ModuleID = "org.flywaydb" % "flyway-core" % Version.flywayDb
    // web server library - http://http4s.org/
    def http4s(stuff: String): ModuleID = "org.http4s" %% s"http4s$stuff" % Version.http4s
    // Logging library - https://logback.qos.ch/
    val logback: ModuleID = "ch.qos.logback" % "logback-classic" % Version.logback
    // Joda Time for Scala - https://github.com/nscala-time/nscala-time
    val nscala: ModuleID = "com.github.nscala-time" %% "nscala-time" % Version.nscala
    // A boilerplate-free Scala library for loading configuration files - https://github.com/melrief/pureconfig
    val pureConfig: ModuleID = "com.github.pureconfig" %% "pureconfig" % Version.pureConfig
    // Library for type-level predicates which constrain the set of values described by the refined type - https://github.com/fthomas/refined
    def refined(stuff: String): ModuleID = "eu.timepit" %% s"refined$stuff" % Version.refined
    // property based testing - https://www.scalacheck.org/documentation.html
    val scalaCheck: ModuleID = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
    // testing library - http://www.scalatest.org/user_guide
    val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Version.scalaTest
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  gitSettings ++
  headerSettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.12.1",
    organization := "com.aracon",
    licenses += ("Apache 2.0",
                 url("http://www.apache.org/licenses/LICENSE-2.0")),
    mappings.in(Compile, packageBin) += baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding",
      "UTF-8",
      "-opt:l:method",
      "-Xfatal-warnings",
      "-Xlint:_",
      "-Ywarn-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-inaccessible",
      "-Ywarn-infer-any",
      "-Ywarn-nullary-override",
      "-Ywarn-nullary-unit",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused"
      //"-Ywarn-unused-import"  -- can't be used with Twirl templates if we fail-on-error, left in case we replace Twirl later on
    ),
    // Adds ALPN agent to the boot classpath for HTTP/2 support
    javaOptions.in(run) ++= ((managedClasspath in Runtime) map { attList =>
      for {
        file <- attList.map(_.data)
        path = file.getAbsolutePath if path.contains("jetty.alpn")
      } yield {
        s"-Xbootclasspath/p:$path"
      }
    }).value,
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value),
    shellPrompt in ThisBuild := { state =>
      val project = Project.extract(state).currentRef.project
      s"[$project]> "
    },
    coverageMinimum := 80,
    coverageFailOnMinimum := true
)

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

import de.heikoseeberger.sbtheader.license._
lazy val headerSettings =
  Seq(
    headers := Map("scala" -> Apache2_0("2017", "Pere Villega"))
  )

// *****************************************************************************
// Docker file
// *****************************************************************************
dockerfile in docker := {
  val appDir: File = stage.value
  val targetDir = "/app"
  val serverTlsCert : File = file("./selfsigned.jks")

  new Dockerfile {
    from("openjdk:8-jdk-alpine")
    env("JDBC_DATABASE_URL", "jdbc:postgresql://postgres-master:5432/postgres")
    env("JAVA_OPTS", "-Dconfig.resource=application.dev.conf")
    copy(appDir, targetDir)
    copy(serverTlsCert, targetDir)
    workDir(targetDir)
    entryPoint(s"bin/${executableScriptName.value}")
  }
}