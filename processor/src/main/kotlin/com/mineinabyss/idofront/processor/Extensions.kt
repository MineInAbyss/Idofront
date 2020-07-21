package com.mineinabyss.idofront.processor

import javax.annotation.processing.Messager
import javax.tools.Diagnostic.Kind

internal fun Messager.errormessage(message: () -> String) {
    this.printMessage(Kind.ERROR, message())
}

internal fun Messager.noteMessage(message: () -> String) {
    this.printMessage(Kind.NOTE, message())
}

internal fun Messager.warningMessage(message: () -> String) {
    this.printMessage(Kind.WARNING, message())
}