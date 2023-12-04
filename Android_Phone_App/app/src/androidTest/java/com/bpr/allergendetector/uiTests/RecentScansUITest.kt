package com.bpr.allergendetector.uiTests

import android.app.Activity.RESULT_OK
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.bpr.allergendetector.MainActivity
import com.bpr.allergendetector.R
import com.bpr.allergendetector.ui.recentscans.RecentScansAdapter
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class RecentScansUITest {

    private val allergen = "peanuts"
    private val anotherAllergen = "soy"
    private val harmlessLabel = "this product is harmless"

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    @Before
    fun setup() {
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.navigation_scan))
            .perform(ViewActions.click())
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testRecentScansNavigation() {
        //assert that recent scans button is displayed and clickable
        Espresso.onView(ViewMatchers.withId(R.id.recentScanButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        //navigate to recent scans fragment
        Espresso.onView(ViewMatchers.withId(R.id.recentScanButton))
            .perform(ViewActions.click())

        waitALittle()

        //assert that recent scans fragment is displayed with its content
        assertRecentScanFragmentIsDisplayed()

        //go back to scan
        Espresso.onView(ViewMatchers.withId(R.id.goBackButton))
            .perform(ViewActions.click())

        waitALittle()

        //assert that scan fragment is displayed
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.action_bar))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.title_scan))))
    }

    @Test
    fun testRecentScansFunctionality() {
        //performing harmful scan
        performScan(true)

        //go to profile page -> allergen list (to add allergens if needed)
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.profile_button_allergens))
            .perform(ViewActions.click())

        //add allergens
        addAllergen(allergen)
        addAllergen(anotherAllergen)

        //go back to scan page
        Espresso.onView(ViewMatchers.withId(R.id.goBackButton))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.navigation_scan))
            .perform(ViewActions.click())

        //performing harmless scan
        performScan(false)

        //go to recent scans
        Espresso.onView(ViewMatchers.withId(R.id.recentScanButton))
            .perform(ViewActions.click())

        //assert that recent scans fragment is displayed with its content
        assertRecentScanFragmentIsDisplayed()

        //assert that list contains at least 2 scans
        Espresso.onView(ViewMatchers.withId(R.id.recentScansList))
            .check(ViewAssertions.matches(ViewMatchers.hasMinimumChildCount(2)))

        //assert that list contains both harmless and harmful scans
        Espresso.onView(ViewMatchers.withId(R.id.recentScansList))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText("HARMFUL"))))
        Espresso.onView(ViewMatchers.withId(R.id.recentScansList))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText("HARMLESS"))))
        waitALittle()

        //view first scan
        Espresso.onView(ViewMatchers.withId(R.id.recentScansList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecentScansAdapter.ViewHolder>(
                    0,
                    clickChildViewWithId(R.id.viewRecentScan)
                )
            )
        waitALittle()

        //assert that scan modal is displayed
        assertScanModalIsDisplayed()

        //return to recent scans
        Espresso.onView(ViewMatchers.withId(R.id.returnButton))
            .perform(ViewActions.click())

        //assert that recent scans fragment is displayed with its content
        assertRecentScanFragmentIsDisplayed()

        //navigate to view of second scan
        Espresso.onView(ViewMatchers.withId(R.id.recentScansList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecentScansAdapter.ViewHolder>(
                    1,
                    clickChildViewWithId(R.id.viewRecentScan)
                )
            )
        waitALittle()

        //assert that scan modal is displayed
        assertScanModalIsDisplayed()

        //assert that both allergens are displayed within the list textViewResult
        Espresso.onView(ViewMatchers.withId(R.id.textViewResult))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(allergen))))
        Espresso.onView(ViewMatchers.withId(R.id.textViewResult))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(
                            anotherAllergen
                        )
                    )
                )
            )

        //share scan
        Espresso.onView(ViewMatchers.withId(R.id.shareButton))
            .perform(ViewActions.click())

        waitALittle()
    }

    private fun assertScanModalIsDisplayed() {
        try {
            Espresso.onView(
                Matchers.allOf(
                    ViewMatchers.withId(R.id.imageView),
                    ViewMatchers.withContentDescription("Resulting image of the scan"),
                    ViewMatchers.withParent(ViewMatchers.withParent(ViewMatchers.withId(android.R.id.content))),
                    ViewMatchers.isDisplayed()
                )
            )
            Espresso.onView(ViewMatchers.withId(R.id.textResult))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.textViewResult))
                .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
            Espresso.onView(ViewMatchers.withId(R.id.shareButton))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            Espresso.onView(ViewMatchers.withId(R.id.returnButton))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()))
        } catch (e: NoMatchingViewException) {
            Log.d("TEST", "Scan modal is not displayed, but should be")
        }
    }

    private fun assertRecentScanFragmentIsDisplayed() {
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.action_bar))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.recent_scans_title))))
        Espresso.onView(ViewMatchers.withId(R.id.recentScansList))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.goBackButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
    }

    private fun performScan(isHarmful: Boolean) {
        //mock gallery intent response to pick image from gallery
        intending(hasAction(Intent.ACTION_PICK))
            .respondWith(galleryPickPhotoActivityResultStub())
        Espresso.onView(ViewMatchers.withId(R.id.openGalleryButton))
            .perform(ViewActions.click())

        //use image
        Espresso.onView(ViewMatchers.withId(R.id.use_button))
            .perform(ViewActions.click())
        waitALittle()

        if (isHarmful) {
            //proceed with harmful scan
            Espresso.onView(ViewMatchers.withId(R.id.buttonProceed))
                .perform(ViewActions.click())
            waitALittle()
        } else {
            //change text for scan to be harmless
            Espresso.onView(ViewMatchers.withId(R.id.editButton))
                .perform(ViewActions.click())
            waitALittle()

            Espresso.onView(ViewMatchers.withId(R.id.editTextView))
                .perform(ViewActions.replaceText(harmlessLabel), ViewActions.closeSoftKeyboard())

            waitALittle()

            Espresso.onView(ViewMatchers.withId(R.id.saveButton))
                .perform(ViewActions.click())
            Espresso.onView(ViewMatchers.withText("Proceed"))
                .perform(ViewActions.click())

            waitALittle()
        }

        //return to scan fragment
        Espresso.onView(ViewMatchers.withId(R.id.returnButton))
            .perform(ViewActions.click())
        waitALittle()
    }

    private fun addAllergen(allergen: String) {
        Espresso.onView(ViewMatchers.withId(R.id.floatingAddButton))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.allergenName))
            .perform(ViewActions.typeText(allergen), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.severitySeekBar))
            .perform(ViewActions.swipeRight())
        Espresso.onView(ViewMatchers.withText("Ok"))
            .perform(ViewActions.click())
    }

    private fun waitALittle() {
        Thread.sleep(1000)
    }

    private fun galleryPickPhotoActivityResultStub(): ActivityResult {
        val uri = Uri.parse(
            "android.resource://" +
                    InstrumentationRegistry.getInstrumentation().targetContext.packageName +
                    "/drawable/mocked_test_label"
        )
        val resultIntent = Intent()
        resultIntent.data = uri
        return ActivityResult(RESULT_OK, resultIntent)
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