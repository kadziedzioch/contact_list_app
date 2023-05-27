package com.example.contactlistapp.fake

import android.provider.ContactsContract.Contacts
import com.example.contactlistapp.data.ContactRepository
import com.example.contactlistapp.model.Contact
import com.example.contactlistapp.ui.ContactUiState
import com.example.contactlistapp.ui.home.HomeDestination
import com.example.contactlistapp.ui.home.HomeUiState
import kotlinx.coroutines.flow.*

class FakeContactRepository : ContactRepository {

    private val contact1 = Contact(1, "Karo", "","email@mail.com", "6657493747",true)
    private val contact2 = Contact(2, "Ola", "","inny@mail.com", "38539384",false)
    private val contact3 = Contact(3, "Zuza", "","jeszczeinny@mail.com", "03958735",true)
    val contacts = mutableListOf(contact1, contact2, contact3)

    val contactsListFlow = MutableStateFlow<List<Contact>>(emptyList())

    suspend fun emit(value: List<Contact>) = contactsListFlow.emit(value)

    override fun getContacts(query: String): Flow<List<Contact>> = contactsListFlow.map {
        if(query.isNotEmpty()){
            it.filter {
                it.name.contains(query)
            }
        }
        it
    }

    override suspend fun insertContact(contact: Contact) {
        contacts.add(contact)
        emit(contacts)
    }

    override suspend fun deleteContact(contact: Contact) {
       contacts.remove(contact)
        emit(contacts)
    }

    override suspend fun updateContact(contact: Contact) {
        TODO("Not yet implemented")
    }

    override fun getContact(id: Int): Flow<Contact> {
        for(c in contacts){
            if(c.id == id){
                return flow { emit(c) }
            }
        }
        return flow{ emit(Contact(-1, "", "","", "",false))}
    }
}