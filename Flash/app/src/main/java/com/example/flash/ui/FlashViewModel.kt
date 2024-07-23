package com.example.flash.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flash.data.InternetItem
import com.example.flash.network.FlashApi
import com.example.flash.network.FlashApiService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

//changed the viewModel to AndroidViewModel as we need to access the application context and the dataStore
class FlashViewModel(application: Application) : AndroidViewModel(application) {

    // _ is used to indicate that this is a private variable, internal to the class
    private val _uiState = MutableStateFlow(FlashUiState()) // serves as intermediary between the ViewModel and the UI, part of MVVM
    val uiState: StateFlow<FlashUiState> = _uiState.asStateFlow()


    // backing property for isVisible
    val _isVisible = MutableStateFlow<Boolean>(true)
    val isVisible = _isVisible // public property, for external classes to observe changes in the value of isVisible

//    var itemUiState: String by mutableStateOf("")
//        private set     // to ensure that the value of the itemUiState can be modified within the flashViewModel class only

    // setting default value of itemUiState to Loading
    var itemUiState: ItemUiState by mutableStateOf(ItemUiState.Loading)
        private set

    private val _cartItems = MutableStateFlow<List<InternetItem>>(emptyList())
    val cartItems: StateFlow<List<InternetItem>> get() = _cartItems.asStateFlow()

    //mutable stateflow for holding user authentication
    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user: MutableStateFlow<FirebaseUser?> = _user

    private val _phoneNumber = MutableStateFlow<String>("")
    val phoneNumber: MutableStateFlow<String> get()= _phoneNumber

    private val _otp = MutableStateFlow("")
    val otp: MutableStateFlow<String> get() = _otp

    private val _verificationId = MutableStateFlow("")
    val verificationId: MutableStateFlow<String> get() = _verificationId

    private val _ticks = MutableStateFlow(60L)
    val ticks: StateFlow<Long> get() = _ticks

    private val _loading = MutableStateFlow(false)
    val loading: MutableStateFlow<Boolean> get() = _loading

    private val _logoutClicked = MutableStateFlow(false)
    val logoutClicked: MutableStateFlow<Boolean> get() = _logoutClicked

    private lateinit var timerJob: Job

    // Write a message to the database
    val database = Firebase.database
    val myRef = database.getReference("users/${auth.currentUser?.uid}/cart")


    // datastore object to save the cartItems
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name ="cart")

    // preferences key to save the cartItems
    private val cartItemsKey = stringPreferencesKey("cart_items")

    private val context = application.applicationContext

    lateinit var internetJob: Job // lateinit as they will be initialised later
    lateinit var screenJob: Job

    sealed interface ItemUiState{
        // data class is used to save data
        data class Success(val items:List<InternetItem>): ItemUiState

        // these two can be objects as we dont need to save any data in them
        object Loading: ItemUiState
        object Error: ItemUiState
    }

    fun setPhoneNumber(phoneNumber: String){
        _phoneNumber.value = phoneNumber
    }

    fun setOtp(otp: String){
        _otp.value = otp
    }

    fun setVerificationId(verificationId: String){
        _verificationId.value = verificationId
    }

    fun setUser(user: FirebaseUser){
        _user.value = user
    }

    fun runTimer(){
        timerJob = viewModelScope.launch{
            while(_ticks.value > 0){
                delay(1000)
                _ticks.value -= 1
            }
        }
    }

    fun resetTimer(){
        try {
            timerJob.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }finally{
            _ticks.value = 60
        }
    }

    fun clearData(){
        _user.value = null
        _phoneNumber.value = ""
        _otp.value = ""
        _verificationId.value = ""
        resetTimer()
    }

    fun setLoading(isLoading: Boolean){
        _loading.value = isLoading
    }

    fun setLogoutClass(logoutStatus: Boolean){
        _logoutClicked.value = logoutStatus
    }

    private suspend fun saveCartItemsToDataStore(){
        context.dataStore.edit{ preferences ->
            preferences[cartItemsKey] = Json.encodeToString(_cartItems.value)
        }
    }

    private suspend fun loadCartItemsFromDataStore(){
        val fullData = context.dataStore.data.first() // first() is used to get the first value from the flow
        val cartItemsJson = fullData[cartItemsKey]
        if (!cartItemsJson.isNullOrBlank())
            _cartItems.value = Json.decodeFromString(cartItemsJson)
    }

    fun addToCart(item: InternetItem){
        _cartItems.value += item
        viewModelScope.launch {
            saveCartItemsToDataStore()
        }
    }

    fun addToDatabase(item: InternetItem){
        myRef.push().setValue(item)
    }

    fun fillCartItems(){
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                _cartItems.value = emptyList()
                for (childSnapshot in dataSnapshot.children){
                    val item = childSnapshot.getValue(InternetItem::class.java)
                    item?.let{
                        val newItem = it
                        addToCart(newItem)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    fun removeFromCart(oldItem: InternetItem){
//        _cartItems.value -= item
//        viewModelScope.launch {
//            saveCartItemsToDataStore()
//        }
        myRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (childSnapshot in dataSnapshot.children){
                    var itemRemoved = false
                    val item = childSnapshot.getValue(InternetItem::class.java)
                    item?.let {
                        if(item.itemName == oldItem.itemName && item.itemPrice == item.itemPrice){
                            childSnapshot.ref.removeValue()
                            itemRemoved = true
                        }
                    }
                    if(itemRemoved){
                        break
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    fun updateClickText(updatedText: String){
        _uiState.update {
            it.copy(
                clickStatus = updatedText
            )
        }
    }

    fun updateSelectedCategory(updatedCategory: Int){
        _uiState.update{
            it.copy(
                selectedCategory = updatedCategory
            )
        }
    }

    fun toggleVisibility(){
        _isVisible.value = false
    }

    fun getFlashItems(){

        internetJob = viewModelScope.launch{
            try{
                // fetch items from the API
                // save the data fetched in other parts of the app
                val listResult = FlashApi.retrofitService.getItems()
//                itemUiState = listResult
                itemUiState = ItemUiState.Success(listResult)
                loadCartItemsFromDataStore()
            }
            catch (e: Exception){
//                itemUiState = "Internet Unavailable, Try again. \nError: ${e.message}"
                itemUiState = ItemUiState.Error
                toggleVisibility()
                screenJob.cancel()
            }
        }
    }

    init{
        // launches a coroutine in the viewModelScope
        screenJob = viewModelScope.launch(Dispatchers.Default){
            delay(3000)
//            getFlashItems()
            toggleVisibility()

        }
        getFlashItems()
        fillCartItems()
    }
}