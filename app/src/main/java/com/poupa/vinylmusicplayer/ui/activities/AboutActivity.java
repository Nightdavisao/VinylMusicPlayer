package com.poupa.vinylmusicplayer.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.poupa.vinylmusicplayer.R;
import com.poupa.vinylmusicplayer.databinding.ActivityAboutBinding;
import com.poupa.vinylmusicplayer.dialogs.ChangelogDialog;
import com.poupa.vinylmusicplayer.ui.activities.base.AbsBaseActivity;
import com.poupa.vinylmusicplayer.ui.activities.bugreport.BugReportActivity;
import com.poupa.vinylmusicplayer.ui.activities.intro.AppIntroActivity;

import de.psdev.licensesdialog.LicensesDialog;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
public class AboutActivity extends AbsBaseActivity implements View.OnClickListener {

    private static final String GITHUB = "https://github.com/AdrienPoupa/VinylMusicPlayer";

    private static final String WEBSITE = "https://adrien.poupa.fr/";

    private static final String RATE_ON_GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=com.poupa.vinylmusicplayer";

    private static final String KABOUZEID_GOOGLE_PLUS = "https://google.com/+KarimAbouZeid23697";
    private static final String KABOUZEID_WEBSITE = "https://kabouzeid.com";

    private static final String AIDAN_FOLLESTAD_GOOGLE_PLUS = "https://google.com/+AidanFollestad";
    private static final String AIDAN_FOLLESTAD_GITHUB = "https://github.com/afollestad";

    private static final String MICHAEL_COOK_GOOGLE_PLUS = "https://plus.google.com/102718493746376292361";
    private static final String MICHAEL_COOK_WEBSITE = "https://cookicons.co/";

    private static final String MAARTEN_CORPEL_GOOGLE_PLUS = "https://google.com/+MaartenCorpel";

    private static final String ALEKSANDAR_TESIC_GOOGLE_PLUS = "https://google.com/+aleksandarte??i??";

    private static final String EUGENE_CHEUNG_GITHUB = "https://github.com/arkon";
    private static final String EUGENE_CHEUNG_WEBSITE = "https://echeung.me/";

    private static final String ADRIAN_TWITTER = "https://twitter.com/froschgames";
    private static final String ADRIAN_WEBSITE = "https://froschgames.com/";

    Toolbar toolbar;
    TextView appVersion;
    LinearLayout changelog;
    LinearLayout intro;
    LinearLayout licenses;
    LinearLayout writeAnEmail;
    LinearLayout forkOnGitHub;
    LinearLayout visitWebsite;
    LinearLayout reportBugs;
    LinearLayout rateOnGooglePlay;

    AppCompatButton kabouzeidGooglePlus;
    AppCompatButton kabouzeidWebsite;
    AppCompatButton aidanFollestadGooglePlus;
    AppCompatButton aidanFollestadGitHub;
    AppCompatButton michaelCookGooglePlus;
    AppCompatButton michaelCookWebsite;
    AppCompatButton maartenCorpelGooglePlus;
    AppCompatButton aleksandarTesicGooglePlus;
    AppCompatButton eugeneCheungGitHub;
    AppCompatButton eugeneCheungWebsite;
    AppCompatButton adrianTwitter;
    AppCompatButton adrianWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAboutBinding binding = ActivityAboutBinding.inflate(LayoutInflater.from(this));
        toolbar = binding.toolbar;

        appVersion = binding.content.cardAboutApp.appVersion;
        changelog = binding.content.cardAboutApp.changelog;
        intro = binding.content.cardAboutApp.intro;
        licenses = binding.content.cardAboutApp.licenses;
        forkOnGitHub = binding.content.cardAboutApp.forkOnGithub;

        writeAnEmail = binding.content.cardAuthor.writeAnEmail;
        visitWebsite = binding.content.cardAuthor.visitWebsite;

        reportBugs = binding.content.cardSupportDevelopment.reportBugs;
        rateOnGooglePlay = binding.content.cardSupportDevelopment.rateOnGooglePlay;

        kabouzeidGooglePlus = binding.content.cardSpecialThanks.kabouzeidGooglePlus;
        kabouzeidWebsite = binding.content.cardSpecialThanks.kabouzeidWebsite;
        aidanFollestadGooglePlus = binding.content.cardSpecialThanks.aidanFollestadGooglePlus;
        aidanFollestadGitHub = binding.content.cardSpecialThanks.aidanFollestadGitHub;
        michaelCookGooglePlus = binding.content.cardSpecialThanks.michaelCookGooglePlus;
        michaelCookWebsite = binding.content.cardSpecialThanks.michaelCookWebsite;
        maartenCorpelGooglePlus = binding.content.cardSpecialThanks.maartenCorpelGooglePlus;
        aleksandarTesicGooglePlus = binding.content.cardSpecialThanks.aleksandarTesicGooglePlus;
        eugeneCheungGitHub = binding.content.cardSpecialThanks.eugeneCheungGitHub;
        eugeneCheungWebsite = binding.content.cardSpecialThanks.eugeneCheungWebsite;
        adrianTwitter = binding.content.cardSpecialThanks.adrianTwitter;
        adrianWebsite = binding.content.cardSpecialThanks.adrianWebsite;

