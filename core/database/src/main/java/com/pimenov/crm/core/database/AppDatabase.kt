package com.pimenov.crm.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pimenov.crm.core.database.dao.ChatMessageDao
import com.pimenov.crm.core.database.dao.NoteDao
import com.pimenov.crm.core.database.dao.TaskDao
import com.pimenov.crm.core.database.entity.ChatMessageEntity
import com.pimenov.crm.core.database.entity.NoteEntity
import com.pimenov.crm.core.database.entity.TaskEntity

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
