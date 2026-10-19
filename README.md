# Calculator (Android, Java + Gradle)

A simple, fully functional Android calculator with **addition, subtraction,
multiplication, division, decimals, clear (C) and delete (DEL)**. Built with
native Android (Java), Gradle, and Material 3 components.

- **Min SDK:** 21 (Android 5.0)
- **Target SDK:** 34 (Android 14)
- **Language:** Java 17
- **Build system:** Gradle 8.2 + Android Gradle Plugin 8.1.4

The project is ready to build as-is. No extra code is required from you.

---

## 1. Project structure

```
CalculatorApp/
├── .github/workflows/build.yml      # GitHub Actions workflow (builds APKs)
├── app/
│   ├── build.gradle                 # App module build config (+ release signing)
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/calculator/MainActivity.java
│       └── res/
│           ├── layout/activity_main.xml
│           ├── drawable/            # Launcher icon foreground
│           ├── mipmap-*/            # Launcher icons (all densities)
│           └── values/              # colors, strings, themes, dimens
├── gradle/wrapper/                  # Gradle wrapper jar + properties
├── build.gradle                     # Root build file
├── settings.gradle
├── gradle.properties
├── gradlew / gradlew.bat            # Gradle wrapper scripts
├── .gitignore
└── README.md
```

---

## 2. Build locally (optional)

You need **JDK 17** and **Android SDK** installed. From the project root:

```bash
./gradlew assembleDebug      # debug APK -> app/build/outputs/apk/debug/
./gradlew assembleRelease    # release APK -> app/build/outputs/apk/release/
```

The debug APK is signed with the default Android debug keystore, so you can
install it right away:

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 3. Upload the project to GitHub

1. Unzip the downloaded ZIP anywhere on your computer.
2. Create a new, **empty** repository on GitHub (do **not** add a README,
   .gitignore, or license — the project already includes them).
3. In a terminal, from inside the unzipped `CalculatorApp` folder:

```bash
git init
git add .
git commit -m "Initial calculator project"
git branch -M main
git remote add origin https://github.com/<your-username>/<your-repo>.git
git push -u origin main
```

Replace `<your-username>` and `<your-repo>` with your GitHub username and
the repository name.

---

## 4. Run the build workflow & download the debug APK

The workflow file `.github/workflows/build.yml` runs automatically on every
push to `main` or `master`, on pull requests, and can be triggered manually.

### Run it
- After pushing, open your repository on GitHub.
- Go to the **Actions** tab.
- Click the latest **"Build Android APK"** run (or trigger one with
  **Run workflow** under *Build Android APK* → *Run workflow*).

### Download the debug APK
1. Open the completed workflow run.
2. Scroll to the **Artifacts** section at the bottom of the run summary.
3. Click **calculator-debug-apk** to download a ZIP containing
   `app-debug.apk`.
4. Unzip it and install the APK on your device:

```bash
adb install app-debug.apk
```

> Note: GitHub Actions artifacts expire after 30 days by default (set in the
> workflow). Re-run the workflow to regenerate them.

---

## 5. Build a **signed release** APK (optional)

The debug APK is enough to install and run the app. For a release you can
publish (e.g. on the Play Store or for distribution), you must sign it with
your own keystore. The workflow already supports this — you only need to
generate a keystore and store it in GitHub Secrets.

### 5.1 Generate a release-signing keystore

On your computer (with **JDK 17+** installed so `keytool` is available),
run:

```bash
keytool -genkey -v \
  -keystore release.keystore \
  -alias calculator-key \
  -keyalg RSA -keysize 2048 \
  -validity 10000 \
  -storepass YOUR_KEYSTORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD
```

You will be prompted for name, organization, etc. — fill them in or press
Enter to skip. **Keep this keystore file safe.** If you lose it, you cannot
update a published app signed with it.

Notes:
- `release.keystore` → the keystore file
- `calculator-key` → the key alias (the `-alias` value)
- `YOUR_KEYSTORE_PASSWORD` → the keystore password (the `-storepass` value)
- `YOUR_KEY_PASSWORD` → the key password (the `-keypass` value)

You can use the same password for both, or different ones.

### 5.2 Store the keystore in GitHub Secrets

The keystore is a binary file. GitHub Secrets are text, so we base64-encode
the keystore before storing it.

1. Encode the keystore:

```bash
base64 -i release.keystore | tr -d '\n' > keystore.b64
# On Windows (PowerShell):
# [Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore")) | Out-File -NoNewline keystore.b64
```

2. On GitHub, go to your repository → **Settings → Secrets and variables →
   Actions → New repository secret**. Add these four secrets:

| Name                  | Value                                                       |
| --------------------- | ----------------------------------------------------------- |
| `SIGNING_KEY_BASE64`  | The full contents of `keystore.b64` (the base64 string)     |
| `KEYSTORE_PASSWORD`   | Your keystore password (e.g. `YOUR_KEYSTORE_PASSWORD`)     |
| `KEY_ALIAS`           | Your key alias (e.g. `calculator-key`)                      |
| `KEY_PASSWORD`        | Your key password (e.g. `YOUR_KEY_PASSWORD`)               |

> Never commit the keystore file or these passwords to the repository. The
> included `.gitignore` already excludes `*.keystore`.

### 5.3 Build the release APK

Once the four secrets are set, the workflow will automatically build a
**signed release APK** on every run (in addition to the debug APK). The
release step only runs when `SIGNING_KEY_BASE64` is present, so the build
still succeeds without it.

Download it the same way as the debug APK, but pick the artifact named
**calculator-release-apk**. The APK inside is signed with your keystore and
ready for distribution.

---

## 6. Troubleshooting

- **Workflow doesn't start:** Make sure the default branch is `main` or
  `master`, or trigger it manually via *Actions → Run workflow*.
- **No artifact appears:** Open the failed run and check the logs. The
  most common cause is a typo in a secret name.
- **Release step skipped:** The `Build signed release APK` step is
  skipped until all four signing secrets are set (see section 5.2).
- **Keystore error:** Verify the base64 string is a single line with no
  whitespace, and that `KEY_ALIAS` / `KEYSTORE_PASSWORD` / `KEY_PASSWORD`
  match exactly what you passed to `keytool`.

---

## 7. License

You are free to use, modify, and distribute this project.
