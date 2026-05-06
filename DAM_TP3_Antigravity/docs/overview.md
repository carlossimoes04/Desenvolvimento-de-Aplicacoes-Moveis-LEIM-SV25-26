# Overview

## Purpose of the Application
The purpose of this application is to retrieve and display a gallery of random images using the public Picsum Photos API. 
For Phase 3 (MIP-3), the project has been refactored into a multi-module architecture to demonstrate the separation of business logic from the UI. It now features two distinct presentation layers consuming the same shared core: a legacy XML-based app and a modern Jetpack Compose app.

## Target Users
Photography enthusiasts and developers learning architectural migration from imperative to declarative UI patterns.

## How the System Works
The system operates using a shared `:core` module that makes HTTP requests to the Picsum Photos API using Retrofit, caching and managing data via a Repository. 
- The `:app-xml` module consumes this data and binds it to a traditional `RecyclerView`.
- The `:app-compose` module consumes the exact same data but binds it to an adaptive `LazyVerticalStaggeredGrid` using modern state hoisting and Compose features.
