package tech.mksoft.testradiofrance.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NamedNavArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import tech.mksoft.testradiofrance.MainViewModel
import tech.mksoft.testradiofrance.design.components.LocalLivePlayerPlaying
import tech.mksoft.testradiofrance.design.components.PLAYER_HEIGHT
import tech.mksoft.testradiofrance.presentation.radiostations.RadioStationRoute
import tech.mksoft.testradiofrance.presentation.radiostations.RadioStationRouteNavigation
import tech.mksoft.testradiofrance.presentation.stationprograms.StationProgramsRoute
import tech.mksoft.testradiofrance.presentation.stationprograms.StationProgramsRouteNavigation

@Composable
fun SetupAppNavigation() {
    val navController = rememberNavController()
    val mainViewModel = koinViewModel<MainViewModel>()

    val mainUiState by mainViewModel.uiStateFlow.collectAsState()
    CompositionLocalProvider(LocalLivePlayerPlaying provides if (mainUiState?.showPlayerBanner == true) PLAYER_HEIGHT.dp else 0.dp) {
        NavHost(
            navController = navController,
            startDestination = RadioStationRouteNavigation.routeName,
            route = "root"
        ) {
            composable(
                route = RadioStationRouteNavigation.routeName
            ) {
                RadioStationRoute(navHostController = navController)
            }

            composable(
                route = StationProgramsRouteNavigation.routeName,
                arguments = StationProgramsRouteNavigation.navArguments,
            ) {
                StationProgramsRoute(
                    navHostController = navController,
                    arguments = it.arguments,
                )
            }
        }
    }
}

interface NavigationRoute {
    val routeName: String
    val navArguments: List<NamedNavArgument>
}

fun String.withNavArgument(argumentName: String, argumentValue: String) = this.replace("{$argumentName}", argumentValue)