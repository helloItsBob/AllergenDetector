package com.bpr.allergendetector.uiTests

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.bpr.allergendetector.MainActivity
import com.bpr.allergendetector.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class StatisticsUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    @Before
    fun setUp() {
        Intents.init()
        //go to profile page -> statistics
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())
        //assert that statistics button is displayed"
        Espresso.onView(ViewMatchers.withId(R.id.profileButtonList))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.profile_button_statistics))))
        //proceed to statistics
        Espresso.onView(ViewMatchers.withText(R.string.profile_button_statistics))
            .perform(ViewActions.click())
        //assert that statistics fragment is displayed
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.action_bar))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.profile_button_statistics))))
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testGoBackToProfilePage() {
        //assert that go back button is displayed and clickable
        Espresso.onView(ViewMatchers.withId(R.id.backButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        //click on go back button
        Espresso.onView(ViewMatchers.withId(R.id.backButton))
            .perform(ViewActions.click())

        //assert that profile page is displayed with profile image and buttons
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.action_bar))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.title_profile))))
        Espresso.onView(ViewMatchers.withId(R.id.profileImage))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.profileButtonList))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        waitALittle()
    }

    @Test
    fun testStatisticsFunctionality() {
        //assert that statistics fragment content is displayed (both charts and button to button to change week/year view)
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.action_bar))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.profile_button_statistics))))
        Espresso.onView(ViewMatchers.withId(R.id.weekYearButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
        Espresso.onView(ViewMatchers.withId(R.id.barChart))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.lineChart))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.backButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        waitALittle()

        //switching to year view by pressing the button
        Espresso.onView(ViewMatchers.withId(R.id.weekYearButton))
            .perform(ViewActions.click())

        //assert that visuals are displayed
        Espresso.onView(ViewMatchers.withId(R.id.barChart))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.lineChart))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        waitALittle()
    }

    private fun waitALittle() {
        Thread.sleep(1000)
    }
}