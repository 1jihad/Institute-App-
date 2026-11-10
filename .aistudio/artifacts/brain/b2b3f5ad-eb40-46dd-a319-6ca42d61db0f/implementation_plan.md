# Excel ETA Student App — 21-Box Curriculum & Interactive Rooms Plan

This plan restructures the Excel ETA Student App around your teacher's official **21-Box (Level) Curriculum**, introducing a first-time student academic onboarding flow, sequential **3-star progression** (earning at least **2/3 stars** unlocks the next box, alongside direct Admin Unlock Codes), specialized **4-Room General Boxes** and **2-Room Skills Boxes** (RW & Listening/Speaking), an in-app **Vocabulary Bank with `.xlsx` and `.csv` Teacher Sheet Upload**, and an accurate **On-Device Speech Recognition** evaluator—while preserving your existing **Grades & Mistakes**, **Study Reminders**, **Personal Notes**, and **Flashcards**.

---

## User Review & Confirmed Decisions

> [!IMPORTANT]
> All product decisions—including the new **`.xlsx` and `.csv` Teacher Vocabulary Upload**—have been incorporated into this plan:

- **In-App Vocabulary Bank with `.xlsx` & `.csv` File Upload**:
  - Every box has a dedicated, 100% in-app **Vocabulary** room/tab (nothing opens outside the app).
  - Supports **importing Teacher Vocabulary files in both Excel (`.xlsx`) and CSV (`.csv`) formats** via the system file picker (`ActivityResultContracts.OpenDocument`), as well as pre-loaded starter vocabulary for each of the 21 boxes.
  - Imported `.xlsx` and `.csv` words are stored locally in the Room database (`VocabularyBankEntity`) and automatically power **Room 1 (Vocabulary Bank)**, **Room 3 (Vocabulary Practice drills)**, and the unit's **Flashcards**.
- **First-Time Student Onboarding & Starting Box**: After device activation, first-time students complete a welcoming **Personal & Academic Onboarding** screen (entering their name, academic background, learning goal, and avatar) and start at **Box 1 (Essence)**, with the option to enter an **Admin Activation Code** to jump directly to a higher box.
- **Accurate On-Device Speech Recognition (Skills 2, 4, 6)**: Speaking rooms use Android's on-device `SpeechRecognizer` paired with a multi-pass accuracy scorer (evaluating top-5 N-best recognition candidates, phonetic/token similarity, and required grammar/vocabulary keywords) so students get reliable, encouraging pronunciation and answer grading.
- **Preserved Student Companion Tools**: Each box retains access to **Flashcards**, **Study Reminders & Personal Notes**, **Read-Only Grades & Mistakes**, and **Message Teacher** alongside the new interactive learning rooms.

---

## 1. Overview & Core Concept

- **What It Does**: Guides Excel ETA students through **21 sequential learning boxes** (from *1. Essence* to *21. Skills 6*). Students study inside structured rooms tailored to each box type, import official teacher vocabulary spreadsheets (`.xlsx` or `.csv`) directly into any box, earn up to **3 stars** per box from interactive practice rooms, and automatically unlock the next box as soon as they achieve **2 out of 3 stars** (or unlock any box immediately with an institute Admin Code).
- **Target Audience**: Students enrolled at Excel ETA For Training studying on their registered Android phone.
- **Key Value**: Combines gamified 21-box progression with focused skill rooms—`.xlsx`/`.csv`-powered Vocabulary & Grammar rooms for general levels, Sentence Construction rooms for Reading & Writing courses (*Skills 1, 3, 5 RW*), and Audio Dictation + Voice Q&A rooms for Listening & Speaking courses (*Skills 2, 4, 6*).

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Adaptive Brand Identity & First-Time Student Onboarding**:
   - Dynamic layered canvas and gradient background using the official Excel ETA palette (Deep Navy `#1E246B`, Royal Blue `#1863B8`, Crimson Red `#B8181E`, and Achievement Gold `#F59E0B`).
   - On first launch after device activation, a guided **Student Academic Onboarding Card** prompts the student for their **Full Name**, **Academic/Education Level**, **Target English Goal**, **Avatar**, and an optional **Direct Box Activation Code**. Completing onboarding initializes their learning path at **Box 1 (Essence)** (or their code-unlocked box).
2. **21-Box Gamified Progression Map**:
   - Displays all **21 official boxes** as interactive level chests along a winding progression path.
   - Each box displays its **Box Number (1–21)**, **Title**, **Box Type Badge** (*General 4-Room*, *Skills RW*, or *Skills Listening & Speaking*), and **3-Star Rating Bar (⭐⭐⭐)**.
   - **Unlocking Logic**:
     - **Performance Unlock**: Earning **2/3 stars** (or **3/3 stars**) in Box $N$ automatically unlocks Box $N+1$ with a celebratory reveal banner.
     - **Admin Code Unlock**: Tapping any locked box opens the **Admin Code Dialog** so students with an institute code can unlock that specific box immediately.
