package com.example.data

data class QuickGuideItem(
    val id: String,
    val titlePt: String,
    val titleXm: String,
    val subtitlePt: String,
    val subtitleXm: String,
    val iconName: String,
    val category: String,
    val prompt: String,
    val offlineContentPt: String,
    val offlineContentXm: String
)

data class EmergencyContact(
    val name: String,
    val number: String,
    val descriptionPt: String,
    val descriptionXm: String,
    val isFree: Boolean = true
)

object KnowledgeRepository {

    val emergencyContacts = listOf(
        EmergencyContact(
            name = "Linha Fala Criança",
            number = "116",
            descriptionPt = "Linha telefónica gratuita para denúncia de violência, abuso e proteção de menores em Moçambique.",
            descriptionXm = "Namba ya bule ya kupilika na kusaidia vana vanaoshushiwa au kupata tabu.",
            isFree = true
        ),
        EmergencyContact(
            name = "Linha Verde de Emergência",
            number = "1458",
            descriptionPt = "Atendimento gratuito de emergência, desastres e assistência humanitária.",
            descriptionXm = "Namba ya bule ya usaidizi wa haraka na msaada wa kibinadamu.",
            isFree = true
        ),
        EmergencyContact(
            name = "Gabinete da Mulher e Criança (PRM)",
            number = "112",
            descriptionPt = "Polícia da República de Moçambique - Atendimento especializado a vítimas de violência.",
            descriptionXm = "Mapolisi va kuvalangila vakwetu na vana vankuteshiwa.",
            isFree = true
        ),
        EmergencyContact(
            name = "Apoio Psicossocial Comunitário",
            number = "116",
            descriptionPt = "Aconselhamento emocional para crianças, jovens e famílias em momentos difíceis.",
            descriptionXm = "Mashauri la ntima na kupumula mutima kwanganya vana na madadaye.",
            isFree = true
        )
    )

