// sidepanel.js


document.addEventListener('DOMContentLoaded', () => {
    const parseButton = document.getElementById('parseButton');
    const contentDiv = document.getElementById('content');

    parseButton.addEventListener('click', () => {
        chrome.runtime.sendMessage({ action: "parseContent", selector: 'div.jobs-search__job-details--wrapper' }, (response) => {
            contentDiv.innerHTML = response.content;
        });
    });
});

chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
    if (request.action === "parseContent") {
        document.getElementById('content').innerHTML = request.content;
    }
});

// Initial parsing request on load
chrome.runtime.sendMessage({ action: "parseContent", selector: 'div.jobs-search__job-details--wrapper' }, (response) => {
    document.getElementById('content').innerHTML = response.content;
});

// Setup observer
chrome.runtime.sendMessage({ action: "setupObserver", selector: 'div.jobs-search__job-details--wrapper' }, (response) => {
    document.getElementById('content').innerHTML = response.content;
});
