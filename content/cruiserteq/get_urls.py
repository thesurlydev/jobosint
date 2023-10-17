# filename: get_urls.py

import requests
from bs4 import BeautifulSoup
from urllib.parse import urlparse

def get_unique_urls(url):
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')
    unique_urls = set()
    for link in soup.find_all('a', href=True):
        parsed_url = urlparse(link.get('href'))
        if parsed_url.netloc == '':
            continue
        if parsed_url.netloc == urlparse(url).netloc:
            unique_urls.add(link.get('href'))
    return unique_urls

def main():
    url = "https://cruiserteq.com"
    unique_urls = get_unique_urls(url)
    for url in unique_urls:
        print(url)

if __name__ == "__main__":
    main()