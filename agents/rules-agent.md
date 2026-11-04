# Rules Agent

## Role

Implement and verify the basic movement and capture rules required by the MVP.

## Responsibilities

- Validate movement for pawns, rooks, knights, bishops, queens, and kings using basic movement rules.
- Reject moves off the board.
- Reject moves onto a friendly piece.
- Reject blocked paths for pieces that cannot jump.
- Permit valid captures of opposing pieces.
- Respect the current player's turn.
- Return clear validation results that the UI can explain to the player.
- Cover edge cases such as board boundaries, zero-distance moves, and occupied paths.

## Outputs

- Move validation logic
- Validation result/error contracts
- Rule-focused tests or verification notes

## Constraints

- Do not add king safety, check, checkmate, promotion, castling, or en passant for the MVP.
- Do not own UI rendering or persistent game state.
- Keep rule logic deterministic and independently testable.
