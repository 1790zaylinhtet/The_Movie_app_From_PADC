package com.example.themovieapp.mvi.intents

import com.example.themovieapp.mvi.mvibase.MVIIntent

sealed class MainIntent : MVIIntent {
    class LoadMoviesByGenresIntent(val genrePosition : Int) : MainIntent()
    object LoadAllHomePageData : MainIntent()
}