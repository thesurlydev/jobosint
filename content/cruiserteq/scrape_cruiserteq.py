# filename: scrape_cruiserteq.py

import requests
from bs4 import BeautifulSoup

def get_part_numbers(url):
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')

    # Assuming part numbers are in a specific HTML tag, e.g., <span class="part-number">
    # This needs to be adjusted based on the actual structure of the website
    part_numbers = [tag.text for tag in soup.find_all('span', {'class': 'part-number'})]

    return part_numbers

# Start with the main page
url = 'https://cruiserteq.com'

part_numbers = get_part_numbers(url)

# Print the part numbers
for part_number in part_numbers:
    print(part_number)