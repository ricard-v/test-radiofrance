package tech.mksoft.testradiofrance.core.common.extensions

fun String?.nullIfEmpty(): String? = if (isNullOrEmpty() || isBlank()) null else this