    val quickGuides = listOf(
        QuickGuideItem(
            id = "english_basics",
            titlePt = "Aprender Inglês Básico",
            titleXm = "Kulifunda Chingelesi",
            subtitlePt = "Saudações, frases do dia a dia e vocabulário",
            subtitleXm = "Salamu na maneno la kila liduva",
            iconName = "School",
            category = "english",
            prompt = "Ensina-me 5 frases essenciais em inglês para iniciantes com tradução e pronúncia simplificada.",
            offlineContentPt = """
                📘 FRASES ESSENCIAIS EM INGLÊS:
                
                1. "Hello! How are you?"
                   Pronúncia: [Relou! Rau ar iu?]
                   Significado: Olá! Como está?
                   Resposta: "I am fine, thank you!" [Ai ém fain, tenk iu] (Estou bem, obrigado!)
                   
                2. "What is your name?"
                   Pronúncia: [Uót iz ior neim?]
                   Significado: Qual é o seu nome?
                   Resposta: "My name is..." [Mai neim iz...] (O meu nome é...)
                   
                3. "Please" e "Thank you"
                   Significado: Por favor e Obrigado/a.
                   
                4. "I want to learn English"
                   Pronúncia: [Ai uont tu lérn inglich]
                   Significado: Eu quero aprender inglês.
                   
                5. "Good morning / Good afternoon / Good evening"
                   Significado: Bom dia / Boa tarde / Boa noite.
            """.trimIndent(),
            offlineContentXm = """
                📘 MANENO LA CHINGELESI:
                
                1. "Hello! How are you?" -> Salama! Mwamuchete chidoni?
                2. "My name is..." -> Lina lyangu nangu ni...
                3. "Please" na "Thank you" -> Ninkushukuru / Samahani.
                4. "I want to learn" -> Nindaleka kusoma.
                5. "Good morning" -> Salama ya kuche!
            """.trimIndent()
        ),
        QuickGuideItem(
            id = "child_protection",
            titlePt = "Proteção da Criança",
            titleXm = "Kunyemelela Vana",
            subtitlePt = "Direitos, prevenção e Linha 116",
            subtitleXm = "Haki ya vana na namba 116",
            iconName = "Security",
            category = "protection",
            prompt = "Explica quais são os direitos fundamentais da criança em Moçambique e como protegê-las contra o abuso e casamentos prematuros.",
            offlineContentPt = """
                🛡️ DIREITOS DA CRIANÇA EM MOÇAMBIQUE:
                
                • Direito à Educação: Toda a criança deve frequentar a escola primária e secundária.
                • Não aos Casamentos Prematuros: A lei moçambicana proíbe casamentos de menores de 18 anos. Criança não é esposa!
                • Proteção contra Trabalho Infantil: Crianças não devem fazer trabalhos perigosos que prejudiquem os seus estudos e saúde.
                • Direito ao Registo de Nascimento: Ter certidão de nascimento é a chave para a cidadania.
                • Onde Denunciar: Ligue GRÁTIS para a LINHA FALA CRIANÇA: 116 ou procure os Líderes Comunitários e a PRM.
            """.trimIndent(),
            offlineContentXm = """
                🛡️ HAKI NA KUNYEMELELA VANA:
                
                • Mwana anavakele kusoma shule.
                • Kutolana mwana nkanana aikulupwilwe! Sheria ya Moçambique inakahana kutolana mwana asina miaka 18.
                • Kunkoloma mwana madengo lakudidimika aipalemwe.
                • Kupata cheti cha kuvyalikwa ni haki ya kila mwana.
                • Kupilika usaidizi: Piga namba ya bule 116 (Linha Fala Criança).
            """.trimIndent()
        ),
        QuickGuideItem(
            id = "psychosocial_support",
            titlePt = "Apoio Psicossocial & Calma",
            titleXm = "Kupumula Mutima",
            subtitlePt = "Alívio de stress, respiração 4-4-4 e acolhimento",
            subtitleXm = "Kuleka woga, kupumula salama",
            iconName = "Favorite",
            category = "psychosocial",
            prompt = "Sinto-me angustiado e com muito stress. Dá-me palavras de conforto e guia-me num exercício prático para acalmar a mente.",
            offlineContentPt = """
                💚 RESPIRAÇÃO CONSCIENTE (4-4-4):
                
                1. Inspire pelo nariz devagar contando mentalmente até 4. (1... 2... 3... 4)
                2. Segure o ar no peito contando até 4.
                3. Solte o ar suavemente pela boca contando até 4.
                4. Repita 3 a 5 vezes.
                
                Lembre-se:
                • As suas emoções são válidas. É normal sentir medo ou cansaço.
                • Não precisa carregar todo o peso do mundo sozinho hoje.
                • Converse com alguém da sua confiança na sua comunidade.
            """.trimIndent(),
            offlineContentXm = """
                💚 KUPUMULA MUTIMA NA KULEKA WOGA:
                
                1. Vuta mpumu mupua uvalangile 1... 2... 3... 4.
                2. Chimika mpumu munsula 1... 2... 3... 4.
                3. Fucha mpumu mpaka kankano 1... 2... 3... 4.
                4. Tenda chidoni mara tatu au tano.
                
                Kumbukila:
                • Auna weka. Vakwetu vakupatika.
                • Mutima unalema, hema kadi kidoko. Salama indapwateka.
            """.trimIndent()
        ),
        QuickGuideItem(
            id = "educational_guidance",
            titlePt = "Orientação de Estudo",
            titleXm = "Mashauri la Kusoma",
            subtitlePt = "Como estudar bem mesmo com poucos materiais",
            subtitleXm = "Kusoma vinani hata vinu vachoko",
            iconName = "AutoStories",
            category = "education",
            prompt = "Dá-me as melhores dicas para organizar os meus estudos para os exames da escola com poucos recursos.",
            offlineContentPt = """
                🎓 DICAS PRÁTICAS DE ESTUDO:
                
                1. Pequenos Blocos (25 minutos):
                   Estude 25 minutos com foco total e descanse 5 minutos.
                   
                2. Explicar com as suas próprias palavras:
                   Depois de ler um parágrafo, feche o caderno e tente explicar em voz alta o que compreendeu.
                   
                3. Grupos de Estudo com Colegas:
                   Reúna 2 ou 3 amigos debaixo de uma sombra para resolver exercícios juntos e tirar dúvidas.
                   
                4. Faça Resumos Breves:
                   Escreva as fórmulas, palavras-chave e datas numa folha de rascunho para rever antes de dormir.
            """.trimIndent(),
            offlineContentXm = """
                🎓 MASHAURI LA KUSOMA VINANI:
                
                1. Soma kidoko kidoko, pumula kadiki.
                2. Tangola kwa linolyoko vinu vyausomile bila kulola mu libuku.
                3. Kusoma pamo na vakwetu: Linganyanga na ashikole vakwenu mulole vinu vyavashuluvike.
                4. Andika maneno makulu mu lipapila uvalangile livaluva.
            """.trimIndent()
        ),
        QuickGuideItem(
            id = "ximakonde_phrases",
            titlePt = "Vocabulário Ximakonde",
            titleXm = "Luga ya Ximakonde",
            subtitlePt = "Palavras de respeito, saudações e diálogo",
            subtitleXm = "Maneno la heshima na salamano",
            iconName = "Translate",
            category = "general",
            prompt = "Ensina-me saudações respeitosas e palavras de amizade em Ximakonde com tradução para Português.",
            offlineContentPt = """
                🇲🇿 DIALOGANDO EM XIMAKONDE:
                
                • Hodi! = Licença! (ao chegar a uma casa)
                • Karibu! = Seja bem-vindo! Entre!
                • Mwamuchete chidoni? = Como estão vocês? / Tudo bem?
                • Nindichete salama = Estou bem com paz.
                • Ninkushukuru namene = Muito obrigado / agradeço muito.
                • Ashikole = Alunos / estudantes.
                • Kusoma ni lyuva = O estudo é a luz do sol.
                • Tulyanje pamo = Vamos caminhar juntos / união.
            """.trimIndent(),
            offlineContentXm = """
                🇲🇿 MANENO LA XIMAKONDE:
                
                • Hodi! -> Kwikuta pamulyango.
                • Karibu! -> Yinjiila munyumba!
                • Mwamuchete? -> Salama!
                • Ninkushukuru namene -> Kutumbila mutima.
                • Vana ni vinu vyamaana -> Vana vapalikwa kunyemelelwa.
            """.trimIndent()
        )
    )

    fun getInitialGreeting(isXimakonde: Boolean): String {
        return if (isXimakonde) {
            "Hodi vakwetu! Lina lyangu ni Buana AI. Nindiva apa kukusaidia kulifunda Chingelesi (English), kunyemelela vana, kupumula mutima na mashauri la kusoma. Lemba lilove lyoko apa pashini au chukula chimwe cha vinu vyalelolwe!"
        } else {
            "Olá! Sou o Buana AI, o teu assistente inteligente e companheiro de aprendizagem. Posso ajudar-te a:\n\n• Aprender Inglês (vocabulário, pronúncia e diálogos)\n• Direitos e Proteção da Criança (apoio e Linha 116)\n• Apoio Psicossocial (conforto, alívio de ansiedade e respiração)\n• Orientação Educacional (técnicas de estudo e exames)\n\nEscreve uma mensagem ou escolhe um dos tópicos rápidos abaixo. Também podes alternar para Ximakonde a qualquer momento no topo!"
        }
    }
}