        setContentView(binding.getRoot());

        setDrawUnderStatusbar();

        setStatusbarColorAuto();
        setNavigationbarColorAuto();
        setTaskDescriptionColorAuto();

        setUpViews();
    }

    private void setUpViews() {
        setUpToolbar();
        setUpAppVersion();
        setUpOnClickListeners();
    }

    private void setUpToolbar() {
        toolbar.setBackgroundColor(ThemeStore.primaryColor(this));
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpAppVersion() {
        appVersion.setText(getCurrentVersionName(this));
    }

    private void setUpOnClickListeners() {
        changelog.setOnClickListener(this);
        intro.setOnClickListener(this);
        licenses.setOnClickListener(this);
        forkOnGitHub.setOnClickListener(this);
        visitWebsite.setOnClickListener(this);
        reportBugs.setOnClickListener(this);
        writeAnEmail.setOnClickListener(this);
        rateOnGooglePlay.setOnClickListener(this);
        aidanFollestadGooglePlus.setOnClickListener(this);
        aidanFollestadGitHub.setOnClickListener(this);
        kabouzeidGooglePlus.setOnClickListener(this);
        kabouzeidWebsite.setOnClickListener(this);
        michaelCookGooglePlus.setOnClickListener(this);
        michaelCookWebsite.setOnClickListener(this);
        maartenCorpelGooglePlus.setOnClickListener(this);
        aleksandarTesicGooglePlus.setOnClickListener(this);
        eugeneCheungGitHub.setOnClickListener(this);
        eugeneCheungWebsite.setOnClickListener(this);
        adrianTwitter.setOnClickListener(this);
        adrianWebsite.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static String getCurrentVersionName(@NonNull final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "Unkown";
    }

    @Override
    public void onClick(View v) {
        if (v == changelog) {
            ChangelogDialog.create().show(getSupportFragmentManager(), "CHANGELOG_DIALOG");
        } else if (v == licenses) {
            showLicenseDialog();
        } else if (v == intro) {
            startActivity(new Intent(this, AppIntroActivity.class));
        } else if (v == forkOnGitHub) {
            openUrl(GITHUB);
        } else if (v == visitWebsite) {
            openUrl(WEBSITE);
        } else if (v == reportBugs) {
            startActivity(new Intent(this, BugReportActivity.class));
        } else if (v == writeAnEmail) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:adrien@poupa.fr"));
            intent.putExtra(Intent.EXTRA_EMAIL, "adrien@poupa.fr");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Vinyl Music Player");
            startActivity(Intent.createChooser(intent, "E-Mail"));
        } else if (v == rateOnGooglePlay) {
            openUrl(RATE_ON_GOOGLE_PLAY);
        } else if (v == aidanFollestadGooglePlus) {
            openUrl(AIDAN_FOLLESTAD_GOOGLE_PLUS);
        } else if (v == aidanFollestadGitHub) {
            openUrl(AIDAN_FOLLESTAD_GITHUB);
        } else if (v == kabouzeidGooglePlus) {
            openUrl(KABOUZEID_GOOGLE_PLUS);
        } else if (v == kabouzeidWebsite) {
            openUrl(KABOUZEID_WEBSITE);
        } else if (v == michaelCookGooglePlus) {
            openUrl(MICHAEL_COOK_GOOGLE_PLUS);
        } else if (v == michaelCookWebsite) {
            openUrl(MICHAEL_COOK_WEBSITE);
        } else if (v == maartenCorpelGooglePlus) {
            openUrl(MAARTEN_CORPEL_GOOGLE_PLUS);
        } else if (v == aleksandarTesicGooglePlus) {
            openUrl(ALEKSANDAR_TESIC_GOOGLE_PLUS);
        } else if (v == eugeneCheungGitHub) {
            openUrl(EUGENE_CHEUNG_GITHUB);
        } else if (v == eugeneCheungWebsite) {
            openUrl(EUGENE_CHEUNG_WEBSITE);
        } else if (v == adrianTwitter) {
            openUrl(ADRIAN_TWITTER);
        } else if (v == adrianWebsite) {
            openUrl(ADRIAN_WEBSITE);
        }
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void showLicenseDialog() {
        new LicensesDialog.Builder(this)
                .setNotices(R.raw.notices)
                .setTitle(R.string.licenses)
                .setNoticesCssStyle(getString(R.string.license_dialog_style)
                        .replace("{bg-color}", ThemeSingleton.get().darkTheme ? "424242" : "ffffff")
                        .replace("{text-color}", ThemeSingleton.get().darkTheme ? "ffffff" : "000000")
                        .replace("{license-bg-color}", ThemeSingleton.get().darkTheme ? "535353" : "eeeeee")
                )
                .setIncludeOwnLicense(true)
                .build()
                .show();
    }
}
