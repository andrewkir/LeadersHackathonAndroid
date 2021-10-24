package ru.andrewkir.vincitoriandroid.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.andrewkir.vincitoriandroid.flows.main.MainRepository
import ru.andrewkir.vincitoriandroid.flows.main.MainViewModel

class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(repository as MainRepository) as T
           else -> throw IllegalArgumentException("Provide correct viewModel class")
        }
    }
}