package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

data class PromptChipData(
    val id: String,
    val labelPt: String,
    val labelXm: String,
    val icon: ImageVector,
    val promptPt: String,
    val promptXm: String,
    val category: String,
    val isAction: Boolean = false // If true, triggers dedicated action (e.g., breathe, emergency)
)

@Composable
fun QuickPromptChips(
    isXimakonde: Boolean,
    onPromptSelected: (prompt: String, category: String) -> Unit,
    onOpenEmergency: () -> Unit,
    onOpenBreathe: () -> Unit,
    onOpenQuickGuides: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chips = listOf(
        PromptChipData(
            id = "chip_english",
            labelPt = "📘 Aprender Inglês",
            labelXm = "📘 Kulifunda Chingelesi",
            icon = Icons.Default.School,
            promptPt = "Podes ensinar-me uma lição simples de inglês para o dia a dia?",
            promptXm = "Nindaleka kulifunda Chingelesi kidoko na maneno la kila liduva.",
            category = "english"
        ),
        PromptChipData(
            id = "chip_protection",
            labelPt = "🛡️ Proteção Criança (116)",
            labelXm = "🛡️ Kunyemelela Vana (116)",
            icon = Icons.Default.Shield,
            promptPt = "Explica-me os direitos da criança e como funciona a Linha Fala Criança 116 em Moçambique.",
            promptXm = "Ntangolele haki ya vana na sheria ya kutokutola mwana nkanana na namba 116.",
            category = "protection"
        ),
        PromptChipData(
            id = "chip_psychosocial",
            labelPt = "💚 Apoio Emocional",
            labelXm = "💚 Kupumula Mutima",
            icon = Icons.Default.Favorite,
            promptPt = "Preciso de apoio emocional. Podes dar-me palavras de ânimo e serenidade?",
            promptXm = "Mutima unanitesha na woga. Nipe mashauri la kupumula.",
            category = "psychosocial"
        ),
        PromptChipData(
            id = "chip_breathe",
            labelPt = "🧘 Exercício Respiração",
            labelXm = "🧘 Kupumula Mpumu",
            icon = Icons.Default.SelfImprovement,
            promptPt = "",
            promptXm = "",
            category = "psychosocial",
            isAction = true
        ),
        PromptChipData(
            id = "chip_education",
            labelPt = "🎓 Dicas de Estudo",
            labelXm = "🎓 Mashauri la Kusoma",
            icon = Icons.Default.MenuBook,
            promptPt = "Como posso organizar o meu tempo de estudo para ter boas notas nos exames?",
            promptXm = "Mashauri gani lakuvelele kusoma shule na kupata maksi vinani?",
            category = "education"
        ),
        PromptChipData(
            id = "chip_ximakonde",
            labelPt = "🇲🇿 Saudações Ximakonde",
            labelXm = "🇲🇿 Salamano Ximakonde",
            icon = Icons.Default.Translate,
            promptPt = "Ensina-me saudações respeitosas em língua Ximakonde de Moçambique.",
            promptXm = "Ntangolele maneno makulu la heshima mu Ximakonde.",
            category = "general"
        ),
        PromptChipData(
            id = "chip_emergency",
            labelPt = "📞 Linhas de Socorro",
            labelXm = "📞 Namba ya Msaada",
            icon = Icons.Default.Call,
            promptPt = "",
            promptXm = "",
            category = "protection",
            isAction = true
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("quick_prompt_chips_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        chips.forEach { chip ->
            AssistChip(
                onClick = {
                    when (chip.id) {
                        "chip_emergency" -> onOpenEmergency()
                        "chip_breathe" -> onOpenBreathe()
                        else -> {
                            val prompt = if (isXimakonde) chip.promptXm else chip.promptPt
                            onPromptSelected(prompt, chip.category)
                        }
                    }
                },
                label = {
                    Text(
                        text = if (isXimakonde) chip.labelXm else chip.labelPt,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = chip.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                elevation = AssistChipDefaults.assistChipElevation(elevation = 1.dp),
                modifier = Modifier.testTag("prompt_chip_${chip.id}")
            )
        }
    }
}
