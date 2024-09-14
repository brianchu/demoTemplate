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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.demoapp.Destination.Library
import com.example.demoapp.ads.AdViewModel
import com.example.demoapp.ui.theme.DemoAppTheme
import com.example.demoapp.view.CharacterDetailScreen
import com.example.demoapp.view.CharactersBottomNav
import com.example.demoapp.view.CollectionScreen
import com.example.demoapp.view.LibraryScreen
import com.example.demoapp.viewmodel.LibraryApiViewModel
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
    private lateinit var adViewModel: AdViewModel

    private val lvm by viewModels<LibraryApiViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()


        adViewModel = ViewModelProvider(this)[AdViewModel::class.java]
        adViewModel.loadAd()

        setContent {
            DemoAppTheme {
                Surface(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    CharactersScaffold(navController = navController, lvm, adViewModel)
                }
            }
        }
    }
}

@Composable
fun CharactersScaffold(navController: NavHostController, lvm: LibraryApiViewModel, adViewModel: AdViewModel) {
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
                    adViewModel = adViewModel
                )
            }
            composable(Destination.Collection.route) {
                CollectionScreen()
            }
            composable(Destination.CharacterDetail.route) { navBackStackEntry ->
                val id = navBackStackEntry.arguments?.getString("characterId")?.toIntOrNull()
                if (id == null) {
                    Toast.makeText(ctx, "", Toast.LENGTH_LONG).show()
                } else {
                    lvm.retrieveSingleCharacter(id)
                    CharacterDetailScreen(
                        lvm = lvm,
                        paddingValues = contentPadding,
                        navController = navController
                    )
                }
            }
        }
    }
}
