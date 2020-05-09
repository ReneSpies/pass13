package com.aresid.simplepasswordgeneratorapp

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.billingclient.api.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.material.snackbar.Snackbar
import java.util.*

class MainActivity: AppCompatActivity(), OnFragmentInteractionListener, View.OnClickListener, OnUnlockFeaturesDialogInteractionListener, PurchasesUpdatedListener, BillingClientStateListener, SkuDetailsResponseListener, AcknowledgePurchaseResponseListener, ConsumeResponseListener, OnSharedPreferenceChangeListener {
	private var mCurrentNightMode = 0
	private var mNavController: NavController? = null
	private var mToolbar: Toolbar? = null
	private var mBillingClient: BillingClient? = null
	private val mSkuDetailsList: MutableList<SkuDetails> = ArrayList()
	override fun onCreate(savedInstanceState: Bundle?) {
		Log.d(
			TAG,
			"onCreate: called"
		)
		setTheme(R.style.Gratify_AppTheme)
		super.onCreate(savedInstanceState)
		setCurrentNightModeFromSharedPreferences()
		setContentView(R.layout.activity_main)
		registerOnSharedPreferencesChangeListener()
		setUpNavController(findViewById(R.id.nav_host_fragment))
		setUpToolbar()
		setUpBillingClient()
		initializeMobileAds()
		handlePass13ExclusiveState()
	}
	
	private fun setCurrentNightModeFromSharedPreferences() {
		Log.d(
			TAG,
			"setCurrentNightModeFromSharedPreferences: called"
		)
		val nightModeKey = getString(R.string.night_mode_key)
		val preferences = getSharedPreferences(
			nightModeKey,
			Context.MODE_PRIVATE
		)
		mCurrentNightMode = // If preferences not null and contain a night mode int
				// set the current night mode accordingly
			preferences?.getInt(
				nightModeKey,
				resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
			) ?: resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
	}
	
	private fun registerOnSharedPreferencesChangeListener() {
		Log.d(
			TAG,
			"registerOnSharedPreferencesChangeListener: called"
		)
		getSharedPreferences(
			getString(R.string.pass13_exclusive_preferences_key),
			Context.MODE_PRIVATE
		).registerOnSharedPreferenceChangeListener(this /* onSharedPreferenceChanged */)
	}
	
	private fun setUpNavController(navHostFragment: View) {
		Log.d(
			TAG,
			"setUpNavController: called"
		)
		mNavController = Navigation.findNavController(navHostFragment)
	}
	
	private fun setUpToolbar() {
		Log.d(
			TAG,
			"setUpToolbar: called"
		)
		// Set toolbar member variable here
		mToolbar = findViewById(R.id.toolbar)
		setSupportActionBar(mToolbar)
		mToolbar!!.setNavigationOnClickListener(this)
		if (supportActionBar != null) {
			supportActionBar!!.setDisplayShowTitleEnabled(false)
		}
	}
	
	private fun setUpBillingClient() {
		Log.d(
			TAG,
			"setUpBillingClient: called"
		)
		mBillingClient = BillingClient.newBuilder(this).setListener(this).enablePendingPurchases().build()
		mBillingClient!!.startConnection(
			this /* onBillingSetupFinished or
		onBillingServiceDisconnected */
		)
	}
	
	private fun initializeMobileAds() {
		Log.d(
			TAG,
			"initializeMobileAds: called"
		)
		MobileAds.initialize(
			this
		) { initializationStatus: InitializationStatus? -> }
	}
	
	private fun showPass13ToolbarTitle(show: Boolean) {
		Log.d(
			TAG,
			"showPass13ToolbarTitle: called"
		)
		val pass13Title = findViewById<TextView>(R.id.toolbar_title)
		if (show) {
			pass13Title.visibility = View.VISIBLE
			if (appIsExclusive()) {
				toggleShowToolbarTitleExclusive(true)
			}
		}
		else {
			pass13Title.visibility = View.GONE
			if (appIsExclusive()) {
				toggleShowToolbarTitleExclusive(false)
			}
		}
	}
	
	private fun appIsExclusive(): Boolean {
		Log.d(
			TAG,
			"appIsExclusive: called"
		)
		val key = getString(R.string.pass13_exclusive_preferences_key)
		val preferences = getSharedPreferences(
			key,
			Context.MODE_PRIVATE
		)
		return preferences.getBoolean(
			key,
			false
		)
	}
	
