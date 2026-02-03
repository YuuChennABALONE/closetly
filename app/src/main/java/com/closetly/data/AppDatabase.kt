package com.closetly.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ClothingItem::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clothingDao(): ClothingDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // 仅新增可空列：不破坏已有数据
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE items ADD COLUMN purchaseAt INTEGER")
                db.execSQL("ALTER TABLE items ADD COLUMN material TEXT")
                db.execSQL("ALTER TABLE items ADD COLUMN brand TEXT")
                db.execSQL("ALTER TABLE items ADD COLUMN size TEXT")
                db.execSQL("ALTER TABLE items ADD COLUMN price REAL")
                db.execSQL("ALTER TABLE items ADD COLUMN note TEXT")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "closetly.db"
                ).addMigrations(MIGRATION_1_2)
                 .build().also { INSTANCE = it }
            }
        }
    }
}
