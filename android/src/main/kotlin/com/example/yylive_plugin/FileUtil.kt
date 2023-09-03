package com.example.yylive_plugin

import android.content.Context
import com.yy.yyeva.util.ELog
import java.io.*
import java.security.MessageDigest

object FileUtil {

    private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    private val TAG = "FileUtil"

    fun copyAssetsToStorage(context: Context, dir: String, files: Array<String>, loadSuccess:()->Unit) {
        Thread {
            var outputStream: OutputStream
            var inputStream: InputStream
            val buf = ByteArray(4096)
            val names = context.assets.list("")
            if (names == null) {
                ELog.e(TAG,"assets has no file")
                return@Thread
            }
            files.forEach {
                try {
                    if (File("$dir/$it").exists()) {
                        return@forEach
                    }
                    if (!names.contains(it)) {
                        ELog.e(TAG,"assets has not $it")
                        return@Thread
                    }

                    inputStream = context.assets.open(it)
                    outputStream = FileOutputStream("$dir/$it")
                    var length = inputStream.read(buf)
                    while (length > 0) {
                        outputStream.write(buf, 0, length)
                        length = inputStream.read(buf)
                    }
                    outputStream.close()
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                    return@Thread
                }
            }
            loadSuccess.invoke()
        }.start()

    }

    fun getFileMD5(file: File): String? {
        if (!file.exists() || !file.isFile || file.length() <= 0) {
            return null
        }
        var inputStream: InputStream? = null
        try {
            val md = MessageDigest.getInstance("MD5")
            inputStream = FileInputStream(file)
            val dataBytes = ByteArray(4096)
            var iRd: Int
            iRd = inputStream.read(dataBytes)
            while (iRd != -1) {
                md.update(dataBytes, 0, iRd)
                iRd = inputStream.read(dataBytes)
            }
            inputStream.close()
            val digest = md.digest()
            if (digest != null) {
                return bufferToHex(digest)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }

            }
        }
        return null
    }

    private fun bufferToHex(bytes: ByteArray): String {
        return bufferToHex(bytes, 0, bytes.size)
    }

    private fun bufferToHex(bytes: ByteArray, m: Int, n: Int): String {
        val sb = StringBuffer(2 * n)
        val k = m + n
        for (l in m until k) {
            appendHexPair(bytes[l], sb)
        }
        return sb.toString()
    }

    private fun appendHexPair(bt: Byte, sb: StringBuffer) {
        val c0 = hexDigits[bt.toInt() and 0xf0 ushr 4]
        val c1 = hexDigits[bt.toInt() and 0x0f]
        sb.append(c0)
        sb.append(c1)
    }
}