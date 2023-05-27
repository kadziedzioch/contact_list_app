package com.example.contactlistapp.ui.details

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import com.example.contactlistapp.R
import com.example.contactlistapp.model.Contact
import com.example.contactlistapp.navigation.NavigationDestination
import com.example.contactlistapp.ui.ContactTopAppBar
import com.example.contactlistapp.ui.ContactUiState
import com.example.contactlistapp.ui.theme.ContactListAppTheme
import com.example.contactlistapp.ui.toContact

object DetailsDestination : NavigationDestination{
    override val route: String = "Details"
    const val itemIdArg = "itemId"
    val routeWithArgs = "$route/{$itemIdArg}"
}

@Composable
fun ContactDetailsScreen(
    navigateUp: ()-> Unit,
    navigateToEdit: (Int)->Unit
){
    val viewModel : ContactDetailsViewModel = viewModel(factory = ContactDetailsViewModel.factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    Scaffold(
        topBar = {
            ContactTopAppBar(
                title = DetailsDestination.route,
                navigateUp = navigateUp,
                isEdit = false,
                navigateToEdit = {
                    navigateToEdit(uiState.id)
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            ContactInfo(contactUiState = uiState)
            ContactButtons(
                contactUiState = uiState,
                onEmailClicked = {ctx.sendMail(uiState.email)},
                onPhoneClicked = {ctx.phone(uiState.phone)}
            )
            AdditionalContactInfo(
                contactUiState = uiState,
                onEmailClicked = {ctx.sendMail(uiState.email)},
                onPhoneClicked = {ctx.phone(uiState.phone)}
            )
        }
    }

}

@Composable
fun ContactButtons(
    contactUiState: ContactUiState,
    onEmailClicked: () -> Unit,
    onPhoneClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    val ctx = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Card(
            elevation = 3.dp,
            modifier = Modifier
                .weight(1f)
                .padding(5.dp)
        ) {
            IconButton(onClick = onPhoneClicked) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Search Icon"
                )
            }
        }
        Card(
            elevation = 3.dp,
            modifier = Modifier
                .weight(1f)
                .padding(5.dp)
        ) {
            IconButton(onClick = onEmailClicked) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Search Icon"
                )
            }
        }

    }
}

@Composable
fun ContactInfo(
    contactUiState: ContactUiState
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()){
            Spacer(modifier = Modifier.weight(1f))
            if(contactUiState.imgUri.isNotEmpty()){
                AsyncImage(
                    model = contactUiState.imgUri,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null,
                    error = painterResource(id = R.drawable.img_deleted),
                )
            }
            else
            {
                Icon(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape),
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            }
            if(contactUiState.isFavourite){
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )

            }
            else{
                Spacer(modifier = Modifier.weight(1f))
            }

        }

        Text(
            text = contactUiState.name,
            style= MaterialTheme.typography.h4
        )
    }
}

@Composable
fun AdditionalContactInfo(
    contactUiState: ContactUiState,
    modifier: Modifier = Modifier,
    onPhoneClicked: ()->Unit,
    onEmailClicked: ()->Unit
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AdditionalContactInfoItem(
            itemName = "phone",
            itemContent = contactUiState.phone,
            onItemClicked = onPhoneClicked
        )
        AdditionalContactInfoItem(
            itemName = "email",
            itemContent = contactUiState.email,
            onItemClicked = onEmailClicked
        )
    }
}

@Composable
fun AdditionalContactInfoItem(
    itemName: String,
    itemContent: String,
    onItemClicked: ()->Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .clickable(onClick = onItemClicked),
        elevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(5.dp)
        ) {
            Text(
                text = itemName,
                modifier = Modifier.padding(bottom = 3.dp)
            )
            Text(
                text = itemContent,
                style= MaterialTheme.typography.body1
            )
        }
    }
}

fun Context.sendMail(to: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "vnd.android.cursor.item/email" // or "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
        startActivity(intent)

    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
    } catch (t: Throwable) {
        Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
    }
}

fun Context.phone(num: String){
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", num, null))
        startActivity(intent)
    } catch (t: Throwable) {
        Toast.makeText(this, t.message, Toast.LENGTH_SHORT).show()
    }
}


@Preview(showBackground = true)
@Composable
fun PhotoWithNamePreview(){
    ContactListAppTheme() {

    }
}