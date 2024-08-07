package org.idp.wallet.verifiable_credentials_library.domain.type.oidc

data class OidcMetadata(
    // OIDD
    val issuer: String,
    val authorizationEndpoint: String,
    val tokenEndpoint: String,
    val userinfoEndpoint: String,
    val jwksUri: String,
    val registrationEndpoint: String?,
    val scopesSupported: List<String>?,
    val responseTypesSupported: List<String>,
    val responseModesSupported: List<String>?,
    val grantTypesSupported: List<String>?,
    val acrValuesSupported: List<String>?,
    val subjectTypesSupported: List<String>,
    val idTokenSigningAlgValuesSupported: List<String>,
    val idTokenEncryptionAlgValuesSupported: List<String>?,
    val idTokenEncryptionEncValuesSupported: List<String>?,
    val userinfoSigningAlgValuesSupported: List<String>?,
    val userinfoEncryptionAlgValuesSupported: List<String>?,
    val userinfoEncryptionEncValuesSupported: List<String>?,
    val requestObjectSigningAlgValuesSupported: List<String>?,
    val requestObjectEncryptionAlgValuesSupported: List<String>?,
    val requestObjectEncryptionEncValuesSupported: List<String>?,
    val tokenEndpointAuthMethodsSupported: List<String>?,
    val tokenEndpointAuthSigningAlgValuesSupported: List<String>?,
    val displayValuesSupported: List<String>?,
    val claimTypesSupported: List<String>?,
    val claimsSupported: List<String>?,
    val serviceDocumentation: String?,
    val claimsLocalesSupported: Boolean?,
    val claimsParameterSupported: Boolean?,
    val requestParameterSupported: Boolean?,
    val requestUriParameterSupported: Boolean?,
    val requireRequestUriRegistration: Boolean?,
    val opPolicyUri: String?,
    val opTosUri: String?,

    // OAuth2.0 extension
    val revocationEndpoint: String?,
    val revocationEndpointAuthMethodsSupported: List<String>?,
    val revocationEndpointAuthSigningAlgValuesSupported: List<String>?,
    val introspectionEndpoint: String?,
    val introspectionEndpointAuthMethodsSupported: List<String>?,
    val introspectionEndpointAuthSigningAlgValuesSupported: List<String>?,
    val codeChallengeMethodsSupported: List<String>?,
    val tlsClientCertificateBoundAccessTokens: Boolean?,
    val requireSignedRequestObject: Boolean?,
    val authorizationResponseIssParameterSupported: Boolean?,

    // CIBA
    val backchannelTokenDeliveryModesSupported: List<String>?,
    val backchannelAuthenticationEndpoint: String?,
    val backchannelAuthenticationRequestSigningAlgValuesSupported: List<String>?,
    val backchannelUserCodeParameterSupported: Boolean?,
    val authorizationDetailsTypesSupported: List<String>?,

    // JARM
    val authorizationSigningAlgValuesSupported: List<String>?,
    val authorizationEncryptionAlgValuesSupported: List<String>?,
    val authorizationEncryptionEncValuesSupported: List<String>?,

    // PAR
    val pushedAuthorizationRequestEndpoint: String?,
    // Dpop
    val dpopSigningAlgValuesSupported: List<String>?
) {

  fun scopesSupportedAsString(): String {
    return scopesSupported?.filter { it != "openid" && it != "offline_access" }?.joinToString(" ")
        ?: ""
  }
}
