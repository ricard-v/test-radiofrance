package tech.mksoft.testradiofrance.presentation.radiostations

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import tech.mksoft.testradiofrance.navigation.NavigationRoute
import tech.mksoft.testradiofrance.presentation.radiostations.model.RadioStationNavigation
import tech.mksoft.testradiofrance.presentation.radiostations.model.ShowProgramsForStation
import tech.mksoft.testradiofrance.presentation.radiostations.ui.RadioStationsUi
import tech.mksoft.testradiofrance.presentation.stationprograms.StationProgramsRouteNavigation.navigate

@Composable
fun RadioStationRoute(navHostController: NavHostController) {
    val viewModel = koinViewModel<RadioStationsViewModel>()
    val navDirection: RadioStationNavigation? by viewModel.navigationDirection.collectAsStateWithLifecycle(null)

    RadioStationsUi()

    LaunchedEffect(navDirection) {
        when (val nav = navDirection) {
            is ShowProgramsForStation -> navHostController.navigate(stationId = nav.stationId)
            null -> Unit // nothing to do here
        }
    }
}

object RadioStationRouteNavigation : NavigationRoute {
    private const val BASE_ROUTE_NAME = "radio-stations/"

    override val routeName: String = BASE_ROUTE_NAME
    override val navArguments: List<NamedNavArgument> = emptyList()
}