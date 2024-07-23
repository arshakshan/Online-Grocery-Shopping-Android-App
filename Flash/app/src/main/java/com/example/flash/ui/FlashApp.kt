package com.example.flash.ui

import android.app.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.flash.R
import com.example.flash.data.InternetItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

enum class FlashAppScreens(val title: String){
    Start("FlashCart"),
    Items("Choose Items"),
    Cart("Your Cart")
}

var canNavigate = false
val auth = FirebaseAuth.getInstance()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashApp(
    flashViewModel: FlashViewModel = viewModel(),
    navController: NavHostController= rememberNavController()
    ){
//    StartScreen(flashViewModel)

    auth.useAppLanguage()
    val user by flashViewModel.user.collectAsState()
    val logoutClicked by flashViewModel.logoutClicked.collectAsState()

    auth.currentUser?.let { flashViewModel.setUser(it) }

    val isVisible by flashViewModel.isVisible.collectAsState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = FlashAppScreens.valueOf(
        backStackEntry?.destination?.route ?: FlashAppScreens.Start.name
    )

    canNavigate = navController.previousBackStackEntry != null

    // add a reference to the cartItems
    val cartItems by flashViewModel.cartItems.collectAsState()

    if(isVisible){
        OfferScreen()
    }else if(user == null){
        LoginUi(flashViewModel)
    } else{

        Scaffold(
            topBar = {
//            Text(text = "Welcome to Flash", modifier = Modifier.padding(20.dp))
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currentScreen.title,
                                    fontSize = 26.sp,
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                if (currentScreen == FlashAppScreens.Cart) {
                                    Text(
                                        text = "(${cartItems.size})",
                                        fontSize = 26.sp,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.clickable {
                                    flashViewModel.setLogoutClass(true)
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.logout),
                                    contentDescription = "Logout",
                                    Modifier.size(24.dp)
                                )
                                Text(
                                    text = "Logout",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(
                                        end = 14.dp,
                                        start = 4.dp
                                    )
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        if(canNavigate){
                            IconButton(onClick = {
                                navController.navigateUp()
                            }) {
                                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back button")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                FlashAppBar(navController, currentScreen, cartItems)
            }
        ) {

            NavHost(
                navController = navController,
                startDestination = FlashAppScreens.Start.name,
                modifier = Modifier.padding(it)
            ){
                composable(route = FlashAppScreens.Start.name){
                    StartScreen(flashViewModel
                    ) {
                        flashViewModel.updateSelectedCategory(it)
                        navController.navigate(FlashAppScreens.Items.name)
                    }
                }
                // setting up route and destination for the navGraph
                composable(route = FlashAppScreens.Items.name){
//                    ItemsScreen(flashViewModel)
                    InternetItemScreen(
                        flashViewModel = flashViewModel,
                        itemUiState = flashViewModel.itemUiState
                    )
                }
                composable(route = FlashAppScreens.Cart.name) {
                    CartScreen(flashViewModel) {
                        navController.navigate(FlashAppScreens.Start.name){
                            popUpTo(0)
                        }
                    }
                }
            }
        }
        if(logoutClicked){
            AlertCheck(
                onYesButtonPressed = {
                    flashViewModel.setLogoutClass(false)
                    auth.signOut()
                    flashViewModel.clearData()
                },
                onNoButtonPressed = {
                    flashViewModel.setLogoutClass(false)
                }

            )
        }
    }

}

@Composable
fun FlashAppBar(
    navController: NavHostController,
    currentScreens: FlashAppScreens,
    cartItems: List<InternetItem>
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 70.dp,
                vertical = 10.dp
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable {
                navController.navigate(FlashAppScreens.Start.name){
                    popUpTo(0)
                }
            }
        ) {
            Icon(imageVector = Icons.Outlined.Home, contentDescription = "Home")
            Text(text = "Home", fontSize = 10.sp)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable {
                if(currentScreens != FlashAppScreens.Cart){
                    navController.navigate(FlashAppScreens.Cart.name)
                }
            }
        ) {
            Box() {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart")
                if(cartItems.isNotEmpty()){
                    Card(
                        Modifier
                            .align(alignment = Alignment.TopEnd)
                            .size(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red
                        ),
                    ){
                        Text(
                            text = cartItems.size.toString(),
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }
            Text(text = "Cart", fontSize = 10.sp)
        }
    }
}

@Composable
fun AlertCheck(
    onYesButtonPressed: () -> Unit,
    onNoButtonPressed: () -> Unit
){
    AlertDialog(
        title = {
            Text("Logout?", fontWeight = FontWeight.Bold, )
        },
        containerColor = Color.White,
        text = {
            Text(text = "Are you sure you want to logout?", fontWeight = FontWeight.Normal)
        },
        confirmButton = {
            TextButton(onClick = {
                onYesButtonPressed()
            }) {
                Text("Yes", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onNoButtonPressed()
            }) {
                Text(text = "No", fontWeight = FontWeight.Bold)
            }
        },
        onDismissRequest = {
            onNoButtonPressed()
        }

    )
}

