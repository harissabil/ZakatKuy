package com.harissabil.zakatkuy.core.recorder

import java.io.File

interface AudioRecorder {

    fun start(outputFile: File)

    fun stop()
}