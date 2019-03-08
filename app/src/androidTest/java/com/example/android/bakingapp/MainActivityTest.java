package com.example.android.bakingapp;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private static final String NUTELLA = "Nutella Pie";
    private static final String BROWNIES = "Brownies";
    private static final String YELLOW_CAKE = "Yellow Cake";
    private static final String CHEESECAKE = "Cheesecake";
    private IdlingResource mIdlingResource;
    private IdlingRegistry idlingRegistry = IdlingRegistry.getInstance();

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mainActivityActivityTestRule.getActivity().getIdlingResource();
        idlingRegistry.register(mIdlingResource);
    }

    @Test
    public void clickOnListViewIte_OpensStepsFragmentWithCorrectItemDisplayed() {
        onView(ViewMatchers.withId(R.id.main_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0,
                        click()));
        onView(withId(R.id.detail_title_text_view)).check(matches(withText(NUTELLA)));
        pressBack();
        onView(ViewMatchers.withId(R.id.main_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1,
                        click()));
        onView(withId(R.id.detail_title_text_view)).check(matches(withText(BROWNIES)));
        pressBack();
        onView(ViewMatchers.withId(R.id.main_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(2,
                        click()));
        onView(withId(R.id.detail_title_text_view)).check(matches(withText(YELLOW_CAKE)));
        pressBack();
        onView(ViewMatchers.withId(R.id.main_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3,
                        click()));
        onView(withId(R.id.detail_title_text_view)).check(matches(withText(CHEESECAKE)));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            idlingRegistry.unregister(mIdlingResource);
        }
    }
}
