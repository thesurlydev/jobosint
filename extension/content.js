(async () => {
    // get the current tab's document
    const tabDoc = await chrome.scripting.executeScript({
        target: {tabId: active},
        function: () => document
    });

    // send a message to the service worker with the document and the current tab url
    await chrome.runtime.sendMessage({document: tabDoc});
})();