package `fun`.ponyets.ktoroomsample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import `fun`.ponyets.ktoroomsample.ui.theme.KtorOOMSampleTheme
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.streams.asInput
import kotlinx.coroutines.launch

const val HOST = "http://10.0.2.2:8080"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KtorOOMSampleTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding), Alignment.Center) {
                        val coroutineScope = rememberCoroutineScope()
                        val galleryLauncher =
                            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                                Log.d("MainActivity", "Selected: $it")
                                val httpClient = HttpClient()
                                if (it == null) return@rememberLauncherForActivityResult
                                coroutineScope.launch {
                                    try {
                                        httpClient.submitFormWithBinaryData(
                                            "$HOST/upload",
                                            formData {
                                                appendInput(
                                                    "file",
                                                    Headers.build {
                                                        append(
                                                            HttpHeaders.ContentType,
                                                            ContentType.Video.MP4.toString()
                                                        )
                                                        append(
                                                            HttpHeaders.ContentDisposition,
                                                            "filename=\"video.mp4\""
                                                        )
                                                    },
                                                ) {
                                                    applicationContext.contentResolver.openInputStream(
                                                        it
                                                    )!!.asInput()
                                                }
                                            }
                                        )
                                        snackbarHostState.showSnackbar("Upload success")
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Upload failed: ${e.javaClass.simpleName}")
                                        e.printStackTrace()
                                    }
                                }
                            }
                        Button(
                            onClick = {
                                galleryLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.VideoOnly
                                    )
                                )
                            }
                        ) {
                            Text("Upload")
                        }
                    }
                }
            }
        }
    }
}