package com.example.contactlistapp.ui.home

import com.example.contactlistapp.fake.FakeContactRepository
import com.example.contactlistapp.rules.TestDispatcherRule
import com.example.contactlistapp.ui.ContactUiState
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContactListViewModelTest {
    @get:Rule
    val testDispatcher = TestDispatcherRule()

    @Test
    fun initialValueTest() = runTest {
        val fakeRepo =  FakeContactRepository()
        val contactListViewModel = ContactListViewModel(
            contactRepository = fakeRepo
        )
        val job = launch {
            contactListViewModel.homeUiState.collect()
        }
        assertTrue(
            contactListViewModel.homeUiState.value is HomeUiState.NothingFound
        )
        fakeRepo.emit(fakeRepo.contacts)
        advanceTimeBy(500)
        runCurrent()
        assertEquals(
            contactListViewModel.homeUiState.value,
            HomeUiState.SearchResult(fakeRepo.contacts.sortedBy {
                it.name[0].uppercase()
            }.groupBy {
                it.name[0].uppercase()
            })
        )
        job.cancel()
    }

    @Test
    fun queryTest() = runTest {
        val fakeRepo = FakeContactRepository()
        val contactListViewModel = ContactListViewModel(
            contactRepository = fakeRepo
        )
        val job = launch {
            contactListViewModel.homeUiState.collect()
        }
        fakeRepo.emit(fakeRepo.contacts)
        contactListViewModel.updateSearchText("Ka")
        advanceTimeBy(500)
        runCurrent()
        assertTrue(
            contactListViewModel.homeUiState.value is HomeUiState.SearchResult
        )
        job.cancel()
    }

    @Test
    fun insertContactTest() = runTest {
        val fakeRepo =  FakeContactRepository()
        val contactListViewModel = ContactListViewModel(
            contactRepository = fakeRepo
        )
        val job = launch {
            contactListViewModel.homeUiState.collect()
        }
        contactListViewModel.updateUiState(
            ContactUiState(4, "ka","dkd","dkdf","38387",true)
        )
        contactListViewModel.insertContact()
        assertEquals(
            contactListViewModel.contactUiState.value,
            ContactUiState()
        )
        advanceTimeBy(500)
        runCurrent()
        assertEquals(
            contactListViewModel.homeUiState.value,
            HomeUiState.SearchResult(fakeRepo.contacts.sortedBy {
                it.name.uppercase()
            }.groupBy {
                it.name[0].uppercase()
            })
        )
        job.cancel()
    }







}