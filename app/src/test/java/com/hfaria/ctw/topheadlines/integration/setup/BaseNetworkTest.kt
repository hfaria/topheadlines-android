package com.hfaria.ctw.topheadlines.integration.setup

import org.junit.Before

abstract class BaseNetworkTest {

    @Before
    fun setupDI() {
        val component = DaggerNetworkTestComponent
            .builder()
            .application(this)
            .build()
        injectTest(component)
    }

    abstract fun injectTest(component: NetworkTestComponent)
}