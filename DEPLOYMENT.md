# Deployment

## Backend — Render

A `render.yaml` blueprint is in `Backend/`. To deploy:

1. Push repo to GitHub
2. In Render dashboard: New → Web Service → select repo
3. Render auto-detects `render.yaml`. Set the 9 env vars manually (sync: false):
   `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`,
   `CORS_ALLOWED_ORIGINS`, `JWT_SECRET`, `JWT_EXPIRATION`, `ENCRYPTION_KEY`
4. Deploy. URL looks like `https://transiq-backend.onrender.com`
5. Verify at `{URL}/actuator/health`

## Frontend — Vercel

1. Import repo to Vercel, root directory = `Frontend`
2. Vite preset auto-detects
3. Add env var: `VITE_API_BASE_URL = {Render URL}/api/v1`
4. `vercel.json` handles SPA fallback for React Router
5. Update backend `CORS_ALLOWED_ORIGINS` to the Vercel URL

## Cold-start wake-up

The landing page fires `GET /actuator/health` on mount (plain fetch).
If the hardcoded path doesn't hit the deployed backend, update it to use `VITE_API_BASE_URL`'s origin.

## Checklist

- [ ] Deploy backend to Render, note URL
- [ ] Deploy frontend to Vercel, note URL
- [ ] Wire CORS: update backend env with Vercel URL
- [ ] Full e2e pass: register, login, dashboard, payments, checkout demo
- [ ] Cold-start test: wait 15+ min, reload landing page, login
- [ ] Check browser console for CORS/network errors
