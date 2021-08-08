package com.korilin.concentration_detection

fun String.toDoubleDigit() = if (length < 2) "0$this" else this
