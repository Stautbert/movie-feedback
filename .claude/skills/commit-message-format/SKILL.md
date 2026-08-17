---
name: commit-message-format
description: Defines and checks this repository's commit message standard (summary line, why-focused body, trailers). Use whenever writing a commit message for this project, or when reviewing a diff/PR that includes commits, to verify messages meet the standard.
---

# Commit message format

This repo's early history is inconsistent ("Fixed UI Issue", "Code Fix",
"Modified all config files") — those are exactly the anti-patterns this
skill exists to stop. Commits from this point forward follow the format
below.

## Structure

```
<imperative summary, ≤72 chars, no trailing period>

<body: why this change, wrapped at ~72-100 chars per line>

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
```

- **Summary line**: imperative mood ("Fix", "Add", "Remove" — not "Fixed",
  "Adds", "Removed"). States what changed, specifically enough to act as
  a changelog entry on its own. No vague summaries ("Code Fix", "Update
  stuff", "WIP").
- **Body**: required for anything beyond a trivial change (typo, formatting).
  Explains *why* the change was needed — the bug's root cause, the
  requirement driving it, what would break without it — not a restatement
  of the diff. If the change fixes a bug, describe the failure mode
  (what broke, for whom, under what conditions), matching the standard
  already set by `74d2e21` and `e16591e` in this repo's history.
  Bullet points are fine for multi-part changes; prose is fine for a
  single coherent change.
- **Trailer**: `Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>`
  when the commit was AI-assisted (this is the existing convention in
  this repo's own history). Omit for commits written entirely by a human
  without AI assistance.

## What NOT to do

- Don't describe *what* the diff does line-by-line — the diff already
  shows that. Describe intent and consequence instead.
- Don't bundle unrelated changes under one vague summary — split into
  separate commits if the changes aren't part of one coherent fix/feature.
- Don't reference internal conversation context ("as discussed", "per
  Claude's suggestion") — the message should stand alone for someone
  reading `git log` with no other context.

## Checking an existing commit message

When reviewing a commit (e.g. as part of a code review), verify:
1. Summary is imperative mood, specific, ≤72 chars.
2. A body is present unless the change is genuinely trivial, and it
   explains motivation/root cause rather than restating the diff.
3. No vague/generic summaries matching the anti-pattern list above.
4. `Co-Authored-By` trailer present if the change was AI-assisted.

Flag violations by quoting the offending message and the specific rule
it breaks — not just "commit message could be better."
