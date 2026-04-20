mktoid/
├── .github/workflows/build.yml      ← workflow do GitHub Actions
├── build.gradle                      ← config raiz
├── settings.gradle                   ← config raiz
├── gradle.properties                 ← config raiz
├── gradlew                           ← script gradle (opcional, o Actions cria)
└── app/
    ├── build.gradle
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/mktoid/app/MainActivity.kt
        └── res/
            ├── layout/activity_main.xml
            ├── values/strings.xml
            └── values/themes.xml
