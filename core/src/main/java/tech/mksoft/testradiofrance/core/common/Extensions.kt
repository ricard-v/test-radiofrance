package tech.mksoft.testradiofrance.core.common

fun <R> R?.asNotEmpty(): R? where R : Collection<*> = this?.ifEmpty { null }

fun <T> T.wrapAsSuccess(): DataRequestResult<T> = DataRequestResult.Success(data = this)