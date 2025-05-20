package com.hotel.evergreenkuakata.presentation.onboard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hotel.evergreenkuakata.BaseActivity
import com.hotel.evergreenkuakata.R
import com.hotel.evergreenkuakata.databinding.ActivityHomeBinding
import com.hotel.evergreenkuakata.presentation.GenericDialog
import com.hotel.evergreenkuakata.presentation.booking.BookingActivity
import com.hotel.evergreenkuakata.presentation.booking.BookingListActivity
import com.hotel.evergreenkuakata.presentation.room.RoomActivity
import com.hotel.evergreenkuakata.presentation.room.RoomListActivity
import com.hotel.evergreenkuakata.utils.Constants
import com.hotel.evergreenkuakata.utils.LanguageCallBack
import com.hotel.evergreenkuakata.utils.LocaleManager
import com.hotel.evergreenkuakata.utils.PrefManager
import com.hotel.evergreenkuakata.utils.Tools
import com.hotel.evergreenkuakata.utils.UX
import com.hotel.evergreenkuakata.utils.UtilsForAll
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(), LanguageCallBack {
    private lateinit var activityMainBinding: ActivityHomeBinding

    @Inject
    lateinit var prefManager: PrefManager

    private var tools = Tools(this)
    private lateinit var utilsForAll: UtilsForAll
    private lateinit var ux: UX
    private val viewModel by viewModels<HomeViewModel>()
    private var languageMap = HashMap<String, String>()
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            tools.doPopUpForExitApp()
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_home

    override fun setVariables(dataBinding: ActivityHomeBinding) {
        activityMainBinding = dataBinding
    }

//    override fun attachBaseContext(newBase: Context) {
//        super.attachBaseContext(LocaleManager.applyLocale(newBase))
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setupDrawerToggle()
        bindUIWithComponents()
        setupLanguage()
        initListeners()
        initObservers()
        viewModel.fetchBookingsForDate(tools.getTodayDate())
        viewModel.fetchAllBookings()
        viewModel.fetchRoomsWithAvailability(tools.getTodayDate())
    }

    private fun init() {
        ux = UX(this)
        utilsForAll = UtilsForAll(this)
    }

    private fun setupDrawerToggle() {
        setSupportActionBar(activityMainBinding.toolBar)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_icon_menu)
    }

    private fun initListeners() {
        activityMainBinding.toolBar.setTitleTextColor(
            ContextCompat.getColor(
                this,
                R.color.md_green_800
            )
        )

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        activityMainBinding.addRoom.setOnClickListener {
            activityLauncher.launch(Intent(this, RoomActivity::class.java))
        }

        activityMainBinding.roomList.setOnClickListener {
            activityLauncher.launch(Intent(this, RoomListActivity::class.java))
        }

        activityMainBinding.addBooking.setOnClickListener {
            activityLauncher.launch(Intent(this, BookingActivity::class.java))
        }

        activityMainBinding.bookingList.setOnClickListener {
            activityLauncher.launch(Intent(this, BookingListActivity::class.java))
        }
    }

    private fun initObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.roomsWithAvailability.collect { availableRooms ->
                        activityMainBinding.tvAvailableRooms.text =
                            getString(R.string.available_x, availableRooms.size)
                    }
                }

                launch {
                    viewModel.bookingsForDate.collect { bookings ->
                        activityMainBinding.tvBookedRooms.text =
                            getString(R.string.booked_x, bookings.size)
                    }
                }

                launch {
                    viewModel.incomeData.collect { incomeInfo ->
                        activityMainBinding.tvDailyEarnings.text =
                            getString(R.string.today_x, incomeInfo.today)
                        activityMainBinding.tvMonthlyEarnings.text =
                            getString(R.string.this_month_x, incomeInfo.thisMonth)
                    }
                }
            }
        }
    }

    private fun bindUIWithComponents() {
        activityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.fetchBookingsForDate(tools.getTodayDate())
                viewModel.fetchAllBookings()
                viewModel.fetchRoomsWithAvailability(tools.getTodayDate())
            }
        }


        if (Build.VERSION.SDK_INT > 32) {
            if (ContextCompat.checkSelfPermission(
                    this@HomeActivity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this@HomeActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.POST_NOTIFICATIONS),
                    Constants.REQUEST_CALL_CODE
                )
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this@HomeActivity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@HomeActivity,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    Constants.REQUEST_CALL_CODE
                )
            }
        }
    }

    private fun setupLanguage() {
        languageMap.clear()
        languageMap["bn"] = getString(R.string.bengali)
        languageMap["en"] = getString(R.string.english)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                val bottomSheet = GenericDialog<View>(
                    context = this,
                    layoutResId = R.layout.logout_confirmation_layout,
                    onClose = {

                    },
                    onPrimaryAction = {

                    },
                    onSecondaryAction = {
                        viewModel.logout()
                        tools.clearPrefForLogout(HomeActivity::class.java, prefManager)
                    }
                )
                bottomSheet.show()
                return true
            }

            R.id.menu_language -> {
                LocaleManager.doPopUpForLanguage(
                    this, this
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLanguageChange(selectedLan: String) {
        LocaleManager.setLocale(this, selectedLan)
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finishAffinity()
    }

}
