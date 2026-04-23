Uygulama Özeti — AI Kategori Önerisi

Ne yapıldı (kısa):
- Gemini tabanlı kategori önerisi eklendi.
- Varsayılan kategoriler "ticket_categories" tablosuna seedleniyor (StartupSeeder).
- Kullanıcı kategori seçmezse UserController, CategorySuggestionService.resolveCategory() ile kategori alır.

Değişen/eklenen dosyalar:
- src/main/java/.../service/CategoryCatalogService.java (mevcut) — default kategori listesi.
- src/main/java/.../service/CategorySuggestionService.java (yeni) — Gemini entegrasyonu ve eşleme mantığı.
- src/main/java/.../service/StartupSeeder.java (yeni) — uygulama başında seeding.
- src/main/resources/ai.properties (yeni/ayrıntılı) — gemini.api.key, gemini.model, gemini.url

Test:
1. Uygulamayı yeniden başlatın. Kayıtlı kategorileri DB'de doğrulayın.
2. Yeni ticket oluşturun (kategori boş). Loglarda prompt ve model cevabını kontrol edin.

Not: Geminin model çıktı verisinde tutarlı değilse deterministik fallback devreye girer. Gerekirse prompt örnekleri sıkılaştırıldı.