3. **Inside a General Box (Boxes 1, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 16, 18, 19, 20) — 4 Sub-Rooms**:
   - **Room 1 — Vocabulary (المفردات) + `.xlsx` / `.csv` Teacher Sheet Upload**:
     - Dedicated in-app Vocabulary Bank featuring categorized word cards, English definitions & Arabic translations, contextual example sentences, and Normal (`1.0x`) & Slow (`0.7x`) offline TTS audio pronunciation buttons.
     - **"Upload Teacher Vocab (`.xlsx` / `.csv`)" Button**: Students can select an Excel `.xlsx` or `.csv` file shared by their teacher (for a single box or a multi-level workbook) to immediately import all rows into the in-app Vocabulary Bank.
   - **Room 2 — Grammar Explanation (شرح القواعد)**: Visual grammar breakdowns, sentence formulas, positive/negative/question structure cards, and spoken example sentences.
   - **Room 3 — Vocabulary Practice (تطبيق المفردات)**: Interactive multi-question vocabulary drills (definition matching, context fill-in-the-blank, and word spelling) dynamically generated from the box's Vocabulary Bank (including any uploaded `.xlsx` / `.csv` words) that award XP and count toward the box's 3-star score.
   - **Room 4 — Grammar Practice (تطبيقات القواعد)**: Interactive grammar application challenges (verb form selection, error fixing, and sentence completion) that award XP and stars toward unlocking the next box.
4. **Inside a Reading & Writing Skills Box (Boxes 4, 10, 17 — `Skills 1 RW`, `Skills 3 RW`, `Skills 5 RW`) — 2 Sub-Rooms**:
   - **Room 1 — Grammar & Structure Explanation (غرفة شرح القواعد)**: Focused instruction on the box's writing & reading grammar rules (e.g., *Past Continuous vs. Past Simple*, *Nominal vs. Verbal Sentence Construction*, *Linking Words & Paragraph Structure*).
   - **Room 2 — Writing & Sentence Construction Practice (غرفة تطبيقات الكتابة والتركيب)**:
     - **Interactive Sentence Reordering**: Tap word tiles in the right sequence to build grammatically valid nominal and verbal sentences.
     - **Guided Sentence Transformation & Writing**: Write/complete sentences applying the target grammar rule (such as Past Continuous) with instant structural verification, scoring points and stars.
5. **Inside a Listening & Speaking Skills Box (Boxes 7, 13, 21 — `Skills 2`, `Skills 4`, `Skills 6`) — 2 Sub-Rooms**:
   - **Room 1 — Listening & Vocabulary Dictation (غرفة الاستماع والتطبيقي المفرداتي)**:
     - Students play spoken audio prompts (Normal or Slow speed), type the exact sentence they hear in real time with live word-by-word accuracy highlighting, add new vocabulary words directly to the box's in-app Vocabulary Bank, or upload a teacher `.xlsx`/`.csv` vocabulary sheet for listening practice.
   - **Room 2 — Speaking & Voice Interaction (غرفة التحدث والتفاعل الصوتي)**:
     - The app asks an audio question aloud; the student taps the microphone button to answer with their voice.
     - **Accurate On-Device Speech Evaluator**: Uses Android's `SpeechRecognizer` (`EXTRA_PREFER_OFFLINE` + `LANGUAGE_MODEL_FREE_FORM` + top-5 N-best hypothesis matching + Levenshtein/phonetic token similarity + key phrase verification) to accurately grade pronunciation and answer correctness, awarding points and stars.
6. **Student Companion Tools Bar (Inside Every Box)**:
   - Seamless access to **Flashcards**, **My Study Reminders & Notes**, **Read-Only Teacher Grades & Mistakes**, and **Message Teacher**.

---

## 3. Complete 21-Box Approved Curriculum Table

