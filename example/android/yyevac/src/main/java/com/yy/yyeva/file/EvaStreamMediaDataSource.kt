package com.yy.yyeva.file

import android.annotation.TargetApi
import android.media.MediaDataSource
import android.os.Build

@TargetApi(Build.VERSION_CODES.M)
class EvaStreamMediaDataSource(val bytes: ByteArray) : MediaDataSource() {

    override fun close() {
    }

    override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
        var newSize = size
        synchronized(EvaStreamMediaDataSource::class) {
            val length = bytes.size
            if (position >= length) {
                return -1
            }
            if (position + newSize > length) {
                newSize -= (position + newSize).toInt() - length
            }
            System.arraycopy(bytes, position.toInt(), buffer, offset, newSize)
            return newSize
        }

    }

    override fun getSize(): Long {
        synchronized(EvaStreamMediaDataSource::class) {
            return bytes.size.toLong()
        }
    }
}
