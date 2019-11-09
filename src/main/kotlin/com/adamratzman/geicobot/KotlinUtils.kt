package com.adamratzman.geicobot

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toDate() = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
    .format(DateTimeFormatter.ISO_DATE_TIME)