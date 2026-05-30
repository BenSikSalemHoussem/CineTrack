package com.medianet.cinetrack.ui.chat;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.medianet.cinetrack.R;
import com.medianet.cinetrack.adapter.ChatAdapter;

public class GeminiChatFragment extends Fragment {

    private GeminiChatViewModel viewModel;
    private ChatAdapter adapter;

    private RecyclerView recyclerMessages;
    private EditText editMessage;
    private ImageButton btnSend;
    private TextView textMovieCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gemini_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerMessages = view.findViewById(R.id.recyclerMessages);
        editMessage      = view.findViewById(R.id.editMessage);
        btnSend          = view.findViewById(R.id.btnSend);
        textMovieCount   = view.findViewById(R.id.textMovieCount);

        viewModel = new ViewModelProvider(this).get(GeminiChatViewModel.class);
        adapter   = new ChatAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerMessages.setLayoutManager(layoutManager);
        recyclerMessages.setAdapter(adapter);

        viewModel.getMessages().observe(getViewLifecycleOwner(), msgs -> {
            adapter.setMessages(msgs);
            if (!msgs.isEmpty()) {
                recyclerMessages.scrollToPosition(msgs.size() - 1);
            }
        });

        viewModel.getMovieCount().observe(getViewLifecycleOwner(), count -> {
            if (count > 0) textMovieCount.setText(count + " films");
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            btnSend.setEnabled(!loading);
            btnSend.setAlpha(loading ? 0.5f : 1f);
        });

        btnSend.setOnClickListener(v -> sendMessage());

        editMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN)) {
                sendMessage();
                return true;
            }
            return false;
        });

        viewModel.init();
    }

    private void sendMessage() {
        String text = editMessage.getText().toString().trim();
        if (!text.isEmpty()) {
            editMessage.setText("");
            viewModel.sendMessage(text);
        }
    }
}
