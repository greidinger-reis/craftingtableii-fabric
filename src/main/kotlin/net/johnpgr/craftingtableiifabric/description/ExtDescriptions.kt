@file:Suppress("UNCHECKED_CAST")

package net.johnpgr.craftingtableiifabric.description

import com.google.gson.Gson
import net.minecraft.resource.Resource
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.nio.file.Files

fun Resource.toDescriptionsDict(): HashMap<String, String> {
    val inputStream = this.inputStream
    val json = inputStream.bufferedReader().use { it.readText() }

    val res = Gson().fromJson(
        json, HashMap::class.java
    ) as HashMap<String, String>
    return res
}

fun HashMap<String, String>.writeToFile(file: File) {
    val gson = Gson()

    BufferedWriter(FileWriter(file)).use { it.write(gson.toJson(this)) }
}

fun File.readAsDescriptionsDict(): HashMap<String, String> {
    val gson = Gson()

    return Files.newBufferedReader(toPath()).use { reader ->
        gson.fromJson(reader, HashMap::class.java) as HashMap<String, String>
    }
}
