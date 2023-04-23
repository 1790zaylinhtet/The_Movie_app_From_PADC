package com.example.themovieapp.mvi.mvibase

interface MVIViews<S : MVIState> {
    fun render(state : S)
}