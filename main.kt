import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.content.Context
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session

class NetworkUtils(context: Context) {
    private var context = context

    fun isWifiConnectedToSSID(ssid: String): Boolean {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        return info != null && info.ssid != null && info.ssid.replace("\"", "") == ssid
    }

    fun executeSSHCommand(host: String, username: String, password: String, command: String): String {
        val jsch = JSch()
        var session: Session? = null
        try {
            session = jsch.getSession(username, host, 22)
            session.setConfig("StrictHostKeyChecking", "no")
            session.setPassword(password)
            session.connect()
            
            val channel = session.openChannel("exec")
            (channel as com.jcraft.jsch.ChannelExec).setCommand(command)
            channel.inputStream = null
            
            val inputStream = channel.inputStream
            channel.connect()
            
            val byteArrayOutputStream = ByteArrayOutputStream()
            var readByte: Int
            while (inputStream.read().also { readByte = it } != -1) {
                byteArrayOutputStream.write(readByte)
            }
            return byteArrayOutputStream.toString()
        } finally {
            session?.disconnect()
        }
    }
}
