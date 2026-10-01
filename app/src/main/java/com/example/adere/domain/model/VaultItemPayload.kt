package com.example.adere.domain.model

import org.json.JSONArray
import org.json.JSONObject

/**
 * Structured secret payload.
 *
 * This entire object is serialized to JSON, encrypted with AES-256-GCM using the Vault DEK,
 * and only stored in ciphertext form in Room database or encrypted backups.
 */
data class VaultItemPayload(
    val password: String = "",
    val pin: String = "",
    val url: String = "",
    val notes: String = "",
    val totpSecret: String = "",
    val totpDigits: Int = 6,
    val totpPeriod: Int = 30,
    val cryptoAddress: String = "",
    val cryptoSeedPhrase: String = "",
    val cryptoPrivateKey: String = "",
    val cryptoNetwork: String = "",
    val wifiSsid: String = "",
    val wifiPassword: String = "",
    val wifiSecurityType: String = "WPA2/WPA3",
    val cardNumber: String = "",
    val cardExpiry: String = "",
    val cardCvv: String = "",
    val cardHolder: String = "",
    val cardBrand: String = "",
    val bankName: String = "",
    val docType: String = "",
    val docNumber: String = "",
    val recoveryCodes: List<String> = emptyList(),
    val customFields: Map<String, String> = emptyMap()
) {
    fun toJson(): String {
        val json = JSONObject()
        json.put("password", password)
        json.put("pin", pin)
        json.put("url", url)
        json.put("notes", notes)
        json.put("totpSecret", totpSecret)
        json.put("totpDigits", totpDigits)
        json.put("totpPeriod", totpPeriod)
        json.put("cryptoAddress", cryptoAddress)
        json.put("cryptoSeedPhrase", cryptoSeedPhrase)
        json.put("cryptoPrivateKey", cryptoPrivateKey)
        json.put("cryptoNetwork", cryptoNetwork)
        json.put("wifiSsid", wifiSsid)
        json.put("wifiPassword", wifiPassword)
        json.put("wifiSecurityType", wifiSecurityType)
        json.put("cardNumber", cardNumber)
        json.put("cardExpiry", cardExpiry)
        json.put("cardCvv", cardCvv)
        json.put("cardHolder", cardHolder)
        json.put("cardBrand", cardBrand)
        json.put("bankName", bankName)
        json.put("docType", docType)
        json.put("docNumber", docNumber)

        val codesArray = JSONArray()
        for (code in recoveryCodes) {
            codesArray.put(code)
        }
        json.put("recoveryCodes", codesArray)

        val customObj = JSONObject()
        for ((key, value) in customFields) {
            customObj.put(key, value)
        }
        json.put("customFields", customObj)

        return json.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): VaultItemPayload {
            if (jsonStr.isBlank()) return VaultItemPayload()
            return try {
                val json = JSONObject(jsonStr)
                val codes = mutableListOf<String>()
                if (json.has("recoveryCodes")) {
                    val arr = json.getJSONArray("recoveryCodes")
                    for (i in 0 until arr.length()) {
                        codes.add(arr.getString(i))
                    }
                }

                val custom = mutableMapOf<String, String>()
                if (json.has("customFields")) {
                    val obj = json.getJSONObject("customFields")
                    val keys = obj.keys()
                    while (keys.hasNext()) {
                        val key = keys.next()
                        custom[key] = obj.optString(key, "")
                    }
                }

                VaultItemPayload(
                    password = json.optString("password", ""),
                    pin = json.optString("pin", ""),
                    url = json.optString("url", ""),
                    notes = json.optString("notes", ""),
                    totpSecret = json.optString("totpSecret", ""),
                    totpDigits = json.optInt("totpDigits", 6),
                    totpPeriod = json.optInt("totpPeriod", 30),
                    cryptoAddress = json.optString("cryptoAddress", ""),
                    cryptoSeedPhrase = json.optString("cryptoSeedPhrase", ""),
                    cryptoPrivateKey = json.optString("cryptoPrivateKey", ""),
                    cryptoNetwork = json.optString("cryptoNetwork", ""),
                    wifiSsid = json.optString("wifiSsid", ""),
                    wifiPassword = json.optString("wifiPassword", ""),
                    wifiSecurityType = json.optString("wifiSecurityType", "WPA2/WPA3"),
                    cardNumber = json.optString("cardNumber", custom["cardNumber"] ?: ""),
                    cardExpiry = json.optString("cardExpiry", custom["cardExpiry"] ?: ""),
                    cardCvv = json.optString("cardCvv", custom["cardCvv"] ?: ""),
                    cardHolder = json.optString("cardHolder", custom["cardHolder"] ?: ""),
                    cardBrand = json.optString("cardBrand", custom["cardBrand"] ?: ""),
                    bankName = json.optString("bankName", custom["bankName"] ?: ""),
                    docType = json.optString("docType", custom["docType"] ?: ""),
                    docNumber = json.optString("docNumber", custom["docNumber"] ?: ""),
                    recoveryCodes = codes,
                    customFields = custom
                )
            } catch (e: Exception) {
                // If corrupted or empty, fallback safely
                VaultItemPayload()
            }
        }
    }
}
