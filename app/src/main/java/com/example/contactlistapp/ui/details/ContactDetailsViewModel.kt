package com.example.contactlistapp.ui.details

import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.contactlistapp.ContactListApplication
import com.example.contactlistapp.data.ContactRepository
import com.example.contactlistapp.model.toContactUiState
import com.example.contactlistapp.ui.ContactUiState
import kotlinx.coroutines.flow.*

class ContactDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    contactRepository: ContactRepository
) : ViewModel(){
    private val contactId: Int = checkNotNull(savedStateHandle[DetailsDestination.itemIdArg])

    val uiState: StateFlow<ContactUiState> =
        contactRepository.getContact(contactId)
            .filterNotNull()
            .map {
                it.toContactUiState()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ContactUiState()
            )

    companion object{
        private const val TIMEOUT_MILLIS = 5_000L
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ContactListApplication
                ContactDetailsViewModel(
                    this.createSavedStateHandle(),
                    application.appContainer.contactRepository
                )
            }
        }
    }
}