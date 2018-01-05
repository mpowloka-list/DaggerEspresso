package com.example.mpowloka.dagger_espresso.pojo

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefsHelper @Inject constructor(val sharedPreferences: SharedPreferences) {

//TODO

}

val PREF_KEY_ACCESS_TOKEN = "access-token"