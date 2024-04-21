package org.idp.wallet.verifiable_credentials_library

import android.content.Context
import org.idp.wallet.verifiable_credentials_library.basic.resource.AssetsReader
import org.idp.wallet.verifiable_credentials_library.configuration.ClientConfiguration
import org.idp.wallet.verifiable_credentials_library.configuration.ClientConfigurationRepository
import org.idp.wallet.verifiable_credentials_library.configuration.WalletConfigurationReader
import org.idp.wallet.verifiable_credentials_library.handler.oauth.OAuthRequestHandler
import org.idp.wallet.verifiable_credentials_library.handler.verifiable_presentation.VerifiablePresentationHandler
import org.idp.wallet.verifiable_credentials_library.verifiable_credentials.VerifiableCredentialRegistry
import org.idp.wallet.verifiable_credentials_library.verifiable_credentials.VerifiableCredentialsRecords
import org.idp.wallet.verifiable_credentials_library.verifiable_credentials.VerifiableCredentialsService
import org.json.JSONObject

object VerifiableCredentialsClient {

  private lateinit var verifiableCredentialsService: VerifiableCredentialsService
  private lateinit var verifiablePresentationHandler: VerifiablePresentationHandler
  private lateinit var walletConfigurationReader: WalletConfigurationReader

  fun init(context: Context, clientId: String) {
    val assetsReader = AssetsReader(context)
    val registry = VerifiableCredentialRegistry(context)
    walletConfigurationReader = WalletConfigurationReader(assetsReader)
    verifiableCredentialsService = VerifiableCredentialsService(registry, clientId)
    val mock = ClientConfigurationRepository {
      return@ClientConfigurationRepository ClientConfiguration()
    }
    verifiablePresentationHandler =
        VerifiablePresentationHandler(
            registry, OAuthRequestHandler(walletConfigurationReader, mock))
  }

  suspend fun requestVCI(url: String, format: String = "vc+sd-jwt"): JSONObject {
    return verifiableCredentialsService.requestVCI(url, format)
  }

  fun getAllCredentials(): Map<String, VerifiableCredentialsRecords> {
    return verifiableCredentialsService.getAllCredentials()
  }

  suspend fun handleVpRequest(url: String) {
    verifiablePresentationHandler.handleVpRequest(url)
  }
}
