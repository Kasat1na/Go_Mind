package com.example.gomind;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import static androidx.test.espresso.assertion.ViewAssertions.matches;


import com.example.gomind.activities.MainActivity;
import com.example.gomind.fragments.ProfileFragment;
import com.example.gomind.fragments.QuizFragment;
import com.example.gomind.model.Token;
import com.example.gomind.model.User;
import androidx.fragment.app.testing.FragmentScenario;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    /**
     * Тест проверяет, что имя пакета приложения соответствует ожидаемому.
     */
    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.gomind", appContext.getPackageName());
    }

    /**
     * Тест проверяет корректность сохранения и извлечения токена доступа и токена обновления.
     */
    @Test
    public void testTokenSaveAndGet() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        Token token = new Token("access123", "refresh456");
        pref.saveToken(token);

        Token savedToken = pref.getToken();

        assertNotNull(savedToken);
        assertEquals("access123", savedToken.getAccess_token());
        assertEquals("refresh456", savedToken.getRefresh_token());
    }

    /**
     * Тест проверяет, устанавливается ли флаг принятия пользовательского соглашения.
     */
    @Test
    public void testAgreementAcceptedFlag() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        pref.saveAgreementAccepted();
        assertTrue(pref.isAgreementAccepted());
    }

    /**
     * Тест проверяет логику определения первого запуска приложения.
     */
    @Test
    public void testFirstRunLogic() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        pref.clear(); // сброс всех сохранённых данных
        assertTrue(pref.isFirstRun()); // первый запуск должен быть true

        pref.setFirstRunDone(); // устанавливаем, что первый запуск завершён
        assertFalse(pref.isFirstRun()); // теперь должен быть false
    }

    /**
     * Тест проверяет корректность сохранения и извлечения пользовательских данных.
     */
    @Test
    public void testUserSaveAndGet() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        Token token = new Token("abc", "def");
        User user = new User("test@mail.com", "tester", 10, 5, token);

        pref.saveUser(user);
        User loadedUser = pref.getUser();

        assertNotNull(loadedUser);
        assertEquals("test@mail.com", loadedUser.getEmail());
        assertEquals("tester", loadedUser.getNickname());
        assertEquals(10, loadedUser.getPears());
        assertEquals(5, loadedUser.getCount());
    }

    /**
     * Тест проверяет возможность сохранения и извлечения языковых предпочтений.
     */
    @Test
    public void testLanguagePreference() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        pref.saveLanguage(true); // сохраняем английский
        assertTrue(pref.getLanguage());

        pref.saveLanguage(false); // сохраняем русский
        assertFalse(pref.getLanguage());
    }

    /**
     * Тест проверяет возможность сохранения и загрузки изображения (Bitmap) из SharedPreferences.
     */
    @Test
    public void testImageSaveAndLoad() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        Bitmap testBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888); // создаём тестовое изображение
        pref.saveImage(testBitmap);

        Bitmap loadedBitmap = pref.loadImage();

        assertNotNull(loadedBitmap);
        assertEquals(testBitmap.getWidth(), loadedBitmap.getWidth());
        assertEquals(testBitmap.getHeight(), loadedBitmap.getHeight());
    }

    @Test
    public void testSaveNullTokenReturnsNull() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        pref.clear();
        pref.saveToken(null);

        Token token = pref.getToken();
        assertNull(token);
    }
    @Test
    public void testIsLoggedInDependsOnToken() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        pref.clear();
        assertFalse(pref.isLoggedIn());

        Token token = new Token("abc", "def");
        pref.saveToken(token);
        assertTrue(pref.isLoggedIn());
    }
    @Test
    public void testClearPreferences() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        pref.saveAgreementAccepted();
        pref.saveLanguage(true);
        pref.saveToken(new Token("token", "refresh"));
        pref.clear();

        assertFalse(pref.isAgreementAccepted());
        assertNull(pref.getToken());
        assertFalse(pref.getLanguage()); // по умолчанию false = русский
    }
    @Test
    public void testShouldShowPDFLogic() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        pref.clear();
        assertTrue(pref.shouldShowPDF());
        assertTrue(pref.shouldShowPDFOnStart());

        pref.setPDFShown();
        pref.saveAgreementAccepted();

        assertFalse(pref.shouldShowPDF());
        assertFalse(pref.shouldShowPDFOnStart());
    }

    @Test
    public void testLanguageSwitching() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        pref.saveLanguage(true); // английский

        assertTrue(pref.getLanguage());

        pref.saveLanguage(false); // русский
        assertFalse(pref.getLanguage());
    }


}
