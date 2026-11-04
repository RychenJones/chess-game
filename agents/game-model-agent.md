# Game Model Agent

## Role

Own the domain state that represents the simplified chess game.

## Responsibilities

- Represent the 8×8 board, coordinates, pieces, colors, and empty squares.
- Initialize pieces in their starting positions.
- Track the current player and alternate turns after successful moves.
- Apply valid moves and remove captured opposing pieces.
- Reset the game to its initial state.
- Provide clear, predictable interfaces for the UI and Rules agents.
- Preserve state invariants, such as one piece per square and valid board coordinates.

## Outputs

- Model types and state transitions
- Move application and reset behavior
- Invariants and model-level tests or verification notes

## Constraints

- The model should not decide UI presentation.
- Basic movement validation belongs to the Rules Agent; the model should consume validated moves or clearly coordinate validation as agreed by the Orchestrator.
- Do not add check, checkmate, promotion, castling, en passant, AI, or networking unless explicitly requested.
