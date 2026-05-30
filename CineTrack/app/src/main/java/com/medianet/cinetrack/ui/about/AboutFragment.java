package com.medianet.cinetrack.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.medianet.cinetrack.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        // Features
        String[][] features = {
                {"🎬", "Top 100 Films IMDB",      "Découvrez les meilleurs films de tous les temps"},
                {"📺", "Trending Séries",          "Suivez les séries les plus populaires du moment"},
                {"📋", "Watchlist personnelle",    "Sauvegardez les films à regarder"},
                {"⭐", "Notes & Avis",             "Notez et commentez vos films vus"},
                {"🤖", "CineAI — Gemini",         "Assistant IA pour vos recommandations"},
        };

        int[] featureIds = {
                R.id.feature1, R.id.feature2,
                R.id.feature3, R.id.feature4, R.id.feature5
        };

        for (int i = 0; i < featureIds.length; i++) {
            View item = view.findViewById(featureIds[i]);
            ((TextView) item.findViewById(R.id.textIcon)).setText(features[i][0]);
            ((TextView) item.findViewById(R.id.textFeatureTitle)).setText(features[i][1]);
            ((TextView) item.findViewById(R.id.textFeatureDesc)).setText(features[i][2]);
        }
    }
}