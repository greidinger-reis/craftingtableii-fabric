plugins {
	id("maven-publish")
	id("fabric-loom")
	id("org.jetbrains.kotlin.jvm")
	id("com.modrinth.minotaur")
}

operator fun Project.get(property: String): String {
	return property(property) as String
}

fun getChangeLog(): String {
	return "A changelog can be found at https://github.com/johnpgr/$name/commits/"
}

val environment: Map<String, String> = System.getenv()
val releaseName = "${name.split("-").joinToString(" ") { it.capitalize() }} ${(version as String).split("+")[0]}"
val releaseType = (version as String).split("+")[0].split("-").let { if(it.size > 1) if(it[1] == "BETA" || it[1] == "ALPHA") it[1] else "ALPHA" else "RELEASE" }
val releaseFile = "${buildDir}/libs/${base.archivesName.get()}-${version}.jar"

configure<JavaPluginExtension> {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

tasks.compileKotlin{
	kotlinOptions {
		jvmTarget = "17"
	}
}

version = project["mod_version"]
group = project["maven_group"]

base {
	archivesName = project["archives_base_name"]
}

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${project["minecraft_version"]}")
	mappings("net.fabricmc:yarn:${project["yarn_mappings"]}:v2")
	modImplementation("net.fabricmc:fabric-loader:${project["loader_version"]}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${project["fabric_version"]}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${project["fabric_kotlin_version"]}")
	// Uncomment the following line to enable the deprecated Fabric API modules. 
	// These are included in the Fabric API production distribution and allow you to update your mod to the latest modules at a later more convenient time.
	// modImplementation "net.fabricmc.fabric-api:fabric-api-deprecated:${project.fabric_version}"
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand(mutableMapOf("version" to project.version))
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.release.set(17)
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${project.base.archivesName.get()}"}
	}
}

//Modrinth publishing
modrinth {
	environment["MODRINTH_TOKEN"]?.let { token.set(it) }

	projectId.set(project["modrinth_id"])
	changelog.set(getChangeLog())

	versionNumber.set(version as String)
	versionName.set(releaseName)
	versionType.set(releaseType.toLowerCase())

	uploadFile.set(tasks.remapJar.get())

	gameVersions.add(project["minecraft_version"])
	loaders.add("fabric")

	dependencies {
		required.project("fabric-api")
	}
}
tasks.modrinth.configure {
	group = "upload"
}
