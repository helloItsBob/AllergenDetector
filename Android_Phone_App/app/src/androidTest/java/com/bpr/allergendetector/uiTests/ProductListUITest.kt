package com.bpr.allergendetector.uiTests

import android.app.Activity.RESULT_OK
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.test.espresso.Espresso
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
import com.bpr.allergendetector.ui.lists.ListRecyclerViewAdapter
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class ProductListUITest {

    private val productName = "test product"
    private val productAnotherName = "Edit Test Product"
    private val tag1 = "mocked tag"
    private val tag2 = "test tag"
    private val editTag = "edit tag"


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
    fun testForSaveProductListFunctionality() {
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

        //save product
        Espresso.onView(ViewMatchers.withId(R.id.saveButton))
            .perform(ViewActions.click())

        waitALittle()

        //change product image (negative outcome)
        Espresso.onView(ViewMatchers.withId(R.id.captureButton))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.image_capture_button))
            .perform(ViewActions.click())
        waitALittle()
        Espresso.onView(ViewMatchers.withText("Cancel"))
            .perform(ViewActions.click())

        waitALittle()

        //change product image (positive outcome)
        Espresso.onView(ViewMatchers.withId(R.id.captureButton))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.image_capture_button))
            .perform(ViewActions.click())
        waitALittle()
        Espresso.onView(ViewMatchers.withText("Yes"))
            .perform(ViewActions.click())

        waitALittle()

        //trying to save product without name
        Espresso.onView(ViewMatchers.withId(R.id.saveButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())

        waitALittle()

        //filling in product name
        Espresso.onView(ViewMatchers.withId(R.id.productName))
            .perform(
                ViewActions.scrollTo(),
                ViewActions.typeText(productName),
                ViewActions.closeSoftKeyboard()
            )

        //trying to save product without category
        Espresso.onView(ViewMatchers.withId(R.id.saveButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())

        waitALittle()

        //picking category from spinner
        Espresso.onView(ViewMatchers.withId(R.id.dropdownSpinner))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Espresso.onData(allOf())
            .atPosition(1)
            .perform(ViewActions.click())
        waitALittle()
        //changing category
        Espresso.onView(ViewMatchers.withId(R.id.dropdownSpinner))
            .perform(ViewActions.click())
        Espresso.onData(allOf())
            .atPosition(2)
            .perform(ViewActions.click())
        waitALittle()

        //clearing name to test saving product without name
        Espresso.onView(ViewMatchers.withId(R.id.productName))
            .perform(
                ViewActions.scrollTo(),
                ViewActions.clearText(),
                ViewActions.closeSoftKeyboard()
            )
        Espresso.onView(ViewMatchers.withId(R.id.saveButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())

        //adding tag (negative)
        Espresso.onView(ViewMatchers.withId(R.id.addTagChip))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.tagInputEditText))
            .perform(ViewActions.typeText(tag2), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withText("Cancel"))
            .perform(ViewActions.click())

        //scroll down the screen to access add tag button in case of small screen
        Espresso.onView(ViewMatchers.withId(R.id.scrollView))
            .perform(ViewActions.swipeUp())

        //adding tags (positive)
        addTag(tag1)

        //scroll down the screen
        Espresso.onView(ViewMatchers.withId(R.id.scrollView))
            .perform(ViewActions.swipeUp())

        addTag(tag2)

        waitALittle()

        //asserting that tags are added
        Espresso.onView(ViewMatchers.withId(R.id.tagChipGroup))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(2)))

        waitALittle()

        //trying to save product without name, but with category and tags
        Espresso.onView(ViewMatchers.withId(R.id.saveButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())

        waitALittle()

        Espresso.onView(ViewMatchers.withId(R.id.productName))
            .perform(
                ViewActions.scrollTo(),
                ViewActions.typeText(productName),
                ViewActions.closeSoftKeyboard()
            )

        waitALittle()

        Espresso.onView(ViewMatchers.withId(R.id.saveButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())

        //assert that product is saved and displayed in the list
        Espresso.onView(ViewMatchers.withText(productName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        waitALittle()
    }

    @Test
    fun testSwitchProductList() {
        //go to product list
        Espresso.onView(ViewMatchers.withId(R.id.navigation_lists))
            .perform(ViewActions.click())

        //assert that category switch is displayed and enabled
        Espresso.onView(ViewMatchers.withId(R.id.category_switch))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.category_switch))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        //assert that switch has two tabs with according text
        Espresso.onView(ViewMatchers.withId(R.id.category_switch))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(2)))
        Espresso.onView(ViewMatchers.withId(R.id.category_switch))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.harmful_tab))))
        Espresso.onView(ViewMatchers.withId(R.id.category_switch))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(ViewMatchers.withText(R.string.harmless_tab))))

        //assert that descendant with text "harmful" is selected and "harmless" is not
        Espresso.onView(ViewMatchers.withId(R.id.category_switch))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasDescendant(
                        allOf(
                            ViewMatchers.withText(R.string.harmful_tab),
                            ViewMatchers.hasTextColor(R.color.white)
                        )
                    )
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.category_switch))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasDescendant(
                        allOf(
                            ViewMatchers.withText(R.string.harmless_tab),
                            ViewMatchers.hasTextColor(R.color.purple_500)
                        )
                    )
                )
            )

        waitALittle()

        //switch to "harmless" tab
        Espresso.onView(ViewMatchers.withId(R.id.category_switch))
            .perform(ViewActions.click())

        //assert that descendant with text "harmless" is selected and "harmful" is not
        Espresso.onView(ViewMatchers.withId(R.id.category_switch))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasDescendant(
                        allOf(
                            ViewMatchers.withText(R.string.harmless_tab),
                            ViewMatchers.hasTextColor(R.color.white)
                        )
                    )
                )
            )
        Espresso.onView(ViewMatchers.withId(R.id.category_switch))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasDescendant(
                        allOf(
                            ViewMatchers.withText(R.string.harmful_tab),
                            ViewMatchers.hasTextColor(R.color.purple_500)
                        )
                    )
                )
            )

        waitALittle()
    }

    @Test
    fun testSearchFunctionality() {
        //go to product list
        Espresso.onView(ViewMatchers.withId(R.id.navigation_lists))
            .perform(ViewActions.click())

        waitALittle()

        //assert that harmful product is displayed
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.product),
                ViewMatchers.withText(productName)
            )
        )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //switch to "harmless" tab
        Espresso.onView(ViewMatchers.withId(R.id.category_switch))
            .perform(ViewActions.click())

        //assert that harmful product is not displayed
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.product),
                ViewMatchers.withText(productName)
            )
        )
            .check(ViewAssertions.doesNotExist())

        //switch back to "harmful" tab
        Espresso.onView(ViewMatchers.withId(R.id.category_switch))
            .perform(ViewActions.click())

        //assert that search bar is displayed
        Espresso.onView(ViewMatchers.withId(R.id.searchString))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //search for product and assert that it is displayed
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.search_src_text))
            .perform(ViewActions.typeText(productName), ViewActions.closeSoftKeyboard())
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.product),
                ViewMatchers.withText(productName)
            )
        )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        waitALittle()

        //search for product which is not in the list and assert that product is not displayed
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.search_src_text))
            .perform(ViewActions.replaceText(productAnotherName), ViewActions.closeSoftKeyboard())
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.product),
                ViewMatchers.withText(productName)
            )
        )
            .check(ViewAssertions.doesNotExist())
        waitALittle()

        //clear search bar by pressing clear button and assert that product is displayed
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.search_close_btn))
            .perform(ViewActions.click())
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.product),
                ViewMatchers.withText(productName)
            )
        )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        waitALittle()

        //search by tags and assert that product is displayed with existing tags
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.search_src_text))
            .perform(ViewActions.typeText(tag1), ViewActions.closeSoftKeyboard())
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.product),
                ViewMatchers.withText(productName)
            )
        )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        waitALittle()
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.search_src_text))
            .perform(ViewActions.replaceText(tag2), ViewActions.closeSoftKeyboard())
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.product),
                ViewMatchers.withText(productName)
            )
        )
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        waitALittle()

        //search by not existing tag and assert that product is not displayed
        Espresso.onView(ViewMatchers.withId(androidx.appcompat.R.id.search_src_text))
            .perform(ViewActions.replaceText(editTag), ViewActions.closeSoftKeyboard())
        Espresso.onView(
            allOf(
                ViewMatchers.withId(R.id.product),
                ViewMatchers.withText(productName)
            )
        )
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun testRemoveEditFunctionality() {

        waitALittle()

        //go to product list
        Espresso.onView(ViewMatchers.withId(R.id.navigation_lists))
            .perform(ViewActions.click())

        waitALittle()

        //assert that product is displayed
        Espresso.onView(ViewMatchers.withText(productName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //edit product (negative)
        productEditProcedure()

        //going back without saving changes
        Espresso.pressBack()

        //assert that product name is not changed
        Espresso.onView(ViewMatchers.withText(productName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //edit product (positive)
        productEditProcedure()

        //saving changes
        Espresso.onView(ViewMatchers.withId(R.id.okButton))
            .perform(ViewActions.scrollTo(), ViewActions.click())

        //assert that product name is changed
        Espresso.onView(ViewMatchers.withText(productAnotherName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //removing product (negative)
        Espresso.onView(ViewMatchers.withId(R.id.productList))
            .perform(
                RecyclerViewActions.actionOnItem<ListRecyclerViewAdapter.ViewHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText(productAnotherName)),
                    clickChildViewWithId(R.id.deleteProduct)
                )
            )

        //canceling delete
        Espresso.onView(ViewMatchers.withText("No"))
            .perform(ViewActions.click())

        //assert that product is still displayed
        Espresso.onView(ViewMatchers.withText(productAnotherName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        //removing product (positive)
        Espresso.onView(ViewMatchers.withId(R.id.productList))
            .perform(
                RecyclerViewActions.actionOnItem<ListRecyclerViewAdapter.ViewHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText(productAnotherName)),
                    clickChildViewWithId(R.id.deleteProduct)
                )
            )
        waitALittle()

        //confirming delete
        Espresso.onView(ViewMatchers.withText("Yes"))
            .perform(ViewActions.click())

        //assert that product is not displayed
        Espresso.onView(ViewMatchers.withText(productAnotherName))
            .check(ViewAssertions.doesNotExist())
    }

    private fun productEditProcedure() {
        Espresso.onView(ViewMatchers.withId(R.id.productList))
            .perform(
                RecyclerViewActions.actionOnItem<ListRecyclerViewAdapter.ViewHolder>(
                    ViewMatchers.hasDescendant(ViewMatchers.withText(productName)),
                    clickChildViewWithId(R.id.viewEditProduct)
                )
            )

        //replace product name
        Espresso.onView(ViewMatchers.withId(R.id.productName))
            .perform(
                ViewActions.clearText(),
                ViewActions.typeText(productAnotherName),
                ViewActions.closeSoftKeyboard()
            )

        //asserting that product name is changed from original
        Espresso.onView(ViewMatchers.withText(productName))
            .check(ViewAssertions.doesNotExist())

        //asserting that initial amount of tags is 2
        Espresso.onView(ViewMatchers.withId(R.id.tagChipGroup))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(2)))

        //scroll down the modal
        Espresso.onView(ViewMatchers.withId(R.id.viewEditModalScrollView))
            .perform(ViewActions.swipeUp())
        //adding tag
        addTag(editTag)

        //asserting that amount of tags increased
        Espresso.onView(ViewMatchers.withId(R.id.tagChipGroup))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(3)))
    }

    private fun addTag(tag: String) {
        Espresso.onView(ViewMatchers.withId(R.id.addTagChip))
            .perform(ViewActions.scrollTo(), ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.tagInputEditText))
            .perform(ViewActions.typeText(tag), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withText("OK"))
            .perform(ViewActions.click())
        Thread.sleep(1000)
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