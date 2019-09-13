/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package teamcity.slack.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.module.kotlin.KotlinModule
import spark.Spark.*

fun main() {
    val buildRequests = mutableListOf<BuildRequest>()
    val cancelRequests = mutableListOf<CancelRequest>()

    port(getPort())

    get("/build") { _, _ ->
        buildRequests.map { ObjectMapper().writeValueAsString(it) }
    }
    post("/build") { req, res ->
        val buildId = req.queryParams("text")
        buildRequests.add(BuildRequest(buildId, req.queryParams("response_url")))
        res.type("application/json")
        "{\"text\": \"$buildId build request is accepted and will be processed shortly\"}"
    }
    delete("/build") { req, _ ->
        buildRequests.removeIf {
            it.id == ObjectMapper()
                    .registerModule(KotlinModule())
                    .readValue(req.body(), BuildId::class.java).id
        }
    }

    get("/cancel") { _, _ ->
        cancelRequests.map { ObjectMapper().writeValueAsString(it) }
    }
    post("/cancel") { req, res ->
        val buildId = req.queryParams("text")
        cancelRequests.add(CancelRequest(buildId, req.queryParams("response_url")))
        res.type("application/json")
        "{\"text\": \"$buildId cancel request is accepted and will be processed shortly\"}"
    }
    delete("/cancel") { req, _ ->
        cancelRequests.removeIf {
            it.id == ObjectMapper()
                    .registerModule(KotlinModule())
                    .readValue(req.body(), BuildId::class.java).id
        }
    }
}

data class BuildRequest(val id: String, val responseUrl: String)
data class CancelRequest(val id: String, val responseUrl: String)

@JacksonXmlRootElement
data class BuildId(val id: String)

fun getPort() = ProcessBuilder().environment()["PORT"]?.toInt() ?: 4567