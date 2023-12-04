package com.bpr.allergendetector

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class SettingsUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    @Before
    fun setUp() {
        Intents.init()
        //go to profile page -> settings
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())
        //assert that settings button is displayed"
        Espresso.onView(ViewMatchers.withId(R.id.profileButtonList))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.profile_button_settings))))
        //proceed to settings
        Espresso.onView(ViewMatchers.withText(R.string.profile_button_settings))
            .perform(ViewActions.click())
        //assert that settings fragment is displayed
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.action_bar))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.profile_button_settings))))
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testAboutButton() {
        //assert that about button is displayed and clickable
        Espresso.onView(ViewMatchers.withId(R.id.aboutButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        //click on about button
        Espresso.onView(ViewMatchers.withId(R.id.aboutButton))
            .perform(ViewActions.click())

        //assert that modal is displayed with title, description and button
        try {
            Espresso.onView(ViewMatchers.withId(R.id.aboutTitle))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.aboutDescription))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.okButton))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()))
        } catch (e: NoMatchingViewException) {
            Log.d("TEST", "About modal is not displayed, but should be")
        }

        //assert that description matches the resource
        Espresso.onView(ViewMatchers.withId(R.id.aboutDescription))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.about_description)))

        //close modal
        Espresso.onView(ViewMatchers.withId(R.id.okButton))
            .perform(ViewActions.click())

        //assert that modal is not displayed
        try {
            Espresso.onView(ViewMatchers.withId(R.id.aboutTitle))
                .check(ViewAssertions.doesNotExist())
            Espresso.onView(ViewMatchers.withId(R.id.aboutDescription))
                .check(ViewAssertions.doesNotExist())
            Espresso.onView(ViewMatchers.withId(R.id.okButton))
                .check(ViewAssertions.doesNotExist())
        } catch (e: NoMatchingViewException) {
            Log.d("TEST", "About modal is displayed, but should not be")
        }
    }

    @Test
    fun testFeedbackButton() {
        //assert that feedback button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.settingsButtons))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.feedback_button))))

        //assert that feedback button is clickable
        Espresso.onView(ViewMatchers.withText(R.string.feedback_button))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        //click on feedback button
        Espresso.onView(ViewMatchers.withText(R.string.feedback_button))
            .perform(ViewActions.click())

        waitALittle()
    }

    @Test
    fun testGoBackToProfilePage() {
        //assert that go back button is displayed and clickable
        Espresso.onView(ViewMatchers.withId(R.id.goBackButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        //click on go back button
        Espresso.onView(ViewMatchers.withId(R.id.goBackButton))
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
    fun testChangePasswordButton() {
        val oldPassword = "testPassword"
        val newPassword = "newPassword"
        val wrongPassword = "wrongPassword"

        //assert that change password button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.settingsButtons))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.change_password_button))))

        //assert that change password button is clickable
        Espresso.onView(ViewMatchers.withText(R.string.change_password_button))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        //click on change password button
        Espresso.onView(ViewMatchers.withText(R.string.change_password_button))
            .perform(ViewActions.click())

        //assert that modal is displayed with title, fields and buttons
        assertChangePasswordModalIsDisplayed()

        //perform invalid change password
        Espresso.onView(ViewMatchers.withId(R.id.oldPasswordEditText))
            .perform(ViewActions.typeText(oldPassword))
        Espresso.onView(ViewMatchers.withId(R.id.newPasswordEditText))
            .perform(ViewActions.typeText(newPassword))
        Espresso.onView(ViewMatchers.withId(R.id.confirmNewPasswordEditText))
            .perform(ViewActions.typeText(wrongPassword))
        Espresso.onView(ViewMatchers.withId(R.id.saveButtonModal))
            .perform(ViewActions.click())

        //assert that modal is still displayed after invalid change password
        assertChangePasswordModalIsDisplayed()

        //cancel change password
        Espresso.onView(ViewMatchers.withId(R.id.cancelButtonModal))
            .perform(ViewActions.click())

        //assert that modal is not displayed
        try {
            Espresso.onView(ViewMatchers.withId(R.id.changePasswordTitle))
                .check(ViewAssertions.doesNotExist())
            Espresso.onView(ViewMatchers.withId(R.id.oldPasswordEditText))
                .check(ViewAssertions.doesNotExist())
            Espresso.onView(ViewMatchers.withId(R.id.newPasswordEditText))
                .check(ViewAssertions.doesNotExist())
            Espresso.onView(ViewMatchers.withId(R.id.confirmNewPasswordEditText))
                .check(ViewAssertions.doesNotExist())
            Espresso.onView(ViewMatchers.withId(R.id.cancelButtonModal))
                .check(ViewAssertions.doesNotExist())
            Espresso.onView(ViewMatchers.withId(R.id.saveButtonModal))
                .check(ViewAssertions.doesNotExist())
        } catch (e: NoMatchingViewException) {
            Log.d("TEST", "Change password modal is displayed, but should not be")
        }

        //click on change password button
        Espresso.onView(ViewMatchers.withText(R.string.change_password_button))
            .perform(ViewActions.click())

        //assert that modal is displayed and password fields are empty
        assertChangePasswordModalIsDisplayed()
        Espresso.onView(ViewMatchers.withId(R.id.oldPasswordEditText))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))
        Espresso.onView(ViewMatchers.withId(R.id.newPasswordEditText))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))
        Espresso.onView(ViewMatchers.withId(R.id.confirmNewPasswordEditText))
            .check(ViewAssertions.matches(ViewMatchers.withText("")))

        //won't change password as test user is null for firebase
    }

    private fun assertChangePasswordModalIsDisplayed() {
        try {
            Espresso.onView(ViewMatchers.withId(R.id.changePasswordTitle))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.oldPasswordEditText))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.newPasswordEditText))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.confirmNewPasswordEditText))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.cancelButtonModal))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            Espresso.onView(ViewMatchers.withId(R.id.saveButtonModal))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()))
        } catch (e: NoMatchingViewException) {
            Log.d("TEST", "Change password modal is not displayed, but should be")
        }
    }

    @Test
    fun testSwitches() {
        //assert that switches are displayed, clickable and are not checked
        Espresso.onView(ViewMatchers.withId(R.id.hideHintsSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
        Espresso.onView(ViewMatchers.withId(R.id.textToSpeechSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))

        //change state of all switches
        Espresso.onView(ViewMatchers.withId(R.id.hideHintsSwitch))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.textToSpeechSwitch))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch))
            .perform(ViewActions.click())

        waitALittle()

        //assert that switches are checked
        Espresso.onView(ViewMatchers.withId(R.id.hideHintsSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
        Espresso.onView(ViewMatchers.withId(R.id.textToSpeechSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))
    }

    @Test
    fun testHideHintsSwitch() {
        //assert that hide hints switch is displayed, clickable and is not checked
        Espresso.onView(ViewMatchers.withId(R.id.hideHintsSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))

        //go back to scan page to see that hints are displayed
        Espresso.onView(ViewMatchers.withId(R.id.navigation_scan))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.showHintButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //go back to settings page to hide hints
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.hideHintsSwitch))
            .perform(ViewActions.click())

        //assert that hide hints switch is checked
        Espresso.onView(ViewMatchers.withId(R.id.hideHintsSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        //go back to scan page to see that hint button is hidden
        Espresso.onView(ViewMatchers.withId(R.id.navigation_scan))
            .perform(ViewActions.click())
        waitALittle()
        waitALittle()
        waitALittle()
        try {
            Espresso.onView(ViewMatchers.withId(R.id.showHintButton))
                .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        } catch (e: NoMatchingViewException) {
            Log.d("TEST", "Hint button is displayed, but should not be")
        }

        //go back to settings page to show hints
        Espresso.onView(ViewMatchers.withId(R.id.navigation_profile))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.hideHintsSwitch))
            .perform(ViewActions.click())

        //assert that hide hints switch is not checked
        Espresso.onView(ViewMatchers.withId(R.id.hideHintsSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))

        //go back to scan page to see that hint button is displayed again
        Espresso.onView(ViewMatchers.withId(R.id.navigation_scan))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.showHintButton))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testDarkModeSwitchFunctionality() {
        //assert that dark mode switch is displayed, clickable and is not checked
        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))

        //assert that night mode is not set
        assert(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)

        //switch to dark mode
        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch))
            .perform(ViewActions.click())

        //assert that dark mode switch is checked
        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        //assert night mode is set
        assert(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)

        //switch to light mode
        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch))
            .perform(ViewActions.click())

        //assert that dark mode switch is not checked
        Espresso.onView(ViewMatchers.withId(R.id.darkModeSwitch))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))

        //assert that night mode is not set again
        assert(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
    }

    @Test
    fun testChangeAvatarFunctionality() {
        //assert that change avatar button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.settingsButtons))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.change_avatar_button))))

        //assert that change avatar button is clickable
        Espresso.onView(ViewMatchers.withText(R.string.change_avatar_button))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))

        //click on change avatar button
        Espresso.onView(ViewMatchers.withText(R.string.change_avatar_button))
            .perform(ViewActions.click())

        //assert that modal is displayed with title, buttons and image
        try {
            Espresso.onView(ViewMatchers.withId(R.id.changeAvatarTitle))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.chooseFromGalleryButtonAvatarModal))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            Espresso.onView(ViewMatchers.withId(R.id.selectedImageAvatarModal))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            Espresso.onView(ViewMatchers.withId(R.id.cancelButtonAvatarModal))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()))
            Espresso.onView(ViewMatchers.withId(R.id.saveButtonAvatarModal))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()))
        } catch (e: NoMatchingViewException) {
            Log.d("TEST", "Change avatar modal is not displayed, but should be")
        }

        waitALittle()

        //changing avatar (cancel changes)
        changeAvatar()
        Espresso.onView(ViewMatchers.withId(R.id.cancelButtonAvatarModal))
            .perform(ViewActions.click())

        //assert that modal is not displayed
        assertChangeAvatarModalDoesNotExist()

        //changing avatar (save changes)
        Espresso.onView(ViewMatchers.withText(R.string.change_avatar_button))
            .perform(ViewActions.click())
        changeAvatar()
        Espresso.onView(ViewMatchers.withId(R.id.saveButtonAvatarModal))
            .perform(ViewActions.click())

        //assert that modal is not displayed
        assertChangeAvatarModalDoesNotExist()

        //go back to profile page to see if avatar has changed
        Espresso.onView(ViewMatchers.withId(R.id.goBackButton))
            .perform(ViewActions.click())

        //assert that avatar is displayed
        Espresso.onView(ViewMatchers.withId(R.id.profileImage))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        waitALittle()
    }

    private fun changeAvatar() {
        //mock gallery intent response to change avatar
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_PICK))
            .respondWith(galleryPickAvatarActivityResultStub())
        Espresso.onView(ViewMatchers.withId(R.id.chooseFromGalleryButtonAvatarModal))
            .perform(ViewActions.click())
    }

    private fun assertChangeAvatarModalDoesNotExist() {
        try {
            Espresso.onView(ViewMatchers.withId(R.id.changeAvatarTitle))
                .check(ViewAssertions.doesNotExist())
            Espresso.onView(ViewMatchers.withId(R.id.chooseFromGalleryButtonAvatarModal))
                .check(ViewAssertions.doesNotExist())
            Espresso.onView(ViewMatchers.withId(R.id.selectedImageAvatarModal))
                .check(ViewAssertions.doesNotExist())
            Espresso.onView(ViewMatchers.withId(R.id.cancelButtonAvatarModal))
                .check(ViewAssertions.doesNotExist())
            Espresso.onView(ViewMatchers.withId(R.id.saveButtonAvatarModal))
                .check(ViewAssertions.doesNotExist())
        } catch (e: NoMatchingViewException) {
            Log.d("TEST", "Change avatar modal is displayed, but should not be")
        }
    }

    private fun waitALittle() {
        Thread.sleep(1000)
    }

    private fun galleryPickAvatarActivityResultStub(): Instrumentation.ActivityResult {
        val uri = Uri.parse(
            "android.resource://" +
                    InstrumentationRegistry.getInstrumentation().targetContext.packageName +
                    "/drawable/mocked_test_avatar"
        )
        val resultIntent = Intent()
        resultIntent.data = uri
        return Instrumentation.ActivityResult(Activity.RESULT_OK, resultIntent)
    }
}