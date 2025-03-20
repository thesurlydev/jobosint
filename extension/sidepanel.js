// sidepanel.js

// Utility functions for getting page content
function getPageContent() {
    return document.documentElement.innerHTML;
}

function getContentFromSearchPage() {
    return document.querySelector('div.jobs-search__job-details--wrapper').innerHTML;
}

function getDocumentFromSearchPage() {
    return document.documentElement;
}

// Activity log management
const activityLog = {
    add: function(action, status, details = '', updateUI = false) {
        const timestamp = new Date().toLocaleTimeString();
        const date = new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
        const logEntry = { timestamp, date, action, status, details };
        
        // Get existing log from localStorage or initialize empty array
        const existingLog = JSON.parse(localStorage.getItem('activityLog') || '[]');
        
        // Add new entry at the beginning
        existingLog.unshift(logEntry);
        
        // Save back to localStorage - no entry limit
        localStorage.setItem('activityLog', JSON.stringify(existingLog));
        
        // Only update the UI when explicitly requested
        if (updateUI) {
            this.updateUI();
        }
    },
    
    updateUI: function() {
        const logContainer = document.getElementById('activity-log');
        const log = JSON.parse(localStorage.getItem('activityLog') || '[]');
        
        // Filter out "Job Selected" events - only keep "Save Page" events
        const filteredLog = log.filter(entry => entry.action === 'Save Page');
        
        if (filteredLog.length === 0) {
            logContainer.innerHTML = '<div class="flex items-center justify-center py-4">' +
                '<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-slate-400 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">' +
                '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />' +
                '</svg>' +
                '<p class="text-sm text-slate-500 italic">No recent activity</p></div>';
            return;
        }
        
        logContainer.innerHTML = '';
        
        // Group logs by date
        const groupedLogs = {};
        filteredLog.forEach(entry => {
            if (!groupedLogs[entry.date]) {
                groupedLogs[entry.date] = [];
            }
            groupedLogs[entry.date].push(entry);
        });
        
        // Create log entries by date groups
        Object.keys(groupedLogs).forEach(date => {
            // Date header
            const dateHeader = document.createElement('div');
            dateHeader.className = 'text-xs font-medium text-slate-400 mb-2 mt-3 first:mt-0';
            dateHeader.textContent = date;
            logContainer.appendChild(dateHeader);
            
            // Log entries for this date
            groupedLogs[date].forEach(entry => {
                const statusClass = entry.status === 'success' 
                    ? 'bg-green-50 border-green-100 text-green-700' 
                    : entry.status === 'error' 
                        ? 'bg-red-50 border-red-100 text-red-700' 
                        : 'bg-yellow-50 border-yellow-100 text-yellow-700';
                
                const statusIcon = entry.status === 'success'
                    ? '<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" /></svg>'
                    : entry.status === 'error'
                        ? '<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" /></svg>'
                        : '<svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" /></svg>';
                
                const logItem = document.createElement('div');
                logItem.className = `mb-2 rounded-lg border ${statusClass} overflow-hidden animate-fade-in`;
                
                logItem.innerHTML = `
                    <div class="p-3">
                        <div class="flex justify-between items-center">
                            <div class="flex items-center">
                                <div class="mr-2">${statusIcon}</div>
                                <span class="font-medium text-sm">${entry.action}</span>
                            </div>
                            <span class="text-xs opacity-70">${entry.timestamp}</span>
                        </div>
                        ${entry.details ? `
                            <div class="mt-1.5 text-xs opacity-90 truncate">
                                ${entry.details.length > 60 
                                    ? `${entry.details.substring(0, 60)}...` 
                                    : entry.details}
                            </div>
                        ` : ''}
                    </div>
                `;
                
                // Add tooltip for long details
                if (entry.details && entry.details.length > 60) {
                    logItem.title = entry.details;
                }
                
                logContainer.appendChild(logItem);
            });
        });
        
        // Add clear button to the top right if there are logs
        const clearLogContainer = document.getElementById('clear-log-container');
        clearLogContainer.innerHTML = '';
        
        if (log.length > 0) {
            const clearButton = document.createElement('button');
            clearButton.className = 'text-xs text-slate-500 hover:text-slate-700 flex items-center';
            clearButton.innerHTML = `
                <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
                Clear activity log
            `;
            clearButton.addEventListener('click', () => this.clear());
            clearLogContainer.appendChild(clearButton);
        }
    },
    
    clear: function() {
        localStorage.removeItem('activityLog');
        // This is explicitly called by the clear button, so we always update the UI
        this.updateUI();
        showMessage('Activity log cleared', 'info');
    }
};

