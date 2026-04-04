package com.pimenov.crm.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pimenov.crm.core.database.dao.ChatMessageDao
import com.pimenov.crm.core.database.dao.NoteDao
import com.pimenov.crm.core.database.dao.TaskDao
import com.pimenov.crm.core.database.entity.ChatMessageEntity
import com.pimenov.crm.core.database.entity.NoteEntity
import com.pimenov.crm.core.database.entity.TaskEntity

@Database(
    entities = [NoteEntity::class, ChatMessageEntity::class, TaskEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun taskDao(): TaskDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN reminderAt INTEGER DEFAULT NULL")
            }
        }
    }
}
