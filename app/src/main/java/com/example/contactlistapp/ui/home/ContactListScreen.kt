package com.example.contactlistapp.ui.home

import android.content.Intent
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.contactlistapp.R
import com.example.contactlistapp.model.Contact
import com.example.contactlistapp.navigation.NavigationDestination
import com.example.contactlistapp.ui.ContactUiState
import com.example.contactlistapp.ui.details.sendMail
import kotlinx.coroutines.launch

enum class SearchWidgetState{
    OPENED,
    CLOSED
}

object HomeDestination : NavigationDestination{
    override val route: String = "Home"
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContactListScreen(
    navigateToDetails: (Int) -> Unit,
    viewModel: ContactListViewModel
){
    val homeUiState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val searchWidgetState by viewModel.searchWidgetState
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val contactUiState by viewModel.contactUiState

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true,
    )

    BackHandler(modalSheetState.isVisible) {
        coroutineScope.launch { modalSheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            EditContactLayout(
                contactUiState = contactUiState,
                onValueChange = viewModel::updateUiState,
                onSaveClick = {
                    viewModel.insertContact()
                    coroutineScope.launch {
                        modalSheetState.hide()
                    }
                }
            )
        }
    )
    {
        Scaffold(
            topBar = {
                MainAppBar(
                    searchWidgetState = searchWidgetState,
                    searchTextState = searchText,
                    onTextChange = viewModel::updateSearchText,
                    onCloseClicked = {
                        viewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
                    },
                    onSearchTriggered = {
                        viewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = {  Text(text = "Add contact") },
                    onClick = {
                        coroutineScope.launch {
                            if (modalSheetState.isVisible)
                                modalSheetState.hide()
                            else
                                modalSheetState.animateTo(ModalBottomSheetValue.Expanded)
                        }
                    },
                    icon ={ Icon(Icons.Filled.Add,"")}
                )
            }
        ) {

            when(val state = homeUiState){
                is HomeUiState.NothingFound -> NothingFoundScreen(modifier = Modifier.padding(it))
                is HomeUiState.SearchResult -> {
                    ContactList(
                        modifier = Modifier.padding(it),
                        contacts = state.contacts,
                        onEmailClicked = {
                            email -> context.sendMail(email)
                        },
                        onContactClicked = {
                            id-> navigateToDetails(id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NothingFoundScreen(modifier: Modifier = Modifier){

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ){
        Text(text = "No contacts found")
    }

}



@Composable
fun EditContactLayout(
    contactUiState: ContactUiState,
    onValueChange: (ContactUiState)->Unit,
    onSaveClick: ()->Unit,
    modifier: Modifier = Modifier
){
    val ctx = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()){ uri ->
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val resolver = ctx.contentResolver
            if (uri !=null){
                resolver.takePersistableUriPermission(uri, flags)
                onValueChange(contactUiState.copy(imgUri = uri.toString()))
            }
        }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            Spacer(modifier = Modifier.weight(1f))
            if(contactUiState.imgUri.isNotEmpty()){
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(contactUiState.imgUri)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .clickable {
                            launcher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null,
                    error = painterResource(id = R.drawable.img_deleted)
                )
            }
            else
            {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .clickable {
                            launcher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                )
            }
            IconButton(
                onClick = {onValueChange(contactUiState.copy(isFavourite = !contactUiState.isFavourite))},
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = if (contactUiState.isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    Modifier.size(30.dp)
                )
            }

        }

       
        OutlinedTextField(
            value = contactUiState.name,
            onValueChange = {onValueChange(contactUiState.copy(name = it))},
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Name")}
        )
        OutlinedTextField(
            value = contactUiState.email,
            onValueChange = {onValueChange(contactUiState.copy(email = it))},
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Email")}
        )
        OutlinedTextField(
            value = contactUiState.phone,
            onValueChange = {onValueChange(contactUiState.copy(phone = it))},
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Phone")}
        )
        Button(
            onClick = onSaveClick,
            enabled = contactUiState.actionEnabled
        ) {
            Text(text="Save")
        }

    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactList(
    contacts: Map<String,List<Contact>>,
    modifier: Modifier = Modifier,
    onEmailClicked: (String) -> Unit,
    onContactClicked: (Int) -> Unit
){
    LazyColumn(
        modifier = modifier
    ){
        contacts.forEach { (initial, contactsForInitial) ->
            stickyHeader {
                CharacterHeader(initial)
            }
            items(contactsForInitial){ contact ->
                ContactListItem(
                    imgUri = contact.imgUri,
                    name = contact.name,
                    onEmailClicked = {onEmailClicked(contact.email)},
                    onContactClicked = {onContactClicked(contact.id)}
                )
            }
        }

    }
}
@Composable
fun CharacterHeader(
    initial: String,
    modifier: Modifier = Modifier
){
    Text(
        modifier = modifier.padding(5.dp),
        text = initial.toString(),
        style = MaterialTheme.typography.h5
    )
    Divider(
        thickness = 1.dp,
        color = MaterialTheme.colors.primaryVariant
    )
}

@Composable
fun ContactListItem(
    modifier: Modifier = Modifier,
    imgUri : String,
    name : String,
    onEmailClicked: () -> Unit,
    onContactClicked: ()->Unit
){
    Row(
       modifier = modifier
           .height(60.dp)
           .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
           .clickable(onClick = onContactClicked),
        verticalAlignment = Alignment.CenterVertically
    ){

        if(imgUri.isNotEmpty()){
            AsyncImage(
                model = imgUri,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.FillBounds,
                contentDescription = null,
                error = painterResource(id = R.drawable.img_deleted),
                onError = {
                    Log.d("TAG", it.result.throwable.message.toString())
                }
            )
        }
        else
        {
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically),
                imageVector = Icons.Default.Person,
                contentDescription = null
            )
        }
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onEmailClicked
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null
            )
        }
    }
}

