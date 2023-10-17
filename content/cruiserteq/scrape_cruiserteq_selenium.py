# filename: scrape_cruiserteq_selenium.py

from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.by import By

# Setup Selenium
s = Service(ChromeDriverManager().install())
driver = webdriver.Chrome(service=s)

# Open the website
driver.get('https://cruiserteq.com')

# Assuming part numbers are in a specific HTML tag, e.g., <span class="part-number">
# This needs to be adjusted based on the actual structure of the website
part_numbers = driver.find_elements(By.CSS_SELECTOR, 'span.part-number')

# Print the part numbers
for part_number in part_numbers:
    print(part_number.text)

# Close the browser
driver.quit()