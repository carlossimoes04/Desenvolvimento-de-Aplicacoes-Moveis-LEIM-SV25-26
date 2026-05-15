package dam

import kotlinx.coroutines.runBlocking

/**
 * Main entry point for the LLM Assistant application
 */
fun main() = runBlocking {
    println("\n🤖 Starting LLM Assistant application... 😀😀😀😀😀\n")

    // Get configuration properties
    val properties = getProperties()

    // Set up logging
    configureLogging(properties)
    println()

    // Write LLM used
    println("✨ Using AI_LLM: ${properties.getProperty("AI_LLM")}")

    // Use the factory to create the appropriate assistant based on configuration
    val assistant: AIAssistant = AIAssistantFactory.createAssistant(properties)
    println()

    // Write system and model
    println("✨ Using: ${assistant.getSystem()} ${assistant.model}\n")

    while (true) {
        // Ask for mode
        print("🔧 Mode? [1] Chat  [2] Sentiment Analysis  [0] Exit: ")
        val mode = readlnOrNull()?.trim() ?: break
        println()

        when (mode) {
            "0" -> break
            "1" -> {
                println("💬 Type your questions and press Enter to chat with the AI.")
                println("💬 Type 'menu' to go back to mode selection.\n")

                while (true) {
                    println("➖➖➖➖➖➖➖➖➖➖")
                    print("🧠 Your question: ")
                    val input = readlnOrNull() ?: break

                    if (input.trim().lowercase() == "menu") break

                    if (input.isBlank()) {
                        println("⚠️ Please enter a question.")
                        continue
                    }

                    val output = assistant.processInput(input)
                    println("\n🤖 Answer: $output\n")
                }
            }
            "2" -> {
                println("🔍 Sentiment Analysis mode — type text to analyze.")
                println("💬 Type 'menu' to go back to mode selection.\n")

                while (true) {
                    println("➖➖➖➖➖➖➖➖➖➖")
                    print("📝 Text to analyze: ")
                    val input = readlnOrNull() ?: break

                    if (input.trim().lowercase() == "menu") break

                    if (input.isBlank()) {
                        println("⚠️ Please enter some text.")
                        continue
                    }

                    val result = assistant.analyzeSentiment(input)
                    println("\n⭐ Rating: ${result.rating}/7 — ${result.justification}\n")
                }
            }
            else -> println("⚠️ Invalid mode. Please choose 1 or 2.\n")
        }
    }

    // Bye message
    println("\n👋 Thank you for using LLM Assistant. Goodbye!")

}

