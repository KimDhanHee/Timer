package damin.tothemoon.timer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import damin.tothemoon.timer.model.TimerInfo
import damin.tothemoon.timer.ui.componenets.TimerDestination
import damin.tothemoon.timer.ui.componenets.TimerEditorScreen
import damin.tothemoon.timer.ui.componenets.TimerInfoListScreen
import damin.tothemoon.timer.ui.componenets.TitleInputScreen
import damin.tothemoon.timer.ui.theme.TimerTheme
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ComposeActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      TimerApp()
    }
  }

  @Composable
  private fun TimerApp() {
    TimerTheme {
      val navController = rememberNavController()

      NavHost(navController, startDestination = TimerDestination.TimerInfoList.route) {
        homeGraph(navController)
        editorGraph(navController)
      }
    }
  }

  private fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    composable(route = TimerDestination.TimerInfoList.route) {
      TimerInfoListScreen(
        onClickAdd = {
          val newTimerInfo = TimerInfo()
          navController.navigateToSingleTop("${TimerDestination.TimerEditor.Home.route}/$newTimerInfo")
        },
        onClickTimerInfo = { timerInfo ->
          navController.navigateToSingleTop("${TimerDestination.TimerEditor.Home.route}/$timerInfo")
        }
      )
    }
  }

  private fun NavGraphBuilder.editorGraph(navController: NavHostController) {
    navigation(
      startDestination = TimerDestination.TimerEditor.Home.routeWithArgs,
      route = TimerDestination.TimerEditor.route
    ) {
      composable(
        route = TimerDestination.TimerEditor.Home.routeWithArgs,
        arguments = TimerDestination.TimerEditor.Home.arguments
      ) { navBackStackEntry ->
        val timerInfoJson =
          navBackStackEntry.arguments?.getString(TimerDestination.TimerEditor.Home.timerInfoArg)
            ?: return@composable
        var timerInfo: TimerInfo = Json.decodeFromString(timerInfoJson)

        val title by navBackStackEntry.savedStateHandle.getStateFlow(
          TimerDestination.TimerEditor.TitleInput.KEY_TITLE,
          ""
        ).collectAsState()
        if (title.isNotBlank()) {
          timerInfo = timerInfo.copy(title = title)
        }

        TimerEditorScreen(timerInfo, navController)
      }

      composable(
        route = TimerDestination.TimerEditor.TitleInput.routeWithArgs,
        arguments = TimerDestination.TimerEditor.TitleInput.arguments
      ) { navBackStackEntry ->
        val title =
          navBackStackEntry.arguments?.getString(TimerDestination.TimerEditor.TitleInput.titleArg)
            ?: return@composable
        val color =
          navBackStackEntry.arguments?.getInt(TimerDestination.TimerEditor.TitleInput.colorArg)
            ?: return@composable

        TitleInputScreen(title, color, navController)
      }
    }
  }
}

fun NavHostController.navigateToSingleTop(route: String) =
  this.navigate(route) {
    launchSingleTop = true
    restoreState = true
  }