// UI message handling
function showMessage(message, type = 'info') {
    const msgElement = document.getElementById('msg');
    
    // Clear any existing content and classes
    msgElement.innerHTML = '';
    msgElement.className = 'px-4 py-3 rounded-lg mb-4 transition-all duration-300 flex items-center';
    
    // Set appropriate styling and icon based on message type
    let icon = '';
    if (type === 'success') {
        msgElement.classList.add('bg-green-50', 'text-green-800', 'border', 'border-green-200');
        icon = `<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
               </svg>`;
    } else if (type === 'error') {
        msgElement.classList.add('bg-red-50', 'text-red-800', 'border', 'border-red-200');
        icon = `<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
               </svg>`;
    } else if (type === 'warning') {
        msgElement.classList.add('bg-yellow-50', 'text-yellow-800', 'border', 'border-yellow-200');
        icon = `<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-yellow-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
               </svg>`;
    } else {
        msgElement.classList.add('bg-primary-50', 'text-primary-800', 'border', 'border-primary-200');
        icon = `<svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2 text-primary-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
               </svg>`;
    }
    
    // Add close button
    const closeButton = `
        <button class="ml-auto text-slate-400 hover:text-slate-600 focus:outline-none" onclick="document.getElementById('msg').classList.add('hidden');">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
        </button>
    `;
    
    // Set the content with icon and message
    msgElement.innerHTML = `
        ${icon}
        <span class="text-sm font-medium">${message}</span>
        ${closeButton}
    `;
    
    // Add animation classes
    msgElement.classList.remove('hidden');
    msgElement.classList.add('animate-fade-in');
    
    // Auto-hide after 5 seconds
    const autoHideTimer = setTimeout(() => {
        msgElement.style.opacity = '0';
        setTimeout(() => {
            msgElement.classList.add('hidden');
            msgElement.style.opacity = '1';
        }, 300);
    }, 5000);
    
    // Clear timer if user manually closes the message
    const closeBtn = msgElement.querySelector('button');
    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            clearTimeout(autoHideTimer);
        });
    }
}

// Update current page info
function updateCurrentPageInfo() {
    // First check if we have a stored job ID from a recent click
    chrome.storage.local.get(['currentJobId', 'currentJobUrl'], function(data) {
        if (data.currentJobId && data.currentJobUrl) {
            // We have a stored job ID, use it to update the UI
            updateUIWithJobInfo(data.currentJobId, data.currentJobUrl);
            return;
        }
        
        // If no stored job ID, fall back to the current tab URL
        chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
            if (tabs && tabs[0]) {
                updateUIWithTabInfo(tabs[0]);
            }
        });
    });
}

// Function to extract job title from the active tab
function getJobTitleFromActiveTab(callback) {
    chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
        if (tabs && tabs.length > 0) {
            chrome.scripting.executeScript({
                target: {tabId: tabs[0].id},
                function: () => {
                    // Try to find the job title in the h1 tag
                    const h1Element = document.querySelector('h1');
                    return h1Element ? h1Element.textContent.trim() : 'Unknown Job Title';
                }
            }, function(results) {
                if (results && results.length > 0 && results[0].result) {
                    callback(results[0].result);
                } else {
                    callback('Unknown Job Title');
                }
            });
        } else {
            callback('Unknown Job Title');
        }
    });
}

// Function to extract job title from the active tab
function getCompanyNameFromActiveTab(callback) {
    chrome.tabs.query({active: true, currentWindow: true}, function(tabs) {
        if (tabs && tabs.length > 0) {
            chrome.scripting.executeScript({
                target: {tabId: tabs[0].id},
                function: () => {
                    // Try to find the job title in the h1 tag
                    const companyEl = document.querySelector('div.job-details-jobs-unified-top-card__company-name');
                    return companyEl ? companyEl.textContent.trim() : 'Unknown Company Name';
                }
            }, function(results) {
                if (results && results.length > 0 && results[0].result) {
                    callback(results[0].result);
                } else {
                    callback('Unknown Company Name');
                }
            });
        } else {
            callback('Unknown Company Name');
        }
    });
}