	private fun toggleShowToolbarTitleExclusive(show: Boolean) {
		Log.d(
			TAG,
			"enableToolbarTitleExclusive: called"
		)
		val exclusiveTitle = findViewById<TextView>(R.id.toolbar_title_exclusive)
		if (show) {
			exclusiveTitle.visibility = View.VISIBLE
		}
		else {
			exclusiveTitle.visibility = View.GONE
		}
	}
	
	private fun setToolbarNavigationIcon(set: Boolean) {
		Log.d(
			TAG,
			"setToolbarNavigationIcon: called"
		)
		if (set) {
			mToolbar!!.setNavigationIcon(R.drawable.ic_done)
		}
		else {
			mToolbar!!.navigationIcon = null
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		Log.d(
			TAG,
			"onCreateOptionsMenu: called"
		)
		menuInflater.inflate(
			R.menu.toolbar_menu,
			menu
		)
		if (mCurrentNightMode == resources.getInteger(R.integer.night_mode_dark)) {
			// Default state of toolbar menu item is "Light Mode" so change it if dark
			// mode is activated from shared preferences
			activateNightMode(true)
		}
		else if (mCurrentNightMode == resources.getInteger(R.integer.night_mode_light)) {
			activateNightMode(false)
		}
		if (appIsExclusive()) {
			menu.removeItem(R.id.action_unlock_features)
		}
		return super.onCreateOptionsMenu(menu)
	}
	
	private val adSize: AdSize
		private get() {
			Log.d(
				TAG,
				"getAdSize: called"
			)
			val display = windowManager.defaultDisplay
			val outMetrics = DisplayMetrics()
			display.getMetrics(outMetrics)
			val widthPixels = outMetrics.widthPixels.toFloat()
			val density = outMetrics.density
			val adWidth = (widthPixels / density).toInt()
			return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
				this,
				adWidth
			)
		}
	
	override fun onConfigurationChanged(newConfig: Configuration) {
		Log.d(
			TAG,
			"onConfigurationChanged: called"
		)
		super.onConfigurationChanged(newConfig)
		mCurrentNightMode = newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
		getSharedPreferences(
			getString(R.string.night_mode_key),
			Context.MODE_PRIVATE
		).edit().putInt(
			getString(R.string.night_mode_key),
			mCurrentNightMode
		).apply()
	}
	
	override fun onMainFragmentViewCreated() {
		Log.d(
			TAG,
			"onMainFragmentViewCreated: called"
		)
		invalidateOptionsMenu()
		mToolbar!!.title = null
		showPass13ToolbarTitle(true)
		setToolbarNavigationIcon(false)
	}
	
	private fun activateNightMode(activate: Boolean) {
		Log.d(
			TAG,
			"activateNightMode: called"
		)
		val nightModeKey = getString(R.string.night_mode_key)
		if (activate) {
			// Activate dark mode
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
			// Set current night mode variable accordingly
			mCurrentNightMode = resources.getInteger(R.integer.night_mode_dark)
			// Save current night mode state in shared preferences
			getSharedPreferences(
				nightModeKey,
				Context.MODE_PRIVATE
			).edit().putInt(
				nightModeKey,
				mCurrentNightMode
			).apply()
			renameToolbarMenuNightMode(true)
		}
		else {
			// Activate light mode
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
			// Set current night mode variable accordingly
			mCurrentNightMode = resources.getInteger(R.integer.night_mode_light)
			// Save current night mode state in shared preferences
			getSharedPreferences(
				nightModeKey,
				Context.MODE_PRIVATE
			).edit().putInt(
				nightModeKey,
				mCurrentNightMode
			).apply()
			renameToolbarMenuNightMode(false)
		}
	}
	
	private fun onMenuChangeThemeClicked() {
		Log.d(
			TAG,
			"onMenuChangeThemeClicked: called"
		)
		if (mCurrentNightMode == resources.getInteger(R.integer.night_mode_dark)) {
			activateNightMode(false)
		}
		else if (mCurrentNightMode == resources.getInteger(R.integer.night_mode_light)) {
			activateNightMode(true)
		}
	}
	
