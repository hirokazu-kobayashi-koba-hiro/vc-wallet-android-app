package org.idp.wallet.verifiable_credentials_library.util.jose

import com.nimbusds.jose.Algorithm
import com.nimbusds.jose.Header
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory
import com.nimbusds.jose.jwk.Curve
import com.nimbusds.jose.jwk.ECKey
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKMatcher
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.gen.ECKeyGenerator
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT

object JoseUtils {

  fun parse(jose: String): JwtObject {
    val parsedJwt = JWTParser.parse(jose)
    return JwtObject(parsedJwt)
  }

  fun parseAndVerifySignature(jose: String, jwks: String): JwtObject {
    val jwkSet = JWKSet.parse(jwks)
    val signedJWT = SignedJWT.parse(jose)
    val jwk = findKey(signedJWT, jwkSet)
    val defaultJWSVerifierFactory = DefaultJWSVerifierFactory()
    val jwsHeader = signedJWT.header
    val publicKey = jwk.transformPublicKey()
    val jWSVerifier = defaultJWSVerifierFactory.createJWSVerifier(jwsHeader, publicKey)
    val verified = signedJWT.verify(jWSVerifier)
    if (!verified) {
      throw RuntimeException("signature is inValid")
    }
    return JwtObject(signedJWT)
  }

  fun findKey(signedJWT: SignedJWT, jwkSet: JWKSet): JWK {
    val kid = signedJWT.header.keyID
    val jwk = jwkSet.getKeyByKeyId(kid)
    jwk?.let {
      return jwk
    }
    val algorithm = signedJWT.header.algorithm
    val jwkMatcher = JWKMatcher.Builder().algorithm(algorithm).build()
    val filteredJwks = jwkSet.filter(jwkMatcher)
    if (filteredJwks.size() > 2) {
      throw RuntimeException("JWK can not identify, jwk of same algorithm is found multiple")
    }
    return filteredJwks.keys[0]
  }

  fun sign(
      header: Map<String, Any>,
      payload: Map<String, Any>,
      jwks: String,
      keyId: String
  ): String {
    val jwkSet = JWKSet.parse(jwks)
    val jwk = jwkSet.getKeyByKeyId(keyId)
    val headers =
        JWSHeader.Builder(JWSAlgorithm.parse(jwk.algorithm.name)).customParams(header).build()
    val claimSet = JWTClaimsSet.parse(payload)
    val jws = SignedJWT(headers, claimSet)
    val jwsSigner = DefaultJWSSignerFactory().createJWSSigner(jwk)
    jws.sign(jwsSigner)
    return jws.serialize()
  }

  fun sign(
      additionalHeaders: Map<String, Any>,
      payload: Map<String, Any>,
      privateKey: String
  ): String {
    val jwk = JWK.parse(privateKey)
    val headers =
        JWSHeader.Builder(JWSAlgorithm.parse(jwk.algorithm.name))
            .customParams(additionalHeaders)
            .build()
    val claimSet = JWTClaimsSet.parse(payload)
    val jws = SignedJWT(headers, claimSet)
    val jwsSigner = DefaultJWSSignerFactory().createJWSSigner(jwk)
    jws.sign(jwsSigner)
    return jws.serialize()
  }

  fun sign(additionalHeaders: Map<String, Any>, payload: String, privateKey: String): String {
    val jwk = JWK.parse(privateKey)
    val headers =
        JWSHeader.Builder(JWSAlgorithm.parse(jwk.algorithm.name))
            .customParams(additionalHeaders)
            .build()
    val claimSet = JWTClaimsSet.parse(payload)
    val jws = SignedJWT(headers, claimSet)
    val jwsSigner = DefaultJWSSignerFactory().createJWSSigner(jwk)
    jws.sign(jwsSigner)
    return jws.serialize()
  }

  fun generateECKey(keyId: String): String {
    val ecJWK: ECKey =
        ECKeyGenerator(Curve.P_256).keyID(keyId).algorithm(Algorithm.parse("ES256")).generate()
    return ecJWK.toJSONString()
  }

  fun transformJwksAsString(jwk: String): String {
    val parsedJwk = JWK.parse(jwk)
    val jwkSet = JWKSet(parsedJwk)
    return jwkSet.toString(false)
  }

  fun transformPublicKeyAsJwk(jwk: String): JWK {
    val parsedJwk = JWK.parse(jwk)
    return parsedJwk.toPublicJWK()
  }
}

class JwtObject(private val jwt: JWT) {

  fun header(): Header? {
    return jwt.header
  }

  fun kid(): String {
    return jwt.header.customParams.getOrDefault("kid", "") as String
  }

  fun algorithm(): String {
    return jwt.header.algorithm.name
  }

  fun payload(): Map<String, Any> {
    return jwt.jwtClaimsSet.claims
  }

  fun valueAsStringFromPayload(key: String): String? {
    if (containsKey(key)) {
      return jwt.jwtClaimsSet.getStringClaim(key)
    }
    return null
  }

  fun valueAsStringListFromPayload(key: String): List<Any>? {
    if (containsKey(key)) {
      return jwt.jwtClaimsSet.getListClaim(key)
    }
    return null
  }

  fun valueAsLongFromPayload(key: String): Long? {
    if (containsKey(key)) {
      return jwt.jwtClaimsSet.getLongClaim(key)
    }
    return null
  }

  fun valueAsObjectFromPayload(key: String): Map<String, Any>? {
    if (containsKey(key)) {
      return jwt.jwtClaimsSet.getJSONObjectClaim(key)
    }
    return null
  }

  fun containsKey(key: String): Boolean {
    return jwt.jwtClaimsSet.claims.containsKey(key)
  }
}
