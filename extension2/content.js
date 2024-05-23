// content.js

function setupObserver(selector) {
    const targetNode = document.querySelector(selector);
    if (!targetNode) return;

    const config = { attributes: true, childList: true, subtree: true };

    const callback = (mutationsList, observer) => {
        for (const mutation of mutationsList) {
            if (mutation.type === 'childList' || mutation.type === 'attributes') {
                chrome.runtime.sendMessage({ action: "contentChanged" });
            }
        }
    };

    const observer = new MutationObserver(callback);
    observer.observe(targetNode, config);
}

chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
    if (request.action === "setupObserver") {
        setupObserver(request.selector);
    } else if (request.action === "parseContent") {
        const content = document.querySelector(request.selector)?.innerHTML || "No content found";
        sendResponse({ content: content });
    }
});
