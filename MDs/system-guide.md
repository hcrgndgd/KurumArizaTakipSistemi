AI Kategori Entegrasyonu — Sistem Rehberi

Kısa açıklama:
- Ticket oluşturulurken kullanıcı kategori seçmezse sistem AI ile kategori önerir.
- AI: Google Gemini (generateContent). Yedek: deterministik anahtar eşleme.

Önemli dosyalar:
- src/main/java/.../service/CategoryCatalogService.java — Türkçe varsayılan kategorileri (Yazilim, Donanim, Ag, Tesisat, Guvenlik, Malzeme eksikligi, Elektrik) DB'ye ekler.
- src/main/java/.../service/CategorySuggestionService.java — Gemini çağrısı, prompt oluşturma, yanıtın kategoriye eşlenmesi.
- src/main/java/.../service/StartupSeeder.java — Uygulama başında kategori tohumlama.
- src/main/resources/ai.properties — gemini.api.key, gemini.model, gemini.url

Konfigürasyon ve güvenlik:
- gemini.api.key gizli tutulmalı (env/secret manager önerilir).
- Anahtar yoksa sistem deterministik (keyword/token overlap) eşlemeye döner.

Logs:
- Prompt ve ham cevap debug olarak loglanır ("Prompt:\n...", "Raw Gemini response:").
