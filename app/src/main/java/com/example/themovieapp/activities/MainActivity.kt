package com.example.themovieapp.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.themovieapp.R
import com.example.themovieapp.activities.MovieDetailsActivity
import com.example.themovieapp.activities.MovieSearchActivity
import com.example.themovieapp.adapters.BannerAdapter
import com.example.themovieapp.adapters.ShowcaseAdapter
import com.example.themovieapp.data.models.MovieModels
import com.example.themovieapp.data.models.MovieModelsImpl
import com.example.themovieapp.data.vos.ActorsVO
import com.example.themovieapp.data.vos.GenreVO
import com.example.themovieapp.data.vos.MovieVO
import com.example.themovieapp.delegates.BannerViewHolderDelegate
import com.example.themovieapp.delegates.MovieViewHolderDelegate
import com.example.themovieapp.delegates.ShowCaseViewHolderDelegate
import com.example.themovieapp.dummy.dummyGenreList
import com.example.themovieapp.mvi.intents.MainIntent
import com.example.themovieapp.mvi.mvibase.MVIViews
import com.example.themovieapp.mvi.states.MainState
import com.example.themovieapp.mvi.viewmodels.MainViewModelMVI
import com.example.themovieapp.mvvm.MainViewModel
import com.example.themovieapp.network.dataagents.MovieDataAgent
import com.example.themovieapp.viewpods.ActorListViewPods
import com.example.themovieapp.viewpods.MovieListViewPods
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_movie_details.*

class MainActivity : AppCompatActivity(),BannerViewHolderDelegate,
    ShowCaseViewHolderDelegate,MovieViewHolderDelegate,MVIViews<MainState> {


    lateinit var mBannerAdapter: BannerAdapter
    lateinit var mShowcaseAdapter: ShowcaseAdapter

    lateinit var mBestPopularMovieListViewPod: MovieListViewPods
    lateinit var mMoviesByGenreViewPod: MovieListViewPods
    lateinit var mActorsListViewPods: ActorListViewPods



    //View Model
    //private lateinit var mViewModel: MainViewModel

    //Mvi View Model
    private lateinit var mViewModel : MainViewModelMVI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         //mvvm
        //setUpViewModel()
        setUpViewModelMVI()
        setUpToolbar()
        setUpViewPager()
        //setUpGenreTabLayout()
        setUpListeners()
        setUpShowcaseRecyclerView()
        setUpViewPods()

        //observeLiveData()
        setInitialIntents()
        observeState()


    }
    private fun observeState(){
        mViewModel.state.observe(this, this::render)
    }
    private fun setUpViewModelMVI(){
        mViewModel = ViewModelProvider(this)[MainViewModelMVI::class.java]
    }
    private fun setInitialIntents(){
        mViewModel.processIntent(MainIntent.LoadAllHomePageData,this)
    }
    //MVVM
//    private fun setUpViewModel(){
//        mViewModel = ViewModelProvider(this)[MainViewModel::class.java]
//        mViewModel.getInitialData()
//    }
//MVVM
//    private fun observeLiveData(){
//        mViewModel.nowPlayingMovieLiveData?.observe(this,mBannerAdapter::setNewData)
//        mViewModel.popularMoviesLiveData?.observe(this,mBestPopularMovieListViewPod::setData)
//        mViewModel.topRatedMovieLiveData?.observe(this,mShowcaseAdapter::setNewData)
//        mViewModel.genresLiveData.observe(this,this::setUpGenreTabLayout)
//        mViewModel.moviesByGenreLiveData.observe(this,mMoviesByGenreViewPod::setData)
//        mViewModel.actorsLiveData.observe(this,mActorsListViewPods::setData)
//    }


    private fun setUpViewPods() {
        mBestPopularMovieListViewPod = vpBestPopularMovieList as MovieListViewPods
        mBestPopularMovieListViewPod.setUpMovieListViewPod(this)

        mMoviesByGenreViewPod = vpMovieByGenre as MovieListViewPods
        mMoviesByGenreViewPod.setUpMovieListViewPod(this)

        mActorsListViewPods = vpActorsHomeScreen as ActorListViewPods
    }

    private fun setUpListeners() {
        //Genre TabLayout
        tabLayoutGenre.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
//                mViewModel.(tab?.position ?: 0)
                mViewModel.processIntent(
                    MainIntent.LoadMoviesByGenresIntent(tab?.position ?: 0),
                    this@MainActivity
                )
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }

    private fun setUpViewPager() {
        mBannerAdapter = BannerAdapter(this)
        viewPagerBanner.adapter = mBannerAdapter

        dotsindicatorBanner.attachTo(viewPagerBanner)
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    private fun setUpGenreTabLayout(genreList : List<GenreVO>) {
        genreList.forEach {
            tabLayoutGenre.newTab().apply {
                text = it.name
                tabLayoutGenre.addTab(this)
            }

        }
    }

    private fun setUpShowcaseRecyclerView() {
        mShowcaseAdapter = ShowcaseAdapter(this)
        rvShowCases.adapter = mShowcaseAdapter
        rvShowCases.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_discover, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.ivSearch){
            startActivity(MovieSearchActivity.newIntent(this))
            return true
        }
        return false
    }


    override fun onTapMovieFromBanner(movieId: Int) {
      startActivity(MovieDetailsActivity.newIntent(this,movieId = movieId))
    }

    override fun onTapMovie(movieId: Int) {
        startActivity(MovieDetailsActivity.newIntent(this,movieId = movieId))
    }

    override fun onTapMovieFromShowcase(movieId: Int) {
        startActivity(MovieDetailsActivity.newIntent(this,movieId = movieId))
    }

    override fun render(state: MainState) {
        if(state.errorMessage.isNotEmpty()){
           Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
        }
        mBannerAdapter.setNewData(state.nowPlayingMovies)
        mBestPopularMovieListViewPod.setData(state.popularMovies)
        mShowcaseAdapter.setNewData(state.topRatedMovies)
        setUpGenreTabLayout(state.genres)
        mMoviesByGenreViewPod.setData(state.moviesByGenre)
        mActorsListViewPods.setData(state.actors)
    }


}