/**
 * The temperature value (typically between 0.0 and 1.0) affects how deterministic
 * or creative the AI model's responses will be:
 * - Low temperature (e.g., 0.1-0.3): More deterministic, focused, and predictable responses.
 *   The model is more likely to choose the most probable next token at each step.
 * - Medium temperature (e.g., 0.4-0.7): Balanced between determinism and creativity,
 *   providing reasonably varied responses while maintaining coherence.
 * - High temperature (e.g., 0.8-1.0): More random, diverse, and creative responses.
 *   The model may take more risks and generate more surprising content.
 *
 * Use cases:
 *  1. For technical documentation: use low temperature (0.1-0.3)
 *  2. For creative storytelling: use high temperature (0.8-1.0)
 *  3. For conversation: use medium temperature (0.4-0.7)
 *  4. For code generation: use low-medium temperature (0.2-0.5)
 *  5. For summarization: use medium temperature (0.4-0.7)
 *  6. For sentiment analysis: use high temperature (0.8-1.0)
 *  7. For image generation: use medium temperature (0.4-0.7)
 *  8. For image captioning: use medium temperature (0.4-0.7)
 *  9. For question answering: use medium temperature (0.4-0.7)
 * 10. For chatbots: use medium temperature (0.4-0.7)
 * 11. For summarization: use medium temperature (0.4-0.7)
 * 12. For translation: use low temperature (0.1-0.3)
 * 13. For voice conversion: use low temperature (0.1-0.3)
 *
 * As max tokens definem o tamanho máximo da resposta. Quanto maior o valor,
 * mais longa pode ser a resposta, e quanto menor, mais curta e rápida tende a ser.
 * Também ajuda a controlar custo e tempo de resposta.
 *
 * 3.3 - Task 3: Temperature tests
 * Provide two test cases that demonstrate how changes in the temperature value lead to
 * noticeably different outputs. Show two prompts that produce very distinct outputs,
 * with temperature: 0 (min.), 0.5 (med.) and 1 (max.).
 *  Nota: eu deixei o max tokens em 300 para ambos os prompts e para todas as temperaturas,
 *  para garantir que as respostas sejam comparáveis. Como o Kimi tem o Thinking ativo,
 *  as respostas aqui colocadas serão a "response" após o thinking.
 *
 *  Prompt 1: Describe what makes Lisbon special
 *  - Temp 0:
 *  Lisbon is truly one of a kind! It s a city that manages to feel both grand and intimate,
 *  historic and effortlessly cool.
 *  **The Light and the Views**
 *  First, there s the light. Lisbon is famous for its luminous, golden quality thanks to its southern
 *  location and the way the sun reflects off the Tagus River. The city is built on seven hills, which means you re
 *  never far from a
 *
 *  - Temp 0.5:
 *  Lisbon is one of those cities that just *swoops* you off your feet! It s a place where old-world
 *  charm dances with a surprisingly youthful, creative energy. Here are a few things that make it so special:
 *  **The Light**
 *  First, there s the famous *Luz de Lisboa* a soft, golden light that seems to wrap the whole city in a warm glow.
 *  Thanks to its location on the Atlantic and the Tagus River, the light here is simply magical, especially at sunset
 *  when the terracotta rooftops turn fiery orange.
 *  **A City of Seven Hills (and Miradouros)**
 *  Lisbon is built on seven steep hills, which means you ll get a great workout
 *  walking around but the reward is incredible. Scattered across the city
 *
 *  - Temp 1:
 *  Lisbon is truly magical there s something about it that captures your heart the moment you arrive.
 *  Here are a few of the things that make it so special:
 *  Lisbon is famously built on seven hills, which means you re never far from a stunning miradouro (viewpoint)
 *  overlooking terracotta rooftops, the River Tagus, and the iconic 25 de Abril Bridge.
 *  Plus, the city gets more than 300 days of sunshine a year, and the golden Atlantic light bouncing off
 *
 *  Prompt 2: Write a short, imaginative travel story set in Lisbon
 *  - Temp 0: (não me deu reposta, apenas mostrou o pensamento)
 *  Thinking: the user wants me to write a short imaginative travel story set in Lisbon. The preferred language is English.
 *  I should respond in a friendly and helpful manner. The story should be short, imaginative, and set in Lisbon.
 *  I need to incorporate Lisbon's unique elements: trams (especially Tram 28), hills, azulejos (tiles), Fado music,
 *  the Tagus River, Alfama district, miradouros (viewpoints), etc. Keep it relatively short but evocative.
 *  I'll write a story about a traveler discovering something magical in Lisbon. Maybe about a tram that takes a
 *  different route, or a hidden azulejo that tells a story. Let's go with a story about a traveler who boards Tram 28
 *  but it takes an unexpected turn into a Lisbon that exists between the present and the past. Or perhaps something
 *  more grounded but magical realist. Let's do a story about a woman named Clara who follows a stray cat through
 *  Alfama and discovers a Fado singer in a hidden courtyard. Actually, let's make it more imaginative.
 *  A story about a traveler who finds a pair of vintage sunglasses at a flea market (Feira da Ladra)
 *  and when wearing them, sees Lisbon as it was in the 1920s. That's nice and imaginative. Let's write that.
 *  Keep it short, around 300-400 words. Friendly opening, then the story. Title maybe. Let's craft it.
 *  Title: "The Amber Lens" or something. Let's call it "
 *
 *  - Temp 0.5: (não me deu reposta, apenas mostrou o pensamento)
 *  Thinking: The user wants a short, imaginative travel story set in Lisbon. The constraints are: my name is Assistant,
 *  preferred language is English, respond in a friendly and helpful manner. No other restrictions. I should
 *  write a creative, evocative story that captures the magic of Lisbon perhaps the trams, the hills, the light,
 *  the Tagus River, fado music, tiles (azulejos), etc. Keep it relatively short but imaginative. Tone should be
 *  warm and friendly in the intro/outro.
 *  Let's craft something with a touch of magical realism or wonder. Maybe about a traveler discovering a hidden
 *  tram line, or a conversation with a local, or a mysterious bookshop. Lisbon is great for stories about memory,
 *  saudade, and light.
 *  Idea: A traveler follows a stray cat through the Alfama district at dawn and discovers a tram that runs on
 *  moonlight, taking them through different eras of the city. Or simpler: A woman finds a pair of vintage sunglasses
 *  in a flea market (Feira da Ladra) that lets her see the city's past. Let's go with something grounded but magical:
 *  A traveler arrives in Lisbon and every time they climb a set of stairs in Alfama, they step into a different version
 *  of the city past and present blending. Ending with a fado song in a miradouro.
 *  Actually, let's do a story about a lost notebook. The protagonist finds a leather notebook in a pastel de nata shop,
 *  and following its clues
 *
 *  - Temp 1:
 *  Thinking: need me to respond based on role settings, but the user hasn't provided specific role information or context.
 *  Looking carefully at the user's input:
 *
 *  User says:
 *  "Your name is Assistant.
 *  The user's request is: "Write a short & imaginative travel story set in Lisbon"
 *  Respond in a friendly and helpful manner.
 *  The preferred language is English."
 *
 *  This appears to be an instruction set or a meta-prompt. The user wants me to??????????????,??????????????????????
 *
 *  So I need to:
 *  1. Write a short and imaginative travel story set in Lisbon
 *  2. Be friendly and helpful in tone
 *  3. Use English
 *
 *  Let me draft a short, imaginative story about Lisbon. I'll include elements like:
 *  - Tram 28 (the iconic yellow tram)
 *  - Alfama district
 *  - Fado music
 *  - The scent of the sea and custard tarts (past is de nata)
 *  - Maybe a magical realism twist to make it imaginative
 *
 *  Draft idea:
 *  A traveler arrives in Lisbon and gets on Tram 28, but instead of just seeing the city,
 *  the tram takes them through different eras of Lisbon's history - they see Moorish sailors,
 *  Age of Discovery explorers, and????. Or maybe something more subtle.
 *
 *  Let's go with a story about a woman named Maya who finds a vintage tram ticket in a book at a Lisbon
 *  bookshop, and when she boards the tram, it takes her on a journey through the city's
 *
 * 3.4 - Task 4: Sentiment analysis (temperature = 0.5 | max tokens = 300)
 * - Text to analyze: I absolutely love Lisbon, it is the most beautiful city in the world!
 *  - Rating: 7/7 � The text expresses strong enthusiasm with words like 'absolutely love' and
 * 'most beautiful city in the world', indicating extremely positive sentiment.
 *
 * - Text to analyze: The service was terrible and the food was cold and disgusting.
 *  - Rating: 1/7 � The text contains strongly negative language including 'terrible,'
 *  'cold,' and 'disgusting,' indicating an extremely poor experience with both service
 *  and food quality.
 *
 * - Text to analyze: "It was okay, nothing special, just a regular day."
 *  - Rating: 4/7 � The text expresses mild indifference with phrases like 'okay,' 'nothing special,'
 *  and 'just a regular day,' indicating a neutral, unenthusiastic sentiment without any positive
 *  or negative intensity.
 */
