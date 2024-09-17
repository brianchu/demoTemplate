package com.example.demoapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demoapp.Destination.Library
import com.example.demoapp.ui.theme.DemoAppTheme
import com.example.demoapp.view.CharacterDetailScreen
import com.example.demoapp.view.CharactersBottomNav
import com.example.demoapp.view.CollectionScreen
import com.example.demoapp.view.LibraryScreen
import com.example.demoapp.viewmodel.CollectionDbViewModel
import com.example.demoapp.viewmodel.InterstitialAdViewModel
import com.example.demoapp.viewmodel.LibraryApiViewModel
import com.example.demoapp.viewmodel.RewardedAdViewModel
import dagger.hilt.android.AndroidEntryPoint


sealed class Destination(val route: String) {
    data object Library : Destination("library")
    data object Collection : Destination("collection")
    data object CharacterDetail : Destination("character/{characterId}") {
        fun createRoute(characterId: Int?) = "character/$characterId"
    }
}


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val lvm by viewModels<LibraryApiViewModel>()
    private val cvm by viewModels<CollectionDbViewModel>()
    private val interstitialAdViewModel by viewModels<InterstitialAdViewModel>()
    private val rewardedAdViewModel by viewModels<RewardedAdViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        // Call loadRewardedAd() as soon as the activity is created
        interstitialAdViewModel.loadAd()
        rewardedAdViewModel.loadRewardedAd()

        setContent {
            DemoAppTheme {
                Surface(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    CharactersScaffold(navController = navController, lvm, cvm)
                }
            }
        }
    }
}

@Composable
fun CharactersScaffold(
    navController: NavHostController,
    lvm: LibraryApiViewModel,
    cvm: CollectionDbViewModel
) {
    val rewardedAdViewModel : RewardedAdViewModel = hiltViewModel()
    val interstitialAdViewModel : InterstitialAdViewModel = hiltViewModel()

    val ctx = LocalContext.current
    Scaffold(
        bottomBar = { CharactersBottomNav(navController) }
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = Library.route)
        {
            composable(Library.route) {
                LibraryScreen(
                    navController = navController,
                    vm = lvm,
                    paddingValues = contentPadding,
                    interstitialAdViewModel = interstitialAdViewModel,
                    rewardedAdViewModel = rewardedAdViewModel
                )
            }
            composable(Destination.Collection.route) {
                CollectionScreen(cvm = cvm, navController = navController)
            }
            composable(Destination.CharacterDetail.route) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("characterId")?.toIntOrNull()
                if (id == null) {
                    Toast.makeText(ctx, "", Toast.LENGTH_LONG).show()
                } else {
                    lvm.retrieveSingleCharacter(id)
                    CharacterDetailScreen(
                        cvm = cvm,
                        lvm = lvm,
                        paddingValues = contentPadding,
                        navController = navController
                    )
                }
            }
        }
    }
}
