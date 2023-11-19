import android.content.Context
import com.bpr.allergendetector.ui.UiText
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class UiTextTest {

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testEmptyUiTextAsString() {
        val uiText = UiText.Empty
        val result = uiText.asString(mockContext)
        assertEquals("", result)
    }

    @Test
    fun testDynamicStringUiTextAsString() {
        val dynamicText = "Dynamic Text"
        val uiText = UiText.DynamicString(dynamicText)
        val result = uiText.asString(mockContext)
        assertEquals(dynamicText, result)
    }

    @Test
    fun testStringResourceUiTextAsString() {
        val stringRes = "String Resource"
        val uiText = UiText.StringResource(1, stringRes)
        `when`(mockContext.getString(1, stringRes)).thenReturn(stringRes)
        val result = uiText.asString(mockContext)
        assertEquals(stringRes, result)
    }
}
