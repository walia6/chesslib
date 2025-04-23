# chesslib

**chesslib** is a fast, immutable, and dependency-free Java library for modeling and analyzing chess games at scale. Built for performance and correctness, it powers large-scale analysis pipelines like [Chess Visualizer](https://github.com/walia6/chessvisualizer), which processes millions of games to uncover spatial trends in checkmates and draws.

📚 **Javadocs**:  
👉 [https://walia6.github.io/chesslib/index.html](https://walia6.github.io/chesslib/index.html)

---

## Features

- 🧊 Immutable `Position` objects for safe concurrent analysis  
- ♟️ Full legal move generation (castling, en passant, promotions, etc.)  
- 🔁 FEN and SAN parsing/generation  
- 📜 PGN parsing support (including bulk PGN stream handling)  
- 🧠 Clean separation of game state, piece behavior, and utility logic  
- ⚙️ Static utility classes — no runtime dependencies  
- ✅ Extensive JSON-driven and perft test coverage  

---

## Installation

```bash
git clone https://github.com/walia6/chesslib.git
cd chesslib
mvn clean install
```

---

## Example Usage

### Load and inspect a position

```java
import gg.w6.chesslib.model.Position;
import gg.w6.chesslib.util.FenParser;

Position pos = FenParser.parse("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

System.out.println(pos.getToMove()); // WHITE
System.out.println(pos.getSquare("e2").getPiece()); // Pawn
System.out.println(pos.getCastlingRights().whiteKingside()); // true
```

---

### Parse a SAN move and apply it

```java
import gg.w6.chesslib.model.Position;
import gg.w6.chesslib.model.Move;
import gg.w6.chesslib.util.FenParser;
import gg.w6.chesslib.util.SanParser;

Position start = FenParser.parse("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1");

Move response = SanParser.parse("c5", start);
Position next = start.applyTo(response);

System.out.println(next.generateFEN());
```

---

### Generate and filter legal moves

```java
import gg.w6.chesslib.model.Position;
import gg.w6.chesslib.util.MoveGenerator;
import gg.w6.chesslib.model.Move;
import gg.w6.chesslib.util.Moves;

Position pos = FenParser.parse("rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");

for (Move m : MoveGenerator.getLegalMoves(pos)) {
    if (Moves.isCapture(m, pos)) {
        System.out.println("Capture: " + m);
    }
}
```

---

### Create positions programmatically

```java
import gg.w6.chesslib.util.PositionBuilder;
import gg.w6.chesslib.model.Coordinate;
import gg.w6.chesslib.model.piece.King;
import gg.w6.chesslib.model.piece.Rook;
import gg.w6.chesslib.model.Color;

PositionBuilder builder = new PositionBuilder();
builder.setToMove(Color.WHITE);
builder.setWhiteKingsideRight(true);
builder.setBlackKingsideRight(true);
builder.addPiece(new King(Color.WHITE), Coordinate.valueOf("e1"));
builder.addPiece(new Rook(Color.WHITE), Coordinate.valueOf("h1"));
builder.addPiece(new King(Color.BLACK), Coordinate.valueOf("e8"));
builder.addPiece(new Rook(Color.BLACK), Coordinate.valueOf("h8"));

Position position = builder.toPosition();
System.out.println(position.generateFEN());
```

---

### Parse a PGN into a structured game record

```java
import gg.w6.chesslib.util.PgnParser;
import gg.w6.chesslib.model.Game;

String pgn = """
[Event "Rated Blitz game"]
[Site "https://lichess.org/xyz"]
[Date "2023.01.01"]
[Round "-"]
[White "Player1"]
[Black "Player2"]
[Result "1-0"]
[UTCDate "2023.01.01"]
[UTCTime "12:00:00"]
[WhiteElo "1500"]
[BlackElo "1450"]
[Variant "Standard"]
[TimeControl "300+0"]
[Termination "Normal"]

1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6 5. O-O Be7 6. Re1 b5 7. Bb3 d6 8. c3 O-O 9. h3
""";

Game game = PgnParser.parse(pgn);
System.out.println("White: " + game.white());
System.out.println("Moves: " + game.sanStrings());
```

---

## See It in Action

Check out [Chess Visualizer](https://github.com/walia6/chessvisualizer), a companion project that uses `chesslib` to analyze PGNs and generate heatmaps showing where pieces most often land in checkmate and drawn games.

![Heatmap Output](https://github.com/walia6/chessvisualizer/raw/main/output.png)

---

## License

MIT — see [LICENSE](./LICENSE)