| Box # | Level Title | Box Architecture | Core Rooms |
|---|---|---|---|
| **1** | **Essence** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **2** | **Beginner A** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **3** | **Beginner B** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **4** | **Skills 1 RW** | Reading & Writing Skills (2 Rooms) | 1. Grammar & Sentence Rules (Past Continuous, Nominal/Verbal) • 2. Sentence Building & Reordering |
| **5** | **Elementary A** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **6** | **Elementary B** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **7** | **Skills 2** | Listening & Speaking Skills (2 Rooms) | 1. Audio Dictation & Vocab Builder (`.xlsx`/`.csv`) • 2. Voice Q&A & Pronunciation Check |
| **8** | **Pre-intermediate A** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **9** | **Pre-intermediate B** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **10** | **Skills 3 RW** | Reading & Writing Skills (2 Rooms) | 1. Grammar & Sentence Rules • 2. Sentence Building & Reordering |
| **11** | **Intermediate A** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **12** | **Intermediate B** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **13** | **Skills 4** | Listening & Speaking Skills (2 Rooms) | 1. Audio Dictation & Vocab Builder (`.xlsx`/`.csv`) • 2. Voice Q&A & Pronunciation Check |
| **14** | **Upper intermediate A** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **15** | **Upper intermediate B** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **16** | **Upper intermediate C** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **17** | **Skills 5 RW** | Reading & Writing Skills (2 Rooms) | 1. Grammar & Sentence Rules • 2. Sentence Building & Reordering |
| **18** | **Advance A** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **19** | **Advance B** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **20** | **Advance C** | General Box (4 Rooms) | 1. Vocabulary (`.xlsx`/`.csv`) • 2. Grammar • 3. Vocab Practice • 4. Grammar Practice |
| **21** | **Skills 6** | Listening & Speaking Skills (2 Rooms) | 1. Audio Dictation & Vocab Builder (`.xlsx`/`.csv`) • 2. Voice Q&A & Pronunciation Check |

---

## 4. Technical Architecture & Data Strategy

```
┌───────────────────────────────────────────────────────────────────────────┐
│                        MainActivity & DiplomaQuestApp                     │
│  ┌─────────────────────────┐  ┌─────────────────────────────────────────┐ │
│  │ Device MAC Provisioning │─▶│ First-Time Student Academic Onboarding  │ │
│  └─────────────────────────┘  └───────────────────┬─────────────────────┘ │
└───────────────────────────────────────────────────┼───────────────────────┘
                                                    ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                    21-Box Gamified Curriculum Map Screen                  │
│  • Adaptive Institute Background + Daily Streak & Exam Countdown          │
│  • 21 Sequential Boxes (0..3 Stars per Box; >= 2 Stars Unlocks Next Box)  │
│  • Direct Box Unlock via Admin Activation Code                            │
└───────────────────┬───────────────────────────────────┬───────────────────┘
                    │                                   │
      ┌─────────────▼─────────────┐       ┌─────────────▼─────────────┐
      │   General Box (4 Rooms)   │       │  Skills Box (2 Rooms)     │
      │  1. In-App Vocab Bank     │       │  • RW (Boxes 4, 10, 17):  │
      │     + .XLSX/.CSV Importer │       │    1. Grammar Rules       │
      │  2. Grammar Explanation   │       │    2. Sentence Builder    │
      │  3. Vocabulary Practice   │       │  • L&S (Boxes 7, 13, 21): │
      │  4. Grammar Practice      │       │    1. Audio Dictation     │
      └─────────────┬─────────────┘       │    2. Voice Q&A Scorer    │
                    │                     └─────────────┬─────────────┘
                    └──────────────┬────────────────────┘
                                   ▼
┌───────────────────────────────────────────────────────────────────────────┐
│           Preserved Student Companion Tools (Inside Every Box)            │
│  • Flashcard Review Deck  • Study Reminders & Notes  • Grades & Mistakes  │
└───────────────────────────────────────────────────────────────────────────┘
```

### Offline `.xlsx` & `.csv` Teacher Vocabulary Importer (`VocabSpreadsheetImporter`)
- **Zero-Permission File Selection**: Uses Android's `ActivityResultContracts.OpenDocument()` supporting Excel spreadsheets (`application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`, `.xlsx`) and CSV files (`text/csv`, `text/comma-separated-values`, `.csv`).
- **Native `.xlsx` OpenXML Parser**: Reads `.xlsx` archives directly on-device using `ZipInputStream` and `XmlPullParser` (`xl/sharedStrings.xml`, `xl/workbook.xml`, and `xl/worksheets/sheet*.xml`)—extracting word, definition/translation, part of speech, and example sentence columns without external library bloat. Also supports multi-sheet `.xlsx` workbooks where each sheet name matches one of the 21 boxes!
- **RFC-4180 `.csv` Parser**: Parses comma-, semicolon-, or tab-separated `.csv` files with full UTF-8 support for English and Arabic meanings.
- **Dynamic Practice Sync**: Imported words are saved in Room (`VocabularyEntryEntity`) and immediately included in that box's **Vocabulary Practice (Room 3)** quizzes and **Interactive Flashcards**.

### 3-Star Calculation & Automatic Next-Box Unlock
- Practice rooms record the student's best accuracy score (`room1PracticeScore`, `room2PracticeScore` on a `0..100` scale).
- Box combined practice score determines stars:
  - **1 Star (⭐)**: Score $\ge 40\%$
  - **2 Stars (⭐⭐ — Minimum to Advance)**: Score $\ge 65\%$ (automatically unlocks Box $N+1$ in Room database!)
  - **3 Stars (⭐⭐⭐ — Mastery)**: Score $\ge 85\%$
