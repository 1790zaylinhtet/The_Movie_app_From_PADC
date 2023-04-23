package com.example.themovieapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.themovieapp.R
import com.example.themovieapp.adapters.MovieAdapter
import com.example.themovieapp.data.models.MovieModelsImpl
import com.example.themovieapp.delegates.MovieViewHolderDelegate
import com.jakewharton.rxbinding4.widget.textChanges
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit

class MovieSearchActivity : AppCompatActivity(R.layout.activity_search),MovieViewHolderDelegate {

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context,MovieSearchActivity::class.java)
        }
    }

    private lateinit var mMovieAdapter: MovieAdapter

    private val mMovieModels = MovieModelsImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpRecyclerView()
        setUpListeners()
    }
    private fun setUpListeners(){
        etSearch.textChanges()
            .debounce(500L, TimeUnit.MILLISECONDS)
            .flatMap { mMovieModels.searchMovie(it.toString()) }
            .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
            .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe({
                mMovieAdapter.setNewData(it)
            },{
                Toast.makeText(this,it.localizedMessage, Toast.LENGTH_SHORT).show()
            })
    }
    private fun setUpRecyclerView(){
        mMovieAdapter = MovieAdapter(this)
        rvMovies.adapter = mMovieAdapter
        rvMovies.layoutManager = GridLayoutManager(this,2)
    }


    override fun onTapMovie(movieId: Int) {

    }
}