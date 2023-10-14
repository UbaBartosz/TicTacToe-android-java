package com.example.tictactoe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    private String boardSize;
    private GridLayout gridLayout;
    private int rows, cols, howFields;
    private String player1Name, player2Name;
    private char player1Char, player2Char;
    private Boolean turnPlayer1;
    private View player1Box;
    private View player2Box;
    private Button playAgainButton;
    private Activity activity;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        activity = this;

        initializeViews();
        setPlayerFrame(turnPlayer1, !turnPlayer1);

        gridLayout = findViewById(R.id.gridLayoutBoard);
        boardSize = getIntent().getStringExtra("boardSize");
        howFields = getIntent().getIntExtra("howFields", 0);

        rows = Integer.parseInt(String.valueOf(boardSize.charAt(0)));
        cols = rows;
        createGridLayout(rows, cols);
    }

    // Inicjalizacja widoków graczy oraz ich parametrów na początku gry.
    private void initializeViews() {
        player1Name = getIntent().getStringExtra("player1Name");
        player2Name = getIntent().getStringExtra("player2Name");

        player1Char = getIntent().getCharExtra("player1Char", 'O');
        player2Char = player1Char == 'X' ? 'O' : 'X';
        turnPlayer1 = (player1Char == 'X');

        player1Box = findViewById(R.id.player1Box);
        player2Box = findViewById(R.id.player2Box);

        TextView player1NameText = player1Box.findViewById(R.id.playerName);
        player1NameText.setText(player1Name);

        TextView player2NameText = player2Box.findViewById(R.id.playerName);
        player2NameText.setText(player2Name);

        ImageView player1CharImage = player1Box.findViewById(R.id.charImage);
        ImageView player2CharImage = player2Box.findViewById(R.id.charImage);
        player1CharImage.setImageResource(player1Char == 'X' ? R.drawable.cross : R.drawable.circle);
        player2CharImage.setImageResource(player1Char == 'X' ? R.drawable.circle : R.drawable.cross);

        ImageView player1Image = player1Box.findViewById(R.id.playerImage);
        ImageView player2Image = player2Box.findViewById(R.id.playerImage);
        player1Image.setImageResource(R.drawable.player);
        player2Image.setImageResource(R.drawable.player);

        mediaPlayer = MediaPlayer.create(this, R.raw.click);
    }

    // Tworzenie planszy do gry o określonych wymiarach.
    private void createGridLayout(int rows, int cols) {
        gridLayout.removeAllViews();
        gridLayout.setRowCount(rows);
        gridLayout.setColumnCount(cols);

        int marginInPixels = 10;

        for (int i = 0; i < rows * cols; i++) {
            ImageView imageView = createGridCell(marginInPixels, i);
            gridLayout.addView(imageView);
        }

        playAgainButton = findViewById(R.id.playAgainButton);
        playAgainButton.setVisibility(View.GONE);

        Button endGameButton = findViewById(R.id.endGameButton);
        endGameButton.setOnClickListener(v -> displayEnd());
    }

    // Tworzenie i inicjalizacja komórki planszy (ImageView) do gry w kółko i krzyżyk.
    private ImageView createGridCell(int marginInPixels, int index) {
        ImageView imageView = new ImageView(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels);

        int col = index % cols;
        int row = index / cols;

        params.columnSpec = GridLayout.spec(col, 1, 1f);
        params.rowSpec = GridLayout.spec(row, 1);

        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageResource(R.drawable.empty_image);
        imageView.setBackgroundResource(R.drawable.empty);

        int paddingInPixels = 15;
        imageView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);

        imageView.setTag("empty");

        imageView.setOnClickListener(v -> handleCellClick(imageView));

        imageView.setAdjustViewBounds(true);
        return imageView;
    }

    // Obsługa kliknięcia w komórkę planszy i aktualizacja stanu gry.
    private void handleCellClick(ImageView imageView) {
        if (imageView.getTag().toString().equals("empty")) {
            setPlayerImageAndTag(imageView, player1Char, turnPlayer1);
            turnPlayer1 = !turnPlayer1;
            setPlayerFrame(turnPlayer1, !turnPlayer1);

            if (isGameWon(gridLayout, player1Char, howFields)) {
                handleGameWin(player1Name);
                setPlayerFrame(true, false);
            } else if (isGameWon(gridLayout, player2Char, howFields)) {
                handleGameWin(player2Name);
                setPlayerFrame(false, true);
            } else if (isBoardFull(gridLayout)) {
                handleGameDraw();
                setPlayerFrame(true, true);
            }
        }
    }

    // Sprawdzenie, czy plansza jest pełna (czy wszystkie komórki są zajęte).
    private boolean isBoardFull(GridLayout gridLayout) {
        int childCount = gridLayout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) gridLayout.getChildAt(i);
            String tag = (String) imageView.getTag();
            if (tag == null || tag.equals("empty")) {
                return false;
            }
        }

        return true;
    }

    // Sprawdzenie, czy gra została wygrana przez danego gracza na podstawie aktualnego stanu planszy.
    private boolean isGameWon(GridLayout gridLayout, char playerSign, int howFieldsToWin) {
        int[][] directions = {
                {0, 1},   // Poziomo
                {1, 0},   // Pionowo
                {1, 1},   // Przekątna od lewego górnego rogu do prawego dolnego rogu
                {1, -1}   // Przekątna od prawego górnego rogu do lewego dolnego rogu
        };

        for (int[] dir : directions) {
            int dx = dir[0];
            int dy = dir[1];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    int consecutiveCount = 0;  //ile pól się zgadza
                    for (int k = 0; k < howFieldsToWin; k++) {
                        int x = i + k * dx;
                        int y = j + k * dy;

                        if (x >= 0 && x < rows && y >= 0 && y < cols) {
                            ImageView cell = (ImageView) gridLayout.getChildAt(x * cols + y);
                            String tag = (String) cell.getTag();
                            if (tag != null && tag.equals(String.valueOf(playerSign))) {
                                consecutiveCount++;
                                if (consecutiveCount == howFieldsToWin) {
                                    highlightWinningCells(i, j, dx, dy, howFieldsToWin);
                                    return true;
                                }
                            } else {
                                consecutiveCount = 0;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    // Podświetla wygrywające komórki planszy po wygranej grze przez gracza.
    private void highlightWinningCells(int startRow, int startCol, int dx, int dy, int howFieldsToWin) {
        for (int k = 0; k < howFieldsToWin; k++) {
            int x = startRow + k * dx;
            int y = startCol + k * dy;

            ImageView winningCell = (ImageView) gridLayout.getChildAt(x * cols + y);
            winningCell.setBackgroundResource(R.drawable.win_field);
        }
    }

    // Obsługuje wygraną gry, wyświetla odpowiednie powiadomienie i kończy rozgrywkę.
    private void handleGameWin(String winnerName) {
        showToast(winnerName + " wygrywa!");
        endGame();
    }

    // Obsługuje remis gry, wyświetla odpowiednie powiadomienie i kończy rozgrywkę.
    private void handleGameDraw() {
        showToast("Remis!");
        endGame();
    }

    // Kończy rozgrywkę, ustawiając tagi komórek na "end", wyświetla przycisk "Zagraj ponownie" i obsługuje jego kliknięcie.
    private void endGame() {
        for (int i = 0; i < rows * cols; i++) {
            ImageView cell = (ImageView) gridLayout.getChildAt(i);
            cell.setTag("end");
        }
        playAgainButton.setVisibility(View.VISIBLE);
        playAgainButton.setOnClickListener(v -> restartGame());
    }

    // Rozpoczyna nową grę, inicjalizując ją na nowo i przechodząc do aktywności gry.
    private void restartGame() {
        mediaPlayer.start();
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra("previousActivity", "GameActivity");
        intent.putExtra("player1Name", player1Name);
        intent.putExtra("player2Name", player2Name);
        intent.putExtra("player1Char", player1Char);
        intent.putExtra("boardSize", boardSize);
        intent.putExtra("howFields", howFields);
        startActivity(intent);
        finish();
    }

    // Ustala obraz gracza i ustawia odpowiedni tag komórki planszy.
    public void setPlayerImageAndTag(ImageView imageView, char playerSign, boolean player1Turn) {
        char currentPlayerSign = player1Turn ? playerSign : (playerSign == 'X' ? 'O' : 'X');
        imageView.setImageResource(currentPlayerSign == 'X' ? R.drawable.cross : R.drawable.circle);
        imageView.setTag(String.valueOf(currentPlayerSign));
    }

    // Ustala wygląd ramki (kolor) gracza w interfejsie użytkownika.
    public void setPlayerFrame(boolean player1Turn, boolean player2Turn) {
        int player1Resource = player1Turn ? R.drawable.circle_green : R.drawable.circle_yellow;
        int player2Resource = player2Turn ? R.drawable.circle_green : R.drawable.circle_yellow;

        player1Box.findViewById(R.id.circleView).setBackgroundResource(player1Resource);
        player2Box.findViewById(R.id.circleView).setBackgroundResource(player2Resource);
    }

    // Wyświetla krótkie powiadomienie typu Toast z określoną wiadomością.
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // Wyświetla potwierdzający komunikat o zakończeniu gry i obsługuje odpowiedzi gracza.
    public void displayEnd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Czy na pewno chcesz zakończyć grę?")
                .setPositiveButton("Tak", (dialog, id) -> {
                    // Jeśli gracz potwierdzi, przechodzi do aktywności SettingsActivity, aby zakończyć grę.
                    mediaPlayer.start();
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Nie", (dialog, id) -> {
                    mediaPlayer.start();
                    dialog.dismiss(); // Zamyka okno dialogowe.
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
