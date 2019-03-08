package com.example.android.bakingapp;

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
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class DetailsTest {
    private static final String NUTELLA = "Nutella Pie";
    private static final String DESCRIPTION_1 = "Starting prep";
    private static final String DESCRIPTION_2 = "Prep the cookie crust.";
    private static final String DESCRIPTION_3 = "Press the crust into baking form.";
    private static final String DESCRIPTION_4 = "Start filling prep";
    private static final String DESCRIPTION_5 = "Finish filling prep";
    private static final String DESCRIPTION_6 = "Finishing Steps";
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
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.detail_title_text_view)).check(matches(withText(NUTELLA)));
        onView(ViewMatchers.withId(R.id.details_steps_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.video_main_steps_number_text_view)).check(matches(withText("1")));
        onView(withId(R.id.video_main_steps_title_text_view)).check(matches(withText(DESCRIPTION_1)));
        onView(ViewMatchers.withId(R.id.video_mian_steps_forward_button))
                .perform(click());
        onView(withId(R.id.video_main_steps_number_text_view)).check(matches(withText("2")));
        onView(withId(R.id.video_main_steps_title_text_view)).check(matches(withText(DESCRIPTION_2)));
        onView(ViewMatchers.withId(R.id.video_mian_steps_forward_button))
                .perform(click());
        onView(withId(R.id.video_main_steps_number_text_view)).check(matches(withText("3")));
        onView(withId(R.id.video_main_steps_title_text_view)).check(matches(withText(DESCRIPTION_3)));
        onView(ViewMatchers.withId(R.id.video_mian_steps_forward_button))
                .perform(click());
        onView(withId(R.id.video_main_steps_number_text_view)).check(matches(withText("4")));
        onView(withId(R.id.video_main_steps_title_text_view)).check(matches(withText(DESCRIPTION_4)));
        onView(ViewMatchers.withId(R.id.video_mian_steps_forward_button))
                .perform(click());
        onView(withId(R.id.video_main_steps_number_text_view)).check(matches(withText("5")));
        onView(withId(R.id.video_main_steps_title_text_view)).check(matches(withText(DESCRIPTION_5)));
        onView(ViewMatchers.withId(R.id.video_mian_steps_forward_button))
                .perform(click());
        onView(withId(R.id.video_main_steps_number_text_view)).check(matches(withText("6")));
        onView(withId(R.id.video_main_steps_title_text_view)).check(matches(withText(DESCRIPTION_6)));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            idlingRegistry.unregister(mIdlingResource);
        }
    }
}
