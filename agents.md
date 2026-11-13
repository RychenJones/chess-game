# Agent Team

This project is a simplified Kotlin chess game. The agent team is organized around the MVP in `README.md`: display the board, move pieces using basic movement rules, capture opposing pieces, alternate turns, show selection/current player, and reset the game.

## Architect Boundary

The Architect must never edit source code, tests, configuration, documentation, or other project files. For implementation requests, it must create an implementation brief, hand it to the Orchestrator, and stop without using file-editing tools. A direct request to implement does not override this boundary; if the Orchestrator is unavailable, the Architect must report the work as blocked.

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
| QA Engineer | Combines test design, acceptance verification, regression checking, and code review |
| Integration Agent | Connects agent outputs and verifies the complete playable flow |

## Standard workflow

1. The Architect is the only agent that conducts requirements discovery with the user. It turns the user's request into an approved task brief and explicit acceptance criteria (AC).
2. The Architect sends the Orchestrator an implementation handoff containing the task brief, AC, scope boundaries, assumptions, dependencies, and risks. The Architect does not implement the change.
3. The Orchestrator decomposes the approved task into ordered work steps and sends each assigned specialist a work package containing its step, the complete applicable AC, required interfaces, constraints, and dependencies.
4. Each specialist (Game Model, Rules, UI, and Integration when assigned) completes its work package, performs its own checks, and directly submits its implementation and completion package to the QA Engineer. The completion package lists the task received, files or interfaces changed, checks performed, results, and remaining risks. A specialist must not report completion to the Orchestrator before QA has reviewed the work.
5. QA evaluates the specialist's submission against the applicable AC and returns an explicit `PASS` or `FAIL` verdict directly to that specialist.
6. On `FAIL`, the specialist fixes the QA findings and directly resubmits the updated implementation and completion package to QA. The specialist owns this repair-and-retest loop and repeats it until QA returns `PASS`.
7. Only after QA returns `PASS` does the specialist report the passing result and completion package to the Orchestrator. The Orchestrator records the verdict, integrates dependent work, and requests any required integration or regression checks.
8. When all assigned work has passed verification, the Orchestrator prepares the final implementation report. The report contains one concise paragraph for every participating agent, overall verification status, unresolved risks, and follow-up work. If the Orchestrator is running as a subagent or asynchronous handoff, it sends that completed report back to the parent/initiating agent, and the parent/initiating agent must faithfully relay it to the user as the final response.

The Orchestrator's final report is a completion output, not requirements discovery. It must not ask the user to define or clarify scope; all user requirements enter through the Architect. The Orchestrator coordinates assignments and records passed reports; it does not act as the QA relay for specialist submissions. Receiving the report is an explicit return-path event: the parent/initiating agent must surface the report rather than replying only that the work was handed off.

## Handoff contract

Every agent should state:

- The task it received
- The inputs it consumed and their source
- Assumptions and constraints
- Files or interfaces it changed
- Tests or checks performed
- Remaining risks or follow-up work

Every agent file must include explicit `Role`, `Inputs`, and `Outputs` sections. Inputs must identify the upstream agent or artifact that provides them; inferred inputs are not sufficient.

The Orchestrator's final user-facing report must include one concise paragraph for each participating agent explaining that agent's contribution to the task. The report should also include verification status and any unresolved risks.

Agents should keep changes within their ownership area and flag cross-cutting changes to the Orchestrator.
