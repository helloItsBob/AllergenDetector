package com.bpr.allergendetector

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AllergenListUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    @Before
    fun setUp() {
        //go to profile page
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())

        //go to allergen list
        Espresso.onView(ViewMatchers.withText(R.string.profile_button_allergens))
            .perform(ViewActions.click())
    }

    @Test
    fun addAllergenTest() {
        val allergen = "test allergen"

        //press add button
        Espresso.onView(ViewMatchers.withId(R.id.floatingAddButton))
            .perform(ViewActions.click())

        //enter allergen name
        Espresso.onView(ViewMatchers.withId(R.id.allergenName))
            .perform(ViewActions.typeText(allergen), ViewActions.closeSoftKeyboard())

        waitALittle()

        //swipe on severity bar
        Espresso.onView(ViewMatchers.withId(R.id.severitySeekBar))
            .perform(ViewActions.swipeRight())

        waitALittle()

        //press ok
        Espresso.onView(ViewMatchers.withText("Ok"))
            .perform(ViewActions.click())

        waitALittle()
    }

    @Test
    fun addedAllergenEditTest() {
        val allergen = "another test allergen"

        //press edit button
        Espresso.onView(ViewMatchers.withId(R.id.allergen_edit))
            .perform(ViewActions.click())

        waitALittle()

        //enter allergen name
        Espresso.onView(ViewMatchers.withId(R.id.allergenName))
            .perform(ViewActions.replaceText(allergen), ViewActions.closeSoftKeyboard())

        waitALittle()

        //cancel edit to see if allergen name is still the same afterwards
        Espresso.onView(ViewMatchers.withText("Cancel"))
            .perform(ViewActions.click())

        //press edit button again
        Espresso.onView(ViewMatchers.withId(R.id.allergen_edit))
            .perform(ViewActions.click())

        //enter allergen name
        Espresso.onView(ViewMatchers.withId(R.id.allergenName))
            .perform(ViewActions.replaceText(allergen), ViewActions.closeSoftKeyboard())

        waitALittle()

        //swipe on severity bar to the left
        Espresso.onView(ViewMatchers.withId(R.id.severitySeekBar))
            .perform(ViewActions.slowSwipeLeft())

        waitALittle()

        //apply changes
        Espresso.onView(ViewMatchers.withText("Ok"))
            .perform(ViewActions.click())

        waitALittle()
    }

    @Test
    fun shareAllergensTest() {
        //press share button
        Espresso.onView(ViewMatchers.withId(R.id.floatingShareButton))
            .perform(ViewActions.click())

        waitALittle()
    }

    @Test
    fun removeAllergenTest() {
        waitALittle()

        //press delete button
        Espresso.onView(ViewMatchers.withId(R.id.allergen_delete))
            .perform(ViewActions.click())

        waitALittle()

        //confirm delete
        Espresso.onView(ViewMatchers.withText("Yes"))
            .perform(ViewActions.click())

        waitALittle()
    }

    private fun waitALittle() {
        Thread.sleep(1000)
    }

}
