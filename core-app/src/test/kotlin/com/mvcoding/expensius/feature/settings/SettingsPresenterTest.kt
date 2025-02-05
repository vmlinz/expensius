/*
 * Copyright (C) 2016 Mantas Varnagiris.
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

package com.mvcoding.expensius.feature.settings

import com.mvcoding.expensius.Settings
import com.mvcoding.expensius.SubscriptionType
import com.mvcoding.expensius.SubscriptionType.FREE
import com.mvcoding.expensius.SubscriptionType.PREMIUM_PAID
import com.mvcoding.expensius.feature.currency.CurrenciesProvider
import com.mvcoding.expensius.model.Currency
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import rx.lang.kotlin.BehaviorSubject
import rx.lang.kotlin.PublishSubject
import rx.observers.TestSubscriber.create

class SettingsPresenterTest {
    val mainCurrencyRequestedSubject = PublishSubject<Unit>()
    val supportDeveloperRequestedSubject = PublishSubject<Unit>()
    val aboutRequestedSubject = PublishSubject<Unit>()
    val requestMainCurrencySubject = PublishSubject<Currency>()
    val subscriptionTypes = BehaviorSubject(FREE)

    val settings = mock<Settings>()
    val currenciesProvider = CurrenciesProvider()
    val view = mock<SettingsPresenter.View>()
    val presenter = SettingsPresenter(settings, currenciesProvider)

    @Before
    fun setUp() {
        whenever(view.onMainCurrencyRequested()).thenReturn(mainCurrencyRequestedSubject)
        whenever(view.onSupportDeveloperRequested()).thenReturn(supportDeveloperRequestedSubject)
        whenever(view.onAboutRequested()).thenReturn(aboutRequestedSubject)
        whenever(view.requestMainCurrency(any())).thenReturn(requestMainCurrencySubject)
        whenever(settings.subscriptionTypes()).thenReturn(subscriptionTypes)
    }

    @Test
    fun showsMainCurrency() {
        val mainCurrency = Currency("GBP")
        whenever(settings.mainCurrency).thenReturn(mainCurrency)

        presenter.onViewAttached(view)

        verify(view).showMainCurrency(mainCurrency)
    }

    @Test
    fun canSelectNewMainCurrency() {
        val oldCurrency = Currency("GBP")
        val newCurrency = Currency("EUR")
        val allCurrencies = create<List<Currency>>().apply { currenciesProvider.currencies().subscribe(this) }.onNextEvents.first()
        whenever(settings.mainCurrency).thenReturn(oldCurrency)
        presenter.onViewAttached(view)

        requestMainCurrency()
        selectMainCurrency(newCurrency)

        verify(view).requestMainCurrency(allCurrencies)
        verify(view).showMainCurrency(newCurrency)
        verify(settings).mainCurrency = newCurrency
    }

    @Test
    fun showsSubscriptionType() {
        selectSubscriptionType(PREMIUM_PAID)

        presenter.onViewAttached(view)

        verify(view).showSubscriptionType(PREMIUM_PAID)
    }

    @Test
    fun showsUpdatedSubscriptionType() {
        selectSubscriptionType(FREE)
        presenter.onViewAttached(view)

        selectSubscriptionType(PREMIUM_PAID)

        verify(view).showSubscriptionType(PREMIUM_PAID)
    }

    @Test
    fun displaysAbout() {
        presenter.onViewAttached(view)

        requestAbout()

        verify(view).displayAbout()
    }

    @Test
    fun displaysSupportDeveloper() {
        presenter.onViewAttached(view)

        requestSupportDeveloper()

        verify(view).displaySupportDeveloper()
    }

    private fun requestMainCurrency() = mainCurrencyRequestedSubject.onNext(Unit)
    private fun requestSupportDeveloper() = supportDeveloperRequestedSubject.onNext(Unit)
    private fun requestAbout() = aboutRequestedSubject.onNext(Unit)
    private fun selectMainCurrency(currency: Currency) = requestMainCurrencySubject.onNext(currency)
    private fun selectSubscriptionType(subscriptionType: SubscriptionType) = subscriptionTypes.onNext(subscriptionType)
}