/**
 * ==============================================================================
 * GOOGLE PLAY CONSOLE - BATCH SCREENSHOT LOADER SCRIPT
 * ==============================================================================
 * Automatically loads generic screenshots (01.png, 02.png, 03.png, 04.png, 05.png)
 * from the Asset Library into the "Phone screenshots" section for all languages,
 * EXCLUDING languages that have custom localized promo frames in the repository.
 *
 * Repository Promo Frames Excluded:
 * https://github.com/ochashchin/android-metronome-app/tree/main/screenshots/promo_frames
 * Excluded: ar, de-DE, eng-US (en), es-ES, fr-FR, hi-IN, id-ID, it-IT, ja-JP,
 *           ko-KR, pl-PL, pt-BR, pt-PT, th-TH, tr-TR, uk-UA, vi-VN.
 *
 * Exact Flow per Non-Excluded Language:
 * 1. Scroll to "Phone screenshots" section / "Add assets" button
 * 2. Add asset -> Search 01 + Press Enter + Wait 4.5s -> Select 01 -> Click Add
 * 3. Add asset -> Search 02 + Press Enter + Wait 4.5s -> Select 02 -> Click Add
 * 4. Add asset -> Search 03 + Press Enter + Wait 4.5s -> Select 03 -> Click Add
 * 5. Add asset -> Search 04 + Press Enter + Wait 4.5s -> Select 04 -> Click Add
 * 6. Add asset -> Search 05 + Press Enter + Wait 4.5s -> Select 05 -> Click Add
 * 7. Click "Next language" -> Repeat!
 * ==============================================================================
 */