// Update UI with job information
function updateUIWithJobInfo(jobId, jobUrl, title) {
    const jobTitle = document.getElementById('job-title');
    const companyName = document.getElementById('company-name');
    const currentPage = document.getElementById('current-page');
    const pageDetails = document.getElementById('page-details');
    const platformBadge = document.querySelector('header + main > div:nth-child(2) > div:first-child > span');
    
    try {
        const url = new URL(jobUrl);
        
        // Handle LinkedIn job
        if (url.hostname === 'www.linkedin.com') {
            // Update UI elements
            currentPage.textContent = `LinkedIn Job #${jobId}`;
            pageDetails.textContent = `Captured at ${new Date().toLocaleTimeString()}`;
            if (platformBadge) platformBadge.textContent = 'LinkedIn';
            
            // Set tooltips
            currentPage.title = jobUrl;
            if (pageDetails) pageDetails.title = jobUrl;
            
            // Always try to get the job title directly from the active tab
            getJobTitleFromActiveTab(function(fetchedTitle) {
                jobTitle.textContent = fetchedTitle;
                jobTitle.title = fetchedTitle; // Set tooltip
                
                // Also save to storage for future reference
                chrome.storage.local.set({currentJobTitle: fetchedTitle});
            });

            getCompanyNameFromActiveTab(function(fetchedCompany) {
                companyName.textContent = fetchedCompany;
                companyName.title = fetchedCompany; // Set tooltip

                // Also save to storage for future reference
                chrome.storage.local.set({currentCompanyName: fetchedCompany});
            });
            
            // Log activity
            activityLog.add('Job Selected', 'success', `LinkedIn Job #${jobId}`);
        }
    } catch (error) {
        console.error('Error updating UI with job info:', error);
    }
}

