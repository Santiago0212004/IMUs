package edu.co.icesi.imus.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.co.icesi.imus.components.DeviceCard
import edu.co.icesi.imus.components.SignalVisualization
import edu.co.icesi.imus.viewmodel.DataCollectionViewModel

@SuppressLint("MissingPermission")
@Composable
fun DataCollectionScreen(
    viewModel: DataCollectionViewModel,
    onFinish: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Dispositivos Conectados",
            style = MaterialTheme.typography.headlineSmall
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            val statusColor = if (uiState.allTargetDevicesConnected) Color.Green else Color.Red
            Text(
                text = if (uiState.allTargetDevicesConnected)
                    "Todos los dispositivos conectados"
                else
                    "Esperando conexión...",
                color = statusColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )

            if (!uiState.allTargetDevicesConnected) {
                CircularProgressIndicator(
                    modifier = Modifier.height(16.dp),
                    strokeWidth = 2.dp
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .height(150.dp)
                .padding(vertical = 8.dp)
        ) {
            items(uiState.connectedDevices) { device ->
                DeviceCard(device = device)
            }
        }

        if (uiState.isCollecting) {
            if (uiState.allTargetDevicesConnected) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(600.dp)
                ) {
                    items(uiState.connectedDevices) { device ->
                        Text(device.name)
                        SignalVisualization(
                            data = uiState.imuData,
                            device = device.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            } else {
                viewModel.stopDataCollection()
            }

        } else {
            Text(
                text = if (uiState.allTargetDevicesConnected)
                    "Listo para iniciar la recolección de datos"
                else
                    "Esperando a que todos los dispositivos se conecten...",
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { viewModel.startDataCollection() },
                enabled = uiState.allTargetDevicesConnected && !uiState.isCollecting
            ) {
                Text("Iniciar")
            }

            Button(
                onClick = {
                    viewModel.stopDataCollection()
                    onFinish()
                },
                enabled = uiState.isCollecting
            ) {
                Text("Detener")
            }

            Button(
                onClick = { viewModel.scanForDevices() },
                enabled = !uiState.isCollecting
            ) {
                Text("Escanear")
            }
        }

        Button(
            onClick = { onFinish() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = !uiState.isCollecting
        ) {
            Text("Finalizar")
        }
    }
}