	override fun onSettingsFragmentViewCreated() {
		Log.d(
			TAG,
			"onSettingsFragmentViewCreated: called"
		)
		mToolbar!!.title = getString(R.string.settings)
		showPass13ToolbarTitle(false)
		disableToolbarMenu()
		setToolbarNavigationIcon(true)
	}
	
	private fun disableToolbarMenu() {
		Log.d(
			TAG,
			"disableToolbarMenu: called"
		)
		mToolbar!!.menu.clear()
	}
	
	private fun onMenuSettingsClicked() {
		Log.d(
			TAG,
			"onMenuSettingsClicked: called"
		)
		mNavController!!.navigate(R.id.action_mainFragment_to_settingsFragment)
	}
	
	private fun onMenuUnlockFeaturesClicked() {
		Log.d(
			TAG,
			"onMenuUnlockFeaturesClicked: called"
		)
		UnlockFeaturesDialog().show(
			supportFragmentManager,
			"UnlockFeaturesDialog"
		)
	}
	
	private fun renameToolbarMenuNightMode(rename: Boolean) {
		Log.d(
			TAG,
			"renameToolbarMenuNightMode: called"
		)
		val menu = mToolbar!!.menu
		if (rename) {
			menu.getItem(0).title = getString(R.string.light_mode)
		}
		else {
			menu.getItem(0).title = getString(R.string.dark_mode)
		}
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_change_theme -> onMenuChangeThemeClicked()
			R.id.action_settings -> onMenuSettingsClicked()
			R.id.action_unlock_features -> onMenuUnlockFeaturesClicked()
		}
		return true
	}
	
	override fun onClick(v: View) {
		Log.d(
			TAG,
			"onClick: called"
		)
		if (v.id == -1) {
			// This is the navigation icon
			onBackPressed()
		}
	}
	
	override fun onPurchasesUpdated(
		result: BillingResult,
		list: List<Purchase>?
	) {
		Log.d(
			TAG,
			"onPurchasesUpdated: called"
		)
		if (result.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
			for (purchase in list) {
				handlePurchase(purchase)
			}
		}
		else if (result.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
			Log.d(
				TAG,
				"onPurchasesUpdated: already owned"
			)
		}
	}
	
	private fun handlePurchase(purchase: Purchase) {
		Log.d(
			TAG,
			"handlePurchase: called"
		)
		if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
			// Grant entitlement to user
			if (!purchase.isAcknowledged) {
				val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
				mBillingClient!!.acknowledgePurchase(
					params,
					this /*
				onAcknowledgePurchaseResponse */
				)
			}
		}
		else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
			// Here you can confirm to the user that they've started the pending
			// purchase, and to complete it, they should follow instructions that
			// are given to them. You can also choose to remind the user in the
			// future to complete the purchase if you detect that it is still
			// pending.
			showSnackbar(
				findViewById(R.id.toolbar),
				getString(R.string.pending_purchase)
			)
		}
	}
	
	private fun showSnackbar(
		snackbarView: View,
		message: String
	) {
		Log.d(
			TAG,
			"showSnackbar: called"
		)
		Snackbar.make(
			snackbarView,
			message,
			Snackbar.LENGTH_LONG
		).setBackgroundTint(
			ContextCompat.getColor(
				this,
				R.color.secondary
			)
		).show()
	}
	
	override fun onSharedPreferenceChanged(
		sharedPreferences: SharedPreferences,
		key: String
	) {
		Log.d(
			TAG,
			"onSharedPreferenceChanged: called"
		)
		handlePass13ExclusiveState()
	}
	
	private fun handlePass13ExclusiveState() {
		Log.d(
			TAG,
			"handlePass13ExclusiveState: called"
		)
		if (appIsExclusive()) {
			toggleShowToolbarTitleExclusive(true)
			disableUnlockFeaturesAction()
		}
		else {
			loadAds()
		}
	}
	
	private fun disableUnlockFeaturesAction() {
		Log.d(
			TAG,
			"disableUnlockFeaturesAction: called"
		)
		invalidateOptionsMenu()
	}
	
	private fun loadAds() {
		Log.d(
			TAG,
			"loadAds: called"
		)
		val adViewContainer = findViewById<FrameLayout>(R.id.ad_view_container)
		val adView = AdView(this)
		adView.adUnitId = getString(R.string.adaptive_banner_ad_unit_id)
		adViewContainer.addView(adView)
		val adRequest = AdRequest.Builder().build()
		val adSize = adSize
		adView.adSize = adSize
		adView.loadAd(adRequest)
	}
	
	override fun onBillingSetupFinished(result: BillingResult) {
		Log.d(
			TAG,
			"onBillingSetupFinished: called"
		)
		querySkuDetailsAsync()
		handleAlreadyPurchased()
	}
	
	private fun querySkuDetailsAsync() {
		Log.d(
			TAG,
			"querySkuDetailsAsync: called"
		)
		val paramsBuilder = SkuDetailsParams.newBuilder()
		paramsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
		mBillingClient!!.querySkuDetailsAsync(
			paramsBuilder.build(),
			this /* onSkuDetailsResponse */
		)
	}
	
	private val skuList: List<String>
		private get() {
			Log.d(
				TAG,
				"getSkuList: called"
			)
			val skuList: MutableList<String> = ArrayList()
			skuList.add(getString(R.string.pass13_exclusive_id))
			return skuList
		}
	
	/**
	 * This method uses the BillingClient to find out whether or not the user has already bought the item.
	 */
	private fun handleAlreadyPurchased() {
		Log.d(
			TAG,
			"handleAlreadyPurchased: called"
		)
		val purchasesResult = mBillingClient!!.queryPurchases(BillingClient.SkuType.INAPP)
		if (purchasesResult.responseCode == BillingClient.BillingResponseCode.OK && purchasesResult.purchasesList != null) {
			for (purchase in purchasesResult.purchasesList) {
				if (purchase.sku == getString(R.string.pass13_exclusive_id)) {
					val key = getString(R.string.pass13_exclusive_preferences_key)
					val editor = getSharedPreferences(
						key,
						Context.MODE_PRIVATE
					).edit()
					editor.putBoolean(
						key,
						true
					)
					editor.apply()
				}
			}
		}
	}
	
	override fun onBillingServiceDisconnected() {
		Log.d(
			TAG,
			"onBillingServiceDisconnected: called"
		)
		showErrorSnackbar(
			findViewById(R.id.toolbar),
			getString(R.string.bad_connection)
		)
	}
	
	private fun showErrorSnackbar(
		snackbarView: View,
		message: String
	) {
		Log.d(
			TAG,
			"showErrorSnackbar: called"
		)
		Snackbar.make(
			snackbarView,
			message,
			Snackbar.LENGTH_LONG
		).setBackgroundTint(
			ContextCompat.getColor(
				this,
				R.color.error
			)
		).show()
	}
	
	override fun onSkuDetailsResponse(
		result: BillingResult,
		list: List<SkuDetails>
	) {
		Log.d(
			TAG,
			"onSkuDetailsResponse: called"
		)
		if (result.responseCode == BillingClient.BillingResponseCode.OK && list != null) {
			mSkuDetailsList.addAll(list)
		}
	}
	
	override fun onAcknowledgePurchaseResponse(result: BillingResult) {
		Log.d(
			TAG,
			"onAcknowledgePurchaseResponse: called"
		)
	}
	
	override fun onConsumeResponse(
		result: BillingResult,
		s: String
	) {
		Log.d(
			TAG,
			"onConsumeResponse: called"
		)
		if (result.responseCode == BillingClient.BillingResponseCode.OK) {
			Log.d(
				TAG,
				"onConsumeResponse: response = $s"
			)
		}
	}
	
	override fun onUnlockFeaturesDialogPositiveButtonClicked() {
		Log.d(
			TAG,
			"onUnlockFeaturesDialogPositiveButtonClicked: called"
		)
		if (mSkuDetailsList.size == 0) {
			showErrorSnackbar(
				findViewById(R.id.toolbar),
				getString(R.string.bad_connection)
			)
			querySkuDetailsAsync()
			return
		}
		val params = BillingFlowParams.newBuilder().setSkuDetails(mSkuDetailsList[0]).build()
		mBillingClient!!.launchBillingFlow(
			this,
			params
		) // Continues with
		// onPurchasesUpdated
	}
	
	companion object {
		// Features included in the paid version: extra setting for specific export path,
		// prompt user for path if setting is not activated, remove ads.
		private const val TAG = "MainActivity"
	}
}