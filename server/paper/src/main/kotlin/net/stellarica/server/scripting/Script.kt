package net.stellarica.server.scripting

import org.bukkit.Bukkit
import java.io.File
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.refineConfigurationBeforeCompiling
import kotlin.script.experimental.dependencies.DependsOn
import kotlin.script.experimental.dependencies.Repository
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

@KotlinScript(
	fileExtension = "stellarica.kts",
	compilationConfiguration = StellaricaScriptCompilationConfiguration::class,
	evaluationConfiguration = StellaricaScriptEvaluationConfiguration::class
)
abstract class StellaricaScript {
	fun doStuff() {
		println("did stuff")
	}
}

object StellaricaScriptCompilationConfiguration: ScriptCompilationConfiguration({
	defaultImports()
	jvm {
		this.dependenciesFromClassContext(ScriptingAPI::class, wholeClasspath = true)
	}
})

object StellaricaScriptEvaluationConfiguration : ScriptEvaluationConfiguration(
	{
	}
)

fun run(script: String) {
	val conf = createJvmCompilationConfigurationFromTemplate<StellaricaScript>()
	BasicJvmScriptingHost().eval(script.toScriptSource(), conf, null).reports.forEach { println(it) }
}

fun run(script: File) {
	run(script.readText())
}


fun main() {
	run(File("test.stellarica.kts"))
	println("ran")
}