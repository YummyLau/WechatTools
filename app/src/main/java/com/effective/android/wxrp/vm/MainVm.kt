package com.effective.android.wxrp.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.effective.android.wxrp.data.db.PacketRecord
import com.effective.android.wxrp.data.db.PacketRepository
import com.effective.android.wxrp.utils.singleArgViewModelFactory
import kotlinx.coroutines.*


class MainVm(private val repository: PacketRepository) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val _all_data = MutableLiveData<List<PacketRecord>>()

    companion object {
        val facotry = singleArgViewModelFactory(::MainVm)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun loadPacketList() {
        uiScope.launch (Dispatchers.Main + viewModelJob){
            val packetRecords = async(Dispatchers.IO) {
                return@async repository.getPacketList() }.await()

            _all_data.value = packetRecords
        }
    }
}