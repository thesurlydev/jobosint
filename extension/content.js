// Function to extract job ID from URL
function extractJobId(url) {
    try {
        const urlObj = new URL(url);
        
        // LinkedIn job view page
        if (urlObj.hostname === 'www.linkedin.com' && urlObj.pathname.startsWith('/jobs/view/')) {
            // Extract the job ID and remove any trailing slashes or other characters
            const jobIdWithPossibleTrailing = urlObj.pathname.split('/')[3];
            // Extract only the numeric part of the job ID
            const numericJobId = jobIdWithPossibleTrailing.match(/^\d+/);
            return numericJobId ? numericJobId[0] : null;
        }
        
        // LinkedIn job search page with job selected
        if (urlObj.hostname === 'www.linkedin.com' && 
            (urlObj.pathname.startsWith('/jobs/search/') || urlObj.pathname.startsWith('/jobs/collections'))) {
            return urlObj.searchParams.get('currentJobId');
        }
        
        return null;
    } catch (error) {
        console.error('Error extracting job ID:', error);
        return null;
    }
}

// Function to extract job title from the page
function extractJobTitle() {
    // Try to find the job title in the h1 tag
    const h1Element = document.querySelector('h1');
    return h1Element ? h1Element.textContent.trim() : 'Unknown Job Title';
}

// Initial page load
(async () => {
    try {
        // Content scripts can't use chrome.tabs API directly
        // We'll use the current window location and document instead
        const currentUrl = window.location.href;
        const jobId = extractJobId(currentUrl);
        const jobTitle = extractJobTitle();
        
        // Send a message to the service worker with the job information
        try {
            await chrome.runtime.sendMessage({
                type: 'pageLoad',
                document: document.documentElement.innerHTML,
                url: currentUrl,
                jobId: jobId,
                jobTitle: jobTitle
            });
        } catch (e) {
            console.log('Extension communication error:', e.message);
            // If the extension context is invalidated, we can't do much but log it
        }
    } catch (error) {
        console.error('Error in content script:', error);
    }
})();

// Listen for URL changes (for LinkedIn job navigation)
let lastUrl = window.location.href;
const observer = new MutationObserver(() => {
    if (window.location.href !== lastUrl) {
        const oldUrl = lastUrl;
        lastUrl = window.location.href;
        
        // Only send message if we're on LinkedIn and the URL has changed
        if (window.location.hostname === 'www.linkedin.com') {
            const jobId = extractJobId(window.location.href);
            
            // Only send if we have a job ID or if we're moving away from a job
            if (jobId || extractJobId(oldUrl)) {
                try {
                    chrome.runtime.sendMessage({
                        type: 'urlChange',
                        url: window.location.href,
                        jobId: jobId
                    });
                } catch (e) {
                    console.log('Extension communication error:', e.message);
                    // If the extension context is invalidated, we can't do much but log it
                }
            }
        }
    }
});

// Start observing URL changes
observer.observe(document, { subtree: true, childList: true });

// Listen for clicks on job cards in the LinkedIn jobs search interface
document.addEventListener('click', (event) => {
    // Check if we're on LinkedIn jobs page
    if (window.location.hostname !== 'www.linkedin.com' || 
        !(window.location.pathname.startsWith('/jobs/search/') || 
          window.location.pathname.startsWith('/jobs/collections'))) {
        return;
    }
    
    // Find if the click was on a job card or its children
    let element = event.target;
    let jobCard = null;
    
    // Traverse up to find job card
    while (element && !jobCard) {
        // LinkedIn job cards typically have data attributes or specific classes
        if (element.classList && 
            (element.classList.contains('job-card-container') || 
             element.classList.contains('jobs-search-results__list-item'))) {
            jobCard = element;
        }
        element = element.parentElement;
    }
    
    // If we found a job card, extract the job ID and send a message
    if (jobCard) {
        // Wait a short time for the URL to update after the click
        setTimeout(() => {
            const jobId = extractJobId(window.location.href);
            const jobTitle = extractJobTitle();
            if (jobId) {
                try {
                    chrome.runtime.sendMessage({
                        type: 'jobCardClick',
                        url: window.location.href,
                        jobId: jobId,
                        jobTitle: jobTitle
                    });
                } catch (e) {
                    console.log('Extension communication error:', e.message);
                    // If the extension context is invalidated, we can't do much but log it
                }
            }
        }, 300);
    }
});