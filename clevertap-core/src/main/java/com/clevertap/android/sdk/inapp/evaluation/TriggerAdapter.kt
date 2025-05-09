package com.clevertap.android.sdk.inapp.evaluation

import androidx.annotation.VisibleForTesting
import com.clevertap.android.sdk.Constants
import com.clevertap.android.sdk.isInvalidIndex
import org.json.JSONArray
import org.json.JSONObject

/**
 * Data class representing a trigger condition for in-app messages.
 *
 * @param propertyName The name of the property to be checked.
 * @param op The operator used for comparison (e.g., GreaterThan, Equals, etc.).
 * @param value The value to compare against.
 */
data class TriggerCondition(
    val propertyName: String,
    val op: TriggerOperator,
    val value: TriggerValue,
)

data class TriggerGeoRadius(
    var latitude: Double,
    var longitude: Double,
    var radius: Double
)

/**
 * Enum class representing possible operators for trigger conditions.
 *
 * @param operatorValue The raw value associated with the operator.
 */
enum class TriggerOperator(val operatorValue: Int) {
    GreaterThan(0),
    Equals(1),
    LessThan(2),
    Contains(3),
    Between(4),
    NotEquals(15),
    Set(26), // Exists
    NotSet(27), // Not exists
    NotContains(28);

    companion object {
        /**
         * Converts a raw operator value to a TriggerOperator instance.
         * If no match is found, it defaults to Equals.
         *
         * @param operatorValue The raw operator value to convert.
         * @return The corresponding TriggerOperator instance.
         */
        fun fromOperatorValue(operatorValue: Int) =
            values().find { it.operatorValue == operatorValue }
                ?: Equals
    }
}

/**
 * Extension function for JSONObject that retrieves a TriggerOperator from the specified key.
 *
 * @param key The key to look up in the JSONObject.
 * @return The TriggerOperator associated with the key or Equals if not found.
 */
fun JSONObject?.optTriggerOperator(key: String): TriggerOperator {
    val optInt = this?.optInt(key, TriggerOperator.Equals.operatorValue)
        ?: TriggerOperator.Equals.operatorValue
    return TriggerOperator.fromOperatorValue(optInt)
}

/**
 * Class responsible for adapting trigger conditions from a JSON object.
 *
 * @param triggerJSON The JSON object containing trigger conditions.
 */
class TriggerAdapter(triggerJSON: JSONObject) {

    /**
     * The name of the event associated with the trigger conditions.
     */
    val eventName: String = triggerJSON.optString(KEY_EVENT_NAME, "")

    /**
     * The JSONArray containing event property trigger conditions.
     */
    val properties: JSONArray? = triggerJSON.optJSONArray(KEY_EVENT_PROPERTIES)

    /**
     * The JSONArray containing item property trigger conditions.Used for Charged event.
     */
    val items: JSONArray? = triggerJSON.optJSONArray(KEY_ITEM_PROPERTIES)

    /**
     * The JSONArray containing Geographic radius trigger conditions.
     * Used for location-based trigger conditions within a specified geographical radius.
     */
    val geoRadiusArray: JSONArray? = triggerJSON.optJSONArray(KEY_GEO_RADIUS_PROPERTIES)

    /**
     * The string associated with the attribute name for changes in the user-profile
     * Used for user attribute changes trigger conditions
     */
    val profileAttrName: String? = triggerJSON.optString(KEY_PROFILE_ATTR_NAME, null)

    val firstTimeOnly: Boolean = triggerJSON.optBoolean(KEY_FIRST_TIME_ONLY, false)

    /**
     * Get the count of event property trigger conditions.
     */
    val propertyCount: Int
        get() = properties?.length() ?: 0

    /**
     * Get the count of item property trigger conditions.
     */
    val itemsCount: Int
        get() = items?.length() ?: 0

    /**
     * Get the count of geoRadius property based trigger conditions.
     */
    val geoRadiusCount: Int
        get() = geoRadiusArray?.length() ?: 0

    internal companion object {
        const val KEY_FIRST_TIME_ONLY = "firstTimeOnly"
        const val KEY_EVENT_NAME = "eventName"
        const val KEY_EVENT_PROPERTIES = "eventProperties"
        const val KEY_ITEM_PROPERTIES = "itemProperties"
        const val KEY_GEO_RADIUS_PROPERTIES = "geoRadius"
        const val KEY_PROFILE_ATTR_NAME = "profileAttrName"
        const val KEY_PROPERTY_VALUE = "propertyValue"
        const val INAPP_OPERATOR = "operator"
        const val INAPP_PROPERTYNAME = "propertyName"
    }

    /**
     * Internal function to create a TriggerCondition from a JSON property object.
     *
     * @param property The JSON object representing a trigger condition property.
     * @return The corresponding TriggerCondition.
     */
    @VisibleForTesting
    fun triggerConditionFromJSON(property: JSONObject): TriggerCondition {
        val value = TriggerValue(property.opt(KEY_PROPERTY_VALUE))

        val operator = property.optTriggerOperator(INAPP_OPERATOR)

        return TriggerCondition(
            property.optString(INAPP_PROPERTYNAME, ""),
            operator,
            value
        )
    }

    /**
     * Retrieve a TriggerCondition at the specified index from event properties.
     *
     * @param index The index of the TriggerCondition to retrieve.
     * @return The TriggerCondition at the specified index or null if not found or invalid index.
     */
    fun propertyAtIndex(index: Int): TriggerCondition? {
        if (properties.isInvalidIndex(index)) {
            return null
        }

        val propertyJSONObject = properties?.optJSONObject(index) ?: return null

        return triggerConditionFromJSON(propertyJSONObject)
    }


    /**
     * Retrieve a TriggerCondition at the specified index from item properties.
     *
     * @param index The index of the TriggerCondition to retrieve.
     * @return The TriggerCondition at the specified index or null if not found or invalid index.
     */
    fun itemAtIndex(index: Int): TriggerCondition? {
        if (items.isInvalidIndex(index)) {
            return null
        }

        val itemJSONObject = items?.optJSONObject(index) ?: return null

        return triggerConditionFromJSON(itemJSONObject)
    }

    /**
     * Retrieves a [TriggerGeoRadius] instance at the specified index from the geoRadiusArray.
     *
     * The function checks if the provided index is valid and, if so, extracts the latitude, longitude, and radius
     * information from the corresponding JSON object within the geoRadiusArray. It then creates and returns a
     * [TriggerGeoRadius] instance based on the extracted data.
     *
     * @param index The index indicating the position of the desired [TriggerGeoRadius] in the geoRadiusArray.
     * @return A [TriggerGeoRadius] instance if the index is valid and data is available; otherwise, `null`.
     */
    fun geoRadiusAtIndex(index: Int): TriggerGeoRadius? {
        if (geoRadiusArray.isInvalidIndex(index)) {
            return null
        }

        val geoRadiusItem = geoRadiusArray?.optJSONObject(index) ?: return null

        val latitude = geoRadiusItem.optDouble("lat")
        val longitude = geoRadiusItem.optDouble("lng")
        val radius = geoRadiusItem.optDouble("rad")
        return TriggerGeoRadius(latitude, longitude, radius)
    }
}
