package com.bpr.allergendetector

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.bpr.allergendetector.ui.profile.ProfileButtonAdapter
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private val timeForVisibility = Thread.sleep(1000)

@LargeTest
@RunWith(AndroidJUnit4::class)
class AllergenListUITest {

    @get:Rule
    val fragmentRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    @Before
    fun setUp() {
        //go to profile page
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())

        //go to allergen list
        Espresso.onView(ViewMatchers.withId(R.id.profileButtonList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ProfileButtonAdapter.ButtonViewHolder>(
                0,
                ViewActions.click()
            )
        )
    }

    @Test
    fun addAllergenTest() {
        val allergen = "test allergen"

        Espresso.onView(ViewMatchers.withId(R.id.floatingAddButton))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.allergenName))
            .perform(ViewActions.typeText(allergen), ViewActions.closeSoftKeyboard())

        timeForVisibility

        //pick 2 at severity from seekBar
        Espresso.onView(ViewMatchers.withId(R.id.severitySeekBar))
            .perform(ViewActions.swipeRight())

        timeForVisibility

        //press ok
        Espresso.onView(ViewMatchers.withText("Ok"))
            .perform(ViewActions.click())

        timeForVisibility
    }

    @Test
    fun addedAllergenEditTest() {
        val allergen = "another test allergen"

        Espresso.onView(ViewMatchers.withId(R.id.allergen_edit))
            .perform(ViewActions.click())

        timeForVisibility

        Espresso.onView(ViewMatchers.withId(R.id.allergenName))
            .perform(ViewActions.replaceText(allergen), ViewActions.closeSoftKeyboard())

        timeForVisibility

        Espresso.onView(ViewMatchers.withText("Cancel"))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.allergen_edit))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.allergenName))
            .perform(ViewActions.replaceText(allergen), ViewActions.closeSoftKeyboard())

        timeForVisibility

        //pick 2 at severity from seekBar
        Espresso.onView(ViewMatchers.withId(R.id.severitySeekBar))
            .perform(ViewActions.slowSwipeLeft())

        timeForVisibility

        Espresso.onView(ViewMatchers.withText("Ok"))
            .perform(ViewActions.click())

        timeForVisibility
    }

    @Test
    fun removeAllergenTest() {
        timeForVisibility

        Espresso.onView(ViewMatchers.withId(R.id.allergen_delete))
            .perform(ViewActions.click())

        timeForVisibility

        Espresso.onView(ViewMatchers.withText("Yes"))
            .perform(ViewActions.click())

        timeForVisibility
    }


}
