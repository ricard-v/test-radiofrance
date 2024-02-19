package tech.mksoft.testradiofrance.presentation.stationprograms

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import tech.mksoft.testradiofrance.design.components.ErrorState
import tech.mksoft.testradiofrance.navigation.NavigationRoute
import tech.mksoft.testradiofrance.navigation.withNavArgument
import tech.mksoft.testradiofrance.presentation.stationprograms.StationProgramsRouteNavigation.getStationNameFromNavArguments
import tech.mksoft.testradiofrance.presentation.stationprograms.ui.StationProgramsUi

@Composable
fun StationProgramsRoute(
    navHostController: NavHostController,
    arguments: Bundle?,
) {
    val stationId = arguments.getStationNameFromNavArguments()
    if (stationId == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            ErrorState(message = "Missing station name from arguments!")
        }
    } else {
        StationProgramsUi(stationId = stationId) {
            navHostController.popBackStack()
        }
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