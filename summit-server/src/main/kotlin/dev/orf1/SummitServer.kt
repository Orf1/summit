package dev.orf1

import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.LinkedHashSet

data class Connection(val session: DefaultWebSocketSession)

class SummitServer {
    private val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    init {
        embeddedServer(Netty, port = 80, host = "0.0.0.0") {
            routing {
                webSocket("/socket") {
                    val c = Connection(this)
                    connections.add(c)
                    Thread {
                        runBlocking {
                            try {
                                while(true) {
                                    for (frame in incoming){
                                        val text = (frame as Frame.Text).readText()
                                        println("Received: $text")
                                    }
                                }
                            } catch (e: Throwable) {
                                connections.remove(c)
                            }
                        }
                    }.start()
                }
            }
        }.start(wait = true).application
    }
}


fun main() {

}
