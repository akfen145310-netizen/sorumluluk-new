package com.example.data

data class TaskDefinition(
    val id: String,
    val title: String,
    val categoryId: String,
    val subCategoryId: String? = null,
    val diamondReward: Int = 5
)

object DefaultTasks {
    val HOME_TASKS = listOf(
        "Yatağını toplamak",
        "Kirli kıyafetlerini kirli sepetine atmak",
        "Odasını düzenli tutmak",
        "Yemekten önce sofranın kurulmasına yardım etmek",
        "Yemekten sonra sofranın toplanmasına yardım etmek",
        "Kendi tabağını mutfağa götürmek",
        "Oyuncaklarını toplamak",
        "Kitaplarını ve çalışma masasını düzenlemek",
        "Çiçekleri sulamak",
        "Evdeki çöpleri boşaltmaya yardım etmek",
        "Ayakkabılarını düzenli bir şekilde yerleştirmek",
        "Kıyafetlerini katlamaya yardım etmek",
        "Evcil hayvanla ilgilenmek/beslemek",
        "Toz almaya yardım etmek",
        "Kendi eşyalarını asmak",
        "Pijama ve ev kıyafetlerini değiştirmek",
        "Ortak kullanım alanlarını temiz bırakmak",
        "Aile bireylerine yardım etmek",
        "Uyku saatine zamanında hazırlanmak",
        "Kişisel hijyenini bağımsız yapmak"
    )

    val SCHOOL_TASKS = listOf(
        "Derslerini dikkatle dinlemek",
        "Ödevlerini zamanında ve eksiksiz yapmak",
        "Derse zamanında girmek",
        "Sınıf içi kurallarına uymak",
        "Arkadaşlarıyla saygılı iletişim kurmak",
        "Sınıf eşyalarını özenli kullanmak",
        "Çantasını akşamdan hazırlamak",
        "Ders araç gereçlerini eksiksiz getirmek",
        "Söz alarak konuşmak",
        "Teneffüslerde güvenli oyunlar oynamak",
        "Okul bahçesini ve sınıfını temiz tutmak",
        "Öğretmenlerine karşı saygılı olmak",
        "Grup çalışmalarında iş birliği yapmak",
        "Okuma saatlerinde kitabına odaklanmak",
        "Şikayet etmek yerine çözüm üretmek",
        "Okul formasına ve dış görünüşüne özen göstermek",
        "Ders araç gereçlerini paylaşmak",
        "Sınıf görevlilerine yardımcı olmak",
        "Hatalı olduğunda özür dilemek",
        "Ders çıkışı eşyalarını unutmadan toplamak"
    )

    val RELIGION_TASKS = listOf(
        "Sabah namazını kılmak",
        "Öğle namazını kılmak",
        "İkindi namazını kılmak",
        "Akşam namazını kılmak",
        "Yatsı namazını kılmak",
        "Yemekten önce elleri yıkamak (Sünnet)",
        "Yemekten sonra elleri yıkamak (Sünnet)",
        "Yemekten sonra dişleri fırçalamak/Misvak",
        "Güne Bismillah diyerek başlamak",
        "Yemekten sonra Elhamdülillah demek",
        "Günlük kısa bir sure veya ayet okumak",
        "Yatmadan önce dua etmek",
        "Anne ve babaya saygılı davranmak",
        "Selamlaşma adabına uymak",
        "Sadaka vermeyi alışkanlık haline getirmek",
        "Abdest almayı uygulamak",
        "Kuran harflerini/okumayı çalışmak",
        "Yalan söylememek",
        "Emanete hıyanet etmemek",
        "Canlılara şefkatle davranmak"
    )

