const LINKEDIN_ORIGIN = 'https://www.linkedin.com';

// Allows users to open the side panel by clicking on the action toolbar icon
chrome.sidePanel
    .setPanelBehavior({
        openPanelOnActionClick: true
    })
    .catch((error) => console.error(error));

chrome.tabs.onUpdated.addListener(async (tabId, info, tab) => {
    if (!tab.url) return;
    const url = new URL(tab.url);
    // Enables the side panel on linkedin.com
    if (url.origin === LINKEDIN_ORIGIN) {
        await chrome.sidePanel.setOptions({
            tabId,
            path: 'sidepanel.html',
            enabled: true
        });
    } else {
        // Disables the side panel on all other sites
        await chrome.sidePanel.setOptions({
            tabId,
            enabled: false
        });
    }
});

// handle messages from content.js and update side panel
chrome.runtime.onMessage.addListener((message) => {
    const {document, url} = message;
    // handle message
    console.log('Received message from content script:', document, url);

    // save message to storage with 'foo' as key
    chrome.storage.local.set({foo: message}, () => {
        console.log('Message saved to storage:', message);
    });


});
