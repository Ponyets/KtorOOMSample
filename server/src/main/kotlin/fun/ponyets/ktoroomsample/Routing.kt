package `fun`.ponyets.ktoroomsample

import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.*
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.readAvailable

fun Application.configureRouting() {
    routing {
        post("/upload") {
            val multipart = call.receiveMultipart(Long.MAX_VALUE)
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val buffer = ByteArray(4 * 1024)
                    val channel = part.provider()
                    var totalBytes = 0L
                    while (!channel.isClosedForRead) {
                        totalBytes += channel.readAvailable(buffer)
                    }
                    // Save the fileBytes to a file or process them as needed
                    println("Received file: ${part.originalFileName} with size: $totalBytes bytes")
                }
                part.dispose()
            }
            call.respondText("File uploaded successfully")
        }
    }
}
