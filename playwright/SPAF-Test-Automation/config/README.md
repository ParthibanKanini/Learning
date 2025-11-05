# Config

Centralised environment configuration. The framework uses the `APP_ENV` env var (default `dev`) to select the correct JSON file and merge in any secrets from `.env.local` or CI-provided environment variables.

Copy .env.example to .env.local
Edit .env.local with your personal credentials

Files:

- `env.ts` loader
- `appEnv.<env>.json` per environment, holds baseUrl & default credential placeholders
