package com.psoffritti.librarysampleapptemplate.core

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.navigation.NavigationView
import com.psoffritti.librarysampleapptemplate.core.customviews.ProgressBarWebView
import com.psoffritti.librarysampleapptemplate.core.utils.Configuration
import com.psoffritti.librarysampleapptemplate.core.utils.ExampleActivityDetails
import com.psoffritti.librarysampleapptemplate.core.utils.Utils.addItems
import com.psoffritti.librarysampleapptemplate.core.utils.Utils.getScreenWidth
import com.psoffritti.librarysampleapptemplate.core.utils.Utils.openUri
import com.psoffritti.librarysampleapptemplate.core.utils.Utils.resolveColorAttribute
import com.psoffritti.librarysampleapptemplate.core.utils.Utils.setStatusBarTranslucency
import com.psoffritti.librarysampleapptemplate.core.utils.Utils.setWidth

/**
 * This Activity is meant to be used as a template for sample applications.
 *
 * You can configure many properties by passing extras to the Activity Intent. To learn more [read the documentation](https://github.com/PierfrancescoSoffritti/library-sample-app-template).
 */
class SampleAppTemplateActivity : AppCompatActivity() {

    private lateinit var configuration: Configuration

    private lateinit var no_home_page_view: View
    private lateinit var drawer_layout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigation_view: NavigationView
    private lateinit var webview: ProgressBarWebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lsat_activity_sample_app_template)

        no_home_page_view = findViewById(R.id.no_home_page_view)
        drawer_layout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        navigation_view = findViewById(R.id.navigation_view)
        webview = findViewById(R.id.webview)

        configuration = Configuration.getInstance(intent.extras ?: Bundle())

        setStatusBarTranslucency()

        initToolbar(configuration.title)
        initNavDrawer(configuration.examples, configuration.githubUrl, configuration.playStorePackageName)

        if(configuration.homepageUrl != null) initWebView(configuration.homepageUrl!!) else no_home_page_view.visibility = View.VISIBLE

        showTutorial()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.lsat_main_activity_menu, menu)
        if(configuration.githubUrl == null)
            menu.removeItem(R.id.open_on_github)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { drawer_layout.openDrawer(GravityCompat.START); true }
            R.id.open_on_github -> { openUri(configuration.githubUrl); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
            else -> super.onBackPressed()
        }
    }

    private fun initToolbar(appTitle: String?) {
        setSupportActionBar(toolbar)

        val actionbar = supportActionBar
        actionbar?.title = appTitle
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(R.drawable.lsat_ic_nav_drawer_menu_24dp)
    }

    private fun initNavDrawer(
        examplesDetails: List<ExampleActivityDetails>?,
        githubUrl: String?,
        playStorePackageName: String?
    ) {
        navigation_view.setWidth(getScreenWidth() - toolbar.layoutParams.height)

        navigation_view.menu.addItems(examplesDetails)
        if(githubUrl == null)
            navigation_view.menu.removeItem(R.id.star_on_github)
        if(playStorePackageName == null)
            navigation_view.menu.removeItem(R.id.rate_on_playstore)

        navigation_view.setNavigationItemSelectedListener { menuItem ->
            drawer_layout.closeDrawers()

            when {
                examplesDetails != null && menuItem.itemId >= 0 && menuItem.itemId < examplesDetails.size -> startActivity(Intent(this, examplesDetails[menuItem.itemId].clazz))
                menuItem.itemId == R.id.star_on_github ->  openUri("$githubUrl/stargazers")
                menuItem.itemId == R.id.rate_on_playstore -> {
                    try {
                        openUri("market://details?id=$playStorePackageName")
                    } catch (exception: ActivityNotFoundException) {
                        openUri("https://play.google.com/store/apps/details?id=$playStorePackageName")
                    }
                }
                else -> return@setNavigationItemSelectedListener false
            }

            return@setNavigationItemSelectedListener true
        }
    }

    private fun initWebView(homePageUrl: String) {
        webview.enableJavascript(true)
        webview.loadUrl(homePageUrl)
        webview.onUrlClick = { openUri(it) }
    }

    private fun showTutorial() {
        val preferenceKey = "featureDiscoveryShown"
        val sharedPreferencesKey = "sampleApp_MainActivity_SharedPreferences"
        val prefs = getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val featureDiscoveryShown = prefs.getBoolean(preferenceKey, false)

        if (featureDiscoveryShown)
            return
        else
            prefs.edit().putBoolean(preferenceKey, true).apply()

        val target = toolbar.getChildAt(1)

        TapTargetView.showFor(
            this,
            TapTarget.forView(
                target,
                getString(R.string.lsat_tutorial_title),
                getString(R.string.lsat_tutorial_description)
            )
                .outerCircleColorInt(resolveColorAttribute(R.attr.lsat_tutorial_background_color, R.color.lsat_tutorial_background_color))
                .outerCircleAlpha(1f)
                .targetCircleColorInt(resolveColorAttribute(R.attr.lsat_tutorial_target_circle_color, R.color.lsat_tutorial_target_circle_color))
                .titleTextColorInt(resolveColorAttribute(R.attr.lsat_tutorial_text_color, R.color.lsat_tutorial_text_color))
                .drawShadow(true)
                .transparentTarget(true), object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    target.performClick()
                }
            })
    }
}
