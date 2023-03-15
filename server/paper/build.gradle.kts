plugins {
	id("com.github.johnrengelman.shadow")
	kotlin("plugin.serialization")
}

repositories {
	maven("https://repo.mineinabyss.com/releases") // ProtocolBurrito
	maven("https://repo.aikar.co/content/groups/aikar/") // acf-paper
	maven("https://repo.dmulloy2.net/repository/public/") // ProtocolLib
	mavenCentral()
}


dependencies {
	implementation(project(":common", "namedElements"))
	compileOnly(project(":server:mixin"))

	paperDevBundle("${property("minecraft_version")}-R0.1-SNAPSHOT")
	compileOnly("io.papermc.paper:paper-api:${property("minecraft_version")}-R0.1-SNAPSHOT")

	implementation("io.github.microutils:kotlin-logging-jvm:${property("kt_log_version")}")

	implementation("co.aikar:acf-paper:${property("acf_version")}")

	implementation("com.mineinabyss:protocolburrito:${property("protocolburrito_version")}") // Designed to be installed separately but uh.. :cringe:
	compileOnly("com.comphenix.protocol:ProtocolLib:${property("protocollib_version")}")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${property("kotlin_coroutines_version")}")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${property("kx_ser_version")}")

	// Seems to be the only modern-ish (farmboy0) luaj fork that I could find
	// plus, figura is a minecraft mod, so that's... something
	implementation("com.github.Moonlight-MC.LuaJ:luaj-core:${property("luaj_version")}figura")
	implementation("com.github.Moonlight-MC.LuaJ:luaj-jse:${property("luaj_version")}-figura")
}

val version = property("mod_version")!!
val mc = property("minecraft_version")!!.toString()

tasks {
	build {
		dependsOn(reobfJar)
	}

	shadowJar {
		relocate("co.aikar.commands", "io.github.stellaricamc.stellarica.libraries.co.aikar.commands")
		relocate("co.aikar.locales", "io.github.stellaricamc.stellarica.libraries.co.aikar.locales")
	}

	processResources {
		inputs.property("version", version)
		filesMatching("plugin.yml") {
			expand(mutableMapOf("version" to version))
		}
	}
}