(async function loadGooglePlayScreenshots() {
  'use strict';

  // CONFIGURATION
  const CONFIG = {
    // Sequence of assets to load from the library (order is preserved strictly)
    assetsToLoad: ['01', '02', '03', '04', '05'],

    // Resume / start specifically from this language (matches "Swahili – sw", "Swahili", "sw")
    // If empty string '', starts immediately on whatever language is open
    startFromLanguage: 'Swahili',

    // If false, adds screenshots regardless of whether some already exist
    skipIfAlreadyHasScreenshots: false,
    targetScreenshotCount: 5,

    // Test on just the currently open language before running across all languages
    onlyCurrentLocale: false,

    // Timing configurations (in milliseconds)
    drawerOpenWaitMs: 1500,
    drawerSearchWaitMs: 4500, // Wait 4.5s after typing + Enter for search query
    afterSelectWaitMs: 700,
    afterAddWaitMs: 1800,
    afterLocaleWaitMs: 1000,
    switchWaitMs: 2500,
    maxWaitTimeoutMs: 6000
  };

  // 17 LOCALE CODES FROM GITHUB PROMO FRAMES REPOSITORY TO EXCLUDE:
  // https://github.com/ochashchin/android-metronome-app/tree/main/screenshots/promo_frames
  const EXCLUDED_LANGUAGE_CODES = [
    'ar',
    'de-de', 'de',
    'eng-us', 'en-us', 'en', 'eng',
    'es-es', 'es', 'es-419',
    'fr-fr', 'fr', 'fr-ca',
    'hi-in', 'hi',
    'id-id', 'id',
    'it-it', 'it',
    'ja-jp', 'ja',
    'ko-kr', 'ko',
    'pl-pl', 'pl',
    'pt-br', 'pt-pt', 'pt',
    'th-th', 'th',
    'tr-tr', 'tr',
    'uk-ua', 'uk',
    'vi-vn', 'vi'
  ];

  const EXCLUDED_LANGUAGE_NAMES = [
    'arabic', 'german', 'english', 'spanish', 'french',
    'hindi', 'indonesian', 'italian', 'japanese', 'korean',
    'polish', 'portuguese', 'thai', 'turkish', 'ukrainian', 'vietnamese'
  ];

  const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms));

  function isLanguageExcluded(langText) {
    if (!langText) return false;
    const lower = langText.toLowerCase();

    for (const code of EXCLUDED_LANGUAGE_CODES) {
      const regex = new RegExp(`(?:[-–—\\s(]|^)${code}(?:[-–—\\s)]|$)`, 'i');
      if (regex.test(lower)) return true;
    }

    for (const name of EXCLUDED_LANGUAGE_NAMES) {
      if (lower.includes(name)) return true;
    }

    return false;
  }

  function getCurrentLanguageText() {
    const btn = document.querySelector('language-switcher-button-menu [debug-id="text-button"]') ||
                document.querySelector('[debug-id="text-button"]');
    if (!btn) return '';
    return btn.getAttribute('aria-label') || btn.textContent.trim();
  }

  function getNextLanguageButton() {
    return document.querySelector('button[debug-id="next-language-button"]');
  }

  function getSaveButtonStatus() {
    const saveBtn = document.querySelector('button[debug-id="save-draft-button"]') ||
                    Array.from(document.querySelectorAll('button')).find(b =>
                      b.textContent.includes('Save') || b.textContent.includes('Сохранить')
                    );
    if (!saveBtn) return '❓ Not found';
    const isDisabled = saveBtn.disabled || saveBtn.hasAttribute('disabled') || saveBtn.getAttribute('aria-disabled') === 'true';
    return isDisabled ? '❌ Disabled' : '✅ ACTIVE & CLICKABLE (Ready to save)';
  }

  // PHONE SCREENSHOTS SECTION HELPERS
  function getPhoneScreenshotsHolder() {
    return document.querySelector('[debug-id="phone-screenshots-holder"], phone-screenshots-holder') ||
           document.querySelector('[debug-id="phone-screenshots-field"]') ||
           document.querySelector('[debug-id="phone-assets-section"]');
  }

  function getExistingPhoneScreenshotsCount() {
    const holder = getPhoneScreenshotsHolder();
    if (!holder) return 0;

    // 1. Check for text like "5 / 8" or "0 / 8"
    const textMatch = holder.textContent.match(/(\d+)\s*\/\s*8/);
    if (textMatch) {
      return parseInt(textMatch[1], 10);
    }

    // 2. Count image preview elements inside the holder
    const previews = holder.querySelectorAll('[debug-id="image-preview"], .screenshot-preview, [debug-id="image"]');
    return previews.length;
  }

  function getOpenAssetLibraryButton() {
    const holder = getPhoneScreenshotsHolder();
    if (holder) {
      const btn = holder.querySelector('button[debug-id="open-asset-library-button"]') ||
                  Array.from(holder.querySelectorAll('button')).find(b =>
                    b.textContent.includes('Add asset') || b.textContent.includes('Добавить')
                  );
      if (btn) return btn;
    }
    return document.querySelector('button[debug-id="open-asset-library-button"]');
  }

  // ASSET LIBRARY DRAWER HELPERS
  function getAssetDrawer() {
    const drawers = document.querySelectorAll('material-drawer, side-panel, [role="complementary"], .side-panel, aside');
    for (const d of drawers) {
      if (d.offsetParent !== null && (d.textContent.includes('Store listing') || d.textContent.includes('Manage tags') || d.querySelector('input'))) {
        return d;
      }
    }
    return document.body;
  }

  function getDrawerSearchInput() {
    const drawer = getAssetDrawer();
    const inputs = Array.from(drawer.querySelectorAll('input'));
    // Find text search input, strictly excluding file, checkbox, radio, hidden
    const searchInput = inputs.find(inp => {
      const type = (inp.getAttribute('type') || inp.type || 'text').toLowerCase();
      return type !== 'file' && type !== 'checkbox' && type !== 'radio' && type !== 'hidden';
    });
    return searchInput || null;
  }

  async function searchInDrawer(input, text) {
    if (!input || input.type === 'file') {
      console.warn('  ⚠️ searchInDrawer skipped: input element is file or null.');
      return;
    }

    try {
      input.focus();
      input.select();
    } catch (e) {}

    // 1. Clear existing text
    try {
      const proto = Object.getPrototypeOf(input);
      const setter = Object.getOwnPropertyDescriptor(proto, 'value')?.set;
      if (setter) {
        setter.call(input, '');
      } else {
        input.value = '';
      }
      input.dispatchEvent(new Event('input', { bubbles: true, cancelable: true }));
    } catch (e) {
      console.warn('  ⚠️ Could not clear input value:', e.message);
    }
    await sleep(150);

    // 2. Set new query text
    try {
      const proto = Object.getPrototypeOf(input);
      const setter = Object.getOwnPropertyDescriptor(proto, 'value')?.set;
      if (setter) {
        setter.call(input, text);
      } else {
        input.value = text;
      }
      input.dispatchEvent(new Event('input', { bubbles: true, cancelable: true }));
      input.dispatchEvent(new Event('change', { bubbles: true, cancelable: true }));
    } catch (e) {
      console.warn('  ⚠️ Could not set input value:', e.message);
    }
    await sleep(200);

    // 3. Dispatch Enter key events (keydown, keypress, keyup)
    const enterEvents = ['keydown', 'keypress', 'keyup'];
    for (const evtName of enterEvents) {
      input.dispatchEvent(new KeyboardEvent(evtName, {
        key: 'Enter',
        code: 'Enter',
        keyCode: 13,
        which: 13,
        charCode: 13,
        bubbles: true,
        cancelable: true
      }));
    }

    console.log(`  ⌨️ Typed "${text}" and pressed Enter. Waiting ${CONFIG.drawerSearchWaitMs / 1000}s for search results...`);
    await sleep(CONFIG.drawerSearchWaitMs);
  }

  function findAssetSelectTarget(targetName) {
    const expectedFilename = `${targetName}.png`.toLowerCase();
    const shortTarget = targetName.toLowerCase();

    // 1. Search all list rows with class list-row-container
    const rows = Array.from(document.querySelectorAll('.list-row-container, [debug-id="asset-list-row-container"]'));
    for (const row of rows) {
      const nameSpan = row.querySelector('[debug-id="asset-name"], .asset-name-text, .asset-name');
      const nameText = (nameSpan ? nameSpan.textContent : row.textContent).trim().toLowerCase();

      if (nameText === expectedFilename || nameText.includes(expectedFilename) || nameText === shortTarget) {
        const selectBtn = row.querySelector('[debug-id="select-button"], .select-button');
        const tileContainer = row.querySelector('[debug-id="tile-container"], .tile-container');
        const img = row.querySelector('img[debug-id="image"], img');
        return { row, selectBtn, tileContainer, img };
      }
    }

    // 2. Search by [debug-id="asset-name"]
    const allNameSpans = Array.from(document.querySelectorAll('[debug-id="asset-name"]'));
    for (const span of allNameSpans) {
      const txt = span.textContent.trim().toLowerCase();
      if (txt === expectedFilename || txt.includes(expectedFilename)) {
        const row = span.closest('.list-row-container, div') || span.parentElement;
        const selectBtn = row.querySelector('[debug-id="select-button"], .select-button');
        const tileContainer = row.querySelector('[debug-id="tile-container"], .tile-container');
        const img = row.querySelector('img[debug-id="image"], img');
        return { row, selectBtn, tileContainer, img };
      }
    }

    // 3. Fallback: Take first row in filtered drawer results
    if (rows.length > 0) {
      console.log(`  ℹ️ Selecting first row in filtered drawer list.`);
      const row = rows[0];
      const selectBtn = row.querySelector('[debug-id="select-button"], .select-button');
      const tileContainer = row.querySelector('[debug-id="tile-container"], .tile-container');
      const img = row.querySelector('img[debug-id="image"], img');
      return { row, selectBtn, tileContainer, img };
    }

    return null;
  }

  async function clickAssetSelectTarget(targetInfo, targetName) {
    const { row, selectBtn, tileContainer, img } = targetInfo;

    const clickable = selectBtn || tileContainer || img || row;
    clickable.scrollIntoView({ behavior: 'auto', block: 'center' });
    await sleep(200);

    const rect = clickable.getBoundingClientRect();
    const eventOpts = {
      bubbles: true,
      cancelable: true,
      view: window,
      clientX: rect.left + rect.width / 2,
      clientY: rect.top + rect.height / 2
    };

    // 1. Click select-button (<a> tag with debug-id="select-button")
    if (selectBtn) {
      selectBtn.dispatchEvent(new PointerEvent('pointerdown', eventOpts));
      selectBtn.dispatchEvent(new MouseEvent('mousedown', eventOpts));
      selectBtn.dispatchEvent(new PointerEvent('pointerup', eventOpts));
      selectBtn.dispatchEvent(new MouseEvent('mouseup', eventOpts));
      selectBtn.click();
      console.log(`  🖱️ Clicked [debug-id="select-button"] for "${targetName}.png"`);
    }

    await sleep(300);

    // 2. Check if selected, otherwise also click tile-container
    const aria = tileContainer ? (tileContainer.getAttribute('aria-label') || '').toLowerCase() : '';
    const isSelected = aria.includes('selected asset') && !aria.includes('deselected');

    if (!isSelected && tileContainer) {
      console.log(`  🔄 Also clicking [debug-id="tile-container"]...`);
      const tRect = tileContainer.getBoundingClientRect();
      const tOpts = {
        bubbles: true,
        cancelable: true,
        view: window,
        clientX: tRect.left + tRect.width / 2,
        clientY: tRect.top + tRect.height / 2
      };
      tileContainer.dispatchEvent(new PointerEvent('pointerdown', tOpts));
      tileContainer.dispatchEvent(new MouseEvent('mousedown', tOpts));
      tileContainer.dispatchEvent(new PointerEvent('pointerup', tOpts));
      tileContainer.dispatchEvent(new MouseEvent('mouseup', tOpts));
      tileContainer.click();
    }
  }

  async function clickDrawerAddButton(maxWaitMs = 5000) {
    const start = Date.now();
    let addTarget = null;

    // 1. Wait until the Add button / label appears and is enabled
    while (Date.now() - start < maxWaitMs) {
      // Find span.mdc-button__label or button with exact text "Add"
      const allLabels = Array.from(document.querySelectorAll('.mdc-button__label, button span, a span, button'));
      const foundLabel = allLabels.reverse().find(el => {
        const txt = el.textContent.trim().toLowerCase();
        return txt === 'add' || txt === 'добавить';
      });

      if (foundLabel) {
        const btn = foundLabel.closest('button, [role="button"]') || foundLabel;
        const isDisabled = btn.disabled ||
                           btn.hasAttribute('disabled') ||
                           btn.getAttribute('aria-disabled') === 'true' ||
                           btn.classList.contains('mdc-button--disabled');
        if (!isDisabled) {
          addTarget = { btn, span: foundLabel };
          break;
        }
      }
      await sleep(250);
    }

    if (!addTarget) {
      throw new Error('Could not find enabled "Add" button (<span class="mdc-button__label">Add</span>) in drawer.');
    }

    const { btn, span } = addTarget;

    // Scroll into view
    btn.scrollIntoView({ behavior: 'smooth', block: 'center' });
    await sleep(200);

    // Full pointer + mouse event dispatch
    const rect = span.getBoundingClientRect();
    const eventOpts = {
      bubbles: true,
      cancelable: true,
      view: window,
      clientX: rect.left + rect.width / 2,
      clientY: rect.top + rect.height / 2
    };

    // Dispatch on SPAN (<span class="mdc-button__label">Add</span>)
    span.dispatchEvent(new PointerEvent('pointerdown', eventOpts));
    span.dispatchEvent(new MouseEvent('mousedown', eventOpts));
    span.dispatchEvent(new PointerEvent('pointerup', eventOpts));
    span.dispatchEvent(new MouseEvent('mouseup', eventOpts));
    span.click();

    // Dispatch on BUTTON (<button class="mdc-button ...">)
    if (btn !== span) {
      btn.dispatchEvent(new PointerEvent('pointerdown', eventOpts));
      btn.dispatchEvent(new MouseEvent('mousedown', eventOpts));
      btn.dispatchEvent(new PointerEvent('pointerup', eventOpts));
      btn.dispatchEvent(new MouseEvent('mouseup', eventOpts));
      btn.click();
    }

    console.log(`  ➕ Clicked "Add" (<span class="mdc-button__label">Add</span>)`);
    await sleep(CONFIG.afterAddWaitMs);
  }

  // STEP: LOAD SINGLE ASSET VIA DRAWER
  async function loadSingleAsset(targetName) {
    console.log(`  🔍 [Asset ${targetName}] Opening Asset Library...`);

    // 1. Find and click "Add assets" button
    const openBtn = getOpenAssetLibraryButton();
    if (!openBtn) {
      throw new Error(`Could not find "Add assets" button in Phone screenshots section.`);
    }

    openBtn.scrollIntoView({ behavior: 'smooth', block: 'center' });
    await sleep(400);
    openBtn.click();
    await sleep(CONFIG.drawerOpenWaitMs);

    // 2. Type targetName into search input, press Enter, wait 4.5 seconds
    const searchInput = getDrawerSearchInput();
    if (searchInput) {
      console.log(`  🔎 Searching for "${targetName}" in drawer search...`);
      await searchInDrawer(searchInput, targetName);
    } else {
      console.log(`  ⚠️ Search input not found, looking in visible drawer items directly...`);
      await sleep(1000);
    }

    // 3. Find asset row / select button (01.png)
    let targetInfo = findAssetSelectTarget(targetName);
    if (!targetInfo) {
      await sleep(1500);
      targetInfo = findAssetSelectTarget(targetName);
    }

    if (!targetInfo) {
      throw new Error(`Could not find asset row for "${targetName}.png" in Asset Library drawer.`);
    }

    // 4. Click select button / tile to select asset
    await clickAssetSelectTarget(targetInfo, targetName);
    console.log(`  🖱️ Selected asset: "${targetName}.png"`);
    await sleep(CONFIG.afterSelectWaitMs);

    // 5. Click "Add" button (<span class="mdc-button__label">Add</span>)
    await clickDrawerAddButton(CONFIG.maxWaitTimeoutMs);
  }

  // PROCESS CURRENT PAGE
  async function processLanguageScreenshots(langLabel) {
    console.group(`🌐 Google Play Screenshots: "${langLabel}"`);

    // Check exclusion
    if (isLanguageExcluded(langLabel)) {
      console.log(`  ⏭️ SKIPPED: Language is in excluded list (has custom promo frames in repo).`);
      console.groupEnd();
      return { skipped: true, reason: 'excluded' };
    }

    // Check existing count
    const existingCount = getExistingPhoneScreenshotsCount();
    if (CONFIG.skipIfAlreadyHasScreenshots && existingCount >= CONFIG.targetScreenshotCount) {
      console.log(`  ⏭️ SKIPPED: Already has ${existingCount} screenshots (>= ${CONFIG.targetScreenshotCount}).`);
      console.groupEnd();
      return { skipped: true, reason: 'already_filled' };
    }

    console.log(`  🚀 Loading screenshots 01 -> 02 -> 03 -> 04 -> 05... (Current: ${existingCount} screenshots)`);

    // Sequentially load each asset: 01, 02, 03, 04, 05
    for (const assetName of CONFIG.assetsToLoad) {
      try {
        await loadSingleAsset(assetName);
      } catch (err) {
        console.error(`  ❌ Error adding asset ${assetName}:`, err.message);
        throw err;
      }
    }

    const finalCount = getExistingPhoneScreenshotsCount();
    console.log(`  🎉 Successfully loaded all 5 screenshots! (Total: ${finalCount}/8)`);
    console.log(`  💾 Save Button Status: ${getSaveButtonStatus()}`);
    console.groupEnd();
    return { skipped: false, count: finalCount };
  }

  // SINGLE LOCALE MODE
  if (CONFIG.onlyCurrentLocale) {
    const curLang = getCurrentLanguageText() || 'Unknown Language';
    console.log(`🚀 Running SCREENSHOT LOADER in SINGLE LOCALE mode for "${curLang}"...`);
    await processLanguageScreenshots(curLang);
    console.log(`🎉 Finished single locale! Save button: ${getSaveButtonStatus()}`);
    return;
  }

  // BATCH MODE ACROSS ALL LANGUAGES
  console.log(`🚀 Starting Google Play Console batch screenshot loading...`);
  console.log(`ℹ️ Sequence: 01 -> 02 -> 03 -> 04 -> 05 (Search + Enter + wait 4.5s)`);
  console.log(`ℹ️ Excluded 17 languages from repo promo_frames.`);

  let processedCount = 0;
  let addedCount = 0;
  let skippedCount = 0;
  const visitedLanguages = new Set();
  let reachedStartLanguage = !CONFIG.startFromLanguage;

  while (true) {
    const curLang = getCurrentLanguageText();
    if (!curLang) {
      console.log('🏁 Finished: Language indicator element not found.');
      break;
    }
    if (visitedLanguages.has(curLang)) {
      console.log(`🏁 All configured languages completed! Cycled back to "${curLang}". Total visited: ${processedCount}.`);
      break;
    }

    visitedLanguages.add(curLang);
    processedCount++;

    // Check if we need to fast-forward until startFromLanguage
    if (!reachedStartLanguage) {
      const matchStart = curLang.toLowerCase().includes(CONFIG.startFromLanguage.toLowerCase());
      if (matchStart) {
        reachedStartLanguage = true;
        console.log(`🎯 Reached start language: "${curLang}". Resuming automated screenshot upload!`);
      } else {
        console.log(`⏩ [Fast-forward] Skipping "${curLang}" to reach "${CONFIG.startFromLanguage}"...`);
        const nextBtn = getNextLanguageButton();
        if (!nextBtn || nextBtn.disabled || nextBtn.hasAttribute('disabled')) break;
        nextBtn.click();
        await sleep(1500);
        continue;
      }
    }

    const result = await processLanguageScreenshots(curLang);
    if (result.skipped) {
      skippedCount++;
    } else {
      addedCount++;
    }

    await sleep(CONFIG.afterLocaleWaitMs);

    const nextBtn = getNextLanguageButton();
    if (!nextBtn || nextBtn.disabled || nextBtn.hasAttribute('disabled')) {
      console.log('🏁 Reached the last language (Next language button disabled or not found).');
      break;
    }

    console.log(`👉 Switching to next language...`);
    nextBtn.click();
    await sleep(CONFIG.switchWaitMs);
  }

  console.log(`\n=============================================================`);
  console.log(`🎉 Finished Batch Screenshot Loader!`);
  console.log(`   Total Languages Checked : ${processedCount}`);
  console.log(`   Screenshots Added       : ${addedCount}`);
  console.log(`   Skipped (Excluded/Done) : ${skippedCount}`);
  console.log(`👉 Final Save button status : ${getSaveButtonStatus()}`);
  console.log(`👉 Please click "Save as draft" or "Save changes" in Google Play Console.`);
  console.log(`=============================================================\n`);
})();
