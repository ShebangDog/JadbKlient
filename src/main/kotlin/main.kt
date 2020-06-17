import se.vidstige.jadb.JadbConnection
import se.vidstige.jadb.JadbDevice
import se.vidstige.jadb.Stream
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets

fun main() {
    val jadbClient = JadbConnection()

    try {
        val device = jadbClient.devices?.firstOrNull() ?: return
        val ip = device.ip()

        device.enableAdbOverTCP(4455)

        readLine() ?: return

        jadbClient.connectToTcpDevice(InetSocketAddress(ip, 4455))

    } catch (e: Exception) {
        e.message?.also { println(it) }
    }

}

fun JadbDevice.ip(): String {
    val prefix = "inet"
    val readAll = Stream.readAll(executeShell("ip", "addr", "show", "wlan0"), StandardCharsets.UTF_8)

    val matching = Regex(pattern = "$prefix (.*\n)").find(readAll)?.groupValues?.firstOrNull() ?: "not found"
    val cidrFormat = matching.split(" ").drop(1).first()
    val ip = cidrFormat.split("/").first()

    return ip
}