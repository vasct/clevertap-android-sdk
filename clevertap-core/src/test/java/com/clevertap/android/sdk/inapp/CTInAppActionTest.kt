package com.clevertap.android.sdk.inapp

import org.junit.Test
import kotlin.test.assertEquals

class CTInAppActionTest {

    @Test
    fun `test create key value`() {
        val kv = HashMap<String, String>()
        val action = CTInAppAction.createKeyValuesAction(kv)

        assertEquals(action.type, InAppActionType.KEY_VALUES)
    }

}