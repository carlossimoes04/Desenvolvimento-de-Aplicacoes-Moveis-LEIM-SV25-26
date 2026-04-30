# AI Agent Guidelines

The AI agent must follow a planning-first approach for a Multi-Module Android Architecture.

Rules:
1. Always read the documentation inside `/docs` before generating code.
2. Follow the multi-module architecture defined in `docs/06_architecture.md` strictly (`:core`, `:app-xml`, `:app-compose`).
3. Generate Kotlin code only.
4. When working on `:core`, generate pure data/logic (no UI dependencies).
5. When working on `:app-xml`, use XML Views and traditional Activities/Fragments.
6. When working on `:app-compose`, use Jetpack Compose exclusively (Declarative UI).
7. Do not generate large files at once. Generate code step-by-step following the implementation plan.
