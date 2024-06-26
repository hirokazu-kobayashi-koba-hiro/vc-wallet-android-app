package org.idp.wallet.verifiable_credentials_library.domain.verifiable_presentation

import java.util.UUID
import java.util.stream.Collectors
import org.idp.wallet.verifiable_credentials_library.domain.type.oauth.ResponseMode
import org.idp.wallet.verifiable_credentials_library.domain.type.oauth.ResponseType
import org.idp.wallet.verifiable_credentials_library.domain.type.vp.PresentationDefinition
import org.idp.wallet.verifiable_credentials_library.util.http.HttpClient
import org.idp.wallet.verifiable_credentials_library.util.jose.JoseUtils
import org.idp.wallet.verifiable_credentials_library.util.jose.JwtObject
import org.idp.wallet.verifiable_credentials_library.util.json.JsonUtils

class AuthorizationRequestCreationService(
    private val parameters: VerifiablePresentationRequestParameters
) {

  suspend fun create(): AuthorizationRequest {
    val identifier = UUID.randomUUID().toString()
    val jwtObject = getRequestObject()
    val scopes =
        jwtObject?.let {
          if (it.containsKey("scope")) {
            return@let it.valueAsStringFromPayload("scope")
                ?.split(" ")
                ?.stream()
                ?.collect(Collectors.toSet())
          }
          return@let null
        } ?: parameters.getScope()
    val responseType =
        jwtObject?.let {
          if (it.containsKey("response_type")) {
            return@let ResponseType.of(it.valueAsStringFromPayload("response_type"))
          }
          return@let null
        } ?: parameters.getResponseType()
    val clientId =
        jwtObject?.let {
          if (it.containsKey("client_id")) {
            return@let it.valueAsStringFromPayload("client_id")
          }
          return@let null
        } ?: parameters.getClientId()
    val redirectUri =
        jwtObject?.let {
          if (it.containsKey("redirect_uri")) {
            return@let it.valueAsStringFromPayload("redirect_uri")
          }
          return@let null
        } ?: parameters.getRedirectUri()
    val state =
        jwtObject?.let {
          if (it.containsKey("state")) {
            return@let it.valueAsStringFromPayload("state")
          }
          return@let null
        } ?: parameters.getState()
    val responseMode =
        jwtObject?.let {
          if (it.containsKey("response_mode")) {
            return@let ResponseMode.of(it.valueAsStringFromPayload("response_mode"))
          }
          return@let null
        } ?: parameters.getResponseMode()
    val nonce =
        jwtObject?.let {
          if (it.containsKey("nonce")) {
            return@let it.valueAsStringFromPayload("nonce")
          }
          return@let null
        } ?: parameters.getNonce()
    val requestObject = parameters.getRequestObject()
    val requestUri = parameters.getRequestUri()
    val presentationDefinition = getPresentationDefinition(jwtObject)
    val presentationDefinitionUri =
        jwtObject?.let {
          if (it.containsKey("presentation_definition_uri")) {
            return@let it.valueAsStringFromPayload("presentation_definition_uri")
          }
          return@let null
        } ?: parameters.getPresentationDefinitionUri()
    return AuthorizationRequest(
        identifier = identifier,
        scopes = scopes,
        responseType = responseType,
        clientId = clientId,
        redirectUri = redirectUri,
        state = state,
        responseMode = responseMode,
        nonce = nonce,
        requestObject = requestObject,
        requestUri = requestUri,
        // FIXME
        presentationDefinition = presentationDefinition,
        presentationDefinitionUri = presentationDefinitionUri)
  }

  private suspend fun getRequestObject(): JwtObject? {
    parameters.getRequestUri()?.let {
      val requestObjectResponse = HttpClient.getForJwt(it)
      return JoseUtils.parse(requestObjectResponse)
    }
    return parameters.getRequestObject()?.let {
      return JoseUtils.parse(it)
    }
  }

  private suspend fun getPresentationDefinition(jwtObject: JwtObject?): PresentationDefinition {
    jwtObject?.let {
      it.valueAsObjectFromPayload("presentation_definition")?.let {
        val jsonString = JsonUtils.write(it)
        return JsonUtils.read(jsonString, PresentationDefinition::class.java)
      }
      it.valueAsStringFromPayload("presentation_definition_uri")?.let {
        val response = HttpClient.get(it)
        return JsonUtils.read(response.toString(), PresentationDefinition::class.java)
      }
    }
    parameters.getPresentationDefinitionObject()?.let {
      return JsonUtils.read(it, PresentationDefinition::class.java)
    }
    parameters.getPresentationDefinitionUri()?.let {
      val response = HttpClient.get(it)
      return JsonUtils.read(response.toString(), PresentationDefinition::class.java)
    }
    throw RuntimeException("invalid request, presentation_definition is not found")
  }
}
