package com.example.data

object CurriculumCatalog {

    private data class LevelSeed(
        val id: String,
        val order: Int,
        val track: DiplomaTrack,
        val stageGroup: String,
        val title: String,
        val hours: Int,
        val code: String,
        val sizeMb: Float,
        val badgeName: String,
        val emoji: String,
        val badgeDesc: String,
        val skills: List<String>,
        val topicFocus: String,
        val vocab: List<Pair<String, String>>,
        val grammarRule: String,
        val assignmentTask: String,
        val quiz: List<SkillQuizQuestion>
    )

    private val seeds = listOf(
        // === INTERMEDIATE DIPLOMA: STAGE 1 (Levels 1-6) ===
        LevelSeed(
            id = "lvl_01_essence",
            order = 1,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 1 • Foundation Block",
            title = "Essence",
            hours = 45,
            code = "ESS-101",
            sizeMb = 11.4f,
            badgeName = "Alphabet & Phonics Spark",
            emoji = "🌟",
            badgeDesc = "Mastered the core sounds, greetings, numbers, and everyday survival expressions in Essence (45 Hrs).",
            skills = listOf("Phonics", "Greetings", "Core Vocabulary", "Listening"),
            topicFocus = "Alphabet phonics, self-introductions, numbers 1–100, and classroom phrases.",
            vocab = listOf(
                "Good morning! How are you?" to "A friendly greeting used before noon.",
                "Nice to meet you" to "Said when meeting someone for the first time.",
                "Could you repeat that, please?" to "Polite classroom phrase to ask for help.",
                "Spell your name" to "Say each letter of your name in order."
            ),
            grammarRule = "Subject Pronouns (I, You, We, They, He, She, It) + Verb 'To Be' (am / is / are). Example: 'I am a student. She is my teacher.'",
            assignmentTask = "Introduce yourself to 3 classmates in English: share your name, age, favorite color, and spell your last name aloud.",
            quiz = listOf(
                SkillQuizQuestion("q1", "Choose the correct verb: 'She ___ my English teacher.'", listOf("am", "is", "are", "be"), 1, "'She' takes the singular verb 'is'."),
                SkillQuizQuestion("q2", "What do you say when you meet a new classmate?", listOf("Goodbye forever", "Nice to meet you!", "See you yesterday", "Sleep well"), 1, "'Nice to meet you!' is the standard friendly greeting."),
                SkillQuizQuestion("q3", "Which word starts with a vowel sound?", listOf("Book", "Apple", "Pencil", "Chair"), 1, "'Apple' starts with the vowel 'A'.")
            )
        ),
        LevelSeed(
            id = "lvl_02_beginner_a",
            order = 2,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 1 • Foundation Block",
            title = "Beginner A",
            hours = 45,
            code = "BEGA-102",
            sizeMb = 12.8f,
            badgeName = "Beginner A Explorer",
            emoji = "🧭",
            badgeDesc = "Completed 45 hours of Beginner A: daily routines, family trees, and present simple sentences.",
            skills = listOf("Present Simple", "Daily Routines", "Family", "Reading"),
            topicFocus = "Talking about family members, daily schedules, telling time, and favorite hobbies.",
            vocab = listOf(
                "Wake up early" to "Stop sleeping in the morning.",
                "Brush my teeth" to "Clean teeth with a toothbrush.",
                "Grandparents" to "The parents of your father or mother.",
                "Every weekend" to "Happening on Saturdays and Sundays."
            ),
            grammarRule = "Present Simple for habits: Add '-s' or '-es' for He/She/It. Example: 'I play soccer, but he plays basketball.'",
            assignmentTask = "Draw and label your Morning Routine timeline with 5 sentences using time expressions (at 7:00 AM, in the afternoon).",
            quiz = listOf(
                SkillQuizQuestion("q1", "Complete the sentence: 'My brother ___ breakfast at 7:30 AM.'", listOf("eat", "eats", "eating", "to eat"), 1, "For 'he/she/it' (my brother), add -s to the verb."),
                SkillQuizQuestion("q2", "Which preposition goes with clock times? 'Class starts ___ 9 o'clock.'", listOf("on", "in", "at", "by"), 2, "Use 'at' for specific clock times."),
                SkillQuizQuestion("q3", "Who is your mother's brother?", listOf("Cousin", "Uncle", "Nephew", "Grandfather"), 1, "Your mother's or father's brother is your uncle.")
            )
        ),
        LevelSeed(
            id = "lvl_03_beginner_b",
            order = 3,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 1 • Foundation Block",
            title = "Beginner B",
            hours = 45,
            code = "BEGB-103",
            sizeMb = 13.5f,
            badgeName = "Town & Market Navigator",
            emoji = "🏘️",
            badgeDesc = "Conquered Beginner B (45 Hrs): shopping, food countable/uncountable nouns, and asking for directions.",
            skills = listOf("Directions", "Countable Nouns", "Shopping", "Speaking"),
            topicFocus = "Places around town, ordering food, countable & uncountable nouns, and Present Continuous.",
            vocab = listOf(
                "Turn left at the corner" to "Change direction to the left side of the street.",
                "Across from the library" to "On the opposite side of the road from the library.",
                "A loaf of bread" to "A measured unit for uncountable bread.",
                "How much does this cost?" to "Asking for the price of an item."
            ),
            grammarRule = "Use 'There is / some / much' for uncountable nouns (water, milk) and 'There are / many / a few' for countable plurals (apples, books).",
            assignmentTask = "Roleplay a Market Buyer & Seller dialogue with a partner using a 5-item grocery list and present prices to the teacher.",
            quiz = listOf(
                SkillQuizQuestion("q1", "Which question is correct for uncountable water?", listOf("How many water is there?", "How much water is in the bottle?", "Are there a water?", "How few water?"), 1, "Water is uncountable, so we use 'How much'."),
                SkillQuizQuestion("q2", "Look! The children ___ in the playground right now.", listOf("runs", "are running", "ran", "is running"), 1, "'Right now' signals Present Continuous ('are running')."),
                SkillQuizQuestion("q3", "Where do you go to borrow books?", listOf("Bakery", "Pharmacy", "Library", "Station"), 2, "You borrow books from a library.")
            )
        ),
        LevelSeed(
            id = "lvl_04_skills_rw_1",
            order = 4,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 1 • Foundation Block",
            title = "Skills (R&W)",
            hours = 45,
            code = "SRW1-104",
            sizeMb = 14.2f,
            badgeName = "Story Reader & Scribe I",
            emoji = "📖",
            badgeDesc = "Finished Skills (Reading & Writing) 45 Hrs: paragraph structure, scanning stories, and punctuation mastery.",
            skills = listOf("Reading Comprehension", "Paragraph Writing", "Punctuation", "Main Ideas"),
            topicFocus = "Finding main ideas in short stories, topic sentences, linking words (and, but, because, so), and writing clear paragraphs.",
            vocab = listOf(
                "Topic Sentence" to "The first sentence that tells the main idea of a paragraph.",
                "Supporting Details" to "Facts or examples that explain the main idea.",
                "Skimming" to "Reading quickly to understand the general topic.",
                "Scanning" to "Looking through text quickly to find a specific name or number."
            ),
            grammarRule = "Compound Sentences: Connect two ideas with 'and' (addition), 'but' (contrast), 'because' (reason), or 'so' (result).",
            assignmentTask = "Read the short story 'The Lost Kite' and write a 6-sentence paragraph about your favorite childhood toy with a clear topic sentence.",
            quiz = listOf(
                SkillQuizQuestion("q1", "Choose the best connector: 'I wanted to play outside, ___ it started to rain.'", listOf("because", "but", "so", "or"), 1, "'But' shows contrast between wanting to play and rain."),
                SkillQuizQuestion("q2", "What is 'scanning' when reading a text?", listOf("Reading every word aloud", "Looking quickly for a specific piece of info like a date", "Closing the book", "Writing a summary"), 1, "Scanning means searching rapidly for specific details."),
                SkillQuizQuestion("q3", "Which part of a paragraph introduces the main idea?", listOf("Title page", "Topic sentence", "Footnote", "Bibliography"), 1, "The topic sentence introduces the main idea.")
            )
        ),
        LevelSeed(
            id = "lvl_05_elementary_a",
            order = 5,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 1 • Foundation Block",
            title = "Elementary A",
            hours = 45,
            code = "ELEA-105",
            sizeMb = 14.9f,
            badgeName = "Time Traveler: Past Simple",
            emoji = "⏳",
            badgeDesc = "Mastered Elementary A (45 Hrs): regular and irregular Past Simple verbs, biographies, and memorable stories.",
            skills = listOf("Past Simple", "Irregular Verbs", "Biographies", "Listening"),
            topicFocus = "Talking about last weekend, historical inventors, regular (-ed) and irregular past verbs, and time markers (yesterday, ago).",
            vocab = listOf(
                "Discovered" to "Found something for the first time.",
                "Two days ago" to "48 hours before today in the past.",
                "Memorable trip" to "A journey worth remembering for a long time.",
                "Checklist" to "A list of tasks to complete."
            ),
            grammarRule = "Past Simple Questions & Negatives: Use 'did / didn't + base verb'. Example: 'Did you go to the museum? Yes, I went there yesterday.'",
            assignmentTask = "Interview a family member or classmate about their best holiday and write 8 Past Simple sentences using at least 4 irregular verbs.",
            quiz = listOf(
                SkillQuizQuestion("q1", "What is the Past Simple form of 'buy'?", listOf("buyed", "bought", "brought", "buying"), 1, "'Bought' is the irregular past form of 'buy'."),
                SkillQuizQuestion("q2", "Spot the correct question:", listOf("Did she went home?", "Did she go home?", "Went she home?", "Does she went home?"), 1, "After 'Did', always use the base form of the verb ('go')."),
                SkillQuizQuestion("q3", "We ___ a great science documentary last night.", listOf("watch", "watched", "watching", "watches"), 1, "'Last night' requires Past Simple 'watched'.")
            )
        ),
        LevelSeed(
            id = "lvl_06_elementary_b",
            order = 6,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 1 • Foundation Block",
            title = "Elementary B",
            hours = 45,
            code = "ELEB-106",
            sizeMb = 15.3f,
            badgeName = "Comparison & Future Planner",
            emoji = "🚀",
            badgeDesc = "Completed Elementary B (45 Hrs): comparatives, superlatives, and future plans with 'going to' & 'will'.",
            skills = listOf("Comparatives", "Superlatives", "Future Plans", "Grammar"),
            topicFocus = "Comparing cities and animals, superlatives (the fastest, the most exciting), and future ambitions using 'be going to'.",
            vocab = listOf(
                "More comfortable than" to "Comparative form for long adjectives.",
                "The tallest building" to "Superlative form comparing 3 or more items.",
                "Ambitious goal" to "A big plan that takes effort to achieve.",
                "Weather forecast" to "Prediction of tomorrow's weather."
            ),
            grammarRule = "Short adjectives add '-er / -est' (bigger, biggest). Long adjectives use 'more / the most' (more interesting, the most interesting).",
            assignmentTask = "Compare three animals or gadgets in a neat comparison chart and present 6 comparative/superlative sentences in class.",
            quiz = listOf(
                SkillQuizQuestion("q1", "The cheetah is ___ land animal in the world.", listOf("faster than", "the fastest", "more fast", "most fast"), 1, "When comparing one animal to all others in the world, use the superlative 'the fastest'."),
                SkillQuizQuestion("q2", "Look at those dark clouds! It ___ rain soon.", listOf("is going to", "was", "did", "going"), 0, "Use 'is going to' for predictions based on visible evidence."),
                SkillQuizQuestion("q3", "What is the comparative form of 'good'?", listOf("gooder", "more good", "better", "best"), 2, "'Better' is the irregular comparative of 'good'.")
            )
        ),

        // === INTERMEDIATE DIPLOMA: STAGE 2 (Levels 7-13) ===
        LevelSeed(
            id = "lvl_07_skill_ls_1",
            order = 7,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 2 • Intermediate Core",
            title = "Skill (L&S)",
            hours = 45,
            code = "SLS1-107",
            sizeMb = 18.0f,
            badgeName = "Clear Voice Communicator I",
            emoji = "🎙️",
            badgeDesc = "Mastered Skill (Listening & Speaking) 45 Hrs: pronunciation intonation, active listening, and confident dialogues.",
            skills = listOf("Pronunciation", "Active Listening", "Intonation", "Roleplay"),
            topicFocus = "Listening for tone and key details, question intonation, word stress, and holding 2-minute natural conversations.",
            vocab = listOf(
                "Rising intonation" to "Voice pitch goes up at the end of Yes/No questions.",
                "Follow-up question" to "A question that keeps a conversation going.",
                "Key takeaway" to "The most important point from an audio clip.",
                "Clarification" to "Making something easier to understand."
            ),
            grammarRule = "Conversation Starters & Echo Questions: Use 'Really?', 'Did you?', and 'What happened next?' to show active listening.",
            assignmentTask = "Perform a 2-minute live mini-interview with a classmate in front of the teacher using 4 follow-up questions.",
            quiz = listOf(
                SkillQuizQuestion("q1", "In English, voice pitch usually rises at the end of:", listOf("Normal statements", "Yes/No questions", "Commands", "Sad news"), 1, "Yes/No questions like 'Are you ready?' use rising intonation."),
                SkillQuizQuestion("q2", "Which phrase politely asks a speaker to explain more?", listOf("Stop talking", "What do you mean by that?", "Never mind", "I don't care"), 1, "'What do you mean by that?' politely asks for clarification."),
                SkillQuizQuestion("q3", "When listening to a train announcement, what should you listen for?", listOf("Platform number and departure time", "Every single adjective", "The speaker's favorite color", "Background music"), 0, "Selective listening focuses on key information like platform and time.")
            )
        ),
        LevelSeed(
            id = "lvl_08_pre_intermediate_a",
            order = 8,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 2 • Intermediate Core",
            title = "Pre-Intermediate A",
            hours = 45,
            code = "PREA-108",
            sizeMb = 15.8f,
            badgeName = "Present Perfect Pioneer",
            emoji = "🏅",
            badgeDesc = "Completed Pre-Intermediate A (45 Hrs): life experiences with Present Perfect, 'for' vs 'since', and Past Continuous.",
            skills = listOf("Present Perfect", "Past Continuous", "Life Experiences", "Storytelling"),
            topicFocus = "Sharing life experiences (Have you ever...?), contrasting Present Perfect with Past Simple, and interrupted past actions.",
            vocab = listOf(
                "Have you ever...?" to "Asking about an experience at any time in someone's life.",
                "Since last September" to "From a specific starting point in time until now.",
                "For three hours" to "A duration or period of time.",
                "While I was walking" to "Background action in Past Continuous."
            ),
            grammarRule = "Present Perfect (have/has + past participle) connects past to present: 'I have lived here for 5 years.' Use Past Simple for finished times: 'I moved here in 2021.'",
            assignmentTask = "Write a 'Two Truths and a Lie' experience card using 'I have...' and answer classmates' Past Simple detective questions.",
            quiz = listOf(
                SkillQuizQuestion("q1", "I have known my best friend ___ five years.", listOf("since", "for", "ago", "during"), 1, "Use 'for' with a duration of time ('five years')."),
                SkillQuizQuestion("q2", "While we ___ dinner, the phone rang.", listOf("eat", "were eating", "have eaten", "eats"), 1, "'While' introduces a longer background action in Past Continuous ('were eating')."),
                SkillQuizQuestion("q3", "Have you ___ ridden a camel in the desert?", listOf("never", "ever", "yet", "since"), 1, "In questions about life experience, use 'ever'.")
            )
        ),
        LevelSeed(
            id = "lvl_09_pre_intermediate_b",
            order = 9,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 2 • Intermediate Core",
            title = "Pre-Intermediate B",
            hours = 45,
            code = "PREB-109",
            sizeMb = 16.1f,
            badgeName = "Conditional & Modal Tactician",
            emoji = "⚡",
            badgeDesc = "Finished Pre-Intermediate B (45 Hrs): First Conditional, modal verbs of obligation, and passive voice basics.",
            skills = listOf("First Conditional", "Modal Verbs", "Advice & Rules", "Vocabulary"),
            topicFocus = "Giving advice (should/ought to), rules (must/have to), real future possibilities (First Conditional), and inventions.",
            vocab = listOf(
                "Mustn't" to "Something is forbidden or not allowed.",
                "Don't have to" to "Something is optional; not necessary.",
                "Consequence" to "The result of an action.",
                "Eco-friendly" to "Not harmful to the environment."
            ),
            grammarRule = "First Conditional: 'If + Present Simple, will + base verb'. Example: 'If we recycle more paper, we will save many trees.'",
            assignmentTask = "Design a 'Green Classroom Charter' poster with 5 modal rules and 3 First Conditional promises.",
            quiz = listOf(
                SkillQuizQuestion("q1", "If you study hard every day, you ___ pass the level exam!", listOf("would", "will", "did", "have"), 1, "First Conditional uses 'If + Present Simple, will + base verb'."),
                SkillQuizQuestion("q2", "Tomorrow is a holiday, so we ___ wake up early.", listOf("mustn't", "don't have to", "shouldn't to", "cannot"), 1, "'Don't have to' means there is no obligation."),
                SkillQuizQuestion("q3", "You look tired. You ___ get some rest tonight.", listOf("should", "would", "mustn't", "did"), 0, "'Should' is used to give friendly advice.")
            )
        ),
        LevelSeed(
            id = "lvl_10_skills_rw_2",
            order = 10,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 2 • Intermediate Core",
            title = "Skills (R&W) II",
            hours = 45,
            code = "SRW2-110",
            sizeMb = 16.7f,
            badgeName = "Essay Architect II",
            emoji = "✍️",
            badgeDesc = "Mastered Intermediate Skills (R&W) 45 Hrs: multi-paragraph essays, opinion writing, and inference reading.",
            skills = listOf("Opinion Essays", "Inference", "Transition Words", "Proofreading"),
            topicFocus = "Reading between the lines (making inferences), organizing a 3-paragraph opinion essay, and using transitions (However, Furthermore).",
            vocab = listOf(
                "Furthermore" to "In addition; used to add a strong supporting point.",
                "On the other hand" to "Used to introduce a contrasting view.",
                "In conclusion" to "Signals the final summary paragraph.",
                "Inference" to "A conclusion reached based on evidence and reasoning."
            ),
            grammarRule = "Paragraph Transitions: Always follow introductory transitions ('However,', 'Therefore,', 'For instance,') with a comma.",
            assignmentTask = "Write a structured 150-word opinion essay on 'Why Learning a Second Language Opens Doors' with Introduction, Body, and Conclusion.",
            quiz = listOf(
                SkillQuizQuestion("q1", "Which transition word adds another supporting reason?", listOf("However,", "Furthermore,", "Instead,", "Although"), 1, "'Furthermore,' adds an additional point to support your argument."),
                SkillQuizQuestion("q2", "If a story says 'Sara opened her umbrella and put on boots', what can you infer?", listOf("It is raining outside", "It is a hot summer day", "She is going swimming", "She lost her shoes"), 0, "Umbrella and boots provide clues that it is raining."),
                SkillQuizQuestion("q3", "Where does the thesis statement belong in a short essay?", listOf("In the introduction paragraph", "In the page margin", "Only in the title", "Nowhere"), 0, "The introduction presents the main thesis or opinion.")
            )
        ),
        LevelSeed(
            id = "lvl_11_intermediate_a",
            order = 11,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 2 • Intermediate Core",
            title = "Intermediate A",
            hours = 45,
            code = "INTA-111",
            sizeMb = 17.2f,
            badgeName = "Second Conditional Dreamer",
            emoji = "🌈",
            badgeDesc = "Completed Intermediate A (45 Hrs): Second Conditional hypotheticals, Passive Voice across tenses, and relative clauses.",
            skills = listOf("Second Conditional", "Passive Voice", "Relative Clauses", "Reading"),
            topicFocus = "Imaginary situations (If I were...), defining relative clauses (who, which, where), and how things are made (Passive Voice).",
            vocab = listOf(
                "Hypothetical" to "Imagined or suggested, but not necessarily real.",
                "Manufactured" to "Made on a large scale using machinery.",
                "Bilingual" to "Able to speak two languages fluently.",
                "Obstacle" to "Something that blocks your way or makes progress hard."
            ),
            grammarRule = "Second Conditional for unreal present/future: 'If + Past Simple, would + base verb'. Example: 'If I had wings, I would fly to school.'",
            assignmentTask = "Present a 5-slide or 5-card pitch: 'If I Were Principal for a Week' using Second Conditional and relative clauses.",
            quiz = listOf(
                SkillQuizQuestion("q1", "If I ___ you, I would practice speaking every day.", listOf("am", "were", "will be", "been"), 1, "In Second Conditional, 'If I were you' is the standard subjunctive form."),
                SkillQuizQuestion("q2", "The telephone ___ invented by Alexander Graham Bell in 1876.", listOf("is", "was", "has", "were"), 1, "Past Simple Passive uses 'was/were + past participle'."),
                SkillQuizQuestion("q3", "A chef is a person ___ prepares delicious meals.", listOf("which", "who", "where", "whose"), 1, "Use 'who' for people in defining relative clauses.")
            )
        ),
        LevelSeed(
            id = "lvl_12_intermediate_b",
            order = 12,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 2 • Intermediate Core",
            title = "Intermediate B",
            hours = 45,
            code = "INTB-112",
            sizeMb = 17.6f,
            badgeName = "Reported Speech Reporter",
            emoji = "📰",
            badgeDesc = "Conquered Intermediate B (45 Hrs): Past Perfect, Reported Speech, and phrasal verbs in action.",
            skills = listOf("Reported Speech", "Past Perfect", "Phrasal Verbs", "Journalism"),
            topicFocus = "Reporting what others said, sequencing past events with Past Perfect (had done), and everyday phrasal verbs.",
            vocab = listOf(
                "Look forward to" to "Feel excited about something that is going to happen.",
                "Figure out" to "Solve a problem or understand something.",
                "Give up" to "Stop trying to do something.",
                "Eyewitness" to "A person who saw an event happen with their own eyes."
            ),
            grammarRule = "Reported Speech shifts tense one step back: Direct: 'I am ready.' -> Reported: 'She said that she was ready.'",
            assignmentTask = "Act as a school news reporter: interview two students and write a news report using at least 5 Reported Speech sentences.",
            quiz = listOf(
                SkillQuizQuestion("q1", "Direct: 'I love English.' -> Reported: He said that he ___ English.", listOf("loves", "loved", "will love", "loving"), 1, "Present Simple 'love' shifts back to Past Simple 'loved'."),
                SkillQuizQuestion("q2", "When we arrived at the station, the train ___ already left.", listOf("has", "had", "was", "did"), 1, "Past Perfect ('had left') shows an action that happened before another past action."),
                SkillQuizQuestion("q3", "What does the phrasal verb 'figure out' mean?", listOf("Solve or understand", "Throw away", "Wake up late", "Walk outside"), 0, "'Figure out' means to find the answer or understand.")
            )
        ),
        LevelSeed(
            id = "lvl_13_skills_ls_2",
            order = 13,
            track = DiplomaTrack.INTERMEDIATE_DIPLOMA,
            stageGroup = "Stage 2 • Intermediate Core",
            title = "Skills (L&S) II",
            hours = 45,
            code = "SLS2-113",
            sizeMb = 19.4f,
            badgeName = "Debate & Presentation Star",
            emoji = "🎤",
            badgeDesc = "Graduated Intermediate Diploma with Skills (L&S) II (45 Hrs): structured debates, note-taking, and public speaking!",
            skills = listOf("Public Speaking", "Debating", "Note-Taking", "Fluency"),
            topicFocus = "Delivering 3-minute presentations, polite agreement & disagreement, and taking structured notes from lectures.",
            vocab = listOf(
                "I see your point, however..." to "Polite way to disagree in a class discussion.",
                "To sum up" to "Phrase used to summarize main points at the end of a talk.",
                "Eye contact" to "Looking directly at your audience while speaking.",
                "Signposting language" to "Phrases that guide listeners through a presentation."
            ),
            grammarRule = "Polite Hedging in Discussions: Use 'It seems to me that...', 'Could it be that...?', and 'While I agree up to a point...'",
            assignmentTask = "Deliver a 3-minute mini-talk on a topic of your choice using visual cues and answer 2 audience questions.",
            quiz = listOf(
                SkillQuizQuestion("q1", "Which phrase is the most polite way to disagree in a classroom debate?", listOf("You are totally wrong!", "I see your point, but have we considered...?", "That makes zero sense.", "Stop speaking."), 1, "Acknowledging the other person's point before sharing a counterpoint is respectful and persuasive."),
                SkillQuizQuestion("q2", "What is 'signposting' in a presentation?", listOf("Using phrases like 'First, let's look at...' to guide the audience", "Holding a traffic sign", "Whispering quietly", "Reading without looking up"), 0, "Signposting helps listeners follow the structure of your talk."),
                SkillQuizQuestion("q3", "When taking notes during a listening task, you should write:", listOf("Key words, symbols, and main ideas", "Every single word slowly", "Nothing at all", "Only punctuation marks"), 0, "Effective note-taking uses keywords and abbreviations.")
            )
        ),

        // === ADVANCED DIPLOMA: STAGE 1 (Levels 14-17) ===
        LevelSeed(
            id = "lvl_14_upper_int_a",
            order = 14,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Upper-Intermediate Block",
            title = "Upper-Intermediate A",
            hours = 45,
            code = "UPIA-201",
            sizeMb = 18.2f,
            badgeName = "Narrative Tense Virtuoso",
            emoji = "🏛️",
            badgeDesc = "Completed Upper-Intermediate A (45 Hrs): narrative tenses, Present Perfect Continuous, and nuanced collocations.",
            skills = listOf("Present Perfect Continuous", "Narrative Tenses", "Collocations", "Reading"),
            topicFocus = "Combining Past Simple, Continuous, and Perfect Continuous in rich storytelling; strong collocations.",
            vocab = listOf(
                "Make a breakthrough" to "Achieve an important discovery or success.",
                "Heavy traffic / Heavy rain" to "Natural English collocation using 'heavy'.",
                "Exhausted" to "Extreme adjective meaning 'very tired'.",
                "Furious" to "Extreme adjective meaning 'very angry'."
            ),
            grammarRule = "Present Perfect Continuous ('have/has been + -ing') emphasizes the ongoing duration of an activity: 'She has been practicing piano all morning.'",
            assignmentTask = "Write a dramatic 200-word mystery or adventure story incorporating all four narrative tenses and 5 extreme adjectives.",
            quiz = listOf(
                SkillQuizQuestion("q1", "Which modifier is used with extreme adjectives like 'exhausted' or 'freezing'?", listOf("very", "absolutely", "a bit", "fairly"), 1, "Extreme adjectives pair with 'absolutely' or 'completely', not 'very'."),
                SkillQuizQuestion("q2", "We ___ waiting for the bus for twenty minutes, and it just arrived!", listOf("have been", "are", "did", "had"), 0, "'Have been waiting' highlights the duration up to the present moment."),
                SkillQuizQuestion("q3", "Which word naturally collocates with 'a mistake'?", listOf("do a mistake", "make a mistake", "take a mistake", "have a mistake"), 1, "In English, we say 'make a mistake'.")
            )
        ),
        LevelSeed(
            id = "lvl_15_upper_int_b",
            order = 15,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Upper-Intermediate Block",
            title = "Upper-Intermediate B",
            hours = 45,
            code = "UPIB-202",
            sizeMb = 18.8f,
            badgeName = "Third Conditional Strategist",
            emoji = "🔮",
            badgeDesc = "Mastered Upper-Intermediate B (45 Hrs): Third Conditional, modals of deduction (must have / might have), and Future Continuous.",
            skills = listOf("Third Conditional", "Past Modals", "Future Continuous", "Critical Thinking"),
            topicFocus = "Reflecting on past outcomes (Third Conditional), detective deductions (must have / can't have), and Future Continuous/Perfect.",
            vocab = listOf(
                "In hindsight" to "Understanding a situation only after it has happened.",
                "Jump to conclusions" to "Make a hasty judgment before having all the facts.",
                "By this time next year" to "Time marker for Future Continuous / Future Perfect.",
                "Inevitable" to "Certain to happen; unavoidable."
            ),
            grammarRule = "Third Conditional (unreal past): 'If + Past Perfect, would have + past participle'. Example: 'If we had left earlier, we would have caught the flight.'",
            assignmentTask = "Solve the 'Classroom Mystery Case' worksheet by writing 6 deductions using 'must have', 'might have', and 'can't have'.",
            quiz = listOf(
                SkillQuizQuestion("q1", "If she had set her alarm, she ___ woken up on time.", listOf("will have", "would have", "had", "would"), 1, "Third Conditional uses 'would have + past participle' in the result clause."),
                SkillQuizQuestion("q2", "The streets are completely wet. It ___ rained while we were inside.", listOf("must have", "can't have", "should", "will"), 0, "'Must have + past participle' expresses a strong logical deduction about the past."),
                SkillQuizQuestion("q3", "By 2030, our institute ___ graduated thousands of bilingual students.", listOf("will have", "is", "was", "had"), 0, "'By + future year' pairs with Future Perfect ('will have + past participle').")
            )
        ),
        LevelSeed(
            id = "lvl_16_upper_int_c",
            order = 16,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Upper-Intermediate Block",
            title = "Upper-Intermediate C",
            hours = 45,
            code = "UPIC-203",
            sizeMb = 19.5f,
            badgeName = "Rhetoric & Inversion Knight",
            emoji = "🛡️",
            badgeDesc = "Conquered Upper-Intermediate C (45 Hrs): causative verbs, cleft sentences for emphasis, and advanced idioms.",
            skills = listOf("Causative Verbs", "Cleft Sentences", "Idioms", "Synthesizing"),
            topicFocus = "Causative structures (have/get something done), adding emphasis (What I love most is...), and idiomatic fluency.",
            vocab = listOf(
                "Hit the nail on the head" to "Describe exactly what is causing a situation or problem.",
                "A blessing in disguise" to "Something that seems bad at first, but results in something good.",
                "Once in a blue moon" to "Happening very rarely.",
                "State-of-the-art" to "Using the newest and most advanced ideas and features."
            ),
            grammarRule = "Causative 'Have/Get + Object + Past Participle': Used when someone else performs a service for you: 'I had my laptop repaired yesterday.'",
            assignmentTask = "Rewrite 8 standard sentences into emphatic Cleft Sentences ('What really inspires me is...', 'It was in 2024 that...') and use 4 idioms in a dialogue.",
            quiz = listOf(
                SkillQuizQuestion("q1", "Choose the correct causative form: 'We ___ our classroom painted last week.'", listOf("had", "made", "did", "were"), 0, "'Had + object + past participle' ('had our classroom painted') is the causative structure."),
                SkillQuizQuestion("q2", "What does the idiom 'hit the nail on the head' mean?", listOf("Break a tool", "Be exactly right about something", "Build a house", "Make a loud noise"), 1, "It means to state the exact truth or point."),
                SkillQuizQuestion("q3", "'___ I enjoy most about English class is the group debates.'", listOf("Which", "What", "Who", "Where"), 1, "Wh-cleft sentences begin with 'What...' to create emphasis.")
            )
        ),
        LevelSeed(
            id = "lvl_17_skills_wr",
            order = 17,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Upper-Intermediate Block",
            title = "Skills (Wr)",
            hours = 45,
            code = "SKWR-204",
            sizeMb = 17.9f,
            badgeName = "Master Quill Author",
            emoji = "🪶",
            badgeDesc = "Completed Upper-Intermediate Skills (Wr) 45 Hrs: persuasive essays, formal reports, and academic paraphrasing.",
            skills = listOf("Academic Writing", "Paraphrasing", "Formal Reports", "Cohesion"),
            topicFocus = "Writing formal reports, persuasive argument essays, counter-arguments, and paraphrasing without plagiarism.",
            vocab = listOf(
                "Counter-argument" to "An opposing viewpoint that you address and refute in an essay.",
                "Cohesive devices" to "Linking words and pronouns that hold a text together smoothly.",
                "Paraphrase" to "Express the meaning of a writer using different words and structures.",
                "Objective tone" to "Writing based on facts rather than personal feelings."
            ),
            grammarRule = "Concession Clauses: Use 'Although / Even though + clause' or 'Despite / In spite of + noun/-ing' to acknowledge opposing views gracefully.",
            assignmentTask = "Submit a 250-word Formal Proposal Report recommending a new student club at the institute, complete with headings and recommendations.",
            quiz = listOf(
                SkillQuizQuestion("q1", "___ the heavy rain, all students arrived on time for the workshop.", listOf("Although", "Despite", "Even though", "Because"), 1, "'Despite' is followed by a noun phrase ('the heavy rain'), whereas 'Although' requires a subject and verb."),
                SkillQuizQuestion("q2", "Which greeting is most appropriate for a formal report or letter to an unknown recipient?", listOf("Hey buddy,", "Dear Sir or Madam,", "Yo!", "What's up,"), 1, "'Dear Sir or Madam,' is the standard formal salutation."),
                SkillQuizQuestion("q3", "Effective paraphrasing requires:", listOf("Changing both vocabulary and sentence structure while keeping the original meaning", "Copying every word without quotation marks", "Deleting half the meaning", "Changing only one comma"), 0, "Good paraphrasing rewords and restructures the sentence while preserving meaning.")
            )
        ),

        // === ADVANCED DIPLOMA: STAGE 2 (Levels 18-21) ===
        LevelSeed(
            id = "lvl_18_advanced_a",
            order = 18,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Advanced Mastery Block",
            title = "Advanced A",
            hours = 45,
            code = "ADVA-205",
            sizeMb = 20.1f,
            badgeName = "Inversion & Nuance Scholar",
            emoji = "🎓",
            badgeDesc = "Mastered Advanced A (45 Hrs): negative inversion, mixed conditionals, and C1 academic reading.",
            skills = listOf("Negative Inversion", "Mixed Conditionals", "C1 Vocabulary", "Critical Reading"),
            topicFocus = "Stylistic negative inversion (Never have I seen..., Seldom does...), mixed conditionals, and analyzing complex articles.",
            vocab = listOf(
                "Unprecedented" to "Never done or known before.",
                "Ubiquitous" to "Present, appearing, or found everywhere.",
                "Meticulous" to "Showing great attention to detail; very careful and precise.",
                "Pragmatic" to "Dealing with things sensibly and realistically."
            ),
            grammarRule = "Negative Inversion: When a sentence starts with a limiting adverb ('Never', 'Seldom', 'Not only'), invert the auxiliary and subject: 'Not only did she pass, but she also earned top marks.'",
            assignmentTask = "Write an analytical book or documentary review using at least 3 Negative Inversions and 2 Mixed Conditional sentences.",
            quiz = listOf(
                SkillQuizQuestion("q1", "Not only ___ the project early, but they also helped the other team.", listOf("they finished", "did they finish", "finished they", "they did finish"), 1, "After 'Not only' at the start of a sentence, invert the auxiliary verb and subject ('did they finish')."),
                SkillQuizQuestion("q2", "What does 'ubiquitous' mean?", listOf("Extremely rare", "Found everywhere", "Very quiet", "Ancient"), 1, "'Ubiquitous' means appearing or found everywhere."),
                SkillQuizQuestion("q3", "If I had learned Spanish as a child, I ___ fluent now.", listOf("would be", "would have been", "will be", "am"), 0, "Mixed conditional (past condition -> present result) uses 'If + Past Perfect, would + base verb' now.")
            )
        ),
        LevelSeed(
            id = "lvl_19_advanced_b",
            order = 19,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Advanced Mastery Block",
            title = "Advanced B",
            hours = 45,
            code = "ADVB-206",
            sizeMb = 20.8f,
            badgeName = "Subjunctive & Discourse Sage",
            emoji = "💎",
            badgeDesc = "Completed Advanced B (45 Hrs): the formal Subjunctive mood, participle clauses, and register shifting.",
            skills = listOf("Subjunctive Mood", "Participle Clauses", "Register Shifting", "Synthesis"),
            topicFocus = "Using participle clauses for conciseness, formal subjunctive (It is vital that he attend), and shifting between formal/informal registers.",
            vocab = listOf(
                "Imperative" to "Of vital importance; crucial.",
                "Albeit" to "Although; even though.",
                "Nuance" to "A subtle difference in meaning, expression, or sound.",
                "Synthesize" to "Combine ideas from multiple sources into a coherent whole."
            ),
            grammarRule = "Participle Clauses replace full adverbial clauses: 'Having finished her homework, Maya joined the game' (= After she had finished...).",
            assignmentTask = "Transform an informal 150-word blog post into a high-register academic briefing using Participle Clauses and the Subjunctive.",
            quiz = listOf(
                SkillQuizQuestion("q1", "It is essential that every candidate ___ on time for the examination.", listOf("arrives", "arrive", "arrived", "arriving"), 1, "The formal mandative subjunctive uses the bare infinitive ('arrive') after 'It is essential that...'."),
                SkillQuizQuestion("q2", "___ by the results, the scientists repeated the experiment.", listOf("Surprising", "Surprised", "Having surprise", "To surprise"), 1, "Use a past participle ('Surprised') when the subject experienced the feeling passively."),
                SkillQuizQuestion("q3", "Which word is a formal synonym for 'although'?", listOf("Albeit", "Plus", "Anyway", "Besides"), 0, "'Albeit' is a concise formal conjunction meaning 'although'.")
            )
        ),
        LevelSeed(
            id = "lvl_20_advanced_c",
            order = 20,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Advanced Mastery Block",
            title = "Advanced C",
            hours = 45,
            code = "ADVC-207",
            sizeMb = 21.4f,
            badgeName = "C1-C2 Fluency Sovereign",
            emoji = "👑",
            badgeDesc = "Conquered Advanced C (45 Hrs): nominalization, abstract argumentation, and idiomatic precision.",
            skills = listOf("Nominalization", "Abstract Debate", "Academic Style", "Precision"),
            topicFocus = "Turning verb-heavy sentences into elegant noun phrases (nominalization), hedging claims, and critical analysis.",
            vocab = listOf(
                "Proliferation" to "Rapid increase in the number or amount of something.",
                "Corroborate" to "Confirm or give support to a statement or finding.",
                "Paradigm shift" to "A fundamental change in approach or underlying assumptions.",
                "Equivocal" to "Open to more than one interpretation; ambiguous."
            ),
            grammarRule = "Academic Nominalization: Instead of 'Because cities grew rapidly, pollution increased', write 'The rapid growth of cities led to an increase in pollution.'",
            assignmentTask = "Present and defend a 300-word critical synthesis paper in class using academic nominalization and evidence-based hedging.",
            quiz = listOf(
                SkillQuizQuestion("q1", "Which sentence uses academic nominalization most effectively?", listOf("The implementation of the new policy improved efficiency.", "They put the new policy in and things got better.", "People worked faster because of rules.", "The rules made it good."), 0, "'The implementation of...' turns a verb into a formal academic noun phrase."),
                SkillQuizQuestion("q2", "What does 'corroborate' mean in academic research?", listOf("Contradict completely", "Confirm or support with evidence", "Forget about", "Guess randomly"), 1, "To corroborate is to confirm or give support to a claim."),
                SkillQuizQuestion("q3", "Which phrase best hedges a scientific claim?", listOf("The data suggests a strong correlation...", "This proves 100% forever...", "Everyone knows...", "Obviously it is..."), 0, "'The data suggests...' is precise and academically cautious.")
            )
        ),
        LevelSeed(
            id = "lvl_21_skills_ls_3",
            order = 21,
            track = DiplomaTrack.ADVANCED_DIPLOMA,
            stageGroup = "Advanced Mastery Block",
            title = "Skills (L&S) III",
            hours = 45,
            code = "SLS3-208",
            sizeMb = 22.0f,
            badgeName = "Keynote Orator Laureate",
            emoji = "🏆",
            badgeDesc = "Graduated the Advanced Diploma with Skills (L&S) III (45 Hrs): TED-style keynote speaking, panel moderation, and seminar listening.",
            skills = listOf("Keynote Speaking", "Seminar Moderation", "Rhetorical Devices", "Critical Listening"),
            topicFocus = "Rhetorical triads, persuasive pacing, moderating a live panel discussion, and decoding fast-paced academic lectures.",
            vocab = listOf(
                "Rhetorical question" to "A question asked for dramatic effect rather than to get an answer.",
                "Rule of Three (Tricolon)" to "Grouping ideas in threes for rhythm and memorability.",
                "Devil's advocate" to "Someone who expresses a contentious opinion to provoke debate.",
                "Articulate" to "Having or showing the ability to speak fluently and coherently."
            ),
            grammarRule = "Rhetorical Parallelism: Match grammatical structures in triads: 'Tell me and I forget. Teach me and I remember. Involve me and I learn.'",
            assignmentTask = "Deliver a 5-minute Capstone Keynote Speech to your class and teacher, followed by a live Q&A defense.",
            quiz = listOf(
                SkillQuizQuestion("q1", "'Government of the people, by the people, for the people' is an example of:", listOf("Tricolon / Parallelism", "Passive voice", "Past Perfect", "Spelling error"), 0, "Repeating three parallel prepositional phrases is a classic tricolon."),
                SkillQuizQuestion("q2", "What does 'playing devil's advocate' mean in a seminar?", listOf("Testing an argument by taking the opposing side", "Ending the meeting early", "Agreeing with everyone silently", "Reading from a dictionary"), 0, "It means raising counterpoints to test the strength of an argument."),
                SkillQuizQuestion("q3", "During a Q&A session, what is a strong strategy before answering a complex question?", listOf("Briefly paraphrase the question to confirm understanding", "Ignore the question", "Walk off the stage", "Change the subject"), 0, "Paraphrasing confirms you understood the question and gives you a moment to structure your answer.")
            )
        ),

        // === TOEFL TEST PREPARATION (Level 22 - 100 Hrs) ===
        LevelSeed(
            id = "lvl_22_toefl_prep",
            order = 22,
            track = DiplomaTrack.TOEFL_PREP,
            stageGroup = "100-Hour Intensive Exam Track",
            title = "TOEFL Test Preparation",
            hours = 100,
            code = "TOEFL-301",
            sizeMb = 28.5f,
            badgeName = "TOEFL iBT Grandmaster (100h)",
            emoji = "🎯",
            badgeDesc = "Completed the intensive 100-Hour TOEFL Test Preparation track covering Reading, Listening, Integrated Speaking, and Academic Writing!",
            skills = listOf("TOEFL Reading", "TOEFL Listening", "Integrated Speaking", "Academic Discussion Writing"),
            topicFocus = "Mastering all 4 TOEFL iBT sections: academic reading passages, campus/lecture listening, 45–60s timed speaking responses, and Integrated + Academic Discussion writing.",
            vocab = listOf(
                "Factual & Negative Factual Questions" to "TOEFL Reading items asking what is true or NOT true according to the passage.",
                "Integrated Task" to "Combining reading notes and lecture audio into a single spoken or written response.",
                "Pacing Strategy" to "Managing strict exam timers across reading passages and speaking prep windows.",
                "Synthesize the Lecture" to "Explain how the professor's examples support or challenge the reading."
            ),
            grammarRule = "TOEFL Integrated Template: 'According to the reading passage, [Main Point]. In the lecture, the professor casts doubt on / supports this by explaining that...'",
            assignmentTask = "Complete a full timed in-class TOEFL Simulation (Reading, Listening, 4 Speaking Tasks, and 2 Writing Tasks) and review your rubric score with your instructor.",
            quiz = listOf(
                SkillQuizQuestion("q1", "In the TOEFL Integrated Writing task, your primary job is to:", listOf("Give only your personal childhood story", "Summarize how the lecture points relate to the reading passage", "Copy the reading word-for-word", "Write a poem"), 1, "Integrated Writing tests how accurately you connect the lecture's arguments to the reading passage."),
                SkillQuizQuestion("q2", "How much preparation time do you get for TOEFL Independent Speaking (Task 1)?", listOf("15 seconds prep, 45 seconds speaking", "10 minutes prep", "No timer at all", "2 hours"), 0, "Task 1 gives 15 seconds to prepare and 45 seconds to speak."),
                SkillQuizQuestion("q3", "When tackling a TOEFL Vocabulary-in-Context question, you should:", listOf("Check how the word functions in the surrounding sentence", "Pick the longest word", "Skip the sentence completely", "Choose an unrelated rhyme"), 0, "Context clues in the sentence ensure you pick the meaning used by the author.")
            )
        ),

        // === PROFESSIONAL TEACHING PROGRAM - PTP (Levels 23-25) ===
        LevelSeed(
            id = "lvl_23_tkt",
            order = 23,
            track = DiplomaTrack.PTP_PROGRAM,
            stageGroup = "Professional Teaching Program (PTP)",
            title = "TKT",
            hours = 20,
            code = "TKT-401",
            sizeMb = 14.6f,
            badgeName = "TKT Pedagogy Specialist",
            emoji = "📘",
            badgeDesc = "Completed TKT (Teaching Knowledge Test - 20 Hrs): language analysis, lesson planning frameworks, and learner styles.",
            skills = listOf("Lesson Planning", "Concept Checking (CCQs)", "Learner Needs", "Error Correction"),
            topicFocus = "Cambridge TKT Modules 1–3: describing language skills, PPP vs TBL lesson frameworks, and Concept Checking Questions (CCQs).",
            vocab = listOf(
                "CCQs (Concept Checking Questions)" to "Targeted questions teachers ask to verify students understand a grammar/vocab concept.",
                "ICQs (Instruction Checking Questions)" to "Short questions to confirm students know what to do in an activity.",
                "Scaffolding" to "Step-by-step support given to learners so they can achieve a task.",
                "PPP (Presentation, Practice, Production)" to "Classic three-stage lesson staging model."
            ),
            grammarRule = "Golden Rule of CCQs: Never use the target grammar/word inside the Concept Checking Question itself; break the meaning into simple facts.",
            assignmentTask = "Write 3 Concept Checking Questions (CCQs) and a 45-minute PPP Lesson Plan for any Beginner or Elementary grammar point.",
            quiz = listOf(
                SkillQuizQuestion("q1", "Why do teachers ask ICQs (Instruction Checking Questions) before starting group work?", listOf("To confirm students understand the activity instructions", "To test spelling", "To end the class early", "To grade final exams"), 0, "ICQs like 'Are you working alone or in pairs?' prevent confusion before an activity begins."),
                SkillQuizQuestion("q2", "In the PPP lesson framework, what does the third 'P' stand for?", listOf("Punishment", "Production (freer communicative use)", "Pronouns", "Punctuation"), 1, "Presentation -> Practice -> Production."),
                SkillQuizQuestion("q3", "What is 'scaffolding' in classroom teaching?", listOf("Providing structured support and models that are gradually removed as learners gain confidence", "Giving a test on day one without teaching", "Speaking for 60 minutes non-stop", "Cleaning the whiteboard"), 0, "Scaffolding supports students step-by-step toward independent mastery.")
            )
        ),
        LevelSeed(
            id = "lvl_24_tesol",
            order = 24,
            track = DiplomaTrack.PTP_PROGRAM,
            stageGroup = "Professional Teaching Program (PTP)",
            title = "TESOL",
            hours = 40,
            code = "TESOL-402",
            sizeMb = 21.0f,
            badgeName = "TESOL Methodology Master",
            emoji = "🌍",
            badgeDesc = "Completed TESOL (40 Hrs): Communicative Language Teaching (CLT), classroom management, and assessment design.",
            skills = listOf("Communicative Teaching", "TTT vs STT", "Classroom Management", "Rubric Design"),
            topicFocus = "Maximizing Student Talking Time (STT), designing communicative tasks, differentiated instruction, and formative vs summative assessment.",
            vocab = listOf(
                "STT vs TTT" to "Student Talking Time vs Teacher Talking Time — aim to maximize STT!",
                "Formative Assessment" to "Ongoing in-class checks that help students improve while learning.",
                "Summative Assessment" to "End-of-unit evaluation measuring overall achievement.",
                "Information Gap Activity" to "A task where partners have different pieces of info and must speak to complete it."
            ),
            grammarRule = "Task-Based Learning (TBL) Cycle: Pre-Task (introduce topic) -> Task Cycle (students collaborate & report) -> Language Focus (analyze & practice).",
            assignmentTask = "Design an original Information Gap speaking activity and a 4-criteria grading rubric for an Intermediate ESL class.",
            quiz = listOf(
                SkillQuizQuestion("q1", "In a communicative language classroom, teachers generally aim to:", listOf("Maximize Student Talking Time (STT) and keep Teacher Talking Time (TTT) purposeful", "Speak for 100% of the lesson", "Forbid pair work", "Only translate word lists silently"), 0, "Students learn to communicate by actively using the language (high STT)."),
                SkillQuizQuestion("q2", "What is an 'Information Gap' activity?", listOf("Student A has info Student B needs, forcing real communication", "A silent reading test", "A 10-minute break", "Copying from the board"), 0, "Information gaps create a genuine need for students to talk to each other."),
                SkillQuizQuestion("q3", "Ongoing quizzes and teacher feedback given during a unit are examples of:", listOf("Formative assessment", "Final graduation ceremony", "School holidays", "Attendance sheets"), 0, "Formative assessment monitors learning in progress.")
            )
        ),
        LevelSeed(
            id = "lvl_25_teaching_practice",
            order = 25,
            track = DiplomaTrack.PTP_PROGRAM,
            stageGroup = "Professional Teaching Program (PTP)",
            title = "Teaching Practice",
            hours = 40,
            code = "PRAC-403",
            sizeMb = 24.0f,
            badgeName = "Master Classroom Practitioner",
            emoji = "🌟",
            badgeDesc = "Completed 40 Hours of live Teaching Practice! Observed lessons, delivered micro-teaching demos, and graduated the full Institute Curriculum!",
            skills = listOf("Live Teaching", "Peer Observation", "Self-Reflection", "Boardwork & Pacing"),
            topicFocus = "Delivering observed lessons, managing real classroom dynamics, reflective teaching journals, and warmers/coolers.",
            vocab = listOf(
                "Micro-teaching" to "Practicing a short 15–20 minute lesson segment with peers and trainer feedback.",
                "Delayed Error Correction" to "Noting errors during fluency work and reviewing them on the board afterward.",
                "Rapport" to "A warm, trusting, and harmonious relationship between teacher and students.",
                "Reflective Action Plan" to "Evaluating what went well in a lesson and what to adjust next time."
            ),
            grammarRule = "Error Correction Timing: Correct immediately during accuracy drills (on-the-spot), but use delayed correction during fluency speaking tasks so you don't interrupt student flow.",
            assignmentTask = "Deliver two full 45-minute observed practicum lessons, complete 3 peer observation sheets, and present your Reflective Teaching Portfolio.",
            quiz = listOf(
                SkillQuizQuestion("q1", "When students are engaged in a lively fluency roleplay, when is the best time to correct minor grammar slips?", listOf("After the roleplay using delayed board correction", "Interrupt every 3 seconds loudly", "Never teach grammar again", "Cancel the activity"), 0, "Delayed correction preserves student confidence and communicative flow during fluency tasks."),
                SkillQuizQuestion("q2", "What is the main goal of a 5-minute 'Warmer' at the start of class?", listOf("Engage students, activate prior knowledge, and switch their minds into English", "Give a surprise final exam", "Take a nap", "Assign homework for next month"), 0, "Warmers build energy and activate English thinking right away."),
                SkillQuizQuestion("q3", "After teaching an observed practicum lesson, a reflective teacher first:", listOf("Identifies strengths and concrete action points based on student learning outcomes", "Throws away the lesson plan", "Blames the textbook", "Skips feedback"), 0, "Reflective practice focuses on how effectively students achieved the lesson aims.")
            )
        )
    )

