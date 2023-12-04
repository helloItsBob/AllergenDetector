package com.bpr.allergendetector

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.bpr.allergendetector.ui.allergenlist.AllergenRecyclerViewAdapter
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AllergenListUITest {

    private val allergen = "test allergen"
    private val anotherAllergen = "another test allergen"

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
        //assert that allergen name is not used
        Espresso.onView(ViewMatchers.withText(allergen))
            .check(ViewAssertions.doesNotExist())

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

        //assert that allergen is added
        Espresso.onView(ViewMatchers.withText(allergen))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        waitALittle()
    }

    @Test
    fun addedAllergenEditTest() {

        //assert that allergen name is not used
        Espresso.onView(ViewMatchers.withText(anotherAllergen))
            .check(ViewAssertions.doesNotExist())

        //press edit button
        Espresso.onView(ViewMatchers.withId(R.id.allergen_list))
            .perform(
                RecyclerViewActions.actionOnItem<AllergenRecyclerViewAdapter.ViewHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText(allergen)),
                    clickChildViewWithId(R.id.allergen_edit)
                )
            )

        waitALittle()

        //enter allergen name
        Espresso.onView(ViewMatchers.withId(R.id.allergenName))
            .perform(ViewActions.replaceText(anotherAllergen), ViewActions.closeSoftKeyboard())

        waitALittle()

        //cancel edit to see if allergen name is still the same afterwards
        Espresso.onView(ViewMatchers.withText("Cancel"))
            .perform(ViewActions.click())

        //assert that allergen name is not changed
        Espresso.onView(ViewMatchers.withText(anotherAllergen))
            .check(ViewAssertions.doesNotExist())

        //press edit button again
        Espresso.onView(ViewMatchers.withId(R.id.allergen_list))
            .perform(
                RecyclerViewActions.actionOnItem<AllergenRecyclerViewAdapter.ViewHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText(allergen)),
                    clickChildViewWithId(R.id.allergen_edit)
                )
            )

        waitALittle()

        //enter allergen name
        Espresso.onView(ViewMatchers.withId(R.id.allergenName))
            .perform(ViewActions.replaceText(anotherAllergen), ViewActions.closeSoftKeyboard())

        waitALittle()

        //swipe on severity bar to the left
        Espresso.onView(ViewMatchers.withId(R.id.severitySeekBar))
            .perform(ViewActions.slowSwipeLeft())

        waitALittle()

        //apply changes
        Espresso.onView(ViewMatchers.withText("Ok"))
            .perform(ViewActions.click())

        //assert that allergen name is changed
        Espresso.onView(ViewMatchers.withText(anotherAllergen))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        waitALittle()
    }

    @Test
    fun shareAllergensTest() {
        //assert that share button is displayed and clickable
        Espresso.onView(ViewMatchers.withId(R.id.floatingShareButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        //press share button
        Espresso.onView(ViewMatchers.withId(R.id.floatingShareButton))
            .perform(ViewActions.click())

        waitALittle()
    }

    @Test
    fun removeAllergenTest() {
        //assert that allergen is displayed
        Espresso.onView(ViewMatchers.withText(anotherAllergen))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        waitALittle()

        //press delete button
        Espresso.onView(ViewMatchers.withId(R.id.allergen_list))
            .perform(
                RecyclerViewActions.actionOnItem<AllergenRecyclerViewAdapter.ViewHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText(anotherAllergen)),
                    clickChildViewWithId(R.id.allergen_delete)
                )
            )

        waitALittle()

        //cancel delete
        Espresso.onView(ViewMatchers.withText("No"))
            .perform(ViewActions.click())

        waitALittle()

        //assert that allergen is still displayed
        Espresso.onView(ViewMatchers.withText(anotherAllergen))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //press delete button
        Espresso.onView(ViewMatchers.withId(R.id.allergen_list))
            .perform(
                RecyclerViewActions.actionOnItem<AllergenRecyclerViewAdapter.ViewHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText(anotherAllergen)),
                    clickChildViewWithId(R.id.allergen_delete)
                )
            )

        waitALittle()

        //confirm delete
        Espresso.onView(ViewMatchers.withText("Yes"))
            .perform(ViewActions.click())

        //assert that allergen is not displayed anymore
        Espresso.onView(ViewMatchers.withText(anotherAllergen))
            .check(ViewAssertions.doesNotExist())

        waitALittle()
    }

    private fun waitALittle() {
        Thread.sleep(1000)
    }

    private fun clickChildViewWithId(id: Int): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController?, view: View) {
                val v: View = view.findViewById(id)
                v.performClick()
            }
        }
    }
}
