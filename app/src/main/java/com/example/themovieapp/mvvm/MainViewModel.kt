package com.example.themovieapp.mvvm

import android.graphics.Movie
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.themovieapp.data.models.MovieModelsImpl
import com.example.themovieapp.data.vos.ActorsVO
import com.example.themovieapp.data.vos.GenreVO
import com.example.themovieapp.data.vos.MovieVO

class MainViewModel : ViewModel() {

    //Model
    private val mMovieModel = MovieModelsImpl

    //Live Data
    var nowPlayingMovieLiveData : LiveData<List<MovieVO>>? = null
    var popularMoviesLiveData : LiveData<List<MovieVO>>? = null
    var topRatedMovieLiveData : LiveData<List<MovieVO>>? = null
    val genresLiveData = MutableLiveData<List<GenreVO>>()
    val moviesByGenreLiveData = MutableLiveData<List<MovieVO>>()
    val actorsLiveData = MutableLiveData<List<ActorsVO>>()
    val mErrorLiveData = MutableLiveData<String>()

    fun getInitialData(){
        nowPlayingMovieLiveData = mMovieModel.getNowPlayingMovies { mErrorLiveData.postValue(it) }
        popularMoviesLiveData = mMovieModel.getPopularMovies { mErrorLiveData.postValue(it) }
        topRatedMovieLiveData = mMovieModel.getTopRatedMovies { mErrorLiveData.postValue(it) }

        mMovieModel.getGenres(
            onSuccess = {
                genresLiveData.postValue(it)
                getMovieByGenre(0)
            },
            onFailure = {
                mErrorLiveData.postValue(it)
            }
        )

        mMovieModel.getActors(
            onSuccess = {
                actorsLiveData.postValue(it)
            },
            onFailure = {
                mErrorLiveData.postValue(it)
            }
        )
    }
    fun getMovieByGenre(genrePosition: Int){
        Log.d("genres","${genresLiveData.value}")
        genresLiveData.value?.getOrNull(genrePosition)?.id?.let {
            mMovieModel.getMoviesByGenres(it.toString(),
            onSuccess = { moviesByGenre ->
                moviesByGenreLiveData.postValue(moviesByGenre)
            },
            onFailure = { errorMessage ->
                mErrorLiveData.postValue(errorMessage)
            })
        }
    }
}