# filename: get_part_numbers.py

import requests
from bs4 import BeautifulSoup
import re

def get_part_numbers(url):
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')
    skus = soup.body(text=re.compile(r'SKU:'))
    part_numbers = [sku.split(':')[1].strip() for sku in skus]
    return part_numbers

def get_all_pages(url):
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')
    page_links = soup.find_all('a', href=True)
    page_urls = [link['href'] for link in page_links if link['href'].startswith(url)]
    return page_urls

def main():
    url = 'https://cruiserteq.com'
    page_urls = get_all_pages(url)
    all_part_numbers = []
    for page_url in page_urls:
        part_numbers = get_part_numbers(page_url)
        all_part_numbers.extend(part_numbers)
    print(all_part_numbers)

if __name__ == "__main__":
    main()