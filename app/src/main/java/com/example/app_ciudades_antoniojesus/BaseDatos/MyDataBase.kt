package com.example.app_ciudades_antoniojesus.BaseDatos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app_ciudades_antoniojesus.DataClass.Ciudad

@Database(entities = [Ciudad::class], version = 2, exportSchema = false)
abstract class MyDataBase : RoomDatabase() {
    abstract fun ciudadesDao(): CiudadesDAO

    companion object {
        const val DBNAME = "ciudades_database"

        @Volatile
        private var INSTANCE: MyDataBase? = null

        fun getDatabase(context: Context): MyDataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(MyDataBase::class) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDataBase::class.java,
                    DBNAME
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}