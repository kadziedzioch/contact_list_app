package com.example.contactlistapp.data

import com.example.contactlistapp.model.Contact
import kotlinx.coroutines.flow.Flow

interface ContactRepository {
    fun getContacts(query: String) : Flow<List<Contact>>
    suspend fun insertContact(contact: Contact)
    suspend fun deleteContact(contact: Contact)
    suspend fun updateContact(contact: Contact)
    fun getContact(id: Int) : Flow<Contact>
}

class DefaultContactRepository(
    private val contactDao: ContactDao
) : ContactRepository {

    override fun getContacts(query: String): Flow<List<Contact>> {
       return contactDao.getContacts(query)
    }

    override suspend fun insertContact(contact: Contact) {
        contactDao.insertContact(contact)
    }

    override suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact)
    }

    override suspend fun updateContact(contact: Contact) {
        contactDao.updateContact(contact)
    }

    override fun getContact(id: Int): Flow<Contact> {
       return contactDao.getContact(id)
    }

}