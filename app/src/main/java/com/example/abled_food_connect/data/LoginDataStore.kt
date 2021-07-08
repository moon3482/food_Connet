package com.example.abled_food_connect.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.createDataStore
import com.example.abled_food_connect.MainActivity
import com.example.abled_food_connect.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class LoginDataStore(context: Context) {
    private var dataStore = context.createDataStore(context.getString(R.string.lastLogin))


        val index = intPreferencesKey("index")


    val exampleCounterFlow: Flow<Int>
        get() = dataStore.data.catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            // No type safety.
            preferences[index] ?: 0

            Log.e("데이터스토어", preferences[index].toString())
        }

    suspend fun incrementCounter(int: Int) {
        dataStore.edit { settings ->
            val currentCounterValue = settings[index] ?: 0
            settings[index] = int
            Log.e("데이터스토어수정", settings[index].toString())
        }
    }

}