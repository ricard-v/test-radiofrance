package tech.mksoft.testradiofrance.core.domain.model

data class RadioStation(
    val id: String,
    val name: String,
    val pitch: String?,
    val description: String?,
    val isFavorite: Boolean = false,
    val position: Int = 0,
)
