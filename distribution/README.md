# Google Play Store Automation & Distribution

This directory contains browser automation scripts and documentation for distributing **Metronome** on the **Google Play Store**.

---

## 📜 Available Scripts

### 1. `google_play_load_images_script.js`
- **Target Page:** Google Play Console $\rightarrow$ **Store presence** $\rightarrow$ **Main store listing** (or Default store listing)
- **Purpose:** Automatically loads phone screenshots (`01.png`, `02.png`, `03.png`, `04.png`, `05.png`) in strict order into the **Phone screenshots** section using the Google Play Asset Library drawer.
- **Exclusion List:** Automatically skips languages that have dedicated custom localized promo frames in the repository ([`screenshots/promo_frames`](../screenshots/promo_frames)):
  `ar`, `de-DE`, `eng-US` (`en`), `es-ES`, `fr-FR`, `hi-IN`, `id-ID`, `it-IT`, `ja-JP`, `ko-KR`, `pl-PL`, `pt-BR`, `pt-PT`, `th-TH`, `tr-TR`, `uk-UA`, `vi-VN`.
- **Workflow:**
  1. Checks if the active language is excluded (skips if it has dedicated promo frames).
  2. Checks if phone screenshots are already uploaded ($\ge 5$, skips if already filled).
  3. Locates and clicks "Add assets" in the Phone screenshots section.
  4. Sequentially searches the drawer for `01` $\rightarrow$ selects `01.png` $\rightarrow$ clicks `Add`.
  5. Repeats for `02.png`, `03.png`, `04.png`, and `05.png`.
  6. Automatically clicks "Next language" and processes subsequent locales.

### 2. `google_play_store_listing_script.js`
- **Target Page:** Google Play Console $\rightarrow$ **Store presence** $\rightarrow$ **Main store listing**
- **Coverage:** 110 locales (92 primary languages + regional variants).
- **Metadata Updated:**
  - **App Name:** Max 30 characters
  - **Short Description:** Max 80 characters
  - **Full Description:** Max 4000 characters
- **Configurable Modes:**
  - `ONLY_IF_EMPTY` (default): Only fills empty fields, safely preserving existing translations.
  - `FORCE_UPDATE`: Overwrites all fields with current translation data.
  - `SKIP_IF_MATCHING`: Updates only if different from target translation.
- **Keystroke Simulation:** Emulates real keyboard interaction to reliably trigger Angular / Material Design dirty state and activate the **Save as draft** button.

---

## 🚀 How to Run

1. Open [Google Play Console](https://play.google.com/console) in Chrome.
2. Navigate to your app's **Store presence** $\rightarrow$ **Main store listing**.
3. Open Developer Tools (`F12` or `Ctrl + Shift + I`) and switch to the **Console** tab.
4. Copy the entire contents of the desired `.js` script and paste it into the console.
5. Press `Enter` to start the automation.
6. When execution completes across all languages, click **Save as draft** or **Save changes** at the bottom of the page.
