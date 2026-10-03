package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        TaskEntity::class,
        DailyScheduleEntity::class,
        TaskLogEntity::class,
        StudentProfileEntity::class,
        ClassmateStatEntity::class,
        ClassroomConfigEntity::class,
        RegisteredUserEntity::class,
        ActivityEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun responsibilityDao(): ResponsibilityDao

    companion object {

        /**
         * v7 -> v8: streak/ödül alanları eklendi ve sık kullanılan sorgular için indeksler açıldı.
         * Kullanıcı verisi silinmeden yükseltilir (eski sürümdeki destructive migration kaldırıldı).
         */
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE student_profiles ADD COLUMN lastApprovedDate TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE student_profiles ADD COLUMN rewardClaimedMonth TEXT NOT NULL DEFAULT ''")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_daily_schedules_studentId ON daily_schedules (studentId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_task_logs_studentId_dateString ON task_logs (studentId, dateString)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_task_logs_studentId_status ON task_logs (studentId, status)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_registered_users_linkedStudentId ON registered_users (linkedStudentId)")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_responsibility.db"
                )
                    .addMigrations(MIGRATION_7_8)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
