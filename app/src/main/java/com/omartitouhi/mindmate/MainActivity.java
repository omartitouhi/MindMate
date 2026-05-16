package com.omartitouhi.mindmate;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.graphics.Insets;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.omartitouhi.mindmate.databinding.ActivityMainBinding;
import com.omartitouhi.mindmate.ui.settings.SettingsViewModel;
import com.omartitouhi.mindmate.utils.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, android.R.color.transparent));

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeNotifications();
        applySystemBarInsets();

        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            throw new IllegalStateException("NavHostFragment introuvable dans activity_main.xml.");
        }
        navController = navHostFragment.getNavController();
        DrawerLayout drawerLayout = binding.drawerLayout;
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.journalListFragment,
                R.id.aiChatFragment,
                R.id.statisticsFragment,
                R.id.profileFragment
        ).setOpenableLayout(drawerLayout).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        NavigationUI.setupWithNavController(binding.appBarMain.bottomNavigation, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();
            boolean bottomDestination = destinationId == R.id.homeFragment
                    || destinationId == R.id.journalListFragment
                    || destinationId == R.id.aiChatFragment
                    || destinationId == R.id.statisticsFragment
                    || destinationId == R.id.profileFragment;
            binding.appBarMain.bottomNavigation.setVisibility(bottomDestination ? View.VISIBLE : View.GONE);
        });
        handleNavigationIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNavigationIntent(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initializeNotifications() {
        SharedPreferences preferences = getSharedPreferences(SettingsViewModel.PREFS_NAME, MODE_PRIVATE);
        boolean notificationsEnabled = preferences.getBoolean(SettingsViewModel.KEY_NOTIFICATIONS_ENABLED, true);
        int reminderHour = preferences.getInt(SettingsViewModel.KEY_JOURNAL_REMINDER_HOUR, 20);
        int reminderMinute = preferences.getInt(SettingsViewModel.KEY_JOURNAL_REMINDER_MINUTE, 0);
        NotificationHelper notificationHelper = new NotificationHelper(this);
        notificationHelper.createNotificationChannels();
        if (notificationsEnabled) {
            notificationHelper.scheduleDailyJournalReminder(reminderHour, reminderMinute);
        } else {
            notificationHelper.cancelDailyJournalReminder();
        }
        NotificationHelper.fetchAndSaveFcmToken();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PermissionChecker.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void applySystemBarInsets() {
        final int appBarInitialPaddingLeft = binding.appBarLayout.getPaddingLeft();
        final int appBarInitialPaddingTop = binding.appBarLayout.getPaddingTop();
        final int appBarInitialPaddingRight = binding.appBarLayout.getPaddingRight();
        final int appBarInitialPaddingBottom = binding.appBarLayout.getPaddingBottom();

        final int bottomNavInitialPaddingLeft = binding.appBarMain.bottomNavigation.getPaddingLeft();
        final int bottomNavInitialPaddingTop = binding.appBarMain.bottomNavigation.getPaddingTop();
        final int bottomNavInitialPaddingRight = binding.appBarMain.bottomNavigation.getPaddingRight();
        final int bottomNavInitialPaddingBottom = binding.appBarMain.bottomNavigation.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout, (view, insets) -> {
            Insets statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.displayCutout());
            Insets navigationBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars());

            binding.appBarLayout.setPadding(
                    appBarInitialPaddingLeft,
                    appBarInitialPaddingTop + statusBars.top,
                    appBarInitialPaddingRight,
                    appBarInitialPaddingBottom
            );

            binding.appBarMain.bottomNavigation.setPadding(
                    bottomNavInitialPaddingLeft,
                    bottomNavInitialPaddingTop,
                    bottomNavInitialPaddingRight,
                    bottomNavInitialPaddingBottom + navigationBars.bottom
            );

            return insets;
        });
        ViewCompat.requestApplyInsets(binding.drawerLayout);
    }

    private void handleNavigationIntent(Intent intent) {
        if (intent == null || navController == null) {
            return;
        }
        int destinationId = intent.getIntExtra(NotificationHelper.EXTRA_DESTINATION_ID, 0);
        if (destinationId == 0) {
            return;
        }
        if (navController.getCurrentDestination() != null
                && navController.getCurrentDestination().getId() == destinationId) {
            return;
        }
        navController.navigate(destinationId);
        intent.removeExtra(NotificationHelper.EXTRA_DESTINATION_ID);
    }

    private void applySavedTheme() {
        SharedPreferences preferences = getSharedPreferences(SettingsViewModel.PREFS_NAME, MODE_PRIVATE);
        boolean darkMode = preferences.getBoolean(SettingsViewModel.KEY_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(darkMode
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
