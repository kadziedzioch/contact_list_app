package com.example.contactlistapp.ui.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.contactlistapp.ContactListApplication
import com.example.contactlistapp.data.ContactRepository
import com.example.contactlistapp.ui.ContactUiState
import com.example.contactlistapp.ui.isValid
import com.example.contactlistapp.ui.toContact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContactListViewModel(
    private val contactRepository: ContactRepository
) : ViewModel(){

    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)
    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _contactUiState: MutableState<ContactUiState> =
        mutableStateOf(ContactUiState())
    val contactUiState: State<ContactUiState> = _contactUiState

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val homeUiState = searchText
        .debounce(500)
        .flatMapLatest {
            contactRepository.getContacts(it)
        }
        .map {
            it.sortedBy {contact -> contact.name.uppercase()}
        }
        .map {
            it.groupBy { contact -> contact.name[0].uppercase()}
        }
        .map{
            result ->
            if (result.isEmpty()) HomeUiState.NothingFound
            else HomeUiState.SearchResult(result)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState.NothingFound
        )

    fun updateSearchText(query: String){
        _searchText.value = query
    }

    fun updateSearchWidgetState(newValue: SearchWidgetState){
        _searchWidgetState.value = newValue
    }

    fun updateUiState(contactUiState: ContactUiState){
        _contactUiState.value = contactUiState.copy(actionEnabled = contactUiState.isValid())
    }

    fun insertContact(){
        if(_contactUiState.value.isValid()){
            viewModelScope.launch {
                contactRepository.insertContact(_contactUiState.value.toContact())
                _contactUiState.value = ContactUiState()
            }
        }
    }


    companion object{
        private const val TIMEOUT_MILLIS = 5_000L
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ContactListApplication
                ContactListViewModel(
                    application.appContainer.contactRepository
                )
            }
        }
    }

}