    val ETIQUETTE_MAP = mapOf(
        "sitting" to listOf(
            "Büyüklerin yanında uygun şekilde oturmak",
            "Bir yere otururken önce büyüklere yer vermek",
            "Kaba bir oturuş sergilememek",
            "Oturduğu yeri temiz bırakmak",
            "Ders sırasında dik ve dikkatli oturmak"
        ),
        "speaking" to listOf(
            "Söz kesmemek",
            "Alçak ve yumuşak sesle konuşmak",
            "Konuşurken göz teması kurmak",
            "Lütfen, Teşekkür Ederim kelimelerini kullanmak",
            "Argo ve kırıcı kelimelerden uzak durmak"
        ),
        "eating_drinking" to listOf(
            "Yemeğe Bismillah ile başlamak",
            "Ağzı kapalıyken çiğnemek",
            "Sağ el ile yemek yemek",
            "Önünden yemek ve israf etmemek",
            "Yemekten sonra Elhamdülillah demek"
        ),
        "sleeping_waking" to listOf(
            "Uyku saatine sadık kalmak",
            "Yatmadan önce diş fırçalamak ve abdest almak",
            "Gereksiz ışıkları kapatmak",
            "Uyanınca güler yüzle selam vermek",
            "Kalkar kalkmaz yatağını düzeltmek"
        ),
        "greeting" to listOf(
            "Ortama güler yüzle selam vermek",
            "Kendini uygun şekilde tanıtmak",
            "El sıkışırken nazik olmak",
            "Büyüklerin elini öpmek/selamlamak",
            "Ayrılırken iyi dileklerde bulunmak"
        ),
        "guest_etiquette" to listOf(
            "İzinsiz odalara girmemek",
            "Ev sahibine teşekkür etmek",
            "Yüksek sesle konuşmamak",
            "İkramları nazikçe kabul/reddetmek",
            "Ayrılırken teşekkür ederek veda etmek"
        ),
        "hygiene" to listOf(
            "Kişisel bakıma dikkat etmek",
            "Ortak alanları temiz bırakmak",
            "Hapşırırken ağzı kapatmak",
            "Kıyafetlerin temizliğine özen göstermek",
            "Çöpleri kutuya atmak"
        ),
        "elders" to listOf(
            "Büyüklerin sözünü dikkatle dinlemek",
            "Siz diye hitap etmek",
            "Yardım tekliflerini nazikçe kabul etmek",
            "Yanlarında yüksek sesle gülmemek",
            "Sabırlı ve anlayışlı olmak"
        ),
        "peers" to listOf(
            "Eşyaları izinsiz almamak",
            "Alay etmekten kaçınmak",
            "Başarıları tebrik etmek",
            "Sır tutmak ve güvenilir olmak",
            "Sakin kalıp uzlaşmaya çalışmak"
        ),
        "public_tech" to listOf(
            "Toplu taşımada yaşlılara yer vermek",
            "Sırasını beklemek",
            "Telefonla çevreyi rahatsız etmemek",
            "İnternette nazik dil kullanmak",
            "Kütüphanede fısıldayarak konuşmak"
        )
    )

    val ALL_TASKS: List<TaskDefinition> by lazy {
        val list = mutableListOf<TaskDefinition>()
        var counter = 1

        HOME_TASKS.forEachIndexed { index, title ->
            list.add(TaskDefinition("home_${index + 1}", title, "home", null, 5))
            counter++
        }

        SCHOOL_TASKS.forEachIndexed { index, title ->
            list.add(TaskDefinition("school_${index + 1}", title, "school", null, 5))
            counter++
        }

        RELIGION_TASKS.forEachIndexed { index, title ->
            list.add(TaskDefinition("religion_${index + 1}", title, "religion", null, 5))
            counter++
        }

        ETIQUETTE_MAP.forEach { (subCat, items) ->
            items.forEachIndexed { index, title ->
                list.add(TaskDefinition("etiquette_${subCat}_${index + 1}", title, "etiquette", subCat, 5))
                counter++
            }
        }

        list
    }

    // Default 20 tasks: Exactly 5 from Home, 5 from School, 5 from Religion, 5 from Etiquette
    val DEFAULT_DAILY_20_IDS: List<String> = listOf(
        // 5 Home
        "home_1", "home_2", "home_3", "home_4", "home_8",
        // 5 School
        "school_1", "school_2", "school_3", "school_7", "school_12",
        // 5 Religion
        "religion_1", "religion_6", "religion_8", "religion_9", "religion_12",
        // 5 Etiquette
        "etiquette_speaking_1", "etiquette_eating_drinking_1", "etiquette_greeting_1", "etiquette_elders_1", "etiquette_peers_3"
    )
}