// Update UI with tab information
function updateUIWithTabInfo(tab) {
    const currentPage = document.getElementById('current-page');
    const pageDetails = document.getElementById('page-details');
    const url = new URL(tab.url);
    
    // Format the URL nicely
    let displayText = '';
    let detailsText = '';
    let platformBadge = document.querySelector('header + main > div:nth-child(2) > div:first-child > span');
    
    // Handle different job platforms
    if (url.hostname === 'www.linkedin.com' && url.pathname.startsWith('/jobs/view/')) {
        const jobId = url.pathname.split('/')[3];
        displayText = `LinkedIn Job #${jobId}`;
        detailsText = `Captured at ${new Date().toLocaleTimeString()}`;
        if (platformBadge) platformBadge.textContent = 'LinkedIn';
    } else if (url.hostname === 'www.linkedin.com' && 
              (url.pathname.startsWith('/jobs/search/') || url.pathname.startsWith('/jobs/collections'))) {
        const jobId = url.searchParams.get('currentJobId');
        displayText = jobId ? `LinkedIn Job #${jobId}` : 'LinkedIn Jobs';
        detailsText = `Job search results page`;
        if (platformBadge) platformBadge.textContent = 'LinkedIn';
    } else if (url.hostname.includes('greenhouse.io')) {
        displayText = `Greenhouse Job Posting`;
        detailsText = url.pathname.replace(/\//g, ' › ').substring(1);
        if (platformBadge) platformBadge.textContent = 'Greenhouse';
    } else if (url.hostname.includes('lever.co')) {
        displayText = `Lever Job Posting`;
        detailsText = url.pathname.replace(/\//g, ' › ').substring(1);
        if (platformBadge) platformBadge.textContent = 'Lever';
    } else if (url.hostname.includes('myworkdayjobs.com')) {
        displayText = `Workday Job Posting`;
        detailsText = url.pathname.replace(/\//g, ' › ').substring(1);
        if (platformBadge) platformBadge.textContent = 'Workday';
    } else if (url.hostname.includes('smartrecruiters.com')) {
        displayText = `SmartRecruiters Job`;
        detailsText = url.pathname.replace(/\//g, ' › ').substring(1);
        if (platformBadge) platformBadge.textContent = 'SmartRecruiters';
    } else if (url.hostname.includes('glassdoor.com')) {
        displayText = `Glassdoor Job Listing`;
        detailsText = url.pathname.replace(/\//g, ' › ').substring(1);
        if (platformBadge) platformBadge.textContent = 'Glassdoor';
    } else if (url.hostname.includes('builtin.com')) {
        displayText = `BuiltIn Job Listing`;
        detailsText = url.pathname.replace(/\//g, ' › ').substring(1);
        if (platformBadge) platformBadge.textContent = 'BuiltIn';
    } else {
        // For other pages, show the hostname and truncated path
        displayText = url.hostname;
        detailsText = url.pathname !== '/' ? url.pathname.substring(0, 30) + (url.pathname.length > 30 ? '...' : '') : '';
        if (platformBadge) platformBadge.textContent = 'Web Page';
    }
    
    currentPage.textContent = displayText;
    currentPage.title = tab.url; // Set full URL as tooltip
    
    if (pageDetails) {
        pageDetails.textContent = detailsText || tab.url;
        pageDetails.title = tab.url; // Set full URL as tooltip
    }
}

// Save page functionality
function savePageListener() {
    // Show loading state
    const saveBtn = document.getElementById('saveBtn');
    const originalBtnText = saveBtn.innerHTML;
    saveBtn.innerHTML = `
        <svg class="animate-spin h-5 w-5 mr-2" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        Saving...
    `;
    saveBtn.disabled = true;
    
    chrome.tabs.query({active: true, currentWindow: true}, function (tabs) {
        const activeTab = tabs[0];
        const originalUrl = new URL(activeTab.url);

        // Check if we're on LinkedIn
        if (originalUrl.hostname !== 'www.linkedin.com') {
            // Not on LinkedIn, show warning message
            showMessage('This feature only works on LinkedIn job pages', 'warning');
            activityLog.add('Save Page', 'warning', 'Not a LinkedIn page: ' + originalUrl.toString(), true);
            saveBtn.innerHTML = originalBtnText;
            saveBtn.disabled = false;
            return;
        }

        let url;
        let functionToExecute;
        if (originalUrl.pathname.startsWith('/jobs/view/')) {
            let jobId = originalUrl.pathname.split('/')[3];
            url = `https://www.linkedin.com/jobs/view/${jobId}`;
            functionToExecute = getPageContent;
        } else if (originalUrl.pathname.startsWith('/jobs/search/') || originalUrl.pathname.startsWith('/jobs/collections')) {
            let jobId = originalUrl.searchParams.get('currentJobId');
            if (!jobId) {
                showMessage('No job selected on this LinkedIn page', 'warning');
                activityLog.add('Save Page', 'warning', 'No job selected on LinkedIn search page', true);
                saveBtn.innerHTML = originalBtnText;
                saveBtn.disabled = false;
                return;
            }
            url = `https://www.linkedin.com/jobs/view/${jobId}`;
            functionToExecute = getContentFromSearchPage;
        } else {
            // On LinkedIn but not on a job page
            showMessage('This feature only works on LinkedIn job pages', 'warning');
            activityLog.add('Save Page', 'warning', 'Not a LinkedIn job page: ' + originalUrl.toString(), true);
            saveBtn.innerHTML = originalBtnText;
            saveBtn.disabled = false;
            return;
        }

        chrome.scripting.executeScript(
            {
                target: {tabId: activeTab.id},
                function: functionToExecute
            },
            function (result) {
                if (!result || result.length === 0) {
                    showMessage('Failed to extract content from page', 'error');
                    activityLog.add('Save Page', 'error', originalUrl.toString(), true);
                    saveBtn.innerHTML = originalBtnText;
                    saveBtn.disabled = false;
                    return;
                }
                
                const content = result[0].result;

                // Get cookies for the current domain
                chrome.cookies.getAll({domain: originalUrl.hostname}, function(cookies) {
                    // Format cookies for inclusion in the request body
                    const cookiesArray = cookies.map(cookie => ({
                        name: cookie.name,
                        value: cookie.value,
                        domain: cookie.domain,
                        path: cookie.path,
                        expires: cookie.expirationDate,
                        httpOnly: cookie.httpOnly,
                        secure: cookie.secure
                    }));

                    const source = "chrome-extension";

                    // Include cookies in the request body
                    const body = JSON.stringify({
                        url,
                        content,
                        source,
                        cookies: cookiesArray
                    });

                    fetch('http://localhost:8080/api/pages', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: body
                    })
                    .then(response => {
                        if (response.ok) {
                            showMessage('Job information saved successfully!', 'success');
                            activityLog.add('Save Page', 'success', url, true);
                            return response.json();
                        } else {
                            showMessage(`Error: ${response.status}`, 'error');
                            activityLog.add('Save Page', 'error', `Status: ${response.status}`, true);
                            throw new Error(`Server responded with ${response.status}`);
                        }
                    })
                    .then(data => {
                        console.log('Response from server:', data);
                    })
                    .catch(error => {
                        showMessage('Error saving page!', 'error');
                        console.error('Error details:', error);
                        activityLog.add('Save Page', 'error', error.message, true);
                    })
                    .finally(() => {
                        // Restore button state
                        saveBtn.innerHTML = originalBtnText;
                        saveBtn.disabled = false;
                    });
                });
            }
        );
    });
}

// Removed refresh functionality as it's no longer needed with automatic updates

// Initialize the extension
function run() {
    // Set up event listeners
    document.getElementById('saveBtn').addEventListener('click', savePageListener);

    // Initialize UI components
    updateCurrentPageInfo();
    // Initialize the activity log UI once on startup
    activityLog.updateUI();
    
    // Get the job title on initial load
    getJobTitleFromActiveTab(function(fetchedTitle) {
        const jobTitle = document.getElementById('job-title');
        if (jobTitle) {
            jobTitle.textContent = fetchedTitle;
            jobTitle.title = fetchedTitle; // Set tooltip
        }
    });

    // Get the company name on initial load
    getCompanyNameFromActiveTab(function(fetchedCompany) {
        const companyName = document.getElementById('company-name');
        if (companyName) {
            companyName.textContent = fetchedCompany;
            companyName.title = fetchedCompany; // Set tooltip
        }
    });
    
    // Listen for storage changes
    chrome.storage.onChanged.addListener((changes, namespace) => {
        if (changes.currentJobId) {
            console.log('Job ID has changed:', changes.currentJobId.newValue);
            // Update the UI when job ID changes in storage
            if (changes.currentJobId.newValue && changes.currentJobUrl && changes.currentJobUrl.newValue) {
                const jobTitle = changes.currentJobTitle ? changes.currentJobTitle.newValue : null;
                updateUIWithJobInfo(changes.currentJobId.newValue, changes.currentJobUrl.newValue, jobTitle);
            }
        }
    });
    
    // Listen for tab changes to update current page info
    chrome.tabs.onActivated.addListener(function() {
        updateCurrentPageInfo();
    });
    
    // Listen for runtime messages from the service worker
    chrome.runtime.onMessage.addListener(function(message) {
        if (message.type === 'jobUpdate') {
            console.log('Received job update message:', message);
            updateUIWithJobInfo(message.jobId, message.url, message.jobTitle);
        }
    });
    
    chrome.tabs.onUpdated.addListener(function(tabId, changeInfo, tab) {
        if (changeInfo.status === 'complete') {
            updateCurrentPageInfo();
            
            // Also refresh the job title when the tab updates
            getJobTitleFromActiveTab(function(fetchedTitle) {
                const jobTitle = document.getElementById('job-title');
                if (jobTitle) {
                    jobTitle.textContent = fetchedTitle;
                    jobTitle.title = fetchedTitle; // Set tooltip
                }
            });

            // Also refresh the job title when the tab updates
            getCompanyNameFromActiveTab(function(fetchedCompanyName) {
                const companyName = document.getElementById('company-name');
                if (companyName) {
                    companyName.textContent = fetchedCompanyName;
                    companyName.title = fetchedCompanyName; // Set tooltip
                }
            });
        }
    });
    
    // Set up message listener for job updates
    chrome.runtime.onMessage.addListener((message, sender, sendResponse) => {
        console.log('Sidepanel received message:', message);
        
        // Handle job update messages
        if (message.type === 'jobUpdate' && message.jobId) {
            // Update the UI with the new job information
            updateUIWithJobInfo(message.jobId, message.url);
        }
    });
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', run);

