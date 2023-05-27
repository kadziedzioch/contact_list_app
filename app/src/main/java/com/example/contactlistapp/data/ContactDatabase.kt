package com.example.contactlistapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.contactlistapp.model.Contact

@Database(entities = [Contact::class], version = 2, exportSchema = false)
abstract class ContactDatabase : RoomDatabase(){
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var Instance: ContactDatabase? = null
        fun getDatabase(context: Context): ContactDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ContactDatabase::class.java, "contacts_db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

}