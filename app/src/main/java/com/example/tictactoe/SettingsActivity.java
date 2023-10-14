package com.example.tictactoe;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private String boardSize = "3x3";
    private String player1Name = "Player 1 Name";
    private String player2Name = "Player 2 Name";
    private Spinner boardSizeSpinner;
    private Spinner winningFieldsSpinner;
    private ImageView player1CharImage;
    private ImageView player2CharImage;
    private View player1Box;
    private View player2Box;
    private char player1Char = 'X';
    private ArrayAdapter<String> defaultFieldsAdapter;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setSpinners();
        setPlayerNameTextWatchers();
        setCenterImageClickListener();
        setPlayButtonClickListener();
    }

    // Inicjalizacja różnych komponentów interfejsu użytkownika i znajdowanie widoków na podstawie ich identyfikatorów.
    private void initializeViews() {
        boardSizeSpinner = findViewById(R.id.boardSizeSpinner);
        winningFieldsSpinner = findViewById(R.id.winningFieldsSpinner);
        player1Box = findViewById(R.id.player1Box);
        player2Box = findViewById(R.id.player2Box);
        player1CharImage = player1Box.findViewById(R.id.charImage);
        player2CharImage = player2Box.findViewById(R.id.charImage);

        // Ustawienie początkowych obrazów znaków graczy i obrazów postaci graczy.
        player1CharImage.setImageResource(R.drawable.cross);
        player2CharImage.setImageResource(R.drawable.circle);

        ImageView player1Image = player1Box.findViewById(R.id.playerImage);
        ImageView player2Image = player2Box.findViewById(R.id.playerImage);
        player1Image.setImageResource(R.drawable.player);
        player2Image.setImageResource(R.drawable.player);

        TextView player1NameText = player1Box.findViewById(R.id.playerName);
        player1NameText.setText(player1Name);

        TextView player2NameText = player2Box.findViewById(R.id.playerName);
        player2NameText.setText(player2Name);

        mediaPlayer = MediaPlayer.create(this, R.raw.click);
    }

    // Konfiguruje spinnerów dla rozmiaru planszy i liczby pól do wygrania.
    private void setSpinners() {
        // Konfiguruje boardSizeSpinner z dostępnymi opcjami.
        String[] boardSizeOptions = {"Wybierz rozmiar planszy", "3x3", "4x4", "5x5"};
        ArrayAdapter<String> boardSizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, boardSizeOptions);
        boardSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boardSizeSpinner.setAdapter(boardSizeAdapter);

        defaultFieldsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Wybierz liczbę pól do wygrania"});
        defaultFieldsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        winningFieldsSpinner.setAdapter(defaultFieldsAdapter);

        boardSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                handleBoardSizeSelection(parentView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                winningFieldsSpinner.setEnabled(false);
                winningFieldsSpinner.setAdapter(defaultFieldsAdapter); // Reset drugiego spinnera
            }
        });
    }

    // Obsługuje wybór rozmiaru planszy i aktualizuje winningFieldsSpinner zgodnie z wyborem.
    private void handleBoardSizeSelection(AdapterView<?> parentView) {
        String selectedSize = (String) parentView.getItemAtPosition(parentView.getSelectedItemPosition());
        if (!selectedSize.equals("Wybierz rozmiar planszy")) {
            winningFieldsSpinner.setEnabled(true);
            int maxWinningFields = Integer.parseInt(selectedSize.substring(0, 1));
            fillWinningFieldsSpinner(maxWinningFields);
        } else {
            winningFieldsSpinner.setEnabled(false);
            winningFieldsSpinner.setAdapter(defaultFieldsAdapter); // Reset drugiego spinnera
        }
    }

    // Wypełnia winningFieldsSpinner dostępnymi opcjami na podstawie wybranego rozmiaru planszy.
    private void fillWinningFieldsSpinner(int maxFields) {
        String[] winningFieldsOptions = new String[maxFields - 1];
        winningFieldsOptions[0] = "Wybierz liczbę pól do wygrania";
        for (int i = 3; i <= maxFields; i++) {
            winningFieldsOptions[i - 2] = String.valueOf(i);
        }

        ArrayAdapter<String> winningFieldsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, winningFieldsOptions);
        winningFieldsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        winningFieldsSpinner.setAdapter(winningFieldsAdapter);
    }

    // Ustawia TextWatchers do pól wprowadzania nazw graczy, aby aktualizować nazwy graczy w czasie rzeczywistym.
    private void setPlayerNameTextWatchers() {
        EditText player1EditText = findViewById(R.id.player1Name);
        EditText player2EditText = findViewById(R.id.player2Name);

        player1EditText.addTextChangedListener(playerNameTextWatcher(player1Box));
        player2EditText.addTextChangedListener(playerNameTextWatcher(player2Box));
    }

    // Tworzy TextWatcher do aktualizacji nazw graczy w czasie rzeczywistym.
    private TextWatcher playerNameTextWatcher(final View playerBox) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredText = s.toString();
                TextView playerNameText = playerBox.findViewById(R.id.playerName);
                playerNameText.setText(enteredText);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    // Ustawia OnClickListener dla centerImage w celu przełączania się między "X" a "O" dla Gracza 1.
    private void setCenterImageClickListener() {
        ImageView centerImage = findViewById(R.id.centerImage);
        centerImage.setImageResource(R.drawable.change);
        centerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player1CharImage.setImageResource(player1Char == 'X' ? R.drawable.circle : R.drawable.cross);
                player2CharImage.setImageResource(player1Char == 'X' ? R.drawable.cross : R.drawable.circle);
                player1Char = player1Char == 'X' ? 'O' : 'X';
            }
        });
    }

    // Ustawia OnClickListener dla przycisku "Play" w celu rozpoczęcia gry z wybranymi ustawieniami.
    private void setPlayButtonClickListener() {
        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player1Name = getPlayerName(R.id.player1Name);
                player2Name = getPlayerName(R.id.player2Name);
                boardSize = boardSizeSpinner.getSelectedItem().toString();
                
                mediaPlayer.start();

                if (isInputValid()) {
                    startGameActivity();
                }
            }
        });
    }

    // Pobiera tekst z pola wprowadzania nazwy gracza na podstawie jego ID.
    private String getPlayerName(int editTextId) {
        EditText editText = findViewById(editTextId);
        return editText.getText().toString();
    }

    // Sprawdza poprawność wprowadzonych danych (nazw graczy i rozmiaru planszy).
    private boolean isInputValid() {
        if (player1Name.length() < 3 || player2Name.length() < 3) {
            showToast("Nazwa powinna zawierać przynajmniej 3 znaki");
            return false;
        } else if (player1Name.length() > 16 || player2Name.length() > 16) {
            showToast("Nazwa powinna zawierać maksymalnie 16 znaków");
            return false;
        } else if (boardSize.equals("Wybierz rozmiar planszy")) {
            showToast("Wybierz rozmiar Planszy");
            return false;
        }
        return true;
    }

    // Wyświetla krótką wiadomość typu Toast.
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Rozpoczyna aktywność gry z wybranymi ustawieniami.
    private void startGameActivity() {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra("previousActivity", "SettingActivity");

        intent.putExtra("player1Name", player1Name);
        intent.putExtra("player2Name", player2Name);
        intent.putExtra("player1Char", player1Char);
        intent.putExtra("boardSize", boardSize);

        if (!winningFieldsSpinner.getSelectedItem().toString().equals("Wybierz liczbę pól do wygrania")) {
            int howFields = Integer.parseInt(winningFieldsSpinner.getSelectedItem().toString());
            intent.putExtra("howFields", howFields);
            startActivity(intent);
        } else {
            showToast("Wybierz liczbę pól do wygrania");
        }
    }
}
