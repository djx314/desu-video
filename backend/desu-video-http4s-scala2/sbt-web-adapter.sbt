enablePlugins(SbtWeb)

Assets / pipelineStages := Seq(scalaJSPipeline)
Compile / compile       := ((Compile / compile) dependsOn scalaJSPipeline).value
