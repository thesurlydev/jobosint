// sidepanel.js

function getPageContent() {
    return document.documentElement.innerHTML;
}

function getContentFromSearchPage() {
    return document.querySelector('div.jobs-search__job-details--wrapper').innerHTML;
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

                const source = "chrome-extension";
                const body = JSON.stringify({url, content, source});
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
                    });
            }
        );
    });
}

function refreshListener() {
    chrome.tabs.query({active: true, currentWindow: true}, function (tabs) {
        const activeTab = tabs[0];
        alert(activeTab);
    });
}

function run() {
    document.getElementById('saveBtn').addEventListener('click', savePageListener);
    document.getElementById('refreshBtn').addEventListener('click', refreshListener);
    // document.querySelector('div.job-card-container--clickable').addEventListener('click', function () {
    //     alert('changed');
    //     const title = document.querySelector('h1').textContent
    //     document.getElementById('msg').innerText = title;
    // });



    // const targetNode = document.querySelector('div.jobs-search__job-details--wrapper');
    // const config = { attributes: true, childList: true, subtree: true };
    // const callback = (mutationsList) => {
    //     for(const mutation of mutationsList) {
    //         alert('boo');
    //     }
    // };
    // const observer = new MutationObserver(callback);
    // observer.observe(targetNode, config);

}

document.addEventListener('DOMContentLoaded', run);


