package tech.mksoft.testradiofrance.presentation.stationprograms

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import tech.mksoft.testradiofrance.navigation.NavigationRoute
import tech.mksoft.testradiofrance.navigation.withNavArgument
import tech.mksoft.testradiofrance.presentation.stationprograms.StationProgramsRouteNavigation.getStationNameFromNavArguments

@Composable
fun StationProgramsRoute(arguments: Bundle?) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "show programs for station: ${arguments.getStationNameFromNavArguments()}",
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

object StationProgramsRouteNavigation : NavigationRoute {
    private const val BASE_ROUTE_NAME = "station-programs/"
    private const val ARG_STATION_ID = "station_id"

    override val routeName: String = "$BASE_ROUTE_NAME{$ARG_STATION_ID}"
    override val navArguments: List<NamedNavArgument> = listOf(
        navArgument(ARG_STATION_ID) {
            type = NavType.StringType
        }
    )

    fun Bundle?.getStationNameFromNavArguments(): String? = this?.getString(ARG_STATION_ID)

    fun NavHostController.navigate(stationId: String) {
        navigate(route = routeName.withNavArgument(ARG_STATION_ID, stationId))
    }
}