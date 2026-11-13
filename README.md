# Simple Chess Game

This is a chess project I made to learn Kotlin and get familiar with mobile development. It is a simplified chess game focusing on piece movement and capturing.

## Instructions for Build and Use

Steps to build and/or run the software:

1. Install JDK 25 or a compatible newer JDK.
2. Open a terminal in the project root directory.
3. Build the project and run its tests:

   ```bash
   ./gradlew build
   ```

   On Windows, use `gradlew.bat build` instead.

4. Launch the desktop application:

   ```bash
   ./gradlew run
   ```

   On Windows, use `gradlew.bat run` instead.

Instructions for using the software:

1. When the game opens, White moves first. The current player is shown above the board.
2. Click one of the current player’s pieces to select it. The selected square is highlighted.
3. Click a destination square to move the piece. Valid moves update the board and switch turns; invalid moves display a readable error message.
4. Capture an opposing piece by moving onto its square. Friendly pieces cannot be captured.
5. Use the `Reset` control in the upper-right area to restore the starting board and begin again.

## Development Environment

To recreate the development environment, you need the following software and/or libraries with the specified versions:

* JDK 25. The Gradle build is configured with `jvmToolchain(25)`.
* Gradle 9.6.0, provided through the included Gradle Wrapper.
* Kotlin JVM 2.4.20.
* JetBrains Compose Multiplatform 1.12.2 with Compose Desktop for the current operating system.
* Compose Material 3 and Kotlin Test, resolved by Gradle from the configured repositories.

## Useful Websites to Learn More

I found these websites useful in developing this software:

* [YouTube](https://youtu.be/iYrgWO2oibY?si=Y5oZz6YPEpbO_VUG)
* [YouTube](https://youtu.be/KvehHqnEXuc?si=0PB3PF7hHuk5oOyN)
* [YouTube](https://youtu.be/eXT1wglHAQo?si=CtkCH7EII5gbDE02)

## Future Work

The following items I plan to fix, improve, and/or add to this project in the future:

### Standard chess functionality

* [x] Detect checkmate.
* [x] Detect stalemate.
* [x] Detect insufficient-material draws.
* [x] Detect the fifty-move rule.
* [ ] Detect threefold repetition.
* [ ] Add pawn promotion with a choice of promoted piece.
* [ ] Add castling, including the restrictions on the king and rook movement.
* [x] Add en passant captures.
* [ ] Add move history and standard algebraic move notation.
* [ ] Add multi-move history.

### Other improvements

* [ ] Add an AI opponent with selectable difficulty levels.
* [ ] Add networked multiplayer.
* [ ] Add save and load for games in progress.
* [ ] Add move animations and optional sound effects.
* [ ] Add selectable board themes and piece sets.
* [ ] Improve accessibility with keyboard navigation and additional screen-reader support.
* [ ] Add broader end-to-end UI tests and continuous integration checks.
