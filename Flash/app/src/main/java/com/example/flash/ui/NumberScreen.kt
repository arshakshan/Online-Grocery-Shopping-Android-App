package com.example.flash.ui

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@Composable
fun NumberScreen(
    flashViewModel: FlashViewModel,
    callbacks:  PhoneAuthProvider.OnVerificationStateChangedCallbacks
) {

    val phoneNumber = flashViewModel.phoneNumber.collectAsState()
    val context = LocalContext.current

    Text(
        text = "LOGIN",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = "Enter your phone number to login to proceed",
        fontSize = 20.sp,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = "This phone number will be used for all communications",
        fontSize = 12.sp,
        color = Color(105, 103, 100)
    )
    TextField(
        value = phoneNumber.value,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        onValueChange = {
            flashViewModel.setPhoneNumber(
                it
            )
        },
        label = {
            Text("Your Number")
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Button(
        onClick = {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("${phoneNumber.value}") // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(context as Activity) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()
            Log.d("NumberScreen", "Phone number changed to: +1${phoneNumber.value}") // Log the phone number change

            PhoneAuthProvider.verifyPhoneNumber(options)
            flashViewModel.setLoading(true)
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Send OTP")
    }

}