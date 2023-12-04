package com.bpr.allergendetector.uiTests

import android.util.Log
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.bpr.allergendetector.LoginActivity
import com.bpr.allergendetector.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    @Test
    fun loginFailTest() {
        val email = "asd"
        val password = "dsa"

        waitALittle()

        //assert that login fields and button are displayed
        assertLoginFieldsAndButtonExist()

        //enter bad email and password to fail login
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.typeText(email), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))
            .perform(ViewActions.click())

        //assert that login fields and button are still displayed
        assertLoginFieldsAndButtonExist()

        waitALittle()
    }

    @Test
    fun loginLogoutTest() {
        val email = "logintest@gmail.com"
        val password = "test123"

        waitALittle()

        //assert that login fields and button are displayed
        assertLoginFieldsAndButtonExist()

        //enter email and password to login
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.typeText(email), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))
            .perform(ViewActions.click())

        //need to wait for profile to load
        waitALittle()

        // press stay safe button if it exists
        try {
            Espresso.onView(ViewMatchers.withId(R.id.guidelinesScrollView))
                .perform(ViewActions.swipeUp())
            Espresso.onView(ViewMatchers.withId(R.id.staySafeButton)).check(
                ViewAssertions.matches(
                    ViewMatchers.isDisplayed()
                )
            )
                .perform(ViewActions.click())
        } catch (e: NoMatchingViewException) {
            Log.d("Test", "Button with ID R.id.staySafeButton not found. Skipping this step.")
        }

        waitALittle()

        //assert that login fields and button are not displayed
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .check(ViewAssertions.doesNotExist())
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .check(ViewAssertions.doesNotExist())
        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))
            .check(ViewAssertions.doesNotExist())

        //go to profile page
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())

        //logout
        Espresso.onView(ViewMatchers.withId(R.id.logoutButton))
            .perform(ViewActions.click())

        waitALittle()
    }

    private fun assertLoginFieldsAndButtonExist() {
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.buttonLogin))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun waitALittle() {
        Thread.sleep(1000)
    }
}
