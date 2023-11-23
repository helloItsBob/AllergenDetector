package com.bpr.allergendetector

import android.util.Log
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class RegistrationActivityUITest {


    @get:Rule
    val activityRule = ActivityScenarioRule(RegistrationActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA,
            android.Manifest.permission.INTERNET)

    @Test
    fun failRegistrationTest() {
        val email = "fail test"
        val password = "fail"
        val longPassword = "longpasswordtofail"

        waitALittle()

        //password too short and bad email
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.typeText(email), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.editTextRepeatPassword))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click())

        waitALittle()

        //passwords don't match and bad email
        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.replaceText(longPassword), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click())

        waitALittle()

        //bad email
        Espresso.onView(ViewMatchers.withId(R.id.editTextRepeatPassword))
            .perform(ViewActions.replaceText(longPassword), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click())

        waitALittle()
    }
    @Test
    fun registerAndDeleteAccountTest() {
        val email = "MockedTest@gmail.com"
        val password = "paSSword123!"

        waitALittle()

        //register account
        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.typeText(email), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.editTextRepeatPassword))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click())

        waitALittle()//need to wait for profile to load

        // press stay safe button if it exists
        try {
            Espresso.onView(ViewMatchers.withId(R.id.staySafeButton)).check(matches(isDisplayed()))
                .perform(ViewActions.click())
        } catch (e: NoMatchingViewException) {
            Log.d("Test", "Button with ID R.id.staySafeButton not found. Skipping this step.")
        }

        waitALittle()

        //go to -> profile -> settings
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())

        waitALittle()

        Espresso.onView(ViewMatchers.withText(R.string.profile_button_settings))
            .perform(ViewActions.click())

        waitALittle()

        //select delete account from settings
        Espresso.onView(ViewMatchers.withText(R.string.delete_account_button))
            .perform(ViewActions.click())

        //delete account
        Espresso.onView(ViewMatchers.withId(R.id.deleteAccountPassword))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())

        waitALittle()

        Espresso.onView(ViewMatchers.withId(R.id.confirmButton))
            .perform(ViewActions.click())

        waitALittle()
    }

    private fun waitALittle() {
        Thread.sleep(1000)
    }
}
