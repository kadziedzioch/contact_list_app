package com.example.contactlistapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.contactlistapp.ui.ContactUiState

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val name: String,
    val imgUri: String,
    val email: String,
    val phone: String,
    val isFavourite: Boolean
)

fun Contact.toContactUiState() : ContactUiState = ContactUiState(
    id = id,
    name = name,
    imgUri = imgUri,
    email = email,
    phone = phone,
    isFavourite = isFavourite
)