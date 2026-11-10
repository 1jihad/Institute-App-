package com.example.data

object CurriculumCatalog {

    private data class LevelSeed(
        val id: String,
        val order: Int,
        val track: DiplomaTrack,
        val stageGroup: String,
        val title: String,
        val boxType: BoxArchitectureType,
        val hours: Int,
        val code: String,
        val sizeMb: Float,
        val badgeName: String,
        val emoji: String,
        val badgeDesc: String,
        val skills: List<String>,
        val topicFocus: String,
        val vocab: List<VocabWordItem>,
        val grammarRule: String,
        val grammarFormula: String,
        val affirmativeExamples: List<String>,
        val negativeExamples: List<String>,
        val questionExamples: List<String>,
        val nominalVsVerbalNotes: List<Pair<String, String>> = emptyList(),
        val assignmentTask: String,
        val vocabPractice: List<SkillQuizQuestion>,
        val grammarPractice: List<SkillQuizQuestion>,
        val rwExercises: List<SentenceBuildExercise> = emptyList(),
        val listeningExercises: List<ListeningDictationExercise> = emptyList(),
        val speakingPrompts: List<SpeakingVoicePrompt> = emptyList()
    )

    private val seeds = listOf(
        // === BOX 1: Essence (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_01_essence",
            order = 1,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 1 • Foundation Block (Boxes 1–4)",
            title = "Essence",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "ESS-101",
            sizeMb = 11.4f,
            badgeName = "Alphabet & Phonics Spark",
            emoji = "🌟",
            badgeDesc = "Mastered core phonics, survival greetings, numbers, and Verb To Be in Box 1: Essence.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Alphabet phonics, self-introductions, numbers 1–100, and classroom survival phrases.",
            vocab = listOf(
                VocabWordItem("v1_1", "Good morning! How are you?", "A friendly greeting used before noon.", "صباح الخير! كيف حالك؟", "Good morning! How are you feeling today?", "Greeting"),
                VocabWordItem("v1_2", "Nice to meet you", "Said when meeting someone for the first time.", "تشرفت بمعرفتك", "Hello Sara, it is very nice to meet you.", "Phrase"),
                VocabWordItem("v1_3", "Could you repeat that, please?", "Polite classroom phrase asking the teacher to say something again.", "هل يمكنك إعادة ذلك من فضلك؟", "Excuse me teacher, could you repeat that, please?", "Classroom"),
                VocabWordItem("v1_4", "Spell your name", "Say each letter of your name in order.", "تهجّى اسمك", "Can you spell your last name for the registration form?", "Verb Phrase"),
                VocabWordItem("v1_5", "Notebook and dictionary", "Essential study tools for recording new English words.", "دفتر ملاحظات وقاموس", "I always bring my notebook and dictionary to class.", "Noun")
            ),
            grammarRule = "Subject Pronouns (I, You, We, They, He, She, It) + Verb 'To Be' (am / is / are) to identify people, places, and states.",
            grammarFormula = "Subject + am / is / are + Complement",
            affirmativeExamples = listOf("I am a new student at Excel ETA.", "She is our English teacher.", "They are in Room 1 today."),
            negativeExamples = listOf("I am not late for class.", "He is not from London.", "We are not tired."),
            questionExamples = listOf("Are you ready for the lesson?", "Is she in the Beginner class?", "Where are your books?"),
            assignmentTask = "Introduce yourself in English: state your full name, academic goal, and spell your surname aloud.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp1_1", "What do you say when you meet a classmate for the first time?", listOf("Goodbye forever", "Nice to meet you!", "See you yesterday", "Close the door"), 1, "'Nice to meet you!' is used when meeting someone new."),
                SkillQuizQuestion("vp1_2", "Which classroom phrase politely asks the teacher to say a word again?", listOf("Could you repeat that, please?", "Where is the exit?", "I am hungry", "Give me a pen"), 0, "'Could you repeat that, please?' is the polite request for repetition."),
                SkillQuizQuestion("vp1_3", "What does 'Spell your name' mean?", listOf("Write your age", "Say each letter of your name in order", "Whisper quietly", "Leave the room"), 1, "Spelling means saying or writing each letter in sequence.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp1_1", "Choose the correct verb: 'She ___ my English teacher.'", listOf("am", "is", "are", "be"), 1, "Singular pronoun 'She' takes 'is'."),
                SkillQuizQuestion("gp1_2", "Complete the negative sentence: 'We ___ late for class today.'", listOf("is not", "am not", "are not", "be not"), 2, "Plural pronoun 'We' takes 'are not'."),
                SkillQuizQuestion("gp1_3", "Choose the correct question form: '___ you a student at Excel ETA?'", listOf("Is", "Am", "Are", "Be"), 2, "Use 'Are' with the pronoun 'you'.")
            )
        ),

        // === BOX 2: Beginner A (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_02_beginner_a",
            order = 2,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 1 • Foundation Block (Boxes 1–4)",
            title = "Beginner A",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "BEGA-102",
            sizeMb = 12.8f,
            badgeName = "Beginner A Explorer",
            emoji = "🧭",
            badgeDesc = "Completed Box 2 (Beginner A): daily routines, family relationships, and Present Simple.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Daily routines, family members, telling time, and Present Simple habits.",
            vocab = listOf(
                VocabWordItem("v2_1", "Wake up early", "Stop sleeping in the morning.", "يستيقظ مبكراً", "I wake up early at 6:30 AM every weekday.", "Verb Phrase"),
                VocabWordItem("v2_2", "Brush my teeth", "Clean teeth with a toothbrush.", "أفرش أسناني", "She brushes her teeth after breakfast.", "Routine"),
                VocabWordItem("v2_3", "Grandparents", "The parents of your father or mother.", "الأجداد", "We visit our grandparents every Friday.", "Noun"),
                VocabWordItem("v2_4", "Every weekend", "Happening on Saturdays and Sundays.", "كل عطلة نهاية أسبوع", "They play football every weekend.", "Time Phrase")
            ),
            grammarRule = "Present Simple for daily habits and facts. Add '-s' or '-es' to the verb with He, She, and It.",
            grammarFormula = "I/You/We/They + Base Verb | He/She/It + Verb(-s/-es)",
            affirmativeExamples = listOf("I study English every evening.", "He watches educational videos at night.", "She goes to the institute at 4:00 PM."),
            negativeExamples = listOf("I do not drink coffee at night.", "He does not wake up late.", "They do not miss their classes."),
            questionExamples = listOf("Do you practice vocabulary every day?", "Does she live near the institute?", "What time does the lesson start?"),
            assignmentTask = "Write 5 sentences describing your daily routine using time prepositions (at 7:00 AM, in the morning).",
            vocabPractice = listOf(
                SkillQuizQuestion("vp2_1", "Who are your father's or mother's parents?", listOf("Cousins", "Grandparents", "Neighbors", "Classmates"), 1, "Your parents' parents are your grandparents."),
                SkillQuizQuestion("vp2_2", "Which phrase means 'stop sleeping in the morning'?", listOf("Wake up early", "Go to bed", "Eat dinner", "Close the book"), 0, "'Wake up early' means getting out of sleep in the morning."),
                SkillQuizQuestion("vp2_3", "Which preposition goes with clock times? 'Class starts ___ 9:00 o'clock.'", listOf("on", "in", "at", "by"), 2, "Use 'at' before specific clock times.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp2_1", "Complete the sentence: 'My brother ___ breakfast at 7:30 AM.'", listOf("eat", "eats", "eating", "to eat"), 1, "With 'My brother' (he), add -s to the verb: 'eats'."),
                SkillQuizQuestion("gp2_2", "Choose the correct negative: 'Sara ___ like loud music.'", listOf("don't", "doesn't", "isn't", "not"), 1, "Singular subject 'Sara' takes 'doesn't + base verb'."),
                SkillQuizQuestion("gp2_3", "Complete the question: '___ they study together on Mondays?'", listOf("Does", "Do", "Are", "Is"), 1, "Use 'Do' with 'they' in Present Simple questions.")
            )
        ),

        // === BOX 3: Beginner B (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_03_beginner_b",
            order = 3,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 1 • Foundation Block (Boxes 1–4)",
            title = "Beginner B",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "BEGB-103",
            sizeMb = 13.5f,
            badgeName = "Town & Market Navigator",
            emoji = "🏘️",
            badgeDesc = "Conquered Box 3 (Beginner B): shopping, countable/uncountable nouns, and Present Continuous.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Places around town, shopping, countable/uncountable nouns, and Present Continuous actions.",
            vocab = listOf(
                VocabWordItem("v3_1", "Turn left at the corner", "Change direction to the left side of the street.", "انعطف يساراً عند الزاوية", "Walk straight and turn left at the corner.", "Directions"),
                VocabWordItem("v3_2", "Across from the library", "On the opposite side of the street from the library.", "مقابل المكتبة", "The café is across from the library.", "Preposition"),
                VocabWordItem("v3_3", "A loaf of bread", "A measured unit for uncountable bread.", "رغيف خبز", "Please buy a loaf of bread from the bakery.", "Noun Phrase"),
                VocabWordItem("v3_4", "How much does this cost?", "Asking for the price of an item.", "كم سعر هذا؟", "Excuse me, how much does this dictionary cost?", "Shopping")
            ),
            grammarRule = "Countable vs. Uncountable Nouns ('How many' for countable plurals; 'How much' for uncountable nouns) & Present Continuous for actions happening right now.",
            grammarFormula = "How many + Plural Noun | How much + Uncountable Noun | am/is/are + Verb-ing",
            affirmativeExamples = listOf("There are many books on the shelf.", "There is some water in the bottle.", "Look! The students are reading right now."),
            negativeExamples = listOf("There aren't any apples left.", "There isn't much sugar in my tea.", "He isn't watching TV right now."),
            questionExamples = listOf("How many notebooks do you need?", "How much milk is in the fridge?", "What are you doing at the moment?"),
            assignmentTask = "Write a 6-line shopping dialogue using 'How much', 'How many', and Present Continuous.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp3_1", "Where do you go to borrow or read books?", listOf("Bakery", "Pharmacy", "Library", "Airport"), 2, "A library is where you borrow and read books."),
                SkillQuizQuestion("vp3_2", "What do you ask when you want to know the price of a jacket?", listOf("How tall is it?", "How much does this cost?", "Where is the bank?", "What time is it?"), 1, "'How much does this cost?' asks for the price."),
                SkillQuizQuestion("vp3_3", "Which phrase means 'on the opposite side of the street'?", listOf("Across from", "Inside of", "Underneath", "Behind the"), 0, "'Across from' means on the opposite side.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp3_1", "Which question is correct for uncountable water?", listOf("How many water is there?", "How much water is in the bottle?", "Are there a water?", "How few water?"), 1, "Water is uncountable, so we use 'How much'."),
                SkillQuizQuestion("gp3_2", "Look! The children ___ in the courtyard right now.", listOf("runs", "are running", "ran", "is running"), 1, "'Right now' requires Present Continuous: 'are running'."),
                SkillQuizQuestion("gp3_3", "Choose the correct quantifier: 'There aren't ___ chairs in the hall.'", listOf("much", "a little", "many", "some"), 2, "Use 'many' with plural countable nouns ('chairs') in negatives.")
            )
        ),

        // === BOX 4: Skills 1 RW (Reading & Writing Skills - 2 Specialized Rooms) ===
        LevelSeed(
            id = "lvl_04_skills_rw_1",
            order = 4,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 1 • Foundation Block (Boxes 1–4)",
            title = "Skills 1 RW",
            boxType = BoxArchitectureType.SKILLS_RW_2_ROOMS,
            hours = 45,
            code = "SRW1-104",
            sizeMb = 14.2f,
            badgeName = "Sentence Architect I",
            emoji = "✍️",
            badgeDesc = "Mastered Box 4 (Skills 1 RW): Past Continuous, Nominal & Verbal sentence formation, and sentence reordering.",
            skills = listOf("Grammar Explanation Room", "Writing & Sentence Construction Room"),
            topicFocus = "Past Continuous vs. Past Simple, Nominal vs. Verbal Sentence Structure, and Sentence Reordering.",
            vocab = listOf(
                VocabWordItem("v4_1", "While I was studying", "Indicates an ongoing background action in the past.", "بينما كنت أدرس", "While I was studying, the phone rang.", "Time Clause"),
                VocabWordItem("v4_2", "Nominal Sentence (State/Identity)", "A sentence centered on Subject + Be + Noun/Adjective describing state or identity.", "الجملة الاسمية (مبتدأ وخبر / وصف حالة)", "The classroom is bright and quiet.", "Syntax"),
                VocabWordItem("v4_3", "Verbal Sentence (Action)", "A sentence driven by an action verb performed by the subject.", "الجملة الفعلية (فعل وفاعل ومفعول)", "The students wrote three paragraphs carefully.", "Syntax"),
                VocabWordItem("v4_4", "Suddenly", "Happening quickly and unexpectedly (triggers Past Simple).", "فجأة", "We were walking home when suddenly it started to rain.", "Adverb")
            ),
            grammarRule = "1) Past Continuous (was/were + V-ing) describes an ongoing action in the past interrupted by a shorter action (Past Simple). 2) Sentence Architecture: Nominal Sentences describe state/identity using Subject + Verb To Be + Complement, whereas Verbal Sentences express dynamic actions using Subject + Action Verb + Object.",
            grammarFormula = "Past Continuous: Subject + was/were + Verb-ing | While + Past Continuous, Past Simple",
            affirmativeExamples = listOf(
                "Nominal Structure: The library is a quiet place for reading.",
                "Verbal Structure: The students completed the reading worksheet.",
                "Past Continuous: I was reading a story when my friend arrived."
            ),
            negativeExamples = listOf(
                "He was not sleeping at 9:00 PM; he was writing his essay.",
                "They were not making noise during the exam."
            ),
            questionExamples = listOf(
                "What were you doing when the teacher entered the room?",
                "Was she studying in the library yesterday evening?"
            ),
            nominalVsVerbalNotes = listOf(
                "Nominal Sentence (بناء الجملة الاسمية بالإنجليزية)" to "Starts with the Subject followed by a linking verb (am/is/are/was/were) + Noun or Adjective complement. Example: 'English grammar is logical.'",
                "Verbal Sentence (بناء الجملة الفعلية بالإنجليزية)" to "Starts with the Subject followed by a dynamic Action Verb + Object/Adverb. Example: 'The student solved the grammar exercise quickly.'",
                "Past Continuous (الماضي المستمر)" to "Use 'was/were + verb-ing' for longer background actions: 'While we were writing the paragraph, the bell rang.'"
            ),
            assignmentTask = "Construct 3 Nominal sentences, 3 Verbal action sentences, and 3 Past Continuous sentences using 'while' and 'when'.",
            vocabPractice = emptyList(),
            grammarPractice = listOf(
                SkillQuizQuestion("gp4_1", "Complete with Past Continuous: 'While Sara ___ her essay, the lights went out.'", listOf("wrote", "was writing", "were writing", "writes"), 1, "Singular subject 'Sara' after 'While' takes 'was writing'."),
                SkillQuizQuestion("gp4_2", "Which of the following represents a Nominal-style state sentence (Subject + Be + Complement)?", listOf("The teacher explained the lesson.", "The final exam is very important.", "We wrote five sentences.", "They ran to the bus."), 1, "'The final exam is very important' links the subject to an adjective complement using 'is'."),
                SkillQuizQuestion("gp4_3", "Choose the correct sentence order:", listOf("Was studying he English yesterday.", "He was studying English at 8 PM yesterday.", "Studying was he English yesterday.", "Yesterday at 8 PM studying he was."), 1, "Standard English word order is Subject + Auxiliary + Main Verb + Object + Time.")
            ),
            rwExercises = listOf(
                SentenceBuildExercise(
                    id = "rw1_ex1",
                    instruction = "Reorder the words to form a Past Continuous sentence with 'while':",
                    sentenceTypeTag = "Past Continuous (الماضي المستمر)",
                    scrambledWords = listOf("studying", "While", "I", "was", "my", "called", "brother"),
                    targetSentence = "While I was studying my brother called",
                    grammarHint = "Start with 'While + Subject + was/were + V-ing', followed by the Past Simple clause."
                ),
                SentenceBuildExercise(
                    id = "rw1_ex2",
                    instruction = "Form a Nominal State Sentence (جملة اسمية وصفية) describing the course:",
                    sentenceTypeTag = "Nominal Sentence (جملة اسمية)",
                    scrambledWords = listOf("is", "beneficial", "very", "Reading", "daily"),
                    targetSentence = "Reading daily is very beneficial",
                    grammarHint = "Place the Subject ('Reading daily') first, then the linking verb ('is'), then the modifier + adjective ('very beneficial')."
                ),
                SentenceBuildExercise(
                    id = "rw1_ex3",
                    instruction = "Form a Verbal Action Sentence (جملة فعلية) in the past tense:",
                    sentenceTypeTag = "Verbal Sentence (جملة فعلية)",
                    scrambledWords = listOf("carefully", "wrote", "The", "students", "paragraphs", "their"),
                    targetSentence = "The students wrote their paragraphs carefully",
                    grammarHint = "Follow Subject ('The students') + Action Verb ('wrote') + Object ('their paragraphs') + Adverb ('carefully')."
                ),
                SentenceBuildExercise(
                    id = "rw1_ex4",
                    instruction = "Reorder to build a Past Continuous question:",
                    sentenceTypeTag = "Past Continuous Question",
                    scrambledWords = listOf("doing", "What", "you", "were", "yesterday", "evening"),
                    targetSentence = "What were you doing yesterday evening",
                    grammarHint = "Question word ('What') + were + Subject ('you') + Verb-ing ('doing') + time."
                )
            )
        ),

        // === BOX 5: Elementary A (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_05_elementary_a",
            order = 5,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 2 • Elementary Block (Boxes 5–7)",
            title = "Elementary A",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "ELEA-105",
            sizeMb = 14.8f,
            badgeName = "Past Storyteller",
            emoji = "⏳",
            badgeDesc = "Completed Box 5 (Elementary A): Past Simple regular & irregular verbs, travel, and biographies.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Past Simple regular and irregular verbs, past holidays, and life events.",
            vocab = listOf(
                VocabWordItem("v5_1", "Booked a flight", "Reserved an airplane ticket in advance.", "حجز رحلة طيران", "We booked a flight to Cairo last month.", "Travel"),
                VocabWordItem("v5_2", "Visited historical sites", "Went to see ancient monuments and museums.", "زار المواقع التاريخية", "The tourists visited historical sites in the old city.", "Phrase"),
                VocabWordItem("v5_3", "Left my luggage", "Forgot or placed suitcases somewhere.", "تركت أمتعتي", "He left his luggage at the hotel reception.", "Travel"),
                VocabWordItem("v5_4", "Two weeks ago", "A specific time in the past before now.", "قبل أسبوعين", "I passed my first assessment two weeks ago.", "Time Marker")
            ),
            grammarRule = "Past Simple for completed actions in the past. Use '-ed' for regular verbs and special forms for irregular verbs (go -> went, buy -> bought). Use 'did not + base verb' in negatives.",
            grammarFormula = "Subject + Verb-ed / Irregular Past | Negative: did not + Base Verb",
            affirmativeExamples = listOf("We traveled to Alexandria last summer.", "She bought a new English dictionary yesterday."),
            negativeExamples = listOf("I did not go to the mall yesterday.", "They did not forget their homework."),
            questionExamples = listOf("Where did you spend your last holiday?", "Did he finish the assignment on time?"),
            assignmentTask = "Write a 60-word paragraph about a memorable trip using at least 6 Past Simple verbs.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp5_1", "What does 'Booked a flight' mean?", listOf("Read a book on a plane", "Reserved an airplane ticket", "Missed the bus", "Packed a bag"), 1, "'Booked a flight' means reserved an airline ticket."),
                SkillQuizQuestion("vp5_2", "Which time expression signals the Past Simple tense?", listOf("Two weeks ago", "Next month", "Right now", "Tomorrow morning"), 0, "'Ago' refers to a completed time in the past."),
                SkillQuizQuestion("vp5_3", "What is 'luggage' when traveling?", listOf("Passports only", "Suitcases and travel bags", "Airplane seats", "Hotel keys"), 1, "Luggage refers to suitcases and bags.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp5_1", "What is the Past Simple form of 'go'?", listOf("goed", "went", "gone", "going"), 1, "'Went' is the irregular past tense of 'go'."),
                SkillQuizQuestion("gp5_2", "Choose the correct negative sentence in the Past Simple:", listOf("She didn't went home.", "She didn't go home.", "She don't went home.", "She not went home."), 1, "After 'didn't', always use the base verb ('go')."),
                SkillQuizQuestion("gp5_3", "Complete the question: 'Where ___ you buy that notebook?'", listOf("were", "did", "was", "do"), 1, "Use 'did' + subject + base verb for Past Simple questions.")
            )
        ),

        // === BOX 6: Elementary B (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_06_elementary_b",
            order = 6,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 2 • Elementary Block (Boxes 5–7)",
            title = "Elementary B",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "ELEB-106",
            sizeMb = 15.1f,
            badgeName = "Comparison & Future Planner",
            emoji = "🚀",
            badgeDesc = "Completed Box 6 (Elementary B): comparatives, superlatives, and future plans with 'going to'.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Comparatives & Superlatives, technology, weather, and future intentions with 'be going to'.",
            vocab = listOf(
                VocabWordItem("v6_1", "More convenient than", "Easier and more practical in comparison.", "أكثر ملاءمة من", "Studying with flashcards is more convenient than carrying heavy books.", "Comparative"),
                VocabWordItem("v6_2", "The most crowded", "Having the highest number of people.", "الأكثر ازدحاماً", "Downtown is the most crowded area in the evening.", "Superlative"),
                VocabWordItem("v6_3", "Upgrade my skills", "Improve my knowledge and abilities.", "أطوّر مهاراتي", "I am going to upgrade my English skills this semester.", "Goal"),
                VocabWordItem("v6_4", "Weather forecast", "Prediction of upcoming weather conditions.", "النشرة الجوية", "According to the weather forecast, it is going to rain tomorrow.", "Noun")
            ),
            grammarRule = "1) Comparatives (-er / more ... than) compare two things; Superlatives (the -est / the most ...) compare three or more. 2) Use 'am/is/are + going to + base verb' for planned future intentions.",
            grammarFormula = "Short Adj-er + than | more + Long Adj + than | am/is/are going to + Verb",
            affirmativeExamples = listOf("This laptop is faster than my old one.", "Box 6 is more advanced than Box 1.", "We are going to practice speaking tonight."),
            negativeExamples = listOf("Today is not colder than yesterday.", "He is not going to miss the review session."),
            questionExamples = listOf("Which city is the largest in your country?", "What are you going to study this weekend?"),
            assignmentTask = "Compare two devices you use daily and write 3 future study goals using 'am going to'.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp6_1", "What does 'Upgrade my skills' mean?", listOf("Forget what I learned", "Improve my abilities and knowledge", "Sell my books", "Cancel my class"), 1, "Upgrading skills means improving your abilities."),
                SkillQuizQuestion("vp6_2", "Which phrase describes the place with the highest number of people?", listOf("The most crowded", "The quietest", "Smaller than", "Less expensive"), 0, "'The most crowded' means having the most people."),
                SkillQuizQuestion("vp6_3", "What tells you if it will be sunny or rainy tomorrow?", listOf("A dictionary", "A weather forecast", "A receipt", "A timetable"), 1, "A weather forecast predicts upcoming weather.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp6_1", "Choose the correct comparative: 'An airplane is ___ than a train.'", listOf("fast", "faster", "fastest", "more fast"), 1, "One-syllable adjective 'fast' becomes 'faster than'."),
                SkillQuizQuestion("gp6_2", "Choose the correct superlative: 'This is ___ book in the library.'", listOf("more interesting", "the most interesting", "interestinger", "most interest"), 1, "Long adjectives use 'the most + adjective'."),
                SkillQuizQuestion("gp6_3", "Complete the future plan: 'They ___ going to take the test on Thursday.'", listOf("is", "am", "are", "be"), 2, "'They' takes 'are going to'.")
            )
        ),

        // === BOX 7: Skills 2 (Listening & Speaking Skills - 2 Specialized Rooms) ===
        LevelSeed(
            id = "lvl_07_skills_ls_2",
            order = 7,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 2 • Elementary Block (Boxes 5–7)",
            title = "Skills 2",
            boxType = BoxArchitectureType.SKILLS_LS_2_ROOMS,
            hours = 45,
            code = "SLS2-107",
            sizeMb = 18.6f,
            badgeName = "Active Listener & Speaker I",
            emoji = "🎙️",
            badgeDesc = "Mastered Box 7 (Skills 2): real-time audio dictation, vocabulary extraction, and interactive voice Q&A.",
            skills = listOf("Listening & Vocabulary Dictation Room", "Speaking & Voice Interaction Room"),
            topicFocus = "Real-time audio dictation, vocabulary capture, and spoken Q&A with automated pronunciation verification.",
            vocab = listOf(
                VocabWordItem("v7_1", "Could you speak a bit slower?", "Polite request when listening to fast speech.", "هل يمكنك التحدث ببطء أكثر قليلاً؟", "Excuse me, could you speak a bit slower so I can write the notes?", "Listening"),
                VocabWordItem("v7_2", "Appointment", "A scheduled meeting at a specific time.", "موعد محدد", "I have a dentist appointment at four o'clock.", "Noun"),
                VocabWordItem("v7_3", "Platform number three", "The specific area where a train departs.", "الرصيف رقم ثلاثة", "The express train leaves from platform number three.", "Travel"),
                VocabWordItem("v7_4", "In my opinion", "Phrase used to introduce your personal view when speaking.", "في رأيي", "In my opinion, daily listening practice builds strong fluency.", "Speaking")
            ),
            grammarRule = "Spoken Question Intonation & Clear Sentence Framing: Answer questions in complete sentences using the tense of the question.",
            grammarFormula = "Listen -> Transcribe Key Words -> Respond Aloud with Complete Sentence",
            affirmativeExamples = listOf("I wake up at seven o'clock every morning.", "The train departs from platform three at noon."),
            negativeExamples = listOf("I didn't catch the last word; could you repeat it?"),
            questionExamples = listOf("What time do you usually start your study session?", "How do you travel to the institute?"),
            assignmentTask = "Complete all 3 audio dictation passages and answer the 3 spoken voice prompts aloud.",
            vocabPractice = emptyList(),
            grammarPractice = emptyList(),
            listeningExercises = listOf(
                ListeningDictationExercise(
                    id = "ls2_dict_1",
                    title = "Passage 1 • Morning Study Routine",
                    audioTranscript = "I always review my vocabulary cards before breakfast",
                    hintText = "Listen for the frequency adverb ('always') and what is reviewed before breakfast.",
                    keyWordsToExtract = listOf(
                        "review" to "مراجعة / يراجع",
                        "vocabulary cards" to "بطاقات المفردات"
                    )
                ),
                ListeningDictationExercise(
                    id = "ls2_dict_2",
                    title = "Passage 2 • Train Station Announcement",
                    audioTranscript = "The express train to London leaves from platform three",
                    hintText = "Listen for the type of train, destination, and platform number.",
                    keyWordsToExtract = listOf(
                        "express train" to "القطار السريع",
                        "platform" to "رصيف المحطة"
                    )
                ),
                ListeningDictationExercise(
                    id = "ls2_dict_3",
                    title = "Passage 3 • Scheduling an Appointment",
                    audioTranscript = "Please arrive fifteen minutes before your appointment",
                    hintText = "How many minutes early should you arrive?",
                    keyWordsToExtract = listOf(
                        "arrive" to "يصل",
                        "appointment" to "موعد"
                    )
                )
            ),
            speakingPrompts = listOf(
                SpeakingVoicePrompt(
                    id = "ls2_spk_1",
                    questionAudioText = "What time do you usually wake up and what do you do first in the morning?",
                    arabicPromptHint = "أجب بصوتك: متى تستيقظ عادةً وماذا تفعل أولاً في الصباح؟",
                    sampleTargetAnswer = "I usually wake up at seven in the morning and brush my teeth",
                    requiredKeywords = listOf("wake", "morning"),
                    grammarFocusLabel = "Present Simple Routine"
                ),
                SpeakingVoicePrompt(
                    id = "ls2_spk_2",
                    questionAudioText = "How do you travel to the institute and how long does it take?",
                    arabicPromptHint = "أجب بصوتك: كيف تذهب إلى المعهد وكم يستغرق الطريق؟",
                    sampleTargetAnswer = "I go to the institute by bus and it takes twenty minutes",
                    requiredKeywords = listOf("institute", "minutes"),
                    grammarFocusLabel = "Transport & Duration"
                ),
                SpeakingVoicePrompt(
                    id = "ls2_spk_3",
                    questionAudioText = "What did you study in your English class yesterday?",
                    arabicPromptHint = "أجب بصوتك: ماذا درست في حصة اللغة الإنجليزية بالأمس؟",
                    sampleTargetAnswer = "Yesterday I studied new vocabulary and practiced speaking in class",
                    requiredKeywords = listOf("studied", "vocabulary", "speaking"),
                    grammarFocusLabel = "Past Simple Response"
                )
            )
        ),

        // === BOX 8: Pre-intermediate A (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_08_pre_inter_a",
            order = 8,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 3 • Pre-Intermediate Block (Boxes 8–10)",
            title = "Pre-intermediate A",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "PREA-108",
            sizeMb = 15.6f,
            badgeName = "Life Experience Explorer",
            emoji = "🌍",
            badgeDesc = "Completed Box 8 (Pre-intermediate A): Present Perfect experiences ('ever/never/already/yet').",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Present Perfect vs. Past Simple, life experiences, achievements, and workplace communication.",
            vocab = listOf(
                VocabWordItem("v8_1", "Have you ever", "Asking about life experience at any time up to now.", "هل سبق لك أن...", "Have you ever given a presentation in English?", "Present Perfect"),
                VocabWordItem("v8_2", "Achievement", "Something important succeeded in through effort.", "إنجاز", "Earning two stars in every box is a great achievement.", "Noun"),
                VocabWordItem("v8_3", "Deadline", "The latest time or date by which a task must be completed.", "الموعد النهائي", "We finished the project before the Friday deadline.", "Workplace"),
                VocabWordItem("v8_4", "Already and Yet", "'Already' for completed actions; 'yet' in negatives/questions.", "بالفعل / حتى الآن", "I have already completed Room 1, but I haven't taken the quiz yet.", "Adverbs")
            ),
            grammarRule = "Present Perfect (have/has + Past Participle) connects past experiences or actions to the present without a specific past time marker.",
            grammarFormula = "Subject + have / has + Past Participle (V3)",
            affirmativeExamples = listOf("I have visited three countries so far.", "She has already uploaded the vocabulary list."),
            negativeExamples = listOf("We have not seen that documentary yet.", "He has never missed an English exam."),
            questionExamples = listOf("Have you ever spoken to a native speaker?", "Has the teacher posted the grades yet?"),
            assignmentTask = "Write 5 sentences about your academic achievements using 'have/has + V3' with 'already', 'never', and 'yet'.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp8_1", "What do we call the final date by which an assignment must be submitted?", listOf("A holiday", "A deadline", "A souvenir", "A recipe"), 1, "A 'deadline' is the final due date."),
                SkillQuizQuestion("vp8_2", "Which word means something important you succeeded in doing?", listOf("Achievement", "Mistake", "Traffic", "Luggage"), 0, "An achievement is a successful result of effort."),
                SkillQuizQuestion("vp8_3", "Which word is placed at the end of Present Perfect negative sentences?", listOf("already", "ever", "yet", "just"), 2, "'Yet' goes at the end of negatives and questions.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp8_1", "Complete with Present Perfect: 'She ___ already finished her grammar exercise.'", listOf("have", "has", "did", "was"), 1, "Singular subject 'She' takes 'has' + past participle."),
                SkillQuizQuestion("gp8_2", "Choose the correct verb form: 'Have you ever ___ in public?'", listOf("speak", "spoke", "spoken", "speaking"), 2, "'Have you ever' requires the Past Participle (V3): 'spoken'."),
                SkillQuizQuestion("gp8_3", "Why is 'I have seen him yesterday' incorrect?", listOf("Because 'yesterday' is a finished past time requiring Past Simple ('I saw him yesterday')", "Because 'seen' is present tense", "Because 'I' takes 'has'", "It is correct"), 0, "Finished time markers like 'yesterday' require Past Simple, not Present Perfect.")
            )
        ),

        // === BOX 9: Pre-intermediate B (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_09_pre_inter_b",
            order = 9,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 3 • Pre-Intermediate Block (Boxes 8–10)",
            title = "Pre-intermediate B",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "PREB-109",
            sizeMb = 16.0f,
            badgeName = "Modal & Conditional Thinker",
            emoji = "⚖️",
            badgeDesc = "Completed Box 9 (Pre-intermediate B): Modals of obligation/advice and First Conditional.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Modals of advice & obligation (should, must, have to) and First Conditional ('If + Present, will + Verb').",
            vocab = listOf(
                VocabWordItem("v9_1", "Must follow the rules", "Strong obligation or necessity.", "يجب اتباع القواعد", "Students must follow the institute rules during exams.", "Modal Phrase"),
                VocabWordItem("v9_2", "Consequence", "A result or effect of an action or condition.", "نتيجة / عاقبة", "Think carefully about the consequences of your decision.", "Noun"),
                VocabWordItem("v9_3", "Recommend", "Suggest something as good or suitable.", "يوصي بـ", "I strongly recommend reviewing your flashcards every night.", "Verb"),
                VocabWordItem("v9_4", "As soon as", "Immediately at the moment something happens.", "بمجرد أن", "The next box unlocks as soon as you earn two stars.", "Conjunction")
            ),
            grammarRule = "1) Modals: 'should' for advice, 'must / have to' for obligation, 'don't have to' for lack of necessity. 2) First Conditional: 'If + Present Simple, will + Base Verb' for real future possibilities.",
            grammarFormula = "If + Subject + Present Simple, Subject + will + Base Verb",
            affirmativeExamples = listOf("If you earn two stars, you will unlock the next box.", "You should listen to the slow audio first."),
            negativeExamples = listOf("If he doesn't hurry, he won't catch the bus.", "You don't have to memorize everything in one hour."),
            questionExamples = listOf("What will you do if you pass the exam early?", "Should I write this down in my notebook?"),
            assignmentTask = "Write 4 First Conditional sentences about your study plan and 3 pieces of advice using 'should'.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp9_1", "What does 'Recommend' mean?", listOf("Forbid", "Suggest something as helpful or good", "Break a rule", "Forget"), 1, "To recommend means to suggest something positively."),
                SkillQuizQuestion("vp9_2", "Which phrase means 'immediately when something happens'?", listOf("As soon as", "Even though", "Instead of", "Because of"), 0, "'As soon as' means right at that moment."),
                SkillQuizQuestion("vp9_3", "What is the result or effect of an action called?", listOf("A consequence", "A schedule", "A dictionary", "An accent"), 0, "A consequence is the result of an action.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp9_1", "Complete the First Conditional: 'If it ___ tomorrow, we will study indoors.'", listOf("rain", "rains", "will rain", "rained"), 1, "The 'If' clause uses Present Simple ('rains')."),
                SkillQuizQuestion("gp9_2", "Which modal expresses friendly advice?", listOf("mustn't", "should", "had", "did"), 1, "'Should' is used to give advice."),
                SkillQuizQuestion("gp9_3", "What does 'You don't have to bring a laptop' mean?", listOf("It is forbidden to bring a laptop", "It is not necessary to bring a laptop, but you can if you want", "You must bring a laptop", "Laptops are broken"), 1, "'Don't have to' means there is no obligation.")
            )
        ),

        // === BOX 10: Skills 3 RW (Reading & Writing Skills - 2 Specialized Rooms) ===
        LevelSeed(
            id = "lvl_10_skills_rw_3",
            order = 10,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 3 • Pre-Intermediate Block (Boxes 8–10)",
            title = "Skills 3 RW",
            boxType = BoxArchitectureType.SKILLS_RW_2_ROOMS,
            hours = 45,
            code = "SRW3-110",
            sizeMb = 16.4f,
            badgeName = "Sentence Architect II",
            emoji = "📝",
            badgeDesc = "Mastered Box 10 (Skills 3 RW): complex clauses, nominal & verbal sentence transformations, and paragraph linking.",
            skills = listOf("Grammar Explanation Room", "Writing & Sentence Construction Room"),
            topicFocus = "Relative Clauses (who/which/that), Nominal vs. Verbal Sentence Transformation, and Paragraph Connectors.",
            vocab = listOf(
                VocabWordItem("v10_1", "Furthermore", "In addition; used to add a stronger supporting point in writing.", "علاوة على ذلك", "Regular reading expands vocabulary; furthermore, it improves writing style.", "Connector"),
                VocabWordItem("v10_2", "Although", "In spite of the fact that; introduces a contrast clause.", "على الرغم من أن", "Although the test was challenging, all students passed.", "Conjunction"),
                VocabWordItem("v10_3", "Relative Clause", "A clause starting with who, which, or that to define a noun.", "جملة الموصول (الذي / التي)", "The student who practices daily achieves the highest grade.", "Grammar"),
                VocabWordItem("v10_4", "Topic Sentence", "The opening sentence that states the main idea of a paragraph.", "الجملة المفتتاحية للفقرة", "Every clear academic paragraph begins with a strong topic sentence.", "Writing")
            ),
            grammarRule = "1) Combining sentences using Relative Clauses ('who' for people, 'which/that' for things) and Contrast Links ('although', 'however'). 2) Transforming between Nominal Descriptive Sentences (Subject + Be + Adjective/Noun) and Verbal Action Sentences (Subject + Action Verb + Adverb).",
            grammarFormula = "Noun + who/which/that + Clause | Although + Subject + Verb, Main Clause",
            affirmativeExamples = listOf(
                "Nominal Focus: Her explanation was remarkably clear.",
                "Verbal Focus: She explained the grammar rule clearly.",
                "Relative Clause: The dictionary which I bought yesterday is very helpful."
            ),
            negativeExamples = listOf(
                "Do not use a comma before 'that' in a defining relative clause.",
                "Never use both 'although' and 'but' in the same sentence."
            ),
            questionExamples = listOf(
                "How can we transform a verbal sentence into a nominal descriptive sentence?",
                "Which relative pronoun refers to people?"
            ),
            nominalVsVerbalNotes = listOf(
                "Nominal to Verbal Transformation" to "Nominal: 'His progress is rapid.' -> Verbal: 'He progresses rapidly.'",
                "Combining with 'Who' & 'Which'" to "Instead of two short choppy sentences, link them: 'The teacher praised the student who completed all four rooms.'",
                "Contrast Connectors ('Although' vs 'However')" to "'Although' joins two clauses in one sentence: 'Although it was late, I finished my notes.'"
            ),
            assignmentTask = "Complete all 4 sentence construction & reordering drills and write a 4-sentence paragraph using 'Although' and 'Furthermore'.",
            vocabPractice = emptyList(),
            grammarPractice = listOf(
                SkillQuizQuestion("gp10_1", "Choose the correct relative pronoun: 'The instructor ___ taught us Box 10 was very supportive.'", listOf("which", "who", "where", "whose"), 1, "Use 'who' to refer to people ('The instructor')."),
                SkillQuizQuestion("gp10_2", "Which sentence is grammatically correct?", listOf("Although it was raining, but we went to class.", "Although it was raining, we went to class.", "Despite it was raining, we went.", "However it was raining, we went."), 1, "In English, do not use 'but' after 'Although'."),
                SkillQuizQuestion("gp10_3", "Which sentence is the Verbal (Action) equivalent of 'Their discussion was productive'?", listOf("They discussed the topic productively.", "Their productive discussion was good.", "The discussion is very productive.", "Productive was their discussion."), 0, "'They discussed the topic productively' uses the action verb 'discussed' + adverb 'productively'.")
            ),
            rwExercises = listOf(
                SentenceBuildExercise(
                    id = "rw3_ex1",
                    instruction = "Reorder the words to build a complex sentence with 'Although':",
                    sentenceTypeTag = "Contrast Clause (Although)",
                    scrambledWords = listOf("difficult", "Although", "was", "the", "passed", "quiz", "we"),
                    targetSentence = "Although the quiz was difficult we passed",
                    grammarHint = "Start with 'Although + Subject + Verb + Adjective', then the main clause ('we passed')."
                ),
                SentenceBuildExercise(
                    id = "rw3_ex2",
                    instruction = "Build a sentence with a Relative Clause using 'who':",
                    sentenceTypeTag = "Relative Clause (جملة الموصول)",
                    scrambledWords = listOf("who", "Students", "daily", "succeed", "practice"),
                    targetSentence = "Students who practice daily succeed",
                    grammarHint = "Place 'who practice daily' right after the subject 'Students', followed by the main verb 'succeed'."
                ),
                SentenceBuildExercise(
                    id = "rw3_ex3",
                    instruction = "Construct a Nominal Descriptive Sentence (جملة اسمية) about academic writing:",
                    sentenceTypeTag = "Nominal Sentence (جملة اسمية)",
                    scrambledWords = listOf("essential", "Clear", "is", "organization", "paragraph"),
                    targetSentence = "Clear paragraph organization is essential",
                    grammarHint = "Noun Phrase Subject ('Clear paragraph organization') + Linking Verb ('is') + Adjective ('essential')."
                ),
                SentenceBuildExercise(
                    id = "rw3_ex4",
                    instruction = "Construct the matching Verbal Action Sentence (جملة فعلية):",
                    sentenceTypeTag = "Verbal Sentence (جملة فعلية)",
                    scrambledWords = listOf("their", "organize", "Skilled", "paragraphs", "writers", "clearly"),
                    targetSentence = "Skilled writers organize their paragraphs clearly",
                    grammarHint = "Subject ('Skilled writers') + Action Verb ('organize') + Object ('their paragraphs') + Adverb ('clearly')."
                )
            )
        ),

        // === BOX 11: Intermediate A (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_11_inter_a",
            order = 11,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 4 • Intermediate Block (Boxes 11–13)",
            title = "Intermediate A",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "INTA-111",
            sizeMb = 16.8f,
            badgeName = "Passive Voice & Process Master",
            emoji = "⚙️",
            badgeDesc = "Completed Box 11 (Intermediate A): Passive Voice across tenses and Second Conditional.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Passive Voice (Present & Past) and Second Conditional ('If + Past Simple, would + Verb').",
            vocab = listOf(
                VocabWordItem("v11_1", "Manufactured in", "Produced on a large scale in a specific place.", "صُنع في", "These smartphones are manufactured in East Asia.", "Passive"),
                VocabWordItem("v11_2", "Hypothetical situation", "An imagined scenario rather than a current fact.", "موقف افتراضي", "We use the Second Conditional to describe a hypothetical situation.", "Academic"),
                VocabWordItem("v11_3", "Renewable energy", "Energy from sources that are naturally replenished like solar or wind.", "الطاقة المتجددة", "Solar power is a clean form of renewable energy.", "Science"),
                VocabWordItem("v11_4", "Implemented", "Put a decision, plan, or system into effect.", "تم تطبيقه / تنفيذه", "The new curriculum was implemented this year.", "Verb")
            ),
            grammarRule = "1) Passive Voice (be + Past Participle) focuses on the receiver of the action rather than the doer. 2) Second Conditional ('If + Past Simple, would + Base Verb') describes unreal or hypothetical present/future situations.",
            grammarFormula = "Passive: Object + am/is/are/was/were + V3 | 2nd Cond: If + Past, would + Verb",
            affirmativeExamples = listOf("English is spoken all over the world.", "The telephone was invented in 1876.", "If I had more free time, I would read two books a week."),
            negativeExamples = listOf("The results were not announced yesterday.", "If I were you, I would not skip the practice room."),
            questionExamples = listOf("When was the institute founded?", "What would you do if you won a scholarship abroad?"),
            assignmentTask = "Write 3 Passive Voice sentences about inventions and 3 Second Conditional sentences.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp11_1", "What does 'Manufactured in' mean?", listOf("Destroyed in", "Produced or made in a factory", "Forgotten in", "Hidden in"), 1, "Manufactured means produced or made."),
                SkillQuizQuestion("vp11_2", "Which phrase refers to solar and wind power?", listOf("Renewable energy", "Fossil fuel", "Heavy luggage", "Ancient history"), 0, "Solar and wind are renewable energy sources."),
                SkillQuizQuestion("vp11_3", "What is an imagined, non-factual scenario called?", listOf("A hypothetical situation", "A historical fact", "A daily routine", "A receipt"), 0, "Hypothetical means imagined or theoretical.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp11_1", "Choose the correct Passive form: 'The new bridge ___ built last year.'", listOf("is", "was", "were", "has"), 1, "Singular subject 'The new bridge' + 'last year' requires 'was built'."),
                SkillQuizQuestion("gp11_2", "Complete the Second Conditional: 'If I ___ the answer, I would tell you.'", listOf("know", "knew", "known", "will know"), 1, "Second Conditional uses Past Simple ('knew') in the 'if' clause."),
                SkillQuizQuestion("gp11_3", "Complete the advice phrase: 'If I ___ you, I would practice every day.'", listOf("am", "was", "were", "be"), 2, "In formal Second Conditional, we use 'If I were you'.")
            )
        ),

        // === BOX 12: Intermediate B (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_12_inter_b",
            order = 12,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 4 • Intermediate Block (Boxes 11–13)",
            title = "Intermediate B",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "INTB-112",
            sizeMb = 17.2f,
            badgeName = "Reported Speech & Perfect Continuous",
            emoji = "🗣️",
            badgeDesc = "Completed Box 12 (Intermediate B): Reported Speech, Past Perfect, and Present Perfect Continuous.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Reported Speech (Indirect Speech), Past Perfect ('had + V3'), and Present Perfect Continuous.",
            vocab = listOf(
                VocabWordItem("v12_1", "Confirmed that", "Stated officially that something is true.", "أكّد أن", "The coordinator confirmed that the exam starts at 10 AM.", "Reporting"),
                VocabWordItem("v12_2", "Prior to", "Before a particular time or event.", "قبل / سابق لـ", "She had studied basic vocabulary prior to joining the course.", "Preposition"),
                VocabWordItem("v12_3", "Consistently", "In every case or on every occasion; steadily.", "بانتظام / باستمرار", "He has been practicing consistently for three months.", "Adverb"),
                VocabWordItem("v12_4", "Clarify", "Make a statement or situation less confused and more understandable.", "يوضّح", "Could you clarify the difference between these two tenses?", "Verb")
            ),
            grammarRule = "1) Past Perfect ('had + V3') shows which of two past actions happened first. 2) Reported Speech shifts present tenses back one step into the past ('am/is' -> 'was', 'will' -> 'would').",
            grammarFormula = "Past Perfect: Subject + had + V3 | Reported: He said (that) + backshifted verb",
            affirmativeExamples = listOf("When we arrived, the class had already started.", "She said that she was preparing for Box 13.", "I have been studying English since morning."),
            negativeExamples = listOf("He told me that he had not received the file yet."),
            questionExamples = listOf("Had you studied Past Perfect before today?", "What did the teacher say about the homework?"),
            assignmentTask = "Convert 4 direct quotes from your teacher into Reported Speech and write 2 Past Perfect sentences.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp12_1", "What does 'Prior to' mean?", listOf("After", "Before", "During", "Because of"), 1, "'Prior to' is a formal phrase meaning 'before'."),
                SkillQuizQuestion("vp12_2", "Which word means 'make something clearer and easier to understand'?", listOf("Clarify", "Complicate", "Cancel", "Ignore"), 0, "To clarify is to make clear."),
                SkillQuizQuestion("vp12_3", "If someone studies steadily every single day, they study ___.", listOf("consistently", "rarely", "accidentally", "never"), 0, "'Consistently' means steadily and regularly.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp12_1", "Complete with Past Perfect: 'By the time I got to the station, the train ___ left.'", listOf("has", "had", "was", "did"), 1, "'Had left' shows the train departed before I arrived."),
                SkillQuizQuestion("gp12_2", "Report this statement: Sara said, 'I will unlock the next level.'", listOf("Sara said that she will unlock the next level.", "Sara said that she would unlock the next level.", "Sara said that I would unlock it.", "Sara says she unlocked it."), 1, "In Reported Speech, 'will' shifts back to 'would'."),
                SkillQuizQuestion("gp12_3", "Complete with Present Perfect Continuous: 'We ___ waiting for twenty minutes.'", listOf("have been", "has been", "had", "are"), 0, "'We' takes 'have been + V-ing'.")
            )
        ),

        // === BOX 13: Skills 4 (Listening & Speaking Skills - 2 Specialized Rooms) ===
        LevelSeed(
            id = "lvl_13_skills_ls_4",
            order = 13,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 4 • Intermediate Block (Boxes 11–13)",
            title = "Skills 4",
            boxType = BoxArchitectureType.SKILLS_LS_2_ROOMS,
            hours = 45,
            code = "SLS4-113",
            sizeMb = 19.4f,
            badgeName = "Active Listener & Speaker II",
            emoji = "🎧",
            badgeDesc = "Mastered Box 13 (Skills 4): intermediate audio dictation, vocabulary capture, and fluent spoken discussions.",
            skills = listOf("Listening & Vocabulary Dictation Room", "Speaking & Voice Interaction Room"),
            topicFocus = "Intermediate listening dictation, workplace & academic dialogues, and structured voice responses.",
            vocab = listOf(
                VocabWordItem("v13_1", "From my perspective", "Used to express a thoughtful point of view in discussion.", "من وجهة نظري", "From my perspective, technology makes language learning much faster.", "Speaking"),
                VocabWordItem("v13_2", "Collaborate", "Work jointly with others on an activity or project.", "يتعاون", "Students collaborate in pairs during speaking workshops.", "Verb"),
                VocabWordItem("v13_3", "Significant improvement", "A noticeable and important positive change.", "تحسّن ملحوظ", "Your pronunciation showed significant improvement this month.", "Collocation"),
                VocabWordItem("v13_4", "Could you elaborate on that?", "Polite way to ask a speaker for more detail.", "هل يمكنك توضيح ذلك بالتفصيل؟", "That is an interesting idea; could you elaborate on that?", "Discussion")
            ),
            grammarRule = "Structured Spoken Opinions: State your main point clearly, support it with a reason ('because / since'), and give a brief example.",
            grammarFormula = "Point of View + Reason ('because') + Personal Example",
            affirmativeExamples = listOf("I believe group study is effective because students can correct each other.", "We have been collaborating on this presentation all week."),
            negativeExamples = listOf("I don't completely agree with that statement because the circumstances have changed."),
            questionExamples = listOf("How has technology changed the way you learn English?", "What would you do if you could study anywhere in the world?"),
            assignmentTask = "Complete the 3 intermediate dictation passages and record accurate spoken answers to all 3 discussion questions.",
            vocabPractice = emptyList(),
            grammarPractice = emptyList(),
            listeningExercises = listOf(
                ListeningDictationExercise(
                    id = "ls4_dict_1",
                    title = "Passage 1 • Academic Progress Feedback",
                    audioTranscript = "Your speaking fluency has shown significant improvement this semester",
                    hintText = "Listen for the two-word phrase meaning 'noticeable positive change'.",
                    keyWordsToExtract = listOf(
                        "fluency" to "الطلاقة اللغوية",
                        "significant improvement" to "تحسّن ملحوظ"
                    )
                ),
                ListeningDictationExercise(
                    id = "ls4_dict_2",
                    title = "Passage 2 • Team Project Meeting",
                    audioTranscript = "We need to collaborate closely to finish before the deadline",
                    hintText = "Listen for the verb meaning 'work together' and the final due date noun.",
                    keyWordsToExtract = listOf(
                        "collaborate" to "يتعاون",
                        "deadline" to "الموعد النهائي"
                    )
                ),
                ListeningDictationExercise(
                    id = "ls4_dict_3",
                    title = "Passage 3 • Problem Solving",
                    audioTranscript = "If we practice every day our confidence will grow rapidly",
                    hintText = "A First Conditional sentence about daily practice and confidence.",
                    keyWordsToExtract = listOf(
                        "confidence" to "الثقة بالنفس",
                        "rapidly" to "بسرعة"
                    )
                )
            ),
            speakingPrompts = listOf(
                SpeakingVoicePrompt(
                    id = "ls4_spk_1",
                    questionAudioText = "How has practicing listening and speaking helped your confidence in English?",
                    arabicPromptHint = "أجب بصوتك: كيف ساعدك تدريب الاستماع والتحدث على زيادة ثقتك في الإنجليزية؟",
                    sampleTargetAnswer = "Practicing listening and speaking every day has improved my confidence and pronunciation",
                    requiredKeywords = listOf("practicing", "confidence"),
                    grammarFocusLabel = "Present Perfect Impact"
                ),
                SpeakingVoicePrompt(
                    id = "ls4_spk_2",
                    questionAudioText = "If you could visit any country to practice English, where would you go and why?",
                    arabicPromptHint = "أجب بصوتك: لو أتيحت لك زيارة أي دولة لممارسة الإنجليزية، أين ستذهب ولماذا؟",
                    sampleTargetAnswer = "If I could visit any country I would go to Britain to practice English with native speakers",
                    requiredKeywords = listOf("would", "practice", "English"),
                    grammarFocusLabel = "Second Conditional Speaking"
                ),
                SpeakingVoicePrompt(
                    id = "ls4_spk_3",
                    questionAudioText = "Do you prefer studying alone or collaborating with classmates, and why?",
                    arabicPromptHint = "أجب بصوتك: هل تفضل الدراسة بمفردك أم التعاون مع زملائك، ولماذا؟",
                    sampleTargetAnswer = "I prefer collaborating with classmates because we can share ideas and practice speaking together",
                    requiredKeywords = listOf("prefer", "because"),
                    grammarFocusLabel = "Opinion & Justification"
                )
            )
        ),

        // === BOX 14: Upper intermediate A (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_14_upper_inter_a",
            order = 14,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Stage 5 • Upper-Intermediate Block (Boxes 14–17)",
            title = "Upper intermediate A",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "UPIA-201",
            sizeMb = 17.8f,
            badgeName = "Third Conditional & Past Deduction",
            emoji = "🔬",
            badgeDesc = "Completed Box 14 (Upper intermediate A): Third Conditional and Past Modals ('must have / should have').",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Third Conditional ('If + had + V3, would have + V3') and Modals of Past Deduction & Regret.",
            vocab = listOf(
                VocabWordItem("v14_1", "In hindsight", "Understanding of a situation only after it has happened.", "بالنظر إلى الماضي / بعد فوات الأوان", "In hindsight, I should have started preparing a week earlier.", "Idiom"),
                VocabWordItem("v14_2", "Inevitable", "Certain to happen; unavoidable.", "حتمي / لا مفر منه", "With daily dedication, academic progress is inevitable.", "Adjective"),
                VocabWordItem("v14_3", "Crucial factor", "A decisive or critically important element.", "عامل حاسم", "Consistent vocabulary review is a crucial factor in fluency.", "Collocation"),
                VocabWordItem("v14_4", "Overcome obstacles", "Successfully deal with and defeat difficulties.", "يتغلب على العقبات", "She worked hard to overcome obstacles in pronunciation.", "Verb Phrase")
            ),
            grammarRule = "1) Third Conditional ('If + had + V3, would have + V3') talks about hypothetical past situations and their imagined past results. 2) Past Modals: 'should have + V3' (past regret/criticism), 'must have + V3' (logical past certainty).",
            grammarFormula = "If + Subject + had + V3, Subject + would have + V3",
            affirmativeExamples = listOf("If I had known about the workshop, I would have attended.", "He must have studied hard because he earned three stars."),
            negativeExamples = listOf("If she hadn't set an alarm, she wouldn't have arrived on time."),
            questionExamples = listOf("What would you have done if you had missed the train?", "Should we have reviewed Room 2 before taking the quiz?"),
            assignmentTask = "Write 3 Third Conditional sentences reflecting on past decisions and 2 sentences using 'should have + V3'.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp14_1", "What does 'In hindsight' mean?", listOf("Looking into the future", "Understanding a situation after it has already happened", "Closing your eyes", "Forgetting the past"), 1, "Hindsight is understanding after an event has occurred."),
                SkillQuizQuestion("vp14_2", "If something is certain to happen and cannot be avoided, it is ___.", listOf("inevitable", "optional", "unlikely", "accidental"), 0, "'Inevitable' means unavoidable."),
                SkillQuizQuestion("vp14_3", "What does 'Overcome obstacles' mean?", listOf("Create new problems", "Successfully deal with difficulties", "Give up quickly", "Avoid studying"), 1, "Overcoming obstacles means succeeding despite difficulties.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp14_1", "Complete the Third Conditional: 'If they had left earlier, they ___ caught the flight.'", listOf("will have", "would have", "would", "had"), 1, "The main clause of the Third Conditional uses 'would have + V3'."),
                SkillQuizQuestion("gp14_2", "Which phrase expresses regret about a past action?", listOf("I should have studied more.", "I must study tomorrow.", "I can study now.", "I will study."), 0, "'Should have + V3' expresses past regret or advice in hindsight."),
                SkillQuizQuestion("gp14_3", "The ground is completely wet. It ___ rained heavily last night.", listOf("must have", "should", "can't", "would"), 0, "'Must have + V3' expresses a strong logical deduction about the past.")
            )
        ),

        // === BOX 15: Upper intermediate B (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_15_upper_inter_b",
            order = 15,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Stage 5 • Upper-Intermediate Block (Boxes 14–17)",
            title = "Upper intermediate B",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "UPIB-202",
            sizeMb = 18.1f,
            badgeName = "Future Perfect & Causative Master",
            emoji = "🏛️",
            badgeDesc = "Completed Box 15 (Upper intermediate B): Future Perfect, Future Continuous, and Causative 'have/get something done'.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Future Continuous ('will be + V-ing'), Future Perfect ('will have + V3'), and Causative structures.",
            vocab = listOf(
                VocabWordItem("v15_1", "By the end of this year", "Time marker used with Future Perfect for completion before a deadline.", "بحلول نهاية هذا العام", "By the end of this year, I will have completed all 21 boxes.", "Time Marker"),
                VocabWordItem("v15_2", "Delegate tasks", "Assign responsibility for specific tasks to others.", "يفوّض المهام", "Effective managers know how to delegate tasks clearly.", "Business"),
                VocabWordItem("v15_3", "Comprehensive", "Complete and including all or nearly all elements.", "شامل / وافٍ", "Room 2 provides a comprehensive explanation of the grammar rule.", "Adjective"),
                VocabWordItem("v15_4", "Milestone", "A significant stage or event in the development of something.", "محطة هامة / إنجاز مرحلي", "Completing Box 15 is a major milestone in your English journey.", "Noun")
            ),
            grammarRule = "1) Future Perfect ('will have + V3') describes an action that will be completed before a specific future time. 2) Causative ('have/get + Object + Past Participle') describes arranging for someone else to do a service for you.",
            grammarFormula = "Future Perfect: will have + V3 | Causative: have/get + Object + V3",
            affirmativeExamples = listOf("By next month, she will have mastered 500 new words.", "I had my essay proofread by the instructor."),
            negativeExamples = listOf("They won't have finished the renovation by Monday."),
            questionExamples = listOf("How many boxes will you have unlocked by June?", "Where did you get your laptop repaired?"),
            assignmentTask = "Write 3 Future Perfect sentences starting with 'By next year...' and 2 Causative sentences.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp15_1", "What is a 'Milestone'?", listOf("A small mistake", "An important stage or achievement in a journey", "A broken road", "A short break"), 1, "A milestone is a significant stage of progress."),
                SkillQuizQuestion("vp15_2", "Which word means 'complete and covering all necessary details'?", listOf("Comprehensive", "Partial", "Brief", "Careless"), 0, "'Comprehensive' means thorough and complete."),
                SkillQuizQuestion("vp15_3", "Which time phrase typically triggers the Future Perfect tense?", listOf("Yesterday morning", "By the end of this year", "Two days ago", "Every Sunday"), 1, "'By + future time' signals Future Perfect.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp15_1", "Complete with Future Perfect: 'By 6:00 PM, we ___ completed the exam.'", listOf("will be", "will have", "would have", "have"), 1, "'By 6:00 PM' takes 'will have + V3'."),
                SkillQuizQuestion("gp15_2", "Choose the correct Causative form: 'I need to get my car ___ before the trip.'", listOf("service", "servicing", "serviced", "to service"), 2, "Causative structure is 'get + object + Past Participle (serviced)'."),
                SkillQuizQuestion("gp15_3", "At 10 AM tomorrow, I ___ taking my speaking assessment.", listOf("will be", "will have", "had been", "am"), 0, "An action in progress at a specific future time uses Future Continuous ('will be + V-ing').")
            )
        ),

        // === BOX 16: Upper intermediate C (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_16_upper_inter_c",
            order = 16,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Stage 5 • Upper-Intermediate Block (Boxes 14–17)",
            title = "Upper intermediate C",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "UPIC-203",
            sizeMb = 18.5f,
            badgeName = "Participle Clauses & Emphasis",
            emoji = "🌟",
            badgeDesc = "Completed Box 16 (Upper intermediate C): Participle clauses, Cleft sentences, and mixed conditionals.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Cleft Sentences for emphasis ('What I need is... / It was... that...'), Participle Clauses, and Mixed Conditionals.",
            vocab = listOf(
                VocabWordItem("v16_1", "Compelling argument", "A convincing and powerful reason that persuades others.", "حجة مقنعة وقوية", "The speaker presented a compelling argument supported by data.", "Academic"),
                VocabWordItem("v16_2", "Acknowledge", "Accept or admit the existence or truth of something.", "يقرّ بـ / يعترف بـ", "Good writers acknowledge different viewpoints in an essay.", "Verb"),
                VocabWordItem("v16_3", "Furthermore / Moreover", "Formal transitions used to add weight to an argument.", "علاوة على ذلك / فضلاً عن ذلك", "The method is fast; moreover, it is highly accurate.", "Transition"),
                VocabWordItem("v16_4", "Substantial", "Of considerable importance, size, or worth.", "جوهري / كبير", "She made substantial progress in her academic writing.", "Adjective")
            ),
            grammarRule = "1) Cleft Sentences ('What + clause + is/was...' or 'It is/was + X + that...') add strong focus and emphasis. 2) Participle Clauses ('Having finished the work, he...') express cause or sequence concisely.",
            grammarFormula = "What + Subject + Verb + is/was + Focus | Having + V3, Main Clause",
            affirmativeExamples = listOf("What helped me most was the daily flashcard review.", "Having completed Room 3, she moved directly to Room 4."),
            negativeExamples = listOf("It wasn't the vocabulary that confused him, but the complex tense."),
            questionExamples = listOf("What is it that motivates you to master English?"),
            assignmentTask = "Rewrite 4 standard sentences as Cleft Sentences ('What...' and 'It was...') for emphasis.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp16_1", "What is a 'Compelling argument'?", listOf("A weak excuse", "A convincing and persuasive reason", "A loud shouting match", "An unrelated question"), 1, "Compelling means powerful and convincing."),
                SkillQuizQuestion("vp16_2", "Which word means 'of considerable size, worth, or importance'?", listOf("Substantial", "Tiny", "Rare", "Accidental"), 0, "'Substantial' means large and important."),
                SkillQuizQuestion("vp16_3", "What does 'Acknowledge' mean?", listOf("Recognize or admit the truth of something", "Deny completely", "Forget quickly", "Hide"), 0, "To acknowledge is to recognize or accept as true.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp16_1", "Choose the Cleft Sentence that emphasizes 'daily practice':", listOf("Daily practice is good.", "What improves fluency most is daily practice.", "Practice daily.", "Fluency is good."), 1, "'What improves fluency most is...' is a Wh-cleft structure for emphasis."),
                SkillQuizQuestion("gp16_2", "Complete the participle clause: '___ finished the assignment, Sara submitted her notebook.'", listOf("Have", "Having", "Had", "Has"), 1, "'Having + Past Participle' shows the first action was completed before the second."),
                SkillQuizQuestion("gp16_3", "Complete the mixed conditional: 'If I had taken that course last year, I ___ fluent now.'", listOf("would be", "would have been", "will be", "am"), 0, "Past condition ('If I had taken') with a present result ('now') uses 'would be'.")
            )
        ),

        // === BOX 17: Skills 5 RW (Reading & Writing Skills - 2 Specialized Rooms) ===
        LevelSeed(
            id = "lvl_17_skills_rw_5",
            order = 17,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Stage 5 • Upper-Intermediate Block (Boxes 14–17)",
            title = "Skills 5 RW",
            boxType = BoxArchitectureType.SKILLS_RW_2_ROOMS,
            hours = 45,
            code = "SRW5-204",
            sizeMb = 18.9f,
            badgeName = "Academic Sentence Architect III",
            emoji = "🖋️",
            badgeDesc = "Mastered Box 17 (Skills 5 RW): academic nominalization, complex verbal/nominal synthesis, and advanced reordering.",
            skills = listOf("Grammar Explanation Room", "Writing & Sentence Construction Room"),
            topicFocus = "Academic Nominalization (turning verbal clauses into sophisticated nominal structures), Inversion, and Essay Synthesis.",
            vocab = listOf(
                VocabWordItem("v17_1", "Nominalization", "Converting verbs/adjectives into noun phrases for formal academic style.", "تحويل الجمل الفعلية إلى تراكيب اسمية أكاديمية", "Nominalization makes academic essays more concise and objective.", "Academic Writing"),
                VocabWordItem("v17_2", "Not only ... but also", "Correlative conjunction for strong dual emphasis.", "ليس فقط ... بل أيضاً", "Not only did she master the vocabulary, but she also scored 100% in grammar.", "Structure"),
                VocabWordItem("v17_3", "Consequently", "As a result; therefore.", "وبالتالي / نتيجة لذلك", "He practiced sentence construction daily; consequently, his writing score rose.", "Connector"),
                VocabWordItem("v17_4", "Cohesion", "The grammatical and lexical linking within a text that holds it together.", "الترابط النصي والمنطقي", "Strong cohesion ensures every sentence flows naturally into the next.", "Writing")
            ),
            grammarRule = "1) Academic Nominalization vs. Verbal Clauses: Transform action-heavy clauses ('The institute analyzed the results carefully') into formal Nominal structures ('The careful analysis of the results was conducted by the institute'). 2) Negative Inversion ('Not only did + Subject + Base Verb...') for rhetorical power.",
            grammarFormula = "Nominalization: Determiner + Adjective + Noun Phrase + Verb | Inversion: Not only + Auxiliary + Subject + Verb",
            affirmativeExamples = listOf(
                "Verbal Clause: Students participated actively, which improved their grades.",
                "Nominalized Structure: Active student participation led to higher grades.",
                "Inversion: Not only does reading build vocabulary, but it also refines grammar."
            ),
            negativeExamples = listOf(
                "Avoid stringing together too many simple 'and / so' clauses in formal writing."
            ),
            questionExamples = listOf(
                "How does nominalization elevate the tone of an academic paragraph?",
                "Why do we invert the auxiliary and subject after 'Not only' at the start of a sentence?"
            ),
            nominalVsVerbalNotes = listOf(
                "Academic Nominal Sentence (التركيب الاسمي الأكاديمي)" to "Centers on a rich Noun Phrase subject: 'The rapid expansion of digital education transformed classrooms.'",
                "Dynamic Verbal Sentence (التركيب الفعلي الحركي)" to "Centers on an active verb: 'Digital education expanded rapidly and transformed classrooms.'",
                "Emphatic Inversion (التقديم والتأخير البلاغي)" to "'Seldom have we seen such dedication' / 'Not only did he pass, but he also earned three stars.'"
            ),
            assignmentTask = "Complete all 4 advanced sentence construction exercises and transform 3 verbal sentences into formal nominal structures.",
            vocabPractice = emptyList(),
            grammarPractice = listOf(
                SkillQuizQuestion("gp17_1", "Which sentence is the formal Nominalized version of 'The team investigated the issue thoroughly'?", listOf("The team did a thorough investigate.", "A thorough investigation of the issue was conducted by the team.", "Investigating thoroughly the team.", "The issue thoroughly investigated."), 1, "'A thorough investigation of the issue...' converts the verb 'investigated' and adverb 'thoroughly' into a formal noun phrase."),
                SkillQuizQuestion("gp17_2", "Choose the correct inverted structure after 'Not only':", listOf("Not only she passed the test, but she also got 100%.", "Not only did she pass the test, but she also got 100%.", "Not only passed she the test.", "Not only she did pass."), 1, "When a sentence begins with 'Not only', invert the auxiliary verb: 'Not only did she pass...'."),
                SkillQuizQuestion("gp17_3", "Which connector means 'as a result / therefore'?", listOf("Consequently", "Although", "Whereas", "Despite"), 0, "'Consequently' introduces a logical result.")
            ),
            rwExercises = listOf(
                SentenceBuildExercise(
                    id = "rw5_ex1",
                    instruction = "Reorder the words to build an Academic Nominal Sentence (جملة اسمية أكاديمية):",
                    sentenceTypeTag = "Academic Nominal Structure",
                    scrambledWords = listOf("fluency", "Consistent", "accelerates", "practice", "language"),
                    targetSentence = "Consistent practice accelerates language fluency",
                    grammarHint = "Start with the Nominal Subject ('Consistent practice'), then the verb ('accelerates'), then the object ('language fluency')."
                ),
                SentenceBuildExercise(
                    id = "rw5_ex2",
                    instruction = "Reorder the words to build a formal inverted sentence starting with 'Not only':",
                    sentenceTypeTag = "Emphatic Inversion",
                    scrambledWords = listOf("Not", "only", "did", "study", "he", "passed", "also", "he"),
                    targetSentence = "Not only did he study he also passed",
                    grammarHint = "Use 'Not only + did + Subject + Base Verb', followed by the second clause."
                ),
                SentenceBuildExercise(
                    id = "rw5_ex3",
                    instruction = "Construct a Verbal Action Sentence (جملة فعلية) showing cause and effect:",
                    sentenceTypeTag = "Verbal Action Clause",
                    scrambledWords = listOf("carefully", "reviewed", "She", "her", "mistakes", "yesterday"),
                    targetSentence = "She reviewed her mistakes carefully yesterday",
                    grammarHint = "Subject ('She') + Verb ('reviewed') + Object ('her mistakes') + Manner ('carefully') + Time ('yesterday')."
                ),
                SentenceBuildExercise(
                    id = "rw5_ex4",
                    instruction = "Reorder to form a complex sentence with 'Despite':",
                    sentenceTypeTag = "Complex Prepositional Phrase",
                    scrambledWords = listOf("completed", "Despite", "the", "challenge", "they", "task", "the"),
                    targetSentence = "Despite the challenge they completed the task",
                    grammarHint = "'Despite + Noun Phrase ('the challenge')', followed by 'they completed the task'."
                )
            )
        ),

        // === BOX 18: Advance A (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_18_advance_a",
            order = 18,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Stage 6 • Advance Mastery Block (Boxes 18–21)",
            title = "Advance A",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "ADVA-205",
            sizeMb = 19.2f,
            badgeName = "Inversion & Subjunctive Scholar",
            emoji = "🎓",
            badgeDesc = "Completed Box 18 (Advance A): Negative Inversion ('Seldom / Never / Rarely') and the Formal Subjunctive.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Negative Inversion ('Rarely / Seldom / Little did...') and Formal Subjunctive ('essential that he be...').",
            vocab = listOf(
                VocabWordItem("v18_1", "Unprecedented", "Never done or known before.", "غير مسبوق", "The digital library gives students unprecedented access to study materials.", "Adjective"),
                VocabWordItem("v18_2", "Imperative", "Of vital importance; crucial.", "ضروري للغاية / حتمي", "It is imperative that every candidate arrive fifteen minutes early.", "Subjunctive Trigger"),
                VocabWordItem("v18_3", "Articulate", "Able to express ideas clearly and effectively in speech or writing.", "فصيح / بليغ", "An articulate speaker organizes thoughts before answering.", "Adjective"),
                VocabWordItem("v18_4", "Scrutinize", "Examine or inspect closely and thoroughly.", "يدقق / يفحص بعناية", "Always scrutinize your essay for grammar accuracy before submitting.", "Verb")
            ),
            grammarRule = "1) Negative Adverbial Inversion: When a sentence begins with 'Seldom', 'Rarely', 'Never', or 'Little', invert the auxiliary and subject ('Rarely have I seen...'). 2) Formal Subjunctive: After 'essential / imperative / recommend that', use the bare infinitive ('that he study', 'that she be').",
            grammarFormula = "Rarely/Seldom + Auxiliary + Subject + Verb | It is imperative that + Subject + Base Verb",
            affirmativeExamples = listOf("Seldom have we witnessed such rapid progress.", "It is essential that every student complete the practice rooms."),
            negativeExamples = listOf("The committee recommended that the policy not be delayed."),
            questionExamples = listOf("When do we use the bare infinitive in the subjunctive mood?"),
            assignmentTask = "Write 3 sentences using Negative Inversion ('Rarely', 'Never before', 'Little did') and 2 Subjunctive sentences.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp18_1", "What does 'Unprecedented' mean?", listOf("Happening every day", "Never done or known before", "Old-fashioned", "Predictable"), 1, "'Unprecedented' means never known or experienced before."),
                SkillQuizQuestion("vp18_2", "If a speaker expresses complex ideas very clearly and fluently, they are ___.", listOf("articulate", "silent", "hesitant", "confused"), 0, "'Articulate' means able to speak clearly and effectively."),
                SkillQuizQuestion("vp18_3", "What does 'Scrutinize' mean?", listOf("Glance quickly", "Examine closely and thoroughly", "Delete", "Ignore"), 1, "To scrutinize is to inspect carefully.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp18_1", "Complete the inverted sentence: 'Rarely ___ such dedication among students.'", listOf("we see", "do we see", "saw we", "we have seen"), 1, "After 'Rarely' at the start of a sentence, invert auxiliary + subject: 'do we see'."),
                SkillQuizQuestion("gp18_2", "Complete with the Formal Subjunctive: 'It is imperative that every student ___ on time.'", listOf("is", "be", "are", "was"), 1, "The formal subjunctive uses the bare infinitive 'be'."),
                SkillQuizQuestion("gp18_3", "Little ___ know that the surprise test was today.", listOf("he did", "did he", "he does", "does he"), 1, "'Little' at the beginning triggers inversion: 'Little did he know'.")
            )
        ),

        // === BOX 19: Advance B (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_19_advance_b",
            order = 19,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Stage 6 • Advance Mastery Block (Boxes 18–21)",
            title = "Advance B",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "ADVB-206",
            sizeMb = 19.6f,
            badgeName = "Nuance & Discourse Strategist",
            emoji = "🧠",
            badgeDesc = "Completed Box 19 (Advance B): Academic hedging, passive reporting structures, and idiomatic precision.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Academic Hedging, Impersonal Passive Reporting ('It is widely believed that... / X is thought to have...'), and Collocations.",
            vocab = listOf(
                VocabWordItem("v19_1", "Plausible", "Seeming reasonable or probable.", "معقول / محتمل", "The researcher offered a plausible explanation for the findings.", "Adjective"),
                VocabWordItem("v19_2", "Mitigate", "Make less severe, serious, or painful.", "يخفف من حدة", "Preparation helps mitigate exam anxiety.", "Verb"),
                VocabWordItem("v19_3", "Consensus", "A general agreement among a group of people.", "إجماع / توافق آراء", "There is a growing consensus that active recall is the best study method.", "Noun"),
                VocabWordItem("v19_4", "Discrepancy", "A lack of compatibility or similarity between two or more facts.", "تفاوت / تضارب", "The auditor noticed a small discrepancy in the report.", "Noun")
            ),
            grammarRule = "Impersonal Passive Reporting: Use 'It is said/believed/reported that...' or 'Subject + is/are thought to + Base Verb / to have + V3' to present objective academic claims.",
            grammarFormula = "It is widely believed that + Clause | Subject + is thought to have + V3",
            affirmativeExamples = listOf("It is widely accepted that bilingualism enhances cognitive flexibility.", "The manuscript is believed to have been written in the twelfth century."),
            negativeExamples = listOf("The new regulation is not expected to cause any delay."),
            questionExamples = listOf("How do we convert 'People think he left early' into an impersonal passive?"),
            assignmentTask = "Rewrite 4 general claims using Impersonal Passive structures ('It is reported that...' and '...is believed to have...').",
            vocabPractice = listOf(
                SkillQuizQuestion("vp19_1", "What does 'Mitigate' mean?", listOf("Make worse", "Make less severe or serious", "Celebrate", "Exaggerate"), 1, "To mitigate means to reduce the severity of something."),
                SkillQuizQuestion("vp19_2", "What is a 'Consensus'?", listOf("A heated argument", "General agreement among a group", "A private secret", "A sudden cancellation"), 1, "Consensus means general agreement."),
                SkillQuizQuestion("vp19_3", "If an explanation seems reasonable and believable, it is ___.", listOf("plausible", "impossible", "reckless", "invisible"), 0, "'Plausible' means reasonable or probable.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp19_1", "Complete the impersonal passive: 'The ancient city is believed ___ been founded 3,000 years ago.'", listOf("that", "to have", "having", "for"), 1, "'is believed + to have + V3' refers to a past event."),
                SkillQuizQuestion("gp19_2", "Which sentence uses academic hedging appropriately?", listOf("This proves 100% that everyone always succeeds.", "The evidence suggests that regular practice tends to improve retention.", "Nobody ever forgets anything.", "It is impossible to disagree."), 1, "Words like 'suggests' and 'tends to' provide measured academic hedging."),
                SkillQuizQuestion("gp19_3", "Choose the correct preposition: 'There is a strong correlation ___ study habits and test scores.'", listOf("between", "among", "with", "for"), 0, "Use 'correlation between X and Y'.")
            )
        ),

        // === BOX 20: Advance C (General - 4 Rooms) ===
        LevelSeed(
            id = "lvl_20_advance_c",
            order = 20,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Stage 6 • Advance Mastery Block (Boxes 18–21)",
            title = "Advance C",
            boxType = BoxArchitectureType.GENERAL_4_ROOMS,
            hours = 45,
            code = "ADVC-207",
            sizeMb = 20.1f,
            badgeName = "C1/C2 Rhetoric & Synthesis Master",
            emoji = "👑",
            badgeDesc = "Completed Box 20 (Advance C): Advanced ellipsis, substitution, and C1/C2 academic synthesis.",
            skills = listOf("Vocabulary", "Grammar Explanation", "Vocab Practice", "Grammar Practice"),
            topicFocus = "Ellipsis & Substitution (avoiding repetition in advanced prose), Advanced Discourse Markers, and C1/C2 Precision.",
            vocab = listOf(
                VocabWordItem("v20_1", "Ubiquitous", "Present, appearing, or found everywhere.", "منتشر في كل مكان", "Smartphones have become ubiquitous in modern education.", "Adjective"),
                VocabWordItem("v20_2", "Pragmatic", "Dealing with things sensibly and realistically in a practical way.", "عملي / واقعي", "We need a pragmatic approach to time management.", "Adjective"),
                VocabWordItem("v20_3", "Notwithstanding", "In spite of; nevertheless.", "على الرغم من ذلك", "Notwithstanding the initial difficulties, the project succeeded brilliantly.", "Preposition/Adverb"),
                VocabWordItem("v20_4", "Synthesize", "Combine multiple sources or ideas into a coherent whole.", "يدمج / يؤلف بين الأفكار", "Advanced learners synthesize ideas from multiple readings.", "Verb")
            ),
            grammarRule = "Ellipsis & Substitution: Replace repeated clauses with 'so / not' ('I believe so', 'If not...') or auxiliary verbs ('She has finished, and so have I') to achieve native-like conciseness.",
            grammarFormula = "Auxiliary Substitution: so + Aux + Subject | Clause Substitution: think/hope/believe so",
            affirmativeExamples = listOf("Sara passed with three stars, and so did her study partner.", "Are we ready for Box 21? I certainly hope so."),
            negativeExamples = listOf("He hasn't missed a single session, and neither have I."),
            questionExamples = listOf("How does substitution prevent repetitive phrasing in advanced essays?"),
            assignmentTask = "Complete the C1/C2 vocabulary and grammar mastery drills to unlock the final capstone box.",
            vocabPractice = listOf(
                SkillQuizQuestion("vp20_1", "What does 'Ubiquitous' mean?", listOf("Extremely rare", "Found or appearing everywhere", "Broken", "Temporary"), 1, "'Ubiquitous' means present everywhere."),
                SkillQuizQuestion("vp20_2", "A sensible, practical approach to solving problems is called ___.", listOf("pragmatic", "chaotic", "fragile", "obscure"), 0, "'Pragmatic' means practical and realistic."),
                SkillQuizQuestion("vp20_3", "What does 'Notwithstanding' mean?", listOf("Because of", "In spite of / nevertheless", "In addition to", "Before"), 1, "'Notwithstanding' is a formal synonym for 'despite'.")
            ),
            grammarPractice = listOf(
                SkillQuizQuestion("gp20_1", "Complete the substitution: 'I haven't visited London yet, and ___ has my brother.'", listOf("so", "neither", "either", "too"), 1, "After a negative clause, use 'neither + auxiliary + subject'."),
                SkillQuizQuestion("gp20_2", "Complete with clause substitution: 'Will the exam be difficult? I don't think ___.'", listOf("it", "that", "so", "this"), 2, "'I don't think so' substitutes for the previous clause."),
                SkillQuizQuestion("gp20_3", "Choose the correct structure: '___ the rain, the outdoor ceremony continued.'", listOf("Although", "Notwithstanding", "Even though", "Whereas"), 1, "'Notwithstanding' is a preposition that can be followed directly by a noun phrase ('the rain').")
            )
        ),

        // === BOX 21: Skills 6 (Listening & Speaking Skills - 2 Specialized Rooms) ===
        LevelSeed(
            id = "lvl_21_skills_ls_6",
            order = 21,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Stage 6 • Advance Mastery Block (Boxes 18–21)",
            title = "Skills 6",
            boxType = BoxArchitectureType.SKILLS_LS_2_ROOMS,
            hours = 45,
            code = "SLS6-208",
            sizeMb = 21.5f,
            badgeName = "Grand Master Communicator (Box 21)",
            emoji = "🏆",
            badgeDesc = "Graduated Box 21 (Skills 6): Advanced academic listening dictation and fluent C1 voice responses!",
            skills = listOf("Listening & Vocabulary Dictation Room", "Speaking & Voice Interaction Room"),
            topicFocus = "Capstone academic lecture dictation, advanced vocabulary capture, and C1/C2 spoken argument & pronunciation evaluation.",
            vocab = listOf(
                VocabWordItem("v21_1", "To sum up", "Used to introduce a concise final summary of a presentation.", "خلاصة القول / باختصار", "To sum up, consistent practice across all four skills guarantees success.", "Presentation"),
                VocabWordItem("v21_2", "Undeniably", "In a way that cannot be disputed or doubted.", "مما لا شك فيه", "Mastering a second language undeniably opens global career doors.", "Adverb"),
                VocabWordItem("v21_3", "Crucial milestone", "A decisive turning point or achievement.", "إنجاز مفصلي هام", "Completing Box 21 is a crucial milestone in your academic career.", "Collocation"),
                VocabWordItem("v21_4", "Fluency and accuracy", "The twin goals of natural flow and grammatical correctness.", "الطلاقة والدقة اللغوية", "Our curriculum balances spoken fluency and grammatical accuracy.", "Core Concept")
            ),
            grammarRule = "Capstone Spoken Rhetoric: Structure spoken answers with a thesis statement, a supporting causal clause, and a concluding summary.",
            grammarFormula = "Thesis Statement -> Supporting Evidence -> Concluding Takeaway",
            affirmativeExamples = listOf("Mastering English requires both daily listening exposure and active speaking practice."),
            negativeExamples = listOf("Not only does passive reading build vocabulary, but active speaking also cements fluency."),
            questionExamples = listOf("How will you apply the English skills you mastered across all 21 boxes in your future career?"),
            assignmentTask = "Complete the 3 advanced dictation passages and deliver your 3 capstone spoken responses aloud.",
            vocabPractice = emptyList(),
            grammarPractice = emptyList(),
            listeningExercises = listOf(
                ListeningDictationExercise(
                    id = "ls6_dict_1",
                    title = "Lecture 1 • The Science of Language Mastery",
                    audioTranscript = "Consistent daily practice is the most reliable path to fluency",
                    hintText = "Listen for the superlative adjective ('the most reliable') and the final noun.",
                    keyWordsToExtract = listOf(
                        "consistent" to "مستمر / منتظم",
                        "reliable" to "موثوق"
                    )
                ),
                ListeningDictationExercise(
                    id = "ls6_dict_2",
                    title = "Lecture 2 • Global Career Communication",
                    audioTranscript = "Articulate speakers express complex ideas with clarity and confidence",
                    hintText = "Listen for the opening adjective meaning 'fluent and clear' and the final two nouns.",
                    keyWordsToExtract = listOf(
                        "articulate" to "فصيح / بليغ",
                        "clarity" to "وضوح"
                    )
                ),
                ListeningDictationExercise(
                    id = "ls6_dict_3",
                    title = "Lecture 3 • Capstone Graduation Message",
                    audioTranscript = "Completing all twenty one boxes is a remarkable academic achievement",
                    hintText = "Listen for the number of boxes and the final two-word phrase.",
                    keyWordsToExtract = listOf(
                        "remarkable" to "رائع / استثنائي",
                        "academic achievement" to "إنجاز أكاديمي"
                    )
                )
            ),
            speakingPrompts = listOf(
                SpeakingVoicePrompt(
                    id = "ls6_spk_1",
                    questionAudioText = "How will mastering English help you achieve your academic and career goals in the future?",
                    arabicPromptHint = "أجب بصوتك: كيف سيساعدك إتقان الإنجليزية في تحقيق أهدافك الأكاديمية والمهنية مستقبلاً؟",
                    sampleTargetAnswer = "Mastering English will help me achieve my academic and career goals by communicating with confidence",
                    requiredKeywords = listOf("English", "goals", "confidence"),
                    grammarFocusLabel = "Future Career Vision"
                ),
                SpeakingVoicePrompt(
                    id = "ls6_spk_2",
                    questionAudioText = "What advice would you give to a new student starting at Box 1 Essence?",
                    arabicPromptHint = "أجب بصوتك: ما النصيحة التي تقدمها لطالب جديد يبدأ من المستوى الأول Essence؟",
                    sampleTargetAnswer = "I would advise a new student to practice vocabulary and speaking every single day",
                    requiredKeywords = listOf("practice", "vocabulary", "day"),
                    grammarFocusLabel = "Giving Structured Advice"
                ),
                SpeakingVoicePrompt(
                    id = "ls6_spk_3",
                    questionAudioText = "Summarize what you enjoyed most about learning across the twenty one boxes.",
                    arabicPromptHint = "أجب بصوتك: لخّص أكثر ما استمتعت به خلال رحلة التعلم عبر المستويات الـ 21.",
                    sampleTargetAnswer = "I enjoyed earning stars in the vocabulary grammar listening and speaking rooms",
                    requiredKeywords = listOf("enjoyed", "speaking"),
                    grammarFocusLabel = "Capstone Reflection"
                )
            )
        )
    )

    val allLevels: List<CurriculumLevel> = seeds.map { seed ->
        val materials = buildList {
            add(
                StudyMaterial(
                    id = "${seed.id}_vocab",
                    title = "${seed.title} • Vocabulary Bank",
                    category = MaterialCategory.VOCABULARY,
                    summary = seed.topicFocus,
                    contentBody = "Core topic focus for Box ${seed.order} (${seed.title}): ${seed.topicFocus}\nPractice pronouncing each phrase aloud and review your in-app Vocabulary Bank.",
                    keyPhrases = seed.vocab.map { it.word to "${it.meaning} (${it.arabicHint})" },
                    xpReward = 30
                )
            )
            add(
                StudyMaterial(
                    id = "${seed.id}_grammar",
                    title = "${seed.title} • Grammar Explanation",
                    category = MaterialCategory.GRAMMAR,
                    summary = "Key Grammar Rule & Examples",
                    contentBody = "${seed.grammarRule}\n\nFormula: ${seed.grammarFormula}",
                    keyPhrases = seed.affirmativeExamples.map { it to "Affirmative Example" } +
                        seed.nominalVsVerbalNotes,
                    xpReward = 35
                )
            )
        }

        val assignments = listOf(
            LevelAssignment(
                id = "${seed.id}_assign_1",
                title = "${seed.title} Practical Task",
                typeLabel = seed.boxType.title,
                instructions = seed.assignmentTask,
                rubricCriteria = "Earn at least 2/3 Stars (⭐⭐) across this box's practice rooms to unlock Box ${seed.order + 1}.",
                maxPoints = 100,
                xpReward = 50
            )
        )

        val combinedQuiz = (seed.vocabPractice + seed.grammarPractice).ifEmpty {
            listOf(
                SkillQuizQuestion(
                    id = "${seed.id}_q1",
                    prompt = "Which skill is the primary focus of ${seed.title}?",
                    options = listOf("Interactive Listening & Speaking", "Cooking", "Driving", "Painting"),
                    correctIndex = 0,
                    explanation = "${seed.title} focuses on active communication and accuracy."
                )
            )
        }

        CurriculumLevel(
            id = seed.id,
            orderIndex = seed.order,
            track = seed.track,
            stageGroup = seed.stageGroup,
            title = seed.title,
            hours = seed.hours,
            teacherUnlockCode = seed.code,
            packSizeMb = seed.sizeMb,
            badgeName = seed.badgeName,
            badgeEmoji = seed.emoji,
            badgeDescription = seed.badgeDesc,
            focusSkills = seed.skills,
            materials = materials,
            assignments = assignments,
            quizQuestions = combinedQuiz,
            boxType = seed.boxType,
            vocabBankItems = seed.vocab,
            grammarContent = GrammarRoomContent(
                ruleTitle = "${seed.title} Grammar Rule",
                formulaPattern = seed.grammarFormula,
                explanationBody = seed.grammarRule,
                affirmativeExamples = seed.affirmativeExamples,
                negativeExamples = seed.negativeExamples,
                questionExamples = seed.questionExamples,
                nominalVsVerbalNotes = seed.nominalVsVerbalNotes
            ),
            vocabPracticeQuestions = seed.vocabPractice,
            grammarPracticeQuestions = seed.grammarPractice,
            rwSentenceExercises = seed.rwExercises,
            listeningExercises = seed.listeningExercises,
            speakingPrompts = seed.speakingPrompts
        )
    }
}
