package com.example.contactlistapp.ui

import com.example.contactlistapp.R
import com.example.contactlistapp.model.Contact

data class ContactUiState(
    val id: Int = 0,
    val name:String = "",
    val imgUri: String = "",
    val email: String = "",
    val phone: String = "",
    val isFavourite :Boolean = false,
    val actionEnabled: Boolean = false
)

fun ContactUiState.isValid() : Boolean {
    return name.isNotBlank() && email.isNotBlank() && phone.isNotBlank()
}

fun ContactUiState.toContact(): Contact =  Contact(
    id = id,
    name = name,
    imgUri = imgUri,
    email = email,
    phone = phone,
    isFavourite = isFavourite
)