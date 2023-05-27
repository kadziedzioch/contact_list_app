package com.example.contactlistapp.ui.edit
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.contactlistapp.navigation.NavigationDestination
import com.example.contactlistapp.ui.ContactTopAppBar
import com.example.contactlistapp.ui.home.EditContactLayout
import kotlinx.coroutines.launch

object EditDestination : NavigationDestination{
    override val route: String = "Edit"
    const val itemIdArg = "itemId"
    val routeWithArgs = "${route}/{${itemIdArg}}"
}

@Composable
fun ContactEditScreen(
    navigateUp: ()->Unit,
    navigateBack: ()->Unit,
    navigateToHomeScreen: () ->Unit
){
    val viewModel: ContactEditViewModel = viewModel(factory = ContactEditViewModel.factory)
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            ContactTopAppBar(
                title = EditDestination.route,
                isEdit = true,
                navigateUp = navigateUp,
                onDeleteClicked = {
                    scope.launch {
                        viewModel.deleteContact()
                        navigateToHomeScreen()
                    }
                }
            )
        }
    ) {
        EditContactLayout(
            contactUiState = viewModel.uiState,
            onValueChange = viewModel::updateUiState,
            onSaveClick = {
                scope.launch{
                    viewModel.updateContact()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(it)
        )
    }

}