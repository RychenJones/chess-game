# Agent Team

This project is a simplified Kotlin chess game. The agent team is organized around the MVP in `README.md`: display the board, move pieces using basic movement rules, capture opposing pieces, alternate turns, show selection/current player, and reset the game.

## Scope

In scope:

- Board and piece state
- Basic movement and capture validation
- Turn management and reset behavior
- Board interaction and player feedback
- Buildable, playable MVP behavior
- Tests, review, and integration

Out of scope unless the user explicitly expands the project:

- Check and checkmate
- King safety
- Promotion, castling, and en passant
- Computer opponents or AI
- Networked multiplayer

## Agent roster

| Agent | Primary responsibility |
| --- | --- |
| Architect | Primary user-facing planning agent; discusses requested features and turns approved ideas into implementation requests |
| Orchestrator | Breaks implementation requests into ordered work packages, coordinates agent handoffs, and returns the implementation summary to the user |
| UI Agent | Owns board presentation, interaction, feedback, and accessibility/UX |
| Game Model Agent | Owns board, piece, turn, capture, and reset state |
| Rules Agent | Owns basic piece movement, path blocking, and move validation |
| QA/Review Agent | Combines test design, acceptance verification, regression checking, and code review |
| Integration Agent | Connects agent outputs and verifies the complete playable flow |

## Standard workflow

1. The Architect, as the primary user-facing planning agent, clarifies the requested feature and records acceptance criteria.
2. The Architect asks the Orchestrator to implement an approved request.
3. The Orchestrator assigns focused work to the Game Model, Rules, UI, and other relevant agents.
4. The Integration Agent combines compatible changes and checks cross-agent contracts.
5. The QA/Review Agent tests the result and reviews it against the acceptance criteria.
6. The Orchestrator sends failures back to the responsible agent and repeats verification.
7. The Orchestrator reports the completed result to the user, including a one-paragraph summary of what each participating agent did.

## Handoff contract

Every agent should state:

- The task it received
- Assumptions and constraints
- Files or interfaces it changed
- Tests or checks performed
- Remaining risks or follow-up work

The Orchestrator's final user-facing report must include one concise paragraph for each participating agent explaining that agent's contribution to the task. The report should also include verification status and any unresolved risks.

Agents should keep changes within their ownership area and flag cross-cutting changes to the Orchestrator.
