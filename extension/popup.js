// popup.js
document.getElementById('saveBtn').addEventListener('click', function () {
    chrome.tabs.query({active: true, currentWindow: true}, function (tabs) {
        const activeTab = tabs[0];
        const originalUrl = new URL(activeTab.url);

        let url;
        if (originalUrl.hostname === 'www.linkedin.com') {
            let jobId = originalUrl.pathname.split('/')[3];
            url = `https://www.linkedin.com/jobs/view/${jobId}`;
        } else {
            url = originalUrl;
        }

        chrome.scripting.executeScript(
            {
                target: {tabId: activeTab.id},
                function: getPageContent
            },
            function (result) {
                const content = result[0].result;

                const source = "chrome-extension";
                const body = JSON.stringify({url, content, source});
                console.log('Body: ', body);

                fetch('http://localhost:8080/api/pages', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: body
                })
                    .then(response => {
                        if (!response.ok) {
                            document.getElementById('msg').innerText = 'Error: ' + response.status;
                        }
                        document.getElementById('msg').innerText = 'Page saved successfully';
                        return response.json();

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
});

function getPageContent() {
    return document.documentElement.innerHTML;
}
