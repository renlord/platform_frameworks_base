/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.wm.flicker;

import static com.android.server.wm.flicker.CommonTransitions.openAppWarm;
import static com.android.server.wm.flicker.WindowUtils.getDisplayBounds;
import static com.android.server.wm.flicker.WmTraceSubject.assertThat;

import androidx.test.InstrumentationRegistry;
import androidx.test.filters.FlakyTest;
import androidx.test.filters.LargeTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

/**
 * Test warm launch app.
 * To run this test: {@code atest FlickerTests:OpenAppWarmTest}
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OpenAppWarmTest extends FlickerTestBase {

    public OpenAppWarmTest() {
        this.mTestApp = new StandardAppHelper(InstrumentationRegistry.getInstrumentation(),
                "com.android.server.wm.flicker.testapp", "SimpleApp");
    }

    @Before
    public void runTransition() {
        super.runTransition(openAppWarm(mTestApp, mUiDevice).includeJankyRuns().build());
    }

    @Test
    public void checkVisibility_navBarIsAlwaysVisible() {
        checkResults(result -> assertThat(result)
                .showsAboveAppWindow(NAVIGATION_BAR_WINDOW_TITLE).forAllEntries());
    }

    @Test
    public void checkVisibility_statusBarIsAlwaysVisible() {
        checkResults(result -> assertThat(result)
                .showsAboveAppWindow(STATUS_BAR_WINDOW_TITLE).forAllEntries());
    }

    @Test
    public void checkVisibility_wallpaperBecomesInvisible() {
        checkResults(result -> assertThat(result)
                .showsBelowAppWindow("Wallpaper")
                .then()
                .hidesBelowAppWindow("Wallpaper")
                .forAllEntries());
    }

    @Test
    public void checkZOrder_appWindowReplacesLauncherAsTopWindow() {
        checkResults(result -> assertThat(result)
                .showsAppWindowOnTop(
                        "com.android.launcher3/.Launcher")
                .then()
                .showsAppWindowOnTop(mTestApp.getPackage())
                .forAllEntries());
    }

    @FlakyTest(bugId = 141235985)
    @Ignore("Waiting bug feedback")
    @Test
    public void checkCoveredRegion_noUncoveredRegions() {
        checkResults(result -> LayersTraceSubject.assertThat(result).coversRegion(
                getDisplayBounds()).forAllEntries());
    }

    @Test
    public void checkVisibility_navBarLayerIsAlwaysVisible() {
        checkResults(result -> LayersTraceSubject.assertThat(result)
                .showsLayer(NAVIGATION_BAR_WINDOW_TITLE).forAllEntries());
    }

    @Test
    public void checkVisibility_statusBarLayerIsAlwaysVisible() {
        checkResults(result -> LayersTraceSubject.assertThat(result)
                .showsLayer(STATUS_BAR_WINDOW_TITLE).forAllEntries());
    }

    @Test
    public void checkVisibility_wallpaperLayerBecomesInvisible() {
        checkResults(result -> LayersTraceSubject.assertThat(result)
                .showsLayer("Wallpaper")
                .then()
                .hidesLayer("Wallpaper")
                .forAllEntries());
    }
}
