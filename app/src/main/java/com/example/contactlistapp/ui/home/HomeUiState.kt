package com.example.contactlistapp.ui.home

import com.example.contactlistapp.model.Contact

sealed interface HomeUiState {
    data class SearchResult(val contacts: Map<String, List<Contact>> = emptyMap()) : HomeUiState
    object NothingFound : HomeUiState
}