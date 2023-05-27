package com.example.contactlistapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.contactlistapp.ui.details.ContactDetailsScreen
import com.example.contactlistapp.ui.details.DetailsDestination
import com.example.contactlistapp.ui.edit.ContactEditScreen
import com.example.contactlistapp.ui.edit.EditDestination
import com.example.contactlistapp.ui.home.ContactListScreen
import com.example.contactlistapp.ui.home.ContactListViewModel
import com.example.contactlistapp.ui.home.HomeDestination

@Composable
fun ContactNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
){

    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    )
    {
        composable(route= HomeDestination.route){
            val viewModel : ContactListViewModel = viewModel(factory = ContactListViewModel.factory)

            ContactListScreen(
                navigateToDetails = {
                    navController.navigate("${DetailsDestination.route}/${it}")
                },
                viewModel = viewModel
            )
        }
        composable(
            route = DetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(DetailsDestination.itemIdArg) {
                type = NavType.IntType
            })
        )
        {
            ContactDetailsScreen(
                navigateUp = {
                    navController.navigateUp()
                },
                navigateToEdit = {
                    navController.navigate("${EditDestination.route}/${it}")
                }
            )
        }
        composable(
            route = EditDestination.routeWithArgs,
            arguments = listOf(navArgument(EditDestination.itemIdArg) {
                type = NavType.IntType
            })
        ){
            ContactEditScreen(
                navigateUp = {
                    navController.navigateUp()
                },
                navigateBack = {
                    navController.popBackStack()
                },
                navigateToHomeScreen = {
                    navController.navigate(HomeDestination.route) {
                        popUpTo(HomeDestination.route)
                    }
                }
            )
        }

    }
}