# Запуск тестов клиента

Из корня `GoodRoad-Client` на Windows:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Из WSL/Linux:

```bash
./gradlew :app:testDebugUnitTest
```

Для WSL в `local.properties` должен быть путь к Android SDK в формате `/mnt/c/...`.
Для Windows можно оставить путь вида `C\:\\Users\\...\\Android\\Sdk`.

Успешный результат:

```text
BUILD SUCCESSFUL
```

HTML-отчет:

```text
app/build/reports/tests/testDebugUnitTest/index.html
```
