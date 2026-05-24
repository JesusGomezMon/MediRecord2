## MediRecord Multiplataforma (React Native + Express)

Nuevo stack multiplataforma para Android/iOS con backend Express + SQLite.

### Estructura
- `app/`: cliente React Native (Expo, TypeScript, React Navigation, Zustand).
- `server/`: API Express + SQLite (better-sqlite3, Zod, Vitest + Supertest).
- `android-legacy/`: código Android Compose previo (solo referencia).

### Backend (API)
1) `cd server`
2) `npm install`
3) Desarrollo: `npm run dev` (Puerto por defecto: `4000`)
4) Producción: `npm run build && npm start`
5) Tests: `npm test` (usa DB en memoria)

Endpoints principales:
- `GET /health` (stats)
- CRUD: `/medications`, `/reminders`, `/appointments`, `/intakes`
- `GET /search?q=...` (proxy a Wikipedia resumen ES/EN)

### App móvil (Expo)
1) `cd app`
2) `npm install`
3) Con backend local: `npm run android` (usa `http://10.0.2.2:4000` en emulador) / `npm run web`
4) Para dispositivo físico, define `EXPO_PUBLIC_API_URL=http://TU_IP_LOCAL:4000`
5) iOS requiere macOS + Xcode (expo run:ios)

Pantallas clave:
- Dashboard con cuadrícula 2 columnas y métricas.
- Medicamentos, Recordatorios, Citas con formularios rápidos y listas.
- Historial de tomas.
- Búsqueda web con resumen Wikipedia y guardado directo a meds.

### Notas de UX
- Tema claro minimalista, tarjetas elevadas, botones grandes.
- Grid principal con gradientes y CTA claros.
- Textos legibles y espaciado amplio para personas mayores.
