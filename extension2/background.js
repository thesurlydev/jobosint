// background.js


// Allows users to open the side panel by clicking on the action toolbar icon
chrome.sidePanel
    .setPanelBehavior({
        openPanelOnActionClick: true
    })
    .catch((error) => console.error(error));


chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
    if (request.action === "parseContent") {
        chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
            chrome.scripting.executeScript(
                {
                    target: { tabId: tabs[0].id },
                    files: ['content.js']
                },
                () => {
                    chrome.tabs.sendMessage(tabs[0].id, { action: "parseContent", selector: request.selector }, (response) => {
                        if (response) {
                            sendResponse({ content: response.content });
                        } else {
                            sendResponse({ content: "No response from content script" });
                        }
                    });
                }
            );
        });
        return true; // Required to use sendResponse asynchronously
    } else if (request.action === "contentChanged") {
        chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
            chrome.scripting.executeScript(
                {
                    target: { tabId: tabs[0].id },
                    func: (selector) => {
                        return document.querySelector(selector)?.innerHTML || "No content found";
                    },
                    args: ['div.jobs-search__job-details--wrapper']
                },
                (results) => {
                    if (results && results[0]) {
                        chrome.runtime.sendMessage({ action: "parseContent", content: results[0].result });
                    } else {
                        chrome.runtime.sendMessage({ action: "parseContent", content: "No content found" });
                    }
                }
            );
        });
    }
});
