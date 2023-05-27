package com.example.contactlistapp.ui.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.contactlistapp.ContactListApplication
import com.example.contactlistapp.data.ContactRepository
import com.example.contactlistapp.model.toContactUiState
import com.example.contactlistapp.ui.ContactUiState
import com.example.contactlistapp.ui.details.ContactDetailsViewModel
import com.example.contactlistapp.ui.details.DetailsDestination
import com.example.contactlistapp.ui.isValid
import com.example.contactlistapp.ui.toContact
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContactEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val contactId: Int = checkNotNull(savedStateHandle[EditDestination.itemIdArg])

    var uiState by mutableStateOf(ContactUiState())
        private set

    init {
        viewModelScope.launch {
            uiState = contactRepository.getContact(contactId)
                .filterNotNull()
                .first()
                .toContactUiState()
        }
    }

    fun updateUiState(contactUiState: ContactUiState){
        uiState = contactUiState.copy(actionEnabled = contactUiState.isValid())
    }

    suspend fun updateContact(){
        if(uiState.isValid()){
            contactRepository.updateContact(uiState.toContact())
        }
    }

    suspend fun deleteContact(){
        contactRepository.deleteContact(uiState.toContact())
    }

    companion object{
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ContactListApplication
                ContactEditViewModel(
                    this.createSavedStateHandle(),
                    application.appContainer.contactRepository
                )
            }
        }
    }
}