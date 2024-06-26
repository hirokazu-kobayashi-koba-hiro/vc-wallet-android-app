package org.idp.wallet.verifiable_credentials_library.domain.verifiable_credentials

import org.idp.wallet.verifiable_credentials_library.util.http.extractQueriesAsSingleStringMap
import org.idp.wallet.verifiable_credentials_library.util.http.extractScheme

class CredentialOfferRequest(private val url: String) {
  val scheme: String? = extractScheme(url)
  val params: Map<String, String> = extractQueriesAsSingleStringMap(url)

  fun credentialOffer(): String? {
    return params["credential_offer"]
  }

  fun credentialOfferUri(): String? {
    return params["credential_offer_uri"]
  }
}
