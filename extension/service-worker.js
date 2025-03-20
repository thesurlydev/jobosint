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
    console.log('Received message from content script:', message);
    
    // Store the last message for the sidepanel to access
    chrome.storage.local.set({lastMessage: message}, () => {
        console.log('Message saved to storage:', message);
    });
    
    // If this is a job-related message, store the job ID, URL, title, and company name
    if (message.jobId) {
        chrome.storage.local.set({
            currentJobId: message.jobId,
            currentJobUrl: message.url,
            currentJobTitle: message.jobTitle || 'Unknown Job Title',
            currentCompanyName: message.companyName || 'Unknown Company Name'
        }, () => {
            console.log('Job information saved:', message.jobId, message.jobTitle, message.companyName);
        });
        
        // Notify any open sidepanels about the job change
        chrome.runtime.sendMessage({
            type: 'jobUpdate',
            jobId: message.jobId,
            url: message.url,
            jobTitle: message.jobTitle || 'Unknown Job Title',
            companyName: message.companyName || 'Unknown Company Name'
        }).catch(error => {
            // This error is expected if no sidepanel is open to receive the message
            console.log('No receivers for the message, this is normal if sidepanel is not open');
        });
    }
});
