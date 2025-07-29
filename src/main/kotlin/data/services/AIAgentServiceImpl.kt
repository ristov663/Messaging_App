package com.example.data.services

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import com.example.domain.models.AIConversation
import com.example.domain.repos.AIConversationRepository
import com.example.domain.services.AIAgentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AIAgentServiceImpl(
    private val aiConversationRepository: AIConversationRepository
) : AIAgentService {

    val sales_agent_prompt = """
        You are a professional Sales Agent for our company. Your task is to:
        
        1. Greet potential clients in a friendly manner
        2. Understand their needs
        3. Present appropriate products/services
        4. Answer questions about pricing, features, and availability
        5. Try to close the sale or schedule an appointment

        Important:
        - Always be polite and professional
        - Don't make up prices or information you don't know
        - If you don't know something, say you'll check and get back with an answer
        - Answer briefly and clearly

        Answer only the client's question without further explanation.
    """.trimIndent()

    private val apiKey: String = System.getenv("GEMINI_API_KEY")

    private val salesAgent = AIAgent(
        executor = simpleGoogleAIExecutor(apiKey),
        systemPrompt = sales_agent_prompt,
        llmModel = GoogleModels.Gemini1_5Flash
    )

    override suspend fun processUserMessage(
        chatRoomId: Int,
        userId: Int,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        try {
            val history = aiConversationRepository.getConversationHistory(chatRoomId, userId, 5)

            val contextMessage = buildContextMessage(history, userMessage)

            val aiResponse = salesAgent.run(contextMessage)
            println("AI response: $aiResponse")

            aiConversationRepository.saveConversation(
                chatRoomId = chatRoomId,
                userId = userId,
                userMessage = userMessage,
                aiResponse = aiResponse
            )

            return@withContext aiResponse

        } catch (e: Exception) {
            println("Error from AI Agent: ${e.message}")
            return@withContext "Sorry, I'm currently experiencing technical difficulties. Please try again later."
        }
    }

    private fun buildContextMessage(history: List<AIConversation>, currentMessage: String): String {
        val contextBuilder = StringBuilder()

        if (history.isNotEmpty()) {
            contextBuilder.append("Previous conversation:\n")
            history.forEach { conversation ->
                contextBuilder.append("Client: ${conversation.userMessage}\n")
                contextBuilder.append("You: ${conversation.aiResponse}\n\n")
            }
        }

        contextBuilder.append("Current question from client: $currentMessage")

        return contextBuilder.toString()
    }

    override suspend fun getConversationHistory(chatRoomId: Int, userId: Int): List<AIConversation> {
        return aiConversationRepository.getConversationHistory(chatRoomId, userId, 10)
    }
}
