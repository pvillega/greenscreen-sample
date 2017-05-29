// *****************************************************************************
// Projects
// *****************************************************************************

val wartRemoverExclusions = List(Wart.NonUnitStatements)

lazy val greenscreen =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, GitVersioning, GitBranchPrompt, BuildInfoPlugin, SbtTwirl, JavaAppPackaging)
    .settings(settings)
    .settings(
      wartremoverErrors ++= Warts.unsafe.filterNot(wartRemoverExclusions.contains),
      buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
      buildInfoPackage := "com.aracon",
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
        library.flywayDb,
        library.http4s("-blaze-server"),
        library.http4s("-circe"),
        library.http4s("-dsl"),
        library.http4s("-server-metrics"),
        library.http4s("-twirl"),
        library.librato,
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
      val circe      = "0.8.0"
      val doobie     = "0.4.1"
      val dwMetrics  = "3.2.2"
      val flywayDb   = "4.2.0"
      val http4s     = "0.15.13"
      val librato    = "5.0.5"
      val logback    = "1.2.3"
      val nscala     = "2.16.0"
      val pureConfig = "0.7.0"
      val refined    = "0.8.1"
      val scalaCheck = "1.13.5"
      val scalaTest  = "3.0.3"
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
    // Database migrations tool - https://flywaydb.org/getstarted/why
    val flywayDb: ModuleID = "org.flywaydb" % "flyway-core" % Version.flywayDb
    // web server library - http://http4s.org/
    def http4s(stuff: String): ModuleID = "org.http4s" %% s"http4s$stuff" % Version.http4s
    // metrics library - https://github.com/librato/metrics-librato
    val librato: ModuleID = "com.librato.metrics" % "metrics-librato" % Version.librato
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
    scalaVersion := "2.12.2",
    organization := "com.aracon",
    licenses += ("Apache 2.0",
                 url("http://www.apache.org/licenses/LICENSE-2.0")),
    mappings.in(Compile, packageBin) += baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
    // see https://tpolecat.github.io/2017/04/25/scalac-flags.html
    scalacOptions ++= Seq(
      "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
      "-encoding", "utf-8",                // Specify character encoding used by source files.
      "-explaintypes",                     // Explain type errors in more detail.
      "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
      "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
      "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
      "-language:higherKinds",             // Allow higher-kinded types
      "-language:implicitConversions",     // Allow definition of implicit functions called views
      "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
      "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
      "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
      "-Xfuture",                          // Turn on future language features.
      "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
      "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
      "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
      "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
      "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
      "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
      "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
      "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
      "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
      "-Xlint:option-implicit",            // Option.apply used implicit view.
      "-Xlint:package-object-classes",     // Class or object defined in package object.
      "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
      "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
      "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
      "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
      "-Xlint:unsound-match",              // Pattern match may not be typesafe.
      "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
      "-Ypartial-unification",             // Enable partial unification in type constructor inference
      "-Ywarn-dead-code",                  // Warn when dead code is identified.
      "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
      "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
      "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
      "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
      "-Ywarn-numeric-widen",              // Warn when numerics are widened.
      "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
      "-Ywarn-unused:locals",              // Warn if a local definition is unused.
      "-Ywarn-unused:params",              // Warn if a value parameter is unused.
      "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
      "-Ywarn-unused:privates",            // Warn if a private member is unused.
      "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
      // we need to exclude some flags as they can't be used with Twirl templates if we fail-on-error, fix if we replace Twirl later on
      //"-Ywarn-unused:imports"            // Warn if an import selector is not referenced.
    ),
    scalacOptions in (Compile, console) ~= (_.filterNot(Set(
      "-Ywarn-unused:imports",
      "-Xfatal-warnings"
    ))),
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
    coverageFailOnMinimum := true,
    dependencyCheckFormat := "All"
)

// See http://blog.byjean.eu/2015/07/10/painless-release-with-sbt.html
val VersionRegex = "v([0-9]+.[0-9]+.[0-9]+)-?(.*)?".r
lazy val gitSettings =
  Seq(
    git.useGitDescribe := true,
    git.baseVersion := "0.0.0"
)

import de.heikoseeberger.sbtheader.license._
lazy val headerSettings =
  Seq(
    headers := Map("scala" -> Apache2_0("2017", "Pere Villega"))
  )

// *****************************************************************************
// Docker file - commented out as we don't use GKE by now. Plugins for sbt-docker have also been removed from project
// *****************************************************************************
//dockerfile in docker := {
//  val appDir: File = stage.value
//  val targetDir = "/app"
//  val serverTlsCert : File = file("./selfsigned.jks")
//
//  new Dockerfile {
//    from("openjdk:8-jdk-alpine")
//    env("JDBC_DATABASE_URL", "jdbc:postgresql://postgres-master:5432/postgres")
//    env("JAVA_OPTS", "-Dconfig.resource=application.dev.conf")
//    copy(appDir, targetDir)
//    copy(serverTlsCert, targetDir)
//    workDir(targetDir)
//    entryPoint(s"bin/${executableScriptName.value}")
//  }
//}