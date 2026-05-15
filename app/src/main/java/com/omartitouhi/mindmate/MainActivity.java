package com.omartitouhi.mindmate;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.graphics.Insets;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.omartitouhi.mindmate.databinding.ActivityMainBinding;
import com.omartitouhi.mindmate.ui.settings.SettingsViewModel;
import com.omartitouhi.mindmate.utils.NotificationHelper;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initializeNotifications();

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        DrawerLayout drawerLayout = binding.drawerLayout;
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.journalListFragment,
                R.id.aiChatFragment,
                R.id.meditationFragment,
                R.id.moodCheckInFragment,
                R.id.statisticsFragment,
                R.id.profileFragment,
                R.id.settingsFragment,
                R.id.aboutFragment
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
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initializeNotifications() {
        SharedPreferences preferences = getSharedPreferences(SettingsViewModel.PREFS_NAME, MODE_PRIVATE);
        boolean notificationsEnabled = preferences.getBoolean(SettingsViewModel.KEY_NOTIFICATIONS_ENABLED, true);
        int reminderHour = preferences.getInt(SettingsViewModel.KEY_JOURNAL_REMINDER_HOUR, 20);
        int reminderMinute = preferences.getInt(SettingsViewModel.KEY_JOURNAL_REMINDER_MINUTE, 0);
        boolean darkMode = preferences.getBoolean(SettingsViewModel.KEY_DARK_MODE, false);

        AppCompatDelegate.setDefaultNightMode(darkMode
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

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
}
