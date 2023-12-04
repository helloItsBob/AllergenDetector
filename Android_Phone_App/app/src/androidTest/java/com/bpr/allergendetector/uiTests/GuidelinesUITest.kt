package com.bpr.allergendetector.uiTests

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.bpr.allergendetector.MainActivity
import com.bpr.allergendetector.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class GuidelinesUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    @Before
    fun setup() {
        Espresso.onView(ViewMatchers.withId(R.id.navigation_scan))
            .perform(ViewActions.click())
    }

    @Test
    fun testGuidelinesFragment() {
        //assert that scan fragment is displayed
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.action_bar))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.title_scan))))

        //assert that guidelines button is displayed and clickable
        Espresso.onView(ViewMatchers.withId(R.id.showHintButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        //navigate to guidelines fragment
        Espresso.onView(ViewMatchers.withId(R.id.showHintButton))
            .perform(ViewActions.click())

        waitALittle()

        //assert that guidelines fragment is displayed
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.action_bar))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.guidelines_title))))

        //assert fragment content is displayed
        Espresso.onView(ViewMatchers.withId(R.id.welcomeMessage))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.appLogo))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //assert that textViews are displayed with correct text
        val arrayOfTextViews = arrayOf(R.id.guideline1, R.id.guideline2, R.id.guideline3)
        val arrayOfStrings =
            arrayOf(R.string.guideline_1, R.string.guideline_2, R.string.guideline_3)
        for (i in arrayOfTextViews.indices) {
            Espresso.onView(ViewMatchers.withId(arrayOfTextViews[i]))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.withText(arrayOfStrings[i])))
        }

        //assert that scrollView is displayed and scrollable
        Espresso.onView(ViewMatchers.withId(R.id.guidelinesScrollView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .perform(ViewActions.swipeUp())

        //assert that arrows are displayed
        val arrayOfArrowImageViews = arrayOf(
            R.id.arrow1,
            R.id.arrow2,
            R.id.arrow1ToLists,
            R.id.arrow2ToLists,
            R.id.arrow3ToLists,
            R.id.arrowToButton
        )
        for (i in arrayOfArrowImageViews.indices) {
            Espresso.onView(ViewMatchers.withId(arrayOfArrowImageViews[i]))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }

        //assert that numbers are displayed
        val arrayOfNumbers = arrayOf(R.id.number1, R.id.number2, R.id.number3)
        for (i in arrayOfNumbers.indices) {
            Espresso.onView(ViewMatchers.withId(arrayOfNumbers[i]))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.withText((i + 1).toString())))
        }

        //assert that "Stay Safe" button is displayed and clickable
        Espresso.onView(ViewMatchers.withId(R.id.staySafeButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        //navigate back to scan fragment
        Espresso.onView(ViewMatchers.withId(R.id.staySafeButton))
            .perform(ViewActions.click())

        //assert that scan fragment is displayed
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.action_bar))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.title_scan))))

        waitALittle()
    }

    private fun waitALittle() {
        Thread.sleep(1000)
    }
}