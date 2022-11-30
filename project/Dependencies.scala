import sbt._

object Dependencies {
    private val calibanVersion  = "2.0.1"
    private val zioVersion      = "2.0.3"
    private val circeVersion    = "0.14.3"

    val caliban             = "com.github.ghostdogpr"   %% "caliban"                % calibanVersion
    val calibanZioHttp      = "com.github.ghostdogpr"   %% "caliban-zio-http"       % calibanVersion
    val calibanFederation   = "com.github.ghostdogpr"   %% "caliban-federation"     % calibanVersion
    val zioQuery            = "dev.zio"                 %% "zio-query"              % "0.3.2"
    val zioTest             = "dev.zio"                 %% "zio-test"               % zioVersion
    val zioTestSbt          = "dev.zio"                 %% "zio-test-sbt"           % zioVersion
    val circeParser         = "io.circe"                %% "circe-parser"           % circeVersion
}
