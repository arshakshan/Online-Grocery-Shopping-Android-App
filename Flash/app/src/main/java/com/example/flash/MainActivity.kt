package com.example.flash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flash.ui.FlashApp
import com.example.flash.ui.theme.FlashTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    FlashApp()
//                    LazyColumn {
//                        // to use single Composable we need to use items
////                        items {
////                            TextField(value = "First Item", onValueChange = )
////                        }
//                        items(50){ index ->
//                            Text(text = "Item $index")
//                        }
//                    }
//                    LazyVerticalGrid(columns = GridCells.Adaptive(128.dp)) {
//                        items(50){ index ->
//                            Text(text = "Item $index")
//                        }
//                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FlashTheme {
        FlashApp()
    }
}