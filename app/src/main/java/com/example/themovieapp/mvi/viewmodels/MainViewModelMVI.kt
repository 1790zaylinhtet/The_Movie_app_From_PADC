package com.example.themovieapp.mvi.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.themovieapp.mvi.intents.MainIntent
import com.example.themovieapp.mvi.mvibase.MVIViewModel
import com.example.themovieapp.mvi.processor.MainProcessor
import com.example.themovieapp.mvi.states.MainState
import java.util.concurrent.Flow

class MainViewModelMVI(override val state: MutableLiveData<MainState> = MutableLiveData(MainState.idle())) :
    MVIViewModel<MainState,MainIntent> ,ViewModel(){

    private val mProcessor = MainProcessor

    override fun processIntent(intent: MainIntent, lifecycleOwner: LifecycleOwner) {
        when(intent){
            //load Home Page Data
            MainIntent.LoadAllHomePageData ->{
                state.value?.let {
                    mProcessor.loadAllHomePageData(
                        previousState = it
                    ).observe(lifecycleOwner){ newState ->
                        state.postValue(newState)
                        if (newState.moviesByGenre.isEmpty()){
                            processIntent(MainIntent.LoadMoviesByGenresIntent(0),lifecycleOwner)
                        }
                    }
                }
            }
            //Load Movies By Genre
            is MainIntent.LoadMoviesByGenresIntent ->{
                state.value?.let {
                    val genreId = it.genres.getOrNull(intent.genrePosition)?.id ?: 0
                    mProcessor.loadMoviesByGenre(
                        genreId = genreId,
                        previousState = it
                    ).observe(lifecycleOwner , state::postValue)
                }
            }
        }
    }
}