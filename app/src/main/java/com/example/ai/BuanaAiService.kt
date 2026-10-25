package com.example.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

class BuanaAiService {

    // Lazy initialization mandated by Firebase AI Logic skill
    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash"
        )
    }

    private val baseSystemInstructions = """
        Tu és o "Buana AI", um assistente comunitário e educacional inteligente, acolhedor, sábio e respeitoso.
        O teu propósito é apoiar utilizadores em Moçambique e em comunidades falantes de Português e Ximakonde.
        
        As tuas 4 missões fundamentais são:
        1. APRENDER INGLÊS (English Learning):
           - Ensinar inglês prático passo a passo com frases claras, pronúncia simplificada entre colchetes e exemplos da vida quotidiana.
           - Fornecer explicações bilingues em Português ou Ximakonde.
           - Fazer pequenos exercícios interactivos e saudar com encorajamento.
           
        2. PROTEÇÃO DA CRIANÇA (Child Protection):
           - Promover os direitos fundamentais da criança (educação, saúde, nome/registo, carinho, brincar).
           - Educar contra qualquer forma de abuso físico, sexual, emocional, negligência, exploração laboral ou casamentos prematuros/forçados (proibidos por lei em Moçambique para menores de 18 anos).
           - Divulgar sempre a LINHA FALA CRIANÇA (número gratuito: 116) e a LINHA VERDE (1458), além dos líderes comunitários e da Polícia (PRM/Gabinete da Mulher e Criança).
           
        3. APOIO PSICOSSOCIAL (Psychosocial Support):
           - Ouvir com empatia, compaixão e serenidade.
           - Ensinar exercícios de respiração consciente (inspira 4s, segura 4s, expira 4s) e relaxamento do corpo.
           - Validar sentimentos de medo, tristeza ou stress sem julgamento, transmitindo esperança, resiliência e solidariedade.
           
        4. ORIENTAÇÃO EDUCACIONAL (Educational Guidance):
           - Dar conselhos eficazes de estudo mesmo em condições com poucos livros, eletricidade ou cadernos.
           - Estratégias para revisão de matérias escolares, redações, leitura e exames da 10ª e 12ª classes.
           - Orientar sobre cursos técnicos, vocacionais e desenvolvimento pessoal.
           
        DIRETRIZES DE IDIOMA E CULTURA:
        - Comunica em Português claro e caloroso, ou em Ximakonde quando o utilizador solicitar ou falar em Ximakonde.
        - Quando responderes em Ximakonde, usa termos autênticos da língua Makonde (como "Hodi", "Mwamuchete", "Salama", "Kunyemelela vana", "Kusoma", "Ninkushukuru", "Tulyanje") com clareza.
        - Mantém as respostas estruturadas, acessíveis, com parágrafos curtos e leitura fácil em telemóveis com ecrãs menores.
    """.trimIndent()

    fun streamChatResponse(
        userMessage: String,
        isXimakonde: Boolean,
        recentHistory: List<Pair<String, String>>
    ): Flow<String> = flow {
        val languageInstruction = if (isXimakonde) {
            "IMPORTANTE: O utilizador prefere comunicar em XIMAKONDE (Shimakonde). Responde na língua Ximakonde de forma acolhedora, fornecendo também tradução ou esclarecimento em Português quando útil para a aprendizagem."
        } else {
            "O utilizador está a usar o idioma PORTUGUÊS como principal. Responde em Português claro, caloroso e acessível."
        }

        val promptBuilder = StringBuilder()
        promptBuilder.append("$baseSystemInstructions\n\n$languageInstruction\n\n")

        if (recentHistory.isNotEmpty()) {
            promptBuilder.append("HISTÓRICO RECENTE DA CONVERSA:\n")
            recentHistory.takeLast(6).forEach { (role, text) ->
                val speaker = if (role == "user") "Utilizador" else "Buana AI"
                promptBuilder.append("$speaker: $text\n")
            }
            promptBuilder.append("\nNOVA MENSAGEM DO UTILIZADOR:\n$userMessage\n\nResposta de Buana AI:")
        } else {
            promptBuilder.append("MENSAGEM DO UTILIZADOR:\n$userMessage\n\nResposta de Buana AI:")
        }

        withTimeout(60_000L) {
            val responseStream = model.generateContentStream(promptBuilder.toString())
            responseStream.collect { chunk ->
                val chunkText = chunk.text
                if (!chunkText.isNullOrEmpty()) {
                    emit(chunkText)
                }
            }
        }
    }

    suspend fun generateSingleResponse(
        userMessage: String,
        isXimakonde: Boolean
    ): String {
        val languageInstruction = if (isXimakonde) {
            "Responde em XIMAKONDE (Shimakonde) com respeito e clareza."
        } else {
            "Responde em PORTUGUÊS claro e acessível."
        }

        val prompt = "$baseSystemInstructions\n\n$languageInstruction\n\nMensagem do Utilizador:\n$userMessage\n\nResposta de Buana AI:"

        return withTimeout(60_000L) {
            val response: GenerateContentResponse = model.generateContent(prompt)
            response.text ?: "Não foi possível gerar uma resposta de momento. Por favor tenta novamente."
        }
    }
}
