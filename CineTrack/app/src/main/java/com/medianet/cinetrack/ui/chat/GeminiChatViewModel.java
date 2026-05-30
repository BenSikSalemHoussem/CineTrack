package com.medianet.cinetrack.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.medianet.cinetrack.data.api.model.Movie;
import com.medianet.cinetrack.data.repository.MovieRepository;
import com.medianet.cinetrack.utils.Constants;
import com.medianet.cinetrack.utils.UiState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiChatViewModel extends ViewModel {

    private final MovieRepository movieRepo = new MovieRepository();
    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> movieCount = new MutableLiveData<>(0);

    private final GenerativeModelFutures geminiModel;
    private final Executor bgExecutor = Executors.newSingleThreadExecutor();
    private ChatFutures chat;
    private boolean initialized = false;

    public GeminiChatViewModel() {
        GenerativeModel gm = new GenerativeModel("gemini-3-flash-preview", Constants.GEMINI_API_KEY);
        geminiModel = GenerativeModelFutures.from(gm);
    }

    public LiveData<List<ChatMessage>> getMessages()    { return messages; }
    public LiveData<Boolean>          getIsLoading()    { return isLoading; }
    public LiveData<Integer>          getMovieCount()   { return movieCount; }

    public void init() {
        if (initialized) return;
        initialized = true;

        LiveData<UiState<List<Movie>>> liveData = movieRepo.getTop100Movies();
        Observer<UiState<List<Movie>>> observer = new Observer<UiState<List<Movie>>>() {
            @Override
            public void onChanged(UiState<List<Movie>> state) {
                if (state.status == UiState.Status.SUCCESS && state.data != null) {
                    liveData.removeObserver(this);
                    setupChat(state.data);
                } else if (state.status == UiState.Status.ERROR) {
                    liveData.removeObserver(this);
                    setupFallbackChat();
                }
            }
        };
        liveData.observeForever(observer);
    }

    private void setupChat(List<Movie> movies) {
        movieCount.postValue(movies.size());

        StringBuilder ctx = new StringBuilder(
            "Tu es CineBot, un assistant cinéma dans l'application CineTrack. " +
            "Tu connais les films suivants (IMDb Top 100) :\n\n"
        );
        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);
            ctx.append(i + 1).append(". ").append(m.getTitle())
               .append(" (").append(m.getYear() != null ? m.getYear() : "?").append(")");
            if (m.getRating() != null) ctx.append(" ★").append(m.getRating());
            if (m.getGenre() != null && !m.getGenre().isEmpty())
                ctx.append(" • ").append(m.getGenre().get(0));
            ctx.append("\n");
        }
        ctx.append("\nRéponds toujours en français, de façon concise et amicale. " +
                   "Quand tu suggères un film, indique son titre, son année et sa note. " +
                   "Utilise uniquement les films de cette liste.");

        String welcome = "Bonjour ! Je suis CineBot 🎬\n" +
                "Je connais " + movies.size() + " films du Top IMDb. " +
                "Dites-moi ce que vous aimez (genre, ambiance, acteur…) et je vous ferai des suggestions !";

        List<Content> history = new ArrayList<>();
        history.add(buildContent("user", ctx.toString()));
        history.add(buildContent("model", welcome));

        chat = geminiModel.startChat(history);

        List<ChatMessage> init = new ArrayList<>();
        init.add(new ChatMessage(welcome, ChatMessage.Type.AI));
        messages.postValue(init);
    }

    private void setupFallbackChat() {
        chat = geminiModel.startChat(new ArrayList<>());
        List<ChatMessage> init = new ArrayList<>();
        init.add(new ChatMessage(
            "Bonjour ! Je suis CineBot 🎬 Comment puis-je vous aider ?",
            ChatMessage.Type.AI));
        messages.postValue(init);
    }

    public void sendMessage(String text) {
        if (chat == null || text.trim().isEmpty()) return;

        List<ChatMessage> current = new ArrayList<>(
            messages.getValue() != null ? messages.getValue() : new ArrayList<>()
        );
        current.add(new ChatMessage(text, ChatMessage.Type.USER));
        current.add(new ChatMessage("", ChatMessage.Type.LOADING));
        messages.setValue(current);
        isLoading.setValue(true);

        Content userContent = buildContent("user", text);

        ListenableFuture<GenerateContentResponse> future = chat.sendMessage(userContent);
        Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String reply = result.getText() != null
                    ? result.getText()
                    : "Désolé, je n'ai pas pu répondre.";
                replaceLoadingWith(new ChatMessage(reply, ChatMessage.Type.AI));
            }

            @Override
            public void onFailure(Throwable t) {
                replaceLoadingWith(new ChatMessage(
                    "Erreur : " + t.getMessage(), ChatMessage.Type.AI));
            }
        }, bgExecutor);
    }

    private static Content buildContent(String role, String text) {
        Content.Builder builder = new Content.Builder();
        builder.setRole(role);
        builder.addText(text);
        return builder.build();
    }

    private void replaceLoadingWith(ChatMessage msg) {
        List<ChatMessage> updated = new ArrayList<>(
            messages.getValue() != null ? messages.getValue() : new ArrayList<>()
        );
        if (!updated.isEmpty()) updated.remove(updated.size() - 1);
        updated.add(msg);
        messages.postValue(updated);
        isLoading.postValue(false);
    }
}
