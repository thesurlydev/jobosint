(async () => {
    try {
        // First get the active tab ID
        const tabs = await chrome.tabs.query({ active: true, currentWindow: true });
        if (!tabs || tabs.length === 0) {
            console.error('No active tab found');
            return;
        }
        
        const activeTab = tabs[0];
        
        // Get the document from the active tab
        const results = await chrome.scripting.executeScript({
            target: { tabId: activeTab.id },
            function: () => document.documentElement.innerHTML
        });
        
        if (results && results.length > 0) {
            // Send a message to the service worker with the document and the current tab url
            await chrome.runtime.sendMessage({
                document: results[0].result,
                url: activeTab.url
            });
        }
    } catch (error) {
        console.error('Error in content script:', error);
    }
})();