package tech.mksoft.testradiofrance.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import tech.mksoft.testradiofrance.presentation.radiostations.RadioStationRoute
import tech.mksoft.testradiofrance.presentation.radiostations.RadioStationRouteNavigation
import tech.mksoft.testradiofrance.presentation.stationprograms.StationProgramsRoute
import tech.mksoft.testradiofrance.presentation.stationprograms.StationProgramsRouteNavigation

@Composable
fun SetupAppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = RadioStationRouteNavigation.routeName,
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
            StationProgramsRoute(arguments = it.arguments)
        }
    }
}

interface NavigationRoute {
    val routeName: String
    val navArguments: List<NamedNavArgument>
}

fun String.withNavArgument(argumentName: String, argumentValue: String) = this.replace("{$argumentName}", argumentValue)