/*
 * Copyright (C) 2015 Mantas Varnagiris.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.mvcoding.expensius

import android.content.SharedPreferences
import com.mvcoding.expensius.extension.getObject
import com.mvcoding.expensius.extension.putBoolean
import com.mvcoding.expensius.extension.putObject
import com.mvcoding.expensius.model.Currency
import java.util.*

class UserSettings(private val sharedPreferences: SharedPreferences) : Settings {
    private val KEY_IS_INTRODUCTION_SEEN = "${UserSettings::class.java.name}.KEY_IS_INTRODUCTION_SEEN"
    private val KEY_MAIN_CURRENCY = "${UserSettings::class.java.name}.KEY_MAIN_CURRENCY"

    override var isIntroductionSeen: Boolean = sharedPreferences.getBoolean(KEY_IS_INTRODUCTION_SEEN, false)
        set(value) {
            field = value
            sharedPreferences.putBoolean(KEY_IS_INTRODUCTION_SEEN, value)
        }

    override var mainCurrency: Currency = sharedPreferences.getObject(
            KEY_MAIN_CURRENCY,
            PersistedCurrency::class,
            defaultCurrency()).toCurrency()
        set(value) {
            field = value
            sharedPreferences.putObject(KEY_MAIN_CURRENCY, value.toPersistedCurrency())
        }

    private fun defaultCurrency() = {
        try {
            java.util.Currency.getInstance(Locale.getDefault()).let { PersistedCurrency(it.currencyCode) }
        } catch (e: Exception) {
            PersistedCurrency("USD")
        }
    }

    private fun Currency.toPersistedCurrency() = PersistedCurrency(code)

    private data class PersistedCurrency(val code: String) {
        fun toCurrency() = Currency(code)
    }
}