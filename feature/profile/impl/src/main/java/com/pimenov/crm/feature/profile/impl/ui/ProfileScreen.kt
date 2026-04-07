package com.pimenov.crm.feature.profile.impl.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.pimenov.crm.feature.settings.impl.ui.AuthState
import com.pimenov.crm.feature.settings.impl.ui.SettingsViewModel
import com.pimenov.uikit.UiCoreString
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(UiCoreString.nav_profile),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(24.dp))

        // Avatar area
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Rounded.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
                tint = if (authState.isSignedIn) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(12.dp))
            if (authState.isSignedIn) {
                Text(
                    text = authState.displayName ?: authState.email.orEmpty(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (authState.displayName != null && authState.email != null) {
                    Text(
                        text = authState.email.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = stringResource(UiCoreString.profile_not_signed_in),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // Sync card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (authState.isSignedIn) {
                    SignedInContent(
                        authState = authState,
                        onSyncNow = viewModel::syncNow,
                        onSignOut = viewModel::signOut
                    )
                } else {
                    SignInContent(onSignInWithToken = viewModel::signInWithGoogleIdToken)
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SignedInContent(
    authState: AuthState,
    onSyncNow: () -> Unit,
    onSignOut: () -> Unit
) {
    if (authState.isSyncing) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(UiCoreString.settings_account_syncing),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        val msg = authState.syncMessage
        if (msg != null) {
            Text(
                text = if (msg == "synced") stringResource(UiCoreString.settings_account_sync_done)
                else stringResource(UiCoreString.settings_account_sync_error, msg),
                style = MaterialTheme.typography.bodySmall,
                color = if (msg == "synced") MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(12.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onSyncNow, modifier = Modifier.weight(1f)) {
                Text(stringResource(UiCoreString.profile_sync_notes))
            }
            OutlinedButton(onClick = onSignOut, modifier = Modifier.weight(1f)) {
                Text(stringResource(UiCoreString.settings_account_sign_out))
            }
        }
    }
}

@Composable
private fun SignInContent(onSignInWithToken: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Text(
        text = stringResource(UiCoreString.profile_sign_in_description),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = {
            scope.launch {
                try {
                    val credentialManager = CredentialManager.create(context)
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(
                            context.getString(
                                context.resources.getIdentifier(
                                    "default_web_client_id", "string", context.packageName
                                )
                            )
                        )
                        .build()
                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()
                    val result = credentialManager.getCredential(context, request)
                    val googleIdToken = GoogleIdTokenCredential
                        .createFrom(result.credential.data)
                        .idToken
                    onSignInWithToken(googleIdToken)
                } catch (e: Exception) {
                    Log.e("ProfileScreen", "Google sign-in failed", e)
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(stringResource(UiCoreString.settings_account_sign_in))
    }
}
