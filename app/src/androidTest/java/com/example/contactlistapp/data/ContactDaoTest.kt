package com.example.contactlistapp.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.contactlistapp.model.Contact
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ContactDaoTest {

    private lateinit var database: ContactDatabase
    private lateinit var contactDao: ContactDao
    private val contact = Contact(1, "Karo", "","email@mail.com", "6657493747",true)

    @Before
    fun createDb(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ContactDatabase::class.java
        ).allowMainThreadQueries().build()

        contactDao = database.contactDao()
    }

    @After
    fun closeDb(){
        database.close()
    }

    private suspend fun addContactToDb(){
        contactDao.insertContact(contact)
    }

    @Test
    fun insertContact() = runBlocking {
        addContactToDb()
        val allContacts = contactDao.getContacts("").first()
        assertEquals(allContacts[0], contact)
    }

    @Test
    fun deleteContact() = runBlocking {
        addContactToDb()
        contactDao.deleteContact(contact)
        val allContacts = contactDao.getContacts("").first()
        assertTrue(allContacts.isEmpty())
    }

    @Test
    fun updateContact() = runBlocking {
        addContactToDb()
        val newContact = Contact(1,"zu","","mail@mail","393746393",false)
        contactDao.updateContact(newContact)
        val allContacts = contactDao.getContacts("").first()
        assertEquals(allContacts[0], newContact)
    }

    @Test
    fun getContact() = runBlocking {
        addContactToDb()
        val contact1 = contactDao.getContact(1).first()
        assertEquals(contact1, contact)
    }

    @Test
    fun getSpecifiedContacts() = runBlocking {
        addContactToDb()
        val contacts = contactDao.getContacts("zuza").first()
        assertTrue(contacts.isEmpty())
    }
}