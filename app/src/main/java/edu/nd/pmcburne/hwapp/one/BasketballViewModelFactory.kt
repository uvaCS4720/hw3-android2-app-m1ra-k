package edu.nd.pmcburne.hwapp.one

import BasketballRepository
import BasketballScoresViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BasketballViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BasketballScoresViewModel::class.java)) {

            val database = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "scores-db"
            ).build()

            val dao = database.gameDao()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://ncaa-api.henrygd.me/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(BasketballApiService::class.java)

            val repository = BasketballRepository(api, dao)

            return BasketballScoresViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}