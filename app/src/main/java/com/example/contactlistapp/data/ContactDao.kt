package com.example.contactlistapp.data

import androidx.room.*
import com.example.contactlistapp.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contacts WHERE name LIKE '%' || :query || '%'")
    fun getContacts(query: String) : Flow<List<Contact>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContact(id: Int) : Flow<Contact>

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Insert
    suspend fun insertContact(contact: Contact)

    @Update
    suspend fun updateContact(contact: Contact)

}