    val allLevels: List<CurriculumLevel> = seeds.map { seed ->
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
            materials = listOf(
                StudyMaterial(
                    id = "${seed.id}_mat_vocab",
                    title = "${seed.title} • Core Vocabulary & Phrases",
                    category = MaterialCategory.VOCABULARY,
                    summary = "Tap cards to flip and master key phrases for ${seed.title} (${seed.hours} Hrs).",
                    contentBody = "Focus Area: ${seed.topicFocus}\nPractice saying each phrase aloud three times with clear stress and intonation.",
                    keyPhrases = seed.vocab,
                    xpReward = 30
                ),
                StudyMaterial(
                    id = "${seed.id}_mat_grammar",
                    title = "${seed.title} • Grammar & Language Rulebook",
                    category = MaterialCategory.GRAMMAR,
                    summary = "Essential language pattern and classroom examples for this unit.",
                    contentBody = seed.grammarRule,
                    keyPhrases = listOf(
                        "Golden Rule" to seed.grammarRule,
                        "Unit Focus" to seed.topicFocus
                    ),
                    xpReward = 35
                ),
                StudyMaterial(
                    id = "${seed.id}_mat_skills",
                    title = "${seed.title} • Interactive Skill Workshop",
                    category = if (seed.skills.any { it.contains("Listening") || it.contains("Speaking") }) {
                        MaterialCategory.LISTENING_SPEAKING
                    } else {
                        MaterialCategory.READING_WRITING
                    },
                    summary = "Guided workshop covering ${seed.skills.joinToString(", ")}.",
                    contentBody = "In this ${seed.hours}-hour module, students practice ${seed.skills.joinToString(" & ")} through pair work, guided reading, and interactive classroom drills.",
                    keyPhrases = seed.skills.map { skill ->
                        skill to "Core competency assessed locally in class for ${seed.title}."
                    },
                    xpReward = 35
                )
            ),
            assignments = listOf(
                LevelAssignment(
                    id = "${seed.id}_assign_1",
                    title = "${seed.title} Classroom Mission",
                    typeLabel = "In-Class Activity",
                    instructions = seed.assignmentTask,
                    rubricCriteria = "Accuracy (25 pts) • Fluency/Clarity (25 pts) • Vocabulary Use (25 pts) • Classroom Participation (25 pts)",
                    maxPoints = 100,
                    xpReward = 50
                ),
                LevelAssignment(
                    id = "${seed.id}_assign_2",
                    title = "${seed.title} Workbook & Portfolio Check",
                    typeLabel = "Workbook & Skill Log",
                    instructions = "Complete the unit workbook exercises for ${seed.title} (${seed.hours} Hrs) and review corrections with your teacher in class.",
                    rubricCriteria = "Completeness (50 pts) • Self-Correction & Neatness (50 pts)",
                    maxPoints = 100,
                    xpReward = 40
                )
            ),
            quizQuestions = seed.quiz
        )
    }

    fun getLevelById(id: String): CurriculumLevel? = allLevels.find { it.id == id }
}
