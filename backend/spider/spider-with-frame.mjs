import puppeteer from 'puppeteer-extra';
import StealthPlugin from 'puppeteer-extra-plugin-stealth';

const url = process.argv[2];

let browser = null;
let page = null;

try {
    puppeteer.use(StealthPlugin());
    // Launch the browser and open a new blank page
    browser = await puppeteer.launch(
        {
            headless: true
        }
    );
    const context = browser.defaultBrowserContext();
    await context.overridePermissions(url, ['clipboard-read', 'clipboard-write', 'clipboard-sanitized-write']);
    const pages = await browser.pages();
    page = pages[0];
    await page.goto(url, {waitUntil: 'networkidle0'});
    let html = await page.content(); // serialized HTML of page DOM.
    // 遍历所有 iframe，提取它们的内容
    if (page.frames()) {
        for (let iframe of page.frames()) {
            const frameContent = await iframe.content();
            if (frameContent) {
                html += frameContent;
            }
        }
    }
    console.log(html);
} catch (error) {
    console.log('zfoo_error', error);
} finally {
    if (page != null) {
        await page.close();
    }
    if (browser != null) {
        await browser.close();
    }
}