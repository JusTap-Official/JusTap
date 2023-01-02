package com.binay.shaw.justap.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binay.shaw.justap.data.LocalUserDatabase
import com.binay.shaw.justap.helper.Util
import com.binay.shaw.justap.model.LocalUser
import com.binay.shaw.justap.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by binay on 02,January,2023
 */
class Home_ViewModel : ViewModel() {

    var status = MutableLiveData<Int>()
    var localUser = MutableLiveData<LocalUser>()
    var firstName = MutableLiveData<String>()


    init {
        status.value = 0
        //1 -> success, 2 -> fail
    }

    fun getUser(localUserDatabase: LocalUserDatabase) {
        viewModelScope.launch(Dispatchers.IO) {
            val name: String = localUserDatabase.localUserDao().getName()[0]
            Util.log("NAME IS : $name")
            firstName.postValue(name.split(" ")[0])
        }
    }



}