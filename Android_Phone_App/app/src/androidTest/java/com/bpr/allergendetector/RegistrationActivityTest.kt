package com.bpr.allergendetector

import android.content.Context
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.bpr.allergendetector.ui.profile.ProfileButtonAdapter
import com.bpr.allergendetector.ui.settings.SettingsButtonAdapter
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class RegistrationActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RegistrationActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA,
            android.Manifest.permission.INTERNET)

    @Test
    fun registrationFailureTest() {
        // TODO

        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click())
    }
    @Test
    fun registrationAndDeleteAccountTest() {
        val email = "MockedTest@gmail.com"
        val password = "paSSword123!"

        Espresso.onView(ViewMatchers.withId(R.id.editTextEmail))
            .perform(ViewActions.typeText(email), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.editTextPassword))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.editTextRepeatPassword))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.buttonRegister))
            .perform(ViewActions.click())

        Thread.sleep(1000) //need to wait for profile to load

        //go to profile page
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())

        //click button #3 from profileButtonList
        Espresso.onView(ViewMatchers.withId(R.id.profileButtonList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ProfileButtonAdapter.ButtonViewHolder>(
                3,
                ViewActions.click()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.profileButtonList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ProfileButtonAdapter.ButtonViewHolder>(
                4,
                ViewActions.click()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.settingsButtons)).perform(
            RecyclerViewActions.actionOnItemAtPosition<SettingsButtonAdapter.CustomViewHolder>(
                4,
                ViewActions.click()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.deleteAccountPassword))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())

        Espresso.onView(ViewMatchers.withId(R.id.confirmButton))
            .perform(ViewActions.click())

        Thread.sleep(1000)
        //in the end get internal error in Cloud FireStore, but account is deleted...
    }
}
