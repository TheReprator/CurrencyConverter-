package reprator.currencyconverter.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import reprator.currencyconverter.data.repository.db.DBCurrencyDao
import reprator.currencyconverter.data.repository.db.DBCurrencyDatabase
import reprator.currencyconverter.data.repository.db.DBManager
import reprator.currencyconverter.data.repository.db.DBManagerImpl
import timber.log.Timber

@InstallIn(SingletonComponent::class)
@Module
class DBCurrencyModule {

    companion object{
        private const val DATABASE_NAME = "currency_database"
    }
    private val databaseCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Timber.d("RoomDatabaseModule onCreate")
        }
    }

    @Provides
    fun providesRoomDatabase(@ApplicationContext context: Context): DBCurrencyDatabase {
        return Room.databaseBuilder(context, DBCurrencyDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .addCallback(databaseCallback)
            .build()
    }

    @Provides
    fun providesCategoryDAO(dbCurrencyDatabase: DBCurrencyDatabase): DBCurrencyDao =
        dbCurrencyDatabase.currencyDAO()

    @Provides
    fun providesDBManager(dbCurrencyDao: DBCurrencyDao): DBManager =
        DBManagerImpl(dbCurrencyDao)

}