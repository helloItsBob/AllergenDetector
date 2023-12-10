package com.bpr.allergendetector.uiTests

import android.app.Activity.RESULT_OK
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
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
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class ScanUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun harmfulScanResultTest() {
        val allergen = "peanuts"
        val anotherAllergen = "soy"

        //go to profile page -> allergen list
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.profile_button_allergens))
            .perform(ViewActions.click())

        //add allergens
        Espresso.onView(ViewMatchers.withId(R.id.floatingAddButton))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.allergenName))
            .perform(ViewActions.typeText(allergen), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.severitySeekBar))
            .perform(ViewActions.swipeRight())
        Espresso.onView(ViewMatchers.withText("Ok"))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.floatingAddButton))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.allergenName))
            .perform(ViewActions.typeText(anotherAllergen), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withText("Ok"))
            .perform(ViewActions.click())

        waitALittle()

        //go back to scan page
        Espresso.onView(ViewMatchers.withId(R.id.goBackButton))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.navigation_scan))
            .perform(ViewActions.click())

        waitALittle()

        //mock gallery intent response and pick image from gallery
        intending(hasAction(Intent.ACTION_PICK))
            .respondWith(galleryPickPhotoActivityResultStub())
        Espresso.onView(ViewMatchers.withId(R.id.openGalleryButton))
            .perform(ViewActions.click())

        waitALittle()

        //use image
        Espresso.onView(ViewMatchers.withId(R.id.use_button))
            .perform(ViewActions.click())

        waitALittle()

        //proceed without editing text
        Espresso.onView(ViewMatchers.withId(R.id.buttonProceed))
            .perform(ViewActions.click())
        waitALittle()

        //assert that allergens are detected and displayed
        Espresso.onView(ViewMatchers.withText(allergen))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(anotherAllergen))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //return to scan fragment
        Espresso.onView(ViewMatchers.withId(R.id.returnButton))
            .perform(ViewActions.click())

        waitALittle()
    }

    @Test
    fun harmlessScanEditResultTest() {
        val harmlessLabel = "There is no spoon"

        //mock gallery intent response and pick image from gallery
        intending(hasAction(Intent.ACTION_PICK))
            .respondWith(galleryPickPhotoActivityResultStub())
        Espresso.onView(ViewMatchers.withId(R.id.openGalleryButton))
            .perform(ViewActions.click())

        waitALittle()

        //use image
        Espresso.onView(ViewMatchers.withId(R.id.use_button))
            .perform(ViewActions.click())

        waitALittle()

        //proceed to text edit
        Espresso.onView(ViewMatchers.withId(R.id.editButton))
            .perform(ViewActions.click())

        waitALittle()

        //assert that mocked text is not displayed
        Espresso.onView(ViewMatchers.withText(harmlessLabel))
            .check(ViewAssertions.doesNotExist())

        //edit text
        Espresso.onView(ViewMatchers.withId(R.id.editTextView))
            .perform(ViewActions.replaceText(harmlessLabel), ViewActions.closeSoftKeyboard())

        //assert that text is changed
        Espresso.onView(ViewMatchers.withId(R.id.editTextView))
            .check(ViewAssertions.matches(ViewMatchers.withText(harmlessLabel)))

        waitALittle()

        //confirm changes
        Espresso.onView(ViewMatchers.withId(R.id.saveButton))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Proceed"))
            .perform(ViewActions.click())

        waitALittle()

        //return to scan fragment
        Espresso.onView(ViewMatchers.withId(R.id.returnButton))
            .perform(ViewActions.click())

        waitALittle()
    }

    @Test
    fun cancelEditResultTest() {
        val harmlessLabel = "I'm batman"

        //mock gallery intent response and pick image from gallery
        intending(hasAction(Intent.ACTION_PICK))
            .respondWith(galleryPickPhotoActivityResultStub())
        Espresso.onView(ViewMatchers.withId(R.id.openGalleryButton))
            .perform(ViewActions.click())

        waitALittle()

        //use image
        Espresso.onView(ViewMatchers.withId(R.id.use_button))
            .perform(ViewActions.click())

        waitALittle()

        //proceed to text edit
        Espresso.onView(ViewMatchers.withId(R.id.editButton))
            .perform(ViewActions.click())

        waitALittle()

        //assert that mocked text is not displayed
        Espresso.onView(ViewMatchers.withText(harmlessLabel))
            .check(ViewAssertions.doesNotExist())

        //edit text
        Espresso.onView(ViewMatchers.withId(R.id.editTextView))
            .perform(ViewActions.replaceText(harmlessLabel), ViewActions.closeSoftKeyboard())

        //assert that text is changed
        Espresso.onView(ViewMatchers.withId(R.id.editTextView))
            .check(ViewAssertions.matches(ViewMatchers.withText(harmlessLabel)))

        waitALittle()

        //go back but refuse to do it
        Espresso.onView(ViewMatchers.withId(R.id.backButton))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("No"))
            .perform(ViewActions.click())
        //Close keyboard, as it opens automatically
        Espresso.closeSoftKeyboard()

        waitALittle()

        //go back without saving changes
        Espresso.onView(ViewMatchers.withId(R.id.backButton))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText("Yes"))
            .perform(ViewActions.click())

        waitALittle()

        //assert that mocked text is not displayed after canceling edit
        Espresso.onView(ViewMatchers.withText(harmlessLabel))
            .check(ViewAssertions.doesNotExist())

        waitALittle()
    }

    @Test
    fun pickImageFromGallery() {
        //mock gallery intent response
        intending(hasAction(Intent.ACTION_PICK))
            .respondWith(galleryPickPhotoActivityResultStub())

        //pick image from gallery
        Espresso.onView(ViewMatchers.withId(R.id.openGalleryButton))
            .perform(ViewActions.click())

        waitALittle()

        //use image
        Espresso.onView(ViewMatchers.withId(R.id.use_button))
            .perform(ViewActions.click())

        waitALittle()

        //check if image is displayed with edit and proceed buttons
        Espresso.onView(ViewMatchers.withId(R.id.imageView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.buttonProceed))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.editButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        waitALittle()
    }

    @Test
    fun imageRetakeTest() {
        //perform scan
        Espresso.onView(ViewMatchers.withId(R.id.image_capture_button))
            .perform(ViewActions.click())
        waitALittle()

        //retake image
        Espresso.onView(ViewMatchers.withId(R.id.retake_button))
            .perform(ViewActions.click())
        waitALittle()

        //check if image capture button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.image_capture_button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        waitALittle()
    }

    @Test
    fun imageNoTextFoundTest() {
        //perform scan
        Espresso.onView(ViewMatchers.withId(R.id.image_capture_button))
            .perform(ViewActions.click())
        waitALittle()

        //proceed while no text found
        Espresso.onView(ViewMatchers.withId(R.id.use_button))
            .perform(ViewActions.click())
        waitALittle()

        //check if retake & crop buttons are still displayed
        Espresso.onView(ViewMatchers.withId(R.id.cropButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.retake_button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        waitALittle()
    }

    @Test
    fun shareScanResultsTest() {
        //mock gallery intent response and pick image from gallery
        intending(hasAction(Intent.ACTION_PICK))
            .respondWith(galleryPickPhotoActivityResultStub())
        Espresso.onView(ViewMatchers.withId(R.id.openGalleryButton))
            .perform(ViewActions.click())

        waitALittle()

        //use image
        Espresso.onView(ViewMatchers.withId(R.id.use_button))
            .perform(ViewActions.click())

        waitALittle()
        //proceed without editing text
        Espresso.onView(ViewMatchers.withId(R.id.buttonProceed))
            .perform(ViewActions.click())

        waitALittle()

        //assert that share button is displayed and clickable
        Espresso.onView(ViewMatchers.withId(R.id.shareButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        //press share button
        Espresso.onView(ViewMatchers.withId(R.id.shareButton))
            .perform(ViewActions.click())

        waitALittle()
    }

    @Test
    fun textToSpeechTest() {
        //go to profile page -> settings
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.profile_button_settings))
            .perform(ViewActions.click())

        //enable text to speech
        Espresso.onView(ViewMatchers.withId(R.id.textToSpeechSwitch))
            .perform(ViewActions.click())

        //go back to scan page
        Espresso.onView(ViewMatchers.withId(R.id.navigation_scan))
            .perform(ViewActions.click())

        //mock gallery intent response and pick image from gallery
        intending(hasAction(Intent.ACTION_PICK))
            .respondWith(galleryPickPhotoActivityResultStub())
        Espresso.onView(ViewMatchers.withId(R.id.openGalleryButton))
            .perform(ViewActions.click())

        waitALittle()

        //use image
        Espresso.onView(ViewMatchers.withId(R.id.use_button))
            .perform(ViewActions.click())

        waitALittle()

        //proceed without editing text
        Espresso.onView(ViewMatchers.withId(R.id.buttonProceed))
            .perform(ViewActions.click())
        waitALittle()

        //assert that text to speech button is displayed and clickable
        Espresso.onView(ViewMatchers.withId(R.id.textToSpeechButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        //press text to speech button
        Espresso.onView(ViewMatchers.withId(R.id.textToSpeechButton))
            .perform(ViewActions.click())

        waitALittle()

        //return to scan fragment
        Espresso.onView(ViewMatchers.withId(R.id.returnButton))
            .perform(ViewActions.click())

        waitALittle()

        //go back to settings
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())

        //disable text to speech
        Espresso.onView(ViewMatchers.withId(R.id.textToSpeechSwitch))
            .perform(ViewActions.click())

        waitALittle()
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

}
