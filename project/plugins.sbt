// Added by sbt-fresh - source code formatting - see https://github.com/olafurpg/scalafmt
addSbtPlugin("com.geirsson"      % "sbt-scalafmt" % "0.6.6")
// Added by sbt-fresh - allows running git commands from within sbt - see https://github.com/sbt/sbt-git
addSbtPlugin("com.typesafe.sbt"  % "sbt-git"      % "0.9.2")
// Added by sbt-fresh - adds license header to source files - see https://github.com/sbt/sbt-header
addSbtPlugin("de.heikoseeberger" % "sbt-header"   % "1.8.0")

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25" // Needed by sbt-git
