# filename: download_webpage.py

import os
import requests

# URL of the webpage
url = "https://cruiserteq.com/new-category-2/"

# Send a GET request to the webpage
response = requests.get(url)

# Check if the request was successful
if response.status_code == 200:
    # Get the last part of the URL
    filename = url.rstrip('/').split('/')[-1] + '.html'

    # Save the content to a file
    with open(filename, 'w') as file:
        file.write(response.text)

    print(f"Webpage content saved to {filename}")
else:
    print(f"Failed to download webpage. Status code: {response.status_code}")