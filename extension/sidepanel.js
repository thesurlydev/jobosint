// sidepanel.js

function getPageContent() {
    return document.documentElement.innerHTML;
}

function getContentFromSearchPage() {
    return document.querySelector('div.jobs-search__job-details--wrapper').innerHTML;
}

function getDocumentFromSearchPage() {
    return document.documentElement;
}

function savePageListener() {
    chrome.tabs.query({active: true, currentWindow: true}, function (tabs) {
        const activeTab = tabs[0];
        const originalUrl = new URL(activeTab.url);

        let url;
        let functionToExecute;
        if (originalUrl.hostname === 'www.linkedin.com' && originalUrl.pathname.startsWith('/jobs/view/')) {
            let jobId = originalUrl.pathname.split('/')[3];
            url = `https://www.linkedin.com/jobs/view/${jobId}`;
            functionToExecute = getPageContent;
        } else if (originalUrl.hostname === 'www.linkedin.com'
            && (originalUrl.pathname.startsWith('/jobs/search/') || originalUrl.pathname.startsWith('/jobs/collections'))) {
            let jobId = originalUrl.searchParams.get('currentJobId')
            url = `https://www.linkedin.com/jobs/view/${jobId}`;
            functionToExecute = getContentFromSearchPage;
        } else {
            url = originalUrl;
            functionToExecute = getPageContent;
        }

        chrome.scripting.executeScript(
            {
                target: {tabId: activeTab.id},
                function: functionToExecute
            },
            function (result) {
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

                    // console.log('Body: ', body);

                    fetch('http://localhost:8080/api/pages', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: body
                    })
                        .then(response => {
                            if (response.ok) {
                                document.getElementById('msg').innerText = 'Page saved successfully';
                                return response.json();
                            }
                            document.getElementById('msg').innerText = 'Error: ' + response.status;

                        })
                        .then(data => {
                            console.log('Response from server:', data);
                        })
                        .catch(error => {
                            document.getElementById('msg').innerText = 'Error saving page!';
                            console.error('Error details:', error);
                        });
                });
            }
        );
    });
}


function refreshListener() {
    chrome.tabs.query({active: true, currentWindow: true}, function (tabs) {
        const activeTab = tabs[0];
        const originalUrl = new URL(activeTab.url);

        let url;
        let functionToExecute;
        if (originalUrl.hostname === 'www.linkedin.com' && originalUrl.pathname.startsWith('/jobs/view/')) {
            let jobId = originalUrl.pathname.split('/')[3];
            url = `https://www.linkedin.com/jobs/view/${jobId}`;
            functionToExecute = getPageContent;
        } else if (originalUrl.hostname === 'www.linkedin.com'
            && (originalUrl.pathname.startsWith('/jobs/search/') || originalUrl.pathname.startsWith('/jobs/collections'))) {
            let jobId = originalUrl.searchParams.get('currentJobId')
            url = `https://www.linkedin.com/jobs/view/${jobId}`;
            functionToExecute = getDocumentFromSearchPage;
        } else {
            url = originalUrl;
            functionToExecute = getPageContent;
        }

        chrome.scripting.executeScript(
            {
                target: {tabId: activeTab.id},
                function: functionToExecute
            },
            function (result) {
                const doc = result[0].result;

                alert(doc);
            }
        );
    });

}

function run() {
    document.getElementById('saveBtn').addEventListener('click', savePageListener);
    document.getElementById('refreshBtn').addEventListener('click', refreshListener);

    // detect if local storage with 'foo' key has changed
    chrome.storage.onChanged.addListener((changes, namespace) => {
        if (changes.foo) {
            const message = changes.foo.newValue;
            console.log('Storage foo has changed:', message);
        }
    });
}

document.addEventListener('DOMContentLoaded', run);


