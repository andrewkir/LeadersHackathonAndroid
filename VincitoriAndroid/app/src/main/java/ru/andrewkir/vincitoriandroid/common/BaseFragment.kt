package ru.andrewkir.vincitoriandroid.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.launch
import ru.andrewkir.vincitoriandroid.web.service.ApiBuilder


abstract class BaseFragment<viewModel : BaseViewModel, repo : BaseRepository, viewBinding : ViewBinding> :
    Fragment() {

    protected lateinit var bind: viewBinding
    protected lateinit var viewModel: viewModel

    protected var apiProvider = ApiBuilder()
    protected lateinit var userPrefsManager: ViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = provideBinding(inflater, container)
        val viewModelFactory = ViewModelFactory(provideRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(provideViewModelClass())

        return bind.root
    }


    abstract fun provideViewModelClass(): Class<viewModel>

    abstract fun provideRepository(): repo

    abstract fun provideBinding(inflater: LayoutInflater, container: ViewGroup?): viewBinding
}