package com.bitwisor.mirrorquiz.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bitwisor.mirrorquiz.repo.Repository

class HomeViewModelFactory (repository: Repository): ViewModelProvider.Factory {
    val repository = repository
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return  HomeViewModel(repository) as T
    }
}