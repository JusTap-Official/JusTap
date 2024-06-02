package com.binay.shaw.justap.domain

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val exception: Exception) : Resource<T>()
    data class Loading<T>(val message: String?) : Resource<T>()
    data class Clear<T>(val message: String? = null) : Resource<T>()

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Success(data)
        }

        fun <T> error(exception: Exception): Resource<T> {
            return Error(exception)
        }

        fun <T> loading(message: String? = null): Resource<T> {
            return Loading(message)
        }

        fun <T> clear(message: String? = null): Resource<T> {
            return Clear(message)
        }
    }
}