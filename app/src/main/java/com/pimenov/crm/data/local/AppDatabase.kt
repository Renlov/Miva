package com.pimenov.crm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pimenov.crm.data.local.dao.ChatMessageDao
import com.pimenov.crm.data.local.dao.NoteDao
import com.pimenov.crm.data.local.dao.TaskDao
import com.pimenov.crm.data.local.entity.ChatMessageEntity
import com.pimenov.crm.data.local.entity.NoteEntity
import com.pimenov.crm.data.local.entity.TaskEntity

@Database(
    entities = [NoteEntity::class, ChatMessageEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun taskDao(): TaskDao
}
