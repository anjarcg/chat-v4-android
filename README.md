# Chat V4 Android — Sidajaya AI

Mirror **Chat V4** (`labsai-app/chat.php` + `n8n j5Cl4ODSdDmYimah /Chat-v4`).

## Arsitektur (sama persis web)
```
Android (Kotlin Compose) → https://ai.sidajaya.shop/chat_start (POST multipart: message, sessionId, model, files)
 → n8n https://n8n.sidajaya.shop/webhook/Chat-v4 (38 nodes: Cek Command → History → Vision → AI Agent Tavily+pgvector)
 → chat_callback → POST https://ai.sidajaya.shop/chat_callback
Android poll GET https://ai.sidajaya.shop/chat_status?job_id= tiap 2s → tampil reply
Login POST https://ai.sidajaya.shop/index.php (username+password+action=login) → cookie session 30 hari
```
- Mode: auto/flash/pro/coding/mendalam (mapping `config.json mode_models`)
- DB: `sidajaya_ai` (`chat_sessions`, `chat_messages`, `chat_jobs`) — tetap server, app hanya client
- Vision: upload image via multipart `chat_start` → n8n `HTTP Vision`

## Build
`./gradlew assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions
Push ke `main` → build APK otomatis (artifact `chat-v4-debug-apk`).
