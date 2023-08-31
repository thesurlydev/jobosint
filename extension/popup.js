// popup.js
document.getElementById('sendButton').addEventListener('click', function() {
    chrome.tabs.query({ active: true, currentWindow: true }, function(tabs) {
        const activeTab = tabs[0];
        const url = activeTab.url;

        chrome.scripting.executeScript(
            {
                target: { tabId: activeTab.id },
                function: getPageContent
            },
            function(result) {
                const content = result[0].result;

                const source = "chrome-extension";
                const body =JSON.stringify({ url, content, source });
                console.log('Body: ', body);

                fetch('http://localhost:8080/api/pages', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: body
                })
                    .then(response => response.json())
                    .then(data => {
                        console.log('Response from server:', data);
                    })
                    .catch(error => {
                        console.error('Error:', error);
                    });
            }
        );
    });
});

function getPageContent() {
    return document.documentElement.innerHTML;
}
